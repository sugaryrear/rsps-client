package com.ferox.net.requester;

import com.ferox.collection.Cacheable;

public final class Resource extends Cacheable {

    public int dataType; //The data index. dataType 3 would be index 4 which is maps.
    public byte buffer[];
    public int ID;
    boolean incomplete;
    int loopCycle;

    public Resource() {
        incomplete = true;
    }
}
