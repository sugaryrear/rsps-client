package com.ferox.cache.graphics.widget.impl;

import com.ferox.Client;
import com.ferox.cache.graphics.dropdown.Dropdown;
import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.Slider;
import com.ferox.cache.graphics.widget.Widget;
import com.ferox.util.ConfigUtility;

public class OptionTabWidget extends Widget {

    public static void unpack(AdvancedFont[] font) {
        widget(font);
        advancedWidget(font);
    }

    private static void widget(AdvancedFont[] tda) {
        Widget tab = addTabInterface(OPTIONS_TAB_WIDGET);

        addSettingsSprite(42501, 607);

        /* Top buttons */
        addHoverButtonWithDisable(DISPLAY_WIDGET, "Display", 579, 580, 581, 582, true, AUDIO_WIDGET, CHAT_WIDGET, CONTROLS_WIDGET);
        addHoverButtonWithDisable(AUDIO_WIDGET, "Audio", 583, 584, 585, 586, false, DISPLAY_WIDGET, CHAT_WIDGET, CONTROLS_WIDGET);
        addHoverButtonWithDisable(CHAT_WIDGET, "Chat", 587, 588, 589, 590, false, DISPLAY_WIDGET, AUDIO_WIDGET, CONTROLS_WIDGET);
        addHoverButtonWithDisable(CONTROLS_WIDGET, "Controls", 591, 592, 593, 594, false, DISPLAY_WIDGET, AUDIO_WIDGET, CHAT_WIDGET);

        /* Bottom buttons */
        addConfigButton(ACCEPT_AID, OPTIONS_TAB_WIDGET, 634, 601, "Toggle Accept Aid", 427, 0, OPTION_OK);
        addConfigButton(RUN, OPTIONS_TAB_WIDGET, 602, 603, "Toggle Run", 173, 0, OPTION_OK);
        addButton(HOUSE_OPTIONS, 628, "View House Options");
        addButton(BOND_POUCH, 604, "Open Bond Pouch");

        /* Middle */
        Widget display = addTabInterface(DISPLAY_TAB);
        Widget audio = addTabInterface(AUDIO_TAB);
        Widget chat = addTabInterface(CHAT_TAB);
        Widget controls = addTabInterface(CONTROLS_TAB);

        tab.totalChildren(10);
        int childNum = 0;

        setBounds(42501, 3, 42, childNum++, tab);
        int x = 0;
        for (int i=0; i<4; i++, x += 46) {
            setBounds(DISPLAY_WIDGET+i, 6 + x, 0, childNum++, tab);
            setBounds(ACCEPT_AID+i, 6 + x, 219, childNum++, tab);
        }
        setBounds(DISPLAY_TAB, 0, 0, childNum++, tab);

        displaySettings(display, tda);
        audioSettings(audio, tda);
        chatSettings(chat, tda);
        controlsSettings(controls, tda);
    }

    private static void displaySettings(Widget display, AdvancedFont[] font) {
        /* Mouse zoom */
        addButton(RESTORE_ZOOM, 599,"Restore Default Zoom");
        addConfigButton(ZOOM_TOGGLE, DISPLAY_TAB, 600, 771, "Select", 294, 0, OPTION_OK);

        /* Screen sizes */
        configHoverButton(FIXED_MODE, "Fixed mode", 595, 595, 619, 596, true, RESIZABLE_MODE);
        configHoverButton(RESIZABLE_MODE, "Resizable mode", 597, 598, 618, 618, false, FIXED_MODE);

        /* Advanced options */
        hoverButton(ADVANCED_OPTIONS, "Configure <col=ff9040>Advanced options", 605, 605, "Advanced options", font, 1, 0xff981f, 0xffffff, true);

        slider(ZOOM_SLIDER, 0, 1100, 606, 615);

        addConfigButton(BRIGHTNESS_STATE_ONE, 904, 397, 393, "Adjust Screen Brightness", 166, 1, OPTION_RESET_SETTING);

        addConfigButton(BRIGHTNESS_STATE_TWO, 904, 398, 394, "Adjust Screen Brightness", 166, 2, OPTION_RESET_SETTING);

        addConfigButton(BRIGHTNESS_STATE_THREE, 904, 399, 395, "Adjust Screen Brightness", 166, 3, OPTION_RESET_SETTING);

        addConfigButton(BRIGHTNESS_STATE_FOUR, 904, 400, 396, "Adjust Screen Brightness", 166, 4, OPTION_RESET_SETTING);

        addSettingsSprite(BRIGHTNESS_IMAGE, 617);

        display.totalChildren(12);
        int childNum = 0;

        addText(RUN_TEXT, "100%", font, 1, 0xfe971e, true, true);
        setBounds(RUN_TEXT, 71, 241, childNum++, display);
        setBounds(RESTORE_ZOOM, 11, 50, childNum++, display);
        setBounds(ZOOM_TOGGLE, 11, 50, childNum++, display);
        setBounds(FIXED_MODE, 25, 118, childNum++, display);
        setBounds(RESIZABLE_MODE, 102, 118, childNum++, display);
        setBounds(ADVANCED_OPTIONS, 25, 176, childNum++, display);
        setBounds(ZOOM_SLIDER, 47, 59, childNum++, display);
        setBounds(BRIGHTNESS_STATE_ONE, 47, 92, childNum++, display);
        setBounds(BRIGHTNESS_STATE_TWO, 79, 92, childNum++, display);
        setBounds(BRIGHTNESS_STATE_THREE, 111, 92, childNum++, display);
        setBounds(BRIGHTNESS_STATE_FOUR, 143, 92, childNum++, display);
        setBounds(BRIGHTNESS_IMAGE, 11, 83, childNum++, display);

    }

