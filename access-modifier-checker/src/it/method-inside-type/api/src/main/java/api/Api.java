package api;

import api.impl.NotApi;

public class Api {

    public Api() {}

    public static void actuallyPublic() {
        NotApi.notReallyPublic(); // OK
        new NotApi().notReallyPublicF.hashCode(); // OK
        NotApi.AlsoNotApi.alsoNotReallyPublic(); // OK
    }

    public final Object actuallyPublicF = new Object();

}
