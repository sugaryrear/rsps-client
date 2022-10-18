package com.ferox.cache.def;

import com.ferox.ClientConstants;
import com.ferox.cache.Archive;
import com.ferox.cache.def.impl.items.CustomItems;
import com.ferox.cache.factory.ItemSpriteFactory;
import com.ferox.collection.TempCache;
import com.ferox.entity.model.Model;
import com.ferox.io.Buffer;
import com.ferox.model.texture.TextureCoordinate;
import com.ferox.util.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.ferox.util.ItemIdentifiers.*;

public final class ItemDefinition {

    public static void init(Archive archive) {
        data_buffer = new Buffer(ClientConstants.LOAD_OSRS_DATA_FROM_CACHE_DIR ? FileUtils.read(ClientConstants.DATA_DIR + "items/obj.dat") : archive
            .get("obj.dat"));
        Buffer index_buffer = new Buffer(ClientConstants.LOAD_OSRS_DATA_FROM_CACHE_DIR ? FileUtils.read(ClientConstants.DATA_DIR + "items/obj.idx") : archive
            .get("obj.idx"));
        length = index_buffer.readUShort();

        System.out.printf("Loaded %d items loading OSRS version %d and SUB version %d%n", length, ClientConstants.OSRS_DATA_VERSION, ClientConstants.OSRS_DATA_SUB_VERSION);

        pos = new int[length + 30_000];

        int offset = 2;
        for (int index = 0; index < length; index++) {
            pos[index] = offset;
            offset += index_buffer.readUShort();
        }

        cache = new ItemDefinition[10];

        for (int index = 0; index < 10; index++) {
            cache[index] = new ItemDefinition();
        }

        //dump();
    }

//    public void decode(Buffer buffer) {
//        while (true) {
//            int opcode = buffer.readUByte();
//            if (opcode == 0)
//                return;
//            if (opcode == 1)
//                inventory_model = buffer.readUShort();
//            else if (opcode == 2)
//                name = buffer.readString();
//            else if (opcode == 3)
//                description = buffer.readString();
//            else if (opcode == 4)
//                model_zoom = buffer.readUShort();
//            else if (opcode == 5)
//                rotation_y = buffer.readUShort();
//            else if (opcode == 6)
//                rotation_x = buffer.readUShort();
//            else if (opcode == 7) {
//                translate_x = buffer.readUShort();
//                if (translate_x > 32767)
//                    translate_x -= 0x10000;
//            } else if (opcode == 8) {
//                translate_y = buffer.readUShort();
//                if (translate_y > 32767)
//                    translate_y -= 0x10000;
//            } else if (opcode == 11)
//                stackable = true;
//            else if (opcode == 12)
//                cost = buffer.readInt();
//            else if (opcode == 16)
//                membership_required = true;
//            else if (opcode == 23) {
//                male_equip_main = buffer.readUShort();
//                male_equip_translate_y = buffer.readSignedByte();
//            } else if (opcode == 24)
//                male_equip_attachment = buffer.readUShort();
//            else if (opcode == 25) {
//                female_equip_main = buffer.readUShort();
//                female_equip_translate_y = buffer.readSignedByte();
//            } else if (opcode == 26)
//                female_equip_attachment = buffer.readUShort();
//            else if (opcode >= 30 && opcode < 35) {
//                if (scene_actions == null)
//                    scene_actions = new String[5];
//
//                scene_actions[opcode - 30] = buffer.readString();
//                if (scene_actions[opcode - 30].equalsIgnoreCase("hidden"))
//                    scene_actions[opcode - 30] = null;
//
//            } else if (opcode >= 35 && opcode < 40) {
//                if (widget_actions == null)
//                    widget_actions = new String[5];
//
//                widget_actions[opcode - 35] = buffer.readString();
//
//            } else if (opcode == 40) {
//                int length = buffer.readUByte();
//                //if models aren't recoloring properly, typically switch the position of src with dst
//                color_to_replace = new int[length];
//                color_to_replace_with = new int[length];
//                for (int index = 0; index < length; index++) {
//                    color_to_replace_with[index] = buffer.readUShort();
//                    color_to_replace[index] = buffer.readUShort();
//                }
//            } else if (opcode == 41) {
//                int length = buffer.readUByte();
//                src_texture = new short[length];
//                dst_texture = new short[length];
//                for (int index = 0; index < length; index++) {
//                    src_texture[index] = (short) buffer.readUShort();
//                    dst_texture[index] = (short) buffer.readUShort();
//                }
//            } else if (opcode == 42) {
//                buffer.readUByte();//shift_menu_index
//            } else if (opcode == 65) {
//                searchable = true;
//            } else if (opcode == 78)
//                male_equip_emblem = buffer.readUShort();
//            else if (opcode == 79)
//                female_equip_emblem = buffer.readUShort();
//            else if (opcode == 90)
//                male_dialogue_head = buffer.readUShort();
//            else if (opcode == 91)
//                female_dialogue_head = buffer.readUShort();
//            else if (opcode == 92)
//                male_dialogue_headgear = buffer.readUShort();
//            else if (opcode == 93)
//                female_dialogue_headgear = buffer.readUShort();
//            else if (opcode == 95)
//                rotation_z = buffer.readUShort();
//            else if (opcode == 97)
//                unnoted_item_id = buffer.readUShort();
//            else if (opcode == 98)
//                noted_item_id = buffer.readUShort();
//            else if (opcode >= 100 && opcode < 110) {
//                if (stack_variant_id == null) {
//                    stack_variant_id = new int[10];
//                    stack_variant_size = new int[10];
//                }
//                stack_variant_id[opcode - 100] = buffer.readUShort();
//                stack_variant_size[opcode - 100] = buffer.readUShort();
//            } else if (opcode == 110)
//                model_scale_x = buffer.readUShort();
//            else if (opcode == 111)
//                model_scale_y = buffer.readUShort();
//            else if (opcode == 112)
//                model_scale_z = buffer.readUShort();
//            else if (opcode == 113)
//                ambient = buffer.readSignedByte();
//            else if (opcode == 114)
//                contrast = buffer.readSignedByte(); //We had this as * 5 but runelite has it without * 5.
//            else if (opcode == 115)
//                team_id = buffer.readUByte();
//            else if (opcode == 139)
//                unnotedId = buffer.readUShort();
//            else if (opcode == 140)
//                notedId = buffer.readUShort();
//            else if (opcode == 148)
//                buffer.readUShort(); // placeholder id
//            else if (opcode == 149) {
//                buffer.readUShort(); // placeholder template
//            }
//        }
//    }
private void decode(Buffer buffer) {
    while (true) {
        int opcode = buffer.readUnsignedByte();
        if (opcode == 0)
            return;
        int category;
        int placeholder_id;
        if (opcode == 1)
            inventory_model = buffer.readUShort();
        else if (opcode == 2)
            name = buffer.readString();
        else if (opcode == 3)
            description = buffer.readString();
        else if (opcode == 4)
            model_zoom  = buffer.readUShort();
        else if (opcode == 5)
            rotation_y  = buffer.readUShort();
        else if (opcode == 6)
            rotation_x  = buffer.readUShort();
        else if (opcode == 7) {
            translate_x  = buffer.readUShort();
            if (translate_x  > 32767)
                translate_x  -= 0x10000;
        } else if (opcode == 8) {
            translate_y  = buffer.readUShort();
            if (translate_y  > 32767)
                translate_y  -= 0x10000;
        } else if (opcode == 11)
            stackable = true;
        else if (opcode == 12)
            cost  = buffer.readInt();
        else if (opcode == 16)
            membership_required = true;
        else if (opcode == 23) {
            male_equip_main = buffer.readUShort();
            male_equip_translate_y = buffer.readSignedByte();
        } else if (opcode == 24)
            male_equip_attachment = buffer.readUShort();
        else if (opcode == 25) {
            female_equip_main = buffer.readUShort();
            female_equip_attachment = buffer.readSignedByte();
        } else if (opcode == 26)
            female_equip_attachment  = buffer.readUShort();
        else if (opcode >= 30 && opcode < 35) {
            if (scene_actions == null)
                scene_actions = new String[5];
            scene_actions[opcode - 30] = buffer.readString();
            if (scene_actions[opcode - 30].equalsIgnoreCase("hidden"))
                scene_actions[opcode - 30] = null;
        } else if (opcode >= 35 && opcode < 40) {
            if (widget_actions == null)
                widget_actions = new String[5];
            widget_actions[opcode - 35] = buffer.readString();
        } else if (opcode == 40) {
            int length = buffer.readUnsignedByte();
            color_to_replace  = new int[length];
            color_to_replace_with  = new int[length];
            for (int index = 0; index < length; index++) {
                color_to_replace_with [index] = buffer.readUShort();
                color_to_replace [index] = buffer.readUShort();
            }
        } else if (opcode == 41) {
            int length = buffer.readUnsignedByte();
            src_texture = new short[length];
            dst_texture = new short[length];
            for (int index = 0; index < length; index++) {
                src_texture[index] = (short) buffer.readUShort();
                dst_texture[index] = (short) buffer.readUShort();
            }
        } else if (opcode == 42) {
            int shiftClickIndex = buffer.readUnsignedByte();
        } else if (opcode == 65) {
            searchable  = true;
        } else if (opcode == 78)
            male_equip_emblem = buffer.readUShort();
        else if (opcode == 79)
            female_equip_emblem = buffer.readUShort();
        else if (opcode == 90)
            male_dialogue_head = buffer.readUShort();
        else if (opcode == 91)
            female_dialogue_head = buffer.readUShort();
        else if (opcode == 92)
            male_dialogue_headgear  = buffer.readUShort();
        else if (opcode == 93)
            female_dialogue_headgear  = buffer.readUShort();
        else if (opcode == 94)
            category = buffer.readUShort();

        else if (opcode == 95)
            rotation_z  = buffer.readUShort();
        else if (opcode == 97)
            unnoted_item_id = buffer.readUShort();
        else if (opcode == 98)
            noted_item_id = buffer.readUShort();
        else if (opcode >= 100 && opcode < 110) {

            if (stack_variant_id == null) {
                stack_variant_id = new int[10];
                stack_variant_size = new int[10];
            }
            stack_variant_id[opcode - 100] = buffer.readUShort();
            stack_variant_size[opcode - 100] = buffer.readUShort();

        } else if (opcode == 110)
            model_scale_x  = buffer.readUShort();
        else if (opcode == 111)
            model_scale_y  = buffer.readUShort();
        else if (opcode == 112)
            model_scale_z  = buffer.readUShort();
        else if (opcode == 113)
            ambient  = buffer.readSignedByte();
        else if (opcode == 114)
            contrast  = buffer.readSignedByte() * 5;
        else if (opcode >= 100 && opcode < 110) {
            if (stack_variant_id == null) {
                stack_variant_id = new int[10];
                stack_variant_size = new int[10];
            }
            stack_variant_id[opcode - 100] = buffer.readUShort();
            stack_variant_size[opcode - 100] = buffer.readUShort();
        }
        else if (opcode == 115)
            team_id  = buffer.readUnsignedByte();
        else if (opcode == 139)
            unnotedId  = buffer.readUShort();
        else if (opcode == 140)
            notedId  = buffer.readUShort();
        else if (opcode == 148)
            placeholder_id = buffer.readUShort();
        else if (opcode == 149) {
            int placeholder_template_id = buffer.readUShort();
        } else if (opcode == 249) {
            int length = buffer.readUnsignedByte();

            params = new HashMap<>(length);

            for (int i = 0; i < length; i++) {
                boolean isString = buffer.readUnsignedByte() == 1;
                int key = buffer.read24Int();
                Object value;

                if (isString) {
                    value = buffer.readString();
                } else {
                    value = buffer.readInt();
                }

                params.put(key, value);
            }
        } else {
            System.err.printf("Error unrecognised {Items} opcode: %d%n%n", opcode);
        }
    }
}
    public static void copyInventory(ItemDefinition itemDef, int id) {
        ItemDefinition copy = ItemDefinition.get(id);
        itemDef.inventory_model = copy.inventory_model;
        itemDef.model_zoom = copy.model_zoom;
        itemDef.rotation_y = copy.rotation_y;
        itemDef.rotation_x = copy.rotation_x;
        itemDef.rotation_z = copy.rotation_z;
        itemDef.model_scale_x = copy.model_scale_x;
        itemDef.model_scale_y = copy.model_scale_y;
        itemDef.model_scale_z = copy.model_scale_z;
        itemDef.translate_x = copy.translate_x;
        itemDef.translate_y = copy.translate_y;
        itemDef.widget_actions = copy.widget_actions;
        itemDef.cost = copy.cost;
        itemDef.stackable = copy.stackable;
    }

