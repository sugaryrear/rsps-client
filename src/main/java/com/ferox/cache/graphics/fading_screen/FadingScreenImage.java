package com.ferox.cache.graphics.fading_screen;

import com.ferox.Client;
import com.ferox.cache.graphics.SimpleImage;
import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.draw.Rasterizer2D;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * april 27, 2020
 */
public class FadingScreenImage extends FadingScreen {

    SimpleImage background;

    private int width, height;

    public FadingScreenImage(AdvancedFont font, String text, byte state, byte seconds, int x, int y, int maximumWidth) {
        super(font, text, state, seconds, x, y, maximumWidth);
        width = maximumWidth + 10;
        height = 16 + wrapped.length * 20;

        SimpleImage[] resources = Client.fadingScreenImages;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setComposite(AlphaComposite.Clear);
        graphics.fillRect(0, 0, width, height);
        graphics.setComposite(AlphaComposite.Src);

        for (int xOffset = 0; xOffset < width; xOffset += 8) {
            graphics.drawImage(resources[1].getImage(), xOffset, 0, null);
            graphics.drawImage(resources[7].getImage(), xOffset, height - 8, null);
        }
        for (int yOffset = 0; yOffset < height; yOffset += 8) {
            graphics.drawImage(resources[6].getImage(), 0, yOffset, null);
            graphics.drawImage(resources[5].getImage(), width - 8, yOffset, null);
        }
        graphics.drawImage(resources[0].getImage(), 0, 0, null);
        graphics.drawImage(resources[4].getImage(), 0, height - 8, null);
        graphics.drawImage(resources[2].getImage(), width - 8, 0, null);
        graphics.drawImage(resources[3].getImage(), width - 8, height - 8, null);
        background = new SimpleImage(image);
    }

    @Override
    public void draw() {
        if (state == 0) {
            return;
        }
        long end = watch.getStartTime() + (1000L * seconds);
        long increment = ((end - watch.getStartTime()) / 100);
        if (increment > 0) {
            long percentile = watch.getTime() / increment;
            int opacity = (int) (percentile * 2.55);
            if (state < 0) {
                opacity = 255 - opacity;
            }
            if (percentile > -1 && percentile <= 100) {
                Rasterizer2D.set_clip(x, y, x + width,y + height);
                Rasterizer2D.drawAlphaBox(x + 4, y + 4, width - 8, height - 8, 0x000000, opacity);
                background.drawAdvancedTransparentSprite(x, y, opacity);
                int textYOffset = 22;
                for (String sentence : wrapped) {
                    font.draw_centered(sentence, x + width / 2, y + textYOffset, 0xFFFFFF, 0x000000, opacity);
                    textYOffset += 18;
                }
                if (percentile >= 100) {
                    watch.stop();
                    state = 0;
                }
            }
        } else {
            watch.stop();
            state = 0;
        }
    }

}
