package org.kohsuke.depann;

import org.kohsuke.depann.impl.ErrorListener;
import org.kohsuke.depann.impl.Location;
import org.kohsuke.depann.impl.RestrictedElement;

/**
 * Access restriction policy &mdash; determines what access is OK and what are not.
 *
 * <p>
 * The subtype of this interface is a stateless strategy object.
 *
 * @author Kohsuke Kawaguchi
 */
public interface AccessRestriction {
    /**
     * The type on which this restriction is placed is used as a supertype by another class.
     * The location points to the subtype.
     *
     * @param loc
     *      Points to the subtype.
     * @param errorListener
     *      Report any error here.
     */
    void usedAsSuperType(Location loc, RestrictedElement target, ErrorListener errorListener);

    /**
     * The type on which this restriction is placed is used as an interface that another class/interface implements.
     *
     * @param loc
     *      Points to the subtype.
     * @param errorListener
     *      Report any error here.
     */
    void usedAsInterface(Location loc, RestrictedElement target, ErrorListener errorListener);

    /**
     * The type on which this restriction is placed is instantiated elsewhere.
     */
    void instantiated(Location loc, RestrictedElement target, ErrorListener errorListener);

    /**
     * The method on which this restriction is placed is invoked elsewhere.
     */
    void invoked(Location loc, RestrictedElement target, ErrorListener errorListener);

    /**
     * The field on which this restriction is placed is read.
     */
    void read(Location loc, RestrictedElement target, ErrorListener errorListener);

    /**
     * The field on which this restriction is placed is updated.
     */
    void written(Location loc, RestrictedElement target, ErrorListener errorListener);
}
