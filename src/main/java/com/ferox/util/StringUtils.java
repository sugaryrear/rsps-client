package com.ferox.util;

import java.util.regex.Pattern;

public final class StringUtils {

    public static final Pattern VALID_NAME = Pattern.compile("^[a-zA-Z0-9_ ]{3,12}$");

    private static final char[] BASE_37_CHARACTERS = {'_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    public static long encodeBase37(String string) {
        long encoded = 0L;
        for (int index = 0; index < string.length() && index < 12; index++) {
            char c = string.charAt(index);
            encoded *= 37L;
            if (c >= 'A' && c <= 'Z')
                encoded += (1 + c) - 65;
            else if (c >= 'a' && c <= 'z')
                encoded += (1 + c) - 97;
            else if (c >= '0' && c <= '9')
                encoded += (27 + c) - 48;
        }

        for (; encoded % 37L == 0L && encoded != 0L; encoded /= 37L)
            ;

        return encoded;
    }

    public static String decodeBase37(long encoded) {
        try {
            if (encoded <= 0L || encoded >= 0x5b5b57f8a98a5dd1L)
                return "invalid_name";
            if (encoded % 37L == 0L)
                return "invalid_name";
            int length = 0;
            char[] chars = new char[12];
            while (encoded != 0L) {
                long l1 = encoded;
                encoded /= 37L;
                char c = BASE_37_CHARACTERS[(int) (l1 - encoded * 37L)];
                chars[11 - length++] = c;
            }
            return new String(chars, 12 - length, length);
        } catch (RuntimeException runtimeexception) {
            runtimeexception.printStackTrace();
            System.out.println("81570, " + encoded + ", " + (byte) -99 + ", " + runtimeexception.toString());
        }
        throw new RuntimeException();
    }

    /**
     * Capitalizes any characters succeeding a space otherwise we don't worry about any casing.
     * @param string
     * @return
     */
    public static String capitalizeIf(String string) {
        string = string.trim();
        if (string.trim().length() < 1)
            return "";
        byte[] data = string.getBytes();
        if (data == null || data.length < 1)
            return "";
        for (int i = 0; i < string.length(); i++) {
            byte b = data[i];
            if (b == 95 || b == 32) {
                if (b == 95)
                    data[i] = 32;
                byte next = data[i + 1];
                if (next >= 97 && next <= 122) {
                    data[i + 1] = (byte) (next - 32);
                }
            }
        }
        if (data[0] >= 97 && data[0] <= 122)
            data[0] = (byte) (data[0] - 32);
        return new String(data, 0, string.length());
    }

    public static long hashSpriteName(String name) {
        name = name.toUpperCase();
        long hash = 0L;
        for (int index = 0; index < name.length(); index++) {
            hash = (hash * 61L + (long) name.charAt(index)) - 32L;
            hash = hash + (hash >> 56) & 0xffffffffffffffL;
        }
        return hash;
    }

    /**
     * Used to format a users ip address on the welcome screen.
     */
    public static String decodeIp(int ip) {
        return (ip >> 24 & 0xff) + "." + (ip >> 16 & 0xff) + "." + (ip >> 8 & 0xff) + "." + (ip & 0xff);
    }

    public static String capitalize(String text) {
        if (text == null)
            return text;
        for (int text_length = 0; text_length < text.length(); text_length++) {
            if (text_length == 0) {
                text = String.format("%s%s", Character.toUpperCase(text.charAt(0)), text.substring(1));
            }
            if (!Character.isLetterOrDigit(text.charAt(text_length))) {
                if (text_length + 1 < text.length()) {
                    text = String.format("%s%s%s", text.subSequence(0, text_length + 1),  Character.toUpperCase(text.charAt(text_length + 1)), text.substring(text_length + 2));
                }
            }
        }
        return text;
    }

    /**
     * Used to format a players name.
     */
    public static String formatText(String t) {
        if (t.length() > 0) {
            char chars[] = t.toCharArray();
            for (int index = 0; index < chars.length; index++)
                if (chars[index] == '_') {
                    chars[index] = ' ';
                    if (index + 1 < chars.length && chars[index + 1] >= 'a' && chars[index + 1] <= 'z') {
                        chars[index + 1] = (char) ((chars[index + 1] + 65) - 97);
                    }
                }

            if (chars[0] >= 'a' && chars[0] <= 'z') {
                chars[0] = (char) ((chars[0] + 65) - 97);
            }
            return new String(chars);
        } else {
            return t;
        }
    }

    public static String insertCommasToNumber(String number) {
        return number.length() < 4 ? number : insertCommasToNumber(number
                .substring(0, number.length() - 3))
                + ","
                + number.substring(number.length() - 3, number.length());
    }

    /**
     * Used for the login screen to hide a users password
     */
    public static String passwordAsterisks(String password) {
        StringBuffer stringbuffer = new StringBuffer();
        for (int index = 0; index < password.length(); index++)
            stringbuffer.append("*");
        return stringbuffer.toString();
    }

    public static String toAsterisks(String s) {
        StringBuilder result = new StringBuilder();
        for (int j = 0; j < s.length(); j++) {
            result.append("*");
        }
        return result.toString();
    }
}
