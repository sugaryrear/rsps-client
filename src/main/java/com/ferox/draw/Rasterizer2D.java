package com.ferox.draw;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.util.Hashtable;

import com.ferox.cache.graphics.SimpleImage;
import com.ferox.collection.Cacheable;

public class Rasterizer2D extends Cacheable {

    public static void init(int width, int height, int[] pixels, float[] depth) {
        depth_buffer = depth;
        Rasterizer2D.pixels = pixels;
        Rasterizer2D.width = width;
        Rasterizer2D.height = height;
        set_clip(0, 0, width, height);
    }

    public static void draw_arc(int x, int y, int width, int height, int stroke, int start, int sweep, int color, int alpha, int closure, boolean fill) {
        Graphics2D graphics = SimpleImage.createGraphics(Rasterizer2D.pixels, Rasterizer2D.width, Rasterizer2D.height);
        graphics.setColor(new Color((color >> 16 & 0xff), (color >> 8 & 0xff), (color & 0xff), ((alpha >= 256 || alpha < 0) ? 255 : alpha)));

        RenderingHints render = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        render.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        graphics.setRenderingHints(render);
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        if (!fill) {
            graphics.setStroke(new BasicStroke((stroke < 1 ? 1 : stroke)));
        }
        // Closure types - OPEN(0), CHORD(1), PIE(2)
        Arc2D.Double arc = new Arc2D.Double(x + stroke, y + stroke, width, height, start, sweep, closure);
        if (fill) {
            graphics.fill(arc);
        } else {
            graphics.draw(arc);
        }
    }

    /**
     * Draws a transparent box with a gradient that changes from top to bottom.
     * @param leftX The left edge X-Coordinate of the box.
     * @param topY The top edge Y-Coordinate of the box.
     * @param width The width of the box.
     * @param height The height of the box.
     * @param topColour The top rgbColour of the gradient.
     * @param bottomColour The bottom rgbColour of the gradient.
     * @param opacity The opacity value ranging from 0 to 256.
     */
    public static void drawTransparentGradientBox(int leftX, int topY, int width, int height, int topColour, int bottomColour, int opacity) {
        int gradientProgress = 0;
        int progressPerPixel = 0x10000 / height;
        if (leftX < Rasterizer2D.clip_left) {
            width -= Rasterizer2D.clip_left - leftX;
            leftX = Rasterizer2D.clip_left;
        }
        if (topY < Rasterizer2D.clip_top) {
            gradientProgress += (Rasterizer2D.clip_top - topY) * progressPerPixel;
            height -= Rasterizer2D.clip_top - topY;
            topY = Rasterizer2D.clip_top;
        }
        if (leftX + width > clip_right)
            width = clip_right - leftX;
        if (topY + height > clip_bottom)
            height = clip_bottom - topY;
        int leftOver = Rasterizer2D.width - width;
        int transparency = 256 - opacity;
        int pixelIndex = leftX + topY * Rasterizer2D.width;
        for (int rowIndex = 0; rowIndex < height; rowIndex++) {
            int gradient = 0x10000 - gradientProgress >> 8;
            int inverseGradient = gradientProgress >> 8;
            int gradientColour = ((topColour & 0xff00ff) * gradient + (bottomColour & 0xff00ff) * inverseGradient & 0xff00ff00) + ((topColour & 0xff00) * gradient + (bottomColour & 0xff00) * inverseGradient & 0xff0000) >>> 8;
            int transparentPixel = ((gradientColour & 0xff00ff) * opacity >> 8 & 0xff00ff) + ((gradientColour & 0xff00) * opacity >> 8 & 0xff00);
            for (int columnIndex = 0; columnIndex < width; columnIndex++) {
                int backgroundPixel = pixels[pixelIndex];
                backgroundPixel = ((backgroundPixel & 0xff00ff) * transparency >> 8 & 0xff00ff) + ((backgroundPixel & 0xff00) * transparency >> 8 & 0xff00);
                pixels[pixelIndex++] = transparentPixel + backgroundPixel;
            }
            pixelIndex += leftOver;
            gradientProgress += progressPerPixel;
        }
    }

    /**
     * Sets the drawingArea to the default size and position.
     * Position: Upper left corner.
     * Size: As specified before.
     */
    public static void set_default_size() {
        clip_left = 0;
        clip_top = 0;
        clip_right = width;
        clip_bottom = height;
        center_x = clip_right;
        viewport_center_y = clip_right / 2;
    }

