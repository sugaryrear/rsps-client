package com.ferox.cache.graphics.widget;

import com.ferox.util.ItemIdentifiers;

import static com.ferox.util.CustomItemIdentifiers.*;
import static com.ferox.util.ItemIdentifiers.GRANITE_MAUL_24225;

public enum SpecialAttackBars {
    //Range
    DRAGON_THROWNAXE(20849, "Momentum Throw: Deal a fast attack with 25% improved accuracy.", 25.0, 31394, 7649,7661),
    TOXIC_BLOWPIPE(12926, "Toxic Siphon: Deal an attack that inflicts 50% more damage and heals you for 50% of the damage dealt.", 50.0, 31397, 7649,7661),
    TOXIC_BLOWPIPE_CUSTOM(30049, "Toxic Siphon: Deal an attack that inflicts 50% more damage and heals you for 50% of the damage dealt.", 50.0, 31397, 7649,7661),
    HWEEN_BLOWPIPE_(HWEEN_BLOWPIPE, "Toxic Siphon: Deal an attack that inflicts 50% more damage and heals you for 50% of the damage dealt.", 50.0, 31397, 7649,7661),
    DRAGON_KNIVE(22804, "Duality: Throws two knives at your opponent. (25%)", 25.0, 31429, 7649,7661),
    DRAGON_KNIVE_P(22806, "Duality: Throws two knives at your opponent. (25%)", 25.0, 31429, 7649,7661),
    DRAGON_KNIVE_P_PLUS(22808, "Duality: Throws two knives at your opponent. (25%)", 25.0, 31429, 7649,7661),
    DRAGON_KNIVE_P_PLUS_PLUS(22810, "Duality: Throws two knives at your opponent. (25%)", 25.0, 31429, 7649,7661),
    DORGESHUUN_CROSSBOW(8880, "Snipe: Deal an attack with greatly increased accuracy on unsuspecting targets and that lowers your target's Defence level by the damage dealt.", 75.0, 31411, 7574, 7586),
    DRAGON_CROSSBOW(21902, "Annihilate: Hit your target with 20% extra damage and in multicombat zones, hit all adjacent targets by 20% less damage.", 60.0, 31400, 7549, 7561),
    MAGIC_COMP_BOW(10284, "Powershot: Deal an attack that is guaranteed to hit your target.", 35.0, 31367, 7549, 7561),
    RUNE_THROWNAXE(805, "Chainhit: Throw an axe which hits your target and than chains to hit further nearby targets, costing 10% additional special attack per extra target.", 10.0, 31363, 7649,7661),
    MAGIC_SHORTBOW(861, "Snapshot: Fire two arrows within quick succession, but with reduced accuracy.", 55.0, 31387, 7549, 7561),
    MAGIC_SHORTBOW_I(12788, "Snapshot: Fire two arrows within quick succession, but with reduced accuracy.", 50.0,31452, 7549, 7561),
    MAGIC_SHORTBOW_I2(20558, "Snapshot: Fire two arrows within quick succession, but with reduced accuracy.", 55.0, 31381, 7549, 7561),
    MAGIC_LONGBOW(859, "Powershot: Deal an attack that is guaranteed to hit your target.", 35.0, 31386, 7549, 7561),
    SEERCULL(6724, "Soulshot: Deal an attack that is guaranteed to hit your target and lowers your target's Magic level by the damage dealt.", 100.0, 31379, 7549, 7561),
    ARMADYL_CROSSBOW(11785, "Armadyl Eye: Deal an attack with double accuracy.", 40.0, 31344, 7549, 7561),
    LIGHT_BALLISTA(19478, "Concentrated Shot: Deal an attack with 25% increased accuracy and damage, but takes an additional 2.4 seconds to fire.", 65.0, 31347, 7649, 7661),
    HEAVY_BALLISTA(19481, "Concentrated Shot: Deal an attack with 25% increased accuracy and damage, but takes an additional 2.4 seconds to fire.", 65.0, 31352, 7649, 7661),
    TIER_5_4_DARK_BOW(15710,"Descent of Darkness: Deal a double attack that inflicts up to 30% more damage (minimum damage of 5 per hit).",55.0, 31437, 7549, 7561),
    TIER_5_3_DARK_BOW(15709,"Descent of Darkness: Deal a double attack that inflicts up to 30% more damage (minimum damage of 5 per hit).",55.0, 31437, 7549, 7561),
    TIER_5_2_DARK_BOW(15708,"Descent of Darkness: Deal a double attack that inflicts up to 30% more damage (minimum damage of 5 per hit).",55.0, 31437, 7549, 7561),
    TIER_5_1_DARK_BOW(15707,"Descent of Darkness: Deal a double attack that inflicts up to 30% more damage (minimum damage of 5 per hit).",55.0, 31437, 7549, 7561),
    TIER_5_DARK_BOW(15706,"Descent of Darkness: Deal a double attack that inflicts up to 30% more damage (minimum damage of 5 per hit).",55.0, 31437, 7549, 7561),
    DARK_BOW_5(12765,"Descent of Darkness: Deal a double attack that inflicts up to 30% more damage (minimum damage of 5 per hit).",55.0, 31437, 7549, 7561),
    DARK_BOW_4(12766,"Descent of Darkness: Deal a double attack that inflicts up to 30% more damage (minimum damage of 5 per hit).",55.0, 31439, 7549, 7561),
    DARK_BOW_3(12767,"Descent of Darkness: Deal a double attack that inflicts up to 30% more damage (minimum damage of 5 per hit).",55.0, 31441, 7549, 7561),
    DARK_BOW_2(12768,"Descent of Darkness: Deal a double attack that inflicts up to 30% more damage (minimum damage of 5 per hit).",55.0, 31443, 7549, 7561),
    DARK_BOW_1(11235,"Descent of Darkness: Deal a double attack that inflicts up to 30% more damage (minimum damage of 5 per hit).",55.0, 31445, 7549, 7561),
    DARK_BOW(20408, "Descent of Darkness: Deal a double attack that inflicts up to 30% more damage (minimum damage of 5 per hit).",55.0, 31415, 7549, 7561),
    DAWNBRINGER(22516, "Pulsate: Fire an incredibly powerful burst of energy.", 35.0, 31453, 7649,7661),
    MORRIGANS_JAVELIN(22636, "Phantom Strike: This does nothing vs non-player targets. As well as dealing a regular attack, your target will suffer that damage again over time.", 50.0, 31458, 7649,7661),
    MORRIGANS_JAVELIN_23619(23619, "Phantom Strike: This does nothing vs non-player targets. As well as dealing a regular attack, your target will suffer that damage again over time.", 50.0, 31458, 7649,7661),
    MORRIGANS_THROWING_AXE(22634, "Hamstring: Deal a minimum of 20% extra damage and hamstring player targets, causing them to lose run energy 6x faster for 1 minute.", 50.0, 31459, 7649,7661),
    ELEMENTAL_BOW(12081, "Elementals: Fires very powerful arrows towards your target. 50% extra damage.", 50.0, 31461, 7549, 7561),
    ZARYTE_BOW(20171,"Zaryte: Fires a very powerful arrow towards your target draining all prayer points.",75.0,24148,7549, 7561),

