package io.github.mooninaut;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public enum GenericEnumKey implements GenericKey /* cursed: raw type */ {
    STRING(String.class),
    AN_INTEGER(Integer.class),
    A_DOUBLE(Double.class),
    ANOTHER_DOUBLE(Double.class),
    OUT(PrintStream.class),
    ERR(PrintStream.class),
    IN(InputStream.class),
    FLOATS_RANDOM_ACCESS(List.class, RandomAccess.class),
    OUT_PRINTLN_STRING(Consumer.class),
    ;

    private final Class<?> baseClass;
    private final Class<?>[] interfaces;

    GenericEnumKey(Class<?> baseClass) {
        this(baseClass, new Class[0]);
    }

    GenericEnumKey(final Class<?> baseClass, Class<?> ...interfaces) {
        this.baseClass = baseClass;
        this.interfaces = validateInterfaces(interfaces);
    }

    private static Class<?>[] validateInterfaces(final Class<?> ...interfacesToCheck) {
        for (Class<?> interfaceToCheck : interfacesToCheck) {
            if (!interfaceToCheck.isInterface()) {
                throw new IllegalArgumentException("Not an interface: " + interfaceToCheck);
            }
        }
        return interfacesToCheck;
    }

    @Override
    public Class<?> baseClass() {
        return baseClass;
    }

    @Override
    public List<Class<?>> interfaces() {
        return List.of(interfaces);
    }

    @Override
    public boolean isInstance(final Object o) {
        return checkInstance(o) == null;
    }

    /**
     * @return The first class or interface that `o` does not extend or
     *         implement, or null if it properly extends or implements
     *         all classes and interfaces this key requires.
     */
    private Class<?> checkInstance(final Object o) {
        if (o == null) {
            return null;
        }
        if (!baseClass.isInstance(o)) {
            return baseClass;
        }
        if (interfaces == null) {
            return null;
        }
        for (final var interfaceClass : interfaces) {
            if (!interfaceClass.isInstance(o)) {
                return interfaceClass;
            }
        }
        return null;
    }

    @Override
    public Object cast(final Object o) {
        final Class<?> failedClass = checkInstance(o);
        if (failedClass == null) {
            return o;
        }
        throw new ClassCastException("Key " + keyName() + " rejected value of type " +
                o.getClass().getName() + ": Does not " +
                (failedClass.isInterface() ? "implement interface " : "extend class ") +
                failedClass.getName());
    }

    @Override
    public String keyName() {
        if (interfaces == null) {
            return name() + '(' + baseClass.getSimpleName() + ')';
        }
        return Arrays.stream(interfaces)
                .map(Class::getSimpleName)
                .collect(Collectors.joining(",",
                        name() + '(' + baseClass.getSimpleName() + ",",
                        ")"));
    }

    @Override
    public String toString() {
        return "GenericEnumKey{" + keyName() + '}';
    }

    private <T, KEY extends Enum<GenericEnumKey> & GenericKey<T>>
    KEY typeAssert(final Class<T> tClass) {
        if (tClass == baseClass) {
            return (KEY) this; // cursed: unchecked cast
        }
        throw new IllegalArgumentException("Class " + tClass.getName() + " incorrect for " + keyName());
    }

    // Since there are no generic class literals, an enum constant whose type
    // parameter is itself generic has to have that parameter erased in order
    // to perform the raw type check.
    private <T> T rawTypeAssert(final Class<?> /* cursed: wildcard type */ tClass) {
        if (tClass == baseClass) {
            return (T) this; // cursed: unchecked cast
        }
        throw new IllegalArgumentException("Class " + tClass.getName() + " incorrect for " + keyName());
    }

    // cursed accessors

    // Each (cursed) accessor returns a value with a non-reifiable but statically
    // and (as far as erasure allows) dynamically correct type, which can only
    // be inhabited by an enum constant of this class with the correct raw base
    // type.

    public static <T extends Enum<GenericEnumKey> & GenericKey<String>>
    T string() {
        return GenericEnumKey.STRING.typeAssert(String.class);
    }

    public static <T extends Enum<GenericEnumKey> & GenericKey<Integer>>
    T anInteger() {
        return GenericEnumKey.AN_INTEGER.typeAssert(Integer.class);
    }

    public static <T extends Enum<GenericEnumKey> & GenericKey<Double>>
    T aDouble() {
        return GenericEnumKey.A_DOUBLE.typeAssert(Double.class);
    }

    // Demonstrates that this pattern supports distinct keys of the same type,
    // unlike the one in Effective Java that uses a map of Class objects to values.

    // On the other hand, this pattern only supports a fixed set of enum values, rather
    // than objects of any class. This may be beneficial, depending on the use case.
    public static <T extends Enum<GenericEnumKey> & GenericKey<Double>>
    T anotherDouble() {
        return GenericEnumKey.ANOTHER_DOUBLE.typeAssert(Double.class);
    }

    public static <T extends Enum<GenericEnumKey> & GenericKey<PrintStream>>
    T out() {
        return GenericEnumKey.OUT.typeAssert(PrintStream.class);
    }

    public static <T extends Enum<GenericEnumKey> & GenericKey<PrintStream>>
    T err() {
        return GenericEnumKey.ERR.typeAssert(PrintStream.class);
    }

    public static <T extends Enum<GenericEnumKey> & GenericKey<InputStream>>
    T in() {
        return GenericEnumKey.IN.typeAssert(InputStream.class);
    }

    // Has to use rawTypeAssert to avoid an even more cursed unchecked raw cast:
    // (Class<L>) (Class) List.class
    public static <T extends Enum<GenericEnumKey> & GenericKey<L>,
                   L extends List<Float> & RandomAccess>
    T floatsRandomAccess() {
        return GenericEnumKey.FLOATS_RANDOM_ACCESS.rawTypeAssert(List.class);
    }

    // Has to use rawTypeAssert to avoid an even more cursed unchecked raw cast:
    // (Class<Consumer<String>>) (Class) Consumer.class
    public static <T extends Enum<GenericEnumKey> & GenericKey<Consumer<String>>>
    T outPrintlnString() {
        return GenericEnumKey.OUT_PRINTLN_STRING.rawTypeAssert(Consumer.class);
    }
}
