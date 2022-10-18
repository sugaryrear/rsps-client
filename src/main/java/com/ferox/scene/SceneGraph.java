package com.ferox.scene;

import com.ferox.ClientConstants;
import com.ferox.collection.LinkedList;
import com.ferox.draw.Rasterizer2D;
import com.ferox.draw.Rasterizer3D;
import com.ferox.entity.InteractiveObject;
import com.ferox.entity.GroundItem;
import com.ferox.entity.Renderable;
import com.ferox.entity.model.Model;
import com.ferox.entity.model.Vertex;
import com.ferox.scene.object.GroundDecoration;
import com.ferox.scene.object.Wall;
import com.ferox.scene.object.WallDecoration;
import com.ferox.scene.object.tile.ComplexTile;
import com.ferox.scene.object.tile.SimpleTile;
import com.ferox.scene.object.tile.Tile;

public final class SceneGraph {

    public SceneGraph(int[][][] map) {
        int y = 104;// was parameter
        int x = 104;// was parameter
        int z = 4;// was parameter
        //aBoolean434 = true;
        interactive_obj_cache = new InteractiveObject[5000];
        merge_a_normals = new int[10000];
        merge_b_normals = new int[10000];
        map_size_z = z;
        map_size_x = x;
        map_size_y = y;
        tile_array = new Tile[z][x][y];
        anIntArrayArrayArray445 = new int[z][x + 1][y + 1];//scene_viewport_angle?
        height_map = map;
        rebuild();
    }

    public static void release() {
        interactive_obj = null;
        culling_cluster_ptr = null;
        culling_cluster = null;
        tile_list = null;
        TILE_VISIBILITY_MAPS = null;
        TILE_VISIBILITY_MAP = null;
    }

    public void rebuild() {
        for (int z = 0; z < map_size_z; z++) {
            for (int x = 0; x < map_size_x; x++) {
                for (int y = 0; y < map_size_y; y++)
                    tile_array[z][x][y] = null;
            }
        }
        for (int index = 0; index < culling_index; index++) {
            for (int face = 0; face < culling_cluster_ptr[index]; face++)
                culling_cluster[index][face] = null;

            culling_cluster_ptr[index] = 0;
        }

        for (int index = 0; index < interactive_obj_cache_current_pos; index++)
            interactive_obj_cache[index] = null;

        interactive_obj_cache_current_pos = 0;
        for (int index = 0; index < interactive_obj.length; index++)
            interactive_obj[index] = null;

    }

    public void set_height(int plane) {
        current_pos_z = plane;
        for (int x = 0; x < map_size_x; x++) {
            for (int y = 0; y < map_size_y; y++)
                if (tile_array[plane][x][y] == null)
                    tile_array[plane][x][y] = new Tile(plane, x, y);

        }
    }

    public void create_bridge(int x, int y) {
        Tile sub = tile_array[0][y][x];
        for (int plane = 0; plane < 3; plane++) {
            Tile tile = tile_array[plane][y][x] = tile_array[plane + 1][y][x];
            if (tile != null) {
                tile.z--;
                for (int index = 0; index < tile.occupants; index++) {
                    InteractiveObject entity = tile.interactive_obj[index];
                    if ((entity.uid >> 29 & 0x3L) == 2L && entity.left == y && entity.top == x)
                        entity.plane--;

                }
            }
        }
        if (tile_array[0][y][x] == null)
            tile_array[0][y][x] = new Tile(0, y, x);

        tile_array[0][y][x].sub_tile = sub;
        tile_array[3][y][x] = null;
    }

    public static void create_culling_occlusion_box(int cull_index, int x, int depth, int width, int height, int z, int y, int mask) {
        SceneCluster cluster = new SceneCluster();
        cluster.tile_x = x / 128;
        cluster.tile_width = width / 128;
        cluster.tile_y = y / 128;
        cluster.tile_height = height / 128;
        cluster.search_mask = mask;
        cluster.world_x = x;
        cluster.world_width = width;
        cluster.world_y = y;
        cluster.world_height = height;
        cluster.world_z = z;
        cluster.world_depth = depth;
        culling_cluster[cull_index][culling_cluster_ptr[cull_index]++] = cluster;
    }

    public void set_visible_planes(int plane, int x, int z, int height) {
        Tile tile = tile_array[plane][x][z];
        if (tile != null) {
            tile_array[plane][x][z].logic_height = height;
        }
    }

    public void add_tile(int plane, int x, int y, int shape, int rotation, int texture_id, int v_sw, int v_se, int v_ne, int v_nw, int hsl_sw, int hsl_se,
                         int hsl_ne, int hsl_nw, int l_sw, int l_se, int l_ne, int l_nw, int color_id, int minimap_color) {
        if (shape == 0) {
            SimpleTile simple = new SimpleTile(hsl_sw, hsl_se, hsl_ne, hsl_nw, -1, color_id, false);
            for (int z = plane; z >= 0; z--)
                if (tile_array[z][x][y] == null)
                    tile_array[z][x][y] = new Tile(z, x, y);

            tile_array[plane][x][y].simple = simple;
            return;
        }
        if (shape == 1) {
            SimpleTile simple = new SimpleTile(l_sw, l_se, l_ne, l_nw, texture_id, minimap_color, v_sw == v_se && v_sw == v_ne && v_sw == v_nw);
            for (int z = plane; z >= 0; z--)
                if (tile_array[z][x][y] == null)
                    tile_array[z][x][y] = new Tile(z, x, y);

            tile_array[plane][x][y].simple = simple;
            return;
        }
        ComplexTile complex = new ComplexTile(y, l_sw, hsl_nw, v_ne, texture_id, l_ne, rotation, hsl_sw, color_id, hsl_ne, v_nw, v_se, v_sw, shape, l_nw, l_se, hsl_se, x, minimap_color);
        for (int z = plane; z >= 0; z--)
            if (tile_array[z][x][y] == null)
                tile_array[z][x][y] = new Tile(z, x, y);

        tile_array[plane][x][y].complex = complex;
    }

    public void add_ground_decor(int plane, int z, int y, Renderable ground, long uid, int x) {
        if (ground == null)
            return;

        GroundDecoration decor = new GroundDecoration();
        decor.node = ground;
        decor.world_x = x * 128 + 64;
        decor.world_y = y * 128 + 64;
        decor.world_z = z;
        decor.uid = uid;
        //class49.aByte816 = byte0;//remnant
        if (tile_array[plane][x][y] == null)
            tile_array[plane][x][y] = new Tile(plane, x, y);

        tile_array[plane][x][y].ground_decor = decor;
    }

    public void add_ground_item(int x, long uid, Renderable first, int plane, Renderable second, Renderable third, int z, int y) {
        GroundItem item = new GroundItem();
        item.third = third;
        item.world_x = x * 128 + 64;
        item.world_y = y * 128 + 64;
        item.world_z = plane;
        item.uid = uid;
        item.first = first;
        item.second = second;
        int offset = 0;
        Tile tile = tile_array[z][x][y];
        if (tile != null) {//using an item on a table, or something off the ground
            for (int index = 0; index < tile.occupants; index++)
                if ((tile.interactive_obj[index].uid & 0x400000L) == 4194304L && tile.interactive_obj[index].node instanceof Model) {//class39
                    int interacted_obj_height_offset = ((Model) tile.interactive_obj[index].node).obj_height;
                    if (interacted_obj_height_offset > offset)
                        offset = interacted_obj_height_offset;
                }

        }
        item.height_offset = offset;
        if (tile_array[z][x][y] == null)
            tile_array[z][x][y] = new Tile(z, x, y);

        tile_array[z][x][y].ground_item = item;
    }

    public void add_wall(int wall_orientation, Renderable side, long uid, int y, int x, Renderable corner, int plane, int corner_orientation, int z) {
        if (side == null && corner == null)
            return;

        Wall wall = new Wall();
        wall.uid = uid;
        wall.world_x = x * 128 + 64;
        wall.world_y = y * 128 + 64;
        wall.plane = plane;
        wall.wall = side;
        wall.corner = corner;
        wall.wall_orientation = wall_orientation;
        wall.corner_orientation = corner_orientation;
        for (int tile_z = z; tile_z >= 0; tile_z--)
            if (tile_array[tile_z][x][y] == null)
                tile_array[tile_z][x][y] = new Tile(tile_z, x, y);

        tile_array[z][x][y].wall = wall;
    }

    public void add_wall_decor(long uid, int y, int orientation, int z, int x_offset, int plane, Renderable object, int x, int y_offset, int config) {
        if (object == null)
            return;

        WallDecoration decor = new WallDecoration();
        decor.uid = uid;
        decor.world_x = x * 128 + 64 + x_offset;
        decor.world_y = y * 128 + 64 + y_offset;
        decor.plane = plane;
        decor.node = object;
        decor.config_mask = config;
        decor.orientation = orientation;
        for (int tile_z = z; tile_z >= 0; tile_z--)
            if (tile_array[tile_z][x][y] == null)
                tile_array[tile_z][x][y] = new Tile(tile_z, x, y);

        tile_array[z][x][y].wall_decor = decor;
    }

    public boolean add_entity(long uid, int world_z, int height_offset, Renderable entity, int width_offset, int plane, int orientation, int y, int x) {
        if (entity == null) {
            return true;
        } else {
            int world_x = x * 128 + 64 * width_offset;
            int world_y = y * 128 + 64 * height_offset;
            return add_entity(plane, x, y, width_offset, height_offset, world_x, world_y, world_z, entity, orientation, false, uid);
        }
    }

    public boolean add_entity(int plane, int yaw, int z, long uid, int y, int size, int x, Renderable entity, boolean rotate) {
        if (entity == null)
            return true;

        int width = x - size;
        int height = y - size;
        int tile_width = x + size;
        int tile_height = y + size;
        if (rotate) {
            if (yaw > 640 && yaw < 1408)
                tile_height += 128;

            if (yaw > 1152 && yaw < 1920)
                tile_width += 128;

            if (yaw > 1664 || yaw < 384)
                height -= 128;

            if (yaw > 128 && yaw < 896)
                width -= 128;

        }
        width /= 128;
        height /= 128;
        tile_width /= 128;
        tile_height /= 128;
        return add_entity(plane, width, height, (tile_width - width) + 1, (tile_height - height) + 1, x, y, z, entity, yaw, true, uid);
    }

    public boolean add_transformed_entity(int plane, int y, Renderable entity, int orientation, int height_offset, int x, int z, int width, int width_offset,
                                          long uid, int height) {

        return entity == null || add_entity(plane, width, height, (width_offset - width) + 1, (height_offset - height) + 1, x, y, z,
            entity, orientation, true, uid);
    }

    private boolean add_entity(int plane, int width, int height, int width_offset, int height_offset, int world_x, int world_y, int world_z, Renderable entity,
                               int orientation, boolean cached, long uid) {
        for (int x = width; x < width + width_offset; x++) {
            for (int y = height; y < height + height_offset; y++) {
                if (x < 0 || y < 0 || x >= map_size_x || y >= map_size_y)
                    return false;

                Tile tile = tile_array[plane][x][y];
                if (tile != null && tile.occupants >= 5)
                    return false;

            }
        }
        InteractiveObject object = new InteractiveObject();
        object.uid = uid;
        object.plane = plane;
        object.world_x = world_x;
        object.world_y = world_y;
        object.world_z = world_z;
        object.node = entity;
        object.orientation = orientation;
        object.left = width;
        object.top = height;
        object.right = (width + width_offset) - 1;
        object.bottom = (height + height_offset) - 1;
        for (int x = width; x < width + width_offset; x++) {
            for (int y = height; y < height + height_offset; y++) {
                int size = 0;
                if (x > width)
                    size++;

                if (x < (width + width_offset) - 1)
                    size += 4;

                if (y > height)
                    size += 8;

                if (y < (height + height_offset) - 1)
                    size += 2;

                for (int z = plane; z >= 0; z--)
                    if (tile_array[z][x][y] == null)
                        tile_array[z][x][y] = new Tile(z, x, y);

                Tile tile = tile_array[plane][x][y];
                tile.interactive_obj[tile.occupants] = object;
                tile.interactive_obj_size[tile.occupants] = size;
                tile.origin_mask |= size;
                tile.occupants++;
            }
        }
        if (cached)
            interactive_obj_cache[interactive_obj_cache_current_pos++] = object;

        return true;
    }

