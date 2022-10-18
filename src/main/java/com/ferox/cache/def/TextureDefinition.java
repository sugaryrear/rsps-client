/*
 * Copyright (c) 2016-2017, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.ferox.cache.def;

import com.ferox.cache.def.provider.SpriteProvider;
import com.ferox.draw.Rasterizer3D;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-02-14
 */
public class TextureDefinition {

    private int id;
    private int[] fileIds;

    public transient int[] pixels;
    public int[] field1780;
    public int[] field1781;
    public int[] field1786;

    public boolean renderHQ;
    public int averageRGBColor;
    public int field1782;
    public int field1783;

    public void rasterize(int brightness, int width, SpriteProvider spriteProvider) {

        int pixelCount = width * width;
        this.pixels = new int[pixelCount];

        for (int fileId = 0; fileId < this.fileIds.length; ++fileId) {

            final SpriteDefinition spriteDefinition = spriteProvider.provide(fileIds[fileId], 0);
            spriteDefinition.normalize();

            byte[] pixelIdx = spriteDefinition.pixelIdx;
            int[] texturePalette = spriteDefinition.palette;
            int var10 = this.field1786[fileId];

            int var11;
            int var12;
            int x;
            int y;
            if ((var10 & -16777216) == 50331648)
            {
                var11 = var10 & 16711935;
                var12 = var10 >> 8 & 255;

                for (x = 0; x < texturePalette.length; ++x)
                {
                    y = texturePalette[x];
                    if (y >> 8 == (y & 65535))
                    {
                        y &= 255;
                        texturePalette[x] = var11 * y >> 8 & 16711935 | var12 * y & 65280;
                    }
                }
            }

            for (var11 = 0; var11 < texturePalette.length; ++var11)
                texturePalette[var11] = Rasterizer3D.adjust_brightness(texturePalette[var11], brightness);


            if (fileId == 0)
                var11 = 0;
            else
                var11 = this.field1780[fileId - 1];

            if (var11 == 0) {
                if (width == spriteDefinition.getMaxWidth()) {
                    for (var12 = 0; var12 < pixelCount; ++var12)
                        this.pixels[var12] = texturePalette[pixelIdx[var12] & 255];
                }
                else if (spriteDefinition.getMaxWidth() == 64 && width == 128) {
                    var12 = 0;
                    for (x = 0; x < width; ++x) {
                        for (y = 0; y < width; ++y) {
                            this.pixels[var12++] = texturePalette[pixelIdx[(x >> 1 << 6) + (y >> 1)] & 255];
                        }
                    }
                }
                else {
                    if (spriteDefinition.getMaxWidth() != 128 || width != 64)
                        throw new RuntimeException();

                    var12 = 0;

                    for (x = 0; x < width; ++x) {
                        for (y = 0; y < width; ++y) {
                            this.pixels[var12++] = texturePalette[pixelIdx[(y << 1) + (x << 1 << 7)] & 255];
                        }
                    }
                }
            }
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setFileIds(int[] files) {
        this.fileIds = files;
    }

}
