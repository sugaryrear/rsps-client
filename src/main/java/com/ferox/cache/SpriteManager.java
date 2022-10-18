package com.ferox.cache;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.ferox.cache.def.SpriteDefinition;
import com.ferox.cache.def.loaders.SpriteLoader;
import com.ferox.cache.def.provider.SpriteProvider;

import java.io.IOException;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-02-14
 */
public class SpriteManager implements SpriteProvider {

    private final Archive fileArchive;
    private final Multimap<Integer, SpriteDefinition> sprites = LinkedListMultimap.create();

    public SpriteManager(Archive fileArchive) {
        this.fileArchive = fileArchive;
    }

    public void load() throws IOException
    {

        SpriteLoader loader = new SpriteLoader();
        SpriteDefinition[] defs = loader.load(317, fileArchive.get("mapscene.dat"));

        for (SpriteDefinition sprite : defs)
            sprites.put(sprite.getId(), sprite);

    }

    @Override
    public SpriteDefinition provide(int spriteId, int frameId) {
        return findSprite(spriteId, frameId);
    }

    private SpriteDefinition findSprite(int spriteId, int frameId) {

        for (SpriteDefinition sprite : sprites.get(spriteId)) {
            if (sprite.getFrame() == frameId)
                return sprite;
        }

        return null;
    }
}
