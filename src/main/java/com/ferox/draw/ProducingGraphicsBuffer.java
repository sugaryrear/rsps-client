package com.ferox.draw;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public final class ProducingGraphicsBuffer {

    public final int[] canvasRaster;
    public final int canvasWidth;
    public final int canvasHeight;
    private float[] depthbuffer;
    private final BufferedImage bufferedImage;

    public ProducingGraphicsBuffer(int canvasWidth, int canvasHeight) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        depthbuffer = new float [canvasWidth * canvasHeight];
        bufferedImage = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB);
        canvasRaster = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();
        init();
    }

    public void drawGraphics(int x, Graphics graphics, int y) {
        graphics.drawImage(bufferedImage, y, x, null);
    }

    public void init() {
        Rasterizer2D.init(canvasWidth, canvasHeight, canvasRaster, depthbuffer);
    }
}
