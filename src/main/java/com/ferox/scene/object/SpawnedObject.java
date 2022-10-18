package com.ferox.scene.object;
import com.ferox.collection.Node;

public final class SpawnedObject extends Node {

    public SpawnedObject()
    {
        getLongetivity = -1;
    }

    public int id;
    public int orientation;
    public int type;
    public int getLongetivity;
    public int plane;
    public int group;
    public int x;
    public int y;
    public int getPreviousId;
    public int previousOrientation;
    public int previousType;
    public int delay;
}
