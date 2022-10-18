package com.ferox.cache.graphics.widget.impl;

import com.ferox.Client;
import com.ferox.ClientConstants;
import com.ferox.cache.graphics.SimpleImage;
import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.Widget;
import com.ferox.util.ConfigUtility;

/**
 * This class represents the bank interface, referenced: https://oldschool.runescape.wiki/w/Bank
 * @author Zerikoth | 1 sep. 2019 : 14:39:32
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 *
 */
public class BankWidget extends Widget {

    public static void unpack(AdvancedFont[] font) {
        bank(font);
        bank_settings(font);
    }

    private static void bank(AdvancedFont[] font) {
        int interfaceId = 26000;
        Widget bank = addInterface(interfaceId);
        bank.totalChildren(71);
        int child = 9;
        for (int tab = 0; tab < 40; tab += 4) {
            addButton(interfaceId + 31 + tab, Client.spriteCache.get(109), Client.spriteCache.get(109),
                    "Collapse " + (tab == 0 ? "<col=ff7000>all tabs" : "tab <col=ff7000>" + (tab / 4)), 39, 40);
            int[] array = { 21, (tab / 4), 0 };
            if (tab / 4 == 0) {
                array = new int[] { 5, 211, 0 };
            }
            addHoverConfigButton(interfaceId + 32 + tab, interfaceId + 33 + tab, 110, 109, 39, 40,
                    tab == 0 ? "View all" : "New tab", new int[] { 1, tab / 4 == 0 ? 1 : 3 },
                    new int[] { (tab / 4), 0 }, new int[][] { { 5, 211, 0 }, array });
            addHoveredConfigButton(cache[interfaceId + 32 + tab], interfaceId + 33 + tab,
                    interfaceId + 34 + tab, 111, 109);
            cache[interfaceId + 32 + tab].parent = interfaceId;
            cache[interfaceId + 33 + tab].parent = interfaceId;
            cache[interfaceId + 34 + tab].parent = interfaceId;
            bank.child(child++, interfaceId + 31 + tab, 55 + 40 * (tab / 4), 37);
            bank.child(child++, interfaceId + 32 + tab, 55 + 40 * (tab / 4), 37);
            bank.child(child++, interfaceId + 33 + tab, 55 + 40 * (tab / 4), 37);
        }
        addSprite(interfaceId + 1, 106);
        addHoverButton(interfaceId + 2, 107, 25, 25, "Close", -1, interfaceId + 3, 1);
        addHoveredButton(interfaceId + 3, 108, 25, 25, interfaceId + 4);
        addText(interfaceId + 5, "The Bank of "+ ClientConstants.CLIENT_NAME, font, 2, 0xFE9624, true, true);

        SimpleImage disabled = Client.spriteCache.get(766);
        SimpleImage enabled = Client.spriteCache.get(767);

        adjustableConfig(interfaceId + 101, "Toggle <col=ff9040>Always set placeholders", 726, 180, 728, 727);
        adjustableConfig(interfaceId + 102, "Search", 729, 180, 728, 727);
        adjustableConfig(interfaceId + 104, "Deposit worn items", 731, 180, 728, 727);
        adjustableConfig(interfaceId + 103, "Deposit inventory", 730, 180, 728, 727);
        addConfigButton(interfaceId + 105, 26000, 122, 122, "", 120, 0, 5);
        addConfigButton(interfaceId + 106, 26000, 123, 123, "Show menu", 121, 0, 5);

        // above config buttons
        addText(interfaceId + 94, "Rearrange mode:", font, 1, 0xff981f, true, true);
        addText(interfaceId + 95, "Withdraw as:", font, 1, 0xff981f, true, true);
        addText(interfaceId + 107, "Quantity:", font, 1, 0xff981f, true, true);

        // on the config buttons
        addText(interfaceId + 96, "Swap", font, 1, 0xff981f, true, true);
        addText(interfaceId + 97, "Insert", font, 1, 0xff981f, true, true);

        addText(interfaceId + 98, "Item", font, 1, 0xff981f, true, true);
        addText(interfaceId + 99, "Note", font, 1, 0xff981f, true, true);

        addText(interfaceId + 17, "%1", font, 0, 0xFE9624, true);
        cache[interfaceId + 17].valueIndexArray = new int[][] { { 22, 5382, 0 } };
        addText(interfaceId + 18, "size", font, 0, 0xFE9624, true, true);
        addInputField(interfaceId + 19, 25, 0xFF981F, "Search...", 125, 22, false, true, "[A-Za-z0-9 ]");
        addHoverButton(interfaceId + 20, 124, 0, 0, "", -1, interfaceId + 21, 1);
        addHoveredButton(interfaceId + 21, 124, 0, 0, interfaceId + 22);
        addSprite(interfaceId + 29, 208);
        addContainer(39414, TYPE_CONTAINER, 3, 2, 10, 8, 0, false, true, true);

        addContainer(5382, 109, 8, 102, "Withdraw-1", "Withdraw-5", "Withdraw-10", "Withdraw-All", "Withdraw-X", null,
                "Withdraw-All but one");
        Widget line = addInterface(interfaceId + 100);
        line.type = 3;
        line.allowSwapItems = true;
        line.width = 12;
        line.height = 1;
        line.textColour = 0xFE9624;
        cache[5385].width += 22;
        cache[5385].height -= 18;
        cache[5385].scrollMax = 1444;
        cache[5382].contentType = 206;
        bank.child(0, interfaceId + 1, 12, 2);
        bank.child(1, interfaceId + 2, 472, 9);
        bank.child(2, interfaceId + 3, 472, 9);
        bank.child(3, interfaceId + 5, 260, 12);
        bank.child(4, interfaceId + 17, 30, 8);
        bank.child(5, interfaceId + 18, 30, 20);
        bank.child(6, interfaceId + 19, 9999, 9999); //Set this old search input off-screen.
        bank.child(7, interfaceId + 20, 195, 300);
        bank.child(8, interfaceId + 21, 195, 300);
        bank.child(51 - 6 - 6, interfaceId + 29, 58, 42);
        bank.child(52 - 6 - 6, 5385, 30, 80);
        bank.child(41, 8130, 17, 308);
        bank.child(42, 8131, 76, 308);
        bank.child(43, 5386, 197, 308);
        bank.child(44, 5387, 138, 308);
        bank.child(45, interfaceId + 94, 65, 291);
        bank.child(46, interfaceId + 95, 160, 291);
        bank.child(47, interfaceId + 96, 40, 310);
        bank.child(48, interfaceId + 97, 92, 310);
        bank.child(49, interfaceId + 98, 142, 310);
        bank.child(50, interfaceId + 99, 192, 310);
        bank.child(51, interfaceId + 100, 24, 19);
        bank.child(52, interfaceId + 101, 343, 291);
        bank.child(53, interfaceId + 102, 382, 291);
        bank.child(54, interfaceId + 103, 421, 291);
        bank.child(55, interfaceId + 104, 458, 291);
        bank.child(56, interfaceId + 105, 462, 45);
        bank.child(57, interfaceId + 106, 467, 48);
        bank.child(58, interfaceId + 107, 270, 291);
        bank.child(59, interfaceId + 108, 317, 308);
        bank.child(60, interfaceId + 109, 292, 308);
        bank.child(61, interfaceId + 110, 267, 308);
        bank.child(62, interfaceId + 111, 242, 308);
        bank.child(63, interfaceId + 112, 217, 308);
        bank.child(64, interfaceId + 113, 329, 310);
        bank.child(65, interfaceId + 114, 303, 310);
        bank.child(66, interfaceId + 115, 279, 310);
        bank.child(67, interfaceId + 116, 254, 310);
        bank.child(68, interfaceId + 117, 229, 310);
        bank.child(69, interfaceId + 118, 300, 14);
        bank.child(70, interfaceId + 119, 25, 43);

        int[] interfaces = new int[] { 5386, 5387, 8130, 8131 };

        for (int rsint : interfaces) {
            cache[rsint].disabledSprite = disabled;
            cache[rsint].enabledSprite = enabled;
            cache[rsint].width = enabled.width;
            cache[rsint].height = enabled.height;
        }

        cache[8130].enabledSprite = Client.spriteCache.get(893);
        cache[8130].disabledSprite = Client.spriteCache.get(894);
        cache[8130].width = 48;
        cache[8130].tooltip = "Swap";

        cache[8131].enabledSprite = Client.spriteCache.get(893);
        cache[8131].disabledSprite = Client.spriteCache.get(894);
        cache[8131].x = -9;
        cache[8131].width = 48;
        cache[8131].tooltip = "Insert";

        cache[5387].enabledSprite = Client.spriteCache.get(893);
        cache[5387].disabledSprite = Client.spriteCache.get(894);
        cache[5387].x = -21;
        cache[5387].width = 48;
        cache[5387].tooltip = "Item";

        cache[5386].enabledSprite = Client.spriteCache.get(893);
        cache[5386].disabledSprite = Client.spriteCache.get(894);
        cache[5386].x = -30;
        cache[5386].width = 48;
        cache[5386].tooltip = "Note";

        addConfigButton(interfaceId + 108, interfaceId, 891, 892, "Default quantity: All",
                ConfigUtility.BANK_QUANTITY_ALL, 0, OPTION_OK);
        addConfigButton(interfaceId + 109, interfaceId, 891, 892, "Default quantity: X", ConfigUtility.BANK_QUANTITY_X,
                0, OPTION_OK);
        addConfigButton(interfaceId + 110, interfaceId, 891, 892, "Default quantity: 10",
                ConfigUtility.BANK_QUANTITY_TEN, 0, OPTION_OK);
        addConfigButton(interfaceId + 111, interfaceId, 891, 892, "Default quantity: 5",
                ConfigUtility.BANK_QUANTITY_FIVE, 0, OPTION_OK);
        addConfigButton(interfaceId + 112, interfaceId, 891, 892, "Default quantity: 1",
                ConfigUtility.BANK_QUANTITY_ONE, 0, OPTION_OK);

        addText(interfaceId + 113, "All", font, 1, 0xff981f, true, true);
        addText(interfaceId + 114, "X", font, 1, 0xff981f, true, true);
        addText(interfaceId + 115, "10", font, 1, 0xff981f, true, true);
        addText(interfaceId + 116, "5", font, 1, 0xff981f, true, true);
        addText(interfaceId + 117, "1", font, 1, 0xff981f, true, true);
        addText(interfaceId + 118, "", font, 0, 0xFF981F, false, true);
        addButton(interfaceId + 119,1419,"Show worn items");
    }

