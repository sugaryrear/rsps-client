package com.ferox.cache.graphics.widget.impl;

import com.ferox.ClientConstants;
import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.DrawLine;
import com.ferox.cache.graphics.widget.Widget;

/**
 * @author Zerikoth
 * @Since oktober 12, 2020
 */
public class PresetWidget extends Widget {

    public static void unpack(AdvancedFont[] font) {
        final Widget rsi = addTabInterface(62500);

        int frame = 0;
        int childId = 62504;
        int startX = 0;
        int startY = 0;
        int statIcon = 1253;
        int statIconX = 0;
        int statIconY = 0;
        int iconImageId = 1245;

        addSprite(62501, 1252);
        closeButton(62502, 24, 25, false);

        rsi.totalChildren(ClientConstants.PVP_MODE ? 134 : 131);
        rsi.child(frame++, 62501, 5, 5);
        rsi.child(frame++, 62502, 486, 10);

        if(ClientConstants.PVP_MODE) {
            addText(childId, "Default Presets:", 0xFFFF32, true, true, 52, font, 1);
            setBounds(childId++, 71, 29, frame++, rsi);
        }

        addText(childId, ClientConstants.PVP_MODE ? "Custom Presets:" : "My Presets", 0xFFFF32, true, true, 52, font, 1);
        setBounds(childId++, ClientConstants.PVP_MODE ? 71 : 55, ClientConstants.PVP_MODE ? 123 : 26, frame++, rsi);

        if(ClientConstants.PVP_MODE) {
            new DrawLine(62634, 94, 0xFFFF32, 255, DrawLine.LineType.HORIZONTAL);
        }
        new DrawLine(62635, 93, 0xFFFF32, 255, DrawLine.LineType.HORIZONTAL);

        if(ClientConstants.PVP_MODE) {
            setBounds(62634, 24, 43, frame++, rsi);
        }
        setBounds(62635, 25, ClientConstants.PVP_MODE ? 137 : 43, frame++, rsi);

        if(ClientConstants.PVP_MODE) {
            setBounds(62506, 20, 46, frame++, rsi);
        }
        setBounds(62507, 20, ClientConstants.PVP_MODE ? 142 : 46, frame++, rsi);

        if(ClientConstants.PVP_MODE) {
            final Widget globalPresetScrollbar = createWidget(62506, 92, 73);

            int globalPresetFrames = 0;

            globalPresetScrollbar.scrollMax = 150;

            globalPresetScrollbar.totalChildren(10);

            startY = 1;
            for (int i = 62712; i < 62722; i++) {
                addCustomClickableText(i, "", "Select", font, 0, 0xcc8400, false, false, 150, 10);
                setBounds(i, 0, startY, globalPresetFrames++, globalPresetScrollbar);
                startY += 15;
            }
        }

        final Widget customPresetScrollbar = createWidget(62507, 92, ClientConstants.PVP_MODE ? 73 : 170);

        int customPresetFrames = 0;

        customPresetScrollbar.scrollMax = 280;

        customPresetScrollbar.totalChildren(20);

        startY = 3;

        for (int i = ClientConstants.PVP_MODE ? 62722 : 62712; i < 62742; i++) {
            addCustomClickableText(i, "Test", "Select", font, 0, 0xcc8400, false, false, 150, 10);
            setBounds(i, 0, startY, customPresetFrames++, customPresetScrollbar);
            startY += 14;
        }

        childId = 62508;

        /**
         * Extras Buttons
         */
        startX = 17;
        startY = 229;
        int hoverChildId = 62742;
        for (int i = 0; i < 3; i++) {
            addHoverButton(childId, 1242, 110, 29, i == 1 || i == 2 ? "Yes/No" : "", -1, hoverChildId, 1);
            rsi.child(frame++, childId, startX, startY);
            addHoveredButton(hoverChildId, 1242, 110, 29, hoverChildId + 1);
            rsi.child(frame++, hoverChildId, startX, startY);
            childId++;
            hoverChildId += 2;
            addSprite(childId, i == 1 ? 81 : iconImageId);
            iconImageId++;
            rsi.child(frame++, childId, startX + 2, startY + 2);
            childId++;
            startY = startY + 31;
        }

        /**
         * Extras Buttons Text
         */
        startX = 84;
        startY = 232;
        for (int i = 0; i < 3; i++) {
            addText(childId, i == 0 ? "Spellbook" : i == 1 ? "Save levels" : "Open on death", font, 0, 0xff981f, true);
            setBounds(childId, startX, startY, frame++, rsi);
            childId++;
            addText(childId,"", 0xFFFF32, true, true, 52, font, 0);
            setBounds(childId, startX, startY + 12, frame++, rsi);
            childId++;
            startY = startY + 31;
        }

        /**
         * Stats Buttons
         */
        startX = 143;
        startY = 53;
        statIconX = 146;
        statIconY = 55;
        for (int i = 0; i < 7; i++) {
            addButton(childId, 1244);
            rsi.child(frame++, childId, startX, startY);
            childId++;
            addSprite(childId, statIcon++);
            rsi.child(frame++, childId, (i == 1 || i == 2) ? statIconX + 2 : statIconX,
                statIconY + (i == 0 ? -1 : (i == 1 || i == 3) ? 2 : 0));
            childId++;
            startY = startY + 29;
            statIconY = statIconY + 29;
        }

        /**
         * Stats Buttons Text
         */
        startX = 176;
        startY = 57;
        for (int i = 0; i < 7; i++) {
            addText(childId, "", font, 0, 0xFFFF32, true);
            rsi.child(frame++, childId, startX, startY);
            childId++;
            addText(childId, "", font, 0, 0xFFFF32, true);
            rsi.child(frame++, childId, startX + 15, startY + 9);
            childId++;
            startY = startY + 29;
        }

        /**
         * Bottom Stats Button to show pure/zerker/main text
         */
        addHoverButton(childId, 1248, 63, 29, "", -1, hoverChildId, 1);
        rsi.child(frame++, childId++, 141, 291);
        addHoveredButton(hoverChildId, 1248, 63, 29, hoverChildId + 1);
        rsi.child(frame++, hoverChildId, 141, 291);
        hoverChildId += 2;

        addText(childId, "Stats", font, 0, 0xFFFF32, true);
        setBounds(childId++, 172, 300, frame++, rsi);

        /**
         * Top Text's to represent column name.
         */
        startX = 175;
        startY = 29;
        addText(childId, "Stats", 0xFFFF32, true, true, 52, font, 1);
        setBounds(childId++, startX, startY, frame++, rsi);
        startX = startX + 120;
        addText(childId, "Inventory", 0xFFFF32, true, true, 52, font, 1);
        setBounds(childId++, startX, startY, frame++, rsi);
        startX = startX + 148;
        addText(childId, "Equipment", 0xFFFF32, true, true, 52, font, 1);
        setBounds(childId++, startX, startY, frame++, rsi);

        /**
         * 28 x inventory slot sprites
         */
        for (int i = 0; i < 7; i++) {
            startX = 217;
            startY = 52 + i * 38;
            for (int row = 0; row < 4; row++) {
                addSprite(childId, 1243);
                setBounds(childId, startX, startY, frame++, rsi);
                childId++;
                startX = startX + 38;
            }
        }
        /**
         * Buttons below Equipment column
         */
        startX = 376;
        startY = 262;
        iconImageId = 1249;
        for (int i = 0; i < 2; i++) {
            addHoverButton(childId, 1251, 125, 29, "", -1, hoverChildId, 1);
            rsi.child(frame++, childId, startX, startY);
            childId++;
            addHoveredButton(hoverChildId, 1251, 125, 29, hoverChildId + 1);
            rsi.child(frame++, hoverChildId, startX, startY);
            hoverChildId += 2;
            addSprite(childId, iconImageId++);
            rsi.child(frame++, childId, i == 1 ? startX + 16 : startX + 26, i == 0 ? startY + 6 : startY + 2);
            childId++;
            addClickableText(childId, i == 0 ? "Edit Preset" : "Load Preset", i == 0 ? "Edit Preset" : "Load Preset", font, 0, 0xFFFF32, false, true, 60, 11);
            rsi.child(frame++, childId, i == 1 ? startX + 50 : startX + 50, startY + 10);
            childId++;
            startY = startY + 29;
        }

        childId = 62590;
        /**
         * Refresh
         */
        addSprite(childId, 40, "Interfaces/Presets/IMAGE");//set 40 to 20 if wanting to reenable
        rsi.child(frame++, childId++, 485, 55);

        /**
         * Interface main title
         */
        addText(childId, "Preloading Gear Management", 0xFF981F, true, true, 52, font, 2);
        setBounds(childId++, 252, 9, frame++, rsi);

        /**
         * Item on interface for inventory slots.
         */

        for (int i = 0; i < 7; i++) {
            startX = 219;
            startY = 54 + i * 38;
            for (int row = 0; row < 4; row++) {
                addItemOnInterface(childId, 62500, null, 5, 6, 7, 4);
                setBounds(childId, startX, startY, frame++, rsi);
                childId++;
                startX = startX + 38;
            }
        }

        /**
         * Equipment Interface
         */
        for (int i = 0; i < 14; i++) {
            addItemOnInterface(childId + i, 62500, null, 5, 6, 7, 4);
        }

        rsi.child(frame++, 62620, 420, 54); // Head slot
        rsi.child(frame++, 62621, 381, 95); // Cape slot
        rsi.child(frame++, 62622, 423, 95); // Amulet slot
        rsi.child(frame++, 62623, 381, 136); // Weapon slot
        rsi.child(frame++, 62624, 423, 136); // Body slot
        rsi.child(frame++, 62625, 464, 136); // Shield slot
        rsi.child(frame++, 62627, 423, 177); // Legs slot

        rsi.child(frame++, 62629, 381, 218); // Hands slot
        rsi.child(frame++, 62630, 423, 218); // Feet slot
        rsi.child(frame++, 62632, 464, 218); // Ring slot
        rsi.child(frame++, 62633, 464, 95); // Ammo slot

    }
}