    public static void set_clip(int x, int y, int width, int height) {
        if(x < 0) {
            x = 0;
        }
        if(y < 0) {
            y = 0;
        }
        if (width > Rasterizer2D.width) {
            width = Rasterizer2D.width;
        }
        if (height > Rasterizer2D.height) {
            height = Rasterizer2D.height;
        }
        clip_left = x;
        clip_top = y;
        clip_right = width;
        clip_bottom = height;
        center_x = clip_right;
        viewport_center_y = clip_right / 2;
        viewport_center_x = clip_bottom / 2;
    }

    /**
     * Clears the drawingArea by setting every pixel to 0 (black).
     */
    public static void clear()    {
        int size = width * height;
        for(int coordinates = 0; coordinates < size; coordinates++) {
            pixels[coordinates] = 0;
            depth_buffer[coordinates] = Float.MAX_VALUE;
        }
    }

    public static void draw_filled_rect(int x, int y, int width, int height, int color) {
        draw_filled_rect(x, y, width, height, color, 255);
    }

    /**
     * Draws a transparent box.
     * @param leftX The left edge X-Coordinate of the box.
     * @param topY The top edge Y-Coordinate of the box.
     * @param width The box width.
     * @param height The box height.
     * @param rgbColour The box colour.
     * @param opacity The opacity value ranging from 0 to 256.
     */
    public static void draw_filled_rect(int leftX, int topY, int width, int height, int rgbColour, int opacity) {
        if (leftX < Rasterizer2D.clip_left) {
            width -= Rasterizer2D.clip_left - leftX;
            leftX = Rasterizer2D.clip_left;
        }
        if (topY < Rasterizer2D.clip_top) {
            height -= Rasterizer2D.clip_top - topY;
            topY = Rasterizer2D.clip_top;
        }
        if (leftX + width > clip_right)
            width = clip_right - leftX;
        if (topY + height > clip_bottom)
            height = clip_bottom - topY;
        int transparency = 256 - opacity;
        int red = (rgbColour >> 16 & 0xff) * opacity;
        int green = (rgbColour >> 8 & 0xff) * opacity;
        int blue = (rgbColour & 0xff) * opacity;
        int leftOver = Rasterizer2D.width - width;
        int pixelIndex = leftX + topY * Rasterizer2D.width;
        for (int rowIndex = 0; rowIndex < height; rowIndex++) {
            for (int columnIndex = 0; columnIndex < width; columnIndex++) {
                int otherRed = (pixels[pixelIndex] >> 16 & 0xff) * transparency;
                int otherGreen = (pixels[pixelIndex] >> 8 & 0xff) * transparency;
                int otherBlue = (pixels[pixelIndex] & 0xff) * transparency;
                int transparentColour = ((red + otherRed >> 8) << 16) + ((green + otherGreen >> 8) << 8) + (blue + otherBlue >> 8);
                pixels[pixelIndex++] = transparentColour;
            }
            pixelIndex += leftOver;
        }
    }

    public static void drawPixels(int height, int posY, int posX, int color, int w) {
        if (posX < clip_left) {
            w -= clip_left - posX;
            posX = clip_left;
        }
        if (posY < clip_top) {
            height -= clip_top - posY;
            posY = clip_top;
        }
        if (posX + w > clip_right) {
            w = clip_right - posX;
        }
        if (posY + height > clip_bottom) {
            height = clip_bottom - posY;
        }
        int k1 = width - w;
        int l1 = posX + posY * width;
        for (int i2 = -height; i2 < 0; i2++) {
            for (int j2 = -w; j2 < 0; j2++) {
                pixels[l1++] = color;
            }

            l1 += k1;
        }
    }

    /**
     * Draws a 1 pixel thick box outline in a certain colour.
     * @param x The left edge X-Coordinate.
     * @param y The top edge Y-Coordinate.
     * @param width The width.
     * @param height The height.
     * @param rgbColour The RGB-Colour.
     */
    public static void draw_rect_outline(int x, int y, int width, int height, int rgbColour) {
        draw_horizontal_line(x, y, width, rgbColour);
        draw_horizontal_line(x, (y + height) - 1, width, rgbColour);
        draw_vertical_line(x, y, height, rgbColour);
        draw_vertical_line((x + width) - 1, y, height, rgbColour);
    }

    /**
     * Draws a coloured horizontal line in the drawingArea.
     * @param xPosition The start X-Position of the line.
     * @param yPosition The Y-Position of the line.
     * @param width The width of the line.
     * @param rgbColour The colour of the line.
     */
    public static void draw_horizontal_line(int xPosition, int yPosition, int width, int rgbColour) {
        if (yPosition < clip_top || yPosition >= clip_bottom)
            return;
        if (xPosition < clip_left) {
            width -= clip_left - xPosition;
            xPosition = clip_left;
        }
        if (xPosition + width > clip_right)
            width = clip_right - xPosition;
        int pixelIndex = xPosition + yPosition * Rasterizer2D.width;
        for (int i = 0; i < width; i++)
            pixels[pixelIndex + i] = rgbColour;
    }

