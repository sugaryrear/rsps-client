package com.ferox.cache.graphics.widget.impl;

import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.Widget;

/**
 * @author Patrick van Elderen | February, 20, 2021, 22:17
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class OrnateJewelleryBoxWidget extends Widget {

    public static final int ORNATE_JEWELLERY_BOX = 65000;

    public static void unpack(AdvancedFont[] font) {
        widget(font);
    }

    private static void widget(AdvancedFont[] font) {
        Widget widget = addTabInterface(ORNATE_JEWELLERY_BOX);

        addSprite(ORNATE_JEWELLERY_BOX + 1, 1032);
        addText(ORNATE_JEWELLERY_BOX + 2,"Ornate jewellery box", font, 2, 0xff981f, false, true);
        closeButton(ORNATE_JEWELLERY_BOX + 3, 142, 143, false);

        addContainer(ORNATE_JEWELLERY_BOX + 4, TYPE_CONTAINER, 1, 15, 10, 8, 0, false, false, false);
        addContainer(ORNATE_JEWELLERY_BOX + 5, TYPE_CONTAINER, 1, 15, 10, 8, 0, false, false, false);
        addContainer(ORNATE_JEWELLERY_BOX + 6, TYPE_CONTAINER, 1, 15, 10, 8, 0, false, false, false);
        addContainer(ORNATE_JEWELLERY_BOX + 7, TYPE_CONTAINER, 1, 15, 10, 8, 0, false, false, false);
        addContainer(ORNATE_JEWELLERY_BOX + 8, TYPE_CONTAINER, 1, 15, 10, 8, 0, false, false, false);
        addContainer(ORNATE_JEWELLERY_BOX + 9, TYPE_CONTAINER, 1, 15, 10, 8, 0, false, false, false);

        addText(ORNATE_JEWELLERY_BOX + 10,"Ring of<br>Dueling", font, 0, 0xffffff, false, true);
        addText(ORNATE_JEWELLERY_BOX + 11,"Combat<br>bracelet", font, 0, 0xffffff, false, true);
        addText(ORNATE_JEWELLERY_BOX + 12,"Ring of<br>Wealth", font, 0, 0xffffff, false, true);
        addText(ORNATE_JEWELLERY_BOX + 13,"Games<br>Necklace", font, 0, 0xffffff, false, true);
        addText(ORNATE_JEWELLERY_BOX + 14,"Skills<br>necklace", font, 0, 0xffffff, false, true);
        addText(ORNATE_JEWELLERY_BOX + 15,"Amulet of<br>Glory", font, 0, 0xffffff, false, true);

        addClickableText(ORNATE_JEWELLERY_BOX + 16,"<col=a7a7d0>1:</col> Duel Arena","Duel Arena", font, 0, 0xff981f, false, true, 65);
        addClickableText(ORNATE_JEWELLERY_BOX + 17,"<col=a7a7d0>2:</col> Castle Wars","Castle Wars", font, 0, 0xff981f, false, true, 73);
        addClickableText(ORNATE_JEWELLERY_BOX + 18,"<col=a7a7d0>3:</col> Clan Wars","Clan Wars", font, 0, 0xff981f, false, true, 63);

        addClickableText(ORNATE_JEWELLERY_BOX + 19,"<col=a7a7d0>9:</col> Warriors' Guild","Warriors' Guild", font, 0, 0xff981f, false, true, 90);
        addClickableText(ORNATE_JEWELLERY_BOX + 20,"<col=a7a7d0>A:</col> Champions' Guild","Champions' Guild", font, 0, 0xff981f, false, true, 98);
        addClickableText(ORNATE_JEWELLERY_BOX + 21,"<col=a7a7d0>B:</col> Monastery","Monastery", font, 0, 0xff981f, false, true, 65);
        addClickableText(ORNATE_JEWELLERY_BOX + 22,"<col=a7a7d0>C:</col> Ranging Guild","Ranging Guild", font, 0, 0xff981f, false, true, 82);

        addClickableText(ORNATE_JEWELLERY_BOX + 23,"<col=a7a7d0>I:</col> Miscellania","Miscellania", font, 0, 0xff981f, false, true, 65);
        addClickableText(ORNATE_JEWELLERY_BOX + 24,"<col=a7a7d0>J:</col> Grand Exchange","Grand Exchange", font, 0, 0xff981f, false, true, 93);
        addClickableText(ORNATE_JEWELLERY_BOX + 25,"<col=a7a7d0>K:</col> Falador Park","Falador Park", font, 0, 0xff981f, false, true, 77);
        addClickableText(ORNATE_JEWELLERY_BOX + 26,"<col=a7a7d0>L:</col> Dondakan's Rock","Dondakan's Rock", font, 0, 0xff981f, false, true, 98);

        addClickableText(ORNATE_JEWELLERY_BOX + 27,"<col=a7a7d0>4:</col> Burthope","Burthope", font, 0, 0xff981f, false, true, 57);
        addClickableText(ORNATE_JEWELLERY_BOX + 28,"<col=a7a7d0>5:</col> Barbarian Outpost","Barbarian Outpost", font, 0, 0xff981f, false, true, 106);
        addClickableText(ORNATE_JEWELLERY_BOX + 29,"<col=a7a7d0>6:</col> Corporeal Beast","Corporeal Beast", font, 0, 0xff981f, false, true, 98);
        addClickableText(ORNATE_JEWELLERY_BOX + 30,"<col=a7a7d0>7:</col> Tears of Guthix","Tears of Guthix", font, 0, 0xff981f, false, true, 90);
        addClickableText(ORNATE_JEWELLERY_BOX + 31,"<col=a7a7d0>8:</col> Wintertodt Camp","Wintertodt Camp", font, 0, 0xff981f, false, true, 96);

        addClickableText(ORNATE_JEWELLERY_BOX + 32,"<col=a7a7d0>D:</col> Fishing Guild","Fishing Guild", font, 0, 0xff981f, false, true, 77);
        addClickableText(ORNATE_JEWELLERY_BOX + 33,"<col=a7a7d0>E:</col> Mining Guild","Mining Guild", font, 0, 0xff981f, false, true, 73);
        addClickableText(ORNATE_JEWELLERY_BOX + 34,"<col=a7a7d0>F:</col> Crafting Guild","Crafting Guild", font, 0, 0xff981f, false, true, 82);
        addClickableText(ORNATE_JEWELLERY_BOX + 35,"<col=a7a7d0>G:</col> Cooking Guild","Cooking Guild", font, 0, 0xff981f, false, true, 82);
        addClickableText(ORNATE_JEWELLERY_BOX + 36,"<col=a7a7d0>H:</col> Woodcutting Guild","Woodcutting Guild", font, 0, 0xff981f, false, true, 104);

        addClickableText(ORNATE_JEWELLERY_BOX + 37,"<col=a7a7d0>M:</col> Edgevile","Edgevile", font, 0, 0xff981f, false, true, 57);
        addClickableText(ORNATE_JEWELLERY_BOX + 38,"<col=a7a7d0>N:</col> Karamja","Karamja", font, 0, 0xff981f, false, true, 55);
        addClickableText(ORNATE_JEWELLERY_BOX + 39,"<col=a7a7d0>O:</col> Draynor Village","Draynor Village", font, 0, 0xff981f, false, true, 93);
        addClickableText(ORNATE_JEWELLERY_BOX + 40,"<col=a7a7d0>P:</col> Al Kharid","Al Kharid", font, 0, 0xff981f, false, true, 59);

        widget.totalChildren(40);
        widget.child(0, ORNATE_JEWELLERY_BOX + 1, 19, 10);
        widget.child(1, ORNATE_JEWELLERY_BOX + 2, 200, 18);
        widget.child(2, ORNATE_JEWELLERY_BOX + 3, 467, 17);
        widget.child(3, ORNATE_JEWELLERY_BOX + 4, 53, 68);
        widget.child(4, ORNATE_JEWELLERY_BOX + 5, 53, 158);
        widget.child(5, ORNATE_JEWELLERY_BOX + 6, 53, 248);
        widget.child(6, ORNATE_JEWELLERY_BOX + 7, 286, 68);
        widget.child(7, ORNATE_JEWELLERY_BOX + 8, 286, 158);
        widget.child(8, ORNATE_JEWELLERY_BOX + 9, 286, 248);
        widget.child(9, ORNATE_JEWELLERY_BOX + 10, 53, 105);
        widget.child(10, ORNATE_JEWELLERY_BOX + 11, 53, 195);
        widget.child(11, ORNATE_JEWELLERY_BOX + 12, 53, 285);
        widget.child(12, ORNATE_JEWELLERY_BOX + 13, 286, 105);
        widget.child(13, ORNATE_JEWELLERY_BOX + 14, 286, 195);
        widget.child(14, ORNATE_JEWELLERY_BOX + 15, 286, 285);
        widget.child(15, ORNATE_JEWELLERY_BOX + 16, 153, 60);
        widget.child(16, ORNATE_JEWELLERY_BOX + 17, 145, 85);
        widget.child(17, ORNATE_JEWELLERY_BOX + 18, 153, 112);
        widget.child(18, ORNATE_JEWELLERY_BOX + 19, 133, 148);
        widget.child(19, ORNATE_JEWELLERY_BOX + 20, 130, 167);
        widget.child(20, ORNATE_JEWELLERY_BOX + 21, 143, 186);
        widget.child(21, ORNATE_JEWELLERY_BOX + 22, 133, 204);
        widget.child(22, ORNATE_JEWELLERY_BOX + 23, 143, 237);
        widget.child(23, ORNATE_JEWELLERY_BOX + 24, 131, 255);
        widget.child(24, ORNATE_JEWELLERY_BOX + 25, 140, 272);
        widget.child(25, ORNATE_JEWELLERY_BOX + 26, 129, 290);
        widget.child(26, ORNATE_JEWELLERY_BOX + 27, 375, 57);
        widget.child(27, ORNATE_JEWELLERY_BOX + 28, 352, 71);
        widget.child(28, ORNATE_JEWELLERY_BOX + 29, 360, 86);
        widget.child(29, ORNATE_JEWELLERY_BOX + 30, 363, 101);
        widget.child(30, ORNATE_JEWELLERY_BOX + 31, 360, 115);
        widget.child(31, ORNATE_JEWELLERY_BOX + 32, 365, 146);
        widget.child(32, ORNATE_JEWELLERY_BOX + 33, 367, 161);
        widget.child(33, ORNATE_JEWELLERY_BOX + 34, 363, 177);
        widget.child(34, ORNATE_JEWELLERY_BOX + 35, 363, 192);
        widget.child(35, ORNATE_JEWELLERY_BOX + 36, 360, 206);
        widget.child(36, ORNATE_JEWELLERY_BOX + 37, 380, 240);
        widget.child(37, ORNATE_JEWELLERY_BOX + 38, 380, 257);
        widget.child(38, ORNATE_JEWELLERY_BOX + 39, 363, 275);
        widget.child(39, ORNATE_JEWELLERY_BOX + 40, 380, 293);
    }
}
