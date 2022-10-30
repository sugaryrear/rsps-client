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
import com.ferox.cache.def.ItemList;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

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
//        try {
//            printit(35);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }



    }
    public static ItemList getItemList(int itemId) {
        if (itemId < 0 || itemId > ItemList.length) {
            return null;
        }
        return ItemList[itemId];
    }

    public static void newItemList(int itemId, String ItemName, String ItemDescription, double ShopValue, double LowAlch, double HighAlch, int Bonuses[]) {
        ItemList newItemList = new ItemList(itemId);
        newItemList.itemName = ItemName;
        newItemList.itemDescription = ItemDescription;
        newItemList.ShopValue = ShopValue;
        newItemList.LowAlch = LowAlch;
        newItemList.HighAlch = HighAlch;
        newItemList.Bonuses = Bonuses;
        ItemList[itemId] = newItemList;
    }
    public static ItemList[] ItemList;
    public static boolean loadItemList(String FileName) {
        String line = "";
        String token = "";
        String token2 = "";
        String token2_2 = "";
        String[] token3;
        ItemList = new ItemList[70000];
        for (int i = 0; i < 70000; i++) {
            ItemList[i] = null;
        }
        try (BufferedReader file = new BufferedReader(new FileReader( FileName))) {
            while ((line = file.readLine()) != null && !line.equals("[ENDOFITEMLIST]")) {
                line = line.trim();
                int spot = line.indexOf("=");
                if (spot > -1) {
                    token = line.substring(0, spot);
                    token = token.trim();
                    token2 = line.substring(spot + 1);
                    token2 = token2.trim();
                    token2_2 = token2.replaceAll("\t\t", "\t");
                    token2_2 = token2_2.replaceAll("\t\t", "\t");
                    token2_2 = token2_2.replaceAll("\t\t", "\t");
                    token2_2 = token2_2.replaceAll("\t\t", "\t");
                    token2_2 = token2_2.replaceAll("\t\t", "\t");
                    token3 = token2_2.split("\t");
                    if (token.equals("item")) {
                        int[] Bonuses = new int[12];
                        for (int i = 0; i < 12; i++)
                            if (token3[(6 + i)] != null) {
                                Bonuses[i] = Integer.parseInt(token3[(6 + i)]);
                            } else {
                                break;
                            }
                        newItemList(Integer.parseInt(token3[0]), token3[1].replaceAll("_", " "), token3[2].replaceAll("_", " "), Double.parseDouble(token3[4]),
                            Double.parseDouble(token3[4]), Double.parseDouble(token3[6]), Bonuses);
                    }
                }
            }
        } catch (FileNotFoundException fileex) {
            System.out.println("file not found");
            return false;
        } catch (IOException ioexception) {
            return false;
        }
        return true;
    }
    private static BufferedWriter writer;

    public static void printit(int id) throws IOException {
        writer = new BufferedWriter(new FileWriter(ClientConstants.DATA_DIR + "items/item.json", true));
        for(ItemList item : ItemList){

            if(item == null)
                continue;
            writer.write("  {\n\t\"id\": " + item.itemId + ",\n\t\"bonuses\": [\n\t  " + item.Bonuses[0] + ",\n\t  " + item.Bonuses[1]
                + ",\n\t  " + item.Bonuses[2]+ ",\n\t  " + item.Bonuses[3] + ",\n\t  "
                + item.Bonuses[4] + ",\n\t  " + item.Bonuses[5] + ",\n\t  " + item.Bonuses[6]
                + ",\n\t  " + item.Bonuses[7] + ",\n\t  " + item.Bonuses[8] + ",\n\t  "
                + item.Bonuses[9] + ",\n\t  " + item.Bonuses[10] + ",\n\t  " + item.Bonuses[11] + "\n\t]\n\t },\n");


        }            writer.close();
    }
    public static void getExamineInfo(int i) throws IOException {

        String[] bonuses = new String[14];
        bonuses[0] = bonuses[1] = bonuses[2] = bonuses[3] = bonuses[4] = bonuses[5] = bonuses[6] = bonuses[7] = bonuses[8] = bonuses[9] = bonuses[10] = bonuses[13] = "";
        int slotnumber = 0;
        ItemDefinition item = ItemDefinition.get(i);

        URL url;
        url = new URL("https://oldschool.runescape.wiki/w/" + item.name.replaceAll(" ", "_").replace("(beta - dragon)","").replace("(beta - rune)","").replace("(beta - adamant)","").replace("(beta - mithril)","").replace("(beta - black)","").replace("(beta - steel)","").replace("(beta - iron)","").replace("(beta - bronze)","").replace("(beta)","").replace("(uncharged)","").replace("(l)","").replace("(broken)","").replace("(4)","").replace("(3)","") .replace("(2)","") .replace("(1)","")  + "?action=raw");
        URLConnection con = url.openConnection();
        con.addRequestProperty("User-Agent", "Chrome");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

        String output = "";
        String fullmask = "false";
        String twoHanded = "false";
        String stackable = "false";
        String noteable = "true";
        String tradeable = "";
        // boolean wearable1 = false;
        String wearable = "true";
        String showBeard = "true";
        String members = "true";

        String whichslot = "";
        String isittradeable = "";
        List<String> examine = new ArrayList<>();
        List<String> stab = new ArrayList<>();
        List<String> slash = new ArrayList<>();
        List<String> crush = new ArrayList<>();
        List<String> magic = new ArrayList<>();
        List<String> range = new ArrayList<>();
        List<String> dstab = new ArrayList<>();
        List<String> dslash = new ArrayList<>();
        List<String> dcrush = new ArrayList<>();
        List<String> dmagic = new ArrayList<>();
        List<String> drange = new ArrayList<>();
        List<String> str = new ArrayList<>();
        List<String> prayer = new ArrayList<>();
        List<String> slot= new ArrayList<>();
        List<String> tradeables= new ArrayList<>();

        writer = new BufferedWriter(new FileWriter(System.getProperty("user.home") + "/Desktop/Item Dumpbonuses.cfg", true));
        java.util.stream.Stream <String>tradeableitem=in.lines();
        tradeableitem.filter(line ->line.contains("|tradeable")).findFirst().ifPresent(tradeables::add);

        java.util.stream.Stream <String>examines=in.lines();
        examines.filter(line ->line.contains("|examine")).findFirst().ifPresent(examine::add);

        java.util.stream.Stream <String>stabs=in.lines();
        stabs.filter(line ->line.contains("|astab")).findFirst().ifPresent(stab::add);

        java.util.stream.Stream <String>slashes=in.lines();
        slashes.filter(line ->line.contains("|aslash")).findFirst().ifPresent(slash::add);

        java.util.stream.Stream <String>crushes=in.lines();
        crushes.filter(line ->line.contains("|acrush")).findFirst().ifPresent(crush::add);

        java.util.stream.Stream <String>magics=in.lines();
        magics.filter(line ->line.contains("|amagic")).findFirst().ifPresent(magic::add);

        java.util.stream.Stream <String>ranges=in.lines();
        ranges.filter(line ->line.contains("|arange")).findFirst().ifPresent(range::add);

        java.util.stream.Stream <String>dstabs=in.lines();
        dstabs.filter(line ->line.contains("|dstab")).findFirst().ifPresent(dstab::add);

        java.util.stream.Stream <String>dslashes=in.lines();
        dslashes.filter(line ->line.contains("|dslash")).findFirst().ifPresent(dslash::add);

        java.util.stream.Stream <String>dcrushes=in.lines();
        dcrushes.filter(line ->line.contains("|dcrush")).findFirst().ifPresent(dcrush::add);

        java.util.stream.Stream <String>dmagics=in.lines();
        dmagics.filter(line ->line.contains("|dmagic")).findFirst().ifPresent(dmagic::add);

        java.util.stream.Stream <String>dranges=in.lines();
        dranges.filter(line ->line.contains("|drange")).findFirst().ifPresent(drange::add);

        java.util.stream.Stream <String>strength=in.lines();
        strength.filter(line ->line.contains("|str")).findFirst().ifPresent(str::add);

        java.util.stream.Stream <String>prayers=in.lines();
        prayers.filter(line ->line.contains("|prayer")).findFirst().ifPresent(prayer::add);

        java.util.stream.Stream <String>slots=in.lines();
        slots.filter(line ->line.contains("|slot")).findFirst().ifPresent(slot::add);

        tradeable = tradeables.isEmpty() ? "false" : tradeables.get(0).toString().replace("|tradeable = ","").replace("|tradeable1 = ","");



        output = examine.isEmpty() ? "null" : examine.get(0).toString().replace("|examine = ","").replace("|examine1 = ","").replaceAll("1 dose of"," ").replaceAll("2 dose of"," ").replaceAll("3 dose of"," ").replaceAll("4 dose of"," ");

        bonuses[0] = stab.isEmpty() ? "0" : stab.get(0).toString().replace("|astab = ","").replace("|astab1 = ","").replace("|astab2 = ","").replace("+","");
        bonuses[1] = slash.isEmpty() ? "0" : slash.get(0).toString().replace("|aslash = ","").replace("|aslash1 = ","").replace("|aslash2 = ","").replace("+","");
        bonuses[2] = crush.isEmpty() ? "0" : crush.get(0).toString().replace("|acrush = ","").replace("|acrush1 = ","").replace("|acrush2 = ","").replace("+","");
        bonuses[3] = magic.isEmpty() ? "0"  : magic.get(0).toString().replace("|amagic = ","").replace("|amagic1 = ","").replace("|amagic2 = ","").replace("+","");
        bonuses[4] = range.isEmpty() ? "0" : range.get(0).toString().replace("|arange = ","").replace("|arange1 = ","").replace("|arange2 = ","").replace("+","");
        bonuses[5] = dstab.isEmpty() ? "0"  : dstab.get(0).toString().replace("|dstab = ","").replace("|dstab1 = ","").replace("|dstab2 = ","").replace("+","");
        bonuses[6] = dslash.isEmpty() ? "0"  : dslash.get(0).toString().replace("|dslash = ","").replace("|dslash1 = ","").replace("|dslash2 = ","").replace("+","");
        bonuses[7] = dcrush.isEmpty() ? "0"  : dcrush.get(0).toString().replace("|dcrush = ","").replace("|dcrush1 = ","").replace("|dcrush2 = ","").replace("+","");
        bonuses[8] = dmagic.isEmpty() ? "0"  :dmagic.get(0).toString().replace("|dmagic = ","").replace("|dmagic1 = ","").replace("|dmagic2 = ","").replace("+","");
        bonuses[9] = drange.isEmpty() ? "0"  : drange.get(0).toString().replace("|drange = ","").replace("|drange1 = ","").replace("|drange2 = ","").replace("+","");
        bonuses[10] = str.isEmpty() ? "0"  : str.get(0).toString().replace("|str = ","").replace("|str1 = ","").replace("|str2 = ","").replace("+","");
        bonuses[13] = prayer.isEmpty() ? "0"  : prayer.get(0).toString().replace("|prayer = ","").replace("|prayer1 = ","").replace("|prayer2 = ","").replace("+","");

        whichslot = slot.isEmpty() ? "3" : slot.get(0).toString().replace("|slot = ","");
//System.out.println(tradeables.isEmpty()+"");

        if(whichslot.contains("weapon") || whichslot.contains("2h"))
            slotnumber = Integer.parseInt("3");
        if(whichslot.contains("body"))
            slotnumber = Integer.parseInt("4");
        if(whichslot.contains("head"))
            slotnumber = Integer.parseInt("0");
        if(whichslot.contains("cape"))
            slotnumber = Integer.parseInt("1");
        if(whichslot.contains("shield"))
            slotnumber = Integer.parseInt("5");
        if(whichslot.contains("legs"))
            slotnumber = Integer.parseInt("7");
        if(whichslot.contains("hands"))
            slotnumber = Integer.parseInt("9");
        if(whichslot.contains("feet"))
            slotnumber = Integer.parseInt("10");
        if(whichslot.contains("ring"))
            slotnumber = Integer.parseInt("12");
        if(whichslot.contains("neck"))
            slotnumber = Integer.parseInt("2");
        if(whichslot.contains("ammo"))
            slotnumber = Integer.parseInt("13");


        //System.out.println(tradeable);
        if(tradeable.contains("No"))
            isittradeable = tradeable.replace("No","false");
        if(tradeable.contains("Yes"))
            isittradeable = 	tradeable.replace("Yes","true");

        in.close();
        System.out.println("Got item: ("+i+") "+item.name);
        //Client.instance.pushMessage("Got item info for: @blu@"+item.name+"@bla@(id:"+i+") type ::reloaditems ", 0, "");


        writer.write("  {\n\t\"id\": " + i + ",\n\t\"name\": \"" + item.name
            + "\",\n\t\"desc\": \"" + output + "\",\n\t\"value\": "
            + item.cost + ",\n\t\"dropValue\": " + item.cost + ",\n\t\"bonus\": [\n\t  "
            + bonuses[0] + ",\n\t  " + bonuses[1] + ",\n\t  " + bonuses[2]
            + ",\n\t  " + bonuses[3] + ",\n\t  " + bonuses[4] + ",\n\t  "
            + bonuses[5] + ",\n\t  " + bonuses[6] + ",\n\t  " + bonuses[7]
            + ",\n\t  " + bonuses[8] + ",\n\t  " + bonuses[9] + ",\n\t  "
            + bonuses[10] + ",\n\t  " + bonuses[13] + "\n\t],\n\t\"slot\": " +slotnumber
            + ",\n\t\"fullmask\": " + fullmask + ",\n\t\"stackable\": " + stackable
            + ",\n\t\"noteable\": " + noteable + ",\n\t\"tradable\": " + isittradeable
            + ",\n\t\"wearable\": " + wearable + ",\n\t\"showBeard\": " + showBeard
            + ",\n\t\"members\": " + members + ",\n\t\"twoHanded\": " + twoHanded
            + ",\n\t\"requirements\": [\n\t  0,\n\t  0,\n\t  0,\n\t  0,\n\t  0,\n\t  0,\n\t  0,\n\t  0,\n\t  0,\n\t  0,\n\t  0,\n\t  0,\n\t  0,\n\t  0,\n\t  0,\n\t  0,\n\t  0,\n\t  0,\n\t  0,\n\t  0,\n\t  0\n\t]\n  },\n");

        writer.close();


    }
