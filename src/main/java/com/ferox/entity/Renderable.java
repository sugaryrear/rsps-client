package com.ferox.entity;
import com.ferox.collection.Cacheable;
import com.ferox.entity.model.Model;
import com.ferox.entity.model.Vertex;

public class Renderable extends Cacheable {

    public int model_height;
    public Vertex normals[];

    public void setSceneId(int sceneId) {
        this.sceneId = sceneId;
    }

    public int sceneId = (int) (System.currentTimeMillis() / 1000L);

    public void render_3D(int orientation, int cos_y, int sin_y, int sin_x, int cos_x, int start_x, int start_y, int depth, long uid) {
        Model model = get_rotated_model();
        if(model != null) {
            model_height = model.model_height;
            model.render_3D(orientation, cos_y, sin_y, sin_x, cos_x, start_x, start_y, depth, uid);
            sceneId++;
        }
    }


    public Model get_rotated_model() {
        return null;
    }

    public Vertex getNormal(int index) {
        return normals[index];
    }

    public Vertex[] getNormals() {
        return normals;
    }


    public Renderable() {
        model_height = 1000;
    }


}
