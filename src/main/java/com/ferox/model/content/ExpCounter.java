package com.ferox.model.content;

import com.ferox.Client;
import com.ferox.cache.graphics.SimpleImage;
import com.ferox.cache.graphics.font.AdvancedFont;

import java.util.*;

/**
 * @author Patrick van Elderen | November, 15, 2020, 12:49
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class ExpCounter {

    private static final int START_SPRITE = 82;
    private static final int START = 130;
    private static final int STOP = 35;
    private static final int MIDLINE = (START + STOP) / 2;
    private static int xpCounter;
    private static final ArrayList<ExpGain> GAINS = new ArrayList<>();
    private static ExpGain currentGain = null;

    public static void addXP(int skill, int xp, boolean increment) {
        if (skill == 99) {
            xpCounter = xp;
        } else {
            if (increment) {
                xpCounter += xp;
            }
            if (xp != 0) {
                if (currentGain != null && Math.abs(currentGain.getY() - START) <= getSize(Client.singleton.setting.counter_size).base_char_height) {
                    currentGain.xp += xp;
                    currentGain.addSprite(skill);
                } else {
                    ExpGain gain = new ExpGain(skill, xp);
                    GAINS.add(gain);
                    currentGain = gain;
                }
            }
        }
    }

    public static void drawExperienceCounter() {
        boolean isFixed = Client.screen == Client.ScreenMode.FIXED;

        int x = Client.window_width - 80 - 255, y = 12;
        /*int boxWidth = Client.spriteCache.get(521).width;
        int skillWidth = Client.spriteCache.get(81).width;

        if (Client.singleton.setting.counter_position == 0) {
            x = Client.window_width - boxWidth - 255;
        } else if (Client.singleton.setting.counter_position == 1) {
            x = (Client.window_width - boxWidth - (isFixed ? 255 : 0)) / 2;
        } else if (Client.singleton.setting.counter_position == 2) {
            x = 2;
        }

        Client.spriteCache.get(521).draw_transparent(x, 2, 230);
        Client.spriteCache.get(81).drawSprite(x + 4, 6);

        if (xpCounter >= 0) {
            AdvancedFont text = getSize(Client.singleton.setting.counter_size);
            int xPos = x + (skillWidth + boxWidth) / 2;
            String string = NumberFormat.getInstance().format(xpCounter);
            text.draw_centered(string, xPos, 23 + Client.singleton.setting.counter_size, Client.singleton.setting.counter_color,true);
        }*/

        if (!GAINS.isEmpty()) {
            Iterator<ExpGain> gained = GAINS.iterator();

            while (gained.hasNext()) {
                ExpGain gain = gained.next();

                if (gain.getY() > STOP) {

                    if (gain.getY() >= MIDLINE) {
                        gain.increaseAlpha();
                    } else {
                        gain.decreaseAlpha();
                    }

                    gain.changeY();

                } else if (gain.getY() <= STOP) {
                    gained.remove();
                }

                if (gain.getY() > STOP) {
                    Queue<ExpSprite> temp = new PriorityQueue<>(gain.sprites);
                    int dx = 0;

                    while (!temp.isEmpty()) {
                        ExpSprite expSprite = temp.poll();
                        expSprite.sprite.drawSprite1(x + dx, (int) (y + gain.getY()), gain.getAlpha());
                        dx += expSprite.sprite.width + 1;
                    }
                    String drop = String.format("<trans=%s>%,d", gain.getAlpha(), gain.getXP());
                    getSize(Client.singleton.setting.counter_size).draw(drop, x + dx + 2, (int) (gain.getY() + y) + 14, Client.singleton.setting.counter_color, 0);
                }
            }
        }
    }


    private static AdvancedFont getSize(int size) {
        if (size == 0)
            return Client.adv_font_small;
        if (size == 2)
            return Client.adv_font_bold;
        return Client.adv_font_regular;
    }

    static class ExpSprite implements Comparable<ExpSprite> {
        private int skill;
        private SimpleImage sprite;

        ExpSprite(int skill, SimpleImage sprite) {
            this.skill = skill;
            this.sprite = sprite;
        }

        @Override
        public int compareTo(ExpSprite other) {
            return Integer.signum(other.skill - skill);
        }
    }

    static class ExpGain {
        private int skill;
        private int xp;
        private float y;
        private double alpha = 0;
        private Set<ExpSprite> sprites = new TreeSet<>();

        ExpGain(int skill, int xp) {
            this.skill = skill;
            this.xp = xp;
            this.y = START;
            addSprite(skill);
        }

        void addSprite(int skill) {
            for (ExpSprite sprite : sprites) {
                if (sprite.skill == skill) {
                    return;
                }
            }
            sprites.add(new ExpSprite(skill, Client.spriteCache.get(START_SPRITE + skill)));
        }

        void changeY() {
            y -= Client.singleton.setting.counter_speed;
        }

        int getXP() {
            return xp;
        }

        public float getY() {
            return y;
        }

        public int getAlpha() {
            return (int) alpha;
        }

        void increaseAlpha() {
            alpha += alpha < 256 ? 30 : 0;
            alpha = alpha > 256 ? 256 : alpha;
        }

        void decreaseAlpha() {
            alpha -= (alpha > 0 ? 30 : 0) * 0.10;
            alpha = alpha > 256 ? 256 : alpha;
        }
    }
}
