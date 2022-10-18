package com.ferox.cache.graphics.widget.impl;

import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.Widget;

/**
 * The class which represents functionality for the looting bag widget.
 *
 * @author Patrick van Elderen | 13:46 : dinsdag 2 juli 2019 (CEST)
 * @see <a href="https://github.com/Patrick9-10-1995">Github profile</a>
 */
public class LootingBagWidget extends Widget {

    public static void unpack(AdvancedFont[] font) {
        open(font);
        add(font);
        bank(font);
    }
    
    public static void open(AdvancedFont[] font) {
        Widget widget = addInterface(26700);
        addSprite(26700 + 1, 876);
        closeButton(26700 + 2, 24, 25, true);
        addText(26700 + 5, "Looting bag", font, 2, 0xFF9900, true, true);
        addContainer(26700 + 6, 0, 4, 7, 13, 0, 0, false, true, true);
        addText(26700 + 7, "Value: -", font, 0, 0xFF9900, true, true);
        addText(26700 + 8, "The bag is empty.", font, 1, 0xFF9900, true, true);
        widget.totalChildren(6);
        widget.child(0, 26700 + 1, 9, 21);
        widget.child(1, 26700 + 2, 168, 4);
        widget.child(2, 26700 + 5, 95, 4);
        widget.child(3, 26700 + 6, 12, 23);
        widget.child(4, 26700 + 7, 95, 250);
        widget.child(5, 26700 + 8, 92, 125);
    }
    
    public static void add(AdvancedFont[] font) {
        Widget widget = addInterface(26800);
        addSprite(26800 + 1, 876);
        closeButton(26800 + 2, 24, 25, true);
        addText(26800 + 5, "Add to bag", font, 2, 0xFF9900, true, true);
        addContainer(26800 + 6, 0, 4, 7, 13, 0, 0, false, true, true, "Store-1", "Store-5", "Store-10", "Store-X");
        addText(26800 + 7, "Bag value: -", font, 0, 0xFF9900, true, true);
        widget.totalChildren(5);
        widget.child(0, 26800 + 1, 9, 21);
        widget.child(1, 26800 + 2, 168, 4);
        widget.child(2, 26800 + 5, 95, 4);
        widget.child(3, 26800 + 6, 12, 23);
        widget.child(4, 26800 + 7, 95, 250);
    }
    
    public static void bank(AdvancedFont[] font) {
        Widget widget = addInterface(26900);
        addSprite(26900 + 1, 876);
        closeButton(26900 + 2, 24, 25, true);
        addButton(26900 + 5, 879, "Deposit loot");
        addText(26900 + 6, "Bank your loot", font, 2, 0xFF9900, true, true);
        addContainer(26900 + 7, 0, 4, 7, 13, 0, 0, false, true, true, "Deposit-1", "Deposit-5", "Deposit-10", "Deposit-X");
        addText(26900 + 8, "The bag is empty.", font, 1, 0xFF9900, true, true);
        widget.totalChildren(6);
        widget.child(0, 26900 + 1, 9, 21);
        widget.child(1, 26900 + 2, 168, 4);
        widget.child(2, 26900 + 5, 0, 0);
        widget.child(3, 26900 + 6, 95, 4);
        widget.child(4, 26900 + 7, 12, 23);
        widget.child(5, 26900 + 8, 92, 125);
    }

}
