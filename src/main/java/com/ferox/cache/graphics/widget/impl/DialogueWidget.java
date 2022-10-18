package com.ferox.cache.graphics.widget.impl;

import com.ferox.cache.graphics.widget.Widget;

public class DialogueWidget extends Widget {

    public static void unpack() {
        dialogueInterface();
    }

    private static void dialogueInterface() {

        Widget main = Widget.cache[6231];


        main.copyAndAddChildren(2);

        addToItemGroup(37850, 1, 1, 55, 55, false, false, null);
        addToItemGroup(37851, 1, 1, 55, 95, false, false, null);

        main.child(5, 37850, 35, 15);
        main.child(6, 37851, 33, 37);


    }

}