    //Magic
    STAFF_OF_THE_DEAD(11791, "Power of Death: Reduce all melee damage you receive by 50% for the next minute while the staff remains equipped. Stacks with Protect from Melee.", 100.0, 31345, 7474, 7486),
    TOXIC_STAFF_UNCHARGED(12902, "Power of Death: Reduce all melee damage you receive by 50% for the next minute while the staff remains equipped. Stacks with Protect from Melee.", 100.0, 31390, 7474, 7486),
    TOXIC_STAFF_OF_THE_DEAD(12904, "Power of Death: Reduce all melee damage you receive by 50% for the next minute while the staff remains equipped. Stacks with Protect from Melee.", 100.0, 31391, 7474, 7486),
    TOXIC_STAFF_OF_THE_DEAD_C(27006, "Power of Death: Reduce all melee damage you receive by 50% for the next minute while the staff remains equipped. Stacks with Protect from Melee.", 100.0, 31391, 7474, 7486),
    STAFF_OF_LIGHT(22296, "Power of Death: Reduce all melee damage you receive by 50% for the next minute while the staff remains equipped. Stacks with Protect from Melee.", 100.0, 31349, 7474, 7486),
    VOLATILE_NIGHTMARE_STAFF(24424, "Immolate: Fire a powerful spell with 50% improved accuracy. Damage scales based upon magic level and can be increased with magic damage boosting equipment. Costs no runes to use.", 55.0, 31460, 7474, 7486),
    ELDRITCH_NIGHTMARE_STAFF(24425, "Invocate: Fire a powerful spell that restores your prayer points by 50% of the damage dealt. Damage scales based upon magic level and can be increased with magic damage boosting equipment. Your prayer points can go above your level, to a maximum of 120. Costs no runes to use.", 75.0, 31460, 7474, 7486),
    ELEMENTAL_STAFF(12082, "Elementals: Fires incredibly powerful elemental spells.", 50.0, 31462, 7474, 7486),
    STAFF_OF_BALANCE(24144, "Power of Death: Reduce all melee damage you receive by 50% for the next minute while the staff remains equipped. Stacks with Protect from Melee.", 100.0, 31463, 7474, 7486),

