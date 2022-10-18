package com.ferox.cache.graphics.widget.impl;

import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.Widget;

public class TradeWidget extends Widget {

    public static void unpack(AdvancedFont[] font) {
        tradeScreen(font);
        tradeScreen2(font);
    }

    private static void tradeScreen2(AdvancedFont[] tda) {
        Widget interface_ = addTabInterface(52250);
        addSprite(52251, 855);
        addText(52252, "Are you sure you want to make this trade?", tda, 1, 0x00ffff, true, true);
        addText(52253, "There is <col=ff0000>NO WAY</col> to reverse a trade if you change your mind.", tda, 0, 0xffffff, true, true);
        addText(52254, "You are about to give:", tda, 0, 0xffff00, true, true);
        addText(52255, "(Value: <col=ffffff>0</col> coins)", tda, 0, 0xffff00, true, true);
        addText(52256, "In return you will receive:", tda, 0, 0xffff00, true, true);
        addText(52257, "(Value: <col=ffffff>0</col> coins)", tda, 0, 0xffff00, true, true);
        addText(52258, "Trading with:", tda, 2, 0x00ffff, true, true);
        addText(52259, "", tda, 2, 0x00ffff, true, true);
        for (int i = 0; i < 28; i++) {
            addText(52260 + i, "", tda, 2, 0xff9040, true, true);
        }
        for (int i = 0; i < 28; i++) {
            addText(52290 + i, "", tda, 2, 0xff9040, true, true);
        }
        addButtonWithoutSprite(52319, "Accept", 30, 50);
        addButtonWithoutSprite(52320, "Decline", 30, 50);
        int x = 135, y = 85;
        int tradeChild = 0;
        interface_.totalChildren(68);
        interface_.child(tradeChild++, 52251, 15, 14);
        interface_.child(tradeChild++, 52252, 255, 20);
        interface_.child(tradeChild++, 52253, 255, 35);
        interface_.child(tradeChild++, 52254, 135, 59);
        interface_.child(tradeChild++, 52255, 135, 69);
        interface_.child(tradeChild++, 52256, 375, 59);
        interface_.child(tradeChild++, 52257, 375, 69);
        interface_.child(tradeChild++, 52258, 100, 285);
        interface_.child(tradeChild++, 52259, 100, 297);
        interface_.child(tradeChild++, 52319, 190, 285);
        interface_.child(tradeChild++, 52320, 270, 285);
        interface_.child(tradeChild++, 52102, 478, 24);
        for (int i = 0; i < 28; i++) {
            interface_.child(tradeChild++, 52260 + i, x, y);
            y += 13;
        }
        y = 84;
        x = 375;
        for (int i = 0; i < 28; i++) {
            interface_.child(tradeChild++, 52290 + i, x, y);
            y += 13;
        }
    }

    private static void tradeScreen(AdvancedFont[] font) {
        Widget interface_ = addTabInterface(52000);
        addSprite(52001, 853);
        addText(52002, "Trading With:", font, 2, 0xff9933, true, true);
        addText(52003, "You offer:", font, 0, 0xff9933, true, true);
        addText(52004, "(Value: <col=ffffff>0</col> coins)", font, 0, 0xff9933, true, true);
        addText(52005, "offers:", font, 0, 0xff9933, true, true);
        addText(52006, "(Value: <col=ffffff>0</col> coins)", font, 0, 0xff9933, true, true);
        addText(52007, "has 23", font, 0, 0xff9933, true, true);
        addText(52008, "free inventory", font, 0, 0xff9933, true, true);
        addText(52009, "slots.", font, 0, 0xff9933, true, true);
        addText(52010, "Other player has accepted.", font, 1, 0xffffff, true, true);
        addText(52011, "Accept", font, 1, 0x00c000, true, true);
        addText(52012, "Decline", font, 1, 0xc00000, true, true);
        addText(52013, "Trade modified", font, 0, 0xff0000, true, true);
        addText(52014, "Trade modified", font, 0, 0xff0000, true, true);
        addButtonWithoutSprite(52100, "Accept", 32, 68);
        addButtonWithoutSprite(52101, "Decline", 32, 68);
        closeButton(52102, 24, 25, true);
        for (int i = 0; i < 28; i++) {
            addTransparentSprite(52017 + i, 854, 0);
        }
        for (int i = 0; i < 28; i++) {
            addTransparentSprite(52046 + i, 854, 0);
        }
        Widget.cache[52013].drawingDisabled = true;
        Widget.cache[52014].drawingDisabled = true;
        Widget container = addTabInterface(52015);
        container.actions = new String[]{
            "Remove",
            "Remove-5",
            "Remove-10",
            "Remove-All",
            "Remove-X"
        };
        container.inventoryOffsetX = new int[28];
        container.inventoryOffsetY = new int[28];
        container.inventoryItemId = new int[28];
        container.inventoryAmounts = new int[28];
        container.centerText = true;
        container.filled = false;
        container.inventoryMarginX = 13;
        container.inventoryMarginY = 0;
        container.height = 7;
        container.width = 4;
        container.parent = 52000;
        container.type = TYPE_INVENTORY;

        Widget container2 = addTabInterface(52016);
        container2.inventoryOffsetX = new int[28];
        container2.inventoryOffsetY = new int[28];
        container2.inventoryItemId = new int[28];
        container2.inventoryAmounts = new int[28];
        container2.centerText = true;
        container2.filled = false;
        container2.inventoryMarginX = 13;
        container2.inventoryMarginY = 0;
        container2.height = 7;
        container2.width = 4;
        container2.parent = 52000;
        container2.type = TYPE_INVENTORY;

        int x = 30, y = 75;
        int tradeChild = 0;
        interface_.totalChildren(75);
        interface_.child(tradeChild++, 52001, 15, 14);
        interface_.child(tradeChild++, 52002, 252, 23);
        interface_.child(tradeChild++, 52003, 110, 50);
        interface_.child(tradeChild++, 52004, 110, 60);
        interface_.child(tradeChild++, 52005, 395, 50);
        interface_.child(tradeChild++, 52006, 395, 60);
        interface_.child(tradeChild++, 52007, 258, 79);
        interface_.child(tradeChild++, 52015, 30, 75);
        interface_.child(tradeChild++, 52016, 315, 75);
        interface_.child(tradeChild++, 52008, 258, 89);
        interface_.child(tradeChild++, 52009, 258, 99);
        interface_.child(tradeChild++, 52010, 256, 283);
        interface_.child(tradeChild++, 52011, 256, 173);
        interface_.child(tradeChild++, 52012, 256, 248);
        interface_.child(tradeChild++, 52013, 110, 295);
        interface_.child(tradeChild++, 52014, 395, 295);
        interface_.child(tradeChild++, 52100, 222, 163);
        interface_.child(tradeChild++, 52101, 222, 238);
        interface_.child(tradeChild++, 52102, 477, 24);
        for (int i = 0; i < 28; i++) {
            if (x >= 180) {
                x = 30;
                y += 32;
            }
            interface_.child(tradeChild++, 52017 + i, x, y);
            x += 45;
        }
        x = 315;
        y = 75;
        for (int i = 0; i < 28; i++) {
            if (x >= 465) {
                x = 315;
                y += 32;
            }
            interface_.child(tradeChild++, 52046 + i, x, y);
            x += 45;
        }
    }
}
