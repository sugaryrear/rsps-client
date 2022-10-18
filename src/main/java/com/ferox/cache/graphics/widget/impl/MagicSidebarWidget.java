package com.ferox.cache.graphics.widget.impl;

import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.Widget;

public class MagicSidebarWidget extends Widget {

    public static void unpack(AdvancedFont[] font) {
        lunar_magic_book(font);
        wrathRune();
        normals(font);
        ancients(font);
        normalsAutocast(font);
    }

    private static void normalsAutocast(AdvancedFont[] font) {
        Widget spells = Widget.cache[1829];

        spells.child_y[4] = 42;
        spells.child_y[5] = 42;
        spells.child_y[6] = 42;
        spells.child_y[7] = 42;

        spells.child_y[8] = 77;
        spells.child_y[9] = 77;
        spells.child_y[10] = 77;
        spells.child_y[11] = 77;

        spells.child_y[12] = 112;
        spells.child_y[13] = 112;
        spells.child_y[14] = 112;
        spells.child_y[15] = 112;

        addSpellSmaller(50050, 556, 21880, 7, 1, 30005, 28226, 80, "Wind Surge", "A very high level Air missile", font, 954, 955, 2, 5, false);
        addSpellSmall(50070, 555, 556, 21880, 10, 7, 1, 30004, 30005, 28226, 84, "Water Surge", "A very high level Water missile", font, 956, 957, 2, 5, false);
        addSpellSmall(50090, 557, 556, 21880, 10, 7, 1, 30006, 30005, 28226, 89, "Earth Surge", "A very high level Earth missile", font, 958, 959, 2, 5, false);
        addSpellSmall(50110, 554, 556, 21880, 10, 7, 1, 30003, 30005, 28226, 94, "Fire Surge", "A very high level Fire missile", font, 960, 961, 2, 5,false);

        int[] tempChildIds = new int[spells.children.length + 4];
        int[] tempChildX = new int[spells.child_x.length + 4];
        int[] tempChildY = new int[spells.child_y.length + 4];
        System.arraycopy(spells.children, 0, tempChildIds, 0, spells.children.length);
        System.arraycopy(spells.child_x, 0, tempChildX, 0, spells.child_x.length);
        System.arraycopy(spells.child_y, 0, tempChildY, 0, spells.child_y.length);

        spells.children = tempChildIds;
        spells.child_x = tempChildX;
        spells.child_y = tempChildY;

        spells.child(spells.child_x.length - 1, 50050, 23, 143); // wind surge
        spells.child(spells.child_x.length - 2, 50070, 63, 143); // water surge
        spells.child(spells.child_x.length - 3, 50090, 103, 143); // earth surge
        spells.child(spells.child_x.length - 4, 50110, 143, 143); // fire surge
    }

    private static void wrathRune() {
        Widget rune = addTabInterface(28226);
        rune.totalChildren(1);
        addSprite(28228, 947);
        setBounds(28228, 0, 0, 0, rune);
    }