    //Melee
    ANCIENT_WARRIOR_SWORD(7806,"Ancient warrior: Summons a powerful ancient warrior that can deal up to 55 damage.",100,24145,7599,7611),
    ANCIENT_WARRIOR_AXE(7807,"Ancient warrior: Deal an attack, with double accuracy, that inflicts 15% more damage and drains your target's prayer and defence by the damage dealt.", 50,24146,7499,7511),
    ANCIENT_WARRIOR_MAUL(7808, "Ancient warrior: Deal an attack with 50% more accuracy, 25% more damage and hits guaranteed +35 damage.",50,24147,7474,7486),
    ANCIENT_WARRIOR_SWORD_C(24983,"Ancient warrior: Summons a powerful ancient warrior that can deal up to 55 damage.",100,24145,7599,7611),
    ANCIENT_WARRIOR_AXE_C(24981,"Ancient warrior: Deal an attack, with double accuracy, that inflicts 15% more damage and drains your target's prayer and defence by the damage dealt.", 50,24146,7499,7511),
    ANCIENT_WARRIOR_MAUL_C(24982, "Ancient warrior: Deal an attack with 50% more accuracy, 25% more damage and hits guaranteed +35 damage.",50,24147,7474,7486),
    GRANITE_HAMMER(21742,"Hammer Blow: Deal an attack with 50% more accuracy and a guaranteed +5 damage.", 60.0,31450, 7474, 7486),
    DRAGON_2H_SWORD(7158, "Powerstab: Hit up to fourteen enemies surrounding you.",60.0, 31464, 7699, 7711),
    BONE_DAGGER(8872, "Backstab: Deal an attack with greatly increased accuracy on unsuspecting targets and that lowers your target's Defence level by the damage dealt.", 75.0, 31407, 7574, 7586),
    BONE_DAGGER_P1(8874, "Backstab: Deal an attack with greatly increased accuracy on unsuspecting targets and that lowers your target's Defence level by the damage dealt.", 75.0, 31408, 7574, 7586),
    BONE_DAGGER_P2(8876, "Backstab: Deal an attack with greatly increased accuracy on unsuspecting targets and that lowers your target's Defence level by the damage dealt.", 75.0, 31409, 7574, 7586),
    BONE_DAGGER_P3(8878, "Backstab: Deal an attack with greatly increased accuracy on unsuspecting targets and that lowers your target's Defence level by the damage dealt.", 75.0, 31410, 7574, 7586),
    NEW_CRYSTAL_HALBERD_FULL_I(13080, "Sweep: If your target is small, adjacent targets may be hit too. Otherwise,  your target may be hit a second time,  with 25% decreased accuracy. Damage in all cases is increased by 10% of your max hit.", 30.0, 31350, 8493, 8505),
    NEW_CRYSTAL_HALBERD_FULL(13091, "Sweep: If your target is small, adjacent targets may be hit too. Otherwise,  your target may be hit a second time,  with 25% decreased accuracy. Damage in all cases is increased by 10% of your max hit.", 30.0, 31361, 8493, 8505),
    BRINE_SABRE(11037, "Liquify: Deal an attack with double the chance of hitting that boosts your Strength, Attack and Defence levels by 25% of the damage dealt. Can only be used underwater.", 75.0, 31355, 7574, 7586),
    RUNE_CLAWS(3101, "Impale: Deal an attack with 10% increased Attack and Strength, but with a slower speed.", 25.0, 31356, 7800, 7812),
    EXCALIBUR(35, "Sanctuary: Temporarily increase your Defence level by 8.", 100.0, 31360, 7574, 7586),
    THIRD_AGE_AXE(20011, "Lumber Up: Increase your Woodcutting level by 3.", 100.0, 31366, 7499, 7511),
    THIRD_AGE_PICKAXE(20014, "Rock Knocker: Increase your Mining level by 3.", 100.0, 31368, 7724, 7736),
    DRAGON_CLAWS_1(20784, "Slice and Dice: Hit 4 times in quick succession, with great accuracy.", 50.0, 31372, 7800, 7812),
    HWEEN_DRAGON_CLAWS_(HWEEN_DRAGON_CLAWS, "Slice and Dice: Hit 4 times in quick succession, with great accuracy.", 50.0, 31372, 7800, 7812),
    DRAGON_WARHAMMER_1(20785, "Smash: Deal an attack that inflicts 50% more damage and lowers your target's Defence level by 30%.", 50.0, 31373, 7474, 7486),
    GRANITE_MAUL_OR1(20557, "Quick Smash: Deal an extra attack instantly.", 50.0, 31380, 7474, 7486),
    DRAGON_AXE(6739, "Lumber Up: Increase your Woodcutting level by 3.", 100.0, 31382, 7499, 7511),
    DRAGON_BATTLEAXE(1377, "Rampage: Drain your Attack, Defence,  Ranged & Magic levels by 10%,  while increasing your Strength by 10 levels, plus 25% of the drained levels.", 100.0, 31388, 7499, 7511),
    DRAGON_SPEAR(1249, "Shove: Push your target back and stun them for 3 seconds. This attack deals no damage.",25.0, 31444, 7674, 7686),
    DRAGON_SPEAR_P(1263, "Shove: Push your target back and stun them for 3 seconds. This attack deals no damage.",25.0, 31451, 7674, 7686),
    DRAGON_SPEARP(5716, "Shove: Push your target back and stun them for 3 seconds. This attack deals no damage.", 25.0, 31383, 7674, 7686),
    DRAGON_SPEAR_P1(5730, "Shove: Push your target back and stun them for 3 seconds. This attack deals no damage.", 25.0, 31389, 7674, 7686),
    DRAGON_SPEARKP(3176, "Shove: Push your target back and stun them for 3 seconds. This attack deals no damage.", 25.0, 31392, 7674, 7686),

