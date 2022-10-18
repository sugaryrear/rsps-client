package com.ferox.cache.anim;

import com.ferox.ClientConstants;
import com.ferox.cache.Archive;
import com.ferox.io.Buffer;
import com.ferox.util.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public final class Sequence {

    public static Sequence[] cache;
    public int frames;
    public int[] primary_frame;
    public int[] frame_list;
    public int[] frame_length;
    public int step;
    public int[] flow_control;
    public boolean stretch;
    public int appended_frames;
    public int shield_delta;
    public int weapon_delta;
    public int loops;
    public int tempo;
    public int priority;
    public int reset;

    public Sequence() {
        step = -1;
        stretch = false;
        appended_frames = 5;
        shield_delta = -1; // Removes shield
        weapon_delta = -1; // Removes weapon
        loops = 99;
        tempo = -1; // Stops character from moving
        priority = -1;
        reset = 2; // replayMode default value 2 in OSRS, can change back to 1 if causes problems with animations.
    }

    public static void init(Archive archive) {
        final Buffer buffer = new Buffer(ClientConstants.LOAD_OSRS_DATA_FROM_CACHE_DIR ? FileUtils.read(ClientConstants.DATA_DIR + "/anims/seq.dat") : archive.get("seq.dat"));
        int animation_size = buffer.readUShort();

        System.out.printf("Loaded %d animations loading OSRS version %d and SUB version %d%n", animation_size, ClientConstants.OSRS_DATA_VERSION, ClientConstants.OSRS_DATA_SUB_VERSION);

        if (cache == null) {
            cache = new Sequence[animation_size + 15_000];
        }

        int animation;
        for (animation = 0; animation < animation_size; animation++) {
            if (cache[animation] == null) {
                cache[animation] = new Sequence();
            }
            cache[animation].decode(buffer);

            //Fix Dagannoth Rex animation by reverting frames to older
            if(animation == 2851) {
                cache[animation] = new Sequence();
                cache[animation].frames = 6;
                cache[animation].frame_length = new int[]{5, 5, 3, 6, 4, 3};
                cache[animation].primary_frame = new int[]{983040, 983041, 983042, 983043, 983044, 983045};
                cache[animation].appended_frames = 5;
                cache[animation].reset = 1;
            }
        }

        while (animation < cache.length) {
            if (animation == 11900) {
                cache[animation] = new Sequence();
                cache[animation].frames = 14;
                cache[animation].primary_frame = new int[] {188285009, 188284950, 188284989, 188285162, 188284987, 188285107, 188285131, 188285085, 188285159, 188285041, 188285071, 188285204, 188285174, 188285152};
                cache[animation].frame_list = new int[] {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
                cache[animation].frame_length = new int[]{3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3};
                cache[animation].tempo = 3;
                cache[animation].priority = 1;
            }
            if (animation == 11901) {
                cache[animation] = new Sequence();
                cache[animation].frames = 13;
                cache[animation].primary_frame = new int[] {19267601, 19267602, 19267603, 19267604, 19267605, 19267606, 19267607, 19267606, 19267605, 19267604, 19267603, 19267602, 19267601};
                cache[animation].frame_length = new int[] {4, 3, 3, 4, 10, 10, 15, 10, 10, 4, 3, 3, 4};
                cache[animation].frame_list = new int[] {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
                cache[animation].flow_control = new int[] {1, 2, 9, 11, 13, 15, 17, 19, 37, 39, 41, 43, 45, 164, 166, 168, 170, 172, 174, 176, 178, 180, 182, 183, 185, 191, 192, 9999999};
                cache[animation].appended_frames = 6;
                cache[animation].tempo = 2;
                cache[animation].priority = 2;
                cache[animation].reset = 1;
            }
            if (animation == 11902) {
                cache[animation] = new Sequence();
                cache[animation].frames = 31;
                cache[animation].primary_frame  = new int[] {114295000, 114294953, 114295281, 114295193, 114295189, 114295249, 114295182, 114295061, 114295124, 114295007, 114295337, 114295102, 114294995, 114295283, 114295025, 114294899, 114295233, 114294879, 114295175, 114295169, 114294903, 114295091, 114295059, 114295267, 114295003, 114294981, 114294951, 114295031, 114294986, 114294820, 114295226};
                cache[animation].frame_length  = new int[] {3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3};
                cache[animation].frame_list  = new int[] {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
                cache[animation].flow_control = new int[] {164, 166, 168, 170, 172, 174, 176, 178, 180, 182, 183, 185, 193, 194, 196, 197, 198, 200, 202, 203, 204, 9999999};
                cache[animation].stretch = true;
                cache[animation].appended_frames = 6;
                cache[animation].tempo = 2;
                cache[animation].priority = 2;
            }
            //Custom animations unsupported by the OSRS rev
            animation++;
        }

        /*System.out.println("frames "+cache[7644].frames);
        System.out.println("primary_frame "+Arrays.toString(cache[7644].primary_frame));
        System.out.println("frame_length "+Arrays.toString(cache[7644].frame_length));
        System.out.println("frame_list "+Arrays.toString(cache[7644].frame_list));
        System.out.println("flow_control "+Arrays.toString(cache[7644].flow_control));
        System.out.println("step "+cache[7644].step);
        System.out.println("stretch "+cache[7644].stretch);
        System.out.println("appended_frames "+cache[7644].appended_frames);
        System.out.println("shield_delta "+cache[7644].shield_delta);
        System.out.println("weapon_delta "+cache[7644].weapon_delta);
        System.out.println("loops "+cache[7644].loops);
        System.out.println("tempo "+cache[7644].tempo);
        System.out.println("priority "+cache[7644].priority);
        System.out.println("reset "+cache[7644].reset);*/
        //System.out.println(findAnimFileId(2851));
        //System.out.println(findAnimFileId(6953));

        //dump(8763);
        //dumpAnimations(9144);

        // temp fix for wintertodt howling snowstorm
        cache[7322].step = 10;
    }

    public static void dump(int amount) {
        File f = new File(System.getProperty("user.home") + "/Desktop/osrs_anim.txt");
        try {
            f.createNewFile();
            BufferedWriter bf = new BufferedWriter(new FileWriter(f));
            for (int id = 0; id < amount; id++) {
                bf.write(System.getProperty("line.separator"));
                bf.write("animation "+id+" //fileID: " + (cache[id].primary_frame[0] >> 16));
                bf.write(System.getProperty("line.separator"));
            }
            bf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void dumpAnimations(int amount) {
        File f = new File(System.getProperty("user.home") + "/Desktop/animations.txt");
        try {
            f.createNewFile();
            BufferedWriter bf = new BufferedWriter(new FileWriter(f));
            for (int id = 0; id < amount; id++) {
                bf.write("//fileID: " + (cache[id].primary_frame[0] >> 16));
                bf.write(System.getProperty("line.separator"));
                bf.write("case " + id + ":");
                bf.write(System.getProperty("line.separator"));
                if (cache[id].frames > 0) {
                    bf.write("animations[animation].frames = " + cache[id].frames + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (cache[id].primary_frame != null) {
                    bf.write("animations[animation].primary_frame = new int[] \""
                        + Arrays.toString(cache[id].primary_frame).replace("[", "{").replace("]", "}") + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (cache[id].frame_list != null) {
                    bf.write("animations[animation].frame_list = new int[] \""
                        + Arrays.toString(cache[id].frame_list).replace("[", "{").replace("]", "}") + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (cache[id].frame_length != null) {
                    bf.write("animations[animation].frame_length = new int[] "
                        + Arrays.toString(cache[id].frame_length).replace("[", "{").replace("]", "}") + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (cache[id].step != -1) {
                    bf.write("animations[animation].step = " + cache[id].step + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (cache[id].flow_control != null) {
                    bf.write("animations[animation].flow_control = " + cache[id].flow_control + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (cache[id].stretch != false) {
                    bf.write("animations[animation].stretch = " + cache[id].stretch + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (cache[id].appended_frames != 5) {
                    bf.write("animations[animation].appended_frames = " + cache[id].appended_frames + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (cache[id].shield_delta != -1) {
                    bf.write("animations[animation].shield_delta = " + cache[id].shield_delta + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (cache[id].weapon_delta != -1) {
                    bf.write("animations[animation].weapon_delta = " + cache[id].weapon_delta + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (cache[id].loops != 99) {
                    bf.write("animations[animation].loops = " + cache[id].loops + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (cache[id].tempo != -1) {
                    bf.write("animations[animation].tempo = " + cache[id].tempo + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (cache[id].priority != 2) {
                    bf.write("animations[animation].priority = " + cache[id].priority + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (cache[id].reset > 0) {
                    bf.write("animations[animation].reset = " + cache[id].reset + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                bf.write("break;");
                bf.write(System.getProperty("line.separator"));
                bf.write(System.getProperty("line.separator"));
            }
            bf.close();
        } catch (

            IOException e) {
            e.printStackTrace();
        }
    }

    public static int findAnimFileId(int animId) {
        return Animation.getFileId(cache[animId].primary_frame[0]);
    }

    public int get_length(int id) {
        int duration = frame_length[id];
        if (duration == 0) {
            final Animation frame = Animation.get(primary_frame[id]);
            if (frame != null) {
                duration = frame_length[id] = frame.length;
            }
        }
        if (duration == 0) {
            duration = 1;
        }
        return duration;
    }
    private void decode(Buffer buffer) {
        while(true) {
            int opcode = buffer.readUnsignedByte();

            if (opcode == 0) {
                break;
            } else if (opcode == 1) {
                frames  = buffer.readUShort();
                primary_frame  = new int[frames];
                frame_list  = new int[frames];
                frame_length  = new int[frames];

                for (int i = 0; i < frames; i++) {
                    frame_length[i] = buffer.readUShort();
                }

                for (int i = 0; i < frames; i++) {
                    primary_frame[i] = buffer.readUShort();
                    frame_list[i] = -1;
                }

                for (int i = 0; i < frames; i++) {
                    primary_frame[i] += buffer.readUShort() << 16;
                }

            } else if (opcode == 2) {
                step = buffer.readUShort();
            } else if (opcode == 3) {
                int len = buffer.readUnsignedByte();
                flow_control  = new int[len + 1];
                for (int i = 0; i < len; i++) {
                    flow_control [i] = buffer.readUnsignedByte();
                }
                flow_control [len] = 9999999;
            } else if (opcode == 4) {
                stretch  = true;
            } else if (opcode == 5) {
                appended_frames  = buffer.readUnsignedByte();
            } else if (opcode == 6) {
                shield_delta  = buffer.readUShort();
            } else if (opcode == 7) {
                weapon_delta  = buffer.readUShort();
            } else if (opcode == 8) {
                loops  = buffer.readUnsignedByte();
            } else if (opcode == 9) {
                tempo  = buffer.readUnsignedByte();
            } else if (opcode == 10) {
                priority = buffer.readUnsignedByte();
            } else if (opcode == 11) {
                reset  = buffer.readUnsignedByte();
            } else if (opcode == 12) {
                int len = buffer.readUnsignedByte();

                for (int i = 0; i < len; i++) {
                    buffer.readUShort();
                }

                for (int i = 0; i < len; i++) {
                    buffer.readUShort();
                }
            } else if (opcode == 13) {
                int len = buffer.readUnsignedByte();

                for (int i = 0; i < len; i++) {
                    buffer.read24Int();
                }
            }
        }
post_decode();
    }

//
//    private void decode(Buffer buffer) {
//        do {
//            int opcode = buffer.readUByte();
//            if (opcode == 0)
//                break;
//
//            if (opcode == 1) {
//                frames = buffer.readUShort();
//                primary_frame = new int[frames];
//                frame_list = new int[frames];
//                frame_length = new int[frames];
//
//                for (int frame = 0; frame < frames; frame++) {
//                    frame_length[frame] = buffer.readUShort();
//                }
//
//                for (int frame = 0; frame < frames; frame++) {
//                    primary_frame[frame] = buffer.readUShort();
//                    frame_list[frame] = -1;
//                }
//
//                for (int frame = 0; frame < frames; frame++) {
//                    primary_frame[frame] += buffer.readUShort() << 16;
//                }
//            } else if (opcode == 2) {
//                step = buffer.readUShort();
//            } else if (opcode == 3) {
//                int index = buffer.readUByte();
//                flow_control = new int[index + 1];
//                for (int id = 0; id < index; id++) {
//                    flow_control[id] = buffer.readUByte();
//                }
//                flow_control[index] = 0x98967f;
//            } else if (opcode == 4) {
//                stretch = true;
//            } else if (opcode == 5) {
//                appended_frames = buffer.readUByte();
//            } else if (opcode == 6) {
//                shield_delta = buffer.readUShort();
//            } else if (opcode == 7) {
//                weapon_delta = buffer.readUShort();
//            } else if (opcode == 8) {
//                loops = buffer.readUByte();
//            } else if (opcode == 9) {
//                tempo = buffer.readUByte();
//            } else if (opcode == 10) {
//                priority = buffer.readUByte();
//            } else if (opcode == 11) {
//                reset = buffer.readUByte();
//            } else if (opcode == 12) {
//                int length = buffer.readUByte();
//                for (int index = 0; index < length; index++) {
//                    buffer.readUShort();
//                }
//                for (int index = 0; index < length; index++) {
//                    buffer.readUShort();
//                }
//            } else if (opcode == 13) {
//                int length = buffer.readUByte();
//                for (int index = 0; index < length; index++) {
//                    buffer.read24Int();
//                }
//            } else {
//                //System.out.println("Error unrecognised {SEQ} opcode: " + opcode);//use for debugging otherwise it spams the console (127 unrecognized)
//            }
//        } while (true);
//
//        post_decode();
//    }

    private void post_decode() {
        if (frames == 0) {
            frames = 1;
            primary_frame = new int[1];
            primary_frame[0] = -1;
            frame_list = new int[1];
            frame_list[0] = -1;
            frame_length = new int[1];
            frame_length[0] = -1;
        }

        if (tempo == -1) {
            tempo = (flow_control == null) ? 0 : 2;
        }

        if (priority == -1) {
            priority = (flow_control == null) ? 0 : 2;
        }
    }

    @Override
    public String toString() {
        return "Animation{" +
            "frames=" + frames +
            ", primary_frame=" + Arrays.toString(primary_frame) +
            ", frame_list=" + Arrays.toString(frame_list) +
            ", frame_length=" + Arrays.toString(frame_length) +
            ", loopOffset=" + step +
            ", interleaveOrder=" + Arrays.toString(flow_control) +
            ", stretches=" + stretch +
            ", appended_frames=" + appended_frames +
            ", playerOffhand=" + shield_delta +
            ", playerMainhand=" + weapon_delta +
            ", maximumLoops=" + loops +
            ", animatingPrecedence=" + tempo +
            ", walkingPrecedence=" + priority +
            ", replayMode=" + reset +
            '}';
    }
}
