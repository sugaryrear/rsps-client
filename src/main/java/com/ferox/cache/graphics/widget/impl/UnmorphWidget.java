package com.ferox.cache.graphics.widget.impl;

import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.Widget;

public class UnmorphWidget extends Widget {

    public static void unpack(AdvancedFont[] fonts) {
        Widget widget = addInterface(36666);
        addSprite(36667, 531);
        addHoverText(36668, "Unmorph", "Unmorph", fonts, 1, 0xffffff, true, true, 85, 18, 0xff0000);

        widget.totalChildren(2);
        widget.child(0, 36667, 55, 110);
        widget.child(1, 36668, 55, 112);
    }

}
