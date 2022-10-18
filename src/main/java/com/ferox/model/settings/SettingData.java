package com.ferox.model.settings;

import com.ferox.Client;

public enum SettingData implements SettingsAction<Client> {

    STATUS_BARS("Status bars") {
        @Override
        public String name(Client client) {
            return client.setting.status_bars + setting;
        }

        public boolean status() {
            return Client.singleton.setting.status_bars;
        }

        @Override
        public void handle(Client client) {
            client.setting.status_bars = !client.setting.status_bars;
        }
    },

    DRAW_FPS("Draw FPS") {
        @Override
        public String name(Client client) {
            return client.setting.draw_fps + setting;
        }

        public boolean status() {
            return Client.singleton.setting.draw_fps;
        }

        @Override
        public void handle(Client client) {
            client.setting.draw_fps = !client.setting.draw_fps;
        }
    },

    DRAW_SPEC_BUTTON("Special attack button") {
        @Override
        public String name(Client client) {
            return client.setting.draw_special_orb + setting;
        }

        public boolean status() {
            return Client.singleton.setting.draw_special_orb;
        }

        @Override
        public void handle(Client client) {
            client.setting.draw_special_orb = !client.setting.draw_special_orb;
        }
    },

    ORBS_ARC_SETTING("Draw hp and prayer arc") {
        @Override
        public String name(Client client) {
            return client.setting.draw_orb_arc + setting;
        }

        public boolean status() {
            return Client.singleton.setting.draw_orb_arc;
        }

        @Override
        public void handle(Client client) {
            client.setting.draw_orb_arc = !client.setting.draw_orb_arc;
        }
    },

    ENTITY_FEED_SETTING("Health overlay") {
        @Override
        public String name(Client client) {
            return client.setting.draw_health_overlay + setting;
        }

        public boolean status() {
            return Client.singleton.setting.draw_health_overlay;
        }

        @Override
        public void handle(Client client) {
            client.setting.draw_health_overlay = !client.setting.draw_health_overlay;
        }
    },

    WIDGETS_SETTING("Timers") {
        @Override
        public String name(Client client) {
            return client.setting.draw_timers + setting;
        }

        public boolean status() {
            return Client.singleton.setting.draw_timers;
        }

        @Override
        public void handle(Client client) {
            client.setting.draw_timers = !client.setting.draw_timers;
        }
    },

    DISPLAY_PLAYER_OVERHEAD_NAMES_SETTING("Player overhead names") {
        @Override
        public String name(Client client) {
            return client.setting.toggle_overhead_names + setting;
        }

        public boolean status() {
            return Client.singleton.setting.toggle_overhead_names;
        }

        @Override
        public void handle(Client client) {
            client.setting.toggle_overhead_names = !client.setting.toggle_overhead_names;
        }
    },

    DISPLAY_PLAYER_OVERHEAD_HP_SETTING("Player overhead hitpoints") {
        @Override
        public String name(Client client) {
            return client.setting.toggle_overhead_hp + setting;
        }

        public boolean status() {
            return Client.singleton.setting.toggle_overhead_hp;
        }

        @Override
        public void handle(Client client) {
            client.setting.toggle_overhead_hp = !client.setting.toggle_overhead_hp;
        }
    },

    DISPLAY_NPC_OVERHEAD_NAMES_SETTING("Npc overhead names") {
        @Override
        public String name(Client client) {
            return client.setting.toggle_npc_overhead_names + setting;
        }

        public boolean status() {
            return Client.singleton.setting.toggle_npc_overhead_names;
        }

        @Override
        public void handle(Client client) {
            client.setting.toggle_npc_overhead_names = !client.setting.toggle_npc_overhead_names;
        }
    },

    DISPLAY_NPC_OVERHEAD_HP_SETTING("Npc overhead hitpoints") {
        @Override
        public String name(Client client) {
            return client.setting.toggle_npc_overhead_hp + setting;
        }

        public boolean status() {
            return Client.singleton.setting.toggle_npc_overhead_hp;
        }

        @Override
        public void handle(Client client) {
            client.setting.toggle_npc_overhead_hp = !client.setting.toggle_npc_overhead_hp;
        }
    },

    DISPLAY_GROUND_ITEMS_SETTING("Display Ground Items") {
        @Override
        public String name(Client client) {
            return client.setting.toggle_item_pile_names + setting;
        }

        public boolean status() {
            return Client.singleton.setting.toggle_item_pile_names;
        }

        @Override
        public void handle(Client client) {
            client.setting.toggle_item_pile_names = !client.setting.toggle_item_pile_names;
        }
    },

    FILTER_GROUND_ITEMS_SETTING("Filter Ground Items") {
        @Override
        public String name(Client client) {
            return client.setting.filter_item_pile_names + setting;
        }

        public boolean status() {
            return Client.singleton.setting.filter_item_pile_names;
        }

        @Override
        public void handle(Client client) {
            client.setting.filter_item_pile_names = !client.setting.filter_item_pile_names;
        }
    },

    MOVE_PRAYERS_SETTING("Moveable prayers") {
        @Override
        public String name(Client client) {
            return client.setting.moving_prayers + setting;
        }

        public boolean status() {
            return Client.singleton.setting.moving_prayers;
        }

        @Override
        public void handle(Client client) {
            client.setting.moving_prayers = !client.setting.moving_prayers;
        }
    },

    DRAW_GROUND_SNOW("Draw Ground Snow") {
        @Override
        public String name(Client client) {
            return client.setting.ground_snow + setting;
        }

        public boolean status() {
            return Client.singleton.setting.ground_snow;
        }

        @Override
        public void handle(Client client) {
            client.setting.ground_snow = !client.setting.ground_snow;
            Client.toggleSnow();
        }
    },
    ;

    /**
     * The setting name.
     */
    public final String setting;

    /**
     * Constructs a new <code>SettingData</code>.
     */
    SettingData(String setting) {
        this.setting = setting;
    }

    /**
     * Gets the setting data based off the ordinal of the setting.
     */
    public static SettingData forOrdinal(int index) {
        for (SettingData data : values()) {
            if (data.ordinal() == index) {
                return data;
            }
        }
        return null;
    }
}
