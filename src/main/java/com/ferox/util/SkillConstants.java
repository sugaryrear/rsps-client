package com.ferox.util;

public final class SkillConstants {

    public static final int SKILL_COUNT = 25;

    public static final String[] SKILL_NAMES = { "Attack", "Hitpoints",
            "Mining", "Strength", "Agility", "Smithing", "Defence", "Herblore",
            "Fishing", "Range", "Thieving", "Cooking", "Prayer", "Crafting",
            "Firemaking", "Magic", "Fletching", "Woodcutting", "Runecrafting",
            "Slayer", "Farming", "Hunter", "Construction", "-unused" };

    public static final String[] SKILL_NAMES_ORDER = { "Attack", "Defence", "Strength", "Hitpoints", "Ranged",
                                                        "Prayer", "Magic", "Cooking","Woodcutting","Fletching",
                                                        "Fishing","Firemaking","Crafting", "Smithing","Mining",
                                                        "Herblore","Agility","Theving","Slayer","Farming","Runecrafting",
                                                        "Hunter","Construction"};

    public static String[] SKILL_NAMES_SKILLSTAB = {
            "-1", "Attack", "Strength", "Defence", "Ranged",
            "Prayer", "Magic", "Runecrafting", "Construction", "Hitpoints", "Agility",
            "Herblore", "Thieving", "Crafting", "Fletching", "Slayer", "Hunter", "-1",
            "Mining", "Smithing", "Fishing", "Cooking", "Firemaking", "Woodcutting",
            "Farming"
        };

    public static final boolean[] ENABLED_SKILLS = {
            true, true, true, true, true, true, true, true, true, true,
            true, true, true, true, true, true, true, true, true, true,
            true, true, false, false, false };

}
