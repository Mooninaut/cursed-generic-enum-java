package io.github.mooninaut;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.RandomAccess;

public enum GenericEnumKey implements GenericKey { // cursed raw type
    _STRING(String.class),
    _INTEGER(Integer.class),
    _DOUBLE(Double.class),
    _OTHER_DOUBLE(Double.class),
    _OUT(PrintStream.class),
    _ERR(PrintStream.class),
    _IN(InputStream.class),
    _FLOATS(List.class, List.of(RandomAccess.class)),
    ;

    private final Class<?> aClass;
    private final List<Class<?>> extraInterfaces;

    GenericEnumKey(final Class<?> aClass) {
        this(aClass, null);
    }

    GenericEnumKey(final Class<?> aClass, final List<Class<?>> extraInterfaces) {
        this.aClass = aClass;
        this.extraInterfaces = extraInterfaces;
    }

    @Override
    public Class<?> keyType() {
        return aClass;
    }

    @Override
    public List<Class<?>> extraInterfaces() {
        return extraInterfaces;
    }

    @Override
    public boolean typeCheck(final Object o) {
        final boolean result = aClass.isInstance(o);
        if (!result || extraInterfaces == null) {
            return result;
        }
        for (final var interfaceClass : extraInterfaces) {
            if (!interfaceClass.isInstance(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Object cast(final Object o) {
        if (typeCheck(o)) {
            return o;
        }
        throw new IllegalArgumentException("Failed type check for " + keyName() + ": " + o.getClass());
    }

    @Override
    public String keyName() {
        return name() + '(' + aClass.getSimpleName() + ')';
    }

    @Override
    public String toString() {
        return "GenericEnumKey{" + keyName() + '}';
    }

    public <T, KEY extends Enum<GenericEnumKey> & GenericKey<T>> KEY typeCheck(Class<T> tClass) {
        if (tClass == aClass) {
            return (KEY) this; // unchecked but safe
        }
        throw new IllegalArgumentException("Class " + tClass.getName() + " incorrect for " + keyName());
    }

    // cursed accessors
    public static <T extends Enum<GenericEnumKey> & GenericKey<String>> T STRING() {
        return GenericEnumKey._STRING.typeCheck(String.class);
    }
    public static <T extends Enum<GenericEnumKey> & GenericKey<Integer>> T INTEGER() {
        return GenericEnumKey._INTEGER.typeCheck(Integer.class);
    }
    public static <T extends Enum<GenericEnumKey> & GenericKey<Double>> T DOUBLE() {
        return GenericEnumKey._DOUBLE.typeCheck(Double.class);
    }

    // Demonstrates that this pattern supports distinct keys of the same type,
    // unlike the one in Effective Java that uses a map of Class objects to values.

    // On the other hand, this pattern only supports a fixed set of enum values, rather
    // than objects of any class.
    public static <T extends Enum<GenericEnumKey> & GenericKey<Double>> T OTHER_DOUBLE() {
        return GenericEnumKey._OTHER_DOUBLE.typeCheck(Double.class);
    }
    public static <T extends Enum<GenericEnumKey> & GenericKey<PrintStream>> T OUT() {
        return GenericEnumKey._OUT.typeCheck(PrintStream.class);
    }
    public static <T extends Enum<GenericEnumKey> & GenericKey<PrintStream>> T ERR() {
        return GenericEnumKey._ERR.typeCheck(PrintStream.class);
    }
    public static <T extends Enum<GenericEnumKey> & GenericKey<InputStream>> T IN() {
        return GenericEnumKey._IN.typeCheck(InputStream.class);
    }
    public static <L extends List<Float> & RandomAccess,
            T extends Enum<GenericEnumKey> & GenericKey<L>> T FLOATS_RANDOM_ACCESS() {
        return GenericEnumKey._FLOATS.typeCheck((Class<L>) (Class) List.class);
    }
}
