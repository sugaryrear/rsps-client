package com.ferox.cache.graphics.widget.impl;

import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.Widget;
import com.ferox.model.settings.SettingData;

/**
 * This class represents a custom widget. That shows all our client settings,
 * which can be toggled.
 * 
 * @author Patrick van Elderen | 1 sep. 2019 : 18:13:24
 */
public class ClientSettingsWidget extends Widget {

    public static void unpack(AdvancedFont[] font) {
        Widget widget = addInterface(50290);
        addSprite(50291, 164);
        addText(50292, "Client settings", font, 0, 0xff981f);
        closeButton(50293, 895, 895, true);
        widget.totalChildren(4);

        widget.child(0, 50291, 3, 42);
        widget.child(1, 50292, 11, 48);
        widget.child(2, 50293, 170, 48);
        widget.child(3, 50010, 1, 60);

        Widget scroll_widget = addTabInterface(50010);
        scroll_widget.scrollPosition = 0;
        scroll_widget.contentType = 0;
        scroll_widget.width = 165;
        scroll_widget.height = 145;
        scroll_widget.scrollMax = (SettingData.values().length * 20) + 5; //Dynamically calculate scroll max.
        int y = 5, length = SettingData.values().length;
        scroll_widget.totalChildren(length * 2);
        int child = 0;
        for (int index = 0; index < length; index++) {
            SettingData settingData = SettingData.forOrdinal(index);
            String name = settingData == null ? "None" : settingData.setting;
            addHoverText(50301 + index, name, "Toggle " + name, font, 0, 0xD46E08, false, true, 168, 0xFFFFFF);
            scroll_widget.child(child, 50301 + index, 30, y + 2);
            child++;
            addConfigButton(50350 + index, 50300, 491, 641, "Toggle " + name, 900 + index, 0, OPTION_OK);
            scroll_widget.child(child, 50350 + index, 12, y - 1);
            child++;
            y += 20;
        }
    }

}
