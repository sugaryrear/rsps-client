package com.ferox.cache.graphics.widget;

import com.ferox.Client;
import com.ferox.cache.graphics.SimpleImage;
import com.ferox.util.ConfigUtility;

public class Slider {

    private final double minValue, maxValue, length;
    private final SimpleImage[] images = new SimpleImage[2];
    private int position = 86;
    private double value;
    private int x, y;

    public Slider(SimpleImage icon, SimpleImage background, double minimumValue, double maximumValue) {
        this.images[0] = icon;
        this.images[1] = background;
        this.minValue = this.value = minimumValue;
        this.maxValue = maximumValue;
        this.length = this.images[1].width;
    }

    public static void handleSlider(int mX, int mY) {
        int tabInterfaceId = Client.tabInterfaceIDs[Client.sidebarId];

        if (tabInterfaceId != -1) {

            if (tabInterfaceId == 42500) {
                tabInterfaceId = Widget.cache[42500].children[9];
            } // Settings tab adjustment
            Widget widget = Widget.cache[tabInterfaceId];

            if (widget == null || widget.children == null) {
                return;
            }

            for (int childId : widget.children) {
                if (Client.singleton.settings[ConfigUtility.ZOOM_TOGGLE_ID] == 1) {
                    return;
                }
                Widget child = Widget.cache[childId];
                if (child == null || child.slider == null)
                    continue;
                child.slider.handleClick(mX, mY, Client.screen == Client.ScreenMode.FIXED ? 519 : 0, Client.screen == Client.ScreenMode.FIXED ? 168 : 0);
            }
            Client.update_tab_producer = true;
        }

        int interfaceId = Client.widget_overlay_id;
        if (interfaceId != -1) {
            Widget widget = Widget.cache[interfaceId];
            if (widget == null || widget.children == null) {
                return;
            }
            for (int childId : widget.children) {
                Widget child = Widget.cache[childId];
                if (child == null || child.slider == null)
                    continue;
                child.slider.handleClick(mX, mY, 4, 4);
            }
        }
    }

    public void draw(int x, int y) {
        this.x = x;
        this.y = y;
        images[1].drawSprite(x, y);
        images[0].drawSprite(x + position - (int) (position / length * images[0].width), y - images[0].height / 2 + images[1].height / 2);
    }

    public void handleClick(int mouseX, int mouseY, int offsetX, int offsetY) {
        if (Client.singleton.settings[ConfigUtility.ZOOM_TOGGLE_ID] == 1) {
            return;
        }
        int mX = Client.singleton.cursor_x;
        int mY = Client.singleton.cursor_y;
        if (mX - offsetX >= x && mX - offsetX <= x + length
                && mY - offsetY >= y + images[1].height / 2 - images[0].height / 2
                && mY - offsetY <= y + images[1].height / 2 + images[0].height / 2) {
            position = mouseX - x - offsetX;
            if (position >= length) {
                position = (int) length;
            }
            if (position <= 0) {
                position = 0;
            }
            value = minValue + (position / length) * (maxValue - minValue);
            //System.out.println("d: " + length);
            if (value < minValue) {
                value = minValue;
            }
            if (value > maxValue) {
                value = maxValue;
            }
            Client.zoom_distance = (int) (minValue + maxValue - value);
            Client.singleton.setting.save();
        }
    }

    public double getPercentage() {
        return ((position / length) * 100);
    }

    public void setValue(double value) {

        if (value < minValue) {
            value = minValue;
        } else if (value > maxValue) {
            value = maxValue;
        }

        this.value = value;
        double shift = 1 - ((value - minValue) / (maxValue - minValue));

        position = (int) (length * shift);
    }
}
