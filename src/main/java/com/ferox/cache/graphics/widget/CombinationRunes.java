package com.ferox.cache.graphics.widget;

public enum CombinationRunes {
    MIST_RUNE(4695, new int[]{556, 555}), // Air rune, Water rune
    DUST_RUNE(4696, new int[]{556, 557}), // Air rune, Earth rune
    MUD_RUNE(4698, new int[]{555, 557}), // Water rune, Earth rune
    SMOKE_RUNE(4697, new int[]{556, 554}), // Air rune, Fire rune
    STEAM_RUNE(4694, new int[]{555, 554}), // Water rune, Fire rune
    LAVA_RUNE(4699, new int[]{557, 554}); // Earth rune, Fire rune

    private int runeItemId;

    private int[] combinedRunesId;

    private CombinationRunes(int runeItemId, int[] combinedRunesId) {
        this.runeItemId = runeItemId;
        this.combinedRunesId = combinedRunesId;
    }

    public int getRuneItemId() {
        return runeItemId;
    }

    public int[] getCombinationRunesId() {
        return combinedRunesId;
    }

    public static boolean isCombinationRune(int itemId) {
        for (int index = 0; index < CombinationRunes.values().length; index++) {
            if (CombinationRunes.values()[index].getRuneItemId() == itemId) {
                return true;
            }
        }
        return false;
    }

    public static int getTotalCombinationRunes(int runeId) {
        int total = 0;
        for (CombinationRunes combinationRunes : CombinationRunes.values()) {
            if (combinationRunes.getCombinationRunesId()[0] == runeId || combinationRunes.getCombinationRunesId()[1] == runeId) {
                total++;
            }
        }
        return total;
    }
}
