package com.ferox.cache.graphics.widget.impl;

import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.Widget;

/**
 * @author Patrick van Elderen | April, 13, 2021, 10:39
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class DailyTasksWidget extends Widget {

    public static void unpack(AdvancedFont[] font) {
        Widget widget = addTabInterface(41500);
        addSprite(41501, 1762);
        closeButton(41502, 24, 25, true);
        addText(41503, "Time remaining: 23 hours 22 minutes 10 seconds", font, 2, 0xF7AA25, true, true);
        addSprite(41504, 186);
        addText(41505, "Task List", font, 2, 0xF7AA25, true, true);
        addButton(41506, 1765,"Daily PvP Tasks");
        addButton(41507, 1764,"Daily PvM Tasks");
        addButton(41508, 1763,"Other Daily Tasks");
        addText(41509, "PvP", font, 2, 0xF7AA25, true, true);
        addText(41510, "PvM", font, 2, 0xF7AA25, true, true);
        addText(41511, "Other", font, 2, 0xF7AA25, true, true);
        addText(41512, "Task", font, 2, 0xF7AA25, true, true);
        drawProgressBar(41513,186,20,40);
        addText(41514, "Progress: 40% (1/10000)", font, 2, 0xffffff, true, true);
        addText(41515, "Lorem ipsum dolor sit amet,<br>consectetur adipiscing elit, sed<br>do eiusmod tempor incididuntut<br>labore et dolore magna aliqua.", font, 2, 0xffffff, true, true);

        addButton(41516, 1829,"Claim Reward");
        addText(41517, "Claim", font, 2, 0xF7AA25, true, true);

        addContainer(41518, TYPE_CONTAINER, 2, 1, 13, 0, 0, false, true, true);
        addContainer(41519, TYPE_CONTAINER, 2, 1, 13, 0, 0, false, true, true);

        for (int i = 41521; i < 41521 + 20; i += 2) {
            addButton(i, 1828,"Select");
            addText(i + 1, "Thieving", font, 1, 0xF7AA25, false, true);
        }
        //They jump by 2, 1 is button second string

        int child = 0;
        widget.totalChildren(39);
        widget.child(child++, 41501, 6, 10);
        widget.child(child++, 41502, 480, 20);
        widget.child(child++, 41503, 275, 19);
        widget.child(child++, 41504, 17, 50);
        widget.child(child++, 41505, 73, 54);
        widget.child(child++, 41506, 143, 60);
        widget.child(child++, 41507, 262, 50);
        widget.child(child++, 41508, 380, 60);
        widget.child(child++, 41509, 195, 63);
        widget.child(child++, 41510, 315, 53);
        widget.child(child++, 41511, 435, 63);
        widget.child(child++, 41512, 295, 87);
        widget.child(child++, 41513, 203, 182);
        widget.child(child++, 41514, 290, 184);
        widget.child(child++, 41515, 290, 110);
        widget.child(child++, 41516, 240, 224);
        widget.child(child++, 41517, 293, 227);
        widget.child(child++, 41518, 197, 259);
        widget.child(child++, 41519, 308, 259);
        int y = 78;
        for (int i = 41521; i < 41521 + 20; i += 2) {
            widget.child(child++, i,17, y);
            widget.child(child++, i + 1,23,y + 3);
            y+= 23;
        }
    }
}
