package com.ferox.cache.def;

import com.ferox.Client;
import com.ferox.ClientConstants;
import com.ferox.cache.Archive;
import com.ferox.cache.anim.Animation;
import com.ferox.cache.config.VariableBits;
import com.ferox.cache.def.impl.ObjectManager;
import com.ferox.collection.TempCache;
import com.ferox.entity.model.Model;
import com.ferox.io.Buffer;
import com.ferox.net.requester.ResourceProvider;
import com.ferox.util.FileUtils;

import java.util.HashMap;
import java.util.Map;

public final class ObjectDefinition {

    private Map<Integer, Object> params;

    public static void init(Archive archive) {
        data_buffer = new Buffer(archive.get("loc.dat"));
        Buffer index_buffer = new Buffer(archive.get("loc.idx"));
        length = index_buffer.readUShort();

        System.out.printf("Loaded %d objects loading OSRS version %d and SUB version %d%n", length, ClientConstants.OSRS_DATA_VERSION, ClientConstants.OSRS_DATA_SUB_VERSION);

        stream_indices = new int[length];
        int offset = 2;
        for (int index = 0; index < length; index++) {
            stream_indices[index] = offset;
            offset += index_buffer.readUShort();
        }

        cache = new ObjectDefinition[20];

        for (int index = 0; index < 20; index++) {
            cache[index] = new ObjectDefinition();
        }
    }

//    public void decode(Buffer buffer) {
//        do {
//            int opcode = buffer.readUByte();
//            if (opcode == 0)
//                break;
//
//            if (opcode == 1) {
//                int length = buffer.readUByte();
//                if (length > 0) {
//                    if (model_ids == null || low_detail) {
//                        model_group = new int[length];
//                        model_ids = new int[length];
//                        for (int index = 0; index < length; index++) {
//                            model_ids[index] = buffer.readUShort();
//                            model_group[index] = buffer.readUByte();
//                        }
//                    } else {
//                        buffer.pos += length * 3;
//                    }
//                }
//            } else if (opcode == 2)
//                name = buffer.readString();
//            else if (opcode == 3)
//                description = buffer.readString();
//            else if (opcode == 5) {
//                int length = buffer.readUByte();
//                if (length > 0) {
//                    if (model_ids == null || low_detail) {
//                        model_group = null;
//                        model_ids = new int[length];
//                        for (int index = 0; index < length; index++)
//                            model_ids[index] = buffer.readUShort();
//                    } else {
//                        buffer.pos += length * 2;
//                    }
//                }
//            } else if (opcode == 14)
//                width = buffer.readUByte();
//            else if (opcode == 15)
//                height = buffer.readUByte();
//            else if (opcode == 17)
//                solid = false;
//            else if (opcode == 18)
//                walkable = false;
//            else if (opcode == 19)
//                interact_state = buffer.readUByte();//(buffer.readUnsignedByte() == 1);
//            else if (opcode == 21)
//                contour_to_tile = true;
//            else if (opcode == 22)
//                gouraud_shading = false;
//            else if (opcode == 23)
//                occlude = true;
//            else if (opcode == 24) {
//                animation = buffer.readUShort();
//                if (animation == 65535)
//                    animation = -1;
//            } else if (opcode == 27) {
//                solid = true;
//            } else if (opcode == 28)
//                decor_offset = buffer.readUByte();
//            else if (opcode == 29)
//                ambient = buffer.readSignedByte();
//            else if (opcode == 39)
//                contrast = buffer.readSignedByte();
//            else if (opcode >= 30 && opcode < 35) {
//                if (scene_actions == null)
//                    scene_actions = new String[5];
//                scene_actions[opcode - 30] = buffer.readString();
//                if (scene_actions[opcode - 30].equalsIgnoreCase("hidden"))
//                    scene_actions[opcode - 30] = null;
//            } else if (opcode == 40) {
//                int length = buffer.readUByte();
//                src_color = new int[length];
//                dst_color = new int[length];
//                for (int index = 0; index < length; index++) {
//                    src_color[index] = buffer.readUShort();
//                    dst_color[index] = buffer.readUShort();
//                }
//            } else if (opcode == 41) {
//                int length = buffer.readUByte();
//                src_texture = new short[length];
//                dst_texture = new short[length];
//                for (int index = 0; index < length; index++) {
//                    src_texture[index] = (short) buffer.readUShort();
//                    dst_texture[index] = (short) buffer.readUShort();
//                }
//            } else if (opcode == 82) {
//                minimap_function_id = buffer.readUShort();
//                if (minimap_function_id == 0xFFFF) {
//                    minimap_function_id = -1;
//                } else if (minimap_function_id == 13)
//                    minimap_function_id = 86;
//            } else if (opcode == 62)
//                rotated = true;
//            else if (opcode == 64)
//                cast_shadow = false;
//            else if (opcode == 65)
//                model_scale_x = buffer.readUShort();
//            else if (opcode == 66)
//                model_scale_y = buffer.readUShort();
//            else if (opcode == 67)
//                model_scale_z = buffer.readUShort();
//            else if (opcode == 68)
//                map_scene_id = buffer.readUShort();
//            else if (opcode == 69)
//                orientation = buffer.readUByte();
//            else if (opcode == 70)
//                translate_x = buffer.readSignedShort();
//            else if (opcode == 71)
//                translate_y = buffer.readSignedShort();
//            else if (opcode == 72)
//                translate_z = buffer.readSignedShort();
//            else if (opcode == 73)
//                obstructs_ground = true;
//            else if (opcode == 74)
//                unwalkable = true;
//            else if (opcode == 75)
//                merge_interact_state = buffer.readUByte();
//            else if (opcode == 77) {
//                varbit_id = buffer.readUShort();
//                if (varbit_id == 65535)
//                    varbit_id = -1;
//
//                varp_id = buffer.readUShort();
//                if (varp_id == 65535)
//                    varp_id = -1;
//
//                int length = buffer.readUByte();
//                configs = new int[length + 2];//+ 1
//                for (int index = 0; index <= length; index++) {
//                    configs[index] = buffer.readUShort();
//                    if (configs[index] == 65535)
//                        configs[index] = -1;
//                }
//                configs[length + 1] = -1;
//            } else if (opcode == 78) {
//                opcode_78_1 = buffer.readUShort();
//                opcode_78_and_79 = buffer.readUByte();
//            } else if (opcode == 79) {
//                opcode_79_1 = buffer.readUShort();
//                opcode_79_2 = buffer.readUShort();
//                opcode_78_and_79 = buffer.readUByte();
//                int length = buffer.readUByte();
//                opcode_79_3 = new int[length];
//                for (int index = 0; index < length; index++) {
//                    opcode_79_3[index] = buffer.readUShort();
//                }
//            } else if (opcode == 81) {
//                buffer.readUByte();// * 256;
//            } else if (opcode == 92) {
//                varbit_id = buffer.readUShort();
//                if (varbit_id == 65535)
//                    varbit_id = -1;
//
//                varp_id = buffer.readUShort();
//                if (varp_id == 65535)
//                    varp_id = -1;
//
//                int var = buffer.readUShort();
//                if (var == 65535)
//                    var = -1;
//
//                int length = buffer.readUByte();
//                configs = new int[length + 2];
//                for (int index = 0; index <= length; index++) {
//                    configs[index] = buffer.readUShort();
//                    if (configs[index] == 65535)
//                        configs[index] = -1;
//                }
//                configs[length + 1] = var;
//            }
//        } while (true);
//
//        post_decode();
//    }
    private void decode(Buffer buffer) {
        while (true) {
            int opcode = buffer.readUnsignedByte();
            if (opcode == 0)
                break;
            if (opcode == 1) {
                int len = buffer.readUnsignedByte();
                if (len > 0) {
                    if (model_ids == null) {
                        model_group = new int[len];
                        model_ids = new int[len];

                        for (int i = 0; i < len; i++) {
                            model_ids[i] = buffer.readUShort();
                            model_group[i] = buffer.readUnsignedByte();
                        }
                    } else {
                        buffer.pos += len * 3;
                    }
                }
            } else if (opcode == 2)
                name = buffer.readString();
            else if (opcode == 3)
                description = buffer.readString();
            else if (opcode == 5) {
                int len = buffer.readUnsignedByte();
                if (len > 0) {
                    if (model_ids == null) {
                        model_group = null;
                        model_ids = new int[len];

                        for (int i = 0; i < len; i++) {
                            model_ids[i] = buffer.readUShort();
                        }
                    } else {
                        buffer.pos += len * 3;
                    }
                }
            } else if (opcode == 14)
                width  = buffer.readUnsignedByte();
            else if (opcode == 15)
                height  = buffer.readUnsignedByte();
            else if (opcode == 17) {
                solid = false;
                walkable = false;
            } else if (opcode == 18)
                walkable = false;
            else if (opcode == 19)
                interact_state  = buffer.readUByte();
            else if (opcode == 21)
                contour_to_tile  = true;
            else if (opcode == 22)
                gouraud_shading  = true;
            else if (opcode == 23)
                occlude  = true;
            else if (opcode == 24) { // Object Animations
                animation = buffer.readUShort();
                if (animation == 65535)
                    animation = -1;
            } else if (opcode == 28)
                decor_offset  = buffer.readUnsignedByte();
            else if (opcode == 29)
                ambient  = buffer.readSignedByte();
            else if (opcode == 39)
                contrast  = buffer.readSignedByte();
            else if (opcode >= 30 && opcode < 35) {
                if (scene_actions  == null)
                    scene_actions  = new String[10];
                scene_actions [opcode - 30] = buffer.readString();
                if (scene_actions [opcode - 30].equalsIgnoreCase("hidden"))
                    scene_actions [opcode - 30] = null;
            } else if (opcode == 40) {
                int len = buffer.readUnsignedByte();
                src_color = new int[len];
                dst_color  = new int[len];
                for (int i = 0; i < len; i++) {
                    src_color[i] = buffer.readUShort();
                    dst_color [i] = buffer.readUShort();
                }
            } else if (opcode == 41) {
                int len = buffer.readUnsignedByte();
                dst_texture = new short[len];
                src_texture = new short[len];
                for (int i = 0; i < len; i++) {
                    dst_texture[i] = (short) buffer.readUShort();
                    src_texture[i] = (short) buffer.readUShort();
                }
            } else if (opcode == 61) {
                int category = buffer.readUShort();
            } else if (opcode == 62)
                rotated  = true;
            else if (opcode == 64)
                rotated  = false;
            else if (opcode == 65)
                model_scale_x  = buffer.readUShort();
            else if (opcode == 66)
                model_scale_y  = buffer.readUShort();
            else if (opcode == 67)
                model_scale_z  = buffer.readUShort();
            else if (opcode == 68)
                map_scene_id  = buffer.readUShort();
            else if (opcode == 69)
                orientation  = buffer.readUnsignedByte();
            else if (opcode == 70)
                translate_x  = buffer.readShort();
            else if (opcode == 71)
                translate_y  = buffer.readShort();
            else if (opcode == 72)
                translate_z  = buffer.readShort();
            else if (opcode == 73)
                obstructs_ground  = true;
            else if (opcode == 74)
                unwalkable  = true;
            else if (opcode == 75)
                merge_interact_state  = buffer.readUnsignedByte();
            else if (opcode == 77 || opcode == 92) {
                varbit_id  = buffer.readUShort();
                if (varbit_id  == 0xFFFF) {
                    varbit_id  = -1;
                }
                varp_id  = buffer.readUShort();
                if (varp_id  == 0xFFFF) {
                    varp_id  = -1;
                }




                int value = -1;

                if (opcode == 92) {
                    value = buffer.readUShort();

                    if (value == 0xFFFF) {
                        value = -1;
                    }
                }

                int len = buffer.readUnsignedByte();

                configs  = new int[len + 2];
                for (int i = 0; i <= len; ++i) {
                    configs [i] = buffer.readUShort();
                    if (configs [i] == 0xFFFF) {
                        configs [i] = -1;
                    }
                }
                configs [len + 1] = value;
            } else if(opcode == 78) {
                opcode_78_1  = buffer.readUShort();
                opcode_78_and_79  = buffer.readUnsignedByte();
            } else if(opcode == 79) {
                opcode_79_1  = buffer.readUShort();
                opcode_79_2  = buffer.readUShort();
                opcode_78_and_79  = buffer.readUShort();

                int length = buffer.readUnsignedByte();
                int[] anims = new int[length];

                for (int index = 0; index < length; ++index)
                {
                    anims[index] = buffer.readUShort();
                }
                int[] ambientSoundIds = anims;
            } else if(opcode == 81) {
                buffer.readUnsignedByte();
            } else if (opcode == 82) {
                int minimapFunction = buffer.readUShort();//AreaType
            } else if(opcode == 89) {
                boolean randomAnimStart = false;
            } else if (opcode == 249) {
                int length = buffer.readUnsignedByte();

                Map<Integer, Object> params = new HashMap<>(length);
                for (int i = 0; i < length; i++)
                {
                    boolean isString = buffer.readUnsignedByte() == 1;
                    int key = buffer.read24Int();
                    Object value;

                    if (isString) {
                        value = buffer.readString();
                        System.out.println(value);
                    } else {
                        value = buffer.readInt();
                    }

                    params.put(key, value);
                }

                this.params = params;
            } else {
                //System.err.printf("Error unrecognised {Objects} opcode: %d%n%n", opcode);
            }
        }
        post_decode();
    }
    public void post_decode() {
        if (interact_state == -1) {
            interact_state = 0;
            if (name != null && !name.equalsIgnoreCase("null")) {
                if (model_ids != null && (model_group == null || model_group[0] == 10))
                    interact_state = 1;//1

                if (scene_actions != null)
                    interact_state = 1;

            }
        }
        if (unwalkable) {
            solid = false;
            walkable = false;
        }
        if (merge_interact_state == -1)
            merge_interact_state = solid ? 1 : 0;

    }

