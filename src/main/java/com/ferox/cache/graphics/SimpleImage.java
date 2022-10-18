package com.ferox.cache.graphics;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.PixelGrabber;
import java.awt.image.RGBImageFilter;

import javax.swing.ImageIcon;

import com.ferox.cache.Archive;
import com.ferox.draw.Rasterizer2D;
import com.ferox.io.Buffer;
import com.ferox.sign.SignLink;

public final class SimpleImage extends Rasterizer2D {

    private Image image;

    public Image getImage() {
        return image;
    }

    public int[] pixels;
    public int width;
    public int height;
    public int x_offset;
    public int y_offset;
    public int max_width;
    public int max_height;

    public SimpleImage(int width, int height) {
        this.pixels = new int[width * height];
        this.width = max_width = width;
        this.height = max_height = height;
        x_offset = y_offset = 0;
    }

    public SimpleImage(Archive archive, String name, int id) {
        Buffer buffer = new Buffer(archive.get(name + ".dat"));
        Buffer data = new Buffer(archive.get("index.dat"));
        data.pos = buffer.readUShort();
        max_width = data.readUShort();
        max_height = data.readUShort();
        int length = data.readUByte();
        int[] pixels = new int[length];
        for (int index = 0; index < length - 1; index++) {
            pixels[index + 1] = data.readTriByte();
            if (pixels[index + 1] == 0) {
                pixels[index + 1] = 1;
            }
        }

        for (int index = 0; index < id; index++) {
            data.pos += 2;
            buffer.pos += data.readUShort() * data.readUShort();
            data.pos++;
        }

        x_offset = data.readUByte();
        y_offset = data.readUByte();
        this.width = data.readUShort();
        this.height = data.readUShort();
        int opcode = data.readUByte();
        int size = this.width * this.height;
        this.pixels = new int[size];
        if (opcode == 0) {
            for (int pixel = 0; pixel < size; pixel++) {
                this.pixels[pixel] = pixels[buffer.readUByte()];
            }
            set_transparent_pixels(255, 0, 255);
            return;
        }
        if (opcode == 1) {
            for (int x = 0; x < this.width; x++) {
                for (int y = 0; y < this.height; y++) {
                    this.pixels[x + y * this.width] = pixels[buffer.readUByte()];
                }
            }
        }
        set_transparent_pixels(255, 0, 255);
    }

