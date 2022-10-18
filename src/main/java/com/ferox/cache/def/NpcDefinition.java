package com.ferox.cache.def;

import com.ferox.Client;
import com.ferox.ClientConstants;
import com.ferox.cache.Archive;
import com.ferox.cache.anim.Animation;
import com.ferox.cache.config.VariableBits;
import com.ferox.cache.def.impl.npcs.CustomBosses;
import com.ferox.cache.def.impl.npcs.CustomPets;
import com.ferox.cache.def.impl.NpcManager;
import com.ferox.cache.def.impl.npcs.MemberNpcs;
import com.ferox.collection.TempCache;
import com.ferox.entity.model.Model;
import com.ferox.io.Buffer;
import com.ferox.util.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class NpcDefinition {

    public static int totalNPCs;

    public static void init(Archive archive) {
        buffer = new Buffer(ClientConstants.LOAD_OSRS_DATA_FROM_CACHE_DIR ? FileUtils.read(ClientConstants.DATA_DIR+"/npcs/npc.dat") : archive.get("npc.dat"));
        final Buffer metaBuf = new Buffer(ClientConstants.LOAD_OSRS_DATA_FROM_CACHE_DIR ? FileUtils.read(ClientConstants.DATA_DIR+"/npcs/npc.idx") : archive.get("npc.idx"));
        totalNPCs = metaBuf.readUShort();

        System.out.printf("Loaded %d npcs loading OSRS version %d and SUB version %d%n", totalNPCs, ClientConstants.OSRS_DATA_VERSION, ClientConstants.OSRS_DATA_SUB_VERSION);

        offsets = new int[totalNPCs + 30_000];
        int metaOffset = 2;
        for (int i = 0; i < totalNPCs; i++) {
            offsets[i] = metaOffset;
            metaOffset += metaBuf.readUShort();
        }

        cache = new NpcDefinition[20];

        for (int i = 0; i < 20; i++) {
            cache[i] = new NpcDefinition();
        }
        if(dump) {
            dump();
        }
    }

    private static final boolean dump = false;

    public static int getModelIds(final int id, final int models) {
        final NpcDefinition npcDefinition = get(id);
        return npcDefinition.model_id[models];
    }

    public static int getadditionalModels(final int id, final int models) {
        final NpcDefinition npcDefinition = get(id);
        return npcDefinition.additionalModels[models];
    }

    public static int getModelColorIds(final int id, final int color) {
        final NpcDefinition npcDefinition = get(id);
        return npcDefinition.src_color[color];
    }

    public static int getStandAnim(final int id) {
        final NpcDefinition npcDefinition = get(id);
        return npcDefinition.standingAnimation;
    }

    public static int getWalkAnim(final int id) {
        final NpcDefinition entityDef = get(id);
        return entityDef.walkingAnimation;
    }

    public static int getHalfTurnAnimation(final int id) {
        final NpcDefinition entityDef = get(id);
        return entityDef.halfTurnAnimation;
    }

    public static int getQuarterClockwiseTurnAnimation(final int id) {
        final NpcDefinition entityDef = get(id);
        return entityDef.quarterClockwiseTurnAnimation;
    }

    public static int getQuarterAnticlockwiseTurnAnimation(final int id) {
        final NpcDefinition entityDef = get(id);
        return entityDef.quarterAnticlockwiseTurnAnimation;
    }

    public static NpcDefinition get(int id) {
        for (int i = 0; i < 20; i++) {
            if (cache[i].interfaceType == (long) id) {
                return cache[i];
            }
        }

        cache_index = (cache_index + 1) % 20;
        NpcDefinition npcDefinition = cache[cache_index] = new NpcDefinition();
        buffer.pos = offsets[id];
        npcDefinition.id = id;
        npcDefinition.interfaceType = id;
        npcDefinition.decode(buffer);

        /*if(id == 8492) {
            System.out.println("Actions: " + Arrays.toString(npcDefinition.actions));
        }*/

        if(id == 1612) {
            npcDefinition.name = "Battle mage";
            npcDefinition.actions = new String[]{null, "Attack", null, null, null};
            npcDefinition.src_color = new int[]{22426, 926};
            npcDefinition.dst_color = new int[]{8090, 22426};
            npcDefinition.cmb_level = 54;
            npcDefinition.model_id = new int[]{2909, 2898, 2920};
            npcDefinition.standingAnimation = 195;
            npcDefinition.walkingAnimation = 189;
        }

        if(!dump) {
            NpcManager.unpack(id);
            CustomPets.unpack(id);
            CustomBosses.unpack(id);
            MemberNpcs.unpack(id);
        }

        /*if(id == 8492) {
            System.out.println("Actions now: " + Arrays.toString(npcDefinition.actions));
        }*/

        return npcDefinition;
    }

    public static void dump() {
        File f = new File(System.getProperty("user.home") + "/Desktop/npcs.txt");
        try {
            f.createNewFile();
            BufferedWriter bf = new BufferedWriter(new FileWriter(f));
            for (int id = 0; id < NpcDefinition.totalNPCs; id++) {
                NpcDefinition definition = NpcDefinition.get(id);

                bf.write("case " + id + ":");
                bf.write(System.getProperty("line.separator"));
                if (definition.name == null || definition.name.equals("null") ||
                    definition.name.isEmpty()) continue;

                bf.write("definition[id].name = " + definition.name + ";");
                bf.write(System.getProperty("line.separator"));
                if (definition.model_id != null) {
                    bf.write("definition[id].model_id = new int[] "
                        + Arrays.toString(definition.model_id).replace("[", "{").replace("]", "}") + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.occupied_tiles != 1) {
                    bf.write("definition[id].occupied_tiles = " + definition.occupied_tiles + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.standingAnimation != -1) {
                    bf.write("definition[id].standingAnimation = " + definition.standingAnimation + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.walkingAnimation != -1) {
                    bf.write("definition[id].walkingAnimation = " + definition.walkingAnimation + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.halfTurnAnimation != -1) {
                    bf.write("definition[id].halfTurnAnimation = " + definition.halfTurnAnimation + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.quarterClockwiseTurnAnimation != -1) {
                    bf.write("definition[id].quarterClockwiseTurnAnimation = " + definition.quarterClockwiseTurnAnimation + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.quarterAnticlockwiseTurnAnimation != -1) {
                    bf.write("definition[id].quarterAnticlockwiseTurnAnimation = " + definition.quarterAnticlockwiseTurnAnimation + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.actions != null) {
                    bf.write("definition[id].actions = new int[] "
                        + Arrays.toString(definition.actions).replace("[", "{").replace("]", "}") + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.src_color != null) {
                    bf.write("definition[id].src_color = new int[] "
                        + Arrays.toString(definition.src_color).replace("[", "{").replace("]", "}") + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.dst_color != null) {
                    bf.write("definition[id].dst_color = new int[] "
                        + Arrays.toString(definition.dst_color).replace("[", "{").replace("]", "}") + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.src_texture != null) {
                    bf.write("definition[id].src_texture = new int[] "
                        + Arrays.toString(definition.src_texture).replace("[", "{").replace("]", "}") + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.dst_texture != null) {
                    bf.write("definition[id].dst_texture = new int[] "
                        + Arrays.toString(definition.dst_texture).replace("[", "{").replace("]", "}") + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.additionalModels != null) {
                    bf.write("definition[id].additionalModels = new int[] "
                        + Arrays.toString(definition.additionalModels).replace("[", "{").replace("]", "}") + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.cmb_level != -1) {
                    bf.write("definition[id].cmb_level = " + definition.cmb_level + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.resizeX != 128) {
                    bf.write("definition[id].model_scale_xy = " + definition.resizeX + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.resizeY != 128) {
                    bf.write("definition[id].model_scale_z = " + definition.resizeY + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (!definition.render_priority) {
                    bf.write("definition[id].render_priority = " + definition.render_priority + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.ambient != -1) {
                    bf.write("definition[id].ambient = " + definition.ambient + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.contrast != -1) {
                    bf.write("definition[id].contrast = " + definition.contrast + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.headIcon != -1) {
                    bf.write("definition[id].headIcon = " + definition.headIcon + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.rotation != 32) {
                    bf.write("definition[id].rotation = " + definition.rotation + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.varbit != -1) {
                    bf.write("definition[id].varbit = " + definition.varbit + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.varp != -1) {
                    bf.write("definition[id].varp = " + definition.varp + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.configs != null) {
                    bf.write("definition[id].configs = new int[] "
                        + Arrays.toString(definition.configs).replace("[", "{").replace("]", "}") + ";");
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

    public static void copy(NpcDefinition definition, int id) {
        NpcDefinition copy = NpcDefinition.get(id);
        definition.occupied_tiles = copy.occupied_tiles;
        definition.rotation = copy.rotation;
        definition.standingAnimation = copy.standingAnimation;
        definition.walkingAnimation = copy.walkingAnimation;
        definition.halfTurnAnimation = copy.halfTurnAnimation;
        definition.quarterClockwiseTurnAnimation = copy.quarterClockwiseTurnAnimation;
        definition.quarterAnticlockwiseTurnAnimation = copy.quarterAnticlockwiseTurnAnimation;
        definition.varbit = copy.varbit;
        definition.varp = copy.varp;
        definition.cmb_level = copy.cmb_level;
        definition.name = copy.name;
        definition.description = copy.description;
        definition.headIcon = copy.headIcon;
        definition.isClickable = copy.isClickable;
        definition.ambient = copy.ambient;
        definition.resizeY = copy.resizeY;
        definition.resizeX = copy.resizeX;
        definition.renderOnMinimap = copy.renderOnMinimap;
        definition.contrast = copy.contrast;
        definition.actions = new String[copy.actions.length];
        System.arraycopy(copy.actions, 0, definition.actions, 0, definition.actions.length);
        definition.model_id = new int[copy.model_id.length];
        System.arraycopy(copy.model_id, 0, definition.model_id, 0, definition.model_id.length);
        definition.render_priority = copy.render_priority;
    }

    public Model get_dialogue_model() {
        if (configs != null) {
            NpcDefinition entityDef = get_configs();
            if (entityDef == null)
                return null;
            else
                return entityDef.get_dialogue_model();
        }
        if (additionalModels == null)
            return null;
        boolean cached = false;
        for (int index = 0; index < additionalModels.length; index++)
            if (!Model.cached(additionalModels[index]))
                cached = true;

        if (cached)
            return null;
        Model head_model[] = new Model[additionalModels.length];
        for (int index = 0; index < additionalModels.length; index++)
            head_model[index] = Model.get(additionalModels[index]);

        Model dialogue_model;
        if (head_model.length == 1)
            dialogue_model = head_model[0];
        else
            dialogue_model = new Model(head_model.length, head_model, true);

        if (modelCustomColor > 0) {
            dialogue_model.completelyRecolor(modelCustomColor);
        }
        if (modelCustomColor2 != 0) {
            dialogue_model.shadingRecolor(modelCustomColor2);
        }
        if (modelCustomColor3 != 0) {
            dialogue_model.shadingRecolor2(modelCustomColor3);
        }
        if (modelCustomColor4 != 0) {
            dialogue_model.shadingRecolor4(modelCustomColor4);
        }
        if (modelSetColor != 0) {
            dialogue_model.shadingRecolor3(modelSetColor);
        }

        if (src_color != null) {
            for (int k = 0; k < src_color.length; k++)
                dialogue_model.recolor(src_color[k], dst_color[k]);
        }
        
        if (src_texture != null) {
            for (int index = 0; index < src_texture.length; index++)
                dialogue_model.retexture(src_texture[index], dst_texture[index]);
        }

        return dialogue_model;
    }

    public NpcDefinition get_configs() {
        //Ken comment: Added try catch to get_configs method to catch any config errors
        try {
            int j = -1;
            if (varbit != -1) {
                VariableBits varBit = VariableBits.cache[varbit];
                int k = varBit.configId;
                int l = varBit.leastSignificantBit;
                int i1 = varBit.mostSignificantBit;
                int j1 = Client.BIT_MASKS[i1 - l];
                j = clientInstance.settings[k] >> l & j1;
            } else if (varp != -1)
                j = clientInstance.settings[varp];
            if (j < 0 || j >= configs.length || configs[j] == -1)
                return null;
            else
                return get(configs[j]);
        } catch (Exception e) {
            System.err.println("There was an error getting configs for NPC " + id);
            e.printStackTrace();
        }
        //Ken comment: return null if we haven't returned already, this shouldn't be possible.
        return null;
    }

    public static void clear() {
        model_cache = null;
        offsets = null;
        cache = null;
        buffer = null;
    }

    public Model get_animated_model(int animation, int current, int[] label) {
        if (configs != null) {
            final NpcDefinition def = get_configs();
            if (def == null) {
                return null;
            } else {
                return def.get_animated_model(animation, current, label);
            }
        }
        Model model = (Model) model_cache.get(interfaceType);
        if (model == null) {
            boolean cached = false;
            if(model_id == null) {
                return null;
            }
            for (int i : model_id) {
                if (!Model.cached(i)) {
                    cached = true;
                }
            }
            if (cached) {
                return null;
            }
            final Model[] models = new Model[model_id.length];
            for (int index = 0; index < model_id.length; index++) {
                models[index] = Model.get(model_id[index]);
            }
            if (models.length == 1) {
                model = models[0];
            } else {
                model = new Model(models.length, models, true);
            }
            if (src_color != null) {
                for (int k1 = 0; k1 < src_color.length; k1++) {
                    model.recolor(src_color[k1], dst_color[k1]);
                }
            }
            if (src_texture != null) {
                for (int index = 0; index < src_texture.length; index++)
                    model.retexture(src_texture[index], dst_texture[index]);
            }

            if (modelCustomColor > 0) {
                model.completelyRecolor(modelCustomColor);
            }
            if (modelCustomColor2 != 0) {
                model.shadingRecolor(modelCustomColor2);
            }
            if (modelCustomColor3 != 0) {
                model.shadingRecolor2(modelCustomColor3);
            }
            if (modelCustomColor4 != 0) {
                model.shadingRecolor4(modelCustomColor4);
            }
            if (modelSetColor != 0) {
                model.shadingRecolor3(modelSetColor);
            }

            model.skin();
            model.light(84 + ambient, 1000 + contrast, -90, -580, -90, true);
            model_cache.put(model, interfaceType);
        }
        final Model animated_model = Model.EMPTY_MODEL;
        animated_model.replace(model, Animation.validate(current) & Animation.validate(animation));
        if (current != -1 && animation != -1)
            animated_model.mix(label, animation, current);
        else if (current != -1)
            animated_model.interpolate(current);
        
        if (resizeX != 128 || resizeY != 128) {
            animated_model.scale(resizeX, resizeY, resizeX);
        }
        
        animated_model.calc_diagonals();
        animated_model.face_skin = null;
        animated_model.vertex_skin = null;
        if (occupied_tiles == 1) {
            animated_model.within_tile = true;
        }
        return animated_model;
    }

//    public void decode(Buffer buffer) {
//        while (true) {
//            int opcode = buffer.readUByte();
//            if (opcode == 0) {
//                return;
//            } else if (opcode == 1) {
//                int len = buffer.readUByte();
//                model_id = new int[len];
//                for (int i = 0; i < len; i++) {
//                    model_id[i] = buffer.readUShort();
//                }
//            } else if (opcode == 2) {
//                name = buffer.readString();
//            } else if (opcode == 12) {
//                occupied_tiles = buffer.readUByte();
//            } else if (opcode == 13) {
//                standingAnimation = buffer.readUShort();
//            } else if (opcode == 14) {
//                walkingAnimation = buffer.readUShort();
//            } else if (opcode == 15) {
//                buffer.readUShort(); //rotate left anim
//            } else if (opcode == 16) {
//                buffer.readUShort(); //rotate right anim
//            } else if (opcode == 17) {
//                walkingAnimation = buffer.readUShort();
//                halfTurnAnimation = buffer.readUShort();
//                quarterClockwiseTurnAnimation = buffer.readUShort();
//                quarterAnticlockwiseTurnAnimation = buffer.readUShort();
//                if (halfTurnAnimation == 65535) {
//                    halfTurnAnimation = walkingAnimation;
//                }
//                if (quarterClockwiseTurnAnimation == 65535) {
//                    quarterClockwiseTurnAnimation = walkingAnimation;
//                }
//                if (quarterAnticlockwiseTurnAnimation == 65535) {
//                    quarterAnticlockwiseTurnAnimation = walkingAnimation;
//                }
//            } else if (opcode >= 30 && opcode < 35) {
//                if (actions == null) {
//                    actions = new String[5];
//                }
//
//                actions[opcode - 30] = buffer.readString();
//
//                if (actions[opcode - 30].equalsIgnoreCase("Hidden")) {
//                    actions[opcode - 30] = null;
//                }
//            } else if (opcode == 40) {
//                int len = buffer.readUByte();
//                src_color = new int[len];
//                dst_color = new int[len];
//                for (int i = 0; i < len; i++) {
//                    src_color[i] = buffer.readUShort();
//                    dst_color[i] = buffer.readUShort();
//                }
//
//            } else if (opcode == 41) {
//                int length = buffer.readUByte();
//                src_texture = new short[length];
//                dst_texture = new short[length];
//                for (int index = 0; index < length; index++) {
//                    src_texture[index] = (short) buffer.readUShort();
//                    dst_texture[index] = (short) buffer.readUShort();
//                }
//            } else if (opcode == 60) {
//                int len = buffer.readUByte();
//                additionalModels = new int[len];
//                for (int i = 0; i < len; i++) {
//                    additionalModels[i] = buffer.readUShort(); //chatheadModels
//                }
//            } else if (opcode == 93) {
//                //Make sure to draw PK bots in the minimap (NPC IDs 13000 to 13009)
//                if (id < 13000 || id > 13009)
//                    renderOnMinimap = false; //isMinimapVisible
//            } else if (opcode == 95)
//                cmb_level = buffer.readUShort();
//            else if (opcode == 97)
//                resizeX = buffer.readUShort(); //widthScale
//            else if (opcode == 98)
//                resizeY = buffer.readUShort();
//            else if (opcode == 99)
//                render_priority = true;
//            else if (opcode == 100)
//                ambient = buffer.readSignedByte();
//            else if (opcode == 101)
//                contrast = buffer.readSignedByte();
//            else if (opcode == 102)
//                headIcon = buffer.readUShort();
//            else if (opcode == 103)
//                rotation = buffer.readUShort();
//            else if (opcode == 106) {
//                varbit = buffer.readUShort();
//                if (varbit == 65535) {
//                    varbit = -1;
//                }
//
//                varp = buffer.readUShort();
//                if (varp == 65535) {
//                    varp = -1;
//                }
//
//                int length = buffer.readUnsignedByte();
//                configs = new int[length + 2];
//
//                for (int index = 0; index <= length; ++index) {
//                    configs[index] = buffer.readUShort();
//                    if (configs[index] == 65535) {
//                        configs[index] = -1;
//                    }
//                }
//
//                configs[length + 1] = -1;
//            }
//            else if (opcode == 107) {
//                //isInteractable = false;
//            }
//            else if (opcode == 109) {
//                isClickable = false; //rotationFlag
//            } else if (opcode == 111) {
//                //isPet = true;
//            }
//            else if (opcode == 118) {
//                varbit = buffer.readUShort();
//                if (varbit == 65535) {
//                    varbit = -1;
//                }
//
//                varp = buffer.readUShort();
//                if (varp == 65535) {
//                    varp = -1;
//                }
//
//                int var = buffer.readUShort();
//                if (var == 0xFFFF) {
//                    var = -1;
//                }
//
//                int length = buffer.readUnsignedByte();
//                configs = new int[length + 2];
//
//                for (int index = 0; index <= length; ++index) {
//                    configs[index] = buffer.readUShort();
//                    if (configs[index] == 65535) {
//                        configs[index] = -1;
//                    }
//                }
//
//                configs[length + 1] = var;
//            } else if (opcode == 249) {
//                int length = buffer.readUnsignedByte();
//
//                this.params = new HashMap<>(length);
//
//                for (int index = 0; index < length; index++) {
//                    boolean isString = buffer.readUnsignedByte() == 1;
//                    int key = buffer.read24Int();
//                    Object value;
//
//                    if (isString) {
//                        value = buffer.readString();
//                    } else {
//                        value = buffer.readInt();
//                    }
//
//                    this.params.put(key, value);
//                }
//            }
//        }
//    }
private void decode(Buffer buffer) {
    while (true) {
        int opcode = buffer.readUnsignedByte();
        if (opcode == 0)
            return;
        if (opcode == 1) {
            int j = buffer.readUnsignedByte();
            model_id  = new int[j];
            for (int j1 = 0; j1 < j; j1++) {
                model_id [j1] = buffer.readUShort();
            }
        } else if (opcode == 2)
            name = buffer.readString();
        else if (opcode == 3)
            description = buffer.readString();
        else if (opcode == 12)
            occupied_tiles  = buffer.readSignedByte();
        else if (opcode == 13)
            standingAnimation = buffer.readUShort();
        else if (opcode == 14)
            walkingAnimation = buffer.readUShort();
        else if(opcode == 15) {
            int rotateLeftAnimation = buffer.readUShort();
        } else if(opcode == 16) {
            int rotateRightAnimation = buffer.readUShort();
        } else if (opcode == 17) {
            walkingAnimation = buffer.readUShort();
            halfTurnAnimation  = buffer.readUShort();
            quarterClockwiseTurnAnimation  = buffer.readUShort();
            quarterAnticlockwiseTurnAnimation  = buffer.readUShort();
            if (halfTurnAnimation  == 65535) {
                halfTurnAnimation  = -1;
            }
            if (quarterClockwiseTurnAnimation  == 65535) {
                quarterClockwiseTurnAnimation  = -1;
            }
            if (quarterAnticlockwiseTurnAnimation  == 65535) {
                quarterAnticlockwiseTurnAnimation  = -1;
            }
        } else if(opcode == 18){
            int category = buffer.readUShort();
        } else if (opcode >= 30 && opcode < 40) {
            if (actions == null)
                actions = new String[10];
            actions[opcode - 30] = buffer.readString();
            if (actions[opcode - 30].equalsIgnoreCase("hidden"))
                actions[opcode - 30] = null;
        } else if (opcode == 40) {
            int k = buffer.readUnsignedByte();
            src_color  = new int[k];
            dst_color  = new int[k];
            for (int k1 = 0; k1 < k; k1++) {
                src_color [k1] = buffer.readUShort();
                dst_color [k1] = buffer.readUShort();
            }
        } else if (opcode == 41) {
            int length = buffer.readUnsignedByte();
            src_texture = new short[length];
            dst_texture = new short[length];
            for (int index = 0; index < length; index++) {
                src_texture[index] = (short) buffer.readUShort();
                dst_texture[index] = (short) buffer.readUShort();
            }

        } else if (opcode == 60) {
            int l = buffer.readUnsignedByte();
            additionalModels  = new int[l];
            for (int l1 = 0; l1 < l; l1++) {
                additionalModels [l1] = buffer.readUShort();
            }
        } else if (opcode == 93)
            renderOnMinimap  = false;
        else if (opcode == 95)
            cmb_level  = buffer.readUShort();
        else if (opcode == 97)
            resizeX  = buffer.readUShort();
        else if (opcode == 98)
            resizeY  = buffer.readUShort();
        else if (opcode == 99)
            render_priority  = true;
        else if (opcode == 100)
            ambient  = buffer.readSignedByte();
        else if (opcode == 101)
            contrast  = buffer.readSignedByte();
        else if (opcode == 102)
            headIcon = buffer.readUShort();
        else if (opcode == 103)
            rotation  = buffer.readUShort();
        else if (opcode == 109) {
            isClickable  = false;
        } else if (opcode == 111) {
          //  isPet = true;
        }
        else if (opcode == 106 || opcode == 118) {
            varbit  = buffer.readUShort();
            if (varbit  == 65535)
                varbit  = -1;
            varp  = buffer.readUShort();
            if (varp  == 65535)
                varp  = -1;

            int var3 = -1;
            if(opcode == 118) {
                var3 = buffer.readUShort();
            }
            int i1 = buffer.readUnsignedByte();
            configs  = new int[i1 + 2];
            for (int i2 = 0; i2 <= i1; i2++) {
                configs [i2] = buffer.readUShort();
                if (configs [i2] == 65535)
                    configs [i2] = -1;
            }
            configs [i1 + 1] = var3;

        } else if (opcode == 107) {
            //    clickable = false;
        }  else if (opcode == 249)
        {
            int length = buffer.readUnsignedByte();

            params = new HashMap<>(length);

            for (int i = 0; i < length; i++) {
                boolean isString = buffer.readUnsignedByte() == 1;
                int key = buffer.read24Int();
                Object value;

                if (isString) {
                    value = buffer.readString();
                }

                else {
                    value = buffer.readInt();
                }

                params.put(key, value);
            }
        } else {
            System.err.printf("Error unrecognised {NPC} opcode: %d%n%n", opcode);
            continue;
        }
    }
}
    public NpcDefinition() {
        modelCustomColor = 0;
        modelCustomColor2 = 0;
        modelCustomColor3 = 0;
        modelCustomColor4 = 0;
        modelSetColor = 0;
        quarterAnticlockwiseTurnAnimation = -1;
        varbit = -1;
        halfTurnAnimation = -1;
        varp = -1;
        cmb_level = -1;
        anInt64 = 1834;
        walkingAnimation = -1;
        occupied_tiles = 1;
        headIcon = -1;
        standingAnimation = -1;
        interfaceType = -1L;
        rotation = 32;
        quarterClockwiseTurnAnimation = -1;
        isClickable = true;
        resizeY = 128;
        renderOnMinimap = true;
        resizeX = 128;
        render_priority = false;
    }

    public int modelCustomColor;
    public int modelCustomColor2;
    public int modelCustomColor3;
    public int modelCustomColor4;
    public int modelSetColor;
    public int quarterAnticlockwiseTurnAnimation;
    public static int cache_index;
    public int varbit;
    public int halfTurnAnimation;
    public int varp;
    public static Buffer buffer;
    public int cmb_level;
    public boolean largeHpBar;
    public final int anInt64;
    public String name;
    public String[] actions;
    public int walkingAnimation;
    public int occupied_tiles;
    public int[] dst_color;
    public static int[] offsets;
    public int[] additionalModels;
    public int headIcon;
    public short[] src_texture;
    public short[] dst_texture;
    public int[] src_color;
    public int standingAnimation;
    public long interfaceType;
    public int rotation;
    public static NpcDefinition[] cache;
    public static Client clientInstance;
    public int quarterClockwiseTurnAnimation;
    public boolean isClickable;
    public int ambient;
    public int resizeY;
    public boolean renderOnMinimap;
    public boolean pet;
    public int[] configs;
    public String description;
    public int resizeX;
    public int contrast;
    public boolean render_priority;
    public int[] model_id;
    public int interfaceZoom = 0;
    public int id;
    public static TempCache model_cache = new TempCache(30);
    private Map<Integer, Object> params = null;
}
