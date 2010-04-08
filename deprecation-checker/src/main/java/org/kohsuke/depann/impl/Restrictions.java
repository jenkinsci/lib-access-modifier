package org.kohsuke.depann.impl;

import org.kohsuke.depann.AccessRestriction;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Kohsuke Kawaguchi
 */
public class Restrictions extends ArrayList<AccessRestriction> implements AccessRestriction {
    public Restrictions(Collection<? extends AccessRestriction> c) {
        super(c);
    }

    public Restrictions() {
    }

    public void usedAsSuperType(Location loc, RestrictedElement target, ErrorListener errorListener) {
        for (AccessRestriction ar : this)
            ar.usedAsSuperType(loc,target,errorListener);
    }

    public void usedAsInterface(Location loc, RestrictedElement target, ErrorListener errorListener) {
        for (AccessRestriction ar : this)
            ar.usedAsInterface(loc,target,errorListener);
    }

    public void instantiated(Location loc, RestrictedElement target, ErrorListener errorListener) {
        for (AccessRestriction ar : this)
            ar.instantiated(loc,target,errorListener);
    }

    public void invoked(Location location, RestrictedElement target, ErrorListener errorListener) {
        for (AccessRestriction ar : this)
            ar.invoked(location,target,errorListener);
    }

    public void read(Location location, RestrictedElement target, ErrorListener errorListener) {
        for (AccessRestriction ar : this)
            ar.read(location,target,errorListener);
    }

    public void written(Location location, RestrictedElement target, ErrorListener errorListener) {
        for (AccessRestriction ar : this)
            ar.written(location,target,errorListener);
    }




    public static final Restrictions NONE = new Restrictions();

    abstract static class Parser implements AnnotationVisitor {
        private List<Type> restrictions = new ArrayList<Type>();

        public void visit(String name, Object value) {
            restrictions.add((Type)value);
        }

        public void visitEnum(String name, String desc, String value) {
        }

        public AnnotationVisitor visitAnnotation(String name, String desc) {
            return this;
        }

        public AnnotationVisitor visitArray(String name) {
            return this;
        }

        public abstract void visitEnd();

        public Restrictions build(AccessRestrictionFactory f) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
            Restrictions r = new Restrictions();
            for (Type t : restrictions) {
                r.add(f.get(t));
            }
            return r;
        }
    }
}
