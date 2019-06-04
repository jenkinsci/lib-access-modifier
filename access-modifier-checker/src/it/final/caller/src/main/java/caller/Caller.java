package caller;

import api.Api;

public class Caller {

    static void x() {
        Api x = null;
        x.notReallyFinal(); // should not fail
    }

}
