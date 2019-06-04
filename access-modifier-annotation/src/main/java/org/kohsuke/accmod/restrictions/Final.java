/*
 * The MIT License
 *
 * Copyright 2019 CloudBees, Inc.
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

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Inherited;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.impl.ErrorListener;
import org.kohsuke.accmod.impl.Location;
import org.kohsuke.accmod.impl.RestrictedElement;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

/**
 * A method which may not be overridden from anywhere.
 * Akin to using applying the {@code final} modifier but allowing legacy implementations to override.
 * Note that {@link Restricted} is not {@link Inherited} so this only protects attempted accesses via the defining type;
 * However, legacy overrides must be migrated to a non-final method when upgraded to a baseline with this restriction
 * so it shouldn't matter.
 * @since FIXME
 */
public class Final extends None {

    @Override
    public void overridden(Location loc, RestrictedElement target, ErrorListener errorListener) {
        errorListener.onError(null, loc, target + " must not be overridden. " + target.message());
    }
}
