package caller;

import api.ApiWithRestrictedMethodAndField;
import api.RestrictedApi;
import org.kohsuke.accmod.restrictions.suppressions.SuppressRestrictedWarnings;

@SuppressRestrictedWarnings( {ApiWithRestrictedMethodAndField.class, RestrictedApi.class})
public class CallerDisabledAtClassLevel extends RestrictedApi {

    private RestrictedApi restrictedApi;

    public CallerDisabledAtClassLevel() {
        ApiWithRestrictedMethodAndField.notReallyPublic(); // illegal but check disabled at the class level
    }

    private void invalidFieldUse() {
        restrictedApi.field = null;
        super.field = null;
    }

    public void callerMethod() {
        ApiWithRestrictedMethodAndField.notReallyPublic(); // illegal but check disabled at the class level
    }

    public void methodWithRestrictedParameter(RestrictedApi api) {
        api.doNotUse(); // illegal but check disabled at the class level
    }

    public RestrictedApi getRestrictedApi() {
        return new RestrictedApi(); // illegal but check disabled at the class level
    }
}