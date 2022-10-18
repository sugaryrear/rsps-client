package com.ferox.util;

import java.lang.reflect.Method;

public final class Utils {

    public static String replaceIcons(String text) {
        if (text.contains("<img=")) {
            int prefix = text.indexOf("<img=");
            int suffix = text.indexOf(">");
            long occurrences = text.chars().filter(ch -> ch == '>').count();
            if (suffix < prefix) {
                for (int i = 0; i < occurrences; i++) {
                    suffix = Utils.nthOccurrence(text, ">", i);
                    if (suffix > prefix) break;
                }
            }
            text = text.replaceAll(text.substring(prefix + 5, suffix), "");
            text = text.replaceAll("</img>", "");
            text = text.replaceAll("<img=>", "");
        } else if (text.contains("<ico=")) {
            int prefix = text.indexOf("<ico=");
            int suffix = text.indexOf(">");
            long occurrences = text.chars().filter(ch -> ch == '>').count();
            if (suffix < prefix) {
                for (int i = 0; i < occurrences; i++) {
                    suffix = Utils.nthOccurrence(text, ">", i);
                    if (suffix > prefix) break;
                }
            }
            text = text.replaceAll(text.substring(prefix + 5, suffix), "");
            text = text.replaceAll("</ico>", "");
            text = text.replaceAll("<ico=>", "");
        }
        return text;
    }

    public static int ensureRange(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }

    public static int random(int min, int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }

    public static int random(int range) {
        return (int)(java.lang.Math.random() * (range+1));
    }

    public static int nthOccurrence(String str1, String str2, int n) {

        String tempStr = str1;
        int tempIndex = -1;
        int finalIndex = 0;
        for(int occurrence = 0; occurrence < n ; ++occurrence){
            tempIndex = tempStr.indexOf(str2);
            if(tempIndex==-1){
                finalIndex = 0;
                break;
            }
            tempStr = tempStr.substring(++tempIndex);
            finalIndex+=tempIndex;
        }
        return --finalIndex;
    }

    public static int[] d2Tod1(int[][] array) {
        int[] newArray = new int[array.length*array[0].length];

        for (int i = 0; i < array.length; ++i)
            for (int j = 0; j < array[i].length; ++j) {
                newArray[i*array[0].length+j] = array[i][j];
            }

        return newArray;
    }

    public static int[][] d1Tod2(int[] array, int width) {
        int[][] newArray = new int[array.length/width][width];

        for (int i = 0; i < array.length; ++i) {
            newArray[i/width][i%width] = array[i];
        }

        return newArray;
    }

    public static void launchURL(String url) {
        String osName = System.getProperty("os.name");
        try {
            if (osName.startsWith("Mac OS")) {
                Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
                Method openURL = fileMgr.getDeclaredMethod("openURL",
                        new Class[] {String.class});
                openURL.invoke(null, new Object[] {url});
            } else if (osName.startsWith("Windows"))
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            else {
                String[] browsers = {"firefox", "opera", "konqueror", "epiphany", "mozilla",
                        "netscape", "safari"};
                String browser = null;
                for (int count = 0; count < browsers.length && browser == null; count++)
                    if (Runtime.getRuntime().exec(new String[] {"which", browsers[count]})
                            .waitFor() == 0)
                        browser = browsers[count];
                if (browser == null) {
                    throw new Exception("Could not find web browser");
                } else
                    Runtime.getRuntime().exec(new String[] {browser, url});
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static final char[] validChars = { '_', 'a', 'b', 'c', 'd', 'e',
            'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
            's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4',
            '5', '6', '7', '8', '9' };

    public static long longForName(String s) {
        long l = 0L;
        for (int i = 0; i < s.length() && i < 12; i++) {
            char c = s.charAt(i);
            l *= 37L;
            if (c >= 'A' && c <= 'Z') {
                l += 1 + c - 65;
            } else if (c >= 'a' && c <= 'z') {
                l += 1 + c - 97;
            } else if (c >= '0' && c <= '9') {
                l += 27 + c - 48;
            }
        }

        for (; l % 37L == 0L && l != 0L; l /= 37L) {
            ;
        }
        return l;
    }

    public static String nameForLong(long l) {
        try {
            if (l <= 0L || l >= 0x5b5b57f8a98a5dd1L) {
                return "invalid_name";
            }

            if (l % 37L == 0L) {
                return "invalid_name";
            }

            int i = 0;
            char ac[] = new char[12];

            while (l != 0L) {
                long l1 = l;
                l /= 37L;
                ac[11 - i++] = validChars[(int) (l1 - l * 37L)];
            }

            return new String(ac, 12 - i, i);
        } catch (RuntimeException runtimeexception) {
            System.err.println("81570, " + l + ", " + (byte) -99 + ", " + runtimeexception.toString());
        }

        throw new RuntimeException();
    }

    public static String fixName(String s) {
        if (s.length() > 0) {
            char ac[] = s.toCharArray();
            for (int j = 0; j < ac.length; j++) {
                if (ac[j] == '_') {
                    ac[j] = ' ';
                    if (j + 1 < ac.length && ac[j + 1] >= 'a' && ac[j + 1] <= 'z') {
                        ac[j + 1] = (char) (ac[j + 1] + 65 - 97);
                    }
                }
            }

            if (ac[0] >= 'a' && ac[0] <= 'z') {
                ac[0] = (char) (ac[0] + 65 - 97);
            }
            return new String(ac);
        } else {
            return s;
        }
    }

}
