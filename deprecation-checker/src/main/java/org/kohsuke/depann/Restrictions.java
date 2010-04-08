package org.kohsuke.depann;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Kohsuke Kawaguchi
 */
public class Restrictions extends ArrayList<AccessRestriction> {
    public Restrictions(Collection<? extends AccessRestriction> c) {
        super(c);
    }

    public Restrictions() {
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
