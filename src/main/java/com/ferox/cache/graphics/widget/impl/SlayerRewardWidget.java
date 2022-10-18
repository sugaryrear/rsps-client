package com.ferox.cache.graphics.widget.impl;

import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.Widget;

/**
 * The class which represents functionality for the slayer interface.
 *
 * @author Patrick van Elderen | 11 mrt. 2019 : 14:34:29
 * @see <a href="https://github.com/Patrick9-10-1995">Github profile</a>
 */
public class SlayerRewardWidget extends Widget {

    public static void unpack(AdvancedFont[] font) {
        unlock(font);
        extend(font);
        task(font);
        confirm(font);
        buy(font);
    }

    private static void unlock(AdvancedFont[] font) {

        Widget tab = addInterface(63400);
        addButton(63401, 531, "Select");
        addButton(63402, 530, "Select");
        addButton(63403, 530, "Select");
        addButton(63404, 530, "Select");
        int x = 9, y = 13;
        tab.totalChildren(13);
        tab.child(0, 64001, x, y);
        tab.child(1, 64002, 460 + x, 7 + y);
        tab.child(2, 64005, 244 + x, 9 + y);
        tab.child(3, 63401, 11 + x, 36 + y);
        tab.child(4, 63402, 97 + x, 36 + y);
        tab.child(5, 63403, 183 + x, 36 + y);
        tab.child(6, 63404, 269 + x, 36 + y);
        tab.child(7, 64010, 52 + x, 40 + y);
        tab.child(8, 64011, 138 + x, 40 + y);
        tab.child(9, 64012, 224 + x, 40 + y);
        tab.child(10, 64013, 310 + x, 40 + y);
        tab.child(11, 64014, 420 + x, 40 + y);
        tab.child(12, 63405, 10 + x, 59 + y);

        Widget scroll = addInterface(63405);
        scroll.totalChildren(100);
        int scrollX = 0, scrollY = 0;

        //Insert the amount of unlocks we have, currently OSRS has 26.
        int amount_of_unlocks = 25;

        //Start adding unlocks
        for (int unlocks = 0; unlocks < amount_of_unlocks; unlocks++) {

            //We start the interface at widget 63406
            addConfigButton(63406 + unlocks, 63405, 536, 537, "Unlock",750 + unlocks, 0, OPTION_TOGGLE_SETTING);

            //We need to add a new child, we added 25 so we add 25 on top of 63406
            //63406 + 25 = 63431
            addContainer(63431 + unlocks, 1, 1, 1, 1, false, false, true, "");
            //We have to add a title on top of our interface, previously the child started at 63431, we add another 25 childs
            //63431 + 25 = 63456
            addText(63456 + unlocks, "Creature Name", font, 1, 0xff9040, true, true);

            //And finally we add the description, we add another 25 childs 63456 + 25 = 63481
            addText(63481 + unlocks, "Description text here<br>Description text here<br>Description text here<br>Description text here", font, 0, 0xff9040, false, true);

            //We're not done yet, we have to add the actual scrollbar childs.

            //Leave this one alone it always adds up
            scroll.child(unlocks, 63406 + unlocks, scrollX, scrollY);

            //Here it becomes interesting: We have to add 25 childs on top of our unlocks, we add + 25 childs.

            scroll.child(unlocks + 25, 63431 + unlocks, 3 + scrollX, 3 + scrollY);
            //Here we add another 25 childs on top of the 25, so 25+25=50
            scroll.child(unlocks + 50, 63456 + unlocks, 128 + scrollX, 13 + scrollY);

            //We do exactly the same here, 50+25=75
            scroll.child(unlocks + 75, 63481 + unlocks, 7 + scrollX, 39 + scrollY);

            //Finally we have 25 unlocks and 75 childs, this meaning we have an total of 100 interface children.
            //This part is unimportant leave it.
            scrollX += 227;
            if (scrollX == 454) {
                scrollX = 0;
                scrollY += 87;
            }
        }

        //System.out.println("we currently have "+amount_of_unlocks+" unlocks.");
        scroll.width = 452;
        scroll.height = 234;
        scroll.scrollMax = scrollY;
    }

