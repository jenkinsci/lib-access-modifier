package caller;

import api.RestrictedApi;
import org.kohsuke.accmod.restrictions.disable.DisableRestriction;

@DisableRestriction(RestrictedApi.class)
public class RestrictedApiSubclass extends RestrictedApi {
}
