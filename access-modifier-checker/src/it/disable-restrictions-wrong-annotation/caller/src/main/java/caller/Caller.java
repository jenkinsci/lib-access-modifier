package caller;

import api.ApiWithRestrictedMethodAndField;

public class Caller {

    @WrongAnnotation(ApiWithRestrictedMethodAndField.class)
    public Caller() {
        ApiWithRestrictedMethodAndField.notReallyPublic(); // illegal
    }
}
