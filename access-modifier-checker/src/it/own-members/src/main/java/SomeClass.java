import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.DoNotUse;

@Restricted(DoNotUse.class)
public class SomeClass {
  private int foo;

  public SomeClass() {
    foo = 12;
  }

  public int getFoo() {
    doSomething();
    return foo;
  }

  private void doSomething() {}
}
