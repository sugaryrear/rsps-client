package com.ferox.cache.graphics.widget.impl;

import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.Widget;

/**
 *
 * The class which represents functionality for the price checker interface.
 *
 * @author Patrick van Elderen
 * @see <a href="https://www.rune-server.ee/members/_Patrick_/">Rune-Server profile</a>
 *
 */
public class PriceChecker extends Widget {

    public static void unpack(AdvancedFont[] font) {

        Widget widget = addInterface(49500);
        addSprite(49501, 180);
        addContainer(49542, 7, 4, 40, 28, true, "Take 1", "Take 5", "Take 10", "Take All", "Take X");
        closeButton(49502, 107, 108, true);
        addHoverButton(49505, 181, 36, 36, "Add all", -1, 49506, 1);
        addHoveredButton(49506, 182, 36, 36, 49507);
        addHoverButton(49508, 183, 36, 36, "Search for item", -1, 49509, 1);
        addHoveredButton(49509, 184, 36, 36, 49510);
        addText(49511, "Grand Exchange guide prices", font, 2, 0xFF981F, true, true);
        addText(49512, "Total guide price:", font, 1, 0xFF981F, true, true);
        addText(49513, "115,424,152", font, 0, 0xffffff, true, true);
        addText(49550, "", font, 0, 0xffffff, true, true);
        addText(49551, "", font, 0, 0xffffff, true, true);
        addText(49552, "", font, 0, 0xffffff, true, true);
        addText(49553, "", font, 0, 0xffffff, true, true);
        addText(49554, "", font, 0, 0xffffff, true, true);
        addText(49555, "", font, 0, 0xffffff, true, true);
        addText(49556, "", font, 0, 0xffffff, true, true);
        addText(49557, "", font, 0, 0xffffff, true, true);
        addText(49558, "", font, 0, 0xffffff, true, true);
        addText(49559, "", font, 0, 0xffffff, true, true);
        addText(49560, "", font, 0, 0xffffff, true, true);
        addText(49561, "", font, 0, 0xffffff, true, true);
        addText(49562, "", font, 0, 0xffffff, true, true);
        addText(49563, "", font, 0, 0xffffff, true, true);
        addText(49564, "", font, 0, 0xffffff, true, true);
        addText(49565, "", font, 0, 0xffffff, true, true);
        addText(49566, "", font, 0, 0xffffff, true, true);
        addText(49567, "", font, 0, 0xffffff, true, true);
        addText(49568, "", font, 0, 0xffffff, true, true);
        addText(49569, "", font, 0, 0xffffff, true, true);
        addText(49570, "", font, 0, 0xffffff, true, true);
        addText(49571, "", font, 0, 0xffffff, true, true);
        addText(49572, "", font, 0, 0xffffff, true, true);
        addText(49573, "", font, 0, 0xffffff, true, true);
        addText(49574, "", font, 0, 0xffffff, true, true);
        addText(49575, "", font, 0, 0xffffff, true, true);
        addText(49576, "", font, 0, 0xffffff, true, true);
        addText(49577, "", font, 0, 0xffffff, true, true);
        addContainer(49581, 7, 4, 40, 28, true, new String[] { null, null, null, null, null });
        addText(49582, "", font, 0, 0xffffff, false, true);
        addText(49583, "", font, 0, 0xffffff, false, true);
        widget.totalChildren(41);
        widget.child(0, 49501, 15, 15);
        widget.child(1, 49502, 467, 22);
        widget.child(2, 49505, 451, 285);
        widget.child(3, 49506, 451, 285);
        widget.child(4, 49508, 25, 285);
        widget.child(5, 49509, 25, 285);
        widget.child(6, 49511, 260, 22);
        widget.child(7, 49512, 255, 290);
        widget.child(8, 49513, 255, 310);
        widget.child(9, 49542, 24, 57);
        widget.child(10, 49550, 39, 70);
        widget.child(11, 49551, 105, 70);
        widget.child(12, 49552, 182, 70);
        widget.child(13, 49553, 254, 70);
        widget.child(14, 49554, 326, 70);
        widget.child(15, 49555, 400, 70);
        widget.child(16, 49556, 468, 70);
        widget.child(17, 49557, 39, 133);
        widget.child(18, 49558, 110, 133);
        widget.child(19, 49559, 182, 133);
        widget.child(20, 49560, 254, 133);
        widget.child(21, 49561, 326, 133);
        widget.child(22, 49562, 400, 133);
        widget.child(23, 49563, 468, 133);
        widget.child(24, 49564, 39, 194);
        widget.child(25, 49565, 110, 194);
        widget.child(26, 49566, 182, 194);
        widget.child(27, 49567, 254, 194);
        widget.child(28, 49568, 326, 194);
        widget.child(29, 49569, 400, 194);
        widget.child(30, 49570, 468, 194);
        widget.child(31, 49571, 39, 256);
        widget.child(32, 49572, 110, 256);
        widget.child(33, 49573, 182, 256);
        widget.child(34, 49574, 254, 256);
        widget.child(35, 49575, 326, 256);
        widget.child(36, 49576, 400, 256);
        widget.child(37, 49577, 468, 256);
        widget.child(38, 49581, 12, 291);
        widget.child(39, 49582, 51, 296);
        widget.child(40, 49583, 51, 312);
    }
}
