package com.ferox.cache.def.impl.npcs;

import com.ferox.cache.def.NpcDefinition;

import static com.ferox.util.CustomNpcIdentifiers.*;
import static com.ferox.util.CustomNpcIdentifiers.ZOMBIES_CHAMPION;
import static com.ferox.util.NpcIdentifiers.*;

public class CustomPets {

    private static final int[] pets = {
        SKELETON_HELLHOUND_PET,
        FAWKES_10981,
        LITTLE_NIGHTMARE_9399,
        ELYSIAN_PET,
        BLOOD_MONEY_PET,
        KERBEROS_PET,
        SKORPIOS_PET,
        ARACHNE_PET,
        ARTIO_PET,
        ANCIENT_KING_BLACK_DRAGON_PET,
        ANCIENT_CHAOS_ELEMENTAL_PET,
        ANCIENT_BARRELCHEST_PET,
        BLOOD_FIREBIRD,
        ZRIAWK,
        FENRIR_GREYBACK_JR,
        FLUFFY_JR,
        DEMENTOR_PET,
        CENTAUR_MALE_PET,
        CENTAUR_FEMALE_PET,
        JALNIBREK_7675,
        TZREKZUK_8011,
        FOUNDER_IMP,
        PET_CORRUPTED_NECHRYARCH,
        MINI_NECROMANCER,
        JALTOK_JAD,
        BABY_LAVA_DRAGON,
        JAWA,
        BABY_ARAGOG,
        WAMPA,
        FAWKES,
        BABY_SQUIRT,
        GRIM_REAPER,
        BABY_DARK_BEAST,
        BABY_ABYSSAL_DEMON,
        ZOMBIES_CHAMPION,
        BARRELCHEST_PET,
        NIFFLER,
        DHAROK_PET,
        GENIE_PET,
        PET_GENERAL_GRAARDOR_BLACK,
        PET_KRIL_TSUTSAROTH_BLACK,
        PET_ZILYANA_WHITE,
        PET_KREEARRA_WHITE,
        ABYSSAL_ORPHAN_5884,
        CALLISTO_CUB_5558,
        HELLPUPPY_3099,
        KALPHITE_PRINCESS_6638,
        KALPHITE_PRINCESS,
        KALPHITE_PRINCESS,
        KALPHITE_PRINCESS_6638,
        CHAOS_ELEMENTAL_JR,
        DAGANNOTH_PRIME_JR_6629,
        DAGANNOTH_REX_JR,
        DAGANNOTH_SUPREME_JR_6628,
        DARK_CORE,
        CORPOREAL_CRITTER_8010,
        GENERAL_GRAARDOR_JR,
        KRIL_TSUTSAROTH_JR,
        KREEARRA_JR,
        ZILYANA_JR,
        KRAKEN_6640,
        PENANCE_PET_6674,
        SMOKE_DEVIL_6639,
        SNAKELING_2130,
        SNAKELING_2128,
        SNAKELING_2131,
        SNAKELING_2129,
        SNAKELING_2132,
        SNAKELING_2127,
        PRINCE_BLACK_DRAGON,
        SCORPIAS_OFFSPRING_5561,
        TZREKJAD_5893,
        VENENATIS_SPIDERLING_5557,
        VETION_JR_5559,
        VETION_JR_5560,
        MIDNIGHT_7893,
        NOON_7892,
        SKOTOS_7671,
        VORKI_8029,
        OLMLET_7520,
        PUPPADILE_8201,
        TEKTINY_8202,
        VANGUARD_8203,
        VASA_MINIRIO_8204,
        VESPINA_8205,
        IKKLE_HYDRA,
        IKKLE_HYDRA_8493,
        IKKLE_HYDRA_8494,
        IKKLE_HYDRA_8495,
        BABY_CHINCHOMPA_6756,
        BABY_CHINCHOMPA_6757,
        BABY_CHINCHOMPA_6758,
        BABY_CHINCHOMPA_6759,
        BEAVER_6724,
        HERON_6722,
        ROCK_GOLEM_7439,
        ROCK_GOLEM_7440,
        ROCK_GOLEM_7441,
        ROCK_GOLEM_7442,
        ROCK_GOLEM_7443,
        ROCK_GOLEM_7444,
        ROCK_GOLEM_7445,
        ROCK_GOLEM_7737,
        ROCK_GOLEM_7446,
        ROCK_GOLEM_7448,
        ROCK_GOLEM_7447,
        ROCK_GOLEM_7736,
        ROCK_GOLEM_7449,
        ROCK_GOLEM_7450,
        ROCK_GOLEM_7711,
        GIANT_SQUIRREL_7351,
        TANGLEROOT_7352,
        ROCKY_7353,
        RIFT_GUARDIAN_7354,
        RIFT_GUARDIAN_7355,
        RIFT_GUARDIAN_7356,
        RIFT_GUARDIAN_7357,
        RIFT_GUARDIAN_7358,
        RIFT_GUARDIAN_7359,
        RIFT_GUARDIAN_7360,
        RIFT_GUARDIAN_7361,
        RIFT_GUARDIAN_7362,
        RIFT_GUARDIAN_7363,
        RIFT_GUARDIAN_7364,
        RIFT_GUARDIAN_7365,
        RIFT_GUARDIAN_7366,
        RIFT_GUARDIAN_7367,
        RIFT_GUARDIAN_8024,
        HERBI_7760,
        BLOODHOUND_7232,
        CHOMPY_CHICK_4002,
        YOUNGLLEF,
        CORRUPTED_YOUNGLLEF,
        BLOOD_REAPER
    };

