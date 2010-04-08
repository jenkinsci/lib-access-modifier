package org.kohsuke.depann.impl;

/**
 * @author Kohsuke Kawaguchi
 */
public interface ErrorListener {
    void onError(Throwable t, Location loc, String msg);
    void onWarning(Throwable t, Location loc, String msg);

    /**
     * No-op listener.
     */
    ErrorListener NULL = new ErrorListener() {
        public void onError(Throwable t, Location loc, String msg) {
        }

        public void onWarning(Throwable t, Location loc, String msg) {
        }
    };
}
