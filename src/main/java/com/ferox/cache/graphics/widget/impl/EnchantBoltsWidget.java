package com.ferox.cache.graphics.widget.impl;

import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.Widget;

import static com.ferox.util.ItemIdentifiers.*;

/**
 * @author Patrick van Elderen | March, 06, 2021, 23:49
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class EnchantBoltsWidget extends Widget {

    public static void unpack(AdvancedFont[] font) {
        Widget tab = addTabInterface(39000);

        // Background
        addSprite(39001, 1451);

        // Close button
        closeButton(39002,107,108,false);

        // Title
        addText(39086, "Enchant Crossbow Bolts", 0xff9933, true, true, -1, font, 2);

        /*
         * Opal
         */
        addHoverButton(39006, "b", 1, 65, 110, "Enchant Opal Bolts", -1, 39007, 1);
        addText(39008, "Opal", font, 1, 0xff9040, true, true);
        addText(39009, "<col=65280>Magic 4", font, 1, 0xff9040, true, true);
        addItemModel(39005, COSMIC_RUNE, 1, 1, 850);
        addItemModel(39010, OPAL_BOLTS, 1, 1, 500);
        addItemModel(39011, AIR_RUNE, 1, 1, 850);
        addText(39012, "<col=ff0000>0/1", 0xff9b00, false, true, 52, font, 0); // Cosmic text
        addText(39013, "<col=ff0000>0/2", 0xff9b00, false, true, 52, font, 0); // Air text

        /*
         * Sapphire
         */
        addHoverButton(39015, "b", 1, 65, 110, "Enchant Sapphire Bolts", -1, 39007, 1);
        addText(39016, "Sapphire", font, 1, 0xff9040, true, true);
        addText(39017, "<col=65280>Magic 7", font, 1, 0xff9040, true, true);
        addItemModel(39014, COSMIC_RUNE, 1, 1, 850);
        addItemModel(39018, SAPPHIRE_BOLTS, 1, 1, 500);
        addItemModel(39019, WATER_RUNE, 1, 1, 850);
        addItemModel(39087, MIND_RUNE, 1, 1, 850);
        addText(39020, "<col=ff0000>0/1", 0xff9b00, false, true, 52, font, 0); // Cosmic text
        addText(39021, "<col=ff0000>0/1", 0xff9b00, false, true, 52, font, 0); // Water text
        addText(39088, "<col=ff0000>0/1", 0xff9b00, false, true, 52, font, 0); // Mind text

        /*
         * Jade
         */
        addItemModel(39022, COSMIC_RUNE, 1, 1, 850); // Cosmic rune
        addHoverButton(39023, "b", 1, 65, 110, "Enchant Jade Bolts", -1, 39007, 1);
        addText(39024, "Jade", font, 1, 0xff9040, true, true);
        addText(39025, "<col=65280>Magic 14", font, 1, 0xff9040, true, true);
        addItemModel(39026, JADE_BOLTS, 1, 1, 500); // bolts
        addItemModel(39027, EARTH_RUNE, 1, 1, 850); // Earth rune
        addText(39028, "<col=ff0000>0/1", 0xff9b00, false, true, 52, font, 0); // Cosmic text
        addText(39029, "<col=ff0000>0/2", 0xff9b00, false, true, 52, font, 0); // Earth text

        /*
         * Pearl
         */
        addItemModel(39030, 564, 1, 1, 850); // Cosmic rune
        addHoverButton(39031, "b", 1, 65, 110, "Enchant Pearl Bolts", -1, 39007, 1);
        addText(39032, "Pearl", font, 1, 0xff9040, true, true);
        addText(39033, "<col=65280>Magic 24", font, 1, 0xff9040, true, true);
        addItemModel(39034, 9238, 1, 1, 500); // bolts
        addItemModel(39035, 555, 1, 1, 850); // Water rune
        addText(39036, "<col=ff0000>0/1", 0xff9b00, false, true, 52, font, 0); // Cosmic text
        addText(39037, "<col=ff0000>0/2", 0xff9b00, false, true, 52, font, 0); // Water text

        /*
         * Emerald
         */
        addItemModel(39038, COSMIC_RUNE, 1, 1, 850);
        addHoverButton(39039, "b", 1, 65, 110, "Enchant Emerald Bolts", -1, 39007, 1);
        addText(39040, "Emerald", font, 1, 0xff9040, true, true);
        addText(39041, "<col=65280>Magic 27", font, 1, 0xff9040, true, true);
        addItemModel(39042, EMERALD_BOLTS, 1, 1, 500);
        addItemModel(39089, AIR_RUNE, 1, 1, 850);
        addItemModel(39043, NATURE_RUNE, 1, 1, 850);
        addText(39044, "<col=ff0000>0/1", 0xff9b00, false, true, 52, font, 0); // Cosmic text
        addText(39045, "<col=ff0000>0/3", 0xff9b00, false, true, 52, font, 0); // Air text
        addText(39090, "<col=ff0000>0/1", 0xff9b00, false, true, 52, font, 0); // Nature text

        /*
         * Topaz
         */
        addItemModel(39046, COSMIC_RUNE, 1, 1, 850);
        addHoverButton(39047, "b", 1, 65, 110, "Enchant Red Topaz Bolts", -1, 39007, 1);
        addText(39048, "Red Topaz", font, 1, 0xff9040, true, true);
        addText(39049, "<col=65280>Magic 29", font, 1, 0xff9040, true, true);
        addItemModel(39050, TOPAZ_BOLTS, 1, 1, 500);
        addItemModel(39051, FIRE_RUNE, 1, 1, 850);
        addText(39052, "<col=ff0000>0/1", 0xff9b00, false, true, 52, font, 0); // Cosmic text
        addText(39053, "<col=ff0000>0/2", 0xff9b00, false, true, 52, font, 0); // Fire text

        /*
         * Ruby
         */
        addItemModel(39054, COSMIC_RUNE, 1, 1, 850);
        addHoverButton(39055, "b", 1, 65, 110, "Enchant Ruby Bolts", -1, 39007, 1);
        addText(39056, "Ruby", font, 1, 0xff9040, true, true);
        addText(39057, "<col=65280>Magic 49", font, 1, 0xff9040, true, true);
        addItemModel(39058, RUBY_BOLTS, 1, 1, 500);
        addItemModel(39059, FIRE_RUNE, 1, 1, 850);
        addItemModel(39091, BLOOD_RUNE, 1, 1, 850);
        addText(39060, "<col=ff0000>0/1", 0xff9b00, false, true, 52, font, 0); // Cosmic text
        addText(39061, "<col=ff0000>0/5", 0xff9b00, false, true, 52, font, 0); // Fire text
        addText(39092, "<col=ff0000>0/1", 0xff9b00, false, true, 52, font, 0); // Blood text

        /*
         * Diamond
         */
        addItemModel(39062, COSMIC_RUNE, 1, 1, 850);
        addHoverButton(39063, "b", 1, 65, 110, "Enchant Diamond Bolts", -1, 39007, 1);
        addText(39064, "Diamond", font, 1, 0xff9040, true, true);
        addText(39065, "<col=65280>Magic 57", font, 1, 0xff9040, true, true);
        addItemModel(39066, DIAMOND_BOLTS, 1, 1, 500);
        addItemModel(39067, EARTH_RUNE, 1, 1, 850);
        addItemModel(39093, LAW_RUNE, 1, 1, 850);
        addText(39068, "<col=ff0000>0/1", 0xff9b00, false, true, 52, font, 0); // Cosmic text
        addText(39069, "<col=ff0000>0/10", 0xff9b00, false, true, 52, font, 0); // Earth text
        addText(39094, "<col=ff0000>0/2", 0xff9b00, false, true, 52, font, 0); // Law text

        /*
         * Dragon
         */
        addItemModel(39070, COSMIC_RUNE, 1, 1, 850);
        addHoverButton(39071, "b", 1, 65, 110, "Enchant Dragonstone Bolts", -1, 39007, 1);
        addText(39072, "Dragonstone", font, 1, 0xff9040, true, true);
        addText(39073, "<col=65280>Magic 68", font, 1, 0xff9040, true, true);
        addItemModel(39074, DRAGONSTONE_BOLTS, 1, 1, 500);
        addItemModel(39075, EARTH_RUNE, 1, 1, 850);
        addItemModel(39095, SOUL_RUNE, 1, 1, 850);
        addText(39076, "<col=ff0000>0/1", 0xff9b00, false, true, 52, font, 0); // Cosmic text
        addText(39077, "<col=ff0000>0/15", 0xff9b00, false, true, 52, font, 0); // Earth text
        addText(39096, "<col=ff0000>0/1", 0xff9b00, false, true, 52, font, 0); // Soul text

        /*
         * Onyx
         */
        addItemModel(39078, COSMIC_RUNE, 1, 1, 850);
        addHoverButton(39079, "b", 1, 65, 110, "Enchant Onyx Bolts", -1, 39007, 1);
        addText(39080, "Onyx", font, 1, 0xff9040, true, true);
        addText(39081, "<col=65280>Magic 87", font, 1, 0xff9040, true, true);
        addItemModel(39082, ONYX_BOLTS, 1, 1, 500);
        addItemModel(39083, FIRE_RUNE, 1, 1, 850);
        addItemModel(39097, DEATH_RUNE, 1, 1, 850);
        addText(39084, "<col=ff0000>0/1", 0xff9b00, false, true, 52, font, 0); // Cosmic text
        addText(39085, "<col=ff0000>0/20", 0xff9b00, false, true, 52, font, 0); // Fire text
        addText(39098, "<col=ff0000>0/1", 0xff9b00, false, true, 52, font, 0); // Death text

        tab.totalChildren(95);
        tab.child(0, 39001, 12, 15); // Background
        tab.child(1, 39002, 472, 22); // Close button

        /*
         * Opal
         */
        tab.child(2, 39005, 40, 145); // Cosmic rune
        tab.child(3, 39006, 30, 60); // options
        tab.child(4, 39008, 62, 53); // title
        tab.child(5, 39009, 62, 67); // magic lvl
        tab.child(6, 39010, 60, 103); // bolts
        tab.child(7, 39011, 75, 145); // Air rune
        tab.child(8, 39012, 32, 160); // Cosmic text
        tab.child(9, 39013, 67, 160); // Air text
        /*
         * Sapphire
         */
        tab.child(10, 39014, 125, 145); // Cosmic rune
        tab.child(11, 39015, 130, 60); // options
        tab.child(12, 39016, 152, 53); // title
        tab.child(13, 39017, 152, 67); // magic lvl
        tab.child(14, 39018, 152, 103); // bolts
        tab.child(15, 39019, 155, 145); // Water rune
        tab.child(16, 39020, 117, 160); // Cosmic text
        tab.child(17, 39021, 147, 160); // Water text
        tab.child(83, 39087, 185, 145); // Mind rune
        tab.child(84, 39088, 177, 160); // Mind text
        /*
         * Jade
         */
        tab.child(18, 39022, 240, 145); // Cosmic rune
        tab.child(19, 39023, 230, 60); // options
        tab.child(20, 39024, 250, 53); // title
        tab.child(21, 39025, 250, 67); // magic lvl
        tab.child(22, 39026, 255, 103); // bolts
        tab.child(23, 39027, 273, 145); // Earth rune
        tab.child(24, 39028, 232, 160); // Cosmic text
        tab.child(25, 39029, 265, 160); // Earth text
        /*
         * Pearl
         */
        tab.child(26, 39030, 330, 145); // Cosmic rune
        tab.child(27, 39031, 330, 60); // options
        tab.child(28, 39032, 345, 53); // title
        tab.child(29, 39033, 345, 67); // magic lvl
        tab.child(30, 39034, 350, 103); // bolts
        tab.child(31, 39035, 365, 145); // Water rune
        tab.child(32, 39036, 322, 160); // Cosmic text
        tab.child(33, 39037, 357, 160); // Water text
        /*
         * Emerald
         */
        tab.child(34, 39038, 410, 145); // Cosmic rune
        tab.child(35, 39039, 430, 60); // options
        tab.child(36, 39040, 440, 53); // title
        tab.child(37, 39041, 440, 67); // magic lvl
        tab.child(38, 39042, 445, 103); // bolts
        tab.child(85, 39043, 470, 145); // Nature rune
        tab.child(39, 39089, 440, 145); // Air rune
        tab.child(40, 39044, 402, 160); // Cosmic text
        tab.child(41, 39045, 433, 160); // Air text
        tab.child(86, 39090, 463, 160); // Nature text
        /*
         * Topaz
         */
        tab.child(42, 39046, 47, 271); // Cosmic rune
        tab.child(43, 39047, 30, 191); // options
        tab.child(44, 39048, 62, 177); // title
        tab.child(45, 39049, 62, 191); // magic lvl
        tab.child(46, 39050, 65, 227); // bolts
        tab.child(47, 39051, 78, 271); // Fire rune
        tab.child(48, 39052, 38, 286); // Cosmic text
        tab.child(49, 39053, 70, 286); // Fire text
        /*
         * Ruby
         */
        tab.child(50, 39054, 120, 271); // Cosmic rune
        tab.child(51, 39055, 130, 191); // options
        tab.child(52, 39056, 157, 177); // title
        tab.child(53, 39057, 157, 191); // magic lvl
        tab.child(54, 39058, 160, 227); // bolts
        tab.child(55, 39059, 150, 271); // Fire rune
        tab.child(87, 39091, 180, 271); // Blood rune
        tab.child(56, 39060, 112, 286); // Cosmic text
        tab.child(57, 39061, 142, 286); // Fire text
        tab.child(88, 39092, 171, 286); // Blood text
        /*
         * Diamond
         */
        tab.child(58, 39062, 220, 271); // Cosmic rune
        tab.child(59, 39063, 230, 191); // options
        tab.child(60, 39064, 252, 177); // title
        tab.child(61, 39065, 252, 191); // magic lvl
        tab.child(62, 39066, 255, 227); // bolts
        tab.child(63, 39067, 252, 271); // Earth rune
        tab.child(89, 39093, 282, 271); // Law rune
        tab.child(64, 39068, 212, 286); // Cosmic text
        tab.child(65, 39069, 242, 286); // Earth text
        tab.child(90, 39094, 273, 286); // Law text
        /*
         * Dragon
         */
        tab.child(66, 39070, 325, 271); // Cosmic rune
        tab.child(67, 39071, 330, 191); // options
        tab.child(68, 39072, 348, 177); // title
        tab.child(69, 39073, 348, 191); // magic lvl
        tab.child(70, 39074, 350, 227); // bolts
        tab.child(71, 39075, 355, 271); // Earth rune
        tab.child(91, 39095, 385, 271); // Soul rune
        tab.child(72, 39076, 317, 286); // Cosmic text
        tab.child(73, 39077, 347, 286); // Earth text
        tab.child(92, 39096, 378, 286); // Soul text
        /*
         * Onyx
         */
        tab.child(74, 39078, 420, 271); // Cosmic rune
        tab.child(75, 39079, 430, 191); // options
        tab.child(76, 39080, 445, 177); // title
        tab.child(77, 39081, 448, 191); // magic lvl
        tab.child(78, 39082, 450, 227); // bolts
        tab.child(79, 39083, 449, 271); // Fire rune
        tab.child(93, 39097, 477, 271); // Death rune
        tab.child(80, 39084, 412, 286); // Cosmic text
        tab.child(81, 39085, 434, 286); // Fire text
        tab.child(94, 39098, 470, 286); // Death text
        tab.child(82, 39086, 262, 25); // Title (Enchant Crossbow Bolts)
    }

}
