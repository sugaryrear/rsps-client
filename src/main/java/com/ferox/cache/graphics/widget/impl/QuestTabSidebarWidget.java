package com.ferox.cache.graphics.widget.impl;

import com.ferox.ClientConstants;
import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.Widget;

/**
 * @author Zerikoth
 * @Since oktober 12, 2020
 */
public class QuestTabSidebarWidget extends Widget {

    public static void unpackInfo(AdvancedFont[] font) {
        Widget interface_ = addTabInterface(12650);
        Widget scrolls = addTabInterface(12655);
        scrolls.height = 216;
        scrolls.scrollMax = 557;
        scrolls.width = 163;
        addSprite(12651, 175);

        addHoverButton(12652, 1267, 18, 18, "View Achievements", -1, 12653, 1);
        addHoveredButton(12653, 1268, 18, 18, 12502);
        addHoverButton(12754, 1265, 18, 18, "View Info", -1, 12755, 1);
        addHoveredButton(12755, 1266, 18, 18, 12503);
        addHoverButton(12750, 1275, 18, 18, "Manage Player Title", -1, 12751, 1);
        addHoveredButton(12751, 1276, 18, 18, 12500);
        addHoverButton(12752, 1271, 18, 18, "Quick Actions Menu", -1, 12753, 1);
        addHoveredButton(12753, 1272, 18, 18, 12501);

        addText(12700, "Players online:", font, 2, 0xff9933, false, true);
        addText(12654, "Panel", font, 2, 0xff9933, false, true);
        addSprite(12656, 176);
        addText(12657, "Server info:", font, 2, 0xff9933, false, true);
        addText(12658, "Time:", font, 1, 0xff9933, false, true);
        addText(12659, "Uptime:", font, 1, 0xff9933, false, true);
        addText(12660, "Next World Boss:", font, 1, 0xff9933, false, true);
        addHoverText(12661, "Open Our Website", "Go to "+ClientConstants.CLIENT_NAME+".com", font, 1, 0xff9933, false, true, 150, 11, 0xFFBD7D);
        addHoverText(12662, "Discord: Click here", "Go to discord",font, 1, 0xff9933, false, true, 130, 11, 0xFFBD7D);
        addHoverText(12663, "Open Webstore", "Go to the webstore", font, 1, 0xff9933, false, true, 150, 11, 0xFFBD7D);

        addSprite(12664, 572);
        addText(12665, "Player info:", font, 2, 0xff9933, false, true);
        addText(12666, "Game Mode:", font, 1, 0xff9933, false, true);
        addText(12667, "Play Time:", font, 1, 0xff9933, false, true);
        addText(12668, "Registered On:", font, 1, 0xff9933, false, true);
        addText(12669, "Member Rank:", font, 1, 0xff9933, false, true);
        addText(12670, "Total Donated:", font, 1, 0xff9933, false, true);
        addText(12671, "Vote Points:", font, 1, 0xff9933, false, true);
        addText(12672, "Boss Points:", font, 1, 0xff9933, false, true);
        addText(12673, "Referrals:", font, 1, 0xff9933, false, true);

        addSprite(12674, 570);
        addText(12675, "Wildy info:", font, 2, 0xff9933, false, true);
        addText(12676, "Players in wildy:", font, 1, 0xff9933, false, true);
        addText(12677, "Elo rating:", font, 1, 0xff9933, false, true);
        addText(12678, "Player kills:", font, 1, 0xff9933, false, true);
        addText(12679, "Player deaths:", font, 1, 0xff9933, false, true);
        addText(12680, "KDR:", font, 1, 0xff9933, false, true);
        addText(12681, "Targets killed:", font, 1, 0xff9933, false, true);
        addText(12682, "Killstreak:", font, 1, 0xff9933, false, true);
        addText(12683, "Wilderness streak:", font, 1, 0xff9933, false, true);
        addText(12684, "Highest killstreak:", font, 1, 0xff9933, false, true);
        addText(12685, "Target points:", font, 1, 0xff9933, false, true);
        addText(12686, "Wealth Risked:", font, 1, 0xff9933, false, true);
        addText(12687, "Wilderness Key:", font, 1, 0xff9933, false, true);

        addSprite(12688, 575);
        addText(12689, "Slayer info:", font, 2, 0xff9933, false, true);
        addText(12690, "Task:", font, 1, 0xff9933, false, true);
        addText(12691, "Task streak:", font, 1, 0xff9933, false, true);
        addText(12692, "Tasks completed:", font, 1, 0xff9933, false, true);
        addText(12693, "Slayer Keys Received:", font, 1, 0xff9933, false, true);
        addText(12694, "Slayer points:", font, 1, 0xff9933, false, true);

        addSprite(12695, 938);
        addText(12696, "Custom Settings:", font, 2, 0xff9933, false, true);
        addCustomClickableText(12697, "Drag setting:", "Set", font, 1, 0xff9933, false, true, 135, 10);
        addCustomClickableText(12698, "Exp: (<col=ff0000>unlocked</col>)", "Select", font, 1, 0xff9933, false, true, 87, 10);


        int intChild = 0;
        int intChild2 = 0;
        scrolls.totalChildren(43);
        scrolls.child(intChild2++, 12656, 1, 1);
        scrolls.child(intChild2++, 12657, 18, 1);
        scrolls.child(intChild2++, 12658, 1, 20);
        scrolls.child(intChild2++, 12659, 1, 34);
        scrolls.child(intChild2++, 12660, 1, 49);
        scrolls.child(intChild2++, 12661, 1, 63);
        scrolls.child(intChild2++, 12662, 1, 77);
        scrolls.child(intChild2++, 12663, 1, 91);
        scrolls.child(intChild2++, 12664, 0, 108);
        scrolls.child(intChild2++, 12665, 18, 108);
        scrolls.child(intChild2++, 12666, 1, 125);
        scrolls.child(intChild2++, 12667, 1, 140);
        scrolls.child(intChild2++, 12668, 1, 152);
        scrolls.child(intChild2++, 12669, 1, 164);
        scrolls.child(intChild2++, 12670, 1, 176);
        scrolls.child(intChild2++, 12671, 1, 188);
        scrolls.child(intChild2++, 12672, 1, 200);
        scrolls.child(intChild2++, 12673, 1, 214);
        scrolls.child(intChild2++, 12674, 0, 234);
        scrolls.child(intChild2++, 12675, 18, 234);
        scrolls.child(intChild2++, 12676, 1, 254);
        scrolls.child(intChild2++, 12677, 1, 267);
        scrolls.child(intChild2++, 12678, 1, 280);
        scrolls.child(intChild2++, 12679, 1, 293);
        scrolls.child(intChild2++, 12680, 1, 307);
        scrolls.child(intChild2++, 12681, 1, 319);
        scrolls.child(intChild2++, 12682, 1, 333);
        scrolls.child(intChild2++, 12683, 1, 345);
        scrolls.child(intChild2++, 12684, 1, 358);
        scrolls.child(intChild2++, 12685, 1, 371);
        scrolls.child(intChild2++, 12686, 1, 383);
        scrolls.child(intChild2++, 12687, 1, 395);
        scrolls.child(intChild2++, 12688, 1, 413);
        scrolls.child(intChild2++, 12689, 18, 413);
        scrolls.child(intChild2++, 12690, 1, 431);
        scrolls.child(intChild2++, 12691, 1, 443);
        scrolls.child(intChild2++, 12692, 1, 455);
        scrolls.child(intChild2++, 12693, 1, 467);
        scrolls.child(intChild2++, 12694, 1, 479);
        scrolls.child(intChild2++, 12695, 1, 500);
        scrolls.child(intChild2++, 12696, 18, 500);
        scrolls.child(intChild2++, 12697, 1, 518);
        scrolls.child(intChild2++, 12698, 1, 532);

        interface_.totalChildren(12);
        interface_.child(intChild++, 12652, 154, 4);
        interface_.child(intChild++, 12653, 154, 4);
        interface_.child(intChild++, 12754, 172, 4);
        interface_.child(intChild++, 12755, 172, 4);
        interface_.child(intChild++, 12654, 9, 4);
        interface_.child(intChild++, 12651, 4, 24);
        interface_.child(intChild++, 12655, 6, 26);
        interface_.child(intChild++, 12700, 9, 244);
        interface_.child(intChild++, 12750, 136, 4);
        interface_.child(intChild++, 12751, 136, 4);
        interface_.child(intChild++, 12752, 118, 4);
        interface_.child(intChild++, 12753, 118, 4);
    }

