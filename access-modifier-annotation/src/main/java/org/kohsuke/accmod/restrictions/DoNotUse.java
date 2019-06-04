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
package org.kohsuke.accmod.restrictions;

import org.kohsuke.accmod.AccessRestriction;
import org.kohsuke.accmod.impl.ErrorListener;
import org.kohsuke.accmod.impl.Location;
import org.kohsuke.accmod.impl.RestrictedElement;

/**
 * This type, field, or method shall never be referenced from anywhere at all.
 *
 * @author Kohsuke Kawaguchi
 */
public class DoNotUse extends AccessRestriction {
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

    public void overridden(Location loc, RestrictedElement target, ErrorListener errorListener) {
        error(loc,target,errorListener);
    }

    public void read(Location loc, RestrictedElement target, ErrorListener errorListener) {
        error(loc,target,errorListener);
    }

    public void error(Location loc, RestrictedElement target, ErrorListener errorListener) {
        errorListener.onError(null,loc,target+" must not be used. " + target.message());
    }

    @Override
    public boolean appliesToNested() {
        return true;
    }

}
