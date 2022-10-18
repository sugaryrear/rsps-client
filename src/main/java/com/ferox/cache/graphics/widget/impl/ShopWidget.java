package com.ferox.cache.graphics.widget.impl;

import com.ferox.ClientConstants;
import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.Widget;

import static com.ferox.util.ConfigUtility.*;

/**
 * The shop interface widget, moved from Widget class to its new structure.
 * This shop widget can display text, and has slightly different positioning.
 * @author Professor Oak
 */
public class ShopWidget extends Widget {

    public static void unpack(AdvancedFont[] font) {
        shop_with_scrollbar(font);
        shop_with_close_button(font);
    }
    
    private static void shop_with_close_button(AdvancedFont[] font) {
        Widget widget = cache[3824];
        int[] childrenId = new int[widget.children.length + 2];
        int[] childrenX = new int[widget.children.length + 2];
        int[] childrenY = new int[widget.children.length + 2];
        for (int i = 0; i < widget.children.length; i++) {
            childrenId[i] = widget.children[i];
            childrenX[i] = widget.child_x[i];
            childrenY[i] = widget.child_y[i];
        }
        closeButton(28056, 107, 108, true);

        Widget tab = addTabInterface(28060);
        addConfigButton(28061, 28060, 112, 113, "Select", SHOP_BUTTON_ONE, 0, 1);
        addConfigButton(28062, 28060, 112, 113, "Select", SHOP_BUTTON_TWO, 0, 1);
        addConfigButton(28063, 28060, 112, 113, "Select", SHOP_BUTTON_THREE, 0, 1);
        addText(28064, "General", 16750623, false, true, 52, font, 2);
        addText(28065, "Cosmetic", 16750623, false, true, 52, font, 2);
        addText(28066, "Other", 16750623, false, true, 52, font, 2);
        setChildren(6, tab);
        setBounds(28061, 19, 55, 0, tab);
        setBounds(28062, 97, 55, 1, tab);
        setBounds(28063, 175, 55, 2, tab);
        setBounds(28064, 30, 58, 3, tab);
        setBounds(28065, 105, 58, 4, tab);
        setBounds(28066, 194, 58, 5, tab);

        addText(3902, "", 16750623, false, true, 52, font, 1);
        setChildren(94, widget);
        for (int i = 0; i < widget.children.length; i++) {
            setBounds(childrenId[i], childrenX[i], childrenY[i], i, widget);
        }
        setBounds(28056, 472, 27, 92, widget);
        setBounds(28060, 0, 0, 93, widget);
    }

    private static void shop_with_scrollbar(AdvancedFont[] font) {
        //Set up the shop inventory
        Widget shopInventory = cache[3900];
        int max_items = 200;
        shopInventory.inventoryItemId = new int[max_items];
        shopInventory.inventoryAmounts = new int[max_items];
        shopInventory.itemOpacity = new int[max_items];
        shopInventory.x = 0;
        shopInventory.width = 10;
        shopInventory.height = 200;
        shopInventory.inventoryMarginX = 10;
        shopInventory.inventoryMarginY = 18;
        if (ClientConstants.PVP_MODE) {
            shopInventory.drawInfinity = true;
            shopInventory.displayAmount = false;
        }

        //The scroll, add the shop inventory to it.
        Widget scroll = addTabInterface(22995);
        scroll.totalChildren(max_items+1);
        setBounds(3900, 0, 0, 0, scroll);
        scroll.height = 234;
        scroll.width = 438;
        scroll.scrollMax = 230;
           
        //Comment out the default text, in case we ever need to hide it in the future
        cache[3903].defaultText = "";

        for (int index = 0; index < max_items; index++) {
            addText(22996 + index, "FREE", font, 0, 0xffffff, true, true);
            int x = index % 10;
            int y = index / 10;

            //   Move distance between text
            x = (24 + 18) * x + 14; // Move left or right
            y = (22 + 28) * y + 36;

            scroll.child(1 + index, 22996 + index, x, y);
        }

        //Position the item container in the actual shop interface
        setBounds(22995, 40, 80, 75, cache[3824]);
    }
}
