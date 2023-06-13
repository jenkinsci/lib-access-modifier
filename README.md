# Access modifier

Allows applications to define custom access modifiers programmatically,
to be enforced at compile time in the opt-in basis. Obviously,
there's no runtime check either --- this is strictly a voluntary annotations.

This mechanism is useful for actually making sure that deprecated features are not used
(without actually removing such declarations, which would break binary compatibility.)

## What is this?

This library defines an annotation and extensible mechanism to let you define your own access modifiers.
Those additional custom access modifiers can be enforced when you compile other source files that refer to them.

For example, consider a deprecated feature in your library that you plan to remove in the near future.
If you just put `@Deprecated`, code can be still written to use them quite casually (I still often use `Date.toGMTString()`, for example).
But if you remove it, it'll break existing applications out there.
Custom access modifier can fix this.
With the following annotation on your method, you can flag an error whenever someone tries to call this method.

```java
public class Library {
    @Deprecated @Restricted(DoNotUse.class)
    public void foo() {
        ...
    }
}
```

These checks are not enforced during the runtime, so in this way, you can keep the old application working, and at the same time prevent the new code from using this feature.
(I should point out that technically speaking the enforcement is optional, even during the compile time -- after all, it's just a semantics imposed by a library external to Javac.)

## Extensible Access Modifiers

The library comes with a few built-in access modifiers, but your application can define your own by extending the `AccessRestriction` type.
For example, maybe you want to allow read but prevent write to a field.
Maybe you want to only allow certain methods to be called from within the constructor (or the other way around.)

There's a lot of freedom.

## Enforcing Access Modifiers

Currently we provide a Maven mojo to enforce access modifiers.
Use it like the following:

```xml
<plugin>
  <groupId>org.kohsuke</groupId>
  <artifactId>access-modifier-checker</artifactId>
  <version>1.0</version>
  <executions>
    <execution>
      <goals>
        <goal>enforce</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```