    public static ObjectDefinition get(int id) {
        if (id > stream_indices.length) {
            id = stream_indices.length - 1;
        }

        if (id == 25913)
            id = 15552;

        if (id == 25916 || id == 25926)
            id = 15553;

        if (id == 25917)
            id = 15554;

        for (int index = 0; index < 20; index++) {
            if (cache[index].id == id) {
                return cache[index];
            }
        }

        cache_index = (cache_index + 1) % 20;
        ObjectDefinition def = cache[cache_index];
        data_buffer.pos = stream_indices[id];
        def.id = id;
        def.set_defaults();
        def.decode(data_buffer);
//def.interact_state = 1;
        if (def.id == 29308)//wintertoldt snow storm 1639 // 3997 cheap fix
            def.gouraud_shading = false;

        if (def.id >= 29167 && def.id <= 29225) {
            def.width = 1;
            def.solid = false;
            def.scene_actions = new String[]{"Take", null, null, null, null};
        }

        if (def.id == 14924) {
            def.width = 1;
        }

        if (ClientConstants.WILDERNESS_DITCH_DISABLED) {
            if (id == 23271) {
                def.model_ids = null;
                def.interact_state = 0;
                def.solid = false;
                return def;
            }
        }

        /*if(def.id > 16500) {
            if(def.delayShading == true)
                def.delayShading = false;

        }*/
        ObjectManager.get(id);

        /*if (def.name == null || def.name.equalsIgnoreCase("null"))
            def.name = "weee";

        def.interact_state = 1;*/
        return def;
    }