    public static void copyEquipment(ItemDefinition itemDef, int id) {
        ItemDefinition copy = ItemDefinition.get(id);
        itemDef.male_equip_main = copy.male_equip_main;
        itemDef.male_equip_attachment = copy.male_equip_attachment;
        itemDef.female_equip_main = copy.female_equip_main;
        itemDef.female_equip_attachment = copy.female_equip_attachment;
        itemDef.male_equip_translate_y = copy.male_equip_translate_y;
        itemDef.female_equip_translate_y = copy.female_equip_translate_y;
    }

    public static void printStatement(final String text) {
        System.out.println(text + ";");
    }

    public static void printDefinitions(final ItemDefinition definition) {
        printStatement("definition.name = \"" + definition.name + "\"");
        printStatement("definition.model_zoom = " + definition.model_zoom);
        printStatement("definition.rotation_y = " + definition.rotation_y);
        printStatement("definition.rotation_x = " + definition.rotation_x);
        printStatement("definition.translate_x = " + definition.translate_x);
        printStatement("definition.translate_y = " + definition.translate_y);
        printStatement("definition.inventory_model = " + definition.inventory_model);
        printStatement("definition.male_equip_main = " + definition.male_equip_main);
        printStatement("definition.female_equip_main = " + definition.female_equip_main);
        printStatement("definition.color_to_replace = " + Arrays.toString(definition.color_to_replace));
        printStatement("definition.color_to_replace_with = " + Arrays.toString(definition.color_to_replace_with));
    }

