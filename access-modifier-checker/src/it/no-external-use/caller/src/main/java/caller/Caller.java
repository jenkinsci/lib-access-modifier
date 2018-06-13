package caller;

import api.Api;
import foo.Bar;

public class Caller {

    public Caller() {
        Api.notReallyPublic(); // illegal
        new Bar(); // access to anything under foo.* is illegal
    }

}
