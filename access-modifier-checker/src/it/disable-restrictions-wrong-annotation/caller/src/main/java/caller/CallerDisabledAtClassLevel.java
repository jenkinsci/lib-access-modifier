package caller;

import api.ApiWithRestrictedMethodAndField;
import api.RestrictedApi;
import org.kohsuke.accmod.restrictions.disable.DisableRestriction;

@WrongAnnotation(ApiWithRestrictedMethodAndField.class)
public class CallerDisabledAtClassLevel {
    public CallerDisabledAtClassLevel() {
        ApiWithRestrictedMethodAndField.notReallyPublic(); // illegal
    }
}