package api;

import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

public class Api {

    @Restricted(NoExternalUse.class)
    public static void notReallyPublic() {}

    static {
        notReallyPublic(); // OK
    }

}
