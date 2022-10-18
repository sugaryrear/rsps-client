package com.ferox.cache.anim;
import com.ferox.io.Buffer;

public final class Skins {

    private int count;

    /**
     * The type of each transformation.
     */
    public final int[] opcodes;
    public final int[][] cache;

    public Skins(Buffer stream) {
        count = stream.readUShort();

        opcodes = new int[count];
        cache = new int[count][];

        for (int index = 0; index < count; index++) {
            opcodes[index] = stream.readUShort();
        }

        for (int label = 0; label < count; label++) {
            cache[label] = new int[stream.readUShort()];
        }

        for (int label = 0; label < count; label++) {
            for (int index = 0; index < cache[label].length; index++) {
                cache[label][index] = stream.readUShort();
            }
        }
    }

}