    private static boolean isPet(int id) {
        for (int pet : pets) {
            if (pet == id) {
                return true;
            }
        }
        return false;
    }

    private static final int[] MORPH_PETS = new int[]{KALPHITE_PRINCESS, KALPHITE_PRINCESS_6638, DARK_CORE, CORPOREAL_CRITTER_8010, SNAKELING_2128, SNAKELING_2130, SNAKELING_2131, SNAKELING_2132, VETION_JR_5559,
        VETION_JR_5560, NOON_7892, MIDNIGHT_7893, IKKLE_HYDRA, IKKLE_HYDRA_8493, IKKLE_HYDRA_8494, IKKLE_HYDRA_8495, BABY_CHINCHOMPA_6757, BABY_CHINCHOMPA_6758, BABY_CHINCHOMPA_6758, BABY_CHINCHOMPA_6759};

    private static boolean morphPets(int id) {
        for (int pet : MORPH_PETS) {
            if (pet == id) {
                return true;
            }
        }
        return false;
    }

    public static void unpack(int id) {
        NpcDefinition definition = NpcDefinition.get(id);

        if (id == 9330) {
            definition.name = "Ancient king black dragon";
            definition.modelCustomColor4 = 235;
            definition.ambient = 30;
            definition.contrast = 30;
            definition.model_id = new int[]{17414, 17415, 17429, 17422, 17423};
            definition.additionalModels = new int[]{28865};
            definition.resizeY = 40;
            definition.resizeX = 40;
            definition.standingAnimation = 90;
            definition.walkingAnimation = 4635;
        }

        if (id == 9331) {
            definition.name = "Ancient chaos elemental";
            definition.modelCustomColor4 = 235;
            definition.model_id = new int[]{28256};
            definition.additionalModels = new int[]{5805};
            definition.standingAnimation = 3144;
            definition.walkingAnimation = 3145;
        }

        if (id == 9332) {
            definition.name = "Ancient barrelchest";
            definition.modelCustomColor4 = 235;
            definition.resizeY = 40;
            definition.resizeX = 40;
            definition.model_id = new int[]{22790};
            definition.standingAnimation = 5893;
            definition.walkingAnimation = 5892;
        }

        if (id == 9340) {
            definition.modelCustomColor4 = 33235;
        }

        if (id == 6635) {
            definition.modelCustomColor4 = 33785;
            definition.name = "Niffler";
        }

        if (id == 4927) {
            definition.name = "Fawkes";
        }

        if (id == 10981) {
            definition.name = "Fawkes";
            definition.ambient = 40;
            definition.model_id = new int[]{26853};
            definition.standingAnimation = 6809;
            definition.walkingAnimation = 6808;
            definition.modelCustomColor4 = 222200;
        }

        if (id == 7370) {
            definition.name = "Blood firebird";
            definition.src_color = new int[]{8, 10176, 10167, 5010, 4894, 914, 29867, 6084, 2880, 4011, 8150, 4399, 4391, 20, 5053, 5066, 4647, 23492, 23483, 6053, 5669, 6622, 1587, 28};
            definition.dst_color = new int[]{910, 933, 910, 933, 910, 910, 910, 933, 910, 933, 910, 933, 910, 910, 910, 933, 910, 933, 910, 933, 910, 910, 910, 933};
        }

        if (id == 1217) {
            definition.name = "Dharok the Wretched";
            definition.resizeX = 80;
            definition.resizeY = 80;
            definition.ambient = 50;
            definition.contrast = 50;
            definition.model_id = new int[]{6652, 6671, 6640, 6661, 6703, 6679};
            definition.standingAnimation = 2065;
            definition.walkingAnimation = 2064;
        }

        if (id == 1219) {
            definition.name = "Zombies champion";
            definition.resizeX = 63;
            definition.resizeY = 63;
            definition.model_id = new int[]{20949};
            definition.standingAnimation = 5573;
            definition.walkingAnimation = 5582;
            definition.halfTurnAnimation = 8634;
            definition.quarterClockwiseTurnAnimation = 8634;
            definition.quarterAnticlockwiseTurnAnimation = 8634;
        }

        if (id == 1220) {
            definition.name = "Wampa";
            definition.resizeX = 63;
            definition.resizeY = 63;
            definition.model_id = new int[]{21802, 21801, 21806};
            definition.standingAnimation = 5722;
            definition.walkingAnimation = 5721;
        }

        if (id == 1221) {
            definition.name = "Zilyana Jr.";
            definition.ambient = 30;
            definition.contrast = 30;
            definition.model_id = new int[]{27989, 27937, 27985, 27968, 27990};
            definition.additionalModels = new int[]{28864};
            definition.resizeX = 60;
            definition.resizeY = 60;
            definition.standingAnimation = 6966;
            definition.walkingAnimation = 6965;
            definition.halfTurnAnimation = 6965;
            definition.quarterClockwiseTurnAnimation = 6965;
            definition.quarterAnticlockwiseTurnAnimation = 6965;
            definition.modelCustomColor4 = 33785;
        }

        if (id == 1222) {
            definition.name = "General Graardor Jr.";
            definition.ambient = 30;
            definition.contrast = 30;
            definition.model_id = new int[]{27660, 27665};
            definition.additionalModels = new int[]{28860};
            definition.resizeX = 30;
            definition.resizeY = 30;
            definition.standingAnimation = 7017;
            definition.walkingAnimation = 7016;
            definition.halfTurnAnimation = 7016;
            definition.quarterClockwiseTurnAnimation = 7016;
            definition.quarterAnticlockwiseTurnAnimation = 7016;
            definition.modelCustomColor4 = 235;
        }

        if (id == 1223) {
            definition.name = "Kree'arra Jr.";
            definition.ambient = 30;
            definition.contrast = 30;
            definition.model_id = new int[]{28019, 28021, 28020};
            definition.additionalModels = new int[]{28859};
            definition.resizeX = 30;
            definition.resizeY = 30;
            definition.standingAnimation = 7166;
            definition.walkingAnimation = 7167;
            definition.halfTurnAnimation = 7166;
            definition.quarterClockwiseTurnAnimation = 7166;
            definition.quarterAnticlockwiseTurnAnimation = 7166;
            definition.modelCustomColor4 = 31575;
        }

        if (id == 1224) {
            definition.name = "K'ril Tsutsaroth Jr.";
            definition.ambient = 30;
            definition.contrast = 30;
            definition.model_id = new int[]{27683, 27681, 27692, 27682, 27690};
            definition.additionalModels = new int[]{28858};
            definition.resizeX = 20;
            definition.resizeY = 20;
            definition.standingAnimation = 6935;
            definition.walkingAnimation = 4070;
            definition.halfTurnAnimation = 4070;
            definition.quarterClockwiseTurnAnimation = 4070;
            definition.quarterAnticlockwiseTurnAnimation = 4070;
            definition.modelCustomColor4 = 235;
        }

        if (id == 1225) {
            definition.name = "Baby Squirt";
            definition.model_id = new int[]{25000, 25006, 25001, 25002, 25003, 25004, 25005};
            definition.standingAnimation = 6317;
            definition.walkingAnimation = 6317;
            definition.halfTurnAnimation = 6317;
            definition.quarterClockwiseTurnAnimation = 6317;
            definition.quarterAnticlockwiseTurnAnimation = 6317;
            definition.resizeY = 30;
            definition.resizeX = 30;
        }

        if (id == 1213) {
            definition.name = "Jawa";
            definition.model_id = new int[]{28285, 181, 249, 28286, 28226, 176, 28223};
            definition.modelCustomColor = 4769;
            definition.resizeY = 80;
            definition.resizeX = 80;
            definition.halfTurnAnimation = 820;
            definition.quarterClockwiseTurnAnimation = 822;
            definition.quarterAnticlockwiseTurnAnimation = 821;
            definition.standingAnimation = 808;
            definition.walkingAnimation = 819;
        }

        if (id == 1214) {
            definition.name = "Baby Aragog";
            definition.ambient = 30;
            definition.contrast = 30;
            definition.model_id = new int[]{28294, 28295};
            definition.additionalModels = new int[]{29186};
            definition.resizeX = 60;
            definition.resizeY = 60;
            definition.standingAnimation = 5326;
            definition.walkingAnimation = 5325;
            definition.halfTurnAnimation = NpcDefinition.getHalfTurnAnimation(VENENATIS_SPIDERLING_5557);
            definition.quarterAnticlockwiseTurnAnimation = NpcDefinition.getQuarterAnticlockwiseTurnAnimation(VENENATIS_SPIDERLING_5557);
            definition.quarterClockwiseTurnAnimation = NpcDefinition.getQuarterAnticlockwiseTurnAnimation(VENENATIS_SPIDERLING_5557);
            definition.src_color = new int[]{138, 908, 794, 912, 916, 0, 103, 107};
            definition.dst_color = new int[]{138, 908, 4769, 4769, 4769, 0, 0, 0};
        }

        if (id == 1182) {
            definition.name = "Baby Barrelchest";
            definition.resizeY = 40;
            definition.resizeX = 40;
            definition.model_id = new int[]{22790};
            definition.standingAnimation = 5893;
            definition.walkingAnimation = 5892;
        }

        if (id == 1228) {
            definition.name = "Grim Reaper";
            definition.resizeY = 90;
            definition.resizeX = 90;
            definition.src_color = new int[]{10004, 25238, 8741, 4550, 908, 7073};
            definition.dst_color = new int[]{5231, 0, 0, 5353, 0, 8084};
            definition.model_id = new int[]{5100, 292, 170, 179, 256, 507};
            definition.halfTurnAnimation = 820;
            definition.quarterClockwiseTurnAnimation = 822;
            definition.quarterAnticlockwiseTurnAnimation = 821;
            definition.standingAnimation = 847;
            definition.walkingAnimation = 819;
        }

        if (id == BLOOD_REAPER) {
            definition.name = "Blood Reaper";
            definition.resizeY = 90;
            definition.resizeX = 90;
            definition.src_color = new int[]{10004, 25238, 8741, 4550, 908, 7073};
            definition.dst_color = new int[]{5231, 0, 0, 5353, 0, 8084};
            definition.model_id = new int[]{5100, 292, 170, 179, 256, 507};
            definition.halfTurnAnimation = 820;
            definition.quarterClockwiseTurnAnimation = 822;
            definition.quarterAnticlockwiseTurnAnimation = 821;
            definition.standingAnimation = 847;
            definition.walkingAnimation = 819;
            definition.modelCustomColor4 = 964;
        }

        if (id == 1216) {
            definition.name = "Baby Dark Beast";
            definition.model_id = new int[]{26395};
            definition.standingAnimation = 2730;
            definition.walkingAnimation = 2729;
            definition.resizeY = 40;
            definition.resizeX = 40;
        }

        if (id == 1218) {
            definition.name = "Baby Abyssal Demon";
            definition.src_color = new int[] {4015};
            definition.dst_color = new int[] {528};
            definition.model_id = new int[] {5062};
            definition.standingAnimation = 1536;
            definition.walkingAnimation = 1534;
            definition.resizeY = 40;
            definition.resizeX = 40;
        }

        if (id == 6849) {
            definition.name = "Genie";
            definition.standingAnimation = 792;
            definition.walkingAnimation = 792;
            definition.halfTurnAnimation = 65535;
            definition.quarterClockwiseTurnAnimation = 65535;
            definition.quarterAnticlockwiseTurnAnimation = 65535;
            definition.resizeY = 128;
            definition.resizeX = 128;
            definition.model_id = new int[]{231, 241, 252, 315, 173, 176, 264, 270};
            definition.src_color = new int[]{4550, 6798, 926, 43072, 0, 25238};
            definition.dst_color = new int[]{39888, 40627, 43924, 13243, 957, 54177};
        }

        if (id == 15035) {
            definition.name = "Kerberos";
            definition.model_id = new int[]{29240};
            definition.additionalModels = new int[]{29392};
            definition.standingAnimation = 6561;
            definition.walkingAnimation = 6560;
            definition.modelCustomColor4 = 125;
        }

        if (id == 15036) {
            definition.name = "Skorpios";
            definition.ambient = 30;
            definition.src_color = new int[]{142, 4525, 4636, 4884, 4645};
            definition.dst_color = new int[]{28, 16, 16, 16, 16};
            definition.contrast = 30;
            definition.model_id = new int[]{29193};
            definition.additionalModels = new int[]{29185};
            definition.resizeX = 280;
            definition.resizeY = 280;
            definition.standingAnimation = 6258;
            definition.walkingAnimation = 6257;
            definition.modelCustomColor4 = 125;
        }

        if (id == 15037) {
            definition.name = "Arachne";
            definition.ambient = 30;
            definition.contrast = 30;
            definition.model_id = new int[]{28294, 28295};
            definition.additionalModels = new int[]{29186};
            definition.resizeX = 60;
            definition.resizeY = 60;
            definition.standingAnimation = 5326;
            definition.walkingAnimation = 5325;
            definition.modelCustomColor4 = 125;
        }

        if (id == 15038) {
            definition.name = "Artio";
            definition.ambient = 30;
            definition.contrast = 30;
            definition.model_id = new int[]{28298};
            definition.additionalModels = new int[]{29187};
            definition.resizeX = 35;
            definition.resizeY = 35;
            definition.standingAnimation = 4919;
            definition.walkingAnimation = 4923;
            definition.modelCustomColor4 = 115;
        }

        if (id == 7315) {
            definition.name = "Blood money";
            definition.src_color = new int[1];
            definition.src_color[0] = 8128;
            definition.dst_color = new int[1];
            definition.dst_color[0] = 940;
        }

        if (id == 336) {
            definition.name = "Elysian";
        }

        if (id == 15040) {
            definition.name = "Centaur";
            definition.model_id = new int[]{16196, 16202, 16199, 16200};
            definition.additionalModels = new int[]{16213};
            definition.resizeX = 78;
            definition.resizeY = 78;
            definition.standingAnimation = 4311;
            definition.walkingAnimation = 4310;
        }

        if (id == 15042) {
            definition.name = "Centaur";
            definition.model_id = new int[]{16195, 16201, 16198, 16197, 16200};
            definition.additionalModels = new int[]{16212, 16211};
            definition.resizeX = 78;
            definition.resizeY = 78;
            definition.standingAnimation = 4311;
            definition.walkingAnimation = 4310;
        }

        if (id == 15044) {
            definition.name = "Dementor";
            definition.ambient = 20;
            definition.src_color = new int[]{10343, -22250, -22365, -22361, -22353, -22464, -22477, -22456, -22473, -22452};
            definition.dst_color = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            definition.contrast = 20;
            definition.model_id = new int[]{21154};
            definition.additionalModels = new int[]{21394};
            definition.resizeX = 78;
            definition.resizeY = 78;
            definition.standingAnimation = 5538;
            definition.walkingAnimation = 5539;
        }

        if (id == 15017) {
            definition.name = "Baby Lava Dragon";
            definition.model_id = new int[]{58995, 58995, 58994, 58996};
            definition.resizeX = 40;
            definition.resizeY = 40;
            definition.standingAnimation = 7870;
            definition.walkingAnimation = 7870;
        }

        if (id == 15005) {
            definition.name = "Mini Necromancer";
            definition.src_color = new int[]{-26527, -24618, -26073, 5018, 61, 10351, 33, 24};
            definition.dst_color = new int[]{-19054, 12, 12, -16870, 11177, 61, 16, 12};
            definition.model_id = new int[]{4953, 4955, 556, 58948, 58907, 58950, 58953, 58956};
            definition.resizeX = 90;
            definition.resizeY = 90;
            definition.standingAnimation = 808;
            definition.walkingAnimation = 819;
        }

        if (id == 15002) {
            definition.name = "Corrupted nechryarch";
            definition.model_id = new int[]{58922};
            definition.resizeX = 35;
            definition.resizeY = 35;
            definition.standingAnimation = 4650;
            definition.walkingAnimation = 6372;
        }

        if (id == 15000) {
            definition.name = "Founder Imp";
            definition.ambient = 30;
            definition.src_color = new int[]{10306, 10297, -25326, 7461, 7469};
            definition.dst_color = new int[]{10549, 10421, -24698, 7952, 7704};
            definition.model_id = new int[]{58916};
            definition.resizeX = 132;
            definition.resizeY = 132;
            definition.standingAnimation = 171;
            definition.walkingAnimation = 168;
        }

        if (id == 15008) {
            definition.name = "JalTok-Jad";
            definition.model_id = new int[]{33012};
            definition.resizeX = 20;
            definition.resizeY = 20;
            definition.standingAnimation = 7589;
            definition.walkingAnimation = 7588;
        }

        if (id == 9338) {
            definition.name = "Fluffy Jr";
            definition.src_color = new int[]{929, 960, 1981, 0, 931, 4029, 926, 902, 922, 918, 924, 904, 916, 912, 935, 939, 906, 920, 955, 910, 914, 7101, 11200, 957, 9149, 908, 4, 5053, 8125, 6069};
            definition.dst_color = new int[]{4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 4769, 0, 4769, 0, 4769, 0, 0, 4769, 4769};
            definition.model_id = new int[]{29270};
            definition.resizeX = 28;
            definition.resizeY = 28;
            definition.standingAnimation = 4484;
            definition.walkingAnimation = 4488;
        }

        if (id == 9339) {
            definition.name = "Fenrir Greyback Jr";
            definition.model_id = new int[]{26177, 26188, 26181};
            definition.additionalModels = new int[]{26113};
            definition.resizeX = 78;
            definition.resizeY = 78;
            definition.standingAnimation = 6539;
            definition.walkingAnimation = 6541;
        }

        if (id == 9340) {
            definition.name = "Zriawk";
            definition.model_id = new int[]{6458};
            definition.standingAnimation = 2017;
            definition.walkingAnimation = 2016;
        }

        if(id == 12022) {
            definition.model_id = new int[]{26262};
            definition.name = "Skeleton hellhound pet";
            definition.standingAnimation = 6580;
            definition.walkingAnimation = (definition.halfTurnAnimation = definition.quarterAnticlockwiseTurnAnimation = definition.quarterClockwiseTurnAnimation = 6577);
            definition.resizeX = 85;
            definition.resizeY = 85;
        }

        //Default definitions for pets
        if (isPet(id)) {
            definition.name = definition.name + " pet";
            if (morphPets(id)) {
                definition.actions = new String[]{"Pick-up", null, "Metamorphosis", null, null};
            } else if (id == NIFFLER) {
                definition.actions = new String[]{"Pick-up", null, "Tickle", null, null};
            } else {
                definition.actions = new String[]{"Pick-up", null, null, null, null};
            }
            definition.pet = true;
            definition.renderOnMinimap = false;
            definition.occupied_tiles = 1;
            definition.cmb_level = 0;
            definition.description = "Tiny but deadly!";
        }
    }
}
