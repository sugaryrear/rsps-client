package com.ferox.cache.factory;

import com.ferox.cache.def.ItemDefinition;
import com.ferox.cache.graphics.SimpleImage;
import com.ferox.collection.TempCache;
import com.ferox.draw.Rasterizer2D;
import com.ferox.draw.Rasterizer3D;
import com.ferox.entity.model.Model;

/**
 * This class represents the item icon sprites. A.K.A inventory models on
 * widgets.
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @author Patrick van Elderen (https://www.rune-server.ee/members/zerikoth/)
 *         Reference: https://www.rune-server.ee/runescape-development/rs2-client/snippets/656135-item-icons-fix.html
 * @version 1.0
 * @since 2019-02-14
 */
public class ItemSpriteFactory {

    public static TempCache sprites_cache = new TempCache(100);
    public static TempCache scaled_cache = new TempCache(100);

    public static SimpleImage get_sized_item_sprite(int id, int stack_size, int border, int w, int h, boolean dumpImages) {
        try {
            if (border == 0) {
                SimpleImage cached = (SimpleImage) sprites_cache.get(id);
                if (cached != null && cached.max_height != stack_size && cached.max_height != -1) {
                    cached.remove();
                    cached = null;
                }
                if (cached != null && cached.width != 32) {
                    //System.err.println("CALLED 32 --->>>");
                    return cached;
                }

            }
            if (border == 1) {
                SimpleImage cached = (SimpleImage) scaled_cache.get(id);
                if (cached != null && cached.max_height != stack_size && cached.max_height != -1) {
                    cached.remove();
                    cached = null;
                }
                if (cached != null)
                    return cached;

            }

            ItemDefinition def = ItemDefinition.get(id);
            if (def == null) {
                //System.out.println("[ERROR] ItemSpriteFactory get_item_sprite - def == null! " + id);
            }

            if (def.stack_variant_id == null)
                stack_size = -1;

            if (stack_size > 1) {
                int stack_item_id = -1;
                for (int index = 0; index < 10; index++)
                    if (stack_size >= def.stack_variant_size[index] && def.stack_variant_size[index] != 0)
                        stack_item_id = def.stack_variant_id[index];

                if (stack_item_id != -1)
                    def = ItemDefinition.get(stack_item_id);

            }

            Model model = def.get_model(1);
            if (model == null)
                return null;

            SimpleImage noted_sprite = null;
            if (def.noted_item_id != -1) {
                noted_sprite = get_item_sprite(def.unnoted_item_id, 10, -1);

                if (noted_sprite == null)
                    return null;

            }

            SimpleImage item = new SimpleImage(w, h);
            int center_x = Rasterizer3D.center_x;
            int center_y = Rasterizer3D.center_y;
            int line_offsets[] = Rasterizer3D.line_offsets;
            int pixels[] = Rasterizer2D.pixels;
            float[] depthBuffer = Rasterizer2D.depth_buffer;
            int width = Rasterizer2D.width;
            int height = Rasterizer2D.height;
            int viewport_left = Rasterizer2D.clip_left;
            int viewport_right = Rasterizer2D.clip_right;
            int viewport_top = Rasterizer2D.clip_top;
            int viewport_bottom = Rasterizer2D.clip_bottom;
            Rasterizer3D.mapped = false;

            Rasterizer2D.init(w, h, item.pixels, new float[w * h]);
            Rasterizer2D.draw_filled_rect(0, 0, w, h, 0);
            Rasterizer3D.set_clip();
            int zoom2d = def.model_zoom;

            if (border == -1)
                zoom2d = (int) ((double) zoom2d * 1.5D);
            if (border > 0)
                zoom2d = (int) ((double) zoom2d * 1.04D);

            if (dumpImages) {
                zoom2d /= 4;
            } else {
                zoom2d *= 1.5D;
            }

            int sine = Rasterizer3D.SINE[def.rotation_y] * zoom2d >> 16;
            int cosine = Rasterizer3D.COSINE[def.rotation_y] * zoom2d >> 16;

            model.render_2D(def.rotation_x, def.rotation_z, def.rotation_y, def.translate_x, sine + model.model_height / 2 + def.translate_y, cosine + def.translate_y);
            item.highlight(1);
            if (border == 0xffffff) {
                item.highlight(16777215);//16777215 = white
            } else {
                if (border < 1 && border != -1)
                    item.shadow(3153952);//3153952 = black
            }

            Rasterizer2D.init(w, h, item.pixels, new float[w * h]);
            if (def.noted_item_id != -1) {
                int old_w = noted_sprite.max_width;
                int old_h = noted_sprite.max_height;
                noted_sprite.max_width = w;
                noted_sprite.max_height = h;
                noted_sprite.drawSprite(0, 0);
                noted_sprite.max_width = old_w;
                noted_sprite.max_height = old_h;
            }

            if (border == 0 && !def.animateInventory)
                sprites_cache.put(item, id);

            if (border == 1 && !def.animateInventory)
                scaled_cache.put(item, id);

            Rasterizer2D.init(width, height, pixels, depthBuffer);
            Rasterizer2D.set_clip(viewport_left, viewport_top, viewport_right, viewport_bottom);
            Rasterizer3D.center_x = center_x;
            Rasterizer3D.center_y = center_y;
            Rasterizer3D.line_offsets = line_offsets;

            Rasterizer3D.mapped = true;
            if (def.stackable)
                item.max_width = w + 1;
            else
                item.max_width = w;

            item.max_height = stack_size;
            return item;

        } catch (NullPointerException e) {
            e.printStackTrace();
            throw new NullPointerException("Error generating item sprite! [ItemSpriteFactory -> get_item_sprite()]");
        }
    }

