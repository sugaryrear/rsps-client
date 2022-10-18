package com.ferox.cache.graphics.widget.impl;

import com.ferox.ClientConstants;
import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.Widget;

/**
 * The user interface for the trading post.
 * @author Patrick van Elderen | April, 03, 2021, 11:35
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class TradingPostWidget extends Widget {

    public static void unpack(AdvancedFont[] font) {
        main(font);
        history(font);
        buy(font);
    }

    private static void main(AdvancedFont[] font) {
        Widget widget = addInterface(66000);

        addSprite(66001, 1458);
        closeButton(66002, 107, 108, true);
        addText(66003, ClientConstants.CLIENT_NAME + " Marketplace", font, 2, 0xdb9c22, true, true);
        addText(66004, "Your sales", font, 2, 0xdb9c22, true, true);
        addHoverButton(66008, 1456, 103, 62, "History", -1, 66009, 1);
        addHoveredButton(66009, 1457, 103, 62, 66010);
        addHoverButton(66011, 1452, 103, 34, "Search item", -1, 66012, 1);
        addHoveredButton(66012, 1453, 103, 34, 66013);
        addHoverButton(66014, 1452, 103, 34, "Search user", -1, 66015, 1);
        addHoveredButton(66015, 1453, 103, 34, 66016);
        addHoverButton(66017, 1452, 103, 34, "Recent sales", -1, 66018, 1);
        addHoveredButton(66018, 1453, 103, 34, 66019);
        addText(66021, "History", font, 2, 0xdb9c22, true, true);
        addText(66022, "Search item", font, 2, 0xdb9c22, true, true);
        addText(66023, "Search user", font, 2, 0xdb9c22, true, true);
        addText(66024, "Recent sales", font, 2, 0xdb9c22, true, true);

        Widget scroll_widget = addTabInterface(66026);
        scroll_widget.scrollPosition = 0;
        scroll_widget.contentType = 0;
        scroll_widget.width = 135;
        scroll_widget.height = 205;
        scroll_widget.scrollMax = 527;

        int y = 2;
        final int CHILD_LENGTH = 25 * 8;
        int child = 0;
        scroll_widget.totalChildren(CHILD_LENGTH);
        int section = 0;
        for (int i = 66030; i < 66030 + CHILD_LENGTH; i += 8) {
            section++;
            addSprite(i, section % 2 == 0 ? 1475 : 1476);
            addText(i + 1, "Elysian spirit shield", font, 0, 16750623);
            addText(i + 2, "100K PKP", font, 0, 16750623);
            addText(i + 3, "0/1 Sold", font, 0, 16750623);
            drawProgressBar(i + 4, 88, 12, 40);
            addItem(i + 5,false);
            addButton(i + 6, 1479,"Select");
            addText(i + 7, "Claim", font, 0, 16750623);

            scroll_widget.child(child++, i, 0, y);
            scroll_widget.child(child++, i + 1, 10, y + 32);
            scroll_widget.child(child++, i + 2, 35, y + 5);
            scroll_widget.child(child++, i + 3, 60, y + 21);
            scroll_widget.child(child++, i + 4, 38, y + 20);
            scroll_widget.child(child++, i + 5, 0, y + 2);
            scroll_widget.child(child++, i + 6, 90, y + 2);
            scroll_widget.child(child++, i + 7, 98, y + 4);
            y += 53;
        }

        widget.totalChildren(17);
        widget.child(0, 66001, 50, 25);//background sprite
        widget.child(1, 66002, 433, 32);//close button
        widget.child(2, 66003, 260, 35);//UI title
        widget.child(3, 66004, 380, 62);//Sales title
        widget.child(4, 66008, 130, 75);//History button
        widget.child(5, 66009, 130, 75);//History hover button
        widget.child(6, 66011, 125, 150);//Search item button
        widget.child(7, 66012, 125, 150);//Search item hover button
        widget.child(8, 66014, 125, 195);//Search user button
        widget.child(9, 66015, 125, 195);//Search user hover button
        widget.child(10, 66017, 125, 240);//Recent sales button
        widget.child(11, 66018, 125, 240);//Recent sales hover button
        widget.child(12, 66021, 198, 98);//History title
        widget.child(13, 66022, 188, 158);//Search item title
        widget.child(14, 66023, 188, 203);//Search user title
        widget.child(15, 66024, 190, 248);//Recent sales title
        widget.child(16, 66026, 305, 85);//Recent sales title
    }

    private static void history(AdvancedFont[] font) {
        Widget widget = addInterface(66300);

        addSprite(66301, 1483);
        closeButton(66302, 107, 108, true);
        addText(66303, "Marketplace history", font, 2, 0xdb9c22, true, true);
        addText(66304, "Item", font, 0, 0xdb9c22, true, true);
        addText(66305, "Price", font, 0, 0xdb9c22, true, true);
        addText(66306, "Seller/Buyer", font, 0, 0xdb9c22, true, true);
        addText(66307, "Date", font, 0, 0xdb9c22, true, true);

        //Move these at least 120 childs further so we don't have to shift the scroll widget.
        addHoverButton(66450, 1477, 41, 18, "Back", -1, 66451, 1);
        addHoveredButton(66451, 1478, 41, 18, 66452);

        widget.totalChildren(10);
        widget.child(0, 66301, 50, 25);//background sprite
        widget.child(1, 66302, 433, 32);//close button
        widget.child(2, 66303, 260, 35);//UI title
        widget.child(3, 66304, 130, 56);//Item title
        widget.child(4, 66305, 215, 56);//Price title
        widget.child(5, 66306, 300, 56);//Seller/Buyer title
        widget.child(6, 66307, 425, 56);//Date title
        widget.child(7, 66310, 55, 70);//Scroll interface
        widget.child(8, 66450, 60, 33);//Back button
        widget.child(9, 66451, 60, 33);//Back hover button

        Widget scroll_widget = addTabInterface(66310);
        scroll_widget.scrollPosition = 0;
        scroll_widget.contentType = 0;
        scroll_widget.width = 383;
        scroll_widget.height = 220;
        scroll_widget.scrollMax = 390;

        int y = 2;
        final int CHILD_LENGTH = 15 * 7;
        int child = 0;
        scroll_widget.totalChildren(CHILD_LENGTH);
        int section = 0;
        for (int i = 66330; i < 66330 + CHILD_LENGTH; i += 7) {
            section++;
            addSprite(i, section % 2 == 0 ? 1481 : 1482);
            addItem(i + 1,false);
            addText(i + 2, "Bought", font, 0, 16750623);
            addText(i + 3, "1x Elysian spirit shield", font, 0, 16750623);
            addText(i + 4, "1000K PKP", font, 0, 16750623);
            addText(i + 5, "Patrick12345", font, 0, 16750623);
            addText(i + 6, "31-12-2021", font, 0, 16750623);

            scroll_widget.child(child++, i, 0, y);
            scroll_widget.child(child++, i + 1, 10, y);
            scroll_widget.child(child++, i + 2, 60, y + 5);
            scroll_widget.child(child++, i + 3, 10, y + 20);
            scroll_widget.child(child++, i + 4, 137, y + 10);
            scroll_widget.child(child++, i + 5, 215, y + 10);
            scroll_widget.child(child++, i + 6, 320, y + 10);
            y += 38;
        }
    }

    private static void buy(AdvancedFont[] font) {
        Widget widget = addInterface(66600);

        addSprite(66601, 1483);
        closeButton(66602, 107, 108, true);
        addText(66603, "Showing offers for item: 5$ bond", font, 2, 0xdb9c22, true, true);
        addText(66604, "Quantity", font, 0, 0xdb9c22, true, true);
        addText(66605, "Item", font, 0, 0xdb9c22, true, true);
        addText(66606, "Price (ea)", font, 0, 0xdb9c22, true, true);
        addText(66607, "Seller", font, 0, 0xdb9c22, true, true);
        addHoverButton(66608, 1477, 18, 18, "Back", -1, 66609, 1);
        addHoveredButton(66609, 1478, 18, 18, 66610);
        addHoverButton(66851, 1778, 31, 21, "Refresh", -1, 66852, 1);
        addHoveredButton(66852, 1779, 31, 21, 66853);

        widget.totalChildren(12);
        widget.child(0, 66601, 50, 25);//background sprite
        widget.child(1, 66602, 433, 32);//close button
        widget.child(2, 66603, 260, 35);//UI title
        widget.child(3, 66604, 90, 56);//Quantity title
        widget.child(4, 66605, 160, 56);//Item title
        widget.child(5, 66606, 255, 56);//Price (ea) title
        widget.child(6, 66607, 365, 56);//Seller title
        widget.child(7, 66608, 60, 33);//Back button
        widget.child(8, 66609, 60, 33);//Back hover button
        widget.child(9, 66851, 80, 31);//Refresh button
        widget.child(10, 66852, 80, 31);//Refresh hover button
        widget.child(11, 66612, 55, 70);//Scroll widget

        Widget scroll_widget = addTabInterface(66612);
        scroll_widget.scrollPosition = 0;
        scroll_widget.contentType = 0;
        scroll_widget.width = 383;
        scroll_widget.height = 220;
        scroll_widget.scrollMax = 268;

        int y = 2;
        final int CHILD_LENGTH = 25 * 8;
        int child = 0;
        scroll_widget.totalChildren(CHILD_LENGTH);
        int section = 0;
        for (int i = 66630; i < 66630 + CHILD_LENGTH; i += 8) {
            section++;
            addSprite(i, section % 2 == 0 ? 1481 : 1482);
            addItem(i + 1,true);
            addText(i + 2, "Elysian spirit shield", font, 0, 16750623);
            addText(i + 3, "1000K PKP", font, 0, 16750623);
            addText(i + 4, "Patrick", font, 0, 16750623);
            addHoverButton(i + 5, 1479, 41, 18, "Buy", -1, i + 6, 1);
            addHoveredButton(i + 6, 1480, 41, 18, 66625);
            addText(i + 7, "Buy", font, 0, 16750623);

            scroll_widget.child(child++, i, 0, y);
            scroll_widget.child(child++, i + 1, 18, y);
            scroll_widget.child(child++, i + 2, 60, y + 11);
            scroll_widget.child(child++, i + 3, 187, y + 11);
            scroll_widget.child(child++, i + 4, 270, y + 11);
            scroll_widget.child(child++, i + 5, 340, y + 7);
            scroll_widget.child(child++, i + 6, 340, y + 7);
            scroll_widget.child(child++, i + 7, 352, y + 10);
            y += 38;
        }
    }
}