    public void set_defaults() {
        model_ids = null;
        model_group = null;
        name = null;
        description = null;
        src_color = null;
        dst_color = null;
        dst_texture = null;
        src_texture = null;
        width = 1;
        height = 1;
        solid = true;
        walkable = true;
        interact_state = -1;
        contour_to_tile = false;
        gouraud_shading = false;
        occlude = false;
        animation = -1;
        decor_offset = 16;
        ambient = 0;
        contrast = 0;
        scene_actions = null;
        minimap_function_id = -1;
        map_scene_id = -1;
        rotated = false;
        cast_shadow = true;
        model_scale_x = 128;
        model_scale_y = 128;
        model_scale_z = 128;
        orientation = 0;
        translate_x = 0;
        translate_y = 0;
        translate_z = 0;
        obstructs_ground = false;
        unwalkable = false;
        merge_interact_state = -1;
        varbit_id = -1;
        varp_id = -1;
        configs = null;
    }

    public void passive_request_load(ResourceProvider provider) {
        if (model_ids == null)
            return;

        for (int index = 0; index < model_ids.length; index++)
            provider.passive_request(model_ids[index] & 0xffff, 0);

    }

    public Model get_object(int type, int orientation, int cosine_y, int sine_y, int cosine_x, int sine_x, int animation_id) {
        Model model = get_animated_model(type, animation_id, orientation);

        if (model == null)
            return null;

        if (contour_to_tile || gouraud_shading) {
            model = new Model(contour_to_tile, gouraud_shading, model);
        }

        if (contour_to_tile) {
            int height = (cosine_y + sine_y + cosine_x + sine_x) / 4;
            for (int vertex = 0; vertex < model.vertices; vertex++) {
                int start_x = model.vertex_x[vertex];
                int start_y = model.vertex_z[vertex];
                int y = cosine_y + ((sine_y - cosine_y) * (start_x + 64)) / 128;
                int x = sine_x + ((cosine_x - sine_x) * (start_x + 64)) / 128;
                int undulation_offset = y + ((x - y) * (start_y + 64)) / 128;
                model.vertex_y[vertex] += undulation_offset - height;
            }
            model.computeSphericalBounds();
        }
        return model;
    }