    private static void extend(AdvancedFont[] font) {
        Widget tab = addInterface(64300);
        addButton(64301, 530, "Select");
        addButton(64302, 531, "Select");
        addButton(64303, 530, "Select");
        addButton(64304, 530, "Select");
        int x = 9, y = 13;
        tab.totalChildren(13);
        tab.child(0, 64001, x, y);
        tab.child(1, 64002, 460 + x, 7 + y);
        tab.child(2, 64005, 244 + x, 9 + y);
        tab.child(3, 64301, 11 + x, 36 + y);
        tab.child(4, 64302, 97 + x, 36 + y);
        tab.child(5, 64303, 183 + x, 36 + y);
        tab.child(6, 64304, 269 + x, 36 + y);
        tab.child(7, 64010, 52 + x, 40 + y);
        tab.child(8, 64011, 138 + x, 40 + y);
        tab.child(9, 64012, 224 + x, 40 + y);
        tab.child(10, 64013, 310 + x, 40 + y);
        tab.child(11, 64014, 420 + x, 40 + y);
        tab.child(12, 64305, 10 + x, 59 + y);

        Widget scroll = addInterface(64305);
        scroll.totalChildren(100);
        int scrollX = 0, scrollY = 0;

        //Insert the amount of extend buttons we have, currently OSRS has 25.
        int amount_of_extend_buttons = 25;

        //Start adding extend buttons
        for (int extend_button = 0; extend_button < amount_of_extend_buttons; extend_button++) {

            //We start the interface at widget 64306
            addConfigButton(64306 + extend_button, 64305, 536, 537, "Unlock",560 + extend_button, 0, OPTION_TOGGLE_SETTING);

            //We need to add a new child, we added 25 so we add 25 on top of 64306
            //64306 + 25 = 64331
            addContainer(64331 + extend_button, 1, 1, 1, 1, false, false, true, "");
            //We have to add a title on top of our interface, previously the child started at 64331, we add another 25 childs
            //64331 + 25 = 64356
            addText(64356 + extend_button, "Creature Name", font, 1, 0xe59e44, true, true);

            //And finally we add the description, we add another 25 childs 64356 + 25 = 64381
            addText(64381 + extend_button, "Description text here<br>Description text here<br>Description text here<br>Description text here", font, 0, 0xe59e44, false, true);

            //We're not done yet, we have to add the actual scrollbar childs.

            //Leave this one alone it always adds up
            scroll.child(extend_button, 64306 + extend_button, scrollX, scrollY);

            //Here it becomes interesting: We have to add 25 childs on top of our extend buttons, we add + 25 childs.
            scroll.child(extend_button + 25, 64331 + extend_button, 3 + scrollX, 3 + scrollY);

            //Here we add another 25 childs on top of the 25, so 25+25=50
            scroll.child(extend_button + 50, 64356 + extend_button, 128 + scrollX, 13 + scrollY);

            //We do exactly the same here, 50+25=75
            scroll.child(extend_button + 75, 64381 + extend_button, 6 + scrollX, 39 + scrollY);

            //Finally we have 25 extend buttons and 75 childs, this meaning we have an total of 100 interface children.
            //This part is unimportant leave it.
            scrollX += 227;
            if (scrollX == 454) {
                scrollX = 0;
                scrollY += 87;
            }
        }
        scrollY += 87;

        scroll.width = 452;
        scroll.height = 234;
        scroll.scrollMax = scrollY;
    }

    private static void task(AdvancedFont[] font) {

        Widget tab = addInterface(63200);
        addButton(63201, 530, "Select");
        addButton(63202, 530, "Select");
        addButton(63203, 530, "Select");
        addButton(63204, 531, "Select");
        addText(63205,
            "You may spend points to <col=ffffff>Cancel<col=ff9040> or <col=ffffff>Block<col=ff9040> your current task.<br>"
                + " If you <col=ffffff>cancel<col=ff9040> it, you may be assigned that target again in future. <col=ff0000>(10 points)<br>"
                + " If you <col=ffffff>block<col=ff9040> it, you will not get that assignment again. <col=ff0000>(100 points)<br>"
                + " <col=ffffff>Neither<col=ff9040> option will reset your current tally of completed Slayer tasks.", font, 0, 0xff9040, true, true);
        addSprite(63206, 533);
        addText(63207, "Current assignment:", font, 2, 0xFFA500, true, true);
        addText(63208, "None", font, 1, 0xffffff, true, true);
        addButton(63209, 530, "Select");
        addButton(63210, 530, "Select");
        addText(63211, "Cancel task", font, 0, 0xFFA500, true, true);
        addText(63212, "Block task", font, 0, 0xFFA500, true, true);
        addText(63213, "Blocked tasks:", font, 2, 0xFFA500, true, true);
        int x = 9, y = 13;
        tab.totalChildren(45);
        tab.child(0, 64001, x, y);
        tab.child(1, 64002, 460 + x, 7 + y);
        tab.child(2, 64005, 244 + x, 9 + y);
        tab.child(3, 63201, 11 + x, 36 + y);
        tab.child(4, 63202, 97 + x, 36 + y);
        tab.child(5, 63203, 183 + x, 36 + y);
        tab.child(6, 63204, 269 + x, 36 + y);
        tab.child(7, 64010, 52 + x, 40 + y);
        tab.child(8, 64011, 138 + x, 40 + y);
        tab.child(9, 64012, 224 + x, 40 + y);
        tab.child(10, 64013, 310 + x, 40 + y);
        tab.child(11, 64014, 420 + x, 40 + y);
        tab.child(12, 63205, 244 + x, 60 + y);
        tab.child(13, 63206, 10 + x, 105 + y);
        tab.child(14, 63207, 140 + x, 110 + y);
        tab.child(15, 63208, 140 + x, 126 + y);
        tab.child(16, 63209, 286 + x, 120 + y);
        tab.child(17, 63210, 386 + x, 120 + y);
        tab.child(18, 63211, 327 + x, 124 + y);
        tab.child(19, 63212, 427 + x, 124 + y);
        tab.child(20, 63213, 244 + x, 148 + y);
        int yy = 0;
        for (int i = 0; i < 6; i++) {
            addText(63214 + i, "Slot " + (i + 1) + ":", font, 1, 0xFFA500, true, true);
            addText(63220 + i, "Empty", font, 1, 0xFFA500, true, true);
            addButton(63226 + i, 530, "Select");
            addText(63232 + i, "Unblock task", font, 0, -8434673, true, true);
            tab.child(21 + i, 63214 + i, 61 + x, 164 + y + yy);
            tab.child(27 + i, 63220 + i, 244 + x, 164 + y + yy);
            tab.child(33 + i, 63226 + i, 360 + x, 161 + y + yy);
            tab.child(39 + i, 63232 + i, 401 + x, 165 + y + yy);
            yy += 22;
        }
    }

