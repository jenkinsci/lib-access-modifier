package caller;

import api.ApiWithRestrictedMethodAndField;
import api.RestrictedApi;
import org.kohsuke.accmod.restrictions.suppressions.SuppressRestrictedWarnings;

public class Caller extends ApiWithRestrictedMethodAndField { // This is fine, ApiWithRestrictedMethodAndField itself is not restricted

    private RestrictedApi restrictedApi;

    @SuppressRestrictedWarnings(ApiWithRestrictedMethodAndField.class)
    public Caller() {
        ApiWithRestrictedMethodAndField.notReallyPublic(); // illegal but check disabled at the method level
    }

    @SuppressRestrictedWarnings({RestrictedApi.class, ApiWithRestrictedMethodAndField.class})
    private void invalidFieldUse() {
        restrictedApi.field = null;
        super.field = null;
    }

    @SuppressRestrictedWarnings(ApiWithRestrictedMethodAndField.class)
    public void callerMethod() {
        ApiWithRestrictedMethodAndField.notReallyPublic(); // illegal but check disabled at the method level
    }

    @SuppressRestrictedWarnings(RestrictedApi.class)
    public void methodWithRestrictedParameter(RestrictedApi api) {
        api.doNotUse(); // illegal but check disabled at the method level
    }

    @SuppressRestrictedWarnings(RestrictedApi.class)
    public RestrictedApi getRestrictedApi() {
        return new RestrictedApi(); // illegal but check disabled at the method level
    }
}
