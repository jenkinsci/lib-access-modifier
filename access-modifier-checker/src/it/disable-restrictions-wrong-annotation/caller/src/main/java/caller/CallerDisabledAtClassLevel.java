package caller;

import api.ApiWithRestrictedMethodAndField;

@WrongAnnotation(ApiWithRestrictedMethodAndField.class)
public class CallerDisabledAtClassLevel {
    public CallerDisabledAtClassLevel() {
        ApiWithRestrictedMethodAndField.notReallyPublic(); // illegal
    }
}