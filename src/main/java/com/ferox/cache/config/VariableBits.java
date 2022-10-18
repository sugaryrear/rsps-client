package com.ferox.cache.config;

import com.ferox.ClientConstants;
import com.ferox.cache.Archive;
import com.ferox.io.Buffer;
import com.ferox.util.FileUtils;

public final class VariableBits {

    public static VariableBits[] cache;
    public int configId;
    public int leastSignificantBit;
    public int mostSignificantBit;
    private final boolean aBoolean651;

     public static void init(Archive archive) {
        Buffer stream = new Buffer(ClientConstants.LOAD_OSRS_DATA_FROM_CACHE_DIR ? FileUtils.read(ClientConstants.DATA_DIR+"/varbits/varbit.dat") : archive.get("varbit.dat"));
        int size = stream.readUShort();

        if (cache == null) {
            cache = new VariableBits[size];
        }

         System.out.printf("Loaded %d varbits loading OSRS version %d and SUB version %d%n", size, ClientConstants.OSRS_DATA_VERSION, ClientConstants.OSRS_DATA_SUB_VERSION);

        for (int index = 0; index < size; index++) {
            if (cache[index] == null) {
                cache[index] = new VariableBits();
            }

            cache[index].decode(stream);

            if (cache[index].aBoolean651) {
                VariableParameter.values[cache[index].configId].aBoolean713 = true;
            }
        }

        if (stream.pos != stream.payload.length) {
            System.err.println("varbit load mismatch");
        }

    }

    private void decode(Buffer buffer) {
        int opcode = buffer.readUByte();

        if (opcode == 0) {
            return;
        } else if (opcode == 1) {
            configId = buffer.readUShort();
            leastSignificantBit = buffer.readUByte();
            mostSignificantBit = buffer.readUByte();
        } else {
            System.out.println(opcode);
        }
    }

    private VariableBits() {
        aBoolean651 = false;
    }

}
