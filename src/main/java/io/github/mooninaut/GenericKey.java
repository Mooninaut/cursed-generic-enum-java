package io.github.mooninaut;

import java.util.List;

public sealed interface GenericKey<T> permits GenericEnumKey {
    Class<T> keyType();

    List<Class<?>> extraInterfaces();

    Class<?> isInstance(T t);

    T cast(Object o);

    String keyName();
}
