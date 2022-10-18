package com.ferox.cache.graphics;

import static java.nio.file.StandardOpenOption.READ;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

import javax.imageio.ImageIO;

public final class SpriteCache implements Closeable {

    public static SimpleImage[] cache;

    private FileChannel dataChannel;
    private FileChannel metaChannel;

    public void init(File dataFile, File metaFile) throws IOException {
        if (!dataFile.exists()) {
            throw new IOException(String.format("Could not find data file=%s", dataFile.getName()));
        }

        if (!metaFile.exists()) {
            throw new IOException(String.format("Could not find meta file=%s", metaFile.getName()));
        }

        dataChannel = FileChannel.open(dataFile.toPath(), READ);
        metaChannel = FileChannel.open(metaFile.toPath(), READ);

        final int spriteCount = (int) (metaChannel.size() / 10);

        cache = new SimpleImage[spriteCount];
    }

    public SimpleImage get(int id) {
        try {
            if (contains(id)) {
                return cache[id];
            }

            if (!dataChannel.isOpen() || !metaChannel.isOpen()) {
                System.err.println("Sprite channels are closed!");
                return null;
            }

            final int entries = (int) (metaChannel.size() / 10);

            if (id > entries) {
                System.err.printf("id=%d > size=%d%n", id, entries);
                return null;
            }

            metaChannel.position(id * 10L);

            final ByteBuffer metaBuf = ByteBuffer.allocate(10);
            metaChannel.read(metaBuf);
            metaBuf.flip();

            final int pos = ((metaBuf.get() & 0xFF) << 16) | ((metaBuf.get() & 0xFF) << 8) | (metaBuf.get() & 0xFF);
            final int len = ((metaBuf.get() & 0xFF) << 16) | ((metaBuf.get() & 0xFF) << 8) | (metaBuf.get() & 0xFF);
            final int offsetX = metaBuf.getShort() & 0xFF;
            final int offsetY = metaBuf.getShort() & 0xFF;

            final ByteBuffer dataBuf = ByteBuffer.allocate(len);

            dataChannel.position(pos);
            dataChannel.read(dataBuf);
            dataBuf.flip();

            try (InputStream is = new ByteArrayInputStream(dataBuf.array())) {

                BufferedImage bimage = ImageIO.read(is);

                if (bimage == null) {
                    System.err.printf("Could not read image at %d%n", id);
                    return null;
                }

                if (bimage.getType() != BufferedImage.TYPE_INT_ARGB) {
                    bimage = convert(bimage);
                }

                final int[] pixels = ((DataBufferInt) bimage.getRaster().getDataBuffer()).getData();

                final SimpleImage sprite = new SimpleImage(bimage.getWidth(), bimage.getHeight(), offsetX, offsetY, pixels);

                // cache so we don't have to perform I/O calls again
                cache[id] = sprite;

                return sprite;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.err.printf("No sprite found for id=%d%n", id);
        return null;
    }

    public void draw(int id, int x, int y) {
        draw(id, x, y, false);
    }

    public void draw(int id, int x, int y, int alpha, boolean advanced) {
        SimpleImage sprite = get(id);
        if (sprite != null) {
            if (advanced) {
                sprite.drawAdvancedSprite(x, y, alpha);
            } else {
                sprite.drawSprite(x, y, alpha);
            }
        }
    }

    public void draw(int id, int x, int y, boolean advanced) {
        SimpleImage sprite = get(id);
        if (sprite != null) {
            if (advanced) {
                sprite.drawAdvancedSprite(x, y);
            } else {
                sprite.drawSprite(x, y);
            }
        }
    }

    public boolean contains(int id) {
        return id < cache.length && cache[id] != null;
    }

    public void set(int id, SimpleImage sprite) {
        if (!contains(id)) {
            return;
        }

        cache[id] = sprite;
    }

    public void clear() {
        Arrays.fill(cache, null);
    }

    private static BufferedImage convert(BufferedImage bimage) {
        BufferedImage converted = new BufferedImage(bimage.getWidth(), bimage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        converted.getGraphics().drawImage(bimage, 0, 0, null);
        return converted;
    }

    public void close() throws IOException {
        if(dataChannel != null) {
            dataChannel.close();
        }
        if(metaChannel != null) {
            metaChannel.close();
        }
    }

}