    public static void drawStroke(int xPos, int yPos, int width, int height, int color, int strokeWidth) {

        drawVerticalStrokeLine(xPos, yPos, height, color, strokeWidth);
        drawVerticalStrokeLine((xPos + width) - strokeWidth, yPos, height, color, strokeWidth);
        drawHorizontalStrokeLine(xPos, yPos, width, color, strokeWidth);
        drawHorizontalStrokeLine(xPos, (yPos + height) - strokeWidth, width, color, strokeWidth);

    }

    public static void drawHorizontalStrokeLine(int xPos, int yPos, int w, int hexColor, int strokeWidth) {

        if (yPos < clip_top || yPos >= clip_bottom)
            return;
        if (xPos < clip_left) {
            w -= clip_left - xPos;
            xPos = clip_left;
        }
        if (xPos + w > clip_right)
            w = clip_right - xPos;
        int index = xPos + yPos * width;
        int leftWidth = width - w;
        for (int x = 0; x < strokeWidth; x++) {
            for (int y = 0; y < w; y++) {
                pixels[index++] = hexColor;
            }
            index += leftWidth;
        }

    }

    public static void drawVerticalStrokeLine(int xPosition, int yPosition, int height, int hexColor, int strokeWidth) {
        if (xPosition < clip_left || xPosition >= clip_right)
            return;
        if (yPosition < clip_top) {
            height -= clip_top - yPosition;
            yPosition = clip_top;
        }
        if (yPosition + height > clip_bottom)
            height = clip_bottom - yPosition;
        int pixelIndex = xPosition + yPosition * width;
        for (int rowIndex = 0; rowIndex < height; rowIndex++) {
            for (int x = 0; x < strokeWidth; x++) {
                pixels[pixelIndex + x + rowIndex * width] = hexColor;
            }
        }
    }

    /**
     * Draws a coloured vertical line in the drawingArea.
     * @param xPosition The X-Position of the line.
     * @param yPosition The start Y-Position of the line.
     * @param height The height of the line.
     * @param rgbColour The colour of the line.
     */
    public static void draw_vertical_line(int xPosition, int yPosition, int height, int rgbColour) {
        if (xPosition < clip_left || xPosition >= clip_right)
            return;
        if (yPosition < clip_top) {
            height -= clip_top - yPosition;
            yPosition = clip_top;
        }
        if (yPosition + height > clip_bottom)
            height = clip_bottom - yPosition;
        int pixelIndex = xPosition + yPosition * width;
        for (int rowIndex = 0; rowIndex < height; rowIndex++)
            pixels[pixelIndex + rowIndex * width] = rgbColour;
    }

    public static void drawHorizontalLine(int x, int y, int length, int color, int alpha) {
        if (y < clip_top || y >= clip_bottom) {
            return;
        }
        if (x < clip_left) {
            length -= clip_left - x;
            x = clip_left;
        }
        if (x + length > clip_right) {
            length = clip_right - x;
        }
        final int j1 = 256 - alpha;
        final int k1 = (color >> 16 & 0xff) * alpha;
        final int l1 = (color >> 8 & 0xff) * alpha;
        final int i2 = (color & 0xff) * alpha;
        int i3 = x + y * width;
        for (int j3 = 0; j3 < length; j3++) {
            final int j2 = (pixels[i3] >> 16 & 0xff) * j1;
            final int k2 = (pixels[i3] >> 8 & 0xff) * j1;
            final int l2 = (pixels[i3] & 0xff) * j1;
            final int k3 = (k1 + j2 >> 8 << 16) + (l1 + k2 >> 8 << 8) + (i2 + l2 >> 8);
            pixels[i3++] = k3;
        }
    }

    public static void draw_line(int i, int j, int k, int l)
    {
        if (i < clip_top || i >= clip_bottom)
            return;
        if (l < clip_left)
        {
            k -= clip_left - l;
            l = clip_left;
        }
        if (l + k > clip_right)
            k = clip_right - l;
        int i1 = l + i * width;
        for (int j1 = 0; j1 < k; j1++)
            pixels[i1 + j1] = j;

    }

