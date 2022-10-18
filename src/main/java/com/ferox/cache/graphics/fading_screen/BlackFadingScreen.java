package com.ferox.cache.graphics.fading_screen;

import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.draw.Rasterizer2D;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * april 27, 2020
 */
public class BlackFadingScreen extends FadingScreen {

    private int width, height;

    public BlackFadingScreen(AdvancedFont font, String text, byte state, byte seconds, int x, int y, int width, int height, int characterWrap) {
        super(font, text, state, seconds, x, y, characterWrap);
        this.width = width;
        this.height = height;
    }

    public void draw() {
        if (state == 0) {
            return;
        }
        long end = watch.getStartTime() + (1000L * seconds);
        long increment = ((end - watch.getStartTime()) / 100);
        if (increment > 0) {
            long percentile = watch.getTime() / increment;
            int opacity = (int) ((percentile * (Byte.MAX_VALUE / 100)) * 2);
            if (state < 0) {
                opacity = 255 - opacity;
            }
            if (percentile > -1 && percentile <= 100) {
                Rasterizer2D.set_clip(x, y,x + width,y + height);
                Rasterizer2D.drawAlphaBox(x, y, width, height, 0x000000, opacity);
                if (percentile > 0 && state == 1 || percentile < 100 && state == -1) {
                    int textYOffset = 0;
                    for (String sentence : wrapped) {
                        font.draw_centered(sentence, x + width / 2, (y + height / 4) + textYOffset, 0xFFFFFF, 0x000000, opacity);
                        textYOffset += 20;
                    }
                } else if (percentile == 100) {
                    watch.stop();
                    state = 0;
                }
            }
        }
    }

}
