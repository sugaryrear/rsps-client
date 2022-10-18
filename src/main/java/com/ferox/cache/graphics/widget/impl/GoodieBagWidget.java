package com.ferox.cache.graphics.widget.impl;

import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.Widget;

public class GoodieBagWidget extends Widget {
    public static void unpack(AdvancedFont[] font) {

        Widget widget = addInterface(79500);
        addSprite(79501, 1869);
        closeButton(79502, 107, 108, true);
      //  addHoverButton(79505, 659, 103, 62, "Withdraw all", -1, 79506, 1);
        addHoveredButton(79506, 660, 103, 62,  79507);
hoverButton10(79505,"Give Out", 1870, 1871, "Give Out", font, 1, 0xdb9c22,0xdb9c22,true);
     //   addHoverButton(79509, 659, 103, 62, "Add all", -1, 79510, 1);
        addHoveredButton(79510, 660, 103, 62,  79511);
        hoverButton10(79509,"Withdraw All", 1870, 1871, "Withdraw All", font, 1, 0xdb9c22,0xdb9c22,true);
        addText(79511, "Goodie Bag", font, 2, 0xFF981F, true, true);
        addText(76022, "Withdraw All", font, 1, 0xdb9c22, true, true);
        addText(76023, "Give Out", font, 1, 0xdb9c22, true, true);

        Widget scroll = addInterface(79098);
        scroll.width = 305;
        scroll.height = 175;
        scroll.scrollMax = 750;
        scroll.totalChildren(1);
        addContainer(79542, 7, 4, 10, 28, true, "Take 1", "Take 5", "Take 10", "Take All", "Take X");
        scroll.child(0, 79542, 15, 15);


        widget.totalChildren(10);
        widget.child(0, 79501, 15, 15);
        widget.child(1, 79502, 458,21);
        widget.child(2, 79505, 360,110);
        widget.child(3, 79506, 4510, 285);
        widget.child(4, 79509, 360,65);
        widget.child(5, 79510, 2510, 285);

        widget.child(6, 79098, 25,65);
        widget.child(7, 79511, 250,25);


        widget.child(8, 76022, 1000, 285);
        widget.child(9, 76023, 1510, 285);


    }
}
