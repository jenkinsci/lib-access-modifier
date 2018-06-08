import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.DoNotUse;

@Restricted(DoNotUse.class)
public class Subtype extends Base<Integer> {
  @Override
  public Integer doStuff() {
    return 42;
  }
}
