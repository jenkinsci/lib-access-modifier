package org.kohsuke.accmod.restrictions;

import org.kohsuke.accmod.impl.ErrorListener;
import org.kohsuke.accmod.impl.Location;
import org.kohsuke.accmod.impl.RestrictedElement;

/**
 * References are allowed only when they are within the same module
 * (that is, if the reference is compiled at the same time as the restricted element.)
 *
 * <p>
 * Otherwise the access is rejected.
 *
 * @author Kohsuke Kawaguchi
 */
public class NoExternalUse extends DoNotUse {
    @Override
    public void error(Location loc, RestrictedElement target, ErrorListener errorListener) {
        if (target.isInTheInspectedModule())
            return; // OK as long as the use happens in the same module
        super.error(loc, target, errorListener);
    }
}
