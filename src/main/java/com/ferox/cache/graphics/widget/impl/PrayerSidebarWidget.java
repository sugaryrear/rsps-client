package com.ferox.cache.graphics.widget.impl;

import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.Widget;
import com.ferox.model.content.prayer.PrayerSystem;

public class PrayerSidebarWidget extends Widget {

    public static void unpack(AdvancedFont[] font) {
        quickPrayers(font);

        addPrayer(28001, 0, 708, 54, 151, 152, "Activate <col=ffb000>Preserve", 28003, 1, -1);
        //addPrayer(28001, "Activate <col=ffb000>Preserve", 31, 32, 150, 1, -1, 151, 152, 1, 708, 28003);

        addPrayer(28004, 0, 710, 73, 153, 154, "Activate <col=ffb000>Rigour", 28006, 1, -5);
        //addPrayer(28004, "Activate <col=ffb000>Rigour", 31, 32, 150, 1, -5, 153, 154, 1, 710, 28006);

        addPrayer(28007, 0, 712, 76, 155, 156, "Activate <col=ffb000>Augury", 28009, 1, -3);
        //addPrayer(28007, "Activate <col=ffb000>Augury", 31, 32, 150, 1, -3, 155, 156, 1, 712, 28009);

        addPrayerHover(28003, "Level 55<br>Preserve<br>Boosted stats last 20% longer.", -135, -60);

        addPrayerHover(28006, "Level 74<br>Rigour<br>Increases your Ranged attack<br>by 20% and damage by 23%,<br>and your defence by 25%", -70, -100);

        addPrayerHover(28009, "Level 77<br>Augury<br>Increases your Magic attack<br>by 25% and your defence by <br>25%", -110, -100);

        PrayerSystem.prayerPlacement();
    }

    private static void quickPrayers(AdvancedFont[] font) {
        int frame = 0;
        Widget tab = addTabInterface(17200);

        addTransparentSprite(17235, 131, 50);
        addSpriteLoader(17201, 132);
        addText(17231, "Select your quick prayers below.", font, 0, 0xFF981F, false, true);

        int child = 17202;
        int config = 620;
        for (int i = 0; i < 29; i++) {
            addConfigButton(child++, 17200, 133, 134, "Select", config++, 0, 1);
        }

        addHoverButton(17232, 135, 190, 24, "Confirm Selection", -1, 17233, 1);
        addHoveredButton(17233, 136, 190, 24, 17234);

        setChildren(64, tab);
        setBounds(5632, 5, 8 + 20, frame++, tab);
        setBounds(5633, 44, 8 + 20, frame++, tab);
        setBounds(5634, 79, 11 + 20, frame++, tab);
        setBounds(19813, 116, 10 + 20, frame++, tab);
        setBounds(19815, 153, 9 + 20, frame++, tab);
        setBounds(5635, 5, 48 + 20, frame++, tab);
        setBounds(5636, 44, 47 + 20, frame++, tab);
        setBounds(5637, 79, 49 + 20, frame++, tab);
        setBounds(5638, 116, 50 + 20, frame++, tab);
        setBounds(5639, 154, 50 + 20, frame++, tab);
        setBounds(5640, 4, 84 + 20, frame++, tab);
        setBounds(19817, 44, 87 + 20, frame++, tab);
        setBounds(19820, 81, 85 + 20, frame++, tab);
        setBounds(5641, 117, 85 + 20, frame++, tab);
        setBounds(5642, 156, 87 + 20, frame++, tab);
        setBounds(5643, 5, 125 + 20, frame++, tab);
        setBounds(5644, 43, 124 + 20, frame++, tab);
        setBounds(13984, 83, 124 + 20, frame++, tab);
        setBounds(5645, 115, 121 + 20, frame++, tab);
        setBounds(19822, 154, 124 + 20, frame++, tab);
        setBounds(19824, 5, 160 + 20, frame++, tab);
        setBounds(5649, 41, 158 + 20, frame++, tab);
        setBounds(5647, 79, 163 + 20, frame++, tab);
        setBounds(5648, 116, 158 + 20, frame++, tab);

        //Preserve
        setBounds(28002, 157, 160 + 20, frame++, tab);

        //Chivarly
        setBounds(19826, 10, 208, frame++, tab);

        //Piety
        setBounds(19828, 45, 207 + 13, frame++, tab);

        //Rigour
        setBounds(28005, 85, 210, frame++, tab);

        //Augury
        setBounds(28008, 124, 210, frame++, tab);

        setBounds(17235, 0, 25, frame++, tab);// Faded backing
        setBounds(17201, 0, 22, frame++, tab);// Split
        setBounds(17201, 0, 237, frame++, tab);// Split

        setBounds(17202, 5 - 3, 8 + 17, frame++, tab);
        setBounds(17203, 44 - 3, 8 + 17, frame++, tab);
        setBounds(17204, 79 - 3, 8 + 17, frame++, tab);
        setBounds(17205, 116 - 3, 8 + 17, frame++, tab);
        setBounds(17206, 153 - 3, 8 + 17, frame++, tab);
        setBounds(17207, 5 - 3, 48 + 17, frame++, tab);
        setBounds(17208, 44 - 3, 48 + 17, frame++, tab);
        setBounds(17209, 79 - 3, 48 + 17, frame++, tab);
        setBounds(17210, 116 - 3, 48 + 17, frame++, tab);
        setBounds(17211, 153 - 3, 48 + 17, frame++, tab);
        setBounds(17212, 5 - 3, 85 + 17, frame++, tab);
        setBounds(17213, 44 - 3, 85 + 17, frame++, tab);
        setBounds(17214, 79 - 3, 85 + 17, frame++, tab);
        setBounds(17215, 116 - 3, 85 + 17, frame++, tab);
        setBounds(17216, 153 - 3, 85 + 17, frame++, tab);
        setBounds(17217, 5 - 3, 124 + 17, frame++, tab);
        setBounds(17218, 44 - 3, 124 + 17, frame++, tab);
        setBounds(17219, 79 - 3, 124 + 17, frame++, tab);
        setBounds(17220, 116 - 3, 124 + 17, frame++, tab);
        setBounds(17221, 153 - 3, 124 + 17, frame++, tab);
        setBounds(17222, 5 - 3, 160 + 17, frame++, tab);
        setBounds(17223, 44 - 3, 160 + 17, frame++, tab);
        setBounds(17224, 79 - 3, 160 + 17, frame++, tab);
        setBounds(17225, 116 - 3, 160 + 17, frame++, tab);
        setBounds(17226, 153 - 3, 160 + 17, frame++, tab);

        setBounds(17227, 1, 207 + 4, frame++, tab); //Chivalry toggle button
        setBounds(17228, 41, 207 + 4, frame++, tab); //Piety toggle button
        setBounds(17229, 77, 207 + 4, frame++, tab); //Rigour toggle button
        setBounds(17230, 116, 207 + 4, frame++, tab); //Augury toggle button

        setBounds(17231, 5, 5, frame++, tab);// text
        setBounds(17232, 0, 237, frame++, tab);// confirm
        setBounds(17233, 0, 237, frame++, tab);// Confirm hover
    }

}
