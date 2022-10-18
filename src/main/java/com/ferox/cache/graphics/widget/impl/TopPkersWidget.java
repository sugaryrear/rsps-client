package com.ferox.cache.graphics.widget.impl;

import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.Widget;

public class TopPkersWidget extends Widget {

    public static void unpack(AdvancedFont[] font){
        Widget main = addInterface(50190);
        addSpriteLoader(50191, 180);
        closeButton(50192, 107,108,false);
        addText(50193, "Leaderboards", font, 2, 0xff981f, true, true);
        addConfigButton(50194, 50190, 114, 115, "weekly", 1800, 0, OPTION_OK);
        addConfigButton(50195, 50190, 114, 115, "all time", 1801, 0, OPTION_OK);

        addText(50196, "weekly", font, 1, 0xff981f, true, true);

        addText(50197, "all time", font, 1, 0xff981f, true, true);

        addText(50198, "@red@Top Kills", font, 2, 0xff981f, true, true);

        addText(50100, "Name", font, 2, 0xff981f, true, true);

        addText(50101, "Kills", font, 2, 0xff981f, true, true);


        addText(50102, "", font, 1, 0xff981f, false, true);
        addText(50103, "number one winners will receive", font, 1, 0xff981f, false, true);

        addText(50104, "current weekly kills", font, 2, 0xff981f, false, true);
       // addOutlinedColorBox(50105, 0x534a40, 100, 40, 100);
        addRectangle(50105, (byte) 0, 0x1A9C35, false, 440, 105);

        addConfigButton(50106, 50190, 114, 115, "weekly", 1802, 0, OPTION_OK);
        addConfigButton(50107, 50190, 114, 115, "all time", 1803, 0, OPTION_OK);
        addText(50108, "weekly", font, 1, 0xff981f, true, true);

        addText(50109, "all time", font, 1, 0xff981f, true, true);
        addText(50120, "@red@Top Boss Points", font, 2, 0xff981f, true, true);


        addConfigButton(50121, 50190, 114, 115, "weekly", 1804, 0, OPTION_OK);
        addConfigButton(50122, 50190, 114, 115, "all time", 1805, 0, OPTION_OK);
        addText(50123, "weekly", font, 1, 0xff981f, true, true);

        addText(50124, "all time", font, 1, 0xff981f, true, true);
        addText(50125, "@red@XP Hiscores", font, 2, 0xff981f, true, true);

        addConfigButton(50126, 50190, 114, 115, "daily", 1806, 0, OPTION_OK);
        addText(50127, "daily", font, 1, 0xff981f, true, true);

        addConfigButton(50128, 50190, 114, 115, "daily", 1807, 0, OPTION_OK);
        addText(50129, "daily", font, 1, 0xff981f, true, true);

        addConfigButton(50130, 50190, 114, 115, "daily", 1808, 0, OPTION_OK);
        addText(50131, "daily", font, 1, 0xff981f, true, true);


        Widget scroll = addTabInterface(50199);
        scroll.width = 470;
        scroll.height = 110;
        //scroll.scrollMax = 300;

        scroll.totalChildren(30);
        int y = 10;
        for (int i = 0; i < 10; i++) {

         addText(50200 + i, "#"+i, font, 2,i == 0 ? 0x1A9C35 :0xEBEB14 , false, true);
           scroll.child( i, 50200 + i, 50, y);
           y+=20;
        }
        y=10;
        for (int i = 0; i < 10; i++) {

            addText(50210 + i, "Sugary", font, 2, i == 0 ? 0x1A9C35 :0xEBEB14 , false, true);
            scroll.child( 10+i, 50210 + i, 130, y);
            y+=20;
        }
        y=10;
        for (int i = 0; i < 10; i++) {

            addText(50220 + i, "12 kills", font, 2,  i == 0 ? 0x1A9C35 :0xEBEB14 , false, true);
            scroll.child( 20+i, 50220 + i, 250, y);
            y+=20;
        }

        main.totalChildren(31);
        main.child(0, 50191, 15,10);
        main.child(1, 50192, 468,16);
        main.child(2, 50193, 250,20);
        main.child(3, 50194, 25,80);
        main.child(4, 50195, 100,80);
        main.child(5, 50196, 60,89);
        main.child(6, 50197, 135,89);
        main.child(7, 50198, 120,65);
        main.child(8, 50199, 0,140);
        main.child(9, 50100, 150,130);
        main.child(10, 50101, 260,130);
        main.child(11, 50102, 30,280);
        main.child(12, 50103, 30,300);
        main.child(13, 50104, 130,255);
        main.child(14, 50105, 30,147);

        main.child(15, 50106, 185,80);
        main.child(16, 50107, 260,80);
        main.child(17, 50108, 220,89);
        main.child(18, 50109, 295,89);
        main.child(19, 50120, 290,65);


        main.child(20, 50121, 340,80);
        main.child(21, 50122, 415,80);
        main.child(22, 50123, 375,89);
        main.child(23, 50124, 452,89);
        main.child(24, 50125, 435,65);
        main.child(25, 50126, 65,101);
        main.child(26, 50127, 100,110);

        main.child(27, 50128, 225,101);
        main.child(28, 50129, 260,110);

        main.child(29, 50130, 385,101);
        main.child(30, 50131, 420,110);



    }

}
