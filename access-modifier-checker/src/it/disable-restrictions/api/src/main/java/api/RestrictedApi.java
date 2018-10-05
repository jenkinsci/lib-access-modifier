package api;

import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

@Restricted(NoExternalUse.class)
public class RestrictedApi {

    public String field;

    public void doNotUse() {}
}
