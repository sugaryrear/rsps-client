package com.ferox.cache.graphics;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import com.ferox.io.Buffer;
import com.ferox.sign.SignLink;
import com.ferox.util.FileUtils;

import static com.ferox.util.FileUtils.writeFile;

public final class SpecialBarSpriteLoader {

    public static SpecialBarSpriteLoader[] cache;
    private static SimpleImage[] sprites = null;
    public String name;
    public int id;
    public int drawOffsetX;
    public int drawOffsetY;
    public byte[] spriteData;

    public SpecialBarSpriteLoader() {
        name = "Unknown";
        id = -1;
        drawOffsetX = 0;
        drawOffsetY = 0;
        spriteData = null;
    }

    public static void loadSprites() {
        try {

            Buffer index = new Buffer(FileUtils.readFile(SignLink.findCacheDir() + "special_bar_sprite.idx"));
            Buffer data = new Buffer(FileUtils.readFile(SignLink.findCacheDir() + "special_bar_sprite.dat"));

            DataInputStream indexFile = new DataInputStream(new GZIPInputStream(new ByteArrayInputStream(index.payload)));
            DataInputStream dataFile = new DataInputStream(new GZIPInputStream(new ByteArrayInputStream(data.payload)));

            int totalSprites = indexFile.readInt();
            if (cache == null) {
                cache = new SpecialBarSpriteLoader[totalSprites];
                sprites = new SimpleImage[totalSprites];
            }
            for (int sprite_index = 0; sprite_index < totalSprites; sprite_index++) {
                int id = indexFile.readInt();
                if (cache[id] == null) {
                    cache[id] = new SpecialBarSpriteLoader();
                }
                cache[id].readValues(indexFile, dataFile);
                createSprite(cache[id]);
            }
            indexFile.close();
            dataFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readValues(DataInputStream index, DataInputStream data) throws IOException {
        do {
            int opcode = data.readByte();
            if (opcode == 0) {
                break;
            }
            if (opcode == 1) {
                id = data.readShort();
            } else if (opcode == 2) {
                name = data.readUTF();
            } else if (opcode == 3) {
                drawOffsetX = data.readShort();
            } else if (opcode == 4) {
                drawOffsetY = data.readShort();
            } else if (opcode == 5) {
                int indexLength = index.readInt();
                byte[] data_read = new byte[indexLength];
                data.readFully(data_read);
                spriteData = data_read;
            }
        } while (true);
    }

    private static boolean DUMP_SPRITES = false;

    private static void createSprite(SpecialBarSpriteLoader sprite) {
        if (DUMP_SPRITES) {
            File directory = new File(SignLink.findCacheDir() + "sprites_dump");
            if (!directory.exists()) {
                directory.mkdir();
            }
            //System.out.println("Dumped: " + directory.getAbsolutePath() + System.getProperty("file.separator") + sprite.id + ".png");
            writeFile(new File(directory.getAbsolutePath() + System.getProperty("file.separator") + sprite.id + ".png"), sprite.spriteData);
        }
        sprites[sprite.id] = new SimpleImage(sprite.spriteData);
        sprites[sprite.id].x_offset = sprite.drawOffsetX;
        sprites[sprite.id].y_offset = sprite.drawOffsetY;
    }

    public static SimpleImage[] getSprites() {
        return sprites;
    }

}
