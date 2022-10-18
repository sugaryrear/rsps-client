package com.ferox.cache.graphics.widget.impl;

import com.ferox.Client;
import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.Widget;

/**
 * This class represents the world's teleporting manager.
 *
 * @author Zerikoth | 27 okt. 2020 : 16:12
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>}
 */
public class TeleportWidget extends Widget {

    public static void unpack(AdvancedFont[] font) {
        teleportInterface(font);
    }

    private static void teleportInterface(AdvancedFont[] font) {
        //To reduce the font size used by the teleport interface,
        //change the addText idx in handleTeleportTab and teleportInterface to 0.
        //Teleport names (for spacing) would likely need to be changed as well.
        Widget main = addInterface(29050);
        addSpriteLoader(29051, 974);
        addHoverButton(29063, 981, 115, 24, "World Boss", -1, 29064, 1);
        addHoveredButton(29064, 982, 115, 24, 29065);
        addSpriteLoader(29066, 990);
        closeButton(29175, 107,108,false);
        addText(29913, "World Boss", font, 1, 0xff981f, true, true);

        main.totalChildren(22);
        main.child(0, 29051, 7, 7);
        main.child(8, 29090, 216 - 100, 62);
        main.child(9, 29063, 19, 281);
        main.child(10, 29064, 19, 281);
        main.child(11, 29066, 150, 273);
        main.child(19, 29078, 213, 17);
        main.child(20, 29175, 478, 13);
        main.child(21, 29913, 75,285);

        final int[] CATEGORY_IDS = new int[]{991, 985, 986, 987, 980, 989, 988};
        final int[] CATEGORY_IDS_HOVER = new int[]{1874, 1875, 1876, 1877, 1878, 1879, 1880};
        final String[] CATEGORY_NAMES = new String[]{"Favourite", "Recent", "PvP", "PvM", "Bossing", "Minigames", "Other"};
        int childStart = 1;
        int yPos = 64;
        for (int i = 0; i < CATEGORY_IDS.length; i++) {
     //       addButton(29055 + i, CATEGORY_IDS[i], "Select");
            hoverButton10(29055 + i,"Select", CATEGORY_IDS[i], CATEGORY_IDS_HOVER[i], "", font, 1, 0xdb9c22,0xdb9c22,true);

            main.child(childStart, 29055 + i, 19, yPos);
            //System.out.println("Added child start to: " + childStart);
            childStart++;
            yPos += 30;
        }
        int childStart1 = 12;
        int yPos1 = 70;
        for (int i = 0; i < CATEGORY_NAMES.length; i++) { // could of been done in the loop above, but i did it after i added plenty of other stuff(by mistake) so i would of had to re-order all the ids.
            addText(29070 + i, CATEGORY_NAMES[i], font, 1, 0xffffff, false, false);
            main.child(childStart1, 29070 + i, 50, yPos1);
            childStart1++;
            yPos1 += 30;
        }
        String[] names = new String[]{"Some Npc", ""};
        addText(29078, "World Teleports - Favourite", font, 2, 0xff981f, false, true); //Favorite is the default tab.

        //addSpriteArray(29079, sprites, "Teleport", 4, 83, 68);


        Widget scroll = addTabInterface(29090);
        scroll.width = 265 + 100;
        scroll.height = 208;
        scroll.scrollMax = 209;

        //Sample teleports when first loading the interface, this is a copy of the PVP teleports:
        /*
        //scroll.scrollMax = 410;
        int[] sprites = new int[]{1001, 1000, 999, 998, 997, 996, 995, 994, 993, 992};
        String[] teleportNames = new String[] {"      ", "          Ay", "      Ye nice", " Looks good", "Rip cub", "Bit sad init", "Ye fella", "Coderfella", "Hi coders", "Hi coder"};
        System.out.println("Length of names: " + teleportNames.length + " | Sprites length: " + sprites.length);
        //long totalUsedSprites = Arrays.stream(displayedTeleportSprites).filter(id -> id != -1).count();
        // System.out.println("Total used sprites: " + totalUsedSprites);
        scroll.totalChildren(sprites.length * 2);


        int x = 35;
        int y = 0;
        for (int i = 0; i < sprites.length; i++) {
            if (i % 4 == 0 && i != 0) {
                x = 35;
                y += 68;
            } else {
                if (i != 0) {
                    x += 83;
                }
            }

            addButton(29095 + i, sprites[i], "Teleport");
            scroll.child(i, 29095 + i, x, y);
        }
        int x1 = 38;
        int y1 = 50;
        for (int i = 0; i < teleportNames.length; i++) {
            int textWidth = font[1].get_width(teleportNames[i]);
            if (i % 4 == 0 && i != 0) {
                x1 = 38;
                y1 += 68;
            } else {
                if (i != 0) {
                    x1 += 83;
                }
            }

            addText(29195 + i, teleportNames[i], font, 1, 0xffffff, false, false);
            scroll.child(10 + i, 29195 + i, x1, y1);
        }
        */
    }
    private static int[] newIDS = null;
    private static final int TELEPORT_CATEGORY_PVP = 1;
    private static final int TELEPORT_CATEGORY_PVM = 2;
    private static final int TELEPORT_CATEGORY_BOSSING = 3;
    private static final int TELEPORT_CATEGORY_MINIGAMES = 4;
    private static final int TELEPORT_CATEGORY_OTHER = 5;
    public static final int STARTING_IMAGE_INDEX = 29095;
    public static final int ENDING_IMAGE_INDEX = 29135; // increment if needed

