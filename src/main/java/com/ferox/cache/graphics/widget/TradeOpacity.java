package com.ferox.cache.graphics.widget;

import com.ferox.Client;

public class TradeOpacity {

    Widget widget;
    boolean finished = false;
    int timer = 0;
    int timer2 = 0;
    int slot = 0;
    int round = 0;
    private int state = 0;

    public boolean cycle() {
        if (Client.widget_overlay_id != 52000) {
            finished = true;
        }
        if (Client.game_tick % 30 > 0) return false;
        timer++;
        if (timer2 == 1) {
            timer2++;
        }
        if (timer == 1 && timer2 == 0) {
            widget.transparency = 0;
            timer2++;
        }
        if (timer2 == 2) {
            if (round < 2) {
                widget.transparency = 200;
            }
            if (round >= 2 && round < 5) {
                widget.transparency = 100;
            }
            if (round >= 5 && round < 8) {
                widget.transparency = 50;
            }
            if (round == 8) {
                finished = true;
            }
            timer = 0;
            timer2 = 0;
            round += 1;
        }
        if (finished) {
            widget.transparency = 0;
            if (state == 2) {
                Widget.cache[52014].drawingDisabled = true;
            } else if (state == 1) {
                Widget.cache[52013].drawingDisabled = true;
            }
            return true;
        }
        return false;
    }

    public TradeOpacity(Widget widget, int slot, int state) {
        this.widget = widget;
        this.slot = slot;
        this.state = state;
        widget.transparency = 200;
    }
}
