package com.ferox.cache.graphics.widget;

import com.ferox.Client;
import com.ferox.cache.def.ItemDefinition;
import com.ferox.util.StringUtils;

import static com.ferox.ClientConstants.PVP_ALLOWED_SPAWNS;

/**
 * @author Patrick van Elderen | May, 29, 2021, 03:05
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class SpawnTabAllItems {

    public static String searchSyntax = "";
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

            //Reset search results
            for (int i = 0; i < searchResults.length; i++) {
                searchResults[i] = -1;
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
            }

            //Draw results onto interface
            //Reset text on interface..
            for (int i = 71031; i < 72475; i++) {
                Widget w = Widget.cache[i];
                w.drawingDisabled = true;
            }

            //Send new text on interface..
            int interface_ = 71031;
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
                if (interface_ == 32475) {
                    break;
                }
            }

            //Update scroll bar
            Widget.cache[71030].scrollMax = results.length * 30;

            fetchSearchResults = false;
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

}
