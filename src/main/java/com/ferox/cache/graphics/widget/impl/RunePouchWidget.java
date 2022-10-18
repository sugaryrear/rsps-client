package com.ferox.cache.graphics.widget.impl;

import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.Widget;

public class RunePouchWidget extends Widget {

    public static void unpack(AdvancedFont[] font) {
        Widget tab = addInterface(48700);
        addSprite(48700 + 1, 528);
        closeButton(48700 + 2, 107, 108, true);
        //Runes widget
        addContainer(48700 + 5, 4, 1, 5, 23, false, true, true, "Withdraw-1", "Withdraw-10", "Withdraw-100", "Withdraw-All", "Withdraw-X");
        //Inventory widget
        addContainer(48700 + 6, 7, 4, 14, 0, false, true, true, "Deposit-1", "Deposit-10", "Deposit-100", "Deposit-All", "Deposit-X");

        tab.totalChildren(4);
        tab.child(0, 48700 + 1, 82, 19);
        tab.child(1, 48700 + 2, 406, 26);
        tab.child(2, 48700 + 5, 188, 84);
        tab.child(3, 48700 + 6, 105, 152);
    }
}
