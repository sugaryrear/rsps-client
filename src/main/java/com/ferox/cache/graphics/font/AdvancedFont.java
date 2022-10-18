package com.ferox.cache.graphics.font;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.ferox.Client;
import com.ferox.cache.Archive;
import com.ferox.cache.graphics.SimpleImage;
import com.ferox.draw.Rasterizer2D;
import com.ferox.io.Buffer;

import static com.ferox.Client.spriteCache;

public class AdvancedFont extends Rasterizer2D {

    public AdvancedFont(boolean monospace, String name, Archive archive) {
        glyph_pixels = new byte[256][];
        glyph_width = new int[256];
        glyph_height = new int[256];
        kerning_x = new int[256];
        kerning_y = new int[256];
        glyph_display_width = new int[256];
        
        Buffer data = new Buffer(archive.get(name + ".dat"));
        Buffer idx = new Buffer(archive.get("index.dat"));
        idx.pos = data.readUShort() + 4;
        int pos = idx.readUByte();
        if (pos > 0) {
            idx.pos += 3 * (pos - 1);
        }
        for (int index = 0; index < 256; index++) {
            kerning_x[index] = idx.readUByte();
            kerning_y[index] = idx.readUByte();
            int width = glyph_width[index] = idx.readUShort();
            int height = glyph_height[index] = idx.readUShort();
            int opcode = idx.readUByte();
            int area = width * height;
            glyph_pixels[index] = new byte[area];
            if (opcode == 0) {
                for (int pixels = 0; pixels < area; pixels++) {
                    glyph_pixels[index][pixels] = data.readSignedByte();
                }
            } else if (opcode == 1) {
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        glyph_pixels[index][x + y * width] = data.readSignedByte();
                    }
                }
            }
            if (height > base_char_height && index < 128) {
                base_char_height = height;
            }
            kerning_x[index] = 1;
            glyph_display_width[index] = width + 2;
            int pixels = 0;
            for (int y = height / 7; y < height; y++) {
                pixels += glyph_pixels[index][y * width];
            }
            if (pixels <= height / 7) {
                glyph_display_width[index]--;
                kerning_x[index] = 0;
            }
            pixels = 0;
            for (int y = height / 7; y < height; y++) {
                pixels += glyph_pixels[index][(width - 1) + y * width];
            }
            if (pixels <= height / 7) {
                glyph_display_width[index]--;
            }
        }
        if (monospace) {
            glyph_display_width[32] = glyph_display_width[73];
        } else {
            glyph_display_width[32] = glyph_display_width[105];
        }
    }
    
    public static void init_sprites(SimpleImage[] chat, SimpleImage[] clan, SimpleImage[] tag) {
        chat_emblems = chat;
        clan_emblems = clan;
    }

    public int get_glyph_width(int index) {
        return glyph_display_width[index & 0xff];
    }

    public void set_defaults(int color, int shadow) {
        strikethrough_color = -1;
        underline_color = -1;
        shadow_color = default_shad = shadow;
        font_color = default_color = color;
        opacity = default_trans = 256;
        anInt4178 = 0;
        anInt4175 = 0;
    }
    
    public void set_defaults(int shadow, int color, int trans) {
        shadow_color = default_shad = shadow;
        font_color = default_color = color;
        opacity = default_trans = trans;
    }

    public void set_adv_defaults(int color, int shadow, int trans) {
        strikethrough_color = -1;
        underline_color = -1;
        shadow_color = default_shad = shadow;
        font_color = default_color = color;
        opacity = default_trans = trans;
        anInt4178 = 0;
        anInt4175 = 0;
    }


   /* public static int method1014(byte[][] is, byte[][] is_27_, int[] is_28_, int[] is_29_, int[] is_30_, int i, int i_31_) {//unused
        int i_32_ = is_28_[i];
        int i_33_ = i_32_ + is_30_[i];
        int i_34_ = is_28_[i_31_];
        int i_35_ = i_34_ + is_30_[i_31_];
        int i_36_ = i_32_;
        if (i_34_ > i_32_) {
            i_36_ = i_34_;
        }
        int i_37_ = i_33_;
        if (i_35_ < i_33_) {
            i_37_ = i_35_;
        }
        int i_38_ = is_29_[i];
        if (is_29_[i_31_] < i_38_) {
            i_38_ = is_29_[i_31_];
        }
        byte[] is_39_ = is_27_[i];
        byte[] is_40_ = is[i_31_];
        int i_41_ = i_36_ - i_32_;
        int i_42_ = i_36_ - i_34_;
        for (int i_43_ = i_36_; i_43_ < i_37_; i_43_++) {
            int i_44_ = is_39_[i_41_++] + is_40_[i_42_++];
            if (i_44_ < i_38_) {
                i_38_ = i_44_;
            }
        }
        return -i_38_;
    }*/

    public String[] wrap(String text, int maximumWidth) {
        String[] words = text.split(" ");

        if (words.length == 0) {
            return new String[] { text };
        }

        List<String> lines = new ArrayList<>();

        String line = new String();

        int lineWidth = 0;

        int spaceWidth = get_width(" ");

        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }
            int wordWidth = get_width(word);
            boolean isLastWord = word.equals(words[words.length - 1]);

            if (wordWidth + lineWidth >= maximumWidth && !isLastWord) {
                lines.add(line.trim());
                line = new String(word.concat(" "));
                lineWidth = wordWidth + spaceWidth;
            } else if (isLastWord) {
                if (wordWidth + lineWidth > maximumWidth) {
                    lines.add(line.trim());
                    lines.add(word);
                } else {
                    lines.add(line.concat(word));
                }
            } else {
                line = line.concat(word).concat(" ");
                lineWidth += wordWidth + spaceWidth;
            }
        }

        return lines.toArray(new String[lines.size()]);
    }
    
    public void draw_centered(String string, int width, int height) {
        if (string != null)
            draw(string, width - get_width(string) / 2, height);
    }

    public void drawRAString(String string, int x, int y, int color, int shadow) {
        if (string != null) {
            set_defaults(color, shadow);
            draw(string, x - get_width(string), y);
        }
    }

    public void draw(String string, int x, int y) {
        y -= base_char_height;
        int index = -1;
        int unknown = -1;
        for (int current = 0; current < string.length(); current++) {
            if (string.charAt(current) == '@' && current + 4 < string.length() && string.charAt(current + 4) == '@') {
                int rgb = get_color(string.substring(current + 1, current + 4));
                if (rgb != -1) {
                    font_color = rgb;
                }
                current += 4;
                continue;
            }
            int character = string.charAt(current);
            if (character > 255) {
                character = 32;
            }
            if (character == 60) {
                index = current;
            } else {
                if (character == 62 && index != -1) {
                    String effect = string.substring(index + 1, current);
                    index = -1;
                    if (effect.equals(lt_string)) {
                        character = 60;
                    } else if (effect.equals(gt_string)) {
                        character = 62;
                    } else if (effect.equals(nbsp_string)) {
                        character = 160;
                    } else if (effect.equals(shy_string)) {
                        character = 173;
                    } else if (effect.equals(times_string)) {
                        character = 215;
                    } else if (effect.equals(euro_string)) {
                        character = 128;
                    } else if (effect.equals(copy_string)) {
                        character = 169;
                    } else if (effect.equals(reg_string)) {
                        character = 174;
                    } else {
                        if (effect.startsWith(set_image)) {
                            try {
                                //System.out.println("effect is: " + effect);
                                int id = Integer.parseInt(effect.substring(4));
                                //System.out.println("id is: " + id);
                                SimpleImage icon = null;
                                if(id != -1) {
                                    icon = spriteCache.get(id);
                                }

                                if(icon == null) {
                                    return;
                                }

                                int height_offset = icon.height;//11;//changed to max_height
                                if (opacity == 256) {
                                    icon.drawSprite(x, (y + base_char_height - height_offset));
                                } else {
                                    icon.draw_highlighted(x, (y + base_char_height - height_offset), opacity);
                                }
                                x += icon.width;
                                unknown = -1;
                            } catch (Exception exception) {
                                exception.printStackTrace();
                                Client.addReportToServer(exception.getMessage());
                                /* was empty now isn't */
                            }
                        } else if (effect.startsWith(set_clan)) {
                                try {
                                    int id = Integer.parseInt(effect.substring(5));
                                    SimpleImage icon = null;
                                    if(id != -1) {
                                        icon = spriteCache.get(id);
                                    }

                                    if(icon == null) {
                                        return;
                                    }
                                    int height_offset = icon.height + icon.y_offset + 1;
                                    if (opacity == 256) {
                                        icon.drawSprite(x, (y + base_char_height - height_offset));
                                    } else {
                                        icon.draw_highlighted(x, (y + base_char_height - height_offset), opacity);
                                    }
                                    x += 11;
                                    unknown = -1;
                                } catch (Exception exception) {
                                    exception.printStackTrace();
                                    Client.addReportToServer(exception.getMessage());
                                }
                        } else {
                            set_effects(effect);
                        }
                        continue;
                    }
                }
                if (index == -1) {
                    //unknown - TODO what's it do in osrs_revison
                    if (this.byte_unknown != null && unknown != -1) {
                        x += this.byte_unknown[(unknown << 8) + character];
                      }

                    int width = glyph_width[character];
                    int height = glyph_height[character];
                    if (character != 32) {
                        if (opacity == 256) {
                            if (shadow_color != -1) {
                                draw_glyph(character, x + kerning_x[character] + 1, y + kerning_y[character] + 1, width, height, shadow_color, true);
                            }
                            draw_glyph(character, x + kerning_x[character], y + kerning_y[character], width, height, font_color, false);
                        } else {
                            if (shadow_color != -1) {
                                draw_transparent_glyph(character, x + kerning_x[character] + 1, y + kerning_y[character] + 1, width, height, shadow_color, opacity, true);
                            }
                            draw_transparent_glyph(character, x + kerning_x[character], y + kerning_y[character], width, height, font_color, opacity, false);
                        }
                    } else if (anInt4178 > 0) {
                        anInt4175 += anInt4178;//anInt2014 += anInt2006;
                        x += anInt4175 >> 8;
                        anInt4175 &= 0xff;
                    }
                    int final_width = glyph_display_width[character];
                    if (strikethrough_color != -1) {
                        draw_line(y + (int) ((double) base_char_height * 0.69999999999999996D), strikethrough_color, final_width, x);
                    }
                    if (underline_color != -1) {
                        draw_line(y + base_char_height, underline_color, final_width, x);
                    }
                    x += final_width;
                    unknown = character;
                }
            }
        }
    }
    
    public void draw_shake(String string, int x, int y, int color, int shadow, int cycle, int tick) {
        if (string != null) {
            set_defaults(color, shadow);
            double amp = 7.0 - (double) tick / 8.0;
            if (amp < 0.0) {
                amp = 0.0;
            }
            int[] glyph_y = new int[string.length()];
            for (int index = 0; index < string.length(); index++) {
                glyph_y[index] = (int) (Math.sin((double) index / 1.5 + (double) cycle) * amp);
            }
            draw_translated(string, x - get_width(string) / 2, y, null, glyph_y);
        }
    }

    public void draw_wave(String string, int x, int y, int color, int shadow, int cycle) {
        if (string != null) {
            set_defaults(color, shadow);
            int[] glyph_x = new int[string.length()];
            int[] glyph_y = new int[string.length()];
            for (int index = 0; index < string.length(); index++) {
                glyph_x[index] = (int) (Math.sin((double) index / 5.0 + (double) cycle / 5.0) * 5.0);
                glyph_y[index] = (int) (Math.sin((double) index / 3.0 + (double) cycle / 5.0) * 5.0);
            }
            draw_translated(string, x - get_width(string) / 2, y, glyph_x, glyph_y);
        }
    }

    public void draw_wave2(String string, int x, int y, int color, int shadow, int cycle) {
        if (string != null) {
            set_defaults(color, shadow);
            int[] glyph_y = new int[string.length()];
            for (int index = 0; index < string.length(); index++) {
                glyph_y[index] = (int) (Math.sin((double) index / 2.0 + (double) cycle / 5.0) * 5.0);
            }
            draw_translated(string, x - get_width(string) / 2, y, null, glyph_y);
        }
    }
    
    public void draw_translated(String string, int x, int y, int[] x_translate, int[] y_translate) {
        y -= base_char_height;
        int index = -1;
        int unknown = -1;
        int translate_offset = 0;
        for (int current = 0; current < string.length(); current++) {
            int character = string.charAt(current);
            if (character == 60) {
                index = current;
            } else {
                if (character == 62 && index != -1) {
                    String effect = string.substring(index + 1, current);
                    index = -1;
                    if (effect.equals(lt_string)) {
                        character = 60;
                    } else if (effect.equals(gt_string)) {
                        character = 62;
                    } else if (effect.equals(nbsp_string)) {
                        character = 160;
                    } else if (effect.equals(shy_string)) {
                        character = 173;
                    } else if (effect.equals(times_string)) {
                        character = 215;
                    } else if (effect.equals(euro_string)) {
                        character = 128;
                    } else if (effect.equals(copy_string)) {
                        character = 169;
                    } else if (effect.equals(reg_string)) {
                        character = 174;
                    } else {
                        if (effect.startsWith(set_image)) {
                            try {
                                int x_translate_offset;
                                if (x_translate != null) {
                                    x_translate_offset = x_translate[translate_offset];
                                } else {
                                    x_translate_offset = 0;
                                }
                                int y_translate_offset;
                                if (y_translate != null) {
                                    y_translate_offset = y_translate[translate_offset];
                                } else {
                                    y_translate_offset = 0;
                                }
                                translate_offset++;
                                
                                int id = Integer.parseInt(effect.substring(4));
                                SimpleImage chat = chat_emblems[id];
                                int icon_offset_y = chat.height;
                                if (opacity == 256) {
                                    chat.drawSprite(x + x_translate_offset, (y + base_char_height - icon_offset_y + y_translate_offset));
                                } else {
                                    chat.draw_highlighted(x + x_translate_offset, (y + base_char_height - icon_offset_y + y_translate_offset), opacity);
                                }
                                x += chat.width;
                                unknown = -1;
                            } catch (Exception exception) {
                                exception.printStackTrace();
                                Client.addReportToServer(exception.getMessage());
                                /* was empty now isn't */
                            }
                        } else {
                            set_effects(effect);
                        }
                        continue;
                    }
                }
                if (index == -1) {
                    if (this.byte_unknown != null && unknown != -1) {
                        x += this.byte_unknown[(unknown << 8) + character];
                      }

                    int width = glyph_width[character];
                    int height = glyph_height[character];
                    int x_translate_offset;
                    if (x_translate != null) {
                        x_translate_offset = x_translate[translate_offset];
                    } else {
                        x_translate_offset = 0;
                    }
                    int y_translate_offset;
                    if (y_translate != null) {
                        y_translate_offset = y_translate[translate_offset];
                    } else {
                        y_translate_offset = 0;
                    }
                    translate_offset++;
                    if (character != 32) {
                        if (opacity == 256) {
                            if (shadow_color != -1) {
                                draw_glyph(character, (x + kerning_x[character] + 1 + x_translate_offset), (y + kerning_y[character] + 1 + y_translate_offset), width, height, shadow_color, true);
                            }
                            draw_glyph(character, x + kerning_x[character] + x_translate_offset, y + kerning_y[character] + y_translate_offset,  width, height, font_color, false);
                        } else {
                            if (shadow_color != -1) {
                                draw_transparent_glyph(character, (x + kerning_x[character] + 1 + x_translate_offset), (y + kerning_y[character] + 1 + y_translate_offset), width, height, shadow_color, opacity, true);
                            }
                            draw_transparent_glyph(character, x + kerning_x[character] + x_translate_offset, y + kerning_y[character] + y_translate_offset, width, height, font_color, opacity, false);
                        }
                    } else if (anInt4178 > 0) {
                        anInt4175 += anInt4178;
                        x += anInt4175 >> 8;
                        anInt4175 &= 0xff;
                    }
                    int final_width = glyph_display_width[character];
                    if (strikethrough_color != -1) {
                        draw_line(y + (int) ((double) base_char_height * 0.7), strikethrough_color, final_width, x);
                    }
                    if (underline_color != -1) {
                        draw_line(y + base_char_height, underline_color, final_width, x);
                    }
                    x += final_width;
                    unknown = character;
                }
            }
        }
    }

    public void set_effects(String string) {
        do {
            try {
                if (string.startsWith(set_color)) {
                    String color = string.substring(4);
                    font_color = color.length() < 6 ? Color.decode(color).getRGB() : Integer.parseInt(color, 16);
                } else if (string.equals(close_color)) {
                    font_color = default_color;
                } else if (string.startsWith(set_transparency)) {
                    opacity = Integer.valueOf(string.substring(6));
                } else if (string.equals(close_transparency)) {
                    opacity = default_trans;
                } else if (string.startsWith(set_strikethrough)) {
                    String color = string.substring(4);
                    strikethrough_color = color.length() < 6 ? Color.decode(color).getRGB() : Integer.parseInt(color, 16);
                } else if (string.equals(set_default_strikethrough)) {
                    strikethrough_color = 8388608;
                } else if (string.equals(close_strikethrough)) {
                    strikethrough_color = -1;
                } else if (string.startsWith(set_underline)) {
                    String color = string.substring(2);
                    underline_color = color.length() < 6 ? Color.decode(color).getRGB() : Integer.parseInt(color, 16);
                } else if (string.equals(set_default_underline)) {
                    underline_color = 0;
                } else if (string.equals(close_underline)) {
                    underline_color = -1;
                } else if (string.startsWith(set_shadow)) {
                    String color = string.substring(5);
                    shadow_color = color.length() < 6 ? Color.decode(color).getRGB() : Integer.parseInt(color, 16);
                } else if (string.equals(set_default_shadow)) {
                    shadow_color = 0;
                } else if (string.equals(close_shadow)) {
                    shadow_color = default_shad;
                } else {
                    if (!string.equals(line_break)) {
                        break;
                    }
                    set_adv_defaults(default_color, default_shad, default_trans);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
                Client.addReportToServer(exception.getMessage());
                break;
            }
            break;
        } while (false);
    }

    public int get_width(String string) {
        if (string == null) {
            return 0;
        }
        int startIndex = -1;
        int unknown = -1;
        int final_width = 0;
        for (int current = 0; current < string.length(); current++) {
            int character = string.charAt(current);
            if (character > 255) {
                character = 32;
            }
            if (character == 60) {
                startIndex = current;
            } else {
                if (character == 62 && startIndex != -1) {
                    String effect = string.substring(startIndex + 1, current);
                    startIndex = -1;
                    if (effect.equals(lt_string)) {
                        character = 60;
                    } else if (effect.equals(gt_string)) {
                        character = 62;
                    } else if (effect.equals(nbsp_string)) {
                        character = 160;
                    } else if (effect.equals(shy_string)) {
                        character = 173;
                    } else if (effect.equals(times_string)) {
                        character = 215;
                    } else if (effect.equals(euro_string)) {
                        character = 128;
                    } else if (effect.equals(copy_string)) {
                        character = 169;
                    } else if (effect.equals(reg_string)) {
                        character = 174;
                    } else {
                        if (effect.startsWith(set_image)) {
                            try {//<img=
                                int id = Integer.parseInt(effect.substring(4));
                                //Added check for null to prevent clan chat NPE
                                if (chat_emblems != null && chat_emblems[id] != null) {
                                    final_width += chat_emblems[id].width;
                                }
                                //If it's not a chat emblem sprite, let's try it as a regular sprite.
                                else if (spriteCache.get(id) != null) {
                                    final_width += spriteCache.get(id).width;
                                }
                                unknown = -1;
                            } catch (Exception exception) {
                                exception.printStackTrace();
                                Client.addReportToServer(exception.getMessage());
                            }
                        }
                        continue;
                    }
                }
                if (startIndex == -1) {
                    final_width += glyph_display_width[character];
                    
                    if (this.byte_unknown != null && unknown != -1) {
                        final_width += this.byte_unknown[(unknown << 8) + character];
                      }
                    unknown = character;//
                }
            }
        }
        return final_width;
    }

    public void draw(String string, int x, int y, int color, boolean shadow) {
        int shad_color;
        if (string != null) {
            if (shadow != true)
                shad_color = -1;
            else
                shad_color = 0;

            set_defaults(color, shad_color);
            draw(string, x, y);
        }
    }

    public void draw_centered(String string, int x, int y, int color, boolean shadow) {
        int shad_color;
        if (string != null) {
            if (shadow != true)
                shad_color = -1;
            else
                shad_color = 0;

            set_defaults(color, shad_color);
            draw(string, x - get_width(string) / 2, y);
        }
    }

    public void draw(String string, int x, int y, int color, int shadow) {
        if (string != null) {
            set_defaults(color, shadow);
            draw(string, x, y);
        }
    }

    public void draw_centered(String string, int x, int y, int color, int shadow) {
        if (string != null) {
            set_defaults(color, shadow);
            draw(string, x - get_width(string) / 2, y);
        }
    }

    public void draw(String string, int x, int y, int color, int shadow, int trans) {
        if (string != null) {
            set_defaults(color, shadow);
            opacity = default_trans = trans;
            draw(string, x, y);
        }
    }

    public void draw_centered(String s, int x, int y, int col, int shad, int trans) {
        if (s != null) {
            set_defaults(col, shad);
            opacity = default_trans = trans;
            draw(s, x - get_width(s) / 2, y);
        }
    }

    public double pcOpacity;
    public boolean maxOpacity;

    public void drawGlowingString(String string, int x, int y, int color, int startOpacity, double speed) {
        if (pcOpacity < startOpacity) {
            pcOpacity = startOpacity;
            maxOpacity = false;
        }
        if (pcOpacity > 255) {
            pcOpacity = 255;
            maxOpacity = true;
        }
        if (pcOpacity >= startOpacity && !maxOpacity) {
            pcOpacity += speed;
            if (pcOpacity == 255) {
                maxOpacity = true;
            }
        }
        if (pcOpacity <= 255 && maxOpacity) {
            pcOpacity -= speed;
            if (pcOpacity == startOpacity) {
                maxOpacity = false;
            }
        }
        setTransLeaveShadow((int)pcOpacity);
        draw_centered(string, x + 19, y, color, false);
    }
    
    public void setTransLeaveShadow(int alpha) {
        opacity = default_trans = alpha;
    }

    public void draw_left(String string, int x, int color, int y, int trans, int shadow) {
         if (string != null) {
            set_defaults(color, shadow);
            opacity = default_trans = trans;
            draw(string, x - get_glyph_length(string), y);
        }
    }

    public int get_glyph_length(String string) {
        if (string == null)
            return 0;

        int length = 0;
        for (int index = 0; index < string.length(); index++)
            length += glyph_display_width[string.charAt(index)];

        return length;
    }

    public void draw_glyph(int glyph, int x, int y, int width, int height, int color, boolean bool) {
        int dst_pos = x + y * Rasterizer2D.width;
        int dst_width = Rasterizer2D.width - width;
        int src_width = 0;
        int src_pos = 0;
        if (y < Rasterizer2D.clip_top) {
            int size = Rasterizer2D.clip_top - y;
            height -= size;
            y = Rasterizer2D.clip_top;
            src_pos += size * width;
            dst_pos += size * Rasterizer2D.width;
        }
        if (y + height > Rasterizer2D.clip_bottom) {
            height -= y + height - Rasterizer2D.clip_bottom;
        }
        if (x < Rasterizer2D.clip_left) {
            int size = Rasterizer2D.clip_left - x;
            width -= size;
            x = Rasterizer2D.clip_left;
            src_pos += size;
            dst_pos += size;
            src_width += size;
            dst_width += size;
        }
        if (x + width > Rasterizer2D.clip_right) {
            int size = x + width - Rasterizer2D.clip_right;
            width -= size;
            
            src_width += size;
            dst_width += size;
        }
        if (width > 0 && height > 0) {
            render(Rasterizer2D.pixels, glyph_pixels[glyph], color, src_pos, dst_pos, width, height, dst_width, src_width);
        }
    }
    
    public void draw_transparent_glyph(int glyph, int x, int y, int width, int height, int color, int alpha, boolean bool) {
        int dst_pos = x + y * Rasterizer2D.width;
        int dst_width = Rasterizer2D.width - width;
        int src_width = 0;
        int src_pos = 0;
        if (y < Rasterizer2D.clip_top) {
            int size = Rasterizer2D.clip_top - y;
            height -= size;
            y = Rasterizer2D.clip_top;
            src_pos += size * width;
            dst_pos += size * Rasterizer2D.width;
        }
        if (y + height > Rasterizer2D.clip_bottom) {
            height -= y + height - Rasterizer2D.clip_bottom;
        }
        if (x < Rasterizer2D.clip_left) {
            int size = Rasterizer2D.clip_left - x;
            width -= size;
            x = Rasterizer2D.clip_left;
            src_pos += size;
            dst_pos += size;
            src_width += size;
            dst_width += size;
        }
        if (x + width > Rasterizer2D.clip_right) {
            int size = x + width - Rasterizer2D.clip_right;
            width -= size;
            src_width += size;
            dst_width += size;
        }
        if (width > 0 && height > 0) {
            render_transparent(Rasterizer2D.pixels, glyph_pixels[glyph], color, src_pos, dst_pos, width, height, dst_width, src_width, alpha);
        }
    }
    
    public static void render(int[] pixels, byte[] glyph_pixels, int color, int src_pos, int dst_pos, int width, int height, int dst_width, int src_width) {
        int length = -(width >> 2);
        width = -(width & 0x3);
        for (int column = -height; column < 0; column++) {
            for (int row = length; row < 0; row++) {
                if (glyph_pixels[src_pos++] != 0) {
                    pixels[dst_pos++] = color;
                } else {
                    dst_pos++;
                }
                if (glyph_pixels[src_pos++] != 0) {
                    pixels[dst_pos++] = color;
                } else {
                    dst_pos++;
                }
                if (glyph_pixels[src_pos++] != 0) {
                    pixels[dst_pos++] = color;
                } else {
                    dst_pos++;
                }
                if (glyph_pixels[src_pos++] != 0) {
                    pixels[dst_pos++] = color;
                } else {
                    dst_pos++;
                }
            }
            for (int index = width; index < 0; index++) {
                if (glyph_pixels[src_pos++] != 0) {
                    pixels[dst_pos++] = color;
                } else {
                    dst_pos++;
                }
            }
            dst_pos += dst_width;
            src_pos += src_width;
        }
    }

    public static void render_transparent(int[] pixels, byte[] glyph_pixels, int color, int src_pos, int dst_pos, int width, int height, int dst_width, int src_width, int alpha) {
        color = ((color & 0xff00ff) * alpha & ~0xff00ff) + ((color & 0xff00) * alpha & 0xff0000) >> 8;
        alpha = 256 - alpha;
        for (int column = -height; column < 0; column++) {
            for (int row = -width; row < 0; row++) {
                if (glyph_pixels[src_pos++] != 0) {
                    int src = pixels[dst_pos];
                    pixels[dst_pos++] = ((((src & 0xff00ff) * alpha & ~0xff00ff) + ((src & 0xff00) * alpha & 0xff0000)) >> 8) + color;
                } else {
                    dst_pos++;
                }
            }
            dst_pos += dst_width;
            src_pos += src_width;
        }
    }
    
    public int get_color(String s) {
        switch (s) {
            case "red":
                return 0xff0000;
            case "gre":
                return 65280;
            case "blu":
                return 255;
            case "yel":
                return 0xffff00;
            case "cya":
                return 65535;
            case "mag":
                return 0xff00ff;
            case "whi":
                return 0xffffff;
            case "bla":
                return 0;
            case "lre":
                return 0xff9040;
            case "dre":
                return 0x800000;
            case "dbl":
                return 128;
            case "or1":
                return 0xffb000;
            case "or2":
                return 0xff7000;
            case "or3":
                return 0xff3000;
            case "gr1":
                return 0xc0ff00;
            case "gr2":
                return 0x80ff00;
            case "gr3":
                return 0x40ff00;
            default:
                return -1;
        }
    }
    
    public static void release() {
        lt_string = null;
        gt_string = null;
        nbsp_string = null;
        shy_string = null;
        times_string = null;
        euro_string = null;
        copy_string = null;
        reg_string = null;
        set_image = null;
        line_break = null;
        set_color = null;
        close_color = null;
        set_transparency = null;
        close_transparency = null;
        set_underline = null;
        set_default_underline = null;
        close_underline = null;
        set_shadow = null;
        set_default_shadow = null;
        close_shadow = null;
        set_strikethrough = null;
        set_default_strikethrough = null;
        close_strikethrough = null;
        //aRSString_4143 = null;
        //split_strings = null;
    }
    
    static {
        set_transparency = "trans=";
        set_strikethrough = "str=";
        set_default_shadow = "shad";
        set_color = "col=";
        line_break = "br";
        set_default_strikethrough = "str";
        close_underline = "/u";
        set_image = "img=";
        set_clan = "clan=";
        set_shadow = "shad=";
        set_underline = "u=";
        close_color = "/col";
        set_default_underline = "u";
        close_transparency = "/trans";

        //aRSString_4143 = Integer.toString(100);
        nbsp_string = "nbsp";
        reg_string = "reg";
        times_string = "times";
        shy_string = "shy";
        copy_string = "copy";
        gt_string = "gt";
        euro_string = "euro";
        lt_string = "lt";
        
        default_trans = 256;
        default_shad = -1;
        anInt4175 = 0;
        shadow_color = -1;
        font_color = 0;
        default_color = 0;
        strikethrough_color = -1;
        //split_strings = new String[100];
        underline_color = -1;
        anInt4178 = 0;
        opacity = 256;
    }
    
    public int base_char_height = 0;
    public int anInt4142;
    public int anInt4144;
    public int[] kerning_y;
    public int[] glyph_height;
    public int[] kerning_x;
    public int[] glyph_width;
    
    public byte[] byte_unknown;//unused
    
    public byte[][] glyph_pixels;
    public int[] glyph_display_width;
    public static SimpleImage[] chat_emblems;
    public static SimpleImage[] clan_emblems;
    public static String nbsp_string;
    public static String set_transparency;
    public static String set_default_shadow;
    public static String close_shadow = "/shad";
    public static String gt_string;
    //public static String aRSString_4143;//unused
    public static String close_strikethrough = "/str";
    public static String euro_string;
    public static String set_color;
    public static String line_break;
    public static String set_strikethrough;
    public static String close_color;
    public static String set_image;
    public static String set_clan;
    public static String close_underline;
    public static String set_default_strikethrough;
    public static String set_shadow;
    public static String lt_string;
    public static String shy_string;
    public static String copy_string;
    public static String close_transparency;
    public static String times_string;
    public static String set_underline;
    public static String set_default_underline;
    public static String reg_string;
    //public static String[] split_strings;//unused
    public static int default_color;
    public static int shadow_color;
    public static int strikethrough_color;
    public static int default_trans;
    public static int anInt4175;//?
    public static int underline_color;
    public static int default_shad;
    public static int anInt4178;//?
    public static int opacity;
    public static int font_color;
    
}
