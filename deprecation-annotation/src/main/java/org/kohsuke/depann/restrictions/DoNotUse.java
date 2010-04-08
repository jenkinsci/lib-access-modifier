package org.kohsuke.depann.restrictions;

import org.kohsuke.depann.AccessRestriction;
import org.kohsuke.depann.impl.ErrorListener;
import org.kohsuke.depann.impl.Location;

/**
 * This type, field, or method shall never be referenced from anywhere at all.
 *
 * @author Kohsuke Kawaguchi
 */
public class DoNotUse implements AccessRestriction {
    public void written(Location loc, ErrorListener errorListener) {
        error(loc,errorListener);
    }

    public void usedAsSuperType(Location loc, ErrorListener errorListener) {
        error(loc,errorListener);
    }

    public void usedAsInterface(Location loc, ErrorListener errorListener) {
        error(loc,errorListener);
    }

    public void instantiated(Location loc, ErrorListener errorListener) {
        error(loc,errorListener);
    }

    public void invoked(Location loc, ErrorListener errorListener) {
        error(loc,errorListener);
    }

    public void read(Location loc, ErrorListener errorListener) {
        error(loc,errorListener);
    }

    public void error(Location loc, ErrorListener errorListener) {
        errorListener.onError(null,loc,"");
    }
}
