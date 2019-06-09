package game.random;

import utils.data.PacketWriter;

public class CRand32 {
    private long s1;
    private long s2;
    private long s3;

    public CRand32() {
        this.s1 = 10 | 0x100000;
        this.s2 = 10 | 0x1000;
        this.s3 = 10 | 0x10;
    }

    public long random() {
        s1 = ((s1 << 12) ^ (s1 >> 19) ^ ((s1 >> 6) ^ (s1 << 12)) & 0x1FFF) & 0xffffffffL;
        s2 = (16 * s2 ^ (s2 >> 25) ^ ((16 * s2) ^ (s2 >> 23)) & 0x7F) & 0xffffffffL;
        s3 = ((s3 >> 11) ^ (s3 << 17) ^ ((s3 >> 8) ^ (s3 << 17)) & 0x1FFFFF) & 0xffffffffL;
        return (s1 ^ s2 ^ s3) & 0xffffffffL;
    }

    public void writeSeeds(PacketWriter pw) {
        pw.writeInt((int) s1);
        pw.writeInt((int) s2);
        pw.writeInt((int) s3);
    }

    public static double getRand(long random, double max, double min) {
        return min + (random % 10000000) * (max - min) / 9999999.0;
    }
}