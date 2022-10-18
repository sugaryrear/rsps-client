package com.ferox.cache.def.impl.items;

import com.ferox.ClientConstants;
import com.ferox.cache.def.ItemDefinition;
import com.ferox.cache.def.NpcDefinition;
import com.ferox.util.ColourUtils;
import com.ferox.util.ItemIdentifiers;
import com.ferox.util.NpcIdentifiers;

import java.awt.*;

import static com.ferox.util.CustomItemIdentifiers.*;
import static com.ferox.util.ItemIdentifiers.*;

public class CustomItems {

    public static void unpack(int id) {
        ItemDefinition def = ItemDefinition.get(id);

        if (id == 30224) {
            def.name = "@gre@Grim h'ween mask";
            def.inventory_model = 2438;
            def.male_equip_main = 3188;
            def.female_equip_main = 3192;
            def.model_zoom = 730;
            def.rotation_y = 516;
            def.rotation_x = 0;
            def.translate_y = -10;
            def.widget_actions = new String[]{null, "Wear", null, null, "Drop"};
            def.color_to_replace = new int[2];
            def.color_to_replace[0] = 926;
            def.color_to_replace[1] = 0;
            def.color_to_replace_with = new int[2];
            def.color_to_replace_with[0] = 28; // Background colour
            def.color_to_replace_with[1] = 9152; // Eyes colour
        }

        if (id == 30225) {
            def.name = "@gre@Grim partyhat";
            def.inventory_model = 2635;
            def.male_equip_main = 187;
            def.female_equip_main = 363;
            def.model_zoom = 440;
            def.rotation_x = 1852;
            def.rotation_y = 76;
            def.translate_x = 1;
            def.translate_y = 1;
            def.widget_actions = new String[]{null, "Wear", null, null, "Drop"};
            def.color_to_replace = new int[1];
            def.color_to_replace[0] = 926;
            def.color_to_replace_with = new int[1];
            def.color_to_replace_with[0] = 28;
        }

        if (id == 30226) {
            def.name = "@gre@Grim scythe";
            def.widget_actions = new String[]{null, "Wield", null, null, "Drop"};
            def.inventory_model = 2511;
            def.male_equip_main = 507;
            def.female_equip_main = 507;
            def.translate_x = 1;
            def.translate_y = 17;
            def.rotation_y = 336;
            def.rotation_x = 20;
            def.model_zoom = 1930;
            def.color_to_replace = new int[]{7073, 61};
            def.color_to_replace_with = new int[]{28, 61};
        }

        if (id == 30227) {
            def.animateInventory = true;
            def.name = "@gre@H'ween mystery chest";
            def.widget_actions = new String[]{"Open", null, null, null, "Drop"};
            def.inventory_model = 55606;
            def.translate_x = 0;
            def.translate_y = 0;
            def.rotation_y = 114;
            def.rotation_x = 1883;
            def.model_zoom = 2640;
            def.color_to_replace = new int[]{24, 49, 4510, 4502, 8128, 7093};
            def.color_to_replace_with = new int[]{24, 49, 374770, 374770, 87770, 87770};
        }

        if (id == 30228) {
            def.name = "@gre@Haunted hellhound pet";
            def.widget_actions = new String[]{null, null, null, null, "Drop"};
            def.inventory_model = 26262;
            def.model_zoom = 3100;
            def.rotation_y = 250;
            def.rotation_x = 280;
            def.translate_x = -7;
            def.translate_y = -329;
        }

        if (id == 30229) {
            def.name = "@gre@H'ween armadyl godsword";
            def.female_equip_main = 27649;
            def.widget_actions = new String[]{null, "Wield", null, null, "Drop"};
            def.inventory_model = 28075;
            def.male_equip_main = 27649;
            def.translate_x = -1;
            def.translate_y = -1;
            def.rotation_y = 498;
            def.rotation_x = 484;
            def.model_zoom = 1957;
            def.modelCustomColor3 = 24;
        }

        if (id == 30230) {
            def.name = "@gre@H'ween blowpipe";
            def.female_equip_main = 14403;
            def.widget_actions = new String[]{null, "Wield", null, null, "Drop"};
            def.inventory_model = 19219;
            def.male_equip_main = 14403;
            def.translate_x = -7;
            def.translate_y = 4;
            def.rotation_y = 768;
            def.rotation_x = 189;
            def.model_zoom = 1158;
            def.modelCustomColor3 = 24;
        }

        if (id == 30231) {
            def.name = "@gre@H'ween dragon claws";
            def.female_equip_main = 29191;
            def.widget_actions = new String[]{null, "Wield", null, null, "Drop"};
            def.inventory_model = 32784;
            def.male_equip_main = 29191;
            def.translate_x = -1;
            def.translate_y = 8;
            def.rotation_y = 349;
            def.rotation_x = 15;
            def.model_zoom = 886;
            def.modelCustomColor3 = 24;
        }

        if (id == 30232) {
            def.name = "@gre@H'ween craw's bow";
            def.female_equip_main = 35769;
            def.widget_actions = new String[]{null, "Wield", null, null, "Drop"};
            def.inventory_model = 35775;
            def.male_equip_main = 35769;
            def.translate_x = -1;
            def.translate_y = -3;
            def.rotation_y = 1463;
            def.rotation_x = 510;
            def.rotation_z = 835;
            def.model_zoom = 1979;
            def.modelCustomColor3 = 24;
        }

        if (id == 30233) {
            def.name = "@gre@H'ween chainmace";
            def.female_equip_main = 35771;
            def.widget_actions = new String[]{null, "Wield", null, null, "Drop"};
            def.inventory_model = 35780;
            def.male_equip_main = 35771;
            def.translate_x = 11;
            def.translate_y = -8;
            def.rotation_y = 1495;
            def.rotation_x = 256;
            def.model_zoom = 1488;
            def.modelCustomColor3 = 24;
        }

        if (id == 30234) {
            ItemDefinition.copyInventory(def, GRANITE_MAUL_24225);
            ItemDefinition.copyEquipment(def, GRANITE_MAUL_24225);
            def.name = "@gre@H'ween granite maul";
            def.modelCustomColor3 = 24;
        }

        if (id == 30239) {
            def.name = "@gre@Haunted sled";
            def.widget_actions = new String[]{null, "Ride", null, null, "Drop"};
            def.female_equip_main = 4946;
            def.inventory_model = 4937;
            def.male_equip_main = 4946;
            def.rotation_y = 136;
            def.rotation_x = 160;
            def.model_zoom = 1960;
            def.modelCustomColor3 = 24;
        }

        if (id == 30240) {
            def.name = "@gre@Haunted crossbow";
            def.female_equip_main = 15472;
            def.widget_actions = new String[]{null, "Wield", null, null, "Drop"};
            def.inventory_model = 15493;
            def.male_equip_main = 15472;
            def.translate_x = 2;
            def.translate_y = 8;
            def.rotation_y = 444;
            def.rotation_x = 1658;
            def.model_zoom = 1104;
            def.modelCustomColor3 = 24;
        }

        if (id == 30241) {
            def.name = "@gre@Haunted dragonfire shield";
            def.ambient = 15;
            def.contrast = 15;
            def.female_equip_main = 26423;
            def.widget_actions = new String[]{null, "Wield", null, null, "Drop"};
            def.inventory_model = 26457;
            def.male_equip_main = 26423;
            def.rotation_y = 540;
            def.rotation_x = 123;
            def.model_zoom = 2022;
            def.modelCustomColor3 = 24;
        }

        if (id == 30242) {
            def.name = "@gre@H'ween item casket";
            def.widget_actions = new String[]{"Open", null, null, null, "Drop"};
            def.inventory_model = 2450;
            def.translate_x = 3;
            def.translate_y = -6;
            def.rotation_y = 164;
            def.rotation_x = 1592;
            def.model_zoom = 1410;
            def.modelCustomColor3 = 24;
        }

        if (id == 30235) {
            ItemDefinition.copyInventory(def, 13204);
            def.name = "@gre@H'ween token";
            def.color_to_replace = new int[]{5813, 9139, 26006};
            def.color_to_replace_with = new int[]{25, 26, 27};
            def.stack_variant_size = new int[]{2, 3, 4, 5, 0, 0, 0, 0, 0, 0};
            def.stack_variant_id = new int[]{30236, 30237, 30238, 30238, 0, 0, 0, 0, 0, 0};
        }

        if (id == 30236) {
            ItemDefinition.copyInventory(def, 3985);
            def.name = "@gre@H'ween Token";
            def.color_to_replace = new int[]{5813, 9139, 26006};
            def.color_to_replace_with = new int[]{25, 26, 27};
        }

        if (id == 30237) {
            ItemDefinition.copyInventory(def, 3987);
            def.name = "@gre@H'ween Token";
            def.color_to_replace = new int[]{5813, 9139, 26006};
            def.color_to_replace_with = new int[]{25, 26, 27};
        }

        if (id == 30238) {
            ItemDefinition.copyInventory(def, 3989);
            def.name = "@gre@H'ween Token";
            def.color_to_replace = new int[]{5813, 9139, 26006};
            def.color_to_replace_with = new int[]{25, 26, 27};
        }

        if (id == 30222) {
            def.animateInventory = true;
            def.name = "@gre@Mystery ticket";
            def.widget_actions = new String[]{"Tear", null, null, null, null};
            def.color_to_replace = new int[]{7364, 11078, -327, -329, 7496, 7500};
            def.color_to_replace_with = new int[]{374770, 374770, -327, -329, 374770, 374770};
            def.inventory_model = 55601;
            def.stackable = true;
            def.rotation_y = 308;
            def.rotation_x = 1888;
            def.model_zoom = 1160;
        }

        if (id == 30223) {
            def.name = "@gre@Blood money casket (100-250k)";
            def.inventory_model = 55568;
            def.model_zoom = 2640;
            def.rotation_y = 114;
            def.rotation_x = 1883;
            def.animateInventory = true;
            def.widget_actions = new String[]{"Open", null, null, null, "Drop"};
            def.color_to_replace = new int[]{24, 49, 4510, 4502, 8128, 7093};
            def.color_to_replace_with = new int[]{933, 933, 933, 933, 933, 933};
        }

        if(id == 19780){
            ItemDefinition.copyInventory(def, 23995);
            ItemDefinition.copyEquipment(def, 23995);
            def.name = "Korasi's Sword";
            def.inventory_model = 61000;
            def.male_equip_main = 61001;
            def.female_equip_main = 61001;
            def.widget_actions = new String[]{null, "Wield", null, null, null};
        }

        if(id == 17350){
            ItemDefinition.copyInventory(def, 4720);
            ItemDefinition.copyEquipment(def, 4720);

            def.color_to_replace = new int[]{10394, 10291, 8656};
            def.color_to_replace_with = new int[]{ColourUtils.RGB_to_RS2HSB(new Color(61,2,0).getRGB()), ColourUtils.RGB_to_RS2HSB(new Color(61,2,0).getRGB()),
                ColourUtils.RGB_to_RS2HSB(new Color(0,0,0).getRGB())};
        }

        if(id == 17350){
            ItemDefinition.copyInventory(def, 4720);
            ItemDefinition.copyEquipment(def, 4720);

            def.color_to_replace = new int[]{10394, 10291, 8656};
            def.color_to_replace_with = new int[]{ColourUtils.RGB_to_RS2HSB(new Color(61,2,0).getRGB()), ColourUtils.RGB_to_RS2HSB(new Color(61,2,0).getRGB()),
                ColourUtils.RGB_to_RS2HSB(new Color(0,0,0).getRGB())};
        }

//        def.color_to_replace = new int[]{40023,40036,1822,36,25,9230,0, 10348};
//        def.color_to_replace_with = new int[]{ColourUtils.RGB_to_RS2HSB(new Color(179,37,178).getRGB()), ColourUtils.RGB_to_RS2HSB(new Color(179,37,178).getRGB()),
//            ColourUtils.RGB_to_RS2HSB(new Color(179,37,178).getRGB()), ColourUtils.RGB_to_RS2HSB(new Color(179,37,178).getRGB()),            ColourUtils.RGB_to_RS2HSB(new Color(179,37,178).getRGB()),
//                ColourUtils.RGB_to_RS2HSB(new Color(179,37,178).getRGB()),             ColourUtils.RGB_to_RS2HSB(new Color(179,37,178).getRGB()),            ColourUtils.RGB_to_RS2HSB(new Color(179,37,178).getRGB())};

        if (id == 30329) {
            def.name = "Casket";
            def.inventory_model = 11204;
            def.model_zoom = 2640;
            def.rotation_y = 114;
            def.rotation_x = 1883;
            def.animateInventory = true;
            def.widget_actions = new String[]{"Open", null, null, null, "Drop"};
            def.color_to_replace = new int[]{4510, 8128, 4502};
            def.color_to_replace_with = new int[]{ColourUtils.RGB_to_RS2HSB(new Color(85,24,152).getRGB()), ColourUtils.RGB_to_RS2HSB(new Color(45,22,94).getRGB()),
                ColourUtils.RGB_to_RS2HSB(new Color(82,20,162).getRGB())};
        }
        if (id == 30122) {
            def.widget_actions = new String[]{null, null, null, null, null};
            def.name = "@gre@Corrupt totem base";
            def.inventory_model = 31621;
            def.translate_y = -4;
            def.rotation_y = 188;
            def.rotation_x = 108;
            def.model_zoom = 530;
            def.color_to_replace = new int[]{22290, -26664};
            def.color_to_replace_with = new int[]{945, 582};
        }

        if (id == 30123) {
            def.widget_actions = new String[]{null, null, null, null, null};
            def.name = "@gre@Corrupt totem middle";
            def.inventory_model = 31622;
            def.translate_y = -1;
            def.rotation_y = 188;
            def.rotation_x = 664;
            def.model_zoom = 480;
            def.color_to_replace = new int[]{-26664, -27417};
            def.color_to_replace_with = new int[]{945, 582};
        }

        if (id == 30124) {
            def.widget_actions = new String[]{null, null, null, null, null};
            def.name = "@gre@Corrupt totem top";
            def.inventory_model = 31623;
            def.translate_x = -3;
            def.translate_y = -1;
            def.rotation_y = 111;
            def.rotation_x = 194;
            def.model_zoom = 724;
            def.color_to_replace = new int[]{-26808, -26664, -26713, -26825};
            def.color_to_replace_with = new int[]{945, 582, 712, 728};
        }

        if (id == 30125) {
            def.widget_actions = new String[]{null, null, null, null, null};
            def.name = "@gre@Corrupt totem";
            def.inventory_model = 31620;
            def.translate_x = 2;
            def.translate_y = -3;
            def.rotation_y = 152;
            def.rotation_x = 111;
            def.model_zoom = 1150;
            def.color_to_replace = new int[]{22290, -26808, -26664, -26713, -26825, -27417};
            def.color_to_replace_with = new int[]{945, 582, 152, 712, 728, -77};
        }

        if (id == 23759 || id == 22319 || id == 24491) {
            def.name = "@gre@" + def.name + " pet";
        }

        if (id == 25731) {
            def.female_equip_main = 42276;
            def.widget_actions = new String[]{null, "Wield", null, null, null};
            def.inventory_model = 42292;
            def.male_equip_main = 42271;
            def.name = "@gre@Holy sanguinesti staff";
            def.translate_x = -5;
            def.translate_y = 3;
            def.cost = 5000000;
            def.rotation_y = 552;
            def.rotation_x = 1558;
            def.model_zoom = 2258;
        }

        if (id == 25734) {
            def.color_to_replace = new int[]{90};
            def.color_to_replace_with = new int[]{-9762};
            def.female_equip_main = 42278;
            def.widget_actions = new String[]{null, "Wield", null, null, null};
            def.inventory_model = 42294;
            def.male_equip_main = 42273;
            def.name = "@gre@Holy ghrazi rapier";
            def.translate_x = 5;
            def.translate_y = -18;
            def.cost = 5000000;
            def.rotation_y = 1603;
            def.rotation_x = 552;
            def.model_zoom = 2064;
        }

        if (id == 25736) {
            def.female_equip_main = 42270;
            def.widget_actions = new String[]{null, "Wield", null, null, null};
            def.inventory_model = 42293;
            def.male_equip_main = 42277;
            def.name = "@gre@Holy scythe of vitur";
            def.translate_x = 1;
            def.translate_y = 17;
            def.cost = 4000000;
            def.rotation_y = 327;
            def.rotation_x = 23;
            def.model_zoom = 2105;
        }

        if (id == 25739) {
            def.female_equip_main = 42272;
            def.widget_actions = new String[]{null, "Wield", null, null, null};
            def.inventory_model = 42295;
            def.male_equip_main = 42279;
            def.name = "@gre@Sanguine scythe of vitur";
            def.translate_x = 1;
            def.translate_y = 17;
            def.cost = 4000000;
            def.rotation_y = 327;
            def.rotation_x = 23;
            def.model_zoom = 2105;
        }

        if (id == 25753) {
            def.color_to_replace = new int[]{11191, 11183};
            def.color_to_replace_with = new int[]{580, 557};
            def.widget_actions = new String[]{"Rub", null, null, null, "Drop"};
            def.inventory_model = 3348;
            def.name = "@gre@99 lamp";
            def.translate_x = 2;
            def.translate_y = -2;
            def.rotation_y = 28;
            def.rotation_x = 228;
            def.model_zoom = 840;
        }

        if (id == 30315) {
            def.name = "@gre@Darklord bow";
            def.female_equip_main = 59109;
            def.widget_actions = new String[]{null, "Wield", null, null, "Drop"};
            def.inventory_model = 59108;
            def.male_equip_main = 59109;
            def.translate_x = 9;
            def.translate_y = 9;
            def.rotation_x = 183;
            def.rotation_y = 320;
            def.model_zoom = 2061;
        }

        if (id == 30309) {
            def.name = "@gre@Darklord sword";
            def.female_equip_main = 59113;
            def.widget_actions = new String[]{null, "Wield", null, null, "Drop"};
            def.inventory_model = 59112;
            def.male_equip_main = 59113;
            def.translate_x = 1;
            def.translate_y = 1;
            def.rotation_x = 270;
            def.rotation_y = 548;
            def.model_zoom = 2061;
        }

        if (id == 30312) {
            def.name = "@gre@Darklord staff";
            def.female_equip_main = 59111;
            def.widget_actions = new String[]{null, "Wield", null, null, "Drop"};
            def.inventory_model = 59110;
            def.male_equip_main = 59111;
            def.translate_x = 9;
            def.translate_y = 9;
            def.rotation_x = 183;
            def.rotation_y = 1713;
            def.model_zoom = 2061;
        }

        if (id == 30175) {
            def.name = "@gre@Ancestral hat (i)";
            def.color_to_replace = new int[]{6323, 6331, 6340, 6348, 6356, 6364, -21992, -22235};
            def.color_to_replace_with = new int[]{15, 17, 19, 23, 25, 27, 29, 524};
            def.female_dialogue_head = 34263;
            def.female_equip_main = 32663;
            def.widget_actions = new String[]{null, "Wear", null, null, "Drop"};
            def.inventory_model = 32794;
            def.male_dialogue_head = 32774;
            def.male_equip_main = 32655;
            def.translate_y = -12;
            def.rotation_y = 118;
            def.rotation_x = 10;
            def.model_zoom = 1236;
        }

        if (id == 30177) {
            def.name = "@gre@Ancestral robe top (i)";
            def.color_to_replace = new int[]{6348, -16318, 6331, -22225, 7108, -22235, -16327, -22231, -16339, 6323};
            def.color_to_replace_with = new int[]{12, 19, 14, 534, 27, 536, 15, 538, 17, 8};
            def.female_equip_main = 32664;
            def.female_equip_attachment = 32665;
            def.widget_actions = new String[]{null, "Wear", null, null, "Drop"};
            def.inventory_model = 32790;
            def.male_equip_main = 32657;
            def.male_equip_attachment = 32658;
            def.translate_y = -3;
            def.rotation_y = 514;
            def.rotation_x = 2041;
            def.model_zoom = 1358;
        }

        if (id == 30179) {
            def.name = "@gre@Ancestral robe bottom (i)";
            def.ambient = 30;
            def.color_to_replace = new int[]{-16339, 6348, -16327, 6331, -16318, -22225, -22235, 6323, -22231};
            def.color_to_replace_with = new int[]{17, 12, 15, 14, 25, 534, 537, 13, 532};
            def.contrast = 20;
            def.female_equip_main = 32662;
            def.widget_actions = new String[]{null, "Wear", null, null, "Drop"};
            def.inventory_model = 32787;
            def.male_equip_main = 32653;
            def.translate_x = -1;
            def.translate_y = 7;
            def.rotation_y = 435;
            def.rotation_x = 9;
            def.model_zoom = 1690;
        }

        if (id == 30183) {
            def.name = "@gre@Twisted bow (i)";
            def.color_to_replace = new int[]{10318, 10334, 14236, 13223};
            def.color_to_replace_with = new int[]{524, 527, 524, 527};
            def.female_equip_main = 32674;
            def.widget_actions = new String[]{null, "Wield", null, null, "Drop"};
            def.inventory_model = 32799;
            def.male_equip_main = 32674;
            def.translate_x = -3;
            def.translate_y = 1;
            def.rotation_y = 720;
            def.rotation_x = 1500;
        }

        if (id == 30049) {
            def.name = "@gre@Magma blowpipe";
            def.female_equip_main = 58976;
            def.widget_actions = new String[]{null, "Wield", null, null, "Drop"};
            def.inventory_model = 58975;
            def.male_equip_main = 58976;
            def.translate_x = -7;
            def.translate_y = 4;
            def.rotation_y = 768;
            def.rotation_x = 189;
            def.model_zoom = 1158;
        }

        if (id == 15331) {
            def.name = "@gre@Ring of manhunting";
            ItemDefinition.copyInventory(def, TYRANNICAL_RING);
            ItemDefinition.copyInventory(def, TYRANNICAL_RING);
            def.modelCustomColor4 = 233333;
        }

        if (id == 15300) {
            def.name = "@gre@Recover special (4)";
            ItemDefinition.copyInventory(def, 2436);
            def.color_to_replace_with = new int[]{38222};
            def.color_to_replace = new int[]{61};
        }

        if (id == 15301) {
            def.name = "@gre@Recover special (3)";
            ItemDefinition.copyInventory(def, 145);
            def.color_to_replace_with = new int[]{38222};
            def.color_to_replace = new int[]{61};
        }

        if (id == 15302) {
            def.name = "@gre@Recover special (2)";
            ItemDefinition.copyInventory(def, 147);
            def.color_to_replace_with = new int[]{38222};
            def.color_to_replace = new int[]{61};
        }

        if (id == 15303) {
            def.name = "@gre@Recover special (1)";
            ItemDefinition.copyInventory(def, 149);
            def.color_to_replace_with = new int[]{38222};
            def.color_to_replace = new int[]{61};
        }

        if (id == 27000) {
            ItemDefinition.copyInventory(def, HELLPUPPY);
            def.name = "@gre@Kerberos pet";
            def.modelCustomColor4 = 125;
        }

        if (id == 27001) {
            ItemDefinition.copyInventory(def, SCORPIAS_OFFSPRING);
            def.name = "@gre@Skorpios pet";
            def.modelCustomColor4 = 125;
        }

        if (id == 27002) {
            ItemDefinition.copyInventory(def, VENENATIS_SPIDERLING);
            def.name = "@gre@Arachne pet";
            def.modelCustomColor4 = 125;
        }

        if (id == 27003) {
            ItemDefinition.copyInventory(def, CALLISTO_CUB);
            def.name = "@gre@Artio pet";
            def.modelCustomColor4 = 125;
        }

        if (id == 27004) {
            def.name = "@gre@Blood money pet";
            ItemDefinition.copyInventory(def, 13316);
            def.stackable = false;
            def.color_to_replace = new int[]{8128};
            def.color_to_replace_with = new int[]{947};
        }

        if (id == 27005) {
            def.name = "@gre@Ring of elysian";
            ItemDefinition.copyInventory(def, 23185);
            def.widget_actions = new String[]{null, "Wear", null, null, "Drop"};
            def.color_to_replace = new int[]{7378, 8289, 8282, 7244};
            def.color_to_replace_with = new int[]{-29116, -29019, -29125, -29110};
        }

        if (id == 27006) {
            def.name = "@gre@Toxic staff of the dead (c)";
            ItemDefinition.copyInventory(def, TOXIC_STAFF_OF_THE_DEAD);
            ItemDefinition.copyEquipment(def, TOXIC_STAFF_OF_THE_DEAD);
            def.modelCustomColor4 = 222200;
        }

        if (id == 15304) {
            def.name = "@gre@Ring of vigour";
            ItemDefinition.copyInventory(def, LUNAR_RING);
            ItemDefinition.copyEquipment(def, LUNAR_RING);
            def.modelCustomColor4 = 222200;
        }

        if (id == 26500) {
            def.name = "@gre@Dark bandos chestplate";
            def.inventory_model = 28042;
            def.model_zoom = 984;
            def.rotation_y = 501;
            def.rotation_x = 6;
            def.translate_x = 1;
            def.translate_y = 4;
            def.stackable = false;
            def.widget_actions = new String[]{null, "Wear", null, null, "Drop"};
            def.male_equip_main = 27636;
            def.male_equip_attachment = 28826;
            def.female_equip_main = 27644;
            def.female_equip_attachment = 28827;
            def.color_to_replace = new int[]{8384, 163, 10275, 10502, 4550, 9515, 8076, 142, 8367, 9523, 22, 8375, 10266, 9403, 8379};
            def.color_to_replace_with = new int[]{16, 272, 0, 274, 20, 4, 8, 142, 968, 272, 0, 274, 20, 968, 968};
        }

        if (id == 26501) {
            def.name = "@gre@Dark bandos tassets";
            def.inventory_model = 28047;
            def.model_zoom = 854;
            def.rotation_y = 540;
            def.rotation_x = 2039;
            def.translate_x = 3;
            def.translate_y = 3;
            def.stackable = false;
            def.widget_actions = new String[]{null, "Wear", null, null, "Drop"};
            def.male_equip_main = 27625;
            def.female_equip_main = 27640;
            def.color_to_replace = new int[]{163, 9523, 4550, 22, 8390, 39, 154};
            def.color_to_replace_with = new int[]{16, 968, 0, 274, 20, 4, 8};
        }

        if (id == 26502) {
            def.name = "@gre@Dark armadyl helmet";
            def.inventory_model = 28043;
            def.model_zoom = 789;
            def.rotation_y = 66;
            def.rotation_x = 372;
            def.translate_x = 9;
            def.translate_y = 0;
            def.stackable = false;
            def.widget_actions = new String[]{null, "Wear", null, null, "Drop"};
            def.male_equip_main = 27623;
            def.female_equip_main = 27639;
            def.male_dialogue_head = 27699;
            def.female_dialogue_head = 27700;
            def.color_to_replace = new int[]{0, -22452, 4550, -22456, -22506, 8650, -22460, -22448};
            def.color_to_replace_with = new int[]{16, 968, 0, 274, 20, 4, 8, 20};
        }

        if (id == 26503) {
            def.name = "@gre@Dark armadyl chestplate";
            def.inventory_model = 28039;
            def.model_zoom = 854;
            def.rotation_y = 453;
            def.rotation_x = 0;
            def.translate_x = 1;
            def.translate_y = -5;
            def.stackable = false;
            def.widget_actions = new String[]{null, "Wear", null, null, "Drop"};
            def.male_equip_main = 27633;
            def.male_equip_attachment = 27629;
            def.female_equip_main = 27645;
            def.female_equip_attachment = 28828;
            def.color_to_replace = new int[]{8658, 4550, -22440, -22489, 8650, -22448};
            def.color_to_replace_with = new int[]{16, 968, 0, 274, 968, 968};
        }

        if (id == 26504) {
            def.name = "@gre@Dark armadyl chainskirt";
            def.inventory_model = 28046;
            def.model_zoom = 1957;
            def.rotation_y = 555;
            def.rotation_x = 2036;
            def.translate_y = -3;
            def.stackable = false;
            def.widget_actions = new String[]{null, "Wear", null, null, "Drop"};
            def.male_equip_main = 27627;
            def.female_equip_main = 27641;
            def.color_to_replace = new int[]{-22485, -22440, -22456, -22489, -22473, 8650, -22448, -22464};
            def.color_to_replace_with = new int[]{16, 968, 0, 274, 20, 4, 8, 20};
        }

        if (id == 24937) {
            ItemDefinition.copyInventory(def, FAWKES);
            def.name = "@gre@Fawkes pet";
            def.modelCustomColor4 = 222200;
        }

        if (id == 24938) {
            ItemDefinition.copyInventory(def, VOID_KNIGHT_GLOVES);
            ItemDefinition.copyEquipment(def, VOID_KNIGHT_GLOVES);
            def.name = "@gre@Void knight gloves";
            def.modelCustomColor4 = 222200;
        }

        if (id == 24939) {
            ItemDefinition.copyInventory(def, VOID_RANGER_HELM);
            ItemDefinition.copyEquipment(def, VOID_RANGER_HELM);
            def.name = "@gre@Void ranger helm";
            def.modelCustomColor4 = 222200;
        }

        if (id == 24940) {
            ItemDefinition.copyInventory(def, VOID_MAGE_HELM);
            ItemDefinition.copyEquipment(def, VOID_MAGE_HELM);
            def.name = "@gre@Void mage helm";
            def.modelCustomColor4 = 222200;
        }

        if (id == 24941) {
            ItemDefinition.copyInventory(def, VOID_MELEE_HELM);
            ItemDefinition.copyEquipment(def, VOID_MELEE_HELM);
            def.name = "@gre@Void melee helm";
            def.modelCustomColor4 = 222200;
        }

        if (id == 24942) {
            ItemDefinition.copyInventory(def, ELITE_VOID_ROBE);
            ItemDefinition.copyEquipment(def, ELITE_VOID_ROBE);
            def.name = "@gre@Elite void robe";
            def.modelCustomColor4 = 222200;
        }

        if (id == 24943) {
            ItemDefinition.copyInventory(def, ELITE_VOID_TOP);
            ItemDefinition.copyEquipment(def, ELITE_VOID_TOP);
            def.name = "@gre@Elite void top";
            def.modelCustomColor4 = 222200;
        }

        if (id == 24944) {
            ItemDefinition.copyInventory(def, GRANITE_MAUL_24225);
            ItemDefinition.copyEquipment(def, GRANITE_MAUL_24225);
            def.name = "@gre@Granite maul";
            def.modelCustomColor4 = 23523;
        }

        if (id == 24945) {
            ItemDefinition.copyInventory(def, PARTYHAT__SPECS);
            ItemDefinition.copyEquipment(def, PARTYHAT__SPECS);
            def.name = "@gre@Partyhat & specs";
            def.modelCustomColor4 = 235;
        }

        if (id == 24946) {
            ItemDefinition.copyInventory(def, FREMENNIK_KILT);
            ItemDefinition.copyEquipment(def, FREMENNIK_KILT);
            def.name = "@gre@Fremennik kilt";
            def.modelCustomColor4 = 222200;
        }

        if (id == 24947) {
            ItemDefinition.copyInventory(def, SPIKED_MANACLES);
            ItemDefinition.copyEquipment(def, SPIKED_MANACLES);
            def.name = "@gre@Spiked manacles";
            def.modelCustomColor4 = 222222;
        }

        if (id == 24948) {
            ItemDefinition.copyInventory(def, ABYSSAL_TENTACLE);
            ItemDefinition.copyEquipment(def, ABYSSAL_TENTACLE);
            def.name = "@gre@Abyssal tentacle";
            def.modelCustomColor4 = 222200;
        }

        if (id == 24949) {
            def.name = "@gre@Dragon dagger(p++)";
            ItemDefinition.copyInventory(def, DRAGON_DAGGERP_5698);
            ItemDefinition.copyEquipment(def, DRAGON_DAGGERP_5698);
            def.modelCustomColor4 = 22459;
        }

        if (id == 18336) {
            def.name = "@gre@Dragon's partyhat";
            def.widget_actions = new String[5];
            def.widget_actions[1] = "Wear";
            def.model_zoom = 440;
            def.inventory_model = 55602;
            def.animateInventory = true;
            def.translate_x = 1;
            def.translate_y = 1;
            def.rotation_x = 1852;
            def.rotation_y = 76;
            def.male_equip_main = 55603;
            def.female_equip_main = 55604;
            def.color_to_replace = new int[]{926};
            def.color_to_replace_with = new int[]{926};
            def.src_texture = new short[]{926};
            def.dst_texture = new short[]{54};
        }

        if (id == 24950) {
            def.name = "@gre@Cyan partyhat";
            def.inventory_model = 2635;
            def.male_equip_main = 187;
            def.female_equip_main = 363;
            def.model_zoom = 440;
            def.rotation_x = 1852;
            def.rotation_y = 76;
            def.translate_x = 1;
            def.translate_y = 1;
            def.widget_actions = new String[]{null, "Wear", null, null, "Drop"};
            def.color_to_replace = new int[1];
            def.color_to_replace[0] = 926;
            def.color_to_replace_with = new int[1];
            def.color_to_replace_with[0] = 34770;
        }

        if (id == 24951) {
            def.name = "@gre@Lime partyhat";
            def.inventory_model = 2635;
            def.male_equip_main = 187;
            def.female_equip_main = 363;
            def.model_zoom = 440;
            def.rotation_x = 1852;
            def.rotation_y = 76;
            def.translate_x = 1;
            def.translate_y = 1;
            def.widget_actions = new String[]{null, "Wear", null, null, "Drop"};
            def.color_to_replace = new int[1];
            def.color_to_replace[0] = 926;
            def.color_to_replace_with = new int[1];
            def.color_to_replace_with[0] = 17350;
        }

        if (id == 24952) {
            def.name = "@gre@Orange partyhat";
            def.inventory_model = 2635;
            def.male_equip_main = 187;
            def.female_equip_main = 363;
            def.model_zoom = 440;
            def.rotation_x = 1852;
            def.rotation_y = 76;
            def.translate_x = 1;
            def.translate_y = 1;
            def.widget_actions = new String[]{null, "Wear", null, null, "Drop"};
            def.color_to_replace = new int[1];
            def.color_to_replace[0] = 926;
            def.color_to_replace_with = new int[1];
            def.color_to_replace_with[0] = 6073;
        }

        if (id == 24953) {
            def.name = "@gre@White h'ween mask";
            def.inventory_model = 2438;
            def.male_equip_main = 3188;
            def.female_equip_main = 3192;
            def.model_zoom = 730;
            def.rotation_y = 516;
            def.rotation_x = 0;
            def.translate_y = -10;
            def.widget_actions = new String[]{null, "Wear", null, null, "Drop"};
            def.color_to_replace = new int[2];
            def.color_to_replace[0] = 926;
            def.color_to_replace[1] = 0;
            def.color_to_replace_with = new int[2];
            def.color_to_replace_with[0] = 124; // Background colour
            def.color_to_replace_with[1] = 933; // Eyes colour
        }

        if (id == 24954) {
            def.name = "@gre@Purple h'ween mask";
            def.inventory_model = 2438;
            def.male_equip_main = 3188;
            def.female_equip_main = 3192;
            def.model_zoom = 730;
            def.rotation_y = 516;
            def.rotation_x = 0;
            def.translate_y = -10;
            def.widget_actions = new String[]{null, "Wear", null, null, "Drop"};
            def.color_to_replace = new int[2];
            def.color_to_replace[0] = 926;
            def.color_to_replace[1] = 0;
            def.color_to_replace_with = new int[2];
            def.color_to_replace_with[0] = 51136; // Background colour
            def.color_to_replace_with[1] = 0; // Eyes colour
        }

        if (id == 24955) {
            def.name = "@gre@Lime green h'ween mask";
            def.inventory_model = 2438;
            def.male_equip_main = 3188;
            def.female_equip_main = 3192;
            def.model_zoom = 730;
            def.rotation_y = 516;
            def.rotation_x = 0;
            def.translate_y = -10;
            def.widget_actions = new String[]{null, "Wear", null, null, "Drop"};
            def.color_to_replace = new int[2];
            def.color_to_replace[0] = 926;
            def.color_to_replace[1] = 0;
            def.color_to_replace_with = new int[2];
            def.color_to_replace_with[0] = 17350; // Background colour
            def.color_to_replace_with[1] = 0; // Eyes colour
        }

        if (id == 24956) {
            ItemDefinition.copyInventory(def, ELDER_MAUL);
            ItemDefinition.copyEquipment(def, ELDER_MAUL);
            def.name = "@gre@Dark elder maul";
            def.modelCustomColor4 = 222200;
        }

        if (id == 28957) {
            ItemDefinition.copyInventory(def, TWISTED_BOW);
            ItemDefinition.copyEquipment(def, TWISTED_BOW);
            def.name = "@gre@Sanguine twisted bow";
            def.modelCustomColor4 = 235;
            def.stackable = false;
        }

        if (id == 24958) {
            ItemDefinition.copyInventory(def, ELDER_MAUL);
            ItemDefinition.copyEquipment(def, ELDER_MAUL);
            def.name = "@gre@Dark elder maul";
            def.modelCustomColor4 = 222200;
        }

        if (id == 24981) {
            ItemDefinition.copyInventory(def, 7807);
            ItemDefinition.copyEquipment(def, 7807);
            def.widget_actions = new String[]{null, "Wield", null, "Dismantle", "Drop"};
            def.name = "@gre@Ancient warrior axe (c)";
            def.modelCustomColor3 = 23532;
        }

        if (id == 24982) {
            ItemDefinition.copyInventory(def, 7808);
            ItemDefinition.copyEquipment(def, 7808);
            def.widget_actions = new String[]{null, "Wield", null, "Dismantle", "Drop"};
            def.name = "@gre@Ancient warrior maul (c)";
            def.modelCustomColor3 = 23532;
        }

        if (id == 24983) {
            ItemDefinition.copyInventory(def, 7806);
            ItemDefinition.copyEquipment(def, 7806);
            def.widget_actions = new String[]{null, "Wield", null, "Dismantle", "Drop"};
            def.name = "@gre@Ancient warrior sword (c)";
            def.modelCustomColor3 = 23532;
        }

        if (id == 24984) {
            ItemDefinition.copyInventory(def, NEITIZNOT_FACEGUARD);
            ItemDefinition.copyEquipment(def, NEITIZNOT_FACEGUARD);
            def.name = "@gre@Ancient faceguard";
            def.modelCustomColor4 = 222200;
        }

        if (id == 24985) {
            def.widget_actions = new String[]{null, null, null, null, "Drop"};
            def.name = "@gre@Ancient warrior clamp";
            def.inventory_model = 55580;
            def.model_zoom = 1280;
            def.translate_x = -22;
            def.translate_y = -7;
            def.rotation_x = 0;
            def.rotation_y = 0;
            def.stackable = false;
            def.animateInventory = true;
        }

        if (id == 786) {
            def.name = "@gre@Gambler scroll";
            def.widget_actions = new String[]{"Redeem", null, null, null, "Drop"};
        }

        if (id == 24986) {
            ItemDefinition.copyInventory(def, PRINCE_BLACK_DRAGON);
            def.name = "@gre@Ancient king black dragon pet";
            def.modelCustomColor4 = 235;
        }

        if (id == 24987) {
            ItemDefinition.copyInventory(def, CHAOS_ELEMENTAL);
            def.name = "@gre@Ancient chaos elemental pet";
            def.modelCustomColor4 = 235;
        }

        if (id == 24988) {
            ItemDefinition.copyInventory(def, BARRELCHEST_PET);
            def.name = "@gre@Ancient barrelchest pet";
            def.modelCustomColor4 = 235;
        }

        if (id == 24989) {
            ItemDefinition.copyInventory(def, ANCIENT_EMBLEM);
            def.name = "@gre@Dark ancient emblem";
            def.modelCustomColor4 = 235;
        }
        if (id == 24990) {
            ItemDefinition.copyInventory(def, ANCIENT_TOTEM);
            def.name = "@gre@Dark ancient totem";
            def.modelCustomColor4 = 235;
        }
        if (id == 24991) {
            ItemDefinition.copyInventory(def, ANCIENT_STATUETTE);
            def.name = "@gre@Dark ancient statuette";
            def.modelCustomColor4 = 235;
        }
        if (id == 24992) {
            ItemDefinition.copyInventory(def, ANCIENT_MEDALLION);
            def.name = "@gre@Dark ancient medallion";
            def.modelCustomColor4 = 235;
        }
        if (id == 24993) {
            ItemDefinition.copyInventory(def, ANCIENT_EFFIGY);
            def.name = "@gre@Dark ancient effigy";
            def.modelCustomColor4 = 235;
        }
        if (id == 24994) {
            ItemDefinition.copyInventory(def, ANCIENT_RELIC);
            def.name = "@gre@Dark ancient relic";
            def.modelCustomColor4 = 235;
        }
        if (id == 24995) {
            ItemDefinition.copyInventory(def, VESTAS_LONGSWORD);
            ItemDefinition.copyEquipment(def, VESTAS_LONGSWORD);
            def.name = "@gre@Ancient vesta's longsword";
            def.modelCustomColor4 = 235;
        }
        if (id == 24996) {
            ItemDefinition.copyInventory(def, STATIUSS_WARHAMMER);
            ItemDefinition.copyEquipment(def, STATIUSS_WARHAMMER);
            def.name = "@gre@Ancient statius's warhammer";
            def.modelCustomColor4 = 235;
        }

//        if (id == 24999) {
//            def.name = "@gre@Blood money casket (5-50k)";
//            def.inventory_model = 55568;
//            def.model_zoom = 2640;
//            def.rotation_y = 114;
//            def.rotation_x = 1883;
//            def.animateInventory = true;
//            def.widget_actions = new String[]{"Open", null, null, null, "Drop"};
//            def.color_to_replace = new int[]{24, 49, 4510, 4502, 8128, 7093};
//            def.color_to_replace_with = new int[]{933, 933, 933, 933, 933, 933};
//        }

        if (id == 28000) {
            def.name = "@gre@Blood firebird pet";
            def.widget_actions = new String[]{null, null, null, null, "Drop"};
            def.inventory_model = 26853;
            def.model_zoom = 2340;
            def.rotation_x = 210;
            def.rotation_y = 10;
            def.translate_x = -2;
            def.translate_y = 9;
            def.color_to_replace = new int[]{10176, 2880, 6082, 6084, 23492, 2983, 4391, 8, 29867, 4011, 4013, 2733, 2735, 4399, 914, 20, 8150, 10167, 1946, 23483, 28, 5053};
            def.color_to_replace_with = new int[]{933, 933, 933, 933, 933, 933, 4391, 8, 29867, 4011, 4013, 2733, 2735, 4399, 914, 20, 8150, 10167, 1946, 23483, 28, 5053};
        }

        if (id == 28001) {
            def.ambient = 15;
            def.contrast = 35;
            def.female_equip_main = 55555;
            def.widget_actions = new String[]{null, "Wield", null, null, "Drop"};
            def.inventory_model = 55557;
            def.male_equip_main = 55555;
            def.translate_x = 1;
            def.translate_y = 16;
            def.cost = 5000000;
            def.rotation_y = 348;
            def.model_zoom = 1642;
            def.name = "@gre@Shadow mace";
            def.modelCustomColor4 = 235;
        }

        if (id == 28002) {
            ItemDefinition.copyInventory(def, INQUISITORS_GREAT_HELM);
            ItemDefinition.copyEquipment(def, INQUISITORS_GREAT_HELM);
            def.name = "@gre@Shadow great helm";
            def.modelCustomColor4 = 235;
        }

        if (id == 28003) {
            ItemDefinition.copyInventory(def, INQUISITORS_HAUBERK);
            ItemDefinition.copyEquipment(def, INQUISITORS_HAUBERK);
            def.name = "@gre@Shadow hauberk";
            def.modelCustomColor4 = 235;
        }

        if (id == 28004) {
            ItemDefinition.copyInventory(def, INQUISITORS_PLATESKIRT);
            ItemDefinition.copyEquipment(def, INQUISITORS_PLATESKIRT);
            def.name = "@gre@Shadow plateskirt";
            def.modelCustomColor4 = 235;
        }

        if (id == 28005) {
            def.widget_actions = new String[]{null, null, null, null, "Drop"};
            def.name = "@gre@Shadow inquisitor ornament kit";
            def.model_zoom = 1616;
            def.rotation_x = 1943;
            def.rotation_y = 564;
            def.translate_x = -10;
            def.translate_y = 20;
            def.inventory_model = 31973;
            def.color_to_replace_with = new int[]{10, 0, 1, 1, 1, 1, 1, 1, 1, 1};
            def.color_to_replace = new int[]{7607, 0, 908, 54162, 41137, 41149, 41143, 6998, 40107, 14734};
        }

        if (id == 28006) {
            def.widget_actions = new String[]{null, null, null, null, "Drop"};
            def.name = "@gre@Inquisitor's mace ornament kit";
            def.model_zoom = 1616;
            def.rotation_x = 1943;
            def.rotation_y = 564;
            def.translate_x = -10;
            def.translate_y = 20;
            def.inventory_model = 55556;
            def.animateInventory = true;
            def.color_to_replace_with = new int[]{10, 0, 1, 1, 1, 1, 1, 1, 1, 1};
            def.color_to_replace = new int[]{7607, 0, 908, 54162, 41137, 41149, 41143, 6998, 40107, 14734};
        }

        if (id == 28007) {
            def.name = "@gre@Ethereal partyhat";
            def.inventory_model = 2635;
            def.male_equip_main = 187;
            def.female_equip_main = 363;
            def.model_zoom = 440;
            def.rotation_x = 1852;
            def.rotation_y = 76;
            def.translate_x = 1;
            def.translate_y = 1;
            def.widget_actions = new String[]{null, "Wear", null, null, "Drop"};
            def.color_to_replace = new int[1];
            def.color_to_replace[0] = 926;
            def.color_to_replace_with = new int[1];
            def.color_to_replace_with[0] = 374770;
        }

        if (id == 28008) {
            def.name = "@gre@Ethereal h'ween mask";
            def.inventory_model = 2438;
            def.male_equip_main = 3188;
            def.female_equip_main = 3192;
            def.model_zoom = 730;
            def.rotation_y = 516;
            def.rotation_x = 0;
            def.translate_y = -10;
            def.widget_actions = new String[]{null, "Wear", null, null, "Drop"};
            def.color_to_replace = new int[2];
            def.color_to_replace[0] = 926;
            def.color_to_replace[1] = 0;
            def.color_to_replace_with = new int[2];
            def.color_to_replace_with[0] = 374770; // Background colour
            def.color_to_replace_with[1] = 933; // Eyes colour
        }

        if (id == 28009) {
            def.name = "@gre@Ethereal santa hat";
            def.inventory_model = 2537;
            def.model_zoom = 540;
            def.rotation_y = 72;
            def.rotation_x = 136;
            def.translate_x = 0;
            def.translate_y = -3;
            def.widget_actions = new String[]{null, "Wear", null, null, "Drop"};
            def.male_equip_main = 189;
            def.female_equip_main = 366;
            def.male_dialogue_head = 69;
            def.female_dialogue_head = 127;
            def.color_to_replace = new int[]{933, 10351};
            def.color_to_replace_with = new int[]{374770, 10351};
        }

        if (id == 14479) {
            def.name = "@gre@Beginner weapon pack";
            def.inventory_model = 20587;
            def.translate_x = 0;
            def.translate_y = 12;
            def.rotation_x = 456;
            def.rotation_y = 318;
            def.model_zoom = 2216;
            def.widget_actions = new String[]{"Open", null, null, null, "Destroy"};
        }

        if (id == 14486) {
            def.name = "@gre@Beginner dragon claws";
            def.female_equip_main = 29191;
            def.widget_actions = new String[]{null, "Wield", null, null, "Destroy"};
            def.inventory_model = 32784;
            def.male_equip_main = 29191;
            def.translate_x = -1;
            def.translate_y = 8;
            def.rotation_y = 349;
            def.rotation_x = 15;
            def.model_zoom = 886;
            def.color_to_replace = new int[]{929, 914, 918, 922};
            def.color_to_replace_with = new int[]{34770, 34770, 34770, 34770};
        }

        if (id == 14487) {
            def.name = "@gre@Beginner AGS";
            def.female_equip_main = 27649;
            def.widget_actions = new String[]{null, "Wield", null, null, "Destroy"};
            def.inventory_model = 28075;
            def.male_equip_main = 27649;
            def.translate_x = -1;
            def.translate_y = -1;
            def.rotation_y = 498;
            def.rotation_x = 484;
            def.model_zoom = 1957;
            def.color_to_replace = new int[]{-22419, -24279, -22423, -22444, -22477, -24271, -22415, -22208, -22464};
            def.color_to_replace_with = new int[]{34770, 34770, 34770, 34770, 34770, 34770, 34770, 34770, 34770};
        }

        if (id == 14488) {
            def.name = "@gre@Beginner chainmace";
            def.female_equip_main = 35771;
            def.widget_actions = new String[]{null, "Wield", null, null, "Destroy"};
            def.inventory_model = 35780;
            def.male_equip_main = 35771;
            def.translate_x = 11;
            def.translate_y = -8;
            def.rotation_y = 1495;
            def.rotation_x = 256;
            def.model_zoom = 1488;
            def.color_to_replace = new int[]{16, -11234, -11238, -11242, -11246, -10719};
            def.color_to_replace_with = new int[]{34770, 34770, 34770, 34770, 34770, 34770};
        }

        if (id == 14489) {
            def.name = "@gre@Beginner craw's bow";
            def.female_equip_main = 35769;
            def.widget_actions = new String[]{null, "Wield", null, null, "Destroy"};
            def.inventory_model = 35775;
            def.male_equip_main = 35769;
            def.translate_x = -1;
            def.translate_y = -3;
            def.rotation_y = 1463;
            def.rotation_x = 510;
            def.rotation_z = 835;
            def.model_zoom = 1979;
            def.color_to_replace = new int[]{-22242, -6087, -22440, 6602, 6699, -22448, 6736, -22225, 3346, -6099, 7124, 6709, -22431};
            def.color_to_replace_with = new int[]{34770, 34770, 34770, 34770, 34770, 34770, 34770, 34770, 34770, 34770, 34770, 34770, 34770};
        }

        if (id == 12810) {
            def.name = "@gre@Darklord helm";
            def.modelCustomColor4 = 33785;
        }

        if (id == 12811) {
            def.name = "@gre@Darklord platebody";
            def.modelCustomColor4 = 33785;
        }

        if (id == 12812) {
            def.name = "@gre@Darklord platelegs";
        }

        if (id == 28013) {
            def.name = "@gre@Veteran partyhat";
            def.inventory_model = 2635;
            def.male_equip_main = 187;
            def.female_equip_main = 363;
            def.model_zoom = 440;
            def.rotation_x = 1852;
            def.rotation_y = 76;
            def.translate_x = 1;
            def.translate_y = 1;
            def.widget_actions = new String[]{null, "Wear", null, null, "Drop"};
            def.color_to_replace = new int[1];
            def.color_to_replace[0] = 926;
            def.color_to_replace_with = new int[1];
            def.color_to_replace_with[0] = 614770;
        }

        if (id == 28014) {
            def.name = "@gre@Veteran h'ween mask";
            def.inventory_model = 2438;
            def.male_equip_main = 3188;
            def.female_equip_main = 3192;
            def.model_zoom = 730;
            def.rotation_y = 516;
            def.rotation_x = 0;
            def.translate_y = -10;
            def.widget_actions = new String[]{null, "Wear", null, null, "Drop"};
            def.color_to_replace = new int[2];
            def.color_to_replace[0] = 926;
            def.color_to_replace[1] = 0;
            def.color_to_replace_with = new int[2];
            def.color_to_replace_with[0] = 614770; // Background colour
            def.color_to_replace_with[1] = 933; // Eyes colour
        }

        if (id == 28015) {
            def.name = "@gre@Veteran santa hat";
            def.inventory_model = 2537;
            def.model_zoom = 540;
            def.rotation_y = 72;
            def.rotation_x = 136;
            def.translate_x = 0;
            def.translate_y = -3;
            def.widget_actions = new String[]{null, "Wear", null, null, "Drop"};
            def.male_equip_main = 189;
            def.female_equip_main = 366;
            def.male_dialogue_head = 69;
            def.female_dialogue_head = 127;
            def.color_to_replace = new int[]{933, 10351};
            def.color_to_replace_with = new int[]{614770, 10351};
        }

        if (id == 30180) {
            ItemDefinition.copyInventory(def, PEGASIAN_BOOTS);
            ItemDefinition.copyEquipment(def, PEGASIAN_BOOTS);
            def.name = "@gre@Pegasian boots (or)";
            def.color_to_replace = new int[]{8481, 17746, 15252, 16549, 8493, 17294};
            def.color_to_replace_with = new int[]{7114, 7114, 15252, 7114, 7114, 17294};
        }

        if (id == 30182) {
            ItemDefinition.copyInventory(def, ETERNAL_BOOTS);
            ItemDefinition.copyEquipment(def, ETERNAL_BOOTS);
            def.name = "@gre@Eternal boots (or)";
            def.color_to_replace = new int[]{9152, -22242, -22326, -10839, -22248, 695, -22361, -22510};
            def.color_to_replace_with = new int[]{9152, -22242, 7114, 7114, 7114, 695, 7114, -22510};
        }

        if (id == 29000) {
            ItemDefinition.copyInventory(def, VIGGORAS_CHAINMACE);
            ItemDefinition.copyEquipment(def, VIGGORAS_CHAINMACE);
            def.name = "@gre@Viggora's chainmace (c)";
            def.color_to_replace = new int[]{16, -11234, -11238, -11242, -11246, -10719};
            def.color_to_replace_with = new int[]{16, 1255, 1255, 5, 5, 1255};
        }

        if (id == 29001) {
            ItemDefinition.copyInventory(def, CRAWS_BOW);
            ItemDefinition.copyEquipment(def, CRAWS_BOW);
            def.name = "@gre@Craw's bow (c)";
            def.color_to_replace = new int[]{-22242, -6087, -22440, 6602, 6699, -22448, 6736, -22225, 3346, -6099, 7124, 6709, -22431};
            def.color_to_replace_with = new int[]{7, 1, 7, 1255, 10, 1255, 1255, 10, 5, 1255, 1255, 1255, 10};
        }

        if (id == 29002) {
            ItemDefinition.copyInventory(def, THAMMARONS_SCEPTRE);
            ItemDefinition.copyEquipment(def, THAMMARONS_SCEPTRE);
            def.name = "@gre@Thammaron's sceptre (c)";
            def.color_to_replace = new int[]{960, 33, 417, 20, 53, 555, 939, 12, 28, 284};
            def.color_to_replace_with = new int[]{10, 15, 1, 15, 1400, 5, 1, 10, 1400, 1400};
        }

        if (id == 2396) {
            def.name = "@gre@3% drop rate boost scroll";
        }

        if (id == 13215) {
            ItemDefinition.copyInventory(def, 13204);
            def.name = "@gre@Bloody Token";
            def.color_to_replace = new int[]{5813, 9139, 26006};
            def.color_to_replace_with = new int[]{947, 948, 949};
            def.stack_variant_size = new int[]{2, 3, 4, 5, 0, 0, 0, 0, 0, 0};
            def.stack_variant_id = new int[]{13216, 13217, 13218, 13218, 0, 0, 0, 0, 0, 0};
        }

        if (id == 13216) {
            ItemDefinition.copyInventory(def, 3985);
            def.name = "@gre@Bloody Token";
            def.color_to_replace = new int[]{5813, 9139, 26006};
            def.color_to_replace_with = new int[]{947, 948, 949};
        }

        if (id == 13217) {
            ItemDefinition.copyInventory(def, 3987);
            def.name = "@gre@Bloody Token";
            def.color_to_replace = new int[]{5813, 9139, 26006};
            def.color_to_replace_with = new int[]{947, 948, 949};
        }

        if (id == 13218) {
            ItemDefinition.copyInventory(def, 3989);
            def.name = "@gre@Bloody Token";
            def.color_to_replace = new int[]{5813, 9139, 26006};
            def.color_to_replace_with = new int[]{947, 948, 949};
        }

        if (id == ItemIdentifiers.TOXIC_BLOWPIPE || id == ItemIdentifiers.SERPENTINE_HELM || id == ItemIdentifiers.TRIDENT_OF_THE_SWAMP || id == ItemIdentifiers.TOXIC_STAFF_OF_THE_DEAD
            || id == ItemIdentifiers.TOME_OF_FIRE || id == ItemIdentifiers.SCYTHE_OF_VITUR || id == ItemIdentifiers.SANGUINESTI_STAFF || id == ItemIdentifiers.CRAWS_BOW
            || id == ItemIdentifiers.VIGGORAS_CHAINMACE || id == ItemIdentifiers.THAMMARONS_SCEPTRE || id == ItemIdentifiers.TRIDENT_OF_THE_SEAS || id == ItemIdentifiers.MAGMA_HELM
            || id == ItemIdentifiers.TANZANITE_HELM || id == ItemIdentifiers.DRAGONFIRE_SHIELD || id == ItemIdentifiers.DRAGONFIRE_WARD || id == ItemIdentifiers.ANCIENT_WYVERN_SHIELD
            || id == ItemIdentifiers.ABYSSAL_TENTACLE || id == ItemIdentifiers.BARRELCHEST_ANCHOR || id == ItemIdentifiers.SARADOMINS_BLESSED_SWORD) {
            def.widget_actions = new String[]{null, "Wield", null, null, "Drop"};
        }

        if (id == 16167) {
            ItemDefinition.copyInventory(def, 24731);
            ItemDefinition.copyEquipment(def, 24731);
            def.name = "@gre@Ring of sorcery";
            def.widget_actions = new String[]{null, "Wear", null, null, "Drop"};
            def.modelCustomColor4 = 235;
        }

        if (id == 16168) {
            def.name = "@gre@Ring of Precision";
            def.widget_actions = new String[]{null, "Wear", null, null, "Drop"};
            ItemDefinition.copyInventory(def, 12601);
            ItemDefinition.copyEquipment(def, 12601);
            def.modelCustomColor4 = 33785;
        }

        if (id == 16169) {
            def.name = "@gre@Ring of Trinity";
            def.widget_actions = new String[]{null, "Wear", null, null, "Drop"};
            ItemDefinition.copyInventory(def, 12603);
            ItemDefinition.copyEquipment(def, 12603);
            def.modelCustomColor4 = 3433;
        }

        if (id == 16172) {
            def.name = "@gre@Baby Aragog";
            ItemDefinition.copyInventory(def, VENENATIS_SPIDERLING);
            def.color_to_replace = new int[]{912, 0, 916, 103, 138, 794, 107, 908};
            def.color_to_replace_with = new int[]{138, 908, 4769, 4769, 4769, 0, 0, 0};
        }

        if (id == 16173) {
            def.name = "@gre@Jawa pet";
            ItemDefinition.copyInventory(def, ATTACK_HOOD);
            def.widget_actions = new String[]{null, null, null, null, "Drop"};
            def.modelCustomColor = 4769;
        }

        if (id == 21205) {
            ItemDefinition.copyInventory(def, 21003);
            ItemDefinition.copyEquipment(def, 21003);
            def.name = "@gre@Elder maul";
            def.widget_actions = new String[]{null, "Wield", null, null, "Destroy"};
            def.color_to_replace = new int[]{5056, 8125};
            def.color_to_replace_with = new int[]{685, 924};
        }

        if (id == 23490) {
            def.name = "@gre@Larran's key tier I";
            def.widget_actions = new String[]{null, null, null, null, "Drop"};
            def.stack_variant_size = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            def.stack_variant_id = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        }

        if (id == 14900) {
            def.name = "@gre@Larran's key tier II";
            def.animateInventory = true;
            def.inventory_model = 55560;
            def.color_to_replace = new int[]{8784, 2974, 8640};
            def.color_to_replace_with = new int[]{8107, 8103, 9129};
            def.model_zoom = 968;
            def.rotation_y = 512;
            def.rotation_x = 741;
            def.rotation_z = 1980;
            def.translate_x = -7;
            def.translate_y = 3;
            def.stackable = true;
            def.widget_actions = new String[]{null, null, null, null, "Drop"};
            def.color_to_replace = new int[]{8784, 2974, 8640};
            def.color_to_replace_with = new int[]{8107, 8103, 9129};
        }

        if (id == 14901) {
            def.name = "@gre@Larran's key tier III";
            def.animateInventory = true;
            def.inventory_model = 55561;
            def.color_to_replace = new int[]{8784, 2974, 8640};
            def.color_to_replace_with = new int[]{8107, 8103, 9129};
            def.model_zoom = 968;
            def.rotation_y = 512;
            def.rotation_x = 741;
            def.rotation_z = 1980;
            def.translate_x = -7;
            def.translate_y = 3;
            def.stackable = true;
            def.widget_actions = new String[]{null, null, null, null, "Drop"};
            def.color_to_replace = new int[]{8784, 2974, 8640};
            def.color_to_replace_with = new int[]{8107, 8103, 9129};
        }

        if (id == 16020) {
            def.name = "@gre@Dharok pet";
            def.widget_actions = new String[]{null, null, null, null, "Drop"};
            def.model_zoom = 1000;
            def.rotation_y = 100;
            def.rotation_x = 100;
            def.translate_x = 0;
            def.translate_y = 100;
            NpcDefinition npcInstance = NpcDefinition.get(NpcIdentifiers.DHAROK_THE_WRETCHED);
            def.inventory_model = npcInstance.model_id[0];
        }

        if (id == ItemIdentifiers.ARMADYL_GODSWORD_OR || id == ItemIdentifiers.BANDOS_GODSWORD_OR || id == ItemIdentifiers.SARADOMIN_GODSWORD_OR || id == ItemIdentifiers.ZAMORAK_GODSWORD_OR || id == ItemIdentifiers.GRANITE_MAUL_12848 || id == DRAGON_CLAWS_OR) {
            def.widget_actions = new String[]{null, "Wield", null, null, "Drop"};
        }

        if (id == ItemIdentifiers.ATTACKER_ICON || id == ItemIdentifiers.COLLECTOR_ICON || id == ItemIdentifiers.DEFENDER_ICON || id == ItemIdentifiers.HEALER_ICON || id == ItemIdentifiers.AMULET_OF_FURY_OR || id == ItemIdentifiers.OCCULT_NECKLACE_OR || id == ItemIdentifiers.NECKLACE_OF_ANGUISH_OR || id == ItemIdentifiers.AMULET_OF_TORTURE_OR || id == ItemIdentifiers.BERSERKER_NECKLACE_OR || id == ItemIdentifiers.TORMENTED_BRACELET_OR || id == ItemIdentifiers.DRAGON_DEFENDER_T || id == ItemIdentifiers.DRAGON_BOOTS_G) {
            def.widget_actions = new String[]{null, "Wear", null, null, "Destroy"};
        }

        if (id == 2944) {
            def.name = "@gre@Key of drops";
        }

        if (id == 24445) {
            def.name = "@gre@Twisted slayer helmet (i)";
            def.female_dialogue_head = 38997;
            def.female_equip_main = 38970;
            def.widget_actions = new String[]{null, "Wear", null, "Disassemble", "Drop"};
            def.inventory_model = 38958;
            def.male_dialogue_head = 38997;
            def.male_equip_main = 38960;
            def.translate_x = -4;
            def.translate_y = -3;
            def.rotation_y = 30;
            def.rotation_x = 1773;
            def.model_zoom = 779;
            def.color_to_replace = new int[]{16, 14272, 33, 10306, 37, 4550, 10343, 24, 10312, 12, 10334, 10318};
            def.color_to_replace_with = new int[]{8, 6073, 8, 10306, 8, 4550, 10343, 8, 10312, 8, 10334, 10318};
        }

        if (id == ItemIdentifiers.BANK_KEY) {
            def.widget_actions = new String[]{null, null, null, null, "Drop"};
        }

        if (id == 20238) {
            def.name = "@gre@Imbuement scroll";
            def.widget_actions = new String[]{null, null, null, null, "Drop"};
            def.description = "Use this scroll to imbue certain items.";
        }

        if (id == 12646) {
            def.modelCustomColor4 = 33785;
            def.name = "@gre@Niffler pet";
        }

        if (id == 20693) {
            def.name = "@gre@Fawkes pet";
        }

        if (id == 28663) {
            def.widget_actions = new String[]{null, null, null, null, "Drop"};
            def.name = "@gre@Zriawk pet";
            def.modelCustomColor4 = 33235;
            def.ambient = 40;
            def.inventory_model = 26852;
            def.translate_x = -8;
            def.translate_y = -13;
            def.rotation_y = 141;
            def.rotation_x = 1790;
            def.model_zoom = 2768;
        }

        if (id == 28639) {
            def.name = "@gre@Elder wand handle";
            def.color_to_replace = new int[]{-19153, 33, -19145, -19500};
            def.color_to_replace_with = new int[]{530, 540, 529, 10};
            def.widget_actions = new String[]{"Inspect", null, null, null, "Drop"};
            def.inventory_model = 32805;
            def.translate_x = -1;
            def.translate_y = -1;
            def.rotation_y = 606;
            def.rotation_x = 498;
            def.model_zoom = 716;
        }

        if (id == 28640) {
            def.name = "@gre@Elder wand stick";
            def.color_to_replace = new int[]{9024, 9009, 5652, 8070, 9015, 7050, 4634, -22413, 8877, 3614};
            def.color_to_replace_with = new int[]{530, 540, 529, 10, 13, 16, 19, 22, 25, 28};
            def.widget_actions = new String[]{"Inspect", null, null, null, "Drop"};
            def.inventory_model = 10565;
            def.translate_x = 6;
            def.translate_y = -7;
            def.rotation_y = 68;
            def.rotation_x = 1092;
            def.model_zoom = 540;
        }

        if (id == 28641) {
            def.name = "@gre@Talonhawk crossbow";
            def.color_to_replace = new int[]{49, 10471, 10475};
            def.color_to_replace_with = new int[]{-32518, 25, -32518};
            def.female_equip_main = 15472;
            def.widget_actions = new String[]{null, "Wield", null, null, "Drop"};
            def.inventory_model = 15493;
            def.male_equip_main = 15472;
            def.translate_x = 2;
            def.translate_y = 8;
            def.rotation_y = 444;
            def.rotation_x = 1658;
            def.model_zoom = 1104;
        }

        if (id == 28642) {
            def.widget_actions = new String[]{null, null, null, null, "Drop"};
            def.name = "@gre@Fluffy Jr pet";
            def.color_to_replace = new int[]{0, 11200, 929, 931, 9542, 902, 262, 906, 910, 914, 918, 922, 955, 9149, 7101, 8125, 6077, 4029, 957, 1981, 926};
            def.color_to_replace_with = new int[]{4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 0, 4769, 0, 0, 4769, 4769};
            def.inventory_model = 29240;
            def.rotation_y = 3;
            def.rotation_x = 213;
            def.model_zoom = 1616;
        }

        if (id == 30338) {
            def.widget_actions = new String[]{null, null, null, null, "Drop"};
            def.name = "@gre@Centaur pet";
            def.inventory_model = 16213;
            def.model_zoom = 3500;
            def.rotation_y = 25;
            def.rotation_x = 25;
            def.translate_y = -10;
        }

        if (id == 30016) {
            def.name = "@gre@Founder imp";
            def.widget_actions = new String[]{null, null, null, null, "Drop"};
            def.inventory_model = 58916;
            def.translate_x = 11;
            def.translate_y = -1;
            def.rotation_y = 116;
            def.rotation_x = 1778;
            def.model_zoom = 1424;
        }

        if (id == 30018) {
            def.name = "@gre@Corrupted nechryarch pet";
            def.widget_actions = new String[]{null, null, null, null, "Drop"};
            def.inventory_model = 58922;
            def.model_zoom = 7000;
            def.rotation_y = 25;
            def.rotation_x = 25;
            def.translate_y = -10;
        }

        if (id == 30033) {
            def.name = "@gre@Mini necromancer";
            def.widget_actions = new String[]{null, null, null, null, "Drop"};
            def.inventory_model = 58979;
            def.translate_y = -12;
            def.rotation_y = 118;
            def.rotation_x = 10;
            def.model_zoom = 1136;
        }

        if (id == 30048) {
            def.name = "@gre@Corrupted ranger gauntlets";
            def.female_equip_main = 36335;
            def.widget_actions = new String[]{null, "Wear", null, null, "Drop"};
            def.inventory_model = 36141;
            def.male_equip_main = 36325;
            def.translate_x = -1;
            def.rotation_y = 420;
            def.rotation_x = 1082;
            def.model_zoom = 917;
            def.color_to_replace = new int[]{16, 30643, 12484, 13493, -32630, 8, 24, 10411, 12};
            def.color_to_replace_with = new int[]{350780, 30643, 347770, 13493, -32630, 8, 24, 350700, 12};
        }

        if (id == 21291) {
            def.name = "@gre@Jal-nib-rek pet";
        }

        if (id == 23757) {
            def.name = "@gre@Yougnleff pet";
        }

        if (id == 23759) {
            def.name = "@gre@Corrupted yougnleff pet";
        }

        if (id == 8788) {
            def.name = "@gre@Corrupting stone";
            def.color_to_replace = new int[]{-22297, 127};
            def.color_to_replace_with = new int[]{945, 582};
        }

        if (id == 30044) {
            def.name = "@gre@Jaltok-jad pet";
            def.widget_actions = new String[]{null, null, null, null, "Drop"};
            def.inventory_model = 33012;
            def.translate_x = -3;
            def.translate_y = -30;
            def.rotation_x = 553;
            def.model_zoom = 12000;
        }

        if (id == 30131) {
            def.name = "@gre@Baby lava dragon";
            def.widget_actions = new String[]{null, null, null, null, "Drop"};
            def.inventory_model = 58995;
            def.translate_x = -97;
            def.translate_y = 9;
            def.rotation_y = 83;
            def.rotation_x = 1826;
            def.model_zoom = 2541;
        }

        if (id == 16171) {
            def.name = "@gre@Wampa pet";
            def.widget_actions = new String[]{null, null, null, null, "Drop"};
            def.inventory_model = 21802;
            def.model_zoom = 4380;
        }

        if (id == 30340) {
            def.widget_actions = new String[]{null, null, null, null, "Drop"};
            def.name = "@gre@Centaur pet";
            def.inventory_model = 16212;
            def.model_zoom = 2300;
            def.rotation_y = 25;
            def.rotation_x = 25;
            def.translate_y = -10;
        }

        if (id == 30342) {
            def.widget_actions = new String[]{null, null, null, null, "Drop"};
            def.name = "@gre@Dementor pet";
            def.color_to_replace = new int[]{3346, 6371};
            def.color_to_replace_with = new int[]{0, 0};
            def.inventory_model = 14992;
            def.translate_x = -1;
            def.translate_y = -10;
            def.rotation_y = 160;
            def.rotation_x = 64;
            def.model_zoom = 530;
        }

        if (id == 28643) {
            def.widget_actions = new String[]{null, null, null, null, "Drop"};
            def.name = "@gre@Fenrir Greyback Jr";
            def.color_to_replace = new int[]{0, 11200, 929, 931, 9542, 902, 262, 906, 910, 914, 918, 922, 955, 9149, 7101, 8125, 6077, 4029, 957, 1981, 926};
            def.color_to_replace_with = new int[]{4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 0, 4769, 0, 0, 4769, 4769};
            def.inventory_model = 26177;
            def.translate_y = 100;
            def.rotation_y = 100;
            def.rotation_x = 100;
            def.model_zoom = 1000;
        }

        if (id == 10858) {
            def.name = "@gre@Sword of gryffindor";
        }

        if (id == 12873 || id == 12875 || id == 12877 || id == 12879 || id == 12881 || id == 12883 || id == 6759 || id == DWARF_CANNON_SET) {
            def.widget_actions = new String[5];
            def.widget_actions[0] = "Open";
        }

        if (id == 13188) {
            ItemDefinition.copyInventory(def, 13652);
            def.name = "@gre@Dragon claws (or)";
            def.color_to_replace = new int[]{929, 914, 918, 922};
            def.color_to_replace_with = new int[]{929, 7114, 7114, 7114};
        }

        if (id == 12791) {
            def.widget_actions = new String[]{"Open", null, "Quick-Fill", "Empty", "Drop"};
        }

        if (id == 23650) {
            ItemDefinition.copyInventory(def, 12791);
            def.widget_actions = new String[]{"Open", null, "Quick-Fill", "Empty", "Destroy"};
            def.name = "@gre@Rune pouch (i)";
            def.ambient = 120;
        }

        if (id == 14500) {
            ItemDefinition.copyInventory(def, 23650);
            def.name = "@gre@Rune pouch (i) (broken)";
            def.ambient = -25;
            def.widget_actions = new String[]{null, null, null, null, "Destroy"};
        }

        if (id == 14501) {
            ItemDefinition.copyInventory(def, 12436);
            def.name = "@gre@Amulet of fury (or) (broken)";
            def.ambient = -25;
            def.widget_actions = new String[]{null, null, null, null, "Destroy"};
        }

        if (id == 14502) {
            ItemDefinition.copyInventory(def, 19720);
            def.name = "@gre@Occult necklace (or) (broken)";
            def.ambient = -25;
            def.widget_actions = new String[]{null, null, null, null, "Destroy"};
        }

        if (id == 14503) {
            ItemDefinition.copyInventory(def, 20366);
            def.name = "@gre@Amulet of torture (or) (broken)";
            def.ambient = -25;
            def.widget_actions = new String[]{null, null, null, null, "Destroy"};
        }

        if (id == 14504) {
            ItemDefinition.copyInventory(def, 22249);
            def.name = "@gre@Necklace of anguish (or) (broken)";
            def.ambient = -25;
            def.widget_actions = new String[]{null, null, null, null, "Destroy"};
        }

        if (id == 14505) {
            ItemDefinition.copyInventory(def, 23444);
            def.name = "@gre@Tormented bracelet (or) (broken)";
            def.ambient = -25;
            def.widget_actions = new String[]{null, null, null, null, "Destroy"};
        }

        if (id == 14506) {
            ItemDefinition.copyInventory(def, 19722);
            def.name = "@gre@Dragon defender (t) (broken)";
            def.ambient = -25;
            def.widget_actions = new String[]{null, null, null, null, "Destroy"};
        }

        if (id == 14507) {
            ItemDefinition.copyInventory(def, 22234);
            def.name = "@gre@Dragon boots (g) (broken)";
            def.ambient = -25;
            def.widget_actions = new String[]{null, null, null, null, "Destroy"};
        }

        if (id == 4447) {
            def.name = "@gre@Double drops lamp";
            def.description = "Receive double drops when killing bosses. (for 1 hour)";
        }

        if (id == 7956) {
            ItemDefinition.copyInventory(def, ItemIdentifiers.CASKET);
            def.name = "@gre@Small blood money casket";
            def.color_to_replace = new int[]{13248, 7062, -22477};
            def.color_to_replace_with = new int[]{7114, 929, 929};
        }

        if (id == 13302) {
            def.name = "@gre@<col=65280>Wilderness key</col>";
        }

        if (id == 6542) {
            def.name = "@gre@Present Mystery Box";
        }

        if (id == 16451 || id == 16452 || id == 16453 || id == 16454 || id == 16455 || id == 16456 || id == 16457 || id == 16458 || id == 16459 || id == 16460 || id == 16461 || id == 16462 || id == 30244) {
            ItemDefinition.copyInventory(def, 6199);
            switch (id) {

                case 16451:
                    def.name = "@gre@Weapon Mystery Box";
                    def.color_to_replace = new int[]{22410, 2999};
                    def.color_to_replace_with = new int[]{-21568, 127};
                    break;
                case 16452:
                    def.name = "@gre@Armour Mystery Box";
                    def.color_to_replace = new int[]{22410, 2999};
                    def.color_to_replace_with = new int[]{22464, 127};
                    break;
                case 16453:
                    def.name = "@gre@Donator Mystery Box";
                    def.color_to_replace = new int[]{22410, 2999};
                    def.color_to_replace_with = new int[]{926, 127};
                    break;

                case 16454:
                    def.name = "@gre@Legendary Mystery Box";
                    def.color_to_replace = new int[]{22410, 2999};
                    def.color_to_replace_with = new int[]{9152, 127};
                    break;

                case 16455:
                    def.name = "@gre@Grand Mystery Box";
                    def.color_to_replace = new int[]{22410, 2999};
                    def.color_to_replace_with = new int[]{0, 9152};
                    break;

                case 16456:
                    def.name = "@gre@Pet Mystery Box";
                    def.color_to_replace = new int[]{22410, 2999};
                    def.color_to_replace_with = new int[]{127, 0};
                    break;

                case 16457:
                    def.widget_actions = new String[]{"Open", null, null, null, null};
                    def.name = "@gre@Fancy Mystery Box";
                    def.modelCustomColor4 = 125;
                    break;

                case 16458:
                    def.name = "@gre@Epic Pet Mystery Box";
                    def.widget_actions = new String[]{"Open", null, null, null, null};
                    def.animateInventory = true;
                    def.inventory_model = 55567;
                    break;

                case 16459:
                    def.widget_actions = new String[]{"Open", null, null, null, null};
                    def.name = "@gre@Raids Mystery Box";
                    def.modelCustomColor4 = 125;
                    break;

                case 16460:
                    def.widget_actions = new String[]{"Open", null, null, null, null};
                    def.name = "@gre@Zenyte Mystery Box";
                    def.modelCustomColor4 = 31575;
                    break;

                case 16461:
                    def.widget_actions = new String[]{"Open", null, null, null, null};
                    def.name = "@gre@Starter Box";
                    def.animateInventory = true;
                    def.inventory_model = 55574;
                    break;

                case 16462:
                    def.widget_actions = new String[]{"Open", null, null, null, null};
                    def.name = "@gre@Clan Box";
                    def.animateInventory = true;
                    def.inventory_model = 55575;
                    break;

                case 30244:
                    def.widget_actions = new String[]{"Open", null, null, null, null};
                    def.name = "@gre@Revenant mystery box";
                    def.color_to_replace = new int[]{22410, 2999};
                    def.color_to_replace_with = new int[]{391770, 100};
                    break;
            }
        }

        if (id == 10858) {
            def.color_to_replace = new int[]{10258, 10291, 10275, 10262, 10266, 10283};
            def.color_to_replace_with = new int[]{82, 125, 125, 121, 125, 125};
        }

        if (id == 3269) {
            def.color_to_replace = new int[]{57, 49};
            def.color_to_replace_with = new int[]{926, 9152};
            def.animateInventory = true;
        }

        if (id == 30185) {
            def.name = "@gre@Donator mystery box";
            def.inventory_model = 55566;
            def.animateInventory = true;
            def.color_to_replace = new int[]{2999, 22410};
            def.color_to_replace_with = new int[]{524, 13};
            def.model_zoom = 1180;
            def.rotation_y = 160;
            def.rotation_x = 172;
            def.translate_x = 0;
            def.translate_y = -14;
            def.widget_actions = new String[]{"Quick-open", null, null, "Open", null};
        }

        if (id == 30186) {
            def.name = "@gre@Halloween mystery box";
            def.inventory_model = 55605;
            def.animateInventory = true;
            def.color_to_replace = new int[]{2999, 22410};
            def.color_to_replace_with = new int[]{524, 13};
            def.model_zoom = 1180;
            def.rotation_y = 160;
            def.rotation_x = 172;
            def.translate_x = 0;
            def.translate_y = -14;
            def.widget_actions = new String[]{"Quick-open", null, null, "Open", null};
        }

        if (id == 962) {
            def.widget_actions = new String[]{"Open", null, null, null, null};
        }

        if (id == 6722) {
            def.name = "@gre@Zombies champion pet";
            def.widget_actions = new String[]{null, null, null, null, null};
        }

        if (id == 23818) {
            def.name = "@gre@Barrelchest pet";
            def.widget_actions = new String[]{null, null, null, null, "Drop"};
            def.inventory_model = 22790;
            def.model_zoom = 5980;
            def.rotation_x = 0;
            def.rotation_y = 100;
            def.translate_x = 2;
            def.translate_y = 153;
            def.stackable = false;
        }

        if (id == 29102) {
            def.name = "@gre@Scythe of vitur kit";
            def.widget_actions = new String[]{null, null, null, null, "Drop"};
            def.inventory_model = 12841;
            def.translate_x = -10;
            def.translate_y = 20;
            def.rotation_y = 564;
            def.rotation_x = 1943;
            def.model_zoom = 1616;
            def.modelCustomColor4 = 235;
        }

        if (id == 29103) {
            def.name = "@gre@Twisted bow kit";
            def.widget_actions = new String[]{null, null, null, null, "Drop"};
            def.inventory_model = 12841;
            def.translate_x = -10;
            def.translate_y = 20;
            def.rotation_y = 564;
            def.rotation_x = 1943;
            def.model_zoom = 1616;
            def.modelCustomColor4 = 31575;
        }

        switch (id) {

            case 30074:
                def.name = "@gre@Lava dhide coif";
                def.female_equip_main = 58987;
                def.female_equip_attachment = 403;
                def.widget_actions = new String[]{null, "Wear", null, null, "Drop"};
                def.inventory_model = 58990;
                def.male_equip_main = 59000;
                def.male_equip_attachment = 230;
                def.translate_y = -35;
                def.rotation_y = 194;
                def.rotation_x = 1784;
                def.model_zoom = 789;
                break;

            case 30077:
                def.name = "@gre@Lava dhide body";
                def.female_equip_main = 58986;
                def.widget_actions = new String[]{null, "Wear", null, null, "Drop"};
                def.inventory_model = 58991;
                def.male_equip_main = 58999;
                def.translate_y = -4;
                def.rotation_x = 0;
                def.rotation_y = 548;
                def.model_zoom = 1180;
                break;

            case 30080:
                def.name = "@gre@Lava dhide chaps";
                def.female_equip_main = 58988;
                def.widget_actions = new String[]{null, "Wear", null, null, "Drop"};
                def.inventory_model = 58992;
                def.male_equip_main = 59001;
                def.translate_y = -1;
                def.rotation_y = 444;
                def.rotation_x = 0;
                def.model_zoom = 1827;
                break;

            case 30038:
                def.name = "@gre@Primordial boots (or)";
                def.female_equip_main = 58967;
                def.widget_actions = new String[]{null, "Wear", null, null, "Drop"};
                def.inventory_model = 58966;
                def.male_equip_main = 58968;
                def.translate_x = 5;
                def.translate_y = -5;
                def.rotation_y = 147;
                def.rotation_x = 279;
                def.model_zoom = 976;
                break;

            case 30297:
                def.name = "@gre@Corrupted boots";
                def.female_equip_main = 59096;
                def.widget_actions = new String[]{null, "Wear", null, null, "Drop"};
                def.inventory_model = 59095;
                def.male_equip_main = 59097;
                def.translate_x = 5;
                def.translate_y = -5;
                def.rotation_y = 147;
                def.rotation_x = 279;
                def.model_zoom = 976;
                break;

            case 30181:
                def.name = "@gre@Elder wand";
                def.color_to_replace = new int[]{-19153, -19500, -19145, 37, -16339, -16331};
                def.color_to_replace_with = new int[]{530, 540, 529, 10, 13, 16};
                def.female_equip_main = 32669;
                def.widget_actions = new String[]{null, "Wield", null, null, "Drop"};
                def.inventory_model = 32789;
                def.male_equip_main = 32669;
                def.translate_x = 2;
                def.translate_y = -4;
                def.rotation_y = 140;
                def.rotation_x = 1416;
                def.model_zoom = 668;
                break;

            case 2572:
                ItemDefinition.copyInventory(def, RING_OF_WEALTH_1);
                def.widget_actions = new String[]{null, "Wear", null, null, "Drop"};
                break;

            case 16012:
                def.name = "@gre@Baby Dark Beast";
                def.widget_actions = new String[]{null, null, null, null, "Drop"};
                def.model_zoom = 550;
                def.rotation_y = 76;
                def.rotation_x = 16;
                def.translate_x = 0;
                def.translate_y = 0;
                def.inventory_model = 7171;
                def.color_to_replace = new int[]{476};
                def.color_to_replace_with = new int[]{54444};
                break;

            case 16013:
                ItemDefinition.copyInventory(def, 12649);
                def.name = "@gre@Pet kree'arra (white)";
                def.widget_actions = new String[]{null, null, null, "Wipe-off paint", null};
                def.modelCustomColor4 = 31575;
                break;

            case 16014:
                ItemDefinition.copyInventory(def, 12651);
                def.name = "@gre@Pet zilyana (white)";
                def.widget_actions = new String[]{null, null, null, "Wipe-off paint", null};
                def.modelCustomColor4 = 33785;
                break;

            case 16015:
                ItemDefinition.copyInventory(def, 12650);
                def.name = "@gre@Pet general graardor (black)";
                def.widget_actions = new String[]{null, null, null, "Wipe-off paint", null};
                def.modelCustomColor4 = 235;
                break;

            case 16016:
                ItemDefinition.copyInventory(def, 12652);
                def.name = "@gre@Pet k'ril tsutsaroth (black)";
                def.widget_actions = new String[]{null, null, null, "Wipe-off paint", null};
                def.modelCustomColor4 = 235;
                break;

            case 16024:
                def.name = "@gre@Baby Abyssal demon";
                def.widget_actions = new String[]{null, null, null, null, "Drop"};
                def.model_zoom = 550;
                def.rotation_y = 76;
                def.rotation_x = 16;
                def.translate_x = 0;
                def.translate_y = 0;
                def.inventory_model = 7171;
                def.modelCustomColor3 = 1343;
                break;

            case 964:
                def.widget_actions = new String[]{"Cast", null, null, null, "Destroy"};
                def.name = "@gre@Vengeance";
                def.description = "Rebound damage to an opponent.";
                def.color_to_replace = new int[]{5231};
                def.color_to_replace_with = new int[]{130770};
                break;

            case 2685:
                def.name = "@gre@PvP task scroll";
                def.widget_actions = new String[]{"Read", null, "Skip task", null, "Destroy"};
                def.color_to_replace = new int[]{6464, 6608, 22305, 22034, 6740, 22422, 6583, 6587, 6604};
                def.color_to_replace_with = new int[]{933, 926, 926, 926, 933, 926, 926, 926, 933};
                break;

            case 16755:
                ItemDefinition.copyInventory(def, 13648);
                def.name = "@gre@PvP task bottle";
                def.ambient = 20;
                def.color_to_replace = new int[]{22422};
                def.color_to_replace_with = new int[]{933};
                def.widget_actions = new String[]{"Open", null, null, null, "Drop"};
                break;

            case 14524:
                ItemDefinition.copyInventory(def, 8151);
                def.name = "@gre@Blood money chest";
                def.description = "Opens for 10.000 blood money.";
                def.widget_actions = new String[]{"Open", null, null, null, "Drop"};
                def.color_to_replace = new int[]{24, 49, 4510, 4502, 8128, 7093};
                def.color_to_replace_with = new int[]{926, 926, 926, 926, 926, 926};
                break;

            case 14525:
                def.inventory_model = 55570;
                def.model_zoom = 2640;
                def.rotation_y = 114;
                def.rotation_x = 1883;
                def.animateInventory = true;
                def.name = "@gre@Mystery chest";
                def.widget_actions = new String[]{"Open", null, null, null, "Drop"};
                def.color_to_replace = new int[]{24, 49, 4510, 4502, 8128, 7093};
                def.color_to_replace_with = new int[]{24, 49, 184770, 184770, 87770, 87770};
                break;

            case 7237:
                def.name = "@gre@PvP reward casket";
                def.color_to_replace = new int[]{13248, 7062, -22477};
                def.color_to_replace_with = new int[]{13248, 563, -22477};
                def.widget_actions = new String[]{"Open", null, null, null, "Drop"};
                break;

            case 16005:
                def.name = "@gre@Baby Squirt pet";
                def.widget_actions = new String[]{null, null, null, null, "Drop"};
                def.model_zoom = 550;
                def.rotation_y = 76;
                def.rotation_x = 16;
                def.translate_x = 0;
                def.translate_y = 0;
                def.inventory_model = 7171;
                def.color_to_replace = new int[]{476};
                def.color_to_replace_with = new int[]{246770};
                break;

            case 7999:
                def.modelCustomColor4 = 155;
                def.name = "@gre@Pet paint (black)";
                def.description = "Changes color of baby K'ril and baby Graardor.";
                break;

            case 8000:
                def.modelCustomColor4 = 2;
                def.name = "@gre@Pet paint (white)";
                def.description = "Changes color of baby Kree'Arra and baby Zilyana.";
                break;

            case 14222:
                def.name = "@gre@X2 PK Points (+30)";
                def.model_zoom = 1020;
                def.rotation_y = 344;
                def.rotation_x = 656;
                def.translate_x = -1;
                def.translate_y = 11;
                def.inventory_model = 10347;
                def.widget_actions[1] = "Claim";
                def.stackable = true;
                def.description = "+30 minutes of 2x PkP.";
                break;

            case 13190:
                def.name = "@gre@$5 bond";
                def.widget_actions = new String[]{"Redeem", null, null, null, null};
                break;

            case 16278:
                ItemDefinition.copyInventory(def, 13190);
                def.name = "@gre@$10 bond";
                def.widget_actions = new String[]{"Redeem", null, null, null, null};
                def.color_to_replace = new int[]{20416, 22451, 11224, 22181, 22449, 22305, 21435, 9164, 11092, 9152, 7087, 32821, 32846, 7997, 8117, 32829, 32838, 22464};
                def.color_to_replace_with = new int[]{32416, 34451, 23224, 34181, 34449, 34305, 33435, 21164, 23092, 21152, 19087, 44821, 44846, 19997, 20117, 44829, 44838, 34464};
                break;

            case 16263:
                ItemDefinition.copyInventory(def, 13190);
                def.name = "@gre@$20 bond";
                def.widget_actions = new String[]{"Redeem", null, null, null, null};
                def.color_to_replace = new int[]{20416, 22451, 11224, 22181, 22449, 22305, 21435, 9164, 11092, 9152, 7087, 32821, 32846, 7997, 8117, 32829, 32838, 22464};
                def.color_to_replace_with = new int[]{63316, 65351, 54124, 65081, 65349, 65205, 64335, 52064, 53992, 52052, 49987, 75721, 75746, 50897, 51017, 75729, 75738, 65364};
                break;

            case 16264:
                ItemDefinition.copyInventory(def, 13190);
                def.name = "@gre@$40 bond";
                def.widget_actions = new String[]{"Redeem", null, null, null, null};
                def.color_to_replace = new int[]{20416, 22451, 11224, 22181, 22449, 22305, 21435, 9164, 11092, 9152, 7087, 32821, 32846, 7997, 8117, 32829, 32838, 22464};
                def.color_to_replace_with = new int[]{25416, 27451, 16224, 27181, 27449, 27305, 26435, 14164, 16092, 14152, 12087, 37821, 37846, 12997, 13117, 37829, 37838, 27464};
                break;

            case 16265:
                ItemDefinition.copyInventory(def, 13190);
                def.name = "@gre@$50 bond";
                def.widget_actions = new String[]{"Redeem", null, null, null, null};
                def.color_to_replace = new int[]{20416, 22451, 11224, 22181, 22449, 22305, 21435, 9164, 11092, 9152, 7087, 32821, 32846, 7997, 8117, 32829, 32838, 22464};
                def.color_to_replace_with = new int[]{35416, 37451, 26224, 37181, 37449, 37305, 36435, 24164, 26092, 24152, 22087, 47821, 47846, 22997, 23117, 47829, 47838, 37464};
                break;

            case 16266:
                ItemDefinition.copyInventory(def, 13190);
                def.name = "@gre@$100 bond";
                def.widget_actions = new String[]{"Redeem", null, null, null, null};
                def.color_to_replace = new int[]{20416, 22451, 11224, 22181, 22449, 22305, 21435, 9164, 11092, 9152, 7087, 32821, 32846, 7997, 8117, 32829, 32838, 22464};
                def.color_to_replace_with = new int[]{77316, 79351, 68124, 79081, 79349, 79205, 78335, 66064, 67992, 66052, 63987, 89721, 89746, 64897, 65017, 89729, 89738, 79364};
                break;

            case 2866:
                def.name = "@gre@Earth arrows";
                break;

            case 4160:
                def.name = "@gre@Fire arrows";
                def.color_to_replace = new int[]{57, 61, 5012, 926};
                def.color_to_replace_with = new int[]{57, 61, 5012, 926};
                break;

            case 7806:
                def.name = "@gre@Ancient warrior sword";
                def.color_to_replace = new int[]{920, 0, 103};
                def.color_to_replace_with = new int[]{391770, 0, 110};
                break;

            case 7808:
                def.name = "@gre@Ancient warrior maul";
                def.color_to_replace = new int[]{78, 103, 920};
                def.color_to_replace_with = new int[]{391470, 391470, 100, 100};
                break;

            case 7807:
                def.name = "@gre@Ancient warrior axe";
                def.color_to_replace = new int[]{0, 78, 920};
                def.color_to_replace_with = new int[]{191770, 191770, 110};
                break;

            case 17000:
                ItemDefinition.copyInventory(def, 995);
                def.stack_variant_size = new int[]{2, 3, 4, 5, 25, 100, 250, 1000, 10000, 0};
                def.stack_variant_id = new int[]{17001, 17002, 17003, 17004, 17005, 17006, 17007, 17008, 17009, 0};
                def.name = ClientConstants.CLIENT_NAME + " coins";
                def.color_to_replace = new int[]{8128};
                def.color_to_replace_with = new int[]{5706};
                break;
            case 17001:
                ItemDefinition.copyInventory(def, 996);
                def.name = ClientConstants.CLIENT_NAME + " coins";
                def.color_to_replace = new int[]{8128};
                def.color_to_replace_with = new int[]{5706};
                break;
            case 17002:
                ItemDefinition.copyInventory(def, 997);
                def.name = ClientConstants.CLIENT_NAME + " coins";
                def.color_to_replace = new int[]{8128};
                def.color_to_replace_with = new int[]{5706};
                break;
            case 17003:
                ItemDefinition.copyInventory(def, 998);
                def.name = ClientConstants.CLIENT_NAME + " coins";
                def.color_to_replace = new int[]{8128};
                def.color_to_replace_with = new int[]{5706};
                break;
            case 17004:
                ItemDefinition.copyInventory(def, 999);
                def.name = ClientConstants.CLIENT_NAME + " coins";
                def.color_to_replace = new int[]{8128};
                def.color_to_replace_with = new int[]{5706};
                break;
            case 17005:
                ItemDefinition.copyInventory(def, 1000);
                def.name = ClientConstants.CLIENT_NAME + " coins";
                def.color_to_replace = new int[]{8128};
                def.color_to_replace_with = new int[]{5706};
                break;
            case 17006:
                ItemDefinition.copyInventory(def, 1001);
                def.name = ClientConstants.CLIENT_NAME + " coins";
                def.color_to_replace = new int[]{8128};
                def.color_to_replace_with = new int[]{5706};
                break;
            case 17007:
                ItemDefinition.copyInventory(def, 1002);
                def.name = ClientConstants.CLIENT_NAME + " coins";
                def.color_to_replace = new int[]{8128};
                def.color_to_replace_with = new int[]{5706};
                break;
            case 17008:
                ItemDefinition.copyInventory(def, 1003);
                def.name = ClientConstants.CLIENT_NAME + " coins";
                def.color_to_replace = new int[]{8128};
                def.color_to_replace_with = new int[]{5706};
                break;
            case 17009:
                ItemDefinition.copyInventory(def, 1004);
                def.name = ClientConstants.CLIENT_NAME + " coins";
                def.color_to_replace = new int[]{8128};
                def.color_to_replace_with = new int[]{5706};
                break;

            case 2944:
                def.modelCustomColor3 = 54325;
                break;

            case 12773:
                def.name = "@gre@Lava whip";
                break;

            case 12774:
                def.name = "@gre@Frost whip";
                break;

            case 10586:
                def.widget_actions = new String[]{null, null, null, null, "Drop"};
                def.name = "@gre@Genie pet";
                break;

            case 12102:
                def.name = "@gre@Grim Reaper pet";
                def.model_zoom = 1010;
                def.rotation_y = 0;
                def.rotation_x = 0;
                def.translate_x = 1;
                def.translate_y = 79;
                def.inventory_model = 5100;
                def.widget_actions = new String[]{null, null, null, null, "Drop"};
                break;

            case 32102:
                def.name = "@gre@Blood Reaper pet";
                def.model_zoom = 1010;
                def.rotation_y = 0;
                def.rotation_x = 0;
                def.translate_x = 1;
                def.translate_y = 79;
                def.inventory_model = 5100;
                def.widget_actions = new String[]{null, null, null, null, "Drop"};
                def.modelCustomColor4 = 964;
                break;

            case 12081:
                def.name = "@gre@Elemental bow";
                def.model_zoom = 1862;
                def.rotation_y = 456;
                def.rotation_x = 1232;
                def.translate_x = 13;
                def.translate_y = -7;
                def.color_to_replace_with = new int[]{311770, 74, 10283, 374770, 311770, 311770, 10299, 66, 127};
                def.color_to_replace = new int[]{26772, 74, 10283, 41, 26780, 26776, 10299, 66, 127};
                def.scene_actions = new String[]{null, null, "Take", null, null};
                def.widget_actions = new String[]{null, "Wield", null, null, "Drop"};
                def.inventory_model = 28678;
                def.male_equip_main = 28622;
                def.female_equip_main = 28622;
                break;

            case 12082:
                ItemDefinition.copyInventory(def, 12373);
                ItemDefinition.copyEquipment(def, 12373);
                def.name = "@gre@Elemental staff";
                def.color_to_replace_with = new int[]{311770, 347770, 347770, 37};
                def.color_to_replace = new int[]{924, 0, 43164, 37};
                break;

            case 4067:
                def.name = "@gre@Donator ticket";
                break;

            case 619:
                def.name = "@gre@Vote ticket";
                def.stackable = true;
                def.widget_actions = new String[]{"Convert to Points", null, null, null, "Drop"};
                break;

            case 18335:
                def.name = "@gre@Lava Party hat";
                def.widget_actions = new String[5];
                def.widget_actions[1] = "Wear";
                def.model_zoom = 440;
                def.inventory_model = 55571;
                def.animateInventory = true;
                def.translate_x = 1;
                def.translate_y = 1;
                def.rotation_x = 1852;
                def.rotation_y = 76;
                def.male_equip_main = 55572;
                def.female_equip_main = 55573;
                def.color_to_replace = new int[]{926};
                def.color_to_replace_with = new int[]{926};
                def.src_texture = new short[]{926};
                def.dst_texture = new short[]{31};
                break;

            case 25936:
                def.name = "@gre@Pharaoh's hilt";
                def.inventory_model = 42786;
                def.model_zoom = 932;
                def.rotation_y = 465;
                def.rotation_x = 1079;
                def.translate_y = 9;
                def.widget_actions = new String[]{null, "Wield", null, null, "Drop"};
                def.male_equip_main = 42750;
                def.female_equip_main = 42753;
                break;

            case 25916:
                def.name = "@gre@Dragon hunter crossbow (t)";
                def.inventory_model = 42792;
                def.model_zoom = 926;
                def.rotation_y = 432;
                def.rotation_x = 258;
                def.translate_x = 0;
                def.translate_y = 9;
                def.stackable = false;
                def.widget_actions = new String[]{null, "Wield", null, null, "Drop"};
                def.male_equip_main = 42744;
                def.female_equip_main = 42740;
                break;

            case 25913:
                def.name = "@gre@Twisted slayer helmet (i)";
                def.inventory_model = 42726;
                def.model_zoom = 779;
                def.rotation_y = 30;
                def.rotation_x = 1773;
                def.translate_x = -4;
                def.translate_y = -3;
                def.stackable = false;
                def.widget_actions = new String[]{null, "Wear", null, "Disassemble", "Drop"};
                def.male_equip_main = 42733;
                def.female_equip_main = 42733;
                def.male_dialogue_head = 42782;
                def.female_dialogue_head = 42782;
                break;

            case 25907:
                def.name = "@gre@Twisted slayer helmet (i)";
                def.inventory_model = 42725;
                def.model_zoom = 779;
                def.rotation_y = 30;
                def.rotation_x = 1773;
                def.translate_x = -4;
                def.translate_y = -3;
                def.stackable = false;
                def.widget_actions = new String[]{null, "Wear", null, "Disassemble", "Drop"};
                def.male_equip_main = 42735;
                def.female_equip_main = 42738;
                def.male_dialogue_head = 42783;
                def.female_dialogue_head = 42783;
                break;

            case 25902:
                def.name = "@gre@Twisted slayer helmet (i)";
                def.inventory_model = 42724;
                def.model_zoom = 779;
                def.rotation_y = 30;
                def.rotation_x = 1773;
                def.translate_x = -4;
                def.translate_y = -3;
                def.stackable = false;
                def.widget_actions = new String[]{null, "Wear", null, "Disassemble", "Drop"};
                def.male_equip_main = 42734;
                def.female_equip_main = 42737;
                def.male_dialogue_head = 42784;
                def.female_dialogue_head = 42784;
                break;

            case 25866:
                def.name = "@gre@Bow of faerdhinen";
                def.inventory_model = 42605;
                def.color_to_replace = new int[]{33001, 32995};
                def.color_to_replace_with = new int[]{827, 957};
                def.rotation_y = 561;
                def.rotation_x = 15;
                def.translate_y = 4;
                def.stackable = false;
                def.widget_actions = new String[]{null, "Wield", null, null, "Drop"};
                def.male_equip_main = 42602;
                def.female_equip_main = 42602;
                def.female_equip_translate_y = 6;
                break;

            case 25870:
                def.name = "@gre@Blade of saeldor";
                def.inventory_model = 37980;
                def.color_to_replace = new int[]{33001, 32995};
                def.color_to_replace_with = new int[]{111, 127};
                def.model_zoom = 1876;
                def.rotation_y = 438;
                def.rotation_x = 40;
                def.rotation_z = 84;
                def.translate_x = 15;
                def.translate_y = -3;
                def.stackable = false;
                def.widget_actions = new String[]{null, "Wield", null, null, "Drop"};
                def.male_equip_main = 38270;
                def.female_equip_main = 38280;
                break;

            case 25871:
                def.name = "@gre@Blade of saeldor";
                def.inventory_model = 37980;
                def.color_to_replace = new int[]{33001, 32995};
                def.color_to_replace_with = new int[]{4, 8};
                def.model_zoom = 1876;
                def.rotation_y = 438;
                def.rotation_x = 40;
                def.rotation_z = 84;
                def.translate_x = 15;
                def.translate_y = -3;
                def.stackable = false;
                def.widget_actions = new String[]{null, "Wield", null, null, "Drop"};
                def.male_equip_main = 38270;
                def.female_equip_main = 38280;
                break;

            case 25873:
                def.name = "@gre@Blade of saeldor";
                def.inventory_model = 37980;
                def.color_to_replace = new int[]{33001, 32995};
                def.color_to_replace_with = new int[]{53671, 52655};
                def.model_zoom = 1876;
                def.rotation_y = 438;
                def.rotation_x = 40;
                def.rotation_z = 84;
                def.translate_x = 15;
                def.translate_y = -3;
                def.stackable = false;
                def.widget_actions = new String[]{null, "Wield", null, null, "Drop"};
                def.male_equip_main = 38270;
                def.female_equip_main = 38280;
                break;

            case 25875:
                def.name = "@gre@Blade of saeldor";
                def.inventory_model = 37980;
                def.color_to_replace = new int[]{33001, 32995};
                def.color_to_replace_with = new int[]{21947, 21958};
                def.model_zoom = 1876;
                def.rotation_y = 438;
                def.rotation_x = 40;
                def.rotation_z = 84;
                def.translate_x = 15;
                def.translate_y = -3;
                def.stackable = false;
                def.widget_actions = new String[]{null, "Wield", null, null, "Drop"};
                def.male_equip_main = 38270;
                def.female_equip_main = 38280;
                break;

            case 25877:
                def.name = "@gre@Blade of saeldor";
                def.inventory_model = 37980;
                def.color_to_replace = new int[]{33001, 32995};
                def.color_to_replace_with = new int[]{9668, 9804};
                def.model_zoom = 1876;
                def.rotation_y = 438;
                def.rotation_x = 40;
                def.rotation_z = 84;
                def.translate_x = 15;
                def.translate_y = -3;
                def.stackable = false;
                def.widget_actions = new String[]{null, "Wield", null, null, "Drop"};
                def.male_equip_main = 38270;
                def.female_equip_main = 38280;
                break;

            case 25879:
                def.name = "@gre@Blade of saeldor";
                def.inventory_model = 37980;
                def.model_zoom = 1876;
                def.rotation_y = 438;
                def.rotation_x = 40;
                def.rotation_z = 84;
                def.translate_x = 15;
                def.translate_y = -3;
                def.stackable = false;
                def.widget_actions = new String[]{null, "Wield", null, null, "Drop"};
                def.male_equip_main = 38270;
                def.female_equip_main = 38280;
                break;

            case 25881:
                def.name = "@gre@Blade of saeldor (t)";
                def.inventory_model = 37980;
                def.color_to_replace = new int[]{33001, 32995};
                def.color_to_replace_with = new int[]{39469, 39607};
                def.model_zoom = 1876;
                def.rotation_y = 438;
                def.rotation_x = 40;
                def.rotation_z = 84;
                def.translate_x = 15;
                def.translate_y = -3;
                def.stackable = false;
                def.widget_actions = new String[]{null, "Wield", null, null, "Drop"};
                def.male_equip_main = 38270;
                def.female_equip_main = 38280;
                break;

            case 25883:
                def.name = "@gre@Bow of faerdhinen";
                def.inventory_model = 42605;
                def.color_to_replace = new int[]{33001, 32995};
                def.color_to_replace_with = new int[]{111, 127};
                def.rotation_y = 561;
                def.rotation_x = 15;
                def.translate_y = 4;
                def.stackable = false;
                def.widget_actions = new String[]{null, "Wield", null, null, "Drop"};
                def.male_equip_main = 42602;
                def.female_equip_main = 42602;
                def.female_equip_translate_y = 6;
                break;

            case 25885:
                def.name = "@gre@Bow of faerdhinen";
                def.inventory_model = 42605;
                def.color_to_replace = new int[]{33001, 32995};
                def.color_to_replace_with = new int[]{4, 8};
                def.rotation_y = 561;
                def.rotation_x = 15;
                def.translate_y = 4;
                def.stackable = false;
                def.widget_actions = new String[]{null, "Wield", null, null, "Drop"};
                def.male_equip_main = 42602;
                def.female_equip_main = 42602;
                def.female_equip_translate_y = 6;
                break;

            case 25887:
                def.name = "@gre@Bow of faerdhinen";
                def.inventory_model = 42605;
                def.color_to_replace = new int[]{33001, 32995};
                def.color_to_replace_with = new int[]{53671, 52655};
                def.rotation_y = 561;
                def.rotation_x = 15;
                def.translate_y = 4;
                def.stackable = false;
                def.widget_actions = new String[]{null, "Wield", null, null, "Drop"};
                def.male_equip_main = 42602;
                def.female_equip_main = 42602;
                def.female_equip_translate_y = 6;
                break;

            case 25889:
                def.name = "@gre@Bow of faerdhinen";
                def.inventory_model = 42605;
                def.color_to_replace = new int[]{33001, 32995};
                def.color_to_replace_with = new int[]{21947, 21958};
                def.rotation_y = 561;
                def.rotation_x = 15;
                def.translate_y = 4;
                def.stackable = false;
                def.widget_actions = new String[]{null, "Wield", null, null, "Drop"};
                def.male_equip_main = 42602;
                def.female_equip_main = 42602;
                def.female_equip_translate_y = 6;
                break;

            case 25891:
                def.name = "@gre@Bow of faerdhinen";
                def.inventory_model = 42605;
                def.color_to_replace = new int[]{33001, 32995};
                def.color_to_replace_with = new int[]{9668, 9804};
                def.rotation_y = 561;
                def.rotation_x = 15;
                def.translate_y = 4;
                def.stackable = false;
                def.widget_actions = new String[]{null, "Wield", null, null, "Drop"};
                def.male_equip_main = 42602;
                def.female_equip_main = 42602;
                def.female_equip_translate_y = 6;
                break;

            case 25893:
                def.name = "@gre@Bow of faerdhinen";
                def.inventory_model = 42605;
                def.rotation_y = 561;
                def.rotation_x = 15;
                def.translate_y = 4;
                def.stackable = false;
                def.widget_actions = new String[]{null, "Wield", null, null, "Drop"};
                def.male_equip_main = 42602;
                def.female_equip_main = 42602;
                def.female_equip_translate_y = 6;
                break;

            case 25895:
                def.name = "@gre@Bow of faerdhinen";
                def.inventory_model = 42605;
                def.color_to_replace = new int[]{33001, 32995};
                def.color_to_replace_with = new int[]{39469, 39607};
                def.rotation_y = 561;
                def.rotation_x = 15;
                def.translate_y = 4;
                def.stackable = false;
                def.widget_actions = new String[]{null, "Wield", null, null, "Drop"};
                def.male_equip_main = 42602;
                def.female_equip_main = 42602;
                def.female_equip_translate_y = 6;
                break;

            case 23975:
            case 23971:
            case 23979:
            case 24668:
            case 24664:
            case 24666:
                def.widget_actions = new String[]{null, "Wear", null, null, "Drop"};
                break;

            case 22517:
                def.name = "@gre@Saeldor shard";
                def.widget_actions = new String[]{null, null, null, null, "Drop"};
                break;

            case 25600:
                def.name = "@gre@Ranger gloves";
                def.inventory_model = 24569;
                def.color_to_replace = new int[]{14658, 14649, 14645, 14637, 16536, 13716};
                def.color_to_replace_with = new int[]{43059, 43055, 43051, 43047, 43030, 43030};
                def.model_zoom = 917;
                def.rotation_y = 408;
                def.rotation_x = 150;
                def.translate_x = 0;
                def.stackable = false;
                def.widget_actions = new String[]{null, "Wear", null, null, "Drop"};
                def.male_equip_main = 55558;
                def.female_equip_main = 29056;
                break;

            case 27644:
                def.name = "@gre@Salazar Slytherin's locket";
                def.inventory_model = 55562;
                def.model_zoom = 590;
                def.rotation_y = 500;
                def.rotation_x = 0;
                def.translate_x = 0;
                def.translate_y = 7;
                def.stackable = false;
                def.scene_actions = new String[]{null, null, "Take", null, null};
                def.widget_actions = new String[]{null, "Wear", null, null, "Drop"};
                def.male_equip_main = 55563;
                def.female_equip_main = 55564;
                break;

            case 3269:
                def.name = "@gre@Slayer key";
                def.inventory_model = 55565;
                def.color_to_replace = new int[]{57, 49};
                def.color_to_replace_with = new int[]{6453, 6569};
                def.model_zoom = 860;
                def.rotation_y = 460;
                def.rotation_x = 20;
                def.translate_x = -9;
                def.translate_y = -2;
                def.stackable = true;
                break;
            case 24670:
                def.widget_actions = new String[]{null, null, null, null, "Drop"};
                break;
        }
    }
}
