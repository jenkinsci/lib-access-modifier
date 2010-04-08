package org.kohsuke.depann;

/**
 * @author Kohsuke Kawaguchi
 */
public interface ErrorListener {
    void onError(Throwable t, String msg);
    void onWarning(Throwable t, String msg);

    /**
     * No-op listener.
     */
    ErrorListener NULL = new ErrorListener() {
        public void onError(Throwable t, String msg) {
        }

        public void onWarning(Throwable t, String msg) {
        }
    };
}