    public static void drawAlphaBox(int x, int y, int lineWidth, int lineHeight, int color, int alpha) {
        if (y < clip_top) {
            if (y > (clip_top - lineHeight)) {
                lineHeight -= (clip_top - y);
                y += (clip_top - y);
            } else {
                return;
            }
        }
        if (y + lineHeight > clip_bottom) {
            lineHeight -= y + lineHeight - clip_bottom;
        }
        //if (y >= bottomY - lineHeight)
        //return;
        if (x < clip_left) {
            lineWidth -= clip_left - x;
            x = clip_left;
        }
        if (x + lineWidth > clip_right)
            lineWidth = clip_right - x;
        for(int yOff = 0; yOff < lineHeight; yOff++) {
            int i3 = x + (y + (yOff)) * width;
            for (int j3 = 0; j3 < lineWidth; j3++) {
                //int alpha2 = (lineWidth-j3) / (lineWidth/alpha);
                int j1 = 256 - alpha;//alpha2 is for gradient
                int k1 = (color >> 16 & 0xff) * alpha;
                int l1 = (color >> 8 & 0xff) * alpha;
                int i2 = (color & 0xff) * alpha;
                int j2 = (pixels[i3] >> 16 & 0xff) * j1;
                int k2 = (pixels[i3] >> 8 & 0xff) * j1;
                int l2 = (pixels[i3] & 0xff) * j1;
                int k3 = ((k1 + j2 >> 8) << 16) + ((l1 + k2 >> 8) << 8)
                    + (i2 + l2 >> 8);
                pixels[i3++] = k3;
            }
        }
    }

    /**
     * Draws a 1 pixel thick transparent box outline in a certain colour.
     * @param leftX The left edge X-Coordinate
     * @param topY The top edge Y-Coordinate.
     * @param width The width.
     * @param height The height.
     * @param rgbColour The RGB-Colour.
     * @param opacity The opacity value ranging from 0 to 256.
     */
    public static void drawTransparentBoxOutline(int leftX, int topY, int width, int height, int rgbColour, int opacity) {
        drawTransparentHorizontalLine(leftX, topY, width, rgbColour, opacity);
        drawTransparentHorizontalLine(leftX, topY + height - 1, width, rgbColour, opacity);
        if (height >= 3) {
            drawTransparentVerticalLine(leftX, topY + 1, height - 2, rgbColour, opacity);
            drawTransparentVerticalLine(leftX + width - 1, topY + 1, height - 2, rgbColour, opacity);
        }
    }

    /**
     * Draws a transparent coloured horizontal line in the drawingArea.
     * @param xPosition The start X-Position of the line.
     * @param yPosition The Y-Position of the line.
     * @param width The width of the line.
     * @param rgbColour The colour of the line.
     * @param opacity The opacity value ranging from 0 to 256.
     */
    public static void drawTransparentHorizontalLine(int xPosition, int yPosition, int width, int rgbColour, int opacity) {
        if (yPosition < clip_top || yPosition >= clip_bottom) {
            return;
        }
        if (xPosition < clip_left) {
            width -= clip_left - xPosition;
            xPosition = clip_left;
        }
        if (xPosition + width > clip_right) {
            width = clip_right - xPosition;
        }
        final int transparency = 256 - opacity;
        final int red = (rgbColour >> 16 & 0xff) * opacity;
        final int green = (rgbColour >> 8 & 0xff) * opacity;
        final int blue = (rgbColour & 0xff) * opacity;
        int pixelIndex = xPosition + yPosition * Rasterizer2D.width;
        for (int i = 0; i < width; i++) {
            final int otherRed = (pixels[pixelIndex] >> 16 & 0xff) * transparency;
            final int otherGreen = (pixels[pixelIndex] >> 8 & 0xff) * transparency;
            final int otherBlue = (pixels[pixelIndex] & 0xff) * transparency;
            final int transparentColour = (red + otherRed >> 8 << 16) + (green + otherGreen >> 8 << 8) + (blue + otherBlue >> 8);
            pixels[pixelIndex++] = transparentColour;
        }
    }

    /**
     * Draws a transparent coloured vertical line in the drawingArea.
     * @param xPosition The X-Position of the line.
     * @param yPosition The start Y-Position of the line.
     * @param height The height of the line.
     * @param rgbColour The colour of the line.
     * @param opacity The opacity value ranging from 0 to 256.
     */
    public static void drawTransparentVerticalLine(int xPosition, int yPosition, int height, int rgbColour, int opacity) {
        if (xPosition < clip_left || xPosition >= clip_right) {
            return;
        }
        if (yPosition < clip_top) {
            height -= clip_top - yPosition;
            yPosition = clip_top;
        }
        if (yPosition + height > clip_bottom) {
            height = clip_bottom - yPosition;
        }
        final int transparency = 256 - opacity;
        final int red = (rgbColour >> 16 & 0xff) * opacity;
        final int green = (rgbColour >> 8 & 0xff) * opacity;
        final int blue = (rgbColour & 0xff) * opacity;
        int pixelIndex = xPosition + yPosition * width;
        for (int i = 0; i < height; i++) {
            final int otherRed = (pixels[pixelIndex] >> 16 & 0xff) * transparency;
            final int otherGreen = (pixels[pixelIndex] >> 8 & 0xff) * transparency;
            final int otherBlue = (pixels[pixelIndex] & 0xff) * transparency;
            final int transparentColour = (red + otherRed >> 8 << 16) + (green + otherGreen >> 8 << 8) + (blue + otherBlue >> 8);
            pixels[pixelIndex] = transparentColour;
            pixelIndex += width;
        }
    }

