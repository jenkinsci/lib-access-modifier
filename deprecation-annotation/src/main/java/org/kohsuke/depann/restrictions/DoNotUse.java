package org.kohsuke.depann.restrictions;

import org.kohsuke.depann.AccessRestriction;
import org.kohsuke.depann.impl.ErrorListener;
import org.kohsuke.depann.impl.Location;
import org.kohsuke.depann.impl.RestrictedElement;

/**
 * This type, field, or method shall never be referenced from anywhere at all.
 *
 * @author Kohsuke Kawaguchi
 */
public class DoNotUse implements AccessRestriction {
    public void written(Location loc, RestrictedElement target, ErrorListener errorListener) {
        error(loc,target,errorListener);
    }

    public void usedAsSuperType(Location loc, RestrictedElement target, ErrorListener errorListener) {
        error(loc,target,errorListener);
    }

    public void usedAsInterface(Location loc, RestrictedElement target, ErrorListener errorListener) {
        error(loc,target,errorListener);
    }

    public void instantiated(Location loc, RestrictedElement target, ErrorListener errorListener) {
        error(loc,target,errorListener);
    }

    public void invoked(Location loc, RestrictedElement target, ErrorListener errorListener) {
        error(loc,target,errorListener);
    }

    public void read(Location loc, RestrictedElement target, ErrorListener errorListener) {
        error(loc,target,errorListener);
    }

    public void error(Location loc, RestrictedElement target, ErrorListener errorListener) {
        errorListener.onError(null,loc,target+" must not be used");
    }
}
