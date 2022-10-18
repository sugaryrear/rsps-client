package com.ferox.cache.graphics.widget.impl;

import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.Widget;

public class DropInterfaceWidget extends Widget {

    public static void unpack(AdvancedFont[] font) {
        dropInterface(font);
    }

    private static void dropInterface(AdvancedFont[] font) {
        Widget main = addInterface(55140);
        addSpriteLoader(55141, 1067);
        addHoverButton(55143, 1058, 171, 16, "Search", -1, 55144, 1);
        addHoveredButton(55144, 1057, 171, 16, 55145);
        addText(55146, "Search", font, 0, 0xB27300, false, true);

        addText(55147, "Item", font, 0, 0xB27300, true, true);
        addText(55148, "Name", font, 0, 0xB27300, true, true);
        addText(55149, "Amount", font, 0, 0xB27300, true, true);
        addText(55150, "BM", font, 0, 0xB27300, true, true);
        addText(55151, "Chance", font, 0, 0xB27300, true, true);
        closeButton(55152, 24, 25, true);
        addText(55154, "Viewing drop table for:", font, 2, 0xffb000, true, true);
        addText(55155, "<col=ffff00>Note:</col> Only base drop rates are displayed, and do not account for drop rate bonuses", font, 0, 0xffffff, true, true);
        main.totalChildren(14);
        main.child(0, 55141, 6, 2);
        main.child(55143, 17, 64);
        main.child(55144, 17, 64);
        main.child(3, 55146, 69, 46);

        main.child(4, 55147, 189, 46);
        main.child(5, 55148, 255, 46);
        main.child(6, 55149, 329, 46);
        main.child(7, 55150, 380, 46);
        main.child(8, 55151, 437, 46);
        main.child(9, 55153, 55 + 107, 67);
        main.child(10, 55152, 480, 12);
        main.child(11, 55200, 10, 84);
        main.child(12, 55154, 275, 11);
        main.child(13, 55155, 257, 313);

        int childStart = 0;
        int yPos = 0;

        Widget dropWidget = addInterface(55153);

        dropWidget.width = 421 - 107;
        dropWidget.height = 238;
        dropWidget.scrollMax = 2000;

        dropWidget.totalChildren(901);

        for (int i = 0; i < 150; i++) {
            addSpriteLoader(56020 + i, 1055);
            dropWidget.child(childStart + i, 56020 + i, 6, yPos);
            yPos += 39;
        }


        int childStart1 = 150;
        int yPos1 = 0;
        //Rarities colours are drawn here
        for (int i = 0; i < 150; i++) {
            addSpriteLoader(56400 + i, 1063);
            dropWidget.child(childStart1 + i, 56400 + i, 303, yPos1 + 1);
            yPos1 += 39;
        }

        addToItemGroup(56015, 1, 100, 1, 7, false, false, null);
        dropWidget.child(300, 56015, 10, 3);

        int nameStart = 301;
        int nameY = 14;
        for (int i = 0; i < 150; i++) {
            addText(56700 + i, "Npc name", font, 0, 0xB27300, false, true);
            dropWidget.child(nameStart + i, 56700 + i, 48, nameY);
            nameY += 39;
        }

        int amountStart = 451;
        int amountY = 14;
        for (int i = 0; i < 150; i++) {
            addText(56850 + i, "52323", font, 0, 0xB27300, true, true);
            dropWidget.child(amountStart + i, 56850 + i, 170, amountY);
            amountY += 39;
        }

        int rateStart = 601;
        int rateY = 14;
        for (int i = 0; i < 150; i++) {
            addText(57000 + i, "0.25%", font, 0, 0xB27300, true, true);
            dropWidget.child(rateStart + i, 57000 + i, 222, rateY);
            rateY += 39;
        }

        int rarityStart = 751;
        int rarityY = 14;
        for (int i = 0; i < 150; i++) {
            addText(57150 + i, "Common", font, 0, 0xB27300, true, true);
            dropWidget.child(rarityStart + i, 57150 + i, 275, rarityY);
            rarityY += 39;
        }


        Widget npcListWidget = addInterface(55200);
        npcListWidget.width = 130;
        npcListWidget.height = 216;
        npcListWidget.scrollMax = 3392; //Increase this to add more NPCs

        npcListWidget.totalChildren(860);
        int spriteY = 1;
        int NpcListChildIds = 57400;
        for (int i = 0; i < 430; i += 2) {
            addSpriteLoader(NpcListChildIds + i, 1057);
            addSpriteLoader(NpcListChildIds + i + 1, 1058);
            npcListWidget.child(i, NpcListChildIds + i, 8, spriteY);
            spriteY += 16;
            npcListWidget.child(i + 1, NpcListChildIds + i + 1, 8, spriteY);
            spriteY += 16;
        }

        int textChild = 430;
        int textY = 1;
        //Maximum 300 NPCs.
        for (int i = 0; i < 430; i++) {
            addCustomClickableText(55510 + i, "", "Select", font, 1, 0xB27300, false, true, 110, 13);
            npcListWidget.child(textChild + i, 55510 + i, 10, textY);
            textY += 16;
        }
    }
}