    private static void audioSettings(Widget audio, AdvancedFont[] tda) {
        addSprite(43531, 635);
        addConfigButton(43532, 904, 737, 732, "Adjust Music Volume", 168, 0, 5);
        addConfigButton(43533, 904, 738, 733, "Adjust Music Volume", 168, 1, 5);
        addConfigButton(43534, 904, 739, 734, "Adjust Music Volume", 168, 2, 5);
        addConfigButton(43535, 904, 740, 735, "Adjust Music Volume", 168, 3, 5);
        addConfigButton(43536, 904, 741, 736, "Adjust Music Volume", 168, 4, 5);

        addSettingsSprite(43537, 631);
        addConfigButton(43538, 904, 737, 732, "Adjust Sound Effect Volume", 169, 0, 5);
        addConfigButton(43539, 904, 738, 733, "Adjust Sound Effect Volume", 169, 1, 5);
        addConfigButton(43540, 904, 739, 734, "Adjust Sound Effect Volume", 169, 2, 5);
        addConfigButton(43541, 904, 740, 735, "Adjust Sound Effect Volume", 169, 3, 5);
        addConfigButton(43542, 904, 741, 736, "Adjust Sound Effect Volume", 169, 4, 5);

        addSettingsSprite(43543, 632);
        addConfigButton(43544, 904, 737, 732, "Adjust Area Sound Effect Volume", 780, 0, 5);
        addConfigButton(43545, 904, 738, 733, "Adjust Area Sound Effect Volume", 780, 1, 5);
        addConfigButton(43546, 904, 739, 734, "Adjust Area Sound Effect Volume", 780, 2, 5);
        addConfigButton(43547, 904, 740, 735, "Adjust Area Sound Effect Volume", 780, 3, 5);
        addConfigButton(43548, 904, 741, 736, "Adjust Area Sound Effect Volume", 780, 4, 5);

        audio.totalChildren(19);
        int childNum = 0;

        int yOffset = 10;
        addText(RUN_TEXT, "100%", tda, 1, 0xfe971e, true, true);
        setBounds(RUN_TEXT, 71, 241, childNum++, audio);
        setBounds(43531, 12, 56 + yOffset, childNum++, audio);
        setBounds(43532, 50, 65 + yOffset, childNum++, audio);
        setBounds(43533, 75, 65 + yOffset, childNum++, audio);
        setBounds(43534, 100, 65 + yOffset, childNum++, audio);
        setBounds(43535, 125, 65 + yOffset, childNum++, audio);
        setBounds(43536, 150, 65 + yOffset, childNum++, audio);
        setBounds(43537, 11, 101 + yOffset, childNum++, audio);
        setBounds(43538, 50, 111 + yOffset, childNum++, audio);
        setBounds(43539, 75, 111 + yOffset, childNum++, audio);
        setBounds(43540, 100, 111 + yOffset,childNum++, audio);
        setBounds(43541, 125, 111 + yOffset, childNum++, audio);
        setBounds(43542, 150, 111 + yOffset, childNum++, audio);
        setBounds(43543, 11, 146 + yOffset, childNum++, audio);
        setBounds(43544, 50, 157 + yOffset, childNum++, audio);
        setBounds(43545, 75, 157 + yOffset, childNum++, audio);
        setBounds(43546, 100, 157 + yOffset, childNum++, audio);
        setBounds(43547, 125, 157 + yOffset, childNum++, audio);
        setBounds(43548, 150, 157 + yOffset, childNum++, audio);
    }

