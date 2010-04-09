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
public class Restrictions extends ArrayList<AccessRestriction> {
    private final RestrictedElement target;

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

    public void read(Location location, ErrorListener errorListener) {
        for (AccessRestriction ar : this)
            ar.read(location,target,errorListener);
    }

    public void written(Location location, ErrorListener errorListener) {
        for (AccessRestriction ar : this)
            ar.written(location,target,errorListener);
    }



    public static final Restrictions NONE = new Restrictions(new RestrictedElement() {
        public String toString() { return "NONE"; }
    });

    abstract static class Parser implements AnnotationVisitor {
        private List<Type> restrictions = new ArrayList<Type>();
        private final RestrictedElement target;

        protected Parser(RestrictedElement target) {
            this.target = target;
        }

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
            Restrictions r = new Restrictions(target);
            for (Type t : restrictions) {
                r.add(f.get(t));
            }
            return r;
        }
    }
}
