package com.ferox.model.settings;

import com.ferox.Client;
import com.ferox.model.content.Keybinding;
import com.ferox.sign.SignLink;

import java.io.*;
import java.util.Arrays;

/**
 * This class is a utility class for the client settings.
 *
 * @author Patrick van Elderen | 18 feb. 2019 : 12:09:17
 * @see <a href="https://www.rune-server.ee/members/_Patrick_/">Rune-Server profile</a>
 */
public class Settings {

    //Custom user settings
    public boolean status_bars = true;
    public boolean draw_fps = false;
    public boolean draw_special_orb = true;
    public boolean draw_orb_arc = true;
    public boolean draw_health_overlay = true;
    public boolean draw_timers = true;
    public boolean toggle_overhead_names = false;
    public boolean toggle_overhead_hp = false;
    public boolean toggle_npc_overhead_names = false;
    public boolean toggle_npc_overhead_hp = false;
    public boolean toggle_item_pile_names = true;
    public boolean filter_item_pile_names = false;
    public boolean moving_prayers = false;
    public boolean ground_snow = false;
    public boolean shift_pet_options = false;

    public int drag_item_value = 10;
    public boolean incinerator = false;
    public boolean hide_equipment_button = false;
    public boolean hide_inventory_button = false;
    public int player_attack_priority = 0;
    public int npc_attack_priority = 2;
    public boolean show_hit_predictor = false;
    public boolean show_exp_counter = true;
    public int counter_size = 1;
    public int counter_color = 0xffffff;
    public float counter_speed = 1.0f;
    public int counter_position;
    public boolean counter_group = true;
    public int sound_state = 4;
    public boolean toggle_music = false;
    public boolean showHitPredictor = false;
    public boolean hidePrivateChat = false;
    public boolean loginLogoutNotification = false;
    public boolean profanityFilter = false;
    public boolean cameraMovement = true;
    public boolean shiftClick = true;
    public boolean esc_close = true;
    public boolean mouseButtons = false;
    public boolean acceptAid = false;
    public int brightness = 3;
    public boolean zoomToggle = false;
    public boolean chatEffects = true;
    public boolean privateChat = false;
    public boolean transparentSidePanel = false;
    public boolean transparentChatbox = false;
    public boolean sideStonesArrangement = false;
    public boolean roofs = true;
    public boolean orbs = true;

    private String fileLine = "";
    public String location;
    
    public void toggleVarbits() {
        Client.singleton.toggleConfig(169, sound_state);
        Client.singleton.toggleConfig(166, brightness);
        Client.singleton.toggleConfig(289, profanityFilter ? 1 : 0);
        Client.singleton.toggleConfig(207, cameraMovement ? 1 : 0);
        Client.singleton.toggleConfig(290, loginLogoutNotification ? 1 : 0);
        Client.singleton.toggleConfig(288, hidePrivateChat ? 1 : 0);
        Client.singleton.toggleConfig(594, esc_close ? 1 : 0);
        Client.singleton.toggleConfig(293, shiftClick ? 1 : 0);
        Client.singleton.toggleConfig(427, acceptAid ? 1 : 0);
        Client.singleton.toggleConfig(291, shift_pet_options ? 1 : 0);
        Client.singleton.toggleConfig(170, mouseButtons ? 1 : 0);
        Client.singleton.toggleConfig(294, zoomToggle ? 1 : 0);
        Client.singleton.toggleConfig(171, chatEffects ? 1 : 0);
        Client.singleton.toggleConfig(287, privateChat ? 1 : 0);
        Client.singleton.toggleConfig(295, transparentSidePanel ? 1 : 0);
        Client.singleton.toggleConfig(296, transparentChatbox ? 1 : 0);
        Client.singleton.toggleConfig(297, sideStonesArrangement ? 1 : 0);
        Client.singleton.toggleConfig(298, roofs ? 1 : 0);
        Client.singleton.toggleConfig(299, orbs ? 1 : 0);
    }