    private static void chatSettings(Widget chat, AdvancedFont[] font) {
        addConfigButton(CHAT_EFFECTS, CHAT_TAB, 620, 621, "Toggle Chat Effects", 171, 0, OPTION_OK);
        addConfigButton(SPLIT_PRIVATE_CHAT, CHAT_TAB, 622, 623, "Toggle Split Private Chat", 287, 0, OPTION_OK);
        addConfigButton(HIDE_PRIVATE_CHAT, CHAT_TAB, 666, 667, "Toggle Hide Private Chat", 288, 0, OPTION_OK);
        addConfigButton(PROFANITY_FILTER, CHAT_TAB, 668, 669, "Toggle Profanity Filter", 289, 0, OPTION_OK);
        addButton(NOTIFICATIONS, 624, "Notifications");
        addConfigButton(LOGIN_LOGOUT_NOTIFICATION_TIMEOUT, CHAT_TAB, 670, 671, "Toggle Login/Logout notification timeout", 290, 0, OPTION_OK);
        hoverButton(DISPLAY_NAME, "Configure <col=ff9040>Display name", 605, 605, "Display name", font, 1, 0xff981f, 0xffffff, true);

        chat.totalChildren(8);
        int childNum = 0;

        int[] buttons = new int[] { CHAT_EFFECTS, SPLIT_PRIVATE_CHAT, HIDE_PRIVATE_CHAT };
        int x = 19;
        for (int btn : buttons) {
            setBounds(btn, x, 61, childNum++, chat);
            x += 56;
        }
        int[] buttons2 = new int[] { PROFANITY_FILTER, NOTIFICATIONS, LOGIN_LOGOUT_NOTIFICATION_TIMEOUT };
        int x2 = 19;
        for (int btn : buttons2) {
            setBounds(btn, x2, 61 + 53, childNum++, chat);
            x2 += 56;
        }
        addText(RUN_TEXT, "100%", font, 1, 0xfe971e, true, true);
        setBounds(RUN_TEXT, 71, 241, childNum++, chat);
        setBounds(DISPLAY_NAME, 25, 168, childNum++, chat);
    }

    private static void controlsSettings(Widget controls, AdvancedFont[] font) {
        addConfigButton(MOUSE_BUTTONS, CONTROLS_TAB, 625, 626, "Toggle number of Mouse Buttons", 170, 0, OPTION_OK);
        addButton(KEYBINDING, 627, "Keybinding");
        addConfigButton(FOLLOWER_OPTIONS, CONTROLS_TAB, 674, 675, "Toggle follower priority", 291, 0, OPTION_OK);

        addConfigButton(MOUSE_CAMERA, CONTROLS_TAB, 672, 673, "Toggle Mouse Camera", 207, 0, OPTION_OK);
        addConfigButton(SHIFT_CLICK_DROP, CONTROLS_TAB, 629, 630, "Toggle Shift Click Drop", 293, 0, OPTION_OK);

        String[] options = {"Depends on combat levels", "Always right-click", "Left-click where available", "Hidden"};

        dropdownMenu(PLAYER_ATTACK_DROPDOWN, 166,0, options, Dropdown.PLAYER_ATTACK_OPTION_PRIORITY, font, 1);
        addText(PLAYER_ATTACK_TEXT, "Player 'Attack' options:", font, 1, 0xfe971e, false, true);

        dropdownMenu(NPC_ATTACK_DROPDOWN, 166,2, options, Dropdown.NPC_ATTACK_OPTION_PRIORITY, font, 1);
        addText(NPC_ATTACK_TEXT, "NPC 'Attack' options:", font, 1, 0xfe971e, false, true);

        controls.totalChildren(10);
        int childNum = 0;

        int[] buttons = new int[] { MOUSE_BUTTONS, MOUSE_CAMERA, FOLLOWER_OPTIONS };
        int x = 25;
        for (int btn : buttons) {
            setBounds(btn, x, 49, childNum++, controls);
            x += 50;
        }

        int[] buttons2 = new int[] { KEYBINDING, SHIFT_CLICK_DROP };
        int x2 = 45;
        for (int btn : buttons2) {
            setBounds(btn, x2, 91, childNum++, controls);
            x2 += 60;
        }

        addText(RUN_TEXT, "100%", font, 1, 0xfe971e, true, true);
        setBounds(RUN_TEXT, 71, 241, childNum++, controls);
        setBounds(PLAYER_ATTACK_TEXT, 13, 114 + 20, childNum++, controls);
        setBounds(NPC_ATTACK_DROPDOWN, 13, 181 + 9, childNum++, controls);
        setBounds(NPC_ATTACK_TEXT, 13, 161 + 11, childNum++, controls);
        setBounds(PLAYER_ATTACK_DROPDOWN, 13, 134 + 17, childNum++, controls);
    }

