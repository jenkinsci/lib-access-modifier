package org.kohsuke.depann;

import org.jvnet.hudson.annotation_indexer.Indexed;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Indicates that a particular element is really deprecated and that the access to it
 * is subject to the additional restrictions.
 *
 * @author Kohsuke Kawaguchi
 */
@Retention(CLASS)
@Documented
@Indexed
public @interface Restricted {
    /**
     * Kind of access that are restricted.
     */
    Class<? extends AccessRestriction>[] value();
}