    public static Graphics2D createGraphics(boolean renderingHints) {
        Graphics2D g2d = createGraphics(pixels, width, height);
        if (renderingHints) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        return g2d;
    }

    public static Graphics2D createGraphics(int[] pixels, int width, int height) {
        return new BufferedImage(COLOR_MODEL, Raster.createWritableRaster(COLOR_MODEL.createCompatibleSampleModel(width, height), new DataBufferInt(pixels, width * height), null), false, new Hashtable<Object, Object>()).createGraphics();
    }

    public static Shape createSector(int x, int y, int r, int angle) {
        return new Arc2D.Double(x, y, r, r, 90, -angle, Arc2D.PIE);
    }

    public static Shape createCircle(int x, int y, int r) {
        return new Ellipse2D.Double(x, y, r, r);
    }

    public static Shape createRing(Shape sector, Shape innerCircle) {
        Area ring = new Area(sector);
        ring.subtract(new Area(innerCircle));
        return ring;
    }

    public static void drawFilledCircle(int x, int y, int radius, int color, int alpha) {
        int y1 = y - radius;
        if (y1 < 0) {
            y1 = 0;
        }
        int y2 = y + radius;
        if (y2 >= height) {
            y2 = height - 1;
        }
        int a2 = 256 - alpha;
        int r1 = (color >> 16 & 0xff) * alpha;
        int g1 = (color >> 8 & 0xff) * alpha;
        int b1 = (color & 0xff) * alpha;
        for (int iy = y1; iy <= y2; iy++) {
            int dy = iy - y;
            int dist = (int) Math.sqrt(radius * radius - dy * dy);
            int x1 = x - dist;
            if (x1 < 0) {
                x1 = 0;
            }
            int x2 = x + dist;
            if (x2 >= width) {
                x2 = width - 1;
            }
            int pos = x1 + iy * width;
            for (int ix = x1; ix <= x2; ix++) {
                /*  Tried replacing all pixels[pos] with:
                    Client.instance.gameScreenImageProducer.canvasRaster[pos]
                    AND Rasterizer3D.pixels[pos] */
                int r2 = (pixels[pos] >> 16 & 0xff) * a2;
                int g2 = (pixels[pos] >> 8 & 0xff) * a2;
                int b2 = (pixels[pos] & 0xff) * a2;
                pixels[pos++] = ((r1 + r2 >> 8) << 16) + ((g1 + g2 >> 8) << 8) + (b1 + b2 >> 8);
            }
        }
    }

    public static void drawRectangle(int x, int y, int width, int height, int color, int alpha) {
        drawHorizontalLine(x, y, width, color, alpha);
        drawHorizontalLine(x, y + height - 1, width, color, alpha);
        if(height >= 3) {
            drawVerticalLine(x, y + 1, height - 2, color, alpha);
            drawVerticalLine(x + width - 1, y + 1, height - 2, color, alpha);
        }
    }

    public static void drawVerticalLine(int x, int y, int length, int color, int alpha) {
        if(x < clip_left || x >= clip_right) {
            return;
        }
        if(y < clip_top) {
            length -= clip_top - y;
            y = clip_top;
        }
        if(y + length > clip_bottom) {
            length = clip_bottom - y;
        }
        final int j1 = 256 - alpha;
        final int k1 = (color >> 16 & 0xff) * alpha;
        final int l1 = (color >> 8 & 0xff) * alpha;
        final int i2 = (color & 0xff) * alpha;
        int i3 = x + y * width;
        for(int j3 = 0; j3 < length; j3++) {
            final int j2 = (pixels[i3] >> 16 & 0xff) * j1;
            final int k2 = (pixels[i3] >> 8 & 0xff) * j1;
            final int l2 = (pixels[i3] & 0xff) * j1;
            final int k3 = (k1 + j2 >> 8 << 16) + (l1 + k2 >> 8 << 8) + (i2 + l2 >> 8);
            pixels[i3] = k3;
            i3 += width;
        }
    }

    public static void fillRectangle(int x, int y, int w, int h, int color) {
        if (x < clip_left) {
            w -= clip_left - x;
            x = clip_left;
        }
        if (y < clip_top) {
            h -= clip_top - y;
            y = clip_top;
        }
        if (x + w > clip_right) {
            w = clip_right - x;
        }
        if (y + h > clip_bottom) {
            h = clip_bottom - y;
        }
        int k1 = width - w;
        int l1 = x + y * width;
        for (int i2 = -h; i2 < 0; i2++) {
            for (int j2 = -w; j2 < 0; j2++) {
                pixels[l1++] = color;
            }
            l1 += k1;
        }
    }