    private static void confirm(AdvancedFont[] font) {

        Widget tab = addInterface(63100);
        addSprite(63101, 532);
        addButton(63102, 530, "Select");
        addButton(63103, 530, "Select");
        addText(63104, "Back", font, 0, 0xFFA500, true, true);
        addText(63105, "Confirm", font, 0, 0xFFA500, true, true);
        addText(63106, "Name", font, 1, 0xFFA500, true, true);
        addText(63107, "", font, 1, 0xFFA500, true, true);
        addText(63108, "", font, 1, 0xFFA500, true, true);
        addText(63109, "", font, 1, 0xFFA500, true, true);
        addText(63110, "Cost:", font, 1, 0xff0000, true, true);
        addText(63111, "", font, 1, 0xFFA500, true, true);
        addText(63112, "", font, 1, 0xFFA500, true, true);
        int x = 9, y = 13;
        tab.totalChildren(15);
        tab.child(0, 64001, x, y);
        tab.child(1, 64002, 460 + x, 7 + y);
        tab.child(2, 64005, 244 + x, 9 + y);
        tab.child(3, 63101, 100 + x, 75 + y);
        tab.child(4, 63102, 134 + x, 241 + y);
        tab.child(5, 63103, 251 + x, 241 + y);
        tab.child(6, 63104, 175 + x, 245 + y);
        tab.child(7, 63105, 292 + x, 245 + y);
        tab.child(8, 63106, 234 + x, 102 + y);
        tab.child(9, 63107, 234 + x, 125 + y);
        tab.child(10, 63108, 234 + x, 138 + y);
        tab.child(11, 63109, 234 + x, 151 + y);
        tab.child(12, 63110, 234 + x, 174 + y);
        tab.child(13, 63111, 234 + x, 197 + y);
        tab.child(14, 63112, 234 + x, 210 + y);
    }

    private static void buy(AdvancedFont[] font) {
        Widget tab = addInterface(64000);
        addSprite(64001, 529);
        closeButton(64002, 107, 108, false);
        addText(64005, "Slayer Rewards", font, 2, 0xFFA500, true, true);
        addButton(64006, 530, "Select");
        addButton(64007, 530, "Select");
        addButton(64008, 531, "Select");
        addButton(64009, 530, "Select");
        addText(64010, "Unlock", font, 0, 0xFFA500, true, true);
        addText(64011, "Extend", font, 0, 0xFFA500, true, true);
        addText(64012, "Buy", font, 0, 0xFFA500, true, true);
        addText(64013, "Tasks", font, 0, 0xFFA500, true, true);
        addText(64014, "Reward Points: ###", font, 0, 0xFFA500, true, true);
        int x = 9, y = 13;
        tab.totalChildren(13);
        tab.child(0, 64001, x, y);
        tab.child(1, 64002, 460 + x, 7 + y);
        tab.child(2, 64005, 244 + x, 9 + y);
        tab.child(3, 64006, 11 + x, 36 + y);
        tab.child(4, 64007, 97 + x, 36 + y);
        tab.child(5, 64008, 183 + x, 36 + y);
        tab.child(6, 64009, 269 + x, 36 + y);
        tab.child(7, 64010, 52 + x, 40 + y);
        tab.child(8, 64011, 138 + x, 40 + y);
        tab.child(9, 64012, 224 + x, 40 + y);
        tab.child(10, 64013, 310 + x, 40 + y);
        tab.child(11, 64014, 420 + x, 40 + y);
        tab.child(12, 64015, 35 + x, 59 + y);

        Widget scroll = addInterface(64015);
        scroll.totalChildren(50);
        addContainer(64016, 5, 10, 60, 30, false,"Info", "Buy 1", "Buy 5", "Buy 10");
        scroll.child(0, 64016, 0, 0);
        int xx = 0, yy = 34;
        for (int i = 1; i < 50; i++) {
            addText(64016 + i, "", font, 1, 0xFFA500, true, true);
            scroll.child(i, 64016 + i, xx + 16, yy);
            xx += 92;
            if (xx == 460) {
                xx = 0;
                yy += 62;
            }
        }
        scroll.width = 426;
        scroll.height = 234;
        scroll.scrollMax = 0;
    }

}
