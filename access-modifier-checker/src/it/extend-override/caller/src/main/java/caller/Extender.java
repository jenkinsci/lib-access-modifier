package caller;

import api.Api;
// TODO this is actually two tests, separate them when fixing
public class Extender extends Api {
    public void foo() {
        notReallyPublic();
    }

    @Override
    public void notReallyPublic() {
    }
}
