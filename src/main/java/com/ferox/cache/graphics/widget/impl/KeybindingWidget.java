package com.ferox.cache.graphics.widget.impl;

import com.ferox.Client;
import com.ferox.cache.graphics.SimpleImage;
import com.ferox.cache.graphics.dropdown.Dropdown;
import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.Widget;
import com.ferox.model.content.Keybinding;

public class KeybindingWidget extends Widget {

    public static void unpack(AdvancedFont[] font) {
        Widget tab = addTabInterface(53000);

        addSpriteLoader(53001, 430);
        //addText(53002, "", font, 2, 0xff8a1f, false, true);
        closeButton(53006, 142, 143, false);

        hoverButton(Keybinding.RESTORE_DEFAULT, "Restore Defaults", 448, 447, "Restore Defaults", font, 1, 0xff8a1f, 0xff8a1f, true);

        addText(53002, "Esc closes current interface", font, 1, 0xff8a1f, false, true);
        addConfigButton(Keybinding.ESCAPE_CONFIG, 53000, 334, 333, "Select", 594, 0, OPTION_TOGGLE_SETTING);

        tab.totalChildren(50);
        int childNum = 0;

        setBounds(53001, 5, 7, childNum++, tab);
        //setBounds(53002, 221, 27, childNum++, tab);
        setBounds(53006, 479, 14, childNum++, tab);
        setBounds(Keybinding.RESTORE_DEFAULT, 343, 275, childNum++, tab);
        setBounds(53002, 60, 285, childNum++, tab);
        setBounds(Keybinding.ESCAPE_CONFIG, 35, 285, childNum++, tab);

        /* Tabs and dropdowns */

        int x = 31;
        int y = 63;
        childNum = 49;
        for (int i = 0; i < 15; i++, y += 43) {
            //replace quest tab icon with spawn tab icon, replace music tab icon with pvp icon.
            if (i == 2) {
                addSpriteLoader(53007 + 3 * i, 645);
            } else if (i == 13) {
                addSpriteLoader(53007 + 3 * i, 336);
            } else if (i == 14) {
                addSpriteLoader(53007 + 3 * i, 1091);
            } else {
                addSpriteLoader(53007 + 3 * i, 431 + i);
            }

            configButton(53008 + 3 * i, "", 446, 445);

            boolean inverted = i == 3 || i == 4 || i == 8 || i == 9 || i == 13 || i == 14;
            keybindingDropdown(53009 + 3 * i, 86, 0, Keybinding.OPTIONS, Dropdown.KEYBIND_SELECTION, inverted);
            if (i == 2) {
                //System.out.println("Childnum before is: " + childNum);
                setBounds(Keybinding.MIN_FRAME - 2 + 3 * i, x + stoneOffset(431 + i, true) + 3, y + stoneOffset(431 + i, false) + 2, childNum--, tab);
                 //System.out.println("Childnum after is: " + childNum);
            } else {
                //System.out.println("Childnum before is: " + childNum);
                setBounds(Keybinding.MIN_FRAME - 2 + 3 * i, x + stoneOffset(431 + i, true), y + stoneOffset(431 + i, false), childNum--, tab);
                //System.out.println("Childnum after is: " + childNum);
            }
            setBounds(Keybinding.MIN_FRAME - 1 + 3 * i, x, y, childNum--, tab);
            setBounds(Keybinding.MIN_FRAME + 3 * i, x + 39, y + 4, childNum--, tab);

            if (i == 4 || i == 9) {
                x += 160;
                y = 20;
            }
        }
    }
    public static int stoneOffset(int spriteId, boolean xOffset) {
        SimpleImage stone = Client.spriteCache.get(445);
        SimpleImage icon = Client.spriteCache.get(spriteId);
        if (xOffset) {
            return (stone.width / 2) - icon.width / 2;
        }
        return (stone.height / 2) - icon.height / 2;
    }

}
