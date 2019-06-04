package org.kohsuke.accmod.restrictions;

import org.kohsuke.accmod.AccessRestriction;
import org.kohsuke.accmod.impl.ErrorListener;
import org.kohsuke.accmod.impl.Location;
import org.kohsuke.accmod.impl.RestrictedElement;

/**
 * No access restriction whatsoever.
 *
 * @author Kohsuke Kawaguchi
 */
public class None extends AccessRestriction {
    public void usedAsSuperType(Location loc, RestrictedElement target, ErrorListener errorListener) {
    }

    public void usedAsInterface(Location loc, RestrictedElement target, ErrorListener errorListener) {
    }

    public void instantiated(Location loc, RestrictedElement target, ErrorListener errorListener) {
    }

    public void invoked(Location loc, RestrictedElement target, ErrorListener errorListener) {
    }

    public void overridden(Location loc, RestrictedElement target, ErrorListener errorListener) {
    }

    public void read(Location loc, RestrictedElement target, ErrorListener errorListener) {
    }

    public void written(Location loc, RestrictedElement target, ErrorListener errorListener) {
    }
}
