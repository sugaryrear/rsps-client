package com.ferox.cache.graphics.widget.impl;

import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.Widget;

public class newinterfaceaor extends Widget {

    public static void unpack(AdvancedFont[] font) {

        Widget widget = addInterface(89500);
        addSprite(89501, 1872);

        Widget scroll = addInterface(89098);
        //scroll.width = 305;
     //   scroll.height = 175;
      //  scroll.scrollMax = 750;
        scroll.totalChildren(9);
        int i = 1;
        int child = 0 ;
        for(int x = 0 ; x < 3; x++){
        for(int y = 0; y < 3; y++){


                hoverButton10(89505+i,""+i, 1873, 1873, ""+i, font, 2, 0xdb9c22,0xdb9c22,true);
                scroll.child(0+child, 89505+i, 15+(x*50), 15 + (y*60));
                i++;
                child++;
            }
        }
        addText(87331, "Pick a number!", font, 2, 0xFF981F, true, true);


        widget.totalChildren(3);
        widget.child(0, 89501, 150,40);
        widget.child(1, 89098, 155,80);
        widget.child(2, 87331, 240,60);

    }
}