    private static void normals(AdvancedFont[] font) {
        Widget newWidget = addTabInterface(938);
        Widget realWidget = cache[1151];
        Widget moveSpells = cache[12424];
        moveSpells.height = 250;

        int[] stack = new int[cache[20552].valueIndexArray[0].length + 6];
        System.arraycopy(cache[20552].valueIndexArray[0], 0, stack, 0, 3);
        stack[3] = 4;
        stack[4] = 1688;
        stack[5] = 11791;
        stack[6] = 4;
        stack[7] = 1688;
        stack[8] = 12904;
        cache[20552].valueIndexArray[0] = stack;

        //Lvl-1 Enchant debug
        /*System.out.println(cache[1155].spellName);
        System.out.println(cache[1155].type);
        System.out.println(cache[1155].optionType);
        System.out.println(cache[1155].contentType);
        System.out.println(cache[1155].hoverType);
        System.out.println(cache[1155].selectedTargetMask);
        System.out.println(cache[1155].selectedActionName);
        System.out.println(cache[1155].tooltip);*/
        for (int index = 57; index < 58; index++) {

            //Move varrock
            moveSpells.child_x[12] = 30;
            moveSpells.child_y[12] = 49;

            moveSpells.child_x[22] = 124;
            moveSpells.child_y[22] = 73;

            // earth wave
            moveSpells.child_x[36] = 96;
            moveSpells.child_y[36] = 168;

            // enfeeble
            moveSpells.child_x[46] = 120;
            moveSpells.child_y[46] = 168;

            // teleother lumbridge
            moveSpells.child_x[53] = 144;
            moveSpells.child_y[53] = 168;

            // fire wave
            moveSpells.child_x[37] = 1;
            moveSpells.child_y[37] = 192;

            // entangle
            moveSpells.child_x[50] = 23;
            moveSpells.child_y[50] = 192;

            // stun
            moveSpells.child_x[47] = 47;
            moveSpells.child_y[47] = 193;

            // charge
            moveSpells.child_x[41] = 71;
            moveSpells.child_y[41] = 192;

            // teleother falador
            moveSpells.child_x[54] = 120;
            moveSpells.child_y[54] = 192;

            // teleblock
            moveSpells.child_x[55] = 0;
            moveSpells.child_y[55] = 218;

            // lvl-6 enchant
            moveSpells.child_x[57] = 47;
            moveSpells.child_y[57] = 218;

            // teleother camelot
            moveSpells.child_x[56] = 71;
            moveSpells.child_y[56] = 218;
        }
        realWidget.child_y[1] = 12;
        realWidget.child_x[1] = 14;

        addSpellSmall2_3(31674, 563, 566, 555, 554, 2, 2, 4, 5, 30012, 30015, 30004, 30003, 68, "Teleport to Kourend", "Teleports you to Kourend", font, 950, 951,7, 5);

        addSpellLarge2(13674, 563, 560, 562, 1, 1, 1, 30012, 30009, 30011, 84, "Teleport to Bounty<br>Target", "Teleports you near your Bounty<br>Hunter Target", font, 948, 949, 7, 5);

        addSpellSmall2(22674, 565, 566, 564, 20, 20, 1, 30014, 30015, 30013, 92, "Lvl-7 Enchant", "For use on zenyte jewellery", font, 952, 953, 16, 2);

        addSpellSmaller(22708, 556, 21880, 7, 1, 30005, 28226, 80, "Wind Surge", "A very high level Air missile", font, 954, 955, 10, 2, true);

        addSpellSmall(22658, 555, 556, 21880, 10, 7, 1, 30004, 30005, 28226, 84, "Water Surge", "A very high level Water missile", font, 956, 957, 10, 2, true);

        addSpellSmall(22628, 557, 556, 21880, 10, 7, 1, 30006, 30005, 28226, 89, "Earth Surge", "A very high level Earth missile", font, 958, 959, 10, 2, true);

        addSpellSmall(22608, 554, 556, 21880, 10, 7, 1, 30003, 30005, 28226, 94, "Fire Surge", "A very high level Fire missile", font, 960, 961, 10, 2, true);

        setChildren(15, newWidget);
        setBounds(31674, 84, 178, 0, newWidget);
        setBounds(13674, 38, 230, 1, newWidget);
        setBounds(22674, 132, 230, 2, newWidget);
        setBounds(22708, 110, 205, 3, newWidget);
        setBounds(22658, 158, 205, 4, newWidget);
        setBounds(22628, 110, 230, 5, newWidget);
        setBounds(22608, 158, 230, 6, newWidget);
        setBounds(1151, 0, 0, 7, newWidget);
        setBounds(22609, 5, 5, 8, newWidget);
        setBounds(22629, 5, 5, 9, newWidget);
        setBounds(22659, 5, 5, 10, newWidget);
        setBounds(22709, 5, 5, 11, newWidget);
        setBounds(31675, 5, 5, 12, newWidget);
        setBounds(13675, 5, 5, 13, newWidget);
        setBounds(22675, 5, 5, 14, newWidget);
    }

