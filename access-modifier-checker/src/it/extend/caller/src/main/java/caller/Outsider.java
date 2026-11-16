package src.main.java.caller;

public class Outsider {

    public void foo() {
        new Extender().notReallyPublic();
    }

}