    public static void fillRectangle(int x, int y, int w, int h, int color, int alpha) {
        if (x < clip_left) {
            w -= clip_left - x;
            x = clip_left;
        }
        if (y < clip_top) {
            h -= clip_top - y;
            y = clip_top;
        }
        if (x + w > clip_right) {
            w = clip_right - x;
        }
        if (y + h > clip_bottom) {
            h = clip_bottom - y;
        }
        int a2 = 256 - alpha;
        int r1 = (color >> 16 & 0xff) * alpha;
        int g1 = (color >> 8 & 0xff) * alpha;
        int b1 = (color & 0xff) * alpha;
        int k3 = width - w;
        int pixel = x + y * width;
        for (int i4 = 0; i4 < h; i4++) {
            for (int j4 = -w; j4 < 0; j4++) {
                int r2 = (pixels[pixel] >> 16 & 0xff) * a2;
                int g2 = (pixels[pixel] >> 8 & 0xff) * a2;
                int b2 = (pixels[pixel] & 0xff) * a2;
                int rgb = ((r1 + r2 >> 8) << 16) + ((g1 + g2 >> 8) << 8) + (b1 + b2 >> 8);
                pixels[pixel++] = rgb;
            }
            pixel += k3;
        }
    }

    public static final void drawAlphaCircle(final int x, int y, int radius, final int color, final int alpha) {
        if (alpha != 0) {
            if (alpha == 256) {
                drawCircle(x, y, radius, color);
            } else {
                if (radius < 0) {
                    radius = -radius;
                }
                final int opacity = 256 - alpha;
                final int source_red = (color >> 16 & 0xff) * alpha;
                final int source_green = (color >> 8 & 0xff) * alpha;
                final int source_blue = (color & 0xff) * alpha;
                int diameter_start = y - radius;
                if (diameter_start < clip_top) {
                    diameter_start = clip_top;
                }
                int diameter_end = y + radius + 1;
                if (diameter_end > clip_bottom) {
                    diameter_end = clip_bottom;
                }
                int i_26_ = diameter_start;
                final int i_27_ = radius * radius;
                int i_28_ = 0;
                int i_29_ = y - i_26_;
                int i_30_ = i_29_ * i_29_;
                int i_31_ = i_30_ - i_29_;
                if (y > diameter_end) {
                    y = diameter_end;
                }
                while (i_26_ < y) {
                    for (/**/; i_31_ <= i_27_ || i_30_ <= i_27_; i_31_ += i_28_++ + i_28_) {
                        i_30_ += i_28_ + i_28_;
                    }
                    int i_32_ = x - i_28_ + 1;
                    if (i_32_ < clip_left) {
                        i_32_ = clip_left;
                    }
                    int i_33_ = x + i_28_;
                    if (i_33_ > clip_right) {
                        i_33_ = clip_right;
                    }
                    int coordinates = i_32_ + i_26_ * width;
                    for (int i_35_ = i_32_; i_35_ < i_33_; i_35_++) {
                        final int dest_red = (pixels[coordinates] >> 16 & 0xff) * opacity;
                        final int dest_green = (pixels[coordinates] >> 8 & 0xff) * opacity;
                        final int dest_blue = (pixels[coordinates] & 0xff) * opacity;
                        final int dest_color = (source_red + dest_red >> 8 << 16) + (source_green + dest_green >> 8 << 8) + (source_blue + dest_blue >> 8);
                        pixels[coordinates++] = dest_color;
                    }
                    i_26_++;
                    i_30_ -= i_29_-- + i_29_;
                    i_31_ -= i_29_ + i_29_;
                }
                i_28_ = radius;
                i_29_ = -i_29_;
                i_31_ = i_29_ * i_29_ + i_27_;
                i_30_ = i_31_ - i_28_;
                i_31_ -= i_29_;
                while (i_26_ < diameter_end) {
                    for (/**/; i_31_ > i_27_ && i_30_ > i_27_; i_30_ -= i_28_ + i_28_) {
                        i_31_ -= i_28_-- + i_28_;
                    }
                    int i_40_ = x - i_28_;
                    if (i_40_ < clip_left) {
                        i_40_ = clip_left;
                    }
                    int i_41_ = x + i_28_;
                    if (i_41_ > clip_right - 1) {
                        i_41_ = clip_right - 1;
                    }
                    int coordinates = i_40_ + i_26_ * width;
                    for (int i_43_ = i_40_; i_43_ <= i_41_; i_43_++) {
                        final int i_44_ = (pixels[coordinates] >> 16 & 0xff) * opacity;
                        final int i_45_ = (pixels[coordinates] >> 8 & 0xff) * opacity;
                        final int i_46_ = (pixels[coordinates] & 0xff) * opacity;
                        final int i_47_ = (source_red + i_44_ >> 8 << 16) + (source_green + i_45_ >> 8 << 8) + (source_blue + i_46_ >> 8);
                        pixels[coordinates++] = i_47_;
                    }
                    i_26_++;
                    i_31_ += i_29_ + i_29_;
                    i_30_ += i_29_++ + i_29_;
                }
            }
        }
    }

