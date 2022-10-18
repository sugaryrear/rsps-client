package com.ferox.entity.model;

import java.util.Arrays;

public class ModelColorUtil {
    public static void main(String[] args) {
        final int[] ORIGINAL_COLORS = {20416, 22464, 22305, 22181, 22449, 22451, 21435};
        int[] negativeColors = new int[ORIGINAL_COLORS.length];

        for(int i = 0; i < ORIGINAL_COLORS.length; i++) {
            negativeColors[i] = (short) ORIGINAL_COLORS[i];
        }

        System.out.println("Fixed colors: " + Arrays.toString(ORIGINAL_COLORS));
        System.out.println("Negative colors: " + Arrays.toString(negativeColors));
    }
}
