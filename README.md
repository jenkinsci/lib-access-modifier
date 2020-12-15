# Access modifier

Allows applications to define custom access modifiers programmatically,
to be enforced at compile time in the opt-in basis. Obviously,
there's no runtime check either --- this is strictly a voluntary annotations.

This mechanism is useful for actually making sure that deprecated features are not used
(without actually removing such declarations, which would break binary compatibility.)
