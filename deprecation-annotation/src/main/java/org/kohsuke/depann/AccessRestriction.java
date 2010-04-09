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
