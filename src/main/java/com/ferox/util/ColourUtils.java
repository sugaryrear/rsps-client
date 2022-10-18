package com.ferox.util;

import java.awt.Color;

import org.apache.commons.lang3.ArrayUtils;



public class ColourUtils {

    public static int getAlpha(int rgb) {
        return rgb >> 24 & 0xFF;
    }

    public static int getARGB(int rgb) {
        return getRGB(getRed(rgb), getGreen(rgb), getBlue(rgb));
    }

    public static int getBlue(int rgb) {
        return rgb & 0xFF;
    }

    public static int getGreen(int rgb) {
        return rgb >> 8 & 0xFF;
    }

    public static int getRed(int rgb) {
        return rgb >> 16 & 0xFF;
    }

    public static int getRGB(int argb) {
        return (getRed(argb) & 0xFF) << 16 | (getGreen(argb) & 0xFF) << 8 | (getBlue(argb) & 0xFF) << 0;
    }

    public static int getRGB(int red, int green, int blue) {
        return getRGB(0xFF, red, green, blue);
    }

    public static int getRGB(int alpha, int red, int green, int blue) {
        return alpha << 24 | (red & 0xFF) << 16 | (green & 0xFF) << 8 | (blue & 0xFF) << 0;
    }

    public static int stripAlpha(int rgb) {
        return rgb & 0xFFFFFF;
    }
    public static int addAlpha(int rgb, int alpha) {
        return getRGB(alpha, getRed(rgb), getGreen(rgb), getBlue(rgb));
    }


    /**
     * Encodes the hue, saturation, and luminance into a hsl value.
     *
     * @param hue
     *            The hue.
     * @param saturation
     *            The saturation.
     * @param luminance
     *            The luminance.
     * @return The colour.
     */
    public final static int toHsl(int hue, int saturation, int luminance) {
        if (luminance > 179) {
            saturation /= 2;
        }
        if (luminance > 192) {
            saturation /= 2;
        }
        if (luminance > 217) {
            saturation /= 2;
        }
        if (luminance > 243) {
            saturation /= 2;
        }

        return (hue / 4 << 10) + (saturation / 32 << 7) + luminance / 2;
    }

    public final static int checkedLight(int colour, int light) {
        if (colour == -2)
            return 0xbc614e;

        if (colour == -1) {
            if (light < 0) {
                light = 0;
            } else if (light > 127) {
                light = 127;
            }
            return 127 - light;
        }

        light = light * (colour & 0x7f) / 128;
        if (light < 2) {
            light = 2;
        } else if (light > 126) {
            light = 126;
        }
        return (colour & 0xff80) + light;
    }

    public static int exponent(int colour, double exponent) {
        int alpha = (colour >> 24 & 0xff);
        double r = (colour >> 16 & 0xff) / 256D;
        double g = (colour >> 8 & 0xff) / 256D;
        double b = (colour & 0xff) / 256D;

        r = Math.pow(r, exponent);
        g = Math.pow(g, exponent);
        b = Math.pow(b, exponent);

        int newR = (int) (r * 256D);
        int newG = (int) (g * 256D);
        int newB = (int) (b * 256D);
        return (alpha << 24) + (newR << 16) + (newG << 8) + newB;
    }

    public static int adjustBrightness(int colour, double brightness) {
        if(brightness == 1.0)
            brightness = 0.9999;
        int alpha = (colour >> 24 & 0xff);
        double r = (colour >> 16 & 0xff);
        double g = (colour >> 8 & 0xff);
        double b = (colour & 0xff);

        r = r * brightness;
        g = g * brightness;
        b = b * brightness;

        int newR = (int) (r);
        int newG = (int) (g);
        int newB = (int) (b);
        return (alpha << 24) + (newR << 16) + (newG << 8) + newB;
    }



    //    public static int RGB_to_RS2HSB(int rgb) {
//
//            double double_0 = (rgb >> 16 & 0xFF) / 256.0D;
//            double double_1 = (rgb >> 8 & 0xFF) / 256.0D;
//            double double_2 = (rgb & 0xFF) / 256.0D;
//            double double_3 = double_0;
//            if (double_1 < double_0) {
//                double_3 = double_1;
//            }
//
//            if (double_2 < double_3) {
//                double_3 = double_2;
//            }
//
//            double double_4 = double_0;
//            if (double_1 > double_0) {
//                double_4 = double_1;
//            }
//
//            if (double_2 > double_4) {
//                double_4 = double_2;
//            }
//
//            double double_5 = 0.0D;
//            double double_6 = 0.0D;
//            double double_7 = (double_3 + double_4) / 2.0D;
//            if (double_3 != double_4) {
//                if (double_7 < 0.5D) {
//                    double_6 = (double_4 - double_3) / (double_4 + double_3);
//                }
//
//                if (double_7 >= 0.5D) {
//                    double_6 = (double_4 - double_3) / (2.0D - double_4 - double_3);
//                }
//
//                if (double_4 == double_0) {
//                    double_5 = (double_1 - double_2) / (double_4 - double_3);
//                } else if (double_4 == double_1) {
//                    double_5 = (double_2 - double_0) / (double_4 - double_3) + 2.0D;
//                } else if (double_2 == double_4) {
//                    double_5 = 4.0D + (double_0 - double_1) / (double_4 - double_3);
//                }
//            }
//
//            double_5 /= 6.0D;
//            int int_1 = (int) (double_5 * 256.0D);
//            int int_2 = (int) (256.0D * double_6);
//            int int_3 = (int) (256.0D * double_7);
//            if (int_2 < 0) {
//                int_2 = 0;
//            } else if (int_2 > 255) {
//                int_2 = 255;
//            }
//
//            if (int_3 < 0) {
//                int_3 = 0;
//            } else if (int_3 > 255) {
//                int_3 = 255;
//            }
//
//            if (int_3 > 243) {
//                int_2 >>= 4;
//            } else if (int_3 > 217) {
//                int_2 >>= 3;
//            } else if (int_3 > 192) {
//                int_2 >>= 2;
//            } else if (int_3 > 179) {
//                int_2 >>= 1;
//            }
//
//            return ((int_1 & 0xFF) >> 2 << 10) + (int_2 >> 5 << 7) + (int_3 >> 1);
//        }
    public static int RGB_to_RS2HSB(int colour) {
        //int idx = rgbIndex(colour);
        //if(idx != -1)
        //	return idx;
        int red = (colour >> 16 & 0xff);
        int green = (colour >> 8 & 0xff);
        int blue = (colour & 0xff);
        float[] HSB = Color.RGBtoHSB(red, green, blue, null);
        float hue = (HSB[0]);
        float saturation = (HSB[1]);
        float brightness = (HSB[2]);
        int encode_hue = (int) (hue * 63);			//to 6-bits
        int encode_saturation = (int) (saturation * 7);		//to 3-bits
        int encode_brightness = (int) (brightness * 127); 	//to 7-bits
        return (encode_hue << 10) + (encode_saturation << 7) + (encode_brightness);
    }
    public static int RS2HSB_to_RGB(int RS2HSB) {
        int decode_hue = (RS2HSB >> 10) & 0x3f;
        int decode_saturation = (RS2HSB >> 7) & 0x07;
        int decode_brightness = (RS2HSB & 0x7f);
        return Color.HSBtoRGB((float)decode_hue/63, (float)decode_saturation/7, (float)decode_brightness/127);
    }

    public static int[] getARGB(int[] pixels) {
        for(int i = 0;i<pixels.length;i++) {
            pixels[i] = addAlpha(pixels[i], 254);
        }
        return pixels;
    }

}
