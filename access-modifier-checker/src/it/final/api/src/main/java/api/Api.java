package api;

import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.Final;

public interface Api {

    @Restricted(value = Final.class, message="Because we say so")
    default void notReallyFinal() {}

    default void okToOverride() {}
}
