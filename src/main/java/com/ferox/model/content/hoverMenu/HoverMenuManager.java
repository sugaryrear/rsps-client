package com.ferox.model.content.hoverMenu;

import com.ferox.Client;
import com.ferox.ClientConstants;
import com.ferox.cache.factory.ItemSpriteFactory;
import com.ferox.cache.graphics.SimpleImage;
import com.ferox.draw.Rasterizer2D;
import com.ferox.util.CustomItemIdentifiers;
import com.ferox.util.ItemIdentifiers;

import java.util.Arrays;
import java.util.HashMap;

import static com.ferox.util.CustomItemIdentifiers.*;
import static com.ferox.util.CustomItemIdentifiers.WAMPA;
import static com.ferox.util.ItemIdentifiers.*;

public class HoverMenuManager {

    public static final int TEXT_COLOUR = 0xFFFFFFF;

    public static HashMap<Integer, HoverMenu> menus = new HashMap<>();

    public static void init() {
        menus.put(GAMBLER_SCROLL, new HoverMenu("Claiming this item makes you gambler! As a gambler, you can host games and play flower poker at @yel@::gambling!"));
        menus.put(BLOOD_MONEY_PET, new HoverMenu("A cosmetic blood money pet, whoever owns this pet is very rich."));
        menus.put(TOXIC_STAFF_OF_THE_DEAD_C, new HoverMenu("A much stronger version of the toxic staff of the dead."));
        menus.put(RING_OF_ELYSIAN, new HoverMenu("When dropping this ring it becomes an elysian pet. When wearing this ring, you turn into either a elysian sigil or shield."));
        menus.put(CORRUPTED_BOOTS, new HoverMenu("The best in slots boots, combined effects of all fused boots."));
        menus.put(BLOOD_FIREBIRD, new HoverMenu("It has a @red@20%@whi@ chance of passively healing the player @red@15%@whi@ of the damage you deal. And gives @red@+500@whi@ blood money when killing players."));
        menus.put(VIGGORAS_CHAINMACE_C, new HoverMenu("Gives the same boosts as a regular Viggora's chainmace. This boost also works outside of the wilderness."));
        menus.put(CRAWS_BOW_C, new HoverMenu("Gives the same boosts as a regular Craw's bow. This boost also works outside of the wilderness."));
        menus.put(THAMMARONS_STAFF_C, new HoverMenu("Gives the same boosts as a regular Thammaron's sceptre. This boost also works outside of the wilderness."));
        menus.put(HOLY_SANGUINESTI_STAFF, new HoverMenu("@red@+10@whi@ extra damage, no accuracy boost."));
        menus.put(HOLY_GHRAZI_RAPIER, new HoverMenu("A much more powerful version of the ghrazi rapier."));
        menus.put(SANGUINE_SCYTHE_OF_VITUR, new HoverMenu("A stronger version then the Holy scythe of Vitur and Always hits 3 targets when fighting monsters."));
        menus.put(LVL_LAMP, new HoverMenu("Ability to make a skill of choice level 99."));
        menus.put(SWORD_OF_GRYFFINDOR, new HoverMenu("@red@+25%@whi@ damage and accuracy boost against any monster."));
        menus.put(ARMADYL_GODSWORD_OR, new HoverMenu("Increases your max hit by @red@+1@whi@ and accuracy by @red@+25%@whi@."));
        menus.put(BANDOS_GODSWORD_OR, new HoverMenu("Increases your max hit by @red@+1@whi@."));
        menus.put(SARADOMIN_GODSWORD_OR, new HoverMenu("Increases your max hit by @red@+1@whi@."));
        menus.put(ZAMORAK_ARMOUR_SET_SK, new HoverMenu("Increases your max hit by @red@+1@whi@."));
        menus.put(AMULET_OF_FURY_OR, new HoverMenu("Increases your max hit by @red@+1@whi@."));
        menus.put(OCCULT_NECKLACE_OR, new HoverMenu("Increases your max hit by @red@+1@whi@."));
        menus.put(AMULET_OF_TORTURE_OR, new HoverMenu("Increases your max hit by @red@+1@whi@."));
        menus.put(NECKLACE_OF_ANGUISH_OR, new HoverMenu("Increases your max hit by @red@+1@whi@."));
        menus.put(TORMENTED_BRACELET_OR, new HoverMenu("Increases your max hit by @red@+1@whi@."));
        menus.put(GRANITE_MAUL_12848, new HoverMenu("Increases your max hit by @red@+1@whi@."));
        menus.put(DRAGON_CLAWS_OR, new HoverMenu("Increases your max hit by @red@+1, and a accuracy boost of @red@+50%@whi@."));
        menus.put(RING_OF_MANHUNTING, new HoverMenu("Has both the effects of the @red@Berserker Ring@whi@ and @red@Ring Of Suffering@whi@."));
        menus.put(RING_OF_SORCERY, new HoverMenu("Has both the effects of the @red@Seer Ring (i)@whi@ and @red@Ring Of Recoil@whi@."));
        menus.put(RING_OF_PRECISION, new HoverMenu("Has both the effects of the Archers Ring (i)@whi@ and @red@Ring Of Recoil@whi@ And @red@+5@whi@ Ranged Strength."));
        menus.put(RING_OF_TRINITY, new HoverMenu("The effect of all forged rings combined into one."));
        menus.put(SLAYER_HELMET_I, new HoverMenu("Whilst wearing you receive a @red@5%@whi@ extra PKP boost for killing players."));
        menus.put(GREEN_SLAYER_HELMET_I, new HoverMenu("Whilst wearing you receive a @red@10%@whi@ Ranged Strength boost."));
        menus.put(TURQUOISE_SLAYER_HELMET_I, new HoverMenu("Whilst wearing you receive a @red@10%@whi@ Magic Strength boost."));
        menus.put(RED_SLAYER_HELMET_I, new HoverMenu("Whilst wearing you receive a @red@10%@whi@ Melee Strength boost."));
        menus.put(BLACK_SLAYER_HELMET_I, new HoverMenu("Whilst wearing you receive a @red@10%@whi@ extra PKP boost for killing players."));
        menus.put(TWISTED_SLAYER_HELMET_I, new HoverMenu("Combined effect of all imbued slayer helmets into one."));
        menus.put(TWISTED_SLAYER_HELMET_I_KBD_HEADS, new HoverMenu("Combined effect of all imbued slayer helmets into one."));
        menus.put(PURPLE_SLAYER_HELMET_I, new HoverMenu("Combined effect of all imbued slayer helmets into one."));
        menus.put(HYDRA_SLAYER_HELMET_I, new HoverMenu("Combined effect of all imbued slayer helmets into one."));
        menus.put(ELDER_MAUL_21205, new HoverMenu("A recolored version of the Elder maul! +1 max hit and better accuracy <col=65535>Untradeable and auto-keep."));
        menus.put(MAGMA_BLOWPIPE, new HoverMenu("A recolored version of the Toxic blowpipe! +3 max hit and better accuracy <col=65535>Untradeable and auto-keep."));
        menus.put(VENGEANCE_SKULL, new HoverMenu("With this item in your inventory, you won't need the required runes for vengeance! <col=65535>Untradeable and auto-keep."));
        menus.put(KEY_OF_DROPS, new HoverMenu("When using the Key of drops on a mystery box you will be guaranteed a rare drop. And 20% chance of a extreme rare drop."));
        menus.put(GRAND_MYSTERY_BOX, new HoverMenu("Gives random mystery boxes in return."));
        menus.put(ANCESTRAL_HAT_I, new HoverMenu("+@red@10%@whi@ extra accuracy and +@red@5@whi@ Magic damage."));
        menus.put(ANCESTRAL_ROBE_TOP_I, new HoverMenu("+@red@10%@whi@ extra accuracy and +@red@5@whi@ Magic damage."));
        menus.put(ANCESTRAL_ROBE_BOTTOM_I, new HoverMenu("+@red@10%@whi@ extra accuracy and +@red@5@whi@ Magic damage."));
        menus.put(PRIMORDIAL_BOOTS_OR, new HoverMenu("+@red@10%@whi@ extra accuracy and +@red@8@whi@ strength bonus."));

        menus.put(SKELETON_HELLHOUND_PET, new HoverMenu("+@red@7.5%@whi@ drop rate, +@red@5%@whi@ damage and +@red@5%@whi@ accuracy."));
        menus.put(JALNIBREK, new HoverMenu("Acts as a ring of recoil and +@red@10%@whi@ special attack accuracy."));
        menus.put(BLOOD_REAPER, new HoverMenu("Keep one extra item on death. This doesn't work while @red@ smited or red skulled@whi@. And @red@+10%@whi@ extra blood money during any activity including chests."));
        menus.put(YOUNGLLEF, new HoverMenu("@red@+1@whi@ max hit and @red@7.5%@whi@ accuracy for all attack styles."));
        menus.put(CORRUPTED_YOUNGLLEF, new HoverMenu("@red@+2@whi@ max hit and @red@12.5%@whi@ accuracy for all attack styles."));
        menus.put(JALTOK_JAD, new HoverMenu("Extra @red@25%@whi@ drop rate when fighting Jad."));
        menus.put(LITTLE_NIGHTMARE, new HoverMenu("@red@+1@whi@ max hit and @red@5%@whi@ accuracy for range and mage."));
        menus.put(ARTIO_PET, new HoverMenu("@red@20%@whi@ Bonus accuracy for special attacks."));
        menus.put(KERBEROS_PET, new HoverMenu("@red@10%@whi@ accuracy to range, mage and melee attacks."));
        menus.put(SKORPIOS_PET, new HoverMenu("Your Venom goes through resistances."));
        menus.put(ARACHNE_PET, new HoverMenu("Binds and freezes are 2 seconds longer."));
        menus.put(CENTAUR_FEMALE, new HoverMenu("+@red@10%@whi@ more raids points."));
        menus.put(CENTAUR_MALE, new HoverMenu("+@red@10%@whi@ more raids points."));
        menus.put(FLUFFY_JR, new HoverMenu("+@red@10@whi@ overload boost in raids."));
        menus.put(FENRIR_GREYBACK_JR, new HoverMenu("+@red@15%@whi@ chance of dealing a second melee hit."));
        menus.put(DEMENTOR_PET, new HoverMenu("+@red@35%@whi@ extra smite prayer damage."));
        menus.put(ANCIENT_BARRELCHEST_PET, new HoverMenu("+@red@20%@whi@ more melee accuracy."));
        menus.put(ANCIENT_CHAOS_ELEMENTAL_PET, new HoverMenu("+@red@5%@whi@ extra special attack restore."));
        menus.put(ANCIENT_KING_BLACK_DRAGON_PET, new HoverMenu("@red@25%@whi@ damage and accuracy boosts vs dragons."));
        menus.put(BLOODHOUND, new HoverMenu("Gives at least one roll on the rare table when opening treasure caskets."));
        menus.put(ZRIAWK, new HoverMenu("@red@15%@whi@ extra drop rate."));
        menus.put(JAWA_PET, new HoverMenu("When having the Jawa pet out you have @red@50%@whi@ more chance on receiving a pet drop."));
        menus.put(FAWKES, new HoverMenu("Ability to teleport you out of wilderness from any level and any place."));
        menus.put(FAWKES_24937, new HoverMenu("Ability to teleport you out of wilderness from any level and any place."));
        menus.put(BARRELCHEST_PET, new HoverMenu("@red@+1@whi@ Max hit And @red@+10%@whi@ attack bonus accuracy."));
        menus.put(WAMPA, new HoverMenu("Has a @red@10%@whi@ chance to freeze your opponent."));
        menus.put(NIFFLER, new HoverMenu("Niffler stores items that you've received from a player kill or pvm drop into his pouch."));
        menus.put(BABY_ARAGOG, new HoverMenu("Gives @red@5%@whi@ vs players and @red@10%@whi@ vs npcs more Ranged damage and @red@15%@whi@ more Ranged accuracy."));
        menus.put(BABY_LAVA_DRAGON, new HoverMenu("Gives Gives @red@5%@whi@ vs players and @red@10%@whi@ vs npcs Magic damage and @red@10%@whi@ more Magic accuracy."));
        menus.put(MINI_NECROMANCER, new HoverMenu("Increases the max hit of any spell by @red@+5@whi@."));
        menus.put(PET_CORRUPTED_NECHRYARCH, new HoverMenu("@red@+2@whi@ Max hit And @red@+15%@whi@ melee attack bonus accuracy."));
        menus.put(FOUNDER_IMP, new HoverMenu("Having this pet out there is a @red@+10%@whi@ chance that your drop is being doubled. Does not stack with other double drop features."));
        menus.put(TZREKZUK, new HoverMenu("Gives @red@+5%@whi@ damage reduction."));
        menus.put(GENIE_PET, new HoverMenu("Whilst having this pet out your experience is always boosted by @red@X2@whi@ Does not stack with other exp boosts."));
        menus.put(DHAROK_PET, new HoverMenu("Increases your Dharok's axe accuracy by @red@+10%@whi@."));
        menus.put(PET_ZOMBIES_CHAMPION, new HoverMenu("Deals @red@+10%@whi@ extra damage and accuracy to the boss version."));
        menus.put(BABY_ABYSSAL_DEMON, new HoverMenu("Changes the dragon dagger special attack requirement to @red@-20%@whi@ instead of @red@-25%@whi@."));
        menus.put(BABY_DARK_BEAST_EGG, new HoverMenu("Makes the dark bow @red@+10%@whi@ more accurate."));
        menus.put(BABY_SQUIRT, new HoverMenu("Gives @red@10%@whi@ more accuracy and acts as a ring of vigour."));
        menus.put(GRIM_REAPER_PET, new HoverMenu("Keep one extra item on death. This doesn't work while @red@ smited or red skulled@whi@."));
        menus.put(OLMLET, new HoverMenu("Gives @red@10%@whi@ extra damage and accuracy during raids. And 10% boost in personal points."));

        menus.put(MYSTERY_TICKET, new HoverMenu("Has a chance to give some of the most valuable items in the game!",
            Arrays.asList(
                MYSTERY_CHEST,
                EPIC_PET_BOX,
                LEGENDARY_MYSTERY_BOX,
                DONATOR_MYSTERY_BOX,
                ARMADYL_GODSWORD_OR,
                DRAGON_CLAWS_OR,
                CRAWS_BOW_C,
                VIGGORAS_CHAINMACE_C,
                THAMMARONS_STAFF_C,
                TOXIC_STAFF_OF_THE_DEAD_C,
                RING_OF_PRECISION,
                RING_OF_SORCERY,
                RING_OF_MANHUNTING,
                ANCIENT_FACEGAURD,
                AMULET_OF_TORTURE_OR,
                NECKLACE_OF_ANGUISH_OR,
                OCCULT_NECKLACE_OR,
                PEGASIAN_BOOTS_OR,
                PRIMORDIAL_BOOTS_OR,
                ETERNAL_BOOTS_OR
            )));

        menus.put(16459, new HoverMenu("Has a chance to give some of the most valuable items in the game!",
            Arrays.asList(
                CustomItemIdentifiers.SANGUINE_SCYTHE_OF_VITUR,
                CustomItemIdentifiers.SANGUINE_TWISTED_BOW,
                CustomItemIdentifiers.HOLY_GHRAZI_RAPIER,
                CustomItemIdentifiers.HOLY_SANGUINESTI_STAFF,
                CustomItemIdentifiers.HOLY_SCYTHE_OF_VITUR,
                CustomItemIdentifiers.TWISTED_BOW_I,
                ItemIdentifiers.TWISTED_BOW,
                ItemIdentifiers.SCYTHE_OF_VITUR,
                ItemIdentifiers.GHRAZI_RAPIER,
                ItemIdentifiers.ANCESTRAL_ROBE_TOP,
                ItemIdentifiers.ANCESTRAL_ROBE_BOTTOM,
                ItemIdentifiers.DRAGON_CLAWS,
                ItemIdentifiers.KODAI_WAND,
                ItemIdentifiers.ANCESTRAL_HAT,
                ItemIdentifiers.SANGUINESTI_STAFF
            )));

        menus.put(16460, new HoverMenu("Has a chance to give some of the most valuable items in the game!",
            Arrays.asList(
                ItemIdentifiers.HEAVY_BALLISTA,
                ItemIdentifiers.LIGHT_BALLISTA,
                ItemIdentifiers.AMULET_OF_TORTURE,
                ItemIdentifiers.NECKLACE_OF_ANGUISH,
                ItemIdentifiers.RING_OF_SUFFERING,
                ItemIdentifiers.NECKLACE_OF_ANGUISH,
                ItemIdentifiers.AMULET_OF_TORTURE_OR,
                ItemIdentifiers.NECKLACE_OF_ANGUISH_OR,
                ItemIdentifiers.NECKLACE_OF_ANGUISH_OR
            )));

        menus.put(16458, new HoverMenu("Has a chance to give some of the most valuable pets in the game!",
            Arrays.asList(
                CustomItemIdentifiers.JAWA_PET,
                CustomItemIdentifiers.CENTAUR_FEMALE,
                CustomItemIdentifiers.CENTAUR_MALE,
                CustomItemIdentifiers.DEMENTOR_PET,
                CustomItemIdentifiers.FENRIR_GREYBACK_JR,
                CustomItemIdentifiers.FLUFFY_JR,
                CustomItemIdentifiers.ANCIENT_KING_BLACK_DRAGON_PET,
                CustomItemIdentifiers.ANCIENT_CHAOS_ELEMENTAL_PET,
                CustomItemIdentifiers.ANCIENT_BARRELCHEST_PET,
                CustomItemIdentifiers.ZRIAWK,
                CustomItemIdentifiers.FAWKES,
                CustomItemIdentifiers.BARRELCHEST_PET,
                CustomItemIdentifiers.WAMPA,
                CustomItemIdentifiers.NIFFLER,
                CustomItemIdentifiers.BABY_ARAGOG,
                CustomItemIdentifiers.FOUNDER_IMP,
                CustomItemIdentifiers.BABY_LAVA_DRAGON,
                CustomItemIdentifiers.JALTOK_JAD,
                CustomItemIdentifiers.MINI_NECROMANCER,
                CustomItemIdentifiers.PET_CORRUPTED_NECHRYARCH,
                ItemIdentifiers.TZREKZUK,
                CustomItemIdentifiers.GRIM_REAPER_PET,
                CustomItemIdentifiers.GENIE_PET,
                CustomItemIdentifiers.DHAROK_PET,
                CustomItemIdentifiers.PET_ZOMBIES_CHAMPION,
                CustomItemIdentifiers.BABY_ABYSSAL_DEMON,
                CustomItemIdentifiers.BABY_DARK_BEAST_EGG,
                CustomItemIdentifiers.BABY_SQUIRT
            )));

        menus.put(DONATOR_MYSTERY_BOX, new HoverMenu("Has a chance to give some of the most valuable items in the game!",
            Arrays.asList(
                //Ext rare
                CustomItemIdentifiers.NIFFLER,
                CustomItemIdentifiers.FAWKES,
                CustomItemIdentifiers.GRIM_REAPER_PET,
                CustomItemIdentifiers.BARRELCHEST_PET,
                _3RD_AGE_LONGSWORD,
                _3RD_AGE_BOW,
                _3RD_AGE_WAND,
                ANCESTRAL_HAT,
                ANCESTRAL_ROBE_TOP,
                ANCESTRAL_ROBE_BOTTOM,
                VESTAS_CHAINBODY,
                VESTAS_PLATESKIRT,
                VESTAS_LONGSWORD,
                MORRIGANS_COIF,
                MORRIGANS_LEATHER_BODY,
                MORRIGANS_LEATHER_CHAPS,
                ZURIELS_HOOD,
                ZURIELS_ROBE_TOP,
                ZURIELS_ROBE_BOTTOM,

                //Rare
                VESTAS_SPEAR,
                STATIUSS_FULL_HELM,
                STATIUSS_PLATEBODY,
                STATIUSS_PLATELEGS,
                STATIUSS_WARHAMMER,
                SANGUINESTI_STAFF,
                ELDER_MAUL,
                GHRAZI_RAPIER,
                DRAGON_CLAWS,
                ARCANE_SPIRIT_SHIELD,
                TOXIC_BLOWPIPE,
                DRAGON_WARHAMMER
            )));

        menus.put(HWEEN_MYSTERY_BOX, new HoverMenu("Has a chance to give some of the most valuable items in the game!",
            Arrays.asList(
                //Ext rare
                CustomItemIdentifiers.NIFFLER,
                CustomItemIdentifiers.FAWKES,
                CustomItemIdentifiers.GRIM_REAPER_PET,
                CustomItemIdentifiers.BARRELCHEST_PET,
                _3RD_AGE_LONGSWORD,
                _3RD_AGE_BOW,
                _3RD_AGE_WAND,
                ANCESTRAL_HAT,
                ANCESTRAL_ROBE_TOP,
                ANCESTRAL_ROBE_BOTTOM,
                VESTAS_CHAINBODY,
                VESTAS_PLATESKIRT,
                VESTAS_LONGSWORD,
                MORRIGANS_COIF,
                MORRIGANS_LEATHER_BODY,
                MORRIGANS_LEATHER_CHAPS,
                ZURIELS_HOOD,
                ZURIELS_ROBE_TOP,
                ZURIELS_ROBE_BOTTOM,

                //Rare
                VESTAS_SPEAR,
                STATIUSS_FULL_HELM,
                STATIUSS_PLATEBODY,
                STATIUSS_PLATELEGS,
                STATIUSS_WARHAMMER,
                SANGUINESTI_STAFF,
                ELDER_MAUL,
                GHRAZI_RAPIER,
                DRAGON_CLAWS,
                ARCANE_SPIRIT_SHIELD,
                TOXIC_BLOWPIPE,
                DRAGON_WARHAMMER
            )));

        menus.put(WEAPON_MYSTERY_BOX, new HoverMenu("Has a chance to give some of the most valuable items in the game!",
            Arrays.asList(
                //Ext rare
                NIGHTMARE_STAFF,
                SANGUINESTI_STAFF,
                VESTAS_LONGSWORD,
                STATIUSS_WARHAMMER,
                DRAGON_CLAWS_OR,
                ARMADYL_GODSWORD_OR,
                KODAI_WAND,
                DINHS_BULWARK,

                //Rare
                ARMADYL_GODSWORD,
                DRAGON_CLAWS,
                BANDOS_GODSWORD_OR,
                SARADOMIN_GODSWORD_OR,
                ZAMORAK_GODSWORD_OR,
                ZURIELS_STAFF,
                VESTAS_SPEAR,
                TOXIC_BLOWPIPE,
                TOXIC_STAFF_OF_THE_DEAD,
                DRAGON_HUNTER_CROSSBOW,
                DRAGON_HUNTER_LANCE,
                BLADE_OF_SAELDOR,
                GHRAZI_RAPIER,
                INQUISITORS_MACE,
                VOLATILE_ORB,
                HARMONISED_ORB,
                ELDRITCH_ORB,
                ELDER_MAUL
            )));

        menus.put(ARMOUR_MYSTERY_BOX, new HoverMenu("Has a chance to give some of the most valuable items in the game!",
            Arrays.asList(
                //Ext rare
                VESTAS_CHAINBODY,
                VESTAS_PLATESKIRT,
                STATIUSS_FULL_HELM,
                STATIUSS_PLATEBODY,
                STATIUSS_PLATELEGS,
                ANCESTRAL_HAT,
                ANCESTRAL_ROBE_TOP,
                ANCESTRAL_ROBE_BOTTOM,
                JUSTICIAR_FACEGUARD,
                JUSTICIAR_CHESTGUARD,
                JUSTICIAR_LEGGUARDS,

                //Rare
                MORRIGANS_COIF,
                MORRIGANS_LEATHER_BODY,
                MORRIGANS_LEATHER_CHAPS,
                ZURIELS_HOOD,
                ZURIELS_ROBE_TOP,
                ZURIELS_ROBE_BOTTOM,
                ARCANE_SPIRIT_SHIELD,
                SPECTRAL_SPIRIT_SHIELD,
                NEITIZNOT_FACEGUARD,
                GUARDIAN_BOOTS,
                TWISTED_BUCKLER,
                MAGMA_HELM,
                SERPENTINE_HELM,
                TANZANITE_HELM
            )));

        menus.put(LEGENDARY_MYSTERY_BOX, new HoverMenu("Has a chance to give some of the most valuable items in the game!",
            Arrays.asList(
                //Ext rare
                TWISTED_BOW,
                SCYTHE_OF_VITUR,
                ELYSIAN_SPIRIT_SHIELD,
                CustomItemIdentifiers.NIFFLER,
                CustomItemIdentifiers.FAWKES,
                CustomItemIdentifiers.GRIM_REAPER_PET,
                CustomItemIdentifiers.BARRELCHEST_PET,
                WAMPA,

                //Rare
                VOLATILE_NIGHTMARE_STAFF,
                HARMONISED_NIGHTMARE_STAFF,
                ELDRITCH_NIGHTMARE_STAFF,
                DRAGON_CLAWS_OR,
                ARMADYL_GODSWORD_OR,
                JUSTICIAR_FACEGUARD,
                JUSTICIAR_CHESTGUARD,
                JUSTICIAR_LEGGUARDS,
                ANCESTRAL_HAT,
                ANCESTRAL_ROBE_TOP,
                ANCESTRAL_ROBE_BOTTOM,
                INFERNAL_CAPE,
                RING_OF_MANHUNTING
            )));

        menus.put(MYSTERY_BOX, new HoverMenu("Has a chance to give some of the most valuable items in the game!",
            Arrays.asList(

                //Ext rare
                CustomItemIdentifiers.GRIM_REAPER_PET,
                CustomItemIdentifiers.BARRELCHEST_PET,
                ARCANE_SPIRIT_SHIELD,
                ELDER_MAUL,

                //Rare
                DRAGON_CLAWS,
                ARMADYL_GODSWORD,
                GHRAZI_RAPIER,
                SPECTRAL_SPIRIT_SHIELD,
                HEAVY_BALLISTA,
                TOXIC_STAFF_OF_THE_DEAD,
                SERPENTINE_HELM
            )));

        menus.put(MYSTERY_CHEST, new HoverMenu("Has a chance to give some of the most rare items in the game!",
            Arrays.asList(SANGUINE_SCYTHE_OF_VITUR, SANGUINE_TWISTED_BOW, ELYSIAN_SPIRIT_SHIELD, TWISTED_BOW, SCYTHE_OF_VITUR, ANCIENT_WARRIOR_AXE_C, ANCIENT_WARRIOR_MAUL_C, ANCIENT_WARRIOR_SWORD_C, SALAZAR_SLYTHERINS_LOCKET, SWORD_OF_GRYFFINDOR, TALONHAWK_CROSSBOW, ANCESTRAL_HAT_I, ANCESTRAL_ROBE_BOTTOM_I, ANCESTRAL_ROBE_TOP_I, LAVA_DHIDE_BODY, LAVA_DHIDE_CHAPS, LAVA_DHIDE_COIF, CORRUPTED_BOOTS, RING_OF_TRINITY,
                CRAWS_BOW_C, VIGGORAS_CHAINMACE_C, BOW_OF_FAERDHINEN_3, DARK_ARMADYL_HELMET, DARK_ARMADYL_CHESTPLATE, DARK_ARMADYL_CHAINSKIRT)));

        menus.put(HWEEN_MYSTERY_CHEST, new HoverMenu("Has a chance to give some of the most rare items in the game!",
            Arrays.asList(SANGUINE_SCYTHE_OF_VITUR, SANGUINE_TWISTED_BOW, ELYSIAN_SPIRIT_SHIELD, TWISTED_BOW, SCYTHE_OF_VITUR, ANCIENT_WARRIOR_AXE_C, ANCIENT_WARRIOR_MAUL_C, ANCIENT_WARRIOR_SWORD_C, SALAZAR_SLYTHERINS_LOCKET, SWORD_OF_GRYFFINDOR, TALONHAWK_CROSSBOW, ANCESTRAL_HAT_I, ANCESTRAL_ROBE_BOTTOM_I, ANCESTRAL_ROBE_TOP_I, LAVA_DHIDE_BODY, LAVA_DHIDE_CHAPS, LAVA_DHIDE_COIF, CORRUPTED_BOOTS, RING_OF_TRINITY,
                CRAWS_BOW_C, VIGGORAS_CHAINMACE_C, BOW_OF_FAERDHINEN_3, DARK_ARMADYL_HELMET, DARK_ARMADYL_CHESTPLATE, DARK_ARMADYL_CHAINSKIRT)));

        System.out.println(ClientConstants.CLIENT_NAME + " has loaded " + menus.size() + "x menu hovers.");
    }