    private static final void setPixel(final int x, final int y, final int color) {
        if (x >= clip_left && y >= clip_top && x < clip_right && y < clip_bottom) {
            pixels[x + y * width] = color;
        }
    }

    private static final void drawCircle(final int x, int y, int radius, final int color) {
        if (radius == 0) {
            setPixel(x, y, color);
        } else {
            if (radius < 0) {
                radius = -radius;
            }
            int i_67_ = y - radius;
            if (i_67_ < clip_top) {
                i_67_ = clip_top;
            }
            int i_68_ = y + radius + 1;
            if (i_68_ > clip_bottom) {
                i_68_ = clip_bottom;
            }
            int i_69_ = i_67_;
            final int i_70_ = radius * radius;
            int i_71_ = 0;
            int i_72_ = y - i_69_;
            int i_73_ = i_72_ * i_72_;
            int i_74_ = i_73_ - i_72_;
            if (y > i_68_) {
                y = i_68_;
            }
            while (i_69_ < y) {
                for (/**/; i_74_ <= i_70_ || i_73_ <= i_70_; i_74_ += i_71_++ + i_71_) {
                    i_73_ += i_71_ + i_71_;
                }
                int i_75_ = x - i_71_ + 1;
                if (i_75_ < clip_left) {
                    i_75_ = clip_left;
                }
                int i_76_ = x + i_71_;
                if (i_76_ > clip_right) {
                    i_76_ = clip_right;
                }
                int i_77_ = i_75_ + i_69_ * width;
                for (int i_78_ = i_75_; i_78_ < i_76_; i_78_++) {
                    pixels[i_77_++] = color;
                }
                i_69_++;
                i_73_ -= i_72_-- + i_72_;
                i_74_ -= i_72_ + i_72_;
            }
            i_71_ = radius;
            i_72_ = i_69_ - y;
            i_74_ = i_72_ * i_72_ + i_70_;
            i_73_ = i_74_ - i_71_;
            i_74_ -= i_72_;
            while (i_69_ < i_68_) {
                for (/**/; i_74_ > i_70_ && i_73_ > i_70_; i_73_ -= i_71_ + i_71_) {
                    i_74_ -= i_71_-- + i_71_;
                }
                int i_79_ = x - i_71_;
                if (i_79_ < clip_left) {
                    i_79_ = clip_left;
                }
                int i_80_ = x + i_71_;
                if (i_80_ > clip_right - 1) {
                    i_80_ = clip_right - 1;
                }
                int i_81_ = i_79_ + i_69_ * width;
                for (int i_82_ = i_79_; i_82_ <= i_80_; i_82_++) {
                    pixels[i_81_++] = color;
                }
                i_69_++;
                i_74_ += i_72_ + i_72_;
                i_73_ += i_72_++ + i_72_;
            }
        }
    }

    public static void draw_rectangle_outline(int x, int y, int line_width, int line_height, int color) {
        draw_vertical_line1(x, y, line_width, color);
        draw_vertical_line1(x, line_height + y - 1, line_width, color);
        draw_horizontal_line1(x, y, line_height, color);
        draw_horizontal_line1(x + line_width - 1, y, line_height, color);
    }

    public static void draw_vertical_line1(int x, int y, int line_width, int color) {
        if (y >= clip_top && y < clip_bottom) {
            if (x < clip_left) {
                line_width -= clip_left - x;
                x = clip_left;
            }
            if (x + line_width > clip_right) {
                line_width = clip_right - x;
            }
            int coordinates = x + width * y;
            for (int step = 0; step < line_width; step++) {
                pixels[coordinates + step] = color;
            }
        }
    }

    public static void draw_horizontal_line1(int x, int y, int line_height, int color) {
        if (x >= clip_left && x < clip_right) {
            if (y < clip_top) {
                line_height -= clip_top - y;
                y = clip_top;
            }
            if (line_height + y > clip_bottom) {
                line_height = clip_bottom - y;
            }
            int coordinates = x + width * y;
            for (int step = 0; step < line_height; step++) {
                pixels[coordinates + step * width] = color;
            }
        }
    }

