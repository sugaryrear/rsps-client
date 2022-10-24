package com.ferox.cache.graphics.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.ferox.Client;
import com.ferox.ClientConstants;
import com.ferox.cache.Archive;
import com.ferox.cache.anim.Animation;
import com.ferox.cache.def.ItemDefinition;
import com.ferox.cache.def.NpcDefinition;
import com.ferox.cache.graphics.SimpleImage;
import com.ferox.cache.graphics.dropdown.Dropdown;
import com.ferox.cache.graphics.dropdown.DropdownMenu;
import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.impl.*;
import com.ferox.collection.TempCache;
import com.ferox.entity.model.Model;
import com.ferox.io.Buffer;
import com.ferox.util.Utils;
import com.ferox.util.StringUtils;

/**
 * Previously known as RSInterface, which is a class used to create and show
 * game interfaces.
 */
public class Widget {

    public static final int OPTION_OK = 1;
    public static final int OPTION_USABLE = 2;
    public static final int OPTION_CLOSE = 3;
    public static final int OPTION_TOGGLE_SETTING = 4;
    public static final int OPTION_RESET_SETTING = 5;
    public static final int OPTION_CONTINUE = 6;
    public static final int OPTION_DROPDOWN = 7;

    public static final int TYPE_CONTAINER = 0;
    public static final int TYPE_MODEL_LIST = 1;
    public static final int TYPE_INVENTORY = 2;
    public static final int TYPE_RECTANGLE = 3;
    public static final int TYPE_TEXT = 4;
    public static final int TYPE_SPRITE = 5;
    public static final int TYPE_MODEL = 6;
    public static final int TYPE_ITEM_LIST = 7;
    public static final int TYPE_OTHER = 8;
    public static final int TYPE_HOVER = 9;
    public static final int TYPE_CONFIG = 10;
    public static final int TYPE_CONFIG_HOVER = 11;
    public static final int TYPE_SLIDER = 12;
    public static final int TYPE_DROPDOWN = 13;
    public static final int TYPE_ROTATING = 14;
    public static final int TYPE_KEYBINDS_DROPDOWN = 15;
    public static final int TYPE_INPUT_FIELD = 16;
    public static final int TYPE_ADJUSTABLE_CONFIG = 17;
    public static final int TYPE_BOX = 18;
    public static final int CLICKABLE_SPRITES = 20;
    public static final int TYPE_SPELL_SPRITE = 21;
    public static final int DARKEN = 22;
    public static final int OUTLINE = 23;
    public static final int LINE = 24;
    public static final int TYPE_CONFIG_BUTTON_HOVERED_SPRITE_OUTLINE = 25;
    public static final int COLOR = 26;
    public static final int DRAW_LINE = 27;
    public static final int TYPE_RADIO_BUTTON = 28;

    public boolean hide;

    public void swapInventoryItems(int itemId, int itemAmount) {
        int id = inventoryItemId[itemId];
        inventoryItemId[itemId] = inventoryItemId[itemAmount];
        inventoryItemId[itemAmount] = id;
        id = inventoryAmounts[itemId];
        inventoryAmounts[itemId] = inventoryAmounts[itemAmount];
        inventoryAmounts[itemAmount] = id;
    }

    public static void addToItemGroup(int id, int w, int h, int x, int y, boolean hasActions, boolean displayAmount, String[] actions) {
        Widget widget = addInterface(id);
        widget.width = w;
        widget.height = h;
        widget.inventoryItemId = new int[w * h];
        widget.inventoryAmounts = new int[w * h];
        widget.usableItems = false;
        widget.displayAmount = displayAmount;
        widget.inventoryMarginX = x;
        widget.inventoryMarginY = y;
        widget.inventoryOffsetX = new int[20];
        widget.inventoryOffsetY = new int[20];
        widget.sprites = new SimpleImage[20];
        // rsi.actions = new String[5];
        if (hasActions)
            widget.actions = actions;
        widget.type = TYPE_INVENTORY;
    }
    public static void hoverButton10(int id, String tooltip, int enabledSprite, int disabledSprite, String buttonText,
                                        AdvancedFont tda[], int idx, int colour, int hoveredColour, boolean centerText) {
        Widget tab = addInterface(id);
        tab.tooltip = tooltip;
        tab.optionType = 1;
        tab.parent = id;
        tab.type = TYPE_HOVER;//9
        tab.contentType = 0;
        tab.opacity = (byte) 0;
        tab.hoverType = 52;
        tab.enabledSprite = Client.spriteCache.get(disabledSprite);
        tab.disabledSprite = Client.spriteCache.get(enabledSprite);
        tab.width = tab.disabledSprite.width;
        tab.height = tab.enabledSprite.height;
        tab.msgX = (tab.width / 2);
        tab.msgY = (tab.height / 2) + 4;
        tab.defaultText = buttonText;
        tab.active = false;
        tab.toggled = false;
        tab.textShadow = true;
        tab.text_type = tda[idx];
        tab.textColour = colour;
        tab.defaultHoverColor = hoveredColour;
        tab.centerText = centerText;
        tab.spriteOpacity = 255;
    }
    public void setHidden(boolean hide) {
        this.hide = hide;
    }

