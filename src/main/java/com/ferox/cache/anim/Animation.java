package com.ferox.cache.anim;

import com.ferox.Client;
import com.ferox.io.Buffer;

public final class Animation {

    public static Animation[][] frame_list = new Animation[6500][0];

    public static void load(byte[] data, int file_id) {
        try {
            Buffer buffer = new Buffer(data);
            Skins list = new Skins((buffer));
            int length = buffer.readUShort();
            frame_list[file_id] = new Animation[length * 3];

            int[] translation_indices = new int[500];
            int[] transform_x = new int[500];
            int[] transform_y = new int[500];
            int[] transform_z = new int[500];
            for (int frames = 0; frames < length; frames++) {
                int id = buffer.readUShort();
                Animation frame = new Animation();
                Animation[] skin = frame_list[file_id];
                skin[id] = frame;
                frame.skins = list;
                int transformations = buffer.readUByte();
                int last = -1;
                int transformation = 0;
                for (int index = 0; index < transformations; index++) {
                    int attribute = buffer.readUByte();
                    if (attribute > 0) {
                        if (list.opcodes[index] != 0) {
                            for (int next = index - 1; next > last; next--) {
                                if (list.opcodes[next] != 0)
                                    continue;

                                translation_indices[transformation] = next;
                                transform_x[transformation] = 0;
                                transform_y[transformation] = 0;
                                transform_z[transformation] = 0;
                                transformation++;
                                break;
                            }
                        }
                        translation_indices[transformation] = index;
                        char c = '\0';
                        if (list.opcodes[index] == 3) {
                            c = '\200';
                        }
                        if ((attribute & 1) != 0) {
                            transform_x[transformation] = (buffer.readShort());
                        } else {
                            transform_x[transformation] = c;
                        }

                        if ((attribute & 2) != 0) {
                            transform_y[transformation] = (buffer.readShort());
                        } else {
                            transform_y[transformation] = c;
                        }

                        if ((attribute & 4) != 0) {
                            transform_z[transformation] = (buffer.readShort());
                        } else {
                            transform_z[transformation] = c;
                        }

                        if (list.opcodes[index] == 2) {
                            transform_x[transformation] = ((transform_x[transformation] & 0xff) << 3) + (transform_x[transformation] >> 8 & 0x7);
                            transform_y[transformation] = ((transform_y[transformation] & 0xff) << 3) + (transform_y[transformation] >> 8 & 0x7);
                            transform_z[transformation] = ((transform_z[transformation] & 0xff) << 3) + (transform_z[transformation] >> 8 & 0x7);
                        }

                        last = index;
                        transformation++;
                    }
                }
                frame.frames = transformation;
                frame.translation_modifier = new int[transformation];
                frame.x_modifier = new int[transformation];
                frame.y_modifier = new int[transformation];
                frame.z_modifier = new int[transformation];
                for (int index = 0; index < transformation; ++index) {
                    frame.translation_modifier[index] = translation_indices[index];
                    frame.x_modifier[index] = transform_x[index];
                    frame.y_modifier[index] = transform_y[index];
                    frame.z_modifier[index] = transform_z[index];
                }
            }
        } catch (Exception ex) {
            //Make silent
            //ex.printStackTrace();
        }
    }

    public static void release() {
        frame_list = null;
    }

    public static int getFileId(int id) {
        return id >> 16;
    }

    public static Animation get(int frame) {
        try {
            int file_id = frame >> 16;
            frame = frame & 0xFFFF;

            if (frame_list[file_id].length == 0) {
                Client.singleton.resourceProvider.provide(1, file_id);
                return null;
            }

            return frame_list[file_id][frame];
        } catch (Exception ex) {
            //System.out.println("Animation: get(frame) - " + ex);
            return null;
        }
    }

    public static boolean validate(int frame) {
        return frame == -1;
    }

    public Animation() {
    }

    public int length;
    public Skins skins;
    public int frames;
    public int[] translation_modifier;
    public int[] x_modifier;
    public int[] y_modifier;
    public int[] z_modifier;
}
