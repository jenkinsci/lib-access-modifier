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
package org.kohsuke.accmod.impl;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import org.kohsuke.accmod.AccessRestriction;
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

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public AccessRestrictionFactory(ClassLoader cl) {
        this.cl = cl;
    }

    public AccessRestriction get(Type type) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        String className = type.getClassName();
        AccessRestriction a = instances.get(className);
        if (a!=null)        return a;

        // in this way, even if the restriction fails to load, we'll just report an error once
        a = AccessRestriction.NONE;
        try {
            Class<?> c = cl.loadClass(className);
            a = (AccessRestriction)c.newInstance();
            return a;
        } finally {
            instances.put(className,a);
        }
    }
}
