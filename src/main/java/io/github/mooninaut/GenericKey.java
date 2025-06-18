package io.github.mooninaut;

import java.util.List;

public sealed interface GenericKey<T> permits GenericEnumKey {
    Class<T> baseClass();

    List<Class<?>> interfaces();

    boolean isInstance(T t);

    T cast(Object o);

    default T coerceCast(final T t) {
        return cast(t);
    }

    String keyName();
}
