package org.kohsuke.depann.impl;

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

    public abstract String getMethodDescriptor();

    public abstract int getLineNumber();

    /**
     * Obtains a human readable description of the location.
     */
    public abstract String toString();
}