    private static void bank_settings(AdvancedFont[] font) {
        Widget bank_settings = addInterface(34000);
        bank_settings.totalChildren(41);

        addSprite(34000 + 1, 116);
        closeButton(34000 + 2, 107, 108, false);
        addText(34000 + 3, "Bank settings menu", font, 2, 0xFE9624, true, true);

        addButton(34000 + 4, 121, "Dismiss menu");
        addSprite(34000 + 5, 123);
        Widget line = addInterface(34000 + 100);
        line.type = 3;
        line.allowSwapItems = true;
        line.width = 12;
        line.height = 1;
        line.textColour = 0xFE9624;
        addOutlinedColorBox(34000 + 6, 0x534a40, 278, 90, 100);
        addText(34000 + 7, "Tab display:", font, 2, 0xFE9624, true, true);
       
        addConfigButton(34000 + 8, 34000, 490, 641, "First item", 750, 0, OPTION_OK);
        addHoverText(34000 + 9, "First item in tab", "First item in tab", font, 1, 0xFE9624, true, true, 90, 0xffffff);
        
        addConfigButton(34000 + 10, 34000, 490, 641, "Digit", 751, 0, OPTION_OK);
        addHoverText(34000 + 11, "Digit (1,2,3)", "Digit (1,2,3)", font, 1, 0xFE9624, true, true, 68, 0xffffff);
        
        addConfigButton(34000 + 12, 34000, 490, 641, "Roman numeral", 752, 0, OPTION_OK);
        addHoverText(34000 + 13, "Roman numeral (I, II, III)", "Roman numeral (I, II, III)", font, 1, 0xFE9624, true, true, 126, 0xffffff);
        
        addConfigButton(34000 + 14, 34000, 490, 491, "", 753, 1, OPTION_OK);
        addText(34000 + 15, "<str>Hide tab bar", font, 1, 0xFE9624, true, true);
        
        addConfigButton(34000 + 16, 34000, 491, 641, Client.singleton.setting.incinerator ? "Disable Incinerator" : "Enable Incinerator", 754, 0, OPTION_OK);
        addHoverText(34000 + 17, "Incinerator", "Incinerator", font, 1, 0xFE9624, true, true, 65, 0xffffff);
        
        addConfigButton(34000 + 18, 34000, 491, 641, Client.singleton.setting.hide_equipment_button ? "Hide 'Deposit worn items' button" : "Show 'Deposit worn items' button", 755, 0, OPTION_OK);
        addHoverText(34000 + 19, "'Deposit worn items' button", "'Deposit worn items' button", font, 1, 0xFE9624, true, true, 155, 0xffffff);
        
        addConfigButton(34000 + 20, 34000, 491, 641, Client.singleton.setting.hide_inventory_button ? "Hide 'Deposit inventory' button" : "Show 'Deposit inventory' button", 756, 0, OPTION_OK);
        addHoverText(34000 + 21, "'Deposit inventory' button", "'Deposit inventory' button", font, 1, 0xFE9624, true, true, 152, 0xffffff);

        addSprite(34000 + 22, 1141);
        addSprite(34000 + 23, 726);
        addCustomClickableText(34000 + 24, "Release all placeholders (0)","Release all placeholders", font, 1, 0xFE9624, false, true, 155, 13);
        
        addOutlinedColorBox(34000 + 25, 0x534a40, 202, 103, 100);
        addText(34000 + 26, "Bank Fillers", font, 2, 0xFE9624, true, true);
        
        addConfigButton(34000 + 27, 34000, 125, 126, "All", 757, 1, OPTION_OK);
        addText(34000 + 28, "All", font, 2, 0xffffff, true, true);
        
        addConfigButton(34000 + 29, 34000, 125, 126, "X", 757, 0, OPTION_OK);
        addText(34000 + 30, "X", font, 2, 0xffffff, true, true);
        
        addConfigButton(34000 + 31, 34000, 125, 126, "50", 757, 0, OPTION_OK);
        addText(34000 + 32, "50", font, 2, 0xffffff, true, true);
        
        addConfigButton(34000 + 33, 34000, 125, 126, "10", 757, 0, OPTION_OK);
        addText(34000 + 34, "10", font, 2, 0xffffff, true, true);
        
        addConfigButton(34000 + 35, 34000, 125, 126, "1", 757, 0, OPTION_OK);
        addText(34000 + 36, "1", font, 2, 0xffffff, true, true);

        addButton(34000 + 37, 119, "Fill");
        addText(34000 + 38, "Fill", font, 2, 0xffffff, true, true);
        
        setBounds(34000 + 1, 12, 2, 0, bank_settings);
        setBounds(34000 + 2, 472, 9, 1, bank_settings);
        setBounds(34000 + 3, 255, 12, 2, bank_settings);
        setBounds(34000 + 4, 462, 45, 3, bank_settings);
        setBounds(34000 + 5, 467, 48, 4, bank_settings);
        setBounds(26000 + 17, 30, 8, 5, bank_settings);
        setBounds(26000 + 18, 30, 20, 6, bank_settings);
        setBounds(34000 + 6, 115, 49, 7, bank_settings);
        setBounds(34000 + 7, 157, 52, 8, bank_settings);
        setBounds(34000 + 8, 117, 68, 9, bank_settings);
        setBounds(34000 + 9, 136, 69, 10, bank_settings);
        setBounds(34000 + 10, 117, 86, 11, bank_settings);
        setBounds(34000 + 11, 136, 87, 12, bank_settings);
        setBounds(34000 + 12, 117, 104, 13, bank_settings);
        setBounds(34000 + 13, 140, 105, 14, bank_settings);
        setBounds(34000 + 14, 117, 121, 15, bank_settings);
        setBounds(34000 + 15, 174, 123, 16, bank_settings);
        setBounds(34000 + 16, 21, 150, 17, bank_settings);
        setBounds(34000 + 17, 40, 150, 18, bank_settings);
        setBounds(34000 + 18, 125, 150, 19, bank_settings);
        setBounds(34000 + 19, 147, 150, 20, bank_settings);
        setBounds(34000 + 20, 318, 150, 21, bank_settings);
        setBounds(34000 + 21, 338, 150, 22, bank_settings);
        setBounds(34000 + 22, 12, -2, 23, bank_settings);
        setBounds(34000 + 23, 169, 183, 24, bank_settings);
        setBounds(34000 + 24, 188, 185, 25, bank_settings);
        setBounds(34000 + 25, 153, 217, 26, bank_settings);
        setBounds(34000 + 26, 255, 220, 27, bank_settings);
        setBounds(34000 + 27, 315, 245, 28, bank_settings);
        setBounds(34000 + 28, 332, 256, 29, bank_settings);
        setBounds(34000 + 29, 277, 245, 30, bank_settings);
        setBounds(34000 + 30, 294, 256, 31, bank_settings);
        setBounds(34000 + 31, 238, 245, 32, bank_settings);
        setBounds(34000 + 32, 254, 256, 33, bank_settings);
        setBounds(34000 + 33, 198, 245, 34, bank_settings);
        setBounds(34000 + 34, 216, 256, 35, bank_settings);
        setBounds(34000 + 35, 158, 245, 36, bank_settings);
        setBounds(34000 + 36, 175, 256, 37, bank_settings);
        setBounds(34000 + 37, 155, 280, 38, bank_settings);
        setBounds(34000 + 38, 254, 293, 39, bank_settings);
        setBounds(34000 + 100, 24, 19, 40, bank_settings);
    }
}
