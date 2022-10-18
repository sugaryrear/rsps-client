package com.ferox.cache.graphics.widget.impl;

import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.Widget;

public class ForgeWidget extends Widget {

    public static void unpack(AdvancedFont[] font) {
        Widget mode = addInterface(69000);
        addSprite(69001, 1855);
        closeButton(69002, 107,108,false);
        addText(69003, "Item forging", font, 2, 0xff9933, true, true);
        addText(69005, "Items required:", font, 2, 0xff9933, true, true);

        configHoverButton(69006, "Upgrade", 1856, 1857, 1857, 1857, false, 69006);
        addText(69007, "Forge", font, 2, 0xff9933, true, true);

        addText(69008, "Success rate: 45%", font, 2, 0xff9933, true, true);

        itemGroup(69009, 1, 1, 5, 3);

        configHoverButton(69010, "View weaponry upgrades",1858, 1859, 1859, 1859, false, 69011, 69012);
        configHoverButton(69011, "View armor upgrades", 1858, 1859, 1859, 1859, false, 69010, 69012);
        configHoverButton(69012, "View pet upgrades",1858, 1859, 1859, 1859, false, 69010, 69011);

        addText(69013, "Weaponry", font, 0, 0xff9933, true, true);
        addText(69014, "Armour", font, 0, 0xff9933, true, true);
        addText(69015, "Misc", font, 0, 0xff9933, true, true);
        addContainer(69016, TYPE_CONTAINER, 5, 2, 5, 5, 0, false, true, true);

        mode.totalChildren(16);
        mode.child(0, 69001, 60, 47);
        mode.child(1, 69002, 446, 54);

        mode.child(2, 69003, 285, 56);
        mode.child(3, 69005, 330, 88);

        mode.child(4, 69006, 313, 255);
        mode.child(5, 69007, 370, 262);

        mode.child(6, 69008, 340, 189);

        mode.child(7, 69009, 358, 212);

        mode.child(8, 69010, 69, 85);
        mode.child(9, 69011, 133, 85);
        mode.child(10, 69012, 196, 85);

        mode.child(11, 69013, 100, 91);
        mode.child(12, 69014, 163, 91);
        mode.child(13, 69015, 227, 91);
        mode.child(14, 69016, 280, 110);
        mode.child(15, 69020, 70, 110);

        Widget nameScroll = addTabInterface(69020);
        int totalItems = 50;
        nameScroll.width = 180;
        nameScroll.height = 175;
        nameScroll.scrollMax = nameScroll.height + 1;

        nameScroll.totalChildren(totalItems);
        int y = 0;
        for(int index = 0; index < totalItems; index++) {
            addClickableText(69021 + index, "Abyssal whip", "Select", font, 0, 0xff9933, false, true, 165);
            nameScroll.child(index, 69021 + index, 2, y + 6);
            textClicked(69021 + index, 1137, 1, 2);
            y += 14;
        }
        nameScroll.scrollMax = y;
    }

    /*int interfaceId = 29000;
    int nameScrollId = 29020;
    int ingScrollId = 29080;
    Widget mode = addInterface(interfaceId);
    int index = 1;
    addSprite(interfaceId + index++, 1855);
    configHoverButton(interfaceId + index, "Close", 24, 25, 25, 25, false, interfaceId + index++);

    addText(interfaceId + index++, "Item upgrade machine", font, 2, 0xff9933, true, true);
    addText(interfaceId + index++, "", font, 0, 0xff9933, true, true);
    addText(interfaceId + index++, "Items required:", font, 1, 0xff9933, true, true);

    configHoverButton(interfaceId + index, "Upgrade", 1856, 1857, 1857, 1857, false, interfaceId + index++);
    addText(interfaceId + index++, "Upgrade", font, 2, 0xff9933, true, true);

    addText(interfaceId + index++, "", font, 1, 0xff9933, true, true);

    itemGroup(interfaceId + index++, 1, 1, 5, 3);

    configHoverButton(69010, "View weaponry upgrades",1858, 1859, 1859, 1859, false, 69011, 69012);
    configHoverButton(69011, "View armor upgrades", 1858, 1859, 1859, 1859, false, 69010, 69012);
    configHoverButton(69012, "View pet upgrades",1858, 1859, 1859, 1859, false, 69010, 69011);

    addText(69013, "Weaponry", font, 0, 0xff9933, true, true);
    addText(69014, "Armour", font, 0, 0xff9933, true, true);
    addText(69015, "Misc", font, 0, 0xff9933, true, true);
    addText(69016, "Safe item:", font, 1, 0xff9933, true, true);
    addSprite(69017, 1860);
    itemGroup(69018, 1, 1, 5, 3);

    index = 0;
    int x = 60; int y = 47;
        mode.totalChildren(20);
        mode.child(index++, interfaceId + index, x, y); //BACKGROUND
        mode.child(index++, interfaceId + index, x + 375, y + 9); //CLOSE BUTTON

        mode.child(index++, interfaceId + index, x + 196, y + 9); //HEAD TEXT
        mode.child(index++, interfaceId + index, x + 88, y + 58); //CURRECY COSTS TEXT
        mode.child(index++, interfaceId + index, x + 270, y + 41); //ITEMS REQUIRED TEXT

        mode.child(index++, interfaceId + index, x + 273, y + 208); //UPGRADE BUTTON
        mode.child(index++, interfaceId + index, x + 314, y + 210); //UPGRADE TEXT

        mode.child(index++, interfaceId + index, x + 283+34, y + 186); //SUCCESS RATE

        mode.child(index++, interfaceId + index, x + 210, y + 203); //PREVIEW UPGRADE ITEM

        mode.child(index++, interfaceId + index, x + 9, y + 40); //HOVERBUTTON
        mode.child(index++, interfaceId + index, x + 73, y + 40); //HOVERBUTTON
        mode.child(index++, interfaceId + index, x + 136, y + 40); //HOVERBUTTON

        mode.child(index++, interfaceId + index, x + 40, y + 44); //WEAPONRY
        mode.child(index++, interfaceId + index, x + 103, y + 44); //ARMOUR
        mode.child(index++, interfaceId + index, x + 167, y + 44); //PETS
        mode.child(index++, interfaceId + index, x + 283, y + 156); //safe item
        mode.child(index++, interfaceId + index, x + 283 + 34, y + 144); //Safe item container
        mode.child(index++, interfaceId + index, x + 283 + 41, y + 149); //item group safe item
        mode.child(index++, nameScrollId, x + 10, y + 66);

        mode.child(index++, ingScrollId, x + 208, y + 66);

    Widget nameScroll = addTabInterface(nameScrollId);
    int totalBoxes = 50;
    nameScroll.width = 172;
    nameScroll.height = 175;
    nameScroll.scrollMax = nameScroll.height + 1;

        nameScroll.totalChildren(totalBoxes);
    nameScrollId++;
    index = 0; x = 0; y = 0;
        for(int i = 0; i < totalBoxes; i++) {
        addClickableText(nameScrollId, "Item name", "View recipe", font, 0, 0xff9933, false, true, 165);
        nameScroll.child(index++, nameScrollId++, x + 2, y + 2);
        y += 14;
    }
    nameScroll.scrollMax = y;

    Widget ingScroll = addTabInterface(ingScrollId);
    ingScroll.width = 167;
    ingScroll.height = 72;
    ingScroll.scrollMax = 120;

        ingScroll.totalChildren(1);
    ingScrollId++;
    index = 0; x = 0; y = 0;
    itemGroup(ingScrollId, 4, 2, 7, 3);
        ingScroll.child(index, ingScrollId, x + 4, y + 2);*/
}
