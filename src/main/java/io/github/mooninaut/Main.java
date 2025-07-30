package io.github.mooninaut;

import java.io.InputStream;
import java.util.*;
import java.util.function.Consumer;

import static io.github.mooninaut.GenericEnumKey.*;

public final class Main {
    private Main() {}
    public static void main(String[] args) {
        final GenericMap map = new GenericEnumMap();

        map.put(out(), System.out);
        map.put(in(), System.in);
        map.put(err(), System.err);
        map.put(outPrintlnString(), System.out::println);

        final var out = map.get(out());

        map.put(aDouble(), Math.PI);
        map.put(anInteger(), -8);
        map.put(string(), "Hi there, %s!\n");
        map.put(anotherDouble(), Math.E);

        out.println();

        final Double pi = map.get(aDouble());
        out.println(Math.cos(pi));
        final Integer i = map.get(anInteger());
        out.println(Integer.signum(i));
        final String format = map.get(string());
        out.printf(format, "user");
        final Double e = map.get(anotherDouble());
        out.println(Math.log(e));

        final InputStream in = map.get(in());
        out.println(in.markSupported());

        map.put(floatsRandomAccess(), new ArrayList<>());

        final var floats = map.get(floatsRandomAccess());

        assert floats instanceof RandomAccess;
        assert floats instanceof List;

        try {
            floatsRandomAccess().cast(new LinkedList<>());
        } catch (final ClassCastException cce) {
            map.get(err()).println(cce.getMessage());
        }

        outPrintlnString()
                .coerceCast(System.out::println).accept("Successful coerceCast");

        try {
            final Consumer<?> unknownConsumer = (Consumer<Integer>) System.out::println;
            final Consumer<String> stringConsumer = (Consumer<String>) unknownConsumer;
            // Erasure prevents checking the real type, only the raw type is
            // available at runtime
            outPrintlnString().coerceCast(stringConsumer)
                    .accept("Unsuccessful coerceCast");
        } catch (final ClassCastException cce) {
            map.get(err()).println(cce.getMessage());
        }
    }
}
