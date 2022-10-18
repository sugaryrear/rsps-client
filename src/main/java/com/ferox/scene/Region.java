package com.ferox.scene;

import com.ferox.Client;
import com.ferox.ClientConstants;
import com.ferox.cache.def.FloDefinition;
import com.ferox.cache.def.ObjectDefinition;
import com.ferox.draw.Rasterizer3D;
import com.ferox.entity.Renderable;
import com.ferox.entity.model.Model;
import com.ferox.io.Buffer;
import com.ferox.net.requester.ResourceProvider;
import com.ferox.util.ChunkUtil;

public final class Region {

    static int hue_offset = (int) (Math.random() * 17.0D) - 8;
    static int lightness_offset = (int) (Math.random() * 33.0D) - 16;

    private final int[] blended_hue;
    private final int[] blended_saturation;
    private final int[] blended_lightness;
    private final int[] blended_hue_factor;
    private final int[] blend_direction_tracker;
    private final int[][][] vertex_heights;
    private final byte[][][] overlay_floor_id;
    public static int plane;
    private final byte[][][] tile_shadow_intensity;
    private final int[][][] tile_culling_bitsets;
    private final byte[][][] overlay_clipping_paths;
    private static final int decor_x_offsets[] = {
        1, 0, -1, 0
    };
    private final int[][] tile_light_intensity;
    private static final int wall_orientations[] = {
        16, 32, 64, 128
    };
    private final byte[][][] underlay_floor_id;
    private static final int decor_y_offsets[] = {
        0, -1, 0, 1
    };
    public static int min_plane = 99;
    private final int region_size_x;
    private final int region_size_y;
    private final byte[][][] overlay_clipping_path_rotations;
    private final byte[][][] tile_flags;
    public static boolean low_detail = ClientConstants.MAPREGION_LOW_MEMORY;
    private static final int wall_corner_orientations[] = {
        1, 2, 4, 8
    };

    private static final int BLOCKED_TILE = 1;
    public static final int BRIDGE_TILE = 2;
    private static final int FORCE_LOWEST_PLANE = 8;

    public Region(byte flags[][][], int heights[][][]) {
        min_plane = 99;
        region_size_x = 104;
        region_size_y = 104;
        vertex_heights = heights;
        tile_flags = flags;
        underlay_floor_id = new byte[4][region_size_x][region_size_y];
        overlay_floor_id = new byte[4][region_size_x][region_size_y];
        overlay_clipping_paths = new byte[4][region_size_x][region_size_y];
        overlay_clipping_path_rotations = new byte[4][region_size_x][region_size_y];
        tile_culling_bitsets = new int[4][region_size_x + 1][region_size_y + 1];
        tile_shadow_intensity = new byte[4][region_size_x + 1][region_size_y + 1];
        tile_light_intensity = new int[region_size_x + 1][region_size_y + 1];
        blended_hue = new int[region_size_y];
        blended_saturation = new int[region_size_y];
        blended_lightness = new int[region_size_y];
        blended_hue_factor = new int[region_size_y];
        blend_direction_tracker = new int[region_size_y];
    }

    private static int perlin(int x, int y) {
        int k = x + y * 57;
        k = k << 13 ^ k;
        int noise = k * (k * k * 15731 + 0xc0ae5) + 0x5208dd0d & 0x7fffffff;
        return noise >> 19 & 0xff;
    }

