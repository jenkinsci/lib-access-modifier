/*
 * The MIT License
 *
 * Copyright (c) 2010, Kohsuke Kawaguchi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.kohsuke.accmod.impl;

import org.kohsuke.accmod.AccessRestriction;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.impl.Restrictions.Parser;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.objectweb.asm.ClassReader.SKIP_FRAMES;

/**
 * Performs check.
 *
 * @author Kohsuke Kawaguchi
 */
public class Checker {
    /**
     * Where dependencies are loaded. We don't actually load classes,
     * but this is used to search for class files and indexed restrictions.
     */
    public final ClassLoader dependencies;

    /**
     * Where errors and warnings are sent.
     */
    private final ErrorListener errorListener;

    private final Properties properties;

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
    private final Map<String,Restrictions> restrictions = new HashMap<String,Restrictions>();

    private final AccessRestrictionFactory factory;


    Checker(ClassLoader dependencies, ErrorListener errorListener, Properties properties) throws IOException {
        this.dependencies = dependencies;
        this.errorListener = errorListener;
        this.properties = properties;
        this.factory = new AccessRestrictionFactory(dependencies);

        // load access restrictions
        loadAccessRestrictions();
    }

    public ErrorListener getErrorListener() {
        return errorListener;
    }

    /**
     * Checks a single class file or a directory full of class files (recursively.)
     */
    public void check(File f) throws IOException {
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
        final Enumeration<URL> res = dependencies.getResources("META-INF/annotations/"+Restricted.class.getName());
        while (res.hasMoreElements()) {
            URL url = res.nextElement();
            loadRestrictions(url.openStream(),false);
        }
    }

