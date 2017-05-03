package caller1;

import api.Api;

public class Caller1 implements Api {

    @Override
    public void notReallyPublic() {
        Api.super.notReallyPublic(); // legal
    }

    static void x() {
        new Caller1().notReallyPublic(); // external call but OK since we are inside the implementing type
    }

}
