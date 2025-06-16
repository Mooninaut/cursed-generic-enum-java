package io.github.mooninaut;

public sealed interface GenericMap permits GenericEnumMap {
    boolean containsKey(GenericKey<?> key);
    <T> T get(GenericKey<T> key);

    <T, K extends GenericKey<T>> T put(final K key, final T value);
}