    public static void optionTabButtons(int button) {
        switch (button) {
            case DISPLAY_WIDGET:
            case AUDIO_WIDGET:
            case CHAT_WIDGET:
            case CONTROLS_WIDGET:
                switchSettings(button);
                break;

            case RESTORE_ZOOM:
                Slider slider = Widget.cache[ZOOM_SLIDER].slider;
                slider.setValue(600);
                break;
        }
    }

    private static void advancedWidget(AdvancedFont[] font) {
        Widget widget = addTabInterface(43000);
        addSpriteLoader(43001, 676);
        addText(43002, "Advanced Options", font, 2, 0xff981f, true, true);
        closeButton(43003, 142, 143, false);

        addConfigButton(43004, 43000, 705, 706, "Chatbox scrollbar", ConfigUtility.CHATBOX_SCROLLBAR_ID, 0, OPTION_OK);
        addTransparentSprite(43005, 703, 120);

        String right_or_left = Client.singleton.settings[ConfigUtility.CHATBOX_SCROLLBAR_ID] == 0 ? "right" : "left";
        createTooltip(43006, "Resizable mode scrollbar position (Currently "+right_or_left+")", 42, 40);

        addConfigButton(43007, 43000, 705, 706, "Transparent side-panel", ConfigUtility.TRANSPARENT_SIDE_PANEL_ID, 0, OPTION_OK);
        addTransparentSprite(43008, 700, 120);
        createTooltip(43009, "Resizable mode side-panel:<br>Opaque", 42, 40);

        addConfigButton(43010, 43000, 705, 706, "'Remaining XP' tooltips", ConfigUtility.REMAINING_XP_ID, 0, OPTION_OK);
        addSprite(43011, 698);
        String panel = Client.singleton.settings[ConfigUtility.REMAINING_XP_ID] == 1 ? "on" : "off";
        createTooltip(43012, "Stats panel shows XP next to level (currently "+panel+")", 35, 40);

        addConfigButton(43013, 43000, 705, 706, "Prayer tooltips", ConfigUtility.PRAYER_TOOLTIPS_ID, 0, OPTION_OK);
        addSprite(43014, 701);
        String prayer_tooltips = Client.singleton.settings[ConfigUtility.PRAYER_TOOLTIPS_ID] == 1 ? "on" : "off";
        createTooltip(43015, "Hovering over prayers displays tooltips (currently "+prayer_tooltips+")", 40, 40);

        addConfigButton(43016, 43000, 705, 706, "Special attack tooltips", ConfigUtility.SPECIAL_ATTACK_BAR_TOOLTIPS_ID, 0, OPTION_OK);
        addSprite(43017, 702);
        String special_bar_tooltips = Client.singleton.settings[ConfigUtility.SPECIAL_ATTACK_BAR_TOOLTIPS_ID] == 1 ? "on" : "off";
        createTooltip(43018, "Hovering over special attack bar displays tooltips (currently "+special_bar_tooltips+")", 40, 40);

        addConfigButton(43019, 43000, 705, 706, "Roof-removal", ConfigUtility.ROOF_REMOVAL_ID, 0, OPTION_OK);
        addSprite(43020, 697);
        String roof_removal = Client.singleton.settings[ConfigUtility.ROOF_REMOVAL_ID] == 1 ? "on" : "off";
        createTooltip(43021, "Always hide roofs (currently "+roof_removal+")", 40, 40);

        addConfigButton(43022, 43000, 705, 706, "Data orbs", ConfigUtility.DATA_ORBS_ID, 0, OPTION_OK);
        addSprite(43023, 696);
        String data_orbs = Client.singleton.settings[ConfigUtility.DATA_ORBS_ID] == 1 ? "on" : "off";
        createTooltip(43024, "Data orbs (currently "+data_orbs+")", 40, 40);

        addConfigButton(43025, 43000, 705, 706, "Transparent Chatbox", ConfigUtility.TRANSPARENT_CHAT_BOX_ID, 0, OPTION_OK);
        addSprite(43026, 699);
        createTooltip(43027, "Resizable mode chatbox:<br>Opaque", 40, 40);

        if (Client.singleton.settings[ConfigUtility.TRANSPARENT_CHAT_BOX_ID] == 0) {
            addText(43028, "Transparent chatbox...", font, 1, 0x8f8f8f);
        } else {
            addHoverText(43028, "Transparent chatbox...", "Click trough chatbox", font, 1, 0xff981f, true, true, 148, 36, 0xffcb64);
        }

        addClickableSprites(43029, "Click trough chatbox", 490, 491, 641);

        if (Client.singleton.settings[ConfigUtility.TRANSPARENT_CHAT_BOX_ID] == 0) {
            addText(43030, "Can be clicked trough.", font, 0, 0x8f8f8f);
        } else {
            addText(43030, "Can be clicked trough.", font, 0, 0xff981f);
        }

        createTooltip(43031, "In resizable mode, if the chatbox is transparent, should it be possible to click trough the<br>chatbox on the ground beneath?", 280, 41);

        addConfigButton(43032, 43000, 705, 706, "Side-stones arrangement", ConfigUtility.SIDE_STONES_ARRANGEMENT_ID, 0, OPTION_OK);
        addSprite(43033, 704);
        createTooltip(43034, "Resizable mode stone buttons:<br>'Old School Box'", 40, 40);

        if (Client.singleton.settings[ConfigUtility.TRANSPARENT_CHAT_BOX_ID] == 0) {
            addText(43035, "Side-panels...", font, 1, 0x8f8f8f);
        } else {
            addText(43035, "Side-panels...", font, 1, 0xff981f);
        }

        addClickableSprites(43036, "Hotkeys behaviour", 490, 491, 641);

        if (Client.singleton.settings[ConfigUtility.CAN_BE_CLOSED_BY_HOTKEYS_ID] == 0) {
            addText(43037, "Can be closed by the hotkeys.", font, 0, 0x8f8f8f);
        } else {
            addText(43037, "Can be closed by the hotkeys.", font, 0, 0xff981f);
        }

        createTooltip(43038, "In resizable mode, if the stone buttons are arranged along the bottom of the screen,<br>should it be possible to shut a side-panel by pressing its hotkey?", 280, 38);

        addButton(43039, 890, "View Custom Settings");

        widget.totalChildren(39);
        int childNum = 0;
        setBounds(43001, 100, 60, childNum++, widget);
        setBounds(43002, 280, 70, childNum++, widget);
        setBounds(43003, 420, 66, childNum++, widget);

        // Chatbox scrollbar
        setBounds(43004, 110, 100, childNum++, widget);
        setBounds(43005, 114, 105, childNum++, widget);

        // Transparent side-panel
        setBounds(43007, 158, 100, childNum++, widget);
        setBounds(43008, 168, 105, childNum++, widget);


        // 'Remaining XP' tooltips
        setBounds(43010, 206, 100, childNum++, widget);
        setBounds(43011, 210, 105, childNum++, widget);

        // Prayer tooltips
        setBounds(43013, 254, 100, childNum++, widget);
        setBounds(43014, 260, 105, childNum++, widget);


        // Special attack tooltips
        setBounds(43016, 302, 100, childNum++, widget);
        setBounds(43017, 307, 105, childNum++, widget);


        // Roof-removal
        setBounds(43019, 350, 100, childNum++, widget);
        setBounds(43020, 355, 114, childNum++, widget);


        // Data orbs
        setBounds(43022, 398, 100, childNum++, widget);
        setBounds(43023, 405, 103, childNum++, widget);


        // Transparent Chatbox
        setBounds(43025, 110, 153, childNum++, widget);
        setBounds(43026, 114, 161, childNum++, widget);
        setBounds(43028, 157, 155, childNum++, widget);
        setBounds(43029, 157, 174, childNum++, widget);
        setBounds(43030, 177, 176, childNum++, widget);

        // Side-stones arrangement
        setBounds(43032, 110, 204, childNum++, widget);
        setBounds(43033, 116, 213, childNum++, widget);
        setBounds(43035, 157, 205, childNum++, widget);
        setBounds(43036, 157, 225, childNum++, widget);
        setBounds(43037, 177, 228, childNum++, widget);

        // Custom settings button
        setBounds(43039, 107, 66, childNum++, widget);

        //The tooltip hovers need to have the bounds set below everything else
        setBounds(43006, 107, 100, childNum++, widget);
        setBounds(43009, 159, 100, childNum++, widget);
        setBounds(43012, 206, 100, childNum++, widget);
        setBounds(43015, 253, 100, childNum++, widget);
        setBounds(43018, 300, 100, childNum++, widget);
        setBounds(43021, 350, 100, childNum++, widget);
        setBounds(43024, 398, 100, childNum++, widget);
        setBounds(43027, 110, 151, childNum++, widget);
        setBounds(43031, 157, 149, childNum++, widget);
        setBounds(43034, 110, 203, childNum++, widget);
        setBounds(43038, 110, 203, childNum++, widget);
    }

