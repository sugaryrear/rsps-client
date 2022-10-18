package com.ferox;

public class IncomingHit {
    public int damage;
    public int pos;

    public IncomingHit(int damage) {
        this.damage = damage;
        this.pos = 0;
    }

    public void incrementPos(int amount) {
        this.pos += amount;
    }
}
