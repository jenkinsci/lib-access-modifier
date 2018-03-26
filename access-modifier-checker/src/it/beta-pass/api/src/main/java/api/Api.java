package api;

import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.Beta;

public class Api {

    @Restricted(Beta.class)
    public static void experimental() {}

    static {
        experimental(); // OK
    }

}