    /**
     * Loads an additional restriction from the specified "META-INF/annotations/org.kohsuke.accmod.Restricted" file.
     *
     * @param isInTheInspectedModule
     *      This value shows up in {@link RestrictedElement#isInTheInspectedModule()}.
     * @param stream
     */
    public void loadRestrictions(InputStream stream, final boolean isInTheInspectedModule) throws IOException {
        if (stream==null)      return;

        BufferedReader r = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        String className;
        while ((className=r.readLine())!=null) {


            try (InputStream is = dependencies.getResourceAsStream(className.replace('.','/') + ".class")) {
                if (is != null) {
                    final ClassVisitor visitor = new AnnotatedClassVisitor(isInTheInspectedModule);
                    new ClassReader(is).accept(visitor, ClassReader.SKIP_CODE);
                } else {
                    try (InputStream i = dependencies.getResourceAsStream(className.replace('.','/') + "/package-info.class")) {
                        if (i==null) {
                            errorListener.onWarning(null, null, "Failed to find class file for " + className);
                            continue;
                        }
                        final ClassVisitor visitor = new AnnotatedPackageVisitor(className, isInTheInspectedModule);
                        new ClassReader(i).accept(visitor, ClassReader.SKIP_CODE);
                    }
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
            cr.accept(new ClassVisitor(Opcodes.ASM5) {
                private String className;
                private String methodName,methodDesc;
                private int line;

                @Override
                public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                    this.className = name;

                    if (isSynthetic(access)) {
                        return;
                    }

                    if (superName != null) {
                        for (Restrictions r : getRestrictions(superName)) {
                            r.usedAsSuperType(currentLocation, errorListener);
                        }
                    }

                    if (interfaces != null) {
                        for (String intf : interfaces) {
                            for (Restrictions r : getRestrictions(intf)) {
                                r.usedAsInterface(currentLocation, errorListener);
                            }
                        }
                    }
                }

                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    this.methodName  = name;
                    this.methodDesc = desc;

                    if (isSynthetic(access)) {
                        return null;
                    }

                    return new MethodVisitor(Opcodes.ASM5) {
                        @Override
                        public void visitLineNumber(int _line, Label start) {
                            line = _line;
                        }

                        public void visitTypeInsn(int opcode, String type) {
                            switch (opcode) {
                            case Opcodes.NEW:
                                for (Restrictions r : getRestrictions(type)) {
                                    r.instantiated(currentLocation, errorListener);
                                }
                            }
                        }

                        @Override
                        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                            for (Restrictions r : getRestrictions(owner + '.' + name + desc)) {
                                r.invoked(currentLocation, errorListener);
                            }
                        }

                        @Override
                        public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                            Iterable<Restrictions> rs = getRestrictions(owner + '.' + name);
                            switch (opcode) {
                            case Opcodes.GETSTATIC:
                            case Opcodes.GETFIELD:
                                for (Restrictions r : rs) {
                                    r.read(currentLocation, errorListener);
                                }
                                break;
                            case Opcodes.PUTSTATIC:
                            case Opcodes.PUTFIELD:
                                for (Restrictions r : rs) {
                                    r.written(currentLocation, errorListener);
                                }
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
                        return className.replace('/','.');
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

                    public ClassLoader getDependencyClassLoader() {
                        return dependencies;
                    }

                    @Override
                    public String getProperty(String key) {
                        return properties.getProperty(key);
                    }
                };
            }, SKIP_FRAMES);
        } finally {
            in.close();
        }
    }

    private Iterable<Restrictions> getRestrictions(String keyName) {
        List<Restrictions> rs = new ArrayList<>();
        Restrictions r = restrictions.get(keyName);
        if (r != null) {
            rs.add(r);
        }
        int idx = Integer.MAX_VALUE;
        while (true) {
            int newIdx = keyName.lastIndexOf('.', idx);
            if (newIdx == -1) {
                newIdx = keyName.lastIndexOf('$', idx);
                if (newIdx == -1) {
                    newIdx = keyName.lastIndexOf('/', idx);
                    if (newIdx == -1) {
                        break;
                    }
                }
            }
            idx = newIdx;
            keyName = keyName.substring(0, idx);

            r = restrictions.get(keyName);
            if (r != null) {
                Collection<AccessRestriction> applicable = new ArrayList<>();
                for (AccessRestriction ar : r) {
                    if (ar.appliesToNested()) {
                        applicable.add(ar);
                    }
                }
                if (!applicable.isEmpty()) {
                    rs.add(new Restrictions(r.target, applicable));
                }
            }
        }
        return rs;
    }

    private static final String RESTRICTED_DESCRIPTOR = Type.getDescriptor(Restricted.class);

    private static boolean isSynthetic(int access) {
        return (access & Opcodes.ACC_SYNTHETIC) != 0;
    }

    private abstract class RestrictionsClassVisitor extends ClassVisitor {
        private final boolean isInTheInspectedModule;

        public RestrictionsClassVisitor(boolean isInTheInspectedModule) {
            super(Opcodes.ASM5);
            this.isInTheInspectedModule = isInTheInspectedModule;
        }


        /**
         * Parse {@link Restricted} annotation on some annotated element.
         */
        AnnotationVisitor onAnnotationFor(final String keyName, String desc) {
            if (RESTRICTED_DESCRIPTOR.equals(desc)) {
                RestrictedElement target = new RestrictedElement() {
                    public boolean isInTheInspectedModule() {
                        return isInTheInspectedModule;
                    }

                    public String toString() {
                        return keyName;
                    }
                };
                return new Parser(target) {
                    @Override
                    public void visitEnd() {
                        try {
                            restrictions.put(keyName, build(factory));
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
                        errorListener.onError(e, null, "Failed to load restrictions");
                    }
                };
            }
            return null;
        }
    }

    private class AnnotatedClassVisitor extends RestrictionsClassVisitor {
        private String className;

        public AnnotatedClassVisitor(boolean isInTheInspectedModule) {
            super(isInTheInspectedModule);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            this.className = name;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            return onAnnotationFor(className, desc);
        }

        @Override
        public FieldVisitor visitField(int access, final String name, String desc, String signature, Object value) {
            return new FieldVisitor(Opcodes.ASM5) {
                @Override
                public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                    return onAnnotationFor(className + '.' + name, desc);
                }
            };
        }

        @Override
        public MethodVisitor visitMethod(int access, final String methodName, final String methodDesc, String signature, String[] exceptions) {
            return new MethodVisitor(Opcodes.ASM5) {
                @Override
                public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                    return onAnnotationFor(className + '.' + methodName + methodDesc, desc);
                }
            };
        }

    }

    private class AnnotatedPackageVisitor extends RestrictionsClassVisitor {

        private final String className;

        public AnnotatedPackageVisitor(String className, boolean isInTheInspectedModule) {
            super(isInTheInspectedModule);
            this.className = className;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            return onAnnotationFor(className, desc);
        }

    }
}
