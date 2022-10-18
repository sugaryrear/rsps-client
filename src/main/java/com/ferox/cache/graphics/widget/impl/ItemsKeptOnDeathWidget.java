package com.ferox.cache.graphics.widget.impl;

import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.Widget;

public class ItemsKeptOnDeathWidget extends Widget {

    public static void unpack(AdvancedFont[] font) {
        Widget widget = addInterface(17100);
        addSpriteLoader(17101, 139);
        addText(17102, "Items Kept on Death", font, 2, 0xff981f, false, false);
        addText(17103, "Items I will keep on death:", font, 1, 0xff981f, false, false);
        addText(17104, "Items I will auto keep on death:", font, 1, 0xff981f, false, false);
        addText(17105, "Items I will lose on death:", font, 1, 0xff981f, false, false);
        addText(17106, "Info", font, 1, 0xff981f, false, false);
        closeButton(17107, 107, 108, false);

        Widget scroll = addTabInterface(17108);
        scroll.width = 298;
        scroll.height = 57;
        scroll.scrollMax = 400;
        addContainer(17110, TYPE_CONTAINER, 7, 15, 3, 5, 0, false, true, true);
        scroll.totalChildren(1);
        scroll.child(0, 17110, 1, 1);

        addContainer(17109, TYPE_CONTAINER, 7, 15, 3, 8, 0, false, true, true);
        addContainer(17111, TYPE_CONTAINER, 9, 15, 3, 0, 0, false, true, true);

        addText(17112, "At the moment nearly all<br>items are tradeable and<br>can be lost on death.<br><br>If you are red-skulled,<br>you will lose all your items<br>and pets regardless of<br>what you are praying!", font, 1, 0xff981f, false, false);

        widget.totalChildren(11);
        widget.child(0, 17101, 7,8);
        widget.child(1, 17102, 185, 18);
        widget.child(2, 17103, 22, 50);
        widget.child(3, 17104, 22, 110);
        widget.child(4, 17105, 22,170);
        widget.child(5, 17106, 347, 50);
        widget.child(6, 17107, 477, 15);
        widget.child(7, 17108, 15, 125);
        widget.child(8, 17109, 22, 70);
        widget.child(9, 17111, 22,190);
        widget.child(10, 17112, 347, 75);
    }

}
