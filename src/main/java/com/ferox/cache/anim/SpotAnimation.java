package com.ferox.cache.anim;

import com.ferox.ClientConstants;
import com.ferox.cache.Archive;
import com.ferox.cache.anim.osrs.OSRSGraphics;
import com.ferox.collection.TempCache;
import com.ferox.entity.model.Model;
import com.ferox.io.Buffer;
import com.ferox.util.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public final class SpotAnimation {

    public static void init(Archive archive) {
        Buffer buffer = new Buffer(ClientConstants.LOAD_OSRS_DATA_FROM_CACHE_DIR ? FileUtils.read(ClientConstants.DATA_DIR+"/gfx/spotanim.dat") : archive.get("spotanim.dat"));
        int amount = buffer.readUShort();
        System.out.printf("Loaded %d graphics loading OSRS version %d and SUB version %d%n", amount, ClientConstants.OSRS_DATA_VERSION, ClientConstants.OSRS_DATA_SUB_VERSION);

        if (cache == null) {
            cache = new SpotAnimation[amount + 10_000];
        }

        int graphic;
        for (graphic = 0; graphic < amount; graphic++) {
            if (cache[graphic] == null) {
                cache[graphic] = new SpotAnimation();
            }
            cache[graphic].id = graphic;
            cache[graphic].decode(buffer);
        }

        while (graphic < cache.length) {
            OSRSGraphics.unpack(graphic);
            graphic++;
        }

        //dump(1969);

        if (buffer.pos != buffer.payload.length) {
            System.out.println("gfx mismatch! " + buffer.pos + " " + buffer.payload.length);
        }
    }

    public static void dump(int amount) {
        File f = new File(System.getProperty("user.home") + "/Desktop/graphics.txt");
        try {
            f.createNewFile();
            BufferedWriter bf = new BufferedWriter(new FileWriter(f));
            for (int id = 0; id < amount; id++) {
                bf.write("case " + id + ":");
                bf.write(System.getProperty("line.separator"));
                if (cache[id].model_id > 0) {
                    bf.write("graphics[graphic].model_id = " + cache[id].model_id + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (cache[id].animation_id > 0) {
                    bf.write("graphics[graphic].animation_id = " + cache[id].animation_id + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (cache[id].rotation != 0) {
                    bf.write("graphics[graphic].rotation = " + cache[id].rotation + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (cache[id].model_scale_x != 128) {
                    bf.write("graphics[graphic].model_scale_x = " + cache[id].model_scale_x + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (cache[id].model_scale_y != 128) {
                    bf.write("graphics[graphic].model_scale_y = " + cache[id].model_scale_y + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (cache[id].ambient != 0) {
                    bf.write("graphics[graphic].ambient = " + cache[id].ambient + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (cache[id].contrast != 0) {
                    bf.write("graphics[graphic].contrast = " + cache[id].contrast + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (cache[id].src_color != null) {
                    bf.write("graphics[graphic].src_color = new int[] "
                        + Arrays.toString(cache[id].src_color).replace("[", "{").replace("]", "}") + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (cache[id].dst_color != null) {
                    bf.write("graphics[graphic].dst_color = new int[] "
                        + Arrays.toString(cache[id].dst_color).replace("[", "{").replace("]", "}") + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (cache[id].src_texture != null) {
                    bf.write("graphics[graphic].src_texture = new int[] "
                        + Arrays.toString(cache[id].src_texture).replace("[", "{").replace("]", "}") + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (cache[id].dst_texture != null) {
                    bf.write("graphics[graphic].dst_texture = new int[] "
                        + Arrays.toString(cache[id].dst_texture).replace("[", "{").replace("]", "}") + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                bf.write("break;");
                bf.write(System.getProperty("line.separator"));
                bf.write(System.getProperty("line.separator"));
            }
            bf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void decode(Buffer stream) {
        while (true) {
            int i = stream.readUnsignedByte();
            if (i == 0)
                return;
            if (i == 1) {
                this.model_id  = stream.readUShort();
                continue;
            }
            if (i == 2) {
                this.animation_id  = stream.readUShort();
                if (Sequence.cache != null) {
                    this.seq  = Sequence.cache[this.animation_id ];
                }
                continue;
            }
            if (i == 4) {
                this.model_scale_x  = stream.readUShort();
                continue;
            }
            if (i == 5) {
                this.model_scale_y  = stream.readUShort();
                continue;
            }
            if (i == 6) {
                this.rotation = stream.readUShort();
                continue;
            }
            if (i == 7) {
                this.ambient  = stream.readUnsignedByte();
                continue;
            }
            if (i == 8) {
                this.contrast  = stream.readUnsignedByte();
                continue;
            }
            if (i == 40) {
                int length = stream.readUByte();
                src_color = new int[length];
                dst_color = new int[length];
                for (int index = 0; index < length; index++) {
                    src_color[index] = (short) stream.readUShort();
                    dst_color[index] = (short) stream.readUShort();
                }
                continue;
            }
            System.out.println("Error unrecognised spotanim config code: " + i);
        }
    }
//    public void decode(Buffer buffer) {
//        do {
//            int opcode = buffer.readUByte();
//            if (opcode == 0)
//                return;
//
//            if (opcode == 1)
//                model_id = buffer.readUShort();
//            else if (opcode == 2) {
//                animation_id = buffer.readUShort();
//                if (Sequence.cache != null)
//                    seq = Sequence.cache[animation_id];
//
//            } else if (opcode == 4)
//                model_scale_x = buffer.readUShort();
//            else if (opcode == 5)
//                model_scale_y = buffer.readUShort();
//            else if (opcode == 6)
//                rotation = buffer.readUShort();
//            else if (opcode == 7)
//                ambient = buffer.readUByte();
//            else if (opcode == 8)
//                contrast = buffer.readUByte();
//            else if (opcode == 40) {
//                int length = buffer.readUByte();
//                src_color = new int[length];
//                dst_color = new int[length];
//                for (int index = 0; index < length; index++) {
//                    src_color[index] = (short) buffer.readUShort();
//                    dst_color[index] = (short) buffer.readUShort();
//                }
//            } else if (opcode == 41) {
//                int length = buffer.readUByte();
//                src_texture = new short[length];
//                dst_texture = new short[length];
//                for (int index = 0; index < length; index++) {
//                    src_texture[index] = (short) buffer.readUShort();
//                    dst_texture[index] = (short) buffer.readUShort();
//                }
//
//            } else {
//                System.out.println("Error unrecognised {SPOTANIM} ocode: " + opcode);
//            }
//        } while (true);
//    }

    public Model get_model() {
        Model model = (Model) model_cache.get(id);
        if (model != null)
            return model;

        model = Model.get(model_id);
        if (model == null)
            return null;

        if(src_color != null) {
            for (int index = 0; index < src_color.length; index++)
                if (src_color[0] != 0)
                    model.recolor(src_color[index], dst_color[index]);

        }
        if (src_texture != null) {
            for (int index = 0; index < src_texture.length; index++)
                if (src_texture[0] != 0)
                    model.retexture(src_texture[index], dst_texture[index]);
        }
        model_cache.put(model, id);
        return model;
    }

    public SpotAnimation() {

    }

    public static SpotAnimation[] cache;
    public int id;
    public int model_id;
    public int animation_id = -1;
    public Sequence seq;
    public int[] src_color;
    public int[] dst_color;
    public short[] src_texture;
    public short[] dst_texture;

    public int model_scale_x = 128;
    public int model_scale_y = 128;
    public int rotation = 0;
    public int ambient = 0;
    public int contrast = 0;
    public static TempCache model_cache = new TempCache(30);
}
