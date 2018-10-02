package caller;

import api.RestrictedInterface;
import org.kohsuke.accmod.restrictions.disable.DisableRestriction;

@DisableRestriction(RestrictedInterface.class)
public class RestrictedInterfaceImplementation implements RestrictedInterface {
}
