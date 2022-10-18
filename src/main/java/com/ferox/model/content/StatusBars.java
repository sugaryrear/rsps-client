package com.ferox.model.content;

import com.ferox.Client;
import com.ferox.draw.Rasterizer2D;

import java.util.HashMap;
import java.util.Map;

/*
 * Copyright (c) 2020, Mark_ <https://www.rune-server.ee/members/mark_/>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
public class StatusBars {

    public Restore restore = Restore.NONE;

    public enum Restore {

        NONE(0,0, BarType.PRAYER),
        PRAYER_3(139,40, BarType.PRAYER);

        private final int item;
        private final int restore;
        private final BarType type;

        public int getItem() {
            return item;
        }

        public int getRestore() {
            return restore;
        }

        public BarType getType() {
            return type;
        }

        Restore(int item, int restore,BarType type) {
            this.item = item;
            this.restore = restore;
            this.type = type;
        }

        private static final Map<Integer, Restore> restoreMap = new HashMap<>();

        static {
            for (Restore restore : values()) {
                restoreMap.put(restore.item, restore);
            }
        }

        public static Restore get(int item) {
            return restoreMap.getOrDefault(item,NONE);
        }

    }

    public enum BarType {
        HP(0x7F230E,0xFF7006,0x0009100,0x004100,9),
        PRAYER(0x2D9491,0x3970BA,10);

        private final int normal;
        private final int heal;
        private int poisoned;
        private int venom;
        private final int icon;

        public int getNormal() {
            return normal;
        }

        public int getHeal() {
            return heal;
        }

        public int getPoisoned() {
            return poisoned;
        }

        public int getVenom() {
            return venom;
        }

        public int getIcon() {
            return icon;
        }

        BarType(int normal, int heal, int icon) {
            this.normal = normal;
            this.heal = heal;
            this.icon = icon;
        }

        BarType(int normal, int heal,int poisoned,int venom,int icon) {
            this.normal = normal;
            this.heal = heal;
            this.poisoned = poisoned;
            this.venom = venom;
            this.icon = icon;
        }
    }

    public void drawStatusBars(int xOffset,int yOffset) {
        if(!Client.singleton.setting.status_bars) {
            return;
        }

        int hpColor = 0;
        int prayerColor = BarType.PRAYER.getNormal();

        if (Client.singleton.poisonType == 0) {
            hpColor = BarType.HP.getNormal();
        } else if (Client.singleton.poisonType == 1) {
            hpColor = BarType.HP.getPoisoned();
        } else if (Client.singleton.poisonType == 2) {
            hpColor = BarType.HP.getVenom();
        }

        renderStatusBars(xOffset,yOffset,BarType.HP,hpColor);
        renderStatusBars(xOffset,yOffset,BarType.PRAYER,prayerColor);
    }

    public void renderStatusBars(int xOffset,int  yOffset,BarType type, int backgroundColor) {
        int hitpoints = Client.singleton.currentLevels[3];
        int prayer = Client.singleton.currentLevels[5];
        int percent = getPercent(getPercent(type,0),250);


        Rasterizer2D.draw_rectangle_outline(xOffset + 11 + getBarOffsetX(type), 42 + yOffset, 20, 250, 0x000000);

        Rasterizer2D.draw_filled_rect(xOffset + 11 + getBarOffsetX(type), 42 + yOffset, 20, 250 , 0x000000,130);

        Rasterizer2D.draw_filled_rect(xOffset + 12 + getBarOffsetX(type), 242 - percent + 50 + yOffset , 18, percent , backgroundColor,135);

        if(restore.type == type && restore != Restore.NONE) {
            Rasterizer2D.draw_filled_rect(xOffset + 12 + getBarOffsetX(type), 242 - getPercent(getPercent(type,restore.restore),250) + 50 + yOffset, 18, getPercent(getPercent(type,restore.restore),250) , type.getHeal(),140);
        }

        if(getPercent(type,0) < 20) {
            if(Client.game_tick % 20 < 10) {
                if(type.icon == 9) {
                    Client.spriteCache.get(type.getIcon()).drawSprite(xOffset + 13 + getBarOffsetX(type), 50 + yOffset);
                } else if(type.icon == 10) {
                    Client.spriteCache.get(type.getIcon()).drawSprite(xOffset + 12 + getBarOffsetX(type), 50 + yOffset);
                }
            }
        } else {
            if(type.icon == 9) {
                Client.spriteCache.get(type.getIcon()).drawSprite(xOffset + 13 + getBarOffsetX(type), 50 + yOffset);
            } else if(type.icon == 10) {
                Client.spriteCache.get(type.getIcon()).drawSprite(xOffset + 12 + getBarOffsetX(type), 50 + yOffset);
            }
        }

        Client.adv_font_small.draw_centered(type == BarType.HP ? hitpoints + "" : prayer + "", xOffset + 21 + getBarOffsetX(type), 80 + yOffset,0xFFFFFF, 1);
    }


    public int getBarOffsetX(BarType type) {
        if(type == BarType.HP) {
            return Client.screen == Client.ScreenMode.RESIZABLE ? - 5 : 0;
        } else if(type == BarType.PRAYER) {
            return Client.screen == Client.ScreenMode.RESIZABLE ? 210 + - 6 : 210;
        }
        return 0;
    }

    public static int getPercent(int current,int pixels) {
        return  (int) ((pixels) * .01 * current);
    }

    public int getPercent(BarType type, int extra) {
        if(type == BarType.HP) {
            int level = Client.singleton.currentLevels[3] + extra;
            int max = Client.singleton.maximumLevels[3];
            double percent = level / (double) max;
            return  level > 99 ? 100 : (int) (percent * 100);
        } else if(type == BarType.PRAYER) {
            int level = Client.singleton.currentLevels[5] + extra;
            int max = Client.singleton.maximumLevels[5];
            double percent = level / (double) max;
            return level > 99 ? 100 : (int) (percent * 100);
        }
        return 0;
    }

    public void setConsume(Restore restore) {
        this.restore = restore;
    }

}