    private static void ancients(AdvancedFont[] font) {
        Widget newInterfaceId = addTabInterface(838);
        Widget widget = cache[12855];
        widget.child_y[22] = 153;
        widget.child_x[22] = 18;
        widget.child_y[30] = 153;
        widget.child_x[30] = 65;
        widget.child_y[44] = 153; // annakarl
        widget.child_x[44] = 112;
        widget.child_y[46] = 180; // ghorrock
        widget.child_x[46] = 63;
        widget.child_y[7] = 181; // ice barrage
        widget.child_x[7] = 18;
        widget.child_y[15] = 155; // blood barrage
        widget.child_x[15] = 154;

        addSpellLarge2(34674, 563, 560, 562, 1, 1, 1, 30012, 30009, 30011, 84, "Teleport to Bounty<br>Target", "Teleports you near your Bounty<br>Hunter Target", font, 948, 949, 7, 5);
        setChildren(3, newInterfaceId);
        setBounds(12855, 0, 0, 0, newInterfaceId);
        setBounds(34674, 150, 126, 1, newInterfaceId);
        setBounds(34675, 5, -5, 2, newInterfaceId);
    }

    private static void lunar_magic_book(AdvancedFont[] tda) {
        constructLunar();
        addLunarRune(30003, 0, "Fire");
        addLunarRune(30004, 1, "Water");
        addLunarRune(30005, 2, "Air");
        addLunarRune(30006, 3, "Earth");
        addLunarRune(30007, 4, "Mind");
        addLunarRune(30008, 5, "Body");
        addLunarRune(30009, 6, "Death");
        addLunarRune(30010, 7, "Nature");
        addLunarRune(30011, 8, "Chaos");
        addLunarRune(30012, 9, "Law");
        addLunarRune(30013, 10, "Cosmic");
        addLunarRune(30014, 11, "Blood");
        addLunarRune(30015, 12, "Soul");
        addLunarRune(30016, 13, "Astral");

        addLunar3RunesSmallBox(30017, 9075, 554, 555, 0, 4, 3, 30003, 30004, 64, "Bake Pie",
            "Bake pies without a stove", tda, 0, 16, 2);
        addLunar2RunesSmallBox(30025, 9075, 557, 0, 7, 30006, 65, "Cure Plant", "Cure disease on farming patch", tda, 1,
            4, 2);
        addLunar3RunesBigBox(30032, 9075, 564, 558, 0, 0, 0, 30013, 30007, 65, "Monster Examine",
            "Detect the combat statistics of a<br>monster", tda, 2, 2, 2);
        addLunar3RunesSmallBox(30040, 9075, 564, 556, 0, 0, 1, 30013, 30005, 66, "NPC Contact",
            "Speak with varied NPCs", tda, 3, 0, 2);
        addLunar3RunesSmallBox(30048, 9075, 563, 557, 0, 0, 9, 30012, 30006, 67, "Cure Other", "Cure poisoned players",
            tda, 4, 8, 2);
        addLunar3RunesSmallBox(30056, 9075, 555, 554, 0, 2, 0, 30004, 30003, 67, "Humidify",
            "Fills certain vessels with water", tda, 5, 0, 5);
        addLunar3RunesSmallBox(30064, 9075, 563, 557, 1, 0, 1, 30012, 30006, 68, "Daily Money Makers",
            "Opens the daily tasks.", tda, 6, 0, 5);
        addLunar3RunesBigBox(30075, 9075, 563, 557, 1, 0, 3, 30012, 30006, 69, "Training & Slayer",
            "Teleport to various monsters", tda, 7, 0, 5);
        addLunar3RunesSmallBox(30083, 9075, 563, 557, 1, 0, 5, 30012, 30006, 70, "Boss Teleports",
            "Teleport to powerful foes", tda, 8, 0, 5);
        addLunar3RunesSmallBox(30091, 9075, 564, 563, 1, 1, 0, 30013, 30012, 70, "Cure Me", "Cures Poison", tda, 9, 0,
            5);
        addLunar2RunesSmallBox(30099, 9075, 557, 1, 1, 30006, 70, "Hunter Kit", "Get a kit of hunting gear", tda, 10, 0,
            5);
        addLunar3RunesSmallBox(30106, 9075, 563, 555, 1, 0, 0, 30012, 30004, 71, "PK Teleports",
            "Teleport Pking spots", tda, 11, 0, 5);
        addLunar3RunesBigBox(30114, 9075, 563, 555, 1, 0, 4, 30012, 30004, 72, "Tele Group Waterbirth",
            "Teleports players to Waterbirth<br>island", tda, 12, 0, 5);
        addLunar3RunesSmallBox(30122, 9075, 564, 563, 1, 1, 1, 30013, 30012, 73, "Cure Group",
            "Cures Poison on players", tda, 13, 0, 5);
        addLunar3RunesBigBox(30130, 9075, 564, 559, 1, 1, 4, 30013, 30008, 74, "Stat Spy",
            "Cast on another player to see their<br>skill levels", tda, 14, 8, 2);
        addLunar3RunesBigBox(30138, 9075, 563, 554, 1, 1, 2, 30012, 30003, 74, "Barbarian Teleport",
            "Teleports you to the Barbarian<br>outpost", tda, 15, 0, 5);
        addLunar3RunesBigBox(30146, 9075, 563, 554, 1, 1, 5, 30012, 30003, 75, "Tele Group Barbarian",
            "Teleports players to the Barbarian<br>outpost", tda, 16, 0, 5);
        addLunar3RunesSmallBox(30154, 9075, 554, 556, 1, 5, 9, 30003, 30005, 76, "Superglass Make",
            "Make glass without a furnace", tda, 17, 16, 2);
        addLunar3RunesSmallBox(30162, 9075, 563, 555, 1, 1, 3, 30012, 30004, 77, "Khazard Teleport",
            "Teleports you to Port khazard", tda, 18, 0, 5);
        addLunar3RunesSmallBox(30170, 9075, 563, 555, 1, 1, 7, 30012, 30004, 78, "Tele Group Khazard",
            "Teleports players to Port khazard", tda, 19, 0, 5);
        addLunar3RunesBigBox(30178, 9075, 564, 559, 1, 0, 4, 30013, 30008, 78, "Dream",
            "Take a rest and restore hitpoints 3<br> times faster", tda, 20, 0, 5);
        addLunar3RunesSmallBox(30186, 9075, 557, 555, 1, 9, 4, 30006, 30004, 79, "String Jewellery",
            "String amulets without wool", tda, 21, 0, 5);
        addLunar3RunesLargeBox(30194, 9075, 557, 555, 1, 9, 9, 30006, 30004, 80, "Stat Restore Pot<br>Share",
            "Share a potion with up to 4 nearby<br>players", tda, 22, 0, 5);
        addLunar3RunesSmallBox(30202, 9075, 554, 555, 1, 6, 6, 30003, 30004, 81, "Magic Imbue",
            "Combine runes without a talisman", tda, 23, 0, 5);
        addLunar3RunesBigBox(30210, 9075, 561, 557, 2, 1, 14, 30010, 30006, 82, "Fertile Soil",
            "Fertilise a farming patch with super<br>compost", tda, 24, 4, 2);
        addLunar3RunesBigBox(30218, 9075, 557, 555, 2, 11, 9, 30006, 30004, 83, "Boost Potion Share",
            "Shares a potion with up to 4 nearby<br>players", tda, 25, 0, 5);
        addLunar3RunesSmallBox(30226, 9075, 563, 555, 2, 2, 9, 30012, 30004, 84, "Fishing Guild Teleport",
            "Teleports you to the fishing guild", tda, 26, 0, 5);
        addLunar3RunesLargeBox(30234, 9075, 563, 555, 1, 2, 13, 30012, 30004, 85, "Tele Group Fishing Guild",
            "Teleports players to the Fishing<br>Guild", tda, 27, 0, 5);
        addSpellBig2(30234, 563, 560, 562, 1, 1, 1, 30012, 30009, 30011, 85, "Teleport to Target", "Teleports you near your Bounty<br>Hunter Target", tda, 948, 949, 7, 5);
        addLunar3RunesSmallBox(30242, 9075, 557, 561, 2, 14, 0, 30006, 30010, 85, "Plank Make", "Turn Logs into planks",
            tda, 28, 16, 5);
        addLunar3RunesSmallBox(30250, 9075, 563, 555, 2, 2, 9, 30012, 30004, 86, "Catherby Teleport",
            "Teleports you to Catherby", tda, 29, 0, 5);
        addLunar3RunesSmallBox(30258, 9075, 563, 555, 2, 2, 14, 30012, 30004, 87, "Tele Group Catherby",
            "Teleports players to Catherby", tda, 30, 0, 5);
        addLunar3RunesSmallBox(30266, 9075, 563, 555, 2, 2, 7, 30012, 30004, 88, "Ice Plateau Teleport",
            "Teleports you to Ice Plateau", tda, 31, 0, 5);
        addLunar3RunesLargeBox(30274, 9075, 563, 555, 2, 2, 15, 30012, 30004, 89, "Tele Group Ice Plateau",
            "Teleports players to Ice Plateau", tda, 32, 0, 5);
        addLunar3RunesBigBox(30282, 9075, 563, 561, 2, 1, 0, 30012, 30010, 90, "Energy Transfer",
            "Spend HP and SA energy to<br> give another SA and run energy", tda, 33, 8, 2);
        addLunar3RunesBigBox(30290, 9075, 563, 565, 2, 2, 0, 30012, 30014, 91, "Heal Other",
            "Transfer up to 75% of hitpoints<br> to another player", tda, 34, 8, 2);
        addLunar3RunesBigBox(30298, 9075, 560, 557, 2, 1, 9, 30009, 30006, 92, "Vengeance Other",
            "Allows another player to rebound<br>damage to an opponent", tda, 35, 8, 2);
        addLunar3RunesSmallBox(30306, 9075, 560, 557, 3, 1, 9, 30009, 30006, 93, "Vengeance",
            "Rebound damage to an opponent", tda, 36, 0, 5);
        addLunar3RunesBigBox(30314, 9075, 565, 563, 3, 2, 5, 30014, 30012, 94, "Heal Group",
            "Transfer up to 75% of hitpoints<br> to a group", tda, 37, 0, 5);
        addLunar3RunesBigBox(30322, 9075, 564, 563, 2, 1, 0, 30013, 30012, 95, "Spellbook Swap",
            "Change to another spellbook for 1<br>spell cast", tda, 38, 0, 5);
    }