    public boolean group_cached(int type) {
        if (model_group == null) {
            if (model_ids == null)
                return true;

            if (type != 10)
                return true;

            boolean cached = true;
            for (int index = 0; index < model_ids.length; index++)
                cached &= Model.cached(model_ids[index]);

            return cached;
        }
        for (int index = 0; index < model_group.length; index++)
            if (model_group[index] == type)
                return Model.cached(model_ids[index]);

        return true;
    }

    public boolean cached() {
        if (model_ids == null)
            return true;

        boolean cached = true;
        for (int model_id : model_ids) cached &= Model.cached(model_id);

        return cached;
    }

    public ObjectDefinition get_configs() {
        int setting_id = -1;
        if (varbit_id != -1) {
            VariableBits bit = VariableBits.cache[varbit_id];
            int setting = bit.configId;
            int low = bit.leastSignificantBit;
            int high = bit.mostSignificantBit;
            int mask = Client.BIT_MASKS[high - low];
            setting_id = Client.singleton.settings[setting] >> low & mask;
        } else if (varp_id != -1)
            setting_id = Client.singleton.settings[varp_id];

        if (setting_id < 0 || setting_id >= configs.length || configs[setting_id] == -1)
            return null;
        else
            return get(configs[setting_id]);
    }

    public Model get_animated_model(int type, int animation_id, int orientation) {
        Model model = null;
        long key;
        if (model_group == null) {
            if (type != 10)
                return null;

            key = (long) ((id << 6) + orientation) + ((long) (animation_id + 1) << 32);
            Model cached = (Model) model_cache.get(key);
            if (cached != null)
                return cached;

            if (model_ids == null)
                return null;

            boolean invert = rotated ^ (orientation > 3);
            int length = model_ids.length;
            for (int index = 0; index < length; index++) {
                int invert_id = model_ids[index];
                if (invert)
                    invert_id += 0x10000;

                model = (Model) animated_model_cache.get(invert_id);
                if (model == null) {
                    model = Model.get(invert_id & 0xffff);
                    if (model == null)
                        return null;

                    if (invert)
                        model.invert();

                    animated_model_cache.put(model, invert_id);
                }
                if (length > 1)
                    models[index] = model;

            }
            if (length > 1)
                model = new Model(length, models, true);//fixes rotating textures on objects

        } else {
            int model_id = -1;
            for (int index = 0; index < model_group.length; index++) {
                if (model_group[index] != type)
                    continue;

                model_id = index;
                break;
            }
            if (model_id == -1)
                return null;

            key = (long) ((id << 8) + (model_id << 3) + orientation) + ((long) (animation_id + 1) << 32);
            Model cached = (Model) model_cache.get(key);
            if (cached != null)
                return cached;

            model_id = model_ids[model_id];
            boolean invert = rotated ^ (orientation > 3);
            if (invert)
                model_id += 0x10000;

            model = (Model) animated_model_cache.get(model_id);
            if (model == null) {
                model = Model.get(model_id & 0xffff);
                if (model == null)
                    return null;

                if (invert)
                    model.invert();

                animated_model_cache.put(model, model_id);
            }
        }
        boolean scale = model_scale_x != 128 || model_scale_y != 128 || model_scale_z != 128;
        boolean translate = translate_x != 0 || translate_y != 0 || translate_z != 0;
        Model animated_model = new Model(src_color == null, Animation.validate(animation_id), orientation == 0 && animation_id == -1 && !scale && !translate, src_texture == null, model);
        if (animation_id != -1) {
            animated_model.skin();
            animated_model.interpolate(animation_id);
            animated_model.face_skin = null;
            animated_model.vertex_skin = null;
        }
        while (orientation-- > 0)
            animated_model.rotate_90();

        if (src_color != null) {
            for (int index = 0; index < src_color.length; index++)
                animated_model.recolor(src_color[index], dst_color[index]);

        }
        if (src_texture != null) {
            for (int index = 0; index < src_texture.length; index++) {
                animated_model.retexture(src_texture[index], dst_texture[index]);
            }
        }

        if (scale)
            animated_model.scale(model_scale_x, model_scale_z, model_scale_y);

        if (translate)
            animated_model.translate(translate_x, translate_y, translate_z);

        animated_model.light(60 + this.ambient, 768 + this.contrast, -50, -10, -50, !this.gouraud_shading); // LocoPk
        if (merge_interact_state == 1) {
            animated_model.obj_height = animated_model.model_height;
        }
        animated_model_cache.put(animated_model, key);
        return animated_model;
    }

