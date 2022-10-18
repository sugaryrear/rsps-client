package com.ferox.cache.graphics.widget.impl;

import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.Widget;

import static com.ferox.util.ConfigUtility.*;

/**
 * @author Patrick van Elderen | January, 19, 2021, 18:37
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class DonationPromoWidget extends Widget {

    public static void unpack(AdvancedFont[] font) {
        Widget widget = addTabInterface(27400);
        addSprite(27401, 1435);
        closeButton(27402, 24, 25, false);
        addText(27403, "Donation Promo Manager", font, 2, 0xF7AA25, true, true);
        addText(27404, "Active Donation Deals", font, 2, 0xffff00, true, true);
        addText(27405, "Information", font, 2, 0xffff00, true, true);
        addText(27406, "Promotions", font, 2, 0xffff00, true, true);
        addText(27407, "Lorem ipsum dolor sit amet, consectetur<br>adipiscing elit, sed do eiusmod tempor<br>incididuntut labore et dolore magna aliqua.", font, 1, 0xffff00, false, true);
        addText(27408, "Lorem ipsum dolor sit amet, consectetur<br>adipiscing elit, sed do eiusmod tempor incididunt<br>ut labore et dolore magna aliqua.", font, 1, 0xffff00, false, true);
        addText(27409, "Your promotion deal will reset once you have<br>reached the last item in the bracket. Promotion<br>" + "items will be changed after you've claimed them<br>" + "all. However your donation amount will stay the<br>" + "same!", font, 1, 0xffff00, false, true);
        addText(27410, "$0", font, 2, 0xffff00, false, true);
        addText(27411, "$150", font, 2, 0xffff00, false, true);
        addText(27412, "$500", font, 2, 0xffff00, false, true);
        addText(27413, "$750", font, 2, 0xffff00, false, true);
        addText(27414, "$1000", font, 2, 0xffff00, false, true);
        addItem(27415, false);
        addItem(27416, false);
        addItem(27417, false);
        addItem(27418, false);
        addConfigButton(27419, 27400, -1, 1436, "", PROMOTION_ONE, 0, 5);
        addConfigButton(27420, 27400, -1, 1436, "", PROMOTION_TWO, 0, 5);
        addConfigButton(27421, 27400, -1, 1436, "", PROMOTION_THREE, 0, 5);
        addConfigButton(27422, 27400, -1, 1436, "", PROMOTION_FOUR, 0, 5);

        widget.totalChildren(22);
        widget.child(0, 27401, 13, 1);
        widget.child(1, 27402, 485, 10);
        widget.child(2, 27403, 270, 9);
        widget.child(3, 27404, 145, 48);
        widget.child(4, 27405, 112, 217);
        widget.child(5, 27406, 410, 53);
        widget.child(6, 27407, 50, 75);
        widget.child(7, 27408, 50, 146);
        widget.child(8, 27409, 46, 240);
        widget.child(9, 27410, 425, 287);
        widget.child(10, 27411, 450, 267);
        widget.child(11, 27412, 347, 216);
        widget.child(12, 27413, 450, 167);
        widget.child(13, 27414, 375, 103);
        widget.child(14, 27415, 451, 233);
        widget.child(15, 27416, 345, 180);
        widget.child(16, 27417, 450, 132);
        widget.child(17, 27418, 399, 70);
        widget.child(18, 27419, 468, 252);
        widget.child(19, 27420, 360, 200);
        widget.child(20, 27421, 468, 150);
        widget.child(21, 27422, 415, 88);
    }
}
