package com.ferox.cache.def.impl;

import com.ferox.cache.def.ObjectDefinition;

import static com.ferox.util.ObjectIdentifiers.RINSING_POOL;

public class ObjectManager {

    public static void get(int id) {
        ObjectDefinition definition = ObjectDefinition.get(id);

        if (id == 50004) {
            definition.name = "Dark rejuvenation pool";
            definition.scene_actions = new String[]{"Drink", null, null, null, null};
            definition.ambient = 40;
            definition.animation = 7304;
            definition.solid = true;
            definition.interact_state = 1;
            definition.height = 2;
            definition.model_ids = new int[]{58959};
            definition.gouraud_shading = true;
            definition.merge_interact_state = 1;
            definition.width = 2;
        }

        if (id == 31621) {
            definition.name = "50s";
        }

        if (id == 31622) {
            definition.name = "Member cave";
        }

        if (id == 31618) {
            definition.name = "gdz";
        }

        if(id == 2515) {
            definition.scene_actions = new String[] {"Travel", null, null, null, null};
        }
        if(id == 9334) {
            definition.scene_actions = new String[] {"Leaderboard", null, null, null, null};
        }
        if(id == 10060 || id == 7127 || id == 31626 || id == 4652 || id == 4653) {
            definition.scene_actions = new String[] {null, null, null, null, null};
        }

        if(id == 562 || id == 3192) {
            definition.scene_actions = new String[] {"Live scoreboard", "Todays top pkers", null, null, null};
        }

        if(id == 6552) {
            definition.scene_actions = new String[] {"Change spellbook", null, null, null, null};
        }

        if (id == 29165) {
            definition.name = "Pile Of Coins";
            definition.scene_actions[0] = null;
            definition.scene_actions[1] = null;
            definition.scene_actions[2] = null;
            definition.scene_actions[3] = null;
            definition.scene_actions[4] = null;
        }

        if(id == 33020) {
            definition.name = "Forging table";
            definition.scene_actions = new String[] {"Forge", null, null, null, null};
        }

        if(id == 8878) {
            definition.name = "Item dispenser";
            definition.scene_actions = new String[] {"Dispense", "Exchange coins", null, null, null};
        }

        if(id == 637) {
            definition.name = "Item cart";
            definition.scene_actions = new String[] {"Check cart", "Item list", "Clear cart", null, null};
        }

        if (id == 13291) {
            definition.scene_actions = new String[] {"Open", null, null, null, null};
        }

        if (id == 23709) {
            definition.scene_actions[0] = "Use";
        }

        if (id == 2156) {
            definition.name = "World Boss Portal";
        }

        if (id == 27780) {
            definition.name = "Scoreboard";
        }

        if (id == 14986) {
            definition.name = "Key Chest";

            ObjectDefinition deadmanChest = ObjectDefinition.get(27269);

            definition.model_ids = deadmanChest.model_ids;
            definition.src_color = deadmanChest.src_color;
            definition.scene_actions = deadmanChest.scene_actions;
            definition.dst_color = deadmanChest.dst_color;
        }

        if (id == 7811) {
            definition.name = "Supplies";
            definition.scene_actions[0] = "Blood money supplies";
            definition.scene_actions[1] = "Vote-rewards";
            definition.scene_actions[2] = "Donator-store";
        }

        if(id == 2654) {
            definition.name = "Blood fountain";
            definition.width = 3;
            definition.height = 3;
            definition.scene_actions[0] = "Rewards";
            definition.scene_actions[1] = null;
            definition.src_color = new int[]{10266, 10270, 10279, 10275, 10283, 33325, 33222};
            definition.dst_color = new int[]{10266, 10270, 10279, 10275, 10283, 926, 926};
        }
    }
}
