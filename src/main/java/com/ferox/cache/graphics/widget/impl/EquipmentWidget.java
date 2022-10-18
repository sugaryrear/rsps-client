package com.ferox.cache.graphics.widget.impl;

import com.ferox.Client;
import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.Widget;

/**
 * The class which represents functionality for the equipment widget.
 *
 * @author Patrick van Elderen | 07:20 : dinsdag 2 juli 2019 (CEST)
 * @see <a href="https://github.com/Patrick9-10-1995">Github profile</a>
 */
public class EquipmentWidget extends Widget {

    public static void unpack(AdvancedFont[] font) {
        equipment_bonus_widget(font);
        equipment_tab_widget();
        buildEquipmentTab();
    }

    public static void buildEquipmentTab() {
        final Widget parent = cache[1644];
        final Widget newTab = createSprite(parent.id, 1835, 1835);
        final Widget inventory = cache[1688];
        inventory.inventoryOffsetX[14] = -97;
        inventory.inventoryOffsetY[14] = -198;
        inventory.sprites[14] = Client.spriteCache.get(1836);
        addChild(parent.id, newTab.id, 37, 4, getIndexOfChild(parent.id, inventory.id));
    }

    private static void equipment_bonus_widget(AdvancedFont[] font) {
        Widget bankEquipment = addTabInterface(15150);

        addButton(15151, 765,"Hide worn items");
        addSprite(15152, 1250);
        bankEquipment.totalChildren(2);
        bankEquipment.child(0, 15151,5,20);
        bankEquipment.child(1, 15152,6,25);

        Widget widget = addTabInterface(15106);
        addSprite(15107, 874);
        //addSprite(15108, 875);
        closeButton(15210, 107, 108, false);
        addText(15111, "Equip Your Character...", font, 2, 0xff981f, false, true);
        addText(15112, "Attack bonus", font, 2, 0xff981f, false, true);
        addText(15113, "Defence bonus", font, 2, 0xff981f, false, true);
        addText(15114, "Other bonuses", font, 2, 0xff981f, false, true);
        addText(15115, "Melee strength: +0", font, 1, 0xff981f, false, true);
        addText(15116, "Ranged strength: +0", font, 1, 0xff981f, false, true);
        addText(15117, "Magic damage: +0%", font, 1, 0xff981f, false, true);
        addText(15118, "Prayer: +0", font, 1, 0xff981f, false, true);
        addText(15121, "Target-specific", font, 2, 0xff981f, false, true);
        addText(15119, "Undead: 0%", font, 1, 0xff981f, false, true);
        addText(15120, "Slayer: 0%", font, 1, 0xff981f, false, true);
        addText(15122, "Drop rate bonus: +30%", font, 1, 0xff981f, false, true);
        addText(15123, "Blood money rate: +25", font, 1, 0xff981f, false, true);
        for (int i = 1675; i <= 1684; i++) {
            textSize(i, font, 1);
        }
        textSize(1686, font, 1);
        textSize(1687, font, 1);
        addCharacterToInterface(15125, 560);
        widget.totalChildren(51);
        widget.child(0, 15107, 6, 6);
        widget.child(1, 15210, 476, 14);
        widget.child(2, 15111, 17, 16);
        int child = 3;
        int Y = 43;
        for (int childs = 1675; childs <= 1679; childs++) {
            widget.child(child, childs, 333, Y); // Attack bonuses
            child++;
            Y += 14;
        }
        widget.child(8, 1680, 333, 132); // Stab defence bonus
        widget.child(9, 1681, 333, 148); // Slash defence bonus
        widget.child(10, 1682, 333, 163); // Crush defence bonus
        widget.child(11, 1683, 333, 178); // Magic defence bonus
        widget.child(12, 1684, 333, 192); // Range defence bonus
        widget.child(13, 15115, 333, 224); // Melee strength
        widget.child(14, 15125, 182, 208); // Adds character to the interface
        widget.child(15, 15112, 325, 29); // Attack bonus
        widget.child(16, 15118, 333, 264); // Prayer frame
        widget.child(17, 15113, 325, 117); // Defence bonus
        widget.child(18, 15114, 325, 210); // Other bonus
        widget.child(19, 1645, 81, 149 - 52); // Head/Ammy binder
        widget.child(20, 1646, 81, 163); // Body/Legs binder
        widget.child(21, 1647, 81, 203); // Legs/Boots binder
        widget.child(22, 1648, 81, 58 + 56); // Ring/Body binder
        widget.child(23, 1649, 25, 110 - 44 + 118 - 13 + 5); // Weapon/Gloves binder
        widget.child(24, 1650, 25, 58 + 154); // Weapon/Gloves binder
        widget.child(25, 1651, 137, 58 + 118); // Shield/Ring binder
        widget.child(26, 1652, 137, 58 + 130); // Shield/Ring binder
        widget.child(27, 1653, 61, 58 + 81); // Weapon/Body/Shield binder
        widget.child(28, 1654, 117, 58 + 81); // Weapon/Body/Shield binder
        widget.child(29, 1655, 74, 58 + 42); // Cape/Ammy binder
        widget.child(30, 1656, 115, 58 + 41); // Ammy/Ammo binder
        widget.child(31, 1657, 81, 58 + 4); // Helmet slot
        widget.child(32, 1658, 40, 58 + 43); // Cape slot
        widget.child(33, 1659, 81, 58 + 43); // Amulet slot
        widget.child(34, 1660, 122, 58 + 43); // Arrows slot
        widget.child(35, 1661, 25, 58 + 82); // Weapon slot
        widget.child(36, 1662, 81, 58 + 82); // Body slot
        widget.child(37, 1663, 137, 58 + 82); // Shield slot
        widget.child(38, 1664, 81, 58 + 122); // Legs slot
        widget.child(39, 1665, 81, 58 + 162); // Boots slot
        widget.child(40, 1666, 25, 58 + 162); // Gloves slot
        widget.child(41, 1667, 137, 58 + 162); // Ring slot
        widget.child(42, 1688, 27, 110 - 13 + 3); // All worn icons
        //widget.child(43, 15108, 74, 275); // Weight
        widget.child(43, 15121, 325, 278); // Target-specific
        widget.child(44, 15119, 333, 292); // Undead
        widget.child(45, 15120, 333, 305); // Slayer
        widget.child(46, 15116, 333, 237); // Range strength
        widget.child(47, 15117, 333, 251); // Magic damage
        widget.child(48, 15122, 35, 265); // Weight text
        widget.child(49, 15150, 20, 22); // Bank equipment widget
        widget.child(50, 15123, 35, 280); // Drop rate
        for (int childs = 1675; childs <= 1684; childs++) {
            Widget rsi = cache[childs];
            rsi.textColour = 0xff981f; // Attack bonuses Color
            rsi.centerText = false;
        }
        for (int childs = 1686; childs <= 1687; childs++) {
            Widget rsi = cache[childs];
            rsi.textColour = 0xff981f;
            rsi.centerText = false;
        }
    }