    public static void unpack(AdvancedFont[] font) {
        final Widget questTabSkeleton = addTabInterface(23350);

        int SMALL_BOX_SPRITE = 1278;
        int MEDIUM_BACK_GROUND_SPRITE = 1279;
        int MEDIUM_BACK_GROUND_BORDER = 1280;

        int LARGE_BACK_GROUND_SPRITE = 1281;
        int LARGE_BACK_GROUND_BORDER = 1282;

        int frame = 0;

        addHoverButton(23347, 1275, 18, 18, "Manage Player Title", -1, 23348, 1);
        addHoveredButton(23348, 1276, 18, 18, 23349);

        addHoverButton(23351, 1265, 18, 18, ClientConstants.CLIENT_NAME + " " + "Journal", -1, 23352, 1);
        addHoveredButton(23352, 1266, 18, 18, 23353);

        addHoverButton(23354, 1267, 18, 18, "Achievements", -1, 23355, 1);
        addHoveredButton(23355, 1268, 18, 18, 23356);

        addHoverButton(23367, 1271, 18, 18, "Quick Actions", -1, 23368, 1);
        addHoveredButton(23368, 1272, 18, 18, 23369);

        addSprite(23360, MEDIUM_BACK_GROUND_SPRITE);
        addSprite(23361, SMALL_BOX_SPRITE);

        questTabSkeleton.totalChildren(8);

        // blue
        setBounds(23367, 150, 2, frame++, questTabSkeleton);
        setBounds(23368, 150, 2, frame++, questTabSkeleton);

        // Green
        setBounds(23351, 170, 2, frame++, questTabSkeleton);
        setBounds(23352, 170, 2, frame++, questTabSkeleton);

        // Red
        setBounds(23354, 130, 2, frame++, questTabSkeleton);
        setBounds(23355, 130, 2, frame++, questTabSkeleton);

        setBounds(23347, 110, 2, frame++, questTabSkeleton);
        setBounds(23348, 110, 2, frame++, questTabSkeleton);

        final Widget infoTab = addTabInterface(23365);

        frame = 0;

        addText(23366, "Journal", 0xFF981F, false, true, 52, font, 2);

        addCustomClickableText(23450, "<img=456>Preset Manager", "Select", font, 2, 0xFF981F, true, false, 130, 10);
        cache[23450].optionType = 0;
        cache[23450].actions = new String[]{"Open", "Load Last"};

        infoTab.totalChildren(7);
        setBounds(23360, 0, 24, frame++, infoTab);
        setBounds(23350, 0, 0, frame++, infoTab);
        setBounds(23366, 8, 3, frame++, infoTab);
        setBounds(23370, 0, 24, frame++, infoTab);
        addSprite(23399, MEDIUM_BACK_GROUND_BORDER);
        setBounds(23399, 0, 24, frame++, infoTab);
        setBounds(23361, 15, 222, frame++, infoTab);
        setBounds(23450, 20, 229, frame++, infoTab);
        final Widget infoTabScrollbar = createWidget(23370, 174, 194);

        frame = 0;

        infoTabScrollbar.scrollMax = 420;

        infoTabScrollbar.totalChildren(32);
        addCustomClickableText(23412, "", "Set", font, 0, 0x00E500, true, false, 60, 10);
        addCustomClickableText(23455, "OFF", "Select", font, 0, 0x00E500, false, true, 20, 10);
        setBounds(23412, 96, 175, frame++, infoTabScrollbar);
        setBounds(23455, 71, 204, frame++, infoTabScrollbar);

        int childId = 23371;
        int startX;
        int startY = 7;
        int colour;
        boolean centre;
        for (int buttonIndex = 0; buttonIndex < 30; buttonIndex++) {
            if (buttonIndex == 0 || buttonIndex == 6 || buttonIndex == 19) {
                startX = 85;
                colour = 0xFFDE00; // title colour
                centre = true;
            } else {
                startX = 10;
                colour = 0xcc8400;
                centre = false;
            }
            if(buttonIndex == 15) {
                addCustomClickableText(childId, "", "Select", font, 0, colour, centre, true, 157, 7);
            } else if (buttonIndex == 23) {
                addCustomClickableText(childId, "", "Exchange for tokens", font, 0, colour, centre, true, 157, 7);
            } else {
                addText(childId, "", colour, centre, true, -1, font, 0);
            }

            setBounds(childId, startX, startY, frame++, infoTabScrollbar);
            startY += centre ? 14 : 14;
            childId++;
        }

        //System.out.println(childId);

        Widget quickActions = addTabInterface(23414);

        frame = 0;

        addText(23415, "Quick Actions", 0xFF981F, false, true, 52, font, 2);

        quickActions.totalChildren(5);

        addSprite(23335, LARGE_BACK_GROUND_SPRITE);
        setBounds(23335, 0, 24, frame++, quickActions);

        setBounds(23350, 0, 0, frame++, quickActions);
        setBounds(23415, 8, 3, frame++, quickActions);
        setBounds(23416, 0, 24, frame++, quickActions);
        addSprite(23334, LARGE_BACK_GROUND_BORDER);
        setBounds(23334, 0, 24, frame++, quickActions);

        Widget quickActionsScrollbar = createWidget(23416, 174, 218);

        frame = 0;

        quickActionsScrollbar.scrollMax = 285;

        final String[] lines = {"General", "Visit Website", "Vote for "+ClientConstants.CLIENT_NAME, ClientConstants.CLIENT_NAME+" Store", "Join Discord", "",
            "Player Tools", "Open NPC Drop Table", "Open Collection Log", "Open Boss Kill Log", "Open Slayer Kill Log", "PvP Leaderboards", "",
            "Instant Supplies", "Vengeance Runes", "Barrage Runes", "Teleblock Runes", "Potion set", "Food",
            "", ""};

        quickActionsScrollbar.totalChildren(21);

        childId = 23417;
        startY = 10;
        int spriteIndex = 0;
        for (int id = 0; id < 21; id++) {

            if (id == 0 || id == 6 || id == 13) {
                startX = 8;
                colour = 0xFFDE00;
            } else {
                startX = 17;
                colour = 0xcc8400;
            }

            switch (id) {
                case 0:
                    spriteIndex = 452;
                    break;
                case 1:
                    spriteIndex = 453;
                    break;
                case 2:
                    spriteIndex = 463;
                    break;
                case 3:
                    spriteIndex = 454;
                    break;
                case 4:
                    spriteIndex = 455;
                    break;
                case 6:
                    spriteIndex = 456;
                    break;
                case 7:
                    spriteIndex = 464;
                    break;
                case 8:
                case 17:
                    spriteIndex = 457;
                    break;
                case 9:
                    spriteIndex = 458;
                    break;
                case 10:
                    spriteIndex = 459;
                    break;
                case 11:
                    spriteIndex = 460;
                    break;
                case 13:
                    spriteIndex = 461;
                    break;
                case 14:
                    spriteIndex = 465;
                    break;
                case 15:
                    spriteIndex = 466;
                    break;
                case 16:
                    spriteIndex = 467;
                    break;
                case 18:
                    spriteIndex = 462;
                    break;
            }

            if (id == 0 || id == 5 || id == 12 || id == 6 || id == 13) {
                startX = startX + 30;
                addText(id == 0 ? 17850 : childId, "<img=" + spriteIndex++ + ">" + lines[id], colour, false, true, -1, font, 0);
            } else {
                startX = startX - 7;
                addCustomClickableText(id == 0 ? 17850 : childId, lines[id].equalsIgnoreCase("") ? "" : lines[id], "Select", font, 0, colour, true, false, 150, 10);
            }

            setBounds(id == 0 ? 17850 : childId, startX, startY, frame++, quickActionsScrollbar);
            startY += 14;
            if (id != 0)
                childId++;
        }

        quickActions = addTabInterface(childId);
        quickActions.totalChildren(6);
    }
}
