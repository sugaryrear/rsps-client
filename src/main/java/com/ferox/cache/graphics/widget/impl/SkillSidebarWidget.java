package com.ferox.cache.graphics.widget.impl;

import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.Widget;

public class SkillSidebarWidget extends Widget {

    public static void unpack(AdvancedFont[] font) {
        skillsTab(font);
    }
    
    private static final String[] names = { "Attack", "Hitpoints", "Mining", "Strength", "Agility", "Smithing", "Defence",
            "Herblore", "Fishing", "Ranged", "Thieving", "Cooking", "Prayer", "Crafting", "Firemaking", "Magic",
            "Fletching", "Woodcutting", "Runecraft", "Slayer", "Farming", "Construction", "Hunter" };
    
    private static int correctPositions1(int id) {
        switch (id) {
        case 0:
            return 3;
        case 3:
            return 3;
        case 2:
            return 5;
        case 4:
            return 4;
        case 5:
            return 5;
        case 6:
        case 21:
        case 22:
            return 3;
        case 10:
        case 17:
            return 5;
        case 11:
            return 6;
        case 13:
        case 14:
        case 16:
            return 5;
        }
        return 4;
    }

    private static void skillsTab(AdvancedFont[] font) {
        int[] firstRow = { -1,

                /** First row (enx. index 9) **/
                14918, 14919, 14920, 14921, 14922, 14923, 14924, 14925

        };
        int[] secondRow = {

                /** Second row (enx. index 8) **/
                14926, 14927, 14928, 14929, 14930, 14931, 14932, 14933

        };
        int[] thirdRow = {

                /** Third row (enx. index 8) **/
                14934, 14935, 14936, 14937, 14938, 14939, 14940

        };
        Widget widget = addTabInterface(10000);

        int skillTabChild = 0;
        int x = 1;
        int y = 1;
        widget.totalChildren(119);

        for (int i = 0; i < 24; i++) {
            if (i <= 22) {
                addButton(10001 + i, 10000, 61, 32, 775, 775, 10151 + i, "View <col=ff9040>" + names[i] + " <col=ffffff>guide");
                addSprite(10031 + i, 777 + i);

            } else if (i >= 23) {
                addSprite(10001 + i, 776);
                addText(10120, "Total level:", font, 0, 0xffff00, true, true);
                addText(10121, "", font, 0, 0xffff00, true, true);
            }
            if (x < 180) {
                widget.child(skillTabChild++, 10001 + i, x, y);
                if (i < 23) {
                    widget.child(skillTabChild++, 10031 + i, x + correctPositions1(i), y + 4);
                }
            } else {
                x = 1;
                y += 32;
                widget.child(skillTabChild++, 10001 + i, x, y);
                if (i < 23) {
                    widget.child(skillTabChild++, 10031 + i, x + correctPositions1(i), y + 4);
                }
            }
            x += 63;
        }
        widget.child(skillTabChild++, 10120, 158, 231);
        widget.child(skillTabChild++, 10121, 158, 241);
        widget.child(skillTabChild++, 4004, 33, 5);
        widget.child(skillTabChild++, 4005, 45, 17);
        widget.child(skillTabChild++, 4006, 33, 5 + 32);
        widget.child(skillTabChild++, 4007, 45, 17 + 32);
        widget.child(skillTabChild++, 4008, 33, 5 + 64);
        widget.child(skillTabChild++, 4009, 45, 17 + 64);
        widget.child(skillTabChild++, 4010, 33, 5 + 96);
        widget.child(skillTabChild++, 4011, 45, 17 + 96);
        widget.child(skillTabChild++, 4012, 33, 5 + 128);
        widget.child(skillTabChild++, 4013, 45, 17 + 128);
        widget.child(skillTabChild++, 4014, 33, 5 + 160);
        widget.child(skillTabChild++, 4015, 45, 17 + 160);
        widget.child(skillTabChild++, 4016, 96, 5);
        widget.child(skillTabChild++, 4017, 108, 17);
        widget.child(skillTabChild++, 4018, 96, 5 + 32);
        widget.child(skillTabChild++, 4019, 108, 17 + 32);
        widget.child(skillTabChild++, 4020, 96, 5 + 64);
        widget.child(skillTabChild++, 4021, 108, 17 + 64);
        widget.child(skillTabChild++, 4022, 96, 5 + 96);
        widget.child(skillTabChild++, 4023, 108, 17 + 96);
        widget.child(skillTabChild++, 4024, 96, 5 + 128);
        widget.child(skillTabChild++, 4025, 108, 17 + 128);
        widget.child(skillTabChild++, 4026, 96, 5 + 160);
        widget.child(skillTabChild++, 4027, 108, 17 + 160);
        widget.child(skillTabChild++, 4028, 159, 5);
        widget.child(skillTabChild++, 4029, 171, 17);
        widget.child(skillTabChild++, 4030, 159, 5 + 32);
        widget.child(skillTabChild++, 4031, 171, 17 + 32);
        widget.child(skillTabChild++, 4032, 159, 5 + 64);
        widget.child(skillTabChild++, 4033, 171, 17 + 64);
        widget.child(skillTabChild++, 4034, 159, 5 + 96);
        widget.child(skillTabChild++, 4035, 171, 17 + 96);
        widget.child(skillTabChild++, 4036, 159, 5 + 128);
        widget.child(skillTabChild++, 4037, 171, 17 + 128);
        widget.child(skillTabChild++, 4038, 159, 5 + 160);
        widget.child(skillTabChild++, 4039, 171, 17 + 160);
        widget.child(skillTabChild++, 4152, 33, 5 + 192);
        widget.child(skillTabChild++, 4153, 45, 17 + 192);
        widget.child(skillTabChild++, 12166, 96, 5 + 192);
        widget.child(skillTabChild++, 12167, 108, 17 + 192);
        widget.child(skillTabChild++, 13926, 159, 5 + 192);
        widget.child(skillTabChild++, 13927, 171, 17 + 192);
        widget.child(skillTabChild++, 18799, 33, 5 + 224);
        widget.child(skillTabChild++, 18800, 45, 17 + 224);
        widget.child(skillTabChild++, 18797, 96, 5 + 224);
        widget.child(skillTabChild++, 18798, 108, 17 + 224);
        for (int i = 1; i < firstRow.length; i++) {
            createSkillHover(firstRow[i], 205 + i, 62);
            setBounds(firstRow[i], 0, 2 + (32 * i) - 33, skillTabChild++, widget);
        }
        for (int i = 0; i < secondRow.length; i++) {
            createSkillHover(secondRow[i], 214 + i, 61);
            setBounds(secondRow[i], 64, 2 + (32 * i) - 1, skillTabChild++, widget);
        }
        for (int i = 0; i < thirdRow.length; i++) {
            createSkillHover(thirdRow[i], 223 + i, 61);
            setBounds(thirdRow[i], 127, 2 + (32 * i) - 1, skillTabChild++, widget);
        }
        createSkillHover(14941, 831, 62);
        setBounds(14941, 127, 225, skillTabChild++, widget);
    }
}