    SARAS_BLESSED_SWORD_FULL(12808,"Saradomin's Lightning: Call upon Saradomin's power to perform an attack with 25% higher max hit.",65.0, 31341, 7699, 7711),
    SARADOMINS_BLESSED_SWORD(12809,"Saradomin's Lightning: Call upon Saradomin's power to perform an attack with 25% higher max hit.",65.0, 31343, 7699, 7711),
    DRAGON_WARHAMMER(13576,"Smash: Deal an attack that inflicts 50% more damage and lowers your target's Defence level by 30%.",50.0, 31342, 7474, 7486),
    DRAGON_SWORD(21009, "Wild Stab: Deal an attack which hits through Protect from Melee with 25% increased accuracy and damage.", 40.0, 31346, 7574, 7586),
    DINHS_BULWARK(21015, "Shield Bash: Hit surrounding targets within a 10x10 area with 20% increased accuracy.", 50.0, 31348, 7474, 7486),
    DRAGON_LONGSWORD(1305, "Cleave: Deal a powerful attack that inflicts 15% more damage.", 25.0, 31351, 7599, 7611),
    ARMADYL_GODSWORD_20593(20593, "The Judgement: Deal an attack that inflicts 37.5% more damage with double accuracy.", 50.0, 31353, 7699, 7711),
    ARMADYL_GODSWORD(11802, "The Judgement: Deal an attack that inflicts 37.5% more damage with double accuracy.", 50.0, 31353, 7699, 7711),
    BEGINNER_AGS(14487, "The Judgement: Deal an attack that inflicts 37.5% more damage with double accuracy.", 50.0, 31353, 7699, 7711),
    HWEEN_AGS(HWEEN_ARMADYL_GODSWORD, "The Judgement: Deal an attack that inflicts 37.5% more damage with double accuracy.", 50.0, 31353, 7699, 7711),
    BANDOS_GODSWORD(11804, "Warstrike: Deal an attack, with double accuracy,  that inflicts 21% more damage and drains one of your target's combat stats by the damage dealt.", 50.0, 31354, 7699, 7711),
    SARADOMIN_GODSWORD(11806, "Healing Blade: Deal an attack that inflicts 10% more damage with double accuracy, while restoring your Hitpoints and Prayer by 50% and 25% of the damage dealt respectively.", 50.0, 31357, 7699, 7711),
    DRAGON_SCIMITAR_OR(20000, "Sever: Deal a slash with increased accuracy that prevents your target from using protection prayers for 5 seconds if it successfully hits.", 55.0, 31358, 7599, 7611),
    ZAMORAK_GODSWORD(11808, "Ice Cleave: Deal an attack that inflicts 10% more damage with double accuracy and freezes your target for 20 seconds if it successfully hits.", 50.0, 31359, 7699, 7711),
    DRAGON_HARPOON(21028, "Fishstabber: Increase your Fishing level by 3.", 100.0, 31362, 7574, 7586),
    INFERNAL_HARPOON(21031, "Fishstabber: Increase your Fishing level by 3.", 100.0, 31364, 7574, 7586),
    INFERNAL_HARPOON_UNCHARGED(21033, "Fishstabber: Increase your Fishing level by 3.", 100.0, 31365, 7574, 7586),
    DRAGON_DAGGER_P1(5680, "Puncture: Deal two quick slashes with 25% increased accuracy and 15% increased damage.", 25.0, 31369, 7574, 7586),
    GRANITE_MAUL_OR(12848, "Quick Smash: Deal an extra attack instantly.", 50.0, 31370, 7474, 7486),
    ZAMORAKIAN_SPEAR(11824, "Shove: Push your target back and stun them for 3 seconds. This attack deals no damage.", 25.0, 31371, 7674, 7686),
    ANCIENT_MACE(11061, "Favour of the War God: Deal an attack which hits through Protect from Melee and siphons Prayer points equal to the damage dealt.", 100.0, 31374, 7624, 7636),
    ABYSSAL_WHIP(4151, "Energy Drain: Deal an attack with 25% increased accuracy that siphons 10% of your target's run energy.", 50.0, 31375, 12323, 12335),
    GRANITE_MAUL(GRANITE_MAUL_24225, "Quick Smash: Deal an extra attack instantly.", 50.0, 31376, 7474, 7486),
    HWEEN_GRANITE_MAUL_(HWEEN_GRANITE_MAUL, "Quick Smash: Deal an extra attack instantly.", 50.0, 31376, 7474, 7486),
    GRANITE_MAUL_24944(24944, "Quick Smash: Deal an extra attack instantly.", 50.0, 31376, 7474, 7486),
    SARADOMIN_SWORD(11838, "Saradomin's Lightning: Call upon Saradomin's power to perform an attack that inflicts 10% more melee damage and 1-16 extra magic damage.", 100.0, 31377, 7699, 7711),
    DRAGON_DAGGERP(5698, "Puncture: Deal two quick slashes with 25% increased accuracy and 15% increased damage.", 25.0, 31378, 7574, 7586),
    DRAGON_DAGGERP_24949(24949, "Puncture: Deal two quick slashes with 25% increased accuracy and 15% increased damage.", 25.0, 31378, 7574, 7586),
    BEGINNER_DRAGON_CLAWS(14486, "Slice and Dice: Hit 4 times in quick succession, with great accuracy.", 50.0, 31384, 7800, 7812),
    DRAGON_CLAWS(13652, "Slice and Dice: Hit 4 times in quick succession, with great accuracy.", 50.0, 31384, 7800, 7812),
    DRAGON_CLAWS_OR(13188, "Slice and Dice: Hit 4 times in quick succession, with great accuracy.", 50.0, 31384, 7800, 7812),
    DARKLIGHT(6746, "Weaken: Temporarily drain your target's Attack, Strength and Defence by 5%. Is twice as effective against demons.", 50.0, 31385, 7574, 7586),
    ZAMORAKIAN_HASTA(11889, "Shove: Push your target back and stun them for 3 seconds. This attack deals no damage.", 25.0, 31393, 7674, 7686),
    ARMADYL_GODSWORD_OR1(20593, "The Judgement: Deal an attack that inflicts 37.5% more damage with double accuracy.", 50.0, 31395, 7699, 7711),
    DRAGON_HALBERD(3204, "Sweep: If your target is small, adjacent targets may be hit too. Otherwise,  your target may be hit a second time,  with 25% decreased accuracy. Damage in all cases is increased by 10% of your max hit.", 30.0, 31398, 8493, 8505),
    BARRELCHEST_ANCHOR(10887, "Sunder: Deal an attack with double accuracy and 10% higher max hit. Lowers your target's Attack, Defence,  Ranged or Magic level by 10% of the damage dealt.", 50.0, 31399, 7624, 7636),
    DRAGON_PICKAXE(11920, "Rock Knocker: Increase your Mining level by 3.", 100.0, 31401, 7724, 7736),
    ARMADYL_GODSWORD_OR(20368, "The Judgement: Deal an attack that inflicts 37.5% more damage with double accuracy.", 50.0, 31402, 7699, 7711),
    BANDOS_GODSWORD_OR(20370, "Warstrike: Deal an attack, with double accuracy,  that inflicts 21% more damage and drains one of your target's combat stats by the damage dealt.", 50.0, 31403, 7699, 7711),
    SARADOMIN_GODSWORD_OR(20372, "Healing Blade: Deal an attack that inflicts 10% more damage with double accuracy, while restoring your Hitpoints and Prayer by 50% and 25% of the damage dealt respectively.", 50.0, 31404, 7699, 7711),
    ZAMORAK_GODSWORD_OR(20374, "Ice Cleave: Deal an attack that inflicts 10% more damage with double accuracy and freezes your target for 20 seconds if it successfully hits.", 50.0, 31405, 7699, 7711),
    DRAGON_MACE(1434, "Shatter: Increase damage by 50% and accuracy by 25% for one hit.", 25.0, 31406, 7624, 7636),
    INQUISITORS_MACE(ItemIdentifiers.INQUISITORS_MACE, "Shatter: Increase damage by 25% and accuracy by 50% for one hit.", 25.0, 31406, 7624, 7636),
    SHADOW_MACE(28001, "Shadow smasher: Increase damage by 35% and accuracy by 75% for one hit.", 35.0, 31406, 7624, 7636),
    ABYSSAL_WHIP_1(20405, "Energy Drain: Deal an attack with 25% increased accuracy that siphons 10% of your target's run energy.", 50.0, 31412, 12323, 12335),
    DRAGON_SCIMITAR_OR1(20406, "Sever: Deal a slash with increased accuracy that can prevent your target from using protection prayers for 5 seconds.", 55.0, 31413, 7599, 7611),
    DRAGON_DAGGER_OR(20407, "Puncture: Deal two quick slashes with 25% increased accuracy and 15% increased damage.", 25.0, 31414, 7574, 7586),
    INFERNAL_AXE(13241, "Lumber Up: Increase your Woodcutting level by 3.", 100.0, 31416, 7499, 7511),
    INFERNAL_AXE_UNCHARGED(13242, "Lumber Up: Increase your Woodcutting level by 3.", 100.0, 31417, 7499, 7511),
    INFERNAL_PICKAXE(13243, "Rock Knocker: Increase your Mining level by 3.", 100.0, 31418, 7724, 7736),
    INFERNAL_PICKAXE_UNCHARGED(13244, "Rock Knocker: Increase your Mining level by 3.", 100.0, 31419, 7724, 7736),
    DRAGON_DAGGER(1215, "Puncture: Deal two quick slashes with 25% increased accuracy and 15% increased damage.",25.0, 31420, 7574, 7586),
    DRAGON_DAGGER_P(1231, "Puncture: Deal two quick slashes with 25% increased accuracy and 15% increased damage.",25.0, 31421, 7574, 7586),
    ABYSSAL_BLUDGEON(13263,"Penance: Deal an attack that inflicts 0.5% more damage per prayer point that you have used.", 50.0,31422, 7474, 7486),
    ABYSSAL_DAGGER(13265,"Abyssal Puncture: Deal an attack that hits twice with 25% increased accuracy, but inflicts 15% less damage per hit. The second hit is guaranteed if the first deals damage.",50.0, 31423, 7574, 7586),
    ABYSSAL_DAGGER_P1(13267,"Abyssal Puncture: Deal an attack that hits twice with 25% increased accuracy, but inflicts 15% less damage per hit. The second hit is guaranteed if the first deals damage.",50.0, 31424, 7574, 7586),
    ABYSSAL_DAGGER_P2(13269,"Abyssal Puncture: Deal an attack that hits twice with 25% increased accuracy, but inflicts 15% less damage per hit. The second hit is guaranteed if the first deals damage.",50.0, 31425, 7574, 7586),
    DRAGON_SWORD_OR(21206,"Wild Stab: Deal an attack which hits through Protect from Melee with 25% increased accuracy and damage.",40.0, 31426, 7574, 7586),
    ABYSSAL_DAGGER_P3(13271,"Abyssal Puncture: Deal an attack that hits twice with 25% increased accuracy, but inflicts 15% less damage per hit. The second hit is guaranteed if the first deals damage.",50.0, 31428, 7574, 7586),
    ARCLIGHT(19675,"Weaken: Temporarily drain your target's Attack, Strength and Defence by 5%. Is twice as effective against demons.",50.0, 31434, 7574, 7586),
    VOLCANIC_ABYSSAL_WHIP(12773,"Energy Drain: Deal an attack with 25% increased accuracy that siphons 10% of your target's run energy.",50.0, 31446, 12323, 12335),
    FROZEN_ABYSSAL_WHIP(12774,"Energy Drain: Deal an attack with 25% increased accuracy that siphons 10% of your target's run energy.",50.0, 31447, 12323, 12335),
    ABYSSAL_TENTACLE(12006,"Binding Tentacle: Bind your target for 5 seconds and increase the chance of them being poisoned.",50.0, 31448, 12323, 12335),
    ABYSSAL_TENTACLE_24948(24948,"Binding Tentacle: Bind your target for 5 seconds and increase the chance of them being poisoned.",50.0, 31448, 12323, 12335),
    DRAGON_SCIMITAR(4587,"Sever: Deal a slash with increased accuracy that prevents your target from using protection prayers for 5 seconds if it successfully hits.",55.0, 31449, 7599, 7611),
    BLADE_OF_SAELDOR_T(BLADE_OF_SAELDOR_8,"Smack: Deal a strong smack with increased accuracy and a damage boost of 20%.",50.0, 31449, 7599, 7611),
    DRAGON_PICKAXE_OR(12797, "Rock Knocker: Increase your Mining level by 3.", 100.0, 31454, 7724, 7736),
    VESTAS_LONGSWORD_23615(23615, "Feint: Deal an attack which treats your target's defence as if it was reduced by 75%. It will deal an minimum of 20% extra damage.", 25.0, 31455, 7599, 7611),
    VESTAS_LONGSWORD(22613, "Feint: Deal an attack which treats your target's defence as if it was reduced by 75%. It will deal an minimum of 20% extra damage.", 25.0, 31455, 7599, 7611),
    ANCIENT_VESTAS_LONGSWORD(24995, "Feint: Deal an attack which treats your target's defence as if it was reduced by 75%. It will deal an minimum of 25% extra damage.", 25.0, 31455, 7599, 7611),
    VESTAS_SPEAR(22610, "Spear Wall: All damage dealt by this attack is reduced by 50%, but will damage up to sixteen targets surrounding you. After using this attack, you will be immune from melee attacks for 5 seconds.", 50.0, 31456, 7674, 7686),
    STATIUS_WARHAMMER(22622, "Smash: Deal an attack which deals a minimum 25% extra damage and lowers your target's Defence level by 30%.", 35.0, 31457, 7474, 7486),
    STATIUS_WARHAMMER_23620(23620, "Smash: Deal an attack which deals a minimum 25% extra damage and lowers your target's Defence level by 30%.", 35.0, 31457, 7474, 7486),
    ANCIENT_STATIUS_WARHAMMER(24996, "Smash: Deal an attack which deals a minimum 25% extra damage and lowers your target's Defence level by 30%.", 35.0, 31457, 7474, 7486),

