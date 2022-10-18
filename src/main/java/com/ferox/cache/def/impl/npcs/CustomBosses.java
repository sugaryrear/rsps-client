package com.ferox.cache.def.impl.npcs;

import com.ferox.cache.def.NpcDefinition;
import com.ferox.util.NpcIdentifiers;

/**
 * @author Patrick van Elderen | April, 07, 2021, 15) {49
 * @see <a href="https) {//www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class CustomBosses {

    public static void unpack(int id) {
        NpcDefinition definition = NpcDefinition.get(id);

        if (id == 15021) {
            definition.name = "Grim";
            definition.actions = new String[]{null, "Attack", null, null, null};
            definition.cmb_level = 1322;
            definition.src_color = new int[] {10004,25238,8741,4550,908,7073};
            definition.dst_color = new int[] {5231,0,0,5353,0,8084};
            definition.model_id = new int[] {5100,292,170,179,256,507};
            definition.renderOnMinimap = true;
            definition.halfTurnAnimation = 820;
            definition.quarterClockwiseTurnAnimation = 822;
            definition.quarterAnticlockwiseTurnAnimation = 821;
            definition.standingAnimation = 847;
            definition.occupied_tiles = 3;
            definition.walkingAnimation = 819;
            definition.resizeX = 250;
            definition.resizeY = 250;
        }

        if (id == 15016) {
            definition.name = "Brutal Lava Dragon";
            definition.actions = new String[]{null, "Attack", null, null, null};
            definition.cmb_level = 420;
            definition.model_id = new int[]{58995, 58995, 58994, 58996};
            definition.resizeX = 170;
            definition.resizeY = 170;
            definition.standingAnimation = 7870;
            definition.occupied_tiles = 7;
            definition.walkingAnimation = 7870;
        }

        if (id == 15019) {
            definition.name = "Brutal Lava Dragon";
            definition.actions = new String[]{null, "Attack", null, null, null};
            definition.cmb_level = 420;
            definition.model_id = new int[]{58995, 58995, 58994, 58996};
            definition.resizeX = 170;
            definition.resizeY = 170;
            definition.standingAnimation = 90;
            definition.occupied_tiles = 7;
            definition.walkingAnimation = 79;
        }

        if (id == 15001) {
            definition.name = "Corrupted Nechryarch";
            definition.actions = new String[]{null, "Attack", null, null, null};
            definition.cmb_level = 300;
            definition.model_id = new int[]{58922};
            definition.standingAnimation = 4650;
            definition.occupied_tiles = 2;
            definition.walkingAnimation = 6372;
        }

        if (id == 15003) {
            definition.name = "Necromancer";
            definition.actions = new String[]{null, "Attack", null, null, null};
            definition.src_color = new int[]{-26527, -24618, -26073, 5018, 61, 10351, 33, 24};
            definition.dst_color = new int[]{-19054, 12, 12, -16870, 11177, 61, 16, 12};
            definition.cmb_level = 300;
            definition.model_id = new int[]{4953, 4955, 556, 58948, 58907, 58950, 58953, 58956};
            definition.resizeX = 160;
            definition.resizeY = 160;
            definition.standingAnimation = 808;
            definition.occupied_tiles = 2;
            definition.walkingAnimation = 819;
        }

        if (id == 15020) {
            definition.name = "Aragog";
            definition.actions = new String[]{null, "Attack", null, null, null};
            definition.src_color = new int[]{138, 908, 794, 912, 916, 0, 103, 107};
            definition.dst_color = new int[]{138, 908, 4769, 4769, 4769, 0, 0, 0};
            definition.cmb_level = 1123;
            definition.model_id = new int[]{28294, 28295};
            definition.resizeX = 190;
            definition.resizeY = 190;
            definition.standingAnimation = 5318;
            definition.occupied_tiles = 4;
            definition.walkingAnimation = 5317;
        }

        if (id == 15026) {
            definition.name = "Fluffy";
            definition.actions = new String[]{null, "Attack", null, null, null};
            definition.src_color = new int[]{929, 960, 1981, 0, 931, 4029, 926, 902, 922, 918, 924, 904, 916, 912, 935, 939, 906, 920, 955, 910, 914, 7101, 11200, 957, 9149, 908, 4, 5053, 8125, 6069};
            definition.dst_color = new int[]{4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 0, 4769, 0, 4769, 0, 0, 4769, 4769};
            definition.cmb_level = 636;
            definition.model_id = new int[]{29270};
            definition.standingAnimation = 4484;
            definition.occupied_tiles = 5;
            definition.walkingAnimation = 4488;
        }

        if (id == 15028) {
            definition.name = "Dementor";
            definition.actions = new String[]{null, "Attack", null, null, null};
            definition.ambient = 20;
            definition.src_color = new int[]{10343, -22250, -22365, -22361, -22353, -22464, -22477, -22456, -22473, -22452};
            definition.dst_color = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            definition.cmb_level = 126;
            definition.contrast = 20;
            definition.model_id = new int[]{21154};
            definition.additionalModels = new int[]{21394};
            definition.standingAnimation = 5538;
            definition.walkingAnimation = 5539;
        }

        if (id == 15030) {
            definition.name = "Centaur";
            definition.actions = new String[]{null, "Attack", null, null, null};
            definition.cmb_level = 126;
            definition.model_id = new int[]{16196, 16202, 16199, 16200};
            definition.additionalModels = new int[]{16213};
            definition.standingAnimation = 4311;
            definition.occupied_tiles = 2;
            definition.walkingAnimation = 4310;
        }

        if (id == 15032) {
            definition.name = "Centaur";
            definition.actions = new String[]{null, "Attack", null, null, null};
            definition.cmb_level = 126;
            definition.model_id = new int[]{16195, 16201, 16198, 16197, 16200};
            definition.additionalModels = new int[]{16212, 16211};
            definition.standingAnimation = 4311;
            definition.occupied_tiles = 2;
            definition.walkingAnimation = 4310;
        }

        if (id == 15034) {
            definition.name = "Hungarian horntail";
            definition.actions = new String[]{null, "Attack", null, null, null};
            definition.ambient = 30;
            definition.src_color = new int[]{0, 30635, 29390, 29526, 31271, 31393, 31151, 32200, 31192, 127};
            definition.dst_color = new int[]{5662, 127, 5662, 5662, 5662, 5662, 5662, 5662, 127, 5662};
            definition.cmb_level = 172;
            definition.model_id = new int[]{38610};
            definition.resizeX = 110;
            definition.resizeY = 110;
            definition.standingAnimation = 90;
            definition.occupied_tiles = 4;
            definition.walkingAnimation = 79;
        }

        if (id == 15050) {
            definition.name = "Fenrir greyback";
            definition.actions = new String[]{null, "Attack", null, null, null};
            definition.cmb_level = 655;
            definition.model_id = new int[]{26177, 26188, 26181};
            definition.additionalModels = new int[]{26113};
            definition.standingAnimation = 6539;
            definition.walkingAnimation = 6541;
        }

        if (id == 16008) {
            NpcDefinition.copy(definition, NpcIdentifiers.CERBERUS);
            definition.name = "Kerberos";
            definition.modelCustomColor4 = 125;
        }

        if (id == 16009) {
            NpcDefinition.copy(definition, NpcIdentifiers.SCORPIA);
            definition.name = "Skorpios";
            definition.modelCustomColor4 = 125;
        }

        if (id == 16010) {
            NpcDefinition.copy(definition, NpcIdentifiers.VENENATIS);
            definition.name = "Arachne";
            definition.modelCustomColor4 = 125;
        }

        if (id == 16011) {
            NpcDefinition.copy(definition, NpcIdentifiers.CALLISTO);
            definition.name = "Artio";
            definition.modelCustomColor4 = 115;
        }
    }
}