    /**
     * This method handles the teleports for the teleportation interface.
     * @param index The teleport category index.
     */
    public static void handleTeleportTab(int index) {
        //To reduce the font size used by the teleport interface,
        //change the addText idx in handleTeleportTab and teleportInterface to 0.
        //Teleport names (for spacing) would likely need to be changed as well.
        Widget widget = Widget.cache[29090];
        int xPos = 0;
        int yPos = 0;
        String[] newNames = null;
        if (index == TELEPORT_CATEGORY_PVP) {
            newIDS = new int[]{1001, 1000, 999, 998, 997, 995, 994, 993, 992, 1101, 1759};
            newNames = new String[]{" Bandit Camp", "Chaos Temple", "Demonic Ruins", "East Dragons", "  Graveyard", "   Magebank", "   Rev Caves", "    The Gate", "West Dragons", "  Black Chins", "Forbidden For"};
            widget.totalChildren(newIDS.length * 2);
            //System.out.println("Widget length: " + widget.children.length);
            xPos = 35;
            yPos = 0;
            for (int i = 0; i < newIDS.length; i++) {
                if (i % 4 == 0 && i != 0) {
                    xPos = 35;
                    yPos += 68;
                } else {
                    if (i != 0) {
                        xPos += 83;
                    }
                }

                Widget.addButtonWithMenu(STARTING_IMAGE_INDEX + i, newIDS[i], new String[]{"Teleport", "Add to favourites", null, null, null});
                Widget.cache[STARTING_IMAGE_INDEX + i].width = 40;
                Widget.cache[STARTING_IMAGE_INDEX + i].height = 65;
                widget.child(i, STARTING_IMAGE_INDEX + i, xPos, yPos);
            }

            int x1 = 38;
            int y1 = 50;
            AdvancedFont[] fonts = {Client.adv_font_small, Client.adv_font_regular, Client.adv_font_bold, Client.adv_font_fancy};
            for (int i = 0; i < newNames.length; i++) {
                if (i % 4 == 0 && i != 0) {
                    x1 = 38;
                    y1 += 68;
                } else {
                    if (i != 0) {
                        x1 += 83;
                    }
                }

                Widget.addText(29195 + i, newNames[i], fonts, 1, 0xffffff, false, false);
                widget.child(newIDS.length + i, 29195 + i, x1, y1);
            }
        } else if (index == TELEPORT_CATEGORY_PVM) {
            newIDS = new int[]{1008, 1007, 1006, 1005, 1004, 1003, 1002, 1043, 1092, 1095, 1094, 1090, 1158, 1159, 1160, 1161, 1162, 1163, 1190, 1009, 1448, 1449, 1814, 1818, 1820};
            newNames = new String[]{"        Cows", " Dagannoths", " Experiments", "  Lizardmen", "  Rock Crabs", "Skele Wyverns", "        Yaks", "Smoke Devils", " Slayer Tower", "Brimhaven Du", " Taverley Du", " Catacombs", "  Sand Crabs", "  Fire Giants", "Slayer Strong", "Rellekka Du", "  Dark Beast", "Kalphite Lair", "Ancient Cave", "Wyvern Cave", "Karuulm Du", "    Lithkren", "Lumbridge Sw", "Brine Rat Cav", "Mos Le'Harm"};
            widget.totalChildren(newIDS.length * 2);
            //System.out.println("Widget length: " + widget.children.length);
            xPos = 35;
            yPos = 0;
            for (int i = 0; i < newIDS.length; i++) {
                if (i % 4 == 0 && i != 0) {
                    xPos = 35;
                    yPos += 68;
                } else {
                    if (i != 0) {
                        xPos += 83;
                    }
                }

                Widget.addButtonWithMenu(STARTING_IMAGE_INDEX + i, newIDS[i], new String[]{"Teleport", "Add to favourites", null, null, null});
                Widget.cache[STARTING_IMAGE_INDEX + i].width = 40;
                Widget.cache[STARTING_IMAGE_INDEX + i].height = 65;
                widget.child(i, STARTING_IMAGE_INDEX + i, xPos, yPos);
            }

            int x1 = 38;
            int y1 = 50;
            AdvancedFont[] fonts = {Client.adv_font_small, Client.adv_font_regular, Client.adv_font_bold, Client.adv_font_fancy};
            for (int i = 0; i < newNames.length; i++) {
                if (i % 4 == 0 && i != 0) {
                    x1 = 38;
                    y1 += 68;
                } else {
                    if (i != 0) {
                        x1 += 83;
                    }
                }

                Widget.addText(29195 + i, newNames[i], fonts, 1, 0xffffff, false, false);
                widget.child(newIDS.length + i, 29195 + i, x1, y1);
            }

        } else if (index == TELEPORT_CATEGORY_BOSSING) {
            newIDS = new int[]{1031, 1030, 1029, 1028, 1027, 1025, 1024, 1023, 1021, 1020, 1018, 1017, 1016, 1042, 1044, 1015, 1104, 1019, 1022, 1447, 1758, 1795, 1812, 1819};
            newNames = new String[]{"  Callisto", "   Cerberus", "   Chaos Fan", "  Corp Beast", "  Crazy Arch", "Demon Gorillas", "         GWD", "         KBD", "     Kraken", "     Shaman", "     Thermo", "   Venenatis", "      Vet'ion", "      Scorpia", "   Chaos Ele", "      Zulrah", "   Vorkath", "   World boss", "          KQ", "Giant Mole", "Alchy Hydra", "Barrelchest", "Corrupted Ne", "Raids Area"};
            widget.totalChildren(newIDS.length * 2);
            //System.out.println("Widget length: " + widget.children.length);
            xPos = 35;
            yPos = 0;
            for (int i = 0; i < newIDS.length; i++) {
                if (i % 4 == 0 && i != 0) {
                    xPos = 35;
                    yPos += 68;
                } else {
                    if (i != 0) {
                        xPos += 83;
                    }
                }

                Widget.addButtonWithMenu(STARTING_IMAGE_INDEX + i, newIDS[i], new String[]{"Teleport", "Add to favourites", null, null, null});
                Widget.cache[STARTING_IMAGE_INDEX + i].width = 40;
                Widget.cache[STARTING_IMAGE_INDEX + i].height = 65;
                widget.child(i, STARTING_IMAGE_INDEX + i, xPos, yPos);
            }

            int x1 = 38;
            int y1 = 50;
            AdvancedFont[] fonts = {Client.adv_font_small, Client.adv_font_regular, Client.adv_font_bold, Client.adv_font_fancy};
            for (int i = 0; i < newNames.length; i++) {
                if (i % 4 == 0 && i != 0) {
                    x1 = 38;
                    y1 += 68;
                } else {
                    if (i != 0) {
                        x1 += 83;
                    }
                }

                Widget.addText(29195 + i, newNames[i], fonts, 1, 0xffffff, false, false);
                widget.child(newIDS.length + i, 29195 + i, x1, y1);
            }

        } else if (index == TELEPORT_CATEGORY_MINIGAMES) {
            newIDS = new int[]{1014, 1012, 1011, 1808, 1804};
            newNames = new String[]{"     Barrows", "    Fight Cave", "   Magebank", "Warriors Guild", "Pest Control"};
            widget.totalChildren(newIDS.length * 2);
            //System.out.println("Widget length: " + widget.children.length);
            xPos = 35;
            yPos = 0;
            for (int i = 0; i < newIDS.length; i++) {
                if (i % 4 == 0 && i != 0) {
                    xPos = 35;
                    yPos += 68;
                } else {
                    if (i != 0) {
                        xPos += 83;
                    }
                }

                Widget.addButtonWithMenu(STARTING_IMAGE_INDEX + i, newIDS[i], new String[]{"Teleport", "Add to favourites", null, null, null});
                Widget.cache[STARTING_IMAGE_INDEX + i].width = 40;
                Widget.cache[STARTING_IMAGE_INDEX + i].height = 65;
                widget.child(i, STARTING_IMAGE_INDEX + i, xPos, yPos);
            }

            int x1 = 38;
            int y1 = 50;
            AdvancedFont[] fonts = {Client.adv_font_small, Client.adv_font_regular, Client.adv_font_bold, Client.adv_font_fancy};
            for (int i = 0; i < newNames.length; i++) {
                if (i % 4 == 0 && i != 0) {
                    x1 = 38;
                    y1 += 68;
                } else {
                    if (i != 0) {
                        x1 += 83;
                    }
                }

                Widget.addText(29195 + i, newNames[i], fonts, 1, 0xffffff, false, false);
                widget.child(newIDS.length + i, 29195 + i, x1, y1);
            }
        } else if (index == TELEPORT_CATEGORY_OTHER) {
            newIDS = new int[]{1093, 1096, 1097, 1116, 1450, 1816, 1817, 1806, 1798, 1815, 1807, 1799, 1796, 1794, 1797, 1802, 1810, 1801, 1803, 1809, 1101, 1805};
            newNames = new String[]{"Port Piscaril", "Gnome Agility", " Barb Agility", "Farming Area", "Catherby", "Karamja", "Lunar Isle", "Tzhaar City", "Edgevile", "Lumbridge", "Varrock", "Falador", "Camelot", "Ardougne", "Canifis", "Keldagrim", "Yanille", "Fishing Areas", "Mining Areas", "Woodcutting", "Hunter Areas", "Smithing Anvil"};
            widget.totalChildren(newIDS.length * 2);
            //System.out.println("Widget length: " + widget.children.length);
            xPos = 35;
            yPos = 0;
            for (int i = 0; i < newIDS.length; i++) {
                if (i % 4 == 0 && i != 0) {
                    xPos = 35;
                    yPos += 68;
                } else {
                    if (i != 0) {
                        xPos += 83;
                    }
                }

                Widget.addButtonWithMenu(STARTING_IMAGE_INDEX + i, newIDS[i], new String[]{"Teleport", "Add to favourites", null, null, null});
                Widget.cache[STARTING_IMAGE_INDEX + i].width = 40;
                Widget.cache[STARTING_IMAGE_INDEX + i].height = 65;
                widget.child(i, STARTING_IMAGE_INDEX + i, xPos, yPos);
            }

            int x1 = 38;
            int y1 = 50;
            AdvancedFont[] fonts = {Client.adv_font_small, Client.adv_font_regular, Client.adv_font_bold, Client.adv_font_fancy};
            for (int i = 0; i < newNames.length; i++) {
                if (i % 4 == 0 && i != 0) {
                    x1 = 38;
                    y1 += 68;
                } else {
                    if (i != 0) {
                        x1 += 83;
                    }
                }

                Widget.addText(29195 + i, newNames[i], fonts, 1, 0xffffff, false, false);
                widget.child(newIDS.length + i, 29195 + i, x1, y1);
            }
        }
        if (newIDS.length > 12) {
            int offset = 69;
            int columns = yPos;
            widget.scrollMax = columns + offset;
        } else {
            widget.scrollMax = widget.height + 1;
        }

    }

