package com.ferox.util;

public final class ChunkUtil {

    public static int get_rotated_map_x(int rotation, int y, int x) {
        rotation &= 3;
        if (rotation == 0)
            return x;
        if (rotation == 1)
            return y;
        if (rotation == 2)
            return 7 - x;
        else
            return 7 - y;
    }

    public static int get_rotated_map_y(int y, int rotation, int x) {
        rotation &= 3;
        if (rotation == 0)
            return y;
        if (rotation == 1)
            return 7 - x;
        if (rotation == 2)
            return 7 - y;
        else
            return x;
    }

    public static int get_rotated_landscape_x(int rotation, int obj_height, int x, int y, int obj_width) {
        rotation &= 3;
        if (rotation == 0)
            return x;
        if (rotation == 1)
            return y;
        if (rotation == 2)
            return 7 - x - (obj_width - 1);
        else
            return 7 - y - (obj_height - 1);
    }

    public static int get_rotated_landscape_y(int y, int obj_height, int rotation, int obj_width, int x) {
        rotation &= 3;
        if (rotation == 0)
            return y;
        if (rotation == 1)
            return 7 - x - (obj_width - 1);
        if (rotation == 2)
            return 7 - y - (obj_height - 1);
        else
            return x;
    }

}
