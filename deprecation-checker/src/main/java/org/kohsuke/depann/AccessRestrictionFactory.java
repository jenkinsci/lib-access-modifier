package org.kohsuke.depann;

import org.objectweb.asm.Type;

import java.util.HashMap;
import java.util.Map;

/**
 * Pool of {@link AccessRestriction} instances.
 *
 * @author Kohsuke Kawaguchi
 */
public class AccessRestrictionFactory {
    private final Map<String,AccessRestriction> instances = new HashMap<String, AccessRestriction>();
    private final ClassLoader cl;

    public AccessRestrictionFactory(ClassLoader cl) {
        this.cl = cl;
    }

    public AccessRestriction get(Type type) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        String className = type.getClassName();
        AccessRestriction a = instances.get(className);
        if (a!=null)        return a;

        // in this way, even if the restriction fails to load, we'll just report an error once
        a = NO_RESTRICTION;
        try {
            Class<?> c = cl.loadClass(className);
            a = (AccessRestriction)c.newInstance();
            return a;
        } finally {
            instances.put(className,a);
        }
    }

    private static final AccessRestriction NO_RESTRICTION = new AccessRestriction(){};
}
