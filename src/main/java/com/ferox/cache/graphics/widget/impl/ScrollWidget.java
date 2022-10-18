package com.ferox.cache.graphics.widget.impl;

import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.Widget;

/**
 * @author Patrick van Elderen | December, 10, 2020, 10:57
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class ScrollWidget extends Widget {

    public static void unpack(AdvancedFont[] font) {
        Widget widget = addInterface(21400);
        widget.centerText = true;
        addSprite(21401, 1410);
        addSprite(21402, 1411);
        addText(21403, "Title", 0, true, false, 52, font, 3);
        closeButton(21404, 26, 27, false);

        setChildren(5, widget);
        setBounds(21401, 18, 62, 0, widget);
        setBounds(21402, 18, 4, 1, widget);
        setBounds(21403, 260, 15, 2, widget);
        setBounds(21404, 452, 63, 3, widget);
        setBounds(21407, 50, 86, 4, widget);

        final Widget scroll = addInterface(21407);

        final int totalLines = 201;
        setChildren(totalLines, scroll);

        scroll.height = 217;
        scroll.width = 404;
        scroll.scrollMax = totalLines * 20 + 20;
        scroll.newScroller = true;

        int index = 21408;
        int child = 0;
        int y = 18;
        for (int i = 0; i < totalLines; i++) {
            addText(index, "Test " + i, 128, true, false, 52, font, 1);
            setBounds(index++, 202, y, child++, scroll);
            y += 19;
            y++;
        }
    }
}
