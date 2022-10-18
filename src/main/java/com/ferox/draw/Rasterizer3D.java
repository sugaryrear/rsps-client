package com.ferox.draw;

import com.ferox.ClientConstants;
import com.ferox.cache.Archive;
import com.ferox.cache.graphics.IndexedImage;
import com.ferox.scene.SceneGraph;

public final class Rasterizer3D extends Rasterizer2D {

    public static final int calc_vertex_x(int v_x, int v_z, int cos, int sin) {
        return v_x * cos + sin * v_z >> 16;
    }

    public static final int calc_vertex_z(int v_x, int v_z, int cos, int sin) {
        return cos * v_z - sin * v_x >> 16;
    }

    public static void release()
    {
        anIntArray1468 = null;
        SINE = null;
        COSINE = null;
        line_offsets = null;
        tex_images = null;

        transparent = null;
        avg_color = null;
        texel_pool = null;
        texel_cache = null;
        cache = null;
        HSL_TO_RGB = null;
        palletes = null;
    }

    public static void set_clip()
    {//set_clip
        line_offsets = new int[Rasterizer2D.height];
        for(int j = 0; j < Rasterizer2D.height; j++)
            line_offsets[j] = Rasterizer2D.width * j;



        center_x = Rasterizer2D.width / 2;//centerx
        center_y = Rasterizer2D.height / 2;//centery

        clipMidX2 = Rasterizer2D.width - center_x;
        clipNegativeMidX = -center_x;
        clipNegativeMidY = -center_y;
        clipMidY2 = Rasterizer2D.height - center_y;
    }

    public static void set_clip(int width, int height)
    {//set_clip(width, height)
        line_offsets = new int[height];
        for(int y = 0; y < height; y++)
            line_offsets[y] = width * y;

        center_x = width / 2;
        center_y = height / 2;
    }

    public static void reset_texels()
    {
        texel_pool = null;
        for(int j = 0; j < media_length; j++)
            texel_cache[j] = null;

    }

    public static void reset_textures()
    {
        if(texel_pool == null)
        {
            texel_indices = 20;
            /*if(low_memory)
                anIntArrayArray1478 = new int[anInt1477][16384];
            else
                anIntArrayArray1478 = new int[anInt1477][0x10000];
            */
            texel_pool = new int[texel_indices][][];
            for (int index = 0; index < texel_indices; index++) {//mipmap
                texel_pool[index] = new int[][] { new int[16384], new int[4096], new int[1024], new int[256], new int[64], new int[16], new int[4], new int[1] };
            }
            for(int index = 0; index < media_length; index++)
                texel_cache[index] = null;

        }
    }

