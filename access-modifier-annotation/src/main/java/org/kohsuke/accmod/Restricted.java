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

import org.jvnet.hudson.annotation_indexer.Indexed;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Indicates that a particular element is really deprecated and that the access to it
 * is subject to the additional restrictions.
 *
 * <p>
 * These annotations and restrictions introduced by them are enforced by the
 * "access-modifier-checker" mojo. 
 *
 * @author Kohsuke Kawaguchi
 */
@Retention(RUNTIME)
@Documented
@Indexed
public @interface Restricted {
    /**
     * Kind of access that are restricted.
     * If multiple values are specified, those restrictions are OR-ed &mdash; thus if an use
     * violates any of the restrictions, it'll be considered as an error.
     */
    Class<? extends AccessRestriction>[] value();

    /**
     * A message providing the reason for the restriction and guidance as to how to evolve code that
     * violates the restriction.
     */
    String message() default "";
}
