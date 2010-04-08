package org.kohsuke.depann.impl;

import org.kohsuke.depann.AccessRestriction;
import org.kohsuke.depann.Restricted;
import org.kohsuke.depann.impl.Restrictions.Parser;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.EmptyVisitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static org.objectweb.asm.ClassReader.SKIP_DEBUG;
import static org.objectweb.asm.ClassReader.SKIP_FRAMES;

/**
 * @author Kohsuke Kawaguchi
 */
public class Checker {
    /**
     * List of dependency jar files or class directories.
     */
//    public final List<File> dependencies = new ArrayList<File>();

    public final ClassLoader dependencies;

    /**
     * Where errors and warnings are sent.
     */
    private ErrorListener errorListener = ErrorListener.NULL;

    /**
     * Restrictions found from dependencies.
     * <p>
     * The string is either:
     * <ul>
     * <li>internal name of a type
     * <li>internal name of a type + '.' + field name
     * <li>internal name of a type + '.' + method name + method descriptor
     * </ul>
     */
    private final Map<String,AccessRestriction> restrictions = new HashMap<String, AccessRestriction>();

    private final AccessRestrictionFactory factory;


    public Checker(ClassLoader dependencies) {
        this.dependencies = dependencies;
        this.factory = new AccessRestrictionFactory(dependencies);
    }

    /**
     * Checks a single class file or a directory full of class files (recursively.)
     */
    public void check(File f) throws IOException {
        // load access restrictions
        loadAccessRestrictions();


        if (f.isDirectory()) {
            for (File c : f.listFiles())
                check(c);
            return;
        }

        if (f.getPath().endsWith(".class"))
            checkClass(f);
    }

    /**
     * Loads all the access restrictions defined in our dependencies.
     */
    private void loadAccessRestrictions() throws IOException {
        final Enumeration<URL> res = dependencies.getResources("META-INF/annotations/"+Restrictions.class.getName());
        while (res.hasMoreElements()) {
            URL url = res.nextElement();
            BufferedReader r = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            String className;
            while ((className=r.readLine())!=null) {
                InputStream is = dependencies.getResourceAsStream(className.replace('.','/') + ".class");
                if (is==null) {
                    errorListener.onWarning(null,"Failed to find class file for "+ className);
                    continue;
                }

                try {
                    new ClassReader(is).accept(new EmptyVisitor() {
                        private String className;

                        @Override
                        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                            this.className = name;
                        }

                        @Override
                        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                            return onAnnotationFor(className,desc);
                        }

                        @Override
                        public FieldVisitor visitField(int access, final String name, String desc, String signature, Object value) {
                            return new EmptyVisitor() {
                                @Override
                                public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                                    return onAnnotationFor(className+'.'+name,desc);
                                }
                            };
                        }

                        @Override
                        public MethodVisitor visitMethod(int access, final String methodName, final String methodDesc, String signature, String[] exceptions) {
                            return new EmptyVisitor() {
                                @Override
                                public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                                    return onAnnotationFor(className+'.'+methodName+methodDesc,desc);
                                }
                            };
                        }

                        /**
                         * Parse {@link Restricted} annotation on some annotated element.
                         */
                        private AnnotationVisitor onAnnotationFor(final String keyName, String desc) {
                            if (RESTRICTED_DESCRIPTOR.equals(desc)) {
                                return new Parser() {
                                    @Override
                                    public void visitEnd() {
                                        try {
                                            restrictions.put(keyName,build(factory));
                                        } catch (ClassNotFoundException e) {
                                            failure(e);
                                        } catch (InstantiationException e) {
                                            failure(e);
                                        } catch (IllegalAccessException e) {
                                            failure(e);
                                        }
                                    }

                                    /**
                                     * Fails to load a {@link AccessRestriction} instance.
                                     */
                                    private void failure(Exception e) {
                                        errorListener.onError(e,"Failed to load restrictions");
                                    }
                                };
                            }
                            return null;
                        }
                    }, ClassReader.SKIP_CODE);
                } finally {
                    is.close();
                }
            }
        }
    }

    /**
     * Inspects a class for the restriction violations.
     */
    public void checkClass(File clazz) throws IOException {
        FileInputStream in = new FileInputStream(clazz);
        try {
            ClassReader cr = new ClassReader(in);
            cr.accept(new EmptyVisitor() {
                private String className;
                private String methodName,methodDesc;
                private int line;

                @Override
                public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                    this.className = name;

                    if (superName!=null)
                        getRestrictions(superName).usedAsSuperType(currentLocation,errorListener);

                    if (interfaces!=null) {
                        for (String intf : interfaces)
                            getRestrictions(intf).usedAsInterface(currentLocation,errorListener);
                    }
                }

                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    this.methodName  = name;
                    this.methodDesc = desc;
                    return new EmptyVisitor() {
                        @Override
                        public void visitLineNumber(int _line, Label start) {
                            line = _line;
                        }

                        public void visitTypeInsn(int opcode, String type) {
                            switch (opcode) {
                            case Opcodes.NEW:
                                getRestrictions(type).instantiated(currentLocation,errorListener);
                            }
                        }

                        @Override
                        public void visitMethodInsn(int opcode, String owner, String name, String desc) {
                            getRestrictions(owner+'.'+name+desc).invoked(currentLocation,errorListener);
                        }

                        @Override
                        public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                            AccessRestriction r = getRestrictions(owner + '.' + name);
                            switch (opcode) {
                            case Opcodes.GETSTATIC:
                            case Opcodes.GETFIELD:
                                r.read(currentLocation,errorListener);
                                break;
                            case Opcodes.PUTSTATIC:
                            case Opcodes.PUTFIELD:
                                r.written(currentLocation,errorListener);
                                break;
                            }
                            super.visitFieldInsn(opcode, owner, name, desc);
                        }
                    };
                }


                /**
                 * Constant that represents the current location.
                 */
                private final Location currentLocation = new Location() {
                    public String getClassName() {
                        return className;
                    }

                    public String getMethodName() {
                        return methodName;
                    }

                    public String getMethodDescriptor() {
                        return methodDesc;
                    }

                    public int getLineNumber() {
                        return line;
                    }

                    public String toString() {
                        return className+':'+line;
                    }
                };
            }, SKIP_DEBUG | SKIP_FRAMES);
        } finally {
            in.close();
        }
    }

    private AccessRestriction getRestrictions(String keyName) {
        AccessRestriction r = restrictions.get(keyName);
        if (r==null)    return Restrictions.NONE;
        return r;
    }

    private static final String RESTRICTED_DESCRIPTOR = Type.getDescriptor(Restricted.class);
}
