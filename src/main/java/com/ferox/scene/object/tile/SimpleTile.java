package com.ferox.scene.object.tile;

public final class SimpleTile {

    public SimpleTile(int hsl_a, int hsl_b, int hsl_c, int hsl_d, int texture_id, int color_id, boolean flat)
    {
        this.flat = true;
        this.shadow_a = hsl_a;
        this.shadow_b = hsl_b;
        this.shadow_c = hsl_c;
        this.shadow_d = hsl_d;
        this.texture_id = texture_id;
        this.color_id = color_id;
        this.flat = flat;
    }

    public final int shadow_a;
    public final int shadow_b;
    public final int shadow_c;
    public final int shadow_d;
    public final int texture_id;
    public boolean flat;
    public final int color_id;
}
