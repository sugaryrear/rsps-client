package com.ferox.cache.graphics.widget.impl;

import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.Widget;

/**
 *
 * The class which represents functionality for the clan chat interface.
 *
 * @author <a href="http://www.rune-server.org/members/_Patrick_/">Patrick van
 *         Elderen</a>
 *
 */
public class ClanChatSidebarWidget extends Widget {

    public static void editClan(AdvancedFont[] font) {
        Widget widget = addTabInterface(40172);
        addSprite(47251, 1157);
        addHoverButton(47252, 1143, 150, 35, "Set name", -1, 47253, 1);
        addHoveredButton(47253, 1144, 150, 35, 47254);
        addHoverButton(47255, 1143, 150, 35, "Anyone", -1, 47256, 1);
        addHoveredButton(47256, 1144, 150, 35, 47257);
        addHoverButton(48000, -1, 150, 35, "<img=204> Only me", -1, 47999, 1);
        addHoverButton(48001, -1, 150, 35, "<img=203> General+", -1, 47999, 1);
        addHoverButton(48002, -1, 150, 35, "<img=202> Captain+", -1, 47999, 1);
        addHoverButton(48003, -1, 150, 35, "<img=201> Lieutenant+", -1, 47999, 1);
        addHoverButton(48004, -1, 150, 35, "<img=200> Sergeant+", -1, 47999, 1);
        addHoverButton(48005, -1, 150, 35, "<img=199> Corporal+", -1, 47999, 1);
        addHoverButton(48006, -1, 150, 35, "<img=198> Recruit+", -1, 47999, 1);
        addHoverButton(48007, -1, 150, 35, "<img=197> Any friends", -1, 47999, 1);
        addHoverButton(47258, 1143, 150, 35, "Anyone", -1, 47259, 1);
        addHoveredButton(47259, 1144, 150, 35, 17260);
        addHoverButton(48010, -1, 150, 35, "<img=204> Only me", -1, 47999, 1);
        addHoverButton(48011, -1, 150, 35, "<img=203> General+", -1, 47999, 1);
        addHoverButton(48012, -1, 150, 35, "<img=202> Captain+", -1, 47999, 1);
        addHoverButton(48013, -1, 150, 35, "<img=201> Lieutenant+", -1, 47999, 1);
        addHoverButton(48014, -1, 150, 35, "<img=200> Sergeant+", -1, 47999, 1);
        addHoverButton(48015, -1, 150, 35, "<img=199> Corporal+", -1, 47999, 1);
        addHoverButton(48016, -1, 150, 35, "<img=198> Recruit+", -1, 47999, 1);
        addHoverButton(48017, -1, 150, 35, "<img=197> Any friends", -1, 47999, 1);
        addHoverButton(47261, 1143, 150, 35, "<img=204> Only me", -1, 47262, 1);
        addHoveredButton(47262, 1144, 150, 35, 47263);
        addHoverButton(48021, -1, 150, 35, "<img=203> General+", -1, 47999, 1);
        addHoverButton(48022, -1, 150, 35, "<img=202> Captain+", -1, 47999, 1);
        addHoverButton(48023, -1, 150, 35, "<img=201> Lieutenant+", -1, 47999, 1);
        addHoverButton(48024, -1, 150, 35, "<img=200> Sergeant+", -1, 47999, 1);
        addHoverButton(48025, -1, 150, 35, "<img=199> Corporal+", -1, 47999, 1);
        addHoverButton(48026, -1, 150, 35, "<img=198> Recruit+", -1, 47999, 1);
        closeButton(47267, 24, 25, false);
        addText(47800, "Clan name:", font, 0, 0xff981f, false, true);
        addText(47801, "Who can enter chat?", font, 0, 0xff981f, false, true);
        addText(47812, "Who can talk on chat?", font, 0, 0xff981f, false, true);
        addText(47813, "Who can manage chat?", font, 0, 0xff981f, false, true);
        addText(47814, "Patrick", font, 0, 0xffffff, true, true);
        addText(47815, "Anyone", font, 0, 0xffffff, true, true);
        addText(47816, "Anyone", font, 0, 0xffffff, true, true);
        addText(47817, "Only me", font, 0, 0xffffff, true, true);
        addText(47818, "Clan Mangement", font, 2, 0xff981f, false, true);
        addText(47819, "Promote:", font, 0, 0xFF981F, true, true);
        addText(47820, "", font, 0, 0xff981f, true, true);
        addConfigButton(47821, 40172, 1145, 1146, "Toggle lootshare", 267, 0, OPTION_OK);
        addText(47822, "Enable lootshare", font, 1, 0xff981f, false, true);
        addConfigButton(47823, 40172, 1145, 1146, "Toggle clan lock", 268, 0, OPTION_OK);
        addText(47824, "Lock clan", font, 1, 0xff981f, false, true);
        addInputField(47828, 12, 0xFF981F, "Kick", 117, 25, false, false);
        addText(47829, "Kick :", font, 0, 0xff981f, true, true);
        addInputField(47830, 12,0xFF981F, "Promote", 100, 20, false, false);
        addHoverButton(47834, 1147, 118, 20, "<img=203> General", -1, 47832, 1);
        addHoverButton(47835, -1, 118, 20, "<img=202> Captain", -1, 47832, 1);
        addHoverButton(47836, -1, 118, 20, "<img=201> Lieutenant", -1, 47832, 1);
        addHoverButton(47837, -1, 118, 20, "<img=200> Sergeant", -1, 47832, 1);
        addHoverButton(47838, -1, 118, 20, "<img=199> Corporal", -1, 47832, 1);
        addHoverButton(47839, -1, 118, 20, "<img=198> Recruit", -1, 47832, 1);
        addHoverButton(47840, -1, 118, 20, "Member", -1, 47832, 1);
        addText(47841, "Captain", font, 0, 0xff981f, true, true);
        addText(47842, "Slogan:", font, 0, 0xff981f, true, true);
        addInputField(47843, 30,0xFF981F, "Slogan", 223, 20, false, false);
        addText(47844, "Member Limit:", font, 0, 0xFF981F, true, true);
        addInputField(47845, 3,0xFF981F, "Limit", 60, 25, false, false);
        widget.totalChildren(63);
        widget.child(0, 47251, 15, 15);
        widget.child(1, 47252, 25, 47);
        widget.child(2, 47253, 25, 47);
        widget.child(3, 47267, 476, 23);
        widget.child(4, 48000, 25, 87);
        widget.child(5, 48001, 25, 87);
        widget.child(6, 48002, 25, 87);
        widget.child(7, 48003, 25, 87);
        widget.child(8, 48004, 25, 87);
        widget.child(9, 48005, 25, 87);
        widget.child(10, 48006, 25, 87);
        widget.child(11, 48007, 25, 87);
        widget.child(12, 47255, 25, 87);
        widget.child(13, 47256, 25, 87);
        widget.child(14, 48010, 25, 128);
        widget.child(15, 48011, 25, 128);
        widget.child(16, 48012, 25, 128);
        widget.child(17, 48013, 25, 128);
        widget.child(18, 48014, 25, 128);
        widget.child(19, 48015, 25, 128);
        widget.child(20, 48016, 25, 128);
        widget.child(21, 48017, 25, 128);
        widget.child(22, 47258, 25, 128);
        widget.child(23, 47259, 25, 128);
        widget.child(24, 48021, 25, 168);
        widget.child(25, 48022, 25, 168);
        widget.child(26, 48023, 25, 168);
        widget.child(27, 48024, 25, 168);
        widget.child(28, 48025, 25, 168);
        widget.child(29, 48026, 25, 168);
        widget.child(30, 47261, 25, 168);
        widget.child(31, 47262, 25, 168);
        widget.child(32, 47800, 73, 50);
        widget.child(33, 47801, 53, 91);
        widget.child(34, 47812, 53, 132);
        widget.child(35, 47813, 53, 173);
        widget.child(36, 47814, 100, 54 + 10);
        widget.child(37, 47815, 100, 95 + 10);
        widget.child(38, 47816, 100, 136 + 10);
        widget.child(39, 47817, 100, 177 + 10);
        widget.child(40, 44000, 0, 119);
        widget.child(41, 47818, 210, 22);
        widget.child(42, 47819, 225, 53);
        widget.child(43, 47820, 380, 53);
        widget.child(44, 47821, 30, 210);
        widget.child(45, 47822, 50, 210);
        widget.child(46, 47823, 30, 230);
        widget.child(47, 47824, 50, 230);
        widget.child(48, 47828, 60, 250);
        widget.child(49, 47829, 40, 258);
        widget.child(50, 47830, 255, 48);
        widget.child(51, 47834, 360, 48);
        widget.child(52, 47835, 360, 48);
        widget.child(53, 47836, 360, 48);
        widget.child(54, 47837, 360, 48);
        widget.child(55, 47838, 360, 48);
        widget.child(56, 47839, 360, 48);
        widget.child(57, 47840, 360, 48);
        widget.child(58, 47841, 415, 53);
        widget.child(59, 47842, 225, 72);
        widget.child(60, 47843, 255, 72);
        widget.child(61, 47844, 65, 286);
        widget.child(62, 47845, 105, 280);
        widget = addTabInterface(44000);
        widget.width = 474;
        widget.height = 190;
        widget.scrollMax = 855;
        for (int i = 44001; i <= 44050; i++) {
            addText(i, "", font, 2, 0xffff64, false, true);
        }
        for (int i = 44801; i <= 44850; i++) {
            addHoverText(i, "", "", font, 2, 0xffffff, false, false, 150);
        }
        widget.totalChildren(100);
        int Child = 0;
        int Y = 3;
        for (int i = 44001; i <= 44050; i++) {
            widget.child(Child, i, 204, Y);
            Child++;
            Y += 17;
        }
        Y = 3;
        for (int i = 44801; i <= 44850; i++) {
            widget.child(Child, i, 343, Y);
            Child++;
            Y += 17;
        }
    }

