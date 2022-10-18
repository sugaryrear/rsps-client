package com.ferox.entity;

/**
 * ObjectGenre = 2
 */
public final class InteractiveObject {

    public int plane;
    public int world_z;
    public int world_x;
    public int world_y;
    public Renderable node;
    public int orientation;
    public int left;
    public int right;
    public int top;
    public int bottom;
    public int camera_distance;//TODO - zooming in and out scales the value, up/down do not
    public int rendered;
    public long uid;
    /**
     * mask = (byte)((objectRotation << 6) + objectType);
     */
    public byte mask;
}
