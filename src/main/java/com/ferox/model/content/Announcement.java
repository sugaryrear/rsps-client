package com.ferox.model.content;

import com.ferox.Client;
import com.ferox.cache.graphics.SimpleImage;

public final class Announcement {

    private final String text;
    private final String link;

    private final Client instance = Client.singleton;

    private int lastX = 0, lastY = 0;

    public Announcement(String text) {
        this(text, " ");
    }

    public Announcement(String text, String link) {
        this.text = text;
        this.link = link;
    }

    public String getText() {
        return text;
    }

    public String getLink() {
        return link;
    }

    public boolean hasUrl() {
        return !link.equals(" ");
    }

    public void isHovered(int mouseX, int mouseY) {
        int x = text.length() * 6;
        boolean hovered = mouseX >= lastX && mouseX <= (lastX + x) && mouseY >= (lastY - 10) && mouseY <= (lastY + 10);

        if (hovered && instance.isDisplayed) {
            actions();
        }
    }

    public void actions() {
        if (hasUrl()) {
            instance.menuActionText[2] = "Open Link";
            instance.menuActionText[1] = "Hide";
            instance.menuActionTypes[1] = 5001;
            instance.menuActionTypes[2] = 5000;
            instance.menuActionRow = 3;
        } else {
            instance.menuActionText[1] = "Hide";
            instance.menuActionTypes[1] = 5001;
            instance.menuActionRow = 2;
        }
    }

    public void dismiss() {
        instance.isDisplayed = false;
    }

    public void process() {
        int offsetX = 0;
        if (hasUrl()) {
            SimpleImage sprite = Client.spriteCache.get(856);
            if (sprite != null) {
                offsetX = sprite.width + 1;
                sprite.drawAdvancedSprite(5, lastY - 11);
            }
        }

        boolean fixed = Client.screen == Client.ScreenMode.FIXED;
        lastX = 5 + offsetX;
        lastY = fixed ? 328 : Client.window_height - 170;
        Client.adv_font_regular.draw(Client.capitalizeFirstChar(text), lastX, lastY, 0xffff00, -1);
    }
}
