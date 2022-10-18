package com.ferox.scene;

import com.ferox.cache.anim.Animation;
import com.ferox.cache.anim.SpotAnimation;
import com.ferox.entity.Renderable;
import com.ferox.entity.model.Model;

public final class StaticObject extends Renderable {

    public final int z;
    public final int x;
    public final int y;
    public final int height;
    public final int cycle;
    public boolean expired;
    private final SpotAnimation graphics;
    private int flow;
    private int duration;

    public StaticObject(int z, int cycle, int offset, int id, int height, int y, int x) {
        expired = false;
        graphics = SpotAnimation.cache[id];
        this.z = z;
        this.x = x;
        this.y = y;
        this.height = height;
        this.cycle = cycle + offset;
        expired = false;
    }

    public Model get_rotated_model() {
        Model model = graphics.get_model();
        if(model == null) {
            return null;
        }
        int frame = graphics.seq.primary_frame[flow];
        Model animated_model = new Model(true, Animation.validate(frame), false, model);
        if(!expired) {
            animated_model.skin();
            animated_model.interpolate(frame);
            animated_model.face_skin = null;
            animated_model.vertex_skin = null;
        }
        if(graphics.model_scale_x != 128 || graphics.model_scale_y != 128) {
            animated_model.scale(graphics.model_scale_x, graphics.model_scale_x, graphics.model_scale_y);
        }
        if(graphics.rotation != 0) {
            if(graphics.rotation == 90) {
                animated_model.rotate_90();
            }
            if(graphics.rotation == 180) {
                animated_model.rotate_90();
                animated_model.rotate_90();
            }
            if(graphics.rotation == 270) {
                animated_model.rotate_90();
                animated_model.rotate_90();
                animated_model.rotate_90();
            }
        }
        animated_model.light(64 + graphics.ambient, 850 + graphics.contrast, -30, -50, -30, true);
        return animated_model;
    }

    public void step(int length) {
        for(duration += length; duration > graphics.seq.get_length(flow);) {
            duration -= graphics.seq.get_length(flow) + 1;
            flow++;
            if(flow >= graphics.seq.frames && (flow < 0 || flow >= graphics.seq.frames)) {
                flow = 0;
                expired = true;
            }
        }
    }
}
