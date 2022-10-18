package com.ferox.cache.graphics.widget.impl;

import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.Widget;
import com.ferox.model.content.Keybinding;

/**
 * @author Patrick van Elderen <https://github.com/PVE95>
 * @Since October 14, 2021
 */
public class EventWidget extends Widget {

    public static void unpack(AdvancedFont[] font) {
        Widget widget = addTabInterface(73300);
        addSprite(73301, 1864);
        closeButton(73302, 107, 108, false);
        addText(73304, "Halloween event 2021", font, 2, 0xF7AA25, true, true);
        addText(73305, "Completion Prize", font, 2, 0xff9040, true, true);

        hoverButton(73307, "Roll", 1867, 1868, "Roll", font, 1, 0xff8a1f, 0xff8a1f, true);
        hoverButton(73310, "Reset", 1856, 1857, "Reset", font, 1, 0xff8a1f, 0xff8a1f, true);

        drawProgressBar(73313, 200, 20, 40);
        addText(73314, "Tokens spent: 0", font, 1, 0xff9040, false, true);

        itemGroup(73315, 1, 1, 5, 3);
        itemGroup(73316, 1, 1, 5, 3);
        itemGroup(73317, 1, 1, 5, 3);

        addContainer(73318, TYPE_CONTAINER, 11, 4, 10, 5, 100, false, false, true);

        widget.totalChildren(12);
        widget.child(0, 73301, 14, 18);
        widget.child(1, 73302, 475, 25);
        widget.child(2, 73304, 260, 27);
        widget.child(3, 73305, 123, 233);
        widget.child(4, 73307, 195, 238);
        widget.child(5, 73310, 40, 280);
        widget.child(6, 73313, 225, 290);
        widget.child(7, 73314, 270, 293);
        widget.child(8, 73315, 32, 235);
        widget.child(9, 73316, 275, 241);
        widget.child(10, 73317, 398, 238);
        widget.child(11, 73318, 25, 60);
    }
}
