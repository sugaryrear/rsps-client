package com.ferox.cache.graphics.widget.impl;

import com.ferox.ClientConstants;
import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.Widget;

/**
 * @author Patrick van Elderen | December, 09, 2020, 10:45
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class ReferralWidget extends Widget {

    public static void unpack(AdvancedFont[] font) {
        Widget widget = addInterface(70000);

        addSprite(70001, 1386);
        closeButton(70002, 24, 25,true);
        addText(70003, ClientConstants.CLIENT_NAME+" - Referral Manager", font, 2, 16750623);
        hoverButton(70005, "Fill in referral", 448, 447, "Fill in referral", font, 1, 0xff8a1f, 0xff8a1f, true);
        hoverButton(70006, "Claim reward", 448, 447, "Claim reward", font, 1, 0xff8a1f, 0xff8a1f, true);

        widget.totalChildren(6);
        widget.child(0, 70001, 5, 7);
        widget.child(1, 70002, 480, 17);
        widget.child(2, 70003, 156, 20);
        widget.child(3, 70010, 45, 77);
        widget.child(4, 70005, 50, 270);
        widget.child(5, 70006, 315, 270);

        Widget scroll_widget = addTabInterface(70010);
        scroll_widget.scrollPosition = 0;
        scroll_widget.contentType = 0;
        scroll_widget.width = 439;
        scroll_widget.height = 179;
        scroll_widget.scrollMax = 275;
        int y = 4;
        final int CHILD_LENGTH = 100 * 4;
        int child = 0;
        scroll_widget.totalChildren(CHILD_LENGTH);
        for (int lineIndex = 70020; lineIndex < 70020 + CHILD_LENGTH; lineIndex+= 4) {
            addText(lineIndex, "Malefique", font , 0,0xc9a749, true);
            addText(lineIndex + 1, "9 hours 58 minutes", font , 0,0xc9a749, true);
            addText(lineIndex + 2, "$5 bond", font , 0,0xc9a749, true);
            addText(lineIndex + 3, "Unclaimed", font , 0,0xc9a749, true);

            scroll_widget.child(child++, lineIndex, 35, y);
            scroll_widget.child(child++, lineIndex + 1, 170, y);
            scroll_widget.child(child++, lineIndex + 2, 280, y);
            scroll_widget.child(child++, lineIndex + 3, 391, y);
            y+=23;
        }
    }
}
