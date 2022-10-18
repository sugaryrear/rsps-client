package com.ferox.entity.model;

import com.ferox.ClientConstants;
import com.ferox.cache.Archive;
import com.ferox.io.Buffer;
import com.ferox.util.FileUtils;

public final class IdentityKit {

    public static int length;
    public static IdentityKit[] cache;
    public int bodyPartId = -1;
    private int[] bodyModels;
    private final int[] originalColors = new int[6];
    private final int[] replacementColors = new int[6];
    private final int[] headModels = { -1, -1, -1, -1, -1 };
    public boolean validStyle;

    private IdentityKit() {
    }

    public static void init(Archive archive) {
        Buffer buffer = new Buffer(ClientConstants.LOAD_OSRS_DATA_FROM_CACHE_DIR ? FileUtils.read(ClientConstants.DATA_DIR+"/identity_kit/idk.dat") : archive.get("idk.dat"));
        length = buffer.readUShort();

        System.out.printf("Loaded %d identities loading OSRS version %d and SUB version %d%n", length, ClientConstants.OSRS_DATA_VERSION, ClientConstants.OSRS_DATA_SUB_VERSION);

        if (cache == null) {
            cache = new IdentityKit[length];
        }

        for (int id = 0; id < length; id++) {

            if (cache[id] == null) {
                cache[id] = new IdentityKit();
            }

            IdentityKit kit = cache[id];
            kit.decode(buffer);
            kit.originalColors[0] = 55232;
            kit.replacementColors[0] = 6798;
            
        }
    }

    private void decode(Buffer buffer) {
        while (true) {
            final int opcode = buffer.readUByte();

            if (opcode == 0) {
                break;
            }

            if (opcode == 1) {
                bodyPartId = buffer.readUByte();
            } else if (opcode == 2) {
                final int count = buffer.readUByte();
                bodyModels = new int[count];
                for (int part = 0; part < count; part++) {
                    bodyModels[part] = buffer.readUShort();
                }
            } else if (opcode == 3) {
                validStyle = true;
            } else if (opcode >= 40 && opcode < 50) {
                originalColors[opcode - 40] = buffer.readUShort();
            } else if (opcode >= 50 && opcode < 60) {
                replacementColors[opcode - 50] = buffer.readUShort();
            } else if (opcode >= 60 && opcode < 70) {
                headModels[opcode - 60] = buffer.readUShort();
            } else {
                System.out.println("Error unrecognised config code: " + opcode);
            }
        }
    }

    public boolean body_cached() {
        if (bodyModels == null) {
            return true;
        }
        boolean ready = true;
        for (int part = 0; part < bodyModels.length; part++) {
            if (!Model.cached(bodyModels[part]))
                ready = false;
        }
        return ready;
    }

    public Model get_body() {
        if (bodyModels == null) {
            return null;
        }

        Model[] models = new Model[bodyModels.length];

        for (int part = 0; part < bodyModels.length; part++) {
            models[part] = Model.get(bodyModels[part]);
        }

        Model model;
        if (models.length == 1) {
            model = models[0];
        } else {
            model = new Model(models.length, models, true);
        }

        for (int part = 0; part < 6; part++) {
            if (originalColors[part] == 0) {
                break;
            }
            model.recolor(originalColors[part], replacementColors[part]);
        }
        return model;
    }

    public boolean headLoaded() {
        boolean ready = true;
        for (int part = 0; part < 5; part++) {
            if (headModels[part] != -1 && !Model.cached(headModels[part])) {
                ready = false;
            }
        }
        return ready;
    }

    public Model get_head() {
        Model[] models = new Model[5];
        int count = 0;
        for (int part = 0; part < 5; part++) {
            if (headModels[part] != -1) {
                models[count++] = Model.get(headModels[part]);
            }
        }

        Model model = new Model(count, models, true);
        for (int part = 0; part < 6; part++) {
            if (originalColors[part] == 0) {
                break;
            }
            model.recolor(originalColors[part], replacementColors[part]);
        }
        return model;
    }
}
