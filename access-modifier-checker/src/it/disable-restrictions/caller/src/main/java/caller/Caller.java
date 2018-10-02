package caller;

import api.ApiWithRestrictedMethodAndField;
import api.RestrictedApi;
import org.kohsuke.accmod.restrictions.disable.DisableRestriction;

public class Caller extends ApiWithRestrictedMethodAndField { // This is fine, ApiWithRestrictedMethodAndField itself is not restricted

    private RestrictedApi restrictedApi;

    @DisableRestriction(ApiWithRestrictedMethodAndField.class)
    public Caller() {
        ApiWithRestrictedMethodAndField.notReallyPublic(); // illegal but check disabled at the method level
    }

    @DisableRestriction({RestrictedApi.class, ApiWithRestrictedMethodAndField.class})
    private void invalidFieldUse() {
        restrictedApi.field = null;
        super.field = null;
    }

    @DisableRestriction(ApiWithRestrictedMethodAndField.class)
    public void callerMethod() {
        ApiWithRestrictedMethodAndField.notReallyPublic(); // illegal but check disabled at the method level
    }

    @DisableRestriction(RestrictedApi.class)
    public void methodWithRestrictedParameter(RestrictedApi api) {
        api.doNotUse(); // illegal but check disabled at the method level
    }

    @DisableRestriction(RestrictedApi.class)
    public RestrictedApi getRestrictedApi() {
        return new RestrictedApi(); // illegal but check disabled at the method level
    }
}
