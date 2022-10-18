package com.ferox.cache.graphics.widget.impl;

import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.Widget;

/**
 * @author Zerikoth
 * @Since september 21, 2020
 */
public class TaskWidget extends Widget {

    public static void unpack(AdvancedFont[] font) {
        Widget widget = addInterface(54731);
        addSprite(54732, 1421);
        addText(54733, "Task Scroll Title", font, 2, 16751360, true, true);
        addText(54734, "Task Information", font, 2, 16751360, false, true);
        addText(54735, "Potential Rewards", font, 2, 16751360, false, true);

        Widget scroll = addInterface(54737);
        scroll.scrollMax = 350;
        scroll.width = 173;
        scroll.height = 135;
        scroll.totalChildren(20);
        int x = 5;
        int y = 5;
        int offset = 15;
        for (int index = 0; index < 20; index++) {
            addText(54738 + index, "Description " + (index + 1) + " (" + (54738 + index) + ")", font, 0, 16777215, false, false);
            scroll.children(index, 54738 + index, x, y + offset * index);
        }

        Widget itemContainer = addInterface(54758);
        itemContainer.scrollMax = 450;
        itemContainer.width = 161;
        itemContainer.height = 126;
        itemContainer.totalChildren(1);
        addContainer(54759, TYPE_CONTAINER, 4, 10, 7, 8, 0, false, true, true);

        itemContainer.children(0, 54759, 0, 0);
        drawProgressBar(54760, 285, 15, 0);
        addText(54762, "0% (0/100)", font, 0, 0, true, false);

        addHoverButton(54763, 1238, 70, 13, "Collect reward", -1, 54764, 1);
        addHoveredButton(54764, 1239, 70, 13, 54765);

        addText(54766, "<col=ffff00>Collect", font, 0, 0, true, true);
        closeButton(54767, 107, 108, false);

        widget.totalChildren(12);
        widget.child(0, 54732, 20, 26);
        widget.child(1, 54733, 263, 35);
        widget.child(2, 54734, 100, 75);
        widget.child(3, 54735, 310, 75);
        widget.child(4, 54737, 40, 105);
        widget.child(5, 54758, 275, 110);
        widget.child(6, 54760, 135, 262);
        widget.child(7, 54762, 267, 264);
        widget.child(8, 54763, 225, 280);
        widget.child(9, 54764, 225, 280);
        widget.child(10, 54766, 253, 281);
        widget.child(11, 54767, 463, 33);

    }
}