    public static int drawType() {
        if (Client.singleton.cursor_x > 0 && Client.singleton.cursor_x < 500 && Client.singleton.cursor_y > 0
            && Client.singleton.cursor_y < 300) {
            return 1;
        }
        return 0;
    }

    public static boolean shouldDraw(int id) {
        return menus.get(id) != null;
    }

    public static boolean showMenu;

    public static String hintName;

    public static int hintId;

    public static int displayIndex;

    public static long displayDelay;

    public static int[] itemDisplay = new int[4];

    private static int lastDraw;

    public static void reset() {
        showMenu = false;
        hintId = -1;
        hintName = "";
    }

    public static boolean canDraw() {
		/*if (Client.singleton.menuActionRow < 2 && Client.singleton.itemSelected == 0
				&& Client.singleton.spellSelected == 0) {
			return false;
		}*/
        if (Client.singleton.menuActionText[Client.singleton.menuActionRow] != null) {
            if (Client.singleton.menuActionText[Client.singleton.menuActionRow].contains("Walk")) {
                return false;
            }
        }
        if (Client.singleton.tooltip != null && (Client.singleton.tooltip.contains("Walk") || Client.singleton.tooltip.contains("World"))) {
            return false;
        }
        if (Client.singleton.menuOpen) {
            return false;
        }
        if (hintId == -1) {
            return false;
        }
        return showMenu;
    }

