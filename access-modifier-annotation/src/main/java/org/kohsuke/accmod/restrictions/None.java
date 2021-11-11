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
    @Override
    public void usedAsSuperType(Location loc, RestrictedElement target, ErrorListener errorListener) {
    }

    @Override
    public void usedAsInterface(Location loc, RestrictedElement target, ErrorListener errorListener) {
    }

    @Override
    public void instantiated(Location loc, RestrictedElement target, ErrorListener errorListener) {
    }

    @Override
    public void invoked(Location loc, RestrictedElement target, ErrorListener errorListener) {
    }

    @Override
    public void read(Location loc, RestrictedElement target, ErrorListener errorListener) {
    }

    @Override
    public void written(Location loc, RestrictedElement target, ErrorListener errorListener) {
    }
}