    public void reset_interactive_obj() {
        for (int index = 0; index < interactive_obj_cache_current_pos; index++) {
            InteractiveObject obj = interactive_obj_cache[index];
            remove_object(obj);
            interactive_obj_cache[index] = null;
        }
        interactive_obj_cache_current_pos = 0;
    }

    private void remove_object(InteractiveObject obj) {
        for (int width = obj.left; width <= obj.right; width++) {
            for (int height = obj.top; height <= obj.bottom; height++) {
                Tile tile = tile_array[obj.plane][width][height];
                if (tile != null) {
                    for (int active = 0; active < tile.occupants; active++) {
                        if (tile.interactive_obj[active] != obj)
                            continue;

                        tile.occupants--;
                        for (int index = active; index < tile.occupants; index++) {
                            tile.interactive_obj[index] = tile.interactive_obj[index + 1];
                            tile.interactive_obj_size[index] = tile.interactive_obj_size[index + 1];
                        }
                        tile.interactive_obj[tile.occupants] = null;
                        break;
                    }
                    tile.origin_mask = 0;
                    for (int index = 0; index < tile.occupants; index++)
                        tile.origin_mask |= tile.interactive_obj_size[index];

                }
            }
        }
    }

    public void offset_wall_decor(int y, int offset, int x, int plane) {
        Tile tile = tile_array[plane][x][y];
        if (tile == null)
            return;

        WallDecoration decor = tile.wall_decor;
        if (decor != null) {
            int abs_x = x * 128 + 64;
            int abs_y = y * 128 + 64;
            decor.world_x = abs_x + ((decor.world_x - abs_x) * offset) / 16;
            decor.world_y = abs_y + ((decor.world_y - abs_y) * offset) / 16;
        }
    }

    public void remove_wall(int x, int plane, int y/*, byte value*/) {
        Tile tile = tile_array[plane][x][y];
        /*if (value != -119)
            aBoolean434 = !aBoolean434;//remnant*/

        if (tile != null) {
            tile.wall = null;
        }
    }

    public void remove_wall_decor(int y, int plane, int x) {
        Tile tile = tile_array[plane][x][y];
        if (tile != null) {
            tile.wall_decor = null;
        }
    }

    public void remove_object(int plane, int x, int y) {
        Tile tile = tile_array[plane][x][y];
        if (tile == null)
            return;

        for (int index = 0; index < tile.occupants; index++) {
            InteractiveObject object = tile.interactive_obj[index];
            if ((object.uid >> 29 & 0x3L) == 2L && object.left == x && object.top == y) {
                remove_object(object);
                return;
            }
        }
    }

    public void remove_ground_decor(int plane, int y, int x) {
        Tile tile = tile_array[plane][x][y];
        if (tile == null)
            return;

        tile.ground_decor = null;
    }

    public void remove_ground_item(int plane, int x, int y) {
        Tile tile = tile_array[plane][x][y];
        if (tile != null) {
            tile.ground_item = null;
        }
    }

    public Wall get_wall(int plane, int x, int y) {
        Tile tile = tile_array[plane][x][y];
        if (tile == null)
            return null;
        else
            return tile.wall;
    }

    public WallDecoration get_wall_decor(int x, int y, int plane) {
        Tile tile = tile_array[plane][x][y];
        if (tile == null)
            return null;
        else
            return tile.wall_decor;
    }

    public InteractiveObject get_interactive_object(int x, int y, int plane) {
        Tile tile = tile_array[plane][x][y];
        if (tile == null)
            return null;

        for (int index = 0; index < tile.occupants; index++) {
            InteractiveObject object = tile.interactive_obj[index];
            if ((object.uid >> 29 & 0x3L) == 2L && object.left == x && object.top == y)
                return object;
        }
        return null;
    }

    public GroundDecoration get_ground_decor(int y, int x, int plane) {
        Tile tile = tile_array[plane][x][y];
        if (tile == null || tile.ground_decor == null)
            return null;
        else
            return tile.ground_decor;
    }

    public long get_wall_uid(int plane, int x, int y) {
        Tile tile = tile_array[plane][x][y];
        if (tile == null || tile.wall == null) {
            return 0L;
        }
        return tile.wall.uid;
    }

    public long get_wall_decor_uid(int plane, int x, int y) {
        Tile tile = tile_array[plane][x][y];
        if (tile == null || tile.wall_decor == null) {
            return 0L;
        }
        return tile.wall_decor.uid;
    }

    public long get_interactive_object_uid(int plane, int x, int y) {
        Tile tile = tile_array[plane][x][y];
        if (tile == null) {
            return 0L;
        }
        for (int index = 0; index < tile.occupants; index++) {
            InteractiveObject object = tile.interactive_obj[index];
            if ((object.uid >> 29 & 0x3L) == 2L && object.left == x && object.top == y) {
                return object.uid;
            }
        }
        return 0L;
    }

    public long get_ground_decor_uid(int plane, int x, int y) {
        Tile tile = tile_array[plane][x][y];
        if (tile == null || tile.ground_decor == null) {
            return 0L;
        }
        return tile.ground_decor.uid;
    }

    public boolean get_object(int plane, int x, int y, long uid) {//method1952 // method298
        Tile tile = tile_array[plane][x][y];
        if (tile == null) {
            return false;
        }
        if (tile.wall != null && tile.wall.uid == uid) {
            return true;
        }
        if (tile.wall_decor != null && tile.wall_decor.uid == uid) {
            return true;
        }
        if (tile.ground_decor != null && tile.ground_decor.uid == uid) {
            return true;
        }
        for (int index = 0; index < tile.occupants; index++) {
            if (tile.interactive_obj[index].uid == uid) {
                return true;
            }
        }
        return false;
    }

    public void flat_lighting(int dir_light_y, int dir_light_x, int dir_light_z) {
        int directional_light_initial_intensity = 96;
        int specular_distribution_factor = 768;
        int pre_dir_light_length = (int) Math.sqrt(dir_light_x * dir_light_x + dir_light_y * dir_light_y + dir_light_z * dir_light_z);
        int pre_specular_distribution_factor = specular_distribution_factor * pre_dir_light_length >> 8;
        for (int plane = 0; plane < map_size_z; plane++) {
            for (int x = 0; x < map_size_x; x++) {
                for (int y = 0; y < map_size_y; y++) {
                    Tile tile = tile_array[plane][x][y];
                    if (tile != null) {
                        Wall wall = tile.wall;
                        if (wall != null && wall.wall != null && wall.wall.normals != null) {
                            get_pos(plane, 1, 1, x, y, (Model) wall.wall);
                            if (wall.corner != null && wall.corner.normals != null) {
                                get_pos(plane, 1, 1, x, y, (Model) wall.corner);
                                merge_normals((Model) wall.wall, (Model) wall.corner, 0, 0, 0, false);
                                ((Model) wall.corner).doShading(directional_light_initial_intensity, pre_specular_distribution_factor, dir_light_x, dir_light_y, dir_light_z);
                            }
                            ((Model) wall.wall).doShading(directional_light_initial_intensity, pre_specular_distribution_factor, dir_light_x, dir_light_y, dir_light_z);
                        }
                        for (int index = 0; index < tile.occupants; index++) {
                            InteractiveObject object = tile.interactive_obj[index];
                            if (object != null && object.node != null && object.node.normals != null) {
                                get_pos(plane, (object.right - object.left) + 1, (object.bottom - object.top) + 1, x, y, (Model) object.node);
                                ((Model) object.node).doShading(directional_light_initial_intensity, pre_specular_distribution_factor, dir_light_x, dir_light_y, dir_light_z);
                            }
                        }
                        GroundDecoration decor = tile.ground_decor;
                        if (decor != null && decor.node.normals != null) {
                            get_pos(x, plane, (Model) decor.node, y);
                            ((Model) decor.node).doShading(directional_light_initial_intensity, pre_specular_distribution_factor, dir_light_x, dir_light_y, dir_light_z);
                        }
                    }
                }
            }
        }
    }

    private void get_pos(int x, int plane, Model model, int y) {
        if (x < map_size_x) {
            Tile tile = tile_array[plane][x + 1][y];
            if (tile != null && tile.ground_decor != null && tile.ground_decor.node.normals != null)
                merge_normals(model, (Model) tile.ground_decor.node, 128, 0, 0, true);
        }
        if (y < map_size_x) {
            Tile tile = tile_array[plane][x][y + 1];
            if (tile != null && tile.ground_decor != null && tile.ground_decor.node.normals != null)
                merge_normals(model, (Model) tile.ground_decor.node, 0, 0, 128, true);
        }
        if (x < map_size_x && y < map_size_y) {
            Tile tile = tile_array[plane][x + 1][y + 1];
            if (tile != null && tile.ground_decor != null && tile.ground_decor.node.normals != null)
                merge_normals(model, (Model) tile.ground_decor.node, 128, 0, 128, true);
        }
        if (x < map_size_x && y > 0) {
            Tile tile = tile_array[plane][x + 1][y - 1];
            if (tile != null && tile.ground_decor != null && tile.ground_decor.node.normals != null)
                merge_normals(model, (Model) tile.ground_decor.node, 128, 0, -128, true);
        }
    }

    private void get_pos(int plane, int width, int height, int map_x, int map_y, Model model) {
        boolean flag = true;
        int start_x = map_x;
        int distance_x = map_x + width;
        int start_y = map_y - 1;
        int distance_y = map_y + height;
        for (int z = plane; z <= plane + 1; z++)
            if (z != map_size_z) {
                for (int x = start_x; x <= distance_x; x++)
                    if (x >= 0 && x < map_size_x) {
                        for (int y = start_y; y <= distance_y; y++)
                            if (y >= 0 && y < map_size_y && (!flag || x >= distance_x || y >= distance_y || y < map_y && x != map_x)) {
                                Tile tile = tile_array[z][x][y];
                                if (tile != null) {
                                    int height_offset = (height_map[z][x][y]
                                        + height_map[z][x + 1][y]
                                        + height_map[z][x][y + 1]
                                        + height_map[z][x + 1][y + 1]) / 4
                                        - (height_map[plane][map_x][map_y] + height_map[plane][map_x + 1][map_y]
                                        + height_map[plane][map_x][map_y + 1]
                                        + height_map[plane][map_x + 1][map_y + 1]) / 4;
                                    Wall wall = tile.wall;
                                    if (wall != null && wall.wall != null && wall.wall.normals != null)
                                        merge_normals(model, (Model) wall.wall, (x - map_x) * 128 + (1 - width) * 64, height_offset, (y - map_y) * 128 + (1 - height) * 64, flag);

                                    if (wall != null && wall.corner != null && wall.corner.normals != null)
                                        merge_normals(model, (Model) wall.corner, (x - map_x) * 128 + (1 - width) * 64, height_offset, (y - map_y) * 128 + (1 - height) * 64, flag);

                                    for (int index = 0; index < tile.occupants; index++) {
                                        InteractiveObject object = tile.interactive_obj[index];
                                        if (object != null && object.node != null && object.node.normals != null) {
                                            int obj_width = (object.right - object.left) + 1;
                                            int obj_height = (object.bottom - object.top) + 1;
                                            merge_normals(model, (Model) object.node,
                                                (object.left - map_x) * 128 + (obj_width - width) * 64, height_offset,
                                                (object.top - map_y) * 128 + (obj_height - height) * 64, flag);
                                        }
                                    }
                                }
                            }
                    }
                start_x--;
                flag = false;
            }
    }