    private static void printDuplicates() {
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < cache.length; i++) {
            if (cache[i] != null) {
                if (cache[i].id == 23995) {
                    //System.out.println("Found 23995");
                }
            }
        }
        ids.stream().filter(i -> Collections.frequency(ids, i) > 1)
            .collect(Collectors.toSet()).forEach(System.out::println);
    }

    public static void load(Archive interfaces, AdvancedFont font[], Archive graphics) {
        //TODO: maybe we should maybe try TempCache(1000) instead of TempCache(50000) for lower memory usage?
        spriteCache = new TempCache(50000);
        Buffer buffer = new Buffer(interfaces.get("data"));
        int defaultParentId = -1;
        buffer.readUShort();
        //Change this value, if you need more interfaces.
        cache = new Widget[90000];

        while (buffer.pos < buffer.payload.length) {
            int interfaceId = buffer.readUShort();
            if (interfaceId == 65535) {
                defaultParentId = buffer.readUShort();
                interfaceId = buffer.readUShort();
            }

            Widget widget = cache[interfaceId] = new Widget();
            widget.id = interfaceId;
            widget.parent = defaultParentId;
            widget.type = buffer.readUByte();
            widget.optionType = buffer.readUByte();
            widget.contentType = buffer.readUShort();
            widget.width = buffer.readUShort();
            widget.height = buffer.readUShort();
            widget.opacity = (byte) buffer.readUByte();
            widget.hoverType = buffer.readUByte();
            if (widget.hoverType != 0)
                widget.hoverType = (widget.hoverType - 1 << 8) + buffer.readUByte();
            else
                widget.hoverType = -1;
            int operators = buffer.readUByte();
            if (operators > 0) {
                widget.valueCompareType = new int[operators];
                widget.requiredValues = new int[operators];
                for (int index = 0; index < operators; index++) {
                    widget.valueCompareType[index] = buffer.readUByte();
                    widget.requiredValues[index] = buffer.readUShort();
                }

            }
            int scripts = buffer.readUByte();
            if (scripts > 0) {
                widget.valueIndexArray = new int[scripts][];
                for (int script = 0; script < scripts; script++) {
                    int instructions = buffer.readUShort();
                    widget.valueIndexArray[script] = new int[instructions];
                    for (int instruction = 0; instruction < instructions; instruction++)
                        widget.valueIndexArray[script][instruction] = buffer.readUShort();

                }

            }
            if (widget.type == TYPE_CONTAINER) {
                widget.drawsTransparent = false;
                widget.scrollMax = buffer.readUShort();
                widget.invisible = buffer.readUByte() == 1;
                int length = buffer.readUShort();

                if (widget.id == 5608) {

                    widget.children = new int[PRAYER_INTERFACE_CHILDREN];
                    widget.child_x = new int[PRAYER_INTERFACE_CHILDREN];
                    widget.child_y = new int[PRAYER_INTERFACE_CHILDREN];

                    for (int index = 0; index < length; index++) {
                        widget.children[BEGIN_READING_PRAYER_INTERFACE + index] = buffer.readUShort();
                        widget.child_x[BEGIN_READING_PRAYER_INTERFACE + index] = buffer.readShort();
                        widget.child_y[BEGIN_READING_PRAYER_INTERFACE + index] = buffer.readShort();
                    }

                } else {


                    widget.children = new int[length];
                    widget.child_x = new int[length];
                    widget.child_y = new int[length];

                    for (int index = 0; index < length; index++) {
                        widget.children[index] = buffer.readUShort();
                        widget.child_x[index] = buffer.readShort();
                        widget.child_y[index] = buffer.readShort();
                    }
                }
            }
            if (widget.type == TYPE_MODEL_LIST) {
                buffer.readUShort();
                buffer.readUByte();
            }
            if (widget.type == TYPE_INVENTORY) {
                widget.inventoryItemId = new int[widget.width * widget.height];
                widget.inventoryAmounts = new int[widget.width * widget.height];
                widget.itemOpacity = new int[widget.width * widget.height];
                widget.allowSwapItems = buffer.readUByte() == 1;
                widget.hasActions = buffer.readUByte() == 1;
                widget.usableItems = buffer.readUByte() == 1;
                widget.replaceItems = buffer.readUByte() == 1;
                widget.inventoryMarginX = buffer.readUByte();
                widget.inventoryMarginY = buffer.readUByte();
                widget.inventoryOffsetX = new int[20];
                widget.inventoryOffsetY = new int[20];
                widget.sprites = new SimpleImage[20];
                for (int j2 = 0; j2 < 20; j2++) {
                    int k3 = buffer.readUByte();
                    if (k3 == 1) {
                        widget.inventoryOffsetX[j2] = buffer.readShort();
                        widget.inventoryOffsetY[j2] = buffer.readShort();
                        String s1 = buffer.readString();
                        if (graphics != null && s1.length() > 0) {
                            int i5 = s1.lastIndexOf(",");

                            int index = Integer.parseInt(s1.substring(i5 + 1));

                            String name = s1.substring(0, i5);

                            widget.sprites[j2] = getSprite(index, graphics, name);
                        }
                    }
                }
                widget.actions = new String[5];
                for (int actionIndex = 0; actionIndex < 5; actionIndex++) {
                    widget.actions[actionIndex] = buffer.readString();
                    if (widget.actions[actionIndex].length() == 0)
                        widget.actions[actionIndex] = null;
                    if (widget.parent == 1644)
                        widget.actions[2] = "Operate";
                    //Replace Buy/Sell 50 as an update of jan 25 2021
                    if (widget.parent == 3824) {
                        widget.actions[4] = "Buy X";
                    }
                    if (widget.parent == 3822) {
                        widget.actions[4] = "Sell X";
                    }
                }
            }
            if (widget.type == TYPE_RECTANGLE)
                widget.filled = buffer.readUByte() == 1;
            if (widget.type == TYPE_TEXT || widget.type == TYPE_MODEL_LIST) {
                widget.centerText = buffer.readUByte() == 1;
                int k2 = buffer.readUByte();
                if (font != null)
                    widget.text_type = font[k2];
                widget.textShadow = buffer.readUByte() == 1;
            }

            if (widget.type == TYPE_TEXT) {
                widget.defaultText = buffer.readString().replaceAll("RuneScape", ClientConstants.CLIENT_NAME);
                widget.secondaryText = buffer.readString();
            }

            if (widget.type == TYPE_MODEL_LIST || widget.type == TYPE_RECTANGLE || widget.type == TYPE_TEXT)
                widget.textColour = buffer.readInt();
            if (widget.type == TYPE_RECTANGLE || widget.type == TYPE_TEXT) {
                widget.secondaryColor = buffer.readInt();
                widget.defaultHoverColor = buffer.readInt();
                widget.secondaryHoverColor = buffer.readInt();
                if (widget.id == 15234) {// Strange place on music tab.
                    widget.secondaryColor = 65280;
                }
            }
            if (widget.type == TYPE_SPRITE) {
                widget.drawsTransparent = false;
                String name = buffer.readString();
                if (graphics != null && name.length() > 0) {
                    int index = name.lastIndexOf(",");
                    widget.enabledSprite = getSprite(Integer.parseInt(name.substring(index + 1)), graphics,
                        name.substring(0, index));
                }
                name = buffer.readString();
                if (graphics != null && name.length() > 0) {
                    int index = name.lastIndexOf(",");
                    widget.disabledSprite = getSprite(Integer.parseInt(name.substring(index + 1)), graphics,
                        name.substring(0, index));
                }
            }
            if (widget.type == TYPE_MODEL) {
                int content = buffer.readUByte();
                if (content != 0) {
                    widget.model_type = 1;
                    widget.model_id = (content - 1 << 8) + buffer.readUByte();
                }
                content = buffer.readUByte();
                if (content != 0) {
                    widget.enabled_model_type = 1;
                    widget.enabled_model_id = (content - 1 << 8) + buffer.readUByte();
                }
                content = buffer.readUByte();
                if (content != 0)
                    widget.defaultAnimationId = (content - 1 << 8) + buffer.readUByte();
                else
                    widget.defaultAnimationId = -1;
                content = buffer.readUByte();
                if (content != 0)
                    widget.secondaryAnimationId = (content - 1 << 8) + buffer.readUByte();
                else
                    widget.secondaryAnimationId = -1;
                widget.modelZoom = buffer.readUShort();
                widget.modelRotation1 = buffer.readUShort();
                widget.modelRotation2 = buffer.readUShort();
            }
            if (widget.type == TYPE_ITEM_LIST) {
                widget.inventoryItemId = new int[widget.width * widget.height];
                widget.inventoryAmounts = new int[widget.width * widget.height];
                widget.centerText = buffer.readUByte() == 1;
                int l2 = buffer.readUByte();
                if (font != null)
                    widget.text_type = font[l2];
                widget.textShadow = buffer.readUByte() == 1;
                widget.textColour = buffer.readInt();
                widget.inventoryMarginX = buffer.readShort();
                widget.inventoryMarginY = buffer.readShort();
                widget.hasActions = buffer.readUByte() == 1;
                widget.actions = new String[5];
                for (int actionCount = 0; actionCount < 5; actionCount++) {
                    widget.actions[actionCount] = buffer.readString();
                    if (widget.actions[actionCount].length() == 0)
                        widget.actions[actionCount] = null;
                }

            }
            if (widget.optionType == OPTION_USABLE || widget.type == TYPE_INVENTORY) {
                widget.selectedActionName = buffer.readString();
                widget.spellName = buffer.readString();
                widget.selectedTargetMask = buffer.readUShort();
            }

            if (widget.type == 8) {
                widget.defaultText = buffer.readString();
            }

            if (widget.optionType == OPTION_OK || widget.optionType == OPTION_TOGGLE_SETTING
                || widget.optionType == OPTION_RESET_SETTING || widget.optionType == OPTION_CONTINUE) {
                widget.tooltip = buffer.readString().replaceAll("Auto Retaliate", "Auto retaliate");
                if (widget.id == 24111 || widget.id == 349) {
                    widget.tooltip = "Choose spell";
                }
                if (widget.tooltip.length() == 0) {
                    if (widget.optionType == OPTION_OK)
                        widget.tooltip = "Ok";
                    if (widget.optionType == OPTION_TOGGLE_SETTING)
                        widget.tooltip = "Select";
                    if (widget.optionType == OPTION_RESET_SETTING)
                        widget.tooltip = "Select";
                    if (widget.optionType == OPTION_CONTINUE)
                        widget.tooltip = "Continue";
                }
            }
        }
        interfaceLoader = interfaces;
        WildernessWidget.unpack(font);
        SpawnTab.unpack(font);
        spellFilters.unpack(font);
        TopPkersWidget.unpack(font);
        RaidsWidget.unpack(font);
        DailyTasksWidget.unpack(font);
        EnchantBoltsWidget.unpack(font);
        IronmanWidget.unpack(font);
        OrnateJewelleryBoxWidget.unpack(font);
        ItemSimulationWidget.unpack(font);
        DonationPromoWidget.unpack(font);
        GambleWidget.unpack(font);
        PetInsuranceWidget.unpack(font);
        SlayerRewardWidget.unpack(font);
        ScrollWidget.unpack(font);
        ReferralWidget.unpack(font);
        MysteryBoxWidget.unpack(font);
        TeleportWidget.unpack(font);
        DuelArenaWidget.unpack(font);
        QuestTabSidebarWidget.unpack(font);
        QuestTabSidebarWidget.unpackInfo(font);
        QuestTabSidebarWidget.unpackInfo(font);
        PresetWidget.unpack(font);
        TaskWidget.unpack(font);
        MagicSidebarWidget.unpack(font);
        BankWidget.unpack(font);
        OptionTabWidget.unpack(font);
        ClientSettingsWidget.unpack(font);
        KeybindingWidget.unpack(font);
        BarrowsRewardWidget.unpack(font);
        ClanChatSidebarWidget.unpack(font);
        ItemsKeptOnDeathWidget.unpack(font);
        CollectionLogWidget.unpack(font);
        LootingBagWidget.unpack(font);
        EquipmentWidget.unpack(font);
        UnmorphWidget.unpack(font);
        LogoutWidget.unpack(font);
        PrayerSidebarWidget.unpack(font);
        ShopWidget.unpack(font);
        SkillSidebarWidget.unpack(font);
        TradeWidget.unpack(font);
        PriceChecker.unpack(font);
        newinterfaceaor.unpack(font);
        RunePouchWidget.unpack(font);
        WeaponInterfacesWidget.unpack(font);
        TournamentWidget.unpack(font);
        ForgeWidget.unpack(font);
        KillCountWidget.unpack(font);
        AchievementWidget.unpack(font);
        DropInterfaceWidget.unpack(font);
        DialogueWidget.unpack();
        TitleWidget.unpack(font);
        JewelryWidget.init();
        TradingPostWidget.unpack(font);
        GoodieBagWidget.unpack(font);
        EventWidget.unpack(font);
        if (ClientConstants.CHECK_UNUSED_INTERFACES) {
            checkUnusedInterfaces();
        }
        if (ClientConstants.CHECK_DUPLICATE_INTERFACES_IDS) {
            printDuplicates();
        }

        spriteCache = null;
    }

    //Interface loader
    public static Archive interfaceLoader;

    //Widgets
    public Widget dropdownOpen;
    public static Widget[] cache;

    //Sprites
    public SimpleImage textSpriteClicked;
    public SimpleImage spriteWithOutline;
    public SimpleImage enabledAltSprite;
    public SimpleImage disabledAltSprite;
    public SimpleImage disabledSprite;
    public SimpleImage enabledSprite;
    public SimpleImage disabledHover;
    public SimpleImage[] sprites;

    //Dropdown menu
    public DropdownMenu dropdown;

    //Slider
    public Slider slider;

    //Fonts
    public AdvancedFont text_type;

    //reference Cache
    private static TempCache spriteCache;
    private static final TempCache models = new TempCache(30);

    //Bytes
    public byte opacity;

    //Array integers
    public int[] buttonsToDisable;
    public int[] dropdownColours;
    public int[] itemOpacity;

    //Integers
    public int id;
    public int[] inventoryAmounts;
    public int[] inventoryItemId;
    public int dropdownHover = -1;
    public int transparency = 255;
    public int hoverXOffset = 0;
    public int hoverYOffset = 0;
    public int spriteXOffset = 0;
    public int spriteYOffset = 0;
    public int lastFrameTime;
    public int clickSprite1 = 0;
    public static int parentchilds = 0;
    public static final int BEGIN_READING_PRAYER_INTERFACE = 6;//Amount of total custom prayers we've added
    public static final int CUSTOM_PRAYER_HOVERS = 3; //Amount of custom prayer hovers we've added
    public static final int PRAYER_INTERFACE_CHILDREN = 80 + BEGIN_READING_PRAYER_INTERFACE + CUSTOM_PRAYER_HOVERS;
    private static final int LUNAR_RUNE_SPRITES_START = 232;
    private static final int LUNAR_OFF_SPRITES_START = 246;
    private static final int LUNAR_ON_SPRITES_START = 285;
    private static final int LUNAR_HOVER_BOX_SPRITES_START = 324;
    public int modelType, modelFileId;
    public int itemId;
    static int achievementChild = 0;
    static int achievementY = 0;
    public int childToIntersect = 0;
    public int positionX, positionY;
    public int radioID = 0;
    public static ArrayList<Integer> radioButtons = new ArrayList<Integer>();
    public int clickSprite2 = 0;
    public static int currentInputFieldId;
    public int hoveredOutlineSpriteXOffset, hoveredOutlineSpriteYOffset;
    public int modelZoom;
    public int modelRotation1;
    public int modelRotation2;
    public int[] child_y;
    public int spriteOpacity;
    public int msgX, msgY;
    public int y;//yOffset
    public int height;
    public int scrollMax;
    public int type;
    public int x;//xOffset
    private int enabled_model_type;
    private int enabled_model_id;
    public int defaultAnimationId;
    public int secondaryAnimationId;
    public int secondaryHoverColor;
    public int[] children;
    public int[] child_x;
    public int inventoryMarginY;
    public int[] valueCompareType;
    public int currentFrame;
    public int[] inventoryOffsetY;
    public int hoverType;
    public int inventoryMarginX;
    public int textColour;
    public int opacity2;
    public int model_type;
    public int model_id;
    public int parent;
    public int selectedTargetMask;
    public int[] requiredValues;
    public int contentType;
    public int[] inventoryOffsetX;
    public int alpha;
    public int defaultHoverColor;
    public int optionType;
    public int secondaryColor;
    public int width;
    public int scrollPosition;
    public int[][] valueIndexArray;
    public int pauseTicks = 20;
    public int color;
    public int textClickedX;
    public int textClickedY;
    public int characterLimit;
    public int[] buttons;

    //Booleans
    public boolean isInFocus;
    public boolean displayAsterisks;
    public boolean updatesEveryInput;
    public boolean updateConfig = true;
    public boolean textIsClicked;
    public boolean endReached = false;
    public boolean inventoryHover;
    public boolean replaceItems;
    public boolean usableItems;
    public boolean hasActions;
    public boolean collection = false;
    public boolean displayAmount = true;
    public boolean displayExamine = true;
    public boolean allowSwapItems;
    public boolean invisible;
    public boolean drawingDisabled;
    public boolean textShadow;
    public boolean toggled = false;
    public boolean active;
    public boolean busy;
    public boolean hovered = false;
    public boolean inverted;
    public boolean isAchievementHover = false;
    public boolean isHovered = false;
    public boolean fullCint = false;
    public boolean drawNumber = false;
    public boolean geSearchButton = false;
    public boolean isRadioButton = false;
    public boolean filled;
    public boolean centerText;
    public boolean rightText;
    public boolean serverCheck = false;
    public boolean regularHoverBox;
    public boolean clickable = false;
    public boolean drawsTransparent;
    public boolean fancy = false;
    public boolean hightDetail;

    //Strings
    public String hoverText;
    public String defaultInputFieldText = "";
    public String spellName;
    public String tooltip;
    public String selectedActionName;
    public String[] actions;
    public String defaultText;
    public String secondaryText;
    public String inputRegex = "";

    //Methods

    public static void textClicked(int interfaceId, int spriteId, int xOffset, int yOffset) {
        cache[interfaceId].textSpriteClicked = Client.spriteCache.get(spriteId);
        cache[interfaceId].textClickedX = xOffset;
        cache[interfaceId].textClickedY = yOffset;
    }

    public static void createSkillHover(int id, int x, int width) {
        Widget hover = addInterface(id);
        hover.inventoryHover = true;
        hover.type = 8;
        hover.defaultText = "ct$" + x;
        hover.contentType = x;
        hover.width = width;
        hover.height = 32;
    }

    /**
     * This method is used to create tooltips, make sure tooltips are always the last childs.
     * The Y child is used to determine the height on the widget itself not the height of the method.
     *
     * @param id     The widget id
     * @param text   The tooltip text
     * @param width  The length of the tooltip box
     * @param height The height position of the tooltip box
     */
    public static void createTooltip(int id, String text, int width, int height) {
        Widget widget = addInterface(id);
        widget.inventoryHover = true;
        widget.type = 8;
        widget.defaultText = text;
        widget.width = width;
        widget.height = height;
    }

    public static void drawProgressBar(int id, int width, int height, int currentPercent) {
        Widget rsi = addInterface(id);
        rsi.type = 287;
        rsi.width = width;
        rsi.height = height;
        rsi.currentPercent = currentPercent;
    }

    public int currentPercent;

    /**
     * Sends a button without a image
     *
     * @param id      The widget id
     * @param tooltip The button action text
     * @param height  The click action height
     * @param width   The click action width
     */


    public static void addButtonWithoutSprite(int id, String tooltip, int height, int width) {
        Widget widget = cache[id] = new Widget();
        widget.id = id;
        widget.parent = id;
        widget.type = 5;
        widget.optionType = 1;
        widget.contentType = 0;
        widget.opacity = (byte) 0;
        widget.hoverType = 52;
        widget.enabledSprite = null;
        widget.disabledSprite = null;
        widget.width = width;
        widget.height = height;
        widget.tooltip = tooltip;
    }

    /**
     * Adds an spell sprite to the weapon interface
     *
     * @param id       The widget id
     * @param spriteId The image id
     */
    public static void addSpellSprite(int id, int spriteId) {
        Widget widget = cache[id] = new Widget();
        widget.id = id;
        widget.parent = id;
        widget.type = TYPE_SPELL_SPRITE;
        widget.optionType = 0;
        widget.contentType = 0;
        widget.opacity = 0;
        widget.hoverType = 0;

        if (spriteId != -1) {
            widget.enabledSprite = Client.spriteCache.get(spriteId);
            widget.disabledSprite = Client.spriteCache.get(spriteId);
        }

        widget.width = 0;
        widget.height = 0;
    }

    /**
     * This method is used to check for unused interfaces
     */
    public static void checkUnusedInterfaces() {
        int freeIdCount = 0;
        int longestRunCount = 0;
        int longestRunStart = -1;
        int curRunStart = 0;
        int curRunCount = 0;
        int sandwichId = -1;
        int lastId = 0;
        //Author: knd6060
        for (int i = 0; i < cache.length; i++) {
            if (cache[i] == null) {
                //System.out.println("This ID is unused: " + i); // prints out this id is unused
                freeIdCount++;

                if (curRunStart == -1) {
                    curRunStart = i;
                }
                curRunCount++;
            } else {
                if (curRunCount == 1 && sandwichId < 0) {
                    sandwichId = i - 1;
                }
                if (curRunCount > longestRunCount) {
                    longestRunCount = curRunCount;
                    longestRunStart = curRunStart;
                }
                curRunStart = -1;
                curRunCount = 0;
                lastId = i;
            }
        }
        System.out.println(freeIdCount + " unused interface ids");
        System.out.println(longestRunStart + " onwards has " + longestRunCount + " unused interface ids in a row");
        System.out.println(sandwichId + " is a single unused interface id surrounded by used ones.");
        System.out.println(lastId + " is the last used interface, interfaceCache.length should be set to " + (lastId + 1));
        System.out.println(cache.length + " is the size of the interfaces array");
    }

    /**
     * Easy access for writing a close button to an interface
     *
     * @param id             The widget id
     * @param enabledSprite  The first sprite
     * @param disabledSprite The hover sprite
     */
    public static void closeButton(int id, int enabledSprite, int disabledSprite, boolean serverActionRequired) {
        Widget widget = addInterface(id);
        if (serverActionRequired) {
            widget.optionType = OPTION_OK;
        } else {
            widget.optionType = OPTION_CLOSE;
        }
        widget.type = TYPE_HOVER;
        widget.disabledSprite = Client.spriteCache.get(enabledSprite);
        widget.enabledSprite = Client.spriteCache.get(disabledSprite);
        widget.width = widget.disabledSprite.width;
        widget.height = widget.enabledSprite.height;
        widget.toggled = false;
        widget.spriteOpacity = 255;
        widget.tooltip = "Close";
    }

    /**
     * Draws a hover button
     *
     * @param id             The widget id
     * @param tooltip        The tooltip text
     * @param enabledSprite  The main sprite
     * @param disabledSprite The hover sprite
     * @param buttonText     The button action text
     * @param font           The font
     * @param colour         The text color
     * @param hoveredColour  The hover text color
     * @param centerText     Return true if we want to center the text, false otherwise
     */
    public static void hoverButton(int id, String tooltip, int enabledSprite, int disabledSprite, String buttonText, AdvancedFont[] font, int idx, int colour, int hoveredColour, boolean centerText) {
        Widget widget = addInterface(id);
        widget.tooltip = tooltip;
        widget.optionType = 1;
        widget.type = TYPE_HOVER;
        widget.disabledSprite = Client.spriteCache.get(enabledSprite);
        widget.enabledSprite = Client.spriteCache.get(disabledSprite);
        widget.width = widget.disabledSprite.width;
        widget.height = widget.enabledSprite.height;
        widget.msgX = widget.width / 2;
        widget.msgY = (widget.height / 2) + 4;
        widget.defaultText = buttonText;
        widget.toggled = false;
        widget.text_type = font[idx];
        widget.textColour = colour;
        widget.defaultHoverColor = hoveredColour;
        widget.centerText = centerText;
        widget.spriteOpacity = 255;
    }

    /**
     * Draws a hover button with opacity
     *
     * @param id             The widget id
     * @param tooltip        The tooltip text
     * @param enabledSprite  The main sprite
     * @param disabledSprite The hover sprite
     * @param opacity        The opacity of the button
     */
    public static void hoverButton(int id, String tooltip, int enabledSprite, int disabledSprite, int opacity) {
        Widget widget = addInterface(id);
        widget.tooltip = tooltip;
        widget.optionType = 1;
        widget.type = TYPE_HOVER;
        widget.disabledSprite = Client.spriteCache.get(enabledSprite);
        widget.enabledSprite = Client.spriteCache.get(disabledSprite);
        widget.width = widget.disabledSprite.width;
        widget.height = widget.enabledSprite.height;
        widget.toggled = false;
        widget.spriteOpacity = opacity;
    }

    /**
     * Sends a config button to the widget
     *
     * @param id             The widget id
     * @param tooltip        The tooltip text
     * @param enabledSprite  The main sprite (config on)
     * @param disabledSprite The secondary sprite (config off)
     */
    public static void configButton(int id, String tooltip, int enabledSprite, int disabledSprite) {
        Widget widget = addInterface(id);
        widget.tooltip = tooltip;
        widget.optionType = OPTION_OK;
        widget.type = TYPE_CONFIG;
        widget.disabledSprite = Client.spriteCache.get(enabledSprite);
        widget.enabledSprite = Client.spriteCache.get(disabledSprite);
        widget.width = widget.disabledSprite.width;
        widget.height = widget.enabledSprite.height;
        widget.active = false;
    }

    /**
     * Send a clickable config button to the widget
     *
     * @param id          The widget id
     * @param parent      The main interface
     * @param width       The width of the button (click)
     * @param height      The height of the button (click)
     * @param config      The config id
     * @param configFrame The config action turned off or on
     * @param sprite1     The primary image
     * @param sprite2     The secondary image
     * @param hoverOver   The hover type
     * @param tooltip     The tooltip text
     */
    public static void addButton(int id, int parent, int width, int height, int config, int configFrame, int sprite1, int sprite2, int hoverOver, String tooltip) {
        Widget widget = addInterface(id);
        widget.parent = parent;
        widget.type = TYPE_SPRITE;
        widget.optionType = 1;
        widget.width = width;
        widget.height = height;
        widget.requiredValues = new int[1];
        widget.valueCompareType = new int[1];
        widget.valueCompareType[0] = 1;
        widget.requiredValues[0] = config;
        widget.valueIndexArray = new int[1][3];
        widget.valueIndexArray[0][0] = 5;
        widget.valueIndexArray[0][1] = configFrame;
        widget.valueIndexArray[0][2] = 0;
        widget.tooltip = tooltip;
        widget.defaultText = tooltip;
        widget.hoverType = hoverOver;
        widget.enabledSprite = Client.spriteCache.get(sprite1);
        widget.disabledSprite = Client.spriteCache.get(sprite2);
    }

    /**
     * Sends a regular button to the widget, without a config action
     *
     * @param id        The widget id
     * @param parent    The main widget
     * @param width     The width of the button (click)
     * @param height    The height of the button (click)
     * @param sprite1   The primary image
     * @param sprite2   The secondary image
     * @param hoverOver The hover type
     * @param tooltip   The tooltip text
     */
    public static void addButton(int id, int parent, int width, int height, int sprite1, int sprite2, int hoverOver, String tooltip) {
        Widget widget = addInterface(id);
        widget.parent = parent;
        widget.type = TYPE_SPRITE;
        widget.optionType = 1;
        widget.width = width;
        widget.height = height;
        widget.tooltip = tooltip;
        widget.defaultText = tooltip;
        widget.hoverType = hoverOver;
        widget.enabledSprite = Client.spriteCache.get(sprite1);
        widget.disabledSprite = Client.spriteCache.get(sprite2);
    }

    /**
     * Sends a regular button to the widget, without a config action
     *
     * @param id        The widget id
     * @param parent    The main widget
     * @param width     The width of the button (click)
     * @param height    The height of the button (click)
     * @param sprite1   The primary image
     * @param sprite2   The secondary image
     * @param hoverOver The hover type
     * @param tooltip   The tooltip text
     */
    public static void addButton(int id, int parent, int width, int height, SimpleImage sprite1, SimpleImage sprite2, int hoverOver, String tooltip) {
        Widget widget = addInterface(id);
        widget.parent = parent;
        widget.type = TYPE_SPRITE;
        widget.optionType = 1;
        widget.width = width;
        widget.height = height;
        widget.tooltip = tooltip;
        widget.defaultText = tooltip;
        widget.hoverType = hoverOver;
        widget.enabledSprite = sprite1;
        widget.disabledSprite = sprite2;
    }

    /**
     * This method adds:
     * Config-toggleable prayer sprites not clickable and
     * Config-toggleable glow on the prayer also clickable
     *
     * @param id             The widget id
     * @param tooltip        The tooltip text
     * @param width          The widgets width
     * @param height         The widgets height
     * @param glowSprite     Sends the glowing sprite
     * @param glowX          Sends the sprite offset X coordinate
     * @param glowY          Sends the sprite offset Y coordinate
     * @param disabledSprite The secondary sprite
     * @param enabledSprite  The primary sprite
     * @param config         The config id
     * @param configFrame    The config type on or off
     * @param hover          The hover type
     */
    public static void addPrayer(int id, String tooltip, int width, int height, int glowSprite, int glowX, int glowY, int disabledSprite, int enabledSprite, int config, int configFrame, int hover) {
        Widget widget = addTabInterface(id);

        widget.parent = 5608;
        widget.type = TYPE_SPRITE;
        widget.optionType = 1;
        widget.width = width;
        widget.height = height;
        widget.requiredValues = new int[1];
        widget.valueCompareType = new int[1];
        widget.valueCompareType[0] = 1;
        widget.requiredValues[0] = config;
        widget.valueIndexArray = new int[1][3];
        widget.valueIndexArray[0][0] = 5;
        widget.valueIndexArray[0][1] = configFrame;
        widget.valueIndexArray[0][2] = 0;
        widget.tooltip = tooltip;
        widget.defaultText = tooltip;
        widget.hoverType = 52;
        widget.disabledSprite = Client.spriteCache.get(glowSprite);
        widget.spriteXOffset = glowX;
        widget.spriteYOffset = glowY;

        widget = addTabInterface(id + 1);
        widget.parent = 5608;
        widget.type = TYPE_SPRITE;
        widget.optionType = 0;
        widget.width = width;
        widget.height = height;
        widget.requiredValues = new int[1];
        widget.valueCompareType = new int[1];
        widget.valueCompareType[0] = 2;
        widget.requiredValues[0] = 1;
        widget.valueIndexArray = new int[1][3];
        widget.valueIndexArray[0][0] = 5;
        widget.valueIndexArray[0][1] = configFrame + 1;
        widget.valueIndexArray[0][2] = 0;
        widget.tooltip = tooltip;
        widget.defaultText = tooltip;
        widget.disabledSprite = Client.spriteCache.get(disabledSprite);
        widget.enabledSprite = Client.spriteCache.get(enabledSprite);
        widget.hoverType = hover;
    }

    public static void addPrayer(int id, int configState, int configFrame, int requiredValues, int disabledSprite, int enabledSprite, String prayerName, int hover, int glowX, int glowY) {
        Widget widget = addTabInterface(id);
        widget.id = id;
        widget.parent = 22500;
        widget.type = 5;
        widget.optionType = 4;
        widget.contentType = 0;
        widget.opacity = 0;
        widget.hoverType = hover;
        widget.enabledSprite = Client.spriteCache.get(150);
        widget.width = 34;
        widget.height = 34;
        widget.spriteXOffset = glowX;
        widget.spriteYOffset = glowY;
        widget.valueCompareType = new int[1];
        widget.requiredValues = new int[1];
        widget.valueCompareType[0] = 1;
        widget.requiredValues[0] = configState;
        widget.valueIndexArray = new int[1][3];
        widget.valueIndexArray[0][0] = 5;
        widget.valueIndexArray[0][1] = configFrame;
        widget.valueIndexArray[0][2] = 0;
        widget.tooltip = "Activate <col=ffb000>" + prayerName;
        widget = addTabInterface(id + 1);
        widget.id = id + 1;
        widget.parent = 22500;
        widget.type = 5;
        widget.optionType = 0;
        widget.contentType = 0;
        widget.opacity = 0;
        widget.disabledSprite = Client.spriteCache.get(disabledSprite);
        widget.enabledSprite = Client.spriteCache.get(enabledSprite);
        widget.width = 34;
        widget.height = 34;
        widget.valueCompareType = new int[1];
        widget.requiredValues = new int[1];
        widget.valueCompareType[0] = 2;
        widget.requiredValues[0] = requiredValues + 1;
        widget.valueIndexArray = new int[1][3];
        widget.valueIndexArray[0][0] = 2;
        widget.valueIndexArray[0][1] = 5;
        widget.valueIndexArray[0][2] = 0;
    }

    /**
     * Adds the prayer hover box
     *
     * @param id      The widget id
     * @param hover   The hover type
     * @param xOffset The X offset
     * @param yOffset The Y offset
     */
    public static void addPrayerHover(int id, String hover, int xOffset, int yOffset) {
        Widget widget = addTabInterface(id);
        widget.parent = 5608;
        widget.type = 8;
        widget.width = 40;
        widget.height = 32;
        widget.hoverText = widget.defaultText = hover;
        widget.hoverXOffset = xOffset;
        widget.hoverYOffset = yOffset;
        widget.regularHoverBox = true;
    }

    /**
     * Adds an setting sprite
     *
     * @param childId  The widget id
     * @param spriteId The main sprite
     */
    public static void addSettingsSprite(int childId, int spriteId) {
        Widget widget = cache[childId] = new Widget();
        widget.id = childId;
        widget.parent = childId;
        widget.type = 5;
        widget.optionType = 0;
        widget.contentType = 0;
        widget.enabledSprite = Client.spriteCache.get(spriteId);
        widget.disabledSprite = Client.spriteCache.get(spriteId);
        widget.width = widget.disabledSprite.width;
        widget.height = widget.enabledSprite.height - 2;
    }

    /**
     * Adds a hover button with a config
     *
     * @param id                The widget id
     * @param tooltip           The tooltip text
     * @param enabledSprite     The main sprite
     * @param disabledSprite    The secondary sprite
     * @param enabledAltSprite  A primary alternative sprite
     * @param disabledAltSprite A secondary alternative sprite
     * @param active            Check if the child is active
     * @param buttonsToDisable  Disabled buttons
     */
    public static void configHoverButton(int id, String tooltip, int enabledSprite, int disabledSprite, int enabledAltSprite, int disabledAltSprite, boolean active, int... buttonsToDisable) {
        Widget widget = addInterface(id);
        widget.tooltip = tooltip;
        widget.optionType = OPTION_OK;
        widget.type = TYPE_CONFIG_HOVER;
        widget.disabledSprite = Client.spriteCache.get(enabledSprite);
        widget.enabledSprite = Client.spriteCache.get(disabledSprite);
        widget.width = widget.disabledSprite.width;
        widget.height = widget.enabledSprite.height;
        widget.enabledAltSprite = Client.spriteCache.get(enabledAltSprite);
        widget.disabledAltSprite = Client.spriteCache.get(disabledAltSprite);
        widget.buttonsToDisable = buttonsToDisable;
        widget.active = active;
        widget.spriteOpacity = 255;
    }

    /**
     * Adds a hover button without a config and disables the buttonsToDisable array.
     *
     * @param id                The widget id
     * @param tooltip           The tooltip text
     * @param enabledSprite     The main sprite
     * @param disabledSprite    The secondary sprite
     * @param enabledAltSprite  A primary alternative sprite
     * @param disabledAltSprite A secondary alternative sprite
     * @param active            Check if the child is active
     * @param buttonsToDisable  Disabled buttons
     */
    public static void addHoverButtonWithDisable(int id, String tooltip, int enabledSprite, int disabledSprite, int enabledAltSprite, int disabledAltSprite, boolean active, int... buttonsToDisable) {
        Widget widget = addInterface(id);
        widget.tooltip = tooltip;
        widget.optionType = OPTION_OK;
        widget.type = TYPE_CONFIG_HOVER;
        widget.disabledSprite = Client.spriteCache.get(enabledSprite);
        widget.enabledSprite = Client.spriteCache.get(disabledSprite);
        widget.width = widget.disabledSprite.width;
        widget.height = widget.enabledSprite.height;
        widget.enabledAltSprite = Client.spriteCache.get(enabledAltSprite);
        widget.disabledAltSprite = Client.spriteCache.get(disabledAltSprite);
        widget.buttonsToDisable = buttonsToDisable;
        widget.active = active;
        widget.spriteOpacity = 255;
    }

    public static void addConfigSprite(int id, int spriteId, int spriteId2, int state, int config, String tooltip) {
        Widget widget = addTabInterface(id);
        widget.id = id;
        widget.parent = id;
        widget.type = 5;
        widget.optionType = 0;
        widget.contentType = 0;
        widget.width = 512;
        widget.height = 334;
        widget.opacity = 0;
        widget.hoverType = -1;
        widget.tooltip = tooltip;
        widget.defaultText = tooltip;
        widget.valueCompareType = new int[1];
        widget.requiredValues = new int[1];
        widget.valueCompareType[0] = 1;
        widget.requiredValues[0] = state;
        widget.valueIndexArray = new int[1][3];
        widget.valueIndexArray[0][0] = 5;
        widget.valueIndexArray[0][1] = config;
        widget.valueIndexArray[0][2] = 0;
        widget.disabledSprite = spriteId < 0 ? null : Client.spriteCache.get(spriteId);
        widget.enabledSprite = spriteId2 < 0 ? null : Client.spriteCache.get(spriteId2);
    }

    public static void addConfigSprite(int id, int spriteId, int spriteId2, int state, int config) {
        Widget widget = addTabInterface(id);
        widget.id = id;
        widget.parent = id;
        widget.type = 5;
        widget.optionType = 0;
        widget.contentType = 0;
        widget.width = 512;
        widget.height = 334;
        widget.opacity = 0;
        widget.hoverType = -1;
        widget.valueCompareType = new int[1];
        widget.requiredValues = new int[1];
        widget.valueCompareType[0] = 1;
        widget.requiredValues[0] = state;
        widget.valueIndexArray = new int[1][3];
        widget.valueIndexArray[0][0] = 5;
        widget.valueIndexArray[0][1] = config;
        widget.valueIndexArray[0][2] = 0;
        widget.disabledSprite = spriteId < 0 ? null : Client.spriteCache.get(spriteId);
        widget.enabledSprite = spriteId2 < 0 ? null : Client.spriteCache.get(spriteId2);
    }

    public static void addSprite(int id, int spriteId) {
        Widget rsint = cache[id] = new Widget();
        rsint.id = id;
        rsint.parent = id;
        rsint.type = 5;
        rsint.optionType = 0;
        rsint.contentType = 0;
        rsint.opacity = 0;
        rsint.hoverType = 0;
        if (spriteId != -1) {
            rsint.enabledSprite = Client.spriteCache.get(spriteId);
            rsint.disabledSprite = Client.spriteCache.get(spriteId);
        }

        rsint.width = 0;
        rsint.height = 0;
    }

    public static void addTransparantSprite(int id, int spriteId, int opacity) {
        Widget widget = cache[id] = new Widget();
        widget.id = id;
        widget.parent = id;
        widget.type = 5;
        widget.optionType = 0;
        widget.contentType = 0;
        widget.opacity = 0;
        widget.hoverType = 0;
        widget.drawsTransparent = true;
        widget.transparency = opacity;
        if (spriteId != -1) {
            widget.enabledSprite = Client.spriteCache.get(spriteId);
            widget.disabledSprite = Client.spriteCache.get(spriteId);
        }
        widget.width = 0;
        widget.height = 0;
    }

    public static void addText(int id, String text, AdvancedFont[] wid, int idx, int color) {
        Widget rsinterface = addTabInterface(id);
        rsinterface.id = id;
        rsinterface.parent = id;
        rsinterface.type = 4;
        rsinterface.optionType = 0;
        rsinterface.width = 174;
        rsinterface.height = 11;
        rsinterface.contentType = 0;
        rsinterface.opacity = 0;
        rsinterface.centerText = false;
        rsinterface.textShadow = true;
        rsinterface.text_type = wid[idx];
        rsinterface.defaultText = text;
        rsinterface.secondaryText = "";
        rsinterface.textColour = color;
        rsinterface.secondaryColor = 0;
        rsinterface.defaultHoverColor = 0;
        rsinterface.secondaryHoverColor = 0;
    }

    public static void addQuestWidgetText(int id, String text, AdvancedFont tda[], int idx, int color, boolean center, boolean shadow) {
        Widget tab = addTabInterface(id);
        tab.parent = id;
        tab.id = id;
        tab.type = 4;
        tab.optionType = 0;
        tab.width = 331;
        tab.height = 11;
        tab.contentType = 0;
        tab.opacity = 0;
        tab.hoverType = -1;
        tab.centerText = center;
        tab.textShadow = shadow;
        tab.text_type = tda[idx];
        tab.defaultText = text;
        tab.secondaryText = "";
        tab.textColour = color;
        tab.secondaryColor = 0;
        tab.defaultHoverColor = 0;
        tab.secondaryHoverColor = 0;
    }

    public static void removeConfig(int id) {
        @SuppressWarnings("unused")
        Widget rsi = cache[id] = new Widget();
    }

    public static void addBackgroundImage(int id, int width, int height, boolean divider) {
        Widget tab = cache[id] = new Widget();
        tab.id = id;
        tab.parent = id;
        tab.type = 5;
        tab.optionType = 0;
        tab.contentType = 0;
        tab.opacity = (byte) 0;
        tab.hoverType = 52;
        tab.enabledSprite = tab.disabledSprite = buildBackground(width, height, divider);
        tab.width = width;
        tab.height = height;
    }

    public static SimpleImage buildBackground(int width, int height, boolean divider) {
        int[][] pixels = new int[height][width];

        // Background
        fillPixels(pixels, Client.autoBackgroundSprites[0], 0, 0, width, height);

        // Top border
        fillPixels(pixels, Client.autoBackgroundSprites[5], 25, 0, width - 25, 6);

        // Left border
        fillPixels(pixels, Client.autoBackgroundSprites[7], 0, 30, 6, height - 30);

        // Right border
        fillPixels(pixels, Client.autoBackgroundSprites[6], width - 6, 30, width, height - 30);

        // Bottom border
        fillPixels(pixels, Client.autoBackgroundSprites[8], 25, height - 6, width - 25, height);

        // Top left corner
        insertPixels(pixels, Client.autoBackgroundSprites[1], 0, 0, true);

        // Top right corner
        insertPixels(pixels, Client.autoBackgroundSprites[2], width - 25, 0, true);

        // Bottom left corner
        insertPixels(pixels, Client.autoBackgroundSprites[3], 0, height - 30, true);

        // Bottom right corner
        insertPixels(pixels, Client.autoBackgroundSprites[4], width - 25, height - 30, true);

        // Divider
        if (divider)
            fillPixels(pixels, Client.autoBackgroundSprites[5], 6, 29, width - 6, 35);

        return new SimpleImage(width, height, 0, 0, Utils.d2Tod1(pixels));
    }

    public static void insertPixels(int[][] pixels, SimpleImage image, int x, int y, boolean ignoreTransparency) {
        int[][] imagePixels = Utils.d1Tod2(image.pixels, image.width);

        for (int j = y; j < y + image.height; j++) {
            for (int i = x; i < x + image.width; i++) {
                if (ignoreTransparency && imagePixels[j - y][i - x] == 0)
                    continue;
                pixels[j][i] = imagePixels[j - y][i - x];
            }
        }
    }

    public static void fillPixels(int[][] pixels, SimpleImage image, int startX, int startY, int endX, int endY) {
        int[][] imagePixels = Utils.d1Tod2(image.pixels, image.width);

        for (int j = startY; j < endY; j++) {
            for (int i = startX; i < endX; i++) {
                pixels[j][i] = imagePixels[(j - startY) % image.height][(i - startX) % image.width];
            }
        }
    }

    public static void addItem(int id, boolean showAmount, boolean upgradeStation) {
        Widget widget = cache[id] = new Widget();
        if (upgradeStation) {
            widget.actions = new String[]{
                "Reclaim",
                null,
                null,
                null,
                null
            };
        } else {
            widget.actions = new String[5];
        }
        widget.inventoryOffsetX = new int[20];
        widget.inventoryItemId = new int[30];
        widget.inventoryAmounts = new int[30];
        widget.itemOpacity = new int[30];
        widget.inventoryOffsetY = new int[20];
        widget.children = new int[0];
        widget.child_x = new int[0];
        widget.child_y = new int[0];
        widget.hasActions = false;
        widget.displayAmount = showAmount;
        widget.inventoryMarginX = 24;
        widget.inventoryMarginY = 24;
        widget.height = 5;
        widget.width = 6;
        widget.parent = 5292;
        widget.id = id;
        widget.type = TYPE_INVENTORY;
    }

    public static void addItem(int id, boolean showAmount) {
        Widget widget = cache[id] = new Widget();
        widget.actions = new String[5];
        widget.inventoryOffsetX = new int[1];
        widget.inventoryOffsetY = new int[1];
        widget.inventoryItemId = new int[1];
        widget.inventoryAmounts = new int[1];
        widget.itemOpacity = new int[1];
        widget.children = new int[0];
        widget.child_x = new int[0];
        widget.child_y = new int[0];
        widget.hasActions = false;
        widget.displayAmount = showAmount;
        widget.inventoryMarginX = 1;
        widget.inventoryMarginY = 1;
        widget.height = 5;
        widget.width = 9;
        widget.parent = 27400;
        widget.id = id;
        widget.type = TYPE_INVENTORY;
    }

    /**
     * Adds the player character to certain interface
     *
     * @param id   The interface child id
     * @param zoom The character zoom
     */
    public static void addCharacterToInterface(int id, int zoom) {
        Widget widget = cache[id] = new Widget();
        widget.id = id;
        widget.parent = id;
        widget.type = 6;
        widget.optionType = 0;
        widget.contentType = 328;
        widget.width = 136;
        widget.height = 168;
        widget.opacity = 0;
        widget.hoverType = 0;
        widget.modelZoom = zoom;
        widget.modelRotation1 = 150;
        widget.modelRotation2 = 0;
        widget.defaultAnimationId = -1;
        widget.secondaryAnimationId = -1;
    }

    public static void addNPCWidget(int id) {
        Widget widget = cache[id] = new Widget();
        widget.id = id;
        widget.parent = id;
        widget.type = 6;
        widget.optionType = 0;
        widget.contentType = 3292;
        widget.width = 136;
        widget.height = 168;
        widget.opacity = 0;
        widget.hoverType = 0;
        widget.modelZoom = 560;
        widget.modelRotation1 = 150;
        widget.modelRotation2 = 0;
        widget.defaultAnimationId = -1;
        widget.secondaryAnimationId = -1;
    }

    public static void addButton(int id, int sprite) {
        Widget tab = cache[id] = new Widget();
        tab.id = id;
        tab.parent = id;
        tab.type = 5;
        tab.optionType = 1;
        tab.contentType = 0;
        tab.opacity = (byte) 0;
        tab.hoverType = 52;
        tab.enabledSprite = Client.spriteCache.get(sprite);
        tab.disabledSprite = Client.spriteCache.get(sprite);
        tab.width = tab.enabledSprite.width;
        tab.height = tab.disabledSprite.height;
        tab.tooltip = "";
    }

    public static void addButton(int id, int sprite, String tooltip) {
        Widget tab = cache[id] = new Widget();
        tab.id = id;
        tab.parent = id;
        tab.type = 5;
        tab.optionType = 1;
        tab.contentType = 0;
        tab.opacity = (byte) 0;
        tab.hoverType = 52;
        tab.enabledSprite = Client.spriteCache.get(sprite);
        tab.disabledSprite = Client.spriteCache.get(sprite);
        tab.width = tab.enabledSprite.width;
        tab.height = tab.disabledSprite.height;
        tab.tooltip = tooltip;
    }

    public static void addButtonWithMenu(int id, int sprite, String[] menu) {
        Widget tab = cache[id] = new Widget();
        tab.id = id;
        tab.parent = id;
        tab.type = 5;
        tab.optionType = 1;
        tab.contentType = 0;
        tab.opacity = (byte) 0;
        tab.hoverType = 17;
        if (Client.spriteCache.get(sprite) != null) {
            tab.enabledSprite = Client.spriteCache.get(sprite);
            tab.disabledSprite = Client.spriteCache.get(sprite);
        }
        tab.width = tab.enabledSprite.width;
        tab.height = tab.disabledSprite.height;
        tab.actions = menu;
        //System.out.println("Added with menu: " + Arrays.toString(menu));
    }

    public static void addTooltipBox(int id, String text) {
        Widget rsi = addInterface(id);
        rsi.id = id;
        rsi.parent = id;
        rsi.type = 8;
        rsi.defaultText = text;
    }

    public static void addTooltipBox(AdvancedFont[] tda, int idx, int ID, String hover, int xOffset, int yOffset, int width,
                                     int height) {
        // Adding hover box
        Widget p = addTabInterface(ID);
        p.inventoryHover = true;
        p.parent = 5608;
        p.type = 8;
        p.width = width;
        p.height = height;
        p.defaultText = hover;
        p.text_type = tda[idx];
        p.hoverXOffset = xOffset;
        p.hoverYOffset = yOffset;
        p.regularHoverBox = true;
    }

    public static void addTooltip(int id, String text) {
        Widget rsi = addInterface(id);
        rsi.id = id;
        rsi.type = 0;
        rsi.invisible = true;
        rsi.hoverType = -1;
        addTooltipBox(id + 1, text);
        rsi.totalChildren(1);
        rsi.child(0, id + 1, 0, 0);
    }

    public static void drawTooltip(int id, String text) {
        Widget widget = addTabInterface(id);
        widget.parent = id;
        widget.type = 0;
        widget.invisible = true;
        widget.hoverType = -1;
        addTooltipBox(id + 1, text);
        widget.totalChildren(1);
        widget.child(0, id + 1, 0, 0);
    }

    private static List<Integer> foundIDS = new ArrayList<>();
    private static List<Integer> foundChildren = new ArrayList<>();

    public static Widget addInterface(int id) {
        Widget rsi = cache[id] = new Widget();
        if (ClientConstants.CHECK_DUPLICATE_INTERFACES_IDS && foundIDS.contains(id)) {
            System.out.println("Tried to add interface " + id + " but it already existed.");
        }
        foundIDS.add(id);
        rsi.id = id;
        rsi.parent = id;
        rsi.width = 512;
        rsi.height = 334;

        return rsi;
    }

    public static void addText(int id, String text, AdvancedFont[] tda, int idx, int color, boolean centered) {
        Widget rsi = cache[id] = new Widget();
        if (centered)
            rsi.centerText = true;
        rsi.textShadow = true;
        rsi.text_type = tda[idx];
        rsi.defaultText = text;
        rsi.textColour = color;
        rsi.id = id;
        rsi.type = 4;
    }

    public static void textColour(int id, int color) {
        Widget rsi = cache[id];
        rsi.textColour = color;
    }

    public static void textSize(int id, AdvancedFont tda[], int idx) {
        Widget rsi = cache[id];
        rsi.text_type = tda[idx];
    }

    public static void addCacheSprite(int id, int sprite1, int sprite2, String sprites) {
        Widget rsi = cache[id] = new Widget();
        rsi.enabledSprite = getSprite(sprite1, interfaceLoader, sprites);
        rsi.disabledSprite = getSprite(sprite2, interfaceLoader, sprites);
        rsi.parent = id;
        rsi.id = id;
        rsi.type = 5;
    }

    public static void sprite1(int id, int sprite) {
        Widget class9 = cache[id];
        class9.enabledSprite = Client.spriteCache.get(sprite);
    }

    public static void addActionButton(int id, int sprite, int sprite2, int width, int height, String s) {
        Widget rsi = cache[id] = new Widget();
        rsi.enabledSprite = Client.spriteCache.get(sprite);
        if (sprite2 == sprite)
            rsi.disabledSprite = Client.spriteCache.get(sprite);
        else
            rsi.disabledSprite = Client.spriteCache.get(sprite2);
        rsi.tooltip = s;
        rsi.contentType = 0;
        rsi.optionType = 1;
        rsi.width = width;
        rsi.hoverType = 52;
        rsi.parent = id;
        rsi.id = id;
        rsi.type = 5;
        rsi.height = height;
    }

    public static void addToggleButton(int id, int sprite, int setconfig, int width, int height, String s) {
        Widget rsi = addInterface(id);
        rsi.enabledSprite = Client.spriteCache.get(sprite);
        rsi.disabledSprite = Client.spriteCache.get(sprite);
        rsi.requiredValues = new int[1];
        rsi.requiredValues[0] = 1;
        rsi.valueCompareType = new int[1];
        rsi.valueCompareType[0] = 1;
        rsi.valueIndexArray = new int[1][3];
        rsi.valueIndexArray[0][0] = 5;
        rsi.valueIndexArray[0][1] = setconfig;
        rsi.valueIndexArray[0][2] = 0;
        rsi.optionType = 4;
        rsi.width = width;
        rsi.hoverType = -1;
        rsi.parent = id;
        rsi.id = id;
        rsi.type = 5;
        rsi.height = height;
        rsi.tooltip = s;
    }

    public void totalChildren(int id, int x, int y) {
        children = new int[id];
        child_x = new int[x];
        child_y = new int[y];
    }

    public static void removeSomething(int id) {
        @SuppressWarnings("unused")
        Widget rsi = cache[id] = new Widget();
    }

    protected static void addTransparentSprite(int id, int spriteId, int transparency) {
        Widget tab = cache[id] = new Widget();
        tab.id = id;
        tab.parent = id;
        tab.type = 5;
        tab.optionType = 0;
        tab.contentType = 0;
        tab.transparency = transparency;
        tab.hoverType = 52;
        tab.enabledSprite = Client.spriteCache.get(spriteId);
        tab.disabledSprite = Client.spriteCache.get(spriteId);
        tab.width = 512;
        tab.height = 334;
        tab.drawsTransparent = true;
    }

    public static void addItemOnInterface(int childId, int interfaceId, String[] options, int invSpritePadX,
                                          int invSpritePadY, int height, int width) {
        Widget rsi = cache[childId] = new Widget();
        rsi.actions = options == null ? null : new String[10];
        rsi.inventoryOffsetX = new int[20];
        rsi.inventoryAmounts = new int[height * width];
        rsi.inventoryItemId = new int[height * width];
        rsi.itemOpacity = new int[height * width];
        rsi.inventoryOffsetY = new int[20];
        rsi.children = new int[0];
        rsi.child_x = new int[0];
        rsi.child_y = new int[0];
        if (rsi.actions != null) {
            for (int i = 0; i < rsi.actions.length; i++) {
                if (i < options.length) {
                    if (options[i] != null) {
                        rsi.actions[i] = options[i];
                    }
                }
            }
        }

        rsi.centerText = true;
        rsi.filled = false;
        rsi.replaceItems = false;
        rsi.usableItems = false;
        //rsi.isInventoryInterface = false;
        rsi.allowSwapItems = false;
        rsi.textShadow = false;
        rsi.inventoryMarginX = invSpritePadX; // 57
        rsi.inventoryMarginY = invSpritePadY; // 45
        rsi.height = height;
        rsi.width = width;
        rsi.parent = childId;
        rsi.id = interfaceId;
        rsi.type = TYPE_INVENTORY;
    }

    public static void addItemOnInterface(int childId, int interfaceId, String[] options, boolean displayExamine) {
        Widget rsi = cache[childId] = new Widget();
        rsi.actions = new String[5];
        rsi.inventoryOffsetX = new int[20];
        rsi.inventoryItemId = new int[30];
        rsi.inventoryAmounts = new int[30];
        rsi.itemOpacity = new int[30];
        rsi.inventoryOffsetY = new int[20];
        rsi.children = new int[0];
        rsi.child_x = new int[0];
        rsi.child_y = new int[0];
        for (int i = 0; i < rsi.actions.length; i++) {
            if (i < options.length) {
                if (options[i] != null) {
                    rsi.actions[i] = options[i];
                }
            }
        }
        rsi.centerText = true;
        rsi.filled = false;
        rsi.replaceItems = false;
        rsi.usableItems = false;
        rsi.displayExamine = displayExamine;
        rsi.allowSwapItems = false;
        rsi.inventoryMarginX = 4;
        rsi.inventoryMarginY = 5;
        rsi.height = 1;
        rsi.width = 1;
        rsi.parent = interfaceId;
        rsi.id = childId;
        rsi.type = TYPE_INVENTORY;
    }

    public static void addText(int id, String text, AdvancedFont tda[], int idx, int color, boolean center, boolean shadow, int hoverColour, String tooltip, int widthHover) {
        Widget tab = addTabInterface(id);
        tab.parent = id;
        tab.id = id;
        tab.tooltip = tooltip;
        tab.type = 4;
        tab.optionType = 4;
        tab.width = widthHover;
        tab.height = 15;
        tab.contentType = 0;
        tab.opacity = 0;
        tab.hoverType = -1;
        tab.centerText = center;
        tab.textShadow = shadow;
        tab.text_type = tda[idx];
        tab.defaultText = text;
        tab.secondaryText = "";
        tab.textColour = color;
        tab.secondaryColor = 0;
        tab.defaultHoverColor = hoverColour;
        tab.secondaryHoverColor = 0;
    }

    public static void addClickableText(int id, String text, String tooltip, AdvancedFont tda[], int idx, int color, boolean center, boolean shadow, int width) {
        Widget tab = addTabInterface(id);
        tab.parent = id;
        tab.id = id;
        tab.type = 4;
        tab.optionType = 1;
        tab.width = width;
        tab.height = 11;
        tab.contentType = 0;
        tab.opacity = 0;
        tab.hoverType = -1;
        tab.centerText = center;
        tab.textShadow = shadow;
        tab.text_type = tda[idx];
        tab.defaultText = text;
        tab.secondaryText = "";
        tab.textColour = color;
        tab.secondaryColor = 0;
        tab.defaultHoverColor = 0xffffff;
        tab.secondaryHoverColor = 0;
        tab.tooltip = tooltip;
    }

    public static void addClickableText(int id, String text, String tooltip, AdvancedFont tda[], int idx, int color, boolean center, boolean shadow, int width, int height) {
        Widget tab = addTabInterface(id);
        tab.parent = id;
        tab.id = id;
        tab.type = 4;
        tab.optionType = 1;
        tab.width = width;
        tab.height = height;
        tab.contentType = 0;
        tab.opacity = 0;
        tab.hoverType = -1;
        tab.centerText = center;
        tab.textShadow = shadow;
        tab.text_type = tda[idx];
        tab.defaultText = text;
        tab.secondaryText = "";
        tab.textColour = color;
        tab.secondaryColor = 0;
        tab.defaultHoverColor = 0xffffff;
        tab.secondaryHoverColor = 0;
        tab.tooltip = tooltip;
    }

    public static void addCustomClickableText(int id, String text, String tooltip, AdvancedFont[] tda, int idx, int color, boolean center, boolean shadow, int width, int height) {
        Widget tab = addTabInterface(id);
        tab.parent = id;
        tab.id = id;
        tab.type = 4;
        tab.optionType = 1;
        tab.width = width;
        tab.height = height;
        tab.contentType = 0;
        tab.opacity = 0;
        tab.hoverType = -1;
        tab.centerText = center;
        tab.textShadow = shadow;
        tab.text_type = tda[idx];
        tab.defaultText = text;
        tab.secondaryText = "";
        tab.textColour = color;
        tab.secondaryColor = 0;
        tab.defaultHoverColor = 0xffffff;
        tab.secondaryHoverColor = 0;
        tab.tooltip = tooltip;
    }

    public static void addHoverText3(int id, String text, String tooltip, AdvancedFont[] tda, int idx, int color, boolean center, boolean textShadow, int width, int hoverColor) {
        Widget rsinterface = addInterface(id);
        rsinterface.id = id;
        rsinterface.parent = id;
        rsinterface.type = 4;
        rsinterface.optionType = 1;
        rsinterface.width = width;
        rsinterface.height = 11;
        rsinterface.contentType = 0;
        rsinterface.opacity = 0;
        rsinterface.hoverType = -1;
        rsinterface.centerText = center;
        rsinterface.textShadow = textShadow;
        rsinterface.text_type = tda[idx];
        rsinterface.defaultText = text;
        rsinterface.secondaryText = "";
        rsinterface.tooltip = tooltip;
        rsinterface.textColour = color;
        rsinterface.secondaryColor = 0;
        rsinterface.defaultHoverColor = hoverColor;
        rsinterface.secondaryHoverColor = 0;
    }

    public static void addText(int id, String text, AdvancedFont[] tda, int idx, int color, boolean center,
                               boolean shadow) {
        Widget tab = addTabInterface(id);
        tab.parent = id;
        tab.id = id;
        tab.type = 4;
        tab.optionType = 0;
        tab.width = 0;
        tab.height = 11;
        tab.contentType = 0;
        tab.opacity = 0;
        tab.hoverType = -1;
        tab.centerText = center;
        tab.textShadow = shadow;
        tab.text_type = tda[idx];
        tab.defaultText = text;
        tab.secondaryText = "";
        tab.textColour = color;
        tab.secondaryColor = 0;
        tab.defaultHoverColor = 0;
        tab.secondaryHoverColor = 0;
    }

    public static void addText(int id, String text, int textColour, boolean center, boolean shadow, int hoverType, AdvancedFont[] font, int fontIndex) {
        Widget widget = addInterface(id);
        widget.parent = id;
        widget.id = id;
        widget.type = 4;
        widget.optionType = 0;
        widget.width = 0;
        widget.height = 0;
        widget.contentType = 0;
        widget.opacity = 0;
        widget.hoverType = hoverType;
        widget.centerText = center;
        widget.textShadow = shadow;
        widget.text_type = font[fontIndex];
        widget.defaultText = text;
        widget.secondaryText = "";
        widget.textColour = textColour;
    }

    public static void addConfigButton(int ID, int pID, int bID, int bID2, int width, int height, String tT,
                                       int configID, int aT, int configFrame) {
        Widget Tab = addTabInterface(ID);
        Tab.parent = pID;
        Tab.id = ID;
        Tab.type = 5;
        Tab.optionType = aT;
        Tab.contentType = 0;
        Tab.width = width;
        Tab.height = height;
        Tab.opacity = 0;
        Tab.hoverType = -1;
        Tab.valueCompareType = new int[1];
        Tab.requiredValues = new int[1];
        Tab.valueCompareType[0] = 1;
        Tab.requiredValues[0] = configID;
        Tab.valueIndexArray = new int[1][3];
        Tab.valueIndexArray[0][0] = 5;
        Tab.valueIndexArray[0][1] = configFrame;
        Tab.valueIndexArray[0][2] = 0;
        Tab.disabledSprite = Client.spriteCache.get(bID);// imageLoader(bID, bName);
        Tab.enabledSprite = Client.spriteCache.get(bID2);
        Tab.tooltip = tT;
    }

    /**
     * Adds a button with a config
     *
     * @param id             The widget id
     * @param parent         The main widget
     * @param disabledSprite The primary sprite
     * @param enabledSprite  The secondary sprite
     * @param tooltip        The tooltip text
     * @param configId       The actual config id
     * @param configState    The config state either 0 or 1 (off or on)
     * @param actionType     The action type
     */
    public static void addConfigButton(int id, int parent, int disabledSprite, int enabledSprite, String tooltip, int configId, int configState, int actionType) {
        Widget widget = addTabInterface(id);
        widget.parent = parent;
        widget.id = id;
        widget.type = 5;
        widget.optionType = actionType;
        widget.contentType = 0;
        widget.disabledSprite = disabledSprite < 0 ? null : Client.spriteCache.get(disabledSprite);
        widget.enabledSprite = enabledSprite < 0 ? null : Client.spriteCache.get(enabledSprite);
        if (disabledSprite > 0 && widget.disabledSprite != null)
            widget.width = widget.disabledSprite.width;
        if (enabledSprite > 0 && widget.enabledSprite != null)
            widget.height = widget.enabledSprite.height;
        widget.opacity = 0;
        widget.hoverType = -1;
        widget.valueCompareType = new int[1];
        widget.requiredValues = new int[1];
        widget.valueCompareType[0] = 1;
        widget.requiredValues[0] = configState;
        widget.valueIndexArray = new int[1][3];
        widget.valueIndexArray[0][0] = 5;
        widget.valueIndexArray[0][1] = configId;
        widget.valueIndexArray[0][2] = 0;
        widget.tooltip = tooltip;

       /* if(parent == 63405) {
            System.out.println(configId);
            System.out.println(configState);
        }*/
    }

    public static void addConfigButton(int identification, int parentIdentification, int disabledSprite, int enabledSprite, int width, int height, String tooltip, int configIdentification, int actionType, int configFrame, boolean updateConfig) {
        Widget widget = addTabInterface(identification);
        widget.parent = parentIdentification;
        widget.id = identification;
        widget.type = 5;
        widget.optionType = actionType;
        widget.contentType = 0;
        widget.width = width;
        widget.height = height;
        widget.opacity = 0;
        widget.hoverType = -1;
        widget.valueCompareType = new int[1];
        widget.requiredValues = new int[1];
        widget.valueCompareType[0] = 1;
        widget.requiredValues[0] = configIdentification;
        widget.valueIndexArray = new int[1][3];
        widget.valueIndexArray[0][0] = 5;
        widget.valueIndexArray[0][1] = configFrame;
        widget.valueIndexArray[0][2] = 0;
        widget.disabledSprite = Client.spriteCache.get(disabledSprite);
        widget.enabledSprite = Client.spriteCache.get(enabledSprite);
        widget.tooltip = tooltip;
        widget.updateConfig = updateConfig;
    }

    public static void addSprite(int id, int spriteId, String spriteName) {
        Widget tab = cache[id] = new Widget();
        tab.id = id;
        tab.parent = id;
        tab.type = 5;
        tab.optionType = 0;
        tab.contentType = 0;
        tab.opacity = (byte) 0;
        tab.hoverType = 52;
        tab.enabledSprite = imageLoader(spriteId, spriteName);
        tab.disabledSprite = imageLoader(spriteId, spriteName);
        tab.width = 512;
        tab.height = 334;
    }

    public static void addHoverText(int id, String text, String tooltip, AdvancedFont advancedFonts[], int idx, int color, boolean centerText, boolean textShadowed, int width) {
        Widget widget = addInterface(id);
        widget.id = id;
        widget.parent = id;
        widget.type = 4;
        widget.optionType = 1;
        widget.width = width;
        widget.height = 11;
        widget.contentType = 0;
        widget.opacity = 0;
        widget.hoverType = -1;
        widget.centerText = centerText;
        widget.textShadow = textShadowed;
        widget.text_type = advancedFonts[idx];
        widget.defaultText = text;
        if (text.contains("<br>")) {
            widget.height += 11;
        }
        widget.secondaryText = "";
        widget.tooltip = tooltip;
        widget.textColour = color;
        widget.secondaryColor = 0;
        widget.defaultHoverColor = 0xFFFFFF;
        widget.secondaryHoverColor = 0;
    }

    public static void addTransButtonHover(int id, int sid, String tooltip, int transparency, boolean selected) {
        Widget tab = cache[id] = new Widget();
        tab.id = id;
        tab.parent = id;
        tab.type = 5;
        tab.optionType = 1;
        tab.contentType = 0;
        tab.opacity = (byte) 0;
        tab.hoverType = 52;
        tab.disabledSprite = Client.spriteCache.get(sid);// imageLoader(sid, spriteName);
        tab.enabledSprite = Client.spriteCache.get(sid);// imageLoader(sid, spriteName);
        tab.width = tab.disabledSprite.width;
        tab.height = tab.enabledSprite.height;
        tab.drawsTransparent = true;
        tab.transparency = transparency;
        tab.tooltip = tooltip;
        //tab.mouseOver = false;
        //tab.selected = selected;
    }

    public static void addHoverText(int id, String text, String tooltip, AdvancedFont[] tda, int idx, int color, boolean center, boolean textShadow, int width, int height, int hoveredColor) {
        Widget rsinterface = addInterface(id);
        rsinterface.id = id;
        rsinterface.parent = id;
        rsinterface.type = 4;
        rsinterface.optionType = 1;
        rsinterface.width = width;
        rsinterface.height = height;
        rsinterface.contentType = 0;
        rsinterface.opacity = 0;
        rsinterface.hoverType = -1;
        rsinterface.centerText = center;
        rsinterface.textShadow = textShadow;
        rsinterface.text_type = tda[idx];
        rsinterface.defaultText = text;
        rsinterface.secondaryText = "";
        rsinterface.textColour = color;
        rsinterface.secondaryColor = 0;
        rsinterface.defaultHoverColor = hoveredColor;
        rsinterface.secondaryHoverColor = 0;
        rsinterface.tooltip = tooltip;
    }

    /* Add Hoverable Texts */
    public static void addHoverText(int id, String text, String tooltip, AdvancedFont[] font, int idx, int color, int hoverColor, boolean centerText, boolean textShadowed, int width) {
        Widget rsinterface = addInterface(id);
        rsinterface.id = id;
        rsinterface.parent = id;
        rsinterface.type = 4;
        rsinterface.optionType = 1;
        rsinterface.width = width;
        rsinterface.height = 11;
        rsinterface.contentType = 0;
        rsinterface.opacity = 0;
        rsinterface.hoverType = -1;
        rsinterface.centerText = centerText;
        rsinterface.textShadow = textShadowed;
        rsinterface.text_type = font[idx];
        rsinterface.defaultText = text;
        rsinterface.secondaryText = "";
        rsinterface.tooltip = tooltip;
        rsinterface.textColour = color;
        rsinterface.secondaryColor = 0;
        rsinterface.defaultHoverColor = hoverColor;
        rsinterface.secondaryHoverColor = 0;
    }


    public static void addHoverText(int id, String text, String tooltip, AdvancedFont[] font, int idx, int color, boolean center, boolean textShadow, int width, int hoveredColor) {
        Widget rsinterface = addInterface(id);
        rsinterface.id = id;
        rsinterface.parent = id;
        rsinterface.type = 4;
        rsinterface.optionType = 1;
        rsinterface.width = width;
        rsinterface.height = 13;
        rsinterface.contentType = 0;
        rsinterface.opacity = 0;
        rsinterface.hoverType = -1;
        rsinterface.centerText = center;
        rsinterface.textShadow = textShadow;
        rsinterface.text_type = font[idx];
        rsinterface.defaultText = text;
        rsinterface.secondaryText = "";
        rsinterface.textColour = color;
        rsinterface.secondaryColor = 0;
        rsinterface.defaultHoverColor = 0xffffff;
        rsinterface.secondaryHoverColor = 0;
        rsinterface.tooltip = tooltip;
    }

    public static void addHoveredConfigButton(Widget original, int ID, int IMAGEID, int disabledID, int enabledID) {
        Widget rsint = addTabInterface(ID);
        rsint.parent = original.id;
        rsint.id = ID;
        rsint.type = 0;
        rsint.optionType = 0;
        rsint.contentType = 0;
        rsint.width = original.width;
        rsint.height = original.height;
        rsint.opacity = 0;
        rsint.hoverType = -1;
        Widget hover = addInterface(IMAGEID);
        hover.type = 5;
        hover.width = original.width;
        hover.height = original.height;
        hover.valueCompareType = original.valueCompareType;
        hover.requiredValues = original.requiredValues;
        hover.valueIndexArray = original.valueIndexArray;
        if (disabledID != -1)
            hover.enabledSprite = Client.spriteCache.get(disabledID);
        if (enabledID != -1)
            hover.disabledSprite = Client.spriteCache.get(enabledID);
        rsint.totalChildren(1);
        setBounds(IMAGEID, 0, 0, 0, rsint);
        rsint.tooltip = original.tooltip;
        rsint.invisible = true;
    }

    public static void addHoverConfigButton(int id, int hoverOver, int disabledID, int enabledID, int width, int height, String tooltip, int[] valueCompareType, int[] requiredValues, int[][] valueIndexArray) {
        Widget rsint = addTabInterface(id);
        rsint.parent = id;
        rsint.id = id;
        rsint.type = 5;
        rsint.optionType = 5;
        rsint.contentType = 206;
        rsint.width = width;
        rsint.height = height;
        rsint.opacity = 0;
        rsint.hoverType = hoverOver;
        rsint.valueCompareType = valueCompareType;
        rsint.requiredValues = requiredValues;
        rsint.valueIndexArray = valueIndexArray;
        if (disabledID != -1)
            rsint.enabledSprite = Client.spriteCache.get(disabledID);
        if (enabledID != -1)
            rsint.disabledSprite = Client.spriteCache.get(enabledID);
        rsint.tooltip = tooltip;
    }

    public static void addButton(int i, int parent, int sprite1, int sprite2, int hoverOver, String tooltip) {
        Widget p = addInterface(i);
        p.parent = parent;
        p.type = TYPE_SPRITE;
        p.type = 1;
        p.width = Client.spriteCache.get(sprite1).width;
        p.height = Client.spriteCache.get(sprite1).height;
        p.tooltip = tooltip;
        p.defaultText = tooltip;
        p.hoverType = hoverOver;
        p.disabledSprite = Client.spriteCache.get(sprite1);
        p.enabledSprite = Client.spriteCache.get(sprite1);
    }

    public static void addButton(int id, SimpleImage enabled, SimpleImage disabled, String tooltip, int w, int h) {
        Widget tab = cache[id] = new Widget();
        tab.id = id;
        tab.parent = id;
        tab.type = 5;
        tab.optionType = 1;
        tab.contentType = 0;
        tab.opacity = (byte) 0;
        tab.hoverType = 52;
        tab.enabledSprite = disabled;
        tab.disabledSprite = enabled;
        tab.width = w;
        tab.height = h;
        tab.tooltip = tooltip;
    }

    public static void addSpriteLoader(int childId, int spriteId) {
        Widget rsi = cache[childId] = new Widget();
        rsi.id = childId;
        rsi.parent = childId;
        rsi.type = 5;
        rsi.optionType = 0;
        rsi.contentType = 0;
        rsi.enabledSprite = Client.spriteCache.get(spriteId);
        rsi.disabledSprite = Client.spriteCache.get(spriteId);
        rsi.width = rsi.enabledSprite.width;
        rsi.height = rsi.disabledSprite.height - 2;
    }

    public static void handleConfigSpriteHover(Widget widget) {
        widget.active = !widget.active;

        configHoverButtonSwitch(widget);
        disableOtherButtons(widget);
    }

    public static void adjustableConfig(int id, String tooltip, int sprite, int opacity, int enabledSpriteBehind, int disabledSpriteBehind) {
        Widget tab = addInterface(id);
        tab.tooltip = tooltip;
        tab.optionType = OPTION_OK;
        tab.type = TYPE_ADJUSTABLE_CONFIG;
        tab.disabledSprite = Client.spriteCache.get(sprite);
        tab.enabledAltSprite = Client.spriteCache.get(enabledSpriteBehind);
        tab.disabledAltSprite = Client.spriteCache.get(disabledSpriteBehind);
        tab.width = tab.enabledAltSprite.width;
        tab.height = tab.disabledAltSprite.height;
        tab.spriteOpacity = opacity;
    }

    public static void addSprite(int childId, SimpleImage sprite1, SimpleImage sprite2) {
        Widget rsi = cache[childId] = new Widget();
        rsi.id = childId;
        rsi.parent = childId;
        rsi.type = 5;
        rsi.optionType = 0;
        rsi.contentType = 0;
        rsi.enabledSprite = sprite1;
        rsi.disabledSprite = sprite2;
        rsi.width = rsi.enabledSprite.width;
        rsi.height = rsi.disabledSprite.height - 2;
    }

    public static void addHoverButton(final int id, final String imageName, final int spriteId, final int width, final int height, final String text, final int contentType, final int hoverType, final int optionType) {
        final Widget tab = addTabInterface(id);
        tab.id = id;
        tab.parent = id;
        tab.type = 5;
        tab.optionType = optionType;
        tab.contentType = contentType;
        tab.opacity = 0;
        tab.hoverType = hoverType;
        tab.enabledSprite = imageLoader(spriteId, imageName);
        tab.disabledSprite = imageLoader(spriteId, imageName);
        tab.width = width;
        tab.height = height;
        tab.tooltip = text;
    }

    public static void addHoveredButton(final int id, final String imageName, final int j, final int w, final int h, final int IMAGEID) {
        final Widget tab = addTabInterface(id);
        tab.parent = id;
        tab.id = id;
        tab.type = 0;
        tab.optionType = 0;
        tab.width = w;
        tab.height = h;
        tab.invisible = true;
        tab.opacity = 0;
        tab.hoverType = -1;
        tab.scrollMax = 0;
        addHoverImage(IMAGEID, j, j, imageName);
        tab.totalChildren(1);
        tab.child(0, IMAGEID, 0, 0);
    }

    private static int getFreeIndex() {
        for (int i = 0; i < cache.length; i++) {
            if (cache[i] == null) {
                return i;
            }
        }
        return -1;
    }

    // Add these to your sprite class or replace their usages in the createSprite code
    public static SimpleImage fetchSprite(final String name, final Archive graphics) {
        final int index = name.lastIndexOf(",");
        return Widget.getSprite(Integer.parseInt(name.substring(index + 1)), graphics, name.substring(0, index));
    }

    public static SimpleImage fetchSprite(final String name) {
        return fetchSprite(name, Widget.interfaceLoader);
    }

    // Add these to your interface class
    /*public static Widget createSprite(final int parentIndex, final int index, final int disabled, final int enabled) {
        //final SimpleImage disabledSprite = SimpleImage.fetchSprite(disabled);
        //final SimpleImage enabledSprite = SimpleImage.fetchSprite(enabled);
        final SimpleImage disabledSprite = Client.spriteCache.get(disabled);
        final SimpleImage enabledSprite = Client.spriteCache.get(enabled);
        final Widget sprite = cache[index] = new Widget();
        sprite.id = index;
        sprite.parent = parentIndex;
        sprite.type = 5;
        sprite.width = disabledSprite.width;
        sprite.height = disabledSprite.height;
        sprite.disabledSprite = disabledSprite;
        sprite.enabledSprite = enabledSprite;
        return sprite;
    }*/

    /*public static Widget createSprite(final int parentIndex, final int disabled, final int enabled) {
        final int index = getFreeIndex();
        if (index < 0) {
            throw new IllegalStateException("Interface cache full; expand the size of the array before attempting to create a component!");
        }
        return createSprite(parentIndex, index, disabled, enabled);
    }*/

    public static int getIndexOfChild(final int parent, final int child) {
        final Widget rsi = cache[parent];
        for (int i = 0; i < rsi.children.length; i++) {
            if (rsi.children[i] == child) {
                return i;
            }
        }
        return -1;
    }

    public static void addChild(final int parentIndex, final int childIndex, final int x, final int y, final int index) {
        final Widget parent = cache[parentIndex];
        final int[] childX = new int[parent.child_x.length + 1];
        final int[] childY = new int[parent.child_y.length + 1];
        final int[] children = new int[parent.children.length + 1];
        System.arraycopy(parent.child_x, 0, childX, 0, index);
        System.arraycopy(parent.child_x, index, childX, index + 1, parent.child_x.length - index);
        System.arraycopy(parent.child_y, 0, childY, 0, parent.child_y.length);
        System.arraycopy(parent.child_y, index, childY, index + 1, parent.child_y.length - index);
        System.arraycopy(parent.children, 0, children, 0, index);
        System.arraycopy(parent.children, index, children, index + 1, parent.children.length - index);
        childX[index] = x;
        childY[index] = y;
        children[index] = childIndex;
        parent.child_x = childX;
        parent.child_y = childY;
        parent.children = children;
    }

    private static void addChild(final int x, final int y, final int child, final Widget parent) {
        final int[] childX = new int[parent.child_x.length + 1];
        final int[] childZ = new int[parent.child_y.length + 1];
        final int[] children = new int[parent.children.length + 1];
        System.arraycopy(parent.child_x, 0, childX, 0, parent.child_x.length);
        System.arraycopy(parent.child_y, 0, childZ, 0, parent.child_y.length);
        System.arraycopy(parent.children, 0, children, 0, parent.children.length);
        childX[childX.length - 1] = x;
        childZ[childZ.length - 1] = y;
        children[children.length - 1] = child;
        parent.child_x = childX;
        parent.child_y = childZ;
        parent.children = children;
    }

    public static void addChild(final int x, final int y, final Widget parent, final Widget child) {
        addChild(x, y, child.id, parent);
    }

    public static Widget createSprite(final int parentIndex, final int index, final int disabled, final int enabled) {
        final SimpleImage spriteDisabled = Client.spriteCache.get(disabled);
        final SimpleImage spriteEnabled = Client.spriteCache.get(enabled);
        final Widget sprite = cache[index] = new Widget();
        sprite.id = index;
        sprite.parent = parentIndex;
        sprite.type = 5;
        sprite.width = spriteDisabled.width;
        sprite.height = spriteDisabled.height;
        sprite.enabledSprite = spriteDisabled;
        sprite.disabledSprite = spriteEnabled;
        return sprite;
    }

    public static Widget createSprite(final int parentIndex, final int disabled, final int enabled) {
        final int index = getFreeIndex();
        if (index < 0) {
            throw new IllegalStateException("Interface cache full; expand the size of the array before attempting to create a component");
        }
        return createSprite(parentIndex, index, disabled, enabled);
    }

    public static List<Widget> createContainerlessHoverButton(final int x, final int y, final int parentIndex, final int unhovered, final int hovered, final String tooltip) {
        final Widget unhoveredButton = createSprite(parentIndex, unhovered, unhovered);
        final Widget hoveredButton = createSprite(parentIndex, hovered, hovered);
        unhoveredButton.optionType = 1;
        unhoveredButton.tooltip = tooltip;
        unhoveredButton.hoverType = hoveredButton.id;
        hoveredButton.hoverType = hoveredButton.id;
        hoveredButton.drawsTransparent = true;
        addChild(x, y, cache[parentIndex], unhoveredButton);
        addChild(x, y, cache[parentIndex], hoveredButton);
        return Stream.of(unhoveredButton, hoveredButton).collect(Collectors.toList());
    }

    public static void addHoverButton(int id, int spriteId, int width, int height, String text, int contentType, int hoverType, int action) {// hoverable
        // button
        Widget tab = addTabInterface(id);
        tab.id = id;
        tab.parent = id;
        tab.type = 5;
        tab.optionType = action;
        tab.contentType = contentType;
        tab.opacity = 0;
        tab.hoverType = hoverType;
        tab.enabledSprite = spriteId < 0 ? null : Client.spriteCache.get(spriteId);
        tab.disabledSprite = spriteId < 0 ? null : Client.spriteCache.get(spriteId);
        tab.width = width;
        tab.height = height;
        tab.tooltip = text;
    }

    public static void addHoveredButton(int id, int spriteId, int width, int height, int imageId) {// hoverable
        // button
        Widget tab = addTabInterface(id);
        tab.parent = id;
        tab.id = id;
        tab.type = 0;
        tab.optionType = 0;
        tab.width = width;
        tab.height = height;
        tab.invisible = true;
        tab.opacity = 0;
        tab.hoverType = -1;
        tab.scrollMax = 0;
        addHoverImage_sprite_loader(imageId, spriteId);
        tab.totalChildren(1);
        tab.child(0, imageId, 0, 0);
    }

    public static void addHDHoverButton_sprite_loader(int i, int spriteId, int width, int height, String text,
                                                      int contentType, int hoverOver, int aT) {// hoverable
        // button
        Widget tab = addTabInterface(i);
        tab.id = i;
        tab.parent = i;
        tab.type = 5;
        tab.optionType = aT;
        tab.contentType = contentType;
        tab.opacity = 0;
        tab.hoverType = hoverOver;
        tab.disabledSprite = Client.spriteCache.get(spriteId);
        tab.enabledSprite = Client.spriteCache.get(spriteId);
        tab.width = width;
        tab.height = height;
        tab.tooltip = text;
        tab.hightDetail = true;
    }

    public static void addHDHoveredButton_sprite_loader(int i, int spriteId, int w, int h, int IMAGEID) {// hoverable
        // button
        Widget tab = addTabInterface(i);
        tab.parent = i;
        tab.id = i;
        tab.type = 0;
        tab.optionType = 0;
        tab.width = w;
        tab.height = h;
        tab.invisible = true;
        tab.opacity = 0;
        tab.hoverType = -1;
        tab.scrollMax = 0;
        addHDHoverImage_sprite_loader(IMAGEID, spriteId);
        tab.totalChildren(1);
        tab.child(0, IMAGEID, 0, 0);
    }

    public static void addHDHoverImage_sprite_loader(int i, int spriteId) {
        Widget tab = addTabInterface(i);
        tab.id = i;
        tab.parent = i;
        tab.type = 5;
        tab.optionType = 0;
        tab.contentType = 0;
        tab.width = 512;
        tab.height = 334;
        tab.opacity = 0;
        tab.hoverType = 52;
        tab.hightDetail = true;
        tab.disabledSprite = Client.spriteCache.get(spriteId);
        tab.enabledSprite = Client.spriteCache.get(spriteId);
    }

    public static void addHoverImage_sprite_loader(int id, int spriteId) {
        Widget tab = addTabInterface(id);
        tab.id = id;
        tab.parent = id;
        tab.type = 5;
        tab.optionType = 0;
        tab.contentType = 0;
        //int w = Client.spriteCache.get(spriteId).myWidth;
        //int h = Client.spriteCache.get(spriteId).myHeight;
        tab.width = 512;
        tab.height = 334;
        tab.opacity = 0;
        tab.hoverType = 52;
        tab.enabledSprite = Client.spriteCache.get(spriteId);
        tab.disabledSprite = Client.spriteCache.get(spriteId);
    }

    public static void addHDSprites(int id, int spriteId) {
        Widget widget = addInterface(id);
        widget.id = id;
        widget.parent = id;
        widget.type = 5;
        widget.optionType = 0;
        widget.contentType = 0;
        widget.hoverType = 52;
        widget.enabledSprite = Client.spriteCache.get(spriteId);
        widget.disabledSprite = Client.spriteCache.get(spriteId);
        widget.hightDetail = true;
        widget.opacity = 64;
        widget.width = 512;
        widget.height = 334;
    }

    public static void addHoverImage(int i, int j, int k, String name) {
        Widget tab = addTabInterface(i);
        tab.id = i;
        tab.parent = i;
        tab.type = 5;
        tab.optionType = 0;
        tab.contentType = 0;
        tab.width = 512;
        tab.height = 334;
        tab.opacity = 0;
        tab.hoverType = 52;
        tab.enabledSprite = imageLoader(j, name);
        tab.disabledSprite = imageLoader(k, name);
    }

    private static Widget createInterface(int id) {
        if (ClientConstants.DEBUG_INTERFACES && cache[id] != null) {
            System.out.println("overwritten interface: " + id);
        }
        Widget widget = new Widget();
        cache[id] = widget;
        return widget;
    }

    public static Widget addFullScreenInterface(int id) {
        Widget rsi = createInterface(id);
        rsi.id = id;
        rsi.parent = id;
        rsi.width = 765;
        rsi.height = 503;
        return rsi;
    }

    protected static Widget createWidget(int id, int width, int height) {
        Widget widget = cache[id] = new Widget();
        widget.id = id;
        widget.parent = id;
        widget.type = 0;
        widget.optionType = 0;
        widget.contentType = 0;
        widget.width = width;
        widget.height = height;
        widget.opacity = 0;
        widget.hoverType = -1;
        return widget;
    }

    public static Widget addTabInterface(int id) {
        Widget tab = cache[id] = new Widget();
        tab.id = id;// 250
        tab.parent = id;// 236
        tab.type = 0;// 262
        tab.optionType = 0;// 217
        tab.contentType = 0;
        tab.width = 512;// 220
        tab.height = 700;// 267
        tab.opacity = (byte) 0;
        tab.hoverType = -1;// Int 230
        return tab;
    }

    public static Widget addTabInterface(int id, Widget toClone) {

        Widget tab = cache[id] = new Widget();
        tab.id = id;
        tab.parent = toClone.parent;
        tab.type = toClone.type;
        tab.optionType = toClone.optionType;
        tab.contentType = toClone.contentType;
        tab.width = toClone.width;
        tab.height = toClone.height;
        tab.opacity = toClone.opacity;
        tab.hoverType = toClone.hoverType;

        return tab;
    }

    public void copyAndAddChildren(int amount) {
        //System.out.println("Old length: " + children.length);
        int[] newChildren = new int[this.children.length + amount];
        for (int i = 0; i < this.children.length; i++) {
            newChildren[i] = this.children[i];
        }

        this.children = newChildren;

        int[] newX = new int[this.child_x.length + amount];

        for (int i = 0; i < this.child_x.length; i++) {
            newX[i] = this.child_x[i];
        }

        this.child_x = newX;

        int[] newY = new int[this.child_y.length + amount];

        for (int i = 0; i < this.child_y.length; i++) {
            newY[i] = this.child_y[i];
        }

        this.child_y = newY;

        //System.out.println("New length: " + children.length);
    }

    protected static SimpleImage imageLoader(int id, String root) {
        long l = (StringUtils.hashSpriteName(root) << 8) + (long) id;
        SimpleImage sprite = (SimpleImage) spriteCache.get(l);
        if (sprite != null)
            return sprite;
        try {
            sprite = new SimpleImage(root + " " + id);
            spriteCache.put(sprite, l);
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
        return sprite;
    }

    public void child(int id, int widgetId, int x, int y) {
        children[id] = widgetId;
        if (ClientConstants.CHECK_DUPLICATE_INTERFACES_IDS && foundChildren.contains(widgetId)) {
            System.out.println("Tried to add widget(child) " + widgetId + " but it already existed");
        }
        foundChildren.add(widgetId);

        child_x[id] = x;
        child_y[id] = y;
        childCount++;
    }

    private int childCount = 0;

    public void child(int widgetId, int x, int y) {
        //System.out.println("Used id: " + childCount);
        children[childCount] = widgetId;
        if (ClientConstants.CHECK_DUPLICATE_INTERFACES_IDS && foundChildren.contains(widgetId)) {
            System.out.println("Tried to add widget(child) " + widgetId + " but it already existed");
        }
        foundChildren.add(widgetId);

        child_x[childCount] = x;
        child_y[childCount] = y;

        childCount++;
    }

    public void totalChildren(int children) {
        this.children = new int[children];
        child_x = new int[children];
        child_y = new int[children];
    }

    private Model get_model(int opcode, int id) {
        Model model = (Model) models.get((opcode << 16) + id);
        if (model != null)
            return model;

        if (opcode == 1)
            model = Model.get(id);

        if (opcode == 2)
            model = NpcDefinition.get(id).get_dialogue_model();

        if (opcode == 3)
            model = Client.local_player.get_dialogue_model();

        if (opcode == 4)
            model = ItemDefinition.get(id).get_widget_model(50);

        if (opcode == 5)
            model = null;

        if (model != null)
            models.put(model, (opcode << 16) + id);

        return model;
    }

    public static SimpleImage getSprite(int i, Archive streamLoader, String s) {
        long l = (StringUtils.hashSpriteName(s) << 8) + (long) i;
        SimpleImage sprite = (SimpleImage) spriteCache.get(l);
        if (sprite != null)
            return sprite;
        try {
            sprite = new SimpleImage(streamLoader, s, i);
            spriteCache.put(sprite, l);
        } catch (Exception _ex) {
            return null;
        }
        return sprite;
    }

    public void set_model(boolean flag, Model model) {
        int id = 0;// was parameter
        int opcode = 5;// was parameter
        if (flag)
            return;

        models.clear();
        if (model != null && opcode != 4)
            models.put(model, (opcode << 16) + id);

    }

    public Model get_animated_model(int j, int k, boolean enabled) {
        Model model;
        if (enabled)
            model = get_model(enabled_model_type, enabled_model_id);
        else
            model = get_model(model_type, model_id);

        if (model == null)
            return null;

        if (k == -1 && j == -1 && model.face_color == null)
            return model;

        Model model_1 = new Model(true, Animation.validate(k) & Animation.validate(j), false, model);
        if (k != -1 || j != -1)
            model_1.skin();

        if (k != -1)
            model_1.interpolate(k);

        if (j != -1)
            model_1.interpolate(j);

        model_1.light(64, 768, -50, -10, -50, true, true);
        return model_1;
    }

    public Widget() {
    }

    public Widget(int identifier, int width, int height, int type, int optionType) {
        id = identifier;
        this.width = width;
        this.height = height;
        this.type = type;
        this.optionType = optionType;
        cache[identifier] = this;
    }

    public static void addLunarHoverBox(int interface_id, int spriteOffset) {
        Widget RSInterface = addInterface(interface_id);
        RSInterface.id = interface_id;
        RSInterface.parent = interface_id;
        RSInterface.type = 5;
        RSInterface.optionType = 0;
        RSInterface.contentType = 0;
        RSInterface.opacity = 0;
        RSInterface.hoverType = 52;
        RSInterface.enabledSprite = Client.spriteCache.get(LUNAR_HOVER_BOX_SPRITES_START + spriteOffset);
        RSInterface.width = 500;
        RSInterface.height = 500;
        RSInterface.tooltip = "";
    }

    public static void addLunarRune(int i, int spriteOffset, String runeName) {
        Widget RSInterface = addInterface(i);
        RSInterface.type = 5;
        RSInterface.optionType = 0;
        RSInterface.contentType = 0;
        RSInterface.opacity = 0;
        RSInterface.hoverType = 52;
        RSInterface.enabledSprite = Client.spriteCache.get(LUNAR_RUNE_SPRITES_START + spriteOffset);
        RSInterface.width = 500;
        RSInterface.height = 500;
    }

    public static void addLunarText(int ID, int runeAmount, int RuneID, AdvancedFont[] font) {
        Widget rsInterface = addInterface(ID);
        rsInterface.id = ID;
        rsInterface.parent = 1151;
        rsInterface.type = 4;
        rsInterface.optionType = 0;
        rsInterface.contentType = 0;
        rsInterface.width = 0;
        rsInterface.height = 14;
        rsInterface.opacity = 0;
        rsInterface.hoverType = -1;
        rsInterface.valueCompareType = new int[1];
        rsInterface.requiredValues = new int[1];
        rsInterface.valueCompareType[0] = 3;
        rsInterface.requiredValues[0] = runeAmount;
        rsInterface.valueIndexArray = new int[1][4];
        rsInterface.valueIndexArray[0][0] = 4;
        rsInterface.valueIndexArray[0][1] = 3214;
        rsInterface.valueIndexArray[0][2] = RuneID;
        rsInterface.valueIndexArray[0][3] = 0;
        rsInterface.centerText = true;
        rsInterface.text_type = font[0];
        rsInterface.textShadow = true;
        rsInterface.defaultText = "%1/" + runeAmount + "";
        rsInterface.secondaryText = "";
        rsInterface.textColour = 12582912;
        rsInterface.secondaryColor = 49152;
    }

    public static void addLunar2RunesSmallBox(int ID, int r1, int r2, int ra1, int ra2, int rune1, int lvl, String name,
                                              String descr, AdvancedFont[] TDA, int spriteOffset, int suo, int type) {
        Widget rsInterface = addInterface(ID);
        rsInterface.id = ID;
        rsInterface.parent = 1151;
        rsInterface.type = 5;
        rsInterface.optionType = type;
        rsInterface.contentType = 0;
        rsInterface.hoverType = ID + 1;
        rsInterface.selectedTargetMask = suo;
        rsInterface.selectedActionName = "Cast On";
        rsInterface.width = 20;
        rsInterface.height = 20;
        rsInterface.tooltip = "Cast <col=65280>" + name;
        rsInterface.spellName = name;
        rsInterface.valueCompareType = new int[3];
        rsInterface.requiredValues = new int[3];
        rsInterface.valueCompareType[0] = 3;
        rsInterface.requiredValues[0] = ra1;
        rsInterface.valueCompareType[1] = 3;
        rsInterface.requiredValues[1] = ra2;
        rsInterface.valueCompareType[2] = 3;
        rsInterface.requiredValues[2] = lvl;
        rsInterface.valueIndexArray = new int[3][];
        rsInterface.valueIndexArray[0] = new int[4];
        rsInterface.valueIndexArray[0][0] = 4;
        rsInterface.valueIndexArray[0][1] = 3214;
        rsInterface.valueIndexArray[0][2] = r1;
        rsInterface.valueIndexArray[0][3] = 0;
        rsInterface.valueIndexArray[1] = new int[4];
        rsInterface.valueIndexArray[1][0] = 4;
        rsInterface.valueIndexArray[1][1] = 3214;
        rsInterface.valueIndexArray[1][2] = r2;
        rsInterface.valueIndexArray[1][3] = 0;
        rsInterface.valueIndexArray[2] = new int[3];
        rsInterface.valueIndexArray[2][0] = 1;
        rsInterface.valueIndexArray[2][1] = 6;
        rsInterface.valueIndexArray[2][2] = 0;
        rsInterface.disabledSprite = Client.spriteCache.get(LUNAR_ON_SPRITES_START + spriteOffset);
        rsInterface.enabledSprite = Client.spriteCache.get(LUNAR_OFF_SPRITES_START + spriteOffset);

        Widget hover = addInterface(ID + 1);
        hover.parent = ID;
        hover.hoverType = -1;
        hover.type = 0;
        hover.opacity = 0;
        hover.scrollMax = 0;
        hover.invisible = true;
        setChildren(7, hover);
        addLunarHoverBox(ID + 2, 0);
        setBounds(ID + 2, 0, 0, 0, hover);
        addText(ID + 3, "Level " + (lvl + 1) + ": " + name, 0xFF981F, true, true, 52, TDA, 1);
        setBounds(ID + 3, 90, 4, 1, hover);
        addText(ID + 4, descr, 0xAF6A1A, true, true, 52, TDA, 0);
        setBounds(ID + 4, 90, 19, 2, hover);
        setBounds(30016, 37, 35, 3, hover);// Rune
        setBounds(rune1, 112, 35, 4, hover);// Rune
        addLunarText(ID + 5, ra1 + 1, r1, TDA);
        setBounds(ID + 5, 50, 66, 5, hover);
        addLunarText(ID + 6, ra2 + 1, r2, TDA);
        setBounds(ID + 6, 123, 66, 6, hover);
    }

    public static void addLunar3RunesSmallBox(int ID, int r1, int r2, int r3, int ra1, int ra2, int ra3, int rune1,
                                              int rune2, int lvl, String name, String descr, AdvancedFont[] TDA, int spriteOffset, int suo, int type) {
        Widget rsInterface = addInterface(ID);
        rsInterface.id = ID;
        rsInterface.parent = 1151;
        rsInterface.type = 5;
        rsInterface.optionType = type;
        rsInterface.contentType = 0;
        rsInterface.hoverType = ID + 1;
        rsInterface.selectedTargetMask = suo;
        rsInterface.selectedActionName = "Cast on";
        rsInterface.width = 20;
        rsInterface.height = 20;
        rsInterface.tooltip = "Cast <col=65280>" + name;
        rsInterface.spellName = name;
        rsInterface.valueCompareType = new int[4];
        rsInterface.requiredValues = new int[4];
        rsInterface.valueCompareType[0] = 3;
        rsInterface.requiredValues[0] = ra1;
        rsInterface.valueCompareType[1] = 3;
        rsInterface.requiredValues[1] = ra2;
        rsInterface.valueCompareType[2] = 3;
        rsInterface.requiredValues[2] = ra3;
        rsInterface.valueCompareType[3] = 3;
        rsInterface.requiredValues[3] = lvl;
        rsInterface.valueIndexArray = new int[4][];
        rsInterface.valueIndexArray[0] = new int[4];
        rsInterface.valueIndexArray[0][0] = 4;
        rsInterface.valueIndexArray[0][1] = 3214;
        rsInterface.valueIndexArray[0][2] = r1;
        rsInterface.valueIndexArray[0][3] = 0;
        rsInterface.valueIndexArray[1] = new int[4];
        rsInterface.valueIndexArray[1][0] = 4;
        rsInterface.valueIndexArray[1][1] = 3214;
        rsInterface.valueIndexArray[1][2] = r2;
        rsInterface.valueIndexArray[1][3] = 0;
        rsInterface.valueIndexArray[2] = new int[4];
        rsInterface.valueIndexArray[2][0] = 4;
        rsInterface.valueIndexArray[2][1] = 3214;
        rsInterface.valueIndexArray[2][2] = r3;
        rsInterface.valueIndexArray[2][3] = 0;
        rsInterface.valueIndexArray[3] = new int[3];
        rsInterface.valueIndexArray[3][0] = 1;
        rsInterface.valueIndexArray[3][1] = 6;
        rsInterface.valueIndexArray[3][2] = 0;
        rsInterface.disabledSprite = Client.spriteCache.get(LUNAR_ON_SPRITES_START + spriteOffset);
        rsInterface.enabledSprite = Client.spriteCache.get(LUNAR_OFF_SPRITES_START + spriteOffset);

        Widget hover = addInterface(ID + 1);
        hover.parent = ID;
        hover.hoverType = -1;
        hover.type = 0;
        hover.opacity = 0;
        hover.scrollMax = 0;
        hover.invisible = true;
        setChildren(9, hover);
        addLunarHoverBox(ID + 2, 0);
        setBounds(ID + 2, 0, 0, 0, hover);
        addText(ID + 3, "Level " + (lvl + 1) + ": " + name, 0xFF981F, true, true, 52, TDA, 1);
        setBounds(ID + 3, 90, 4, 1, hover);
        addText(ID + 4, descr, 0xAF6A1A, true, true, 52, TDA, 0);
        setBounds(ID + 4, 90, 19, 2, hover);
        setBounds(30016, 14, 35, 3, hover);
        setBounds(rune1, 74, 35, 4, hover);
        setBounds(rune2, 130, 35, 5, hover);
        addLunarText(ID + 5, ra1 + 1, r1, TDA);
        setBounds(ID + 5, 26, 66, 6, hover);
        addLunarText(ID + 6, ra2 + 1, r2, TDA);
        setBounds(ID + 6, 87, 66, 7, hover);
        addLunarText(ID + 7, ra3 + 1, r3, TDA);
        setBounds(ID + 7, 142, 66, 8, hover);
    }

    public static void addLunar3RunesBigBox(int ID, int r1, int r2, int r3, int ra1, int ra2, int ra3, int rune1,
                                            int rune2, int lvl, String name, String descr, AdvancedFont[] TDA, int spriteOffset, int suo, int type) {
        Widget rsInterface = addInterface(ID);
        rsInterface.id = ID;
        rsInterface.parent = 1151;
        rsInterface.type = 5;
        rsInterface.optionType = type;
        rsInterface.contentType = 0;
        rsInterface.hoverType = ID + 1;
        rsInterface.selectedTargetMask = suo;
        rsInterface.selectedActionName = "Cast on";
        rsInterface.width = 20;
        rsInterface.height = 20;
        rsInterface.tooltip = "Cast <col=65280>" + name;
        rsInterface.spellName = name;
        rsInterface.valueCompareType = new int[4];
        rsInterface.requiredValues = new int[4];
        rsInterface.valueCompareType[0] = 3;
        rsInterface.requiredValues[0] = ra1;
        rsInterface.valueCompareType[1] = 3;
        rsInterface.requiredValues[1] = ra2;
        rsInterface.valueCompareType[2] = 3;
        rsInterface.requiredValues[2] = ra3;
        rsInterface.valueCompareType[3] = 3;
        rsInterface.requiredValues[3] = lvl;
        rsInterface.valueIndexArray = new int[4][];
        rsInterface.valueIndexArray[0] = new int[4];
        rsInterface.valueIndexArray[0][0] = 4;
        rsInterface.valueIndexArray[0][1] = 3214;
        rsInterface.valueIndexArray[0][2] = r1;
        rsInterface.valueIndexArray[0][3] = 0;
        rsInterface.valueIndexArray[1] = new int[4];
        rsInterface.valueIndexArray[1][0] = 4;
        rsInterface.valueIndexArray[1][1] = 3214;
        rsInterface.valueIndexArray[1][2] = r2;
        rsInterface.valueIndexArray[1][3] = 0;
        rsInterface.valueIndexArray[2] = new int[4];
        rsInterface.valueIndexArray[2][0] = 4;
        rsInterface.valueIndexArray[2][1] = 3214;
        rsInterface.valueIndexArray[2][2] = r3;
        rsInterface.valueIndexArray[2][3] = 0;
        rsInterface.valueIndexArray[3] = new int[3];
        rsInterface.valueIndexArray[3][0] = 1;
        rsInterface.valueIndexArray[3][1] = 6;
        rsInterface.valueIndexArray[3][2] = 0;
        rsInterface.disabledSprite = Client.spriteCache.get(LUNAR_ON_SPRITES_START + spriteOffset);
        rsInterface.enabledSprite = Client.spriteCache.get(LUNAR_OFF_SPRITES_START + spriteOffset);

        Widget hover = addInterface(ID + 1);
        hover.parent = ID;
        hover.hoverType = -1;
        hover.type = 0;
        hover.opacity = 0;
        hover.scrollMax = 0;
        hover.invisible = true;
        setChildren(9, hover);
        addLunarHoverBox(ID + 2, 1);
        setBounds(ID + 2, 0, 0, 0, hover);
        addText(ID + 3, "Level " + (lvl + 1) + ": " + name, 0xFF981F, true, true, 52, TDA, 1);
        setBounds(ID + 3, 90, 4, 1, hover);
        addText(ID + 4, descr, 0xAF6A1A, true, true, 52, TDA, 0);
        setBounds(ID + 4, 90, 21, 2, hover);
        setBounds(30016, 14, 48, 3, hover);
        setBounds(rune1, 74, 48, 4, hover);
        setBounds(rune2, 130, 48, 5, hover);
        addLunarText(ID + 5, ra1 + 1, r1, TDA);
        setBounds(ID + 5, 26, 79, 6, hover);
        addLunarText(ID + 6, ra2 + 1, r2, TDA);
        setBounds(ID + 6, 87, 79, 7, hover);
        addLunarText(ID + 7, ra3 + 1, r3, TDA);
        setBounds(ID + 7, 142, 79, 8, hover);
    }

    public static void addLunar3RunesLargeBox(int ID, int r1, int r2, int r3, int ra1, int ra2, int ra3, int rune1,
                                              int rune2, int lvl, String name, String descr, AdvancedFont[] TDA, int spriteOffset, int suo, int type) {
        Widget rsInterface = addInterface(ID);
        rsInterface.id = ID;
        rsInterface.parent = 1151;
        rsInterface.type = 5;
        rsInterface.optionType = type;
        rsInterface.contentType = 0;
        rsInterface.hoverType = ID + 1;
        rsInterface.selectedTargetMask = suo;
        rsInterface.selectedActionName = "Cast on";
        rsInterface.width = 20;
        rsInterface.height = 20;
        rsInterface.tooltip = "Cast <col=65280>" + name;
        rsInterface.spellName = name;
        rsInterface.valueCompareType = new int[4];
        rsInterface.requiredValues = new int[4];
        rsInterface.valueCompareType[0] = 3;
        rsInterface.requiredValues[0] = ra1;
        rsInterface.valueCompareType[1] = 3;
        rsInterface.requiredValues[1] = ra2;
        rsInterface.valueCompareType[2] = 3;
        rsInterface.requiredValues[2] = ra3;
        rsInterface.valueCompareType[3] = 3;
        rsInterface.requiredValues[3] = lvl;
        rsInterface.valueIndexArray = new int[4][];
        rsInterface.valueIndexArray[0] = new int[4];
        rsInterface.valueIndexArray[0][0] = 4;
        rsInterface.valueIndexArray[0][1] = 3214;
        rsInterface.valueIndexArray[0][2] = r1;
        rsInterface.valueIndexArray[0][3] = 0;
        rsInterface.valueIndexArray[1] = new int[4];
        rsInterface.valueIndexArray[1][0] = 4;
        rsInterface.valueIndexArray[1][1] = 3214;
        rsInterface.valueIndexArray[1][2] = r2;
        rsInterface.valueIndexArray[1][3] = 0;
        rsInterface.valueIndexArray[2] = new int[4];
        rsInterface.valueIndexArray[2][0] = 4;
        rsInterface.valueIndexArray[2][1] = 3214;
        rsInterface.valueIndexArray[2][2] = r3;
        rsInterface.valueIndexArray[2][3] = 0;
        rsInterface.valueIndexArray[3] = new int[3];
        rsInterface.valueIndexArray[3][0] = 1;
        rsInterface.valueIndexArray[3][1] = 6;
        rsInterface.valueIndexArray[3][2] = 0;
        rsInterface.disabledSprite = Client.spriteCache.get(LUNAR_ON_SPRITES_START + spriteOffset);
        rsInterface.enabledSprite = Client.spriteCache.get(LUNAR_OFF_SPRITES_START + spriteOffset);
        Widget hover = addInterface(ID + 1);
        hover.parent = ID;
        hover.hoverType = -1;
        hover.type = 0;
        hover.opacity = 0;
        hover.scrollMax = 0;
        hover.invisible = true;
        setChildren(9, hover);
        addLunarHoverBox(ID + 2, 2);
        setBounds(ID + 2, 0, 0, 0, hover);
        addText(ID + 3, "Level " + (lvl + 1) + ": " + name, 0xFF981F, true, true, 52, TDA, 1);
        setBounds(ID + 3, 90, 4, 1, hover);
        addText(ID + 4, descr, 0xAF6A1A, true, true, 52, TDA, 0);
        setBounds(ID + 4, 90, 34, 2, hover);
        setBounds(30016, 14, 61, 3, hover);
        setBounds(rune1, 74, 61, 4, hover);
        setBounds(rune2, 130, 61, 5, hover);
        addLunarText(ID + 5, ra1 + 1, r1, TDA);
        setBounds(ID + 5, 26, 92, 6, hover);
        addLunarText(ID + 6, ra2 + 1, r2, TDA);
        setBounds(ID + 6, 87, 92, 7, hover);
        addLunarText(ID + 7, ra3 + 1, r3, TDA);
        setBounds(ID + 7, 142, 92, 8, hover);
    }

    public static void addSlayerItems(int childId, int interfaceId, String[] options, int invSpritePadX, int invSpritePadY, int height, int width) {

        Widget rsi = cache[childId] = new Widget();
        rsi.actions = options == null ? null : new String[10];
        rsi.inventoryOffsetX = new int[20];
        rsi.inventoryAmounts = new int[height * width];
        rsi.inventoryItemId = new int[height * width];
        rsi.itemOpacity = new int[height * width];
        rsi.inventoryOffsetY = new int[20];
        rsi.children = new int[0];
        rsi.child_x = new int[0];
        rsi.child_y = new int[0];
        if (rsi.actions != null) {
            for (int i = 0; i < rsi.actions.length; i++) {
                if (i < options.length) {
                    if (options[i] != null) {
                        rsi.actions[i] = options[i];
                    }
                }
            }
        }

        rsi.centerText = true;
        rsi.filled = false;
        rsi.replaceItems = false;
        rsi.usableItems = false;
        //rsi.isInventoryInterface = false;
        rsi.allowSwapItems = false;
        rsi.textShadow = false;
        rsi.inventoryMarginX = invSpritePadX; // 57
        rsi.inventoryMarginY = invSpritePadY; // 45
        rsi.height = height;
        rsi.width = width;
        rsi.parent = childId;
        rsi.id = interfaceId;
        rsi.type = TYPE_INVENTORY;
    }

    public static void addHoverButtonWConfig(int i, int spriteId, int spriteId2, int width, int height, String text,
                                             int contentType, int hoverOver, int aT, int configId,
                                             int configFrame) {// hoverable
        // button
        Widget tab = addTabInterface(i);
        tab.id = i;
        tab.parent = i;
        tab.type = 5;
        tab.optionType = aT;
        tab.contentType = contentType;
        tab.opacity = 0;
        tab.hoverType = hoverOver;
        tab.width = width;
        tab.height = height;
        tab.tooltip = text;
        tab.valueCompareType = new int[1];
        tab.requiredValues = new int[1];
        tab.valueCompareType[0] = 1;
        tab.requiredValues[0] = configId;
        tab.valueIndexArray = new int[1][3];
        tab.valueIndexArray[0][0] = 5;
        tab.valueIndexArray[0][1] = configFrame;
        tab.valueIndexArray[0][2] = 0;

        if (spriteId != -1) {
            tab.disabledSprite = Client.spriteCache.get(spriteId);
        }

        if (spriteId2 != -1) {
            tab.enabledSprite = Client.spriteCache.get(spriteId2);
        }
    }

    public static void setChildren(int total, Widget widget) {
        widget.children = new int[total];
        widget.child_x = new int[total];
        widget.child_y = new int[total];
    }

    public static void setBounds(int id, int x, int y, int frame, Widget widget) {
        widget.children[frame] = id;
        widget.child_x[frame] = x;
        widget.child_y[frame] = y;
    }

    public static void configHoverButtonSpriteOutline(int id, String tooltip, int enabledSprite, int disabledSprite,
                                                      int sprite, boolean active, int spriteIndex, int outlineSpriteXOffset, int outlineSpriteYOffset,
                                                      int... buttonsToDisable) {
        Widget tab = addInterface(id);
        tab.tooltip = tooltip;
        tab.optionType = OPTION_OK;
        tab.type = TYPE_CONFIG_BUTTON_HOVERED_SPRITE_OUTLINE;
        tab.hoveredOutlineSpriteXOffset = outlineSpriteXOffset;
        tab.hoveredOutlineSpriteYOffset = outlineSpriteYOffset;
        tab.disabledSprite = Client.spriteCache.get(enabledSprite);
        tab.enabledSprite = Client.spriteCache.get(enabledSprite);
        tab.spriteWithOutline = Client.spriteCache.get(sprite);
        tab.width = tab.disabledSprite.width;
        tab.height = tab.enabledSprite.height;
        tab.enabledAltSprite = Client.spriteCache.get(disabledSprite);
        tab.disabledAltSprite = Client.spriteCache.get(disabledSprite);
        tab.buttonsToDisable = buttonsToDisable;
        tab.active = active;
        tab.spriteOpacity = 255;
    }

    public static void addButton(int id, int sid, String spriteName, String tooltip) {
        Widget tab = cache[id] = new Widget();
        tab.id = id;
        tab.parent = id;
        tab.type = 5;
        tab.optionType = 1;
        tab.contentType = 0;
        tab.opacity = (byte) 0;
        tab.hoverType = 52;
        tab.enabledSprite = imageLoader(sid, spriteName);
        tab.disabledSprite = imageLoader(sid, spriteName);
        tab.width = tab.enabledSprite.width;
        tab.height = tab.disabledSprite.height;
        tab.tooltip = tooltip;
    }

    public static void addButton(int i, int j, String name, int W, int H, String S, int AT) {
        Widget RSInterface = addInterface(i);
        RSInterface.id = i;
        RSInterface.parent = i;
        RSInterface.type = 5;
        RSInterface.optionType = AT;
        RSInterface.contentType = 0;
        RSInterface.opacity = 0;
        RSInterface.hoverType = 52;
        RSInterface.enabledSprite = imageLoader(j, name);
        RSInterface.disabledSprite = imageLoader(j, name);
        RSInterface.width = W;
        RSInterface.height = H;
        RSInterface.tooltip = S;
    }

    public static void addRectangle(int id, int opacity, int color, boolean filled, int width, int height) {
        Widget RSInterface = addInterface(id);
        RSInterface.textColour = color;
        RSInterface.filled = filled;
        RSInterface.id = id;
        RSInterface.parent = id;
        RSInterface.type = 3;
        RSInterface.optionType = 0;
        RSInterface.contentType = 0;
        RSInterface.opacity = (byte) opacity;
        RSInterface.width = width;
        RSInterface.height = height;
    }

    public static void addSprites(int ID, int i, int i2, String name, int configId, int configFrame) {
        Widget Tab = addTabInterface(ID);
        Tab.id = ID;
        Tab.parent = ID;
        Tab.type = 5;
        Tab.optionType = 0;
        Tab.contentType = 0;
        Tab.width = 512;
        Tab.height = 334;
        Tab.opacity = 0;
        Tab.hoverType = -1;
        Tab.valueCompareType = new int[1];
        Tab.requiredValues = new int[1];
        Tab.valueCompareType[0] = 1;
        Tab.requiredValues[0] = configId;
        Tab.valueIndexArray = new int[1][3];
        Tab.valueIndexArray[0][0] = 5;
        Tab.valueIndexArray[0][1] = configFrame;
        Tab.valueIndexArray[0][2] = 0;
        Tab.enabledSprite = imageLoader(i, name);
        Tab.disabledSprite = imageLoader(i2, name);
    }

    /**
     * An array of background sprites
     */
    public SimpleImage[] backgroundSprites;

    public static void addSprites(int id, int... spriteIds) {
        if (spriteIds.length < 2) {
            throw new IllegalStateException("Error adding sprites, not enough sprite id's provided.");
        }
        Widget component = addInterface(id);
        component.id = id;
        component.type = CLICKABLE_SPRITES;
        component.backgroundSprites = new SimpleImage[spriteIds.length];
        for (int i = 0; i < spriteIds.length; i++) {
            component.backgroundSprites[i] = Client.spriteCache.get(spriteIds[i]);
            if (component.backgroundSprites[i] == null) {
                throw new IllegalStateException("Error adding sprites, unable to find one of the images.");
            }
        }
        component.enabledSprite = component.backgroundSprites[0];
    }

    public static void addClickableSprites(int id, String tooltip, int... spriteIds) {
        addSprites(id, spriteIds);
        Widget component = cache[id];
        component.optionType = OPTION_TOGGLE_SETTING;
        component.tooltip = tooltip;
        component.width = component.backgroundSprites[0].width;
        component.height = component.backgroundSprites[0].height;
    }

    public String[] tooltips;
    public boolean newScroller;
    public boolean drawInfinity;

    public static void handleConfigHover(Widget widget) {
        if (widget.active) {
            return;
        }
        widget.active = true;

        configHoverButtonSwitch(widget);
        disableOtherButtons(widget);
    }

    public static void configHoverButtonSwitch(Widget widget) {
        SimpleImage[] backup = new SimpleImage[]{widget.disabledSprite, widget.enabledSprite};

        widget.disabledSprite = widget.enabledAltSprite;
        widget.enabledSprite = widget.disabledAltSprite;

        widget.enabledAltSprite = backup[0];
        widget.disabledAltSprite = backup[1];
    }

    public static void disableOtherButtons(Widget widget) {
        if (widget.buttonsToDisable == null) {
            return;
        }
        for (int btn : widget.buttonsToDisable) {
            Widget btnWidget = cache[btn];

            if (btnWidget.active) {

                btnWidget.active = false;
                configHoverButtonSwitch(btnWidget);
            }
        }
    }

    public static void slider(int id, double min, double max, int icon, int background) {
        Widget widget = addInterface(id);
        widget.slider = new Slider(Client.spriteCache.get(icon), Client.spriteCache.get(background), min, max);
        widget.type = TYPE_SLIDER;
    }

    public static void keybindingDropdown(int id, int width, int defaultOption, String[] options, Dropdown d,
                                          boolean inverted) {
        Widget widget = addInterface(id);
        widget.type = TYPE_KEYBINDS_DROPDOWN;
        widget.dropdown = new DropdownMenu(width, true, defaultOption, options, d);
        widget.optionType = OPTION_DROPDOWN;
        widget.inverted = inverted;
    }

    public static void dropdownMenu(int id, int width, int defaultOption, String[] options, Dropdown d, AdvancedFont tda[], int idx) {
        dropdownMenu(id, width, defaultOption, options, d, new int[]{0x0d0d0b, 0x464644, 0x473d32, 0x51483c, 0x787169}, false, tda, idx);
    }

    public static void dropdownMenu(int id, int width, int defaultOption, String[] options, Dropdown d, int[] dropdownColours, boolean centerText, AdvancedFont tda[], int idx) {
        Widget menu = addInterface(id);
        menu.type = TYPE_DROPDOWN;
        menu.text_type = tda[idx];
        menu.dropdown = new DropdownMenu(width, false, defaultOption, options, d);
        menu.optionType = OPTION_DROPDOWN;
        menu.dropdownColours = dropdownColours;
        menu.centerText = centerText;
    }

    /* Add Container */
    protected static Widget addContainer(int id, int contentType, int maxItemsOnSingleRow, int rows, int xPad, int yPad, int opacity, boolean move, boolean displayAmount, boolean displayExamine, String... actions) {
        Widget container = addTabInterface(id);
        container.parent = id;
        container.type = 2;
        container.contentType = contentType;
        container.width = maxItemsOnSingleRow;
        container.height = rows;
        container.sprites = new SimpleImage[20];
        container.inventoryOffsetX = new int[20];
        container.inventoryOffsetY = new int[20];
        container.inventoryMarginX = xPad;
        container.inventoryMarginY = yPad;
        container.inventoryItemId = new int[maxItemsOnSingleRow * rows];
        container.inventoryAmounts = new int[maxItemsOnSingleRow * rows];
        container.itemOpacity = new int[rows * maxItemsOnSingleRow];
        container.actions = actions;
        container.allowSwapItems = move;
        container.alpha = opacity;
        container.displayAmount = displayAmount;
        container.displayExamine = displayExamine;
        return container;
    }

    /**
     * Adds an item container layer.
     */
    public static Widget addContainer(int id, int contentType, int maxItemsOnSingleRow, int rows, String... actions) {
        Widget container = addInterface(id);
        container.parent = id;
        container.type = 2;
        container.contentType = contentType;
        container.width = maxItemsOnSingleRow;
        container.height = rows;
        container.sprites = new SimpleImage[20];
        container.inventoryOffsetX = new int[20];
        container.inventoryOffsetY = new int[20];
        container.inventoryMarginX = 14;
        container.inventoryMarginY = 4;
        container.inventoryItemId = new int[maxItemsOnSingleRow * rows];
        container.inventoryAmounts = new int[maxItemsOnSingleRow * rows];
        container.itemOpacity = new int[rows * maxItemsOnSingleRow];
        container.allowSwapItems = true;
        container.actions = actions;
        return container;
    }

    public static Widget addContainer(int id, int contentType, int width, int height, boolean collection, String... actions) {
        Widget container = addInterface(id);
        container.parent = id;
        container.type = 2;
        container.contentType = contentType;
        container.width = width;
        container.height = height;
        container.sprites = new SimpleImage[20];
        container.inventoryOffsetX = new int[20];
        container.inventoryOffsetY = new int[20];
        container.inventoryMarginX = 13;
        container.inventoryMarginY = 5;
        container.inventoryItemId = new int[width * height];
        container.inventoryAmounts = new int[width * height];
        container.itemOpacity = new int[height * width];
        container.collection = collection;
        container.usableItems = false;
        container.centerText = true;
        container.filled = false;
        container.actions = actions;
        return container;
    }

    /* Add Container */
    protected static Widget addContainer(int id, int width, int height, int xPad, int yPad, boolean move, boolean displayAmount, boolean displayExamine, String... actions) {
        return addContainer(id, 0, width, height, xPad, yPad, 0, move, displayAmount, displayExamine, actions);
    }

    /* Add Container */
    protected static Widget addContainer(int id, int width, int height, int xPad, int yPad, boolean move, String... actions) {
        return addContainer(id, 0, width, height, xPad, yPad, 0, move, true, true, actions);
    }

    public static void itemGroup(int id, int w, int h, int x, int y) {
        Widget widget = addInterface(id);
        widget.width = w;
        widget.height = h;
        widget.inventoryItemId = new int[5000];
        widget.inventoryAmounts = new int[5000];
        widget.usableItems = false;
        widget.hasActions = false;
        widget.inventoryMarginX = x;
        widget.inventoryMarginY = y;
        widget.inventoryOffsetX = new int[20];
        widget.inventoryOffsetY = new int[20];
        widget.sprites = new SimpleImage[20];
        widget.type = 2;
    }

    public static void setScrollableItems(Widget tab, int[][] rewards) {
        Widget parent = cache[tab.parent];
        tab.contentType = 1430;
        tab.scrollMax = ((rewards.length * 32) + (rewards.length * tab.inventoryMarginX)) - parent.width;
        for (int i = 0; i < rewards.length; ++i) {
            tab.inventoryItemId[i] = rewards[i][0] + 1;
            tab.inventoryAmounts[i] = rewards[i][1];
        }
    }

    public static void addOutlinedColorBox(int id, int color, int width, int height, int transparency) {
        Widget widget = addInterface(id);
        widget.width = width;
        widget.height = height;
        widget.color = color;
        widget.type = OUTLINE;
        widget.transparency = transparency;
        widget.contentType = 0;
    }

    public static void addColorBox(int id, int color, int width, int height, int transparency) {
        Widget widget = addInterface(id);
        widget.width = width;
        widget.height = height;
        widget.color = color;
        widget.type = COLOR;
        widget.transparency = transparency;
        widget.contentType = 0;
    }

    public static void addLine(int id, int color, int width) {
        Widget widget = addInterface(id);
        widget.width = width;
        widget.color = color;
        widget.type = LINE;
        widget.contentType = 0;
    }

    public static void addSpriteArray(int startID, int[] sprites, String tooltip, int spritesPerRow, int padX, int padY) {
        Widget rsi = cache[startID] = new Widget();
        rsi.id = startID;
        rsi.parent = startID;
        rsi.type = 279;
        rsi.optionType = 1;
        rsi.contentType = 0;
        rsi.spritesToDraw = sprites;
        rsi.width = 80;
        rsi.height = 66;
        rsi.tooltip = tooltip;
        rsi.spritesPerRow = spritesPerRow;
        rsi.inventoryMarginX = padX;
        rsi.inventoryMarginY = padY;
    }

    public static void addSpellSmall2_3(int interfaceId, int rune, int secondaryRune, int thirdRune, int fourthRune, int runeAmount1, int runeAmount2, int runeAmount3, int runeAmount4, int runeChild, int secondaryRuneChild, int thirdRuneChild, int fourthRuneChild, int levelRequired, String spellName, String spellDescription, AdvancedFont[] TDA, int spellOffSprite, int spellOnSprite, int selectedMask, int type) {
        Widget widget = addInterface(interfaceId);
        widget.id = interfaceId;
        widget.parent = 1151;
        widget.type = 5;
        widget.optionType = type;
        widget.contentType = 0;
        widget.hoverType = interfaceId + 1;
        widget.selectedTargetMask = selectedMask;
        widget.selectedActionName = "Cast on";
        widget.width = 20;
        widget.height = 20;
        widget.tooltip = "Cast <col=65280>" + spellName;
        widget.spellName = spellName;
        widget.valueCompareType = new int[5];
        widget.requiredValues = new int[5];
        widget.valueCompareType[0] = 3;
        widget.requiredValues[0] = runeAmount1;
        widget.valueCompareType[1] = 3;
        widget.requiredValues[1] = runeAmount2;
        widget.valueCompareType[2] = 3;
        widget.requiredValues[2] = runeAmount3;
        widget.valueCompareType[3] = 3;
        widget.requiredValues[3] = runeAmount4;
        widget.valueCompareType[4] = 3;
        widget.requiredValues[4] = levelRequired;
        widget.valueIndexArray = new int[5][];
        widget.valueIndexArray[0] = new int[5];
        widget.valueIndexArray[0][0] = 4;
        widget.valueIndexArray[0][1] = 3214;
        widget.valueIndexArray[0][2] = rune;
        widget.valueIndexArray[0][3] = 0;
        widget.valueIndexArray[1] = new int[4];
        widget.valueIndexArray[1][0] = 4;
        widget.valueIndexArray[1][1] = 3214;
        widget.valueIndexArray[1][2] = secondaryRune;
        widget.valueIndexArray[1][3] = 0;
        widget.valueIndexArray[2] = new int[4];
        widget.valueIndexArray[2][0] = 4;
        widget.valueIndexArray[2][1] = 3214;
        widget.valueIndexArray[2][2] = thirdRune;
        widget.valueIndexArray[2][3] = 0;
        widget.valueIndexArray[3] = new int[4];
        widget.valueIndexArray[3][0] = 4;
        widget.valueIndexArray[3][1] = 3214;
        widget.valueIndexArray[3][2] = fourthRune;
        widget.valueIndexArray[3][3] = 0;
        widget.valueIndexArray[4] = new int[4];
        widget.valueIndexArray[4][0] = 1;
        widget.valueIndexArray[4][1] = 0;
        widget.valueIndexArray[4][2] = 0;
        widget.valueIndexArray[4][3] = 0;
        widget.enabledSprite = Client.spriteCache.get(spellOffSprite);
        widget.disabledSprite = Client.spriteCache.get(spellOnSprite);
        Widget subWidget = addInterface(interfaceId + 1);
        subWidget.invisible = true;
        subWidget.hoverType = -1;
        setChildren(11, subWidget);
        addLunarHoverBox(interfaceId + 2, 0);
        setBounds(interfaceId + 2, 0, 0, 0, subWidget);
        addText(interfaceId + 3, "Level " + (levelRequired + 1) + ": " + spellName, 0xFF981F, true, true, 52, TDA, 1);
        setBounds(interfaceId + 3, 90, 4, 1, subWidget);
        addText(interfaceId + 4, spellDescription, 0xAF6A1A, true, true, 52, TDA, 0);
        setBounds(interfaceId + 4, 90, 19, 2, subWidget);
        setBounds(runeChild, 15, 35, 3, subWidget);
        setBounds(secondaryRuneChild, 55, 35, 4, subWidget);
        setBounds(thirdRuneChild, 95, 35, 5, subWidget);
        setBounds(fourthRuneChild, 135, 35, 6, subWidget);
        addRuneText(interfaceId + 5, runeAmount1, rune, TDA);
        setBounds(interfaceId + 5, 29, 66, 7, subWidget);
        addRuneText(interfaceId + 6, runeAmount2, secondaryRune, TDA);
        setBounds(interfaceId + 6, 69, 66, 8, subWidget);
        addRuneText(interfaceId + 7, runeAmount3, thirdRune, TDA);
        setBounds(interfaceId + 7, 109, 66, 9, subWidget);
        addRuneText(interfaceId + 8, runeAmount4, fourthRune, TDA);
        setBounds(interfaceId + 8, 149, 66, 10, subWidget);
    }

    public static void addSpellLarge2(int interfaceId, int rune, int secondaryRune, int thirdRune, int runeAmount1, int runeAmount2, int runeAmount3, int runeChild, int secondaryRuneChild, int thirdRuneChild, int lvl, String spellName, String spellDescription, AdvancedFont[] font, int spellOffSprite, int spellOnSprite, int selectedMask, int type) {
        Widget widget = addInterface(interfaceId);
        widget.id = interfaceId;
        widget.parent = 1151;
        widget.type = 5;
        widget.optionType = type;
        widget.contentType = 0;
        widget.hoverType = interfaceId + 1;
        widget.selectedTargetMask = selectedMask;
        widget.selectedActionName = "Cast on";
        widget.width = 20;
        widget.height = 20;
        widget.tooltip = spellName.contains("Bounty") ? "Cast <col=65280>Teleport to Bounty Target" : "Cast <col=65280>" + spellName;
        widget.spellName = spellName;
        widget.valueCompareType = new int[4];
        widget.requiredValues = new int[4];
        widget.valueCompareType[0] = 3;
        widget.requiredValues[0] = runeAmount1;
        widget.valueCompareType[1] = 3;
        widget.requiredValues[1] = runeAmount2;
        widget.valueCompareType[2] = 3;
        widget.requiredValues[2] = runeAmount3;
        widget.valueCompareType[3] = 3;
        widget.requiredValues[3] = lvl;
        widget.valueIndexArray = new int[4][];
        widget.valueIndexArray[0] = new int[4];
        widget.valueIndexArray[0][0] = 4;
        widget.valueIndexArray[0][1] = 3214;
        widget.valueIndexArray[0][2] = rune;
        widget.valueIndexArray[0][3] = 0;
        widget.valueIndexArray[1] = new int[4];
        widget.valueIndexArray[1][0] = 4;
        widget.valueIndexArray[1][1] = 3214;
        widget.valueIndexArray[1][2] = secondaryRune;
        widget.valueIndexArray[1][3] = 0;
        widget.valueIndexArray[2] = new int[4];
        widget.valueIndexArray[2][0] = 4;
        widget.valueIndexArray[2][1] = 3214;
        widget.valueIndexArray[2][2] = thirdRune;
        widget.valueIndexArray[2][3] = 0;
        widget.valueIndexArray[3] = new int[3];
        widget.valueIndexArray[3][0] = 1;
        widget.valueIndexArray[3][1] = 0;
        widget.valueIndexArray[3][2] = 0;
        widget.enabledSprite = Client.spriteCache.get(spellOffSprite);
        widget.disabledSprite = Client.spriteCache.get(spellOnSprite);
        Widget subWidget = addInterface(interfaceId + 1);
        subWidget.invisible = true;
        subWidget.hoverType = -1;
        setChildren(9, subWidget);
        addLunarHoverBox(interfaceId + 2, 2);
        setBounds(interfaceId + 2, 0, 0, 0, subWidget);
        addText(interfaceId + 3, "Level " + (lvl + 1) + ": " + spellName, 0xFF981F, true, true, 52, font, 1);
        setBounds(interfaceId + 3, 90, 4, 1, subWidget);
        addText(interfaceId + 4, spellDescription, 0xAF6A1A, true, true, 52, font, 0);
        setBounds(interfaceId + 4, 90, 34, 2, subWidget);
        setBounds(runeChild, 14, 61, 3, subWidget);
        setBounds(secondaryRuneChild, 74, 61, 4, subWidget);
        setBounds(thirdRuneChild, 130, 61, 5, subWidget);
        addRuneText(interfaceId + 5, runeAmount1, rune, font);
        setBounds(interfaceId + 5, 26, 92, 6, subWidget);
        addRuneText(interfaceId + 6, runeAmount2, secondaryRune, font);
        setBounds(interfaceId + 6, 87, 92, 7, subWidget);
        addRuneText(interfaceId + 7, runeAmount3, thirdRune, font);
        setBounds(interfaceId + 7, 142, 92, 8, subWidget);
    }

    public static void addSpellSmall2(int interfaceId, int rune, int secondaryRune, int thirdRune, int runeAmount, int runeAmount2, int runeAmount3, int runeChild, int secondaryRuneChild, int thirdRuneChild, int levelRequirement, String spellName, String spellDescription, AdvancedFont[] font, int spellOffSprite, int spellOnSprite, int selectedMask, int optionType) {
        Widget widget = addInterface(interfaceId);
        widget.id = interfaceId;
        widget.parent = 1151;
        widget.type = 5;
        widget.optionType = optionType;
        widget.contentType = 0;
        widget.hoverType = interfaceId + 1;
        widget.selectedTargetMask = selectedMask;
        widget.selectedActionName = "Cast on";
        widget.width = 20;
        widget.height = 20;
        widget.tooltip = "Cast <col=65280>" + spellName;
        widget.spellName = spellName;
        widget.valueCompareType = new int[4];
        widget.requiredValues = new int[4];
        widget.valueCompareType[0] = 3;
        widget.requiredValues[0] = runeAmount;
        widget.valueCompareType[1] = 3;
        widget.requiredValues[1] = runeAmount2;
        widget.valueCompareType[2] = 3;
        widget.requiredValues[2] = runeAmount3;
        widget.valueCompareType[3] = 3;
        widget.requiredValues[3] = levelRequirement;
        widget.valueIndexArray = new int[4][];
        widget.valueIndexArray[0] = new int[4];
        widget.valueIndexArray[0][0] = 4;
        widget.valueIndexArray[0][1] = 3214;
        widget.valueIndexArray[0][2] = rune;
        widget.valueIndexArray[0][3] = 0;
        widget.valueIndexArray[1] = new int[4];
        widget.valueIndexArray[1][0] = 4;
        widget.valueIndexArray[1][1] = 3214;
        widget.valueIndexArray[1][2] = secondaryRune;
        widget.valueIndexArray[1][3] = 0;
        widget.valueIndexArray[2] = new int[4];
        widget.valueIndexArray[2][0] = 4;
        widget.valueIndexArray[2][1] = 3214;
        widget.valueIndexArray[2][2] = thirdRune;
        widget.valueIndexArray[2][3] = 0;
        widget.valueIndexArray[3] = new int[3];
        widget.valueIndexArray[3][0] = 1;
        widget.valueIndexArray[3][1] = 6;
        widget.valueIndexArray[3][2] = 0;
        widget.enabledSprite = Client.spriteCache.get(spellOffSprite);
        widget.disabledSprite = Client.spriteCache.get(spellOnSprite);
        Widget subWidget = addInterface(interfaceId + 1);
        subWidget.invisible = true;
        subWidget.hoverType = -1;
        setChildren(9, subWidget);
        addLunarHoverBox(interfaceId + 2, 0);
        setBounds(interfaceId + 2, 0, 0, 0, subWidget);
        addText(interfaceId + 3, "Level " + (levelRequirement + 1) + ": " + spellName, 0xFF981F, true, true, 52, font, 1);
        setBounds(interfaceId + 3, 90, 4, 1, subWidget);
        addText(interfaceId + 4, spellDescription, 0xAF6A1A, true, true, 52, font, 0);
        setBounds(interfaceId + 4, 90, 19, 2, subWidget);
        setBounds(runeChild, 14, 35, 3, subWidget);
        setBounds(secondaryRuneChild, 74, 35, 4, subWidget);
        setBounds(thirdRuneChild, 130, 35, 5, subWidget);
        addRuneText(interfaceId + 5, runeAmount, rune, font);
        setBounds(interfaceId + 5, 26, 66, 6, subWidget);
        addRuneText(interfaceId + 6, runeAmount2, secondaryRune, font);
        setBounds(interfaceId + 6, 87, 66, 7, subWidget);
        addRuneText(interfaceId + 7, runeAmount3, thirdRune, font);
        setBounds(interfaceId + 7, 142, 66, 8, subWidget);
    }

    public static void addSpellSmaller(int interfaceId, int rune, int secondaryRune, int runeAmount, int secondaryRuneAmount, int runeChild, int secondaryRuneChild, int levelRequirement, String spellName, String spellDescription, AdvancedFont[] font, int spellOffSprite, int spellOnSprite, int targetMask, int type, boolean spell) {
        Widget widget = addInterface(interfaceId);
        widget.id = interfaceId;
        widget.valueIndexArray = new int[3][];

        int index = 0;
        widget.valueIndexArray[0] = new int[4 + (3 * CombinationRunes.getTotalCombinationRunes(rune))];
        widget.valueIndexArray[0][index++] = 4;
        widget.valueIndexArray[0][index++] = 3214;
        widget.valueIndexArray[0][index++] = rune;
        for (CombinationRunes combinationRunes : CombinationRunes.values()) {
            if (combinationRunes.getCombinationRunesId()[0] == rune || combinationRunes.getCombinationRunesId()[1] == rune) {
                widget.valueIndexArray[0][index++] = 4;
                widget.valueIndexArray[0][index++] = 3214;
                widget.valueIndexArray[0][index++] = combinationRunes.getRuneItemId();
            }
        }
        widget.valueIndexArray[0][index++] = 0;

        index = 0;
        widget.valueIndexArray[1] = new int[4 + (3 * CombinationRunes.getTotalCombinationRunes(secondaryRune))];
        widget.valueIndexArray[1][index++] = 4;
        widget.valueIndexArray[1][index++] = 3214;
        widget.valueIndexArray[1][index++] = secondaryRune;
        for (CombinationRunes combinationRunes : CombinationRunes.values()) {
            if (combinationRunes.getCombinationRunesId()[0] == secondaryRune || combinationRunes.getCombinationRunesId()[1] == secondaryRune) {
                widget.valueIndexArray[1][index++] = 4;
                widget.valueIndexArray[1][index++] = 3214;
                widget.valueIndexArray[1][index++] = combinationRunes.getRuneItemId();
            }
        }
        widget.valueIndexArray[1][index++] = 0;

        widget.valueIndexArray[2] = new int[3];
        widget.valueIndexArray[2][0] = 1;
        widget.valueIndexArray[2][1] = 6;
        widget.valueIndexArray[2][2] = 0;
        if (spell) {
            widget.parent = 1151;
            widget.type = 5;
            widget.optionType = type;
            widget.contentType = 0;
            widget.hoverType = interfaceId + 1;
            widget.selectedTargetMask = targetMask;
            widget.selectedActionName = "Cast on";
            widget.width = 20;
            widget.height = 20;
            widget.tooltip = "Cast <col=65280>" + spellName;
            widget.spellName = spellName;
            widget.valueCompareType = new int[3];
            widget.requiredValues = new int[3];

            widget.valueCompareType[0] = 3;
            widget.requiredValues[0] = runeAmount - 1;
            widget.valueCompareType[1] = 3;
            widget.requiredValues[1] = secondaryRuneAmount - 1;
            widget.valueCompareType[2] = 3;
            widget.requiredValues[2] = levelRequirement;
            widget.enabledSprite = Client.spriteCache.get(spellOffSprite);
            widget.disabledSprite = Client.spriteCache.get(spellOnSprite);
        } else {
            widget.parent = interfaceId;
            widget.type = 5;
            widget.optionType = 1;
            widget.contentType = 0;
            widget.opacity = (byte) 0;
            widget.hoverType = 52;
            widget.valueCompareType = new int[3];
            widget.requiredValues = new int[3];
            widget.valueCompareType[0] = 3;
            widget.requiredValues[0] = runeAmount - 1;
            widget.valueCompareType[1] = 3;
            widget.requiredValues[1] = secondaryRuneAmount - 1;
            widget.valueCompareType[2] = 3;
            widget.requiredValues[2] = levelRequirement;
            widget.enabledSprite = Client.spriteCache.get(spellOffSprite);
            widget.disabledSprite = Client.spriteCache.get(spellOnSprite);
            widget.width = 20;
            widget.height = 20;
            widget.tooltip = "Select";
            widget.hoverType = interfaceId + 1;
        }

        Widget subWidget = addInterface(interfaceId + 1);
        subWidget.invisible = true;
        subWidget.hoverType = -1;
        setChildren(7, subWidget);
        addLunarHoverBox(interfaceId + 2, 0);
        setBounds(interfaceId + 2, 0, 0, 0, subWidget);
        addText(interfaceId + 3, "Level " + (levelRequirement + 1) + ": " + spellName, 0xFF981F, true, true, 52, font, 1);
        setBounds(interfaceId + 3, 90, 4, 1, subWidget);
        addText(interfaceId + 4, spellDescription, 0xAF6A1A, true, true, 52, font, 0);
        setBounds(interfaceId + 4, 90, 19, 2, subWidget);
        setBounds(runeChild, 40, 35, 3, subWidget);
        setBounds(secondaryRuneChild, 110, 35, 4, subWidget);
        addRuneText(interfaceId + 5, runeAmount, rune, font);
        setBounds(interfaceId + 5, 53, 66, 5, subWidget);
        addRuneText(interfaceId + 6, secondaryRuneAmount, secondaryRune, font);
        setBounds(interfaceId + 6, 124, 66, 6, subWidget);
    }

    public static void addSpellSmall(int interfaceId, int rune, int secondaryRune, int thirdRune, int runeAmount, int secondaryRuneAmount, int thirdRuneAmount, int runeChild, int secondaryRuneChild, int thirdRuneChild, int levelRequirement, String spellName, String spellDescription, AdvancedFont[] font, int spellOffSprite, int spellOnSprite, int selectedMask, int type, boolean spell) {
        Widget widget = addInterface(interfaceId);
        widget.id = interfaceId;
        widget.valueIndexArray = new int[4][];

        int index = 0;
        widget.valueIndexArray[0] = new int[4 + (3 * CombinationRunes.getTotalCombinationRunes(rune))];
        widget.valueIndexArray[0][index++] = 4;
        widget.valueIndexArray[0][index++] = 3214;
        widget.valueIndexArray[0][index++] = rune;
        for (CombinationRunes combinationRunes : CombinationRunes.values()) {
            if (combinationRunes.getCombinationRunesId()[0] == rune || combinationRunes.getCombinationRunesId()[1] == rune) {
                widget.valueIndexArray[0][index++] = 4;
                widget.valueIndexArray[0][index++] = 3214;
                widget.valueIndexArray[0][index++] = combinationRunes.getRuneItemId();
            }
        }
        widget.valueIndexArray[0][index++] = 0;

        index = 0;
        widget.valueIndexArray[1] = new int[4 + (3 * CombinationRunes.getTotalCombinationRunes(secondaryRune))];
        widget.valueIndexArray[1][index++] = 4;
        widget.valueIndexArray[1][index++] = 3214;
        widget.valueIndexArray[1][index++] = secondaryRune;
        for (CombinationRunes combinationRunes : CombinationRunes.values()) {
            if (combinationRunes.getCombinationRunesId()[0] == secondaryRune || combinationRunes.getCombinationRunesId()[1] == secondaryRune) {
                widget.valueIndexArray[1][index++] = 4;
                widget.valueIndexArray[1][index++] = 3214;
                widget.valueIndexArray[1][index++] = combinationRunes.getRuneItemId();
            }
        }
        widget.valueIndexArray[1][index++] = 0;

        index = 0;
        widget.valueIndexArray[2] = new int[4 + (3 * CombinationRunes.getTotalCombinationRunes(thirdRune))];
        widget.valueIndexArray[2][index++] = 4;
        widget.valueIndexArray[2][index++] = 3214;
        widget.valueIndexArray[2][index++] = thirdRune;
        for (CombinationRunes combinationRunes : CombinationRunes.values()) {
            if (combinationRunes.getCombinationRunesId()[0] == thirdRune || combinationRunes.getCombinationRunesId()[1] == thirdRune) {
                widget.valueIndexArray[2][index++] = 4;
                widget.valueIndexArray[2][index++] = 3214;
                widget.valueIndexArray[2][index++] = combinationRunes.getRuneItemId();
            }
        }
        widget.valueIndexArray[2][index++] = 0;

        widget.valueIndexArray[3] = new int[3];
        widget.valueIndexArray[3][0] = 1;
        widget.valueIndexArray[3][1] = 6;
        widget.valueIndexArray[3][2] = 0;
        if (spell) {
            widget.parent = 1151;
            widget.type = 5;
            widget.optionType = type;
            widget.contentType = 0;
            widget.hoverType = interfaceId + 1;
            widget.selectedTargetMask = selectedMask;
            widget.selectedActionName = "Cast on";
            widget.width = 20;
            widget.height = 20;
            widget.tooltip = "Cast <col=65280>" + spellName;
            widget.spellName = spellName;
            widget.valueCompareType = new int[4];
            widget.requiredValues = new int[4];
            widget.valueCompareType[0] = 3;
            widget.requiredValues[0] = runeAmount - 1;
            widget.valueCompareType[1] = 3;
            widget.requiredValues[1] = secondaryRuneAmount - 1;
            widget.valueCompareType[2] = 3;
            widget.requiredValues[2] = thirdRuneAmount - 1;
            widget.valueCompareType[3] = 3;
            widget.requiredValues[3] = levelRequirement;
            widget.enabledSprite = Client.spriteCache.get(spellOffSprite);
            widget.disabledSprite = Client.spriteCache.get(spellOnSprite);
        } else {
            widget.parent = interfaceId;
            widget.type = 5;
            widget.optionType = 1;
            widget.contentType = 0;
            widget.opacity = (byte) 0;
            widget.hoverType = 52;
            widget.valueCompareType = new int[4];
            widget.requiredValues = new int[4];
            widget.valueCompareType[0] = 3;
            widget.requiredValues[0] = runeAmount - 1;
            widget.valueCompareType[1] = 3;
            widget.requiredValues[1] = secondaryRuneAmount - 1;
            widget.valueCompareType[2] = 3;
            widget.requiredValues[2] = thirdRuneAmount - 1;
            widget.valueCompareType[3] = 3;
            widget.requiredValues[3] = levelRequirement;
            widget.enabledSprite = Client.spriteCache.get(spellOffSprite);
            widget.disabledSprite = Client.spriteCache.get(spellOnSprite);
            widget.width = 20;
            widget.height = 20;
            widget.tooltip = "Select";
            widget.hoverType = interfaceId + 1;
        }
        Widget subWidget = addInterface(interfaceId + 1);
        subWidget.invisible = true;
        subWidget.hoverType = -1;
        setChildren(9, subWidget);
        addLunarHoverBox(interfaceId + 2, 0);
        setBounds(interfaceId + 2, 0, 0, 0, subWidget);
        addText(interfaceId + 3, "Level " + (levelRequirement + 1) + ": " + spellName, 0xFF981F, true, true, 52, font, 1);
        setBounds(interfaceId + 3, 90, 4, 1, subWidget);
        addText(interfaceId + 4, spellDescription, 0xAF6A1A, true, true, 52, font, 0);
        setBounds(interfaceId + 4, 90, 19, 2, subWidget);
        setBounds(runeChild, 14, 35, 3, subWidget);
        setBounds(secondaryRuneChild, 74, 35, 4, subWidget);
        setBounds(thirdRuneChild, 130, 35, 5, subWidget);
        addRuneText(interfaceId + 5, runeAmount, rune, font);
        setBounds(interfaceId + 5, 26, 66, 6, subWidget);
        addRuneText(interfaceId + 6, secondaryRuneAmount, secondaryRune, font);
        setBounds(interfaceId + 6, 87, 66, 7, subWidget);
        addRuneText(interfaceId + 7, thirdRuneAmount, thirdRune, font);
        setBounds(interfaceId + 7, 145, 66, 8, subWidget);
    }

    public static void addSpellBig2(int interfaceId, int rune, int secondaryRune, int thirdRune, int runeAmount, int secondaryRuneAmount, int thirdRuneAmount, int runeChild, int secondaryRuneChild, int thirdRuneChild, int levelRequirement, String spellName, String spellDescription, AdvancedFont[] font, int spellOffSprite, int spellOnSprite, int selectedMask, int type) {
        Widget widget = addInterface(interfaceId);
        widget.id = interfaceId;
        widget.parent = 1151;
        widget.type = 5;
        widget.optionType = type;
        widget.contentType = 0;
        widget.hoverType = interfaceId + 1;
        widget.selectedTargetMask = selectedMask;
        widget.selectedActionName = "Cast on";
        widget.width = 20;
        widget.height = 20;
        widget.tooltip = "Cast <col=65280>" + spellName;
        widget.spellName = spellName;
        widget.valueCompareType = new int[4];
        widget.requiredValues = new int[4];
        widget.valueCompareType[0] = 3;
        widget.requiredValues[0] = runeAmount;
        widget.valueCompareType[1] = 3;
        widget.requiredValues[1] = secondaryRuneAmount;
        widget.valueCompareType[2] = 3;
        widget.requiredValues[2] = thirdRuneAmount;
        widget.valueCompareType[3] = 3;
        widget.requiredValues[3] = levelRequirement;
        widget.valueIndexArray = new int[4][];
        widget.valueIndexArray[0] = new int[4];
        widget.valueIndexArray[0][0] = 4;
        widget.valueIndexArray[0][1] = 3214;
        widget.valueIndexArray[0][2] = rune;
        widget.valueIndexArray[0][3] = 0;
        widget.valueIndexArray[1] = new int[4];
        widget.valueIndexArray[1][0] = 4;
        widget.valueIndexArray[1][1] = 3214;
        widget.valueIndexArray[1][2] = secondaryRune;
        widget.valueIndexArray[1][3] = 0;
        widget.valueIndexArray[2] = new int[4];
        widget.valueIndexArray[2][0] = 4;
        widget.valueIndexArray[2][1] = 3214;
        widget.valueIndexArray[2][2] = thirdRune;
        widget.valueIndexArray[2][3] = 0;
        widget.valueIndexArray[3] = new int[3];
        widget.valueIndexArray[3][0] = 1;
        widget.valueIndexArray[3][1] = 6;
        widget.valueIndexArray[3][2] = 0;
        widget.enabledSprite = Client.spriteCache.get(spellOffSprite);
        widget.disabledSprite = Client.spriteCache.get(spellOnSprite);
        Widget subWidget = addInterface(interfaceId + 1);
        subWidget.invisible = true;
        subWidget.hoverType = -1;
        setChildren(9, subWidget);
        addLunarHoverBox(interfaceId + 2, 1);
        setBounds(interfaceId + 2, 0, 0, 0, subWidget);
        addText(interfaceId + 3, "Level " + (levelRequirement) + ": " + spellName, 0xFF981F, true, true, 52, font, 1);
        setBounds(interfaceId + 3, 90, 4, 1, subWidget);
        addText(interfaceId + 4, spellDescription, 0xAF6A1A, true, true, 52, font, 0);
        setBounds(interfaceId + 4, 90, 21, 2, subWidget);
        setBounds(runeChild, 14, 48, 3, subWidget);
        setBounds(secondaryRuneChild, 74, 48, 4, subWidget);
        setBounds(thirdRuneChild, 130, 48, 5, subWidget);
        addRuneText(interfaceId + 5, runeAmount, rune, font);
        setBounds(interfaceId + 5, 26, 79, 6, subWidget);
        addRuneText(interfaceId + 6, secondaryRuneAmount, secondaryRune, font);
        setBounds(interfaceId + 6, 87, 79, 7, subWidget);
        addRuneText(interfaceId + 7, thirdRuneAmount, thirdRune, font);
        setBounds(interfaceId + 7, 142, 79, 8, subWidget);
    }

    public static void addRuneText(int interfaceId, int runeAmount, int runeId, AdvancedFont[] font) {
        Widget widget = addInterface(interfaceId);
        widget.id = interfaceId;
        widget.parent = 1151;
        widget.type = 4;
        widget.optionType = 0;
        widget.contentType = 0;
        widget.width = 0;
        widget.height = 14;
        widget.opacity = 0;
        widget.hoverType = -1;
        widget.valueCompareType = new int[1];
        widget.requiredValues = new int[1];
        widget.valueCompareType[0] = 3;
        widget.requiredValues[0] = runeAmount;
        widget.valueIndexArray = new int[1][4];
        widget.valueIndexArray[0][0] = 4;
        widget.valueIndexArray[0][1] = 3214;
        widget.valueIndexArray[0][2] = runeId;
        widget.valueIndexArray[0][3] = 0;
        widget.centerText = true;
        widget.text_type = font[0];
        widget.textShadow = true;
        widget.defaultText = "%1/" + runeAmount + "";
        widget.secondaryText = "";
        widget.textColour = 12582912;
        widget.secondaryColor = 49152;
    }

    /* Adds Input Field */
    protected static void addInputField(int identity, int characterLimit, int color, String text, int width, int height, boolean asterisks, boolean updatesEveryInput, String regex) {
        Widget field = addFullScreenInterface(identity);
        field.id = identity;
        field.type = 16;
        field.optionType = 8;
        field.defaultText = text;
        field.width = width;
        field.height = height;
        field.characterLimit = characterLimit;
        field.textColour = color;
        field.displayAsterisks = asterisks;
        field.tooltip = text;
        field.defaultInputFieldText = text;
        field.updatesEveryInput = updatesEveryInput;
        field.inputRegex = regex;
    }

    public static void addInputField(int identity, int characterLimit, int color, String text, int width, int height, boolean asterisks, boolean updatesEveryInput) {
        Widget field = addFullScreenInterface(identity);
        field.id = identity;
        field.type = 16;
        field.optionType = 8;
        field.defaultText = text;
        field.width = width;
        field.height = height;
        field.characterLimit = characterLimit;
        field.textColour = color;
        field.displayAsterisks = asterisks;
        field.defaultInputFieldText = text;
        field.tooltips = new String[]{"Clear", "Edit"};
        field.updatesEveryInput = updatesEveryInput;
    }

    public static void addInputField(int identity, int characterLimit, int color, String text, int width, int height, boolean asterisks) {
        Widget field = addFullScreenInterface(identity);
        field.id = identity;
        field.type = 16;
        field.optionType = 8;
        field.defaultText = text;
        field.width = width;
        field.height = height;
        field.characterLimit = characterLimit;
        field.textColour = color;
        field.displayAsterisks = asterisks;
        field.defaultInputFieldText = text;
        field.tooltips = new String[]{"Clear", "Edit"};
    }

    public int[] spritesToDraw;
    public String[] textToDraw;
    public int spritesPerRow;
    public static int[] displayedTeleportSprites = new int[]{};

    public static void addCloseButton(int child, int hoverChild, int hoverImageChild) {
        addHoverButtonWSpriteLoader(child, 68, 16, 16, "Close", 250, hoverChild, 3);
        addHoveredImageWSpriteLoader(hoverChild, 69, 16, 16, hoverImageChild);
    }

    public static void addHoverButtonWSpriteLoader(int interfaceId, int spriteId, int width, int height, String text, int contentType, int hoverOver, int aT) {// hoverable
        // button
        Widget tab = addTabInterface(interfaceId);
        tab.id = interfaceId;
        tab.parent = interfaceId;
        tab.type = 5;
        tab.optionType = aT;
        tab.contentType = contentType;
        tab.opacity = 0;
        tab.hoverType = hoverOver;
        tab.enabledSprite = Client.spriteCache.get(spriteId);
        tab.disabledSprite = Client.spriteCache.get(spriteId);
        tab.width = width;
        tab.height = height;
        tab.tooltip = text;
    }

    public static void addHoveredImageWSpriteLoader(int interfaceId, int spriteId, int w, int h, int imgInterface) {
        Widget tab = addTabInterface(interfaceId);
        tab.id = interfaceId;
        tab.parent = interfaceId;
        tab.type = 0;
        tab.optionType = 0;
        tab.contentType = 0;
        tab.opacity = 0;
        tab.hoverType = -1;
        tab.scrollMax = 0;
        tab.invisible = true;
        tab.width = w;
        tab.height = h;
        addHoverImageWSpriteLoader(imgInterface, spriteId);
        tab.totalChildren(1);
        tab.child(0, imgInterface, 0, 0);
    }

    public static void addHoverImageWSpriteLoader(int interfaceId, int spriteId) {
        Widget tab = addTabInterface(interfaceId);
        tab.id = interfaceId;
        tab.parent = interfaceId;
        tab.type = 5;
        tab.optionType = 0;
        tab.contentType = 0;
        tab.width = 512;
        tab.height = 334;
        tab.opacity = 0;
        tab.hoverType = 52;
        tab.enabledSprite = Client.spriteCache.get(spriteId);
        tab.disabledSprite = Client.spriteCache.get(spriteId);
    }

    public static void addButtonWSpriteLoader(int id, int sprite, String tooltip, int w, int h) {
        Widget tab = cache[id] = new Widget();
        tab.id = id;
        tab.parent = id;
        tab.type = 5;
        tab.optionType = 1;
        tab.contentType = 0;
        tab.opacity = (byte) 0;
        tab.hoverType = 52;
        if (sprite != -1) {
            tab.disabledSprite = Client.spriteCache.get(sprite);
            tab.enabledSprite = Client.spriteCache.get(sprite);
        }
        tab.width = w;
        tab.height = h;
        tab.tooltip = tooltip;
    }

    /**
     * The color a component is filled with
     */
    public int fillColor;

    public static void darken(int identity, int width, int height, int color, byte transparency) {
        Widget component = addInterface(identity);
        component.id = identity;
        component.type = DARKEN;
        component.width = width;
        component.height = height;
        component.fillColor = color;
        component.opacity = transparency;
    }

    public void children(int id, int child, int x, int y) {
        this.children[id] = child;
        this.child_x[id] = x;
        this.child_y[id] = y;
    }

    public static void addBankItem(int index) {
        Widget widget = cache[index] = new Widget();
        widget.actions = new String[5];
        widget.inventoryOffsetX = new int[20];
        widget.inventoryAmounts = new int[30];
        widget.inventoryItemId = new int[30];
        widget.itemOpacity = new int[30];
        widget.inventoryOffsetY = new int[20];

        widget.children = new int[0];
        widget.child_x = new int[0];
        widget.child_y = new int[0];

        // rsi.hasExamine = false;

        widget.inventoryMarginX = 24;
        widget.inventoryMarginY = 24;
        widget.height = 5;
        widget.width = 6;
        widget.parent = 5292;
        widget.id = index;
        widget.type = 2;
    }

    protected static void addFamiliarHead(final int interfaceID, final int width, final int height, final int zoom) {
        final Widget widget = addTabInterface(interfaceID);
        widget.type = 6;
        widget.model_type = 2;
        widget.model_id = 2449;
        widget.modelZoom = zoom;
        widget.modelRotation1 = 40;
        widget.modelRotation2 = 1800;
        widget.height = height;
        widget.width = width;
    }

    public static void addButton1(int i, int parent, int w, int h, int sprite1, int sprite2, int hoverOver, String tooltip, int itemId, boolean geSearch) {
        Widget p = addInterface(i);
        p.parent = parent;
        p.type = TYPE_SPRITE;
        p.optionType = 1;
        p.width = w;
        p.height = h;
        p.tooltip = tooltip;
        p.defaultText = tooltip;
        p.hoverType = hoverOver;
        p.isHovered = true;
        ;
        p.disabledSprite = Client.spriteCache.get(sprite1);
        p.enabledSprite = Client.spriteCache.get(sprite2);
        p.itemId = itemId;
        p.geSearchButton = geSearch;
    }

    public static void addSelection(int id, AdvancedFont[] font, Widget parent, int x, int y, int menuId) {
        //w.totalChildren(4);
        addButton1(id + 1, 41002, 161, 32, 1262, 1262, id + 2, "", menuId, true);
        addText(id + 3, "Item name", font, 1, 00000, false, false);
        addText(id + 2, "", font, 1, 00000, false, false);
        //int child = 0;
        parent.child(parentchilds++, id + 1, x, y);
        parent.child(parentchilds++, id + 2, x + 40, y + 8);
        parent.child(parentchilds++, id + 3, x + 40, y + 8);
    }

    public static void addPet(int ID) {
        Widget petCanvas = cache[ID] = new Widget();
        petCanvas.id = ID;
        petCanvas.parent = ID;
        petCanvas.type = 6;
        petCanvas.optionType = 0;
        petCanvas.contentType = 3291;
        petCanvas.width = 136;
        petCanvas.height = 168;
        petCanvas.hoverType = 0;
        petCanvas.modelZoom = 1500;
        petCanvas.modelRotation1 = 150;
        petCanvas.modelRotation2 = 0;
    }

    public static void drawHoverableRectangle(final int identity, final int width, final int height, final int color, final byte transparency) {
        final Widget component = addInterface(identity);
        component.id = identity;
        component.type = Widget.TYPE_HOVER;
        component.width = width;
        component.height = height;
        component.fillColor = color;
        component.opacity = transparency;
        component.optionType = Widget.OPTION_OK;
    }

    public static void radioButton(final int id, final String tooltip, final int enabledSprite, final int disabledSprite, final int... relatedButtons) {
        final Widget widget = addInterface(id);
        widget.tooltip = tooltip;
        widget.optionType = OPTION_OK;
        widget.type = TYPE_RADIO_BUTTON;
        widget.enabledSprite = Client.spriteCache.get(enabledSprite);
        widget.disabledSprite = Client.spriteCache.get(disabledSprite);
        widget.width = widget.enabledSprite.width;
        widget.height = widget.disabledSprite.height;
        widget.active = false;
        widget.buttons = relatedButtons;
    }

    public static void addHoverClickText(int id, String text, String tooltip, AdvancedFont[] font, int idx, int color,
                                         boolean center, boolean textShadow, int width) {
        Widget widget = addInterface(id);
        widget.id = id;
        widget.parent = id;
        widget.type = 4;
        widget.optionType = 1;
        widget.width = width;
        widget.height = 11;
        widget.contentType = 0;
        widget.opacity = 0;
        widget.hoverType = -1;
        widget.centerText = center;
        widget.textShadow = textShadow;
        widget.text_type = font[idx];
        widget.defaultText = text;
        widget.secondaryText = "";
        widget.tooltip = tooltip;
        widget.textColour = color;
        widget.secondaryColor = 0;
        widget.defaultHoverColor = 0xFFFFFF;
        widget.secondaryHoverColor = 0;
    }

    public static void addHDSpriteLoader(int childId, int spriteId) {
        Widget widget = cache[childId] = new Widget();
        widget.id = childId;
        widget.parent = childId;
        widget.type = 5;
        widget.optionType = 0;
        widget.contentType = 0;
        widget.disabledSprite = Client.spriteCache.get(spriteId);
        widget.enabledSprite = Client.spriteCache.get(spriteId);
        widget.width = widget.disabledSprite.width;
        widget.height = widget.enabledSprite.height - 2;
        widget.hightDetail = true;
    }

    public static void addItemModel(int interfaceId, int itemId, int w, int h, int zoom) {
        Widget widget = cache[interfaceId] = new Widget();
        ItemDefinition itemDef = ItemDefinition.get(itemId);
        widget.modelRotation1 = itemDef.rotation_y;
        widget.modelRotation2 = itemDef.rotation_x;
        widget.type = 6;
        widget.model_type = 4;
        widget.model_id = itemId;
        widget.modelZoom = zoom;
        widget.height = h;
        widget.width = w;
    }
}
