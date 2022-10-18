package com.ferox.cache.def.impl.npcs;

import com.ferox.cache.def.NpcDefinition;
import com.ferox.util.NpcIdentifiers;

/**
 * @author Patrick van Elderen | July, 12, 2021, 14:02
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class MemberNpcs {

    public static void unpack(int id) {
        NpcDefinition definition = NpcDefinition.get(id);
//        if (id == 11199) {//pk npc
//            NpcDefinition elfinlocks = forID(3014);
//
//            entityDef.models = new int[] {187 /* green party hat*/,6669,35817
//                ,216/*face/head*/,13307/*barrows gloves*/,6659/*ahrims robeskirt*/,3704/*climbing boots*/,9347,5409,9638};
//            entityDef.originalColors = new int []  { 926/*green partyhat*/,8741,14490,9152,};
//            entityDef.newColors = new int [] { 22464/*green partyhat*/,10512,10512, ColourUtils.RGB_to_RS2HSB(new Color(242,245,253).getRGB())};
//
//            entityDef.name = "Durial321";
//            entityDef.description = "Bank your items!";
//            entityDef.actions = new String[]{"Talk-to", null, "Trade", null, null};
//            entityDef.standAnim = elfinlocks.standAnim;
//            entityDef.walkAnim = elfinlocks.walkAnim;
//
//        }
        if(id == 16000) {
            NpcDefinition.copy(definition, NpcIdentifiers.REVENANT_DARK_BEAST);
            definition.name = "Ancient revenant dark beast";
            definition.modelCustomColor4 = 235;
        }
        if(id == 16001) {
            NpcDefinition.copy(definition, NpcIdentifiers.REVENANT_ORK);
            definition.name = "Ancient revenant ork";
            definition.modelCustomColor4 = 235;
        }
        if(id == 16002) {
            NpcDefinition.copy(definition, NpcIdentifiers.REVENANT_CYCLOPS);
            definition.name = "Ancient revenant cyclops";
            definition.modelCustomColor4 = 235;
        }
        if(id == 16003) {
            NpcDefinition.copy(definition, NpcIdentifiers.REVENANT_DRAGON);
            definition.name = "Ancient revenant dragon";
            definition.modelCustomColor4 = 235;
        }
        if(id == 16004) {
            NpcDefinition.copy(definition, NpcIdentifiers.REVENANT_KNIGHT);
            definition.name = "Ancient revenant knight";
            definition.modelCustomColor4 = 235;
        }
        if(id == 16005) {
            NpcDefinition.copy(definition, NpcIdentifiers.BARRELCHEST_6342);
            definition.name = "Ancient barrelchest";
            definition.modelCustomColor4 = 235;
        }
        if(id == 16006) {
            NpcDefinition.copy(definition, NpcIdentifiers.KING_BLACK_DRAGON);
            definition.name = "Ancient king black dragon";
            definition.modelCustomColor4 = 235;
        }
        if(id == 16007) {
            NpcDefinition.copy(definition, NpcIdentifiers.CHAOS_ELEMENTAL);
            definition.name = "Ancient chaos elemental";
            definition.modelCustomColor4 = 235;
        }
    }
}
