/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.util;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Objects;

public final class Random {

    /**
     * generates a random String
     * @return random String
     */
    public String nextString() {
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];
        return new String(buf);
    }

    /**
     * creates a random Integer
     * @param min minimum int
     * @param max maximum int
     * @return random int
     */
    public static int randomInt(int min, int max) {
        return min + (int)(Math.random() * ((max - min) + 1));
    }

    private static final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final String lower = upper.toLowerCase(Locale.ROOT);

    private static final String digits = "0123456789";

    private static final String alphanum = upper + lower + digits;

    private final java.util.Random random;

    private final char[] symbols;

    private final char[] buf;

    public Random(int length, java.util.Random random, String symbols) {
        if (length < 1) throw new IllegalArgumentException();
        if (symbols.length() < 2) throw new IllegalArgumentException();
        this.random = Objects.requireNonNull(random);
        this.symbols = symbols.toCharArray();
        this.buf = new char[length];
    }

    public Random(int length, java.util.Random random) {
        this(length, random, alphanum);
    }

    public Random(int length) {
        this(length, new SecureRandom());
    }

}
