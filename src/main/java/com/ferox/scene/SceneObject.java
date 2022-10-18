package com.ferox.scene;

import com.ferox.Client;
import com.ferox.cache.anim.Sequence;
import com.ferox.cache.config.VariableBits;
import com.ferox.cache.def.ObjectDefinition;
import com.ferox.entity.Renderable;
import com.ferox.entity.model.Model;

public final class SceneObject extends Renderable {

    private int animation_frame;
    private final int[] configs;
    private final int varbit_id;
    private final int config_id;
    private final int cos_y;
    private final int sin_y;
    private final int cos_x;
    private final int sin_x;
    private Sequence seq;
    private int cycle_delay;
    private final int object_id;
    private final int click_type;
    private final int orientation;

    private ObjectDefinition get_configs() {
        int index = -1;
        if (varbit_id != -1) {
            try {
                VariableBits varBit = VariableBits.cache[varbit_id];
                int setting = varBit.configId;
                int low_varbit = varBit.leastSignificantBit;
                int high_varbit = varBit.mostSignificantBit;
                int bit_mask = Client.BIT_MASKS[high_varbit - low_varbit];
                index = Client.singleton.settings[setting] >> low_varbit & bit_mask;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (config_id != -1 && config_id < Client.singleton.settings.length) {
            index = Client.singleton.settings[config_id];
        }
        int var;
        if (index >= 0 && index < configs.length) {
            var = configs[index];
        } else
            var = configs[configs.length - 1];

        return var != -1 ? ObjectDefinition.get(var) : null;
    }

    public Model get_rotated_model() {
        int animation_id = -1;
        if (seq != null) {
            int step = Client.game_tick - cycle_delay;
            if (step > 100 && seq.step > 0) {
                step = 100;
            }
            while (step > seq.get_length(animation_frame)) {
                step -= seq.get_length(animation_frame);
                animation_frame++;
                if (animation_frame < seq.frames)
                    continue;

                animation_frame -= seq.step;
                if (animation_frame >= 0 && animation_frame < seq.frames)
                    continue;

                seq = null;
                break;
            }
            cycle_delay = Client.game_tick - step;
            if (seq != null) {
                animation_id = seq.primary_frame[animation_frame];
            }
        }
        ObjectDefinition def;
        if (configs != null)
            def = get_configs();
        else
            def = ObjectDefinition.get(object_id);

        if (def == null) {
            return null;
        } else {
            return def.get_object(click_type, orientation, cos_y, sin_y, cos_x, sin_x, animation_id);
        }
    }

    public SceneObject(int id, int orientation, int click_type, int sin_y, int cos_x, int cos_y, int sin_x, int animation_id, boolean flag) {
        object_id = id;
        this.click_type = click_type;
        this.orientation = orientation;
        this.cos_y = cos_y;
        this.sin_y = sin_y;
        this.cos_x = cos_x;
        this.sin_x = sin_x;
        if (animation_id != -1) {
            seq = Sequence.cache[animation_id];
            animation_frame = 0;
            cycle_delay = Client.game_tick;
            if (flag && seq.step != -1) {
                animation_frame = (int) (Math.random() * (double) seq.frames);
                cycle_delay -= (int) (Math.random() * (double) seq.get_length(animation_frame));
            }
        }
        ObjectDefinition def = ObjectDefinition.get(object_id);
        this.varbit_id = def.varbit_id;
        this.config_id = def.varp_id;
        this.configs = def.configs;
    }
}
