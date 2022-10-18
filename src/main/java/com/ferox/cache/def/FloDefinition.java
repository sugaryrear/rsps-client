package com.ferox.cache.def;

import com.ferox.Client;
import com.ferox.ClientConstants;
import com.ferox.cache.Archive;
import com.ferox.util.FileUtils;

import java.nio.ByteBuffer;

public class FloDefinition {

    public static void init(Archive archive) {
        ByteBuffer buffer = ClientConstants.LOAD_OSRS_DATA_FROM_CACHE_DIR ? ByteBuffer.wrap(FileUtils.read(ClientConstants.DATA_DIR+"/floors/flo.dat")) : ByteBuffer.wrap(archive.get("flo.dat"));
        underlay_length = buffer.getShort();
        underlay = new FloDefinition[underlay_length];
        for(int index = 0; index < underlay_length; index++) {
            if(underlay[index] == null)
                underlay[index] = new FloDefinition();

            underlay[index].decode(buffer, 0);
            underlay[index].generate();
        }

        overlay_length = buffer.getShort();
        overlay = new FloDefinition[overlay_length];
        for(int index = 0; index < overlay_length; index++) {
            if(overlay[index] == null)
                overlay[index] = new FloDefinition();

            overlay[index].decode(buffer, 1);
            overlay[index].generate();
        }

        System.out.println("Flo read -> (" + underlay_length + " underlays) | (" + overlay_length + " overlays)");
    }

    public void decode(ByteBuffer buffer, int flag) {
        if(flag == 0) {
            do {
                int opcode = buffer.get();
                if (opcode == 0) {
                    break;
                } else if (opcode == 1) {
                    minimap_underlay = ((buffer.get() & 0xff) << 16) + ((buffer.get() & 0xff) << 8) + (buffer.get() & 0xff);
                } else {
                    System.out.println("Error unrecognised {FLO-underlay} opcode: " + opcode);
                }
            } while(true);
        }
        if(flag == 1) {
            do {
                int opcode = buffer.get();
                if (opcode == 0) {
                    break;
                } else if (opcode == 1) {
                    minimap_underlay = ((buffer.get() & 0xff) << 16) + ((buffer.get() & 0xff) << 8) + (buffer.get() & 0xff);
                } else if (opcode == 2) {
                    texture = buffer.get() & 0xff;
                } else if (opcode == 5) {
                    hidden = false;
                } else if (opcode == 7) {
                    minimap_overlay = ((buffer.get() & 0xff) << 16) + ((buffer.get() & 0xff) << 8) + (buffer.get() & 0xff);
                } else {
                    System.out.println("Error unrecognised {FLO-overlay} opcode: " + opcode);
                }
            } while(true);
        }
    }

    private void generate() {
        if (minimap_overlay != -1) {
            convert(minimap_overlay);
            overlay_hue = underlay_hue;
            overlay_saturation = underlay_saturation;
            overlay_luminance = underlay_luminance;
        }
        if (Client.singleton.setting.ground_snow) {
            //Heavy snow (Nearly every brown/green area turns white
            if (this.minimap_underlay == 0x35720A || this.minimap_underlay == 0x58680B || this.minimap_underlay == 0x78680B || this.minimap_underlay == 0x6CAC10 || this.minimap_underlay == 0x819531 || this.minimap_underlay == 0x38562F || this.minimap_underlay == 0x276D27 || this.minimap_underlay == 0x396215

                || this.minimap_underlay == 0x33501E || this.minimap_underlay == 0x64BB75 ||
                //start of browns
                this.minimap_underlay == 0x3D2B0B || this.minimap_underlay == 0xAC9058 || this.minimap_underlay == 0xD9BB93 || this.minimap_underlay == 0xB09058 || //
                this.minimap_underlay == 0x302020 || this.minimap_underlay == 0x604040 || this.minimap_underlay == 0x906830 || this.minimap_underlay == 0x33501E || //
                this.minimap_underlay == 0x64BB75 || this.minimap_underlay == 0x604040 || this.minimap_underlay == 0x807048 || this.minimap_underlay == 0x544828 || //
                this.minimap_underlay == 0xB09058 || this.minimap_underlay == 0x78680B || this.minimap_underlay == 0x858210 || //
                //this.minimap_underlay == 0x644E1E || this.minimap_underlay == 0x654D0B || this.minimap_underlay == 0x664411 || this.minimap_underlay == 0x644E1E || //
                this.minimap_underlay == 0x654D0B || this.minimap_underlay == 0x4B3E14 || this.minimap_underlay == 0x5C543C || this.minimap_underlay == 0x946B03 || this.minimap_underlay == 0xC08048) {
                this.minimap_underlay = 0xEdEdEd;
            }

            //Medium snow (Leaves brown colours, only covers greens
            if (this.minimap_underlay == 0x35720A || this.minimap_underlay == 0x58680B || this.minimap_underlay == 0x6CAC10 || this.minimap_underlay == 0x38562F || this.minimap_underlay == 0x276D27 || this.minimap_underlay == 0x396215 || this.minimap_underlay == 0x33501E || this.minimap_underlay == 0x64BB75) {
                this.minimap_underlay = 0xd9d9d9;
            }

            //White edge, patchy frost everywhere else, tried to replicate osrs areas such as varrock and lum.
            if (this.minimap_underlay == 0x644E1E || this.minimap_underlay == 0x35720A || this.minimap_underlay == 0x50680B || this.minimap_underlay == 0x78680B || this.minimap_underlay == 0x6CAC10 || this.minimap_underlay == 0x819531) {
                this.minimap_underlay = 0xd9d9d9;
            }

            // Other dark spots in the wilderness.
            if (this.minimap_underlay == 0x7A7880 || this.minimap_underlay == 0x383830 || this.minimap_underlay == 0x282820 || this.minimap_underlay == 0x282018 || this.minimap_underlay == 0x504840 || this.minimap_underlay == 0x484038 || this.minimap_underlay == 0x383828 || this.minimap_underlay == 0x003838 || this.minimap_underlay == 0x303525 || this.minimap_underlay == 0x282848 || this.minimap_underlay == 0x183018 || this.minimap_underlay == 0x4D4D4D || this.minimap_underlay == 0x2E2E2E || this.minimap_underlay == 0x3D2B0B || this.minimap_underlay == 0x030303 || this.minimap_underlay == 0x464034 || this.minimap_underlay == 0x4B3E14 || this.minimap_underlay == 0x2F2B1F || this.minimap_underlay == 0x111E1A || this.minimap_underlay == 0x333333 || this.minimap_underlay == 0x2B2E2D || this.minimap_underlay == 0x300000 || this.minimap_underlay == 0x3C2823 || this.minimap_underlay == 0x404040 || this.minimap_underlay == 0x383838 || this.minimap_underlay == 0x303030 || this.minimap_underlay == 0x282828 || this.minimap_underlay == 0x202020 || this.minimap_underlay == 0x181818

            ) {
                this.minimap_underlay = 0xd9d9d9;
            }
            //colours to leave:
            //this.minimap_underlay == 0x38562F
        }
        convert(minimap_underlay);
    }