    private void createSettingsFile(String location1) {
        File location = new File(location1);
        if (location.exists()) {
            return;
        }
        try {
            save();
            location.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean contains(String match) {
        String string = fileLine.substring(0, fileLine.indexOf(" = "));
        return string.equals(match);
    }

    private boolean readBoolean() {
        fileLine = fileLine.substring(fileLine.lastIndexOf("=") + 2);
        return fileLine.equals("true");
    }

    private String readString() {
        fileLine = fileLine.substring(fileLine.indexOf("=") + 2);
        return fileLine;
    }

    private int readInt() {
        fileLine = fileLine.substring(fileLine.lastIndexOf("=") + 2);
        return Integer.parseInt(fileLine);
    }

    private int readFloat() {
        fileLine = fileLine.substring(fileLine.lastIndexOf("=") + 2);
        return (int) Float.parseFloat(fileLine);
    }

    private Double readDouble() {
        fileLine = fileLine.substring(fileLine.lastIndexOf("=") + 2);
        return Double.parseDouble(fileLine);
    }

    private void writeLine(String line, String line1, BufferedWriter bw) {
        try {
            bw.write(line + " = " + line1);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeLine(String line, Boolean line1, BufferedWriter bw) {
        try {
            bw.write(line + " = " + line1);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeLine(Double line1, BufferedWriter bw) {
        try {
            bw.write("counter_speed" + " = " + line1);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeLine(String line, int line1, BufferedWriter bw) {
        try {
            bw.write(line + " = " + line1);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        if (location == null) {
            return;
        }
        BufferedWriter bw;
        try {
            // Clear file contents.
            FileOutputStream writer = new FileOutputStream(location);
            writer.write(("").getBytes());
            writer.close();

            // Write new contents.
            bw = new BufferedWriter(new FileWriter(location, true));
            writeLine("cameraSpeed", Client.cameraSpeed, bw);
            writeLine("zoom_distance", Client.zoom_distance, bw);
            writeLine("drag_item_value", drag_item_value, bw);
            writeLine("player_attack_priority", player_attack_priority, bw);
            writeLine("npc_attack_priority", npc_attack_priority, bw);
            writeLine("keybinds", Arrays.toString(Keybinding.KEYBINDINGS), bw);
            writeLine("show_exp_counter", show_exp_counter, bw);
            writeLine("counter_size", counter_size, bw);
            writeLine("counter_color", counter_color, bw);
            writeLine((double) counter_speed, bw);
            writeLine("counter_position", counter_position, bw);
            writeLine("counter_group", counter_group, bw);
            writeLine("sound_state", sound_state, bw);
            writeLine("toggle_music", toggle_music, bw);
            writeLine("show_hit_predictor", show_hit_predictor, bw);
            writeLine("hidePrivateChat", hidePrivateChat, bw);
            writeLine("loginLogoutNotification", loginLogoutNotification, bw);
            writeLine("profanityFilter", profanityFilter, bw);
            writeLine("cameraMovement", cameraMovement, bw);
            writeLine("shiftClick", shiftClick, bw);
            writeLine("ESC_Close", esc_close, bw);
            writeLine("mouseButtons", mouseButtons, bw);
            writeLine("acceptAid", acceptAid, bw);
            writeLine("brightness", brightness, bw);
            writeLine("zoomToggle", zoomToggle, bw);
            writeLine("chatEffects", chatEffects, bw);
            writeLine("privateChat", privateChat, bw);
            writeLine("transparentSidePanel", transparentSidePanel, bw);
            writeLine("transparentChatbox", transparentChatbox, bw);
            writeLine("sideStonesArrangement", sideStonesArrangement, bw);
            writeLine("roofs", roofs, bw);
            writeLine("orbs", orbs, bw);
            writeLine("draw_orb_arc", draw_orb_arc, bw);
            writeLine("draw_special_orb", draw_special_orb, bw);
            writeLine("draw_health_overlay", draw_health_overlay, bw);
            writeLine("draw_ground_item_overlay", toggle_item_pile_names, bw);
            writeLine("draw_timers", draw_timers, bw);
            writeLine("display_names", toggle_overhead_names, bw);
            writeLine("toggle_overhead_hp", toggle_overhead_hp, bw);
            writeLine("toggle_npc_overhead_names", toggle_npc_overhead_names, bw);
            writeLine("toggle_npc_overhead_hp", toggle_npc_overhead_hp, bw);
            writeLine("ground_snow", ground_snow, bw);
            writeLine("show_pet_options", shift_pet_options, bw);
            writeLine("toggle_music", toggle_music, bw);
            writeLine("draw_fps", draw_fps, bw);
            writeLine("moving_prayers", moving_prayers, bw);
            bw.flush();
            bw.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void load() {
        location = SignLink.findDataDir() + "/settings.txt";
        createSettingsFile(location);

        try {
            BufferedReader file = new BufferedReader(new FileReader(location));
            String line;
            while ((line = file.readLine()) != null) {
                fileLine = line;
                if (contains("cameraSpeed")) {
                    Client.cameraSpeed = readString();
                } else if (contains("zoom_distance")) {
                    Client.zoom_distance = readInt();
                } else if (contains("drag_item_value")) {
                    drag_item_value = readInt();
                } else if (contains("player_attack_priority")) {
                    player_attack_priority = readInt();
                } else if (contains("npc_attack_priority")) {
                    npc_attack_priority = readInt();
                } else if (contains("keybinds")) {
                    String array = readString(); // it will look like [1, 2, 3, 4]
                    //System.out.println("we loaded "+array);
                    array = array.substring(1, array.length() - 1); // strip the [ and ]
                    String[] entries = array.split(","); // split up via delimiter
                    //System.out.println("entries are "+Arrays.toString(entries));
                    for (int index = 0; index < Keybinding.KEYBINDINGS.length; index++) {
                        // set the keybinds as each entry, casting Str to Int
                        Keybinding.KEYBINDINGS[index] = Integer.parseInt(entries[index].trim());
                        // strip whitespace on " -1"
                        //System.out.println("Key: "+Keybinding.KEYBINDINGS[index]);
                    }
                } else if (contains("show_exp_counter")) {
                    show_exp_counter = readBoolean();
                } else if (contains("counter_size")) {
                    counter_size = readInt();
                } else if (contains("counter_color")) {
                    counter_color = readInt();
                } else if (contains("counter_speed")) {
                    counter_speed = readFloat();
                } else if (contains("counter_position")) {
                    counter_position = readInt();
                } else if (contains("counter_group")) {
                    counter_group = readBoolean();
                } else if (contains("sound_state")) {
                    sound_state = readInt();
                } else if (contains("toggle_music")) {
                    toggle_music = readBoolean();
                } else if (contains("show_hit_predictor")) {
                    show_hit_predictor = readBoolean();
                } else if (contains("hidePrivateChat")) {
                    hidePrivateChat = readBoolean();
                } else if (contains("loginLogoutNotification")) {
                    loginLogoutNotification = readBoolean();
                } else if (contains("profanityFilter")) {
                    profanityFilter = readBoolean();
                } else if (contains("cameraMovement")) {
                    cameraMovement = readBoolean();
                } else if (contains("shiftClick")) {
                    shiftClick = readBoolean();
                } else if (contains("esc_close")) {
                    esc_close = readBoolean();
                } else if (contains("mouseButtons")) {
                    mouseButtons = readBoolean();
                } else if (contains("acceptAid")) {
                    acceptAid = readBoolean();
                } else if (contains("brightness")) {
                    brightness = readInt();
                } else if (contains("chatEffects")) {
                    chatEffects = readBoolean();
                } else if (contains("privateChat")) {
                    privateChat = readBoolean();
                } else if (contains("transparentSidePanel")) {
                    transparentSidePanel = readBoolean();
                } else if (contains("transparentChatbox")) {
                    transparentChatbox = readBoolean();
                } else if (contains("sideStonesArrangement")) {
                    sideStonesArrangement = readBoolean();
                } else if (contains("roofs")) {
                    roofs = readBoolean();
                } else if (contains("orbs")) {
                    orbs = readBoolean();
                } else if (contains("draw_orb_arc")) {
                    draw_orb_arc = readBoolean();
                } else if (contains("draw_orb_arc")) {
                    draw_orb_arc = readBoolean();
                } else if (contains("draw_special_orb")) {
                    draw_special_orb = readBoolean();
                } else if (contains("draw_health_overlay")) {
                    draw_health_overlay = readBoolean();
                } else if (contains("draw_ground_item_overlay")) {
                    toggle_item_pile_names = readBoolean();
                } else if (contains("draw_timers")) {
                    draw_timers = readBoolean();
                } else if (contains("display_names")) {
                    toggle_overhead_names = readBoolean();
                } else if (contains("toggle_overhead_hp")) {
                    toggle_overhead_hp = readBoolean();
                } else if (contains("toggle_npc_overhead_names")) {
                    toggle_npc_overhead_names = readBoolean();
                } else if (contains("toggle_npc_overhead_hp")) {
                    toggle_npc_overhead_hp = readBoolean();
                } else if (contains("ground_snow")) {
                    ground_snow = readBoolean();
                } else if (contains("show_pet_options")) {
                    shift_pet_options = readBoolean();
                } else if (contains("toggle_music")) {
                    toggle_music = readBoolean();
                } else if (contains("draw_fps")) {
                    draw_fps = readBoolean();
                } else if (contains("moving_prayers")) {
                    moving_prayers = readBoolean();
                }
            }
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update() {
        int length = SettingData.values().length;
        for (int index = 0; index < length; index++) {
            SettingData setting = SettingData.forOrdinal(index);
            if (setting == null) {
                return;
            }
            Client.singleton.updateString(setting.setting, 50301 + index);
            Client.singleton.toggleConfig(900 + index, setting.status() ? 1 : 0);
        }
        save();
    }

    /**
     * Handles clicking on the settings interface.
     */
    public boolean click(Client client, int button) {

        int base_button = 50301;
        int index = button - base_button;

        SettingData settingData = SettingData.forOrdinal(index);

        if (!(index >= SettingData.values().length) && settingData != null) {
            settingData.handle(client);
            update();
            return true;
        }

        base_button = 50350;
        index = button - base_button;

        settingData = SettingData.forOrdinal(index);

        if (!(index >= SettingData.values().length) && settingData != null) {
            settingData.handle(client);
            update();
            return true;
        }
        return false;
    }

    /**
     * Handles the clicking buttons.
     *
     * @param button The button being pressed
     * @return true if the button is an actual button false otherwise.
     */
    public boolean settingButtons(int button) {
        switch (button) {

//            case ESCAPE_CONFIG_BUTTON:
//                System.out.println("here esc_close: "+esc_close);
//                esc_close = !esc_close;
//                Client.singleton.toggleConfig(594, esc_close ? 1 : 0);
//                return true;

            case BRIGHTNESS_STATE_FOUR:
                Client.singleton.toggleConfig(505, 0);
                Client.singleton.toggleConfig(506, 0);
                Client.singleton.toggleConfig(507, 0);
                Client.singleton.toggleConfig(508, 1);
                Client.singleton.toggleConfig(166, 4);
                brightness = 4;
                return true;

            case BRIGHTNESS_STATE_THREE:
                Client.singleton.toggleConfig(505, 0);
                Client.singleton.toggleConfig(506, 0);
                Client.singleton.toggleConfig(507, 1);
                Client.singleton.toggleConfig(508, 0);
                Client.singleton.toggleConfig(166, 3);
                brightness = 3;
                return true;

            case BRIGHTNESS_STATE_TWO:
                Client.singleton.toggleConfig(505, 0);
                Client.singleton.toggleConfig(506, 1);
                Client.singleton.toggleConfig(507, 0);
                Client.singleton.toggleConfig(508, 0);
                Client.singleton.toggleConfig(166, 2);
                brightness = 2;
                return true;

            case BRIGHTNESS_STATE_ONE:
                Client.singleton.toggleConfig(505, 1);
                Client.singleton.toggleConfig(506, 0);
                Client.singleton.toggleConfig(507, 0);
                Client.singleton.toggleConfig(508, 0);
                Client.singleton.toggleConfig(166, 1);
                brightness = 1;
                return true;

            case FOLLOWER_OPTIONS_BUTTON:
                shift_pet_options = !shift_pet_options;
                Client.singleton.toggleConfig(291, shift_pet_options ? 1 : 0);
                return true;

            case MOUSE_BUTTONS_BUTTON:
                mouseButtons = !mouseButtons;
                Client.singleton.toggleConfig(170, mouseButtons ? 1 : 0);
                return true;

            case ACCEPT_AID_BUTTON:
                acceptAid = !acceptAid;
                Client.singleton.toggleConfig(427, acceptAid ? 1 : 0);
                return true;

            case FIXED_MODE_BUTTON:
                Client.singleton.frameMode(765, 503);
                return true;

            case RESIZABLE_MODE_BUTTON:
                Client.singleton.frameMode(766, 559);
                return true;

            case HOUSE_OPTIONS_BUTTON:
                // We don't have construction
                return true;

            case BOND_POUCH_BUTTON:
                // TODO open bond interface
                return true;

            case RESTORE_ZOOM_BUTTON:
                return true;

            case ZOOM_TOGGLE_BUTTON:
                zoomToggle = !zoomToggle;
                Client.singleton.toggleConfig(294, zoomToggle ? 1 : 0);
                return true;

            case CHAT_EFFECTS_BUTTON:
                chatEffects = !chatEffects;
                Client.singleton.toggleConfig(171, chatEffects ? 1 : 0);
                return true;

            case SPLIT_PRIVATE_CHAT_BUTTON:
                privateChat = !privateChat;
                Client.singleton.toggleConfig(287, privateChat ? 1 : 0);
                return true;

            case HIDE_PRIVATE_CHAT_BUTTON:
                hidePrivateChat = !hidePrivateChat;
                Client.singleton.toggleConfig(288, hidePrivateChat ? 1 : 0);
                return true;

            case PROFANITY_FILTER_BUTTON:
                profanityFilter = !profanityFilter;
                Client.singleton.toggleConfig(289, profanityFilter ? 1 : 0);
                return true;

            case NOTIFICATIONS_BUTTON:

            case DISPLAY_NAME_BUTTON:
                // TODO
                return true;

            case LOGIN_LOGOUT_NOTIFICATION_TIMEOUT_BUTTON:
                loginLogoutNotification = !loginLogoutNotification;
                Client.singleton.toggleConfig(290, loginLogoutNotification ? 1 : 0);
                return true;

            case MOUSE_CAMERA_BUTTON:
                cameraMovement = !cameraMovement;
                Client.singleton.toggleConfig(207, cameraMovement ? 1 : 0);
                return true;

            case SHIFT_CLICK_DROP_BUTTON:
                shiftClick = !shiftClick;
                Client.singleton.toggleConfig(293, shiftClick ? 1 : 0);
                return true;

            case ORBS:
                orbs = !orbs;
                Client.singleton.toggleConfig(299, orbs ? 1 : 0);
                return true;

            case TRANSPARENT_SIDE_PANEL:
                transparentSidePanel = !transparentSidePanel;
                Client.singleton.toggleConfig(295, transparentSidePanel ? 1 : 0);
                break;

            case TRANSPARENT_CHATBOX:
                transparentChatbox = !transparentChatbox;
                Client.singleton.toggleConfig(296, transparentChatbox ? 1 : 0);
                return true;

            case SIDE_STONES_ARRANGEMENT:
                sideStonesArrangement = !sideStonesArrangement;
                Client.singleton.toggleConfig(297, sideStonesArrangement ? 1 : 0);
                return true;

            case ROOF_REMOVAL:
                roofs = !roofs;
                Client.singleton.toggleConfig(298, roofs ? 1 : 0);
                return true;

            case 43039:
                Client.tabInterfaceIDs[11] = 50290;
                Client.update_tab_producer = true;
                return true;

            case 50293:
                Client.tabInterfaceIDs[11] = 42500;
                Client.update_tab_producer = true;
                return true;

            case SOUND_BUTTON_OFF:
                Client.singleton.toggleConfig(169, 0);
                sound_state = 0;
                return true;

            case SOUND_BUTTON_STATE_ONE:
                Client.singleton.toggleConfig(169, 1);
                sound_state = 1;
                return true;

            case SOUND_BUTTON_STATE_TWO:
                Client.singleton.toggleConfig(169, 2);
                sound_state = 2;
                return true;

            case SOUND_BUTTON_STATE_THREE:
                Client.singleton.toggleConfig(169, 3);
                sound_state = 3;
                return true;

            case SOUND_BUTTON_STATE_FOUR:
                Client.singleton.toggleConfig(169, 4);
                sound_state = 4;
                return true;

            default:
                //System.out.println("Unknown settings button with id: " + button + ".");
        }
        return false;
    }

    public static final int SOUND_BUTTON_OFF = 43538;
    public static final int SOUND_BUTTON_STATE_ONE = 43539;
    public static final int SOUND_BUTTON_STATE_TWO = 43540;
    public static final int SOUND_BUTTON_STATE_THREE = 43541;
    public static final int SOUND_BUTTON_STATE_FOUR = 43542;
    public static final int WELCOME_SCREEN_PLAY_BUTTON = 44420;
    public static final int ADVANCED_OPTIONS_BUTTON = 42524;
    public static final int ESCAPE_CONFIG_BUTTON = 53003;
    public static final int BRIGHTNESS_STATE_ONE = 906;
    public static final int BRIGHTNESS_STATE_TWO = 908;
    public static final int BRIGHTNESS_STATE_THREE = 910;
    public static final int BRIGHTNESS_STATE_FOUR = 912;
    public static final int ZOOM_TOGGLE_BUTTON = 44151;
    public static final int RESTORE_ZOOM_BUTTON = 42521;
    public static final int FIXED_MODE_BUTTON = 42522;
    public static final int RESIZABLE_MODE_BUTTON = 42523;
    public static final int ACCEPT_AID_BUTTON = 42506;
    public static final int HOUSE_OPTIONS_BUTTON = 42508;
    public static final int BOND_POUCH_BUTTON = 42509;
    public static final int CHAT_EFFECTS_BUTTON = 42541;
    public static final int SPLIT_PRIVATE_CHAT_BUTTON = 42542;
    public static final int HIDE_PRIVATE_CHAT_BUTTON = 42543;
    public static final int MOUSE_BUTTONS_BUTTON = 42551;
    public static final int FOLLOWER_OPTIONS_BUTTON = 42553;
    public static final int PROFANITY_FILTER_BUTTON = 41541;
    public static final int NOTIFICATIONS_BUTTON = 41542;
    public static final int LOGIN_LOGOUT_NOTIFICATION_TIMEOUT_BUTTON = 41543;
    public static final int DISPLAY_NAME_BUTTON = 42544;
    public static final int KEYBINDING_BUTTON = 42552;
    public static final int MOUSE_CAMERA_BUTTON = 41551;
    public static final int SHIFT_CLICK_DROP_BUTTON = 41552;
    public static final int TRANSPARENT_SIDE_PANEL = 43007;
    public static final int TRANSPARENT_CHATBOX = 43025;
    public static final int SIDE_STONES_ARRANGEMENT = 43032;
    public static final int ROOF_REMOVAL = 43019;
    public static final int ORBS = 43022;

}
