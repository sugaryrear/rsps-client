package com.ferox.scene.object.tile;
import com.ferox.collection.Node;
import com.ferox.entity.GroundItem;
import com.ferox.scene.object.GroundDecoration;
import com.ferox.scene.object.Wall;
import com.ferox.scene.object.WallDecoration;
import com.ferox.entity.InteractiveObject;

public final class Tile extends Node {
    public Tile(int i, int j, int k) {
        interactive_obj = new InteractiveObject[5];
        interactive_obj_size = new int[5];
        plane = z = i;
        x = j;
        y = k;
    }

    public int z;
    public final int x;
    public final int y;
    public final int plane;
    public SimpleTile simple;
    public ComplexTile complex;
    public Wall wall;
    public WallDecoration wall_decor;
    public GroundDecoration ground_decor;
    public GroundItem ground_item;
    public int occupants;
    public final InteractiveObject[] interactive_obj;
    public final int[] interactive_obj_size;
    public int origin_mask;
    public int logic_height;
    public boolean updated;
    public boolean drawn;
    public boolean multiple_objects;
    public int render_mask;
    public int viewport_angle;
    public int culled_face_mask;
    public int depth;
    public Tile sub_tile;
}