    public static void updateTeleportsTab(boolean recent) {
        Widget widget = Widget.cache[29090];

        widget.totalChildren(Client.singleton.teleportSprites.length * 2);
        System.out.println("Widget length: " + widget.children.length);
        int xPos = 35;
        int yPos = 0;
        for (int i = 0; i < Client.singleton.teleportSprites.length; i++) {
            if (i % 4 == 0 && i != 0) {
                xPos = 35;
                yPos += 68;
            } else {
                if (i != 0) {
                    xPos += 83;
                }
            }
            if (Client.singleton.teleportCategoryIndex == 1) {
                Widget.addButtonWithMenu(TeleportWidget.STARTING_IMAGE_INDEX + i, Client.singleton.teleportSprites[i], new String[]{"Teleport", null, "Remove from favourites", null, null});
                Widget.cache[STARTING_IMAGE_INDEX + i].width = 40;
                Widget.cache[STARTING_IMAGE_INDEX + i].height = 65;
            } else if (Client.singleton.teleportCategoryIndex == 2) {
                //The recent tab doesn't have add to favorite working properly,
                //it adds the matching position of the tab of the teleport
                //so if your last teleport was 4th PvP teleport it will add the 1st PvP teleport.
                //Until this is fixed, we will prevent "Add to favourites" from appearing on Recent tab.
                Widget.addButtonWithMenu(TeleportWidget.STARTING_IMAGE_INDEX + i, Client.singleton.teleportSprites[i], new String[]{"Teleport", null, null, null, null});
                Widget.cache[STARTING_IMAGE_INDEX + i].width = 40;
                Widget.cache[STARTING_IMAGE_INDEX + i].height = 65;
            } else {
                Widget.addButtonWithMenu(TeleportWidget.STARTING_IMAGE_INDEX + i, Client.singleton.teleportSprites[i], new String[]{"Teleport", "Add to favourites", null, null, null});
                Widget.cache[STARTING_IMAGE_INDEX + i].width = 40;
                Widget.cache[STARTING_IMAGE_INDEX + i].height = 65;
            }
            widget.child(i, TeleportWidget.STARTING_IMAGE_INDEX + i, xPos, yPos);
        }

        int x1 = 38;
        int y1 = 50;
        AdvancedFont[] fonts = {Client.adv_font_small, Client.adv_font_regular, Client.adv_font_bold, Client.adv_font_fancy};
        for (int i = 0; i < Client.singleton.teleportNames.length; i++) {
            if (i % 4 == 0 && i != 0) {
                x1 = 38;
                y1 += 68;
            } else {
                if (i != 0) {
                    x1 += 83;
                }
            }

            Widget.addText(29195 + i, Client.singleton.teleportNames[i], fonts, 1, 0xffffff, false, false);
            widget.child(Client.singleton.teleportSprites.length + i, 29195 + i, x1, y1);
        }

        Widget scroll = Widget.cache[29090];
        int scrollMax = scroll.height + 1;
        if (Client.singleton.teleportSprites != null && Client.singleton.teleportSprites.length > 12) {
            int offset = 69;
            int columns = Client.singleton.teleportSprites.length / 4;
            scrollMax = (offset * columns) + offset;
        }
        scroll.scrollMax = scrollMax;
    }
}
