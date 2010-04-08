package org.kohsuke.depann;

import org.kohsuke.depann.Restrictions.Parser;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.EmptyVisitor;
import org.objectweb.asm.tree.AnnotationNode;

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
     * Restrictions placed on the type. Lazily loaded.
     */
    private final Map<String,Restrictions> typeRestrictions = new HashMap<String, Restrictions>();

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
                    errorListener.onWarning("Failed to resolve"+ className);
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
                                            typeRestrictions.put(keyName,build(factory));
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
                                        errorListener.onError("Failed to load");
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
                @Override
                public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                    // these are internal names
                }

                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    return new EmptyVisitor();
                }
            }, SKIP_DEBUG|SKIP_FRAMES);
        } finally {
            in.close();
        }
    }

    /**
     * @param internalName
     *      Internal name of the type is FQCN except '.' is '/'.
     */
    private Restrictions getTypeRestrictions(String internalName) throws IOException {
        return typeRestrictions.get(internalName);
    }

    private static final String RESTRICTED_DESCRIPTOR = Type.getDescriptor(Restricted.class);
}
