package com.ferox.cache.graphics.widget.impl;

import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.Widget;

public class TournamentWidget extends Widget {

    public static void unpack(AdvancedFont[] font) {
        tournament(font);
        tournamentWalk(font);
    }

    private static void tournamentWalk(AdvancedFont[] font) {
        Widget widget = addInterface(21100);
        widget.totalChildren(2);

        addOutlinedColorBox(21101, 0x534a40, 100, 40, 100);
        addText(21102, "<col=ffffff>", font, 1, 16750623, true);

        setBounds(21101, 240, 20, 0, widget);
        setBounds(21102, 290, 33, 1, widget);
    }

    private static void tournament(AdvancedFont[] font) {
        Widget widget = addInterface(19999);
        addSprite(20000, 1420);
        addText(20002, "Next world tournament: <col=ffff00>Dharok PK Tournament", font, 2, 16750623, true);
        addText(20003, "Tournament time:", font, 0, 16750623, true);
        addText(20004, "This tournament's prize will be..", font, 0, 16750623, false);
        addText(20005, "<col=ffff00>Previous Tournament Winners", font, 1, 16750623, true);
        addItem(20007,true,false);
        cache[20007].width = 4;
        cache[20007].inventoryMarginX = 11;
        cache[20007].inventoryMarginY = 10;
        cache[20007].inventoryItemId[0] = 13191;
        cache[20007].inventoryAmounts[0] = 1;
        Widget scroll = addTabInterface(20008);
        scroll.scrollMax = 475;
        scroll.width = 255;
        scroll.height = 123;
        scroll.hoverType = 87;
        scroll.totalChildren(35);
        int tick = 0;
        for (int index = 0; index < 35; index++) {
            if (tick == 2) {
                addText(20009 + index, "", font, 0, 16750623, true);
                tick = 0;
            } else if (tick == 0) {
                addText(20009 + index, "Patrick won <col=ffff00>x1 $10.00 bond", font, 0, 16750623, true);
                tick++;
            } else {
                addText(20009 + index, "from <col=ff7000>Dharok PK Tournament <col=ffff00>" + (index + 1) * 6 + " hours ago", font, 0, 16750623, true);
                tick++;
            }
            scroll.child(index, 20009 + index, 131, 6 + index * 13);
        }
        addCustomClickableText(20047, "Enter Tournament", "Enter Tournament", font, 0, 16750623, true, true, 100, 10);
        addCustomClickableText(20052, "Spectate Tournament", "Spectate Tournament", font, 0, 16750623, true, true, 100, 10);
        closeButton(20058, 142, 143, false);
        widget.totalChildren(10);
        widget.child(0, 20000, 7, 15);
        widget.child(1, 20002, 259, 25);
        widget.child(2, 20003, 135, 75);
        widget.child(3, 20004, 48, 90);
        widget.child(4, 20005, 170, 125);
        widget.child(5, 20007, 284, 73);
        widget.child(6, 20008, 50, 150);
        widget.child(7, 20058, 450, 22);
        widget.child(8, 20047, 355, 73);
        widget.child(9, 20052, 353, 117);
    }
}
