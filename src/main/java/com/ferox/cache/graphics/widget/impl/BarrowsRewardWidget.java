package com.ferox.cache.graphics.widget.impl;

import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.Widget;

public class BarrowsRewardWidget extends Widget {

    public static void unpack(AdvancedFont[] font) {
        final Widget widget = addInterface(25400);

        final int startX = 117;
        final int startY = 67;

        setChildren(4, widget);

        addSpriteLoader(25400 + 1, 487);
        setBounds(25400 + 1, startX, startY, 0, widget);

        closeButton(25400 + 2, 142, 143, false);

        setBounds(25400 + 2, startX + 252, startY + 7, 1, widget);

        addText(25400 + 5, "Barrows Chest", 0xFF981F, true, true, 52, font, 2);
        setBounds(25400 + 5, startX + 140, startY + 11, 2, widget);

        addContainer(25400 + 6, TYPE_CONTAINER, 15, 15, 10, 8, 0, false, true, true);
        setBounds(25400 + 6, startX + 100, startY + 48, 3, widget);
    }

}
