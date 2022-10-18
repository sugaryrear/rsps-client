package com.ferox.cache.graphics.widget;

import com.ferox.Client;
import com.ferox.cache.def.ItemDefinition;
import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.util.StringUtils;

import static com.ferox.ClientConstants.PVP_ALLOWED_SPAWNS;

/**
 * @author Patrick van Elderen | May, 29, 2021, 02:51
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class SpawnTab extends Widget {

    public static String searchSyntax = "";
    public static int lastLength = 0;
    public static int[] searchResults = new int[1000];
    public static boolean fetchSearchResults;
    public static boolean searchingSpawnTab;
    public static SpawnTabType spawnType = SpawnTabType.INVENTORY;

    public static void processSpawnTab() {
        //Draw checks..
        switch(spawnType) {
            case INVENTORY:
                //Inventory ticks
                Widget.cache[72007].disabledSprite = Client.spriteCache.get(332);
                Widget.cache[72007].enabledSprite = Client.spriteCache.get(332);
                Widget.cache[72009].enabledSprite = Client.spriteCache.get(333); //Hover

                //Bank ticks
                Widget.cache[72011].disabledSprite = Client.spriteCache.get(335);
                Widget.cache[72011].enabledSprite = Client.spriteCache.get(334); //Hover
                Widget.cache[72013].enabledSprite = Client.spriteCache.get(335);
                break;
            case BANK:
                //Bank ticks
                Widget.cache[72011].disabledSprite = Client.spriteCache.get(332);
                Widget.cache[72011].enabledSprite = Client.spriteCache.get(332);
                Widget.cache[72013].enabledSprite = Client.spriteCache.get(333); //Hover

                //Inventory ticks
                Widget.cache[72007].disabledSprite = Client.spriteCache.get(335);
                Widget.cache[72007].enabledSprite = Client.spriteCache.get(334); //Hover
                Widget.cache[72009].enabledSprite = Client.spriteCache.get(335);
                break;
        }

        if (fetchSearchResults) {
            //System.out.println("In fetchSearchResults");
            //Reset search results
            for (int i = 0; i < searchResults.length; i++) {
                searchResults[i] = -1;
            }
            if (searchSyntax.length() != lastLength) {
                //System.out.println("Search length isn't last length, reset the strings");
                Widget.cache[72003].defaultText = "Enter an item name.";
                Widget.cache[72003].defaultInputFieldText = "Enter an item name.";
                for (int i = 72031; i < 73475; i++) {
                    Widget w = Widget.cache[i];
                    w.drawingDisabled = true;
                    w.defaultText = "";
                }
            }
            if (searchSyntax.length() == 0) {
                Widget.cache[72030].scrollMax = 0;
            }
            //Get new search results
            int totalResults = 0;
            if (searchSyntax.length() >= 2) {
                for (int itemId : PVP_ALLOWED_SPAWNS) {
                    final ItemDefinition def = ItemDefinition.get(itemId);

                    if (def == null || def.name == null || def.noted_item_id != -1) {
                        continue;
                    }

                    if (def.name.toLowerCase().contains(searchSyntax)) {
                        searchResults[totalResults++] = def.id;
                    }
                }
                //Draw results onto interface
                //Reset text on interface..
                //for (int i = 72031; i < 73475; i++) {
                //    Widget w = Widget.interfaceCache[i];
                //    w.drawingDisabled = true;
                //}

                //Send new text on interface..
                int interface_ = 72031;
                final int[] results = getResultsArray();

                for (int def : results) {
                    if (def == -1) {
                        continue;
                    }
                    Widget w = Widget.cache[interface_];
                    w.drawingDisabled = false;
                    ItemDefinition item = ItemDefinition.get(def);
                    if (item == null || item.name == null) {
                        System.err.println("Item " + def + " has null");
                        continue;
                    }
                    String itemName = item.name;
                    if (itemName.length() > 22) {
                        itemName = itemName.substring(0, 22);
                        itemName += "..";
                    }
                    w.defaultText = itemName;
                    interface_++;
                    if (interface_ == 72475) {
                        break;
                    }
                }

                //Update scroll bar
                Widget.cache[72030].scrollMax = results.length * 30;

                fetchSearchResults = false;
            }
            lastLength = searchSyntax.length();
        }

        //Draw input
        String textInput = "";
        if (searchSyntax.length() > 0) {
            textInput = StringUtils.formatText(searchSyntax);
        }

        if (Client.game_tick % 25 < 10) {
            textInput += "|";
        }

        Widget.cache[72003].defaultText = textInput;
    }

    public static int[] getResultsArray() {
        return searchSyntax.length() >= 2 ? searchResults : PVP_ALLOWED_SPAWNS;
    }

    static void unpack(AdvancedFont[] tda) {
        Widget tab = addTabInterface(72000);

        addText(72002, "Spawn Tab", tda, 2, 0xFFFFFF, true, true);
        addText(72003, "Item", tda, 1, 0xff8000, false, true);

        addHoverButton(72004, 330, 172, 20, "Search", -1, 72005, 1);
        addHoveredButton(72005, 331, 172, 20, 72006);

        //Inventory spawn
        addText(72010, "Inventory:", tda, 0, 0xFFFFFF, false, true);
        addHoverButton(72007, 332, 14, 15, "Select", -1, 72008, 1);
        addHoveredButton(72008, 333, 14, 15, 72009);

        //Bank spawn
        addText(72014, "Bank:", tda, 0, 0xFFFFFF, false, true);
        addHoverButton(72011, 332, 14, 15, "Select", -1, 72012, 1);
        addHoveredButton(72012, 333, 14, 15, 72013);

        addHoverButton(72015, 1830, 79, 30, "Presets", -1, 61016, 1);
        addHoveredButton(72016, 1831, 79, 30, 61017);

        cache[72015].optionType = 0;
        cache[72015].actions = new String[]{"Open Presets", "Load Last Preset"};

        addSpriteLoader(72001, 196);
        tab.totalChildren(14);

        tab.child(0, 72001, 0, 89);
        tab.child(1, 72030, 0, 91);
        tab.child(2, 72002, 95, 1);
        tab.child(3, 72004, 10, 25);
        tab.child(4, 72005, 10, 25);
        tab.child(5, 72003, 15, 28);
        tab.child(6, 72007, 75, 50);
        tab.child(7, 72008, 75, 50);
        tab.child(8, 72010, 11, 52);
        tab.child(9, 72011, 75, 70);
        tab.child(10, 72012, 75, 70);
        tab.child(11, 72014, 11, 72);
        tab.child(12, 72015, 103, 52);
        tab.child(13, 72016, 103, 52);

        //Text area
        Widget list = addTabInterface(72030);
        list.totalChildren(1444);

        int child = 0;
        for (int i = 72031, yPos = 0; i < 73475; i++, yPos += 22) {
            addHoverText(i, "", null, tda, 1, 0xff8000, false, true, 240, 0xFFFFFF);
            cache[i].actions = new String[]{"Spawn", "Spawn X"};
            list.children[child] = i;
            list.child_x[child] = 5;
            list.child_y[child] = yPos;
            child++;
        }

        list.height = 154;
        list.width = 174;
        list.scrollMax = 2200;
    }
}
