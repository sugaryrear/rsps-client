package com.ferox.cache.graphics.widget.impl;

import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.Widget;

public class DuelArenaWidget extends Widget {

    public static void unpack(AdvancedFont[] font) {
        move_duel_equipment();
        move_accept_decline_button_lines();
    }
    private static void move_accept_decline_button_lines() {
        Widget main_widget = cache[6575];
        //Print all the child IDs of the main interface.
        //Note that the accept and decline buttons are each an interface/component and have their own child IDs
        //as they are components/interfaces of their own.
        for (int child : main_widget.children) {
            //System.out.println("Child ID " + child);
        }
        //Thanks to displee's interface editor,
        //we know the components 6667 and 6668 have the subcomponent/childIDs that we want to modify,
        //and we can count that it is the 3rd element aka 2nd index (arrays start at 0 in java)
        //so we know which element of the array to modify.
        //Move accept and decline button "top line" sprites down slightly
        Widget accept_button = cache[6667];
        Widget decline_button = cache[6668];
        accept_button.child_y[2] -= 1;
        decline_button.child_y[2] -= 1;

        //Same fix as above, fix the victorious claim button positioning.
        //component 6824 subcomponent 6828 (element 3), interface 6733
        Widget claim_button = cache[6824];
        claim_button.child_y[2] -= 1;

        //Let's set the hover text colour to white.
        Widget load_previous_text = cache[24492];
        load_previous_text.defaultHoverColor = 0xFFFFFF;

        //Center the button, except there is text in the way.
        //main_widget.childX[201] -= 85;
        //main_widget.childY[201] += 270;
    }

    private static void move_duel_equipment() {
        Widget main_widget = cache[6575];

        //Move equipment child up
        main_widget.child_x[178] = 190;
        main_widget.child_y[178] = 92;
    }

    //6576, 6577, 6578, 6579, 6580, 6581, 6582, 6583, 6584, 6585, 6586, 6587, 6588, 6589, 6590, 6591, 6592, 6593, 6594, 6595, 6596, 6597, 6598, 6599, 6600, 6601, 6602, 6603, 6604, 6605, 6606, 6607, 6608, 6609, 6610, 6611, 6612, 6613, 6614, 6615, 6616, 6617, 6618, 6619, 6620, 6621, 6622, 6623, 6624, 6625, 6626, 6627, 6628, 6629, 6630, 6631, 6632, 6633, 6634, 6635, 6636, 6637, 6638, 6639, 6640, 6641, 6642, 6643, 6644, 6645, 6646, 6647, 6648, 6649, 6650, 6651, 6652, 6653, 6654, 6655, 6656, 6657, 6658, 6659, 6660, 6661, 6662, 6663, 13784, 13785, 13786, 13787, 13788, 13789, 6665, 6666, 6667, 6668, 6671, 13790, 8257, 6685, 6687, 6684, 13796, 6696, 6697, 6698, 6699, 8258, 6701, 6702, 6703, 6704, 6710, 6711, 6712, 6713, 6714, 6715, 6716, 6717, 6718, 13797, 13798, 6721, 6722, 13799, 13800, 6725, 6726, 6727, 6728, 6729, 6730, 6731, 6732, 669, 670, 7816, 7817, 8259, 8261, 8263, 8265, 8267, 8269, 8271, 8273, 8275, 8277, 8279, 8281, 8283, 8285, 13801, 13802, 13803, 13804, 13805, 13806, 13807, 13808, 13809, 13810, 13811, 13812, 13813, 13814, 13815, 13816, 13817, 13818, 13819, 13820, 13821, 13822, 13823, 13824, 13825, 13826, 13827, 13828, 13829, 13830, 13831, 13832, 13833, 13834, 13835, 13836, 13838, 13840, 13842, 13844, 13846, 13848, 13850, 13852, 13854, 13856
}
