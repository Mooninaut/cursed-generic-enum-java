package io.github.mooninaut;

import java.util.*;

public final class GenericEnumMap implements GenericMap {
    private final EnumMap<GenericEnumKey, Object> map;

    public GenericEnumMap() {
        this.map = new EnumMap<>(GenericEnumKey.class);
    }
    public GenericEnumMap(final GenericMap genericEnumMap) {
        this.map = new EnumMap<>(GenericEnumKey.class);
        this.map.putAll(((GenericEnumMap) genericEnumMap).map);
    }

    @Override
    public <T> T get(final GenericKey<T> key) {
        return key.baseClass().cast(map.get((GenericEnumKey) key));
    }

    @Override
    public boolean containsKey(final GenericKey<?> key) {
        return map.containsKey((GenericEnumKey) key);
    }

    @Override
    public <T, K extends GenericKey<T>> T put(final K key, final T value) {
        final T t = key.cast(Objects.requireNonNull(value, "value"));
        return key.cast(map.put((GenericEnumKey) key, t));
    }

    @Override
    public boolean remove(final GenericKey<?> key) {
        if (containsKey(key)) {
            map.remove((GenericEnumKey) key);
            return true;
        }
        return false;
    }
}