    public static void draw_line(int x, int y, int line_width, int line_height, int color) {
        line_width -= x;
        line_height -= y;
        if (line_height == 0) {//check for straight lines
            if (line_width >= 0) {
                draw_vertical_line1(x, y, line_width + 1, color);
            } else {
                draw_vertical_line1(x + line_width, y, -line_width + 1, color);
            }

        } else if (line_width == 0) {//check for straight lines
            if (line_height >= 0) {
                draw_horizontal_line1(x, y, line_height + 1, color);
            } else {
                draw_horizontal_line1(x, line_height + y, -line_height + 1, color);
            }
        } else {
            //bresenham algorithm?
            if (line_height + line_width < 0) {
                x += line_width;
                line_width = -line_width;
                y += line_height;
                line_height = -line_height;
            }
            int height_step;
            int width_step;
            if (line_width > line_height) {
                y <<= 16;
                y += 16384;
                line_height <<= 16;
                height_step = (int) Math.floor((double) line_height / (double) line_width + 0.5D);
                line_width += x;
                if (x < clip_left) {
                    y += height_step * (clip_left - x);
                    x = clip_left;
                }
                if (line_width >= clip_right) {
                    line_width = clip_right - 1;
                }
                while (x <= line_width) {
                    width_step = y >> 16;
                    if (width_step >= clip_top && width_step < clip_bottom) {
                        pixels[x + width_step * width] = color;
                    }
                    y += height_step;
                    x++;
                }
            } else {
                x <<= 16;
                x += 16384;
                line_width <<= 16;
                height_step = (int) Math.floor((double) line_width / (double) line_height + 0.5D);
                line_height += y;
                if (y < clip_top) {
                    x += (clip_top - y) * height_step;
                    y = clip_top;
                }
                if (line_height >= clip_bottom) {
                    line_height = clip_bottom - 1;
                }
                while (y <= line_height) {
                    width_step = x >> 16;
                    if (width_step >= clip_left && width_step < clip_right) {
                        pixels[width_step + width * y] = color;
                    }
                    x += height_step;
                    y++;
                }
            }
        }
    }

    private static final ColorModel COLOR_MODEL = new DirectColorModel(32, 0xff0000, 0xff00, 0xff);

    public static int[] pixels;
    public static int width;
    public static int height;
    public static int clip_top;
    public static int clip_bottom;
    public static int clip_left;
    public static int clip_right;
    public static int center_x;
    public static int viewport_center_y;
    public static int viewport_center_x;
    public static float[] depth_buffer;

    static {
        clip_top = 0;
        clip_bottom = 0;
        clip_left = 0;
        clip_right = 0;
    }

    public static void transparentBox(int i, int j, int k, int l, int i1, int opac) {
        int j3 = 256 - opac;
        if (k < Rasterizer2D.clip_left) {
            i1 -= Rasterizer2D.clip_left - k;
            k = Rasterizer2D.clip_left;
        }

        if (j < clip_top) {
            i -= clip_top - j;
            j = clip_top;
        }

        if (k + i1 > clip_right) {
            i1 = clip_right - k;
        }

        if (j + i > clip_bottom) {
            i = clip_bottom - j;
        }

        int k1 = width - i1;
        int l1 = k + j * width;

        for(int i2 = -i; i2 < 0; ++i2) {
            for(int j2 = -i1; j2 < 0; ++j2) {
                int i3 = pixels[l1];
                pixels[l1++] = ((l & 16711935) * opac + (i3 & 16711935) * j3 & -16711936) + ((l & '\uff00') * opac + (i3 & '\uff00') * j3 & 16711680) >> 8;
            }

            l1 += k1;
        }
    }

    public static void fillPixels(int i, int j, int k, int l, int i1) {
        method339(i1, l, j, i);
        method339((i1 + k) - 1, l, j, i);
        method341(i1, l, k, i);
        method341(i1, l, k, (i + j) - 1);
    }

    public static void method339(int i, int j, int k, int l) {
        if (i < clip_top || i >= clip_bottom)
            return;
        if (l < clip_left) {
            k -= clip_left - l;
            l = clip_left;
        }
        if (l + k > clip_right)
            k = clip_right - l;
        int i1 = l + i * width;
        for (int j1 = 0; j1 < k; j1++)
            pixels[i1 + j1] = j;

    }

    public static void method341(int i, int j, int k, int l) {
        if (l < clip_left || l >= clip_right)
            return;
        if (i < clip_top) {
            k -= clip_top - i;
            i = clip_top;
        }
        if (i + k > clip_bottom)
            k = clip_bottom - i;
        int j1 = l + i * width;
        for (int k1 = 0; k1 < k; k1++)
            pixels[j1 + k1 * width] = j;

    }
}
