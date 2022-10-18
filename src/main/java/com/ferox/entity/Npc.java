package com.ferox.entity;

import com.ferox.Client;
import com.ferox.cache.anim.Animation;
import com.ferox.cache.anim.Sequence;
import com.ferox.cache.anim.SpotAnimation;
import com.ferox.cache.def.NpcDefinition;
import com.ferox.entity.model.Model;

public final class Npc extends Entity {

    public NpcDefinition desc;
    public int headIcon = -1;
    public int ownerIndex = -1;

    public boolean showActions() {
        if (ownerIndex == -1) {
            return true;
        }
        return (Client.singleton.localPlayerIndex == ownerIndex);
    }

    public int getHeadIcon() {
        if (headIcon == -1) {
            if (desc != null) {
                return desc.headIcon;
            }
        }
        return headIcon;
    }

    private Model get_animated_model() {
        int current_frame = -1;
        int animation = -1;
        if(super.animation >= 0 && super.animation_delay == 0) {
            Sequence seq = Sequence.cache[super.animation];
            current_frame = seq.primary_frame[super.current_animation_frame];
            if (super.queued_animation_id >= 0 && super.queued_animation_id != super.idle_animation_id)
                animation = Sequence.cache[super.queued_animation_id].primary_frame[super.queued_animation_frame];

            return desc.get_animated_model(animation, current_frame, Sequence.cache[super.animation].flow_control);
        } else if(super.queued_animation_id >= 0) {
            Sequence seq = Sequence.cache[super.queued_animation_id];
            current_frame = seq.primary_frame[super.queued_animation_frame];
        }
        return desc.get_animated_model(animation, current_frame, null);
    }

    public Model get_rotated_model() {
        if (desc == null)
            return null;

        Model animated = get_animated_model();
        if (animated == null)
            return null;

        super.height = animated.model_height;
        if (super.graphic_id != -1 && super.current_animation_id != -1) {
            SpotAnimation anim = SpotAnimation.cache[super.graphic_id];
            Model model = anim.get_model();
            if (model != null) {
                int frame = anim.seq.primary_frame[super.current_animation_id];
                Model graphic = new Model(true, Animation.validate(frame), false, model);
                graphic.translate(0, -super.graphic_height, 0);
                graphic.skin();
                graphic.interpolate(frame);
                graphic.face_skin = null;
                graphic.vertex_skin = null;
                if (anim.model_scale_x != 128 || anim.model_scale_y != 128)
                    graphic.scale(anim.model_scale_x, anim.model_scale_x, anim.model_scale_y);
                graphic.light(64 + anim.ambient, 850 + anim.contrast, -30, -50, -30, true);
                Model[] build = {
                    animated, graphic
                };
                animated = new Model(build);
            }
        }
        if (desc.occupied_tiles == 1)
            animated.within_tile = true;

        return animated;
    }

    public boolean visible() {
        return desc != null;
    }
}
