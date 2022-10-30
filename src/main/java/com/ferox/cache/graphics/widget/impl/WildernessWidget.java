package com.ferox.cache.graphics.widget.impl;

import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.Widget;

public class WildernessWidget extends Widget {
    static int ymove = -6;
    public static void unpack(AdvancedFont[] font) {
        Widget widget = addInterface(53720);

        darken(58001, 170, 43, 0x747404, (byte) 50);

        addText(53723, "Target:", font, 0, 0xFFFF00, false, true);
        addText(53724, "sugary", font, 1, 0xFFFFFF, true, true);
        addText(53725, "Lvl 1-4, Cmb 70", font, 0, 0xCC0000, true, true);
        addText(53726, "Wealth: V. Low", font, 0, 0xFFFF00, false, true);


        for (int i = 0, i2=0; i < 6; i++,i2+=2) {
            addSprite(53730+i2, 1896+i);
        }

        setChildren(13, widget);
        setBounds(58001, 340, 8, 12, widget);
        setBounds(53723, 440, 18+ymove, 1, widget);
        setBounds(53724, 458, 31+ymove, 2, widget);
        setBounds(53725, 465, 47+ymove, 3, widget);
        setBounds(53726, 340, 47+ymove, 0, widget);
        setBounds(28020, 0, 5+ymove, 11, widget);

        setBounds(53730, 345, 25+ymove, 6, widget);
        setBounds(53732, 345, 25+ymove, 7, widget);
        setBounds(53734, 345, 25+ymove, 8, widget);
        setBounds(53736, 345, 25+ymove, 9, widget);
        setBounds(53738, 345, 25+ymove, 10, widget);
        setBounds(53740, 345, 25+ymove, 5, widget);

        setBounds(197, -30, 2, 4, widget);






        Widget statistics = addInterface(28020);
        setChildren(9, statistics);
        darken(28021, 170, 43+ymove, 0x9c4c44, (byte) 80);
        addText(28022, "Current  Record", font, 0, 0xFFFF00, false, true);
        addText(28023, "Rogue:", font, 0, 0xFFFF00, false, true);
        addText(28024, "Hunter:", font, 0, 0xFFFF00, false, true);
        addText(28025, "1", font, 0, 0xFFFF00, true, true);
        addText(28026, "2", font, 0, 0xFFFF00, true, true);
        addText(28027, "3", font, 0, 0xFFFF00, true, true);
        addText(28028, "4", font, 0, 0xFFFF00, true, true);

        addSprite(28029, 1895);

        setBounds(28021, 340, 55, 8, statistics);
        setBounds(28022, 420, 60+ymove, 1, statistics);
        setBounds(28023, 375, 73+ymove, 2, statistics);
        setBounds(28024, 375, 87+ymove, 3, statistics);
        setBounds(28025, 440, 73+ymove, 4, statistics);
        setBounds(28026, 440, 87+ymove, 5, statistics);
        setBounds(28027, 481, 73+ymove, 6, statistics);
        setBounds(28028, 481, 87+ymove, 7, statistics);
        setBounds(28029, 347, 74+ymove, 0, statistics);


    }
}
