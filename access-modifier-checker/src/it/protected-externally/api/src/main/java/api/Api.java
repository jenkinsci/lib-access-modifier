package api;

import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.ProtectedExternally;

public interface Api {

    @Restricted(ProtectedExternally.class)
    default void notReallyPublic() {}

    static void friend() {
        Api x = null;
        x.notReallyPublic(); // OK
    }

}
