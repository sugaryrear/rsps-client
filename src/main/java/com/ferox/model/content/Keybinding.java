package com.ferox.model.content;

import java.awt.event.KeyEvent;

import com.ferox.Client;
import com.ferox.cache.graphics.widget.Widget;
import com.ferox.util.ConfigUtility;

/**
 * Handles bindings for the gameframe tabs.
 * @author Professor Oak
 *
 */
public class Keybinding {

    public static final int MIN_FRAME = 53009;
    public static final int RESTORE_DEFAULT = 53004;
    public static final int ESCAPE_CONFIG = 53003;
    public static final String[] OPTIONS = {"None", "ESC", "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "F10", "F11", "F12"};

    public static final int[] KEYS = {-1, KeyEvent.VK_ESCAPE, KeyEvent.VK_F1, KeyEvent.VK_F2, KeyEvent.VK_F3, KeyEvent.VK_F4, KeyEvent.VK_F5, KeyEvent.VK_F6, KeyEvent.VK_F7, KeyEvent.VK_F8,
            KeyEvent.VK_F9, KeyEvent.VK_F10, KeyEvent.VK_F11, KeyEvent.VK_F12};


    public static int[] KEYBINDINGS;

    static {
        restoreDefault();
    }

    public static void restoreDefault() {
        KEYBINDINGS = new int[]{
                KeyEvent.VK_F1,
                -1,
                -1,
                KeyEvent.VK_F5,
                KeyEvent.VK_F2,
                KeyEvent.VK_F3,
                KeyEvent.VK_F4,
                KeyEvent.VK_F6,
                KeyEvent.VK_F7,
                KeyEvent.VK_F8,
                KeyEvent.VK_F9,
                KeyEvent.VK_F10,
                KeyEvent.VK_F11,
                -1,
                KeyEvent.VK_F12
        };
    }

    public static void checkDuplicates(int key, int index) {
        System.out.println("index: "+index);
        for (int i = 0; i < KEYBINDINGS.length; i++) {
            if (KEYS[key] == KEYBINDINGS[i] && i != index && KEYBINDINGS[i] != -1) {
                turnoffBindFor(i);
            }
        }
    }

    public static void onVarpUpdate(int varpId, int newVal) {
        // If the user is toggling on the escape keybind to close interfaces then remove it from any other mappings.
        if (varpId == ConfigUtility.ESC_CLOSE_ID && newVal != 0) {
           // System.out.println("Here2");
            for (int i = 0; i < KEYBINDINGS.length; i++) {
                if (KEYBINDINGS[i] == KeyEvent.VK_ESCAPE) {
                    turnoffBindFor(i);
                    break;
                }
            }
        }
    }

    private static void turnoffBindFor(int index) {
        KEYBINDINGS[index] = -1;
        Widget.cache[MIN_FRAME + 3 * index].dropdown.setSelected("None");
    }

    public static void bind(int index, int key) {
        System.out.println("index: "+index+" and key: "+key);
        checkDuplicates(key, index);
        final int keycode = KEYS[key];
        KEYBINDINGS[index] = keycode;
        // Turn off the key bind of closing interfaces ith escape key if its being mapped for something else.
        if (keycode == KeyEvent.VK_ESCAPE && Client.singleton.settings[ConfigUtility.ESC_CLOSE_ID] != 0) {

            Client.singleton.settings[ConfigUtility.ESC_CLOSE_ID] = 0;
        }
        Client.singleton.setting.save();
    }

    public static boolean isBound(int key) {
        for (int i = 0; i < KEYBINDINGS.length; i++) {
            if (key == KEYBINDINGS[i]) {
                Client.setTab(i);
                return true;
            }
        }
        return false;
    }

    private static int indexOf(int key) {
        for (int i = 0; i < KEYS.length; i++) {
            if (KEYS[i] == key)
                return i;
        }
        return -1;
    }

    public static void updateInterface() {

        for (int i = 0; i < KEYBINDINGS.length; i++) {

            int key = KEYBINDINGS[i];
            String current = "None";

            if (key != -1) {
                current = OPTIONS[indexOf(key)];
            }

            Widget.cache[MIN_FRAME + 3 * i].dropdown.setSelected(current);
        }
    }
}
