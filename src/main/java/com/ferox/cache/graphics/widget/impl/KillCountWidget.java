package com.ferox.cache.graphics.widget.impl;

import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.Widget;

public class KillCountWidget extends Widget {

    public static void unpack(AdvancedFont[] font) {
        Widget widget = addTabInterface(46300);
        addSprite(46301, 927);
        addText(46304, "Slayer Kill Log", font, 2, 0xFE9624);
        addText(46305, "Monster", font, 2, 0xFE9624);
        addText(46306, "Kills", font, 2, 0xFE9624);
        addText(46307, "Streak", font, 2, 0xFE9624);

        Widget scroll_widget = addTabInterface(46310);
        scroll_widget.scrollPosition = 0;
        scroll_widget.contentType = 0;
        scroll_widget.width = 439;
        scroll_widget.height = 216;
        scroll_widget.scrollMax = 590;
        int y = 2;
        final int CHILD_LENGTH = 75 * 6;
        int child = 0;
        scroll_widget.totalChildren(CHILD_LENGTH);
        for (int i = 46320; i < 46320 + CHILD_LENGTH; i+= 6) {
            addRectangle(i, (byte) 0, i % 12 == 0 ? 0x493e34 : 0x4d4539, true, 443, 18);
            addText(i + 1, "", font , 1, 16750623);
            addText(i + 2, "", font , 1, 16750623);
            Widget.cache[i + 2].rightText = true;
            addText(i + 3, "", font , 1, 16750623);
            Widget.cache[i + 3].rightText = true;
            addHoverButton(i + 4, 1085, 21, 21, "Reset streak", -1, i + 5, 1);
            addHoveredButton(i + 5, 1086, 21, 21, 46314);

            scroll_widget.child(child++, i, 0, y);
            scroll_widget.child(child++, i + 1, 0, y);
            scroll_widget.child(child++, i + 2, 310, y);
            scroll_widget.child(child++, i + 3, 400, y);
            scroll_widget.child(child++, i + 4, 410, y);
            scroll_widget.child(child++, i + 5, 410, y);
            y+=18;
        }
        closeButton(46302, 24, 25, false);
        addRectangle(46311, (byte) 0, 0x000000, true, 443, 1);
        addRectangle(46312, (byte) 0, 0x000000, true, 1, 241);
        addRectangle(46313, (byte) 0, 0x000000, true, 1, 241);
        widget.totalChildren(10);
        widget.child(0, 46301, 20, 22);
        widget.child(1, 46304, 215, 32);
        widget.child(2, 46305, 30, 65);
        widget.child(3, 46306, 307, 65);
        widget.child(4, 46307, 380, 65);
        widget.child(5, 46310, 30, 81);
        widget.child(6, 46311, 30, 81);
        widget.child(7, 46312, 240, 56);
        widget.child(8, 46313, 350, 56);
        widget.child(9, 46302, 470, 33);
    }
}