//
//private void decode(Buffer buffer) {
//    while (true) {
//        int opcode = buffer.readUnsignedByte();
//        if (opcode == 0)
//            return;
//        int category;
//        int placeholder_id;
//        if (opcode == 1)
//            inventory_model = buffer.readUShort();
//        else if (opcode == 2)
//            name = buffer.readString();
//        else if (opcode == 3)
//            description = buffer.readString();
//        else if (opcode == 4)
//            model_zoom  = buffer.readUShort();
//        else if (opcode == 5)
//            rotation_y  = buffer.readUShort();
//        else if (opcode == 6)
//            rotation_x  = buffer.readUShort();
//        else if (opcode == 7) {
//            translate_x  = buffer.readUShort();
//            if (translate_x  > 32767)
//                translate_x  -= 0x10000;
//        } else if (opcode == 8) {
//            translate_y  = buffer.readUShort();
//            if (translate_y  > 32767)
//                translate_y  -= 0x10000;
//        } else if (opcode == 11)
//            stackable = true;
//        else if (opcode == 12)
//            cost  = buffer.readInt();
//        else if (opcode == 16)
//            membership_required = true;
//        else if (opcode == 23) {
//            male_equip_main = buffer.readUShort();
//            male_equip_translate_y = buffer.readSignedByte();
//        } else if (opcode == 24)
//            male_equip_attachment = buffer.readUShort();
//        else if (opcode == 25) {
//            female_equip_main = buffer.readUShort();
//            female_equip_attachment = buffer.readSignedByte();
//        } else if (opcode == 26)
//            female_equip_attachment  = buffer.readUShort();
//        else if (opcode >= 30 && opcode < 35) {
//            if (scene_actions == null)
//                scene_actions = new String[5];
//            scene_actions[opcode - 30] = buffer.readString();
//            if (scene_actions[opcode - 30].equalsIgnoreCase("hidden"))
//                scene_actions[opcode - 30] = null;
//        } else if (opcode >= 35 && opcode < 40) {
//            if (widget_actions == null)
//                widget_actions = new String[5];
//            widget_actions[opcode - 35] = buffer.readString();
//        } else if (opcode == 40) {
//            int length = buffer.readUnsignedByte();
//            color_to_replace  = new int[length];
//            color_to_replace_with  = new int[length];
//            for (int index = 0; index < length; index++) {
//                color_to_replace_with [index] = buffer.readUShort();
//                color_to_replace [index] = buffer.readUShort();
//            }
//        } else if (opcode == 41) {
//            int length = buffer.readUnsignedByte();
//            src_texture = new short[length];
//            dst_texture = new short[length];
//            for (int index = 0; index < length; index++) {
//                src_texture[index] = (short) buffer.readUShort();
//                dst_texture[index] = (short) buffer.readUShort();
//            }
//        } else if (opcode == 42) {
//            int shiftClickIndex = buffer.readUnsignedByte();
//        } else if (opcode == 65) {
//            searchable  = true;
//        } else if (opcode == 78)
//            male_equip_emblem = buffer.readUShort();
//        else if (opcode == 79)
//            female_equip_emblem = buffer.readUShort();
//        else if (opcode == 90)
//            male_dialogue_head = buffer.readUShort();
//        else if (opcode == 91)
//            female_dialogue_head = buffer.readUShort();
//        else if (opcode == 92)
//            male_dialogue_headgear  = buffer.readUShort();
//        else if (opcode == 93)
//            female_dialogue_headgear  = buffer.readUShort();
//        else if (opcode == 94)
//            category = buffer.readUShort();
//
//        else if (opcode == 95)
//            rotation_z  = buffer.readUShort();
//        else if (opcode == 97)
//            unnoted_item_id = buffer.readUShort();
//        else if (opcode == 98)
//            noted_item_id = buffer.readUShort();
//        else if (opcode >= 100 && opcode < 110) {
//
//            if (stack_variant_id == null) {
//                stack_variant_id = new int[10];
//                stack_variant_size = new int[10];
//            }
//            stack_variant_id[opcode - 100] = buffer.readUShort();
//            stack_variant_size[opcode - 100] = buffer.readUShort();
//
//        } else if (opcode == 110)
//            model_scale_x  = buffer.readUShort();
//        else if (opcode == 111)
//            model_scale_y  = buffer.readUShort();
//        else if (opcode == 112)
//            model_scale_z  = buffer.readUShort();
//        else if (opcode == 113)
//            ambient  = buffer.readSignedByte();
//        else if (opcode == 114)
//            contrast  = buffer.readSignedByte() * 5;
//        else if (opcode >= 100 && opcode < 110) {
//            if (stack_variant_id == null) {
//                stack_variant_id = new int[10];
//                stack_variant_size = new int[10];
//            }
//            stack_variant_id[opcode - 100] = buffer.readUShort();
//            stack_variant_size[opcode - 100] = buffer.readUShort();
//        }
//        else if (opcode == 115)
//            team_id  = buffer.readUnsignedByte();
//        else if (opcode == 139)
//            unnotedId  = buffer.readUShort();
//        else if (opcode == 140)
//            notedId  = buffer.readUShort();
//        else if (opcode == 148)
//            placeholder_id = buffer.readUShort();
//        else if (opcode == 149) {
//            int placeholder_template_id = buffer.readUShort();
//        } else if (opcode == 249) {
//            int length = buffer.readUnsignedByte();
//
//            params = new HashMap<>(length);
//
//            for (int i = 0; i < length; i++) {
//                boolean isString = buffer.readUnsignedByte() == 1;
//                int key = buffer.read24Int();
//                Object value;
//
//                if (isString) {
//                    value = buffer.readString();
//                } else {
//                    value = buffer.readInt();
//                }
//
//                params.put(key, value);
//            }
//        } else {
//            System.err.printf("Error unrecognised {Items} opcode: %d%n%n", opcode);
//        }
//    }
//}
public void decode(Buffer buffer) {
    while (true) {
        int opcode = buffer.readUByte();
        if (opcode == 0)
            return;
        if (opcode == 1)
            inventory_model = buffer.readUShort();
        else if (opcode == 2)
            name = buffer.readString();
        else if (opcode == 3)
            description = buffer.readString();
        else if (opcode == 4)
            model_zoom = buffer.readUShort();
        else if (opcode == 5)
            rotation_y = buffer.readUShort();
        else if (opcode == 6)
            rotation_x = buffer.readUShort();
        else if (opcode == 7) {
            translate_x = buffer.readUShort();
            if (translate_x > 32767)
                translate_x -= 0x10000;
        } else if (opcode == 8) {
            translate_y = buffer.readUShort();
            if (translate_y > 32767)
                translate_y -= 0x10000;
        } else if (opcode == 11)
            stackable = true;
        else if (opcode == 12)
            cost = buffer.readInt();
        else if (opcode == 16)
            membership_required = true;
        else if (opcode == 23) {
            male_equip_main = buffer.readUShort();
            male_equip_translate_y = buffer.readSignedByte();
        } else if (opcode == 24)
            male_equip_attachment = buffer.readUShort();
        else if (opcode == 25) {
            female_equip_main = buffer.readUShort();
            female_equip_translate_y = buffer.readSignedByte();
        } else if (opcode == 26)
            female_equip_attachment = buffer.readUShort();
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
            int length = buffer.readUByte();
            //if models aren't recoloring properly, typically switch the position of src with dst
            color_to_replace = new int[length];
            color_to_replace_with = new int[length];
            for (int index = 0; index < length; index++) {
                color_to_replace_with[index] = buffer.readUShort();
                color_to_replace[index] = buffer.readUShort();
            }
        } else if (opcode == 41) {
            int length = buffer.readUByte();
            src_texture = new short[length];
            dst_texture = new short[length];
            for (int index = 0; index < length; index++) {
                src_texture[index] = (short) buffer.readUShort();
                dst_texture[index] = (short) buffer.readUShort();
            }
        } else if (opcode == 42) {
            buffer.readUByte();//shift_menu_index
        } else if (opcode == 65) {
            searchable = true;
        } else if (opcode == 78)
            male_equip_emblem = buffer.readUShort();
        else if (opcode == 79)
            female_equip_emblem = buffer.readUShort();
        else if (opcode == 90)
            male_dialogue_head = buffer.readUShort();
        else if (opcode == 91)
            female_dialogue_head = buffer.readUShort();
        else if (opcode == 92)
            male_dialogue_headgear = buffer.readUShort();
        else if (opcode == 93)
            female_dialogue_headgear = buffer.readUShort();
        else if(opcode == 94)
            buffer.readUShort();
        else if (opcode == 95)
            rotation_z = buffer.readUShort();
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
            model_scale_x = buffer.readUShort();
        else if (opcode == 111)
            model_scale_y = buffer.readUShort();
        else if (opcode == 112)
            model_scale_z = buffer.readUShort();
        else if (opcode == 113)
            ambient = buffer.readSignedByte();
        else if (opcode == 114)
            contrast = buffer.readSignedByte(); //We had this as * 5 but runelite has it without * 5.
        else if (opcode == 115)
            team_id = buffer.readUByte();
        else if (opcode == 139)
            unnotedId = buffer.readUShort();
        else if (opcode == 140)
            notedId = buffer.readUShort();
        else if (opcode == 148)
            buffer.readUShort(); // placeholder id
        else if (opcode == 149) {
            buffer.readUShort(); // placeholder template
        } else if (opcode == 150) {
            String opcode150 = buffer.readString();
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

