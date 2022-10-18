package com.ferox.cache.graphics.widget.impl;

import com.ferox.ClientConstants;
import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.Widget;

public class LogoutWidget extends Widget {

    public static void unpack(AdvancedFont[] fonts) {
        logout(fonts);
    }

    private static void logout(AdvancedFont[] fonts) {
        Widget logout = cache[2449];
        logout.totalChildren(14);
        setBounds(2455, 3 + 20, 210, 5, logout);
        setBounds(2454, 27 + 20, 210, 4, logout);
        setBounds(2453, 53 + 20, 210, 3, logout);
        setBounds(2456, 83 + 20, 210, 6, logout);
        setBounds(2457, 111 + 20, 210, 7, logout);
        addText(2450, "Did you enjoy playing", fonts, 1, 0xFF9300, true, true);
        setBounds(2450, 95, 14, 0, logout);
        addText(2451, ClientConstants.CLIENT_NAME+" today?", fonts, 1, 0xFF9300, true, true);
        setBounds(2451, 94, 32, 1, logout);
        addHoverText(2458, "Click here to logout", "Logout", fonts, 2, 0xF8F0DD, true, true, 148, 36, 0xFF0000);
        setBounds(2458, 21, 220, 8, logout);

        addSprite(2452, 805);
        setBounds(2452, 25, 168, 2, logout);

        addHoverText(12454, "World Switcher", "World Switcher", fonts, 2, 0xF8F0DD, true, true, 148, 36, 0xC0C0C0);
        setBounds(12454, 21, 176, 9, logout);

        addText(12495, "Use the buttons below to", fonts, 1, 0xFF9300, true, true);
        setBounds(12495, 94, 121, 10, logout);

        addText(12456, "logout or switch worlds safely.", fonts, 1, 0xFF9300, true, true);
        setBounds(12456, 94, 139, 11, logout);

        configHoverButtonSpriteOutline(4141, "Thumbs Up", 806, 809, 807, false, 4, 15, 0, 4142);
        configHoverButtonSpriteOutline(4142, "Thumbs Down", 806, 809, 808, false, 4, 15, 0, 4141);

        setBounds(4141, 28, 62, 12, logout);
        setBounds(4142, 100, 62, 13, logout);
    }

}