    private void convert(int rgb) {
        double r = (rgb >> 16 & 0xff) / 256.0;
        double g = (rgb >> 8 & 0xff) / 256.0;
        double b = (rgb & 0xff) / 256.0;
        double min = r;
        if (g < min) {
            min = g;
        }
        if (b < min) {
            min = b;
        }
        double max = r;
        if (g > max) {
            max = g;
        }
        if (b > max) {
            max = b;
        }
        double h = 0.0;
        double s = 0.0;
        double l = (min + max) / 2.0;
        if (min != max) {
            if (l < 0.5) {
                s = (max - min) / (max + min);
            }
            if (l >= 0.5) {
                s = (max - min) / (2.0 - max - min);
            }
            if (r == max) {
                h = (g - b) / (max - min);
            } else if (g == max) {
                h = 2.0 + (b - r) / (max - min);
            } else if (b == max) {
                h = 4.0 + (r - g) / (max - min);
            }
        }
        h /= 6.0;
        underlay_hue = (int) (h * 256.0);
        underlay_saturation = (int) (s * 256.0);
        underlay_luminance = (int) (l * 256.0);
        if (underlay_saturation < 0) {
            underlay_saturation = 0;
        } else if (underlay_saturation > 255) {
            underlay_saturation = 255;
        }
        if (underlay_luminance < 0) {
            underlay_luminance = 0;
        } else if (underlay_luminance > 255) {
            underlay_luminance = 255;
        }
        if (l > 0.5) {
            chroma = (int) ((1.0 - l) * s * 512.0);
        } else {
            chroma = (int) (l * s * 512.0);
        }
        if (chroma < 1) {
            chroma = 1;
        }
        weighted_hue = (int) (h * chroma);
        color = convert24to16(underlay_hue, underlay_saturation, underlay_luminance);
    }

    private final static int convert24to16(int h, int s, int l) {
        if (l > 179) {
            s /= 2;
        }
        if (l > 192) {
            s /= 2;
        }
        if (l > 217) {
            s /= 2;
        }
        if (l > 243) {
            s /= 2;
        }
        return (h / 4 << 10) + (s / 32 << 7) + l / 2;
    }

    private FloDefinition() {
        texture = -1;
        hidden = true;
    }

    public static int underlay_length;
    public static int overlay_length;

    public static FloDefinition[] overlay;
    public static FloDefinition[] underlay;

    public int texture;
    public int minimap_underlay;
    public boolean hidden;
    public int minimap_overlay;

    public int underlay_hue;
    public int underlay_saturation;
    public int underlay_luminance;

    public int overlay_hue;
    public int overlay_saturation;
    public int overlay_luminance;

    public int weighted_hue;
    public int chroma;
    public int color;

}
