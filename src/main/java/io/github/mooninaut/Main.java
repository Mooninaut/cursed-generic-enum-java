package io.github.mooninaut;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;

import static io.github.mooninaut.GenericEnumKey.*;

public final class Main {
    private Main() {}
    public static void main(String[] args) {
        final GenericMap map = new GenericEnumMap();

        map.put(OUT(), System.out);
        map.put(IN(), System.in);
        map.put(ERR(), System.err);

        final PrintStream out = map.get(OUT());

        map.put(DOUBLE(), Math.PI);
        map.put(INTEGER(), -8);
        map.put(STRING(), "Hi there, %s!\n");
        map.put(OTHER_DOUBLE(), Math.E);

        out.println();

        final Double pi = map.get(DOUBLE());
        out.println(Math.cos(pi));
        final Integer i = map.get(INTEGER());
        out.println(Integer.signum(i));
        final String format = map.get(STRING());
        out.printf(format, "user");
        final Double e = map.get(OTHER_DOUBLE());
        out.println(Math.log(e));

        final InputStream in = map.get(IN());
        out.println(in.markSupported());

        map.put(FLOATS_RANDOM_ACCESS(), new ArrayList<>());

        final var floats = map.get(FLOATS_RANDOM_ACCESS());

        // map.put(FLOATS(), new LinkedList<>()); // Can't, doesn't implement RandomAccess

        assert floats instanceof RandomAccess;
        assert floats instanceof List;
    }
}