    private static void switchSettings(int button) {
        int tab = button - DISPLAY_WIDGET;
        int[] tabs = new int[] { DISPLAY_TAB, AUDIO_TAB, CHAT_TAB, CONTROLS_TAB };
        Widget.cache[OPTIONS_TAB_WIDGET].children[9] = tabs[tab];
    }

    public static void updateSettings() {
        if (Widget.cache[PLAYER_ATTACK_DROPDOWN] != null && Widget.cache[PLAYER_ATTACK_DROPDOWN].dropdown != null) {
            Widget.cache[PLAYER_ATTACK_DROPDOWN].dropdown.setSelected(Widget.cache[PLAYER_ATTACK_DROPDOWN].dropdown.getOptions()[Client.singleton.setting.player_attack_priority]);
        }
        if (Widget.cache[NPC_ATTACK_DROPDOWN] != null && Widget.cache[NPC_ATTACK_DROPDOWN].dropdown != null) {
            Widget.cache[NPC_ATTACK_DROPDOWN].dropdown.setSelected(Widget.cache[NPC_ATTACK_DROPDOWN].dropdown.getOptions()[Client.singleton.setting.npc_attack_priority]);
        }
    }

    /** Settings constants*/
    private static final int OPTIONS_TAB_WIDGET = 42500;
    private static final int DISPLAY_WIDGET = 42502;
    private static final int AUDIO_WIDGET = 42503;
    private static final int CHAT_WIDGET = 42504;
    private static final int CONTROLS_WIDGET = 42505;
    private static final int DISPLAY_TAB = 42520;
    private static final int AUDIO_TAB = 43530;
    private static final int CHAT_TAB = 42540;
    private static final int CONTROLS_TAB = 42550;
    private static final int PLAYER_ATTACK_DROPDOWN = 42554;
    private static final int PLAYER_ATTACK_TEXT = 42555;
    private static final int NPC_ATTACK_DROPDOWN = 42556;
    private static final int NPC_ATTACK_TEXT = 42557;
    public static final int ZOOM_SLIDER = 42525;
    private static final int BRIGHTNESS_STATE_ONE = 906;
    private static final int BRIGHTNESS_STATE_TWO = 908;
    private static final int BRIGHTNESS_STATE_THREE = 910;
    private static final int BRIGHTNESS_STATE_FOUR = 912;
    private static final int BRIGHTNESS_IMAGE = 905;
    private static final int ZOOM_TOGGLE = 44151;
    private static final int RESTORE_ZOOM = 42521;
    public static final int FIXED_MODE = 42522;
    public static final int RESIZABLE_MODE = 42523;
    private static final int ADVANCED_OPTIONS = 42524;
    private static final int ACCEPT_AID = 42506;
    private static final int RUN = 42507;
    private static final int HOUSE_OPTIONS = 42508;
    private static final int BOND_POUCH = 42509;
    private static final int CHAT_EFFECTS = 42541;
    private static final int SPLIT_PRIVATE_CHAT = 42542;
    private static final int HIDE_PRIVATE_CHAT = 42543;
    private static final int MOUSE_BUTTONS = 42551;
    private static final int KEYBINDING = 42552;
    private static final int FOLLOWER_OPTIONS = 42553;
    private static final int PROFANITY_FILTER = 41541;
    private static final int NOTIFICATIONS = 41542;
    private static final int LOGIN_LOGOUT_NOTIFICATION_TIMEOUT = 41543;
    private static final int DISPLAY_NAME = 42544;
    private static final int MOUSE_CAMERA = 41551;
    private static final int SHIFT_CLICK_DROP = 41552;
    private static final int RUN_TEXT = 41553;
}
