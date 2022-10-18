package com.ferox.model;

import com.ferox.util.SecondsTimer;

public class EffectTimer {

    private final int sprite;
    private final SecondsTimer secondsTimer;

    public EffectTimer(int seconds, int sprite) {
        this.secondsTimer = new SecondsTimer(seconds);
        this.sprite = sprite;
    }

    public int getSprite() {
        return sprite;
    }

    public void setSeconds(int seconds) {
        this.secondsTimer.start(seconds);
    }

    public SecondsTimer getSecondsTimer() {
        return secondsTimer;
    }
}