    public static void drawHintMenu() {
        int cursor_x = Client.singleton.cursor_x;
        int cursor_y = Client.singleton.cursor_y;
        if (!canDraw()) {
            return;
        }

        if (Client.screen != Client.ScreenMode.FIXED) {
            if (Client.singleton.cursor_y < Client.window_height - 450
                && Client.singleton.cursor_x < Client.window_width - 200) {
                return;
            }
            cursor_x -= 100;
            cursor_y -= 50;
        }
        if (Client.screen == Client.ScreenMode.FIXED) {
            if (Client.singleton.cursor_y < 210 || Client.singleton.cursor_x < 561) {
              //  return;
            } else {
                cursor_x -= 516;
                cursor_y -= 158;
            }
            if (Client.singleton.cursor_x > 600 && Client.singleton.cursor_x < 685) {
                cursor_x -= 60;

            }
            if (Client.singleton.cursor_x > 685) {
                cursor_x -= 80;
            }
        }

        if (lastDraw != hintId) {
            lastDraw = hintId;
            itemDisplay = new int[4];
        }

        HoverMenu menu = menus.get(hintId);
        if (menu != null) {
            String[] text = split(menu.text).split("<br>");

            int height = (text.length * 12) + (menu.items != null ? 40 : 0);

            int width = (16 + text[0].length() * 5) + (menu.items != null ? 30 : 0);
            if (Client.screen == Client.ScreenMode.FIXED) {
                if (drawType() == 1) {
                    if (width + cursor_x > 500) {
                        cursor_x = 500 - width;
                    }
                } else {
                    if (width + cursor_x > 250) {
                        cursor_x = 245 - width;
                    }

                    if (height + cursor_y > 250) {
                        cursor_y = 250 - height;
                    }
                }
            }

            Rasterizer2D.draw_rect_outline(cursor_x, cursor_y + 5, width + 4, 26 + height, 0x383023);
            Rasterizer2D.draw_filled_rect(cursor_x + 1, cursor_y + 6, width + 2, 24 + height, 0x534a40, 200);

            Client.adv_font_small.draw("<col=ff9040>" + hintName, cursor_x + 4, cursor_y + 19,
                TEXT_COLOUR, 1);
            int y = 0;

            for (String string : text) {
                Client.adv_font_small.draw(string, cursor_x + 4, cursor_y + 35 + y, TEXT_COLOUR, 1);
                y += 12;
            }

            if (menu.items != null) {
                int spriteX = 10;

                if (System.currentTimeMillis() - displayDelay > 300) {
                    displayDelay = System.currentTimeMillis();
                    displayIndex++;
                    if (displayIndex == menu.items.size()) {
                        displayIndex = 0;
                    }

                    if (menu.items.size() <= 4) {
                        for (int i = 0; i < menu.items.size(); i++) {
                            itemDisplay[i] = menu.items.get(i);
                        }
                    } else {
                        if (displayIndex >= menu.items.size() - 1) {
                            displayIndex = menu.items.size() - 1;
                        }
                        int next = menu.items.get(displayIndex);
                        if (itemDisplay.length - 1 >= 0)
                            System.arraycopy(itemDisplay, 1, itemDisplay, 0, itemDisplay.length - 1);
                        itemDisplay[3] = next;
                    }
                }

                for (int id : itemDisplay) {
                    if (id < 1) {
                        continue;
                    }
                    SimpleImage item = ItemSpriteFactory.get_item_sprite(id, 1, 0);
                    if (item != null) {
                        item.drawSprite(cursor_x + spriteX, cursor_y + 35 + y);
                        spriteX += 40;
                    }
                }
            }
        }
    }

    private static String split(String text) {
        StringBuilder string = new StringBuilder();

        int size = 0;

        for (String s : text.split(" ")) {
            string.append(s).append(" ");
            size += s.length();
            if (size > 20) {
                string.append("<br>");
                size = 0;
            }
        }
        return string.toString();
    }

}
