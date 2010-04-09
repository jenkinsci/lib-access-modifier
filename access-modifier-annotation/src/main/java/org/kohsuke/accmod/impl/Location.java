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

/**
 * Indicates the location that the use occurred.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class Location {
    /**
     * The class name in which the use happened.
     */
    public abstract String getClassName();

    /**
     * If the use happened in the byte code instruction,
     * method name that the use occurred in.
     */
    public abstract String getMethodName();

    /**
     * This is the encoded method signature like "(II)Z"
     * in which the use happened. Used in conjunction with
     * {@link #getMethodName()} to disambiguate overload.
     */
    public abstract String getMethodDescriptor();

    /**
     * The line number in the source file where the use happened.
     */
    public abstract int getLineNumber();

    /**
     * Obtains a human readable description of the location.
     * Useful for an error message.
     */
    public abstract String toString();
}
