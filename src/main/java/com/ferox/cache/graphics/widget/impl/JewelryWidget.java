package com.ferox.cache.graphics.widget.impl;

import com.ferox.cache.graphics.widget.Widget;

public class JewelryWidget extends Widget {

    public static void init() {
        Widget layer = cache[4161];
        for (int id : layer.children) {
            Widget child = cache[id];
            search(child);
        }
    }

    private static void search(Widget w) {
        if (w.children == null)
            return;
        int index = 0;
        for (int id : w.children) {
            Widget child = cache[id];
            final int type = child.type;
            if (type == 2 && child.inventoryItemId != null) {
                child.inventoryItemId = new int[8];
                child.inventoryAmounts = new int[8];
                child.width++;
                w.child_x[index] -= 24;
            }
            if (type == 0)
                search(child);

            index++;
        }
    }
}
