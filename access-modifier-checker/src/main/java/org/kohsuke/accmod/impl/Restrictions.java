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

import java.util.Objects;
import org.codehaus.plexus.util.StringUtils;
import org.kohsuke.accmod.AccessRestriction;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Kohsuke Kawaguchi
 */
public class Restrictions extends ArrayList<AccessRestriction> {
    final RestrictedElement target;

    public Restrictions(RestrictedElement target, Collection<? extends AccessRestriction> c) {
        super(c);
        this.target = target;
    }

    public Restrictions(RestrictedElement target) {
        this.target = target;
    }

    public void usedAsSuperType(Location loc, ErrorListener errorListener) {
        for (AccessRestriction ar : this)
            ar.usedAsSuperType(loc,target,errorListener);
    }

    public void usedAsInterface(Location loc, ErrorListener errorListener) {
        for (AccessRestriction ar : this)
            ar.usedAsInterface(loc,target,errorListener);
    }

    public void instantiated(Location loc, ErrorListener errorListener) {
        for (AccessRestriction ar : this)
            ar.instantiated(loc,target,errorListener);
    }

    public void invoked(Location location, ErrorListener errorListener) {
        for (AccessRestriction ar : this)
            ar.invoked(location,target,errorListener);
    }

    public void overridden(Location location, ErrorListener errorListener) {
        for (AccessRestriction ar : this)
            ar.overridden(location,target,errorListener);
    }

    public void read(Location location, ErrorListener errorListener) {
        for (AccessRestriction ar : this)
            ar.read(location,target,errorListener);
    }

    public void written(Location location, ErrorListener errorListener) {
        for (AccessRestriction ar : this)
            ar.written(location,target,errorListener);
    }

    abstract static class Parser extends AnnotationVisitor {
        private List<Type> restrictions = new ArrayList<Type>();
        private final String keyName;
        private final boolean isInTheInspectedModule;
        private String message;
        private RestrictedElement target;

        protected Parser(String keyName, boolean isInTheInspectedModule) {
            super(Opcodes.ASM5);
            this.keyName = keyName;
            this.isInTheInspectedModule = isInTheInspectedModule;
        }

        private RestrictedElement target() {
            if (target == null) {
                target = new RestrictedElement() {
                    public boolean isInTheInspectedModule() {
                        return isInTheInspectedModule;
                    }

                    public String toString() {
                        return keyName;
                    }

                    @Override
                    public String message() {
                        return StringUtils.defaultString(message, "");
                    }
                };
            }
            return target;
        }

        public void visit(String name, Object value) {
            if ("message".equals(name)) {
                message = value instanceof String ? (String)value : String.valueOf(value);
            } else {
                restrictions.add((Type) value);
            }
        }

        public void visitEnum(String name, String desc, String value) {
        }

        public AnnotationVisitor visitAnnotation(String name, String desc) {
            // this parser assumes it is invoked on @Restricted which doesn't have any child annotation values
            return this;
        }

        public AnnotationVisitor visitArray(String name) {
            // this parser assumes it is invoked on @Restricted which doesn't have any array values
            return this;
        }

        public abstract void visitEnd();

        public Restrictions build(AccessRestrictionFactory f) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
            Restrictions r = new Restrictions(target());
            for (Type t : restrictions) {
                r.add(f.get(t));
            }
            return r;
        }
    }
}
