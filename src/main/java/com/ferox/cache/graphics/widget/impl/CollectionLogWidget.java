package com.ferox.cache.graphics.widget.impl;

import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.Widget;

public class CollectionLogWidget extends Widget {

    public static void unpack(AdvancedFont[] font) {
        collectionLog(font);
    }

    public static void collectionLog(AdvancedFont[] advancedFonts) {
        Widget tab = addInterface(61000);
        addSprite(61001, 857);
        addText(61002, "Collection Log", advancedFonts, 2, 0xff981f, true, true);

        String[] tabNames = {"Bosses", "Mboxes", "Keys", "Other"};
        for (int i = 0; i < tabNames.length; ++i) {
            addConfigButton(61003 + i, 61000, 858, 859, 96, 20, "View "+tabNames[i], i, 5, 1106);
            addText(61008 + i, tabNames[i], advancedFonts, 1, 0xff981f, false, true);
        }

        addText(61015, "Abyssal Sire", advancedFonts, 2, 0xff981f, false, true);
        addText(61016, "Obtained: <col=ff0000>0/9", advancedFonts, 0, 0xff981f, false, true);
        addText(61017, "Abyssal Sire kills: <col=ffffff>1", advancedFonts, 0, 0xff981f, false, true);
        cache[61017].rightText = true;
        addSprite(61018,1734);
        addText(61019, "<img=1048></img>Rewards for completing the collection log:", advancedFonts, 0, 0xff981f, false, true);
        addHoverButton(61020,1844, 70, 39, "Collect", -1, 61021, 1);
        addHoveredButton(61021, 1845, 70, 39, 61022);
        addText(61023, "Claim", advancedFonts, 2, 0xff981f, false, true);
        addContainer(61024, 5, 1, 7, 5, false);

        int x = 10, y = 10, child = 0;
        tab.totalChildren(22);
        tab.child(child++, 61001, x, y);
        tab.child(child++, 55152, 476+x, 10+y);
        tab.child(child++, 61002, 250+x, 10+y);
        for (int i = 0; i < tabNames.length; ++i) {
            tab.child(child++, 61003 + i, 10+x+(i * 83), 34+y);
            tab.child(child++, 61008 + i, 14+x+(i * 85), 37+y);
        }
        tab.child(child++, 61025, 214+x, 98+y);
        tab.child(child++, 61050, 11+x, 58+y);
        tab.child(child++, 61015, 216+x, 57+y);
        tab.child(child++, 61016, 216+x, 81+y);
        tab.child(child++, 61017, 484+x, 81+y);
        tab.child(child++, 61018, 216+x, 255+y);
        tab.child(child++, 61019, 216+x, 242+y);
        tab.child(child++, 61020, 415+x, 259+y);
        tab.child(child++, 61021, 415+x, 259+y);
        tab.child(child++, 61023, 432+x, 276+y);
        tab.child(child++, 61024, 222+x, 260+y);

        Widget items = addInterface(61025);

        addContainer(61026, TYPE_CONTAINER, 6, 35, 10, 5, 110, false, true, true);
        items.totalChildren(1);
        items.child(0, 61026, 5, 5);
        items.width = 259;
        items.height = 155;
        items.scrollMax = 800;

        Widget scroll = addInterface(61050);
        scroll.totalChildren(150);
        for (int i = 0; i < 50; ++i) {
            addHoverButton(61051 + i, i % 2 == 0 ? 1841 : 1843, 186, 15, "View", -1, 61101 + i, 1);
            addHoveredButton(61101 + i, 1842, 186, 15, 61049);
            addText(61151 + i, "Abyssal sire", advancedFonts, 1, 0xff981f, false, true);
            scroll.child(i, 61051 + i, 0, i * 15);
            scroll.child(i + 50, 61101 + i, 0, i * 25);
            scroll.child(i + 100, 61151 + i, 2, y-10);
            y+= 15;
        }
        scroll.width = 186;
        scroll.height = 243;
        scroll.scrollMax = 750;
    }
}
