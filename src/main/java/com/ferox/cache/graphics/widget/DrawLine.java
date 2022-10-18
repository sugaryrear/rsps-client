package com.ferox.cache.graphics.widget;

public class DrawLine extends Widget {

    private final LineType lineType;

    public DrawLine(final int childId, final int length, int colour, int alpha, LineType type) {
        this.id = childId;
        this.type = Widget.DRAW_LINE;
        this.textColour = colour;
        this.optionType = 0;
        this.opacity2 = alpha;
        this.contentType = 0;
        this.width = length;
        this.lineType = type;
        Widget.cache[childId] = this;
    }

    public LineType getLineType() {
        return lineType;
    }

    public enum LineType {

        HORIZONTAL,
        VERTICAL;

    }

}