    private void merge_normals(Model a, Model b, int x_offset, int y_offset, int z_offset, boolean flag) {
        merge_normals_index++;
        int index = 0;
        int b_x[] = b.vertex_x;
        int b_vertices = b.vertices;
        for (int a_vertex = 0; a_vertex < a.vertices; a_vertex++) {
            Vertex a_normals = a.normals[a_vertex];
            Vertex a_gouraud = a.gouraud_vertex[a_vertex];
            if (a_gouraud.magnitude != 0) {
                int height = a.vertex_y[a_vertex] - y_offset;
                if (height <= b.max_y) {
                    int width = a.vertex_x[a_vertex] - x_offset;
                    if (width >= b.min_x && width <= b.max_x) {
                        int depth = a.vertex_z[a_vertex] - z_offset;
                        if (depth >= b.min_z && depth <= b.max_z) {
                            for (int b_vertex = 0; b_vertex < b_vertices; b_vertex++) {
                                Vertex b_normals = b.normals[b_vertex];
                                Vertex b_gouraud = b.gouraud_vertex[b_vertex];
                                if (width == b_x[b_vertex] && depth == b.vertex_z[b_vertex] && height == b.vertex_y[b_vertex] && b_gouraud.magnitude != 0) {
                                    a_normals.x += b_gouraud.x;
                                    a_normals.y += b_gouraud.y;
                                    a_normals.z += b_gouraud.z;
                                    a_normals.magnitude += b_gouraud.magnitude;
                                    b_normals.x += a_gouraud.x;
                                    b_normals.y += a_gouraud.y;
                                    b_normals.z += a_gouraud.z;
                                    b_normals.magnitude += a_gouraud.magnitude;
                                    index++;
                                    merge_a_normals[a_vertex] = merge_normals_index;
                                    merge_b_normals[b_vertex] = merge_normals_index;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (index < 3 || !flag)
            return;

        for (int face = 0; face < a.faces; face++)
            if (merge_a_normals[a.triangle_edge_a[face]] == merge_normals_index
                && merge_a_normals[a.triangle_edge_b[face]] == merge_normals_index
                && merge_a_normals[a.triangle_edge_c[face]] == merge_normals_index)
                a.render_type[face] = -1;

        for (int face = 0; face < b.faces; face++)
            if (merge_b_normals[b.triangle_edge_a[face]] == merge_normals_index
                && merge_b_normals[b.triangle_edge_b[face]] == merge_normals_index
                && merge_b_normals[b.triangle_edge_c[face]] == merge_normals_index)
                b.render_type[face] = -1;

    }

    public void draw_minimap_tile(int pixels[], int index, int plane, int x, int y) {
        int length = 512;
        Tile tile = tile_array[plane][x][y];
        if (tile == null)
            return;

        SimpleTile simple = tile.simple;
        if (simple != null) {
            int color = simple.color_id;
            if (color == 0)
                return;

            for (int step = 0; step < 4; step++) {
                pixels[index] = color;
                pixels[index + 1] = color;
                pixels[index + 2] = color;
                pixels[index + 3] = color;
                index += length;
            }
            return;
        }
        ComplexTile complex = tile.complex;
        if (complex == null)
            return;

        int shape = complex.shape;
        int rotation = complex.rotation;
        int underlay_color = complex.underlay_color;
        int minimap_color = complex.minimap_color;
        int tile_point_shapes[] = tile_points[shape];
        int tile_shape_rotations[] = tile_indices[rotation];
        int tile_indices = 0;
        if (underlay_color != 0) {
            for (int point = 0; point < 4; point++) {
                pixels[index] = tile_point_shapes[tile_shape_rotations[tile_indices++]] != 0 ? minimap_color : underlay_color;
                pixels[index + 1] = tile_point_shapes[tile_shape_rotations[tile_indices++]] != 0 ? minimap_color : underlay_color;
                pixels[index + 2] = tile_point_shapes[tile_shape_rotations[tile_indices++]] != 0 ? minimap_color : underlay_color;
                pixels[index + 3] = tile_point_shapes[tile_shape_rotations[tile_indices++]] != 0 ? minimap_color : underlay_color;
                index += length;
            }

            return;
        }
        for (int point = 0; point < 4; point++) {
            if (tile_point_shapes[tile_shape_rotations[tile_indices++]] != 0)
                pixels[index] = minimap_color;

            if (tile_point_shapes[tile_shape_rotations[tile_indices++]] != 0)
                pixels[index + 1] = minimap_color;

            if (tile_point_shapes[tile_shape_rotations[tile_indices++]] != 0)
                pixels[index + 2] = minimap_color;

            if (tile_point_shapes[tile_shape_rotations[tile_indices++]] != 0)
                pixels[index + 3] = minimap_color;

            index += length;
        }

    }

    public static void set_viewport(int depth, int depth_length, int width, int height, int raster[]) {//TODO
        left = 0;
        top = 0;
        right = width;
        bottom = height;
        center_x = width / 2;
        center_y = height / 2;
        boolean rendered_tile[][][][] = new boolean[9][32][(render_distance * 2) + 3][(render_distance * 2) + 3];
        for (int angle_y = 128; angle_y <= 384; angle_y += 32) {
            for (int angle_x = 0; angle_x < 2048; angle_x += 64) {
                sin_y = Model.SINE[angle_y];
                cos_y = Model.COSINE[angle_y];
                sin_x = Model.SINE[angle_x];
                cos_x = Model.COSINE[angle_x];
                int reference_y = (angle_y - 128) / 32;
                int reference_x = angle_x / 64;
                for (int yaw = -(render_distance + 1); yaw <= (render_distance + 1); yaw++) {
                    for (int pitch = -(render_distance + 1); pitch <= (render_distance + 1); pitch++) {
                        int x = yaw * 128;
                        int y = pitch * 128;
                        boolean visible = false;
                        for (int z = -depth; z <= depth_length; z += 128) {
                            if (!rendered(raster[reference_y] + z, y, x))
                                continue;

                            visible = true;
                            break;
                        }
                        rendered_tile[reference_y][reference_x][yaw + render_distance + 1][pitch + render_distance + 1] = visible;
                    }
                }
            }
        }
        for (int reference_y = 0; reference_y < 8; reference_y++) {
            for (int reference_x = 0; reference_x < 32; reference_x++) {
                for (int yaw = -render_distance; yaw < render_distance; yaw++) {
                    for (int pitch = -render_distance; pitch < render_distance; pitch++) {
                        boolean visible = false;
                        label0:
                        for (int x_offset = -1; x_offset <= 1; x_offset++) {
                            for (int y_offset = -1; y_offset <= 1; y_offset++) {
                                if (rendered_tile[reference_y][reference_x][yaw + x_offset + render_distance + 1][pitch + y_offset + render_distance + 1])
                                    visible = true;

                                else if (rendered_tile[reference_y][(reference_x + 1) % 31][yaw + x_offset + render_distance + 1][pitch + y_offset + render_distance + 1])
                                    visible = true;

                                else if (rendered_tile[reference_y + 1][reference_x][yaw + x_offset + render_distance + 1][pitch + y_offset + render_distance + 1]) {
                                    visible = true;

                                } else {
                                    if (!rendered_tile[reference_y + 1][(reference_x + 1) % 31][yaw + x_offset + render_distance + 1][pitch + y_offset + render_distance + 1])
                                        continue;

                                    visible = true;
                                }
                                break label0;
                            }
                        }
                        TILE_VISIBILITY_MAPS[reference_y][reference_x][yaw + render_distance][pitch + render_distance] = visible;
                    }
                }
            }
        }
    }

    public static final int VIEW_DISTANCE = 3500;

    private static boolean rendered(int z, int y, int x) {
        int x_curve = y * sin_x + x * cos_x >> 16;
        int x_offset = y * cos_x - x * sin_x >> 16;

        int depth = z * sin_y + x_offset * cos_y >> 16;
        int y_curve = z * cos_y - x_offset * sin_y >> 16;
        if (depth < 50 || depth > VIEW_DISTANCE) //3500
            return false;

        int viewport_width = center_x + (x_curve << view_dist) / depth;
        int viewport_height = center_y + (y_curve << view_dist) / depth;
        return viewport_width >= left && viewport_width <= right && viewport_height >= top && viewport_height <= bottom;
    }


    public static final int[] get_position(int raster_x, int raster_y, int raster_z) {
        int position;
        position = raster_z * sin_x + raster_x * cos_x >> 16;
        raster_z = raster_z * cos_x - raster_x * sin_x >> 16;
        raster_x = position;

        position = cos_y * raster_y - raster_z * sin_y >> 16;
        raster_z = sin_y * raster_y + raster_z * cos_y >> 16;
        raster_z |= 1;

        //int scene_x;// = raster_x * Rasterizer.anInt1894 / raster_z + Rasterizer.textureInt1 + Raster.topX;
        //int scene_y;// = Rasterizer.anInt1894 * position / raster_z + Rasterizer.textureInt2 + Raster.topY;

        int scene_x = Rasterizer3D.center_x + (raster_x << SceneGraph.view_dist) / raster_z;
        int scene_y = Rasterizer3D.center_y + (position << SceneGraph.view_dist) / raster_z;
        return new int[]{
            scene_x, scene_y
        };
    }

    public void register_click(int y, int x) {
        interacted = true;
        click_x = x;
        click_y = y;
        click_tile_x = -1;
        click_tile_y = -1;
    }

    public void render(int cam_pos_x, int cam_pos_z, int curve_x, int cam_pos_y, int plane, int curve_y) {
        if (cam_pos_x < 0)
            cam_pos_x = 0;

        else if (cam_pos_x >= map_size_x * 128)
            cam_pos_x = map_size_x * 128 - 1;

        if (cam_pos_z < 0)
            cam_pos_z = 0;

        else if (cam_pos_z >= map_size_y * 128)
            cam_pos_z = map_size_y * 128 - 1;

        rendered_obj++;
        SceneGraph.sin_y = Model.SINE[curve_y];
        SceneGraph.cos_y = Model.COSINE[curve_y];
        SceneGraph.sin_x = Model.SINE[curve_x];
        SceneGraph.cos_x = Model.COSINE[curve_x];
        TILE_VISIBILITY_MAP = TILE_VISIBILITY_MAPS[(curve_y - 128) / 32][curve_x / 64];
        SceneGraph.cam_pos_x = cam_pos_x;
        SceneGraph.cam_pos_z = cam_pos_y;
        SceneGraph.cam_pos_y = cam_pos_z;
        tile_center_x = cam_pos_x / 128;
        tile_center_y = cam_pos_z / 128;
        SceneGraph.plane = plane;
        current_pos_x = tile_center_x - render_distance;
        if (current_pos_x < 0)
            current_pos_x = 0;

        current_pos_y = tile_center_y - render_distance;
        if (current_pos_y < 0)
            current_pos_y = 0;

        region_x = tile_center_x + render_distance;
        if (region_x > map_size_x)
            region_x = map_size_x;

        region_y = tile_center_y + render_distance;
        if (region_y > map_size_y)
            region_y = map_size_y;

        this.process_culling();
        anInt446 = 0;
        for (int z = current_pos_z; z < map_size_z; z++) {
            Tile tile_height[][] = tile_array[z];
            for (int x = current_pos_x; x < region_x; x++) {
                for (int y = current_pos_y; y < region_y; y++) {
                    Tile tile = tile_height[x][y];
                    if (tile != null)
                        if (tile.logic_height > plane || !TILE_VISIBILITY_MAP[(x - tile_center_x) + render_distance][(y - tile_center_y) + render_distance] && height_map[z][x][y] - cam_pos_y < 2000) {
                            tile.updated = false;
                            tile.drawn = false;
                            tile.render_mask = 0;
                        } else {
                            tile.updated = true;
                            tile.drawn = true;
                            tile.multiple_objects = tile.occupants > 0;
                            anInt446++;
                        }

                }
            }
        }
        for (int z = current_pos_z; z < map_size_z; z++) {
            Tile height[][] = tile_array[z];
            for (int x_offset = -render_distance; x_offset <= 0; x_offset++) {
                int right = tile_center_x + x_offset;
                int left = tile_center_x - x_offset;
                if (right >= current_pos_x || left < region_x) {
                    for (int y_offset = -render_distance; y_offset <= 0; y_offset++) {
                        int bottom = tile_center_y + y_offset;
                        int top = tile_center_y - y_offset;
                        if (right >= current_pos_x) {
                            if (bottom >= current_pos_y) {
                                Tile tile = height[right][bottom];
                                if (tile != null && tile.updated)
                                    load(tile, true);
                            }
                            if (top < region_y) {
                                Tile tile = height[right][top];
                                if (tile != null && tile.updated)
                                    load(tile, true);
                            }
                        }
                        if (left < region_x) {
                            if (bottom >= current_pos_y) {
                                Tile tile = height[left][bottom];
                                if (tile != null && tile.updated)
                                    load(tile, true);
                            }
                            if (top < region_y) {
                                Tile tile = height[left][top];
                                if (tile != null && tile.updated)
                                    load(tile, true);
                            }
                        }
                        if (anInt446 == 0) {
                            interacted = false;
                            return;
                        }
                    }

                }
            }

        }

        for (int z = current_pos_z; z < map_size_z; z++) {
            Tile height[][] = tile_array[z];
            for (int x_offset = -render_distance; x_offset <= 0; x_offset++) {
                int right = tile_center_x + x_offset;
                int left = tile_center_x - x_offset;
                if (right >= current_pos_x || left < region_x) {
                    for (int y_offset = -render_distance; y_offset <= 0; y_offset++) {
                        int top = tile_center_y + y_offset;
                        int bottom = tile_center_y - y_offset;
                        if (right >= current_pos_x) {
                            if (top >= current_pos_y) {
                                Tile tile = height[right][top];
                                if (tile != null && tile.updated)
                                    load(tile, false);
                            }
                            if (bottom < region_y) {
                                Tile tile = height[right][bottom];
                                if (tile != null && tile.updated)
                                    load(tile, false);
                            }
                        }
                        if (left < region_x) {
                            if (top >= current_pos_y) {
                                Tile tile = height[left][top];
                                if (tile != null && tile.updated)
                                    load(tile, false);
                            }
                            if (bottom < region_y) {
                                Tile tile = height[left][bottom];
                                if (tile != null && tile.updated)
                                    load(tile, false);
                            }
                        }
                        if (anInt446 == 0) {
                            interacted = false;
                            return;
                        }
                    }
                }
            }
        }
        interacted = false;
    }

    private void load(Tile loaded, boolean flag) {
        tile_list.insertBack(loaded);
        do {
            Tile ground;
            do {
                ground = (Tile) tile_list.pop();
                if (ground == null)
                    return;

            } while (!ground.drawn);

            int camera_x = ground.x;
            int camera_y = ground.y;
            int camera_z = ground.z;
            int plane = ground.plane;
            Tile tile_heights[][] = tile_array[camera_z];
            //seems to be object decoration related (sharing a tile with multiple objects)
            if (ground.updated) {
                if (flag) {
                    if (camera_z > 0) {
                        Tile tile = tile_array[camera_z - 1][camera_x][camera_y];
                        if (tile != null && tile.drawn)
                            continue;

                    }
                    if (camera_x <= tile_center_x && camera_x > current_pos_x) {
                        Tile tile = tile_heights[camera_x - 1][camera_y];
                        if (tile != null && tile.drawn && (tile.updated || (ground.origin_mask & 1) == 0))
                            continue;
                    }
                    if (camera_x >= tile_center_x && camera_x < region_x - 1) {
                        Tile tile = tile_heights[camera_x + 1][camera_y];
                        if (tile != null && tile.drawn && (tile.updated || (ground.origin_mask & 4) == 0))
                            continue;
                    }
                    if (camera_y <= tile_center_y && camera_y > current_pos_y) {
                        Tile tile = tile_heights[camera_x][camera_y - 1];
                        if (tile != null && tile.drawn && (tile.updated || (ground.origin_mask & 8) == 0))
                            continue;
                    }
                    if (camera_y >= tile_center_y && camera_y < region_y - 1) {
                        Tile tile = tile_heights[camera_x][camera_y + 1];
                        if (tile != null && tile.drawn && (tile.updated || (ground.origin_mask & 2) == 0))
                            continue;
                    }
                } else {
                    flag = true;
                }
                ground.updated = false;
                if (ground.sub_tile != null) {
                    Tile sub = ground.sub_tile;
                    if (sub.simple != null) {
                        if (!tile_visible(0, camera_x, camera_y))
                            render_simple_tile(sub.simple, 0, sin_y, cos_y, sin_x, cos_x, camera_x, camera_y);

                    } else if (sub.complex != null && !tile_visible(0, camera_x, camera_y))
                        render_complex_tile(camera_x, sin_y, sin_x, sub.complex, cos_y, camera_y, cos_x);

                    Wall wall = sub.wall;
                    if (wall != null)
                        wall.wall.render_3D(0, sin_y, cos_y, sin_x, cos_x,
                            wall.world_x - cam_pos_x, wall.plane - cam_pos_z, wall.world_y - cam_pos_y,
                            wall.uid);

                    for (int index = 0; index < sub.occupants; index++) {
                        InteractiveObject object = sub.interactive_obj[index];
                        if (object != null)
                            object.node.render_3D(object.orientation, sin_y, cos_y, sin_x,
                                cos_x, object.world_x - cam_pos_x, object.world_z - cam_pos_z,
                                object.world_y - cam_pos_y, object.uid);

                    }

                }
                boolean enable_decor = false;
                if (ground.simple != null) {
                    if (!tile_visible(plane, camera_x, camera_y)) {
                        enable_decor = true;
                        render_simple_tile(ground.simple, plane, sin_y, cos_y, sin_x, cos_x, camera_x, camera_y);
                    }
                } else if (ground.complex != null && !tile_visible(plane, camera_x, camera_y)) {
                    enable_decor = true;
                    render_complex_tile(camera_x, sin_y, sin_x, ground.complex, cos_y, camera_y, cos_x);
                }
                int cull_factor = 0;
                int camera_x_angle = 0;
                Wall wall = ground.wall;
                WallDecoration wall_decor = ground.wall_decor;
                if (wall != null || wall_decor != null) {
                    if (tile_center_x == camera_x)
                        cull_factor++;
                    else if (tile_center_x < camera_x)
                        cull_factor += 2;
                    if (tile_center_y == camera_y)
                        cull_factor += 3;
                    else if (tile_center_y > camera_y)
                        cull_factor += 6;

                    camera_x_angle = wall_direction_x[cull_factor];
                    ground.depth = wall_direction_z[cull_factor];
                }
                if (wall != null) {
                    if ((wall.wall_orientation & wall_direction_y[cull_factor]) != 0) {
                        if (wall.wall_orientation == 16) {
                            ground.render_mask = 3;
                            ground.viewport_angle = viewport_camera_left[cull_factor];
                            ground.culled_face_mask = 3 - ground.viewport_angle;
                        } else if (wall.wall_orientation == 32) {
                            ground.render_mask = 6;
                            ground.viewport_angle = viewport_camera_right[cull_factor];
                            ground.culled_face_mask = 6 - ground.viewport_angle;
                        } else if (wall.wall_orientation == 64) {
                            ground.render_mask = 12;
                            ground.viewport_angle = viewport_camera_top[cull_factor];
                            ground.culled_face_mask = 12 - ground.viewport_angle;
                        } else {
                            ground.render_mask = 9;
                            ground.viewport_angle = viewport_camera_bottom[cull_factor];
                            ground.culled_face_mask = 9 - ground.viewport_angle;
                        }
                    } else {
                        ground.render_mask = 0;
                    }
                    if ((wall.wall_orientation & camera_x_angle) != 0 && !wall_visible(plane, camera_x, camera_y, wall.wall_orientation))
                        wall.wall.render_3D(0, sin_y, cos_y, sin_x, cos_x,
                            wall.world_x - cam_pos_x, wall.plane - cam_pos_z,
                            wall.world_y - cam_pos_y, wall.uid);

                    if ((wall.corner_orientation & camera_x_angle) != 0 && !wall_visible(plane, camera_x, camera_y, wall.corner_orientation))
                        wall.corner.render_3D(0, sin_y, cos_y, sin_x, cos_x,
                            wall.world_x - cam_pos_x, wall.plane - cam_pos_z,
                            wall.world_y - cam_pos_y, wall.uid);

                }
                if (render_wall_decorations && wall_decor != null && !decor_visible(plane, camera_x, camera_y, wall_decor.node.model_height))
                    if ((wall_decor.config_mask & camera_x_angle) != 0)
                        wall_decor.node.render_3D(wall_decor.orientation, sin_y, cos_y, sin_x,
                            cos_x, wall_decor.world_x - cam_pos_x, wall_decor.plane - cam_pos_z,
                            wall_decor.world_y - cam_pos_y, wall_decor.uid);

                    else if ((wall_decor.config_mask & 0x300) != 0) {
                        //config related
                        int x = wall_decor.world_x - cam_pos_x;
                        int z = wall_decor.plane - cam_pos_z;
                        int y = wall_decor.world_y - cam_pos_y;
                        int orientation = wall_decor.orientation;
                        int yaw;
                        if (orientation == 1 || orientation == 2)
                            yaw = -x;
                        else
                            yaw = x;

                        int pitch;
                        if (orientation == 2 || orientation == 3)
                            pitch = -y;
                        else
                            pitch = y;

                        if ((wall_decor.config_mask & 0x100) != 0 && pitch < yaw) {
                            int pos_x = x + wall_config_0x100_x[orientation];
                            int pos_y = y + wall_config_0x100_y[orientation];
                            wall_decor.node.render_3D(orientation * 512 + 256, sin_y, cos_y, sin_x,
                                cos_x, pos_x, z, pos_y, wall_decor.uid);
                        }
                        if ((wall_decor.config_mask & 0x200) != 0 && pitch > yaw) {
                            int pos_x = x + wall_config_0x200_x[orientation];
                            int pos_y = y + wall_config_0x200_y[orientation];
                            wall_decor.node.render_3D(orientation * 512 + 1280 & 0x7ff, sin_y, cos_y,
                                sin_x, cos_x, pos_x, z, pos_y, wall_decor.uid);
                        }
                    }
                if (enable_decor) {
                    GroundDecoration ground_decor = ground.ground_decor;
                    if (render_ground_decorations && ground_decor != null)
                        ground_decor.node.render_3D(0, sin_y, cos_y, sin_x, cos_x,
                            ground_decor.world_x - cam_pos_x, ground_decor.world_z - cam_pos_z, ground_decor.world_y - cam_pos_y,
                            ground_decor.uid);

                    GroundItem ground_item = ground.ground_item;
                    if (ground_item != null && ground_item.height_offset == 0) {
                        if (ground_item.first != null)
                            ground_item.first.render_3D(0, sin_y, cos_y, sin_x, cos_x,
                                ground_item.world_x - cam_pos_x, ground_item.world_z - cam_pos_z,
                                ground_item.world_y - cam_pos_y, ground_item.uid);

                        if (ground_item.second != null)
                            ground_item.second.render_3D(0, sin_y, cos_y, sin_x, cos_x,
                                ground_item.world_x - cam_pos_x, ground_item.world_z - cam_pos_z,
                                ground_item.world_y - cam_pos_y, ground_item.uid);

                        if (ground_item.third != null)
                            ground_item.third.render_3D(0, sin_y, cos_y, sin_x, cos_x,
                                ground_item.world_x - cam_pos_x, ground_item.world_z - cam_pos_z,
                                ground_item.world_y - cam_pos_y, ground_item.uid);
                    }
                }
                int mask = ground.origin_mask;
                if (mask != 0) {
                    if (camera_x < tile_center_x && (mask & 4) != 0) {
                        Tile tile = tile_heights[camera_x + 1][camera_y];
                        if (tile != null && tile.drawn)
                            tile_list.insertBack(tile);

                    }
                    if (camera_y < tile_center_y && (mask & 2) != 0) {
                        Tile tile = tile_heights[camera_x][camera_y + 1];
                        if (tile != null && tile.drawn)
                            tile_list.insertBack(tile);

                    }
                    if (camera_x > tile_center_x && (mask & 1) != 0) {
                        Tile tile = tile_heights[camera_x - 1][camera_y];
                        if (tile != null && tile.drawn)
                            tile_list.insertBack(tile);

                    }
                    if (camera_y > tile_center_y && (mask & 8) != 0) {
                        Tile tile = tile_heights[camera_x][camera_y - 1];
                        if (tile != null && tile.drawn) {
                            tile_list.insertBack(tile);
                        }

                    }
                }
            }
            if (ground.render_mask != 0) {
                boolean flag2 = true;//TODO
                for (int index = 0; index < ground.occupants; index++) {
                    if (ground.interactive_obj[index].rendered == rendered_obj || (ground.interactive_obj_size[index]
                        & ground.render_mask) != ground.viewport_angle)
                        continue;

                    flag2 = false;
                    break;
                }
                if (flag2) {
                    Wall wall = ground.wall;
                    if (!wall_visible(plane, camera_x, camera_y, wall.wall_orientation))
                        wall.wall.render_3D(0, sin_y, cos_y, sin_x, cos_x, wall.world_x - cam_pos_x, wall.plane - cam_pos_z,
                            wall.world_y - cam_pos_y, wall.uid);

                    ground.render_mask = 0;
                }
            }
            if (ground.multiple_objects)
                try {
                    int occupants = ground.occupants;
                    ground.multiple_objects = false;
                    int interactive_indices = 0;
                    label0:
                    for (int index = 0; index < occupants; index++) {
                        InteractiveObject object = ground.interactive_obj[index];

                        if (object.rendered == rendered_obj)
                            continue;

                        for (int x = object.left; x <= object.right; x++) {
                            for (int y = object.top; y <= object.bottom; y++) {
                                Tile tile = tile_heights[x][y];
                                if (tile.updated) {
                                    ground.multiple_objects = true;
                                } else {
                                    if (tile.render_mask == 0)
                                        continue;

                                    int camera_angle = 0;
                                    if (x > object.left)
                                        camera_angle++;

                                    if (x < object.right)
                                        camera_angle += 4;

                                    if (y > object.top)
                                        camera_angle += 8;

                                    if (y < object.bottom)
                                        camera_angle += 2;

                                    if ((camera_angle & tile.render_mask) != ground.culled_face_mask)
                                        continue;

                                    ground.multiple_objects = true;
                                }
                                continue label0;
                            }
                        }
                        interactive_obj[interactive_indices++] = object;

                        int yaw = tile_center_x - object.left;
                        int angle_x = object.right - tile_center_x;
                        if (angle_x > yaw)
                            yaw = angle_x;

                        int pitch = tile_center_y - object.top;
                        int angle_y = object.bottom - tile_center_y;

                        if (angle_y > pitch) {
                            object.camera_distance = yaw + angle_y;
                        } else {
                            object.camera_distance = yaw + pitch;
                        }
                    }

                    while (interactive_indices > 0) {
                        int distance = -50;
                        int pos = -1;
                        for (int index = 0; index < interactive_indices; index++) {
                            InteractiveObject object = interactive_obj[index];
                            if (object.rendered != rendered_obj)
                                if (object.camera_distance > distance) {
                                    distance = object.camera_distance;
                                    pos = index;
                                } else if (object.camera_distance == distance) {
                                    int cam_x = object.world_x - cam_pos_x;
                                    int cam_y = object.world_y - cam_pos_y;
                                    int obj_x = interactive_obj[pos].world_x - cam_pos_x;
                                    int obj_y = interactive_obj[pos].world_y - cam_pos_y;
                                    if (cam_x * cam_x + cam_y * cam_y > obj_x * obj_x + obj_y * obj_y)
                                        pos = index;
                                }
                        }
                        if (pos == -1)
                            break;

                        InteractiveObject object = interactive_obj[pos];
                        object.rendered = rendered_obj;
                        if (!object_visible(plane, object.left, object.right, object.top, object.bottom, object.node.model_height))
                            object.node.render_3D(object.orientation, sin_y, cos_y, sin_x,
                                cos_x, object.world_x - cam_pos_x, object.world_z - cam_pos_z,
                                object.world_y - cam_pos_y, object.uid);

                        for (int x = object.left; x <= object.right; x++) {
                            for (int y = object.top; y <= object.bottom; y++) {
                                Tile tile = tile_heights[x][y];
                                if (tile.render_mask != 0)
                                    tile_list.insertBack(tile);
                                else if ((x != camera_x || y != camera_y) && tile.drawn)
                                    tile_list.insertBack(tile);
                            }
                        }
                    }
                    if (ground.multiple_objects)
                        continue;

                } catch (Exception _ex) {
                    _ex.printStackTrace();
                    ground.multiple_objects = false;
                }
            if (!ground.drawn || ground.render_mask != 0)
                continue;

            if (camera_x <= tile_center_x && camera_x > current_pos_x) {
                Tile tile = tile_heights[camera_x - 1][camera_y];
                if (tile != null && tile.drawn)
                    continue;
            }
            if (camera_x >= tile_center_x && camera_x < region_x - 1) {
                Tile tile = tile_heights[camera_x + 1][camera_y];
                if (tile != null && tile.drawn)
                    continue;
            }
            if (camera_y <= tile_center_y && camera_y > current_pos_y) {
                Tile tile = tile_heights[camera_x][camera_y - 1];
                if (tile != null && tile.drawn)
                    continue;
            }
            if (camera_y >= tile_center_y && camera_y < region_y - 1) {
                Tile tile = tile_heights[camera_x][camera_y + 1];
                if (tile != null && tile.drawn)
                    continue;
            }
            ground.drawn = false;
            anInt446--;
            GroundItem item = ground.ground_item;
            if (item != null && item.height_offset != 0) {
                if (item.first != null)
                    item.first.render_3D(0, sin_y, cos_y, sin_x,
                        cos_x, item.world_x - cam_pos_x, item.world_z - cam_pos_z - item.height_offset, item.world_y - cam_pos_y,
                        item.uid);

                if (item.second != null)
                    item.second.render_3D(0, sin_y, cos_y, sin_x,
                        cos_x, item.world_x - cam_pos_x, item.world_z - cam_pos_z - item.height_offset, item.world_y - cam_pos_y,
                        item.uid);

                if (item.third != null)
                    item.third.render_3D(0, sin_y, cos_y, sin_x,
                        cos_x, item.world_x - cam_pos_x, item.world_z - cam_pos_z - item.height_offset, item.world_y - cam_pos_y,
                        item.uid);
            }
            if (ground.depth != 0) {
                WallDecoration decor = ground.wall_decor;
                if (render_wall_decorations && decor != null && !decor_visible(plane, camera_x, camera_y, decor.node.model_height))
                    if ((decor.config_mask & ground.depth) != 0)
                        decor.node.render_3D(decor.orientation, sin_y, cos_y, sin_x, cos_x, decor.world_x - cam_pos_x, decor.plane - cam_pos_z, decor.world_y - cam_pos_y, decor.uid);
                    else if ((decor.config_mask & 0x300) != 0) {
                        int x = decor.world_x - cam_pos_x;
                        int z = decor.plane - cam_pos_z;
                        int y = decor.world_y - cam_pos_y;
                        int orientation = decor.orientation;
                        int angle_x;
                        if (orientation == 1 || orientation == 2)
                            angle_x = -x;
                        else
                            angle_x = x;

                        int angle_y;
                        if (orientation == 2 || orientation == 3)
                            angle_y = -y;
                        else
                            angle_y = y;

                        if ((decor.config_mask & 0x100) != 0 && angle_y >= angle_x) {
                            int start_x = x + wall_config_0x100_x[orientation];
                            int start_y = y + wall_config_0x100_y[orientation];
                            decor.node.render_3D(orientation * 512 + 256, sin_y, cos_y, sin_x, cos_x, start_x, z, start_y, decor.uid);
                        }
                        if ((decor.config_mask & 0x200) != 0 && angle_y <= angle_x) {
                            int start_x = x + wall_config_0x200_x[orientation];
                            int start_y = y + wall_config_0x200_y[orientation];
                            decor.node.render_3D(orientation * 512 + 1280 & 0x7ff, sin_y, cos_y, sin_x, cos_x, start_x, z, start_y, decor.uid);
                        }
                    }

                Wall wall = ground.wall;
                if (wall != null) {
                    if ((wall.corner_orientation & ground.depth) != 0 && !wall_visible(plane, camera_x, camera_y, wall.corner_orientation))
                        wall.corner.render_3D(0, sin_y, cos_y, sin_x, cos_x, wall.world_x - cam_pos_x, wall.plane - cam_pos_z, wall.world_y - cam_pos_y, wall.uid);

                    if ((wall.wall_orientation & ground.depth) != 0 && !wall_visible(plane, camera_x, camera_y, wall.wall_orientation))
                        wall.wall.render_3D(0, sin_y, cos_y, sin_x, cos_x, wall.world_x - cam_pos_x, wall.plane - cam_pos_z, wall.world_y - cam_pos_y, wall.uid);

                }
            }
            if (camera_z < map_size_z - 1) {
                Tile tile = tile_array[camera_z + 1][camera_x][camera_y];
                if (tile != null && tile.drawn)
                    tile_list.insertBack(tile);
            }
            if (camera_x < tile_center_x) {
                Tile tile = tile_heights[camera_x + 1][camera_y];
                if (tile != null && tile.drawn)
                    tile_list.insertBack(tile);
            }
            if (camera_y < tile_center_y) {
                Tile tile = tile_heights[camera_x][camera_y + 1];
                if (tile != null && tile.drawn)
                    tile_list.insertBack(tile);
            }
            if (camera_x > tile_center_x) {
                Tile tile = tile_heights[camera_x - 1][camera_y];
                if (tile != null && tile.drawn)
                    tile_list.insertBack(tile);
            }
            if (camera_y > tile_center_y) {
                Tile tile = tile_heights[camera_x][camera_y - 1];
                if (tile != null && tile.drawn)
                    tile_list.insertBack(tile);
            }
        } while (true);
    }

    private void render_simple_tile(SimpleTile simple, int plane, int sin_y, int cos_y, int sin_x, int cos_x, int camera_x, int camera_y) {
        int viewpoint_x_d;
        int viewpoint_x_a = viewpoint_x_d = (camera_x << 7) - cam_pos_x;
        int depth_b;
        int depth_a = depth_b = (camera_y << 7) - cam_pos_y;
        int viewpoint_x_c;
        int viewpoint_x_b = viewpoint_x_c = viewpoint_x_a + 128;
        int depth_d;
        int depth_c = depth_d = depth_a + 128;
        int viewpoint_y_a = height_map[plane][camera_x][camera_y] - cam_pos_z;
        int viewport_y_b = height_map[plane][camera_x + 1][camera_y] - cam_pos_z;
        int viewpoint_y_c = height_map[plane][camera_x + 1][camera_y + 1] - cam_pos_z;
        int viewpoint_y_d = height_map[plane][camera_x][camera_y + 1] - cam_pos_z;
        int pos = depth_a * sin_x + viewpoint_x_a * cos_x >> 16;

        depth_a = depth_a * cos_x - viewpoint_x_a * sin_x >> 16;
        viewpoint_x_a = pos;
        pos = viewpoint_y_a * cos_y - depth_a * sin_y >> 16;
        depth_a = viewpoint_y_a * sin_y + depth_a * cos_y >> 16;
        viewpoint_y_a = pos;
        if (depth_a < 50)
            return;

        pos = depth_b * sin_x + viewpoint_x_b * cos_x >> 16;
        depth_b = depth_b * cos_x - viewpoint_x_b * sin_x >> 16;
        viewpoint_x_b = pos;
        pos = viewport_y_b * cos_y - depth_b * sin_y >> 16;
        depth_b = viewport_y_b * sin_y + depth_b * cos_y >> 16;
        viewport_y_b = pos;
        if (depth_b < 50)
            return;

        pos = depth_c * sin_x + viewpoint_x_c * cos_x >> 16;
        depth_c = depth_c * cos_x - viewpoint_x_c * sin_x >> 16;
        viewpoint_x_c = pos;
        pos = viewpoint_y_c * cos_y - depth_c * sin_y >> 16;
        depth_c = viewpoint_y_c * sin_y + depth_c * cos_y >> 16;
        viewpoint_y_c = pos;
        if (depth_c < 50)
            return;

        pos = depth_d * sin_x + viewpoint_x_d * cos_x >> 16;
        depth_d = depth_d * cos_x - viewpoint_x_d * sin_x >> 16;
        viewpoint_x_d = pos;
        pos = viewpoint_y_d * cos_y - depth_d * sin_y >> 16;
        depth_d = viewpoint_y_d * sin_y + depth_d * cos_y >> 16;
        viewpoint_y_d = pos;
        if (depth_d < 50)
            return;

        int x_a = Rasterizer3D.center_x + (viewpoint_x_a << view_dist) / depth_a;
        int y_a = Rasterizer3D.center_y + (viewpoint_y_a << view_dist) / depth_a;
        int x_b = Rasterizer3D.center_x + (viewpoint_x_b << view_dist) / depth_b;
        int y_b = Rasterizer3D.center_y + (viewport_y_b << view_dist) / depth_b;
        int x_c = Rasterizer3D.center_x + (viewpoint_x_c << view_dist) / depth_c;
        int y_c = Rasterizer3D.center_y + (viewpoint_y_c << view_dist) / depth_c;
        int x_d = Rasterizer3D.center_x + (viewpoint_x_d << view_dist) / depth_d;
        int y_d = Rasterizer3D.center_y + (viewpoint_y_d << view_dist) / depth_d;
        Rasterizer3D.alpha = 0;
        if ((x_c - x_d) * (y_b - y_d) - (y_c - y_d) * (x_b - x_d) > 0) {
            Rasterizer3D.testX = x_c < 0 || x_d < 0 || x_b < 0 || x_c > Rasterizer2D.center_x || x_d > Rasterizer2D.center_x || x_b > Rasterizer2D.center_x;
            if (interacted && entered_tile(click_x, click_y, y_c, y_d, y_b, x_c, x_d, x_b)) {
                click_tile_x = camera_x;
                click_tile_y = camera_y;
            }
            if (simple.texture_id == -1) {
                if (simple.shadow_c != 0xbc614e) {
                    Rasterizer3D.drawShadedTriangle(y_c, y_d, y_b, x_c, x_d, x_b, simple.shadow_c, simple.shadow_d, simple.shadow_b);
                }
            } else if (!low_detail) {
                if (simple.flat) {
                    Rasterizer3D.drawTexturedTriangle(y_c, y_d, y_b, x_c, x_d, x_b, simple.shadow_c, simple.shadow_d,
                        simple.shadow_b, viewpoint_x_a, viewpoint_x_b, viewpoint_x_d, viewpoint_y_a, viewport_y_b, viewpoint_y_d, depth_a, depth_b, depth_d, simple.texture_id);
                } else {
                    Rasterizer3D.drawTexturedTriangle(y_c, y_d, y_b, x_c, x_d, x_b, simple.shadow_c, simple.shadow_d,
                        simple.shadow_b, viewpoint_x_c, viewpoint_x_d, viewpoint_x_b, viewpoint_y_c, viewpoint_y_d, viewport_y_b, depth_c, depth_d, depth_b, simple.texture_id);
                }
            } else {
                int i7 = tile_textures[simple.texture_id];
                Rasterizer3D.drawShadedTriangle(y_c, y_d, y_b, x_c, x_d, x_b, light(i7, simple.shadow_c), light(i7, simple.shadow_d), light(i7, simple.shadow_b));
            }
        }
        if ((x_a - x_b) * (y_d - y_b) - (y_a - y_b) * (x_d - x_b) > 0) {
            Rasterizer3D.testX = x_a < 0 || x_b < 0 || x_d < 0 || x_a > Rasterizer2D.center_x || x_b > Rasterizer2D.center_x || x_d > Rasterizer2D.center_x;
            if (interacted && entered_tile(click_x, click_y, y_a, y_b, y_d, x_a, x_b, x_d)) {
                click_tile_x = camera_x;
                click_tile_y = camera_y;
            }
            if (simple.texture_id == -1) {
                if (simple.shadow_a != 0xbc614e) {
                    Rasterizer3D.drawShadedTriangle(y_a, y_b, y_d, x_a, x_b, x_d, simple.shadow_a, simple.shadow_b,
                        simple.shadow_d);
                }
            } else {
                if (!low_detail) {
                    Rasterizer3D.drawTexturedTriangle(y_a, y_b, y_d, x_a, x_b, x_d, simple.shadow_a, simple.shadow_b,
                        simple.shadow_d, viewpoint_x_a, viewpoint_x_b, viewpoint_x_d, viewpoint_y_a, viewport_y_b, viewpoint_y_d, depth_a, depth_b, depth_d, simple.texture_id);
                    return;
                }
                int texture_id = tile_textures[simple.texture_id];
                Rasterizer3D.drawShadedTriangle(y_a, y_b, y_d, x_a, x_b, x_d, light(texture_id, simple.shadow_a),
                    light(texture_id, simple.shadow_b), light(texture_id, simple.shadow_d));
            }
        }
    }

    private void render_complex_tile(int camera_x, int sin_y, int sin_x, ComplexTile complex, int cos_y, int camera_y, int cos_x) {
        int vertices = complex.vertex_x.length;
        for (int index = 0; index < vertices; index++) {
            int viewport_x = complex.vertex_x[index] - cam_pos_x;
            int viewport_y = complex.vertex_y[index] - cam_pos_z;
            int viewport_z = complex.vertex_z[index] - cam_pos_y;
            int pos = viewport_z * sin_x + viewport_x * cos_x >> 16;
            viewport_z = viewport_z * cos_x - viewport_x * sin_x >> 16;
            viewport_x = pos;

            pos = viewport_y * cos_y - viewport_z * sin_y >> 16;
            viewport_z = viewport_y * sin_y + viewport_z * cos_y >> 16;
            viewport_y = pos;
            if (viewport_z < 50)
                return;

            if (complex.material != null) {
                ComplexTile.texture_viewpoint_x[index] = viewport_x;
                ComplexTile.texture_viewpoint_y[index] = viewport_y;
                ComplexTile.texture_viewpoint_z[index] = viewport_z;
            }
            ComplexTile.vertex_viewpoint_x[index] = Rasterizer3D.center_x + (viewport_x << view_dist) / viewport_z;
            ComplexTile.vertex_viewpoint_y[index] = Rasterizer3D.center_y + (viewport_y << view_dist) / viewport_z;
            ComplexTile.vertex_viewpoint_z[index] = viewport_z;
        }

        Rasterizer3D.alpha = 0;
        vertices = complex.triangle_a.length;
        for (int face = 0; face < vertices; face++) {
            int tri_a = complex.triangle_a[face];
            int tri_b = complex.triangle_b[face];
            int tri_c = complex.triangle_c[face];
            int x_a = ComplexTile.vertex_viewpoint_x[tri_a];
            int x_b = ComplexTile.vertex_viewpoint_x[tri_b];
            int x_c = ComplexTile.vertex_viewpoint_x[tri_c];
            int y_a = ComplexTile.vertex_viewpoint_y[tri_a];
            int y_b = ComplexTile.vertex_viewpoint_y[tri_b];
            int y_c = ComplexTile.vertex_viewpoint_y[tri_c];
            if ((x_a - x_b) * (y_c - y_b) - (y_a - y_b) * (x_c - x_b) > 0) {
                Rasterizer3D.testX = x_a < 0 || x_b < 0 || x_c < 0 || x_a > Rasterizer2D.center_x || x_b > Rasterizer2D.center_x || x_c > Rasterizer2D.center_x;
                if (interacted && entered_tile(click_x, click_y, y_a, y_b, y_c, x_a, x_b, x_c)) {
                    click_tile_x = camera_x;
                    click_tile_y = camera_y;
                }
                if (complex.material == null || complex.material[face] == -1) {
                    if (complex.hue_a[face] != 0xbc614e) {
                        Rasterizer3D.drawShadedTriangle(y_a, y_b, y_c, x_a, x_b, x_c,
                            complex.hue_a[face], complex.hue_b[face], complex.hue_c[face],
                            ComplexTile.vertex_viewpoint_z[tri_a], ComplexTile.vertex_viewpoint_z[tri_b], ComplexTile.vertex_viewpoint_z[tri_c]);
                    }
                } else if (!low_detail) {
                    if (complex.flat) {
                        Rasterizer3D.drawTexturedTriangle(y_a, y_b, y_c, x_a, x_b, x_c,
                            complex.hue_a[face], complex.hue_b[face], complex.hue_c[face],
                            ComplexTile.texture_viewpoint_x[0], ComplexTile.texture_viewpoint_x[1], ComplexTile.texture_viewpoint_x[3],
                            ComplexTile.texture_viewpoint_y[0], ComplexTile.texture_viewpoint_y[1], ComplexTile.texture_viewpoint_y[3],
                            ComplexTile.texture_viewpoint_z[0], ComplexTile.texture_viewpoint_z[1], ComplexTile.texture_viewpoint_z[3],
                            complex.material[face]);
                    } else {
                        Rasterizer3D.drawTexturedTriangle(y_a, y_b, y_c, x_a, x_b, x_c,
                            complex.hue_a[face], complex.hue_b[face], complex.hue_c[face],
                            ComplexTile.texture_viewpoint_x[tri_a], ComplexTile.texture_viewpoint_x[tri_b], ComplexTile.texture_viewpoint_x[tri_c],
                            ComplexTile.texture_viewpoint_y[tri_a], ComplexTile.texture_viewpoint_y[tri_b], ComplexTile.texture_viewpoint_y[tri_c],
                            ComplexTile.texture_viewpoint_z[tri_a], ComplexTile.texture_viewpoint_z[tri_b], ComplexTile.texture_viewpoint_z[tri_c],
                            complex.material[face]);
                    }
                } else {
                    int texture_id = tile_textures[complex.material[face]];
                    Rasterizer3D.drawShadedTriangle(y_a, y_b, y_c, x_a, x_b, x_c,
                        light(texture_id, complex.hue_a[face]), light(texture_id, complex.hue_b[face]), light(texture_id, complex.hue_c[face]));
                }
            }
        }
    }

    private int light(int hsl, int light) {
        light = 127 - light;
        light = (light * (hsl & 0x7f)) / 160;
        if (light < 2)
            light = 2;
        else if (light > 126)
            light = 126;
        return (hsl & 0xff80) + light;
    }

    private boolean entered_tile(int mouse_x, int mouse_y, int y_a, int y_b, int y_c, int x_a, int x_b, int x_c) {
        if (mouse_y < y_a && mouse_y < y_b && mouse_y < y_c)
            return false;

        if (mouse_y > y_a && mouse_y > y_b && mouse_y > y_c)
            return false;

        if (mouse_x < x_a && mouse_x < x_b && mouse_x < x_c)
            return false;

        if (mouse_x > x_a && mouse_x > x_b && mouse_x > x_c)
            return false;

        int triangle_a = (mouse_y - y_a) * (x_b - x_a) - (mouse_x - x_a) * (y_b - y_a);
        int triangle_b = (mouse_y - y_c) * (x_a - x_c) - (mouse_x - x_c) * (y_a - y_c);
        int triangle_c = (mouse_y - y_b) * (x_c - x_b) - (mouse_x - x_b) * (y_c - y_b);
        return triangle_a * triangle_c > 0 && triangle_c * triangle_b > 0;
    }

    private void process_culling() {
        int cull = culling_cluster_ptr[plane];
        SceneCluster list[] = culling_cluster[plane];
        processed_culling_cluster_ptr = 0;
        for (int index = 0; index < cull; index++) {
            SceneCluster cluster = list[index];
            if (cluster.search_mask == 1) {
                int distance_from_cam_start_x = (cluster.tile_x - tile_center_x) + render_distance;
                if (distance_from_cam_start_x < 0 || distance_from_cam_start_x > 50)
                    continue;

                int distance_from_cam_start_y = (cluster.tile_y - tile_center_y) + render_distance;
                if (distance_from_cam_start_y < 0)
                    distance_from_cam_start_y = 0;

                int distance_from_cam_end_x = (cluster.tile_height - tile_center_y) + render_distance;
                if (distance_from_cam_end_x > 50)
                    distance_from_cam_end_x = 50;

                boolean visible = false;
                while (distance_from_cam_start_y <= distance_from_cam_end_x) {
                    if (TILE_VISIBILITY_MAP[distance_from_cam_start_x][distance_from_cam_start_y++]) {
                        visible = true;
                        break;
                    }
                }
                if (!visible)
                    continue;

                int angle_x = cam_pos_x - cluster.world_x;
                if (angle_x > 32) {
                    cluster.tile_dist_enum = 1;
                } else {
                    if (angle_x >= -32)
                        continue;

                    cluster.tile_dist_enum = 2;
                    angle_x = -angle_x;
                }
                cluster.world_dist_from_cam_start_y = (cluster.world_y - cam_pos_y << 8) / angle_x;
                cluster.world_dist_from_cam_end_y = (cluster.world_height - cam_pos_y << 8) / angle_x;
                cluster.world_dist_from_cam_start_z = (cluster.world_z - cam_pos_z << 8) / angle_x;
                cluster.world_dist_from_cam_end_z = (cluster.world_depth - cam_pos_z << 8) / angle_x;
                fixed_culling_clusters[processed_culling_cluster_ptr++] = cluster;
                continue;
            }
            if (cluster.search_mask == 2) {
                int distance_from_cam_start_y = (cluster.tile_y - tile_center_y) + render_distance;
                if (distance_from_cam_start_y < 0 || distance_from_cam_start_y > 50)
                    continue;

                int distance_from_cam_start_x = (cluster.tile_x - tile_center_x) + render_distance;
                if (distance_from_cam_start_x < 0)
                    distance_from_cam_start_x = 0;

                int distance_from_cam_end_x = (cluster.tile_width - tile_center_x) + render_distance;
                if (distance_from_cam_end_x > 50)
                    distance_from_cam_end_x = 50;

                boolean visible = false;
                while (distance_from_cam_start_x <= distance_from_cam_end_x) {
                    if (TILE_VISIBILITY_MAP[distance_from_cam_start_x++][distance_from_cam_start_y]) {
                        visible = true;
                        break;
                    }
                }
                if (!visible)
                    continue;

                int distance_from_cam_end_y = cam_pos_y - cluster.world_y;
                if (distance_from_cam_end_y > 32) {
                    cluster.tile_dist_enum = 3;
                } else {
                    if (distance_from_cam_end_y >= -32)
                        continue;

                    cluster.tile_dist_enum = 4;
                    distance_from_cam_end_y = -distance_from_cam_end_y;
                }
                cluster.world_dist_from_cam_start_x = (cluster.world_x - cam_pos_x << 8) / distance_from_cam_end_y;
                cluster.world_dist_from_cam_end_x = (cluster.world_width - cam_pos_x << 8) / distance_from_cam_end_y;
                cluster.world_dist_from_cam_start_z = (cluster.world_z - cam_pos_z << 8) / distance_from_cam_end_y;
                cluster.world_dist_from_cam_end_z = (cluster.world_depth - cam_pos_z << 8) / distance_from_cam_end_y;
                fixed_culling_clusters[processed_culling_cluster_ptr++] = cluster;
            } else if (cluster.search_mask == 4) {
                int distance_from_cam_start_z = cluster.world_z - cam_pos_z;
                if (distance_from_cam_start_z > 128) {
                    int distance_from_cam_start_y = (cluster.tile_y - tile_center_y) + render_distance;
                    if (distance_from_cam_start_y < 0)
                        distance_from_cam_start_y = 0;

                    int distance_from_cam_end_y = (cluster.tile_height - tile_center_y) + render_distance;
                    if (distance_from_cam_end_y > 50)
                        distance_from_cam_end_y = 50;

                    if (distance_from_cam_start_y <= distance_from_cam_end_y) {
                        int distance_from_cam_start_x = (cluster.tile_x - tile_center_x) + render_distance;
                        if (distance_from_cam_start_x < 0)
                            distance_from_cam_start_x = 0;

                        int distance_from_cam_end_x = (cluster.tile_width - tile_center_x) + render_distance;
                        if (distance_from_cam_end_x > 50)
                            distance_from_cam_end_x = 50;

                        boolean visible = false;
                        loop:
                        for (int x = distance_from_cam_start_x; x <= distance_from_cam_end_x; x++) {
                            for (int y = distance_from_cam_start_y; y <= distance_from_cam_end_y; y++) {
                                if (!TILE_VISIBILITY_MAP[x][y])
                                    continue;

                                visible = true;
                                break loop;
                            }
                        }
                        if (visible) {
                            cluster.tile_dist_enum = 5;
                            cluster.world_dist_from_cam_start_x = (cluster.world_x - cam_pos_x << 8) / distance_from_cam_start_z;
                            cluster.world_dist_from_cam_end_x = (cluster.world_width - cam_pos_x << 8) / distance_from_cam_start_z;
                            cluster.world_dist_from_cam_start_y = (cluster.world_y - cam_pos_y << 8) / distance_from_cam_start_z;
                            cluster.world_dist_from_cam_end_y = (cluster.world_height - cam_pos_y << 8) / distance_from_cam_start_z;
                            fixed_culling_clusters[processed_culling_cluster_ptr++] = cluster;
                        }
                    }
                }
            }
        }
    }

    private boolean tile_visible(int z, int x, int y) {
        int l = anIntArrayArrayArray445[z][x][y];
        if (l == -rendered_obj)
            return false;

        if (l == rendered_obj)
            return true;

        int world_x = x << 7;
        int world_y = y << 7;
        if (visible(world_x + 1, height_map[z][x][y], world_y + 1)
            && visible((world_x + 128) - 1, height_map[z][x + 1][y], world_y + 1)
            && visible((world_x + 128) - 1, height_map[z][x + 1][y + 1], (world_y + 128) - 1)
            && visible(world_x + 1, height_map[z][x][y + 1], (world_y + 128) - 1)) {

            anIntArrayArrayArray445[z][x][y] = rendered_obj;
            return true;
        } else {
            anIntArrayArrayArray445[z][x][y] = -rendered_obj;
            return false;
        }
    }

    private boolean wall_visible(int z, int x, int y, int orientation) {
        if (!tile_visible(z, x, y))
            return false;

        int world_x = x << 7;
        int world_y = y << 7;
        int world_z = height_map[z][x][y] - 1;
        int z_offset_120 = world_z - 120;
        int z_offset_230 = world_z - 230;
        int z_offset_238 = world_z - 238;
        if (orientation < 16) {
            if (orientation == 1) {
                if (world_x > cam_pos_x) {
                    if (!visible(world_x, world_z, world_y))
                        return false;
                    if (!visible(world_x, world_z, world_y + 128))
                        return false;
                }
                if (z > 0) {
                    if (!visible(world_x, z_offset_120, world_y))
                        return false;
                    if (!visible(world_x, z_offset_120, world_y + 128))
                        return false;
                }
                return visible(world_x, z_offset_230, world_y) && visible(world_x, z_offset_230, world_y + 128);
            }
            if (orientation == 2) {
                if (world_y < cam_pos_y) {
                    if (!visible(world_x, world_z, world_y + 128))
                        return false;
                    if (!visible(world_x + 128, world_z, world_y + 128))
                        return false;
                }
                if (z > 0) {
                    if (!visible(world_x, z_offset_120, world_y + 128))
                        return false;
                    if (!visible(world_x + 128, z_offset_120, world_y + 128))
                        return false;
                }
                return visible(world_x, z_offset_230, world_y + 128) && visible(world_x + 128, z_offset_230, world_y + 128);
            }
            if (orientation == 4) {
                if (world_x < cam_pos_x) {
                    if (!visible(world_x + 128, world_z, world_y))
                        return false;
                    if (!visible(world_x + 128, world_z, world_y + 128))
                        return false;
                }
                if (z > 0) {
                    if (!visible(world_x + 128, z_offset_120, world_y))
                        return false;
                    if (!visible(world_x + 128, z_offset_120, world_y + 128))
                        return false;
                }
                return visible(world_x + 128, z_offset_230, world_y) && visible(world_x + 128, z_offset_230, world_y + 128);
            }
            if (orientation == 8) {
                if (world_y > cam_pos_y) {
                    if (!visible(world_x, world_z, world_y))
                        return false;
                    if (!visible(world_x + 128, world_z, world_y))
                        return false;
                }
                if (z > 0) {
                    if (!visible(world_x, z_offset_120, world_y))
                        return false;
                    if (!visible(world_x + 128, z_offset_120, world_y))
                        return false;
                }
                return visible(world_x, z_offset_230, world_y) && visible(world_x + 128, z_offset_230, world_y);
            }
        }
        if (!visible(world_x + 64, z_offset_238, world_y + 64))
            return false;

        if (orientation == 16)
            return visible(world_x, z_offset_230, world_y + 128);

        if (orientation == 32)
            return visible(world_x + 128, z_offset_230, world_y + 128);

        if (orientation == 64)
            return visible(world_x + 128, z_offset_230, world_y);

        if (orientation == 128) {
            return visible(world_x, z_offset_230, world_y);
        } else {
            System.out.println("Warning unsupported wall type");
            return true;
        }
    }

    private boolean decor_visible(int z, int x, int y, int model_height) {
        if (!tile_visible(z, x, y))
            return false;

        int world_x = x << 7;
        int world_y = y << 7;
        return visible(world_x + 1, height_map[z][x][y] - model_height, world_y + 1)
            && visible((world_x + 128) - 1, height_map[z][x + 1][y] - model_height, world_y + 1)
            && visible((world_x + 128) - 1, height_map[z][x + 1][y + 1] - model_height, (world_y + 128) - 1)
            && visible(world_x + 1, height_map[z][x][y + 1] - model_height, (world_y + 128) - 1);

    }

    private boolean object_visible(int plane, int left, int right, int top, int bottom, int model_height) {
        if (left == right && top == bottom) {
            if (!tile_visible(plane, left, top))
                return false;

            int world_x = left << 7;
            int world_y = top << 7;
            return visible(world_x + 1, height_map[plane][left][top] - model_height, world_y + 1)
                && visible((world_x + 128) - 1, height_map[plane][left + 1][top] - model_height, world_y + 1)
                && visible((world_x + 128) - 1, height_map[plane][left + 1][top + 1] - model_height, (world_y + 128) - 1)
                && visible(world_x + 1, height_map[plane][left][top + 1] - model_height, (world_y + 128) - 1);
        }
        for (int x = left; x <= right; x++) {
            for (int y = top; y <= bottom; y++)
                if (anIntArrayArrayArray445[plane][x][y] == -rendered_obj)
                    return false;

        }

        int x = (left << 7) + 1;
        int y = (top << 7) + 2;
        int height = height_map[plane][left][top] - model_height;
        if (!visible(x, height, y))
            return false;

        int width = (right << 7) - 1;
        if (!visible(width, height, y))
            return false;

        int z = (bottom << 7) - 1;
        return visible(x, height, z) && visible(width, height, z);
    }

    private boolean visible(int x, int y, int z) {
        for (int index = 0; index < processed_culling_cluster_ptr; index++) {
            SceneCluster cluster = fixed_culling_clusters[index];
            if (cluster.tile_dist_enum == 1) {
                int center_x = cluster.world_x - x;
                if (center_x > 0) {
                    int y_start_diff = cluster.world_y + (cluster.world_dist_from_cam_start_y * center_x >> 8);
                    int y_end_diff = cluster.world_height + (cluster.world_dist_from_cam_end_y * center_x >> 8);
                    int z_start_diff = cluster.world_z + (cluster.world_dist_from_cam_start_z * center_x >> 8);
                    int z_end_diff = cluster.world_depth + (cluster.world_dist_from_cam_end_z * center_x >> 8);
                    if (z >= y_start_diff && z <= y_end_diff && y >= z_start_diff && y <= z_end_diff)
                        return true;
                }
            } else if (cluster.tile_dist_enum == 2) {
                int center_x = x - cluster.world_x;
                if (center_x > 0) {
                    int y_start_diff = cluster.world_y + (cluster.world_dist_from_cam_start_y * center_x >> 8);
                    int y_end_diff = cluster.world_height + (cluster.world_dist_from_cam_end_y * center_x >> 8);
                    int z_start_diff = cluster.world_z + (cluster.world_dist_from_cam_start_z * center_x >> 8);
                    int z_end_diff = cluster.world_depth + (cluster.world_dist_from_cam_end_z * center_x >> 8);
                    if (z >= y_start_diff && z <= y_end_diff && y >= z_start_diff && y <= z_end_diff)
                        return true;
                }
            } else if (cluster.tile_dist_enum == 3) {
                int center_y = cluster.world_y - z;
                if (center_y > 0) {
                    int x_start_diff = cluster.world_x + (cluster.world_dist_from_cam_start_x * center_y >> 8);
                    int x_end_diff = cluster.world_width + (cluster.world_dist_from_cam_end_x * center_y >> 8);
                    int z_start_diff = cluster.world_z + (cluster.world_dist_from_cam_start_z * center_y >> 8);
                    int z_end_diff = cluster.world_depth + (cluster.world_dist_from_cam_end_z * center_y >> 8);
                    if (x >= x_start_diff && x <= x_end_diff && y >= z_start_diff && y <= z_end_diff)
                        return true;
                }
            } else if (cluster.tile_dist_enum == 4) {
                int center_y = z - cluster.world_y;
                if (center_y > 0) {
                    int i3 = cluster.world_x + (cluster.world_dist_from_cam_start_x * center_y >> 8);
                    int j4 = cluster.world_width + (cluster.world_dist_from_cam_end_x * center_y >> 8);
                    int k5 = cluster.world_z + (cluster.world_dist_from_cam_start_z * center_y >> 8);
                    int l6 = cluster.world_depth + (cluster.world_dist_from_cam_end_z * center_y >> 8);
                    if (x >= i3 && x <= j4 && y >= k5 && y <= l6)
                        return true;
                }
            } else if (cluster.tile_dist_enum == 5) {
                int i2 = y - cluster.world_z;
                if (i2 > 0) {
                    int j3 = cluster.world_x + (cluster.world_dist_from_cam_start_x * i2 >> 8);
                    int k4 = cluster.world_width + (cluster.world_dist_from_cam_end_x * i2 >> 8);
                    int l5 = cluster.world_y + (cluster.world_dist_from_cam_start_y * i2 >> 8);
                    int i7 = cluster.world_height + (cluster.world_dist_from_cam_end_y * i2 >> 8);
                    if (x >= j3 && x <= k4 && z >= l5 && z <= i7)
                        return true;
                }
            }
        }
        return false;
    }

    //private boolean aBoolean434;
    public static boolean low_detail = ClientConstants.SCENEGRAPH_LOW_MEMORY;

    private final int map_size_z;
    public static int map_size_x;
    public static int map_size_y;

    public static int[][][] height_map;
    public final Tile[][][] tile_array;

    private int current_pos_z;
    private int interactive_obj_cache_current_pos;
    private final InteractiveObject[] interactive_obj_cache;
    private final int[][][] anIntArrayArrayArray445;//scene_viewport_angle?
    private static int anInt446;//factor of some sort, TODO
    private static int plane;
    private static int rendered_obj;
    private static int current_pos_x;
    private static int region_x;
    private static int current_pos_y;
    private static int region_y;
    private static int tile_center_x;
    private static int tile_center_y;
    private static int cam_pos_x;
    private static int cam_pos_z;
    private static int cam_pos_y;
    private static int sin_y;
    private static int cos_y;
    private static int sin_x;
    private static int cos_x;
    private static InteractiveObject[] interactive_obj = new InteractiveObject[100];
    private static final int[] wall_config_0x100_x = {
        53, -53, -53, 53
    };
    private static final int[] wall_config_0x100_y = {
        -53, -53, 53, 53
    };
    private static final int[] wall_config_0x200_x = {
        -45, 45, 45, -45
    };
    private static final int[] wall_config_0x200_y = {
        45, 45, -45, -45
    };
    private static boolean interacted;
    private static int click_x;
    private static int click_y;
    public static int click_tile_x = -1;
    public static int click_tile_y = -1;
    private static final int culling_index;
    private static int[] culling_cluster_ptr;
    private static SceneCluster[][] culling_cluster;
    private static int processed_culling_cluster_ptr;
    private static final SceneCluster[] fixed_culling_clusters = new SceneCluster[500];
    private static LinkedList tile_list = new LinkedList();

    private static final int[] wall_direction_x = {
        19, 55, 38, 155, 255, 110, 137, 205, 76
    };

    private static final int[] wall_direction_y = {
        160, 192, 80, 96, 0, 144, 80, 48, 160
    };

    private static final int[] wall_direction_z = {
        76, 8, 137, 4, 0, 1, 38, 2, 19
    };

    private static final int[] viewport_camera_left = {
        0, 0, 2, 0, 0, 2, 1, 1, 0
    };

    private static final int[] viewport_camera_right = {
        2, 0, 0, 2, 0, 0, 0, 4, 4
    };

    private static final int[] viewport_camera_top = {
        0, 4, 4, 8, 0, 0, 8, 0, 0
    };

    private static final int[] viewport_camera_bottom = {
        1, 1, 0, 0, 0, 8, 0, 0, 8
    };

    private static final int[] tile_textures = {
        41, 39248, 41, 4643, 41, 41, 41, 41, 41, 41,
        41, 41, 41, 41, 41, 43086, 41, 41, 41, 41,
        41, 41, 41, 8602, 41, 28992, 41, 41, 41, 41,
        41, 5056, 41, 41, 41, 7079, 41, 41, 41, 41,
        41, 41, 41, 41, 41, 41, 3131, 41, 41, 41
    };

    private final int[] merge_a_normals;
    private final int[] merge_b_normals;
    private int merge_normals_index;
    private final int[][] tile_points = { //TODO
        new int[16],
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 0, 0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 1, 1, 1, 1},
        {1, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0},
        {0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1},
        {0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0},
        {1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 1, 1},
        {1, 1, 1, 1, 1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1}
    };
    private final int[][] tile_indices = { //TODO
        {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15},
        {12, 8, 4, 0, 13, 9, 5, 1, 14, 10, 6, 2, 15, 11, 7, 3},
        {15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0},
        {3, 7, 11, 15, 2, 6, 10, 14, 1, 5, 9, 13, 0, 4, 8, 12}
    };

    public static final int MAXIMUM_RENDER_DISTANCE = 25;
    public static final int MINIMUM_RENDER_DISTANCE = 10;
    public static int render_distance = MAXIMUM_RENDER_DISTANCE; //When changing render distance, make sure to reset viewport.
    public static boolean render_ground_decorations = true;
    public static boolean render_wall_decorations = true;

    private static boolean[][][][] TILE_VISIBILITY_MAPS = new boolean[9][32][(MAXIMUM_RENDER_DISTANCE * 2) + 1][(MAXIMUM_RENDER_DISTANCE * 2) + 1];

    private static boolean[][] TILE_VISIBILITY_MAP;
    private static int center_x;
    private static int center_y;
    private static int left;
    private static int top;
    private static int right;
    private static int bottom;
    public static int view_dist = 9;//rename

    public static int getCam_pos_x() {
        return cam_pos_x;
    }

    public static int getCam_pos_z() {
        return cam_pos_z;
    }

    public static int getCam_pos_y() {
        return cam_pos_y;
    }

    public static int getLeft() {
        return left;
    }

    public static void setLeft(int left) {
        SceneGraph.left = left;
    }

    public static int getTop() {
        return top;
    }

    public static void setTop(int top) {
        SceneGraph.top = top;
    }

    public static int getRight() {
        return right;
    }

    public static void setRight(int right) {
        SceneGraph.right = right;
    }

    public static int getBottom() {
        return bottom;
    }

    public static void setBottom(int bottom) {
        SceneGraph.bottom = bottom;
    }

    static {
        culling_index = 4;
        culling_cluster_ptr = new int[culling_index];
        culling_cluster = new SceneCluster[culling_index][500];
    }
}
