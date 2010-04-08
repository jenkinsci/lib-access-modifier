package org.kohsuke.depann.restrictions;

import org.kohsuke.depann.AccessRestriction;

/**
 * This type, field, or method shall never be referenced from anywhere at all.
 *
 * @author Kohsuke Kawaguchi
 */
public class DoNotUse implements AccessRestriction {
}
