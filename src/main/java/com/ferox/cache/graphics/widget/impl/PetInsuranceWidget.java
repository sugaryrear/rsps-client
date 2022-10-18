package com.ferox.cache.graphics.widget.impl;

import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.Widget;

/**
 * @author Patrick van Elderen | January, 04, 2021, 16:46
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class PetInsuranceWidget extends Widget {

    public static void unpack(AdvancedFont[] font) {
        Widget widget = addInterface(29000);

        addSprite(29001, 889);
        // Close
        closeButton(29002, 107, 108, false);

        // Text
        addText(29005, "Pet insurance", font, 2, 0xFF981F, true, true);
        addText(29006, "You have insured these pets:", font, 1, 0xFF981F, false, true);
        addText(29007, "You may reclaim these pets now:", font, 1, 0xFF981F, false, true);
        addText(29008, "None", font, 1, 0xFFFFFF, true, true);
        addText(29009, "None", font, 1, 0xFFFFFF, true, true);

        widget.totalChildren(9);
        widget.child(0, 29001, 56, 18);
        widget.child(1, 29002, 428, 24);
        widget.child(2, 29005, 255, 27);
        widget.child(3, 29006, 75, 67);
        widget.child(4, 29007, 75, 191);
        widget.child(5, 29008, 255, 120);
        widget.child(6, 29009, 255, 244);
        widget.child(7, 29010, 78, 82);
        widget.child(8, 29012, 78, 206);

        Widget insured = addInterface(29010);

        addContainer(29011, 7, 7, 18, 10, false);

        insured.totalChildren(1);
        insured.child(0, 29011, 0, 5);

        insured.width = 364 - 20;
        insured.height = 90;
        insured.scrollMax = 220;

        Widget reclaim = addInterface(29012);

        addContainer(29013, 7, 7, 18, 10, false,"Reclaim");

        reclaim.totalChildren(1);
        reclaim.child(0, 29013, 0, 5);

        reclaim.width = 364 - 20;
        reclaim.height = 90;
        reclaim.scrollMax = 220;
    }
}
