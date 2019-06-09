package utils;

import java.util.Random;

public class Randomizer {

    private static Random rand = new Random();
    private static int ngen = 0;
    public static final int nextInt() {
        ++ngen;
        if(ngen > 500000) {
            rand = new Random();
        }
        return rand.nextInt();
    }

    public static final int nextInt(final int arg0) {
        ++ngen;
        if(ngen > 500000) {
            rand = new Random();
        }
        return rand.nextInt(arg0);
    }

    public static final void nextBytes(final byte[] bytes) {
        rand.nextBytes(bytes);
    }

    public static final boolean nextBoolean() {
        return rand.nextBoolean();
    }

    public static final double nextDouble() {
        return rand.nextDouble();
    }

    public static final float nextFloat() {
        return rand.nextFloat();
    }

    public static final long nextLong() {
        return rand.nextLong();
    }

    public static final int rand(final int lbound, final int ubound) {
        return nextInt(ubound - lbound + 1) + lbound;
    }

    public static boolean isSuccess(int rate) {
        return rate > nextInt(100);
    }
}