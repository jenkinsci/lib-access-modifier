/*
 * The MIT License
 *
 * Copyright (c) 2010, Kohsuke Kawaguchi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.kohsuke.accmod;

import org.kohsuke.accmod.impl.ErrorListener;
import org.kohsuke.accmod.impl.Location;
import org.kohsuke.accmod.impl.RestrictedElement;
import org.kohsuke.accmod.restrictions.None;

/**
 * Access restriction policy &mdash; determines what access is OK and what are not.
 *
 * <p>
 * The subtype of this interface is a strategy object. Instances of
 * these classes are created during the access enforcement to perform constraint checks.
 *
 * <p>
 * Single execution of the enforcement check would create at most one instance
 * of a given {@link AccessRestriction} type, so instance fields can be used to store
 * heavy-weight objects or other indices that you might need for implementing
 * access control checks. 
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class AccessRestriction {
    /**
     * The type on which this restriction is placed is used as a supertype by another class.
     * The location points to the subtype.
     *
     * @param loc
     *      Points to the subtype.
     * @param errorListener
     *      Report any error here.
     */
    public abstract void usedAsSuperType(Location loc, RestrictedElement target, ErrorListener errorListener);

    /**
     * The type on which this restriction is placed is used as an interface that another class/interface implements.
     *
     * @param loc
     *      Points to the subtype.
     * @param errorListener
     *      Report any error here.
     */
    public abstract void usedAsInterface(Location loc, RestrictedElement target, ErrorListener errorListener);

    /**
     * The type on which this restriction is placed is instantiated elsewhere.
     */
    public abstract void instantiated(Location loc, RestrictedElement target, ErrorListener errorListener);

    /**
     * The method on which this restriction is placed is invoked elsewhere.
     */
    public abstract void invoked(Location loc, RestrictedElement target, ErrorListener errorListener);

    /**
     * The method on which this restriction is placed is overridden elsewhere.
     */
    public abstract void overridden(Location loc, RestrictedElement target, ErrorListener errorListener);

    /**
     * The field on which this restriction is placed is read.
     */
    public abstract void read(Location loc, RestrictedElement target, ErrorListener errorListener);

    /**
     * The field on which this restriction is placed is updated.
     */
    public abstract void written(Location loc, RestrictedElement target, ErrorListener errorListener);

    /**
     * Whether this access restriction, if applied to a type, should also be considered to apply implicitly to all transitively nested members.
     * @return by default, false
     */
    public boolean appliesToNested() {
        return false;
    }

    /**
     * {@link AccessRestriction} that imposes no restriction.
     */
    public static final AccessRestriction NONE = new None();
}