    public final void create_region(CollisionMap clipping_map[], SceneGraph scene) {
        for (int plane = 0; plane < 4; plane++) {
            for (int x = 0; x < 104; x++) {
                for (int y = 0; y < 104; y++)
                    if ((tile_flags[plane][x][y] & BLOCKED_TILE) == 1) {
                        int marking_plane = plane;
                        if ((tile_flags[1][x][y] & BRIDGE_TILE) == 2)
                            marking_plane--;

                        if (marking_plane >= 0)
                            clipping_map[marking_plane].set_solid_flag(x, y);
                    }

            }
        }
        for (int plane = 0; plane < 4; plane++) {
            byte shadow_intensity[][] = tile_shadow_intensity[plane];
            byte directional_light_initial_intensity = 96;
            char specular_distribution_factor = '\u0300';//768
            byte directional_light_x = -50;
            byte directional_light_y = -10;
            byte directional_light_z = -50;
            int directional_light_length = (int) Math.sqrt(directional_light_x * directional_light_x + directional_light_y * directional_light_y + directional_light_z * directional_light_z);
            int specular_distribution = specular_distribution_factor * directional_light_length >> 8;
            for (int y = 1; y < region_size_y - 1; y++) {
                for (int x = 1; x < region_size_x - 1; x++) {
                    int x_height_difference = vertex_heights[plane][x + 1][y] - vertex_heights[plane][x - 1][y];
                    int y_height_difference = vertex_heights[plane][x][y + 1] - vertex_heights[plane][x][y - 1];
                    int normal_length = (int) Math.sqrt(x_height_difference * x_height_difference + 0x10000 + y_height_difference * y_height_difference);
                    int normalized_normal_x = (x_height_difference << 8) / normal_length;
                    int normalized_normal_y = 0x10000 / normal_length;
                    int normalized_normal_z = (y_height_difference << 8) / normal_length;
                    int directional_light_intensity = directional_light_initial_intensity + (directional_light_x * normalized_normal_x + directional_light_y * normalized_normal_y + directional_light_z * normalized_normal_z) / specular_distribution;
                    int weighted_shadow_intensity = (shadow_intensity[x - 1][y] >> 2) + (shadow_intensity[x + 1][y] >> 3) + (shadow_intensity[x][y - 1] >> 2) + (shadow_intensity[x][y + 1] >> 3) + (shadow_intensity[x][y] >> 1);
                    tile_light_intensity[x][y] = directional_light_intensity - weighted_shadow_intensity;
                }
            }
            for (int y = 0; y < region_size_y; y++) {
                blended_hue[y] = 0;
                blended_saturation[y] = 0;
                blended_lightness[y] = 0;
                blended_hue_factor[y] = 0;
                blend_direction_tracker[y] = 0;
            }
            for (int x = -5; x < region_size_x + 5; x++) {
                for (int y = 0; y < region_size_y; y++) {
                    int x_positive_offset = x + 5;
                    if (x_positive_offset >= 0 && x_positive_offset < region_size_x) {
                        int floor_id = underlay_floor_id[plane][x_positive_offset][y] & 0xff;
                        if (floor_id > 0) {
                            FloDefinition def = FloDefinition.underlay[floor_id - 1];
                            blended_hue[y] += def.weighted_hue;
                            blended_saturation[y] += def.underlay_saturation;
                            blended_lightness[y] += def.underlay_luminance;
                            blended_hue_factor[y] += def.chroma;
                            blend_direction_tracker[y]++;
                        }
                    }
                    int x_negative_offset = x - 5;
                    if (x_negative_offset >= 0 && x_negative_offset < region_size_x) {
                        int floor_id = underlay_floor_id[plane][x_negative_offset][y] & 0xff;
                        if (floor_id > 0) {
                            FloDefinition def = FloDefinition.underlay[floor_id - 1];
                            blended_hue[y] -= def.weighted_hue;
                            blended_saturation[y] -= def.underlay_saturation;
                            blended_lightness[y] -= def.underlay_luminance;
                            blended_hue_factor[y] -= def.chroma;
                            blend_direction_tracker[y]--;
                        }
                    }
                }

                if (x >= 1 && x < region_size_x - 1) {
                    int blend_hue = 0;
                    int blend_saturation = 0;
                    int blend_lightness = 0;
                    int blend_hue_factor = 0;
                    int blended_tracker = 0;
                    for (int y = -5; y < region_size_y + 5; y++) {
                        int y_positive_offset = y + 5;
                        if (y_positive_offset >= 0 && y_positive_offset < region_size_y) {
                            blend_hue += blended_hue[y_positive_offset];
                            blend_saturation += blended_saturation[y_positive_offset];
                            blend_lightness += blended_lightness[y_positive_offset];
                            blend_hue_factor += blended_hue_factor[y_positive_offset];
                            blended_tracker += blend_direction_tracker[y_positive_offset];
                        }
                        int y_negative_offset = y - 5;
                        if (y_negative_offset >= 0 && y_negative_offset < region_size_y) {
                            blend_hue -= blended_hue[y_negative_offset];
                            blend_saturation -= blended_saturation[y_negative_offset];
                            blend_lightness -= blended_lightness[y_negative_offset];
                            blend_hue_factor -= blended_hue_factor[y_negative_offset];
                            blended_tracker -= blend_direction_tracker[y_negative_offset];
                        }
                        if (y >= 1 && y < region_size_y - 1 && (!low_detail || (tile_flags[0][x][y] & 2) != 0 || (tile_flags[plane][x][y] & 0x10) == 0 && get_visibility(y, plane, x) == this.plane)) {
                            if (plane < min_plane)
                                min_plane = plane;

                            int underlay_id = underlay_floor_id[plane][x][y] & 0xff;
                            int overlay_id = overlay_floor_id[plane][x][y] & 0xff;
                            if (underlay_id > 0 || overlay_id > 0) {
                                int v_sw = vertex_heights[plane][x][y];
                                int v_se = vertex_heights[plane][x + 1][y];
                                int v_ne = vertex_heights[plane][x + 1][y + 1];
                                int v_nw = vertex_heights[plane][x][y + 1];
                                int l_sw = tile_light_intensity[x][y];
                                int l_se = tile_light_intensity[x + 1][y];
                                int l_ne = tile_light_intensity[x + 1][y + 1];
                                int l_nw = tile_light_intensity[x][y + 1];
                                int hsl_bitset_unmodified = -1;
                                int hsl_bitset_randomized = -1;
                                if (underlay_id > 0) {
                                    int hue = (blend_hue * 256) / blend_hue_factor;
                                    int sat = blend_saturation / blended_tracker;
                                    int lum = blend_lightness / blended_tracker;
                                    hsl_bitset_unmodified = encode_hsl(hue, sat, lum);
                                    if (lum < 0)
                                        lum = 0;
                                    else if (lum > 255)
                                        lum = 255;

                                    hsl_bitset_randomized = encode_hsl(hue, sat, lum);
                                }
                                if (plane > 0) {
                                    boolean hide_underlay = true;
                                    if (underlay_id == 0 && overlay_clipping_paths[plane][x][y] != 0)
                                        hide_underlay = false;

                                    if (overlay_id > 0 && !FloDefinition.overlay[overlay_id - 1].hidden)
                                        hide_underlay = false;

                                    if (hide_underlay && v_sw == v_se && v_sw == v_ne && v_sw == v_nw)
                                        tile_culling_bitsets[plane][x][y] |= 0x924;

                                }
                                int rgb_bitset_randomized = 0;
                                if (hsl_bitset_unmodified != -1)
                                    rgb_bitset_randomized = Rasterizer3D.HSL_TO_RGB[encode_hsl(hsl_bitset_randomized, 96)];

                                if (overlay_id == 0) {
                                    scene.add_tile(
                                        plane, x, y, 0, 0, -1, v_sw, v_se, v_ne, v_nw,
                                        encode_hsl(hsl_bitset_unmodified, l_sw), encode_hsl(hsl_bitset_unmodified, l_se),
                                        encode_hsl(hsl_bitset_unmodified, l_ne), encode_hsl(hsl_bitset_unmodified, l_nw),
                                        0, 0, 0, 0,
                                        rgb_bitset_randomized, 0);

                                } else {
                                    int shape = overlay_clipping_paths[plane][x][y] + 1;
                                    byte rotation = overlay_clipping_path_rotations[plane][x][y];
                                    if ((overlay_id - 1) > FloDefinition.overlay.length)
                                        overlay_id = FloDefinition.overlay.length;

                                    FloDefinition def = FloDefinition.overlay[overlay_id - 1];
                                    int overlay_texture_id = def.texture;
                                    int overlay_hsl;
                                    int minimap_color;

                                    if (overlay_texture_id >= 0) {
                                        minimap_color = Rasterizer3D.set_floor(overlay_texture_id);
                                        if (low_detail)
                                            overlay_hsl = -1;
                                        else
                                            overlay_hsl = 127;

                                    } else if (def.minimap_underlay == 0xff00ff) {
                                        minimap_color = 0;
                                        overlay_hsl = -2;
                                        overlay_texture_id = -1;
                                    } else if (def.minimap_underlay == 0x333333) {
                                        minimap_color = Rasterizer3D.HSL_TO_RGB[encode_signed_hsl(def.color, 96)];
                                        overlay_hsl = -2;
                                        overlay_texture_id = -1;
                                    } else {
                                        overlay_hsl = encode_hsl(def.underlay_hue, def.underlay_saturation, def.underlay_luminance);
                                        minimap_color = Rasterizer3D.HSL_TO_RGB[encode_signed_hsl(def.color, 96)];
                                    }
                                    if (minimap_color == 0x000000 && def.minimap_overlay != -1) {
                                        minimap_color = Rasterizer3D.HSL_TO_RGB[encode_signed_hsl(encode_hsl(def.overlay_hue, def.overlay_saturation, def.overlay_luminance), 96)];
                                    }
                                    scene.add_tile(
                                        plane, x, y, shape, rotation, overlay_texture_id, v_sw, v_se, v_ne, v_nw,
                                        encode_hsl(hsl_bitset_unmodified, l_sw), encode_hsl(hsl_bitset_unmodified, l_se),
                                        encode_hsl(hsl_bitset_unmodified, l_ne), encode_hsl(hsl_bitset_unmodified, l_nw),
                                        encode_signed_hsl(overlay_hsl, l_sw), encode_signed_hsl(overlay_hsl, l_se),
                                        encode_signed_hsl(overlay_hsl, l_ne), encode_signed_hsl(overlay_hsl, l_nw),
                                        rgb_bitset_randomized, minimap_color);
                                }
                            }
                        }
                    }

                }
            }
            for (int y = 1; y < region_size_y - 1; y++) {
                for (int x = 1; x < region_size_x - 1; x++)
                    scene.set_visible_planes(plane, x, y, get_visibility(y, plane, x));

            }
        }
        scene.flat_lighting(-10, -50, -50);
        for (int y = 0; y < region_size_x; y++) {
            for (int x = 0; x < region_size_y; x++)
                if ((tile_flags[1][y][x] & 2) == 2)
                    scene.create_bridge(x, y);

        }
        int flag_0x8 = 1;
        int flag_0x10 = 2;
        int flag_0x20 = 4;
        for (int cur_plane = 0; cur_plane < 4; cur_plane++) {
            if (cur_plane > 0) {
                flag_0x8 <<= 3;
                flag_0x10 <<= 3;
                flag_0x20 <<= 3;
            }
            for (int plane = 0; plane <= cur_plane; plane++) {
                for (int y = 0; y <= region_size_y; y++) {
                    for (int x = 0; x <= region_size_x; x++) {
                        if ((tile_culling_bitsets[plane][x][y] & flag_0x8) != 0) {
                            int lowest_occlusion_y = y;
                            int highest_occlusion_y = y;
                            int lowest_occlusion_plane = plane;
                            int highest_occlusion_plane = plane;
                            for (; lowest_occlusion_y > 0 && (tile_culling_bitsets[plane][x][lowest_occlusion_y - 1] & flag_0x8) != 0; lowest_occlusion_y--)
                                ;
                            for (; highest_occlusion_y < region_size_y && (tile_culling_bitsets[plane][x][highest_occlusion_y + 1] & flag_0x8) != 0; highest_occlusion_y++)
                                ;
                            find_lowest_occlusion_plane:
                            for (; lowest_occlusion_plane > 0; lowest_occlusion_plane--) {
                                for (int occluded_y = lowest_occlusion_y; occluded_y <= highest_occlusion_y; occluded_y++)
                                    if ((tile_culling_bitsets[lowest_occlusion_plane - 1][x][occluded_y] & flag_0x8) == 0)
                                        break find_lowest_occlusion_plane;

                            }
                            find_highest_occlusion_plane:
                            for (; highest_occlusion_plane < cur_plane; highest_occlusion_plane++) {
                                for (int occluded_y = lowest_occlusion_y; occluded_y <= highest_occlusion_y; occluded_y++)
                                    if ((tile_culling_bitsets[highest_occlusion_plane + 1][x][occluded_y] & flag_0x8) == 0)
                                        break find_highest_occlusion_plane;

                            }
                            int occlusion_surface = ((highest_occlusion_plane + 1) - lowest_occlusion_plane) * ((highest_occlusion_y - lowest_occlusion_y) + 1);
                            if (occlusion_surface >= 8) {
                                char c1 = '\360';
                                int highest_occlusion_vertex_height = vertex_heights[highest_occlusion_plane][x][lowest_occlusion_y] - c1;
                                int lowest_occlusion_vertex_height = vertex_heights[lowest_occlusion_plane][x][lowest_occlusion_y];
                                SceneGraph.create_culling_occlusion_box(cur_plane, x * 128, lowest_occlusion_vertex_height, x * 128, highest_occlusion_y * 128 + 128, highest_occlusion_vertex_height, lowest_occlusion_y * 128, 1);
                                for (int occluded_plane = lowest_occlusion_plane; occluded_plane <= highest_occlusion_plane; occluded_plane++) {
                                    for (int occluded_y = lowest_occlusion_y; occluded_y <= highest_occlusion_y; occluded_y++)
                                        tile_culling_bitsets[occluded_plane][x][occluded_y] &= ~flag_0x8;

                                }
                            }
                        }
                        if ((tile_culling_bitsets[plane][x][y] & flag_0x10) != 0) {
                            int lowest_occlusion_x = x;
                            int highest_occlusion_x = x;
                            int lowest_occlusion_plane = plane;
                            int highest_occlusion_plane = plane;
                            for (; lowest_occlusion_x > 0 && (tile_culling_bitsets[plane][lowest_occlusion_x - 1][y] & flag_0x10) != 0; lowest_occlusion_x--)
                                ;
                            for (; highest_occlusion_x < region_size_x && (tile_culling_bitsets[plane][highest_occlusion_x + 1][y] & flag_0x10) != 0; highest_occlusion_x++)
                                ;
                            find_lowest_occlussion_plane:
                            for (; lowest_occlusion_plane > 0; lowest_occlusion_plane--) {
                                for (int occluded_x = lowest_occlusion_x; occluded_x <= highest_occlusion_x; occluded_x++)
                                    if ((tile_culling_bitsets[lowest_occlusion_plane - 1][occluded_x][y] & flag_0x10) == 0)
                                        break find_lowest_occlussion_plane;

                            }
                            find_highest_occlussion_plane:
                            for (; highest_occlusion_plane < cur_plane; highest_occlusion_plane++) {
                                for (int occluded_x = lowest_occlusion_x; occluded_x <= highest_occlusion_x; occluded_x++)
                                    if ((tile_culling_bitsets[highest_occlusion_plane + 1][occluded_x][y] & flag_0x10) == 0)
                                        break find_highest_occlussion_plane;

                            }
                            int occlussion_surface = ((highest_occlusion_plane + 1) - lowest_occlusion_plane) * ((highest_occlusion_x - lowest_occlusion_x) + 1);
                            if (occlussion_surface >= 8) {
                                char c2 = '\360';
                                int highest_occlussion_vertex_height = vertex_heights[highest_occlusion_plane][lowest_occlusion_x][y] - c2;
                                int lowest_occlussion_vertex_height = vertex_heights[lowest_occlusion_plane][lowest_occlusion_x][y];
                                SceneGraph.create_culling_occlusion_box(cur_plane, lowest_occlusion_x * 128, lowest_occlussion_vertex_height, highest_occlusion_x * 128 + 128, y * 128, highest_occlussion_vertex_height, y * 128, 2);
                                for (int occluded_plane = lowest_occlusion_plane; occluded_plane <= highest_occlusion_plane; occluded_plane++) {
                                    for (int occluded_x = lowest_occlusion_x; occluded_x <= highest_occlusion_x; occluded_x++)
                                        tile_culling_bitsets[occluded_plane][occluded_x][y] &= ~flag_0x10;

                                }
                            }
                        }
                        if ((tile_culling_bitsets[plane][x][y] & flag_0x20) != 0) {
                            int lowest_occlusion_x = x;
                            int highest_occlusion_x = x;
                            int lowest_occlusion_y = y;
                            int highest_occlusion_y = y;
                            for (; lowest_occlusion_y > 0 && (tile_culling_bitsets[plane][x][lowest_occlusion_y - 1] & flag_0x20) != 0; lowest_occlusion_y--)
                                ;
                            for (; highest_occlusion_y < region_size_y && (tile_culling_bitsets[plane][x][highest_occlusion_y + 1] & flag_0x20) != 0; highest_occlusion_y++)
                                ;
                            find_lowest_occlussion_x:
                            for (; lowest_occlusion_x > 0; lowest_occlusion_x--) {
                                for (int occluded_y = lowest_occlusion_y; occluded_y <= highest_occlusion_y; occluded_y++)
                                    if ((tile_culling_bitsets[plane][lowest_occlusion_x - 1][occluded_y] & flag_0x20) == 0)
                                        break find_lowest_occlussion_x;

                            }
                            find_highest_occlussion_x:
                            for (; highest_occlusion_x < region_size_x; highest_occlusion_x++) {
                                for (int occluded_y = lowest_occlusion_y; occluded_y <= highest_occlusion_y; occluded_y++)
                                    if ((tile_culling_bitsets[plane][highest_occlusion_x + 1][occluded_y] & flag_0x20) == 0)
                                        break find_highest_occlussion_x;

                            }
                            if (((highest_occlusion_x - lowest_occlusion_x) + 1) * ((highest_occlusion_y - lowest_occlusion_y) + 1) >= 4) {
                                int lowest_occlussion_vertex_height = vertex_heights[plane][lowest_occlusion_x][lowest_occlusion_y];
                                SceneGraph.create_culling_occlusion_box(cur_plane, lowest_occlusion_x * 128, lowest_occlussion_vertex_height, highest_occlusion_x * 128 + 128, highest_occlusion_y * 128 + 128, lowest_occlussion_vertex_height, lowest_occlusion_y * 128, 4);
                                for (int occluded_x = lowest_occlusion_x; occluded_x <= highest_occlusion_x; occluded_x++) {
                                    for (int occluded_y = lowest_occlusion_y; occluded_y <= highest_occlusion_y; occluded_y++)
                                        tile_culling_bitsets[plane][occluded_x][occluded_y] &= ~flag_0x20;

                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static int calc_heights(int x, int y) {
        int height = (interpolated_noise(x + 45365, y + 0x16713, 4) - 128)
            + (interpolated_noise(x + 10294, y + 37821, 2) - 128 >> 1)
            + (interpolated_noise(x, y, 1) - 128 >> 2);
        height = (int) ((double) height * 0.29999999999999999D) + 35;
        if (height < 10)
            height = 10;
        else if (height > 60)
            height = 60;

        return height;
    }

    public static void passive_request_obj_models(Buffer buffer, ResourceProvider provider) {
        load:
        {
            int object_id = -1;
            do {
                int id_offset = buffer.readUSmart2();
                if (id_offset == 0)
                    break load;

                object_id += id_offset;
                ObjectDefinition def = ObjectDefinition.get(object_id);
                def.passive_request_load(provider);
                do {
                    int terminate = buffer.readUSmart();
                    if (terminate == 0)
                        break;

                    buffer.readUByte();
                } while (true);
            } while (true);
        }
    }

    public final void set_vertex_heights(int y, int height, int width, int x) {
        for (int y_pos = y; y_pos <= y + height; y_pos++) {
            for (int x_pos = x; x_pos <= x + width; x_pos++)
                if (x_pos >= 0 && x_pos < region_size_x && y_pos >= 0 && y_pos < region_size_y) {
                    tile_shadow_intensity[0][x_pos][y_pos] = 127;
                    if (x_pos == x && x_pos > 0)
                        vertex_heights[0][x_pos][y_pos] = vertex_heights[0][x_pos - 1][y_pos];

                    if (x_pos == x + width && x_pos < region_size_x - 1)
                        vertex_heights[0][x_pos][y_pos] = vertex_heights[0][x_pos + 1][y_pos];

                    if (y_pos == y && y_pos > 0)
                        vertex_heights[0][x_pos][y_pos] = vertex_heights[0][x_pos][y_pos - 1];

                    if (y_pos == y + height && y_pos < region_size_y - 1)
                        vertex_heights[0][x_pos][y_pos] = vertex_heights[0][x_pos][y_pos + 1];
                }

        }
    }


    /*
     * OBJECT TYPES:
            0    - straight walls, fences etc
            1    - diagonal walls corner, fences etc connectors
            2    - entire walls, fences etc corners
            3    - straight wall corners, fences etc connectors
            4    - straight inside wall decoration
            5    - straight outside wall decoration
            6    - diagonal outside wall decoration
            7    - diagonal inside wall decoration
            8    - diagonal in wall decoration
            9    - diagonal walls, fences etc
            10    - all kinds of objects, trees, statues, signs, fountains etc etc
            11    - ground objects like daisies etc
            12    - straight sloped roofs
            13    - diagonal sloped roofs
            14    - diagonal slope connecting roofs
            15    - straight sloped corner connecting roofs
            16    - straight sloped corner roof
            17    - straight flat top roofs
            18    - straight bottom edge roofs
            19    - diagonal bottom edge connecting roofs
            20    - straight bottom edge connecting roofs
            21    - straight bottom edge connecting corner roofs
            22    - ground decoration + map signs (quests, water fountains, shops etc)
     */
    private void render(int y, SceneGraph scene, CollisionMap map, int type, int plane, int x, int object_id, int orientation) {
        //if(plane >= 0 && plane < 4) {//added for higher revisions
        if (low_detail && (tile_flags[0][x][y] & BRIDGE_TILE) == 0) {
            if ((tile_flags[plane][x][y] & 0x10) != 0)
                return;

            if (get_visibility(y, plane, x) != this.plane)
                return;
        }
        //}
        if (plane < min_plane)
            min_plane = plane;

        ObjectDefinition def = ObjectDefinition.get(object_id);
        int obj_size_offset_a;
        int obj_size_offset_b;
        if (orientation == 1 || orientation == 3) {
            obj_size_offset_a = def.height;
            obj_size_offset_b = def.width;
        } else {
            obj_size_offset_a = def.width;
            obj_size_offset_b = def.height;
        }

        int obj_x_factor_a;
        int obj_x_factor_b;
        if (104 >= (obj_size_offset_a + x)) {
            obj_x_factor_b = x + (obj_size_offset_a + 1 >> 1);
            obj_x_factor_a = x + (obj_size_offset_a >> 1);
        } else {
            obj_x_factor_a = x;
            obj_x_factor_b = x + 1;
        }

        int obj_y_factor_a;
        int obj_y_factor_b;
        if (104 >= (obj_size_offset_b + y)) {
            obj_y_factor_b = y + (obj_size_offset_b + 1 >> 1);
            obj_y_factor_a = (obj_size_offset_b >> 1) + y;
        } else {
            obj_y_factor_a = y;
            obj_y_factor_b = 1 + y;
        }
        int v_sw = vertex_heights[plane][obj_x_factor_a][obj_y_factor_a];
        int v_se = vertex_heights[plane][obj_x_factor_b][obj_y_factor_a];
        int v_ne = vertex_heights[plane][obj_x_factor_b][obj_y_factor_b];
        int v_nw = vertex_heights[plane][obj_x_factor_a][obj_y_factor_b];

        int avg = v_sw + v_se + v_ne + v_nw >> 2;

        int mX = Client.singleton.region_x - 6;
        int mY = Client.singleton.region_y - 6;
        int actualX = mX * 8 + x;
        int actualY = mY * 8 + y;

        /**
         * Prevent objects from being drawn below.
         * It can be any type of object, including walls.
         */

        if(object_id == 1391 && actualX == 3099 && actualY == 3493) {
            return;
        }

        if(object_id == 1123 && actualX == 3099 && actualY == 3491) {
            return;
        }

        //House portal
        if (actualX == 2031 && actualY == 3568) {
            return;
        }

        //Pest control doors, they never worked quite right because of instancing.
        if (actualX == 2670 && actualY == 2593) {
            return;
        }
        if (actualX == 2670 && actualY == 2592) {
            return;
        }
        if (actualX == 2657 && actualY == 2585) {
            return;
        }
        if (actualX == 2656 && actualY == 2585) {
            return;
        }
        if (actualX == 2643 && actualY == 2592) {
            return;
        }
        if (actualX == 2643 && actualY == 2593) {
            return;
        }

        if (plane == 0) {

            //Wildy altar doors
            if (actualX == 2958 && actualY == 3821) {
                return;
            }

            if (actualX == 2958 && actualY == 3820) {
                return;
            }

            //Wildy stairs cave gate 1
            if (actualX == 3040 && actualY == 10307) {
                return;
            }
            if (actualX == 3040 && actualY == 10308) {
                return;
            }

            //Wildy stairs cave gate 2
            if (actualX == 3022 && actualY == 10311) {
                return;
            }
            if (actualX == 3022 && actualY == 10312) {
                return;
            }

            //Wildy stairs cave gate 3
            if (actualX == 3044 && actualY == 10341) {
                return;
            }

            if (actualX == 3044 && actualY == 10342) {
                return;
            }
        }

        long key = (long) (orientation << 20 | type << 14 | (y << 7 | x) + 0x40000000);

        //object - wilderness ditch | example
        //value    //1073916122            //key start                            (original)
        //1078110426            //key |= 0x400000L;                    (key + 4194304)
        //99949262055642         //key |= (long) object_id << 32        (key + (object_id << 32))
        //                        //99949262055642 >> 32 == 23271        (key >> 32 == object_id)

        if (def.interact_state == 0) {
            key |= ~0x7fffffffffffffffL;
        }
        if (def.merge_interact_state == 1) {
            key |= 0x400000L;
        }
        key |= (long) object_id << 32;

        //System.out.println("weeee "+object_id+" and type "+type);

        if (type == 22) {
            if (low_detail && def.interact_state == 0 && !def.obstructs_ground) {
                return;
            }
            Object ground_map_scene_decor;
            if (def.animation == -1 && def.configs == null)
                ground_map_scene_decor = def.get_object(22, orientation, v_sw, v_se, v_ne, v_nw, -1);
            else
                ground_map_scene_decor = new SceneObject(object_id, orientation, 22, v_se, v_ne, v_sw, v_nw, def.animation, true);

            scene.add_ground_decor(plane, avg, y, ((Renderable) (ground_map_scene_decor)), key, x);
            if (def.solid && def.interact_state == 1 && map != null)
                map.set_solid_flag(x, y);

            return;
        }
        if (type == 10 || type == 11) {
            Object scene_objects;
            if (def.animation == -1 && def.configs == null)
                scene_objects = def.get_object(10, orientation, v_sw, v_se, v_ne, v_nw, -1);
            else
                scene_objects = new SceneObject(object_id, orientation, 10, v_se, v_ne, v_sw, v_nw, def.animation, true);

            if (scene_objects != null) {
                int obj_rotation = 0;
                if (type == 11)
                    obj_rotation += 256;

                int size_offset_a;
                int size_offset_b;
                if (orientation == 1 || orientation == 3) {
                    size_offset_a = def.height;
                    size_offset_b = def.width;
                } else {
                    size_offset_a = def.width;
                    size_offset_b = def.height;
                }
                if (scene.add_entity(key, avg, size_offset_b, ((Renderable) (scene_objects)), size_offset_a, plane, obj_rotation, y, x) && def.cast_shadow) {
                    Model model;
                    if (scene_objects instanceof Model)
                        model = (Model) scene_objects;
                    else
                        model = def.get_object(10, orientation, v_sw, v_se, v_ne, v_nw, -1);

                    if (model != null) {
                        for (int x_factor = 0; x_factor <= size_offset_a; x_factor++) {
                            for (int y_factor = 0; y_factor <= size_offset_b; y_factor++) {
                                int shadow = model.diagonal_2D / 4;
                                if (shadow > 30)
                                    shadow = 30;

                                if (shadow > tile_shadow_intensity[plane][x + x_factor][y + y_factor])
                                    tile_shadow_intensity[plane][x + x_factor][y + y_factor] = (byte) shadow;
                            }
                        }
                    }
                }
            }
            if (def.solid && map != null)
                map.mark_interactive_obj(def.walkable, def.width, def.height, x, y, orientation);

            return;
        }
        if (type >= 12) {
            Object roofs;
            if (def.animation == -1 && def.configs == null)
                roofs = def.get_object(type, orientation, v_sw, v_se, v_ne, v_nw, -1);
            else
                roofs = new SceneObject(object_id, orientation, type, v_se, v_ne, v_sw, v_nw, def.animation, true);

            scene.add_entity(key, avg, 1, ((Renderable) (roofs)), 1, plane, 0, y, x);
            if (type >= 12 && type <= 17 && type != 13 && plane > 0)
                tile_culling_bitsets[plane][x][y] |= 0x924;

            if (def.solid && map != null)
                map.mark_interactive_obj(def.walkable, def.width, def.height, x, y, orientation);

            return;
        }
        if (type == 0) {
            Object straight_wall;
            if (def.animation == -1 && def.configs == null)
                straight_wall = def.get_object(0, orientation, v_sw, v_se, v_ne, v_nw, -1);
            else
                straight_wall = new SceneObject(object_id, orientation, 0, v_se, v_ne, v_sw, v_nw, def.animation, true);

            scene.add_wall(wall_corner_orientations[orientation], ((Renderable) (straight_wall)), key, y, x, null, avg, 0, plane);
            if (orientation == 0) {
                if (def.cast_shadow) {
                    tile_shadow_intensity[plane][x][y] = 50;
                    tile_shadow_intensity[plane][x][y + 1] = 50;
                }
                if (def.occlude)
                    tile_culling_bitsets[plane][x][y] |= 0x249;

            } else if (orientation == 1) {
                if (def.cast_shadow) {
                    tile_shadow_intensity[plane][x][y + 1] = 50;
                    tile_shadow_intensity[plane][x + 1][y + 1] = 50;
                }
                if (def.occlude)
                    tile_culling_bitsets[plane][x][y + 1] |= 0x492;

            } else if (orientation == 2) {
                if (def.cast_shadow) {
                    tile_shadow_intensity[plane][x + 1][y] = 50;
                    tile_shadow_intensity[plane][x + 1][y + 1] = 50;
                }
                if (def.occlude)
                    tile_culling_bitsets[plane][x + 1][y] |= 0x249;

            } else if (orientation == 3) {
                if (def.cast_shadow) {
                    tile_shadow_intensity[plane][x][y] = 50;
                    tile_shadow_intensity[plane][x + 1][y] = 50;
                }
                if (def.occlude)
                    tile_culling_bitsets[plane][x][y] |= 0x492;

            }
            if (def.solid && map != null)
                map.mark_wall(y, orientation, x, type, def.walkable);

            if (def.decor_offset != 16)
                scene.offset_wall_decor(y, def.decor_offset, x, plane);

            return;
        }
        if (type == 1) {
            Object diagonal_wall_connector;
            if (def.animation == -1 && def.configs == null)
                diagonal_wall_connector = def.get_object(1, orientation, v_sw, v_se, v_ne, v_nw, -1);
            else
                diagonal_wall_connector = new SceneObject(object_id, orientation, 1, v_se, v_ne, v_sw, v_nw, def.animation, true);

            scene.add_wall(wall_orientations[orientation], ((Renderable) (diagonal_wall_connector)), key, y, x, null, avg, 0, plane);
            if (def.cast_shadow)
                if (orientation == 0)
                    tile_shadow_intensity[plane][x][y + 1] = 50;

                else if (orientation == 1)
                    tile_shadow_intensity[plane][x + 1][y + 1] = 50;

                else if (orientation == 2)
                    tile_shadow_intensity[plane][x + 1][y] = 50;

                else if (orientation == 3)
                    tile_shadow_intensity[plane][x][y] = 50;

            if (def.solid && map != null)
                map.mark_wall(y, orientation, x, type, def.walkable);

            return;
        }
        if (type == 2) {
            int orientation_offset = orientation + 1 & 3;
            Object wall;
            Object corner;
            if (def.animation == -1 && def.configs == null) {
                wall = def.get_object(2, 4 + orientation, v_sw, v_se, v_ne, v_nw, -1);
                corner = def.get_object(2, orientation_offset, v_sw, v_se, v_ne, v_nw, -1);
            } else {
                wall = new SceneObject(object_id, 4 + orientation, 2, v_se, v_ne, v_sw, v_nw, def.animation, true);
                corner = new SceneObject(object_id, orientation_offset, 2, v_se, v_ne, v_sw, v_nw, def.animation, true);
            }
            scene.add_wall(wall_corner_orientations[orientation], ((Renderable) (wall)), key, y, x, ((Renderable) (corner)), avg, wall_corner_orientations[orientation_offset], plane);
            if (def.occlude)
                if (orientation == 0) {
                    tile_culling_bitsets[plane][x][y] |= 0x249;
                    tile_culling_bitsets[plane][x][y + 1] |= 0x492;
                } else if (orientation == 1) {
                    tile_culling_bitsets[plane][x][y + 1] |= 0x492;
                    tile_culling_bitsets[plane][x + 1][y] |= 0x249;
                } else if (orientation == 2) {
                    tile_culling_bitsets[plane][x + 1][y] |= 0x249;
                    tile_culling_bitsets[plane][x][y] |= 0x492;
                } else if (orientation == 3) {
                    tile_culling_bitsets[plane][x][y] |= 0x492;
                    tile_culling_bitsets[plane][x][y] |= 0x249;
                }

            if (def.solid && map != null)
                map.mark_wall(y, orientation, x, type, def.walkable);

            if (def.decor_offset != 16)
                scene.offset_wall_decor(y, def.decor_offset, x, plane);

            return;
        }
        if (type == 3) {
            Object straight_corner_connector;
            if (def.animation == -1 && def.configs == null)
                straight_corner_connector = def.get_object(3, orientation, v_sw, v_se, v_ne, v_nw, -1);
            else
                straight_corner_connector = new SceneObject(object_id, orientation, 3, v_se, v_ne, v_sw, v_nw, def.animation, true);

            scene.add_wall(wall_orientations[orientation], ((Renderable) (straight_corner_connector)), key, y, x, null, avg, 0, plane);
            if (def.cast_shadow)
                if (orientation == 0)
                    tile_shadow_intensity[plane][x][y + 1] = 50;
                else if (orientation == 1)
                    tile_shadow_intensity[plane][x + 1][y + 1] = 50;
                else if (orientation == 2)
                    tile_shadow_intensity[plane][x + 1][y] = 50;
                else if (orientation == 3)
                    tile_shadow_intensity[plane][x][y] = 50;

            if (def.solid && map != null)
                map.mark_wall(y, orientation, x, type, def.walkable);

            return;
        }
        if (type == 9) {
            Object diagonal_walls;
            if (def.animation == -1 && def.configs == null)
                diagonal_walls = def.get_object(type, orientation, v_sw, v_se, v_ne, v_nw, -1);
            else
                diagonal_walls = new SceneObject(object_id, orientation, type, v_se, v_ne, v_sw, v_nw, def.animation, true);

            scene.add_entity(key, avg, 1, ((Renderable) (diagonal_walls)), 1, plane, 0, y, x);
            if (def.solid && map != null)
                map.mark_interactive_obj(def.walkable, def.width, def.height, x, y, orientation);

            return;
        }
        if (def.contour_to_tile)
            if (orientation == 1) {
                int pos = v_nw;
                v_nw = v_ne;
                v_ne = v_se;
                v_se = v_sw;
                v_sw = pos;
            } else if (orientation == 2) {
                int pos = v_nw;
                v_nw = v_se;
                v_se = pos;
                pos = v_ne;
                v_ne = v_sw;
                v_sw = pos;
            } else if (orientation == 3) {
                int pos = v_nw;
                v_nw = v_sw;
                v_sw = v_se;
                v_se = v_ne;
                v_ne = pos;
            }
        if (type == 4) {
            Object inner_wall_decor;
            if (def.animation == -1 && def.configs == null)
                inner_wall_decor = def.get_object(4, 0, v_sw, v_se, v_ne, v_nw, -1);
            else
                inner_wall_decor = new SceneObject(object_id, 0, 4, v_se, v_ne, v_sw, v_nw, def.animation, true);

            scene.add_wall_decor(key, y, orientation * 512, plane, 0, avg, ((Renderable) (inner_wall_decor)), x, 0, wall_corner_orientations[orientation]);
            return;
        }
        if (type == 5) {
            int offset = 16;
            long wall_uid = scene.get_wall_uid(plane, x, y);
            if (wall_uid > 0)
                offset = ObjectDefinition.get(((int) (wall_uid >>> 32) & 0x7fffffff)/*k4 >> 14 & 0x7fff*/).decor_offset;

            Object outer_wall_decor;
            if (def.animation == -1 && def.configs == null)
                outer_wall_decor = def.get_object(4, 0, v_sw, v_se, v_ne, v_nw, -1);
            else
                outer_wall_decor = new SceneObject(object_id, 0, 4, v_se, v_ne, v_sw, v_nw, def.animation, true);

            scene.add_wall_decor(key, y, orientation * 512, plane, decor_x_offsets[orientation] * offset, avg, ((Renderable) (outer_wall_decor)), x, decor_y_offsets[orientation] * offset, wall_corner_orientations[orientation]);
            return;
        }
        if (type == 6) {
            Object outer_diagonal_wall_decor;
            if (def.animation == -1 && def.configs == null)
                outer_diagonal_wall_decor = def.get_object(4, 0, v_sw, v_se, v_ne, v_nw, -1);
            else
                outer_diagonal_wall_decor = new SceneObject(object_id, 0, 4, v_se, v_ne, v_sw, v_nw, def.animation, true);

            scene.add_wall_decor(key, y, orientation, plane, 0, avg, ((Renderable) (outer_diagonal_wall_decor)), x, 0, 256);
            return;
        }
        if (type == 7) {
            Object inner_diagonal_wall_decor;
            if (def.animation == -1 && def.configs == null)
                inner_diagonal_wall_decor = def.get_object(4, 0, v_sw, v_se, v_ne, v_nw, -1);
            else
                inner_diagonal_wall_decor = new SceneObject(object_id, 0, 4, v_se, v_ne, v_sw, v_nw, def.animation, true);

            scene.add_wall_decor(key, y, orientation, plane, 0, avg, ((Renderable) (inner_diagonal_wall_decor)), x, 0, 512);
            return;
        }
        if (type == 8) {
            Object diagonal_window_decor;
            if (def.animation == -1 && def.configs == null)
                diagonal_window_decor = def.get_object(4, 0, v_sw, v_se, v_ne, v_nw, -1);
            else
                diagonal_window_decor = new SceneObject(object_id, 0, 4, v_se, v_ne, v_sw, v_nw, def.animation, true);

            scene.add_wall_decor(key, y, orientation, plane, 0, avg, ((Renderable) (diagonal_window_decor)), x, 0, 768);
        }
    }

    private static int interpolated_noise(int freq_x, int freq_y, int frequency) {
        int x = freq_x / frequency;
        int width = freq_x & frequency - 1;
        int y = freq_y / frequency;
        int height = freq_y & frequency - 1;
        int sw = smooth(x, y);
        int se = smooth(x + 1, y);
        int ne = smooth(x, y + 1);
        int nw = smooth(x + 1, y + 1);
        int a = interpolate(sw, se, width, frequency);
        int b = interpolate(ne, nw, width, frequency);
        return interpolate(a, b, height, frequency);
    }

    public static boolean cached(int object_id, int type) {
        ObjectDefinition def = ObjectDefinition.get(object_id);
        if (type == 11)
            type = 10;

        if (type >= 5 && type <= 8)
            type = 4;

        return def.group_cached(type);
    }

    public final void load_sub_terrain_block(int sub_plane, int rotation, CollisionMap collision[], int block_x, int width, byte data[], int height, int plane, int block_y) {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++)
                if (block_x + x > 0 && block_x + x < 103 && block_y + y > 0 && block_y + y < 103)
                    collision[plane].adjacencies[block_x + x][block_y + y] &= 0xfeffffff;

        }
        Buffer buffer = new Buffer(data);
        for (int tile_plane = 0; tile_plane < 4; tile_plane++) {
            for (int tile_x = 0; tile_x < 64; tile_x++) {
                for (int tile_y = 0; tile_y < 64; tile_y++)
                    if (tile_plane == sub_plane && tile_x >= width && tile_x < width + 8 && tile_y >= height && tile_y < height + 8)
                        load_terrain_tile(block_y + ChunkUtil.get_rotated_map_y(tile_y & 7, rotation, tile_x & 7), 0, buffer,
                            block_x + ChunkUtil.get_rotated_map_x(rotation, tile_y & 7, tile_x & 7), plane, rotation, 0);
                    else
                        load_terrain_tile(-1, 0, buffer, -1, 0, 0, 0);

            }
        }
    }

    public final void load_terrain_block(byte data[], int block_y, int block_x, int x_offset, int y_offset, CollisionMap collision[]) {
        for (int plane = 0; plane < 4; plane++) {
            for (int x = 0; x < 64; x++) {
                for (int y = 0; y < 64; y++)
                    if (block_x + x > 0 && block_x + x < 103 && block_y + y > 0 && block_y + y < 103)
                        collision[plane].adjacencies[block_x + x][block_y + y] &= 0xfeffffff;
            }
        }
        Buffer buffer = new Buffer(data);
        for (int plane = 0; plane < 4; plane++) {
            for (int x = 0; x < 64; x++) {
                for (int y = 0; y < 64; y++)
                    load_terrain_tile(y + block_y, y_offset, buffer, x + block_x, plane, 0, x_offset);

            }
        }
    }

    private void load_terrain_tile(int y, int y_offset, Buffer buffer, int x, int plane, int orientation, int x_offset) {
        try {
            if (x >= 0 && x < 104 && y >= 0 && y < 104) {
                int absX = (x_offset + x);
                int absY = (y_offset + y);
                int absZ = (orientation + plane);
                tile_flags[plane][x][y] = 0;
                do {
                    int index = buffer.readUByte();
                    if (index == 0)
                        if (plane == 0) {
                            vertex_heights[0][x][y] = -calc_heights(0xe3b7b + x + x_offset, 0x87cce + y + y_offset) * 8;
                            return;
                        } else {
                            vertex_heights[plane][x][y] = vertex_heights[plane - 1][x][y] - 240;
                            return;
                        }
                    if (index == 1) {
                        int height = buffer.readUByte();
                        if (height == 1)
                            height = 0;
                        if (plane == 0) {
                            vertex_heights[0][x][y] = -height * 8;
                            return;
                        } else {
                            vertex_heights[plane][x][y] = vertex_heights[plane - 1][x][y] - height * 8;
                            return;
                        }
                    }
                    if (index <= 49) {
                        overlay_floor_id[plane][x][y] = buffer.readSignedByte();
                        overlay_clipping_paths[plane][x][y] = (byte) ((index - 2) / 4);
                        overlay_clipping_path_rotations[plane][x][y] = (byte) ((index - 2) + orientation & 3);
                    } else if (index <= 81)
                        tile_flags[plane][x][y] = (byte) (index - 49);
                    else
                        underlay_floor_id[plane][x][y] = (byte) (index - 81);
                } while (true);
            }
            do {
                int index = buffer.readUByte();
                if (index == 0)
                    break;

                if (index == 1) {
                    buffer.readUByte();
                    return;
                }
                if (index <= 49)
                    buffer.readUByte();

            } while (true);
        } catch (Exception e) {
        }
    }

    private int get_visibility(int y, int z, int x) {
        if ((tile_flags[z][x][y] & FORCE_LOWEST_PLANE) != 0)
            return 0;

        if (z > 0 && (tile_flags[1][x][y] & BRIDGE_TILE) != 0)
            return z - 1;
        else
            return z;
    }

    public final void load_sub_object_block(CollisionMap map[], SceneGraph scene, int sub_plane, int x_offset, int height, int l,
                                            byte abyte0[], int width, int rotation, int y_offset) {//TODO abyte0 // l
        //TODO x_offset/y_offset may not be right, double check
        load:
        {
            Buffer stream = new Buffer(abyte0);
            int object_id = -1;
            do {
                int offset = stream.readUSmart2();
                if (offset == 0)
                    break load;

                object_id += offset;
                int pos = 0;
                do {
                    int pos_offset = stream.readUSmart();
                    if (pos_offset == 0)
                        break;

                    pos += pos_offset - 1;
                    int region_y = pos & 0x3f;
                    int region_x = pos >> 6 & 0x3f;
                    int plane = pos >> 12;
                    int hash = stream.readUByte();
                    int type = hash >> 2;
                    int orientation = hash & 3;
                    if (plane == sub_plane && region_x >= width && region_x < width + 8 && region_y >= height && region_y < height + 8) {
                        ObjectDefinition def = ObjectDefinition.get(object_id);
                        int x = x_offset + ChunkUtil.get_rotated_landscape_x(rotation, def.height, region_x & 7, region_y & 7, def.width);
                        int y = y_offset + ChunkUtil.get_rotated_landscape_y(region_y & 7, def.height, rotation, def.width, region_x & 7);
                        if (x > 0 && y > 0 && x < 103 && y < 103) {
                            int marking_plane = plane;
                            if ((tile_flags[1][x][y] & 2) == 2)
                                marking_plane--;

                            CollisionMap collision = null;
                            if (marking_plane >= 0/* && l4 < 4*/)
                                collision = map[marking_plane];

                            render(y, scene, collision, type, l, x, object_id, orientation + rotation & 3);
                        }
                    }
                } while (true);
            } while (true);
        }
    }

    private static int interpolate(int a, int b, int angle, int frequencyReciprocal) {
        int cosine = 0x10000 - Rasterizer3D.COSINE[(angle * 1024) / frequencyReciprocal] >> 1;
        return (a * (0x10000 - cosine) >> 16) + (b * cosine >> 16);
    }

    private int encode_hsl(int h, int s, int l) {
        if (l > 179)
            s /= 2;

        if (l > 192)
            s /= 2;

        if (l > 217)
            s /= 2;

        if (l > 243)
            s /= 2;

        return (h / 4 << 10) + (s / 32 << 7) + l / 2;
    }

    private static int encode_hsl(int hsl, int light) {
        if (hsl == -1) {
            return 0xbc614e;
        } else {
            light = (light * (hsl & 0x7f)) / 128;
            if (light < 2)
                light = 2;
            else if (light > 126)
                light = 126;

            return (hsl & 0xff80) + light;
        }
    }

    private int encode_signed_hsl(int hsl, int light) {
        if (hsl == -2)
            return 0xbc614e;

        if (hsl == -1) {
            if (light < 0)
                light = 0;
            else if (light > 127)
                light = 127;

            light = 127 - light;
            return light;
        }

        light = (light * (hsl & 0x7f)) / 128;
        if (light < 2)
            light = 2;
        else if (light > 126)
            light = 126;

        return (hsl & 0xff80) + light;
    }

    static final int set_hsl_bitset(int h, int s, int l) {
        if (l > 179) {
            s /= 2;
        }
        if (l > 192) {
            s /= 2;
        }
        if (l > 217) {
            s /= 2;
        }
        if (l > 243) {
            s /= 2;
        }
        int hsl = (s / 32 << 7) + (h / 4 << 10) + l / 2;
        return hsl;

    }

    private static int smooth(int x, int y) {
        int corners = perlin(x - 1, y - 1) + perlin(x + 1, y - 1) + perlin(x - 1, y + 1) + perlin(x + 1, y + 1);
        int sides = perlin(x - 1, y) + perlin(x + 1, y) + perlin(x, y - 1) + perlin(x, y + 1);
        int center = perlin(x, y);
        return corners / 16 + sides / 8 + center / 4;
    }

    public static void place(SceneGraph scene, int orientation, int y, int type, int plane, CollisionMap collision, int vertex_heights[][][], int x, int object_id, int z) {
        ObjectDefinition def = ObjectDefinition.get(object_id);
        int obj_size_offset_a;
        int obj_size_offset_b;
        if (orientation == 1 || orientation == 3) {
            obj_size_offset_a = def.height;
            obj_size_offset_b = def.width;
        } else {
            obj_size_offset_a = def.width;
            obj_size_offset_b = def.height;
        }

        int obj_x_factor_a;
        int obj_x_factor_b;
        if (104 >= (obj_size_offset_a + x)) {
            obj_x_factor_b = x + (obj_size_offset_a + 1 >> 1);
            obj_x_factor_a = x + (obj_size_offset_a >> 1);
        } else {
            obj_x_factor_a = x;
            obj_x_factor_b = x + 1;
        }

        int obj_y_factor_a;
        int obj_y_factor_b;
        if (104 >= (obj_size_offset_b + y)) {
            obj_y_factor_b = y + (obj_size_offset_b + 1 >> 1);
            obj_y_factor_a = (obj_size_offset_b >> 1) + y;
        } else {
            obj_y_factor_a = y;
            obj_y_factor_b = 1 + y;
        }
        int v_sw = vertex_heights[plane][obj_x_factor_a][obj_y_factor_a];
        int v_se = vertex_heights[plane][obj_x_factor_b][obj_y_factor_a];
        int v_ne = vertex_heights[plane][obj_x_factor_b][obj_y_factor_b];
        int v_nw = vertex_heights[plane][obj_x_factor_a][obj_y_factor_b];

        int avg = v_sw + v_se + v_ne + v_nw >> 2;
        
        /*int i3 = i1 + (j << 7) + (object_id << 14) + 0x40000000;
        if (def.interact_state == 0)
            i3 += 0x80000000;*///remnant

        long key = (long) (orientation << 20 | type << 14 | (y << 7 | x) + 0x40000000);
        if (def.interact_state == 0) {
            key |= ~0x7fffffffffffffffL;
        }
        if (def.merge_interact_state == 1) {
            key |= 0x400000L;
        }
        key |= (long) object_id << 32;
        if (type == 22) {
            Object ground_map_scene_decor;
            if (def.animation == -1 && def.configs == null)
                ground_map_scene_decor = def.get_object(22, orientation, v_sw, v_se, v_ne, v_nw, -1);
            else
                ground_map_scene_decor = new SceneObject(object_id, orientation, 22, v_se, v_ne, v_sw, v_nw, def.animation, true);

            scene.add_ground_decor(z, avg, y, ((Renderable) (ground_map_scene_decor)), key, x);

            if (def.solid && def.interact_state == 1)
                collision.set_solid_flag(x, y);

            return;
        }
        if (type == 10 || type == 11) {
            Object scecne_object;
            if (def.animation == -1 && def.configs == null)
                scecne_object = def.get_object(10, orientation, v_sw, v_se, v_ne, v_nw, -1);
            else
                scecne_object = new SceneObject(object_id, orientation, 10, v_se, v_ne, v_sw, v_nw, def.animation, true);

            if (scecne_object != null) {
                int rotation = 0;
                if (type == 11)
                    rotation += 256;

                int size_offset_a;
                int size_offset_b;
                if (orientation == 1 || orientation == 3) {
                    size_offset_a = def.height;
                    size_offset_b = def.width;
                } else {
                    size_offset_a = def.width;
                    size_offset_b = def.height;
                }
                scene.add_entity(key, avg, size_offset_b, ((Renderable) (scecne_object)), size_offset_a, z, rotation, y, x);
            }
            if (def.solid)
                collision.mark_interactive_obj(def.walkable, def.width, def.height, x, y, orientation);

            return;
        }
        if (type >= 12) {
            Object roofs;
            if (def.animation == -1 && def.configs == null)
                roofs = def.get_object(type, orientation, v_sw, v_se, v_ne, v_nw, -1);
            else
                roofs = new SceneObject(object_id, orientation, type, v_se, v_ne, v_sw, v_nw, def.animation, true);

            scene.add_entity(key, avg, 1, ((Renderable) (roofs)), 1, z, 0, y, x);
            if (def.solid)
                collision.mark_interactive_obj(def.walkable, def.width, def.height, x, y, orientation);

            return;
        }
        if (type == 0) {
            Object straight_wall;
            if (def.animation == -1 && def.configs == null)
                straight_wall = def.get_object(0, orientation, v_sw, v_se, v_ne, v_nw, -1);
            else
                straight_wall = new SceneObject(object_id, orientation, 0, v_se, v_ne, v_sw, v_nw, def.animation, true);
            scene.add_wall(wall_corner_orientations[orientation], ((Renderable) (straight_wall)), key, y, x, null, avg, 0, z);

            if (def.solid)
                collision.mark_wall(y, orientation, x, type, def.walkable);

            return;
        }
        if (type == 1) {
            Object diagonal_wall_connector;
            if (def.animation == -1 && def.configs == null)
                diagonal_wall_connector = def.get_object(1, orientation, v_sw, v_se, v_ne, v_nw, -1);
            else
                diagonal_wall_connector = new SceneObject(object_id, orientation, 1, v_se, v_ne, v_sw, v_nw, def.animation, true);

            scene.add_wall(wall_orientations[orientation], ((Renderable) (diagonal_wall_connector)), key, y, x, null, avg, 0, z);
            if (def.solid)
                collision.mark_wall(y, orientation, x, type, def.walkable);

            return;
        }
        if (type == 2) {
            int orientation_offset = orientation + 1 & 3;
            Object wall;
            Object corner;
            if (def.animation == -1 && def.configs == null) {
                wall = def.get_object(2, 4 + orientation, v_sw, v_se, v_ne, v_nw, -1);
                corner = def.get_object(2, orientation_offset, v_sw, v_se, v_ne, v_nw, -1);
            } else {
                wall = new SceneObject(object_id, 4 + orientation, 2, v_se, v_ne, v_sw, v_nw, def.animation, true);
                corner = new SceneObject(object_id, orientation_offset, 2, v_se, v_ne, v_sw, v_nw, def.animation, true);
            }
            scene.add_wall(wall_corner_orientations[orientation], ((Renderable) (wall)), key, y, x, ((Renderable) (corner)), avg, wall_corner_orientations[orientation_offset], z);
            if (def.solid)
                collision.mark_wall(y, orientation, x, type, def.walkable);

            return;
        }
        if (type == 3) {
            Object straight_corner_connector;
            if (def.animation == -1 && def.configs == null)
                straight_corner_connector = def.get_object(3, orientation, v_sw, v_se, v_ne, v_nw, -1);
            else
                straight_corner_connector = new SceneObject(object_id, orientation, 3, v_se, v_ne, v_sw, v_nw, def.animation, true);

            scene.add_wall(wall_orientations[orientation], ((Renderable) (straight_corner_connector)), key, y, x, null, avg, 0, z);
            if (def.solid)
                collision.mark_wall(y, orientation, x, type, def.walkable);

            return;
        }
        if (type == 9) {
            Object diagonal_walls;
            if (def.animation == -1 && def.configs == null)
                diagonal_walls = def.get_object(type, orientation, v_sw, v_se, v_ne, v_nw, -1);
            else
                diagonal_walls = new SceneObject(object_id, orientation, type, v_se, v_ne, v_sw, v_nw, def.animation, true);

            scene.add_entity(key, avg, 1, ((Renderable) (diagonal_walls)), 1, z, 0, y, x);
            if (def.solid)
                collision.mark_interactive_obj(def.walkable, def.width, def.height, x, y, orientation);

            return;
        }
        if (def.contour_to_tile)
            if (orientation == 1) {
                int pos = v_nw;
                v_nw = v_ne;
                v_ne = v_se;
                v_se = v_sw;
                v_sw = pos;
            } else if (orientation == 2) {
                int l3 = v_nw;
                v_nw = v_se;
                v_se = l3;
                l3 = v_ne;
                v_ne = v_sw;
                v_sw = l3;
            } else if (orientation == 3) {
                int pos = v_nw;
                v_nw = v_sw;
                v_sw = v_se;
                v_se = v_ne;
                v_ne = pos;
            }
        if (type == 4) {
            Object straight_inner_decor;
            if (def.animation == -1 && def.configs == null)
                straight_inner_decor = def.get_object(4, 0, v_sw, v_se, v_ne, v_nw, -1);
            else
                straight_inner_decor = new SceneObject(object_id, 0, 4, v_se, v_ne, v_sw, v_nw, def.animation, true);

            scene.add_wall_decor(key, y, orientation * 512, z, 0, avg, ((Renderable) (straight_inner_decor)), x, 0, wall_corner_orientations[orientation]);
            return;
        }
        if (type == 5) {
            int offset = 16;
            long wall_uid = scene.get_wall_uid(z, x, y);
            if (wall_uid > 0)
                offset = ObjectDefinition.get(((int) (wall_uid >>> 32) & 0x7fffffff)/*l4 >> 14 & 0x7fff*/).decor_offset;

            Object outer_wall_decor;
            if (def.animation == -1 && def.configs == null)
                outer_wall_decor = def.get_object(4, 0, v_sw, v_se, v_ne, v_nw, -1);
            else
                outer_wall_decor = new SceneObject(object_id, 0, 4, v_se, v_ne, v_sw, v_nw, def.animation, true);

            scene.add_wall_decor(key, y, orientation * 512, z, decor_x_offsets[orientation] * offset, avg, ((Renderable) (outer_wall_decor)), x, decor_y_offsets[orientation] * offset, wall_corner_orientations[orientation]);
            return;
        }
        if (type == 6) {
            Object outer_diagonal_wall_decor;
            if (def.animation == -1 && def.configs == null)
                outer_diagonal_wall_decor = def.get_object(4, 0, v_sw, v_se, v_ne, v_nw, -1);
            else
                outer_diagonal_wall_decor = new SceneObject(object_id, 0, 4, v_se, v_ne, v_sw, v_nw, def.animation, true);

            scene.add_wall_decor(key, y, orientation, z, 0, avg, ((Renderable) (outer_diagonal_wall_decor)), x, 0, 256);
            return;
        }
        if (type == 7) {
            Object inner_diagonal_wall_decor;
            if (def.animation == -1 && def.configs == null)
                inner_diagonal_wall_decor = def.get_object(4, 0, v_sw, v_se, v_ne, v_nw, -1);
            else
                inner_diagonal_wall_decor = new SceneObject(object_id, 0, 4, v_se, v_ne, v_sw, v_nw, def.animation, true);

            scene.add_wall_decor(key, y, orientation, z, 0, avg, ((Renderable) (inner_diagonal_wall_decor)), x, 0, 512);
            return;
        }
        if (type == 8) {
            Object diagonal_window_decor;
            if (def.animation == -1 && def.configs == null)
                diagonal_window_decor = def.get_object(4, 0, v_sw, v_se, v_ne, v_nw, -1);
            else
                diagonal_window_decor = new SceneObject(object_id, 0, 4, v_se, v_ne, v_sw, v_nw, def.animation, true);

            scene.add_wall_decor(key, y, orientation, z, 0, avg, ((Renderable) (diagonal_window_decor)), x, 0, 768);
        }
    }

    public static boolean cached(int region_x, byte[] data, int region_y) {
        boolean cached = true;
        Buffer buffer = new Buffer(data);
        int object_id = -1;
        do {
            int id_increment = buffer.readUSmart();
            if (id_increment == 0)
                break;

            object_id += id_increment;
            int pos = 0;
            boolean read_second = false;
            do {
                if (read_second) {
                    int second = buffer.readUSmart();
                    if (second == 0)
                        break;
                    buffer.readUByte();
                } else {
                    int pos_offset = buffer.readUSmart();
                    if (pos_offset == 0)
                        break;
                    pos += pos_offset - 1;
                    int region_y_offset = pos & 0x3f;
                    int region_x_offset = pos >> 6 & 0x3f;
                    int obj_type = buffer.readUByte() >> 2;
                    int x = region_x_offset + region_x;
                    int y = region_y_offset + region_y;
                    if (x > 0 && y > 0 && x < 103 && y < 103) {
                        ObjectDefinition def = ObjectDefinition.get(object_id);
                        if (obj_type != 22 || !low_detail || def.interact_state == 1 || def.obstructs_ground) {
                            cached &= def.cached();
                            read_second = true;
                        }
                    }
                }
            } while (true);
        } while (true);
        return cached;
    }

    public final void load(int block_x, CollisionMap map[], int block_y, SceneGraph scene, byte data[]) {
        load:
        {
            Buffer buffer = new Buffer(data);
            int object_id = -1;
            do {
                int id_offset = buffer.readUSmart();
                if (id_offset == 0)
                    break load;

                object_id += id_offset;
                int pos = 0;
                do {
                    int pos_offset = buffer.readUSmart();
                    if (pos_offset == 0)
                        break;

                    pos += pos_offset - 1;
                    int tile_y = pos & 0x3f;
                    int tile_x = pos >> 6 & 0x3f;
                    int plane = pos >> 12;
                    int hash = buffer.readUByte();
                    int type = hash >> 2;
                    int orientation = hash & 3;
                    int x = tile_x + block_x;
                    int y = tile_y + block_y;
                    if (x > 0 && y > 0 && x < 103 && y < 103 && plane >= 0 && plane < 4) {
                        int marking_plane = plane;
                        if ((tile_flags[1][x][y] & 2) == 2)
                            marking_plane--;

                        CollisionMap collision = null;
                        if (marking_plane >= 0/* && marking_plane < 4*/)//added for higher revisions
                            collision = map[marking_plane];

                        render(y, scene, collision, type, plane, x, object_id, orientation);
                    }
                } while (true);
            } while (true);
        }
    }
}
