package com.ferox.util;

public class ImageDescription {

    private int spriteID;
    private String description;
    private int imageIndex;
    private int categoryIndex;

    public ImageDescription(int spriteID, String description, int categoryIndex, int imageIndex) {
        this.spriteID = spriteID;
        this.description = description;
        this.categoryIndex = categoryIndex;
        this.imageIndex = imageIndex;
    }

    public int getSpriteID() {
        return spriteID;
    }

    public String getDescription() {
        return description;
    }

    public int getCategoryIndex() {
        return categoryIndex;
    }

    public int getImageIndex() {
        return imageIndex;
    }
}