    public SimpleImage(Image image) {
        ImageIcon imageicon = new ImageIcon(image);
        imageicon.getIconHeight();
        imageicon.getIconWidth();
        try {
            this.width = imageicon.getIconWidth();
            this.height = imageicon.getIconHeight();
            max_width = this.width;
            max_height = this.height;
            x_offset = 0;
            y_offset = 0;
            this.pixels = new int[this.width * this.height];
            PixelGrabber pixelgrabber = new PixelGrabber(image, 0, 0, this.width, this.height, this.pixels, 0, this.width);
            pixelgrabber.grabPixels();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SimpleImage(byte[] spriteData, float width, int height) {
        try {
            image = Toolkit.getDefaultToolkit().createImage(spriteData);
            ImageIcon sprite = new ImageIcon(image);
            this.width = (int) width;
            this.height = height;
            max_width = this.width;
            max_height = this.height;
            x_offset = 0;
            y_offset = 0;
            this.pixels = new int[this.width * this.height];
            PixelGrabber pixelgrabber = new PixelGrabber(image, 0, 0, this.width, this.height, this.pixels, 0, this.width);
            pixelgrabber.grabPixels();
            image = null;
            set_transparent_pixels(255, 0, 255);
        } catch (Exception _ex) {
            System.err.println(_ex);
        }
    }

    public SimpleImage(String img) {
        try {
            image =  Toolkit.getDefaultToolkit().getImage(SignLink.findCacheDir() + "Sprites/" + img + ".png");
            ImageIcon sprite = new ImageIcon(image);
            this.width = sprite.getIconWidth();
            this.height = sprite.getIconHeight();
            max_width = this.width;
            max_height = this.height;
            x_offset = 0;
            y_offset = 0;
            this.pixels = new int[this.width * this.height];
            PixelGrabber pixelgrabber = new PixelGrabber(image, 0, 0, this.width, this.height, this.pixels, 0, this.width);
            pixelgrabber.grabPixels();
            image = null;
            set_transparent_pixels(255, 0, 255);
        } catch (Exception _ex) {
            System.err.println(_ex);
        }
    }

    public void drawAdvancedTransparentSprite(int x, int y, int opacity) {
        int alpha =  (int) (opacity * 2.56D);
        if (alpha > 256 || alpha < 0) {
            alpha = 256;
        }
        x += x_offset;
        y += y_offset;
        int dst_pos = x + y * Rasterizer2D.width;
        int src_pos = 0;
        int height = this.height;
        int width = this.width;
        int dst_width = Rasterizer2D.width - width;
        int dst_height = 0;
        if (y < Rasterizer2D.clip_top) {
            int k2 = Rasterizer2D.clip_top - y;
            height -= k2;
            y = Rasterizer2D.clip_top;
            src_pos += k2 * width;
            dst_pos += k2 * Rasterizer2D.width;
        }
        if (y + height > clip_bottom)
            height -= (y + height) - clip_bottom;
        if (x < Rasterizer2D.clip_left) {
            int l2 = Rasterizer2D.clip_left - x;
            width -= l2;
            x = Rasterizer2D.clip_left;
            src_pos += l2;
            dst_pos += l2;
            dst_height += l2;
            dst_width += l2;
        }
        if (x + width > Rasterizer2D.clip_right) {
            int i3 = (x + width) - Rasterizer2D.clip_right;
            width -= i3;
            dst_height += i3;
            dst_width += i3;
        }
        if (!(width <= 0 || height <= 0)) {
            render_transparent(src_pos, width, Rasterizer2D.pixels, this.pixels, dst_height, height, dst_width, alpha, dst_pos);
        }
    }

    public void draw_transparent(int x, int y, int alpha) {
        x += x_offset;
        y += y_offset;
        int dst_pos = x + y * Rasterizer2D.width;
        int src_pos = 0;
        int height = this.height;
        int width = this.width;
        int dst_width = Rasterizer2D.width - width;
        int dst_height = 0;
        if (y < Rasterizer2D.clip_top) {
            int size = Rasterizer2D.clip_top - y;
            height -= size;
            y = Rasterizer2D.clip_top;
            src_pos += size * width;
            dst_pos += size * Rasterizer2D.width;
        }
        if (y + height > Rasterizer2D.clip_bottom)
            height -= (y + height) - Rasterizer2D.clip_bottom;
        
        if (x < Rasterizer2D.clip_left) {
            int l2 = Rasterizer2D.clip_left - x;
            width -= l2;
            x = Rasterizer2D.clip_left;
            src_pos += l2;
            dst_pos += l2;
            dst_height += l2;
            dst_width += l2;
        }
        if (x + width > Rasterizer2D.clip_right) {
            int i3 = (x + width) - Rasterizer2D.clip_right;
            width -= i3;
            dst_height += i3;
            dst_width += i3;
        }
        if (!(width <= 0 || height <= 0)) {
            render_transparent(src_pos, width, Rasterizer2D.pixels, this.pixels, dst_height, height, dst_width, alpha, dst_pos);
        }
    }

    public void set_transparent_pixels(int r, int g, int b) {
        for (int index = 0; index < this.pixels.length; index++) {
            if (((this.pixels[index] >> 16) & 255) == r && ((this.pixels[index] >> 8) & 255) == g && (this.pixels[index] & 255) == b) {
                this.pixels[index] = 0;
            }
        }
    }

    public void init() {
        Rasterizer2D.init(this.width, this.height, this.pixels, depth_buffer);
    }

    public void blend(int red, int green, int blue) {
        for (int index = 0; index < this.pixels.length; index++) {
            int color = this.pixels[index];
            if (color != 0) {
                int r = color >> 16 & 0xff;
                r += red;
                if (r < 1)
                    r = 1;
                else if (r > 255)
                    r = 255;

                int g = color >> 8 & 0xff;
                g += green;
                if (g < 1)
                    g = 1;
                else if (g > 255)
                    g = 255;

                int b = color & 0xff;
                b += blue;
                if (b < 1)
                    b = 1;
                else if (b > 255)
                    b = 255;

                this.pixels[index] = (r << 16) + (g << 8) + b;
            }
        }
    }

    public void trim() {
        int[] pixels = new int[max_width * max_height];
        for (int y = 0; y < this.height; y++) {
            System.arraycopy(this.pixels, y * this.width, pixels, y + y_offset * max_width + x_offset, this.width);
        }
        this.pixels = pixels;
        this.width = max_width;
        this.height = max_height;
        x_offset = 0;
        y_offset = 0;
    }

    public void draw_inverse(int x, int y) {
        x += x_offset;
        y += y_offset;
        int dst_pos = x + y * Rasterizer2D.width;
        int src_pos = 0;
        int height = this.height;
        int width = this.width;
        int dst_width = Rasterizer2D.width - width;
        int src_width = 0;
        if (y < Rasterizer2D.clip_top) {
            int size = Rasterizer2D.clip_top - y;
            height -= size;
            y = Rasterizer2D.clip_top;
            src_pos += size * width;
            dst_pos += size * Rasterizer2D.width;
        }
        if (y + height > Rasterizer2D.clip_bottom)
            height -= (y + height) - Rasterizer2D.clip_bottom;

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
            int size = (x + width) - Rasterizer2D.clip_right;
            width -= size;
            src_width += size;
            dst_width += size;
        }
        if (width <= 0 || height <= 0) {
            //
        } else {
            copy(dst_pos, width, height, src_width, src_pos, dst_width, this.pixels, Rasterizer2D.pixels);
        }
    }

    private void copy(int dst_pos, int width, int height, int src_width, int src_pos, int dst_width, int src[], int pixels[]) {
        int length = -(width >> 2);
        width = -(width & 3);
        for (int column = -height; column < 0; column++) {
            for (int row = length; row < 0; row++) {
                pixels[dst_pos++] = src[src_pos++];
                pixels[dst_pos++] = src[src_pos++];
                pixels[dst_pos++] = src[src_pos++];
                pixels[dst_pos++] = src[src_pos++];
            }

            for (int step = width; step < 0; step++)
                pixels[dst_pos++] = src[src_pos++];

            dst_pos += dst_width;
            src_pos += src_width;
        }
    }

    public void drawSprite1(int i, int j) {
          drawSprite1(i, j, 128);
    }

    public void drawSprite1(int i, int j, int k, boolean overrideCanvas) {
        i += x_offset;
        j += y_offset;
        int i1 = i + j * Rasterizer2D.width;
        int j1 = 0;
        int k1 = this.height;
        int l1 = this.width;
        int i2 = Rasterizer2D.width - l1;
        int j2 = 0;
        if (!(overrideCanvas && j > 0) && j < Rasterizer2D.clip_top) {
            int k2 = Rasterizer2D.clip_top - j;
            k1 -= k2;
            j = Rasterizer2D.clip_top;
            j1 += k2 * l1;
            i1 += k2 * Rasterizer2D.width;
        }
        if (j + k1 > Rasterizer2D.clip_bottom)
            k1 -= (j + k1) - Rasterizer2D.clip_bottom;
        if (!overrideCanvas && i < Rasterizer2D.clip_left) {
            int l2 = Rasterizer2D.clip_left - i;
            l1 -= l2;
            i = Rasterizer2D.clip_left;
            j1 += l2;
            i1 += l2;
            j2 += l2;
            i2 += l2;
        }
        if (i + l1 > Rasterizer2D.clip_right) {
            int i3 = (i + l1) - Rasterizer2D.clip_right;
            l1 -= i3;
            j2 += i3;
            i2 += i3;
        }
        if (!(l1 <= 0 || k1 <= 0)) {
            render_transparent(j1, l1, Rasterizer2D.pixels, this.pixels, j2, k1, i2, k, i1);
        }
    }

    public void drawShadedSpriteWithoutBounds(int i, int j, int k, boolean overrideCanvas) {
        i += x_offset;
        j += y_offset;
        int i1 = i + j * Rasterizer2D.width;
        int j1 = 0;
        int k1 = this.height;
        int l1 = this.width;
        int i2 = Rasterizer2D.width - l1;
        int j2 = 0;
        if (!(overrideCanvas && j > 0) && j < Rasterizer2D.clip_top) {
            int k2 = Rasterizer2D.clip_top - j;
            k1 -= k2;
            j = Rasterizer2D.clip_top;
            j1 += k2 * l1;
            i1 += k2 * Rasterizer2D.width;
        }
        if (j + k1 > Rasterizer2D.clip_bottom)
            k1 -= (j + k1) - Rasterizer2D.clip_bottom;
        if (!overrideCanvas && i < Rasterizer2D.clip_left) {
            int l2 = Rasterizer2D.clip_left - i;
            l1 -= l2;
            i = Rasterizer2D.clip_left;
            j1 += l2;
            i1 += l2;
            j2 += l2;
            i2 += l2;
        }
        if (i + l1 > Rasterizer2D.clip_right) {
            int i3 = (i + l1) - Rasterizer2D.clip_right;
            l1 -= i3;
            j2 += i3;
            i2 += i3;
        }
        if (!(l1 <= 0 || k1 <= 0)) {
            renderShadedARGBPixelsWithoutBounds(j1, l1, Rasterizer2D.pixels, this.pixels, j2, k1, i2, k, i1);
        }
        drawTransparentSpriteWithoutBounds(i, j, k, overrideCanvas);
    }

    private void renderShadedARGBPixelsWithoutBounds(int i, int j, int ai[], int ai1[], int l, int i1, int j1, int k1, int l1) {
        int k;// was parameter
        int j2 = 256 - k1;
        for (int k2 = -i1; k2 < 0; k2++) {
            for (int l2 = -j; l2 < 0; l2++) {
                k = ai1[i++];
                if (k != 0) {
                    int i3 = ai[l1];
                    ai[l1++] = 0x000000 >> 8;
                } else {
                    l1++;
                }
            }
            l1 += j1;
            i += l;
        }
    }

    public void drawTransparentSpriteWithoutBounds(int i, int j, int k, boolean overrideCanvas) {
        i += x_offset;
        j += y_offset;
        int i1 = i + j * Rasterizer2D.width;
        int j1 = 0;
        int k1 = this.height;
        int l1 = this.width;
        int i2 = Rasterizer2D.width - l1;
        int j2 = 0;
        if (!(overrideCanvas && j > 0) && j < Rasterizer2D.clip_top) {
            int k2 = Rasterizer2D.clip_top - j;
            k1 -= k2;
            j = Rasterizer2D.clip_top;
            j1 += k2 * l1;
            i1 += k2 * Rasterizer2D.width;
        }
        if (j + k1 > Rasterizer2D.clip_bottom)
            k1 -= (j + k1) - Rasterizer2D.clip_bottom;
        if (!overrideCanvas && i < Rasterizer2D.clip_left) {
            int l2 = Rasterizer2D.clip_left - i;
            l1 -= l2;
            i = Rasterizer2D.clip_left;
            j1 += l2;
            i1 += l2;
            j2 += l2;
            i2 += l2;
        }
        if (i + l1 > Rasterizer2D.clip_right) {
            int i3 = (i + l1) - Rasterizer2D.clip_right;
            l1 -= i3;
            j2 += i3;
            i2 += i3;
        }
        if (!(l1 <= 0 || k1 <= 0)) {
            render_transparent(j1, l1, Rasterizer2D.pixels, this.pixels, j2, k1, i2, k, i1);
        }
    }

    public void drawSpriteWithOutline(int i, int k, int color, boolean overrideCanvas) {
        int tempWidth = this.width + 2;
        int tempHeight = this.height + 2;
        int[] tempArray = new int[tempWidth * tempHeight];
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                if (this.pixels[x + y * this.width] != 0)
                    tempArray[(x + 1) + (y + 1) * tempWidth] = this.pixels[x + y * this.width];
            }
        }
        for (int x = 0; x < tempWidth; x++) {
            for (int y = 0; y < tempHeight; y++) {
                if (tempArray[(x) + (y) * tempWidth] == 0) {
                    if (x < tempWidth - 1 && tempArray[(x + 1) + ((y) * tempWidth)] != 0
                        && tempArray[(x + 1) + ((y) * tempWidth)] != 0xffffff) {
                        tempArray[(x) + (y) * tempWidth] = color;
                    }
                    if (x != 0 && tempArray[(x - 1) + ((y) * tempWidth)] != 0
                        && tempArray[(x - 1) + ((y) * tempWidth)] != 0xffffff) {
                        tempArray[(x) + (y) * tempWidth] = color;
                    }
                    if (y < tempHeight - 1 && tempArray[(x) + ((y + 1) * tempWidth)] != 0
                        && tempArray[(x) + ((y + 1) * tempWidth)] != 0xffffff) {
                        tempArray[(x) + (y) * tempWidth] = color;
                    }
                    if (y != 0 && tempArray[(x) + ((y - 1) * tempWidth)] != 0
                        && tempArray[(x) + ((y - 1) * tempWidth)] != 0xffffff) {
                        tempArray[(x) + (y) * tempWidth] = color;
                    }
                }
            }
        }
        i--;
        k--;
        i += x_offset;
        k += y_offset;
        int l = i + k * Rasterizer2D.width;
        int i1 = 0;
        int j1 = tempHeight;
        int k1 = tempWidth;
        int l1 = Rasterizer2D.width - k1;
        int i2 = 0;
        if (!(overrideCanvas && k > 0) && k < Rasterizer2D.clip_top) {
            int j2 = Rasterizer2D.clip_top - k;
            j1 -= j2;
            k = Rasterizer2D.clip_top;
            i1 += j2 * k1;
            l += j2 * Rasterizer2D.width;
        }
        if (k + j1 > Rasterizer2D.clip_bottom) {
            j1 -= (k + j1) - Rasterizer2D.clip_bottom;
        }
        if (!overrideCanvas && i < Rasterizer2D.clip_left) {
            int k2 = Rasterizer2D.clip_left - i;
            k1 -= k2;
            i = Rasterizer2D.clip_left;
            i1 += k2;
            l += k2;
            i2 += k2;
            l1 += k2;
        }
        if (!overrideCanvas && i + k1 > Rasterizer2D.clip_right) {
            int l2 = (i + k1) - Rasterizer2D.clip_right;
            k1 -= l2;
            i2 += l2;
            l1 += l2;
        }
        if (!(k1 <= 0 || j1 <= 0)) {
            render(Rasterizer2D.pixels, tempArray, i1, l, k1, j1, l1, i2);
        }
    }

    public void draw_highlighted(int x, int y, int color) {
        int highlight_width = this.width + 2;
        int highlight_height = this.height + 2;
        int[] pixels = new int[highlight_width * highlight_height];
        for (int _x = 0; _x < this.width; _x++) {
            for (int _y = 0; _y < this.height; _y++) {
                if (this.pixels[_x + _y * this.width] != 0)
                    pixels[(_x + 1) + (_y + 1) * highlight_width] = this.pixels[_x + _y * this.width];
            }
        }
        for (int x_ = 0; x_ < highlight_width; x_++) {
            for (int y_ = 0; y_ < highlight_height; y_++) {
                if (pixels[(x_) + (y_) * highlight_width] == 0) {
                    if (x_ < highlight_width - 1 && pixels[(x_ + 1) + ((y_) * highlight_width)] != 0
                        && pixels[(x_ + 1) + ((y_) * highlight_width)] != 0xffffff) {
                        pixels[(x_) + (y_) * highlight_width] = color;
                    }
                    if (x_ != 0 && pixels[(x_ - 1) + ((y_) * highlight_width)] != 0
                        && pixels[(x_ - 1) + ((y_) * highlight_width)] != 0xffffff) {
                        pixels[(x_) + (y_) * highlight_width] = color;
                    }
                    if (y_ < highlight_height - 1 && pixels[(x_) + ((y_ + 1) * highlight_width)] != 0
                        && pixels[(x_) + ((y_ + 1) * highlight_width)] != 0xffffff) {
                        pixels[(x_) + (y_) * highlight_width] = color;
                    }
                    if (y_ != 0 && pixels[(x_) + ((y_ - 1) * highlight_width)] != 0
                        && pixels[(x_) + ((y_ - 1) * highlight_width)] != 0xffffff) {
                        pixels[(x_) + (y_) * highlight_width] = color;
                    }
                }
            }
        }
        x--;
        y--;
        x += this.x_offset;
        y += this.y_offset;
        int dst_pos = x + y * Rasterizer2D.width;
        int src_pos = 0;
        int height = highlight_height;
        int width = highlight_width;
        int dst_width = Rasterizer2D.width - width;
        int src_width = 0;
        if (y < Rasterizer2D.clip_top) {
            int size = Rasterizer2D.clip_top - y;
            height -= size;
            y = Rasterizer2D.clip_top;
            src_pos += size * width;
            dst_pos += size * Rasterizer2D.width;
        }
        if (y + height > Rasterizer2D.clip_bottom) {
            height -= (y + height) - Rasterizer2D.clip_bottom;
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
            int size = (x + width) - Rasterizer2D.clip_right;
            width -= size;
            src_width += size;
            dst_width += size;
        }
        if (!(width <= 0 || height <= 0)) {
            render(Rasterizer2D.pixels, pixels, src_pos, dst_pos, width, height, dst_width, src_width);
        }
    }

    public void drawSprite1(int i, int j, int k) {
        i += x_offset;
        j += y_offset;
        int i1 = i + j * Rasterizer2D.width;
        int j1 = 0;
        int k1 = this.height;
        int l1 = this.width;
        int i2 = Rasterizer2D.width - l1;
        int j2 = 0;
        if (j < Rasterizer2D.clip_top) {
            int k2 = Rasterizer2D.clip_top - j;
            k1 -= k2;
            j = Rasterizer2D.clip_top;
            j1 += k2 * l1;
            i1 += k2 * Rasterizer2D.width;
        }
        if (j + k1 > Rasterizer2D.clip_bottom)
            k1 -= (j + k1) - Rasterizer2D.clip_bottom;
        if (i < Rasterizer2D.clip_left) {
            int l2 = Rasterizer2D.clip_left - i;
            l1 -= l2;
            i = Rasterizer2D.clip_left;
            j1 += l2;
            i1 += l2;
            j2 += l2;
            i2 += l2;
        }
        if (i + l1 > Rasterizer2D.clip_right) {
            int i3 = (i + l1) - Rasterizer2D.clip_right;
            l1 -= i3;
            j2 += i3;
            i2 += i3;
        }
        if (!(l1 <= 0 || k1 <= 0)) {
            render_transparent(j1, l1, Rasterizer2D.pixels, this.pixels, j2, k1, i2, k, i1);
        }
    }

    public void drawSprite(int x, int y)
    {
        x += x_offset;
        y += y_offset;
        int rasterClip = x + y * Rasterizer2D.width;
        int imageClip = 0;
        int height = this.height;
        int width = this.width;
        int rasterOffset = Rasterizer2D.width - width;
        int imageOffset = 0;
        if (y < Rasterizer2D.clip_top)
        {
            int dy = Rasterizer2D.clip_top - y;
            height -= dy;
            y = Rasterizer2D.clip_top;
            imageClip += dy * width;
            rasterClip += dy * Rasterizer2D.width;
        }
        if (y + height > Rasterizer2D.clip_bottom)
            height -= (y + height) - Rasterizer2D.clip_bottom;
        if (x < Rasterizer2D.clip_left)
        {
            int dx = Rasterizer2D.clip_left - x;
            width -= dx;
            x = Rasterizer2D.clip_left;
            imageClip += dx;
            rasterClip += dx;
            imageOffset += dx;
            rasterOffset += dx;
        }
        if (x + width > Rasterizer2D.clip_right)
        {
            int dx = (x + width) - Rasterizer2D.clip_right;
            width -= dx;
            imageOffset += dx;
            rasterOffset += dx;
        }
        if (!(width <= 0 || height <= 0))
        {
            render(Rasterizer2D.pixels, this.pixels, imageClip, rasterClip, width, height, rasterOffset, imageOffset);
        }
    }

    public void drawSprite(int i, int k, int color) {
        int tempWidth = this.width + 2;
        int tempHeight = this.height + 2;
        int[] tempArray = new int[tempWidth * tempHeight];
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                if (this.pixels[x + y * this.width] != 0)
                    tempArray[(x + 1) + (y + 1) * tempWidth] = this.pixels[x + y * this.width];
            }
        }
        for (int x = 0; x < tempWidth; x++) {
            for (int y = 0; y < tempHeight; y++) {
                if (tempArray[(x) + (y) * tempWidth] == 0) {
                    if (x < tempWidth - 1 && tempArray[(x + 1) + ((y) * tempWidth)] > 0 && tempArray[(x + 1) + ((y) * tempWidth)] != 0xffffff) {
                        tempArray[(x) + (y) * tempWidth] = color;
                    }
                    if (x > 0 && tempArray[(x - 1) + ((y) * tempWidth)] > 0 && tempArray[(x - 1) + ((y) * tempWidth)] != 0xffffff) {
                        tempArray[(x) + (y) * tempWidth] = color;
                    }
                    if (y < tempHeight - 1 && tempArray[(x) + ((y + 1) * tempWidth)] > 0 && tempArray[(x) + ((y + 1) * tempWidth)] != 0xffffff) {
                        tempArray[(x) + (y) * tempWidth] = color;
                    }
                    if (y > 0 && tempArray[(x) + ((y - 1) * tempWidth)] > 0 && tempArray[(x) + ((y - 1) * tempWidth)] != 0xffffff) {
                        tempArray[(x) + (y) * tempWidth] = color;
                    }
                }
            }
        }
        i--;
        k--;
        i += x_offset;
        k += y_offset;
        int l = i + k * Rasterizer2D.width;
        int i1 = 0;
        int j1 = tempHeight;
        int k1 = tempWidth;
        int l1 = Rasterizer2D.width - k1;
        int i2 = 0;
        if (k < Rasterizer2D.clip_top) {
            int j2 = Rasterizer2D.clip_top - k;
            j1 -= j2;
            k = Rasterizer2D.clip_top;
            i1 += j2 * k1;
            l += j2 * Rasterizer2D.width;
        }
        if (k + j1 > Rasterizer2D.clip_bottom) {
            j1 -= (k + j1) - Rasterizer2D.clip_bottom;
        }
        if (i < Rasterizer2D.clip_left) {
            int k2 = Rasterizer2D.clip_left - i;
            k1 -= k2;
            i = Rasterizer2D.clip_left;
            i1 += k2;
            l += k2;
            i2 += k2;
            l1 += k2;
        }
        if (i + k1 > Rasterizer2D.clip_right) {
            int l2 = (i + k1) - Rasterizer2D.clip_right;
            k1 -= l2;
            i2 += l2;
            l1 += l2;
        }
        if (!(k1 <= 0 || j1 <= 0)) {
            render(Rasterizer2D.pixels, tempArray, i1, l, k1, j1, l1, i2);
        }
    }

    private void render(int pixels[], int src[], int src_pos, int dst_pos, int width, int height, int dst_width, int src_width) {
        int index;// was parameter
        int length = -(width >> 2);
        width = -(width & 3);
        for (int column = -height; column < 0; column++) {
            for (int row = length; row < 0; row++) {
                index = src[src_pos++];
                if (index != 0 && index != -1) {
                    pixels[dst_pos++] = index;
                } else {
                    dst_pos++;
                }
                index = src[src_pos++];
                if (index != 0 && index != -1) {
                    pixels[dst_pos++] = index;
                } else {
                    dst_pos++;
                }
                index = src[src_pos++];
                if (index != 0 && index != -1) {
                    pixels[dst_pos++] = index;
                } else {
                    dst_pos++;
                }
                index = src[src_pos++];
                if (index != 0 && index != -1) {
                    pixels[dst_pos++] = index;
                } else {
                    dst_pos++;
                }
            }

            for (int step = width; step < 0; step++) {
                index = src[src_pos++];
                if (index != 0 && index != -1) {
                    pixels[dst_pos++] = index;
                } else {
                    dst_pos++;
                }
            }
            dst_pos += dst_width;
            src_pos += src_width;
        }
    }

    private void render_transparent(int src_pos, int width, int pixels[], int raster[], int src_width, int height, int dst_width, int alpha, int dst_pos) {
        int color;// was parameter
        int opacity = 256 - alpha;
        for (int column = -height; column < 0; column++) {
            for (int row = -width; row < 0; row++) {
                color = raster[src_pos++];
                if (color != 0) {
                    int src = pixels[dst_pos];
                    pixels[dst_pos++] = ((color & 0xff00ff) * alpha + (src & 0xff00ff) * opacity & 0xff00ff00) + ((color & 0xff00) * alpha + (src & 0xff00) * opacity & 0xff0000) >> 8;
                } else {
                    dst_pos++;
                }
            }

            dst_pos += dst_width;
            src_pos += src_width;
        }
    }

    public void rotate_raster(int height, int rotation, int dst_width[], int hinge_size, int raster_height[], int centerY, int y, int x, int width, int centerX) {
        try {
            int location_x = -width / 2;
            int location_y = -height / 2;
            int sin = (int) (Math.sin((double) rotation / 326.11000000000001D) * 65536D);
            int cos = (int) (Math.cos((double) rotation / 326.11000000000001D) * 65536D);
            sin = sin * hinge_size >> 8;
            cos = cos * hinge_size >> 8;
            int rot_x = (centerX << 16) + (location_y * sin + location_x * cos);
            int rot_b = (centerY << 16) + (location_y * cos - location_x * sin);
            int dst_pos = x + y * Rasterizer2D.width;
            for (y = 0; y < height; y++) {
                int step = raster_height[y];
                int index = dst_pos + step;
                int a = rot_x + cos * step;
                int b = rot_b - sin * step;
                for (x = -dst_width[y]; x < 0; x++) {
                    int top = this.pixels[(a >> 16) + (b >> 16) * this.width];
                    int right = this.pixels[((a >> 16) + 1) + (b >> 16) * this.width];
                    int left = this.pixels[(a >> 16) + ((b >> 16) + 1) * this.width];
                    int bottom = this.pixels[((a >> 16) + 1) + ((b >> 16) + 1) * this.width];

                    int u1 = (a >> 8) - ((a >> 16) << 8);
                    int v1 = (b >> 8) - ((b >> 16) << 8);
                    int u2 = (((a >> 16) + 1) << 8) - (a >> 8);
                    int v2 = (((b >> 16) + 1) << 8) - (b >> 8);

                    int top_a = u2 * v2;
                    int right_a = u1 * v2;
                    int left_a = u2 * v1;
                    int bottom_a = u1 * v1;

                    int red = (top >> 16 & 0xff) * top_a + (right >> 16 & 0xff) * right_a + (left >> 16 & 0xff) * left_a + (bottom >> 16 & 0xff) * bottom_a & 0xff0000;
                    int green = (top >> 8 & 0xff) * top_a + (right >> 8 & 0xff) * right_a + (left >> 8 & 0xff) * left_a + (bottom >> 8 & 0xff) * bottom_a >> 8 & 0xff00;
                    int blue = (top & 0xff) * top_a + (right & 0xff) * right_a + (left & 0xff) * left_a + (bottom & 0xff) * bottom_a >> 16;

                    Rasterizer2D.pixels[index++] = red | green | blue;
                    a += cos;
                    b -= sin;
                }
                rot_x += sin;
                rot_b += cos;
                dst_pos += Rasterizer2D.width;
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public SimpleImage(byte[] data) {
        try {
            Image image = Toolkit.getDefaultToolkit().createImage(data);
            ImageIcon sprite = new ImageIcon(image);
            this.width = sprite.getIconWidth();
            this.height = sprite.getIconHeight();
            max_width = this.width;
            max_height = this.height;
            x_offset = 0;
            y_offset = 0;
            this.pixels = new int[this.width * this.height];
            PixelGrabber grab = new PixelGrabber(image, 0, 0, this.width, this.height, this.pixels, 0, this.width);
            grab.grabPixels();
            image = null;
            set_transparent_pixels(255, 0, 255);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public SimpleImage(int width, int height, int offsetX, int offsetY, int[] pixels) {
        this.width = width;
        this.height = height;
        this.x_offset = offsetX;
        this.y_offset = offsetY;
        this.pixels = pixels;

        Color color = Color.MAGENTA;
        set_transparent_pixels(color.getRed(), color.getGreen(), color.getBlue());
    }

    public static Image create(byte spriteData[]) {
        return Toolkit.getDefaultToolkit().createImage(spriteData);
    }

    public void drawAdvancedSprite(int xPos, int yPos) {
        drawAdvancedSprite(xPos, yPos, 256);
    }

    public void drawAdvancedSprite(int xPos, int yPos, int alpha) {
        int alphaValue = alpha;
        xPos += x_offset;
        yPos += y_offset;
        int i1 = xPos + yPos * Rasterizer2D.width;
        int j1 = 0;
        int spriteHeight = this.height;
        int spriteWidth = this.width;
        int i2 = Rasterizer2D.width - spriteWidth;
        int j2 = 0;
        if (yPos < Rasterizer2D.clip_top) {
            int k2 = Rasterizer2D.clip_top - yPos;
            spriteHeight -= k2;
            yPos = Rasterizer2D.clip_top;
            j1 += k2 * spriteWidth;
            i1 += k2 * Rasterizer2D.width;
        }
        if (yPos + spriteHeight > Rasterizer2D.clip_bottom)
            spriteHeight -= (yPos + spriteHeight) - Rasterizer2D.clip_bottom;
            if (xPos < Rasterizer2D.clip_left) {
            int l2 = Rasterizer2D.clip_left - xPos;
            spriteWidth -= l2;
            xPos = Rasterizer2D.clip_left;
            j1 += l2;
            i1 += l2;
            j2 += l2;
            i2 += l2;
        }
        if (xPos + spriteWidth > Rasterizer2D.clip_right) {
            int i3 = (xPos + spriteWidth) - Rasterizer2D.clip_right;
            spriteWidth -= i3;
            j2 += i3;
            i2 += i3;
        }
        if (!(spriteWidth <= 0 || spriteHeight <= 0)) {
            renderARGBPixels(spriteWidth, spriteHeight, this.pixels, Rasterizer2D.pixels, i1, alphaValue, j1, j2, i2);
        }
    }

    public void drawShadedSprite(int xPos, int yPos, int shade) {
        int shadeValue = shade;
        xPos += x_offset;
        yPos += y_offset;
        int i1 = xPos + yPos * Rasterizer2D.width;
        int j1 = 0;
        int spriteHeight = this.height;
        int spriteWidth = this.width;
        int i2 = Rasterizer2D.width - spriteWidth;
        int j2 = 0;
        if (yPos < Rasterizer2D.clip_top) {
            int k2 = Rasterizer2D.clip_top - yPos;
            spriteHeight -= k2;
            yPos = Rasterizer2D.clip_top;
            j1 += k2 * spriteWidth;
            i1 += k2 * Rasterizer2D.width;
        }
        if (yPos + spriteHeight > Rasterizer2D.clip_bottom)
            spriteHeight -= (yPos + spriteHeight) - Rasterizer2D.clip_bottom;
        if (xPos < Rasterizer2D.clip_left) {
            int l2 = Rasterizer2D.clip_left - xPos;
            spriteWidth -= l2;
            xPos = Rasterizer2D.clip_left;
            j1 += l2;
            i1 += l2;
            j2 += l2;
            i2 += l2;
        }
        if (xPos + spriteWidth > Rasterizer2D.clip_right) {
            int i3 = (xPos + spriteWidth) - Rasterizer2D.clip_right;
            spriteWidth -= i3;
            j2 += i3;
            i2 += i3;
        }
        if (!(spriteWidth <= 0 || spriteHeight <= 0)) {
            renderShadedARGBPixels(spriteWidth, spriteHeight, this.pixels, Rasterizer2D.pixels, i1, shadeValue, j1, j2,
                i2);
        }
        drawAdvancedSprite(xPos, yPos, shade);
    }

    private void renderShadedARGBPixels(int spriteWidth, int spriteHeight, int spritePixels[], int renderAreaPixels[],
                                        int pixel, int alphaValue, int i, int l, int j1) {
        int pixelColor;
        int alphaLevel;
        int alpha = alphaValue;
        for (int height = -spriteHeight; height < 0; height++) {
            for (int width = -spriteWidth; width < 0; width++) {
                alphaValue = ((this.pixels[i] >> 24) & (alpha - 1));
                alphaLevel = 256 - alphaValue;
                if (alphaLevel > 256) {
                    alphaValue = 0;
                }
                if (alpha == 0) {
                    alphaLevel = 256;
                    alphaValue = 0;
                }
                pixelColor = spritePixels[i++];
                if (pixelColor != 0) {
                    int pixelValue = renderAreaPixels[pixel];
                    renderAreaPixels[pixel++] = 0x000000 >> 8;
                } else {
                    pixel++;
                }
            }
            pixel += j1;
            i += l;
        }
    }

    private void renderARGBPixels(int spriteWidth, int spriteHeight, int spritePixels[], int renderAreaPixels[], int pixel, int alphaValue, int i, int l, int j1) {
        int pixelColor;
        int alphaLevel;
        int alpha = alphaValue;
        for (int height = -spriteHeight; height < 0; height++) {
            for (int width = -spriteWidth; width < 0; width++) {
                alphaValue = ((this.pixels[i] >> 24) & (alpha - 1));
                alphaLevel = 256 - alphaValue;
                if (alphaLevel > 256) {
                    alphaValue = 0;
                }
                if (alpha == 0) {
                    alphaLevel = 256;
                    alphaValue = 0;
                }
                pixelColor = spritePixels[i++];
                if (pixelColor != 0) {
                    int pixelValue = renderAreaPixels[pixel];
                    renderAreaPixels[pixel++] = ((pixelColor & 0xff00ff) * alphaValue + (pixelValue & 0xff00ff) * alphaLevel & 0xff00ff00) + ((pixelColor & 0xff00) * alphaValue + (pixelValue & 0xff00) * alphaLevel & 0xff0000) >> 8;
                } else {
                    pixel++;
                }
            }
            pixel += j1;
            i += l;
        }
    }

    public void highlight(int color) {
        int[] pixels = new int[this.width * this.height];
        int index = 0;
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                int src = this.pixels[index];
                if (src == 0) {
                    if (x > 0 && this.pixels[index - 1] != 0) {
                        src = color;
                    } else if (y > 0 && this.pixels[index - this.width] != 0) {
                        src = color;
                    } else if (x < this.width - 1 && this.pixels[index + 1] != 0) {
                        src = color;
                    } else if (y < this.height - 1 && this.pixels[index + this.width] != 0) {
                        src = color;
                    }
                }
                pixels[index++] = src;
            }
        }
        this.pixels = pixels;
    }

    public void shadow(int color) {
        for (int y = this.height - 1; y > 0; y--) {
            int pos = y * this.width;
            for (int x = this.width - 1; x > 0; x--) {
                if (this.pixels[x + pos] == 0 && this.pixels[x + pos - 1 - this.width] != 0) {
                    this.pixels[x + pos] = color;
                }
            }
        }
    }

    public Image convertToImage() {

        // Convert to buffered image
        BufferedImage bufferedimage = new BufferedImage(this.width, this.height, 1);
        bufferedimage.setRGB(0, 0, this.width, this.height, this.pixels, 0, this.width);

        // Filter to ensure transparency preserved
        ImageFilter filter = new RGBImageFilter() {
            public int markerRGB = Color.BLACK.getRGB() | 0xFF000000;

            public final int filterRGB(int x, int y, int rgb) {
                if ((rgb | 0xFF000000) == markerRGB) {
                    return 0x00FFFFFF & rgb;
                } else {
                    return rgb;
                }
            }
        };

        // Create image
        ImageProducer ip = new FilteredImageSource(bufferedimage.getSource(), filter);
        return Toolkit.getDefaultToolkit().createImage(ip);
    }
}
