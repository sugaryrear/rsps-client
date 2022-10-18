package com.ferox.model.texture;

public class TextureCoordinate {
    private final int a, b, c;

    public TextureCoordinate(int a, int b, int c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public int getA() {
        return a;
    }

    public int getB() {
        return b;
    }

    public int getC() {
        return c;
    }
}
