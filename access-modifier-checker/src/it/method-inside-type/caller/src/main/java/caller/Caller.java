package caller;

import api.Api;
import api.impl.NotApi;

public class Caller {

    public Caller() {
        Api.actuallyPublic(); // OK
        new Api().actuallyPublicF.hashCode(); // OK
        NotApi.notReallyPublic(); // illegal
        NotApi na = new NotApi(); // illegal
        na.notReallyPublicF.hashCode(); // illegal
        NotApi.AlsoNotApi.alsoNotReallyPublic(); // illegal
    }

}
