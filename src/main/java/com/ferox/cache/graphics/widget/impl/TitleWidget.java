package com.ferox.cache.graphics.widget.impl;

import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.Widget;

/**
 * @author Patrick van Elderen | December, 01, 2020, 11:35
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class TitleWidget extends Widget {

    public static void unpack(AdvancedFont[] font) {
        playerTitleInterface(font);
    }

    private static void playerTitleInterface(AdvancedFont[] font) {
        Widget widget = addInterface(61380);
        widget.totalChildren(2);
        addHDSpriteLoader(61383, 1381);
        setBounds(61383, 12, 13, 0, widget);
        setBounds(61390, 0, 0, 1, widget);


        widget = addInterface(61381);
        widget.totalChildren(2);
        addHDSpriteLoader(61384, 1382);
        setBounds(61384, 12, 13, 0, widget);
        setBounds(61390, 0, 0, 1, widget);


        widget = addInterface(61382);
        widget.totalChildren(2);
        addHDSpriteLoader(61385, 1383);
        setBounds(61385, 12, 13, 0, widget);
        setBounds(61390, 0, 0, 1, widget);


        /*
         * This is the main interface id, this is bascically all the buttons, scroll,s, text etx
         */
        int id = 61390;
        int frame = 0;

        widget = addInterface(id);
        widget.totalChildren(20);
        id++;


        addButton(id, 0, "", 83, 20, "Select <col=65280>PKing", 1);
        setBounds(id, 40, 51, frame, widget);
        frame++;
        id++;

        addButton(id, 0, "", 83, 20, "Select <col=65280>PvM", 1);
        setBounds(id, 126, 51, frame, widget);
        frame++;
        id++;

        addButton(id, 0, "", 83, 20, "Select <col=65280>Other", 1);
        setBounds(id, 212, 51, frame, widget);
        frame++;
        id++;
        widget.child(frame++, 55152, 476, 21);
        addText(id, "Player Titles", font, 2, 0xFF981F, true, true);
        setBounds(id, 260, 21, frame, widget);
        frame++;
        id++;

        addText(id, "Title preview:", font, 2, 0xFFFF00, true, true);
        setBounds(id, 163, 255, frame, widget);
        frame++;
        id++;

        addText(id, "The wilderfire", font, 2, 0xFF981F, true, true);
        setBounds(id, 163, 276, frame, widget);
        frame++;
        id++;

        addText(id, "Customise Your Title:", font, 2, 0xFF981F, true, true);
        setBounds(id, 170, 85, frame, widget);
        frame++;
        id++;

        addText(id, "Title Requirements:", font, 2, 0xFF981F, true, true);
        setBounds(id, 400, 85, frame, widget);
        frame++;
        id++;

        addText(id, "Some titles require<br>requirements to be met.<br>However, some you can<br>simply purchase.", font, 1,
            0xffffff, true, true);
        setBounds(id, 400, 110, frame, widget);
        frame++;
        id += 3;

        addHDHoverButton_sprite_loader(id, 1385, 72, 19, "Set Title", -1, id + 1, 1);
        setBounds(id, 361 - 36, 277, frame, widget);
        frame++;

        addHDHoveredButton_sprite_loader(id + 1, 1384, 72, 19, id + 2);
        setBounds(id + 1, 361 - 36, 277, frame, widget);
        frame++;
        id += 100;

        addHDHoverButton_sprite_loader(id, 1385, 72, 19, "Clear Title", -1, id + 1, 1);
        setBounds(id, 437 - 35, 277, frame, widget);
        frame++;

        addHDHoveredButton_sprite_loader(id + 1, 1384, 72, 19, id + 2);
        setBounds(id + 1, 437 - 35, 277, frame, widget);
        frame++;
        id += 100;

        addText(id, "Set Title", font, 1, 0xFF981F, true, true);
        setBounds(id, 361, 279, frame, widget);
        frame++;
        id++;

        addText(id, "Clear Title", font, 1, 0xFF981F, true, true);
        setBounds(id, 437, 279, frame, widget);
        frame++;
        id++;

        addText(id, "Select Colour:", font, 2, 0xFFFF00, true, true);
        setBounds(id, 100, 112, frame, widget);
        frame++;
        id++;

        addText(id, "Select Title:", font, 2, 0xFFFF00, true, true);
        setBounds(id, 220, 112, frame, widget);
        frame++;
        id++;

        setBounds(id, 32, 131, frame, widget);
        frame++;

        Widget scroll = addInterface(id);
        id++;
        scroll.totalChildren(30);
        scroll.height = 95;
        scroll.width = 117;
        scroll.scrollMax = 470;
        id++;

        int boss_x = 0;
        int boss_y = 0;

        for (int i = 0; i < 30; i++) {
            addHoverClickText(id, "", "Select title", font, 0, 0xEE9021, true, true, 120);
            setBounds(id, boss_x, boss_y, i, scroll);
            id++;

            boss_y += 15;
        }
        id++;

        setBounds(id, 162, 131, frame, widget);
        frame++;

        scroll = addInterface(id);
        id++;
        scroll.totalChildren(30);
        scroll.height = 95;
        scroll.width = 122;
        scroll.scrollMax = 470;
        id++;

        boss_x = 0;
        boss_y = 0;

        for (int i = 0; i < 30; i++) {
            addHoverClickText(id, "", "Select title", font, 0, 0xEE9021, true, true, 120);
            setBounds(id, boss_x, boss_y, i, scroll);
            id++;

            boss_y += 15;
        }
        id++;

    }
}
