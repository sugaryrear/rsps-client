package com.ferox.cache.graphics.widget.impl;

import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.Widget;

public class WildernessWidget extends Widget {

    public static void unpack(AdvancedFont[] font) {
        Widget widget = addInterface(53720);
        addTransparentSprite(53721, 340, 150);
        addText(53722, "Earning Potential: <col=65280>0", font, 0, 0xffffff, true, true);
        addText(53723, "Target: <col=65280>None", font, 0, 0xffffff, true, true);
        addText(53724, "Wilderness Level: <col=65280>1", font, 0, 0xffffff, true, true);
        widget.totalChildren(5);
        widget.child(0, 53721, 383, 4);
        widget.child(1, 53722, 446, 13);
        widget.child(2, 53723, 446, 27);
        widget.child(3, 53724, 446, 41);
        //Skull icon
        //Be careful with positioning the skull icon especially the y position,
        //we have hardcoded checks for position for this interface and
        //moving the skull icon breaks the checks
        widget.child(4, 197, -30, 2);
    }
}
