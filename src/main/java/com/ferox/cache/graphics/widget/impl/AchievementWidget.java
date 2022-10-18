package com.ferox.cache.graphics.widget.impl;

import com.ferox.ClientConstants;
import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.Widget;

/**
 * The class which represents functionality for the achievements interface.
 *
 * @author <a href="http://www.rune-server.ee/members/Zerikoth/">Zerikoth</a>
 */
public class AchievementWidget extends Widget {

    private static void achievementWidget(AdvancedFont[] font) {
        Widget widget = addTabInterface(39400);
        addSprite(39401, 1167);
        closeButton(39402, 24, 25, false);
        addText(39404, "Achievements Completed (0/50)", font, 2, 0xF7AA25, true, true);
        addText(39405, "Achievement name...", font, 2, 0xff9040, true, true);
        addText(39406, "Item rewards:", font, 1, 0xff9040, true, true);
        addText(39407, "Other rewards:", font, 1, 0xff9040, true, true);
        addConfigButton(39408, 1160, 1165, 1166, 115, 24, "Easy", 0, 5, 1160, false);
        addConfigButton(39409, 1161, 1165, 1166, 115, 24, "Med", 0, 5, 1161, false);
        addConfigButton(39410, 1162, 1165, 1166, 115, 24, "Hard", 0, 5, 1162, false);
        addText(39411, "Easy", font, 1, 0xffffff, true, true);
        addText(39412, "Med", font, 1, 0xffffff, true, true);
        addText(39413, "Hard", font, 1, 0xffffff, true, true);
        addContainer(39414, TYPE_CONTAINER, 3, 2, 10, 8, 0, false, true, true);
        drawProgressBar(39415, 295, 20, 40);
        addText(39416, "Progress: 40% (1/10000)", font, 1, 0xffffff, false, true);
        addText(39417, "Lorem ipsum dolor sit amet, consectetur<br>adipiscing elit, sed do eiusmod tempor incididunt<br>ut labore et dolore magna aliqua.", font, 0, 0xffffff, true, true);
        addText(39418, "- 1 Achievement point<br>    - 1000 "+ ClientConstants.CLIENT_NAME+" points", font, 0, 0xffffff, false, true);
        widget.totalChildren(18);
        widget.child(0, 39401, 5, 18);
        widget.child(1, 39402, 480, 27);
        widget.child(2, 39404, 315, 27);
        widget.child(3, 39405, 345, 72);
        widget.child(4, 39406, 398, 209);
        widget.child(5, 39407, 232, 209);
        widget.child(6, 39408, 11, 26);
        widget.child(7, 39409, 64, 26);
        widget.child(8, 39410, 117, 26);
        widget.child(9, 39411, 31, 29);
        widget.child(10, 39412, 84, 29);
        widget.child(11, 39413, 137, 29);
        widget.child(12, 39414, 365, 229);
        widget.child(13, 39415, 190, 165);
        widget.child(14, 39416, 275, 167);
        widget.child(15, 39417, 335, 110);
        widget.child(16, 39418, 190, 230);
        widget.child(17, 39430, 20, 53);

        Widget scrollInterface = addTabInterface(39430);
        scrollInterface.scrollPosition = 0;
        scrollInterface.contentType = 0;
        scrollInterface.width = 130;
        scrollInterface.height = 255;
        scrollInterface.scrollMax = 1800;
        int x = 5, y = 5;
        int amountOfLines = 100;
        scrollInterface.totalChildren(amountOfLines);
        for (int index = 0; index < amountOfLines; index++) {
            addText(39431 + index, "test", font, 1, 0xff0000, false, false, 0xffffff, "Select achievement", 150);
            textClicked(39431 + index, 1137, 1, 2);
            scrollInterface.child(index, 39431 + index, x, y);
            y += 18;
        }
    }

    public static void unpack(AdvancedFont[] font) {
        achievementWidget(font);
    }

}
