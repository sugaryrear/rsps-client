package com.ferox.cache.graphics.widget.impl;

import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.Widget;

/**
 * @author Patrick van Elderen | February, 14, 2021, 23:05
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class ItemSimulationWidget extends Widget {

    public static void unpack(AdvancedFont[] font) {
        final Widget widget = addTabInterface(27200);
        Widget list = addInterface(27300);
        setChildren(1, list);

        addContainer(27201, 10, 100, 10, 10, false);
        setBounds(27201, 10, 5, 0, list);
        list.height = 273;
        list.width = 428;
        list.scrollMax = 500;

        addText(27202, "Drop Simulator", 0xFF981F, true, true, 52, font, 2);
        addText(27203, "Showing ...", 0xFF981F, true, true, 52, font, 1);

        addSpriteLoader(27204, 538);

        closeButton(27205, 142, 143, false);

        setChildren(5, widget);
        setBounds(27204, 30, 5, 0, widget);
        setBounds(27300, 38, 53, 1, widget);
        setBounds(27202, 259, 13, 2, widget);
        setBounds(27203, 259, 26, 3, widget);
        setBounds(27205, 461, 14, 4, widget);
    }
}