    public static void release() {
        model_cache = null;
        animated_model_cache = null;
        stream_indices = null;
        cache = null;
        data_buffer = null;
    }

    public ObjectDefinition() {
        id = -1;
    }

    public static int length;
    public static int cache_index;
    public static boolean low_detail = ClientConstants.OBJECT_DEFINITION_LOW_MEMORY;
    public static Buffer data_buffer;
    public static ObjectDefinition[] cache;
    public static int[] stream_indices;
    public static final Model[] models = new Model[4];
    public static TempCache model_cache = new TempCache(500);
    public static TempCache animated_model_cache = new TempCache(30);

    public int id;
    public int width;
    public int height;
    public int animation;
    public int orientation;

    public int model_scale_x;
    public int model_scale_y;
    public int model_scale_z;
    public int translate_x;
    public int translate_y;
    public int translate_z;
    public int minimap_function_id;
    public int map_scene_id;
    public int interact_state;
    public int decor_offset;//
    public int merge_interact_state;//
    public int varp_id;
    public int varbit_id;

    public int[] model_ids;
    public int[] configs;
    public int[] model_group;

    public int[] src_color;
    public int[] dst_color;

    public short[] src_texture;
    public short[] dst_texture;

    public String name;
    public String description;
    public String[] scene_actions;

    public int contrast;
    public byte ambient;

    public boolean rotated;
    public boolean walkable;
    public boolean contour_to_tile;
    public boolean occlude;
    public boolean unwalkable;
    public boolean solid;
    public boolean cast_shadow;
    public boolean gouraud_shading;//
    public boolean obstructs_ground;

    /**
     * Later revisions
     */
    int opcode_78_1 = 2019882883;
    int opcode_79_1 = 0;
    int opcode_79_2 = 0;
    int opcode_78_and_79 = 0;
    int[] opcode_79_3;

}
