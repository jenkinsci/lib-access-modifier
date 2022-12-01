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

import org.kohsuke.accmod.AccessRestriction;

/**
 * Indicates the location that the use occurred.
 *
 * @author Kohsuke Kawaguchi
 */
public interface Location {
    /**
     * The fully-qualified class name in which the use happened,
     * for example "abc.def.Ghi"
     */
    String getClassName();

    /**
     * If the use happened in the byte code instruction,
     * method name that the use occurred in.
     * <p>
     * For example "getAbc"
     */
    String getMethodName();

    /**
     * This is the encoded method signature like "(II)Z"
     * in which the use happened. Used in conjunction with
     * {@link #getMethodName()} to disambiguate overload.
     */
    String getMethodDescriptor();

    /**
     * The line number in the source file where the use happened.
     */
    int getLineNumber();

    /**
     * Obtains a human readable description of the location.
     * Useful for an error message.
     */
    @Override
    String toString();

    /**
     * {@link AccessRestriction} implementations can use this classloader
     * to access the classes referenced by classes being inspected.
     *
     * <p>
     * Loading a class has a side effect, so it's generally not recommended
     * to do so, but the caller can use {@link ClassLoader#getResource(String)}
     * and parse the class files via libraries like ASM to define more elaborate
     * access restrictions.
     */
    ClassLoader getDependencyClassLoader();

    /**
     * Loads a configuration setting from the environment, such as when configured by a Maven plugin.
     */
    /*@CheckForNull*/ String getProperty(String key);

}
