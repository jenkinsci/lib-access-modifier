package caller2;

import api.Api;

public class Caller2 {

    static void x() {
        Api x = null;
        x.notReallyPublic(); // should fail
    }

}