    public static void init(Archive archive)
    {
        texture_indices = 0;
        for(int index = 0; index < media_length; index++) {
            try {
                tex_images[index] = new IndexedImage(archive, String.valueOf(index), 0);

                if(low_detail && tex_images[index].max_height == 128) {
                    tex_images[index].scale_half();
                } else {
                    tex_images[index].scale_full();
                }

                //aBackgroundArray1474s[index].method359();

                texture_indices++;
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    //setTextureFloorColor
    public static int set_floor(int id)
    {
        if(avg_color[id] != 0)
            return avg_color[id];

        int r = 0;
        int g = 0;
        int b = 0;
        int grayscale = palletes[id].length;
        for(int pixel = 0; pixel < grayscale; pixel++)
        {
            r += palletes[id][pixel] >> 16 & 0xff;
            g += palletes[id][pixel] >> 8 & 0xff;
            b += palletes[id][pixel] & 0xff;
        }

        int rgb = (r / grayscale << 16) + (g / grayscale << 8) + b / grayscale;
        rgb = adjust_brightness(rgb, 1.3999999999999999D);
        if(rgb == 0)
            rgb = 1;

        avg_color[id] = rgb;
        return rgb;
    }

    public static void reset_texel_pos(int id) {//resetAnimatedTextureTexelPosition
        try {
            if(texel_cache[id] == null) {
                return;
            }
            texel_pool[texel_indices++] = texel_cache[id];
            texel_cache[id] = null;
        } catch(Exception e) {

        }
    }

    public static int[][] get_texels(int id) {
        cache[id] = pos++;
        if(texel_cache[id] != null)
            return texel_cache[id];

        int texels[][];
        if(texel_indices > 0) {
            texels = texel_pool[--texel_indices];
            texel_pool[texel_indices] = null;
        } else {
            int last = 0;
            int target = -1;
            for(int index = 0; index < texture_indices; index++) {
                if(texel_cache[index] != null && (cache[index] < last || target == -1)) {
                    last = cache[index];
                    target = index;
                }
            }
            texels = texel_cache[target];
            texel_cache[target] = null;
        }
        texel_cache[id] = texels;
        IndexedImage background = tex_images[id];
        int[] palette = palletes[id];
        byte[] palette_color = background.palettePixels;
        /*if(low_memory) {
            aBooleanArray1475[textureId] = false;
            for(int pixel_index = 0; pixel_index < 4096; pixel_index++) {
                int source_color = texels[0][pixel_index] = palette[palette_color[pixel_index] & 0xff] & 0xf8f8ff;
                if(source_color == 0)
                    aBooleanArray1475[textureId] = true;

                texels[0][4096 + pixel_index] = source_color - (source_color >>> 3) & 0xf8f8ff;
                texels[0][8192 + pixel_index] = source_color - (source_color >>> 2) & 0xf8f8ff;
                texels[0][12288 + pixel_index] = source_color - (source_color >>> 2) - (source_color >>> 3) & 0xf8f8ff;
            }
        } else {*/

        if(background.width == 64) {
            for(int y = 0; y < 128; y++) {
                for(int x = 0; x < 128; x++) {
                    texels[0][x + (y << 7)] = palette[palette_color[(x >> 1) + ((y >> 1) << 6)] & 0xff];
                }
            }
        } else {
            for(int index = 0; index < 16384; index++) {
                texels[0][index] = palette[palette_color[index] & 0xff];
            }
        }
        transparent[id] = false;
        for(int pixel_index = 0; pixel_index < 16384; pixel_index++) {
            texels[0][pixel_index] &= 0xf8f8ff;
            int source_color = texels[0][pixel_index];
            if(source_color == 0) {
                transparent[id] = true;
            }
                /*texels[0][16384 + pixel_index] = source_color - (source_color >>> 3) & 0xf8f8ff;
                texels[0][32768 + pixel_index] = source_color - (source_color >>> 2) & 0xf8f8ff;
                texels[0][49152 + pixel_index] = source_color - (source_color >>> 2) - (source_color >>> 3) & 0xf8f8ff;
                */
        }

        for (int level = 1, size = 64; level < 8; level++) {
            int[] src = texels[level - 1];
            int[] dst = texels[level];// = new int[size * size];
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    double r = 0;
                    double g = 0;
                    double b = 0;
                    int step = 0;
                    for (int rgb :
                        new int[] {
                            src[x + (y * size << 1) << 1],
                            src[(x + (y * size << 1) << 1) + 1],
                            src[(x + (y * size << 1) << 1) + (size << 1)],
                            src[(x + (y * size << 1) << 1) + (size << 1) + 1]
                        }
                    ) {
                        if (rgb != 0) {
                            double r_ = (rgb >> 16 & 0xff) / 255d;
                            double g_ = (rgb >> 8 & 0xff) / 255d;
                            double b_ = (rgb & 0xff) / 255d;
                            r += r_ * r_;
                            g += g_ * g_;
                            b += b_ * b_;
                            step++;
                        }
                    }
                    if (step != 0) {
                        int dst_r = Math.round(255 * (float) Math.sqrt(r / step));
                        int dst_g = Math.round(255 * (float) Math.sqrt(g / step));
                        int dst_b = Math.round(255 * (float) Math.sqrt(b / step));
                        dst[x + y * size] = dst_r << 16 | dst_g << 8 | dst_b;
                    } else {
                        dst[x + y * size] = 0;
                    }
                }
            }
            size >>= 1;
        }

        //}
        return texels;
    }

    private static int scale;

    private static int get_texel_pos(int index) {
        int x = (index & 127) >> scale;
        int y = (index >> 7) >> scale;
        return x + (y << (7 - scale));
    }

    public static void adjust_brightness(double brightness) {
        adjust_brightness(brightness, 0, 512);
    }

    public static void adjust_brightness(double brightness, int start, int end)
    { //aka Rasterizer3D_buildPalette
        brightness += Math.random() * 0.03D - 0.015D;
        int size = start * 128;

        for(int step = start; step < end; step++)
        {
            double d1 = (double)(step / 8) / 64D + 0.0078125D;
            double d2 = (double)(step & 7) / 8D + 0.0625D;
            for(int k1 = 0; k1 < 128; k1++)
            {
                double d3 = (double)k1 / 128D;
                double r = d3;
                double g = d3;
                double b = d3;
                if(d2 != 0.0D)
                {
                    double d7;
                    if(d3 < 0.5D)
                        d7 = d3 * (1.0D + d2);
                    else
                        d7 = (d3 + d2) - d3 * d2;
                    double d8 = 2D * d3 - d7;
                    double d9 = d1 + 0.33333333333333331D;
                    if(d9 > 1.0D)
                        d9--;
                    double d10 = d1;
                    double d11 = d1 - 0.33333333333333331D;
                    if(d11 < 0.0D)
                        d11++;
                    if(6D * d9 < 1.0D)
                        r = d8 + (d7 - d8) * 6D * d9;
                    else
                    if(2D * d9 < 1.0D)
                        r = d7;
                    else
                    if(3D * d9 < 2D)
                        r = d8 + (d7 - d8) * (0.66666666666666663D - d9) * 6D;
                    else
                        r = d8;
                    if(6D * d10 < 1.0D)
                        g = d8 + (d7 - d8) * 6D * d10;
                    else
                    if(2D * d10 < 1.0D)
                        g = d7;
                    else
                    if(3D * d10 < 2D)
                        g = d8 + (d7 - d8) * (0.66666666666666663D - d10) * 6D;
                    else
                        g = d8;
                    if(6D * d11 < 1.0D)
                        b = d8 + (d7 - d8) * 6D * d11;
                    else
                    if(2D * d11 < 1.0D)
                        b = d7;
                    else
                    if(3D * d11 < 2D)
                        b = d8 + (d7 - d8) * (0.66666666666666663D - d11) * 6D;
                    else
                        b = d8;
                }
                int byteR = (int)(r * 256D);
                int byteG = (int)(g * 256D);
                int byteB = (int)(b * 256D);
                int rgb = (byteR << 16) + (byteG << 8) + byteB;
                rgb = adjust_brightness(rgb, brightness);
                if(rgb == 0)
                    rgb = 1;

                HSL_TO_RGB[size++] = rgb;
            }
        }

        for(int tex_id = 0; tex_id < media_length; tex_id++) {
            if(tex_images[tex_id] != null) {
                int palette[] = tex_images[tex_id].palette;
                palletes[tex_id] = new int[palette.length];
                for(int colourId = 0; colourId < palette.length; colourId++) {
                    palletes[tex_id][colourId] = adjust_brightness(palette[colourId], brightness);

                    if((palletes[tex_id][colourId] & 0xf8f8ff) == 0 && colourId != 0) {
                        palletes[tex_id][colourId] = 1;
                    }
                }
            }
        }

        for(int textureId = 0; textureId < media_length; textureId++) {
            reset_texel_pos(textureId);
        }
        Rasterizer3D.brightness = brightness;
    }

    public static int adjust_brightness(int rgb, double intensity) { //aka Rasterizer3D_brighten
        double r = (double) (rgb >> 16) / 256D;
        double g = (double) (rgb >> 8 & 0xff) / 256D;
        double b = (double) (rgb & 0xff) / 256D;
        r = Math.pow(r, intensity);
        g = Math.pow(g, intensity);
        b = Math.pow(b, intensity);
        int r_byte = (int) (r * 256D);
        int g_byte = (int) (g * 256D);
        int b_byte = (int) (b * 256D);
        return (r_byte << 16) + (g_byte << 8) + b_byte;
    }

    public static void drawShadedTriangle(int y_a, int y_b, int y_c, int x_a, int x_b, int x_c, int hsl1, int hsl2, int hsl3, float z_a, float z_b, float z_c) {
        if (z_a < 0 || z_b < 0 || z_c < 0)
            return;
        int rgb1 = HSL_TO_RGB[hsl1];
        int rgb2 = HSL_TO_RGB[hsl2];
        int rgb3 = HSL_TO_RGB[hsl3];
        int r1 = rgb1 >> 16 & 0xff;
        int g1 = rgb1 >> 8 & 0xff;
        int b1 = rgb1 & 0xff;
        int r2 = rgb2 >> 16 & 0xff;
        int g2 = rgb2 >> 8 & 0xff;
        int b2 = rgb2 & 0xff;
        int r3 = rgb3 >> 16 & 0xff;
        int g3 = rgb3 >> 8 & 0xff;
        int b3 = rgb3 & 0xff;
        int a_to_b = 0;
        int dr1 = 0;
        int dg1 = 0;
        int db1 = 0;
        if (y_b != y_a) {
            a_to_b = (x_b - x_a << 16) / (y_b - y_a);
            dr1 = (r2 - r1 << 16) / (y_b - y_a);
            dg1 = (g2 - g1 << 16) / (y_b - y_a);
            db1 = (b2 - b1 << 16) / (y_b - y_a);
        }
        int b_to_c = 0;
        int dr2 = 0;
        int dg2 = 0;
        int db2 = 0;
        if (y_c != y_b) {
            b_to_c = (x_c - x_b << 16) / (y_c - y_b);
            dr2 = (r3 - r2 << 16) / (y_c - y_b);
            dg2 = (g3 - g2 << 16) / (y_c - y_b);
            db2 = (b3 - b2 << 16) / (y_c - y_b);
        }
        int c_to_a = 0;
        int dr3 = 0;
        int dg3 = 0;
        int db3 = 0;
        if (y_c != y_a) {
            c_to_a = (x_a - x_c << 16) / (y_a - y_c);
            dr3 = (r1 - r3 << 16) / (y_a - y_c);
            dg3 = (g1 - g3 << 16) / (y_a - y_c);
            db3 = (b1 - b3 << 16) / (y_a - y_c);
        }
        float b_aX = x_b - x_a;
        float b_aY = y_b - y_a;
        float c_aX = x_c - x_a;
        float c_aY = y_c - y_a;
        float b_aZ = z_b - z_a;
        float c_aZ = z_c - z_a;

        float div = b_aX * c_aY - c_aX * b_aY;
        float depth_slope = (b_aZ * c_aY - c_aZ * b_aY) / div;
        float depth_increment = (c_aZ * b_aX - b_aZ * c_aX) / div;
        if(y_a <= y_b && y_a <= y_c) {
            if(y_a >= Rasterizer2D.clip_bottom) {
                return;
            }
            if(y_b > Rasterizer2D.clip_bottom) {
                y_b = Rasterizer2D.clip_bottom;
            }
            if(y_c > Rasterizer2D.clip_bottom) {
                y_c = Rasterizer2D.clip_bottom;
            }
            z_a = z_a - depth_slope * x_a + depth_slope;
            if(y_b < y_c) {
                x_c = x_a <<= 16;
                r3 = r1 <<= 16;
                g3 = g1 <<= 16;
                b3 = b1 <<= 16;
                if(y_a < 0) {
                    x_c -= c_to_a * y_a;
                    x_a -= a_to_b * y_a;
                    r3 -= dr3 * y_a;
                    g3 -= dg3 * y_a;
                    b3 -= db3 * y_a;
                    r1 -= dr1 * y_a;
                    g1 -= dg1 * y_a;
                    b1 -= db1 * y_a;
                    z_a -= depth_increment * y_a;
                    y_a = 0;
                }
                x_b <<= 16;
                r2 <<= 16;
                g2 <<= 16;
                b2 <<= 16;
                if(y_b < 0) {
                    x_b -= b_to_c * y_b;
                    r2 -= dr2 * y_b;
                    g2 -= dg2 * y_b;
                    b2 -= db2 * y_b;
                    y_b = 0;
                }
                if(y_a != y_b && c_to_a < a_to_b || y_a == y_b && c_to_a > b_to_c) {
                    y_c -= y_b;
                    y_b -= y_a;
                    for(y_a = line_offsets[y_a]; --y_b >= 0; y_a += Rasterizer2D.width) {
                        drawShadedScanline(Rasterizer2D.pixels, y_a, x_c >> 16, x_a >> 16, r3, g3, b3, r1, g1, b1, z_a, depth_slope);
                        x_c += c_to_a;
                        x_a += a_to_b;
                        r3 += dr3;
                        g3 += dg3;
                        b3 += db3;
                        r1 += dr1;
                        g1 += dg1;
                        b1 += db1;
                        z_a += depth_increment;
                    }
                    while(--y_c >= 0) {
                        drawShadedScanline(Rasterizer2D.pixels, y_a, x_c >> 16, x_b >> 16, r3, g3, b3, r2, g2, b2, z_a, depth_slope);
                        x_c += c_to_a;
                        x_b += b_to_c;
                        r3 += dr3;
                        g3 += dg3;
                        b3 += db3;
                        r2 += dr2;
                        g2 += dg2;
                        b2 += db2;
                        y_a += Rasterizer2D.width;
                        z_a += depth_increment;
                    }
                    return;
                }
                y_c -= y_b;
                y_b -= y_a;
                for(y_a = line_offsets[y_a]; --y_b >= 0; y_a += Rasterizer2D.width) {
                    drawShadedScanline(Rasterizer2D.pixels, y_a, x_a >> 16, x_c >> 16, r1, g1, b1, r3, g3, b3, z_a, depth_slope);
                    x_c += c_to_a;
                    x_a += a_to_b;
                    r3 += dr3;
                    g3 += dg3;
                    b3 += db3;
                    r1 += dr1;
                    g1 += dg1;
                    b1 += db1;
                    z_a += depth_increment;
                }
                while(--y_c >= 0) {
                    drawShadedScanline(Rasterizer2D.pixels, y_a, x_b >> 16, x_c >> 16, r2, g2, b2, r3, g3, b3, z_a, depth_slope);
                    x_c += c_to_a;
                    x_b += b_to_c;
                    r3 += dr3;
                    g3 += dg3;
                    b3 += db3;
                    r2 += dr2;
                    g2 += dg2;
                    b2 += db2;
                    y_a += Rasterizer2D.width;
                    z_a += depth_increment;
                }
                return;
            }
            x_b = x_a <<= 16;
            r2 = r1 <<= 16;
            g2 = g1 <<= 16;
            b2 = b1 <<= 16;
            if(y_a < 0) {
                x_b -= c_to_a * y_a;
                x_a -= a_to_b * y_a;
                r2 -= dr3 * y_a;
                g2 -= dg3 * y_a;
                b2 -= db3 * y_a;
                r1 -= dr1 * y_a;
                g1 -= dg1 * y_a;
                b1 -= db1 * y_a;
                z_a -= depth_increment * y_a;
                y_a = 0;
            }
            x_c <<= 16;
            r3 <<= 16;
            g3 <<= 16;
            b3 <<= 16;
            if(y_c < 0) {
                x_c -= b_to_c * y_c;
                r3 -= dr2 * y_c;
                g3 -= dg2 * y_c;
                b3 -= db2 * y_c;
                y_c = 0;
            }
            if(y_a != y_c && c_to_a < a_to_b || y_a == y_c && b_to_c > a_to_b) {
                y_b -= y_c;
                y_c -= y_a;
                for(y_a = line_offsets[y_a]; --y_c >= 0; y_a += Rasterizer2D.width) {
                    drawShadedScanline(Rasterizer2D.pixels, y_a, x_b >> 16, x_a >> 16, r2, g2, b2, r1, g1, b1, z_a, depth_slope);
                    x_b += c_to_a;
                    x_a += a_to_b;
                    r2 += dr3;
                    g2 += dg3;
                    b2 += db3;
                    r1 += dr1;
                    g1 += dg1;
                    b1 += db1;
                    z_a += depth_increment;
                }
                while(--y_b >= 0) {
                    drawShadedScanline(Rasterizer2D.pixels, y_a, x_c >> 16, x_a >> 16, r3, g3, b3, r1, g1, b1, z_a, depth_slope);
                    x_c += b_to_c;
                    x_a += a_to_b;
                    r3 += dr2;
                    g3 += dg2;
                    b3 += db2;
                    r1 += dr1;
                    g1 += dg1;
                    b1 += db1;
                    y_a += Rasterizer2D.width;
                    z_a += depth_increment;
                }
                return;
            }
            y_b -= y_c;
            y_c -= y_a;
            for(y_a = line_offsets[y_a]; --y_c >= 0; y_a += Rasterizer2D.width) {
                drawShadedScanline(Rasterizer2D.pixels, y_a, x_a >> 16, x_b >> 16, r1, g1, b1, r2, g2, b2, z_a, depth_slope);
                x_b += c_to_a;
                x_a += a_to_b;
                r2 += dr3;
                g2 += dg3;
                b2 += db3;
                r1 += dr1;
                g1 += dg1;
                b1 += db1;
                z_a += depth_increment;
            }
            while(--y_b >= 0) {
                drawShadedScanline(Rasterizer2D.pixels, y_a, x_a >> 16, x_c >> 16, r1, g1, b1, r3, g3, b3, z_a, depth_slope);
                x_c += b_to_c;
                x_a += a_to_b;
                r3 += dr2;
                g3 += dg2;
                b3 += db2;
                r1 += dr1;
                g1 += dg1;
                b1 += db1;
                y_a += Rasterizer2D.width;
                z_a += depth_increment;
            }
            return;
        }
        if(y_b <= y_c) {
            if(y_b >= Rasterizer2D.clip_bottom) {
                return;
            }
            if(y_c > Rasterizer2D.clip_bottom) {
                y_c = Rasterizer2D.clip_bottom;
            }
            if(y_a > Rasterizer2D.clip_bottom) {
                y_a = Rasterizer2D.clip_bottom;
            }
            z_b = z_b - depth_slope * x_b + depth_slope;
            if(y_c < y_a) {
                x_a = x_b <<= 16;
                r1 = r2 <<= 16;
                g1 = g2 <<= 16;
                b1 = b2 <<= 16;
                if(y_b < 0) {
                    x_a -= a_to_b * y_b;
                    x_b -= b_to_c * y_b;
                    r1 -= dr1 * y_b;
                    g1 -= dg1 * y_b;
                    b1 -= db1 * y_b;
                    r2 -= dr2 * y_b;
                    g2 -= dg2 * y_b;
                    b2 -= db2 * y_b;
                    z_b -= depth_increment * y_b;
                    y_b = 0;
                }
                x_c <<= 16;
                r3 <<= 16;
                g3 <<= 16;
                b3 <<= 16;
                if(y_c < 0) {
                    x_c -= c_to_a * y_c;
                    r3 -= dr3 * y_c;
                    g3 -= dg3 * y_c;
                    b3 -= db3 * y_c;
                    y_c = 0;
                }
                if(y_b != y_c && a_to_b < b_to_c || y_b == y_c && a_to_b > c_to_a) {
                    y_a -= y_c;
                    y_c -= y_b;
                    for(y_b = line_offsets[y_b]; --y_c >= 0; y_b += Rasterizer2D.width) {
                        drawShadedScanline(Rasterizer2D.pixels, y_b, x_a >> 16, x_b >> 16, r1, g1, b1, r2, g2, b2, z_b, depth_slope);
                        x_a += a_to_b;
                        x_b += b_to_c;
                        r1 += dr1;
                        g1 += dg1;
                        b1 += db1;
                        r2 += dr2;
                        g2 += dg2;
                        b2 += db2;
                        z_b += depth_increment;
                    }
                    while(--y_a >= 0) {
                        drawShadedScanline(Rasterizer2D.pixels, y_b, x_a >> 16, x_c >> 16, r1, g1, b1, r3, g3, b3, z_b, depth_slope);
                        x_a += a_to_b;
                        x_c += c_to_a;
                        r1 += dr1;
                        g1 += dg1;
                        b1 += db1;
                        r3 += dr3;
                        g3 += dg3;
                        b3 += db3;
                        y_b += Rasterizer2D.width;
                        z_b += depth_increment;
                    }
                    return;
                }
                y_a -= y_c;
                y_c -= y_b;
                for(y_b = line_offsets[y_b]; --y_c >= 0; y_b += Rasterizer2D.width) {
                    drawShadedScanline(Rasterizer2D.pixels, y_b, x_b >> 16, x_a >> 16, r2, g2, b2, r1, g1, b1, z_b, depth_slope);
                    x_a += a_to_b;
                    x_b += b_to_c;
                    r1 += dr1;
                    g1 += dg1;
                    b1 += db1;
                    r2 += dr2;
                    g2 += dg2;
                    b2 += db2;
                    z_b += depth_increment;
                }
                while(--y_a >= 0) {
                    drawShadedScanline(Rasterizer2D.pixels, y_b, x_c >> 16, x_a >> 16, r3, g3, b3, r1, g1, b1, z_b, depth_slope);
                    x_a += a_to_b;
                    x_c += c_to_a;
                    r1 += dr1;
                    g1 += dg1;
                    b1 += db1;
                    r3 += dr3;
                    g3 += dg3;
                    b3 += db3;
                    y_b += Rasterizer2D.width;
                    z_b += depth_increment;
                }
                return;
            }
            x_c = x_b <<= 16;
            r3 = r2 <<= 16;
            g3 = g2 <<= 16;
            b3 = b2 <<= 16;
            if(y_b < 0) {
                x_c -= a_to_b * y_b;
                x_b -= b_to_c * y_b;
                r3 -= dr1 * y_b;
                g3 -= dg1 * y_b;
                b3 -= db1 * y_b;
                r2 -= dr2 * y_b;
                g2 -= dg2 * y_b;
                b2 -= db2 * y_b;
                z_b -= depth_increment * y_b;
                y_b = 0;
            }
            x_a <<= 16;
            r1 <<= 16;
            g1 <<= 16;
            b1 <<= 16;
            if(y_a < 0) {
                x_a -= c_to_a * y_a;
                r1 -= dr3 * y_a;
                g1 -= dg3 * y_a;
                b1 -= db3 * y_a;
                y_a = 0;
            }
            if(a_to_b < b_to_c) {
                y_c -= y_a;
                y_a -= y_b;
                for(y_b = line_offsets[y_b]; --y_a >= 0; y_b += Rasterizer2D.width) {
                    drawShadedScanline(Rasterizer2D.pixels, y_b, x_c >> 16, x_b >> 16, r3, g3, b3, r2, g2, b2, z_b, depth_slope);
                    x_c += a_to_b;
                    x_b += b_to_c;
                    r3 += dr1;
                    g3 += dg1;
                    b3 += db1;
                    r2 += dr2;
                    g2 += dg2;
                    b2 += db2;
                    z_b += depth_increment;
                }
                while(--y_c >= 0) {
                    drawShadedScanline(Rasterizer2D.pixels, y_b, x_a >> 16, x_b >> 16, r1, g1, b1, r2, g2, b2, z_b, depth_slope);
                    x_a += c_to_a;
                    x_b += b_to_c;
                    r1 += dr3;
                    g1 += dg3;
                    b1 += db3;
                    r2 += dr2;
                    g2 += dg2;
                    b2 += db2;
                    y_b += Rasterizer2D.width;
                    z_b += depth_increment;
                }
                return;
            }
            y_c -= y_a;
            y_a -= y_b;
            for(y_b = line_offsets[y_b]; --y_a >= 0; y_b += Rasterizer2D.width) {
                drawShadedScanline(Rasterizer2D.pixels, y_b, x_b >> 16, x_c >> 16, r2, g2, b2, r3, g3, b3, z_b, depth_slope);
                x_c += a_to_b;
                x_b += b_to_c;
                r3 += dr1;
                g3 += dg1;
                b3 += db1;
                r2 += dr2;
                g2 += dg2;
                b2 += db2;
                z_b += depth_increment;
            }
            while(--y_c >= 0) {
                drawShadedScanline(Rasterizer2D.pixels, y_b, x_b >> 16, x_a >> 16, r2, g2, b2, r1, g1, b1, z_b, depth_slope);
                x_a += c_to_a;
                x_b += b_to_c;
                r1 += dr3;
                g1 += dg3;
                b1 += db3;
                r2 += dr2;
                g2 += dg2;
                b2 += db2;
                y_b += Rasterizer2D.width;
                z_b += depth_increment;
            }
            return;
        }
        if(y_c >= Rasterizer2D.clip_bottom) {
            return;
        }
        if(y_a > Rasterizer2D.clip_bottom) {
            y_a = Rasterizer2D.clip_bottom;
        }
        if(y_b > Rasterizer2D.clip_bottom) {
            y_b = Rasterizer2D.clip_bottom;
        }
        z_c = z_c - depth_slope * x_c + depth_slope;
        if(y_a < y_b) {
            x_b = x_c <<= 16;
            r2 = r3 <<= 16;
            g2 = g3 <<= 16;
            b2 = b3 <<= 16;
            if(y_c < 0) {
                x_b -= b_to_c * y_c;
                x_c -= c_to_a * y_c;
                r2 -= dr2 * y_c;
                g2 -= dg2 * y_c;
                b2 -= db2 * y_c;
                r3 -= dr3 * y_c;
                g3 -= dg3 * y_c;
                b3 -= db3 * y_c;
                z_c -= depth_increment * y_c;
                y_c = 0;
            }
            x_a <<= 16;
            r1 <<= 16;
            g1 <<= 16;
            b1 <<= 16;
            if(y_a < 0) {
                x_a -= a_to_b * y_a;
                r1 -= dr1 * y_a;
                g1 -= dg1 * y_a;
                b1 -= db1 * y_a;
                y_a = 0;
            }
            if(b_to_c < c_to_a) {
                y_b -= y_a;
                y_a -= y_c;
                for(y_c = line_offsets[y_c]; --y_a >= 0; y_c += Rasterizer2D.width) {
                    drawShadedScanline(Rasterizer2D.pixels, y_c, x_b >> 16, x_c >> 16, r2, g2, b2, r3, g3, b3, z_c, depth_slope);
                    x_b += b_to_c;
                    x_c += c_to_a;
                    r2 += dr2;
                    g2 += dg2;
                    b2 += db2;
                    r3 += dr3;
                    g3 += dg3;
                    b3 += db3;
                    z_c += depth_increment;
                }
                while(--y_b >= 0) {
                    drawShadedScanline(Rasterizer2D.pixels, y_c, x_b >> 16, x_a >> 16, r2, g2, b2, r1, g1, b1, z_c, depth_slope);
                    x_b += b_to_c;
                    x_a += a_to_b;
                    r2 += dr2;
                    g2 += dg2;
                    b2 += db2;
                    r1 += dr1;
                    g1 += dg1;
                    b1 += db1;
                    y_c += Rasterizer2D.width;
                    z_c += depth_increment;
                }
                return;
            }
            y_b -= y_a;
            y_a -= y_c;
            for(y_c = line_offsets[y_c]; --y_a >= 0; y_c += Rasterizer2D.width) {
                drawShadedScanline(Rasterizer2D.pixels, y_c, x_c >> 16, x_b >> 16, r3, g3, b3, r2, g2, b2, z_c, depth_slope);
                x_b += b_to_c;
                x_c += c_to_a;
                r2 += dr2;
                g2 += dg2;
                b2 += db2;
                r3 += dr3;
                g3 += dg3;
                b3 += db3;
                z_c += depth_increment;
            }
            while(--y_b >= 0) {
                drawShadedScanline(Rasterizer2D.pixels, y_c, x_a >> 16, x_b >> 16, r1, g1, b1, r2, g2, b2, z_c, depth_slope);
                x_b += b_to_c;
                x_a += a_to_b;
                r2 += dr2;
                g2 += dg2;
                b2 += db2;
                r1 += dr1;
                g1 += dg1;
                b1 += db1;
                z_c += depth_increment;
                y_c += Rasterizer2D.width;
            }
            return;
        }
        x_a = x_c <<= 16;
        r1 = r3 <<= 16;
        g1 = g3 <<= 16;
        b1 = b3 <<= 16;
        if(y_c < 0) {
            x_a -= b_to_c * y_c;
            x_c -= c_to_a * y_c;
            r1 -= dr2 * y_c;
            g1 -= dg2 * y_c;
            b1 -= db2 * y_c;
            r3 -= dr3 * y_c;
            g3 -= dg3 * y_c;
            b3 -= db3 * y_c;
            z_c -= depth_increment * y_c;
            y_c = 0;
        }
        x_b <<= 16;
        r2 <<= 16;
        g2 <<= 16;
        b2 <<= 16;
        if(y_b < 0) {
            x_b -= a_to_b * y_b;
            r2 -= dr1 * y_b;
            g2 -= dg1 * y_b;
            b2 -= db1 * y_b;
            y_b = 0;
        }
        if(b_to_c < c_to_a) {
            y_a -= y_b;
            y_b -= y_c;
            for(y_c = line_offsets[y_c]; --y_b >= 0; y_c += Rasterizer2D.width) {
                drawShadedScanline(Rasterizer2D.pixels, y_c, x_a >> 16, x_c >> 16, r1, g1, b1, r3, g3, b3, z_c, depth_slope);
                x_a += b_to_c;
                x_c += c_to_a;
                r1 += dr2;
                g1 += dg2;
                b1 += db2;
                r3 += dr3;
                g3 += dg3;
                b3 += db3;
                z_c += depth_increment;
            }
            while(--y_a >= 0) {
                drawShadedScanline(Rasterizer2D.pixels, y_c, x_b >> 16, x_c >> 16, r2, g2, b2, r3, g3, b3, z_c, depth_slope);
                x_b += a_to_b;
                x_c += c_to_a;
                r2 += dr1;
                g2 += dg1;
                b2 += db1;
                r3 += dr3;
                g3 += dg3;
                b3 += db3;
                z_c += depth_increment;
                y_c += Rasterizer2D.width;
            }
            return;
        }
        y_a -= y_b;
        y_b -= y_c;
        for(y_c = line_offsets[y_c]; --y_b >= 0; y_c += Rasterizer2D.width) {
            drawShadedScanline(Rasterizer2D.pixels, y_c, x_c >> 16, x_a >> 16, r3, g3, b3, r1, g1, b1, z_c, depth_slope);
            x_a += b_to_c;
            x_c += c_to_a;
            r1 += dr2;
            g1 += dg2;
            b1 += db2;
            r3 += dr3;
            g3 += dg3;
            b3 += db3;
            z_c += depth_increment;
        }
        while(--y_a >= 0) {
            drawShadedScanline(Rasterizer2D.pixels, y_c, x_c >> 16, x_b >> 16, r3, g3, b3, r2, g2, b2, z_c, depth_slope);
            x_b += a_to_b;
            x_c += c_to_a;
            r2 += dr1;
            g2 += dg1;
            b2 += db1;
            r3 += dr3;
            g3 += dg3;
            b3 += db3;
            y_c += Rasterizer2D.width;
            z_c += depth_increment;
        }
    }

    public static void drawShadedScanline(int[] dest, int offset, int x1, int x2, int r1, int g1, int b1, int r2, int g2, int b2, float depth, float depth_slope) {
        int n = x2 - x1;
        if (n <= 0) {
            return;
        }
        r2 = (r2 - r1) / n;
        g2 = (g2 - g1) / n;
        b2 = (b2 - b1) / n;


        if (testX) {
            if (x2 > Rasterizer2D.center_x) {
                n -= x2 - Rasterizer2D.center_x;
                x2 = Rasterizer2D.center_x;
            }
            if (x1 < 0) {
                n = x2;
                r1 -= x1 * r2;
                g1 -= x1 * g2;
                b1 -= x1 * b2;
                x1 = 0;
            }
        }
        if (x1 < x2) {
            offset += x1;
            depth += depth_slope * (float) x1;
            if (alpha == 0) {
                while (--n >= 0) {
                    if (true) {
                        dest[offset] = (r1 & 0xff0000) | (g1 >> 8 & 0xff00) | (b1 >> 16 & 0xff);
                        Rasterizer2D.depth_buffer[offset++] = depth;
                    }
                    depth += depth_slope;
                    r1 += r2;
                    g1 += g2;
                    b1 += b2;
                }
            } else {
                final int a1 = alpha;
                final int a2 = 256 - alpha;
                int rgb;
                int dst;
                while (--n >= 0) {
                    rgb = (r1 & 0xff0000) | (g1 >> 8 & 0xff00) | (b1 >> 16 & 0xff);
                    rgb = ((rgb & 0xff00ff) * a2 >> 8 & 0xff00ff) + ((rgb & 0xff00) * a2 >> 8 & 0xff00);
                    dst = dest[offset];
                    if(true) {
                        dest[offset] = rgb + ((dst & 0xff00ff) * a1 >> 8 & 0xff00ff) + ((dst & 0xff00) * a1 >> 8 & 0xff00);
                        Rasterizer2D.depth_buffer[offset] = depth;
                    }
                    depth += depth_slope;
                    r1 += r2;
                    g1 += g2;
                    b1 += b2;
                    offset++;
                }
            }
        }
    }

    /**
     * Probably should use {@link #drawFlatTriangle} instead
     */

    public static void drawFlatTriangle1(int y_a, int y_b, int y_c, int x_a, int x_b, int x_c, int k1, float z_a, float z_b, float z_c) {
        if (z_a < 0 || z_b < 0 || z_c < 0) {
            return;
        }
        int a_to_b = 0;
        if(y_b != y_a) {
            a_to_b = (x_b - x_a << 16) / (y_b - y_a);
        }
        int b_to_c = 0;
        if(y_c != y_b) {
            b_to_c = (x_c - x_b << 16) / (y_c - y_b);
        }
        int c_to_a = 0;
        if(y_c != y_a) {
            c_to_a = (x_a - x_c << 16) / (y_a - y_c);
        }
        float b_aX = x_b - x_a;
        float b_aY = y_b - y_a;
        float c_aX = x_c - x_a;
        float c_aY = y_c - y_a;
        float b_aZ = z_b - z_a;
        float c_aZ = z_c - z_a;

        float div = b_aX * c_aY - c_aX * b_aY;
        float depth_slope = (b_aZ * c_aY - c_aZ * b_aY) / div;
        float depth_increment = (c_aZ * b_aX - b_aZ * c_aX) / div;
        if(y_a <= y_b && y_a <= y_c) {
            if(y_a >= Rasterizer2D.clip_bottom)
                return;
            if(y_b > Rasterizer2D.clip_bottom)
                y_b = Rasterizer2D.clip_bottom;
            if(y_c > Rasterizer2D.clip_bottom)
                y_c = Rasterizer2D.clip_bottom;
            z_a = z_a - depth_slope * x_a + depth_slope;
            if(y_b < y_c)
            {
                x_c = x_a <<= 16;
                if(y_a < 0)
                {
                    x_c -= c_to_a * y_a;
                    x_a -= a_to_b * y_a;
                    z_a -= depth_increment * y_a;
                    y_a = 0;
                }
                x_b <<= 16;
                if(y_b < 0)
                {
                    x_b -= b_to_c * y_b;
                    y_b = 0;
                }
                if(y_a != y_b && c_to_a < a_to_b || y_a == y_b && c_to_a > b_to_c)
                {
                    y_c -= y_b;
                    y_b -= y_a;
                    for(y_a = line_offsets[y_a]; --y_b >= 0; y_a += Rasterizer2D.width)
                    {
                        drawFlatTexturedScanline(Rasterizer2D.pixels, y_a, k1, x_c >> 16, x_a >> 16, z_a, depth_slope);
                        x_c += c_to_a;
                        x_a += a_to_b;
                        z_a += depth_increment;
                    }

                    while(--y_c >= 0)
                    {
                        drawFlatTexturedScanline(Rasterizer2D.pixels, y_a, k1, x_c >> 16, x_b >> 16, z_a, depth_slope);
                        x_c += c_to_a;
                        x_b += b_to_c;
                        y_a += Rasterizer2D.width;
                        z_a += depth_increment;
                    }
                    return;
                }
                y_c -= y_b;
                y_b -= y_a;
                for(y_a = line_offsets[y_a]; --y_b >= 0; y_a += Rasterizer2D.width)
                {
                    drawFlatTexturedScanline(Rasterizer2D.pixels, y_a, k1, x_a >> 16, x_c >> 16, z_a, depth_slope);
                    x_c += c_to_a;
                    x_a += a_to_b;
                    z_a += depth_increment;
                }

                while(--y_c >= 0)
                {
                    drawFlatTexturedScanline(Rasterizer2D.pixels, y_a, k1, x_b >> 16, x_c >> 16, z_a, depth_slope);
                    x_c += c_to_a;
                    x_b += b_to_c;
                    y_a += Rasterizer2D.width;
                    z_a += depth_increment;
                }
                return;
            }
            x_b = x_a <<= 16;
            if(y_a < 0)
            {
                x_b -= c_to_a * y_a;
                x_a -= a_to_b * y_a;
                z_a -= depth_increment * y_a;
                y_a = 0;

            }
            x_c <<= 16;
            if(y_c < 0)
            {
                x_c -= b_to_c * y_c;
                y_c = 0;
            }
            if(y_a != y_c && c_to_a < a_to_b || y_a == y_c && b_to_c > a_to_b)
            {
                y_b -= y_c;
                y_c -= y_a;
                for(y_a = line_offsets[y_a]; --y_c >= 0; y_a += Rasterizer2D.width)
                {
                    drawFlatTexturedScanline(Rasterizer2D.pixels, y_a, k1, x_b >> 16, x_a >> 16, z_a, depth_slope);
                    z_a += depth_increment;
                    x_b += c_to_a;
                    x_a += a_to_b;
                }

                while(--y_b >= 0)
                {
                    drawFlatTexturedScanline(Rasterizer2D.pixels, y_a, k1, x_c >> 16, x_a >> 16, z_a, depth_slope);
                    z_a += depth_increment;
                    x_c += b_to_c;
                    x_a += a_to_b;
                    y_a += Rasterizer2D.width;
                }
                return;
            }
            y_b -= y_c;
            y_c -= y_a;
            for(y_a = line_offsets[y_a]; --y_c >= 0; y_a += Rasterizer2D.width)
            {
                drawFlatTexturedScanline(Rasterizer2D.pixels, y_a, k1, x_a >> 16, x_b >> 16, z_a, depth_slope);
                z_a += depth_increment;
                x_b += c_to_a;
                x_a += a_to_b;
            }

            while(--y_b >= 0)
            {
                drawFlatTexturedScanline(Rasterizer2D.pixels, y_a, k1, x_a >> 16, x_c >> 16, z_a, depth_slope);
                z_a += depth_increment;
                x_c += b_to_c;
                x_a += a_to_b;
                y_a += Rasterizer2D.width;
            }
            return;
        }
        if(y_b <= y_c)
        {
            if(y_b >= Rasterizer2D.clip_bottom)
                return;
            if(y_c > Rasterizer2D.clip_bottom)
                y_c = Rasterizer2D.clip_bottom;
            if(y_a > Rasterizer2D.clip_bottom)
                y_a = Rasterizer2D.clip_bottom;
            z_b = z_b - depth_slope * x_b + depth_slope;
            if(y_c < y_a)
            {
                x_a = x_b <<= 16;
                if(y_b < 0)
                {
                    x_a -= a_to_b * y_b;
                    x_b -= b_to_c * y_b;
                    z_b -= depth_increment * y_b;
                    y_b = 0;
                }
                x_c <<= 16;
                if(y_c < 0)
                {
                    x_c -= c_to_a * y_c;
                    y_c = 0;
                }
                if(y_b != y_c && a_to_b < b_to_c || y_b == y_c && a_to_b > c_to_a)
                {
                    y_a -= y_c;
                    y_c -= y_b;
                    for(y_b = line_offsets[y_b]; --y_c >= 0; y_b += Rasterizer2D.width)
                    {
                        drawFlatTexturedScanline(Rasterizer2D.pixels, y_b, k1, x_a >> 16, x_b >> 16, z_b, depth_slope);
                        z_b += depth_increment;
                        x_a += a_to_b;
                        x_b += b_to_c;
                    }

                    while(--y_a >= 0)
                    {
                        drawFlatTexturedScanline(Rasterizer2D.pixels, y_b, k1, x_a >> 16, x_c >> 16, z_b, depth_slope);
                        z_b += depth_increment;
                        x_a += a_to_b;
                        x_c += c_to_a;
                        y_b += Rasterizer2D.width;
                    }
                    return;
                }
                y_a -= y_c;
                y_c -= y_b;
                for(y_b = line_offsets[y_b]; --y_c >= 0; y_b += Rasterizer2D.width)
                {
                    drawFlatTexturedScanline(Rasterizer2D.pixels, y_b, k1, x_b >> 16, x_a >> 16, z_b, depth_slope);
                    z_b += depth_increment;
                    x_a += a_to_b;
                    x_b += b_to_c;
                }

                while(--y_a >= 0)
                {
                    drawFlatTexturedScanline(Rasterizer2D.pixels, y_b, k1, x_c >> 16, x_a >> 16, z_b, depth_slope);
                    z_b += depth_increment;
                    x_a += a_to_b;
                    x_c += c_to_a;
                    y_b += Rasterizer2D.width;
                }
                return;
            }
            x_c = x_b <<= 16;
            if(y_b < 0)
            {
                x_c -= a_to_b * y_b;
                x_b -= b_to_c * y_b;
                z_b -= depth_increment * y_b;
                y_b = 0;
            }
            x_a <<= 16;
            if(y_a < 0)
            {
                x_a -= c_to_a * y_a;
                y_a = 0;
            }
            if(a_to_b < b_to_c)
            {
                y_c -= y_a;
                y_a -= y_b;
                for(y_b = line_offsets[y_b]; --y_a >= 0; y_b += Rasterizer2D.width)
                {
                    drawFlatTexturedScanline(Rasterizer2D.pixels, y_b, k1, x_c >> 16, x_b >> 16, z_b, depth_slope);
                    z_b += depth_increment;
                    x_c += a_to_b;
                    x_b += b_to_c;
                }

                while(--y_c >= 0)
                {
                    drawFlatTexturedScanline(Rasterizer2D.pixels, y_b, k1, x_a >> 16, x_b >> 16, z_b, depth_slope);
                    z_b += depth_increment;
                    x_a += c_to_a;
                    x_b += b_to_c;
                    y_b += Rasterizer2D.width;
                }
                return;
            }
            y_c -= y_a;
            y_a -= y_b;
            for(y_b = line_offsets[y_b]; --y_a >= 0; y_b += Rasterizer2D.width)
            {
                drawFlatTexturedScanline(Rasterizer2D.pixels, y_b, k1, x_b >> 16, x_c >> 16, z_b, depth_slope);
                z_b += depth_increment;
                x_c += a_to_b;
                x_b += b_to_c;
            }

            while(--y_c >= 0)
            {
                drawFlatTexturedScanline(Rasterizer2D.pixels, y_b, k1, x_b >> 16, x_a >> 16, z_b, depth_slope);
                z_b += depth_increment;
                x_a += c_to_a;
                x_b += b_to_c;
                y_b += Rasterizer2D.width;
            }
            return;
        }
        if(y_c >= Rasterizer2D.clip_bottom)
            return;
        if(y_a > Rasterizer2D.clip_bottom)
            y_a = Rasterizer2D.clip_bottom;
        if(y_b > Rasterizer2D.clip_bottom)
            y_b = Rasterizer2D.clip_bottom;
        z_c = z_c - depth_slope * x_c + depth_slope;
        if(y_a < y_b)
        {
            x_b = x_c <<= 16;
            if(y_c < 0)
            {
                x_b -= b_to_c * y_c;
                x_c -= c_to_a * y_c;
                z_c -= depth_increment * y_c;
                y_c = 0;
            }
            x_a <<= 16;
            if(y_a < 0)
            {
                x_a -= a_to_b * y_a;
                y_a = 0;
            }
            if(b_to_c < c_to_a)
            {
                y_b -= y_a;
                y_a -= y_c;
                for(y_c = line_offsets[y_c]; --y_a >= 0; y_c += Rasterizer2D.width)
                {
                    drawFlatTexturedScanline(Rasterizer2D.pixels, y_c, k1, x_b >> 16, x_c >> 16, z_c, depth_slope);
                    z_c += depth_increment;
                    x_b += b_to_c;
                    x_c += c_to_a;
                }

                while(--y_b >= 0)
                {
                    drawFlatTexturedScanline(Rasterizer2D.pixels, y_c, k1, x_b >> 16, x_a >> 16, z_c, depth_slope);
                    z_c += depth_increment;
                    x_b += b_to_c;
                    x_a += a_to_b;
                    y_c += Rasterizer2D.width;
                }
                return;
            }
            y_b -= y_a;
            y_a -= y_c;
            for(y_c = line_offsets[y_c]; --y_a >= 0; y_c += Rasterizer2D.width)
            {
                drawFlatTexturedScanline(Rasterizer2D.pixels, y_c, k1, x_c >> 16, x_b >> 16, z_c, depth_slope);
                z_c += depth_increment;
                x_b += b_to_c;
                x_c += c_to_a;
            }

            while(--y_b >= 0)
            {
                drawFlatTexturedScanline(Rasterizer2D.pixels, y_c, k1, x_a >> 16, x_b >> 16, z_c, depth_slope);
                z_c += depth_increment;
                x_b += b_to_c;
                x_a += a_to_b;
                y_c += Rasterizer2D.width;
            }
            return;
        }
        x_a = x_c <<= 16;
        if(y_c < 0)
        {
            x_a -= b_to_c * y_c;
            x_c -= c_to_a * y_c;
            z_c -= depth_increment * y_c;
            y_c = 0;
        }
        x_b <<= 16;
        if(y_b < 0)
        {
            x_b -= a_to_b * y_b;
            y_b = 0;
        }
        if(b_to_c < c_to_a)
        {
            y_a -= y_b;
            y_b -= y_c;
            for(y_c = line_offsets[y_c]; --y_b >= 0; y_c += Rasterizer2D.width)
            {
                drawFlatTexturedScanline(Rasterizer2D.pixels, y_c, k1, x_a >> 16, x_c >> 16, z_c, depth_slope);
                z_c += depth_increment;
                x_a += b_to_c;
                x_c += c_to_a;
            }

            while(--y_a >= 0)
            {
                drawFlatTexturedScanline(Rasterizer2D.pixels, y_c, k1, x_b >> 16, x_c >> 16, z_c, depth_slope);
                z_c += depth_increment;
                x_b += a_to_b;
                x_c += c_to_a;
                y_c += Rasterizer2D.width;
            }
            return;
        }
        y_a -= y_b;
        y_b -= y_c;
        for(y_c = line_offsets[y_c]; --y_b >= 0; y_c += Rasterizer2D.width)
        {
            drawFlatTexturedScanline(Rasterizer2D.pixels, y_c, k1, x_c >> 16, x_a >> 16, z_c, depth_slope);
            z_c += depth_increment;
            x_a += b_to_c;
            x_c += c_to_a;
        }

        while(--y_a >= 0)
        {
            drawFlatTexturedScanline(Rasterizer2D.pixels, y_c, k1, x_c >> 16, x_b >> 16, z_c, depth_slope);
            z_c += depth_increment;
            x_b += a_to_b;
            x_c += c_to_a;
            y_c += Rasterizer2D.width;
        }
    }

    private static void drawFlatTexturedScanline(int dest[], int dest_off, int loops, int start_x, int end_x, float depth, float depth_slope) {
        int rgb;
        if(testX) {
            if(end_x > Rasterizer2D.center_x)
                end_x = Rasterizer2D.center_x;

            if(start_x < 0)
                start_x = 0;
        }
        if(start_x >= end_x)
            return;
        dest_off += start_x;
        rgb = end_x - start_x >> 2;
        depth += depth_slope * (float) start_x;
        if(alpha == 0)
        {
            while(--rgb >= 0)
            {
                for (int i = 0; i < 4; i++) {
                    if (true) {
                        dest[dest_off] = loops;
                        Rasterizer2D.depth_buffer[dest_off] = depth;
                    }
                    dest_off++;
                    depth += depth_slope;
                }
            }
            for(rgb = end_x - start_x & 3; --rgb >= 0;) {
                if (true) {
                    dest[dest_off] = loops;
                    Rasterizer2D.depth_buffer[dest_off] = depth;
                }
                dest_off++;
                depth += depth_slope;
            }
            return;
        }
        int dest_alpha = alpha;
        int src_alpha = 256 - alpha;
        loops = ((loops & 0xff00ff) * src_alpha >> 8 & 0xff00ff) + ((loops & 0xff00) * src_alpha >> 8 & 0xff00);
        while(--rgb >= 0)
        {
            for (int i = 0; i < 4; i++) {
                if (true) {
                    dest[dest_off] = loops + ((dest[dest_off] & 0xff00ff) * dest_alpha >> 8 & 0xff00ff) + ((dest[dest_off] & 0xff00) * dest_alpha >> 8 & 0xff00);
                    Rasterizer2D.depth_buffer[dest_off] = depth;
                }
                dest_off++;
                depth += depth_slope;
            }
        }
        for(rgb = end_x - start_x & 3; --rgb >= 0;) {
            if (true) {
                dest[dest_off] = loops + ((dest[dest_off] & 0xff00ff) * dest_alpha >> 8 & 0xff00ff) + ((dest[dest_off] & 0xff00) * dest_alpha >> 8 & 0xff00);
                Rasterizer2D.depth_buffer[dest_off] = depth;
            }
            dest_off++;
            depth += depth_slope;
        }
    }

    public static void drawTexturedTriangle1(
        int y_a, int y_b, int y_c,
        int x_a, int x_b, int x_c,
        int k1, int l1, int i2,
        int Px, int Mx, int Nx,
        int Pz, int Mz, int Nz,
        int Py, int My, int Ny,
        int texture_id,
        float z_a, float z_b, float z_c) {

        if (z_a < 0 || z_b < 0 || z_c < 0)
            return;

        int[] texels = get_texels(texture_id)[scale];
        if(texels == null) {
            drawShadedTriangle(y_a, y_b, y_c, x_a, x_b, x_c, light(texture_id, k1), light(texture_id, l1), light(texture_id, i2), z_a, z_b, z_c);
        } else {
            opaque = !transparent[texture_id];
            Mx = Px - Mx;
            Mz = Pz - Mz;
            My = Py - My;
            Nx -= Px;
            Nz -= Pz;
            Ny -= Py;
            int Oa = Nx * Pz - Nz * Px << (SceneGraph.view_dist == 9 ? 14 : 15);
            int Ha = Nz * Py - Ny * Pz << 8;
            int Va = Ny * Px - Nx * Py << 5;
            int Ob = Mx * Pz - Mz * Px << (SceneGraph.view_dist == 9 ? 14 : 15);
            int Hb = Mz * Py - My * Pz << 8;
            int Vb = My * Px - Mx * Py << 5;
            int Oc = Mz * Nx - Mx * Nz << (SceneGraph.view_dist == 9 ? 14 : 15);
            int Hc = My * Nz - Mz * Ny << 8;
            int Vc = Mx * Ny - My * Nx << 5;
            int a_to_b = 0;
            int grad_a_off = 0;
            if(y_b != y_a)
            {
                a_to_b = (x_b - x_a << 16) / (y_b - y_a);
                grad_a_off = (l1 - k1 << 16) / (y_b - y_a);
            }
            int b_to_c = 0;
            int grad_b_off = 0;
            if(y_c != y_b)
            {
                b_to_c = (x_c - x_b << 16) / (y_c - y_b);
                grad_b_off = (i2 - l1 << 16) / (y_c - y_b);
            }
            int c_to_a = 0;
            int grad_c_off = 0;
            if(y_c != y_a)
            {
                c_to_a = (x_a - x_c << 16) / (y_a - y_c);
                grad_c_off = (k1 - i2 << 16) / (y_a - y_c);
            }
            float b_aX = x_b - x_a;
            float b_aY = y_b - y_a;
            float c_aX = x_c - x_a;
            float c_aY = y_c - y_a;
            float b_aZ = z_b - z_a;
            float c_aZ = z_c - z_a;

            float div = b_aX * c_aY - c_aX * b_aY;
            float depth_slope = (b_aZ * c_aY - c_aZ * b_aY) / div;
            float depth_increment = (c_aZ * b_aX - b_aZ * c_aX) / div;
            if(y_a <= y_b && y_a <= y_c)
            {
                if(y_a >= Rasterizer2D.clip_bottom)
                    return;

                if(y_b > Rasterizer2D.clip_bottom)
                    y_b = Rasterizer2D.clip_bottom;

                if(y_c > Rasterizer2D.clip_bottom)
                    y_c = Rasterizer2D.clip_bottom;

                z_a = z_a - depth_slope * x_a + depth_slope;
                if(y_b < y_c)
                {
                    x_c = x_a <<= 16;
                    i2 = k1 <<= 16;
                    if(y_a < 0)
                    {
                        x_c -= c_to_a * y_a;
                        x_a -= a_to_b * y_a;
                        z_a -= depth_increment * y_a;
                        i2 -= grad_c_off * y_a;
                        k1 -= grad_a_off * y_a;
                        y_a = 0;
                    }
                    x_b <<= 16;
                    l1 <<= 16;
                    if(y_b < 0)
                    {
                        x_b -= b_to_c * y_b;
                        l1 -= grad_b_off * y_b;
                        y_b = 0;
                    }
                    int k8 = y_a - center_y;
                    Oa += Va * k8;
                    Ob += Vb * k8;
                    Oc += Vc * k8;
                    if(y_a != y_b && c_to_a < a_to_b || y_a == y_b && c_to_a > b_to_c)
                    {
                        y_c -= y_b;
                        y_b -= y_a;
                        y_a = line_offsets[y_a];
                        while(--y_b >= 0)
                        {
                            drawTexturedScanline(Rasterizer2D.pixels, texels, y_a, x_c >> 16, x_a >> 16, i2 >> 8, k1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_a, depth_slope);
                            x_c += c_to_a;
                            x_a += a_to_b;
                            z_a += depth_increment;
                            i2 += grad_c_off;
                            k1 += grad_a_off;
                            y_a += Rasterizer2D.width;
                            Oa += Va;
                            Ob += Vb;
                            Oc += Vc;
                        }
                        while(--y_c >= 0)
                        {
                            drawTexturedScanline(Rasterizer2D.pixels, texels, y_a, x_c >> 16, x_b >> 16, i2 >> 8, l1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_a, depth_slope);
                            x_c += c_to_a;
                            x_b += b_to_c;
                            z_a += depth_increment;
                            i2 += grad_c_off;
                            l1 += grad_b_off;
                            y_a += Rasterizer2D.width;
                            Oa += Va;
                            Ob += Vb;
                            Oc += Vc;
                        }
                        return;
                    }
                    y_c -= y_b;
                    y_b -= y_a;
                    y_a = line_offsets[y_a];
                    while(--y_b >= 0)
                    {
                        drawTexturedScanline(Rasterizer2D.pixels, texels, y_a, x_a >> 16, x_c >> 16, k1 >> 8, i2 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_a, depth_slope);
                        x_c += c_to_a;
                        x_a += a_to_b;
                        z_a += depth_increment;
                        i2 += grad_c_off;
                        k1 += grad_a_off;
                        y_a += Rasterizer2D.width;
                        Oa += Va;
                        Ob += Vb;
                        Oc += Vc;
                    }
                    while(--y_c >= 0)
                    {
                        drawTexturedScanline(Rasterizer2D.pixels, texels, y_a, x_b >> 16, x_c >> 16, l1 >> 8, i2 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_a, depth_slope);
                        x_c += c_to_a;
                        x_b += b_to_c;
                        z_a += depth_increment;
                        i2 += grad_c_off;
                        l1 += grad_b_off;
                        y_a += Rasterizer2D.width;
                        Oa += Va;
                        Ob += Vb;
                        Oc += Vc;
                    }
                    return;
                }
                x_b = x_a <<= 16;
                l1 = k1 <<= 16;
                if(y_a < 0)
                {
                    x_b -= c_to_a * y_a;
                    x_a -= a_to_b * y_a;
                    z_a -= depth_increment * y_a;
                    l1 -= grad_c_off * y_a;
                    k1 -= grad_a_off * y_a;
                    y_a = 0;
                }
                x_c <<= 16;
                i2 <<= 16;
                if(y_c < 0)
                {
                    x_c -= b_to_c * y_c;
                    i2 -= grad_b_off * y_c;
                    y_c = 0;
                }
                int l8 = y_a - center_y;
                Oa += Va * l8;
                Ob += Vb * l8;
                Oc += Vc * l8;
                if(y_a != y_c && c_to_a < a_to_b || y_a == y_c && b_to_c > a_to_b)
                {
                    y_b -= y_c;
                    y_c -= y_a;
                    y_a = line_offsets[y_a];
                    while(--y_c >= 0)
                    {
                        drawTexturedScanline(Rasterizer2D.pixels, texels, y_a, x_b >> 16, x_a >> 16, l1 >> 8, k1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_a, depth_slope);
                        x_b += c_to_a;
                        x_a += a_to_b;
                        l1 += grad_c_off;
                        k1 += grad_a_off;
                        z_a += depth_increment;
                        y_a += Rasterizer2D.width;
                        Oa += Va;
                        Ob += Vb;
                        Oc += Vc;
                    }
                    while(--y_b >= 0)
                    {
                        drawTexturedScanline(Rasterizer2D.pixels, texels, y_a, x_c >> 16, x_a >> 16, i2 >> 8, k1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_a, depth_slope);
                        x_c += b_to_c;
                        x_a += a_to_b;
                        i2 += grad_b_off;
                        k1 += grad_a_off;
                        z_a += depth_increment;
                        y_a += Rasterizer2D.width;
                        Oa += Va;
                        Ob += Vb;
                        Oc += Vc;
                    }
                    return;
                }
                y_b -= y_c;
                y_c -= y_a;
                y_a = line_offsets[y_a];
                while(--y_c >= 0)
                {
                    drawTexturedScanline(Rasterizer2D.pixels, texels, y_a, x_a >> 16, x_b >> 16, k1 >> 8, l1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_a, depth_slope);
                    x_b += c_to_a;
                    x_a += a_to_b;
                    l1 += grad_c_off;
                    k1 += grad_a_off;
                    z_a += depth_increment;
                    y_a += Rasterizer2D.width;
                    Oa += Va;
                    Ob += Vb;
                    Oc += Vc;
                }
                while(--y_b >= 0)
                {
                    drawTexturedScanline(Rasterizer2D.pixels, texels, y_a, x_a >> 16, x_c >> 16, k1 >> 8, i2 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_a, depth_slope);
                    x_c += b_to_c;
                    x_a += a_to_b;
                    i2 += grad_b_off;
                    k1 += grad_a_off;
                    z_a += depth_increment;
                    y_a += Rasterizer2D.width;
                    Oa += Va;
                    Ob += Vb;
                    Oc += Vc;
                }
                return;
            }
            if(y_b <= y_c)
            {
                if(y_b >= Rasterizer2D.clip_bottom)
                    return;
                if(y_c > Rasterizer2D.clip_bottom)
                    y_c = Rasterizer2D.clip_bottom;
                if(y_a > Rasterizer2D.clip_bottom)
                    y_a = Rasterizer2D.clip_bottom;
                z_b = z_b - depth_slope * x_b + depth_slope;
                if(y_c < y_a)
                {
                    x_a = x_b <<= 16;
                    k1 = l1 <<= 16;
                    if(y_b < 0)
                    {
                        x_a -= a_to_b * y_b;
                        x_b -= b_to_c * y_b;
                        z_b -= depth_increment * y_b;
                        k1 -= grad_a_off * y_b;
                        l1 -= grad_b_off * y_b;
                        y_b = 0;
                    }
                    x_c <<= 16;
                    i2 <<= 16;
                    if(y_c < 0)
                    {
                        x_c -= c_to_a * y_c;
                        i2 -= grad_c_off * y_c;
                        y_c = 0;
                    }
                    int i9 = y_b - center_y;
                    Oa += Va * i9;
                    Ob += Vb * i9;
                    Oc += Vc * i9;
                    if(y_b != y_c && a_to_b < b_to_c || y_b == y_c && a_to_b > c_to_a)
                    {
                        y_a -= y_c;
                        y_c -= y_b;
                        y_b = line_offsets[y_b];
                        while(--y_c >= 0)
                        {
                            drawTexturedScanline(Rasterizer2D.pixels, texels, y_b, x_a >> 16, x_b >> 16, k1 >> 8, l1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_b, depth_slope);
                            x_a += a_to_b;
                            x_b += b_to_c;
                            k1 += grad_a_off;
                            l1 += grad_b_off;
                            z_b += depth_increment;
                            y_b += Rasterizer2D.width;
                            Oa += Va;
                            Ob += Vb;
                            Oc += Vc;
                        }
                        while(--y_a >= 0)
                        {
                            drawTexturedScanline(Rasterizer2D.pixels, texels, y_b, x_a >> 16, x_c >> 16, k1 >> 8, i2 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_b, depth_slope);
                            x_a += a_to_b;
                            x_c += c_to_a;
                            k1 += grad_a_off;
                            i2 += grad_c_off;
                            z_b += depth_increment;
                            y_b += Rasterizer2D.width;
                            Oa += Va;
                            Ob += Vb;
                            Oc += Vc;
                        }
                        return;
                    }
                    y_a -= y_c;
                    y_c -= y_b;
                    y_b = line_offsets[y_b];
                    while(--y_c >= 0)
                    {
                        drawTexturedScanline(Rasterizer2D.pixels, texels, y_b, x_b >> 16, x_a >> 16, l1 >> 8, k1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_b, depth_slope);
                        x_a += a_to_b;
                        x_b += b_to_c;
                        k1 += grad_a_off;
                        l1 += grad_b_off;
                        z_b += depth_increment;
                        y_b += Rasterizer2D.width;
                        Oa += Va;
                        Ob += Vb;
                        Oc += Vc;
                    }
                    while(--y_a >= 0)
                    {
                        drawTexturedScanline(Rasterizer2D.pixels, texels, y_b, x_c >> 16, x_a >> 16, i2 >> 8, k1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_b, depth_slope);
                        x_a += a_to_b;
                        x_c += c_to_a;
                        k1 += grad_a_off;
                        i2 += grad_c_off;
                        z_b += depth_increment;
                        y_b += Rasterizer2D.width;
                        Oa += Va;
                        Ob += Vb;
                        Oc += Vc;
                    }
                    return;
                }
                x_c = x_b <<= 16;
                i2 = l1 <<= 16;
                if(y_b < 0)
                {
                    x_c -= a_to_b * y_b;
                    x_b -= b_to_c * y_b;
                    z_b -= depth_increment * y_b;
                    i2 -= grad_a_off * y_b;
                    l1 -= grad_b_off * y_b;
                    y_b = 0;
                }
                x_a <<= 16;
                k1 <<= 16;
                if(y_a < 0)
                {
                    x_a -= c_to_a * y_a;
                    k1 -= grad_c_off * y_a;
                    y_a = 0;
                }
                int j9 = y_b - center_y;
                Oa += Va * j9;
                Ob += Vb * j9;
                Oc += Vc * j9;
                if(a_to_b < b_to_c)
                {
                    y_c -= y_a;
                    y_a -= y_b;
                    y_b = line_offsets[y_b];
                    while(--y_a >= 0)
                    {
                        drawTexturedScanline(Rasterizer2D.pixels, texels, y_b, x_c >> 16, x_b >> 16, i2 >> 8, l1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_b, depth_slope);
                        x_c += a_to_b;
                        x_b += b_to_c;
                        i2 += grad_a_off;
                        l1 += grad_b_off;
                        z_b += depth_increment;
                        y_b += Rasterizer2D.width;
                        Oa += Va;
                        Ob += Vb;
                        Oc += Vc;
                    }
                    while(--y_c >= 0)
                    {
                        drawTexturedScanline(Rasterizer2D.pixels, texels, y_b, x_a >> 16, x_b >> 16, k1 >> 8, l1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_b, depth_slope);
                        x_a += c_to_a;
                        x_b += b_to_c;
                        k1 += grad_c_off;
                        l1 += grad_b_off;
                        z_b += depth_increment;
                        y_b += Rasterizer2D.width;
                        Oa += Va;
                        Ob += Vb;
                        Oc += Vc;
                    }
                    return;
                }
                y_c -= y_a;
                y_a -= y_b;
                y_b = line_offsets[y_b];
                while(--y_a >= 0)
                {
                    drawTexturedScanline(Rasterizer2D.pixels, texels, y_b, x_b >> 16, x_c >> 16, l1 >> 8, i2 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_b, depth_slope);
                    x_c += a_to_b;
                    x_b += b_to_c;
                    i2 += grad_a_off;
                    l1 += grad_b_off;
                    z_b += depth_increment;
                    y_b += Rasterizer2D.width;
                    Oa += Va;
                    Ob += Vb;
                    Oc += Vc;
                }
                while(--y_c >= 0)
                {
                    drawTexturedScanline(Rasterizer2D.pixels, texels, y_b, x_b >> 16, x_a >> 16, l1 >> 8, k1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_b, depth_slope);
                    x_a += c_to_a;
                    x_b += b_to_c;
                    k1 += grad_c_off;
                    l1 += grad_b_off;
                    z_b += depth_increment;
                    y_b += Rasterizer2D.width;
                    Oa += Va;
                    Ob += Vb;
                    Oc += Vc;
                }
                return;
            }
            if(y_c >= Rasterizer2D.clip_bottom)
                return;
            if(y_a > Rasterizer2D.clip_bottom)
                y_a = Rasterizer2D.clip_bottom;
            if(y_b > Rasterizer2D.clip_bottom)
                y_b = Rasterizer2D.clip_bottom;
            z_c = z_c - depth_slope * x_c + depth_slope;
            if(y_a < y_b)
            {
                x_b = x_c <<= 16;
                l1 = i2 <<= 16;
                if(y_c < 0)
                {
                    x_b -= b_to_c * y_c;
                    x_c -= c_to_a * y_c;
                    z_c -= depth_increment * y_c;
                    l1 -= grad_b_off * y_c;
                    i2 -= grad_c_off * y_c;
                    y_c = 0;
                }
                x_a <<= 16;
                k1 <<= 16;
                if(y_a < 0)
                {
                    x_a -= a_to_b * y_a;
                    k1 -= grad_a_off * y_a;
                    y_a = 0;
                }
                int k9 = y_c - center_y;
                Oa += Va * k9;
                Ob += Vb * k9;
                Oc += Vc * k9;
                if(b_to_c < c_to_a)
                {
                    y_b -= y_a;
                    y_a -= y_c;
                    y_c = line_offsets[y_c];
                    while(--y_a >= 0)
                    {
                        drawTexturedScanline(Rasterizer2D.pixels, texels, y_c, x_b >> 16, x_c >> 16, l1 >> 8, i2 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_c, depth_slope);
                        x_b += b_to_c;
                        x_c += c_to_a;
                        l1 += grad_b_off;
                        i2 += grad_c_off;
                        z_c += depth_increment;
                        y_c += Rasterizer2D.width;
                        Oa += Va;
                        Ob += Vb;
                        Oc += Vc;
                    }
                    while(--y_b >= 0)
                    {
                        drawTexturedScanline(Rasterizer2D.pixels, texels, y_c, x_b >> 16, x_a >> 16, l1 >> 8, k1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_c, depth_slope);
                        x_b += b_to_c;
                        x_a += a_to_b;
                        l1 += grad_b_off;
                        k1 += grad_a_off;
                        z_c += depth_increment;
                        y_c += Rasterizer2D.width;
                        Oa += Va;
                        Ob += Vb;
                        Oc += Vc;
                    }
                    return;
                }
                y_b -= y_a;
                y_a -= y_c;
                y_c = line_offsets[y_c];
                while(--y_a >= 0)
                {
                    drawTexturedScanline(Rasterizer2D.pixels, texels, y_c, x_c >> 16, x_b >> 16, i2 >> 8, l1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_c, depth_slope);
                    x_b += b_to_c;
                    x_c += c_to_a;
                    l1 += grad_b_off;
                    i2 += grad_c_off;
                    z_c += depth_increment;
                    y_c += Rasterizer2D.width;
                    Oa += Va;
                    Ob += Vb;
                    Oc += Vc;
                }
                while(--y_b >= 0)
                {
                    drawTexturedScanline(Rasterizer2D.pixels, texels, y_c, x_a >> 16, x_b >> 16, k1 >> 8, l1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_c, depth_slope);
                    x_b += b_to_c;
                    x_a += a_to_b;
                    l1 += grad_b_off;
                    k1 += grad_a_off;
                    z_c += depth_increment;
                    y_c += Rasterizer2D.width;
                    Oa += Va;
                    Ob += Vb;
                    Oc += Vc;
                }
                return;
            }
            x_a = x_c <<= 16;
            k1 = i2 <<= 16;
            if(y_c < 0)
            {
                x_a -= b_to_c * y_c;
                x_c -= c_to_a * y_c;
                z_c -= depth_increment * y_c;
                k1 -= grad_b_off * y_c;
                i2 -= grad_c_off * y_c;
                y_c = 0;
            }
            x_b <<= 16;
            l1 <<= 16;
            if(y_b < 0)
            {
                x_b -= a_to_b * y_b;
                l1 -= grad_a_off * y_b;
                y_b = 0;
            }
            int l9 = y_c - center_y;
            Oa += Va * l9;
            Ob += Vb * l9;
            Oc += Vc * l9;
            if(b_to_c < c_to_a)
            {
                y_a -= y_b;
                y_b -= y_c;
                y_c = line_offsets[y_c];
                while(--y_b >= 0)
                {
                    drawTexturedScanline(Rasterizer2D.pixels, texels, y_c, x_a >> 16, x_c >> 16, k1 >> 8, i2 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_c, depth_slope);
                    x_a += b_to_c;
                    x_c += c_to_a;
                    k1 += grad_b_off;
                    i2 += grad_c_off;
                    z_c += depth_increment;
                    y_c += Rasterizer2D.width;
                    Oa += Va;
                    Ob += Vb;
                    Oc += Vc;
                }
                while(--y_a >= 0)
                {
                    drawTexturedScanline(Rasterizer2D.pixels, texels, y_c, x_b >> 16, x_c >> 16, l1 >> 8, i2 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_c, depth_slope);
                    x_b += a_to_b;
                    x_c += c_to_a;
                    l1 += grad_a_off;
                    i2 += grad_c_off;
                    z_c += depth_increment;
                    y_c += Rasterizer2D.width;
                    Oa += Va;
                    Ob += Vb;
                    Oc += Vc;
                }
                return;
            }
            y_a -= y_b;
            y_b -= y_c;
            y_c = line_offsets[y_c];
            while(--y_b >= 0)
            {
                drawTexturedScanline(Rasterizer2D.pixels, texels, y_c, x_c >> 16, x_a >> 16, i2 >> 8, k1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_c, depth_slope);
                x_a += b_to_c;
                x_c += c_to_a;
                k1 += grad_b_off;
                i2 += grad_c_off;
                z_c += depth_increment;
                y_c += Rasterizer2D.width;
                Oa += Va;
                Ob += Vb;
                Oc += Vc;
            }
            while(--y_a >= 0)
            {
                drawTexturedScanline(Rasterizer2D.pixels, texels, y_c, x_c >> 16, x_b >> 16, i2 >> 8, l1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_c, depth_slope);
                x_b += a_to_b;
                x_c += c_to_a;
                l1 += grad_a_off;
                i2 += grad_c_off;
                z_c += depth_increment;
                y_c += Rasterizer2D.width;
                Oa += Va;
                Ob += Vb;
                Oc += Vc;
            }
        }
    }

    public static void drawTexturedScanline(int dest[], int texture[], int dest_off, int start_x, int end_x, int shadeValue, int gradient, int l1, int i2, int j2, int k2, int l2, int i3, float depth, float depth_slope) {
        int rgb = 0;
        int loops = 0;
        if (start_x >= end_x)
            return;
        int j3;
        int k3;
        if (testX) {
            j3 = (gradient - shadeValue) / (end_x - start_x);
            if (end_x > Rasterizer2D.center_x)
                end_x = Rasterizer2D.center_x;
            if (start_x < 0) {
                shadeValue -= start_x * j3;
                start_x = 0;
            }
            if (start_x >= end_x)
                return;
            k3 = end_x - start_x >> 3;
            j3 <<= 12;
            shadeValue <<= 9;
        } else {
            if (end_x - start_x > 7) {
                k3 = end_x - start_x >> 3;
                j3 = (gradient - shadeValue) * anIntArray1468[k3] >> 6;
            } else {
                k3 = 0;
                j3 = 0;
            }
            shadeValue <<= 9;
        }
        dest_off += start_x;
        depth += depth_slope * (float) start_x;
        if (low_detail) {//low_detail
            int i4 = 0;
            int k4 = 0;
            int k6 = start_x - center_x;
            l1 += (k2 >> 3) * k6;
            i2 += (l2 >> 3) * k6;
            j2 += (i3 >> 3) * k6;
            int i5 = j2 >> 12;
            if (i5 != 0) {
                rgb = l1 / i5;
                loops = i2 / i5;
                if (rgb < 0)
                    rgb = 0;
                else
                if (rgb > 4032)
                    rgb = 4032;
            }
            l1 += k2;
            i2 += l2;
            j2 += i3;
            i5 = j2 >> 12;
            if (i5 != 0) {
                i4 = l1 / i5;
                k4 = i2 / i5;
                if (i4 < 7)
                    i4 = 7;
                else
                if (i4 > 4032)
                    i4 = 4032;
            }
            int i7 = i4 - rgb >> 3;
            int k7 = k4 - loops >> 3;
            rgb += (shadeValue & 0x600000) >> 3;
            int i8 = shadeValue >> 23;
            if (opaque) {//opaque
                while (k3-- > 0) {
                    for (int i = 0; i < 8; i++) {
                        if (true) {
                            dest[dest_off] = texture[(loops & 0xfc0) + (rgb >> 6)] >>> i8;
                            Rasterizer2D.depth_buffer[dest_off] = depth;
                        }
                        dest_off++;
                        depth += depth_slope;
                        rgb += i7;
                        loops += k7;
                    }
                    rgb = i4;
                    loops = k4;
                    l1 += k2;
                    i2 += l2;
                    j2 += i3;
                    int j5 = j2 >> 12;
                    if (j5 != 0) {
                        i4 = l1 / j5;
                        k4 = i2 / j5;
                        if (i4 < 7)
                            i4 = 7;
                        else
                        if (i4 > 4032)
                            i4 = 4032;
                    }
                    i7 = i4 - rgb >> 3;
                    k7 = k4 - loops >> 3;
                    shadeValue += j3;
                    rgb += (shadeValue & 0x600000) >> 3;
                    i8 = shadeValue >> 23;
                }
                for (k3 = end_x - start_x & 7; k3-- > 0;) {
                    if (true) {
                        dest[dest_off] = texture[(loops & 0xfc0) + (rgb >> 6)] >>> i8;
                        Rasterizer2D.depth_buffer[dest_off] = depth;
                    }
                    dest_off++;
                    depth += depth_slope;
                    rgb += i7;
                    loops += k7;
                }

                return;
            }
            while (k3-- > 0) {
                int k8;
                for (int i = 0; i < 8; i++) {
                    if ((k8 = texture[(loops & 0xfc0) + (rgb >> 6)] >>> i8) != 0 && true) {
                        dest[dest_off] = k8;
                        Rasterizer2D.depth_buffer[dest_off] = depth;
                    }
                    dest_off++;
                    depth += depth_slope;
                    rgb += i7;
                    loops += k7;
                }

                rgb = i4;
                loops = k4;
                l1 += k2;
                i2 += l2;
                j2 += i3;
                int k5 = j2 >> 12;
                if (k5 != 0) {
                    i4 = l1 / k5;
                    k4 = i2 / k5;
                    if (i4 < 7)
                        i4 = 7;
                    else
                    if (i4 > 4032)
                        i4 = 4032;
                }
                i7 = i4 - rgb >> 3;
                k7 = k4 - loops >> 3;
                shadeValue += j3;
                rgb += (shadeValue & 0x600000) >> 3;
                i8 = shadeValue >> 23;
            }
            for (k3 = end_x - start_x & 7; k3-- > 0;) {
                int l8;
                if ((l8 = texture[(loops & 0xfc0) + (rgb >> 6)] >>> i8) != 0 && true) {
                    dest[dest_off] = l8;
                    Rasterizer2D.depth_buffer[dest_off] = depth;
                }
                dest_off++;
                depth += depth_slope;
                rgb += i7;
                loops += k7;
            }

            return;
        }
        //highmem
        int j4 = 0;
        int l4 = 0;
        int l6 = start_x - center_x;
        l1 += (k2 >> 3) * l6;
        i2 += (l2 >> 3) * l6;
        j2 += (i3 >> 3) * l6;
        int l5 = j2 >> 14;
        if (l5 != 0) {
            rgb = l1 / l5;
            loops = i2 / l5;
            if (rgb < 0)
                rgb = 0;
            else
            if (rgb > 16256)
                rgb = 16256;
        }
        l1 += k2;
        i2 += l2;
        j2 += i3;
        l5 = j2 >> 14;
        if (l5 != 0) {
            j4 = l1 / l5;
            l4 = i2 / l5;
            if (j4 < 7)
                j4 = 7;
            else
            if (j4 > 16256)
                j4 = 16256;
        }
        int j7 = j4 - rgb >> 3;
        int l7 = l4 - loops >> 3;
        rgb += shadeValue & 0x600000;
        int j8 = shadeValue >> 23;
        if (opaque) {
            while (k3-- > 0) {
                for (int i = 0; i < 8; i++) {
                    if (true) {
                        dest[dest_off] = texture[(loops & 0x3f80) + (rgb >> 7)] >>> j8;
                        Rasterizer2D.depth_buffer[dest_off] = depth;
                    }
                    depth += depth_slope;
                    dest_off++;
                    rgb += j7;
                    loops += l7;
                }
                rgb = j4;
                loops = l4;
                l1 += k2;
                i2 += l2;
                j2 += i3;
                int i6 = j2 >> 14;
                if (i6 != 0) {
                    j4 = l1 / i6;
                    l4 = i2 / i6;
                    if (j4 < 7)
                        j4 = 7;
                    else
                    if (j4 > 16256)
                        j4 = 16256;
                }
                j7 = j4 - rgb >> 3;
                l7 = l4 - loops >> 3;
                shadeValue += j3;
                rgb += shadeValue & 0x600000;
                j8 = shadeValue >> 23;
            }
            for (k3 = end_x - start_x & 7; k3-- > 0;) {
                if (true) {
                    dest[dest_off] = texture[(loops & 0x3f80) + (rgb >> 7)] >>> j8;
                    Rasterizer2D.depth_buffer[dest_off] = depth;
                }
                dest_off++;
                depth += depth_slope;
                rgb += j7;
                loops += l7;
            }

            return;
        }
        while (k3-- > 0) {
            int i9;
            for (int i = 0; i < 8; i++) {
                if ((i9 = texture[(loops & 0x3f80) + (rgb >> 7)] >>> j8) != 0) {
                    dest[dest_off] = i9;
                    Rasterizer2D.depth_buffer[dest_off] = depth;
                }
                dest_off++;
                depth += depth_slope;
                rgb += j7;
                loops += l7;
            }
            rgb = j4;
            loops = l4;
            l1 += k2;
            i2 += l2;
            j2 += i3;
            int j6 = j2 >> 14;
            if (j6 != 0) {
                j4 = l1 / j6;
                l4 = i2 / j6;
                if (j4 < 7)
                    j4 = 7;
                else
                if (j4 > 16256)
                    j4 = 16256;
            }
            j7 = j4 - rgb >> 3;
            l7 = l4 - loops >> 3;
            shadeValue += j3;
            rgb += shadeValue & 0x600000;
            j8 = shadeValue >> 23;
        }
        for (int l3 = end_x - start_x & 7; l3-- > 0;) {
            int j9;
            if ((j9 = texture[(loops & 0x3f80) + (rgb >> 7)] >>> j8) != 0) {
                dest[dest_off] = j9;
                Rasterizer2D.depth_buffer[dest_off] = depth;
            }
            depth += depth_slope;
            dest_off++;
            rgb += j7;
            loops += l7;
        }
    }

    public static void drawDepthTriangle(int x_a, int x_b, int x_c, int y_a, int y_b, int y_c, float z_a, float z_b, float z_c) {
        int a_to_b = 0;
        if (y_b != y_a) {
            a_to_b = (x_b - x_a << 16) / (y_b - y_a);
        }
        int b_to_c = 0;
        if (y_c != y_b) {
            b_to_c = (x_c - x_b << 16) / (y_c - y_b);
        }
        int c_to_a = 0;
        if (y_c != y_a) {
            c_to_a = (x_a - x_c << 16) / (y_a - y_c);
        }

        float b_aX = x_b - x_a;
        float b_aY = y_b - y_a;
        float c_aX = x_c - x_a;
        float c_aY = y_c - y_a;
        float b_aZ = z_b - z_a;
        float c_aZ = z_c - z_a;

        float div = b_aX * c_aY - c_aX * b_aY;
        float depth_slope = (b_aZ * c_aY - c_aZ * b_aY) / div;
        float depth_increment = (c_aZ * b_aX - b_aZ * c_aX) / div;
        if (y_a <= y_b && y_a <= y_c) {
            if (y_a < Rasterizer2D.clip_bottom) {
                if (y_b > Rasterizer2D.clip_bottom)
                    y_b = Rasterizer2D.clip_bottom;
                if (y_c > Rasterizer2D.clip_bottom)
                    y_c = Rasterizer2D.clip_bottom;
                z_a = z_a - depth_slope * x_a + depth_slope;
                if (y_b < y_c) {
                    x_c = x_a <<= 16;
                    if (y_a < 0) {
                        x_c -= c_to_a * y_a;
                        x_a -= a_to_b * y_a;
                        z_a -= depth_increment * y_a;
                        y_a = 0;
                    }
                    x_b <<= 16;
                    if (y_b < 0) {
                        x_b -= b_to_c * y_b;
                        y_b = 0;
                    }
                    if (y_a != y_b && c_to_a < a_to_b || y_a == y_b && c_to_a > b_to_c) {
                        y_c -= y_b;
                        y_b -= y_a;
                        y_a = line_offsets[y_a];
                        while (--y_b >= 0) {
                            drawDepthTriangleScanline(y_a, x_c >> 16, x_a >> 16, z_a, depth_slope);
                            x_c += c_to_a;
                            x_a += a_to_b;
                            z_a += depth_increment;
                            y_a += Rasterizer2D.width;
                        }
                        while (--y_c >= 0) {
                            drawDepthTriangleScanline(y_a, x_c >> 16, x_b >> 16, z_a, depth_slope);
                            x_c += c_to_a;
                            x_b += b_to_c;
                            z_a += depth_increment;
                            y_a += Rasterizer2D.width;
                        }
                    } else {
                        y_c -= y_b;
                        y_b -= y_a;
                        y_a = line_offsets[y_a];
                        while (--y_b >= 0) {
                            drawDepthTriangleScanline(y_a, x_a >> 16, x_c >> 16, z_a, depth_slope);
                            x_c += c_to_a;
                            x_a += a_to_b;
                            z_a += depth_increment;
                            y_a += Rasterizer2D.width;
                        }
                        while (--y_c >= 0) {
                            drawDepthTriangleScanline(y_a, x_b >> 16, x_c >> 16, z_a, depth_slope);
                            x_c += c_to_a;
                            x_b += b_to_c;
                            z_a += depth_increment;
                            y_a += Rasterizer2D.width;
                        }
                    }
                } else {
                    x_b = x_a <<= 16;
                    if (y_a < 0) {
                        x_b -= c_to_a * y_a;
                        x_a -= a_to_b * y_a;
                        z_a -= depth_increment * y_a;
                        y_a = 0;
                    }
                    x_c <<= 16;
                    if (y_c < 0) {
                        x_c -= b_to_c * y_c;
                        y_c = 0;
                    }
                    if (y_a != y_c && c_to_a < a_to_b || y_a == y_c && b_to_c > a_to_b) {
                        y_b -= y_c;
                        y_c -= y_a;
                        y_a = line_offsets[y_a];
                        while (--y_c >= 0) {
                            drawDepthTriangleScanline(y_a, x_b >> 16, x_a >> 16, z_a, depth_slope);
                            x_b += c_to_a;
                            x_a += a_to_b;
                            z_a += depth_increment;
                            y_a += Rasterizer2D.width;
                        }
                        while (--y_b >= 0) {
                            drawDepthTriangleScanline(y_a, x_c >> 16, x_a >> 16, z_a, depth_slope);
                            x_c += b_to_c;
                            x_a += a_to_b;
                            z_a += depth_increment;
                            y_a += Rasterizer2D.width;
                        }
                    } else {
                        y_b -= y_c;
                        y_c -= y_a;
                        y_a = line_offsets[y_a];
                        while (--y_c >= 0) {
                            drawDepthTriangleScanline(y_a, x_a >> 16, x_b >> 16, z_a, depth_slope);
                            x_b += c_to_a;
                            x_a += a_to_b;
                            z_a += depth_increment;
                            y_a += Rasterizer2D.width;
                        }
                        while (--y_b >= 0) {
                            drawDepthTriangleScanline(y_a, x_a >> 16, x_c >> 16, z_a, depth_slope);
                            x_c += b_to_c;
                            x_a += a_to_b;
                            z_a += depth_increment;
                            y_a += Rasterizer2D.width;
                        }
                    }
                }
            }
        } else if (y_b <= y_c) {
            if (y_b < Rasterizer2D.clip_bottom) {
                if (y_c > Rasterizer2D.clip_bottom)
                    y_c = Rasterizer2D.clip_bottom;
                if (y_a > Rasterizer2D.clip_bottom)
                    y_a = Rasterizer2D.clip_bottom;
                z_b = z_b - depth_slope * x_b + depth_slope;
                if (y_c < y_a) {
                    x_a = x_b <<= 16;
                    if (y_b < 0) {
                        x_a -= a_to_b * y_b;
                        x_b -= b_to_c * y_b;
                        z_b -= depth_increment * y_b;
                        y_b = 0;
                    }
                    x_c <<= 16;
                    if (y_c < 0) {
                        x_c -= c_to_a * y_c;
                        y_c = 0;
                    }
                    if (y_b != y_c && a_to_b < b_to_c || y_b == y_c && a_to_b > c_to_a) {
                        y_a -= y_c;
                        y_c -= y_b;
                        y_b = line_offsets[y_b];
                        while (--y_c >= 0) {
                            drawDepthTriangleScanline(y_b, x_a >> 16, x_b >> 16, z_b, depth_slope);
                            x_a += a_to_b;
                            x_b += b_to_c;
                            z_b += depth_increment;
                            y_b += Rasterizer2D.width;
                        }
                        while (--y_a >= 0) {
                            drawDepthTriangleScanline(y_b, x_a >> 16, x_c >> 16, z_b, depth_slope);
                            x_a += a_to_b;
                            x_c += c_to_a;
                            z_b += depth_increment;
                            y_b += Rasterizer2D.width;
                        }
                    } else {
                        y_a -= y_c;
                        y_c -= y_b;
                        y_b = line_offsets[y_b];
                        while (--y_c >= 0) {
                            drawDepthTriangleScanline(y_b, x_b >> 16, x_a >> 16, z_b, depth_slope);
                            x_a += a_to_b;
                            x_b += b_to_c;
                            z_b += depth_increment;
                            y_b += Rasterizer2D.width;
                        }
                        while (--y_a >= 0) {
                            drawDepthTriangleScanline(y_b, x_c >> 16, x_a >> 16, z_b, depth_slope);
                            x_a += a_to_b;
                            x_c += c_to_a;
                            z_b += depth_increment;
                            y_b += Rasterizer2D.width;
                        }
                    }
                } else {
                    x_c = x_b <<= 16;
                    if (y_b < 0) {
                        x_c -= a_to_b * y_b;
                        x_b -= b_to_c * y_b;
                        z_b -= depth_increment * y_b;
                        y_b = 0;
                    }
                    x_a <<= 16;
                    if (y_a < 0) {
                        x_a -= c_to_a * y_a;
                        y_a = 0;
                    }
                    if (a_to_b < b_to_c) {
                        y_c -= y_a;
                        y_a -= y_b;
                        y_b = line_offsets[y_b];
                        while (--y_a >= 0) {
                            drawDepthTriangleScanline(y_b, x_c >> 16, x_b >> 16, z_b, depth_slope);
                            x_c += a_to_b;
                            x_b += b_to_c;
                            z_b += depth_increment;
                            y_b += Rasterizer2D.width;
                        }
                        while (--y_c >= 0) {
                            drawDepthTriangleScanline(y_b, x_a >> 16, x_b >> 16, z_b, depth_slope);
                            x_a += c_to_a;
                            x_b += b_to_c;
                            z_b += depth_increment;
                            y_b += Rasterizer2D.width;
                        }
                    } else {
                        y_c -= y_a;
                        y_a -= y_b;
                        y_b = line_offsets[y_b];
                        while (--y_a >= 0) {
                            drawDepthTriangleScanline(y_b, x_b >> 16, x_c >> 16, z_b, depth_slope);
                            x_c += a_to_b;
                            x_b += b_to_c;
                            z_b += depth_increment;
                            y_b += Rasterizer2D.width;
                        }
                        while (--y_c >= 0) {
                            drawDepthTriangleScanline(y_b, x_b >> 16, x_a >> 16, z_b, depth_slope);
                            x_a += c_to_a;
                            x_b += b_to_c;
                            z_b += depth_increment;
                            y_b += Rasterizer2D.width;
                        }
                    }
                }
            }
        } else if (y_c < Rasterizer2D.clip_bottom) {
            if (y_a > Rasterizer2D.clip_bottom)
                y_a = Rasterizer2D.clip_bottom;
            if (y_b > Rasterizer2D.clip_bottom)
                y_b = Rasterizer2D.clip_bottom;
            z_c = z_c - depth_slope * x_c + depth_slope;
            if (y_a < y_b) {
                x_b = x_c <<= 16;
                if (y_c < 0) {
                    x_b -= b_to_c * y_c;
                    x_c -= c_to_a * y_c;
                    z_c -= depth_increment * y_c;
                    y_c = 0;
                }
                x_a <<= 16;
                if (y_a < 0) {
                    x_a -= a_to_b * y_a;
                    y_a = 0;
                }
                if (b_to_c < c_to_a) {
                    y_b -= y_a;
                    y_a -= y_c;
                    y_c = line_offsets[y_c];
                    while (--y_a >= 0) {
                        drawDepthTriangleScanline(y_c, x_b >> 16, x_c >> 16, z_c, depth_slope);
                        x_b += b_to_c;
                        x_c += c_to_a;
                        z_c += depth_increment;
                        y_c += Rasterizer2D.width;
                    }
                    while (--y_b >= 0) {
                        drawDepthTriangleScanline(y_c, x_b >> 16, x_a >> 16, z_c, depth_slope);
                        x_b += b_to_c;
                        x_a += a_to_b;
                        z_c += depth_increment;
                        y_c += Rasterizer2D.width;
                    }
                } else {
                    y_b -= y_a;
                    y_a -= y_c;
                    y_c = line_offsets[y_c];
                    while (--y_a >= 0) {
                        drawDepthTriangleScanline(y_c, x_c >> 16, x_b >> 16, z_c, depth_slope);
                        x_b += b_to_c;
                        x_c += c_to_a;
                        z_c += depth_increment;
                        y_c += Rasterizer2D.width;
                    }
                    while (--y_b >= 0) {
                        drawDepthTriangleScanline(y_c, x_a >> 16, x_b >> 16, z_c, depth_slope);
                        x_b += b_to_c;
                        x_a += a_to_b;
                        z_c += depth_increment;
                        y_c += Rasterizer2D.width;
                    }
                }
            } else {
                x_a = x_c <<= 16;
                if (y_c < 0) {
                    x_a -= b_to_c * y_c;
                    x_c -= c_to_a * y_c;
                    z_c -= depth_increment * y_c;
                    y_c = 0;
                }
                x_b <<= 16;
                if (y_b < 0) {
                    x_b -= a_to_b * y_b;
                    y_b = 0;
                }
                if (b_to_c < c_to_a) {
                    y_a -= y_b;
                    y_b -= y_c;
                    y_c = line_offsets[y_c];
                    while (--y_b >= 0) {
                        drawDepthTriangleScanline(y_c, x_a >> 16, x_c >> 16, z_c, depth_slope);
                        x_a += b_to_c;
                        x_c += c_to_a;
                        z_c += depth_increment;
                        y_c += Rasterizer2D.width;
                    }
                    while (--y_a >= 0) {
                        drawDepthTriangleScanline(y_c, x_b >> 16, x_c >> 16, z_c, depth_slope);
                        x_b += a_to_b;
                        x_c += c_to_a;
                        z_c += depth_increment;
                        y_c += Rasterizer2D.width;
                    }
                } else {
                    y_a -= y_b;
                    y_b -= y_c;
                    y_c = line_offsets[y_c];
                    while (--y_b >= 0) {
                        drawDepthTriangleScanline(y_c, x_c >> 16, x_a >> 16, z_c, depth_slope);
                        x_a += b_to_c;
                        x_c += c_to_a;
                        z_c += depth_increment;
                        y_c += Rasterizer2D.width;
                    }
                    while (--y_a >= 0) {
                        drawDepthTriangleScanline(y_c, x_c >> 16, x_b >> 16, z_c, depth_slope);
                        x_b += a_to_b;
                        x_c += c_to_a;
                        z_c += depth_increment;
                        y_c += Rasterizer2D.width;
                    }
                }
            }
        }
    }

    private static void drawDepthTriangleScanline(int dest_off, int start_x, int end_x, float depth, float depth_slope) {
        int dbl = Rasterizer2D.depth_buffer.length;
        if (testX) {
            if (end_x > Rasterizer2D.width) {
                end_x = Rasterizer2D.width;
            }
            if (start_x < 0) {
                start_x = 0;
            }
        }
        if (start_x >= end_x) {
            return;
        }
        dest_off += start_x - 1;
        int loops = end_x - start_x >> 2;
        depth += depth_slope * (float) start_x;
        if (alpha == 0) {
            while (--loops >= 0) {
                dest_off++;
                if (dest_off >= 0 && dest_off < dbl && true) {
                    Rasterizer2D.depth_buffer[dest_off] = depth;
                }
                depth += depth_slope;
                dest_off++;
                if (dest_off >= 0 && dest_off < dbl && true) {
                    Rasterizer2D.depth_buffer[dest_off] = depth;
                }
                depth += depth_slope;
                dest_off++;
                if (dest_off >= 0 && dest_off < dbl && true) {
                    Rasterizer2D.depth_buffer[dest_off] = depth;
                }
                depth += depth_slope;
                dest_off++;
                if (dest_off >= 0 && dest_off < dbl && true) {
                    Rasterizer2D.depth_buffer[dest_off] = depth;
                }
                depth += depth_slope;
            }
            for (loops = end_x - start_x & 3; --loops >= 0;) {
                dest_off++;
                if (dest_off >= 0 && dest_off < dbl && true) {
                    Rasterizer2D.depth_buffer[dest_off] = depth;
                }
                depth += depth_slope;
            }
            return;
        }
        while (--loops >= 0) {
            dest_off++;
            if (dest_off >= 0 && dest_off < dbl && true) {
                Rasterizer2D.depth_buffer[dest_off] = depth;
            }
            depth += depth_slope;
            dest_off++;
            if (dest_off >= 0 && dest_off < dbl && true) {
                Rasterizer2D.depth_buffer[dest_off] = depth;
            }
            depth += depth_slope;
            dest_off++;
            if (dest_off >= 0 && dest_off < dbl && true) {
                Rasterizer2D.depth_buffer[dest_off] = depth;
            }
            depth += depth_slope;
            dest_off++;
            if (dest_off >= 0 && dest_off < dbl && true) {
                Rasterizer2D.depth_buffer[dest_off] = depth;
            }
            depth += depth_slope;
        }
        for (loops = end_x - start_x & 3; --loops >= 0;) {
            dest_off++;
            if (dest_off >= 0 && dest_off < dbl && true) {
                Rasterizer2D.depth_buffer[dest_off] = depth;
            }
            depth += depth_slope;
        }
    }

    static final int light(int hsl, int light) {
        light = light * (hsl & 127) >> 7;
        if(light < 2) {
            light = 2;
        } else if(light > 126) {
            light = 126;
        }
        return (hsl & '\uff80') + light;
    }

    public static final void drawTexturedTriangle(
        int y_a, int y_b, int y_c,
        int x_a, int x_b, int x_c,
        int hue_a, int hue_b, int hue_c,
        int viewport_x_a, int viewport_x_b, int viewport_x_c,
        int viewport_y_a, int viewport_y_b, int viewport_y_c,
        int viewport_z_a, int viewport_z_b, int viewport_z_c,
        int texture_id, float depth_a, float depth_b, float depth_c) {

        if (depth_a < 0 || depth_b < 0 || depth_c < 0) {
            return;
        }
        int[] texels = get_texels(texture_id)[scale];
        int hsl;
        if(texels == null) {
            System.out.println("[DEBUG] drawTexturedTriangle() : texels == null");
            hsl = 0;
            drawShadedTriangle(y_a, y_b, y_c, x_a, x_b, x_c, light(hsl, hue_a), light(hsl, hue_b), light(hsl, hue_c), depth_a, depth_b, depth_c);
        } else {
            //low_detail = false;
            opaque = !transparent[texture_id];
            int var22 = x_b - x_a;
            int var24 = y_b - y_a;
            int var25 = x_c - x_a;
            int var36 = y_c - y_a;
            int var35 = hue_b - hue_a;
            int var37 = hue_c - hue_a;
            int var26 = 0;
            if(y_b != y_a) {
                var26 = (x_b - x_a << 14) / (y_b - y_a);
            }
            int var33 = 0;
            if(y_c != y_b) {
                var33 = (x_c - x_b << 14) / (y_c - y_b);
            }
            int var23 = 0;
            if(y_c != y_a) {
                var23 = (x_a - x_c << 14) / (y_a - y_c);
            }
            int var34 = var22 * var36 - var25 * var24;
            if(var34 != 0) {
                int var27 = (var35 * var36 - var37 * var24 << 9) / var34;//gradient
                int var38 = (var37 * var22 - var35 * var25 << 9) / var34;
                viewport_x_b = viewport_x_a - viewport_x_b;
                viewport_y_b = viewport_y_a - viewport_y_b;
                viewport_z_b = viewport_z_a - viewport_z_b;
                viewport_x_c -= viewport_x_a;
                viewport_y_c -= viewport_y_a;
                viewport_z_c -= viewport_z_a;
                int var20 = viewport_x_c * viewport_y_a - viewport_y_c * viewport_x_a << (SceneGraph.view_dist == 9 ? 14 : 15);
                int var30 = (int)(((long)(viewport_y_c * viewport_z_a - viewport_z_c * viewport_y_a) << 3 << 14) / (long)scene_div_factor);
                int var21 = (int)(((long)(viewport_z_c * viewport_x_a - viewport_x_c * viewport_z_a) << 14) / (long)scene_div_factor);
                int var28 = viewport_x_b * viewport_y_a - viewport_y_b * viewport_x_a << (SceneGraph.view_dist == 9 ? 14 : 15);
                int var31 = (int)(((long)(viewport_y_b * viewport_z_a - viewport_z_b * viewport_y_a) << 3 << 14) / (long)scene_div_factor);
                int var39 = (int)(((long)(viewport_z_b * viewport_x_a - viewport_x_b * viewport_z_a) << 14) / (long)scene_div_factor);
                int var29 = viewport_y_b * viewport_x_c - viewport_x_b * viewport_y_c << (SceneGraph.view_dist == 9 ? 14 : 15);
                int var32 = (int)(((long)(viewport_z_b * viewport_y_c - viewport_y_b * viewport_z_c) << 3 << 14) / (long)scene_div_factor);
                int var40 = (int)(((long)(viewport_x_b * viewport_z_c - viewport_z_b * viewport_x_c) << 14) / (long)scene_div_factor);
                int var41;
                float depth_slope = ((depth_b - depth_a) * var36 - (depth_c - depth_a) * var24) / var34;
                float depth_increment = ((depth_c - depth_a) * var22 - (depth_b - depth_a) * var25) / var34;
                if(y_a <= y_b && y_a <= y_c) {
                    if(y_a < Rasterizer2D.clip_bottom) {
                        if(y_b > Rasterizer2D.clip_bottom) {
                            y_b = Rasterizer2D.clip_bottom;
                        }
                        if(y_c > Rasterizer2D.clip_bottom) {
                            y_c = Rasterizer2D.clip_bottom;
                        }
                        hue_a = (hue_a << 9) - var27 * x_a + var27;
                        depth_a = depth_a - depth_slope * x_a + depth_slope;
                        if(y_b < y_c) {
                            x_c = x_a <<= 14;
                            if(y_a < 0) {
                                x_c -= var23 * y_a;
                                x_a -= var26 * y_a;
                                hue_a -= var38 * y_a;
                                depth_a -= depth_increment * y_a;
                                y_a = 0;
                            }
                            x_b <<= 14;
                            if(y_b < 0) {
                                x_b -= var33 * y_b;
                                y_b = 0;
                            }
                            var41 = y_a - center_y;
                            var20 += var21 * var41;
                            var28 += var39 * var41;
                            var29 += var40 * var41;
                            if(y_a != y_b && var23 < var26 || y_a == y_b && var23 > var33) {
                                y_c -= y_b;
                                y_b -= y_a;
                                y_a = line_offsets[y_a];
                                while(true) {
                                    --y_b;
                                    if(y_b < 0) {
                                        while(true) {
                                            --y_c;
                                            if(y_c < 0) {
                                                return;
                                            }
                                            draw_textured_scanline(Rasterizer2D.pixels, texels, 0, 0, y_a, x_c >> 14, x_b >> 14, hue_a, var27, var20, var28, var29, var30, var31, var32, depth_a, depth_slope);
                                            x_c += var23;
                                            x_b += var33;
                                            depth_a += depth_increment;
                                            hue_a += var38;
                                            y_a += Rasterizer2D.width;
                                            var20 += var21;
                                            var28 += var39;
                                            var29 += var40;
                                        }
                                    }
                                    draw_textured_scanline(Rasterizer2D.pixels, texels, 0, 0, y_a, x_c >> 14, x_a >> 14, hue_a, var27, var20, var28, var29, var30, var31, var32, depth_a, depth_slope);
                                    x_c += var23;
                                    x_a += var26;
                                    depth_a += depth_increment;
                                    hue_a += var38;
                                    y_a += Rasterizer2D.width;
                                    var20 += var21;
                                    var28 += var39;
                                    var29 += var40;
                                }
                            } else {
                                y_c -= y_b;
                                y_b -= y_a;
                                y_a = line_offsets[y_a];
                                while(true) {
                                    --y_b;
                                    if(y_b < 0) {
                                        while(true) {
                                            --y_c;
                                            if(y_c < 0) {
                                                return;
                                            }
                                            draw_textured_scanline(Rasterizer2D.pixels, texels, 0, 0, y_a, x_b >> 14, x_c >> 14, hue_a, var27, var20, var28, var29, var30, var31, var32, depth_a, depth_slope);
                                            x_c += var23;
                                            x_b += var33;
                                            depth_a += depth_increment;
                                            hue_a += var38;
                                            y_a += Rasterizer2D.width;
                                            var20 += var21;
                                            var28 += var39;
                                            var29 += var40;
                                        }
                                    }
                                    draw_textured_scanline(Rasterizer2D.pixels, texels, 0, 0, y_a, x_a >> 14, x_c >> 14, hue_a, var27, var20, var28, var29, var30, var31, var32, depth_a, depth_slope);
                                    x_c += var23;
                                    x_a += var26;
                                    depth_a += depth_increment;
                                    hue_a += var38;
                                    y_a += Rasterizer2D.width;
                                    var20 += var21;
                                    var28 += var39;
                                    var29 += var40;
                                }
                            }
                        } else {
                            x_b = x_a <<= 14;
                            if(y_a < 0) {
                                x_b -= var23 * y_a;
                                x_a -= var26 * y_a;
                                depth_a -= depth_increment * y_a;
                                hue_a -= var38 * y_a;
                                y_a = 0;
                            }
                            x_c <<= 14;
                            if(y_c < 0) {
                                x_c -= var33 * y_c;
                                y_c = 0;
                            }
                            var41 = y_a - center_y;
                            var20 += var21 * var41;
                            var28 += var39 * var41;
                            var29 += var40 * var41;
                            if(y_a != y_c && var23 < var26 || y_a == y_c && var33 > var26) {
                                y_b -= y_c;
                                y_c -= y_a;
                                y_a = line_offsets[y_a];
                                while(true) {
                                    --y_c;
                                    if(y_c < 0) {
                                        while(true) {
                                            --y_b;
                                            if(y_b < 0) {
                                                return;
                                            }
                                            draw_textured_scanline(Rasterizer2D.pixels, texels, 0, 0, y_a, x_c >> 14, x_a >> 14, hue_a, var27, var20, var28, var29, var30, var31, var32, depth_a, depth_slope);
                                            x_c += var33;
                                            x_a += var26;
                                            depth_a += depth_increment;
                                            hue_a += var38;
                                            y_a += Rasterizer2D.width;
                                            var20 += var21;
                                            var28 += var39;
                                            var29 += var40;
                                        }
                                    }
                                    draw_textured_scanline(Rasterizer2D.pixels, texels, 0, 0, y_a, x_b >> 14, x_a >> 14, hue_a, var27, var20, var28, var29, var30, var31, var32, depth_a, depth_slope);
                                    x_b += var23;
                                    x_a += var26;
                                    depth_a += depth_increment;
                                    hue_a += var38;
                                    y_a += Rasterizer2D.width;
                                    var20 += var21;
                                    var28 += var39;
                                    var29 += var40;
                                }
                            } else {
                                y_b -= y_c;
                                y_c -= y_a;
                                y_a = line_offsets[y_a];
                                while(true) {
                                    --y_c;
                                    if(y_c < 0) {
                                        while(true) {
                                            --y_b;
                                            if(y_b < 0) {
                                                return;
                                            }
                                            draw_textured_scanline(Rasterizer2D.pixels, texels, 0, 0, y_a, x_a >> 14, x_c >> 14, hue_a, var27, var20, var28, var29, var30, var31, var32, depth_a, depth_slope);
                                            x_c += var33;
                                            x_a += var26;
                                            depth_a += depth_increment;
                                            hue_a += var38;
                                            y_a += Rasterizer2D.width;
                                            var20 += var21;
                                            var28 += var39;
                                            var29 += var40;
                                        }
                                    }
                                    draw_textured_scanline(Rasterizer2D.pixels, texels, 0, 0, y_a, x_a >> 14, x_b >> 14, hue_a, var27, var20, var28, var29, var30, var31, var32, depth_a, depth_slope);
                                    x_b += var23;
                                    x_a += var26;
                                    depth_a += depth_increment;
                                    hue_a += var38;
                                    y_a += Rasterizer2D.width;
                                    var20 += var21;
                                    var28 += var39;
                                    var29 += var40;
                                }
                            }
                        }
                    }
                } else if(y_b <= y_c) {
                    if(y_b < Rasterizer2D.clip_bottom) {
                        if(y_c > Rasterizer2D.clip_bottom) {
                            y_c = Rasterizer2D.clip_bottom;
                        }
                        if(y_a > Rasterizer2D.clip_bottom) {
                            y_a = Rasterizer2D.clip_bottom;
                        }
                        hue_b = (hue_b << 9) - var27 * x_b + var27;
                        depth_b = depth_b - depth_slope * x_b + depth_slope;
                        if(y_c < y_a) {
                            x_a = x_b <<= 14;
                            if(y_b < 0) {
                                x_a -= var26 * y_b;
                                x_b -= var33 * y_b;
                                depth_b -= depth_increment * y_b;
                                hue_b -= var38 * y_b;
                                y_b = 0;
                            }
                            x_c <<= 14;
                            if(y_c < 0) {
                                x_c -= var23 * y_c;
                                y_c = 0;
                            }
                            var41 = y_b - center_y;
                            var20 += var21 * var41;
                            var28 += var39 * var41;
                            var29 += var40 * var41;
                            if(y_b != y_c && var26 < var33 || y_b == y_c && var26 > var23) {
                                y_a -= y_c;
                                y_c -= y_b;
                                y_b = line_offsets[y_b];
                                while(true) {
                                    --y_c;
                                    if(y_c < 0) {
                                        while(true) {
                                            --y_a;
                                            if(y_a < 0) {
                                                return;
                                            }
                                            draw_textured_scanline(Rasterizer2D.pixels, texels, 0, 0, y_b, x_a >> 14, x_c >> 14, hue_b, var27, var20, var28, var29, var30, var31, var32, depth_b, depth_slope);
                                            x_a += var26;
                                            x_c += var23;
                                            depth_b += depth_increment;
                                            hue_b += var38;
                                            y_b += Rasterizer2D.width;
                                            var20 += var21;
                                            var28 += var39;
                                            var29 += var40;
                                        }
                                    }
                                    draw_textured_scanline(Rasterizer2D.pixels, texels, 0, 0, y_b, x_a >> 14, x_b >> 14, hue_b, var27, var20, var28, var29, var30, var31, var32, depth_b, depth_slope);
                                    x_a += var26;
                                    x_b += var33;
                                    depth_b += depth_increment;
                                    hue_b += var38;
                                    y_b += Rasterizer2D.width;
                                    var20 += var21;
                                    var28 += var39;
                                    var29 += var40;
                                }
                            } else {
                                y_a -= y_c;
                                y_c -= y_b;
                                y_b = line_offsets[y_b];
                                while(true) {
                                    --y_c;
                                    if(y_c < 0) {
                                        while(true) {
                                            --y_a;
                                            if(y_a < 0) {
                                                return;
                                            }
                                            draw_textured_scanline(Rasterizer2D.pixels, texels, 0, 0, y_b, x_c >> 14, x_a >> 14, hue_b, var27, var20, var28, var29, var30, var31, var32, depth_b, depth_slope);
                                            x_a += var26;
                                            x_c += var23;
                                            depth_b += depth_increment;
                                            hue_b += var38;
                                            y_b += Rasterizer2D.width;
                                            var20 += var21;
                                            var28 += var39;
                                            var29 += var40;
                                        }
                                    }
                                    draw_textured_scanline(Rasterizer2D.pixels, texels, 0, 0, y_b, x_b >> 14, x_a >> 14, hue_b, var27, var20, var28, var29, var30, var31, var32, depth_b, depth_slope);
                                    x_a += var26;
                                    x_b += var33;
                                    depth_b += depth_increment;
                                    hue_b += var38;
                                    y_b += Rasterizer2D.width;
                                    var20 += var21;
                                    var28 += var39;
                                    var29 += var40;
                                }
                            }
                        } else {
                            x_c = x_b <<= 14;
                            if(y_b < 0) {
                                x_c -= var26 * y_b;
                                x_b -= var33 * y_b;
                                depth_b -= depth_increment * y_b;
                                hue_b -= var38 * y_b;
                                y_b = 0;
                            }
                            x_a <<= 14;
                            if(y_a < 0) {
                                x_a -= var23 * y_a;
                                y_a = 0;
                            }
                            var41 = y_b - center_y;
                            var20 += var21 * var41;
                            var28 += var39 * var41;
                            var29 += var40 * var41;
                            if(var26 < var33) {
                                y_c -= y_a;
                                y_a -= y_b;
                                y_b = line_offsets[y_b];
                                while(true) {
                                    --y_a;
                                    if(y_a < 0) {
                                        while(true) {
                                            --y_c;
                                            if(y_c < 0) {
                                                return;
                                            }
                                            draw_textured_scanline(Rasterizer2D.pixels, texels, 0, 0, y_b, x_a >> 14, x_b >> 14, hue_b, var27, var20, var28, var29, var30, var31, var32, depth_b, depth_slope);
                                            x_a += var23;
                                            x_b += var33;
                                            depth_b += depth_increment;
                                            hue_b += var38;
                                            y_b += Rasterizer2D.width;
                                            var20 += var21;
                                            var28 += var39;
                                            var29 += var40;
                                        }
                                    }
                                    draw_textured_scanline(Rasterizer2D.pixels, texels, 0, 0, y_b, x_c >> 14, x_b >> 14, hue_b, var27, var20, var28, var29, var30, var31, var32, depth_b, depth_slope);
                                    x_c += var26;
                                    x_b += var33;
                                    depth_b += depth_increment;
                                    hue_b += var38;
                                    y_b += Rasterizer2D.width;
                                    var20 += var21;
                                    var28 += var39;
                                    var29 += var40;
                                }
                            } else {
                                y_c -= y_a;
                                y_a -= y_b;
                                y_b = line_offsets[y_b];
                                while(true) {
                                    --y_a;
                                    if(y_a < 0) {
                                        while(true) {
                                            --y_c;
                                            if(y_c < 0) {
                                                return;
                                            }
                                            draw_textured_scanline(Rasterizer2D.pixels, texels, 0, 0, y_b, x_b >> 14, x_a >> 14, hue_b, var27, var20, var28, var29, var30, var31, var32, depth_b, depth_slope);
                                            x_a += var23;
                                            x_b += var33;
                                            depth_b += depth_increment;
                                            hue_b += var38;
                                            y_b += Rasterizer2D.width;
                                            var20 += var21;
                                            var28 += var39;
                                            var29 += var40;
                                        }
                                    }
                                    draw_textured_scanline(Rasterizer2D.pixels, texels, 0, 0, y_b, x_b >> 14, x_c >> 14, hue_b, var27, var20, var28, var29, var30, var31, var32, depth_b, depth_slope);
                                    x_c += var26;
                                    x_b += var33;
                                    depth_b += depth_increment;
                                    hue_b += var38;
                                    y_b += Rasterizer2D.width;
                                    var20 += var21;
                                    var28 += var39;
                                    var29 += var40;
                                }
                            }
                        }
                    }
                } else if(y_c < Rasterizer2D.clip_bottom) {
                    if(y_a > Rasterizer2D.clip_bottom) {
                        y_a = Rasterizer2D.clip_bottom;
                    }
                    if(y_b > Rasterizer2D.clip_bottom) {
                        y_b = Rasterizer2D.clip_bottom;
                    }
                    depth_c = depth_c - depth_slope * x_c + depth_slope;
                    hue_c = (hue_c << 9) - var27 * x_c + var27;
                    if(y_a < y_b) {
                        x_b = x_c <<= 14;
                        if(y_c < 0) {
                            x_b -= var33 * y_c;
                            x_c -= var23 * y_c;
                            depth_c -= depth_increment * y_c;
                            hue_c -= var38 * y_c;
                            y_c = 0;
                        }
                        x_a <<= 14;
                        if(y_a < 0) {
                            x_a -= var26 * y_a;
                            y_a = 0;
                        }
                        var41 = y_c - center_y;
                        var20 += var21 * var41;
                        var28 += var39 * var41;
                        var29 += var40 * var41;
                        if(var33 < var23) {
                            y_b -= y_a;
                            y_a -= y_c;
                            y_c = line_offsets[y_c];
                            while(true) {
                                --y_a;
                                if(y_a < 0) {
                                    while(true) {
                                        --y_b;
                                        if(y_b < 0) {
                                            return;
                                        }
                                        draw_textured_scanline(Rasterizer2D.pixels, texels, 0, 0, y_c, x_b >> 14, x_a >> 14, hue_c, var27, var20, var28, var29, var30, var31, var32, depth_c, depth_slope);
                                        x_b += var33;
                                        x_a += var26;
                                        depth_c += depth_increment;
                                        hue_c += var38;
                                        y_c += Rasterizer2D.width;
                                        var20 += var21;
                                        var28 += var39;
                                        var29 += var40;
                                    }
                                }
                                draw_textured_scanline(Rasterizer2D.pixels, texels, 0, 0, y_c, x_b >> 14, x_c >> 14, hue_c, var27, var20, var28, var29, var30, var31, var32, depth_c, depth_slope);
                                x_b += var33;
                                x_c += var23;
                                depth_c += depth_increment;
                                hue_c += var38;
                                y_c += Rasterizer2D.width;
                                var20 += var21;
                                var28 += var39;
                                var29 += var40;
                            }
                        } else {
                            y_b -= y_a;
                            y_a -= y_c;
                            y_c = line_offsets[y_c];
                            while(true) {
                                --y_a;
                                if(y_a < 0) {
                                    while(true) {
                                        --y_b;
                                        if(y_b < 0) {
                                            return;
                                        }
                                        draw_textured_scanline(Rasterizer2D.pixels, texels, 0, 0, y_c, x_a >> 14, x_b >> 14, hue_c, var27, var20, var28, var29, var30, var31, var32, depth_c, depth_slope);
                                        x_b += var33;
                                        x_a += var26;
                                        depth_c += depth_increment;
                                        hue_c += var38;
                                        y_c += Rasterizer2D.width;
                                        var20 += var21;
                                        var28 += var39;
                                        var29 += var40;
                                    }
                                }
                                draw_textured_scanline(Rasterizer2D.pixels, texels, 0, 0, y_c, x_c >> 14, x_b >> 14, hue_c, var27, var20, var28, var29, var30, var31, var32, depth_c, depth_slope);
                                x_b += var33;
                                x_c += var23;
                                depth_c += depth_increment;
                                hue_c += var38;
                                y_c += Rasterizer2D.width;
                                var20 += var21;
                                var28 += var39;
                                var29 += var40;
                            }
                        }
                    } else {
                        x_a = x_c <<= 14;
                        if(y_c < 0) {
                            x_a -= var33 * y_c;
                            x_c -= var23 * y_c;
                            depth_c -= depth_increment * y_c;
                            hue_c -= var38 * y_c;
                            y_c = 0;
                        }
                        x_b <<= 14;
                        if(y_b < 0) {
                            x_b -= var26 * y_b;
                            y_b = 0;
                        }
                        var41 = y_c - center_y;
                        var20 += var21 * var41;
                        var28 += var39 * var41;
                        var29 += var40 * var41;
                        if(var33 < var23) {
                            y_a -= y_b;
                            y_b -= y_c;
                            y_c = line_offsets[y_c];
                            while(true) {
                                --y_b;
                                if(y_b < 0) {
                                    while(true) {
                                        --y_a;
                                        if(y_a < 0) {
                                            return;
                                        }
                                        draw_textured_scanline(Rasterizer2D.pixels, texels, 0, 0, y_c, x_b >> 14, x_c >> 14, hue_c, var27, var20, var28, var29, var30, var31, var32, depth_c, depth_slope);
                                        x_b += var26;
                                        x_c += var23;
                                        depth_c += depth_increment;
                                        hue_c += var38;
                                        y_c += Rasterizer2D.width;
                                        var20 += var21;
                                        var28 += var39;
                                        var29 += var40;
                                    }
                                }
                                draw_textured_scanline(Rasterizer2D.pixels, texels, 0, 0, y_c, x_a >> 14, x_c >> 14, hue_c, var27, var20, var28, var29, var30, var31, var32, depth_c, depth_slope);
                                x_a += var33;
                                x_c += var23;
                                depth_c += depth_increment;
                                hue_c += var38;
                                y_c += Rasterizer2D.width;
                                var20 += var21;
                                var28 += var39;
                                var29 += var40;
                            }
                        } else {
                            y_a -= y_b;
                            y_b -= y_c;
                            y_c = line_offsets[y_c];
                            while(true) {
                                --y_b;
                                if(y_b < 0) {
                                    while(true) {
                                        --y_a;
                                        if(y_a < 0) {
                                            return;
                                        }
                                        draw_textured_scanline(Rasterizer2D.pixels, texels, 0, 0, y_c, x_c >> 14, x_b >> 14, hue_c, var27, var20, var28, var29, var30, var31, var32, depth_c, depth_slope);
                                        x_b += var26;
                                        x_c += var23;
                                        depth_c += depth_increment;
                                        hue_c += var38;
                                        y_c += Rasterizer2D.width;
                                        var20 += var21;
                                        var28 += var39;
                                        var29 += var40;
                                    }
                                }
                                draw_textured_scanline(Rasterizer2D.pixels, texels, 0, 0, y_c, x_c >> 14, x_a >> 14, hue_c, var27, var20, var28, var29, var30, var31, var32, depth_c, depth_slope);
                                x_a += var33;
                                x_c += var23;
                                depth_c += depth_increment;
                                hue_c += var38;
                                y_c += Rasterizer2D.width;
                                var20 += var21;
                                var28 += var39;
                                var29 += var40;
                            }
                        }
                    }
                }
            }

        }
    }

    static final void draw_textured_scanline(int[] dest, int[] texel_map, int offsets, int src, int dest_offset, int start_x, int end_x, int shade, int gradient, int var9, int var10, int var11, int var12, int var13, int var14, float depth, float depth_slope) {
        if(start_x >= end_x)
            return;

        if(testX) {
            if(end_x > Rasterizer2D.center_x) {
                end_x = Rasterizer2D.center_x;
            }

            if(start_x < 0) {
                start_x = 0;
            }

            if(start_x >= end_x)
                return;

        }
        if(start_x < end_x) {
            dest_offset += start_x;
            depth += depth_slope * (float) start_x;
            shade += gradient * start_x;
            int var22 = end_x - start_x;
            int var15;
            int var16;
            int center_offset;//center_offset?
            int var18;
            int rgb;
            int loops;
            int var21;//direction
            int var23;
            if(low_detail) {
                center_offset = start_x - center_x;
                var9 += (var12 >> 3) * center_offset;
                var10 += (var13 >> 3) * center_offset;
                var11 += (var14 >> 3) * center_offset;
                var21 = var11 >> 12;
                if(var21 != 0) {
                    rgb = var9 / var21;
                    loops = var10 / var21;
                    if(rgb < 0) {
                        rgb = 0;
                    } else if(rgb > 4032) {
                        rgb = 4032;
                    }
                } else {
                    rgb = 0;
                    loops = 0;
                }
                var9 += var12;
                var10 += var13;
                var11 += var14;
                var21 = var11 >> 12;
                if(var21 != 0) {
                    var15 = var9 / var21;
                    var23 = var10 / var21;
                    if(var15 < 0) {
                        var15 = 0;
                    } else if(var15 > 4032) {
                        var15 = 4032;
                    }
                } else {
                    var15 = 0;
                    var23 = 0;
                }
                offsets = (rgb << 20) + loops;
                var18 = (var15 - rgb >> 3 << 20) + (var23 - loops >> 3);
                var22 >>= 3;
                gradient <<= 3;
                var16 = shade >> 8;
                if(opaque) {
                    if(var22 > 0) {
                        do {

                            src = texel_map[get_texel_pos((offsets & 4032) + (offsets >>> 26))];
                            dest[dest_offset] = ((src & 16711935) * var16 & -16711936) + ((src & '\uff00') * var16 & 16711680) >> 8;
                            Rasterizer2D.depth_buffer[dest_offset++] = depth;
                            depth += depth_slope;
                            offsets += var18;

                            src = texel_map[get_texel_pos((offsets & 4032) + (offsets >>> 26))];
                            dest[dest_offset] = ((src & 16711935) * var16 & -16711936) + ((src & '\uff00') * var16 & 16711680) >> 8;
                            Rasterizer2D.depth_buffer[dest_offset++] = depth;
                            depth += depth_slope;
                            offsets += var18;

                            src = texel_map[get_texel_pos((offsets & 4032) + (offsets >>> 26))];
                            dest[dest_offset] = ((src & 16711935) * var16 & -16711936) + ((src & '\uff00') * var16 & 16711680) >> 8;
                            Rasterizer2D.depth_buffer[dest_offset++] = depth;
                            depth += depth_slope;
                            offsets += var18;

                            src = texel_map[get_texel_pos((offsets & 4032) + (offsets >>> 26))];
                            dest[dest_offset] = ((src & 16711935) * var16 & -16711936) + ((src & '\uff00') * var16 & 16711680) >> 8;
                            Rasterizer2D.depth_buffer[dest_offset++] = depth;
                            depth += depth_slope;
                            offsets += var18;

                            src = texel_map[get_texel_pos((offsets & 4032) + (offsets >>> 26))];
                            dest[dest_offset] = ((src & 16711935) * var16 & -16711936) + ((src & '\uff00') * var16 & 16711680) >> 8;
                            Rasterizer2D.depth_buffer[dest_offset++] = depth;
                            depth += depth_slope;
                            offsets += var18;

                            src = texel_map[get_texel_pos((offsets & 4032) + (offsets >>> 26))];
                            dest[dest_offset] = ((src & 16711935) * var16 & -16711936) + ((src & '\uff00') * var16 & 16711680) >> 8;
                            Rasterizer2D.depth_buffer[dest_offset++] = depth;
                            depth += depth_slope;
                            offsets += var18;

                            src = texel_map[get_texel_pos((offsets & 4032) + (offsets >>> 26))];
                            dest[dest_offset] = ((src & 16711935) * var16 & -16711936) + ((src & '\uff00') * var16 & 16711680) >> 8;
                            Rasterizer2D.depth_buffer[dest_offset++] = depth;
                            depth += depth_slope;
                            offsets += var18;

                            src = texel_map[get_texel_pos((offsets & 4032) + (offsets >>> 26))];
                            dest[dest_offset] = ((src & 16711935) * var16 & -16711936) + ((src & '\uff00') * var16 & 16711680) >> 8;
                            Rasterizer2D.depth_buffer[dest_offset++] = depth;
                            depth += depth_slope;



                            rgb = var15;
                            loops = var23;
                            var9 += var12;
                            var10 += var13;
                            var11 += var14;
                            var21 = var11 >> 12;
                            if(var21 != 0) {
                                var15 = var9 / var21;
                                var23 = var10 / var21;
                                if(var15 < 0) {
                                    var15 = 0;
                                } else if(var15 > 4032) {
                                    var15 = 4032;
                                }
                            } else {
                                var15 = 0;
                                var23 = 0;
                            }
                            offsets = (rgb << 20) + loops;
                            var18 = (var15 - rgb >> 3 << 20) + (var23 - loops >> 3);
                            shade += gradient;
                            var16 = shade >> 8;
                        } while(--var22 > 0);
                    }
                    var22 = end_x - start_x & 7;
                    if(var22 > 0) {
                        do {
                            src = texel_map[get_texel_pos((offsets & 4032) + (offsets >>> 26))];
                            dest[dest_offset] = ((src & 16711935) * var16 & -16711936) + ((src & '\uff00') * var16 & 16711680) >> 8;
                            Rasterizer2D.depth_buffer[dest_offset++] = depth;
                            depth += depth_slope;
                            offsets += var18;
                        } while(--var22 > 0);
                    }
                } else {
                    if(var22 > 0) {
                        do {
                            if((src = texel_map[get_texel_pos((offsets & 4032) + (offsets >>> 26))]) != 0) {
                                dest[dest_offset] = ((src & 16711935) * var16 & -16711936) + ((src & '\uff00') * var16 & 16711680) >> 8;
                                Rasterizer2D.depth_buffer[dest_offset] = depth;
                            }
                            depth += depth_slope;
                            dest_offset++;
                            offsets += var18;

                            if((src = texel_map[get_texel_pos((offsets & 4032) + (offsets >>> 26))]) != 0) {
                                dest[dest_offset] = ((src & 16711935) * var16 & -16711936) + ((src & '\uff00') * var16 & 16711680) >> 8;
                                Rasterizer2D.depth_buffer[dest_offset] = depth;
                            }
                            depth += depth_slope;
                            dest_offset++;
                            offsets += var18;

                            if((src = texel_map[get_texel_pos((offsets & 4032) + (offsets >>> 26))]) != 0) {
                                dest[dest_offset] = ((src & 16711935) * var16 & -16711936) + ((src & '\uff00') * var16 & 16711680) >> 8;
                                Rasterizer2D.depth_buffer[dest_offset] = depth;
                            }
                            depth += depth_slope;
                            dest_offset++;
                            offsets += var18;

                            if((src = texel_map[get_texel_pos((offsets & 4032) + (offsets >>> 26))]) != 0) {
                                dest[dest_offset] = ((src & 16711935) * var16 & -16711936) + ((src & '\uff00') * var16 & 16711680) >> 8;
                                Rasterizer2D.depth_buffer[dest_offset] = depth;
                            }
                            depth += depth_slope;
                            dest_offset++;
                            offsets += var18;

                            if((src = texel_map[get_texel_pos((offsets & 4032) + (offsets >>> 26))]) != 0) {
                                dest[dest_offset] = ((src & 16711935) * var16 & -16711936) + ((src & '\uff00') * var16 & 16711680) >> 8;
                                Rasterizer2D.depth_buffer[dest_offset] = depth;
                            }
                            depth += depth_slope;
                            dest_offset++;
                            offsets += var18;

                            if((src = texel_map[get_texel_pos((offsets & 4032) + (offsets >>> 26))]) != 0) {
                                dest[dest_offset] = ((src & 16711935) * var16 & -16711936) + ((src & '\uff00') * var16 & 16711680) >> 8;
                                Rasterizer2D.depth_buffer[dest_offset] = depth;
                            }
                            depth += depth_slope;
                            dest_offset++;
                            offsets += var18;

                            if((src = texel_map[get_texel_pos((offsets & 4032) + (offsets >>> 26))]) != 0) {
                                dest[dest_offset] = ((src & 16711935) * var16 & -16711936) + ((src & '\uff00') * var16 & 16711680) >> 8;
                                Rasterizer2D.depth_buffer[dest_offset] = depth;
                            }
                            depth += depth_slope;
                            dest_offset++;
                            offsets += var18;

                            if((src = texel_map[get_texel_pos((offsets & 4032) + (offsets >>> 26))]) != 0) {
                                dest[dest_offset] = ((src & 16711935) * var16 & -16711936) + ((src & '\uff00') * var16 & 16711680) >> 8;
                                Rasterizer2D.depth_buffer[dest_offset] = depth;
                            }
                            depth += depth_slope;
                            dest_offset++;

                            rgb = var15;
                            loops = var23;
                            var9 += var12;
                            var10 += var13;
                            var11 += var14;
                            var21 = var11 >> 12;
                            if(var21 != 0) {
                                var15 = var9 / var21;
                                var23 = var10 / var21;
                                if(var15 < 0) {
                                    var15 = 0;
                                } else if(var15 > 4032) {
                                    var15 = 4032;
                                }
                            } else {
                                var15 = 0;
                                var23 = 0;
                            }
                            offsets = (rgb << 20) + loops;
                            var18 = (var15 - rgb >> 3 << 20) + (var23 - loops >> 3);
                            shade += gradient;
                            var16 = shade >> 8;
                        } while(--var22 > 0);
                    }
                    var22 = end_x - start_x & 7;
                    if(var22 > 0) {
                        do {
                            if((src = texel_map[get_texel_pos((offsets & 4032) + (offsets >>> 26))]) != 0) {
                                dest[dest_offset] = ((src & 16711935) * var16 & -16711936) + ((src & '\uff00') * var16 & 16711680) >> 8;
                                Rasterizer2D.depth_buffer[dest_offset] = depth;
                            }
                            depth += depth_slope;
                            dest_offset++;
                            offsets += var18;
                        } while(--var22 > 0);
                    }
                }
            } else {
                center_offset = start_x - center_x;
                var9 += (var12 >> 3) * center_offset;
                var10 += (var13 >> 3) * center_offset;
                var11 += (var14 >> 3) * center_offset;
                var21 = var11 >> 14;
                if(var21 != 0) {
                    rgb = var9 / var21;
                    loops = var10 / var21;
                    if(rgb < 0) {
                        rgb = 0;
                    } else if(rgb > 16256) {
                        rgb = 16256;
                    }
                } else {
                    rgb = 0;
                    loops = 0;
                }
                var9 += var12;
                var10 += var13;
                var11 += var14;
                var21 = var11 >> 14;
                if(var21 != 0) {
                    var15 = var9 / var21;
                    var23 = var10 / var21;
                    if(var15 < 0) {
                        var15 = 0;
                    } else if(var15 > 16256) {
                        var15 = 16256;
                    }
                } else {
                    var15 = 0;
                    var23 = 0;
                }
                offsets = (rgb << 18) + loops;
                var18 = (var15 - rgb >> 3 << 18) + (var23 - loops >> 3);
                var22 >>= 3;
                gradient <<= 3;
                var16 = shade >> 8;
                if(opaque) {
                    if(var22 > 0) {
                        do {
                            src = texel_map[get_texel_pos((offsets & 16256) + (offsets >>> 25))];
                            dest[dest_offset] = ((src & 16711935) * var16 & -16711936) + ((src & '\uff00') * var16 & 16711680) >> 8;
                            Rasterizer2D.depth_buffer[dest_offset++] = depth;
                            depth += depth_slope;
                            offsets += var18;

                            src = texel_map[get_texel_pos((offsets & 16256) + (offsets >>> 25))];
                            dest[dest_offset] = ((src & 16711935) * var16 & -16711936) + ((src & '\uff00') * var16 & 16711680) >> 8;
                            Rasterizer2D.depth_buffer[dest_offset++] = depth;
                            depth += depth_slope;
                            offsets += var18;

                            src = texel_map[get_texel_pos((offsets & 16256) + (offsets >>> 25))];
                            dest[dest_offset] = ((src & 16711935) * var16 & -16711936) + ((src & '\uff00') * var16 & 16711680) >> 8;
                            Rasterizer2D.depth_buffer[dest_offset++] = depth;
                            depth += depth_slope;
                            offsets += var18;

                            src = texel_map[get_texel_pos((offsets & 16256) + (offsets >>> 25))];
                            dest[dest_offset] = ((src & 16711935) * var16 & -16711936) + ((src & '\uff00') * var16 & 16711680) >> 8;
                            Rasterizer2D.depth_buffer[dest_offset++] = depth;
                            depth += depth_slope;
                            offsets += var18;

                            src = texel_map[get_texel_pos((offsets & 16256) + (offsets >>> 25))];
                            dest[dest_offset] = ((src & 16711935) * var16 & -16711936) + ((src & '\uff00') * var16 & 16711680) >> 8;
                            Rasterizer2D.depth_buffer[dest_offset++] = depth;
                            depth += depth_slope;
                            offsets += var18;

                            src = texel_map[get_texel_pos((offsets & 16256) + (offsets >>> 25))];
                            dest[dest_offset] = ((src & 16711935) * var16 & -16711936) + ((src & '\uff00') * var16 & 16711680) >> 8;
                            Rasterizer2D.depth_buffer[dest_offset++] = depth;
                            depth += depth_slope;
                            offsets += var18;

                            src = texel_map[get_texel_pos((offsets & 16256) + (offsets >>> 25))];
                            dest[dest_offset] = ((src & 16711935) * var16 & -16711936) + ((src & '\uff00') * var16 & 16711680) >> 8;
                            Rasterizer2D.depth_buffer[dest_offset++] = depth;
                            depth += depth_slope;
                            offsets += var18;

                            src = texel_map[get_texel_pos((offsets & 16256) + (offsets >>> 25))];
                            dest[dest_offset] = ((src & 16711935) * var16 & -16711936) + ((src & '\uff00') * var16 & 16711680) >> 8;
                            Rasterizer2D.depth_buffer[dest_offset++] = depth;
                            depth += depth_slope;

                            rgb = var15;
                            loops = var23;
                            var9 += var12;
                            var10 += var13;
                            var11 += var14;
                            var21 = var11 >> 14;
                            if(var21 != 0) {
                                var15 = var9 / var21;
                                var23 = var10 / var21;
                                if(var15 < 0) {
                                    var15 = 0;
                                } else if(var15 > 16256) {
                                    var15 = 16256;
                                }
                            } else {
                                var15 = 0;
                                var23 = 0;
                            }
                            offsets = (rgb << 18) + loops;
                            var18 = (var15 - rgb >> 3 << 18) + (var23 - loops >> 3);
                            shade += gradient;
                            var16 = shade >> 8;
                        } while(--var22 > 0);
                    }
                    var22 = end_x - start_x & 7;
                    if(var22 > 0) {
                        do {
                            src = texel_map[get_texel_pos((offsets & 16256) + (offsets >>> 25))];
                            dest[dest_offset] = ((src & 16711935) * var16 & -16711936) + ((src & '\uff00') * var16 & 16711680) >> 8;
                            Rasterizer2D.depth_buffer[dest_offset++] = depth;
                            depth += depth_slope;
                            offsets += var18;
                        } while(--var22 > 0);
                    }
                } else {
                    if(var22 > 0) {
                        do {
                            if((src = texel_map[get_texel_pos((offsets & 16256) + (offsets >>> 25))]) != 0) {
                                dest[dest_offset] = ((src & 16711935) * var16 & -16711936) + ((src & '\uff00') * var16 & 16711680) >> 8;
                                Rasterizer2D.depth_buffer[dest_offset] = depth;
                            }
                            depth += depth_slope;
                            dest_offset++;
                            offsets += var18;

                            if((src = texel_map[get_texel_pos((offsets & 16256) + (offsets >>> 25))]) != 0) {
                                dest[dest_offset] = ((src & 16711935) * var16 & -16711936) + ((src & '\uff00') * var16 & 16711680) >> 8;
                                Rasterizer2D.depth_buffer[dest_offset] = depth;
                            }
                            depth += depth_slope;
                            dest_offset++;
                            offsets += var18;

                            if((src = texel_map[get_texel_pos((offsets & 16256) + (offsets >>> 25))]) != 0) {
                                dest[dest_offset] = ((src & 16711935) * var16 & -16711936) + ((src & '\uff00') * var16 & 16711680) >> 8;
                                Rasterizer2D.depth_buffer[dest_offset] = depth;
                            }
                            depth += depth_slope;
                            dest_offset++;
                            offsets += var18;

                            if((src = texel_map[get_texel_pos((offsets & 16256) + (offsets >>> 25))]) != 0) {
                                dest[dest_offset] = ((src & 16711935) * var16 & -16711936) + ((src & '\uff00') * var16 & 16711680) >> 8;
                                Rasterizer2D.depth_buffer[dest_offset] = depth;
                            }
                            depth += depth_slope;
                            dest_offset++;
                            offsets += var18;

                            if((src = texel_map[get_texel_pos((offsets & 16256) + (offsets >>> 25))]) != 0) {
                                dest[dest_offset] = ((src & 16711935) * var16 & -16711936) + ((src & '\uff00') * var16 & 16711680) >> 8;
                                Rasterizer2D.depth_buffer[dest_offset] = depth;
                            }
                            depth += depth_slope;
                            dest_offset++;
                            offsets += var18;

                            if((src = texel_map[get_texel_pos((offsets & 16256) + (offsets >>> 25))]) != 0) {
                                dest[dest_offset] = ((src & 16711935) * var16 & -16711936) + ((src & '\uff00') * var16 & 16711680) >> 8;
                                Rasterizer2D.depth_buffer[dest_offset] = depth;
                            }
                            depth += depth_slope;
                            dest_offset++;
                            offsets += var18;

                            if((src = texel_map[get_texel_pos((offsets & 16256) + (offsets >>> 25))]) != 0) {
                                dest[dest_offset] = ((src & 16711935) * var16 & -16711936) + ((src & '\uff00') * var16 & 16711680) >> 8;
                                Rasterizer2D.depth_buffer[dest_offset] = depth;
                            }
                            depth += depth_slope;
                            dest_offset++;
                            offsets += var18;

                            if((src = texel_map[get_texel_pos((offsets & 16256) + (offsets >>> 25))]) != 0) {
                                dest[dest_offset] = ((src & 16711935) * var16 & -16711936) + ((src & '\uff00') * var16 & 16711680) >> 8;
                                Rasterizer2D.depth_buffer[dest_offset] = depth;
                            }
                            depth += depth_slope;
                            dest_offset++;

                            rgb = var15;
                            loops = var23;
                            var9 += var12;
                            var10 += var13;
                            var11 += var14;
                            var21 = var11 >> 14;
                            if(var21 != 0) {
                                var15 = var9 / var21;
                                var23 = var10 / var21;
                                if(var15 < 0) {
                                    var15 = 0;
                                } else if(var15 > 16256) {
                                    var15 = 16256;
                                }
                            } else {
                                var15 = 0;
                                var23 = 0;
                            }
                            offsets = (rgb << 18) + loops;
                            var18 = (var15 - rgb >> 3 << 18) + (var23 - loops >> 3);
                            shade += gradient;
                            var16 = shade >> 8;
                        } while(--var22 > 0);
                    }
                    var22 = end_x - start_x & 7;
                    if(var22 > 0) {
                        do {
                            if((src = texel_map[get_texel_pos((offsets & 16256) + (offsets >>> 25))]) != 0) {
                                dest[dest_offset] = ((src & 16711935) * var16 & -16711936) + ((src & '\uff00') * var16 & 16711680) >> 8;
                                Rasterizer2D.depth_buffer[dest_offset] = depth;
                            }
                            depth += depth_slope;
                            dest_offset++;
                            offsets += var18;
                        } while(--var22 > 0);
                    }
                }
            }
        }
    }

    public static final void drawFlatTriangle(int var0, int var1, int var2, int var3, int var4, int var5, int var6, float z_a, float z_b, float z_c) {
        int var7 = 0;
        if(var1 != var0) {
            var7 = (var4 - var3 << 14) / (var1 - var0);
        }

        int var9 = 0;
        if(var2 != var1) {
            var9 = (var5 - var4 << 14) / (var2 - var1);
        }

        int var8 = 0;
        if(var2 != var0) {
            var8 = (var3 - var5 << 14) / (var0 - var2);
        }

        if(var0 <= var1 && var0 <= var2) {
            if(var0 < Rasterizer2D.clip_bottom) {
                if(var1 > Rasterizer2D.clip_bottom) {
                    var1 = Rasterizer2D.clip_bottom;
                }

                if(var2 > Rasterizer2D.clip_bottom) {
                    var2 = Rasterizer2D.clip_bottom;
                }

                if(var1 < var2) {
                    var5 = var3 <<= 14;
                    if(var0 < 0) {
                        var5 -= var8 * var0;
                        var3 -= var7 * var0;
                        var0 = 0;
                    }

                    var4 <<= 14;
                    if(var1 < 0) {
                        var4 -= var9 * var1;
                        var1 = 0;
                    }

                    if((var0 == var1 || var8 >= var7) && (var0 != var1 || var8 <= var9)) {
                        var2 -= var1;
                        var1 -= var0;
                        var0 = line_offsets[var0];

                        while(true) {
                            --var1;
                            if(var1 < 0) {
                                while(true) {
                                    --var2;
                                    if(var2 < 0) {
                                        return;
                                    }

                                    method976(Rasterizer2D.pixels, var0, var6, 0, var4 >> 14, var5 >> 14);
                                    var5 += var8;
                                    var4 += var9;
                                    var0 += Rasterizer2D.width;
                                }
                            }

                            method976(Rasterizer2D.pixels, var0, var6, 0, var3 >> 14, var5 >> 14);
                            var5 += var8;
                            var3 += var7;
                            var0 += Rasterizer2D.width;
                        }
                    } else {
                        var2 -= var1;
                        var1 -= var0;
                        var0 = line_offsets[var0];

                        while(true) {
                            --var1;
                            if(var1 < 0) {
                                while(true) {
                                    --var2;
                                    if(var2 < 0) {
                                        return;
                                    }

                                    method976(Rasterizer2D.pixels, var0, var6, 0, var5 >> 14, var4 >> 14);
                                    var5 += var8;
                                    var4 += var9;
                                    var0 += Rasterizer2D.width;
                                }
                            }

                            method976(Rasterizer2D.pixels, var0, var6, 0, var5 >> 14, var3 >> 14);
                            var5 += var8;
                            var3 += var7;
                            var0 += Rasterizer2D.width;
                        }
                    }
                } else {
                    var4 = var3 <<= 14;
                    if(var0 < 0) {
                        var4 -= var8 * var0;
                        var3 -= var7 * var0;
                        var0 = 0;
                    }

                    var5 <<= 14;
                    if(var2 < 0) {
                        var5 -= var9 * var2;
                        var2 = 0;
                    }

                    if(var0 != var2 && var8 < var7 || var0 == var2 && var9 > var7) {
                        var1 -= var2;
                        var2 -= var0;
                        var0 = line_offsets[var0];

                        while(true) {
                            --var2;
                            if(var2 < 0) {
                                while(true) {
                                    --var1;
                                    if(var1 < 0) {
                                        return;
                                    }

                                    method976(Rasterizer2D.pixels, var0, var6, 0, var5 >> 14, var3 >> 14);
                                    var5 += var9;
                                    var3 += var7;
                                    var0 += Rasterizer2D.width;
                                }
                            }

                            method976(Rasterizer2D.pixels, var0, var6, 0, var4 >> 14, var3 >> 14);
                            var4 += var8;
                            var3 += var7;
                            var0 += Rasterizer2D.width;
                        }
                    } else {
                        var1 -= var2;
                        var2 -= var0;
                        var0 = line_offsets[var0];

                        while(true) {
                            --var2;
                            if(var2 < 0) {
                                while(true) {
                                    --var1;
                                    if(var1 < 0) {
                                        return;
                                    }

                                    method976(Rasterizer2D.pixels, var0, var6, 0, var3 >> 14, var5 >> 14);
                                    var5 += var9;
                                    var3 += var7;
                                    var0 += Rasterizer2D.width;
                                }
                            }

                            method976(Rasterizer2D.pixels, var0, var6, 0, var3 >> 14, var4 >> 14);
                            var4 += var8;
                            var3 += var7;
                            var0 += Rasterizer2D.width;
                        }
                    }
                }
            }
        } else if(var1 <= var2) {
            if(var1 < Rasterizer2D.clip_bottom) {
                if(var2 > Rasterizer2D.clip_bottom) {
                    var2 = Rasterizer2D.clip_bottom;
                }

                if(var0 > Rasterizer2D.clip_bottom) {
                    var0 = Rasterizer2D.clip_bottom;
                }

                if(var2 < var0) {
                    var3 = var4 <<= 14;
                    if(var1 < 0) {
                        var3 -= var7 * var1;
                        var4 -= var9 * var1;
                        var1 = 0;
                    }

                    var5 <<= 14;
                    if(var2 < 0) {
                        var5 -= var8 * var2;
                        var2 = 0;
                    }

                    if((var1 == var2 || var7 >= var9) && (var1 != var2 || var7 <= var8)) {
                        var0 -= var2;
                        var2 -= var1;
                        var1 = line_offsets[var1];

                        while(true) {
                            --var2;
                            if(var2 < 0) {
                                while(true) {
                                    --var0;
                                    if(var0 < 0) {
                                        return;
                                    }

                                    method976(Rasterizer2D.pixels, var1, var6, 0, var5 >> 14, var3 >> 14);
                                    var3 += var7;
                                    var5 += var8;
                                    var1 += Rasterizer2D.width;
                                }
                            }

                            method976(Rasterizer2D.pixels, var1, var6, 0, var4 >> 14, var3 >> 14);
                            var3 += var7;
                            var4 += var9;
                            var1 += Rasterizer2D.width;
                        }
                    } else {
                        var0 -= var2;
                        var2 -= var1;
                        var1 = line_offsets[var1];

                        while(true) {
                            --var2;
                            if(var2 < 0) {
                                while(true) {
                                    --var0;
                                    if(var0 < 0) {
                                        return;
                                    }

                                    method976(Rasterizer2D.pixels, var1, var6, 0, var3 >> 14, var5 >> 14);
                                    var3 += var7;
                                    var5 += var8;
                                    var1 += Rasterizer2D.width;
                                }
                            }

                            method976(Rasterizer2D.pixels, var1, var6, 0, var3 >> 14, var4 >> 14);
                            var3 += var7;
                            var4 += var9;
                            var1 += Rasterizer2D.width;
                        }
                    }
                } else {
                    var5 = var4 <<= 14;
                    if(var1 < 0) {
                        var5 -= var7 * var1;
                        var4 -= var9 * var1;
                        var1 = 0;
                    }

                    var3 <<= 14;
                    if(var0 < 0) {
                        var3 -= var8 * var0;
                        var0 = 0;
                    }

                    if(var7 < var9) {
                        var2 -= var0;
                        var0 -= var1;
                        var1 = line_offsets[var1];

                        while(true) {
                            --var0;
                            if(var0 < 0) {
                                while(true) {
                                    --var2;
                                    if(var2 < 0) {
                                        return;
                                    }

                                    method976(Rasterizer2D.pixels, var1, var6, 0, var3 >> 14, var4 >> 14);
                                    var3 += var8;
                                    var4 += var9;
                                    var1 += Rasterizer2D.width;
                                }
                            }

                            method976(Rasterizer2D.pixels, var1, var6, 0, var5 >> 14, var4 >> 14);
                            var5 += var7;
                            var4 += var9;
                            var1 += Rasterizer2D.width;
                        }
                    } else {
                        var2 -= var0;
                        var0 -= var1;
                        var1 = line_offsets[var1];

                        while(true) {
                            --var0;
                            if(var0 < 0) {
                                while(true) {
                                    --var2;
                                    if(var2 < 0) {
                                        return;
                                    }

                                    method976(Rasterizer2D.pixels, var1, var6, 0, var4 >> 14, var3 >> 14);
                                    var3 += var8;
                                    var4 += var9;
                                    var1 += Rasterizer2D.width;
                                }
                            }

                            method976(Rasterizer2D.pixels, var1, var6, 0, var4 >> 14, var5 >> 14);
                            var5 += var7;
                            var4 += var9;
                            var1 += Rasterizer2D.width;
                        }
                    }
                }
            }
        } else if(var2 < Rasterizer2D.clip_bottom) {
            if(var0 > Rasterizer2D.clip_bottom) {
                var0 = Rasterizer2D.clip_bottom;
            }

            if(var1 > Rasterizer2D.clip_bottom) {
                var1 = Rasterizer2D.clip_bottom;
            }

            if(var0 < var1) {
                var4 = var5 <<= 14;
                if(var2 < 0) {
                    var4 -= var9 * var2;
                    var5 -= var8 * var2;
                    var2 = 0;
                }

                var3 <<= 14;
                if(var0 < 0) {
                    var3 -= var7 * var0;
                    var0 = 0;
                }

                if(var9 < var8) {
                    var1 -= var0;
                    var0 -= var2;
                    var2 = line_offsets[var2];

                    while(true) {
                        --var0;
                        if(var0 < 0) {
                            while(true) {
                                --var1;
                                if(var1 < 0) {
                                    return;
                                }

                                method976(Rasterizer2D.pixels, var2, var6, 0, var4 >> 14, var3 >> 14);
                                var4 += var9;
                                var3 += var7;
                                var2 += Rasterizer2D.width;
                            }
                        }

                        method976(Rasterizer2D.pixels, var2, var6, 0, var4 >> 14, var5 >> 14);
                        var4 += var9;
                        var5 += var8;
                        var2 += Rasterizer2D.width;
                    }
                } else {
                    var1 -= var0;
                    var0 -= var2;
                    var2 = line_offsets[var2];

                    while(true) {
                        --var0;
                        if(var0 < 0) {
                            while(true) {
                                --var1;
                                if(var1 < 0) {
                                    return;
                                }

                                method976(Rasterizer2D.pixels, var2, var6, 0, var3 >> 14, var4 >> 14);
                                var4 += var9;
                                var3 += var7;
                                var2 += Rasterizer2D.width;
                            }
                        }

                        method976(Rasterizer2D.pixels, var2, var6, 0, var5 >> 14, var4 >> 14);
                        var4 += var9;
                        var5 += var8;
                        var2 += Rasterizer2D.width;
                    }
                }
            } else {
                var3 = var5 <<= 14;
                if(var2 < 0) {
                    var3 -= var9 * var2;
                    var5 -= var8 * var2;
                    var2 = 0;
                }

                var4 <<= 14;
                if(var1 < 0) {
                    var4 -= var7 * var1;
                    var1 = 0;
                }

                if(var9 < var8) {
                    var0 -= var1;
                    var1 -= var2;
                    var2 = line_offsets[var2];

                    while(true) {
                        --var1;
                        if(var1 < 0) {
                            while(true) {
                                --var0;
                                if(var0 < 0) {
                                    return;
                                }

                                method976(Rasterizer2D.pixels, var2, var6, 0, var4 >> 14, var5 >> 14);
                                var4 += var7;
                                var5 += var8;
                                var2 += Rasterizer2D.width;
                            }
                        }

                        method976(Rasterizer2D.pixels, var2, var6, 0, var3 >> 14, var5 >> 14);
                        var3 += var9;
                        var5 += var8;
                        var2 += Rasterizer2D.width;
                    }
                } else {
                    var0 -= var1;
                    var1 -= var2;
                    var2 = line_offsets[var2];

                    while(true) {
                        --var1;
                        if(var1 < 0) {
                            while(true) {
                                --var0;
                                if(var0 < 0) {
                                    return;
                                }

                                method976(Rasterizer2D.pixels, var2, var6, 0, var5 >> 14, var4 >> 14);
                                var4 += var7;
                                var5 += var8;
                                var2 += Rasterizer2D.width;
                            }
                        }

                        method976(Rasterizer2D.pixels, var2, var6, 0, var5 >> 14, var3 >> 14);
                        var3 += var9;
                        var5 += var8;
                        var2 += Rasterizer2D.width;
                    }
                }
            }
        }
    }

    static final void method976(int[] var0, int var1, int var2, int var3, int var4, int var5) {
        if(testX) {
            if(var5 > Rasterizer2D.center_x) {
                var5 = Rasterizer2D.center_x;
            }

            if(var4 < 0) {
                var4 = 0;
            }
        }

        if(var4 < var5) {
            var1 += var4;
            var3 = var5 - var4 >> 2;
            if(alpha != 0) {
                if(alpha == 254) {
                    while(true) {
                        --var3;
                        if(var3 < 0) {
                            var3 = var5 - var4 & 3;

                            while(true) {
                                --var3;
                                if(var3 < 0) {
                                    return;
                                }

                                var0[var1++] = var0[var1];
                            }
                        }

                        var0[var1++] = var0[var1];
                        var0[var1++] = var0[var1];
                        var0[var1++] = var0[var1];
                        var0[var1++] = var0[var1];
                    }
                } else {
                    int var6 = alpha;
                    int var8 = 256 - alpha;
                    var2 = ((var2 & 16711935) * var8 >> 8 & 16711935) + ((var2 & '\uff00') * var8 >> 8 & '\uff00');

                    while(true) {
                        --var3;
                        int var7;
                        if(var3 < 0) {
                            var3 = var5 - var4 & 3;

                            while(true) {
                                --var3;
                                if(var3 < 0) {
                                    return;
                                }

                                var7 = var0[var1];
                                var0[var1++] = var2 + ((var7 & 16711935) * var6 >> 8 & 16711935) + ((var7 & '\uff00') * var6 >> 8 & '\uff00');
                            }
                        }

                        var7 = var0[var1];
                        var0[var1++] = var2 + ((var7 & 16711935) * var6 >> 8 & 16711935) + ((var7 & '\uff00') * var6 >> 8 & '\uff00');
                        var7 = var0[var1];
                        var0[var1++] = var2 + ((var7 & 16711935) * var6 >> 8 & 16711935) + ((var7 & '\uff00') * var6 >> 8 & '\uff00');
                        var7 = var0[var1];
                        var0[var1++] = var2 + ((var7 & 16711935) * var6 >> 8 & 16711935) + ((var7 & '\uff00') * var6 >> 8 & '\uff00');
                        var7 = var0[var1];
                        var0[var1++] = var2 + ((var7 & 16711935) * var6 >> 8 & 16711935) + ((var7 & '\uff00') * var6 >> 8 & '\uff00');
                    }
                }
            } else {
                while(true) {
                    --var3;
                    if(var3 < 0) {
                        var3 = var5 - var4 & 3;

                        while(true) {
                            --var3;
                            if(var3 < 0) {
                                return;
                            }

                            var0[var1++] = var2;
                        }
                    }

                    var0[var1++] = var2;
                    var0[var1++] = var2;
                    var0[var1++] = var2;
                    var0[var1++] = var2;
                }
            }
        }
    }

    public static int clipMidX2;
    public static int clipNegativeMidX;
    public static int clipNegativeMidY;
    public static int clipMidY2;

    public static int media_length = 123;
    public static boolean low_detail = ClientConstants.RASTERIZER3D_LOW_MEMORY;
    public static boolean testX;
    public static boolean aBoolean1464 = true; //draw_texturized
    private static boolean opaque;
    public static boolean mapped = true;
    public static int alpha;
    public static int center_x;
    public static int center_y;
    public static int line_offsets[]; //line_offsets
    private static int texture_indices;
    public static IndexedImage tex_images[] = new IndexedImage[media_length];

    private static boolean[] transparent = new boolean[media_length];
    private static int[] avg_color = new int[media_length];
    private static int texel_indices;
    private static int[][][] texel_pool;
    private static int[][][] texel_cache = new int[media_length][][];
    public static int cache[] = new int[media_length];
    public static int pos;
    private static int[][] palletes = new int[media_length][];
    public static int scene_div_factor;
    public static int HSL_TO_RGB[] = new int[0x10000];
    private static int[] anIntArray1468;
    public static final int[] anIntArray1469;
    public static int SINE[];
    public static int COSINE[];

    static {

        scene_div_factor = 512;

        anIntArray1468 = new int[512];
        anIntArray1469 = new int[2048];
        SINE = new int[2048];
        COSINE = new int[2048];



        for(int index = 1; index < 512; index++) {
            anIntArray1468[index] = 32768 / index;
        }
        for(int index = 1; index < 2048;index++) {
            anIntArray1469[index] = 65536 / index;
        }
        for(int index = 0; index < 2048; index++) {
            SINE[index] = (int)(65536D * Math.sin((double) index * 0.0030679615D));
            COSINE[index] = (int)(65536D * Math.cos((double) index * 0.0030679615D));
        }

    }


    /*
     * For drawing gouraud triangles, when used for floor/tile textures like in SceneGraph at ancient cavern and edge dungeon,
     * the buffer offset needs to be increased. Explanation:
     * less pixels to check(the more offset u add the more far away it should check) looking at this i mean not just less pixels pixels that r closer to u don't get added to the depth buffer
     * about why its a black triangle the only thing i can think of is that they don't get rendered means the tile is just drawn w the default color which is black.
     *  drawGouraudTriangle is the equivalent of drawShadedTriangle.
     */
    public static void drawGouraudTriangle(int y1, int y2, int y3, int x1, int x2, int x3, int hsl1, int hsl2, int hsl3, float z1, float z2, float z3, int bufferOffset) {
        int rgb1 = HSL_TO_RGB[hsl1];
        int rgb2 = HSL_TO_RGB[hsl2];
        int rgb3 = HSL_TO_RGB[hsl3];
        int r1 = rgb1 >> 16 & 0xff;
        int g1 = rgb1 >> 8 & 0xff;
        int b1 = rgb1 & 0xff;
        int r2 = rgb2 >> 16 & 0xff;
        int g2 = rgb2 >> 8 & 0xff;
        int b2 = rgb2 & 0xff;
        int r3 = rgb3 >> 16 & 0xff;
        int g3 = rgb3 >> 8 & 0xff;
        int b3 = rgb3 & 0xff;
        int dx1 = 0;
        int dr1 = 0;
        int dg1 = 0;
        int db1 = 0;
        if (y2 != y1) {
            dx1 = (x2 - x1 << 16) / (y2 - y1);
            dr1 = (r2 - r1 << 16) / (y2 - y1);
            dg1 = (g2 - g1 << 16) / (y2 - y1);
            db1 = (b2 - b1 << 16) / (y2 - y1);
        }
        int dx2 = 0;
        int dr2 = 0;
        int dg2 = 0;
        int db2 = 0;
        if (y3 != y2) {
            dx2 = (x3 - x2 << 16) / (y3 - y2);
            dr2 = (r3 - r2 << 16) / (y3 - y2);
            dg2 = (g3 - g2 << 16) / (y3 - y2);
            db2 = (b3 - b2 << 16) / (y3 - y2);
        }
        int dx3 = 0;
        int dr3 = 0;
        int dg3 = 0;
        int db3 = 0;
        if (y3 != y1) {
            dx3 = (x1 - x3 << 16) / (y1 - y3);
            dr3 = (r1 - r3 << 16) / (y1 - y3);
            dg3 = (g1 - g3 << 16) / (y1 - y3);
            db3 = (b1 - b3 << 16) / (y1 - y3);
        }

        float x21 = x2 - x1;
        float y32 = y2 - y1;
        float x31 = x3 - x1;
        float y31 = y3 - y1;
        float z21 = z2 - z1;
        float z31 = z3 - z1;

        float div = x21 * y31 - x31 * y32;
        float depthSlope = (z21 * y31 - z31 * y32) / div;
        float depthScale = (z31 * x21 - z21 * x31) / div;

        if (y1 <= y2 && y1 <= y3) {
            if (y1 >= Rasterizer2D.clip_bottom) {
                return;
            }
            if (y2 > Rasterizer2D.clip_bottom) {
                y2 = Rasterizer2D.clip_bottom;
            }
            if (y3 > Rasterizer2D.clip_bottom) {
                y3 = Rasterizer2D.clip_bottom;
            }
            z1 = z1 - depthSlope * x1 + depthSlope;
            if (y2 < y3) {
                x3 = x1 <<= 16;
                r3 = r1 <<= 16;
                g3 = g1 <<= 16;
                b3 = b1 <<= 16;
                if (y1 < 0) {
                    x3 -= dx3 * y1;
                    x1 -= dx1 * y1;
                    r3 -= dr3 * y1;
                    g3 -= dg3 * y1;
                    b3 -= db3 * y1;
                    r1 -= dr1 * y1;
                    g1 -= dg1 * y1;
                    b1 -= db1 * y1;
                    z1 -= depthScale * y1;
                    y1 = 0;
                }
                x2 <<= 16;
                r2 <<= 16;
                g2 <<= 16;
                b2 <<= 16;
                if (y2 < 0) {
                    x2 -= dx2 * y2;
                    r2 -= dr2 * y2;
                    g2 -= dg2 * y2;
                    b2 -= db2 * y2;
                    y2 = 0;
                }

                // System.err.println("Shifted x3: " + (x3 >> 16) + " | Shifted: x2 " + (x2 >> 16) + " | y1: " + y1);

                //   System.err.println("Called lol");
                //System.err.println("X1 x2 x3, y1, y2, y3: " + x1 + " | " + x2 + " | " + x3 + " | " + y1 + " | " + y2 + " | " + y3);
                //y1, x1 >> 16, x3 >> 16
                //  Rasterizer2D.draw_line((x1 >> 16), y1, (x3 >> 16), y2);

                if (y1 != y2 && dx3 < dx1 || y1 == y2 && dx3 > dx2) {
                    y3 -= y2;
                    y2 -= y1;
                    for (y1 = line_offsets[y1]; --y2 >= 0; y1 += Rasterizer2D.width) {
                        drawGouraudScanline(Rasterizer2D.pixels, y1, x3 >> 16, x1 >> 16, r3, g3, b3, r1, g1, b1, z1,
                            depthSlope, bufferOffset);
                        x3 += dx3;
                        x1 += dx1;
                        r3 += dr3;
                        g3 += dg3;
                        b3 += db3;
                        r1 += dr1;
                        g1 += dg1;
                        b1 += db1;
                        z1 += depthScale;
                    }
                    while (--y3 >= 0) {
                        drawGouraudScanline(Rasterizer2D.pixels, y1, x3 >> 16, x2 >> 16, r3, g3, b3, r2, g2, b2, z1,
                            depthSlope, bufferOffset);

                        x3 += dx3;
                        x2 += dx2;
                        r3 += dr3;
                        g3 += dg3;
                        b3 += db3;
                        r2 += dr2;
                        g2 += dg2;
                        b2 += db2;
                        y1 += Rasterizer2D.width;
                        z1 += depthScale;
                    }
                    return;
                }
                y3 -= y2;
                y2 -= y1;
                for (y1 = line_offsets[y1]; --y2 >= 0; y1 += Rasterizer2D.width) {
                    drawGouraudScanline(Rasterizer2D.pixels, y1, x1 >> 16, x3 >> 16, r1, g1, b1, r3, g3, b3, z1,
                        depthSlope, bufferOffset);
                    x3 += dx3;
                    x1 += dx1;
                    r3 += dr3;
                    g3 += dg3;
                    b3 += db3;
                    r1 += dr1;
                    g1 += dg1;
                    b1 += db1;
                    z1 += depthScale;
                }
                while (--y3 >= 0) {
                    drawGouraudScanline(Rasterizer2D.pixels, y1, x2 >> 16, x3 >> 16, r2, g2, b2, r3, g3, b3, z1,
                        depthSlope, bufferOffset);
                    x3 += dx3;
                    x2 += dx2;
                    r3 += dr3;
                    g3 += dg3;
                    b3 += db3;
                    r2 += dr2;
                    g2 += dg2;
                    b2 += db2;
                    y1 += Rasterizer2D.width;
                    z1 += depthScale;
                }
                return;
            }
            x2 = x1 <<= 16;
            r2 = r1 <<= 16;
            g2 = g1 <<= 16;
            b2 = b1 <<= 16;
            if (y1 < 0) {
                x2 -= dx3 * y1;
                x1 -= dx1 * y1;
                r2 -= dr3 * y1;
                g2 -= dg3 * y1;
                b2 -= db3 * y1;
                r1 -= dr1 * y1;
                g1 -= dg1 * y1;
                b1 -= db1 * y1;
                z1 -= depthScale * y1;
                y1 = 0;
            }
            x3 <<= 16;
            r3 <<= 16;
            g3 <<= 16;
            b3 <<= 16;
            if (y3 < 0) {
                x3 -= dx2 * y3;
                r3 -= dr2 * y3;
                g3 -= dg2 * y3;
                b3 -= db2 * y3;
                y3 = 0;
            }
            if (y1 != y3 && dx3 < dx1 || y1 == y3 && dx2 > dx1) {
                y2 -= y3;
                y3 -= y1;
                for (y1 = line_offsets[y1]; --y3 >= 0; y1 += Rasterizer2D.width) {
                    drawGouraudScanline(Rasterizer2D.pixels, y1, x2 >> 16, x1 >> 16, r2, g2, b2, r1, g1, b1, z1,
                        depthSlope, bufferOffset);
                    x2 += dx3;
                    x1 += dx1;
                    r2 += dr3;
                    g2 += dg3;
                    b2 += db3;
                    r1 += dr1;
                    g1 += dg1;
                    b1 += db1;
                    z1 += depthScale;
                }
                while (--y2 >= 0) {
                    drawGouraudScanline(Rasterizer2D.pixels, y1, x3 >> 16, x1 >> 16, r3, g3, b3, r1, g1, b1, z1,
                        depthSlope, bufferOffset);
                    x3 += dx2;
                    x1 += dx1;
                    r3 += dr2;
                    g3 += dg2;
                    b3 += db2;
                    r1 += dr1;
                    g1 += dg1;
                    b1 += db1;
                    y1 += Rasterizer2D.width;
                    z1 += depthScale;
                }
                return;
            }
            y2 -= y3;
            y3 -= y1;
            for (y1 = line_offsets[y1]; --y3 >= 0; y1 += Rasterizer2D.width) {
                drawGouraudScanline(Rasterizer2D.pixels, y1, x1 >> 16, x2 >> 16, r1, g1, b1, r2, g2, b2, z1, depthSlope, bufferOffset);
                x2 += dx3;
                x1 += dx1;
                r2 += dr3;
                g2 += dg3;
                b2 += db3;
                r1 += dr1;
                g1 += dg1;
                b1 += db1;
                z1 += depthScale;
            }
            while (--y2 >= 0) {
                drawGouraudScanline(Rasterizer2D.pixels, y1, x1 >> 16, x3 >> 16, r1, g1, b1, r3, g3, b3, z1, depthSlope, bufferOffset);
                x3 += dx2;
                x1 += dx1;
                r3 += dr2;
                g3 += dg2;
                b3 += db2;
                r1 += dr1;
                g1 += dg1;
                b1 += db1;
                y1 += Rasterizer2D.width;
                z1 += depthScale;
            }
            return;
        }
        if (y2 <= y3) {
            if (y2 >= Rasterizer2D.clip_bottom) {
                return;
            }
            if (y3 > Rasterizer2D.clip_bottom) {
                y3 = Rasterizer2D.clip_bottom;
            }
            if (y1 > Rasterizer2D.clip_bottom) {
                y1 = Rasterizer2D.clip_bottom;
            }
            z2 = z2 - depthSlope * x2 + depthSlope;
            if (y3 < y1) {
                x1 = x2 <<= 16;
                r1 = r2 <<= 16;
                g1 = g2 <<= 16;
                b1 = b2 <<= 16;
                if (y2 < 0) {
                    x1 -= dx1 * y2;
                    x2 -= dx2 * y2;
                    r1 -= dr1 * y2;
                    g1 -= dg1 * y2;
                    b1 -= db1 * y2;
                    r2 -= dr2 * y2;
                    g2 -= dg2 * y2;
                    b2 -= db2 * y2;
                    z2 -= depthScale * y2;
                    y2 = 0;
                }
                x3 <<= 16;
                r3 <<= 16;
                g3 <<= 16;
                b3 <<= 16;
                if (y3 < 0) {
                    x3 -= dx3 * y3;
                    r3 -= dr3 * y3;
                    g3 -= dg3 * y3;
                    b3 -= db3 * y3;
                    y3 = 0;
                }
                if (y2 != y3 && dx1 < dx2 || y2 == y3 && dx1 > dx3) {
                    y1 -= y3;
                    y3 -= y2;
                    for (y2 = line_offsets[y2]; --y3 >= 0; y2 += Rasterizer2D.width) {
                        drawGouraudScanline(Rasterizer2D.pixels, y2, x1 >> 16, x2 >> 16, r1, g1, b1, r2, g2, b2, z2,
                            depthSlope, bufferOffset);
                        x1 += dx1;
                        x2 += dx2;
                        r1 += dr1;
                        g1 += dg1;
                        b1 += db1;
                        r2 += dr2;
                        g2 += dg2;
                        b2 += db2;
                        z2 += depthScale;
                    }
                    while (--y1 >= 0) {
                        drawGouraudScanline(Rasterizer2D.pixels, y2, x1 >> 16, x3 >> 16, r1, g1, b1, r3, g3, b3, z2,
                            depthSlope, bufferOffset);
                        x1 += dx1;
                        x3 += dx3;
                        r1 += dr1;
                        g1 += dg1;
                        b1 += db1;
                        r3 += dr3;
                        g3 += dg3;
                        b3 += db3;
                        y2 += Rasterizer2D.width;
                        z2 += depthScale;
                    }
                    return;
                }
                y1 -= y3;
                y3 -= y2;
                for (y2 = line_offsets[y2]; --y3 >= 0; y2 += Rasterizer2D.width) {
                    drawGouraudScanline(Rasterizer2D.pixels, y2, x2 >> 16, x1 >> 16, r2, g2, b2, r1, g1, b1, z2,
                        depthSlope, bufferOffset);
                    x1 += dx1;
                    x2 += dx2;
                    r1 += dr1;
                    g1 += dg1;
                    b1 += db1;
                    r2 += dr2;
                    g2 += dg2;
                    b2 += db2;
                    z2 += depthScale;
                }
                while (--y1 >= 0) {
                    drawGouraudScanline(Rasterizer2D.pixels, y2, x3 >> 16, x1 >> 16, r3, g3, b3, r1, g1, b1, z2,
                        depthSlope, bufferOffset);
                    x1 += dx1;
                    x3 += dx3;
                    r1 += dr1;
                    g1 += dg1;
                    b1 += db1;
                    r3 += dr3;
                    g3 += dg3;
                    b3 += db3;
                    y2 += Rasterizer2D.width;
                    z2 += depthScale;
                }
                return;
            }
            x3 = x2 <<= 16;
            r3 = r2 <<= 16;
            g3 = g2 <<= 16;
            b3 = b2 <<= 16;
            if (y2 < 0) {
                x3 -= dx1 * y2;
                x2 -= dx2 * y2;
                r3 -= dr1 * y2;
                g3 -= dg1 * y2;
                b3 -= db1 * y2;
                r2 -= dr2 * y2;
                g2 -= dg2 * y2;
                b2 -= db2 * y2;
                z2 -= depthScale * y2;
                y2 = 0;
            }
            x1 <<= 16;
            r1 <<= 16;
            g1 <<= 16;
            b1 <<= 16;
            if (y1 < 0) {
                x1 -= dx3 * y1;
                r1 -= dr3 * y1;
                g1 -= dg3 * y1;
                b1 -= db3 * y1;
                y1 = 0;
            }
            if (dx1 < dx2) {
                y3 -= y1;
                y1 -= y2;
                for (y2 = line_offsets[y2]; --y1 >= 0; y2 += Rasterizer2D.width) {
                    drawGouraudScanline(Rasterizer2D.pixels, y2, x3 >> 16, x2 >> 16, r3, g3, b3, r2, g2, b2, z2,
                        depthSlope, bufferOffset);
                    x3 += dx1;
                    x2 += dx2;
                    r3 += dr1;
                    g3 += dg1;
                    b3 += db1;
                    r2 += dr2;
                    g2 += dg2;
                    b2 += db2;
                    z2 += depthScale;
                }
                while (--y3 >= 0) {
                    drawGouraudScanline(Rasterizer2D.pixels, y2, x1 >> 16, x2 >> 16, r1, g1, b1, r2, g2, b2, z2,
                        depthSlope, bufferOffset);
                    x1 += dx3;
                    x2 += dx2;
                    r1 += dr3;
                    g1 += dg3;
                    b1 += db3;
                    r2 += dr2;
                    g2 += dg2;
                    b2 += db2;
                    y2 += Rasterizer2D.width;
                    z2 += depthScale;
                }
                return;
            }
            y3 -= y1;
            y1 -= y2;
            for (y2 = line_offsets[y2]; --y1 >= 0; y2 += Rasterizer2D.width) {
                drawGouraudScanline(Rasterizer2D.pixels, y2, x2 >> 16, x3 >> 16, r2, g2, b2, r3, g3, b3, z2, depthSlope, bufferOffset);
                x3 += dx1;
                x2 += dx2;
                r3 += dr1;
                g3 += dg1;
                b3 += db1;
                r2 += dr2;
                g2 += dg2;
                b2 += db2;
                z2 += depthScale;
            }
            while (--y3 >= 0) {
                drawGouraudScanline(Rasterizer2D.pixels, y2, x2 >> 16, x1 >> 16, r2, g2, b2, r1, g1, b1, z2, depthSlope, bufferOffset);
                x1 += dx3;
                x2 += dx2;
                r1 += dr3;
                g1 += dg3;
                b1 += db3;
                r2 += dr2;
                g2 += dg2;
                b2 += db2;
                y2 += Rasterizer2D.width;
                z2 += depthScale;
            }
            return;
        }
        if (y3 >= Rasterizer2D.clip_bottom) {
            return;
        }
        if (y1 > Rasterizer2D.clip_bottom) {
            y1 = Rasterizer2D.clip_bottom;
        }
        if (y2 > Rasterizer2D.clip_bottom) {
            y2 = Rasterizer2D.clip_bottom;
        }
        z3 = z3 - depthSlope * x3 + depthSlope;
        if (y1 < y2) {
            x2 = x3 <<= 16;
            r2 = r3 <<= 16;
            g2 = g3 <<= 16;
            b2 = b3 <<= 16;
            if (y3 < 0) {
                x2 -= dx2 * y3;
                x3 -= dx3 * y3;
                r2 -= dr2 * y3;
                g2 -= dg2 * y3;
                b2 -= db2 * y3;
                r3 -= dr3 * y3;
                g3 -= dg3 * y3;
                b3 -= db3 * y3;
                z3 -= depthScale * y3;
                y3 = 0;
            }
            x1 <<= 16;
            r1 <<= 16;
            g1 <<= 16;
            b1 <<= 16;
            if (y1 < 0) {
                x1 -= dx1 * y1;
                r1 -= dr1 * y1;
                g1 -= dg1 * y1;
                b1 -= db1 * y1;
                y1 = 0;
            }
            if (dx2 < dx3) {
                y2 -= y1;
                y1 -= y3;
                for (y3 = line_offsets[y3]; --y1 >= 0; y3 += Rasterizer2D.width) {
                    drawGouraudScanline(Rasterizer2D.pixels, y3, x2 >> 16, x3 >> 16, r2, g2, b2, r3, g3, b3, z3,
                        depthSlope, bufferOffset);
                    x2 += dx2;
                    x3 += dx3;
                    r2 += dr2;
                    g2 += dg2;
                    b2 += db2;
                    r3 += dr3;
                    g3 += dg3;
                    b3 += db3;
                    z3 += depthScale;
                }
                while (--y2 >= 0) {
                    drawGouraudScanline(Rasterizer2D.pixels, y3, x2 >> 16, x1 >> 16, r2, g2, b2, r1, g1, b1, z3,
                        depthSlope, bufferOffset);
                    x2 += dx2;
                    x1 += dx1;
                    r2 += dr2;
                    g2 += dg2;
                    b2 += db2;
                    r1 += dr1;
                    g1 += dg1;
                    b1 += db1;
                    y3 += Rasterizer2D.width;
                    z3 += depthScale;
                }
                return;
            }
            y2 -= y1;
            y1 -= y3;
            for (y3 = line_offsets[y3]; --y1 >= 0; y3 += Rasterizer2D.width) {
                drawGouraudScanline(Rasterizer2D.pixels, y3, x3 >> 16, x2 >> 16, r3, g3, b3, r2, g2, b2, z3, depthSlope, bufferOffset);
                x2 += dx2;
                x3 += dx3;
                r2 += dr2;
                g2 += dg2;
                b2 += db2;
                r3 += dr3;
                g3 += dg3;
                b3 += db3;
                z3 += depthScale;
            }
            while (--y2 >= 0) {
                drawGouraudScanline(Rasterizer2D.pixels, y3, x1 >> 16, x2 >> 16, r1, g1, b1, r2, g2, b2, z3, depthSlope, bufferOffset);
                x2 += dx2;
                x1 += dx1;
                r2 += dr2;
                g2 += dg2;
                b2 += db2;
                r1 += dr1;
                g1 += dg1;
                b1 += db1;
                z3 += depthScale;
                y3 += Rasterizer2D.width;
            }
            return;
        }
        x1 = x3 <<= 16;
        r1 = r3 <<= 16;
        g1 = g3 <<= 16;
        b1 = b3 <<= 16;
        if (y3 < 0) {
            x1 -= dx2 * y3;
            x3 -= dx3 * y3;
            r1 -= dr2 * y3;
            g1 -= dg2 * y3;
            b1 -= db2 * y3;
            r3 -= dr3 * y3;
            g3 -= dg3 * y3;
            b3 -= db3 * y3;
            z3 -= depthScale * y3;
            y3 = 0;
        }
        x2 <<= 16;
        r2 <<= 16;
        g2 <<= 16;
        b2 <<= 16;
        if (y2 < 0) {
            x2 -= dx1 * y2;
            r2 -= dr1 * y2;
            g2 -= dg1 * y2;
            b2 -= db1 * y2;
            y2 = 0;
        }
        if (dx2 < dx3) {
            y1 -= y2;
            y2 -= y3;
            for (y3 = line_offsets[y3]; --y2 >= 0; y3 += Rasterizer2D.width) {
                drawGouraudScanline(Rasterizer2D.pixels, y3, x1 >> 16, x3 >> 16, r1, g1, b1, r3, g3, b3, z3, depthSlope, bufferOffset);
                x1 += dx2;
                x3 += dx3;
                r1 += dr2;
                g1 += dg2;
                b1 += db2;
                r3 += dr3;
                g3 += dg3;
                b3 += db3;
                z3 += depthScale;
            }
            while (--y1 >= 0) {
                drawGouraudScanline(Rasterizer2D.pixels, y3, x2 >> 16, x3 >> 16, r2, g2, b2, r3, g3, b3, z3, depthSlope, bufferOffset);
                x2 += dx1;
                x3 += dx3;
                r2 += dr1;
                g2 += dg1;
                b2 += db1;
                r3 += dr3;
                g3 += dg3;
                b3 += db3;
                z3 += depthScale;
                y3 += Rasterizer2D.width;
            }
            return;
        }
        y1 -= y2;
        y2 -= y3;
        for (y3 = line_offsets[y3]; --y2 >= 0; y3 += Rasterizer2D.width) {
            drawGouraudScanline(Rasterizer2D.pixels, y3, x3 >> 16, x1 >> 16, r3, g3, b3, r1, g1, b1, z3, depthSlope, bufferOffset);
            x1 += dx2;
            x3 += dx3;
            r1 += dr2;
            g1 += dg2;
            b1 += db2;
            r3 += dr3;
            g3 += dg3;
            b3 += db3;
            z3 += depthScale;
        }
        while (--y1 >= 0) {
            drawGouraudScanline(Rasterizer2D.pixels, y3, x3 >> 16, x2 >> 16, r3, g3, b3, r2, g2, b2, z3, depthSlope, bufferOffset);
            x2 += dx1;
            x3 += dx3;
            r2 += dr1;
            g2 += dg1;
            b2 += db1;
            r3 += dr3;
            g3 += dg3;
            b3 += db3;
            y3 += Rasterizer2D.width;
            z3 += depthScale;
        }
    }

    public static void drawGouraudScanline(int[] dest, int offset, int x1, int x2, int r1, int g1, int b1, int r2,
                                           int g2, int b2, float z1, float z2, int bufferOffset) {
        int n = x2 - x1;
        if (n <= 0) {
            return;
        }
        r2 = (r2 - r1) / n;
        g2 = (g2 - g1) / n;
        b2 = (b2 - b1) / n;
        if (testX) {
            if (x2 > Rasterizer2D.center_x) {
                n -= x2 - Rasterizer2D.center_x;
                x2 = Rasterizer2D.center_x;
            }
            if (x1 < 0) {
                n = x2;
                r1 -= x1 * r2;
                g1 -= x1 * g2;
                b1 -= x1 * b2;
                x1 = 0;
            }
        }
        if (x1 < x2) {
            offset += x1;
            z1 += z2 * x1;
            if (alpha == 0) {
                while (--n >= 0) {
                    if (!mapped || z1 < depth_buffer[offset] || z1 < depth_buffer[offset] + bufferOffset) {
                        dest[offset] = (r1 & 0xff0000) | (g1 >> 8 & 0xff00) | (b1 >> 16 & 0xff);
                        depth_buffer[offset] = z1;
                    }
                    z1 += z2;
                    r1 += r2;
                    g1 += g2;
                    b1 += b2;
                    offset++;
                }
            } else {
                final int a1 = alpha;
                final int a2 = 256 - alpha;
                int rgb;
                while (--n >= 0) {
                    if (!mapped || z1 < depth_buffer[offset] || z1 < depth_buffer[offset] + bufferOffset) {
                        rgb = r1 & 0xff0000 | g1 >> 8 & 0xff00 | b1 >> 16 & 0xff;
                        rgb = ((rgb & 0xff00ff) * a2 >> 8 & 0xff00ff) + ((rgb & 0xff00) * a2 >> 8 & 0xff00);
                        int dst = dest[offset];
                        dest[offset] = rgb + ((dst & 0xff00ff) * a1 >> 8 & 0xff00ff) + ((dst & 0xff00) * a1 >> 8 & 0xff00);
                        depth_buffer[offset] = z1;
                    }
                    offset++;
                    z1 += z2;
                    r1 += r2;
                    g1 += g2;
                    b1 += b2;
                }
            }
        }
    }

    public static void drawFlatTriangle(int y1, int y2, int y3, int x1, int x2, int x3, int rgb, float z1, float z2, float z3, int bufferOffset) {
        int dx1 = 0;
        if (y2 != y1) {
            dx1 = (x2 - x1 << 16) / (y2 - y1);
        }
        int dx2 = 0;
        if (y3 != y2) {
            dx2 = (x3 - x2 << 16) / (y3 - y2);
        }
        int dx3 = 0;
        if (y3 != y1) {
            dx3 = (x1 - x3 << 16) / (y1 - y3);
        }

        float x21 = x2 - x1;
        float y32 = y2 - y1;
        float x31 = x3 - x1;
        float y31 = y3 - y1;
        float z21 = z2 - z1;
        float z31 = z3 - z1;

        float div = x21 * y31 - x31 * y32;
        float depthSlope = (z21 * y31 - z31 * y32) / div;
        float depthScale = (z31 * x21 - z21 * x31) / div;

        if (y1 <= y2 && y1 <= y3) {
            if (y1 >= Rasterizer2D.clip_bottom) {
                return;
            }
            if (y2 > Rasterizer2D.clip_bottom) {
                y2 = Rasterizer2D.clip_bottom;
            }
            if (y3 > Rasterizer2D.clip_bottom) {
                y3 = Rasterizer2D.clip_bottom;
            }
            z1 = z1 - depthSlope * x1 + depthSlope;
            if (y2 < y3) {
                x3 = x1 <<= 16;
                if (y1 < 0) {
                    x3 -= dx3 * y1;
                    x1 -= dx1 * y1;
                    z1 -= depthScale * y1;
                    y1 = 0;
                }
                x2 <<= 16;
                if (y2 < 0) {
                    x2 -= dx2 * y2;
                    y2 = 0;
                }
                if (y1 != y2 && dx3 < dx1 || y1 == y2 && dx3 > dx2) {
                    y3 -= y2;
                    y2 -= y1;
                    for (y1 = line_offsets[y1]; --y2 >= 0; y1 += Rasterizer2D.width) {
                        drawFlatScanline(Rasterizer2D.pixels, y1, rgb, x3 >> 16, x1 >> 16, z1, depthSlope, bufferOffset);
                        z1 += depthScale;
                        x3 += dx3;
                        x1 += dx1;
                    }
                    while (--y3 >= 0) {
                        drawFlatScanline(Rasterizer2D.pixels, y1, rgb, x3 >> 16, x2 >> 16, z1, depthSlope, bufferOffset);
                        z1 += depthScale;
                        x3 += dx3;
                        x2 += dx2;
                        y1 += Rasterizer2D.width;
                    }
                    return;
                }
                y3 -= y2;
                y2 -= y1;
                for (y1 = line_offsets[y1]; --y2 >= 0; y1 += Rasterizer2D.width) {
                    drawFlatScanline(Rasterizer2D.pixels, y1, rgb, x1 >> 16, x3 >> 16, z1, depthSlope, bufferOffset);
                    z1 += depthScale;
                    x3 += dx3;
                    x1 += dx1;
                }
                while (--y3 >= 0) {
                    drawFlatScanline(Rasterizer2D.pixels, y1, rgb, x2 >> 16, x3 >> 16, z1, depthSlope, bufferOffset);
                    z1 += depthScale;
                    x3 += dx3;
                    x2 += dx2;
                    y1 += Rasterizer2D.width;
                }
                return;
            }
            x2 = x1 <<= 16;
            if (y1 < 0) {
                x2 -= dx3 * y1;
                x1 -= dx1 * y1;
                z1 -= depthScale * y1;
                y1 = 0;
            }
            x3 <<= 16;
            if (y3 < 0) {
                x3 -= dx2 * y3;
                y3 = 0;
            }
            if (y1 != y3 && dx3 < dx1 || y1 == y3 && dx2 > dx1) {
                y2 -= y3;
                y3 -= y1;
                for (y1 = line_offsets[y1]; --y3 >= 0; y1 += Rasterizer2D.width) {
                    drawFlatScanline(Rasterizer2D.pixels, y1, rgb, x2 >> 16, x1 >> 16, z1, depthSlope, bufferOffset);
                    z1 += depthScale;
                    x2 += dx3;
                    x1 += dx1;
                }
                while (--y2 >= 0) {
                    drawFlatScanline(Rasterizer2D.pixels, y1, rgb, x3 >> 16, x1 >> 16, z1, depthSlope, bufferOffset);
                    z1 += depthScale;
                    x3 += dx2;
                    x1 += dx1;
                    y1 += Rasterizer2D.width;
                }
                return;
            }
            y2 -= y3;
            y3 -= y1;
            for (y1 = line_offsets[y1]; --y3 >= 0; y1 += Rasterizer2D.width) {
                drawFlatScanline(Rasterizer2D.pixels, y1, rgb, x1 >> 16, x2 >> 16, z1, depthSlope, bufferOffset);
                z1 += depthScale;
                x2 += dx3;
                x1 += dx1;
            }
            while (--y2 >= 0) {
                drawFlatScanline(Rasterizer2D.pixels, y1, rgb, x1 >> 16, x3 >> 16, z1, depthSlope, bufferOffset);
                z1 += depthScale;
                x3 += dx2;
                x1 += dx1;
                y1 += Rasterizer2D.width;
            }
            return;
        }
        if (y2 <= y3) {
            if (y2 >= Rasterizer2D.clip_bottom) {
                return;
            }
            if (y3 > Rasterizer2D.clip_bottom) {
                y3 = Rasterizer2D.clip_bottom;
            }
            if (y1 > Rasterizer2D.clip_bottom) {
                y1 = Rasterizer2D.clip_bottom;
            }
            z2 = z2 - depthSlope * x2 + depthSlope;
            if (y3 < y1) {
                x1 = x2 <<= 16;
                if (y2 < 0) {
                    x1 -= dx1 * y2;
                    x2 -= dx2 * y2;
                    z2 -= depthScale * y2;
                    y2 = 0;
                }
                x3 <<= 16;
                if (y3 < 0) {
                    x3 -= dx3 * y3;
                    y3 = 0;
                }
                if (y2 != y3 && dx1 < dx2 || y2 == y3 && dx1 > dx3) {
                    y1 -= y3;
                    y3 -= y2;
                    for (y2 = line_offsets[y2]; --y3 >= 0; y2 += Rasterizer2D.width) {
                        drawFlatScanline(Rasterizer2D.pixels, y2, rgb, x1 >> 16, x2 >> 16, z2, depthSlope, bufferOffset);
                        z2 += depthScale;
                        x1 += dx1;
                        x2 += dx2;
                    }
                    while (--y1 >= 0) {
                        drawFlatScanline(Rasterizer2D.pixels, y2, rgb, x1 >> 16, x3 >> 16, z2, depthSlope, bufferOffset);
                        z2 += depthScale;
                        x1 += dx1;
                        x3 += dx3;
                        y2 += Rasterizer2D.width;
                    }
                    return;
                }
                y1 -= y3;
                y3 -= y2;
                for (y2 = line_offsets[y2]; --y3 >= 0; y2 += Rasterizer2D.width) {
                    drawFlatScanline(Rasterizer2D.pixels, y2, rgb, x2 >> 16, x1 >> 16, z2, depthSlope, bufferOffset);
                    z2 += depthScale;
                    x1 += dx1;
                    x2 += dx2;
                }
                while (--y1 >= 0) {
                    drawFlatScanline(Rasterizer2D.pixels, y2, rgb, x3 >> 16, x1 >> 16, z2, depthSlope, bufferOffset);
                    z2 += depthScale;
                    x1 += dx1;
                    x3 += dx3;
                    y2 += Rasterizer2D.width;
                }
                return;
            }
            x3 = x2 <<= 16;
            if (y2 < 0) {
                x3 -= dx1 * y2;
                x2 -= dx2 * y2;
                z2 -= depthScale * y2;
                y2 = 0;
            }
            x1 <<= 16;
            if (y1 < 0) {
                x1 -= dx3 * y1;
                y1 = 0;
            }
            if (dx1 < dx2) {
                y3 -= y1;
                y1 -= y2;
                for (y2 = line_offsets[y2]; --y1 >= 0; y2 += Rasterizer2D.width) {
                    drawFlatScanline(Rasterizer2D.pixels, y2, rgb, x3 >> 16, x2 >> 16, z2, depthSlope, bufferOffset);
                    z2 += depthScale;
                    x3 += dx1;
                    x2 += dx2;
                }
                while (--y3 >= 0) {
                    drawFlatScanline(Rasterizer2D.pixels, y2, rgb, x1 >> 16, x2 >> 16, z2, depthSlope, bufferOffset);
                    z2 += depthScale;
                    x1 += dx3;
                    x2 += dx2;
                    y2 += Rasterizer2D.width;
                }
                return;
            }
            y3 -= y1;
            y1 -= y2;
            for (y2 = line_offsets[y2]; --y1 >= 0; y2 += Rasterizer2D.width) {
                drawFlatScanline(Rasterizer2D.pixels, y2, rgb, x2 >> 16, x3 >> 16, z2, depthSlope, bufferOffset);
                z2 += depthScale;
                x3 += dx1;
                x2 += dx2;
            }
            while (--y3 >= 0) {
                drawFlatScanline(Rasterizer2D.pixels, y2, rgb, x2 >> 16, x1 >> 16, z2, depthSlope, bufferOffset);
                z2 += depthScale;
                x1 += dx3;
                x2 += dx2;
                y2 += Rasterizer2D.width;
            }
            return;
        }
        if (y3 >= Rasterizer2D.clip_bottom) {
            return;
        }
        if (y1 > Rasterizer2D.clip_bottom) {
            y1 = Rasterizer2D.clip_bottom;
        }
        if (y2 > Rasterizer2D.clip_bottom) {
            y2 = Rasterizer2D.clip_bottom;
        }
        z3 = z3 - depthSlope * x3 + depthSlope;
        if (y1 < y2) {
            x2 = x3 <<= 16;
            if (y3 < 0) {
                x2 -= dx2 * y3;
                x3 -= dx3 * y3;
                z3 -= depthScale * y3;
                y3 = 0;
            }
            x1 <<= 16;
            if (y1 < 0) {
                x1 -= dx1 * y1;
                y1 = 0;
            }
            if (dx2 < dx3) {
                y2 -= y1;
                y1 -= y3;
                for (y3 = line_offsets[y3]; --y1 >= 0; y3 += Rasterizer2D.width) {
                    drawFlatScanline(Rasterizer2D.pixels, y3, rgb, x2 >> 16, x3 >> 16, z3, depthSlope, bufferOffset);
                    z3 += depthScale;
                    x2 += dx2;
                    x3 += dx3;
                }
                while (--y2 >= 0) {
                    drawFlatScanline(Rasterizer2D.pixels, y3, rgb, x2 >> 16, x1 >> 16, z3, depthSlope, bufferOffset);
                    z3 += depthScale;
                    x2 += dx2;
                    x1 += dx1;
                    y3 += Rasterizer2D.width;
                }
                return;
            }
            y2 -= y1;
            y1 -= y3;
            for (y3 = line_offsets[y3]; --y1 >= 0; y3 += Rasterizer2D.width) {
                drawFlatScanline(Rasterizer2D.pixels, y3, rgb, x3 >> 16, x2 >> 16, z3, depthSlope, bufferOffset);
                z3 += depthScale;
                x2 += dx2;
                x3 += dx3;
            }
            while (--y2 >= 0) {
                drawFlatScanline(Rasterizer2D.pixels, y3, rgb, x1 >> 16, x2 >> 16, z3, depthSlope, bufferOffset);
                z3 += depthScale;
                x2 += dx2;
                x1 += dx1;
                y3 += Rasterizer2D.width;
            }
            return;
        }
        x1 = x3 <<= 16;
        if (y3 < 0) {
            x1 -= dx2 * y3;
            x3 -= dx3 * y3;
            z3 -= depthScale * y3;
            y3 = 0;
        }
        x2 <<= 16;
        if (y2 < 0) {
            x2 -= dx1 * y2;
            y2 = 0;
        }
        if (dx2 < dx3) {
            y1 -= y2;
            y2 -= y3;
            for (y3 = line_offsets[y3]; --y2 >= 0; y3 += Rasterizer2D.width) {
                drawFlatScanline(Rasterizer2D.pixels, y3, rgb, x1 >> 16, x3 >> 16, z3, depthSlope, bufferOffset);
                z3 += depthScale;
                x1 += dx2;
                x3 += dx3;
            }
            while (--y1 >= 0) {
                drawFlatScanline(Rasterizer2D.pixels, y3, rgb, x2 >> 16, x3 >> 16, z3, depthSlope, bufferOffset);
                z3 += depthScale;
                x2 += dx1;
                x3 += dx3;
                y3 += Rasterizer2D.width;
            }
            return;
        }
        y1 -= y2;
        y2 -= y3;
        for (y3 = line_offsets[y3]; --y2 >= 0; y3 += Rasterizer2D.width) {
            drawFlatScanline(Rasterizer2D.pixels, y3, rgb, x3 >> 16, x1 >> 16, z3, depthSlope, bufferOffset);
            z3 += depthScale;
            x1 += dx2;
            x3 += dx3;
        }
        while (--y1 >= 0) {
            drawFlatScanline(Rasterizer2D.pixels, y3, rgb, x3 >> 16, x2 >> 16, z3, depthSlope, bufferOffset);
            z3 += depthScale;
            x2 += dx1;
            x3 += dx3;
            y3 += Rasterizer2D.width;
        }
    }

    private static void drawFlatScanline(int[] dest, int offset, int rgb, int x1, int x2, float z1, float z2, int bufferOffset) {
        if (x1 >= x2) {
            return;
        }
        if (testX) {
            if (x2 > Rasterizer2D.center_x) {
                x2 = Rasterizer2D.center_x;
            }
            if (x1 < 0) {
                x1 = 0;
            }
        }
        if (x1 >= x2) {
            return;
        }
        offset += x1;
        z1 += z2 * x1;
        int n = x2 - x1;
        if (alpha == 0) {
            while (--n >= 0) {
                if (!mapped || z1 < depth_buffer[offset] || z1 < depth_buffer[offset] + bufferOffset) {
                    dest[offset] = rgb;
                    depth_buffer[offset] = z1;
                }
                z1 += z2;
                offset++;
            }
        } else {
            final int a1 = alpha;
            final int a2 = 256 - alpha;
            rgb = ((rgb & 0xff00ff) * a2 >> 8 & 0xff00ff) + ((rgb & 0xff00) * a2 >> 8 & 0xff00);
            while (--n >= 0) {
                if (!mapped || z1 < depth_buffer[offset] || z1 < depth_buffer[offset] + bufferOffset) {
                    dest[offset] = rgb + ((dest[offset] & 0xff00ff) * a1 >> 8 & 0xff00ff) + ((dest[offset] & 0xff00) * a1 >> 8 & 0xff00);
                    depth_buffer[offset] = z1;
                }
                z1 += z2;
                offset++;
            }
        }
    }

    public static void drawTexturedTriangle(int y1, int y2, int y3, int x1, int x2, int x3, int c1, int c2, int c3, int tx1, int tx2, int tx3, int ty1, int ty2, int ty3, int tz1, int tz2, int tz3, int tex, float z1, float z2, float z3, int bufferOffset) {
        //c1 c2 c3 water texture fix (tex id 1) by suic
        c1 = tex == 1 ? c1 << 1 : 0x7F - c1 << 1;
        c2 = tex == 1 ? c2 << 1 : 0x7F - c2 << 1;
        c3 = tex == 1 ? c3 << 1 : 0x7F - c3 << 1;
        int texels[] = get_texels(tex)[scale];
        opaque = !transparent[tex];
        tx2 = tx1 - tx2;
        ty2 = ty1 - ty2;
        tz2 = tz1 - tz2;
        tx3 -= tx1;
        ty3 -= ty1;
        tz3 -= tz1;
        int l4 = tx3 * ty1 - ty3 * tx1 << (SceneGraph.view_dist == 9 ? 14 : 15);
        int i5 = ty3 * tz1 - tz3 * ty1 << 8;
        int j5 = tz3 * tx1 - tx3 * tz1 << 5;
        int k5 = tx2 * ty1 - ty2 * tx1 << (SceneGraph.view_dist == 9 ? 14 : 15);
        int l5 = ty2 * tz1 - tz2 * ty1 << 8;
        int i6 = tz2 * tx1 - tx2 * tz1 << 5;
        int j6 = ty2 * tx3 - tx2 * ty3 << (SceneGraph.view_dist == 9 ? 14 : 15);
        int k6 = tz2 * ty3 - ty2 * tz3 << 8;
        int l6 = tx2 * tz3 - tz2 * tx3 << 5;
        int i7 = 0;
        int j7 = 0;
        if (y2 != y1) {
            i7 = (x2 - x1 << 16) / (y2 - y1);
            j7 = (c2 - c1 << 16) / (y2 - y1);
        }
        int k7 = 0;
        int l7 = 0;
        if (y3 != y2) {
            k7 = (x3 - x2 << 16) / (y3 - y2);
            l7 = (c3 - c2 << 16) / (y3 - y2);
        }
        int i8 = 0;
        int j8 = 0;
        if (y3 != y1) {
            i8 = (x1 - x3 << 16) / (y1 - y3);
            j8 = (c1 - c3 << 16) / (y1 - y3);
        }

        float x21 = x2 - x1;
        float y32 = y2 - y1;
        float x31 = x3 - x1;
        float y31 = y3 - y1;
        float z21 = z2 - z1;
        float z31 = z3 - z1;

        float div = x21 * y31 - x31 * y32;
        float depthSlope = (z21 * y31 - z31 * y32) / div;
        float depthScale = (z31 * x21 - z21 * x31) / div;

        if (y1 <= y2 && y1 <= y3) {
            if (y1 >= Rasterizer2D.clip_bottom) {
                return;
            }
            if (y2 > Rasterizer2D.clip_bottom) {
                y2 = Rasterizer2D.clip_bottom;
            }
            if (y3 > Rasterizer2D.clip_bottom) {
                y3 = Rasterizer2D.clip_bottom;
            }
            z1 = z1 - depthSlope * x1 + depthSlope;
            if (y2 < y3) {
                x3 = x1 <<= 16;
                c3 = c1 <<= 16;
                if (y1 < 0) {
                    x3 -= i8 * y1;
                    x1 -= i7 * y1;
                    z1 -= depthScale * y1;
                    c3 -= j8 * y1;
                    c1 -= j7 * y1;
                    y1 = 0;
                }
                x2 <<= 16;
                c2 <<= 16;
                if (y2 < 0) {
                    x2 -= k7 * y2;
                    c2 -= l7 * y2;
                    y2 = 0;
                }
                int k8 = y1 - center_y;
                l4 += j5 * k8;
                k5 += i6 * k8;
                j6 += l6 * k8;
                if (y1 != y2 && i8 < i7 || y1 == y2 && i8 > k7) {
                    y3 -= y2;
                    y2 -= y1;
                    y1 = line_offsets[y1];
                    while (--y2 >= 0) {
                        drawTexturedScanline(Rasterizer2D.pixels, texels, y1, x3 >> 16, x1 >> 16, c3, c1, l4, k5, j6, i5, l5,
                            k6, z1, depthSlope, bufferOffset);
                        z1 += depthScale;
                        x3 += i8;
                        x1 += i7;
                        c3 += j8;
                        c1 += j7;
                        y1 += Rasterizer2D.width;
                        l4 += j5;
                        k5 += i6;
                        j6 += l6;
                    }
                    while (--y3 >= 0) {
                        drawTexturedScanline(Rasterizer2D.pixels, texels, y1, x3 >> 16, x2 >> 16, c3, c2, l4, k5, j6, i5, l5,
                            k6, z1, depthSlope, bufferOffset);
                        z1 += depthScale;
                        x3 += i8;
                        x2 += k7;
                        c3 += j8;
                        c2 += l7;
                        y1 += Rasterizer2D.width;
                        l4 += j5;
                        k5 += i6;
                        j6 += l6;
                    }
                    return;
                }
                y3 -= y2;
                y2 -= y1;
                y1 = line_offsets[y1];
                while (--y2 >= 0) {
                    drawTexturedScanline(Rasterizer2D.pixels, texels, y1, x1 >> 16, x3 >> 16, c1, c3, l4, k5, j6, i5, l5, k6,
                        z1, depthSlope, bufferOffset);
                    z1 += depthScale;
                    x3 += i8;
                    x1 += i7;
                    c3 += j8;
                    c1 += j7;
                    y1 += Rasterizer2D.width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                while (--y3 >= 0) {
                    drawTexturedScanline(Rasterizer2D.pixels, texels, y1, x2 >> 16, x3 >> 16, c2, c3, l4, k5, j6, i5, l5, k6,
                        z1, depthSlope, bufferOffset);
                    z1 += depthScale;
                    x3 += i8;
                    x2 += k7;
                    c3 += j8;
                    c2 += l7;
                    y1 += Rasterizer2D.width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                return;
            }
            x2 = x1 <<= 16;
            c2 = c1 <<= 16;
            if (y1 < 0) {
                x2 -= i8 * y1;
                z1 -= depthScale * y1;
                x1 -= i7 * y1;
                c2 -= j8 * y1;
                c1 -= j7 * y1;
                y1 = 0;
            }
            x3 <<= 16;
            c3 <<= 16;
            if (y3 < 0) {
                x3 -= k7 * y3;
                c3 -= l7 * y3;
                y3 = 0;
            }
            int l8 = y1 - center_y;
            l4 += j5 * l8;
            k5 += i6 * l8;
            j6 += l6 * l8;
            if (y1 != y3 && i8 < i7 || y1 == y3 && k7 > i7) {
                y2 -= y3;
                y3 -= y1;
                y1 = line_offsets[y1];
                while (--y3 >= 0) {
                    drawTexturedScanline(Rasterizer2D.pixels, texels, y1, x2 >> 16, x1 >> 16, c2, c1, l4, k5, j6, i5, l5, k6,
                        z1, depthSlope, bufferOffset);
                    z1 += depthScale;
                    x2 += i8;
                    x1 += i7;
                    c2 += j8;
                    c1 += j7;
                    y1 += Rasterizer2D.width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                while (--y2 >= 0) {
                    drawTexturedScanline(Rasterizer2D.pixels, texels, y1, x3 >> 16, x1 >> 16, c3, c1, l4, k5, j6, i5, l5, k6,
                        z1, depthSlope, bufferOffset);
                    z1 += depthScale;
                    x3 += k7;
                    x1 += i7;
                    c3 += l7;
                    c1 += j7;
                    y1 += Rasterizer2D.width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                return;
            }
            y2 -= y3;
            y3 -= y1;
            y1 = line_offsets[y1];
            while (--y3 >= 0) {
                drawTexturedScanline(Rasterizer2D.pixels, texels, y1, x1 >> 16, x2 >> 16, c1, c2, l4, k5, j6, i5, l5, k6, z1,
                    depthSlope, bufferOffset);
                z1 += depthScale;
                x2 += i8;
                x1 += i7;
                c2 += j8;
                c1 += j7;
                y1 += Rasterizer2D.width;
                l4 += j5;
                k5 += i6;
                j6 += l6;
            }
            while (--y2 >= 0) {
                drawTexturedScanline(Rasterizer2D.pixels, texels, y1, x1 >> 16, x3 >> 16, c1, c3, l4, k5, j6, i5, l5, k6, z1,
                    depthSlope, bufferOffset);
                z1 += depthScale;
                x3 += k7;
                x1 += i7;
                c3 += l7;
                c1 += j7;
                y1 += Rasterizer2D.width;
                l4 += j5;
                k5 += i6;
                j6 += l6;
            }
            return;
        }
        if (y2 <= y3) {
            if (y2 >= Rasterizer2D.clip_bottom) {
                return;
            }
            if (y3 > Rasterizer2D.clip_bottom) {
                y3 = Rasterizer2D.clip_bottom;
            }
            if (y1 > Rasterizer2D.clip_bottom) {
                y1 = Rasterizer2D.clip_bottom;
            }
            z2 = z2 - depthSlope * x2 + depthSlope;
            if (y3 < y1) {
                x1 = x2 <<= 16;
                c1 = c2 <<= 16;
                if (y2 < 0) {
                    x1 -= i7 * y2;
                    x2 -= k7 * y2;
                    z2 -= depthScale * y2;
                    c1 -= j7 * y2;
                    c2 -= l7 * y2;
                    y2 = 0;
                }
                x3 <<= 16;
                c3 <<= 16;
                if (y3 < 0) {
                    x3 -= i8 * y3;
                    c3 -= j8 * y3;
                    y3 = 0;
                }
                int i9 = y2 - center_y;
                l4 += j5 * i9;
                k5 += i6 * i9;
                j6 += l6 * i9;
                if (y2 != y3 && i7 < k7 || y2 == y3 && i7 > i8) {
                    y1 -= y3;
                    y3 -= y2;
                    y2 = line_offsets[y2];
                    while (--y3 >= 0) {
                        drawTexturedScanline(Rasterizer2D.pixels, texels, y2, x1 >> 16, x2 >> 16, c1, c2, l4, k5, j6, i5, l5,
                            k6, z2, depthSlope, bufferOffset);
                        z2 += depthScale;
                        x1 += i7;
                        x2 += k7;
                        c1 += j7;
                        c2 += l7;
                        y2 += Rasterizer2D.width;
                        l4 += j5;
                        k5 += i6;
                        j6 += l6;
                    }
                    while (--y1 >= 0) {
                        drawTexturedScanline(Rasterizer2D.pixels, texels, y2, x1 >> 16, x3 >> 16, c1, c3, l4, k5, j6, i5, l5,
                            k6, z2, depthSlope, bufferOffset);
                        z2 += depthScale;
                        x1 += i7;
                        x3 += i8;
                        c1 += j7;
                        c3 += j8;
                        y2 += Rasterizer2D.width;
                        l4 += j5;
                        k5 += i6;
                        j6 += l6;
                    }
                    return;
                }
                y1 -= y3;
                y3 -= y2;
                y2 = line_offsets[y2];
                while (--y3 >= 0) {
                    drawTexturedScanline(Rasterizer2D.pixels, texels, y2, x2 >> 16, x1 >> 16, c2, c1, l4, k5, j6, i5, l5, k6,
                        z2, depthSlope, bufferOffset);
                    z2 += depthScale;
                    x1 += i7;
                    x2 += k7;
                    c1 += j7;
                    c2 += l7;
                    y2 += Rasterizer2D.width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                while (--y1 >= 0) {
                    drawTexturedScanline(Rasterizer2D.pixels, texels, y2, x3 >> 16, x1 >> 16, c3, c1, l4, k5, j6, i5, l5, k6,
                        z2, depthSlope, bufferOffset);
                    z2 += depthScale;
                    x1 += i7;
                    x3 += i8;
                    c1 += j7;
                    c3 += j8;
                    y2 += Rasterizer2D.width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                return;
            }
            x3 = x2 <<= 16;
            c3 = c2 <<= 16;
            if (y2 < 0) {
                x3 -= i7 * y2;
                z2 -= depthScale * y2;
                x2 -= k7 * y2;
                c3 -= j7 * y2;
                c2 -= l7 * y2;
                y2 = 0;
            }
            x1 <<= 16;
            c1 <<= 16;
            if (y1 < 0) {
                x1 -= i8 * y1;
                c1 -= j8 * y1;
                y1 = 0;
            }
            int j9 = y2 - center_y;
            l4 += j5 * j9;
            k5 += i6 * j9;
            j6 += l6 * j9;
            if (i7 < k7) {
                y3 -= y1;
                y1 -= y2;
                y2 = line_offsets[y2];
                while (--y1 >= 0) {
                    drawTexturedScanline(Rasterizer2D.pixels, texels, y2, x3 >> 16, x2 >> 16, c3, c2, l4, k5, j6, i5, l5, k6,
                        z2, depthSlope, bufferOffset);
                    z2 += depthScale;
                    x3 += i7;
                    x2 += k7;
                    c3 += j7;
                    c2 += l7;
                    y2 += Rasterizer2D.width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                while (--y3 >= 0) {
                    drawTexturedScanline(Rasterizer2D.pixels, texels, y2, x1 >> 16, x2 >> 16, c1, c2, l4, k5, j6, i5, l5, k6,
                        z2, depthSlope, bufferOffset);
                    z2 += depthScale;
                    x1 += i8;
                    x2 += k7;
                    c1 += j8;
                    c2 += l7;
                    y2 += Rasterizer2D.width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                return;
            }
            y3 -= y1;
            y1 -= y2;
            y2 = line_offsets[y2];
            while (--y1 >= 0) {
                drawTexturedScanline(Rasterizer2D.pixels, texels, y2, x2 >> 16, x3 >> 16, c2, c3, l4, k5, j6, i5, l5, k6, z2,
                    depthSlope, bufferOffset);
                z2 += depthScale;
                x3 += i7;
                x2 += k7;
                c3 += j7;
                c2 += l7;
                y2 += Rasterizer2D.width;
                l4 += j5;
                k5 += i6;
                j6 += l6;
            }
            while (--y3 >= 0) {
                drawTexturedScanline(Rasterizer2D.pixels, texels, y2, x2 >> 16, x1 >> 16, c2, c1, l4, k5, j6, i5, l5, k6, z2,
                    depthSlope, bufferOffset);
                z2 += depthScale;
                x1 += i8;
                x2 += k7;
                c1 += j8;
                c2 += l7;
                y2 += Rasterizer2D.width;
                l4 += j5;
                k5 += i6;
                j6 += l6;
            }
            return;
        }
        if (y3 >= Rasterizer2D.clip_bottom) {
            return;
        }
        if (y1 > Rasterizer2D.clip_bottom) {
            y1 = Rasterizer2D.clip_bottom;
        }
        if (y2 > Rasterizer2D.clip_bottom) {
            y2 = Rasterizer2D.clip_bottom;
        }
        z3 = z3 - depthSlope * x3 + depthSlope;
        if (y1 < y2) {
            x2 = x3 <<= 16;
            c2 = c3 <<= 16;
            if (y3 < 0) {
                x2 -= k7 * y3;
                x3 -= i8 * y3;
                z3 -= depthScale * y3;
                c2 -= l7 * y3;
                c3 -= j8 * y3;
                y3 = 0;
            }
            x1 <<= 16;
            c1 <<= 16;
            if (y1 < 0) {
                x1 -= i7 * y1;
                c1 -= j7 * y1;
                y1 = 0;
            }
            int k9 = y3 - center_y;
            l4 += j5 * k9;
            k5 += i6 * k9;
            j6 += l6 * k9;
            if (k7 < i8) {
                y2 -= y1;
                y1 -= y3;
                y3 = line_offsets[y3];
                while (--y1 >= 0) {
                    drawTexturedScanline(Rasterizer2D.pixels, texels, y3, x2 >> 16, x3 >> 16, c2, c3, l4, k5, j6, i5, l5, k6,
                        z3, depthSlope, bufferOffset);
                    z3 += depthScale;
                    x2 += k7;
                    x3 += i8;
                    c2 += l7;
                    c3 += j8;
                    y3 += Rasterizer2D.width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                while (--y2 >= 0) {
                    drawTexturedScanline(Rasterizer2D.pixels, texels, y3, x2 >> 16, x1 >> 16, c2, c1, l4, k5, j6, i5, l5, k6,
                        z3, depthSlope, bufferOffset);
                    z3 += depthScale;
                    x2 += k7;
                    x1 += i7;
                    c2 += l7;
                    c1 += j7;
                    y3 += Rasterizer2D.width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                return;
            }
            y2 -= y1;
            y1 -= y3;
            y3 = line_offsets[y3];
            while (--y1 >= 0) {
                drawTexturedScanline(Rasterizer2D.pixels, texels, y3, x3 >> 16, x2 >> 16, c3, c2, l4, k5, j6, i5, l5, k6, z3,
                    depthSlope, bufferOffset);
                z3 += depthScale;
                x2 += k7;
                x3 += i8;
                c2 += l7;
                c3 += j8;
                y3 += Rasterizer2D.width;
                l4 += j5;
                k5 += i6;
                j6 += l6;
            }
            while (--y2 >= 0) {
                drawTexturedScanline(Rasterizer2D.pixels, texels, y3, x1 >> 16, x2 >> 16, c1, c2, l4, k5, j6, i5, l5, k6, z3,
                    depthSlope, bufferOffset);
                z3 += depthScale;
                x2 += k7;
                x1 += i7;
                c2 += l7;
                c1 += j7;
                y3 += Rasterizer2D.width;
                l4 += j5;
                k5 += i6;
                j6 += l6;
            }
            return;
        }
        x1 = x3 <<= 16;
        c1 = c3 <<= 16;
        if (y3 < 0) {
            x1 -= k7 * y3;
            x3 -= i8 * y3;
            z3 -= depthScale * y3;
            c1 -= l7 * y3;
            c3 -= j8 * y3;
            y3 = 0;
        }
        x2 <<= 16;
        c2 <<= 16;
        if (y2 < 0) {
            x2 -= i7 * y2;
            c2 -= j7 * y2;
            y2 = 0;
        }
        int l9 = y3 - center_y;
        l4 += j5 * l9;
        k5 += i6 * l9;
        j6 += l6 * l9;
        if (k7 < i8) {
            y1 -= y2;
            y2 -= y3;
            y3 = line_offsets[y3];
            while (--y2 >= 0) {
                drawTexturedScanline(Rasterizer2D.pixels, texels, y3, x1 >> 16, x3 >> 16, c1, c3, l4, k5, j6, i5, l5, k6, z3,
                    depthSlope, bufferOffset);
                z3 += depthScale;
                x1 += k7;
                x3 += i8;
                c1 += l7;
                c3 += j8;
                y3 += Rasterizer2D.width;
                l4 += j5;
                k5 += i6;
                j6 += l6;
            }
            while (--y1 >= 0) {
                drawTexturedScanline(Rasterizer2D.pixels, texels, y3, x2 >> 16, x3 >> 16, c2, c3, l4, k5, j6, i5, l5, k6, z3,
                    depthSlope, bufferOffset);
                z3 += depthScale;
                x2 += i7;
                x3 += i8;
                c2 += j7;
                c3 += j8;
                y3 += Rasterizer2D.width;
                l4 += j5;
                k5 += i6;
                j6 += l6;
            }
            return;
        }
        y1 -= y2;
        y2 -= y3;
        y3 = line_offsets[y3];
        while (--y2 >= 0) {
            drawTexturedScanline(Rasterizer2D.pixels, texels, y3, x3 >> 16, x1 >> 16, c3, c1, l4, k5, j6, i5, l5, k6, z3,
                depthSlope, bufferOffset);
            z3 += depthScale;
            x1 += k7;
            x3 += i8;
            c1 += l7;
            c3 += j8;
            y3 += Rasterizer2D.width;
            l4 += j5;
            k5 += i6;
            j6 += l6;
        }
        while (--y1 >= 0) {
            drawTexturedScanline(Rasterizer2D.pixels, texels, y3, x3 >> 16, x2 >> 16, c3, c2, l4, k5, j6, i5, l5, k6, z3,
                depthSlope, bufferOffset);
            z3 += depthScale;
            x2 += i7;
            x3 += i8;
            c2 += j7;
            c3 += j8;
            y3 += Rasterizer2D.width;
            l4 += j5;
            k5 += i6;
            j6 += l6;
        }
    }

    private static void drawTexturedScanline(int[] dest, int[] src, int offset, int x1, int x2, int hsl1, int hsl2, int t1, int t2, int t3, int t4, int t5, int t6, float z1, float z2, int bufferOffset) {
        int darken = 0;
        int srcPos = 0;
        if (x1 >= x2) {
            return;
        }
        int dl = (hsl2 - hsl1) / (x2 - x1);
        int n;
        if (testX) {
            if (x2 > Rasterizer2D.center_x) {
                x2 = Rasterizer2D.center_x;
            }
            if (x1 < 0) {
                hsl1 -= x1 * dl;
                x1 = 0;
            }
        }
        if (x1 >= x2) {
            return;
        }
        n = x2 - x1 >> 3;
        offset += x1;
        z1 += z2 * x1;
        int j4 = 0;
        int l4 = 0;
        int l6 = x1 - center_x;
        t1 += (t4 >> 3) * l6;
        t2 += (t5 >> 3) * l6;
        t3 += (t6 >> 3) * l6;
        int l5 = t3 >> 14;
        if (l5 != 0) {
            darken = t1 / l5;
            srcPos = t2 / l5;
            if (darken < 0) {
                darken = 0;
            } else if (darken > 16256) {
                darken = 16256;
            }
        }
        t1 += t4;
        t2 += t5;
        t3 += t6;
        l5 = t3 >> 14;
        if (l5 != 0) {
            j4 = t1 / l5;
            l4 = t2 / l5;
            if (j4 < 7) {
                j4 = 7;
            } else if (j4 > 16256) {
                j4 = 16256;
            }
        }
        int j7 = j4 - darken >> 3;
        int l7 = l4 - srcPos >> 3;
        if (opaque) {
            while (n-- > 0) {
                int rgb;
                int l;
                for (int x = 0; x < 8; x++) {
                    rgb = src[(srcPos & 0x3f80) + (darken >> 7)];
                    l = hsl1 >> 16;
                    if (!mapped || z1 < depth_buffer[offset] || z1 < depth_buffer[offset] + bufferOffset) {
                        dest[offset] = ((rgb & 0xff00ff) * l & ~0xff00ff) + ((rgb & 0xff00) * l & 0xff0000) >> 8;
                        depth_buffer[offset] = z1;
                    }
                    offset++;
                    z1 += z2;
                    darken += j7;
                    srcPos += l7;
                    hsl1 += dl;
                }
                t1 += t4;
                t2 += t5;
                t3 += t6;
                int i6 = t3 >> 14;
                if (i6 != 0) {
                    j4 = t1 / i6;
                    l4 = t2 / i6;
                    if (j4 < 7) {
                        j4 = 7;
                    } else if (j4 > 16256) {
                        j4 = 16256;
                    }
                }
                j7 = j4 - darken >> 3;
                l7 = l4 - srcPos >> 3;
                hsl1 += dl;
            }
            for (n = x2 - x1 & 7; n-- > 0;) {
                int rgb;
                int l;
                rgb = src[(srcPos & 0x3f80) + (darken >> 7)];
                l = hsl1 >> 16;
                if (!mapped || z1 < depth_buffer[offset] || z1 < depth_buffer[offset] + bufferOffset) {
                    dest[offset] = ((rgb & 0xff00ff) * l & ~0xff00ff) + ((rgb & 0xff00) * l & 0xff0000) >> 8;
                    depth_buffer[offset] = z1;
                }
                z1 += z2;
                offset++;
                darken += j7;
                srcPos += l7;
                hsl1 += dl;
            }
            return;
        }
        while (n-- > 0) {
            int i9;
            int l;
            for (int x = 0; x < 8; x++) {
                if ((i9 = src[(srcPos & 0x3f80) + (darken >> 7)]) != 0) {
                    l = hsl1 >> 16;
                    if (!mapped || z1 < depth_buffer[offset] || z1 < depth_buffer[offset] + bufferOffset) {
                        dest[offset] = ((i9 & 0xff00ff) * l & ~0xff00ff) + ((i9 & 0xff00) * l & 0xff0000) >> 8;
                        depth_buffer[offset] = z1;
                    }
                }
                z1 += z2;
                offset++;
                darken += j7;
                srcPos += l7;
                hsl1 += dl;
            }
            t1 += t4;
            t2 += t5;
            t3 += t6;
            int j6 = t3 >> 14;
            if (j6 != 0) {
                j4 = t1 / j6;
                l4 = t2 / j6;
                if (j4 < 7) {
                    j4 = 7;
                } else if (j4 > 16256) {
                    j4 = 16256;
                }
            }
            j7 = j4 - darken >> 3;
            l7 = l4 - srcPos >> 3;
            hsl1 += dl;
        }
        for (int l3 = x2 - x1 & 7; l3-- > 0;) {
            int j9;
            int l;
            if ((j9 = src[(srcPos & 0x3f80) + (darken >> 7)]) != 0) {
                l = hsl1 >> 16;
                if (!mapped || z1 < depth_buffer[offset] || z1 < depth_buffer[offset] + bufferOffset) {
                    dest[offset] = ((j9 & 0xff00ff) * l & ~0xff00ff) + ((j9 & 0xff00) * l & 0xff0000) >> 8;
                    depth_buffer[offset] = z1;
                }
            }
            z1 += z2;
            offset++;
            darken += j7;
            srcPos += l7;
            hsl1 += dl;
        }
    }

    public static void drawShadedTriangle(int y1, int y2, int y3, int x1, int x2, int x3, int hsl1, int hsl2, int hsl3) {
        int j2 = 0;
        int k2 = 0;
        if (y2 != y1) {
            j2 = (x2 - x1 << 16) / (y2 - y1);
            k2 = (hsl2 - hsl1 << 15) / (y2 - y1);
        }
        int l2 = 0;
        int i3 = 0;
        if (y3 != y2) {
            l2 = (x3 - x2 << 16) / (y3 - y2);
            i3 = (hsl3 - hsl2 << 15) / (y3 - y2);
        }
        int j3 = 0;
        int k3 = 0;
        if (y3 != y1) {
            j3 = (x1 - x3 << 16) / (y1 - y3);
            k3 = (hsl1 - hsl3 << 15) / (y1 - y3);
        }
        if (y1 <= y2 && y1 <= y3) {
            if (y1 >= Rasterizer2D.clip_bottom)
                return;
            if (y2 > Rasterizer2D.clip_bottom)
                y2 = Rasterizer2D.clip_bottom;
            if (y3 > Rasterizer2D.clip_bottom)
                y3 = Rasterizer2D.clip_bottom;
            if (y2 < y3) {
                x3 = x1 <<= 16;
                hsl3 = hsl1 <<= 15;
                if (y1 < 0) {
                    x3 -= j3 * y1;
                    x1 -= j2 * y1;
                    hsl3 -= k3 * y1;
                    hsl1 -= k2 * y1;
                    y1 = 0;
                }
                x2 <<= 16;
                hsl2 <<= 15;
                if (y2 < 0) {
                    x2 -= l2 * y2;
                    hsl2 -= i3 * y2;
                    y2 = 0;
                }
                if (y1 != y2 && j3 < j2 || y1 == y2 && j3 > l2) {
                    y3 -= y2;
                    y2 -= y1;
                    for (y1 = line_offsets[y1]; --y2 >= 0; y1 += Rasterizer2D.width) {
                        drawGouraudScanline(Rasterizer2D.pixels, y1, x3 >> 16, x1 >> 16, hsl3 >> 7, hsl1 >> 7);
                        x3 += j3;
                        x1 += j2;
                        hsl3 += k3;
                        hsl1 += k2;
                    }

                    while (--y3 >= 0) {
                        drawGouraudScanline(Rasterizer2D.pixels, y1, x3 >> 16, x2 >> 16, hsl3 >> 7, hsl2 >> 7);
                        x3 += j3;
                        x2 += l2;
                        hsl3 += k3;
                        hsl2 += i3;
                        y1 += Rasterizer2D.width;
                    }
                    return;
                }
                y3 -= y2;
                y2 -= y1;
                for (y1 = line_offsets[y1]; --y2 >= 0; y1 += Rasterizer2D.width) {
                    drawGouraudScanline(Rasterizer2D.pixels, y1, x1 >> 16, x3 >> 16, hsl1 >> 7, hsl3 >> 7);
                    x3 += j3;
                    x1 += j2;
                    hsl3 += k3;
                    hsl1 += k2;
                }

                while (--y3 >= 0) {
                    drawGouraudScanline(Rasterizer2D.pixels, y1, x2 >> 16, x3 >> 16, hsl2 >> 7, hsl3 >> 7);
                    x3 += j3;
                    x2 += l2;
                    hsl3 += k3;
                    hsl2 += i3;
                    y1 += Rasterizer2D.width;
                }
                return;
            }
            x2 = x1 <<= 16;
            hsl2 = hsl1 <<= 15;
            if (y1 < 0) {
                x2 -= j3 * y1;
                x1 -= j2 * y1;
                hsl2 -= k3 * y1;
                hsl1 -= k2 * y1;
                y1 = 0;
            }
            x3 <<= 16;
            hsl3 <<= 15;
            if (y3 < 0) {
                x3 -= l2 * y3;
                hsl3 -= i3 * y3;
                y3 = 0;
            }
            if (y1 != y3 && j3 < j2 || y1 == y3 && l2 > j2) {
                y2 -= y3;
                y3 -= y1;
                for (y1 = line_offsets[y1]; --y3 >= 0; y1 += Rasterizer2D.width) {
                    drawGouraudScanline(Rasterizer2D.pixels, y1, x2 >> 16, x1 >> 16, hsl2 >> 7, hsl1 >> 7);
                    x2 += j3;
                    x1 += j2;
                    hsl2 += k3;
                    hsl1 += k2;
                }

                while (--y2 >= 0) {
                    drawGouraudScanline(Rasterizer2D.pixels, y1, x3 >> 16, x1 >> 16, hsl3 >> 7, hsl1 >> 7);
                    x3 += l2;
                    x1 += j2;
                    hsl3 += i3;
                    hsl1 += k2;
                    y1 += Rasterizer2D.width;
                }
                return;
            }
            y2 -= y3;
            y3 -= y1;
            for (y1 = line_offsets[y1]; --y3 >= 0; y1 += Rasterizer2D.width) {
                drawGouraudScanline(Rasterizer2D.pixels, y1, x1 >> 16, x2 >> 16, hsl1 >> 7, hsl2 >> 7);
                x2 += j3;
                x1 += j2;
                hsl2 += k3;
                hsl1 += k2;
            }

            while (--y2 >= 0) {
                drawGouraudScanline(Rasterizer2D.pixels, y1, x1 >> 16, x3 >> 16, hsl1 >> 7, hsl3 >> 7);
                x3 += l2;
                x1 += j2;
                hsl3 += i3;
                hsl1 += k2;
                y1 += Rasterizer2D.width;
            }
            return;
        }
        if (y2 <= y3) {
            if (y2 >= Rasterizer2D.clip_bottom)
                return;
            if (y3 > Rasterizer2D.clip_bottom)
                y3 = Rasterizer2D.clip_bottom;
            if (y1 > Rasterizer2D.clip_bottom)
                y1 = Rasterizer2D.clip_bottom;
            if (y3 < y1) {
                x1 = x2 <<= 16;
                hsl1 = hsl2 <<= 15;
                if (y2 < 0) {
                    x1 -= j2 * y2;
                    x2 -= l2 * y2;
                    hsl1 -= k2 * y2;
                    hsl2 -= i3 * y2;
                    y2 = 0;
                }
                x3 <<= 16;
                hsl3 <<= 15;
                if (y3 < 0) {
                    x3 -= j3 * y3;
                    hsl3 -= k3 * y3;
                    y3 = 0;
                }
                if (y2 != y3 && j2 < l2 || y2 == y3 && j2 > j3) {
                    y1 -= y3;
                    y3 -= y2;
                    for (y2 = line_offsets[y2]; --y3 >= 0; y2 += Rasterizer2D.width) {
                        drawGouraudScanline(Rasterizer2D.pixels, y2, x1 >> 16, x2 >> 16, hsl1 >> 7, hsl2 >> 7);
                        x1 += j2;
                        x2 += l2;
                        hsl1 += k2;
                        hsl2 += i3;
                    }

                    while (--y1 >= 0) {
                        drawGouraudScanline(Rasterizer2D.pixels, y2, x1 >> 16, x3 >> 16, hsl1 >> 7, hsl3 >> 7);
                        x1 += j2;
                        x3 += j3;
                        hsl1 += k2;
                        hsl3 += k3;
                        y2 += Rasterizer2D.width;
                    }
                    return;
                }
                y1 -= y3;
                y3 -= y2;
                for (y2 = line_offsets[y2]; --y3 >= 0; y2 += Rasterizer2D.width) {
                    drawGouraudScanline(Rasterizer2D.pixels, y2, x2 >> 16, x1 >> 16, hsl2 >> 7, hsl1 >> 7);
                    x1 += j2;
                    x2 += l2;
                    hsl1 += k2;
                    hsl2 += i3;
                }

                while (--y1 >= 0) {
                    drawGouraudScanline(Rasterizer2D.pixels, y2, x3 >> 16, x1 >> 16, hsl3 >> 7, hsl1 >> 7);
                    x1 += j2;
                    x3 += j3;
                    hsl1 += k2;
                    hsl3 += k3;
                    y2 += Rasterizer2D.width;
                }
                return;
            }
            x3 = x2 <<= 16;
            hsl3 = hsl2 <<= 15;
            if (y2 < 0) {
                x3 -= j2 * y2;
                x2 -= l2 * y2;
                hsl3 -= k2 * y2;
                hsl2 -= i3 * y2;
                y2 = 0;
            }
            x1 <<= 16;
            hsl1 <<= 15;
            if (y1 < 0) {
                x1 -= j3 * y1;
                hsl1 -= k3 * y1;
                y1 = 0;
            }
            if (j2 < l2) {
                y3 -= y1;
                y1 -= y2;
                for (y2 = line_offsets[y2]; --y1 >= 0; y2 += Rasterizer2D.width) {
                    drawGouraudScanline(Rasterizer2D.pixels, y2, x3 >> 16, x2 >> 16, hsl3 >> 7, hsl2 >> 7);
                    x3 += j2;
                    x2 += l2;
                    hsl3 += k2;
                    hsl2 += i3;
                }

                while (--y3 >= 0) {
                    drawGouraudScanline(Rasterizer2D.pixels, y2, x1 >> 16, x2 >> 16, hsl1 >> 7, hsl2 >> 7);
                    x1 += j3;
                    x2 += l2;
                    hsl1 += k3;
                    hsl2 += i3;
                    y2 += Rasterizer2D.width;
                }
                return;
            }
            y3 -= y1;
            y1 -= y2;
            for (y2 = line_offsets[y2]; --y1 >= 0; y2 += Rasterizer2D.width) {
                drawGouraudScanline(Rasterizer2D.pixels, y2, x2 >> 16, x3 >> 16, hsl2 >> 7, hsl3 >> 7);
                x3 += j2;
                x2 += l2;
                hsl3 += k2;
                hsl2 += i3;
            }

            while (--y3 >= 0) {
                drawGouraudScanline(Rasterizer2D.pixels, y2, x2 >> 16, x1 >> 16, hsl2 >> 7, hsl1 >> 7);
                x1 += j3;
                x2 += l2;
                hsl1 += k3;
                hsl2 += i3;
                y2 += Rasterizer2D.width;
            }
            return;
        }
        if (y3 >= Rasterizer2D.clip_bottom)
            return;
        if (y1 > Rasterizer2D.clip_bottom)
            y1 = Rasterizer2D.clip_bottom;
        if (y2 > Rasterizer2D.clip_bottom)
            y2 = Rasterizer2D.clip_bottom;
        if (y1 < y2) {
            x2 = x3 <<= 16;
            hsl2 = hsl3 <<= 15;
            if (y3 < 0) {
                x2 -= l2 * y3;
                x3 -= j3 * y3;
                hsl2 -= i3 * y3;
                hsl3 -= k3 * y3;
                y3 = 0;
            }
            x1 <<= 16;
            hsl1 <<= 15;
            if (y1 < 0) {
                x1 -= j2 * y1;
                hsl1 -= k2 * y1;
                y1 = 0;
            }
            if (l2 < j3) {
                y2 -= y1;
                y1 -= y3;
                for (y3 = line_offsets[y3]; --y1 >= 0; y3 += Rasterizer2D.width) {
                    drawGouraudScanline(Rasterizer2D.pixels, y3, x2 >> 16, x3 >> 16, hsl2 >> 7, hsl3 >> 7);
                    x2 += l2;
                    x3 += j3;
                    hsl2 += i3;
                    hsl3 += k3;
                }

                while (--y2 >= 0) {
                    drawGouraudScanline(Rasterizer2D.pixels, y3, x2 >> 16, x1 >> 16, hsl2 >> 7, hsl1 >> 7);
                    x2 += l2;
                    x1 += j2;
                    hsl2 += i3;
                    hsl1 += k2;
                    y3 += Rasterizer2D.width;
                }
                return;
            }
            y2 -= y1;
            y1 -= y3;
            for (y3 = line_offsets[y3]; --y1 >= 0; y3 += Rasterizer2D.width) {
                drawGouraudScanline(Rasterizer2D.pixels, y3, x3 >> 16, x2 >> 16, hsl3 >> 7, hsl2 >> 7);
                x2 += l2;
                x3 += j3;
                hsl2 += i3;
                hsl3 += k3;
            }

            while (--y2 >= 0) {
                drawGouraudScanline(Rasterizer2D.pixels, y3, x1 >> 16, x2 >> 16, hsl1 >> 7, hsl2 >> 7);
                x2 += l2;
                x1 += j2;
                hsl2 += i3;
                hsl1 += k2;
                y3 += Rasterizer2D.width;
            }
            return;
        }
        x1 = x3 <<= 16;
        hsl1 = hsl3 <<= 15;
        if (y3 < 0) {
            x1 -= l2 * y3;
            x3 -= j3 * y3;
            hsl1 -= i3 * y3;
            hsl3 -= k3 * y3;
            y3 = 0;
        }
        x2 <<= 16;
        hsl2 <<= 15;
        if (y2 < 0) {
            x2 -= j2 * y2;
            hsl2 -= k2 * y2;
            y2 = 0;
        }
        if (l2 < j3) {
            y1 -= y2;
            y2 -= y3;
            for (y3 = line_offsets[y3]; --y2 >= 0; y3 += Rasterizer2D.width) {
                drawGouraudScanline(Rasterizer2D.pixels, y3, x1 >> 16, x3 >> 16, hsl1 >> 7, hsl3 >> 7);
                x1 += l2;
                x3 += j3;
                hsl1 += i3;
                hsl3 += k3;
            }

            while (--y1 >= 0) {
                drawGouraudScanline(Rasterizer2D.pixels, y3, x2 >> 16, x3 >> 16, hsl2 >> 7, hsl3 >> 7);
                x2 += j2;
                x3 += j3;
                hsl2 += k2;
                hsl3 += k3;
                y3 += Rasterizer2D.width;
            }
            return;
        }
        y1 -= y2;
        y2 -= y3;
        for (y3 = line_offsets[y3]; --y2 >= 0; y3 += Rasterizer2D.width) {
            drawGouraudScanline(Rasterizer2D.pixels, y3, x3 >> 16, x1 >> 16, hsl3 >> 7, hsl1 >> 7);
            x1 += l2;
            x3 += j3;
            hsl1 += i3;
            hsl3 += k3;
        }

        while (--y1 >= 0) {
            drawGouraudScanline(Rasterizer2D.pixels, y3, x3 >> 16, x2 >> 16, hsl3 >> 7, hsl2 >> 7);
            x2 += j2;
            x3 += j3;
            hsl2 += k2;
            hsl3 += k3;
            y3 += Rasterizer2D.width;
        }
    }

    private static void drawGouraudScanline(int dest[], int offset, int x1, int x2, int hsl1, int hsl2) {
        int j;
        int k;
        if (aBoolean1464) {
            int l1;
            if (testX) {
                if (x2 - x1 > 3)
                    l1 = (hsl2 - hsl1) / (x2 - x1);
                else
                    l1 = 0;
                if (x2 > Rasterizer2D.center_x)
                    x2 = Rasterizer2D.center_x;
                if (x1 < 0) {
                    hsl1 -= x1 * l1;
                    x1 = 0;
                }
                if (x1 >= x2)
                    return;
                offset += x1;
                k = x2 - x1 >> 2;
                l1 <<= 2;
            } else {
                if (x1 >= x2)
                    return;
                offset += x1;
                k = x2 - x1 >> 2;
                if (k > 0)
                    l1 = (hsl2 - hsl1) * anIntArray1468[k] >> 15;
                else
                    l1 = 0;
            }
            if (alpha == 0) {
                while (--k >= 0) {
                    j = HSL_TO_RGB[hsl1 >> 8];
                    hsl1 += l1;
                    dest[offset] = j;
                    offset++;
                    dest[offset] = j;
                    offset++;
                    dest[offset] = j;
                    offset++;
                    dest[offset] = j;
                    offset++;
                }
                k = x2 - x1 & 3;
                if (k > 0) {
                    j = HSL_TO_RGB[hsl1 >> 8];
                    do {
                        dest[offset] = j;
                        offset++;
                    }
                    while (--k > 0);
                    return;
                }
            } else {
                int a1 = alpha;
                int a2 = 256 - alpha;
                while (--k >= 0) {
                    j = HSL_TO_RGB[hsl1 >> 8];
                    hsl1 += l1;
                    j = ((j & 0xff00ff) * a2 >> 8 & 0xff00ff) + ((j & 0xff00) * a2 >> 8 & 0xff00);
                    dest[offset] = j + ((dest[offset] & 0xff00ff) * a1 >> 8 & 0xff00ff) + ((dest[offset] & 0xff00) * a1 >> 8 & 0xff00);
                    offset++;
                    dest[offset] = j + ((dest[offset] & 0xff00ff) * a1 >> 8 & 0xff00ff) + ((dest[offset] & 0xff00) * a1 >> 8 & 0xff00);
                    offset++;
                    dest[offset] = j + ((dest[offset] & 0xff00ff) * a1 >> 8 & 0xff00ff) + ((dest[offset] & 0xff00) * a1 >> 8 & 0xff00);
                    offset++;
                    dest[offset] = j + ((dest[offset] & 0xff00ff) * a1 >> 8 & 0xff00ff) + ((dest[offset] & 0xff00) * a1 >> 8 & 0xff00);
                    offset++;
                }
                k = x2 - x1 & 3;
                if (k > 0) {
                    j = HSL_TO_RGB[hsl1 >> 8];
                    j = ((j & 0xff00ff) * a2 >> 8 & 0xff00ff) + ((j & 0xff00) * a2 >> 8 & 0xff00);
                    do {
                        dest[offset] = j + ((dest[offset] & 0xff00ff) * a1 >> 8 & 0xff00ff) + ((dest[offset] & 0xff00) * a1 >> 8 & 0xff00);
                        offset++;
                    }
                    while (--k > 0);
                }
            }
            return;
        }
        if (x1 >= x2)
            return;
        int i2 = (hsl2 - hsl1) / (x2 - x1);
        if (testX) {
            if (x2 > Rasterizer2D.center_x)
                x2 = Rasterizer2D.center_x;
            if (x1 < 0) {
                hsl1 -= x1 * i2;
                x1 = 0;
            }
            if (x1 >= x2)
                return;
        }
        offset += x1;
        k = x2 - x1;
        if (alpha == 0) {
            do {
                dest[offset] = HSL_TO_RGB[hsl1 >> 8];
                offset++;
                hsl1 += i2;
            } while (--k > 0);
            return;
        }
        int a1 = alpha;
        int a2 = 256 - alpha;
        do {
            j = HSL_TO_RGB[hsl1 >> 8];
            hsl1 += i2;
            j = ((j & 0xff00ff) * a2 >> 8 & 0xff00ff) + ((j & 0xff00) * a2 >> 8 & 0xff00);
            dest[offset] = j + ((dest[offset] & 0xff00ff) * a1 >> 8 & 0xff00ff) + ((dest[offset] & 0xff00) * a1 >> 8 & 0xff00);
            offset++;
        } while (--k > 0);
    }

    public static void drawTexturedTriangle(int y1, int y2, int y3, int x1, int x2, int x3, int c1, int c2, int c3, int tx1, int tx2, int tx3, int ty1, int ty2, int ty3, int tz1, int tz2, int tz3, int tex) {
        c1 = 0x7f - c1 << 1;
        c2 = 0x7f - c2 << 1;
        c3 = 0x7f - c3 << 1;
        int ai[] = get_texels(tex)[scale];
        opaque = !transparent[tex];
        tx2 = tx1 - tx2;
        ty2 = ty1 - ty2;
        tz2 = tz1 - tz2;
        tx3 -= tx1;
        ty3 -= ty1;
        tz3 -= tz1;
        int l4 = tx3 * ty1 - ty3 * tx1 << (SceneGraph.view_dist == 9 ? 14 : 15);
        int i5 = ty3 * tz1 - tz3 * ty1 << 8;
        int j5 = tz3 * tx1 - tx3 * tz1 << 5;
        int k5 = tx2 * ty1 - ty2 * tx1 << (SceneGraph.view_dist == 9 ? 14 : 15);
        int l5 = ty2 * tz1 - tz2 * ty1 << 8;
        int i6 = tz2 * tx1 - tx2 * tz1 << 5;
        int j6 = ty2 * tx3 - tx2 * ty3 << (SceneGraph.view_dist == 9 ? 14 : 15);
        int k6 = tz2 * ty3 - ty2 * tz3 << 8;
        int l6 = tx2 * tz3 - tz2 * tx3 << 5;
        int i7 = 0;
        int j7 = 0;
        if (y2 != y1) {
            i7 = (x2 - x1 << 16) / (y2 - y1);
            j7 = (c2 - c1 << 16) / (y2 - y1);
        }
        int k7 = 0;
        int l7 = 0;
        if (y3 != y2) {
            k7 = (x3 - x2 << 16) / (y3 - y2);
            l7 = (c3 - c2 << 16) / (y3 - y2);
        }
        int i8 = 0;
        int j8 = 0;
        if (y3 != y1) {
            i8 = (x1 - x3 << 16) / (y1 - y3);
            j8 = (c1 - c3 << 16) / (y1 - y3);
        }
        if (y1 <= y2 && y1 <= y3) {
            if (y1 >= Rasterizer2D.clip_bottom)
                return;
            if (y2 > Rasterizer2D.clip_bottom)
                y2 = Rasterizer2D.clip_bottom;
            if (y3 > Rasterizer2D.clip_bottom)
                y3 = Rasterizer2D.clip_bottom;
            if (y2 < y3) {
                x3 = x1 <<= 16;
                c3 = c1 <<= 16;
                if (y1 < 0) {
                    x3 -= i8 * y1;
                    x1 -= i7 * y1;
                    c3 -= j8 * y1;
                    c1 -= j7 * y1;
                    y1 = 0;
                }
                x2 <<= 16;
                c2 <<= 16;
                if (y2 < 0) {
                    x2 -= k7 * y2;
                    c2 -= l7 * y2;
                    y2 = 0;
                }
                int k8 = y1 - center_y;
                l4 += j5 * k8;
                k5 += i6 * k8;
                j6 += l6 * k8;
                if (y1 != y2 && i8 < i7 || y1 == y2 && i8 > k7) {
                    y3 -= y2;
                    y2 -= y1;
                    y1 = line_offsets[y1];
                    while (--y2 >= 0) {
                        drawTexturedScanline(Rasterizer2D.pixels, ai, y1, x3 >> 16, x1 >> 16, c3, c1, l4, k5, j6, i5, l5, k6);
                        x3 += i8;
                        x1 += i7;
                        c3 += j8;
                        c1 += j7;
                        y1 += Rasterizer2D.width;
                        l4 += j5;
                        k5 += i6;
                        j6 += l6;
                    }
                    while (--y3 >= 0) {
                        drawTexturedScanline(Rasterizer2D.pixels, ai, y1, x3 >> 16, x2 >> 16, c3, c2, l4, k5, j6, i5, l5, k6);
                        x3 += i8;
                        x2 += k7;
                        c3 += j8;
                        c2 += l7;
                        y1 += Rasterizer2D.width;
                        l4 += j5;
                        k5 += i6;
                        j6 += l6;
                    }
                    return;
                }
                y3 -= y2;
                y2 -= y1;
                y1 = line_offsets[y1];
                while (--y2 >= 0) {
                    drawTexturedScanline(Rasterizer2D.pixels, ai, y1, x1 >> 16, x3 >> 16, c1, c3, l4, k5, j6, i5, l5, k6);
                    x3 += i8;
                    x1 += i7;
                    c3 += j8;
                    c1 += j7;
                    y1 += Rasterizer2D.width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                while (--y3 >= 0) {
                    drawTexturedScanline(Rasterizer2D.pixels, ai, y1, x2 >> 16, x3 >> 16, c2, c3, l4, k5, j6, i5, l5, k6);
                    x3 += i8;
                    x2 += k7;
                    c3 += j8;
                    c2 += l7;
                    y1 += Rasterizer2D.width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                return;
            }
            x2 = x1 <<= 16;
            c2 = c1 <<= 16;
            if (y1 < 0) {
                x2 -= i8 * y1;
                x1 -= i7 * y1;
                c2 -= j8 * y1;
                c1 -= j7 * y1;
                y1 = 0;
            }
            x3 <<= 16;
            c3 <<= 16;
            if (y3 < 0) {
                x3 -= k7 * y3;
                c3 -= l7 * y3;
                y3 = 0;
            }
            int l8 = y1 - center_y;
            l4 += j5 * l8;
            k5 += i6 * l8;
            j6 += l6 * l8;
            if (y1 != y3 && i8 < i7 || y1 == y3 && k7 > i7) {
                y2 -= y3;
                y3 -= y1;
                y1 = line_offsets[y1];
                while (--y3 >= 0) {
                    drawTexturedScanline(Rasterizer2D.pixels, ai, y1, x2 >> 16, x1 >> 16, c2, c1, l4, k5, j6, i5, l5, k6);
                    x2 += i8;
                    x1 += i7;
                    c2 += j8;
                    c1 += j7;
                    y1 += Rasterizer2D.width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                while (--y2 >= 0) {
                    drawTexturedScanline(Rasterizer2D.pixels, ai, y1, x3 >> 16, x1 >> 16, c3, c1, l4, k5, j6, i5, l5, k6);
                    x3 += k7;
                    x1 += i7;
                    c3 += l7;
                    c1 += j7;
                    y1 += Rasterizer2D.width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                return;
            }
            y2 -= y3;
            y3 -= y1;
            y1 = line_offsets[y1];
            while (--y3 >= 0) {
                drawTexturedScanline(Rasterizer2D.pixels, ai, y1, x1 >> 16, x2 >> 16, c1, c2, l4, k5, j6, i5, l5, k6);
                x2 += i8;
                x1 += i7;
                c2 += j8;
                c1 += j7;
                y1 += Rasterizer2D.width;
                l4 += j5;
                k5 += i6;
                j6 += l6;
            }
            while (--y2 >= 0) {
                drawTexturedScanline(Rasterizer2D.pixels, ai, y1, x1 >> 16, x3 >> 16, c1, c3, l4, k5, j6, i5, l5, k6);
                x3 += k7;
                x1 += i7;
                c3 += l7;
                c1 += j7;
                y1 += Rasterizer2D.width;
                l4 += j5;
                k5 += i6;
                j6 += l6;
            }
            return;
        }
        if (y2 <= y3) {
            if (y2 >= Rasterizer2D.clip_bottom)
                return;
            if (y3 > Rasterizer2D.clip_bottom)
                y3 = Rasterizer2D.clip_bottom;
            if (y1 > Rasterizer2D.clip_bottom)
                y1 = Rasterizer2D.clip_bottom;
            if (y3 < y1) {
                x1 = x2 <<= 16;
                c1 = c2 <<= 16;
                if (y2 < 0) {
                    x1 -= i7 * y2;
                    x2 -= k7 * y2;
                    c1 -= j7 * y2;
                    c2 -= l7 * y2;
                    y2 = 0;
                }
                x3 <<= 16;
                c3 <<= 16;
                if (y3 < 0) {
                    x3 -= i8 * y3;
                    c3 -= j8 * y3;
                    y3 = 0;
                }
                int i9 = y2 - center_y;
                l4 += j5 * i9;
                k5 += i6 * i9;
                j6 += l6 * i9;
                if (y2 != y3 && i7 < k7 || y2 == y3 && i7 > i8) {
                    y1 -= y3;
                    y3 -= y2;
                    y2 = line_offsets[y2];
                    while (--y3 >= 0) {
                        drawTexturedScanline(Rasterizer2D.pixels, ai, y2, x1 >> 16, x2 >> 16, c1, c2, l4, k5, j6, i5, l5, k6);
                        x1 += i7;
                        x2 += k7;
                        c1 += j7;
                        c2 += l7;
                        y2 += Rasterizer2D.width;
                        l4 += j5;
                        k5 += i6;
                        j6 += l6;
                    }
                    while (--y1 >= 0) {
                        drawTexturedScanline(Rasterizer2D.pixels, ai, y2, x1 >> 16, x3 >> 16, c1, c3, l4, k5, j6, i5, l5, k6);
                        x1 += i7;
                        x3 += i8;
                        c1 += j7;
                        c3 += j8;
                        y2 += Rasterizer2D.width;
                        l4 += j5;
                        k5 += i6;
                        j6 += l6;
                    }
                    return;
                }
                y1 -= y3;
                y3 -= y2;
                y2 = line_offsets[y2];
                while (--y3 >= 0) {
                    drawTexturedScanline(Rasterizer2D.pixels, ai, y2, x2 >> 16, x1 >> 16, c2, c1, l4, k5, j6, i5, l5, k6);
                    x1 += i7;
                    x2 += k7;
                    c1 += j7;
                    c2 += l7;
                    y2 += Rasterizer2D.width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                while (--y1 >= 0) {
                    drawTexturedScanline(Rasterizer2D.pixels, ai, y2, x3 >> 16, x1 >> 16, c3, c1, l4, k5, j6, i5, l5, k6);
                    x1 += i7;
                    x3 += i8;
                    c1 += j7;
                    c3 += j8;
                    y2 += Rasterizer2D.width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                return;
            }
            x3 = x2 <<= 16;
            c3 = c2 <<= 16;
            if (y2 < 0) {
                x3 -= i7 * y2;
                x2 -= k7 * y2;
                c3 -= j7 * y2;
                c2 -= l7 * y2;
                y2 = 0;
            }
            x1 <<= 16;
            c1 <<= 16;
            if (y1 < 0) {
                x1 -= i8 * y1;
                c1 -= j8 * y1;
                y1 = 0;
            }
            int j9 = y2 - center_y;
            l4 += j5 * j9;
            k5 += i6 * j9;
            j6 += l6 * j9;
            if (i7 < k7) {
                y3 -= y1;
                y1 -= y2;
                y2 = line_offsets[y2];
                while (--y1 >= 0) {
                    drawTexturedScanline(Rasterizer2D.pixels, ai, y2, x3 >> 16, x2 >> 16, c3, c2, l4, k5, j6, i5, l5, k6);
                    x3 += i7;
                    x2 += k7;
                    c3 += j7;
                    c2 += l7;
                    y2 += Rasterizer2D.width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                while (--y3 >= 0) {
                    drawTexturedScanline(Rasterizer2D.pixels, ai, y2, x1 >> 16, x2 >> 16, c1, c2, l4, k5, j6, i5, l5, k6);
                    x1 += i8;
                    x2 += k7;
                    c1 += j8;
                    c2 += l7;
                    y2 += Rasterizer2D.width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                return;
            }
            y3 -= y1;
            y1 -= y2;
            y2 = line_offsets[y2];
            while (--y1 >= 0) {
                drawTexturedScanline(Rasterizer2D.pixels, ai, y2, x2 >> 16, x3 >> 16, c2, c3, l4, k5, j6, i5, l5, k6);
                x3 += i7;
                x2 += k7;
                c3 += j7;
                c2 += l7;
                y2 += Rasterizer2D.width;
                l4 += j5;
                k5 += i6;
                j6 += l6;
            }
            while (--y3 >= 0) {
                drawTexturedScanline(Rasterizer2D.pixels, ai, y2, x2 >> 16, x1 >> 16, c2, c1, l4, k5, j6, i5, l5, k6);
                x1 += i8;
                x2 += k7;
                c1 += j8;
                c2 += l7;
                y2 += Rasterizer2D.width;
                l4 += j5;
                k5 += i6;
                j6 += l6;
            }
            return;
        }
        if (y3 >= Rasterizer2D.clip_bottom)
            return;
        if (y1 > Rasterizer2D.clip_bottom)
            y1 = Rasterizer2D.clip_bottom;
        if (y2 > Rasterizer2D.clip_bottom)
            y2 = Rasterizer2D.clip_bottom;
        if (y1 < y2) {
            x2 = x3 <<= 16;
            c2 = c3 <<= 16;
            if (y3 < 0) {
                x2 -= k7 * y3;
                x3 -= i8 * y3;
                c2 -= l7 * y3;
                c3 -= j8 * y3;
                y3 = 0;
            }
            x1 <<= 16;
            c1 <<= 16;
            if (y1 < 0) {
                x1 -= i7 * y1;
                c1 -= j7 * y1;
                y1 = 0;
            }
            int k9 = y3 - center_y;
            l4 += j5 * k9;
            k5 += i6 * k9;
            j6 += l6 * k9;
            if (k7 < i8) {
                y2 -= y1;
                y1 -= y3;
                y3 = line_offsets[y3];
                while (--y1 >= 0) {
                    drawTexturedScanline(Rasterizer2D.pixels, ai, y3, x2 >> 16, x3 >> 16, c2, c3, l4, k5, j6, i5, l5, k6);
                    x2 += k7;
                    x3 += i8;
                    c2 += l7;
                    c3 += j8;
                    y3 += Rasterizer2D.width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                while (--y2 >= 0) {
                    drawTexturedScanline(Rasterizer2D.pixels, ai, y3, x2 >> 16, x1 >> 16, c2, c1, l4, k5, j6, i5, l5, k6);
                    x2 += k7;
                    x1 += i7;
                    c2 += l7;
                    c1 += j7;
                    y3 += Rasterizer2D.width;
                    l4 += j5;
                    k5 += i6;
                    j6 += l6;
                }
                return;
            }
            y2 -= y1;
            y1 -= y3;
            y3 = line_offsets[y3];
            while (--y1 >= 0) {
                drawTexturedScanline(Rasterizer2D.pixels, ai, y3, x3 >> 16, x2 >> 16, c3, c2, l4, k5, j6, i5, l5, k6);
                x2 += k7;
                x3 += i8;
                c2 += l7;
                c3 += j8;
                y3 += Rasterizer2D.width;
                l4 += j5;
                k5 += i6;
                j6 += l6;
            }
            while (--y2 >= 0) {
                drawTexturedScanline(Rasterizer2D.pixels, ai, y3, x1 >> 16, x2 >> 16, c1, c2, l4, k5, j6, i5, l5, k6);
                x2 += k7;
                x1 += i7;
                c2 += l7;
                c1 += j7;
                y3 += Rasterizer2D.width;
                l4 += j5;
                k5 += i6;
                j6 += l6;
            }
            return;
        }
        x1 = x3 <<= 16;
        c1 = c3 <<= 16;
        if (y3 < 0) {
            x1 -= k7 * y3;
            x3 -= i8 * y3;
            c1 -= l7 * y3;
            c3 -= j8 * y3;
            y3 = 0;
        }
        x2 <<= 16;
        c2 <<= 16;
        if (y2 < 0) {
            x2 -= i7 * y2;
            c2 -= j7 * y2;
            y2 = 0;
        }
        int l9 = y3 - center_y;
        l4 += j5 * l9;
        k5 += i6 * l9;
        j6 += l6 * l9;
        if (k7 < i8) {
            y1 -= y2;
            y2 -= y3;
            y3 = line_offsets[y3];
            while (--y2 >= 0) {
                drawTexturedScanline(Rasterizer2D.pixels, ai, y3, x1 >> 16, x3 >> 16, c1, c3, l4, k5, j6, i5, l5, k6);
                x1 += k7;
                x3 += i8;
                c1 += l7;
                c3 += j8;
                y3 += Rasterizer2D.width;
                l4 += j5;
                k5 += i6;
                j6 += l6;
            }
            while (--y1 >= 0) {
                drawTexturedScanline(Rasterizer2D.pixels, ai, y3, x2 >> 16, x3 >> 16, c2, c3, l4, k5, j6, i5, l5, k6);
                x2 += i7;
                x3 += i8;
                c2 += j7;
                c3 += j8;
                y3 += Rasterizer2D.width;
                l4 += j5;
                k5 += i6;
                j6 += l6;
            }
            return;
        }
        y1 -= y2;
        y2 -= y3;
        y3 = line_offsets[y3];
        while (--y2 >= 0) {
            drawTexturedScanline(Rasterizer2D.pixels, ai, y3, x3 >> 16, x1 >> 16, c3, c1, l4, k5, j6, i5, l5, k6);
            x1 += k7;
            x3 += i8;
            c1 += l7;
            c3 += j8;
            y3 += Rasterizer2D.width;
            l4 += j5;
            k5 += i6;
            j6 += l6;
        }
        while (--y1 >= 0) {
            drawTexturedScanline(Rasterizer2D.pixels, ai, y3, x3 >> 16, x2 >> 16, c3, c2, l4, k5, j6, i5, l5, k6);
            x2 += i7;
            x3 += i8;
            c2 += j7;
            c3 += j8;
            y3 += Rasterizer2D.width;
            l4 += j5;
            k5 += i6;
            j6 += l6;
        }
    }

    public static boolean repeatTexture = false;
    public static boolean forceRepeat = false;

    private static void drawTexturedScanline(int[] raster, int[] texels, int k, int x1, int x2, int l1, int l2, int a1, int i2, int verticalC, int k2, int a2, int i3) {
        int uA = 0;
        int vA = 0;
        if (x1 >= x2)
            return;
        int dl = (l2 - l1) / (x2 - x1);
        int n;
        if (testX) {
            if (x2 > Rasterizer2D.center_x)
                x2 = Rasterizer2D.center_x;
            if (x1 < 0) {
                l1 -= x1 * dl;
                x1 = 0;
            }
        }
        if (x1 >= x2)
            return;
        n = x2 - x1 >> 3;
        k += x1;
        if (low_detail) {
            int i4 = 0;
            int k4 = 0;
            int k6 = x1 - center_x;
            a1 += (k2 >> 3) * k6;
            i2 += (a2 >> 3) * k6;
            verticalC += (i3 >> 3) * k6;
            int c = verticalC >> 12;
            if (c != 0) {
                uA = a1 / c;
                vA = i2 / c;
                if(!repeatTexture) {
                    if (uA < 0)
                        uA = 0;
                    else if (uA > 4032)
                        uA = 4032;
                }
            }
            a1 += k2;
            i2 += a2;
            verticalC += i3;
            c = verticalC >> 12;
            if (c != 0) {
                i4 = a1 / c;
                k4 = i2 / c;
                if(!repeatTexture) {
                    if (i4 < 7)
                        i4 = 7;
                    else if (i4 > 4032)
                        i4 = 4032;
                }
            }
            int i7 = i4 - uA >> 3;
            int k7 = k4 - vA >> 3;
            if (opaque) {
                int rgb;
                int l;
                while (n-- > 0) {
                    rgb = texels[(vA & 0xfc0) + (uA >> 6) & (repeatTexture ? 4095 : (vA & 0xfc0) + (uA >> 6))];
                    l = l1 >> 16;
                    raster[k++] = ((rgb & 0xff00ff) * l & ~0xff00ff) + ((rgb & 0xff00) * l & 0xff0000) >> 8;
                    uA += i7;
                    vA += k7;
                    l1 += dl;
                    rgb = texels[(vA & 0xfc0) + (uA >> 6) & (repeatTexture ? 4095 : (vA & 0xfc0) + (uA >> 6))];
                    l = l1 >> 16;
                    raster[k++] = ((rgb & 0xff00ff) * l & ~0xff00ff) + ((rgb & 0xff00) * l & 0xff0000) >> 8;
                    uA += i7;
                    vA += k7;
                    l1 += dl;
                    rgb = texels[(vA & 0xfc0) + (uA >> 6) & (repeatTexture ? 4095 : (vA & 0xfc0) + (uA >> 6))];
                    l = l1 >> 16;
                    raster[k++] = ((rgb & 0xff00ff) * l & ~0xff00ff) + ((rgb & 0xff00) * l & 0xff0000) >> 8;
                    uA += i7;
                    vA += k7;
                    l1 += dl;
                    rgb = texels[(vA & 0xfc0) + (uA >> 6) & (repeatTexture ? 4095 : (vA & 0xfc0) + (uA >> 6))];
                    l = l1 >> 16;
                    raster[k++] = ((rgb & 0xff00ff) * l & ~0xff00ff) + ((rgb & 0xff00) * l & 0xff0000) >> 8;
                    uA += i7;
                    vA += k7;
                    l1 += dl;
                    rgb = texels[(vA & 0xfc0) + (uA >> 6) & (repeatTexture ? 4095 : (vA & 0xfc0) + (uA >> 6))];
                    l = l1 >> 16;
                    raster[k++] = ((rgb & 0xff00ff) * l & ~0xff00ff) + ((rgb & 0xff00) * l & 0xff0000) >> 8;
                    uA += i7;
                    vA += k7;
                    l1 += dl;
                    rgb = texels[(vA & 0xfc0) + (uA >> 6) & (repeatTexture ? 4095 : (vA & 0xfc0) + (uA >> 6))];
                    l = l1 >> 16;
                    raster[k++] = ((rgb & 0xff00ff) * l & ~0xff00ff) + ((rgb & 0xff00) * l & 0xff0000) >> 8;
                    uA += i7;
                    vA += k7;
                    l1 += dl;
                    rgb = texels[(vA & 0xfc0) + (uA >> 6) & (repeatTexture ? 4095 : (vA & 0xfc0) + (uA >> 6))];
                    l = l1 >> 16;
                    raster[k++] = ((rgb & 0xff00ff) * l & ~0xff00ff) + ((rgb & 0xff00) * l & 0xff0000) >> 8;
                    uA += i7;
                    vA += k7;
                    l1 += dl;
                    rgb = texels[(vA & 0xfc0) + (uA >> 6) & (repeatTexture ? 4095 : (vA & 0xfc0) + (uA >> 6))];
                    l = l1 >> 16;
                    raster[k++] = ((rgb & 0xff00ff) * l & ~0xff00ff) + ((rgb & 0xff00) * l & 0xff0000) >> 8;
                    uA += i7;
                    vA += k7;
                    l1 += dl;
                    a1 += k2;
                    i2 += a2;
                    verticalC += i3;
                    int j5 = verticalC >> 12;
                    if (j5 != 0) {
                        i4 = a1 / j5;
                        k4 = i2 / j5;
                        if(!repeatTexture) {
                            if (i4 < 7)
                                i4 = 7;
                            else if (i4 > 4032)
                                i4 = 4032;
                        }
                    }
                    i7 = i4 - uA >> 3;
                    k7 = k4 - vA >> 3;
                    l1 += dl;
                }
                for (n = x2 - x1 & 7; n-- > 0; ) {
                    rgb = texels[(vA & 0xfc0) + (uA >> 6) & (repeatTexture ? 4095 : (vA & 0xfc0) + (uA >> 6))];
                    l = l1 >> 16;
                    raster[k++] = ((rgb & 0xff00ff) * l & ~0xff00ff) + ((rgb & 0xff00) * l & 0xff0000) >> 8;
                    uA += i7;
                    vA += k7;
                    l1 += dl;
                }
                return;
            }
            while (n-- > 0) {
                int k8;
                int l;
                if ((k8 = texels[(vA & 0xfc0) + (uA >> 6) & (repeatTexture ? 4095 : (vA & 0xfc0) + (uA >> 6))]) != 0) {
                    l = l1 >> 16;
                    raster[k] = ((k8 & 0xff00ff) * l & ~0xff00ff) + ((k8 & 0xff00) * l & 0xff0000) >> 8;
                }
                k++;
                uA += i7;
                vA += k7;
                l1 += dl;
                if ((k8 = texels[(vA & 0xfc0) + (uA >> 6) & (repeatTexture ? 4095 : (vA & 0xfc0) + (uA >> 6))]) != 0) {
                    l = l1 >> 16;
                    raster[k] = ((k8 & 0xff00ff) * l & ~0xff00ff) + ((k8 & 0xff00) * l & 0xff0000) >> 8;
                }
                k++;
                uA += i7;
                vA += k7;
                l1 += dl;
                if ((k8 = texels[(vA & 0xfc0) + (uA >> 6) & (repeatTexture ? 4095 : (vA & 0xfc0) + (uA >> 6))]) != 0) {
                    l = l1 >> 16;
                    raster[k] = ((k8 & 0xff00ff) * l & ~0xff00ff) + ((k8 & 0xff00) * l & 0xff0000) >> 8;
                }
                k++;
                uA += i7;
                vA += k7;
                l1 += dl;
                if ((k8 = texels[(vA & 0xfc0) + (uA >> 6) & (repeatTexture ? 4095 : (vA & 0xfc0) + (uA >> 6))]) != 0) {
                    l = l1 >> 16;
                    raster[k] = ((k8 & 0xff00ff) * l & ~0xff00ff) + ((k8 & 0xff00) * l & 0xff0000) >> 8;
                }
                k++;
                uA += i7;
                vA += k7;
                l1 += dl;
                if ((k8 = texels[(vA & 0xfc0) + (uA >> 6) & (repeatTexture ? 4095 : (vA & 0xfc0) + (uA >> 6))]) != 0) {
                    l = l1 >> 16;
                    raster[k] = ((k8 & 0xff00ff) * l & ~0xff00ff) + ((k8 & 0xff00) * l & 0xff0000) >> 8;
                }
                k++;
                uA += i7;
                vA += k7;
                l1 += dl;
                if ((k8 = texels[(vA & 0xfc0) + (uA >> 6) & (repeatTexture ? 4095 : (vA & 0xfc0) + (uA >> 6))]) != 0) {
                    l = l1 >> 16;
                    raster[k] = ((k8 & 0xff00ff) * l & ~0xff00ff) + ((k8 & 0xff00) * l & 0xff0000) >> 8;
                }
                k++;
                uA += i7;
                vA += k7;
                l1 += dl;
                if ((k8 = texels[(vA & 0xfc0) + (uA >> 6) & (repeatTexture ? 4095 : (vA & 0xfc0) + (uA >> 6))]) != 0) {
                    l = l1 >> 16;
                    raster[k] = ((k8 & 0xff00ff) * l & ~0xff00ff) + ((k8 & 0xff00) * l & 0xff0000) >> 8;
                }
                k++;
                uA += i7;
                vA += k7;
                l1 += dl;
                if ((k8 = texels[(vA & 0xfc0) + (uA >> 6) & (repeatTexture ? 4095 : (vA & 0xfc0) + (uA >> 6))]) != 0) {
                    l = l1 >> 16;
                    raster[k] = ((k8 & 0xff00ff) * l & ~0xff00ff) + ((k8 & 0xff00) * l & 0xff0000) >> 8;
                }
                k++;
                uA += i7;
                vA += k7;
                l1 += dl;
                a1 += k2;
                i2 += a2;
                verticalC += i3;
                int k5 = verticalC >> 12;
                if (k5 != 0) {
                    i4 = a1 / k5;
                    k4 = i2 / k5;
                    if (i4 < 7)
                        i4 = 7;
                    else if (i4 > 4032)
                        i4 = 4032;
                }
                i7 = i4 - uA >> 3;
                k7 = k4 - vA >> 3;
                l1 += dl;
            }
            for (n = x2 - x1 & 7; n-- > 0; ) {
                int l8;
                int l;
                if ((l8 = texels[(vA & 0xfc0) + (uA >> 6) & (repeatTexture ? 4095 : (vA & 0xfc0) + (uA >> 6))]) != 0) {
                    l = l1 >> 16;
                    raster[k] = ((l8 & 0xff00ff) * l & ~0xff00ff) + ((l8 & 0xff00) * l & 0xff0000) >> 8;
                }
                k++;
                uA += i7;
                vA += k7;
                l1 += dl;
            }

            return;
        }
        int j4 = 0;
        int l4 = 0;
        int l6 = x1 - center_x;
        a1 += (k2 >> 3) * l6;
        i2 += (a2 >> 3) * l6;
        verticalC += (i3 >> 3) * l6;
        int l5 = verticalC >> 14;
        if (l5 != 0) {
            uA = a1 / l5;
            vA = i2 / l5;
            if(!repeatTexture) {
                if (uA < 0)
                    uA = 0;
                else if (uA > 16256)
                    uA = 16256;
            }
        }
        a1 += k2;
        i2 += a2;
        verticalC += i3;
        l5 = verticalC >> 14;
        if (l5 != 0) {
            j4 = a1 / l5;
            l4 = i2 / l5;
            if(!repeatTexture) {
                if (j4 < 7)
                    j4 = 7;
                else if (j4 > 16256)
                    j4 = 16256;
            }
        }
        int j7 = j4 - uA >> 3;
        int l7 = l4 - vA >> 3;
        if (opaque) {
            while (n-- > 0) {
                int rgb;
                int l;
                rgb = texels[(vA & 0x3f80) + (uA >> 7) & (repeatTexture ? 16383 : (vA & 0x3f80) + (uA >> 7))];
                l = l1 >> 16;
                raster[k++] = ((rgb & 0xff00ff) * l & ~0xff00ff) + ((rgb & 0xff00) * l & 0xff0000) >> 8;
                uA += j7;
                vA += l7;
                l1 += dl;
                rgb = texels[(vA & 0x3f80) + (uA >> 7) & (repeatTexture ? 16383 : (vA & 0x3f80) + (uA >> 7))];
                l = l1 >> 16;
                raster[k++] = ((rgb & 0xff00ff) * l & ~0xff00ff) + ((rgb & 0xff00) * l & 0xff0000) >> 8;
                uA += j7;
                vA += l7;
                l1 += dl;
                rgb = texels[(vA & 0x3f80) + (uA >> 7) & (repeatTexture ? 16383 : (vA & 0x3f80) + (uA >> 7))];
                l = l1 >> 16;
                raster[k++] = ((rgb & 0xff00ff) * l & ~0xff00ff) + ((rgb & 0xff00) * l & 0xff0000) >> 8;
                uA += j7;
                vA += l7;
                l1 += dl;
                rgb = texels[(vA & 0x3f80) + (uA >> 7) & (repeatTexture ? 16383 : (vA & 0x3f80) + (uA >> 7))];
                l = l1 >> 16;
                raster[k++] = ((rgb & 0xff00ff) * l & ~0xff00ff) + ((rgb & 0xff00) * l & 0xff0000) >> 8;
                uA += j7;
                vA += l7;
                l1 += dl;
                rgb = texels[(vA & 0x3f80) + (uA >> 7) & (repeatTexture ? 16383 : (vA & 0x3f80) + (uA >> 7))];
                l = l1 >> 16;
                raster[k++] = ((rgb & 0xff00ff) * l & ~0xff00ff) + ((rgb & 0xff00) * l & 0xff0000) >> 8;
                uA += j7;
                vA += l7;
                l1 += dl;
                rgb = texels[(vA & 0x3f80) + (uA >> 7) & (repeatTexture ? 16383 : (vA & 0x3f80) + (uA >> 7))];
                l = l1 >> 16;
                raster[k++] = ((rgb & 0xff00ff) * l & ~0xff00ff) + ((rgb & 0xff00) * l & 0xff0000) >> 8;
                uA += j7;
                vA += l7;
                l1 += dl;
                rgb = texels[(vA & 0x3f80) + (uA >> 7) & (repeatTexture ? 16383 : (vA & 0x3f80) + (uA >> 7))];
                l = l1 >> 16;
                raster[k++] = ((rgb & 0xff00ff) * l & ~0xff00ff) + ((rgb & 0xff00) * l & 0xff0000) >> 8;
                uA += j7;
                vA += l7;
                l1 += dl;
                rgb = texels[(vA & 0x3f80) + (uA >> 7) & (repeatTexture ? 16383 : (vA & 0x3f80) + (uA >> 7))];
                l = l1 >> 16;
                raster[k++] = ((rgb & 0xff00ff) * l & ~0xff00ff) + ((rgb & 0xff00) * l & 0xff0000) >> 8;
                uA += j7;
                vA += l7;
                l1 += dl;
                a1 += k2;
                i2 += a2;
                verticalC += i3;
                int i6 = verticalC >> 14;
                if (i6 != 0) {
                    j4 = a1 / i6;
                    l4 = i2 / i6;
                    if(!repeatTexture) {
                        if (j4 < 7)
                            j4 = 7;
                        else if (j4 > 16256)
                            j4 = 16256;
                    }
                }
                j7 = j4 - uA >> 3;
                l7 = l4 - vA >> 3;
                l1 += dl;
            }
            for (n = x2 - x1 & 7; n-- > 0; ) {
                int rgb;
                int l;
                rgb = texels[(vA & 0x3f80) + (uA >> 7) & (repeatTexture ? 16383 : (vA & 0x3f80) + (uA >> 7))];
                l = l1 >> 16;
                raster[k++] = ((rgb & 0xff00ff) * l & ~0xff00ff) + ((rgb & 0xff00) * l & 0xff0000) >> 8;
                uA += j7;
                vA += l7;
                l1 += dl;
            }

            return;
        }
        while (n-- > 0) {
            int i9;
            int l;
            if ((i9 = texels[(vA & 0x3f80) + (uA >> 7) & (repeatTexture ? 16383 : (vA & 0x3f80) + (uA >> 7))]) != 0) {
                l = l1 >> 16;
                raster[k] = ((i9 & 0xff00ff) * l & ~0xff00ff) + ((i9 & 0xff00) * l & 0xff0000) >> 8;
                ;
            }
            k++;
            uA += j7;
            vA += l7;
            l1 += dl;
            if ((i9 = texels[(vA & 0x3f80) + (uA >> 7) & (repeatTexture ? 16383 : (vA & 0x3f80) + (uA >> 7))]) != 0) {
                l = l1 >> 16;
                raster[k] = ((i9 & 0xff00ff) * l & ~0xff00ff) + ((i9 & 0xff00) * l & 0xff0000) >> 8;
                ;
            }
            k++;
            uA += j7;
            vA += l7;
            l1 += dl;
            if ((i9 = texels[(vA & 0x3f80) + (uA >> 7) & (repeatTexture ? 16383 : (vA & 0x3f80) + (uA >> 7))]) != 0) {
                l = l1 >> 16;
                raster[k] = ((i9 & 0xff00ff) * l & ~0xff00ff) + ((i9 & 0xff00) * l & 0xff0000) >> 8;
                ;
            }
            k++;
            uA += j7;
            vA += l7;
            l1 += dl;
            if ((i9 = texels[(vA & 0x3f80) + (uA >> 7) & (repeatTexture ? 16383 : (vA & 0x3f80) + (uA >> 7))]) != 0) {
                l = l1 >> 16;
                raster[k] = ((i9 & 0xff00ff) * l & ~0xff00ff) + ((i9 & 0xff00) * l & 0xff0000) >> 8;
                ;
            }
            k++;
            uA += j7;
            vA += l7;
            l1 += dl;
            if ((i9 = texels[(vA & 0x3f80) + (uA >> 7) & (repeatTexture ? 16383 : (vA & 0x3f80) + (uA >> 7))]) != 0) {
                l = l1 >> 16;
                raster[k] = ((i9 & 0xff00ff) * l & ~0xff00ff) + ((i9 & 0xff00) * l & 0xff0000) >> 8;
                ;
            }
            k++;
            uA += j7;
            vA += l7;
            l1 += dl;
            if ((i9 = texels[(vA & 0x3f80) + (uA >> 7) & (repeatTexture ? 16383 : (vA & 0x3f80) + (uA >> 7))]) != 0) {
                l = l1 >> 16;
                raster[k] = ((i9 & 0xff00ff) * l & ~0xff00ff) + ((i9 & 0xff00) * l & 0xff0000) >> 8;
                ;
            }
            k++;
            uA += j7;
            vA += l7;
            l1 += dl;
            if ((i9 = texels[(vA & 0x3f80) + (uA >> 7) & (repeatTexture ? 16383 : (vA & 0x3f80) + (uA >> 7))]) != 0) {
                l = l1 >> 16;
                raster[k] = ((i9 & 0xff00ff) * l & ~0xff00ff) + ((i9 & 0xff00) * l & 0xff0000) >> 8;
                ;
            }
            k++;
            uA += j7;
            vA += l7;
            l1 += dl;
            if ((i9 = texels[(vA & 0x3f80) + (uA >> 7) & (repeatTexture ? 16383 : (vA & 0x3f80) + (uA >> 7))]) != 0) {
                l = l1 >> 16;
                raster[k] = ((i9 & 0xff00ff) * l & ~0xff00ff) + ((i9 & 0xff00) * l & 0xff0000) >> 8;
                ;
            }
            k++;
            uA += j7;
            vA += l7;
            l1 += dl;
            a1 += k2;
            i2 += a2;
            verticalC += i3;
            int j6 = verticalC >> 14;
            if (j6 != 0) {
                j4 = a1 / j6;
                l4 = i2 / j6;
                if(!repeatTexture) {
                    if (j4 < 7)
                        j4 = 7;
                    else if (j4 > 16256)
                        j4 = 16256;
                }
            }
            j7 = j4 - uA >> 3;
            l7 = l4 - vA >> 3;
            l1 += dl;
        }
        for (int l3 = x2 - x1 & 7; l3-- > 0; ) {
            int j9;
            int l;
            if ((j9 = texels[(vA & 0x3f80) + (uA >> 7) & (repeatTexture ? 16383 : (vA & 0x3f80) + (uA >> 7))]) != 0) {
                l = l1 >> 16;
                raster[k] = ((j9 & 0xff00ff) * l & ~0xff00ff) + ((j9 & 0xff00) * l & 0xff0000) >> 8;
                ;
            }
            k++;
            uA += j7;
            vA += l7;
            l1 += dl;
        }
    }

    private static void drawFlatScanline(int dest[], int offset, int rgb, int x1, int x2) {
        if (testX) {
            if (x2 > Rasterizer2D.center_x) {
                x2 = Rasterizer2D.center_x;
            }
            if (x1 < 0) {
                x1 = 0;
            }
        }
        if (x1 >= x2) {
            return;
        }
        offset += x1;
        int pos = x2 - x1 >> 2;
        if (alpha == 0) {
            while (--pos >= 0) {
                for (int i = 0; i < 4; i++) {
                    dest[offset] = rgb;
                    offset++;
                }
            }
            for (pos = x2 - x1 & 3; --pos >= 0; ) {
                dest[offset] = rgb;
                offset++;
            }
            return;
        }
        int a1 = alpha;
        int a2 = 256 - alpha;
        rgb = ((rgb & 0xff00ff) * a2 >> 8 & 0xff00ff) + ((rgb & 0xff00) * a2 >> 8 & 0xff00);
        while (--pos >= 0) {
            for (int i = 0; i < 4; i++) {
                dest[offset] = rgb + ((dest[offset] & 0xff00ff) * a1 >> 8 & 0xff00ff) + ((dest[offset] & 0xff00) * a1 >> 8 & 0xff00);
                offset++;
            }
        }
        for (pos = x2 - x1 & 3; --pos >= 0; ) {
            dest[offset++] = rgb + ((dest[offset] & 0xff00ff) * a1 >> 8 & 0xff00ff) + ((dest[offset] & 0xff00) * a1 >> 8 & 0xff00);
        }
    }
    public static void drawFlatTriangle(int y1, int y2, int y3, int x1, int x2, int x3, int rgb) {
        int a_to_b = 0;
        if (y2 != y1) {
            a_to_b = (x2 - x1 << 16) / (y2 - y1);
        }
        int b_to_c = 0;
        if (y3 != y2) {
            b_to_c = (x3 - x2 << 16) / (y3 - y2);
        }
        int c_to_a = 0;
        if (y3 != y1) {
            c_to_a = (x1 - x3 << 16) / (y1 - y3);
        }
        if (y1 <= y2 && y1 <= y3) {
            if (y1 >= Rasterizer2D.clip_bottom)
                return;
            if (y2 > Rasterizer2D.clip_bottom)
                y2 = Rasterizer2D.clip_bottom;
            if (y3 > Rasterizer2D.clip_bottom)
                y3 = Rasterizer2D.clip_bottom;
            if (y2 < y3) {
                x3 = x1 <<= 16;
                if (y1 < 0) {
                    x3 -= c_to_a * y1;
                    x1 -= a_to_b * y1;
                    y1 = 0;
                }
                x2 <<= 16;
                if (y2 < 0) {
                    x2 -= b_to_c * y2;
                    y2 = 0;
                }
                if (y1 != y2 && c_to_a < a_to_b || y1 == y2 && c_to_a > b_to_c) {
                    y3 -= y2;
                    y2 -= y1;
                    for (y1 = line_offsets[y1]; --y2 >= 0; y1 += Rasterizer2D.width) {
                        drawFlatScanline(Rasterizer2D.pixels, y1, rgb, x3 >> 16, x1 >> 16);
                        x3 += c_to_a;
                        x1 += a_to_b;
                    }

                    while (--y3 >= 0) {
                        drawFlatScanline(Rasterizer2D.pixels, y1, rgb, x3 >> 16, x2 >> 16);
                        x3 += c_to_a;
                        x2 += b_to_c;
                        y1 += Rasterizer2D.width;
                    }
                    return;
                }
                y3 -= y2;
                y2 -= y1;
                for (y1 = line_offsets[y1]; --y2 >= 0; y1 += Rasterizer2D.width) {
                    drawFlatScanline(Rasterizer2D.pixels, y1, rgb, x1 >> 16, x3 >> 16);
                    x3 += c_to_a;
                    x1 += a_to_b;
                }

                while (--y3 >= 0) {
                    drawFlatScanline(Rasterizer2D.pixels, y1, rgb, x2 >> 16, x3 >> 16);
                    x3 += c_to_a;
                    x2 += b_to_c;
                    y1 += Rasterizer2D.width;
                }
                return;
            }
            x2 = x1 <<= 16;
            if (y1 < 0) {
                x2 -= c_to_a * y1;
                x1 -= a_to_b * y1;
                y1 = 0;

            }
            x3 <<= 16;
            if (y3 < 0) {
                x3 -= b_to_c * y3;
                y3 = 0;
            }
            if (y1 != y3 && c_to_a < a_to_b || y1 == y3 && b_to_c > a_to_b) {
                y2 -= y3;
                y3 -= y1;
                for (y1 = line_offsets[y1]; --y3 >= 0; y1 += Rasterizer2D.width) {
                    drawFlatScanline(Rasterizer2D.pixels, y1, rgb, x2 >> 16, x1 >> 16);
                    x2 += c_to_a;
                    x1 += a_to_b;
                }

                while (--y2 >= 0) {
                    drawFlatScanline(Rasterizer2D.pixels, y1, rgb, x3 >> 16, x1 >> 16);
                    x3 += b_to_c;
                    x1 += a_to_b;
                    y1 += Rasterizer2D.width;
                }
                return;
            }
            y2 -= y3;
            y3 -= y1;
            for (y1 = line_offsets[y1]; --y3 >= 0; y1 += Rasterizer2D.width) {
                drawFlatScanline(Rasterizer2D.pixels, y1, rgb, x1 >> 16, x2 >> 16);
                x2 += c_to_a;
                x1 += a_to_b;
            }

            while (--y2 >= 0) {
                drawFlatScanline(Rasterizer2D.pixels, y1, rgb, x1 >> 16, x3 >> 16);
                x3 += b_to_c;
                x1 += a_to_b;
                y1 += Rasterizer2D.width;
            }
            return;
        }
        if (y2 <= y3) {
            if (y2 >= Rasterizer2D.clip_bottom)
                return;
            if (y3 > Rasterizer2D.clip_bottom)
                y3 = Rasterizer2D.clip_bottom;
            if (y1 > Rasterizer2D.clip_bottom)
                y1 = Rasterizer2D.clip_bottom;
            if (y3 < y1) {
                x1 = x2 <<= 16;
                if (y2 < 0) {
                    x1 -= a_to_b * y2;
                    x2 -= b_to_c * y2;
                    y2 = 0;
                }
                x3 <<= 16;
                if (y3 < 0) {
                    x3 -= c_to_a * y3;
                    y3 = 0;
                }
                if (y2 != y3 && a_to_b < b_to_c || y2 == y3 && a_to_b > c_to_a) {
                    y1 -= y3;
                    y3 -= y2;
                    for (y2 = line_offsets[y2]; --y3 >= 0; y2 += Rasterizer2D.width) {
                        drawFlatScanline(Rasterizer2D.pixels, y2, rgb, x1 >> 16, x2 >> 16);
                        x1 += a_to_b;
                        x2 += b_to_c;
                    }

                    while (--y1 >= 0) {
                        drawFlatScanline(Rasterizer2D.pixels, y2, rgb, x1 >> 16, x3 >> 16);
                        x1 += a_to_b;
                        x3 += c_to_a;
                        y2 += Rasterizer2D.width;
                    }
                    return;
                }
                y1 -= y3;
                y3 -= y2;
                for (y2 = line_offsets[y2]; --y3 >= 0; y2 += Rasterizer2D.width) {
                    drawFlatScanline(Rasterizer2D.pixels, y2, rgb, x2 >> 16, x1 >> 16);
                    x1 += a_to_b;
                    x2 += b_to_c;
                }

                while (--y1 >= 0) {
                    drawFlatScanline(Rasterizer2D.pixels, y2, rgb, x3 >> 16, x1 >> 16);
                    x1 += a_to_b;
                    x3 += c_to_a;
                    y2 += Rasterizer2D.width;
                }
                return;
            }
            x3 = x2 <<= 16;
            if (y2 < 0) {
                x3 -= a_to_b * y2;
                x2 -= b_to_c * y2;
                y2 = 0;
            }
            x1 <<= 16;
            if (y1 < 0) {
                x1 -= c_to_a * y1;
                y1 = 0;
            }
            if (a_to_b < b_to_c) {
                y3 -= y1;
                y1 -= y2;
                for (y2 = line_offsets[y2]; --y1 >= 0; y2 += Rasterizer2D.width) {
                    drawFlatScanline(Rasterizer2D.pixels, y2, rgb, x3 >> 16, x2 >> 16);
                    x3 += a_to_b;
                    x2 += b_to_c;
                }

                while (--y3 >= 0) {
                    drawFlatScanline(Rasterizer2D.pixels, y2, rgb, x1 >> 16, x2 >> 16);
                    x1 += c_to_a;
                    x2 += b_to_c;
                    y2 += Rasterizer2D.width;
                }
                return;
            }
            y3 -= y1;
            y1 -= y2;
            for (y2 = line_offsets[y2]; --y1 >= 0; y2 += Rasterizer2D.width) {
                drawFlatScanline(Rasterizer2D.pixels, y2, rgb, x2 >> 16, x3 >> 16);
                x3 += a_to_b;
                x2 += b_to_c;
            }

            while (--y3 >= 0) {
                drawFlatScanline(Rasterizer2D.pixels, y2, rgb, x2 >> 16, x1 >> 16);
                x1 += c_to_a;
                x2 += b_to_c;
                y2 += Rasterizer2D.width;
            }
            return;
        }
        if (y3 >= Rasterizer2D.clip_bottom)
            return;
        if (y1 > Rasterizer2D.clip_bottom)
            y1 = Rasterizer2D.clip_bottom;
        if (y2 > Rasterizer2D.clip_bottom)
            y2 = Rasterizer2D.clip_bottom;
        if (y1 < y2) {
            x2 = x3 <<= 16;
            if (y3 < 0) {
                x2 -= b_to_c * y3;
                x3 -= c_to_a * y3;
                y3 = 0;
            }
            x1 <<= 16;
            if (y1 < 0) {
                x1 -= a_to_b * y1;
                y1 = 0;
            }
            if (b_to_c < c_to_a) {
                y2 -= y1;
                y1 -= y3;
                for (y3 = line_offsets[y3]; --y1 >= 0; y3 += Rasterizer2D.width) {
                    drawFlatScanline(Rasterizer2D.pixels, y3, rgb, x2 >> 16, x3 >> 16);
                    x2 += b_to_c;
                    x3 += c_to_a;
                }

                while (--y2 >= 0) {
                    drawFlatScanline(Rasterizer2D.pixels, y3, rgb, x2 >> 16, x1 >> 16);
                    x2 += b_to_c;
                    x1 += a_to_b;
                    y3 += Rasterizer2D.width;
                }
                return;
            }
            y2 -= y1;
            y1 -= y3;
            for (y3 = line_offsets[y3]; --y1 >= 0; y3 += Rasterizer2D.width) {
                drawFlatScanline(Rasterizer2D.pixels, y3, rgb, x3 >> 16, x2 >> 16);
                x2 += b_to_c;
                x3 += c_to_a;
            }

            while (--y2 >= 0) {
                drawFlatScanline(Rasterizer2D.pixels, y3, rgb, x1 >> 16, x2 >> 16);
                x2 += b_to_c;
                x1 += a_to_b;
                y3 += Rasterizer2D.width;
            }
            return;
        }
        x1 = x3 <<= 16;
        if (y3 < 0) {
            x1 -= b_to_c * y3;
            x3 -= c_to_a * y3;
            y3 = 0;
        }
        x2 <<= 16;
        if (y2 < 0) {
            x2 -= a_to_b * y2;
            y2 = 0;
        }
        if (b_to_c < c_to_a) {
            y1 -= y2;
            y2 -= y3;
            for (y3 = line_offsets[y3]; --y2 >= 0; y3 += Rasterizer2D.width) {
                drawFlatScanline(Rasterizer2D.pixels, y3, rgb, x1 >> 16, x3 >> 16);
                x1 += b_to_c;
                x3 += c_to_a;
            }

            while (--y1 >= 0) {
                drawFlatScanline(Rasterizer2D.pixels, y3, rgb, x2 >> 16, x3 >> 16);
                x2 += a_to_b;
                x3 += c_to_a;
                y3 += Rasterizer2D.width;
            }
            return;
        }
        y1 -= y2;
        y2 -= y3;
        for (y3 = line_offsets[y3]; --y2 >= 0; y3 += Rasterizer2D.width) {
            drawFlatScanline(Rasterizer2D.pixels, y3, rgb, x3 >> 16, x1 >> 16);
            x1 += b_to_c;
            x3 += c_to_a;
        }

        while (--y1 >= 0) {
            drawFlatScanline(Rasterizer2D.pixels, y3, rgb, x3 >> 16, x2 >> 16);
            x2 += a_to_b;
            x3 += c_to_a;
            y3 += Rasterizer2D.width;
        }
    }





    public static int fogColor = 0xC8C0A8;

    public static void drawFog(int begin, int end) {
        for (int depth = depth_buffer.length - 1; depth >= 0; depth--) {
            if (depth_buffer[depth] >= end) {
                pixels[depth] = fogColor;
            } else if (depth_buffer[depth] >= begin) {
                int alpha = (int) (depth_buffer[depth] - begin) / 3;
                int src = ((fogColor & 0xff00ff) * alpha >> 8 & 0xff00ff) + ((fogColor & 0xff00) * alpha >> 8 & 0xff00);
                alpha = 256 - alpha;
                int dst = pixels[depth];
                dst = ((dst & 0xff00ff) * alpha >> 8 & 0xff00ff) + ((dst & 0xff00) * alpha >> 8 & 0xff00);
                pixels[depth] = src + dst;
            }
        }
    }

    public double getBrightness() {
        return brightness;
    }

    static double brightness;

}