    public static SimpleImage get_item_sprite(int id, int stack_size, int highlight) {
        try {
            if (highlight == 0) {
                SimpleImage cached = (SimpleImage) sprites_cache.get(id);
                if (cached != null && cached.max_height != stack_size && cached.max_height != -1) {
                    cached.remove();
                    cached = null;
                }
                if (cached != null)
                    return cached;

            }
            if (highlight == 1) {
                SimpleImage cached = (SimpleImage) scaled_cache.get(id);

                if (cached != null && cached.max_height != stack_size && cached.max_height != -1) {
                    cached.remove();
                    cached = null;
                }

                if (cached != null)
                    return cached;

            }

            ItemDefinition def = ItemDefinition.get(id);
            if (def == null) {
                System.out.println("[ERROR] ItemSpriteFactory get_item_sprite - def == null! " + id);
            }

            if (def.stack_variant_id == null)
                stack_size = -1;

            if (stack_size > 1) {
                int stack_item_id = -1;
                for (int index = 0; index < 10; index++)
                    if (stack_size >= def.stack_variant_size[index] && def.stack_variant_size[index] != 0)
                        stack_item_id = def.stack_variant_id[index];

                if (stack_item_id != -1)
                    def = ItemDefinition.get(stack_item_id);

            }

            Model model = def.get_model(1);
            if (model == null)
                return null;

            SimpleImage noted_sprite = null;
            if (def.noted_item_id != -1) {
                noted_sprite = get_item_sprite(def.unnoted_item_id, 10, -1);

                if (noted_sprite == null)
                    return null;

            }

            SimpleImage item = new SimpleImage(32, 32);
            int center_x = Rasterizer3D.center_x;
            int center_y = Rasterizer3D.center_y;
            int[] line_offsets = Rasterizer3D.line_offsets;
            int[] pixels = Rasterizer2D.pixels;
            float[] depth = Rasterizer2D.depth_buffer;
            int width = Rasterizer2D.width;
            int height = Rasterizer2D.height;
            int viewport_left = Rasterizer2D.clip_left;
            int viewport_right = Rasterizer2D.clip_right;
            int viewport_top = Rasterizer2D.clip_top;
            int viewport_bottom = Rasterizer2D.clip_bottom;
            Rasterizer3D.mapped = false;

            Rasterizer2D.init(32, 32, item.pixels, new float[32 * 32]);
            Rasterizer2D.draw_filled_rect(0, 0, 32, 32, 0);
            Rasterizer3D.set_clip();
            int zoom = def.model_zoom;
            if (highlight == -1)
                zoom = (int) ((double) zoom * 1.5D);
            if (highlight > 0)
                zoom = (int) ((double) zoom * 1.04D);

            int sine = Rasterizer3D.SINE[def.rotation_y] * zoom >> 16;
            int cosine = Rasterizer3D.COSINE[def.rotation_y] * zoom >> 16;

            model.render_2D(def.rotation_x, def.rotation_z, def.rotation_y, def.translate_x, sine + model.model_height / 2 + def.translate_y, cosine + def.translate_y);
            item.highlight(1);
            if(highlight == 0xffffff) {
                item.highlight(16777215);//16777215 = white
            } else {
                if(highlight < 1 && highlight != -1)
                    item.shadow(3153952);//3153952 = black
            }

            Rasterizer2D.init(32, 32, item.pixels, new float[32 * 32]);
            if (def.noted_item_id != -1) {
                int old_w = noted_sprite.max_width;
                int old_h = noted_sprite.max_height;
                noted_sprite.max_width = 32;
                noted_sprite.max_height = 32;
                noted_sprite.drawSprite(0, 0);
                noted_sprite.max_width = old_w;
                noted_sprite.max_height = old_h;
            }

            if (highlight == 0 && !def.animateInventory)
                sprites_cache.put(item, id);

            if (highlight == 1 && !def.animateInventory)
                scaled_cache.put(item, id);

            Rasterizer2D.init(width, height, pixels, depth);
            Rasterizer2D.set_clip(viewport_left, viewport_top, viewport_right, viewport_bottom);
            Rasterizer3D.center_x = center_x;
            Rasterizer3D.center_y = center_y;
            Rasterizer3D.line_offsets = line_offsets;

            Rasterizer3D.mapped = true;
            if (def.stackable)
                item.max_width = 33;
            else
                item.max_width = 32;

            item.max_height = stack_size;
            return item;

        } catch (NullPointerException e) {
            e.printStackTrace();
            throw new NullPointerException("Error generating item sprite! [ItemSpriteFactory -> get_item_sprite()]");
        }
    }

}