    //We don't have the interface...
    IVANDIS_FLAIL(22398, "Retainer: Traps a vampyre juvenile or juvinate provided they have 50% or less hitpoints remaining.", 10.0, 31396, -1, -1),
    ROD_OF_IVANDIS_10(7639,"Retainer: Traps a vampyre juvenile or juvinate provided they have 50% or less hitpoints remaining.",10.0, 31427, -1, -1),
    ROD_OF_IVANDIS_9(7640,"Retainer: Traps a vampyre juvenile or juvinate provided they have 50% or less hitpoints remaining.",10.0, 31430, -1, -1),
    ROD_OF_IVANDIS_8(7641,"Retainer: Traps a vampyre juvenile or juvinate provided they have 50% or less hitpoints remaining.",10.0, 31431, -1, -1),
    ROD_OF_IVANDIS_7(7642,"Retainer: Traps a vampyre juvenile or juvinate provided they have 50% or less hitpoints remaining.",10.0, 31432, -1, -1),
    ROD_OF_IVANDIS_6(7643,"Retainer: Traps a vampyre juvenile or juvinate provided they have 50% or less hitpoints remaining.",10.0, 31433, -1, -1),
    ROD_OF_IVANDIS_5(7644,"Retainer: Traps a vampyre juvenile or juvinate provided they have 50% or less hitpoints remaining.",10.0, 31435, -1, -1),
    ROD_OF_IVANDIS_4(7645,"Retainer: Traps a vampyre juvenile or juvinate provided they have 50% or less hitpoints remaining.",10.0, 31436, -1, -1),
    ROD_OF_IVANDIS_3(7646,"Retainer: Traps a vampyre juvenile or juvinate provided they have 50% or less hitpoints remaining.",10.0, 31438, -1, -1),
    ROD_OF_IVANDIS_2(7647,"Retainer: Traps a vampyre juvenile or juvinate provided they have 50% or less hitpoints remaining.",10.0, 31440, -1, -1),
    ROD_OF_IVANDIS_1(7648,"Retainer: Traps a vampyre juvenile or juvinate provided they have 50% or less hitpoints remaining.",10.0, 31442, -1, -1);

    private int itemId, interfaceId, specialMeter, tooltipChild;
    private double specialAmount;
    private String tooltip;

    SpecialAttackBars(int itemId, String tooltip, double specialAmount, int tooltipChild, int interfaceId, int specialMeter) {
        this.itemId = itemId;
        this.interfaceId = interfaceId;
        this.specialMeter = specialMeter;
        this.specialAmount = specialAmount;
        this.tooltip = tooltip;
        this.tooltipChild = tooltipChild;
    }

    public int getItemId() {
        return itemId;
    }

    public int readInterfaceId() {
        return interfaceId;
    }

    public int getSpecialMeter() {
        return specialMeter;
    }

    public double getSpecialAmount() {
        return specialAmount;
    }

    public String getTooltip() {
        return tooltip;
    }

    public int getTooltipChild() {
        return tooltipChild;
    }
}
