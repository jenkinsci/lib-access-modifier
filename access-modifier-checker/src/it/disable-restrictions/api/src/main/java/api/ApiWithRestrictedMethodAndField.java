package api;

import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

public class ApiWithRestrictedMethodAndField {

    @Restricted(NoExternalUse.class)
    public String field;

    @Restricted(NoExternalUse.class)
    public static void notReallyPublic() {}
}
