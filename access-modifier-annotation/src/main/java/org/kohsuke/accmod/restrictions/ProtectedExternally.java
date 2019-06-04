/*
 * The MIT License
 *
 * Copyright 2017 CloudBees, Inc.
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
 * A method which may be implemented/overridden from anywhere, including {@code super} calls, but may not be otherwise called outside the source module.
 * Akin to using the {@code protected} modifier but allowing “package” access elsewhere in the same package root, and also supporting interface methods.
 * Note that {@link Restricted} is not {@link Inherited} so this only protects attempted accesses via the defining type;
 * you will generally also want to restrict the implementations (for example as {@link DoNotUse}).
 * @since FIXME
 */
public class ProtectedExternally extends None {

    @Override
    public void invoked(Location loc, RestrictedElement target, ErrorListener errorListener) {
        if (target.isInTheInspectedModule()) {
            return;
        }
        try (InputStream is = loc.getDependencyClassLoader().getResourceAsStream(loc.getClassName().replace('.', '/') + ".class")) {
            if (is == null) {
                errorListener.onError(null, loc, "could not find class");
                return;
            }
            ClassReader cr = new ClassReader(is);
            final AtomicBoolean ok = new AtomicBoolean();
            final String supe = target.toString().replaceFirst("[.].+$", "");
            cr.accept(new ClassVisitor(Opcodes.ASM5) {
                @Override
                public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                    // TODO traverse supertype hierarchy recursively
                    if (supe.equals(superName) || interfaces != null && Arrays.asList(interfaces).contains(supe)) {
                        ok.set(true);
                    }
                }
            }, ClassReader.SKIP_FRAMES);
            if (ok.get()) {
                return;
            }
        } catch (IOException x) {
            errorListener.onError(x, loc, "cannot inspect caller");
            return;
        }
        errorListener.onError(null, loc, target + " must not be called except as if protected. " + target.message());
    }

}
