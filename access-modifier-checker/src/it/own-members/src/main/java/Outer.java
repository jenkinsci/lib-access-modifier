import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.DoNotUse;

class Outer {
  @Restricted(DoNotUse.class)
  static class Middle {
    static class Inner {
      static {new Middle();}
    }
  }
}