    private static void equipment_tab_widget() {
        removeConfig(21338);
        removeConfig(21344);
        removeConfig(21342);
        removeConfig(21341);
        removeConfig(21340);
        removeConfig(15103);
        removeConfig(15104);
        Widget main_widget = cache[1644];
        main_widget.children[26] = 27650;
        main_widget.child_x[26] = 0;
        main_widget.child_y[26] = 0;

        //Move equipment widget
        main_widget.child_x[23] = 23;
        main_widget.child_y[23] = 42;

        main_widget = addInterface(27650);

        addHoverButton(27651, 146, 40, 40, "View guide prices", -1, 27652, 1);
        addHoveredButton(27652, 147, 40, 40, 27658);

        addHoverButton(27653, 144, 40, 40, "View equipment stats", -1, 27655, 1);
        addHoveredButton(27655, 145, 40, 40, 27665);

        addHoverButton(27654, 148, 40, 40, "View items kept on death", -1, 27657, 1);
        addHoveredButton(27657, 149, 40, 40, 27666);

        addHoverButton(27668, 872, 40, 40, "Call follower", -1, 27669, 1);
        addHoveredButton(27669, 873, 40, 40, 27670);

        setChildren(8, main_widget);

        // Prices
        setBounds(27651, 52, 205, 0, main_widget);
        setBounds(27652, 52, 205, 1, main_widget);
        // Death
        setBounds(27657, 98, 205, 5, main_widget);
        setBounds(27654, 98, 205, 3, main_widget);
        // Follower
        setBounds(27668, 143, 205, 6, main_widget);
        setBounds(27669, 143, 205, 7, main_widget);
        // Equip
        setBounds(27655, 6, 205, 4, main_widget);
        setBounds(27653, 6, 205, 2, main_widget);
    }

}