    public static void dump() {
        File f = new File(System.getProperty("user.home") + "/Desktop/items.txt");
        try {
            f.createNewFile();
            BufferedWriter bf = new BufferedWriter(new FileWriter(f));
            for (int id = 0; id < ItemDefinition.length; id++) {
                ItemDefinition definition = ItemDefinition.get(id);

                bf.write("case " + id + ":");
                bf.write(System.getProperty("line.separator"));
                if (definition.name == null || definition.name.equals("null") ||
                    definition.name.isEmpty()) continue;

                bf.write("definition[id].name = " + definition.name + ";");
                bf.write(System.getProperty("line.separator"));
                if (definition.inventory_model != 0) {
                    bf.write("definition[id].inventory_model = " + definition.inventory_model + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.color_to_replace != null) {
                    bf.write("definition[id].color_to_replace = new int[] "
                        + Arrays.toString(definition.color_to_replace).replace("[", "{").replace("]", "}") + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.color_to_replace_with != null) {
                    bf.write("definition[id].color_to_replace_with = new int[] "
                        + Arrays.toString(definition.color_to_replace_with).replace("[", "{").replace("]", "}") + ";");
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
                if (definition.model_zoom != 2000) {
                    bf.write("definition[id].model_zoom = " + definition.model_zoom + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.rotation_y != 0) {
                    bf.write("definition[id].rotation_y = " + definition.rotation_y + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.rotation_x != 0) {
                    bf.write("definition[id].rotation_x = " + definition.rotation_x + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.rotation_z != 0) {
                    bf.write("definition[id].rotation_z = " + definition.rotation_z + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.translate_x != -1) {
                    bf.write("definition[id].translate_x = " + definition.translate_x + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.translate_y != -1) {
                    bf.write("definition[id].translate_y = " + definition.translate_y + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                bf.write("definition[id].stackable = " + definition.stackable + ";");
                bf.write(System.getProperty("line.separator"));
                if (definition.scene_actions != null) {
                    bf.write("definition[id].scene_actions = new int[] "
                        + Arrays.toString(definition.scene_actions).replace("[", "{").replace("]", "}") + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.widget_actions != null) {
                    bf.write("definition[id].widget_actions = new int[] "
                        + Arrays.toString(definition.widget_actions).replace("[", "{").replace("]", "}") + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.male_equip_main != -1) {
                    bf.write("definition[id].male_equip_main = " + definition.male_equip_main + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.male_equip_attachment != -1) {
                    bf.write("definition[id].male_equip_attachment = " + definition.male_equip_attachment + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.male_equip_translate_y != 0) {
                    bf.write("definition[id].male_equip_translate_y = " + definition.male_equip_translate_y + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.female_equip_main != -1) {
                    bf.write("definition[id].female_equip_main = " + definition.female_equip_main + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.female_equip_attachment != -1) {
                    bf.write("definition[id].female_equip_attachment = " + definition.female_equip_attachment + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.female_equip_translate_y != 0) {
                    bf.write("definition[id].female_equip_translate_y = " + definition.female_equip_translate_y + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.male_equip_emblem != -1) {
                    bf.write("definition[id].male_equip_emblem = " + definition.male_equip_emblem + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.female_equip_emblem != -1) {
                    bf.write("definition[id].female_equip_emblem = " + definition.female_equip_emblem + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.male_dialogue_head != -1) {
                    bf.write("definition[id].male_dialogue_head = " + definition.male_dialogue_head + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.male_dialogue_headgear != -1) {
                    bf.write("definition[id].male_dialogue_headgear = " + definition.male_dialogue_headgear + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.female_dialogue_head != -1) {
                    bf.write("definition[id].female_dialogue_head = " + definition.female_dialogue_head + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.female_dialogue_headgear != -1) {
                    bf.write("definition[id].female_dialogue_headgear = " + definition.female_dialogue_headgear + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.stack_variant_id != null) {
                    bf.write("definition[id].stack_variant_id = new int[] "
                        + Arrays.toString(definition.stack_variant_id).replace("[", "{").replace("]", "}") + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.stack_variant_size != null) {
                    bf.write("definition[id].stack_variant_size = new int[] "
                        + Arrays.toString(definition.stack_variant_size).replace("[", "{").replace("]", "}") + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.unnoted_item_id != -1) {
                    bf.write("definition[id].unnoted_item_id = " + definition.unnoted_item_id + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.noted_item_id != -1) {
                    bf.write("definition[id].model_scale_xy = " + definition.noted_item_id + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.model_scale_x != 128) {
                    bf.write("definition[id].model_scale_x = " + definition.model_scale_x + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.model_scale_y != 128) {
                    bf.write("definition[id].model_scale_y = " + definition.model_scale_y + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.model_scale_z != 128) {
                    bf.write("definition[id].model_scale_z = " + definition.model_scale_z + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.ambient != 0) {
                    bf.write("definition[id].ambient = " + definition.ambient + ";");
                    bf.write(System.getProperty("line.separator"));
                }
                if (definition.contrast != 0) {
                    bf.write("definition[id].contrast = " + definition.contrast + ";");
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

    public static ItemDefinition get(int id) {
        for (int index = 0; index < 10; index++)
            if (cache[index].id == id)
                return cache[index];

        cache_index = (cache_index + 1) % 10;
        ItemDefinition def = cache[cache_index];
        data_buffer.pos = pos[id];

        def.id = id;
        def.set_defaults();
        def.decode(data_buffer);

        if(def.name != null && (def.name.contains("Max cape") || def.name.contains("max cape"))) {
            def.widget_actions = new String[]{null, "Wear", "Features", null, "Drop"};
        }

        if(def.name != null && (def.name.contains("slayer helmet") || def.name.contains("Slayer helmet"))) {
            def.widget_actions = new String[]{null, "Wear", null, "Disassemble", "Drop"};
        }

        if(id == 6199) {
            def.widget_actions = new String[]{"Quick-open", null, null, "Open", null};
        }

        if(id == 24225) {
            def.widget_actions = new String[]{null, "Wield", null, null, null};
        }

        CustomItems.unpack(id);

        if (def.noted_item_id != -1)
            def.set_noted_values();

        int[] items = { BABYDRAGON_BONES, DRAGON_BONES, WYVERN_BONES, DAGANNOTH_BONES, FLIPPERS, SLED_4084, FISHBOWL_HELMET, DIVING_APPARATUS, CHICKEN_WINGS_11020, CHICKEN_LEGS_11022, CHICKEN_FEET_11019, CHICKEN_HEAD_11021, FANCY_BOOTS, GAS_MASK, MIME_MASK_10629, FROG_MASK, LIT_BUG_LANTERN, SKELETON_BOOTS, SKELETON_GLOVES, SKELETON_MASK, SKELETON_LEGGINGS, SKELETON_SHIRT, LIT_BUG_LANTERN, FOX, CHICKEN, BONESACK, GRAIN_5607, RUBBER_CHICKEN, BUNNY_EARS };
        if (def.name != null) {
            if (!def.name.startsWith("@gre@")) {
                for (int item_id : items) {
                    if (id == item_id) {
                        def.name = ("@gre@" + def.name);
                        break;
                    }
                }
            }
            if (!def.name.startsWith("@gre@"))
            {
                if ((def.name.toLowerCase().contains("pet")) || (def.name.toLowerCase().contains("3rd")) || (def.name.toLowerCase().contains("toxic")) || (def.name.toLowerCase().contains("occult")) || (def.name.toLowerCase().contains("anguish")) || (def.name.toLowerCase().contains("torture")) || (def.name.toLowerCase().contains("avernic")) || (def.name.toLowerCase().contains("serpentine")) || (def.name.toLowerCase().contains("tanzanite")) || (def.name.toLowerCase().contains("magma")) || (def.name.toLowerCase().contains("ancestral")) || (def.name.toLowerCase().contains("armadyl")) || (def.name.toLowerCase().contains("void"))
                    || (def.name.toLowerCase().contains("bandos")) || (def.name.toLowerCase().contains("pegasian")) || (def.name.toLowerCase().contains("primordial")) || (def.name.toLowerCase().contains("eternal")) || (def.name.toLowerCase().contains("partyhat")) || (def.name.toLowerCase().contains("staff of light")) || (def.name.toLowerCase().contains("infernal")) || (def.name.toLowerCase().contains("slayer helm")) || (def.name.toLowerCase().contains("dragon hunter")) || (def.name.toLowerCase().contains("spectral")) || (def.name.toLowerCase().contains("ballista"))
                    || (def.name.toLowerCase().contains("justiciar")) || (def.name.toLowerCase().contains("dragon claws")) || (def.name.toLowerCase().contains("bulwark")) || (def.name.toLowerCase().contains("dragon warhammer")) || (def.name.toLowerCase().contains("blessed sword")) || (def.name.toLowerCase().contains("godsword")) || (def.name.toLowerCase().contains("ward")) || (def.name.toLowerCase().contains("wyvern shield")) || (def.name.toLowerCase().contains("morrigan")) || (def.name.toLowerCase().contains("vesta")) || (def.name.toLowerCase().contains("zuriel"))
                    || (def.name.toLowerCase().contains("statius")) || (def.name.toLowerCase().contains("dragon crossbow")) || (def.name.toLowerCase().contains("abyssal dagger")) || (def.name.toLowerCase().contains("ghrazi")) || (def.name.toLowerCase().contains("elder maul")) || (def.name.toLowerCase().contains("tormented")) || (def.name.toLowerCase().contains("infinity")) || (def.name.toLowerCase().contains("dragonfire")) || (def.name.toLowerCase().contains("blessed spirit shield")) || (def.name.toLowerCase().contains("of the dead")) || (def.name.toLowerCase().contains("ice arrow"))
                    || (def.name.toLowerCase().contains("dragon javelin")) || (def.name.toLowerCase().contains("dragon knife")) || (def.name.toLowerCase().contains("dragon thrownaxe")) || (def.name.toLowerCase().contains("abyssal tentacle")) || (def.name.toLowerCase().contains("dark bow")) || (def.name.toLowerCase().contains("fremennik kilt")) || (def.name.toLowerCase().contains("spiked manacles")) || (def.name.toLowerCase().contains("fury")) || (def.name.toLowerCase().contains("dragon boots")) || (def.name.toLowerCase().contains("ranger boots")) || (def.name.toLowerCase().contains("mage's book"))
                    || (def.name.toLowerCase().contains("master wand")) || (def.name.toLowerCase().contains("granite maul")) || (def.name.toLowerCase().contains("tome of fire")) || (def.name.toLowerCase().contains("recoil")) || (def.name.toLowerCase().contains("dharok")) || (def.name.toLowerCase().contains("karil")) || (def.name.toLowerCase().contains("guthan")) || (def.name.toLowerCase().contains("torag")) || (def.name.toLowerCase().contains("verac")) || (def.name.toLowerCase().contains("ahrim")) || (def.name.toLowerCase().contains("fire cape")) || (def.name.toLowerCase().contains("max cape"))
                    || (def.name.toLowerCase().contains("blighted")) || (def.name.toLowerCase().contains("dragon defender")) || (def.name.toLowerCase().contains("healer hat")) || (def.name.toLowerCase().contains("fighter hat")) || (def.name.toLowerCase().contains("runner hat")) || (def.name.toLowerCase().contains("ranger hat")) || (def.name.toLowerCase().contains("fighter torso")) || (def.name.toLowerCase().contains("runner boots")) || (def.name.toLowerCase().contains("penance skirt")) || (def.name.toLowerCase().contains("looting bag")) || (def.name.toLowerCase().contains("rune pouch"))
                    || (def.name.toLowerCase().contains("stamina")) || (def.name.toLowerCase().contains("anti-venom")) || (def.name.toLowerCase().contains("zamorakian")) || (def.name.toLowerCase().contains("blood money")) || (def.name.toLowerCase().contains("hydra")) || (def.name.toLowerCase().contains("ferocious")) || (def.name.toLowerCase().contains("jar of")) || (def.name.toLowerCase().contains("brimstone")) || (def.name.toLowerCase().contains("crystal")) || (def.name.toLowerCase().contains("dagon")) || (def.name.toLowerCase().contains("dragon pickaxe")) || (def.name.toLowerCase().contains("tyrannical"))
                    || (def.name.toLowerCase().contains("dragon 2h")) || (def.name.toLowerCase().contains("elysian")) || (def.name.toLowerCase().contains("holy elixer")) || (def.name.toLowerCase().contains("odium")) || (def.name.toLowerCase().contains("malediction")) || (def.name.toLowerCase().contains("fedora")) || (def.name.toLowerCase().contains("suffering")) || (def.name.toLowerCase().contains("mole")) || (def.name.toLowerCase().contains("vampyre dust")) || (def.name.toLowerCase().contains("bludgeon")) || (def.name.toLowerCase().contains("kbd heads")) || (def.name.toLowerCase().contains("trident"))
                    || (def.name.toLowerCase().contains("nightmare")) || (def.name.toLowerCase().contains("kodai wand")) || (def.name.toLowerCase().contains("dragon sword")) || (def.name.toLowerCase().contains("dragon harpoon")) || (def.name.toLowerCase().contains("mystery box")) || (def.name.toLowerCase().contains("crystal key")) || (def.name.toLowerCase().contains("volatile")) || (def.name.toLowerCase().contains("eldritch")) || (def.name.toLowerCase().contains("harmonised")) || (def.name.toLowerCase().contains("inquisitor")) || (def.name.toLowerCase().contains("treasonous")) || (def.name.toLowerCase().contains("ring of the gods"))
                    || (def.name.toLowerCase().contains("vorkath")) || (def.name.toLowerCase().contains("dragonbone")) || (def.name.toLowerCase().contains("uncut onyx")) || (def.name.toLowerCase().contains("zulrah")) || (def.name.toLowerCase().contains("zul-andra")) || (def.name.toLowerCase().contains("sanguinesti")) || (def.name.toLowerCase().contains("blade of saeldor")) || (def.name.toLowerCase().contains("barrelchest anchor")) || (def.name.toLowerCase().contains("staff of balance")) || (def.name.toLowerCase().contains("twisted bow")) || (def.name.toLowerCase().contains("facegaurd")) || (def.name.toLowerCase().contains("guardian"))
                    || (def.name.toLowerCase().contains("twisted buckler")) || (def.name.toLowerCase().contains("dragon dart")) || (def.name.toLowerCase().contains("guthix rest")) || (def.name.toLowerCase().contains("obsidian")) || (def.name.toLowerCase().contains("regen bracelet")) || (def.name.toLowerCase().contains("rangers'")) || (def.name.toLowerCase().contains("dragon scimitar (or)")) || (def.name.toLowerCase().contains("ferox coins")) || (def.name.toLowerCase().contains("divine")) || (def.name.toLowerCase().contains("super antifire")) || (def.name.toLowerCase().contains("robin hood hat")) || (def.name.toLowerCase().contains("ankou"))
                    || (def.name.toLowerCase().contains("santa")) || (def.name.toLowerCase().contains("halloween"))) {
                    def.name = ("@gre@" + def.name);
                }
                if ((def.name.toLowerCase().contains("berserker ring")) || (def.name.toLowerCase().contains("seers")) || (def.name.toLowerCase().contains("archers")) || (def.name.toLowerCase().contains("warrior ring"))) {
                    def.name = ("@gre@" + def.name);
                }
                if (def.name.toLowerCase().contains("scythe")) {
                    def.name = ("@gre@" + def.name);
                }
                if (def.name.toLowerCase().contains("gilded")) {
                    def.name = ("@gre@" + def.name);
                }
                if (def.name.toLowerCase().contains("bunny")) {
                    def.name = ("@gre@" + def.name);
                }
                if (def.name.toLowerCase().contains("zanik")) {
                    def.name = ("@gre@" + def.name);
                }
                if (def.name.toLowerCase().contains("ele'")) {
                    def.name = ("@gre@" + def.name);
                }
                if (def.name.toLowerCase().contains("prince")) {
                    def.name = ("@gre@" + def.name);
                }
                if (def.name.toLowerCase().contains("zombie")) {
                    def.name = ("@gre@" + def.name);
                }
                if (def.name.toLowerCase().contains("mithril seeds")) {
                    def.name = ("@gre@" + def.name);
                }
                if (def.name.toLowerCase().contains("tribal")) {
                    def.name = ("@gre@" + def.name);
                }
                if (def.name.toLowerCase().contains("broodoo")) {
                    def.name = ("@gre@" + def.name);
                }
                if ((def.name.toLowerCase().contains("scarf")) || (def.name.toLowerCase().contains("woolly")) || (def.name.toLowerCase().contains("bobble"))) {
                    def.name = ("@gre@" + def.name);
                }
                if (def.name.toLowerCase().contains("cane")) {
                    def.name = ("@gre@" + def.name);
                }
                if (def.name.toLowerCase().contains("jester")) {
                    def.name = ("@gre@" + def.name);
                }
                if (def.name.toLowerCase().contains("(g)")) {
                    def.name = ("@gre@" + def.name);
                }
                if ((def.name.toLowerCase().contains("(t)")) && (!def.name.toLowerCase().endsWith("cape(t)"))) {
                    def.name = ("@gre@" + def.name);
                }
                if ((def.name.toLowerCase().contains("camo")) || (def.name.toLowerCase().contains("boxing glove"))) {
                    def.name = ("@gre@" + def.name);
                }
                if (def.name.toLowerCase().contains("dharok")) {
                    def.name = ("@gre@" + def.name);
                }
                if (def.name.toLowerCase().contains("dragon spear")) {
                    def.name = ("@gre@" + def.name);
                }
                if (def.name.toLowerCase().contains("phoenix neck")) {
                    def.name = ("@gre@" + def.name);
                }
                if (def.name.toLowerCase().contains("dragon bolts (e)")) {
                    def.name = ("@gre@" + def.name);
                }
            }
        }
        return def;
    }

    private void set_defaults() {
        inventory_model = 0;
        name = null;
        description = null;
        color_to_replace = null;
        color_to_replace_with = null;
        src_texture = null;
        dst_texture = null;
        model_zoom = 2000;
        rotation_y = 0;
        rotation_x = 0;
        rotation_z = 0;
        translate_x = 0;
        translate_y = 0;
        stackable = false;
        cost = 1;
        membership_required = false;
        scene_actions = null;
        widget_actions = null;
        male_equip_main = -1;
        male_equip_attachment = -1;
        male_equip_translate_y = 0;
        female_equip_main = -1;
        female_equip_attachment = -1;
        female_equip_translate_y = 0;
        male_equip_emblem = -1;
        female_equip_emblem = -1;
        male_dialogue_head = -1;
        male_dialogue_headgear = -1;
        female_dialogue_head = -1;
        female_dialogue_headgear = -1;
        stack_variant_id = null;
        stack_variant_size = null;
        unnoted_item_id = -1;
        noted_item_id = -1;
        model_scale_x = 128;
        model_scale_y = 128;
        model_scale_z = 128;
        ambient = 0;
        contrast = 0;
        team_id = 0;
        animate_inv_sprite = false;
        modelCustomColor = 0;
        modelCustomColor2 = 0;
        modelCustomColor3 = 0;
        modelCustomColor4 = 0;
        modelSetColor = 0;
    }

    private void set_noted_values() {
        ItemDefinition noted = get(noted_item_id);
        inventory_model = noted.inventory_model;
        model_zoom = noted.model_zoom;
        rotation_y = noted.rotation_y;
        rotation_x = noted.rotation_x;
        rotation_z = noted.rotation_z;
        translate_x = noted.translate_x;
        translate_y = noted.translate_y;
        color_to_replace = noted.color_to_replace;
        color_to_replace_with = noted.color_to_replace_with;

        ItemDefinition unnoted = get(unnoted_item_id);
        name = unnoted.name;
        membership_required = unnoted.membership_required;
        cost = unnoted.cost;

        String consonant_or_vowel_lead = "a";
        if (!ClientConstants.OSRS_DATA) {
            char character = unnoted.name.charAt(0);
            if (character == 'A' || character == 'E' || character == 'I' || character == 'O' || character == 'U')
                consonant_or_vowel_lead = "an";
        } else {
            String character = unnoted.name;
            if (character.equals("A") || character.equals("E") || character.equals("I") || character
                .equals("O") || character.equals("U"))
                consonant_or_vowel_lead = "an";
        }
        description = ("Swap this note at any bank for " + consonant_or_vowel_lead + " " + unnoted.name + ".");
        stackable = true;
    }

    public Model get_model(int stack_size) {
        if (stack_variant_id != null && stack_size > 1) {
            int stack_item_id = -1;
            for (int index = 0; index < 10; index++)
                if (stack_size >= stack_variant_size[index] && stack_variant_size[index] != 0)
                    stack_item_id = stack_variant_id[index];

            if (stack_item_id != -1)
                return get(stack_item_id).get_model(1);

        }
        Model model = (Model) model_cache.get(id);
        if (model != null) {
            return model;
        }

        model = Model.get(inventory_model);
        if (model == null) {
            return null;
        }
        if (model_scale_x != 128 || model_scale_y != 128 || model_scale_z != 128)
            model.scale(model_scale_x, model_scale_z, model_scale_y);
        //System.err.println("Color to replace: " + color_to_replace + " | for id: " + id);
        if (color_to_replace != null) {
            //System.out.println("ISNT for model: " + id);
            for (int index = 0; index < color_to_replace.length; index++) {
                model.recolor(color_to_replace[index], color_to_replace_with[index]);
            }
        }
        if (src_texture != null) {
            for (int index = 0; index < src_texture.length; index++) {
                model.retexture(src_texture[index], dst_texture[index]);
            }
        }
        /*if (color_to_replace != null && color_to_replace_with != null) {
            if (src_texture != null && dst_texture != null) {
                for (int index = 0; index < color_to_replace.length; index++) {
                    model.color_to_texture(model, src_texture[index], dst_texture[index], false);
                }
            }
        }*/
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

        model.light(64 + ambient, 768 + contrast, -50, -10, -50, true);
        model.within_tile = true;
        model_cache.put(model, id);
        return model;
    }

    public Model get_widget_model(int stack_size) {
        if (stack_variant_id != null && stack_size > 1) {
            int stack_item_id = -1;
            for (int index = 0; index < 10; index++) {
                if (stack_size >= stack_variant_size[index] && stack_variant_size[index] != 0)
                    stack_item_id = stack_variant_id[index];
            }
            if (stack_item_id != -1)
                return get(stack_item_id).get_widget_model(1);

        }
        Model widget_model = Model.get(inventory_model);
        if (widget_model == null)
            return null;
        //System.err.println("Color to replace: " + color_to_replace + " | for id: " + id);
        if (color_to_replace != null) {
            //System.out.println("ISNT for model: " + id);
            for (int index = 0; index < color_to_replace.length; index++) {
                widget_model.recolor(color_to_replace[index], color_to_replace_with[index]);
            }

        }
        if (src_texture != null) {
            for (int index = 0; index < src_texture.length; index++) {
                widget_model.retexture(src_texture[index], dst_texture[index]);
            }
        }
        /*if (color_to_replace != null && color_to_replace_with != null) {
            if (src_texture != null && dst_texture != null) {
                for (int index = 0; index < color_to_replace.length; index++) {
                    widget_model.color_to_texture(widget_model, src_texture[index], dst_texture[index], false);
                }
            }
        }*/

        //System.err.println("Color to replace: " + color_to_replace + " | for id: " + id);

        if (modelCustomColor > 0) {
            widget_model.completelyRecolor(modelCustomColor);
        }
        if (modelCustomColor2 != 0) {
            widget_model.shadingRecolor(modelCustomColor2);
        }
        if (modelCustomColor3 != 0) {
            widget_model.shadingRecolor2(modelCustomColor3);
        }
        if (modelCustomColor4 != 0) {
            widget_model.shadingRecolor4(modelCustomColor4);
        }
        if (modelSetColor != 0) {
            widget_model.shadingRecolor3(modelSetColor);
        }

        return widget_model;
    }

    public Model get_equipped_model(int gender) {
        int main = male_equip_main;
        int attatchment = male_equip_attachment;
        int emblem = male_equip_emblem;
        if (gender == 1) {
            main = female_equip_main;
            attatchment = female_equip_attachment;
            emblem = female_equip_emblem;
        }
        if (main == -1)
            return null;

        Model equipped_model = Model.get(main);
        if (equipped_model == null) {
            return null;
        }
        if (attatchment != -1) {
            if (emblem != -1) {
                Model attachment_model = Model.get(attatchment);
                Model emblem_model = Model.get(emblem);
                Model[] list = {
                    equipped_model, attachment_model, emblem_model
                };
                equipped_model = new Model(3, list, true);
            } else {
                Model attachment_model = Model.get(attatchment);
                Model[] list = {
                    equipped_model, attachment_model
                };
                equipped_model = new Model(2, list, true);
            }
        }
        if (gender == 0 && male_equip_translate_y != 0)
            equipped_model.translate(0, male_equip_translate_y, 0);

        if (gender == 1 && female_equip_translate_y != 0)
            equipped_model.translate(0, female_equip_translate_y, 0);

        if (color_to_replace != null) {
            //System.out.println("ISNT for model: " + id);
            for (int index = 0; index < color_to_replace.length; index++) {
                equipped_model.recolor(color_to_replace[index], color_to_replace_with[index]);
            }
        }
        if (src_texture != null) {
            for (int index = 0; index < src_texture.length; index++) {
                equipped_model.retexture(src_texture[index], dst_texture[index]);
            }
        }
       /* if (color_to_replace != null && color_to_replace_with != null) {
            if (src_texture != null && dst_texture != null) {
                for (int index = 0; index < color_to_replace.length; index++) {
                    equipped_model.color_to_texture(equipped_model, src_texture[index], dst_texture[index], true);
                }
            }
        }*/
        if (modelCustomColor > 0) {
            equipped_model.completelyRecolor(modelCustomColor);
        }
        if (modelCustomColor2 != 0) {
            equipped_model.shadingRecolor(modelCustomColor2);
        }
        if (modelCustomColor3 != 0) {
            equipped_model.shadingRecolor2(modelCustomColor3);
        }
        if (modelCustomColor4 != 0) {
            equipped_model.shadingRecolor4(modelCustomColor4);
        }
        if (modelSetColor != 0) {
            equipped_model.shadingRecolor3(modelSetColor);
        }

        return equipped_model;
    }

    public boolean equipped_model_cached(int gender) {
        int main = male_equip_main;
        int attachment = male_equip_attachment;
        int emblem = male_equip_emblem;
        if (gender == 1) {
            main = female_equip_main;
            attachment = female_equip_attachment;
            emblem = female_equip_emblem;
        }
        if (main == -1)
            return true;

        boolean cached = true;
        if (!Model.cached(main))
            cached = false;

        if (attachment != -1 && !Model.cached(attachment))
            cached = false;

        if (emblem != -1 && !Model.cached(emblem))
            cached = false;

        return cached;
    }

    public Model get_equipped_dialogue_model(int gender) {
        int head_model = male_dialogue_head;
        int equipped_headgear = male_dialogue_headgear;
        if (gender == 1) {
            head_model = female_dialogue_head;
            equipped_headgear = female_dialogue_headgear;
        }
        if (head_model == -1)
            return null;

        Model dialogue_model = Model.get(head_model);
        if (equipped_headgear != -1) {
            Model headgear = Model.get(equipped_headgear);
            Model[] list = {
                dialogue_model, headgear
            };
            dialogue_model = new Model(2, list, true);
        }
        if (color_to_replace != null) {
            for (int index = 0; index < color_to_replace.length; index++) {
                dialogue_model.recolor(color_to_replace[index], color_to_replace_with[index]);
            }

        }
        if (src_texture != null) {
            for (int index = 0; index < src_texture.length; index++) {
                dialogue_model.retexture(src_texture[index], dst_texture[index]);
            }
        }
       /* if (color_to_replace != null && color_to_replace_with != null) {
            if (src_texture != null && dst_texture != null) {
                for (int index = 0; index < color_to_replace.length; index++) {
                    dialogue_model.color_to_texture(dialogue_model, src_texture[index], dst_texture[index], false);
                }
            }
        }*/
        return dialogue_model;
    }

    public boolean dialogue_model_cached(int gender) {
        int head_model = male_dialogue_head;
        int equipped_headgear = male_dialogue_headgear;
        if (gender == 1) {
            head_model = female_dialogue_head;
            equipped_headgear = female_dialogue_headgear;
        }
        if (head_model == -1)
            return true;

        boolean cached = true;
        if (!Model.cached(head_model))
            cached = false;

        if (equipped_headgear != -1 && !Model.cached(equipped_headgear))
            cached = false;

        return cached;
    }

    public static void release() {
        model_cache = null;
        ItemSpriteFactory.sprites_cache = null;
        ItemSpriteFactory.scaled_cache = null;
        pos = null;
        cache = null;
        data_buffer = null;
    }

    private ItemDefinition() {
        id = -1;
    }

    public static int length;
    private static int cache_index;
    private static Buffer data_buffer;
    private static ItemDefinition[] cache;
    private static int[] pos;
    public static TempCache model_cache = new TempCache(50);

    public int cost;
    public int id;
    public int team_id;
    public int model_zoom;
    public int rotation_x;
    public int rotation_y;
    public int rotation_z;
    public int inventory_model;
    public int male_equip_main;
    public int male_equip_attachment;
    public int male_equip_emblem;

    public int female_equip_main;
    public int female_equip_attachment;
    public int female_equip_emblem;

    public int male_dialogue_head;
    private int male_dialogue_headgear;
    public byte male_equip_translate_y;

    public int female_dialogue_head;
    private int female_dialogue_headgear;
    public byte female_equip_translate_y;

    public int translate_x;
    public int translate_y;
    private int model_scale_x;
    private int model_scale_y;
    private int model_scale_z;
    public int noted_item_id;
    public int unnoted_item_id;
    public int ambient;
    public int contrast;
    public int[] stack_variant_id;
    public int[] stack_variant_size;
    public int[] color_to_replace;
    public int[] color_to_replace_with;
    public short[] src_texture;
    public short[] dst_texture;

    public String[] widget_actions;
    public String[] scene_actions;
    public String name;
    public String description;
    public static boolean membership_required;
    public boolean stackable;
    public boolean animateInventory;

    public boolean animate_inv_sprite;

    public boolean searchable;
    public int unnotedId;
    public int notedId;

    public Map<Integer, Object> params = null;

    //Custom coloring
    public int modelCustomColor = 0;
    public int modelCustomColor2 = 0;
    public int modelCustomColor3 = 0;
    public int modelCustomColor4 = 0;
    public int modelSetColor = 0;

    public static int setInventoryModel(final int id) {
        final ItemDefinition definition = get(id);
        return definition.inventory_model;
    }

    public static String setItemName(final int id) {
        final ItemDefinition definition = get(id);
        return definition.name;
    }

    public static int setMaleEquipmentId(final int id) {
        final ItemDefinition definition = get(id);
        return definition.male_equip_main;
    }

    public static int setFemaleEquipmentId(final int id) {
        final ItemDefinition definition = get(id);
        return definition.female_equip_main;
    }

    public static int setModelZoom(final int id) {
        final ItemDefinition definition = get(id);
        return definition.model_zoom;
    }

    public static int setRotationX(final int id) {
        final ItemDefinition definition = get(id);
        return definition.rotation_x;
    }

    public static int setRotationY(final int id) {
        final ItemDefinition definition = get(id);
        return definition.rotation_y;
    }

    public static int setTranslateX(final int id) {
        final ItemDefinition definition = get(id);
        return definition.translate_x;
    }

    public static int setTranslateY(final int id) {
        final ItemDefinition definition = get(id);
        return definition.translate_y;
    }
}

