package com.ferox.cache.graphics.widget.impl;

import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.SpecialAttackBars;
import com.ferox.cache.graphics.widget.Widget;

public class WeaponInterfacesWidget extends Widget {

    public static void unpack(AdvancedFont[] fonts) {
        addButtonWithoutSprite(12322, "Use <col=65280>Special Attack", 27, 150);
        createTooltip(22850, "When active your player<br>will automatically fight<br>back if attacked.", 150, 44);

        Widget dinhsBulwark = addTabInterface(11799);
        createTooltip(11802, "No attacking!", 68, 44);
        addSprite(11840, 864);
        dinhsBulwark.totalChildren(14);
        setBounds(428, 0, 5, 0, dinhsBulwark);
        setBounds(429, 0, 25, 1, dinhsBulwark);
        setBounds(432, 21, 46, 2, dinhsBulwark);
        setBounds(438, 21, 75, 3, dinhsBulwark);
        setBounds(431, 105, 46, 4, dinhsBulwark);
        setBounds(439, 105, 75, 5, dinhsBulwark);
        setBounds(11840, 41, 51, 6, dinhsBulwark);
        setBounds(434, 124, 50, 7, dinhsBulwark);
        setBounds(24044, 21, 46, 8, dinhsBulwark);
        setBounds(11802, 105, 46, 9, dinhsBulwark);
        setBounds(22845, 20, 153, 10, dinhsBulwark);
        setBounds(24042, 52, 161, 11, dinhsBulwark);
        setBounds(7474, 17, 201, 12, dinhsBulwark);
        setBounds(22850, 20, 153, 13, dinhsBulwark);

        Widget chinchompa = addTabInterface(24899);
        createTooltip(24901, "(Short fuse)<br>(Ranged XP)", 68, 44);
        createTooltip(24902, "(Medium fuse)<br>(Ranged XP)", 68, 44);
        createTooltip(24908, "(Long fuse)<br>(Ranged XP)<br>(Defence XP)", 68, 44);
        addText(24903, "Short fuse", fonts, 0, 0xff9933, false, true);
        addText(24904, "Medium fuse", fonts, 0, 0xff9933, false, true);
        addText(24907, "Long fuse", fonts, 0, 0xff9933, false, true);
        addSprite(24900, 865);
        addSprite(24905, 867);
        addSprite(24906, 866);
        chinchompa.totalChildren(17);
        setBounds(428, -1, 5, 0, chinchompa);
        setBounds(429, 0, 25, 1, chinchompa);
        setBounds(433, 21, 46, 2, chinchompa);
        setBounds(431, 21, 99, 3, chinchompa);
        setBounds(24903, 28, 75, 4, chinchompa);
        setBounds(24906, 40, 103, 5, chinchompa);
        setBounds(24907, 30, 128, 6, chinchompa);
        setBounds(432, 105, 46, 7, chinchompa);
        setBounds(24904, 108, 75, 8, chinchompa);
        setBounds(24900, 40, 50, 9, chinchompa);
        setBounds(24905, 124, 50, 10, chinchompa);
        setBounds(24901, 21, 46, 11, chinchompa);
        setBounds(24902, 105, 46, 12, chinchompa);
        setBounds(22845, 20, 153, 13, chinchompa);
        setBounds(24042, 52, 161, 14, chinchompa);
        setBounds(22850, 20, 153, 15, chinchompa);
        setBounds(24908, 21, 99, 16, chinchompa);

        Widget salamander = addTabInterface(22899);
        createTooltip(22601, "(Aggressive)<br>(Slash)<br>(Strength XP)", 68, 44);
        createTooltip(22602, "(Accurate)<br>(Ranged)<br>(Ranged XP)", 68, 44);
        createTooltip(22618, "(Defensive)<br>(Magic)<br>(Magic XP)", 68, 44);
        addText(22603, "Scorch", fonts, 0, 0xff9933, false, true);
        addText(22604, "Flare", fonts, 0, 0xff9933, false, true);
        addText(22607, "Blaze", fonts, 0, 0xff9933, false, true);
        addSprite(22600, 868);
        addSprite(22605, 869);
        addSprite(22606, 870);
        salamander.totalChildren(17);
        setBounds(428, -1, 5, 0, salamander);
        setBounds(429, 0, 25, 1, salamander);
        setBounds(433, 21, 46, 2, salamander);
        setBounds(431, 21, 99, 3, salamander);
        setBounds(22603, 38, 75, 4, salamander);
        setBounds(22606, 40, 103, 5, salamander);
        setBounds(12607, 41, 128, 6, salamander);
        setBounds(432, 105, 46, 7, salamander);
        setBounds(22604, 126, 75, 8, salamander);
        setBounds(22600, 40, 50, 9, salamander);
        setBounds(22605, 124, 50, 10, salamander);
        setBounds(22601, 21, 46, 11, salamander);
        setBounds(22602, 105, 46, 12, salamander);
        setBounds(22845, 20, 153, 13, salamander);
        setBounds(24042, 52, 161, 14, salamander);
        setBounds(22850, 20, 153, 15, salamander);
        setBounds(22618, 21, 99, 16, salamander);

        Widget pickAxe = cache[5570];
        setBounds(24029, 105, 46, 5, pickAxe);
        setBounds(24031, 105, 99, 7, pickAxe);
        setBounds(22850, 20, 153, 8, pickAxe);
        Widget pickAxeOptions = cache[5575];
        int spikeTextX = pickAxeOptions.child_x[8];
        int spikeTextY = pickAxeOptions.child_y[8];
        int impaleTextX = pickAxeOptions.child_x[9];
        int impaleTextY = pickAxeOptions.child_y[9];
        int smashTextX = pickAxeOptions.child_x[10];
        int smashTextY = pickAxeOptions.child_y[10];
        int impaleBoxX = pickAxeOptions.child_x[3];
        int impaleBoxY = pickAxeOptions.child_y[3];
        int impaleIconX = pickAxeOptions.child_x[6];
        int impaleIconY = pickAxeOptions.child_y[6];
        int blockTextX = pickAxeOptions.child_x[11];
        int blockTextY = pickAxeOptions.child_y[11];
        int blockIconX = pickAxeOptions.child_x[4];
        int blockIconY = pickAxeOptions.child_y[4];
        int blockBoxX = pickAxeOptions.child_x[1];
        int blockBoxY = pickAxeOptions.child_y[1];
        setBounds(5584, spikeTextX - 1, spikeTextY - 1, 8, pickAxeOptions);
        setBounds(5586, smashTextX, smashTextY - 1, 10, pickAxeOptions);
        setBounds(5585, impaleTextX, impaleTextY - 1, 9, pickAxeOptions);
        setBounds(5579, impaleBoxX + 1, impaleBoxY, 3, pickAxeOptions);
        setBounds(5582, impaleIconX + 1, impaleIconY, 6, pickAxeOptions);
        setBounds(5587, blockTextX + 1, blockTextY - 1, 11, pickAxeOptions);
        setBounds(5580, blockIconX + 1, blockIconY, 4, pickAxeOptions);
        setBounds(5577, blockBoxX + 1, blockBoxY, 1, pickAxeOptions);
        setBounds(22845, 17, 150, 12, pickAxeOptions);

        Widget axe = cache[1698];
        createTooltip(24021, "(Aggressive)<br>(Slash)<br>(Strength XP)", 68, 44);
        setBounds(24021, 105, 46, 5, axe);
        setBounds(24023, 105, 99, 7, axe);
        setBounds(22850, 20, 153, 8, axe);

        Widget axeOptions = cache[1703];
        setBounds(1705, 102, 96, 1, axeOptions);
        setBounds(1707, 102, 43, 3, axeOptions);
        setBounds(1708, 121, 100, 4, axeOptions);
        setBounds(1710, 121, 47, 6, axeOptions);
        setBounds(1712, 17, 72, 8, axeOptions);
        setBounds(1715, 102, 125, 11, axeOptions);
        setBounds(22845, 17, 150, 12, axeOptions);

        Widget mace = cache[3796];
        setBounds(3799, -1, 5, 0, mace);
        setBounds(3800, -1, 25, 1, mace);
        setBounds(24037, 105, 46, 5, mace);
        setBounds(24039, 105, 99, 7, mace);
        setBounds(22850, 20, 153, 8, mace);

        Widget maceOptions = cache[3801];
        setBounds(3803, 102, 96, 1, maceOptions);
        setBounds(3805, 102, 43, 3, maceOptions);
        setBounds(3808, 121, 47, 6, maceOptions);
        setBounds(3807, 37, 47, 5, maceOptions);
        setBounds(3806, 121, 100, 4, maceOptions);
        setBounds(3810, 17, 72, 8, maceOptions);
        setBounds(3811, 102, 72, 9, maceOptions);
        setBounds(3812, 17, 125, 10, maceOptions);
        setBounds(3813, 102, 125, 11, maceOptions);
        setBounds(22845, 17, 150, 12, maceOptions);

        Widget thrown = cache[4446];
        setBounds(4449, -1, 5, 0, thrown);
        setBounds(4450, -1, 25, 1, thrown);
        setBounds(22907, 105, 46, 5, thrown);
        setBounds(22850, 20, 153, 7, thrown);

        Widget thrownOptions = cache[4451];
        setBounds(4453, 102, 43, 1, thrownOptions);
        setBounds(4457, 121, 47, 5, thrownOptions);
        setBounds(4459, 122, 72, 7, thrownOptions);
        setBounds(22845, 17, 150, 9, thrownOptions);

        Widget bow = cache[1764];
        setBounds(1767, -1, 5, 0, bow);
        setBounds(1768, -1, 25, 1, bow);
        setBounds(24014, 105, 46, 5, bow);
        setBounds(22850, 20, 153, 7, bow);

        Widget bowOptions = cache[1769];
        setBounds(1775, 121, 47, 5, bowOptions);
        setBounds(1776, 18, 72, 6, bowOptions);
        setBounds(1777, 102, 72, 7, bowOptions);
        setBounds(1778, 18, 125, 8, bowOptions);
        setBounds(1771, 102, 43, 1, bowOptions);
        setBounds(22845, 17, 150, 9, bowOptions);

        Widget spear = cache[4679];
        setBounds(4683, -1, 25, 1, spear);
        setBounds(22880, 105, 46, 5, spear);
        setBounds(22882, 105, 99, 7, spear);
        setBounds(22850, 20, 153, 8, spear);

        Widget spearOptions = cache[4684];
        setBounds(4686, 102, 96, 1, spearOptions);
        setBounds(4689, 121, 100, 4, spearOptions);
        setBounds(4696, 123, 125, 11, spearOptions);
        setBounds(4688, 102, 43, 3, spearOptions);
        setBounds(4691, 121, 47, 6, spearOptions);
        setBounds(4694, 121, 72, 9, spearOptions);
        setBounds(22845, 17, 150, 12, spearOptions);

        Widget sword1 = cache[2276];
        setBounds(2280, -1, 25, 1, sword1);
        setBounds(22850, 20, 153, 8, sword1);

        Widget swordOptions1 = cache[2281];
        setBounds(2283, 103, 97, 1, swordOptions1);
        setBounds(2286, 122, 101, 4, swordOptions1);
        setBounds(2293, 124, 126, 11, swordOptions1);
        setBounds(2285, 103, 44, 3, swordOptions1);
        setBounds(2288, 122, 48, 6, swordOptions1);
        setBounds(2291, 122, 73, 9, swordOptions1);
        setBounds(22845, 18, 151, 12, swordOptions1);

        Widget sword2 = cache[2423];
        createTooltip(22873, "(Aggressive)<br>(Slash)<br>(Strength XP)", 68, 44);
        createTooltip(22875, "(Defensive)<br>(Slash)<br>(Defence XP)", 68, 44);
        setBounds(2426, -1, 5, 0, sword2);
        setBounds(2427, -1, 25, 1, sword2);
        setBounds(22873, 105, 46, 5, sword2);
        setBounds(22875, 105, 99, 7, sword2);
        setBounds(22850, 20, 153, 8, sword2);

        Widget swordOptions2 = cache[2428];
        setBounds(2430, 103, 97, 1, swordOptions2);
        setBounds(2433, 122, 101, 4, swordOptions2);
        setBounds(2440, 124, 126, 11, swordOptions2);
        setBounds(2432, 103, 44, 3, swordOptions2);
        setBounds(2435, 122, 48, 6, swordOptions2);
        setBounds(2438, 123, 73, 9, swordOptions2);
        setBounds(22845, 18, 151, 12, swordOptions2);

        Widget normalStaff = cache[328];
        setBounds(333, 3, 40, 3, normalStaff);
        setBounds(331, -1, 25, 2, normalStaff);
        setBounds(7474, 18, 200, 7, normalStaff);
        setBounds(22850, 20, 153, 8, normalStaff);

        Widget normalStaffOptions = cache[333];
        setBounds(24115, 17, 113, 16, normalStaffOptions);
        setBounds(24113, 122, 35, 14, normalStaffOptions);
        setBounds(24112, 102, 10, 13, normalStaffOptions);
        configButton(24111, "Choose spell", 820, 821);
        setBounds(24111, 102, 6, 12, normalStaffOptions);
        setBounds(349, 102, 59, 9, normalStaffOptions);
        addSpellSprite(18583, 822);
        setBounds(18583, 124, 64, 10, normalStaffOptions);
        addSpellSprite(24114, 822);
        setBounds(24114, 138, 10, 15, normalStaffOptions);
        setBounds(18584, 123, 88, 11, normalStaffOptions);

        Widget normalStaffTooltips = cache[328];
        createTooltip(24117, "(Accurate)<br>(Crush)<br>(Attack XP)", 71, 36);
        setBounds(24117, 20, 45, 4, normalStaffTooltips);
        createTooltip(24118, "(Aggressive)<br>(Crush)<br>(Strength XP)", 72, 35);
        setBounds(24118, 20, 81, 5, normalStaffTooltips);
        createTooltip(24119, "(Defensive)<br>(Crush)<br>(Defence XP)", 72, 35);
        setBounds(24119, 20, 116, 6, normalStaffTooltips);

        Widget whip = cache[12290];
        setBounds(12293, -1, 5, 0, whip);
        setBounds(12294, -1, 25, 1, whip);
        setBounds(22850, 20, 153, 7, whip);
        setBounds(22852, 105, 46, 5, whip);

        Widget whipOptions = cache[12295];
        setBounds(12297, 102, 43, 1, whipOptions);
        setBounds(12301, 121, 47, 5, whipOptions);
        setBounds(12303, 124, 72, 7, whipOptions);
        setBounds(22845, 17, 150, 9, whipOptions);
        setBounds(12307, 49, 158, 10, whipOptions);

        Widget unarmed = cache[5855];
        setBounds(5857, -1, 5, 0, unarmed);
        setBounds(5858, -1, 25, 1, unarmed);
        setBounds(22850, 20, 153, 7, unarmed);
        setBounds(22848, 105, 46, 5, unarmed);

        Widget unarmedOptions = cache[5859];
        setBounds(5862, 104, 46, 2, unarmedOptions);
        setBounds(5864, 123, 50, 4, unarmedOptions);
        setBounds(5867, 128, 75, 7, unarmedOptions);
        setBounds(22845, 19, 153, 9, unarmedOptions);
        setBounds(22846, 51, 161, 10, unarmedOptions);

        Widget maul = cache[425];
        setBounds(428, -1, 5, 0, maul);
        setBounds(429, -1, 25, 1, maul);
        setBounds(22850, 20, 153, 7, maul);
        setBounds(24045, 105, 46, 5, maul);

        Widget maulOptions = cache[430];
        setBounds(432, 102, 43, 1, maulOptions);
        setBounds(439, 18, 125, 8, maulOptions);
        setBounds(438, 102, 72, 7, maulOptions);
        setBounds(437, 17, 72, 6, maulOptions);
        setBounds(436, 121, 47, 5, maulOptions);
        setBounds(22845, 17, 150, 9, maulOptions);

        Widget halberd = cache[8460];
        setBounds(8464, -1, 25, 1, halberd);
        setBounds(22850, 20, 153, 7, halberd);
        setBounds(22894, 105, 46, 5, halberd);

        Widget halberdOptions = cache[8465];
        setBounds(8468, 104, 44, 2, halberdOptions);
        setBounds(8470, 123, 48, 4, halberdOptions);
        setBounds(8473, 123, 73, 7, halberdOptions);
        setBounds(22845, 19, 151, 9, halberdOptions);

        //Special attack bar
        for (int i = 0; i < SpecialAttackBars.values().length; i++) {
            if (SpecialAttackBars.values()[i].readInterfaceId() != -1) {
                removeSomething(SpecialAttackBars.values()[i].readInterfaceId());
                Widget specBar = addTabInterface(SpecialAttackBars.values()[i].readInterfaceId());
                specBar.totalChildren(2);
                setBounds(SpecialAttackBars.values()[i].getSpecialMeter(), 1, 11, 0, specBar);
                setBounds(12322, 4, 5, 1, specBar);
            }
        }
    }

    public static int weaponId, ammoId;
}