    private static void constructLunar() {
        Widget widget = addInterface(29999);
        setChildren(80, widget);
        int[] Cid = { 19210, 30017, 30025, 30032, 30040, 30048, 30056, 30064, 30075, 30083, 30091, 30099, 30106, 30114,
            30122, 30130, 30138, 30146, 30154, 30162, 30170, 30178, 30186, 30194, 30202, 30210, 30218, 30226, 30234,
            30242, 30250, 30258, 30266, 30274, 30282, 30290, 30298, 30306, 30314, 30322, 30001, 30018, 30026, 30033,
            30041, 30049, 30057, 30065, 30076, 30084, 30092, 30100, 30107, 30115, 30123, 30131, 30139, 30147, 30155,
            30163, 30171, 30179, 30187, 30195, 30203, 30211, 30219, 30227, 30235, 30243, 30251, 30259, 30267, 30275,
            30283, 30291, 30299, 30307, 30315, 30323 };

        int[] xCord = { 11, 40, 71, 103, 135, 165, 8, 39, 71, 103, 135, 165, 12, 42, 71, 103, 135, 165, 14, 42, 71, 101,
            135, 168, 11, 42, 74, 103, 135, 164, 10, 42, 71, 103, 136, 165, 13, 42, 71, 104, 6, 5, 5, 5, 5, 5, 5, 5,
            5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5 };

        int[] yCord = { 9, 9, 12, 10, 12, 10, 39, 39, 39, 39, 39, 37, 68, 68, 68, 68, 68, 68, 97, 97, 97, 97, 98, 98,
            125, 124, 125, 125, 125, 126, 155, 155, 155, 155, 155, 155, 185, 185, 184, 184, 184, 176, 176, 163, 176,
            176, 176, 176, 163, 176, 176, 176, 176, 163, 176, 163, 163, 163, 176, 176, 176, 163, 176, 149, 176, 163,
            163, 176, 149, 176, 176, 176, 176, 176, 9, 9, 9, 9, 9, 9 };

        for (int i = 0; i < Cid.length; i++) {
            setBounds(Cid[i], xCord[i], yCord[i], i, widget);
        }
    }
}