    public static void clanChat(AdvancedFont[] font) {
        Widget widget = addInterface(33800);
        addText(33801, "Clan Chat", font, 2, 0xDE8B0D, true, true);
        addText(33802, "Talking in: None", font, 0, 0xFE9900, false, true);
        addText(33803, "Owner: None", font, 0, 0xFE9900, false, true);
        addText(33816, "Slogan: None", font, 0, 0xFE9900, false, true);
        addSprite(33804, 196);

        addHoverButton(33806, 194, 72, 32, "Join Chat", -1, 33807, 1);
        addHoveredButton(33807, 195, 72, 32, 33808);
        addHoverButton(33809, 194, 72, 32, "Clan Setup", -1, 33810, 1);
        addHoveredButton(33810, 195, 72, 32, 33811);

        addText(33812, "Join Chat", font, 0, 0xff9b00, true, true);
        addText(33813, "Clan Setup", font, 0, 0xff9b00, true, true);

        addText(33815, "0/100", font, 0, 0xFE9900, false, true);
        widget.totalChildren(13);
        widget.child(0, 33801, 97, 2);
        widget.child(1, 33802, 7, 22);
        widget.child(2, 33803, 7, 37);
        widget.child(3, 33804, 0, 65);
        widget.child(4, 33806, 15, 226);
        widget.child(5, 33807, 15, 226);
        widget.child(6, 33809, 103, 226);
        widget.child(7, 33810, 103, 226);
        widget.child(8, 33812, 51, 237);
        widget.child(9, 33813, 139, 237);
        widget.child(10, 33815, 155, 37);
        widget.child(11, 33820, 1, 67);
        widget.child(12, 33816, 7, 52);
        Widget scrollInterface = addTabInterface(33820);
        scrollInterface.scrollPosition = 0;
        scrollInterface.contentType = 0;
        scrollInterface.width = 174;
        scrollInterface.height = 154;
        scrollInterface.scrollMax = 1490;
        int x = 7, y = 9;
        scrollInterface.totalChildren(100);
        for (int i = 0; i < 100; i++) {
            addHoverText(33821 + i, "", "", font, 0, 0xFFFFFF, false, true, 168);
            scrollInterface.child(i, 33821 + i, x, y);
            y += 15;
        }
    }

    public static void unpack(AdvancedFont[] font) {
        clanChat(font);
        editClan(font);
    }

}
