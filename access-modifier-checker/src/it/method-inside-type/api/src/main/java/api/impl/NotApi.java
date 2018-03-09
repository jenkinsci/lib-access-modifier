package api.impl;

import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

@Restricted(NoExternalUse.class)
public class NotApi {

    public NotApi() {}

    public static void notReallyPublic() {}

    public final Object notReallyPublicF = new Object();

    static {
        notReallyPublic(); // OK
        new NotApi().notReallyPublicF.hashCode(); // OK
        AlsoNotApi.alsoNotReallyPublic(); // OK
    }

    public static final class AlsoNotApi {

        public static void alsoNotReallyPublic() {}

    }

}
