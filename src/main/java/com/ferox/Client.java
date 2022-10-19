package com.ferox;

import com.ferox.cache.Archive;
import com.ferox.cache.FileStore;
import com.ferox.cache.FileStore.Store;
import com.ferox.cache.anim.Animation;
import com.ferox.cache.anim.Sequence;
import com.ferox.cache.anim.SpotAnimation;
import com.ferox.cache.config.VariableBits;
import com.ferox.cache.config.VariableParameter;
import com.ferox.cache.def.*;
import com.ferox.cache.def.loaders.SpriteLoader;
import com.ferox.cache.factory.ItemSpriteFactory;
import com.ferox.cache.graphics.*;
import com.ferox.cache.graphics.dropdown.DropdownMenu;
import com.ferox.cache.graphics.fading_screen.BlackFadingScreen;
import com.ferox.cache.graphics.fading_screen.FadingScreen;
import com.ferox.cache.graphics.font.AdvancedFont;
import com.ferox.cache.graphics.widget.*;
import com.ferox.cache.graphics.widget.impl.OptionTabWidget;
import com.ferox.cache.graphics.widget.impl.TeleportWidget;
import com.ferox.cache.graphics.widget.impl.WeaponInterfacesWidget;
import com.ferox.collection.LinkedList;
import com.ferox.collection.Node;
import com.ferox.draw.ProducingGraphicsBuffer;
import com.ferox.draw.Rasterizer2D;
import com.ferox.draw.Rasterizer3D;
import com.ferox.entity.*;
import com.ferox.entity.model.IdentityKit;
import com.ferox.entity.model.Model;
import com.ferox.io.Buffer;
import com.ferox.io.PacketSender;
import com.ferox.model.EffectTimer;
import com.ferox.model.content.*;
import com.ferox.model.content.account.AccountManager;
import com.ferox.model.content.hoverMenu.HoverMenuManager;
import com.ferox.model.content.prayer.PrayerSystem;
import com.ferox.model.content.prayer.Save;
import com.ferox.model.settings.Settings;
import com.ferox.net.BufferedConnection;
import com.ferox.net.IsaacCipher;
import com.ferox.net.ServerToClientPackets;
import com.ferox.net.requester.Resource;
import com.ferox.net.requester.ResourceProvider;
import com.ferox.scene.*;
import com.ferox.scene.object.GroundDecoration;
import com.ferox.scene.object.SpawnedObject;
import com.ferox.scene.object.Wall;
import com.ferox.scene.object.WallDecoration;
import com.ferox.sign.SignLink;
import com.ferox.sound.SoundConstants;
import com.ferox.sound.SoundPlayer;
import com.ferox.sound.Track;
import com.ferox.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.*;
import java.lang.reflect.Method;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.ferox.model.settings.Settings.ESCAPE_CONFIG_BUTTON;

public class Client extends GameApplet {

    public static int npcPetId = -1;

    private AccountManager accountManager;

    private final StatusBars bars = new StatusBars();

    /**
     * Speed of camera rotation.
     */
    public static String cameraSpeed = "SLOW";

    private final List<ImageDescription> teleportImageDescriptions = new ArrayList<>();

    private String getImageDescription(int index) {
        ImageDescription desc = null;
        if (teleportCategoryIndex == 1 || teleportCategoryIndex == 2) {
            if (this.teleportSprites == null) {
                return null;
            }

            if (index >= this.teleportSprites.length) {
                return null;
            }

            for (int i = 0; i < teleportImageDescriptions.size(); i++) {
                desc = teleportImageDescriptions.get(i);

                if (desc.getSpriteID() == teleportSprites[index]) {
                    //System.out.println("Found match: " + desc.getDescription());
                    return desc.getDescription();
                }
            }

        } else {
            for (int i = 0; i < teleportImageDescriptions.size(); i++) {
                desc = teleportImageDescriptions.get(i);
                if (desc.getCategoryIndex() == teleportCategoryIndex) {
                    if (desc.getImageIndex() == index) {
                        return desc.getDescription();
                    }
                }
            }
        }

        return null;
    }

    public int[] teleportSprites = null;
    public String[] teleportNames = null;

    public int teleportCategoryIndex = 1;

    private void loadImageDescriptions() {
        Path path = Paths.get(SignLink.findCacheDir() + "imagedescriptions.txt");
        //Given an example of 999>3>2>And Even More Text, we know a few things.
        //We know that 999 is the sprite id, and it is the 2nd teleport of the 3rd category.
        if (!path.toFile().exists()) {
            return;
        }
        try (Stream<String> lines = Files.lines(path)) {
            lines.forEach(line -> {
                //We could have any delimiter here, we could have > or
                //even >>> if we wanted our descriptions to have > in them.
                String[] split = line.split(">");
                int spriteID = Integer.parseInt(split[0]);
                int categoryIndex = Integer.parseInt(split[1]);
                int imageIndex = Integer.parseInt(split[2]);
                String description = split[3];
                teleportImageDescriptions.add(new ImageDescription(spriteID, description, categoryIndex, imageIndex));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] getMACAddress() {
        InetAddress ip;
        try {
            ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            byte[] hwaddress = network.getHardwareAddress();
            return hwaddress == null ? new byte[0] : hwaddress;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    /*private static final SystemInfo info = new SystemInfo();

    public static SystemInfo getSystemInfo() {
        return info;
    }

    public static CachedUUIDGroup getCachedUUIDGroup() {
        return cachedUUIDGroup;
    }

    public static CachedUUIDGroup cachedUUIDGroup;

    public static UniqueIdentifier.UniqueIdentifierSet identifierSet = null;*/
    public static String osName = "";
    public static byte[] addressMac;
    public static String addressUid = "";
    public String tooltip;
    private final ArrayList<IncomingHit> expectedHit;

    public static SimpleImage[] fadingScreenImages = new SimpleImage[8];
    private FadingScreen fadingScreen;

    static boolean debug_packet_info = false; //This is for debugging packet info
    public boolean isDisplayed = true;
    public Announcement broadcast;
    public static String broadcastText;
    public static boolean soundsAreEnabled = true;
    String selectedMsg = "";
    private int current_track_length;
    private long track_timer;
    @SuppressWarnings("unused")
    private int current_track_repeat;

    public void changeColour(int id, int colour) {
        int i19 = colour >> 10 & 0x1f;
        int i22 = colour >> 5 & 0x1f;
        int l24 = colour & 0x1f;
        Widget.cache[id].textColour = (i19 << 19) + (i22 << 11) + (l24 << 3);
    }

    public int getMyPrivilege() {
        return myPrivilege;
    }

    public enum RuneType {
        AIR(556), WATER(555), EARTH(557), FIRE(554), MIND(558), CHAOS(562), DEATH(560), BLOOD(565), COSMIC(564),
        NATURE(561), LAW(563), BODY(559), SOUL(566), ASTRAL(9075), MIST(324), MUD(4698), DUST(4696), LAVA(4699),
        STEAM(4694), SMOKE(4697), WRATH(21880);

        private int id;

        RuneType(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        /**
         * @param rune
         * @return
         */
        public static RuneType forId(int rune) {
            for (RuneType type : RuneType.values()) {
                if (type.getId() == rune) {
                    return type;
                }
            }
            return null;
        }
    }

    private static final int[][] runePouch = new int[][]{{-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}};
    private static final int[][] vengeance = new int[][]{{560, 999999999}, {557, 999999999}, {9075, 999999999}};
    private static final int[][] iceSack = new int[][]{{555, 999999999}, {560, 999999999}, {565, 999999999}};
    private static final int[][] bindSack = new int[][]{{555, 999999999}, {557, 999999999}, {561, 999999999}};
    private static final int[][] snareSack = new int[][]{{555, 999999999}, {557, 999999999}, {561, 999999999}};
    private static final int[][] entangleSack = new int[][]{{555, 999999999}, {557, 999999999}, {561, 999999999}};
    private static final int[][] teleportSack = new int[][]{{563, 999999999}, {562, 999999999}, {560, 999999999}};

    private static void handleRunePouch(String text, int frame) {
        if (frame != 49999)
            return;
        // System.out.println("RP decoding : " + text);
        if (!(text.startsWith("#") && text.endsWith("$"))) {
            return;
        }
        text = text.replace("#", "");
        text = text.replace("$", "");
        String[] runes = text.split("-");
        for (int index = 0; index < runes.length; index++) {
            String[] args = runes[index].split(":");
            // We only want the id and amount if it's not empty, otherwise it will cause a
            // stacktrace trying to parse it.
            if (!args[0].equals("") && !args[1].equals("")) {
                int id = Integer.parseInt(args[0]);
                int amt = Integer.parseInt(args[1]);
                if (id < 0 || amt < 0) {
                    return;
                }
                runePouch[index][0] = id;
                runePouch[index][1] = amt;
            } else {
                // System.out.println("Empty rune pouch rune slot");
                return;
            }
        }
    }

    public enum ScreenMode {
        FIXED, RESIZABLE, FULLSCREEN;
    }

    private int widgetId = 0;
    public List<TradeOpacity> tradeSlot = new ArrayList<>();

    private final SimpleImage[] skill_sprites = new SimpleImage[SkillConstants.SKILL_COUNT];

    // Timers
    public List<EffectTimer> effects_list = new ArrayList<>();

    public void addEffectTimer(EffectTimer et) {

        // Check if exists.. If so, update delay.
        for (EffectTimer timer : effects_list) {
            if (timer.getSprite() == et.getSprite()) {
                timer.setSeconds(et.getSecondsTimer().secondsRemaining());
                return;
            }
        }

        effects_list.add(et);
    }

    public void drawEffectTimers() {
        int yDraw = window_height - 195;
        int xDraw = window_width - 330;
        Iterator<EffectTimer> iterator = effects_list.iterator();
        while (iterator.hasNext()) {
            EffectTimer timer = iterator.next();
            if (timer.getSecondsTimer().finished()) {
                iterator.remove();
                continue;
            }

            SimpleImage sprite = spriteCache.get(timer.getSprite());

            if (sprite != null) {
                sprite.drawAdvancedSprite(xDraw + 12, yDraw);
                adv_font_small.draw(calculateInMinutes(timer.getSecondsTimer().secondsRemaining()) + "", xDraw + 40, yDraw + 13, 0xFF8C00, -1);
                yDraw -= 25;
            }
        }
        tradeSlot.removeIf(TradeOpacity::cycle);
    }

    public static void teleport(int x, int z, int level) {
        String text = "clienttele " + x + " " + z + " " + level;
        Client.singleton.packetSender.sendCommand(text);
    }

    private String calculateInMinutes(int paramInt) {
        int i = (int) Math.floor(paramInt / 60);
        int j = paramInt - i * 60;
        String str1 = "" + i;
        String str2 = "" + j;
        if (j < 10) {
            str2 = "0" + str2;
        }
        if (i < 10) {
            str1 = "0" + str1;
        }
        return str1 + ":" + str2;
    }

    /**
     * Draws information about our current target during combat.
     */

    private final SecondsTimer lobbyTimer = new SecondsTimer();

    private void drawLobbyTimer() {
        if (!lobbyTimer.finished()) {
            //System.out.println("sending timer: "+lobbyTimer.secondsRemaining());
            //Convert to milliseconds
            int timeInMilliseconds = lobbyTimer.secondsRemaining() * 1000;

            //Get minutes and seconds
            long minutes = TimeUnit.MILLISECONDS.toMinutes(timeInMilliseconds);
            timeInMilliseconds -= TimeUnit.MINUTES.toMillis(minutes);
            long seconds = TimeUnit.MILLISECONDS.toSeconds(timeInMilliseconds);

            String timeAsString = seconds > 9 ? "0" + minutes + ":" + seconds : minutes + ":0" + seconds;

            singleton.sendString(timeAsString, 57102);
        }
    }

    public static ScreenMode screen = ScreenMode.FIXED;
    public static int window_width = 765;
    public static int window_height = 503;
    public static int gamescreen_width = 512;
    public static int gamescreen_height = 334;
    public static int zoom_distance = 900;
    public static int camera_pos = 3;
    public static double brightnessState = 0.8;

    // Removes the chat box
    public static boolean showChatComponents = true;

    // When set to false all tab interfaces are hidden
    public static boolean showTabComponents = true;

    public final int[] sound_effect_volume;

    private final NumberFormat format = NumberFormat.getInstance(Locale.US);

    int frameValueW = 765, frameValueH = 503;

    public void frameMode(int width, int height) {
        int maxWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        int maxHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();

        if (width == -1 || height == -1) {
            width = maxWidth;
            height = maxHeight;
        }

        if (width > maxWidth)
            width = maxWidth;
        if (height > maxHeight)
            height = maxHeight;

        if (frameValueW == width && frameValueH == height) {
            return;
        }

        if (width > 765 || height > 503) {
            // Change resizable button to clicked.
            if (zoom_distance < 900) {
                zoom_distance = 900;
            }
            screen = ScreenMode.RESIZABLE;
            window_width = width;
            window_height = height;
            forceWidth = forceHeight = -1;
            camera_pos = 1;
            rebuildFrameSize(screen, width, height);
        } else {
            screen = ScreenMode.FIXED;
            window_width = 765;
            window_height = 503;
            forceWidth = forceHeight = -1;
            zoom_distance = 900;
            camera_pos = 3;
            rebuildFrameSize(screen, 765, 503);
        }

        frameValueW = width;
        frameValueH = height;
        updateScreen();
        showChatComponents = screen == ScreenMode.FIXED ? true : showChatComponents;
        showTabComponents = screen == ScreenMode.FIXED ? true : showTabComponents;
    }

    private void frameMode(ScreenMode screenMode) {
        int width = 765;
        int height = 503;
        if (screen != screenMode) {
            screen = screenMode;
            if (screenMode == ScreenMode.FIXED) {
                window_width = forceWidth = width;
                window_height = forceHeight = height;
                zoom_distance = 900;
            } else if (screenMode == ScreenMode.RESIZABLE) {
                width = 766;
                height = 559;
                window_width = width;
                window_height = height;
                forceWidth = forceHeight = -1;
                zoom_distance = 900;
            } else if (screenMode == ScreenMode.FULLSCREEN) {
                width = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
                height = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
                zoom_distance = 900;
                window_width = super.myWidth = width;
                window_height = super.myHeight = height;
                forceWidth = forceHeight = -1;
            }
            rebuildFrameSize(screenMode, window_width, window_height);
            updateScreen();
        }
        showChatComponents = screenMode == ScreenMode.FIXED ? true : showChatComponents;
        showTabComponents = screenMode == ScreenMode.FIXED ? true : showTabComponents;
    }

    private Stopwatch frameDelay = new Stopwatch();
    private Stopwatch loggedInWatch = new Stopwatch();

    private void updateScreen() {
        if (loggedIn && frameDelay.hasElapsed(2, TimeUnit.SECONDS)) {
            if (frameValueW != window_width || frameValueH != window_height) {
                frameValueW = window_width;
                frameValueH = window_height;
            }
            frameDelay.reset();
        }

        gamescreen_width = screen == ScreenMode.FIXED ? 512 : window_width;
        gamescreen_height = screen == ScreenMode.FIXED ? 334 : window_height;
        if (gameScreenImageProducer == null || gameScreenImageProducer.canvasWidth != gamescreen_width
            || gameScreenImageProducer.canvasHeight != gamescreen_height) {
            gameScreenImageProducer = new ProducingGraphicsBuffer(gamescreen_width, gamescreen_height);
        }
        updateGame();
    }

    /**
     * Cuts a string into more than one line if it exceeds the specified max width.
     *
     * @param font
     * @param string
     * @param maxWidth
     * @return
     */
    public static String[] splitString(AdvancedFont font, String prefix, String string, int maxWidth, boolean ranked) {
        maxWidth -= font.get_width(prefix) + (ranked ? 14 : 0);
        if (font.get_width(prefix + string) + (ranked ? 14 : 0) <= maxWidth) {
            return new String[]{string};
        }
        String line = "";
        String[] cut = new String[2];
        boolean split = false;
        char[] characters = string.toCharArray();
        int space = -1;
        for (int index = 0; index < characters.length; index++) {
            char c = characters[index];
            line += c;
            if (c == ' ') {
                space = index;
            }
            if (!split) {
                if (font.get_width(line) + 10 > maxWidth) {
                    if (space != -1 && characters[index - 1] != ' ') {
                        cut[0] = line.substring(0, space);
                        line = line.substring(space);
                    } else {
                        cut[0] = line;
                        line = "";
                    }
                    split = true;
                }
            }
        }
        if (line.length() > 0) {
            cut[1] = line;
        }
        return cut;
    }

    private void refreshFrameSize() {
        if (window_width != (appletClient() ? getGameComponent().getWidth() : getScreenWidth())) {
            window_width = (appletClient() ? getGameComponent().getWidth() : getScreenWidth());
            gamescreen_width = super.myWidth = window_width;
            updateScreen();
        }
        if (window_height != (appletClient() ? getGameComponent().getHeight() : getScreenHeight())) {
            window_height = (appletClient() ? getGameComponent().getHeight() : getScreenHeight());
            gamescreen_height = super.myHeight = window_height;
            updateScreen();
        }
    }

    public void rebuildFrameSize(ScreenMode screenMode, int width, int height) {
        gamescreen_width = (screenMode == ScreenMode.FIXED) ? 512 : width;
        gamescreen_height = (screenMode == ScreenMode.FIXED) ? 334 : height;
        window_width = width;
        window_height = height;
        singleton.rebuildFrame(width, height, screenMode == ScreenMode.RESIZABLE, screenMode == ScreenMode.FULLSCREEN);
    }

    public static void updateGame() {
        Rasterizer3D.set_clip(window_width, window_height);
        fullscreen_texture_raster = Rasterizer3D.line_offsets;
        Rasterizer3D.set_clip(
            screen == ScreenMode.FIXED ? (chatboxImageProducer != null ? chatboxImageProducer.canvasWidth : 519)
                : window_width,
            screen == ScreenMode.FIXED ? (chatboxImageProducer != null ? chatboxImageProducer.canvasHeight : 165)
                : window_height);
        chatOffsets = Rasterizer3D.line_offsets;
        Rasterizer3D.set_clip(
            screen == ScreenMode.FIXED ? (tabImageProducer != null ? tabImageProducer.canvasWidth : 249)
                : window_width,
            screen == ScreenMode.FIXED ? (tabImageProducer != null ? tabImageProducer.canvasHeight : 335)
                : window_height);
        anIntArray1181 = Rasterizer3D.line_offsets;
        Rasterizer3D.set_clip(gamescreen_width, gamescreen_height);
        viewportOffsets = Rasterizer3D.line_offsets;
        int ai[] = new int[9];
        for (int i8 = 0; i8 < 9; i8++) {
            int k8 = 128 + i8 * 32 + 15;
            int l8 = 600 + k8 * 3;
            int i9 = Rasterizer3D.SINE[k8];
            ai[i8] = l8 * i9 >> 16;
        }
        SceneGraph.set_viewport(500, 800, gamescreen_width, gamescreen_height, ai);
        if (loggedIn) {
            gameScreenImageProducer = new ProducingGraphicsBuffer(gamescreen_width, gamescreen_height);
            singleton.fullGameScreen = new ProducingGraphicsBuffer(window_width, window_height);
        }
    }

    public boolean getMousePositions() {
        if (mouseInRegion(window_width - (window_width <= 1000 ? 240 : 420),
            window_height - (window_width <= 1000 ? 90 : 37), window_width, window_height)) {
            return false;
        }
        if (showChatComponents) {
            if (settings[ConfigUtility.TRANSPARENT_CHAT_BOX_ID] == 1 && screen != ScreenMode.FIXED) {
                if (super.cursor_x > 0 && super.cursor_x < 494 && super.cursor_y > window_height - 175
                    && super.cursor_y < window_height) {
                    return true;
                } else {
                    if (super.cursor_x > 494 && super.cursor_x < 515 && super.cursor_y > window_height - 175
                        && super.cursor_y < window_height) {
                        return false;
                    }
                }
            } else if (settings[ConfigUtility.TRANSPARENT_CHAT_BOX_ID] == 0) {
                if (super.cursor_x > 0 && super.cursor_x < 519 && super.cursor_y > window_height - 175
                    && super.cursor_y < window_height) {
                    return false;
                }
            }
        }
        if (mouseInRegion(window_width - 216, 0, window_width, 172)) {
            return false;
        }
        if (settings[ConfigUtility.SIDE_STONES_ARRANGEMENT_ID] == 0) {
            if (super.cursor_x > 0 && super.cursor_y > 0 && super.cursor_y < window_width
                && super.cursor_y < window_height) {
                if (super.cursor_x >= window_width - 242 && super.cursor_y >= window_height - 335) {
                    return false;
                }
                return true;
            }
            return false;
        }
        if (showTabComponents) {
            if (window_width > 1000) {
                if (super.cursor_x >= window_width - 420 && super.cursor_x <= window_width
                    && super.cursor_y >= window_height - 37 && super.cursor_y <= window_height
                    || super.cursor_x > window_width - 225 && super.cursor_x < window_width
                    && super.cursor_y > window_height - 37 - 274 && super.cursor_y < window_height) {
                    return false;
                }
            } else {
                if (super.cursor_x >= window_width - 210 && super.cursor_x <= window_width
                    && super.cursor_y >= window_height - 74 && super.cursor_y <= window_height
                    || super.cursor_x > window_width - 225 && super.cursor_x < window_width
                    && super.cursor_y > window_height - 74 - 274 && super.cursor_y < window_height) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean mouseInRegion(int x1, int y1, int x2, int y2) {
        if (super.cursor_x >= x1 && super.cursor_x <= x2 && super.cursor_y >= y1 && super.cursor_y <= y2) {
            return true;
        }
        return false;
    }

    public boolean mouseMapPosition() {
        if (super.cursor_x >= window_width - 21 && super.cursor_x <= window_width && super.cursor_y >= 0
            && super.cursor_y <= 21) {
            return false;
        }
        return true;
    }

    private void drawLoadingMessages(int lines, String first, String second) {
        int width = adv_font_regular.get_width(lines == 1 ? first : second);
        int height = second == null ? 25 : 38;

        Rasterizer2D.draw_filled_rect(1, 1, width + 6, height, 0);
        Rasterizer2D.draw_filled_rect(1, 1, width + 6, 1, 0xffffff);
        Rasterizer2D.draw_filled_rect(1, 1, 1, height, 0xffffff);
        Rasterizer2D.draw_filled_rect(1, height, width + 6, 1, 0xffffff);
        Rasterizer2D.draw_filled_rect(width + 6, 1, 1, height, 0xffffff);

        adv_font_regular.draw_centered("<col=ffffff>" + first, width / 2 + 5, 18);
        if (second != null) {
            adv_font_regular.draw_centered("<col=ffffff>" + second, width / 2 + 5, 31);
        }
    }

    private static final long serialVersionUID = 5707517957054703648L;

    private static String intToKOrMilLongName(int i) {
        String s = String.valueOf(i);
        for (int k = s.length() - 3; k > 0; k -= 3)
            s = s.substring(0, k) + "," + s.substring(k);
        if (s.length() > 8)
            s = "<col=475154>" + s.substring(0, s.length() - 8) + " million <col=ffffff>(" + s + ")";
        else if (s.length() > 4)
            s = "<col=65535>" + s.substring(0, s.length() - 4) + "K <col=ffffff>(" + s + ")";
        return " " + s;
    }

    public final String formatCoins(int coins) {
        if (coins >= 0 && coins < 10000)
            return String.valueOf(coins);
        if (coins >= 10000 && coins < 10000000)
            return coins / 1000 + "K";
        if (coins >= 10000000 && coins < 999999999)
            return coins / 1000000 + "M";
        if (coins >= 999999999)
            return "*";
        else
            return "?";
    }

    public static final byte[] ReadFile(String fileName) {
        try {
            byte abyte0[];
            File file = new File(fileName);
            int i = (int) file.length();
            abyte0 = new byte[i];
            DataInputStream datainputstream = new DataInputStream(
                new BufferedInputStream(new FileInputStream(fileName)));
            datainputstream.readFully(abyte0, 0, i);
            datainputstream.close();
            return abyte0;
        } catch (Exception e) {
            e.printStackTrace();
            addReportToServer(e.getMessage());
            return null;
        }
    }

    private void drawInputField(Widget child, int interfaceX, int interfaceY, int x, int y, int width, int height) {
        int clickX = super.click_x, clickY = super.click_y;
        if (screen == ScreenMode.FIXED) {
            if (clickX >= 512 && clickY >= 169) {
                clickX -= 512;
                clickY -= 169;
            }
        }
        for (int row = 0; row < width; row += 12) {
            if (row + 12 > width) {
                row -= 12 - (width - row);
            }
            Rasterizer2D.fillRectangle(x + row, y, 12, 12, 0x363227);
            for (int collumn = 0; collumn < height; collumn += 12) {
                if (collumn + 12 > height) {
                    collumn -= 12 - (height - collumn);
                }
                Rasterizer2D.fillRectangle(x + row, y + collumn, 12, 12, 0x363227);
            }
        }
        for (int top = 0; top < width; top += 8) {
            if (top + 8 > width) {
                top -= 8 - (width - top);
            }
            Rasterizer2D.draw_horizontal_line(x + top, y, 8, 0);
            Rasterizer2D.draw_horizontal_line(x + top, y + height - 1, 8, 0);
        }
        for (int bottom = 0; bottom < height; bottom += 8) {
            if (bottom + 8 > height) {
                bottom -= 8 - (height - bottom);
            }
            Rasterizer2D.draw_vertical_line(x, y + bottom, 8, 0);
            Rasterizer2D.draw_vertical_line(x + width - 1, y + bottom, 8, 0);
        }
        String message = child.defaultText;

        if (!child.invisible) {

            if (adv_font_small.get_width(message) > child.width - 10) {
                message = message.substring(message.length() - (child.width / 10) - 1);
            }
            if (child.displayAsterisks) {
                adv_font_small.draw("" + StringUtils.toAsterisks(message) + (((!child.isInFocus ? 0 : 1) & (game_tick % 40 < 20 ? 1 : 0)) != 0 ? "|" : ""), (x + 4), (y + (height / 2) + 6), child.textColour, true);
            } else {
                adv_font_small.draw("" + message + (((!child.isInFocus ? 0 : 1) & (game_tick % 40 < 20 ? 1 : 0)) != 0 ? "|" : ""), (x + 4), (y + (height / 2) + 6), child.textColour, true);
            }
            if (clickX >= x && clickX <= x + child.width && clickY >= y && clickY <= y + child.height) {
                if (!child.isInFocus && getInputFieldFocusOwner() != child) {
                    if ((super.mouse_button == 1 && !menuOpen)) {
                        Widget.currentInputFieldId = child.id;
                        setInputFieldFocusOwner(child);
                        if (child.defaultText != null && child.defaultText.equals(child.defaultInputFieldText)) {
                            child.defaultText = "";
                        }
                        if (child.defaultText == null) {
                            child.defaultText = "";
                        }
                    }
                }
            }
        }
    }

    public void setInputFieldFocusOwner(Widget owner) {
        for (Widget rsi : Widget.cache)
            if (rsi != null)
                if (rsi == owner)
                    rsi.isInFocus = true;
                else
                    rsi.isInFocus = false;
    }

    public Widget getInputFieldFocusOwner() {
        for (Widget rsi : Widget.cache)
            if (rsi != null)
                if (rsi.isInFocus)
                    return rsi;
        return null;
    }

    public boolean isInputFieldInFocus() {
        for (Widget rsi : Widget.cache)
            if (rsi != null)
                if (rsi.type == 16 && rsi.isInFocus)
                    return true;
        return false;
    }

    public void resetInputFieldFocus() {
        for (Widget rsi : Widget.cache)
            if (rsi != null)
                rsi.isInFocus = false;
        Widget.currentInputFieldId = -1;
    }

    private boolean menuHasAddFriend(int j) {
        if (j < 0)
            return false;
        int k = menuActionTypes[j];
        if (k >= 2000)
            k -= 2000;
        return k == 337;
    }

    private void clearHistory(int chatType) {

        // Stops the opening of the tab
        super.click_x = 0;
        super.click_y = 0;

        // Go through each message, compare its type..
        outerLoop:
        for (int i = 0; i < chatMessages.length; i++) {
            if (chatMessages[i] == null)
                continue;
            if (chatMessages[i].getType() == chatType) {

                // Don't clear this message if it was sent from another staff member.
                if (!chatMessages[i].getName().equalsIgnoreCase(local_player.username)) {
                    for (ChatCrown c : chatMessages[i].getCrowns()) {
                        if (c.isStaff()) {
                            continue outerLoop;
                        }
                    }
                }

                chatMessages[i] = null;
            }
        }
    }

    private final int[] modeNamesX = {26, 86, 150, 212, 286, 349, 427},
        modeNamesY = {158, 158, 153, 153, 153, 153, 158}, channelButtonsX = {5, 71, 137, 203, 269, 335, 404};

    private final String[] modeNames = {"All", "Game", "Public", "Private", "Clan", "Trade", "Report Abuse"};

    public void drawChannelButtons() {
        final int yOffset = screen == ScreenMode.FIXED ? 0 : window_height - 165;
        spriteCache.get(49).drawSprite(0, 143 + yOffset);
        String[] text = {"On", "Friends", "Off", "Hide"};
        int[] textColor = {65280, 0xffff00, 0xff0000, 65535};
        switch (cButtonCPos) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                spriteCache.get(16).drawSprite(channelButtonsX[cButtonCPos], 143 + yOffset);
                break;
        }
        if (cButtonHPos == cButtonCPos) {
            switch (cButtonHPos) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    spriteCache.get(17).drawSprite(channelButtonsX[cButtonHPos], 143 + yOffset);
                    break;
            }
        } else {
            switch (cButtonHPos) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                    spriteCache.get(15).drawSprite(channelButtonsX[cButtonHPos], 143 + yOffset);
                    break;
                case 6:
                    spriteCache.get(18).drawSprite(channelButtonsX[cButtonHPos], 143 + yOffset);
                    break;
            }
        }
        int[] modes = {set_public_channel, privateChatMode, clanChatMode, tradeMode};
        for (int i = 0; i < modeNamesX.length; i++) {
            adv_font_small.draw(modeNames[i], modeNamesX[i], modeNamesY[i] + yOffset, 0xffffff, -1);
        }
        for (int index = 0; index < 4; index++) {
            if (ChannelText.getText(index) == null) {
                return;
            }
            switch (text[modes[index]]) {
                case "On":
                    adv_font_small.draw(text[modes[index]], ChannelText.getText(index).onX, 164 + yOffset, textColor[modes[index]], -1);
                    break;
                case "Friends":
                    adv_font_small.draw(text[modes[index]], ChannelText.getText(index).friendsX, 164 + yOffset, textColor[modes[index]], -1);
                    break;
                case "Off":
                    adv_font_small.draw(text[modes[index]], ChannelText.getText(index).offX, 164 + yOffset, textColor[modes[index]], -1);
                    break;
                case "Hide":
                    adv_font_small.draw(text[modes[index]], ChannelText.getText(index).hideX, 164 + yOffset, textColor[modes[index]], -1);
                    break;
            }
        }
    }

    private enum ChannelText {
        PUBLIC(0, 159, 148, 156, 154),
        PRIVATE(1, 225, 213, 223, -1),
        CLAN(2, 291, 280, 289, -1),
        TRADE(3, 357, 345, 355, -1);

        public int index;
        public int onX, friendsX, offX, hideX;

        ChannelText(int index, int onX, int friendsX, int offX, int hideX) {
            this.index = index;
            this.onX = onX;
            this.friendsX = friendsX;
            this.offX = offX;
            this.hideX = hideX;
        }

        public static ChannelText getText(int index) {
            for (ChannelText channelText : ChannelText.values()) {
                if (channelText.index == index) {
                    return channelText;
                }
            }
            return null;
        }
    }

    private boolean chatStateCheck() {
        return messagePromptRaised || inputDialogState != 0 || clickToContinueString != null || backDialogueId != -1
            || dialogueId != -1;
    }

    private String enter_amount_title = "Enter amount:";
    private String enter_name_title = "Enter name:";
    private String enter_amount_title2 = "";

    private void drawChatArea() {
        boolean fixed = screen == ScreenMode.FIXED;
        boolean transparent_chat_box = settings[ConfigUtility.TRANSPARENT_CHAT_BOX_ID] == 1;
        int yOffset = fixed ? 0 : window_height - 165;
        if (fixed && chatboxImageProducer != null) {
            chatboxImageProducer.init();
        }

        Rasterizer3D.line_offsets = chatOffsets;
        if (chatStateCheck()) {
            spriteCache.get(20).drawSprite(0, yOffset);
        }

        if (showChatComponents) {
            if (settings[ConfigUtility.TRANSPARENT_CHAT_BOX_ID] == 1 && !chatStateCheck() && !fixed) {
                Rasterizer2D.draw_horizontal_line(7, 7 + yOffset, 506, 0x575757);
                Rasterizer2D.drawTransparentGradientBox(7, 7 + yOffset, 506, 135, 0, 0xFFFFFF, 20);
            } else {
                spriteCache.get(20).drawSprite(0, yOffset);
            }
        }

        drawChannelButtons();
        //Only check for the input field if there is an active interface for better performance.
        if (widget_overlay_id != -1 && this.isInputFieldInFocus()) {
            inputString = "[Click chat box to enable]";
        } else {
            //In case the user clicks the minimap, reset the input string.
            if (inputString.equals("[Click chat box to enable]")) {
                inputString = "";
            }
        }

        //In case the user clicks the chatbox, reset the input string.
        if (super.click_x >= 0 && super.click_x <= 522
            && super.click_y >= (fixed ? 343 : window_height - 165)
            && super.click_y <= (fixed ? 484 : window_height - 27)) {
            if (!this.isInputFieldInFocus() && inputString.equals("[Click chat box to enable]")) {
                inputString = "";
                this.resetInputFieldFocus();
            }
            if (ClientConstants.SPAWN_TAB_DISPLAY_ALL_ITEMS_PRELOADED) {
                SpawnTabAllItems.searchingSpawnTab = false;
            } else {
                SpawnTab.searchingSpawnTab = false;
            }
        }

        if (messagePromptRaised) {
            adv_font_bold.draw_centered(inputMessage, 259, 60 + yOffset, 0, -1);
            adv_font_bold.draw_centered(promptInput + "*", 259, 80 + yOffset, 128, -1);
        } else if (inputDialogState == 3 || inputDialogState == 4) {
            adv_font_bold.draw_centered(enter_amount_title, 259, yOffset + 40, 0, -1);
            adv_font_bold.draw_centered(enter_amount_title2, 259, yOffset + 55, 0, -1);
            adv_font_bold.draw_centered(amountOrNameInput + "*", 259, 80 + yOffset, 128, -1);
        } else if (inputDialogState == 1) {
            adv_font_bold.draw_centered(enter_amount_title, 259, yOffset + 60, 0, -1);
            adv_font_bold.draw_centered(amountOrNameInput + "*", 259, 80 + yOffset, 128, -1);
        } else if (inputDialogState == 2) {
            adv_font_bold.draw_centered(enter_name_title, 259, 60 + yOffset, 0, -1);
            adv_font_bold.draw_centered(amountOrNameInput + "*", 259, 80 + yOffset, 128, -1);
        } else if (clickToContinueString != null) {
            adv_font_bold.draw_centered(clickToContinueString, 259, 60 + yOffset, 0, -1);
            adv_font_bold.draw_centered("Click to continue", 259, 80 + yOffset, 128, -1);
        } else if (backDialogueId != -1) {
            try {
                drawInterface(Widget.cache[backDialogueId], 20, 20 + yOffset, 0);
            } catch (Exception e) {
                e.printStackTrace();
                addReportToServer(e.getMessage());
            }
        } else if (dialogueId != -1) {
            try {
                drawInterface(Widget.cache[dialogueId], 20, 20 + yOffset, 0);
            } catch (Exception e) {
                e.printStackTrace();
                addReportToServer(e.getMessage());
            }
        } else if (showChatComponents) {
            int playerChatRows = -3;
            int totalMessages = 0;
            int shadow = (transparent_chat_box && !fixed) ? 0 : -1;
            Rasterizer2D.set_clip(0, 7 + yOffset, 497, 122 + yOffset);
            for (int k = 0; k < 500; k++) {
                if (chatMessages[k] != null) {
                    ChatMessage msg = chatMessages[k];
                    int type = msg.getType();
                    String name = msg.getName();
                    String message = msg.getMessage();
                    String title = msg.getTitle() == null ? "" : msg.getTitle();
                    List<ChatCrown> crowns = msg.getCrowns();
                    boolean broadcast = false;
                    if (message.contains("<link")) {
                        broadcast = true;
                    }

                    if (settings[ConfigUtility.TRANSPARENT_CHAT_BOX_ID] == 1 && !fixed) {
                        message = message.replace("<col=0>", "<col=FFFFFF>").replace("<col=255>", "<col=9090ff>")
                            .replace("<col=800000>", "<col=ef5050>").replace("<col=18626b>", "<col=3aa6b3>")
                            .replace("<col=7e5221>", "<col=b1722b>").replace("<col=7a5c66>", "<col=986f7d>");
                    }
                    int y = (70 - playerChatRows * 14) + chatScrollAmount + 5;
                    if (type == 0) {
                        int xPos = 10;
                        if (broadcast) {
                            spriteCache.get(856).drawSprite(xPos + 62, y - 12 + yOffset);
                            xPos += 14;
                        }
                        if (chatTypeView == 0 || chatTypeView == 6) {
                            if (!broadcast) {
                                if (!message.contains("[Global]")) {
                                    if (y > 0 && y < 210) {
                                        adv_font_regular.draw(message, 11, y + yOffset, transparent_chat_box && !fixed ? 0xFFFFFF : 0, shadow);
                                    }
                                } else {
                                    xPos += 48;
                                    String replacementSpaces = "";
                                    for (ChatCrown c : crowns) {
                                        replacementSpaces += "    ";
                                        SimpleImage sprite = spriteCache.get(c.getSpriteId());
                                        if (sprite != null) {
                                            sprite.drawSprite(xPos + 1, y - 12 + yOffset);
                                            xPos += sprite.width + 2;
                                        }
                                    }
                                    if (!crowns.isEmpty()) {
                                        if (crowns.size() > 1) {
                                            // Let's remove the first space, 8 spaces is too many for 2 icons, it should
                                            // be 7.
                                            replacementSpaces = replacementSpaces.substring(1);
                                        }
                                        message = message.replace("[Global]", "[Global]" + replacementSpaces);
                                    }
                                    if (y > 0 && y < 210) {
                                        adv_font_regular.draw(message, 11, y + yOffset, transparent_chat_box && !fixed ? 0xFFFFFF : 0, shadow);
                                    }
                                }
                            } else if (broadcast) {
                                if (y > 0 && y < 210) {
                                    adv_font_regular.draw("<col=004f00>Broadcast:", 10, y + yOffset - 1, transparent_chat_box && !fixed ? 0xFFFFFF : 0, shadow);
                                    adv_font_regular.draw(message, 85, y + yOffset - 1, transparent_chat_box && !fixed ? 0xFFFFFF : 0, shadow);
                                }
                            }
                            totalMessages++;
                            playerChatRows++;
                        }
                    }

                    if ((type == 1 || type == 2)
                        && (set_public_channel == 0 || set_public_channel == 1 && check_username(name))) {
                        if (chatTypeView == 1 || chatTypeView == 0) {
                            int xPos = 11;

                            for (ChatCrown c : crowns) {
                                SimpleImage sprite = spriteCache.get(c.getSpriteId());
                                if (sprite != null) {
                                    sprite.drawSprite(xPos + 1, y - 12 + yOffset);
                                    xPos += sprite.width + 2;
                                }
                            }

                            if (y > 0 && y < 210) {
                                adv_font_regular.draw(title + "<col=0>" + name + ":", xPos, y + yOffset, (transparent_chat_box && !fixed) ? 0xFFFFFF : 0, shadow);
                                xPos += adv_font_regular.get_width(title + "<col=0>" + name) + 8;
                                adv_font_regular.draw(message, xPos, y + yOffset, (transparent_chat_box && !fixed) ? 0x7FA9FF : 255, shadow);
                            }
                            totalMessages++;
                            playerChatRows++;
                        }
                    }

                    if ((type == 3 || type == 7) && (splitPrivateChat == 0 || chatTypeView == 2)
                        && (type == 7 || privateChatMode == 0 || privateChatMode == 1 && check_username(name))) {
                        if (chatTypeView == 2 || chatTypeView == 0) {
                            boolean onIgnore = false;
                            if (name != null) {
                                for (int count = 0; count < ignoreCount; count++) {
                                    if (ignoreListAsLongs[count] != Utils.longForName(name)) {
                                        continue;
                                    }
                                    onIgnore = true;
                                    break;
                                }
                            }
                            if (y > 0 && y < 210 && !onIgnore) {
                                int x = 11;
                                adv_font_regular.draw("From ", x, y + yOffset,
                                    transparent_chat_box && !fixed ? 0xFFFFFF : 0, shadow);
                                x += adv_font_regular.get_width("From ");

                                for (ChatCrown c : crowns) {
                                    SimpleImage sprite = spriteCache.get(c.getSpriteId());
                                    if (sprite != null) {
                                        sprite.drawSprite(x + 1, y - 12 + yOffset);
                                        x += sprite.width + 2;
                                    }
                                }

                                adv_font_regular.draw(title + "<col=0>" + name + ":", x, y + yOffset, (transparent_chat_box && !fixed) ? 0xFFFFFF : 0, shadow);
                                x += adv_font_regular.get_width(title + "<col=0>" + name) + 8;
                                adv_font_regular.draw(message, x, y + yOffset, 0x800000, shadow);
                            }
                            totalMessages++;
                            playerChatRows++;
                        }
                    }

                    if (type == 4 && (tradeMode == 0 || tradeMode == 1 && check_username(name))) {
                        if (chatTypeView == 3 || chatTypeView == 0) {
                            adv_font_regular.draw(name + " " + message, 11, y + yOffset,
                                transparent_chat_box && !fixed ? 0xdf20ff : 0x800080, shadow);
                            playerChatRows++;
                        }
                    }

                    if (type == 5 && splitPrivateChat == 0 && privateChatMode < 2) {
                        if (chatTypeView == 2 || chatTypeView == 0) {
                            adv_font_regular.draw(name + " " + message, 8, y + yOffset, 0x800080, shadow);
                            totalMessages++;
                            playerChatRows++;
                        }
                    }

                    if (type == 6 && (splitPrivateChat == 0 || chatTypeView == 2) && privateChatMode < 2) {
                        if (chatTypeView == 2 || chatTypeView == 0) {
                            if (y > 0 && y < 210) {
                                adv_font_regular.draw("To " + name + ":", 11, y + yOffset, (transparent_chat_box && !fixed) ? 0xFFFFFF : 0, shadow);
                                adv_font_regular.draw(capitalizeFirstChar(message), 15 + adv_font_regular.get_width("To :" + name), y + yOffset, 0x800000, shadow);
                            }
                            totalMessages++;
                            playerChatRows++;
                        }
                    }

                    if (type == 8 && (tradeMode == 0 || tradeMode == 1 && check_username(name))) {
                        if (chatTypeView == 3 || chatTypeView == 0) {
                            if (y > 0 && y < 210) {
                                adv_font_regular.draw(name + " " + message, 11, y + yOffset, 0x7e3200, shadow);
                            }
                            totalMessages++;
                            playerChatRows++;
                        }
                    }

                    if (type == 11 && (clanChatMode == 0 || (clanChatMode == 1 && check_username(name)))) {
                        if (chatTypeView == 11 || chatTypeView == 0) {
                            boolean onIgnore = false;
                            if (name != null) {
                                for (int count = 0; count < ignoreCount; count++) {
                                    if (ignoreListAsLongs[count] != Utils.longForName(name)) {
                                        continue;
                                    }
                                    onIgnore = true;
                                    break;
                                }
                            }
                            if (!onIgnore) {
                                adv_font_regular.draw(
                                    transparent_chat_box && !fixed ? message.replace("<col=9090ff>", "<col=9070ff>")
                                        : message,
                                    10, y + yOffset, 0x7e3200, shadow);
                                totalMessages++;
                                playerChatRows++;
                            }
                        }
                    }
                    if (type == 12) {
                        if (chatTypeView == 5 || chatTypeView == 0) {
                            boolean onIgnore = false;
                            if (name != null) {
                                for (int count = 0; count < ignoreCount; count++) {
                                    if (ignoreListAsLongs[count] != Utils.longForName(name)) {
                                        continue;
                                    }
                                    onIgnore = true;
                                    break;
                                }
                            }
                            if (!onIgnore) {
                                adv_font_regular.draw(message, 10, y + yOffset, 0x7e3200, shadow);
                                totalMessages++;
                                playerChatRows++;
                            }
                        }
                    }

                    if (type == 13 && chatTypeView == 12) {
                        if (y > 0 && y < 210) {
                            adv_font_regular.draw(name + " " + message, 8, y + yOffset, 0x7e3200, shadow);
                        }
                        totalMessages++;
                        playerChatRows++;
                    }

                    if (type == 16) {
                        if (chatTypeView == 11 || chatTypeView == 0) {
                            adv_font_regular.draw(message, 10, y + yOffset, 0x7e3200, shadow);
                            totalMessages++;
                            playerChatRows++;
                        }
                    }

                    if (type == 20) {
                        if (chatTypeView == 3 || chatTypeView == 0) {
                            int offsetX = 0;
                            if (this.broadcast != null && this.broadcast.hasUrl()) {
                                /*SimpleImage sprite = Client.spriteCache.get(856);
                                if (sprite != null) {
                                    offsetX = sprite.width + 1;
                                    sprite.drawAdvancedSprite(11, y + yOffset - 10);
                                }*/
                            }

                            /*adv_font_regular.draw("Broadcast:", 11 + offsetX, y + yOffset, transparent_chat_box && !fixed ? 0x16630f : 0x16630f, shadow);
                            adv_font_regular.draw(name + "" + capitalizeFirstChar(message), 75 + offsetX, y + yOffset, transparent_chat_box && !fixed ? 0 : 0, shadow);
                            totalMessages++;
                            playerChatRows++;*/
                        }
                    }

                    if (type == 40) {
                        if (chatTypeView == 5 || chatTypeView == 0) {
                            boolean onIgnore = false;
                            if (name != null) {
                                for (int count = 0; count < ignoreCount; count++) {
                                    if (ignoreListAsLongs[count] != Utils.longForName(name)) {
                                        continue;
                                    }
                                    onIgnore = true;
                                    break;
                                }
                            }
                            if (!onIgnore) {
                                adv_font_regular.draw(name + " " + message, 10, y + yOffset, 0x00a7e0, shadow);
                                totalMessages++;
                                playerChatRows++;
                            }
                        }
                    }
                }
            }
            Rasterizer2D.set_default_size();
            chatScrollHeight = totalMessages * 14 + 7 + 5;
            if (chatScrollHeight < 111) {
                chatScrollHeight = 111;
            }

            drawScrollbar(114, chatScrollHeight - chatScrollAmount - 113, 7 + yOffset,
                transparent_chat_box && !fixed ? 490 : 496, chatScrollHeight, transparent_chat_box && !fixed);
            String name;

            if (local_player != null && local_player.username != null) {
                name = local_player.getTitle(false) + local_player.username;
            } else {
                name = StringUtils.formatText(capitalize(myUsername));
            }

            Rasterizer2D.set_clip(0, 0 + yOffset, 519, 142 + yOffset);
            int xOffset = 10;
            // Draw crowns in typing area
            for (ChatCrown c : ChatCrown.get(myPrivilege, donatorPrivilege)) {
                SimpleImage sprite = spriteCache.get(c.getSpriteId());
                if (sprite != null) {
                    sprite.drawSprite(xOffset, 122 + yOffset);
                    xOffset += sprite.width + 2;
                }
            }

            adv_font_regular.draw(name + ":", xOffset, 133 + yOffset, (transparent_chat_box && !fixed) ? 0xFFFFFF : 0, shadow);
            adv_font_regular.draw(inputString + "*", xOffset + adv_font_regular.get_width(name + ": "), 133 + yOffset,
                (transparent_chat_box && !fixed) ? 0x7FA9FF : 255, shadow);

            Rasterizer2D.drawHorizontalLine(transparent_chat_box && !fixed ? 4 : 7, 121 + yOffset,
                transparent_chat_box && !fixed ? 509 : 505, transparent_chat_box && !fixed ? 0xFFFFFF : 0x807660,
                transparent_chat_box && !fixed ? 120 : 255);
            Rasterizer2D.set_default_size();
        }

        if (menuOpen) {
            drawMenu(0, fixed ? 338 : 0);
        }

        if (fixed && chatboxImageProducer != null) {
            chatboxImageProducer.drawGraphics(338, super.graphics, 0);
        }

        if (gameScreenImageProducer != null) {
            gameScreenImageProducer.init();
        }
        Rasterizer3D.line_offsets = viewportOffsets;
    }

    public static String capitalize(String name) {
        if (name == null)
            return name;
        for (int length = 0; length < name.length(); length++) {
            if (length == 0) {
                name = String.format("%s%s", Character.toUpperCase(name.charAt(0)), name.substring(1));
            }
            if (!Character.isLetterOrDigit(name.charAt(length))) {
                if (length + 1 < name.length()) {
                    name = String.format("%s%s%s", name.subSequence(0, length + 1),
                        Character.toUpperCase(name.charAt(length + 1)), name.substring(length + 2));
                }
            }
        }
        return name;
    }

    /**
     * Initializes the client for startup
     */
    public void initialize() {
        try {
            nodeID = 10;
            setHighMem();
            isMembers = true;
            SignLink.storeid = 32;
            SignLink.startpriv(InetAddress.getLocalHost());
            initClientFrame(window_width, window_height);
        } catch (Exception exception) {
            exception.printStackTrace();
            addReportToServer(exception.getMessage());
            return;
        }
    }

    public void startRunnable(Runnable runnable, int priority) {
        if (priority > 10)
            priority = 10;
        // if (SignLink.mainapp != null) {
        // SignLink.startthread(runnable, priority);
        // } else {
        super.startRunnable(runnable, priority);
        // }
    }

    public Socket openSocket(int port) throws IOException {
        return new Socket(InetAddress.getByName(serverAddress), port);
        //I believe this is required for IPv6 support, but maybe not.
        //return new Socket(serverAddress, port);
    }

    private void processMenuClick() {
        if (activeInterfaceType != 0)
            return;
        int click = super.click_type;
        if (widget_highlighted == 1 && super.click_x >= 516 && super.click_y >= 160 && super.click_x <= 765
            && super.click_y <= 205)
            click = 0;
        if (menuOpen) {
            if (click != 1) {
                int k = super.cursor_x;
                int j1 = super.cursor_y;
                if (menuScreenArea == 0) {
                    k -= 4;
                    j1 -= 4;
                }
                if (menuScreenArea == 1) {
                    k -= 519;
                    j1 -= 168;
                }
                if (menuScreenArea == 2) {
                    k -= 17;
                    j1 -= 338;
                }
                if (menuScreenArea == 3) {
                    k -= 519;
                    j1 -= 0;
                }
                if (k < menuOffsetX - 10 || k > menuOffsetX + menuWidth + 10 || j1 < menuOffsetY - 10
                    || j1 > menuOffsetY + menuHeight + 10) {
                    menuOpen = false;
                    if (menuScreenArea == 1) {
                    }
                    if (menuScreenArea == 2)
                        update_chat_producer = true;
                }
            }
            if (click == 1) {
                int l = menuOffsetX;
                int k1 = menuOffsetY;
                int i2 = menuWidth;
                int k2 = super.click_x;
                int l2 = super.click_y;
                switch (menuScreenArea) {
                    case 0:
                        k2 -= 4;
                        l2 -= 4;
                        break;
                    case 1:
                        k2 -= 519;
                        l2 -= 168;
                        break;
                    case 2:
                        k2 -= 5;
                        l2 -= 338;
                        break;
                    case 3:
                        k2 -= 519;
                        l2 -= 0;
                        break;
                }
                int i3 = -1;
                for (int j3 = 0; j3 < menuActionRow; j3++) {
                    int k3 = k1 + 31 + (menuActionRow - 1 - j3) * 15;
                    if (k2 > l && k2 < l + i2 && l2 > k3 - 13 && l2 < k3 + 3)
                        i3 = j3;
                }
                if (i3 != -1)
                    processMenuActions(i3);
                menuOpen = false;
                if (menuScreenArea == 1) {
                }
                if (menuScreenArea == 2) {
                    update_chat_producer = true;
                }
            }
        } else {
            if (click == 1 && menuActionRow > 0) {
                int menuId = menuActionTypes[menuActionRow - 1];
                if (menuId == 632 || menuId == 78 || menuId == 867 || menuId == 431 || menuId == 53 || menuId == 74 || menuId == 454 || menuId == 539
                    || menuId == 493 || menuId == 847 || menuId == 447 || menuId == 1125 || menuId == 968) {
                    int l1 = firstMenuAction[menuActionRow - 1];
                    int j2 = secondMenuAction[menuActionRow - 1];
                    Widget class9 = Widget.cache[j2];
                    if (class9.allowSwapItems || class9.replaceItems) {
                        aBoolean1242 = false;
                        draggingCycles = 0;
                        focusedDragWidget = j2;
                        dragFromSlot = l1;
                        activeInterfaceType = 2;
                        mouseDragX = super.click_x;
                        mouseDragY = super.click_y;
                        if (Widget.cache[j2].parent == widget_overlay_id)
                            activeInterfaceType = 1;
                        if (Widget.cache[j2].parent == backDialogueId)
                            activeInterfaceType = 3;
                        return;
                    }
                }
            }
            if (click == 1 && (useOneMouseButton == 1 || menuHasAddFriend(menuActionRow - 1)) && menuActionRow > 2)
                click = 2;
            if (click == 1 && menuActionRow > 0)
                processMenuActions(menuActionRow - 1);
            if (click == 2 && menuActionRow > 0)
                determineMenuSize();
            processMainScreenClick();
            processTabClick();
            processChatModeClick();
            minimapHovers();
        }
    }

    private void loadRegion() {
        try {
            lastKnownPlane = -1;
            incompleteAnimables.clear();
            projectiles.clear();
            Rasterizer3D.reset_texels();
            release();
            scene.rebuild();
            System.gc();
            for (int i = 0; i < 4; i++)
                collisionMaps[i].init();
            for (int l = 0; l < 4; l++) {
                for (int k1 = 0; k1 < 104; k1++) {
                    for (int j2 = 0; j2 < 104; j2++)
                        tileFlags[l][k1][j2] = 0;
                }
            }

            Region objectManager = new Region(tileFlags, tileHeights);
            int k2 = terrainData.length;
            packetSender.sendEmptyPacket();

            if (!requestMapReconstruct) {
                for (int i3 = 0; i3 < k2; i3++) {
                    int i4 = (mapCoordinates[i3] >> 8) * 64 - next_region_start;
                    int k5 = (mapCoordinates[i3] & 0xff) * 64 - next_region_end;
                    byte abyte0[] = terrainData[i3];
                    if (abyte0 != null)
                        objectManager.load_terrain_block(abyte0, k5, i4, (region_x - 6) * 8, (region_y - 6) * 8,
                            collisionMaps);
                }
                for (int j4 = 0; j4 < k2; j4++) {
                    int l5 = (mapCoordinates[j4] >> 8) * 64 - next_region_start;
                    int k7 = (mapCoordinates[j4] & 0xff) * 64 - next_region_end;
                    byte abyte2[] = terrainData[j4];
                    if (abyte2 == null && region_y < 800)
                        objectManager.set_vertex_heights(k7, 64, 64, l5);
                }
                /*
                 * anInt1097++; if (anInt1097 > 160) { anInt1097 = 0; //anticheat?
                 * outgoing.writeOpcode(238); outgoing.writeByte(96); }
                 */
                packetSender.sendEmptyPacket();
                for (int i6 = 0; i6 < k2; i6++) {
                    byte abyte1[] = objectData[i6];
                    if (abyte1 != null) {
                        int l8 = (mapCoordinates[i6] >> 8) * 64 - next_region_start;
                        int k9 = (mapCoordinates[i6] & 0xff) * 64 - next_region_end;
                        objectManager.load(l8, collisionMaps, k9, scene, abyte1);
                    }
                }
            } else {
                for (int plane = 0; plane < 4; plane++) {
                    for (int x = 0; x < 13; x++) {
                        for (int y = 0; y < 13; y++) {
                            int chunkBits = constructRegionData[plane][x][y];
                            if (chunkBits != -1) {
                                int z = chunkBits >> 24 & 3;
                                int rotation = chunkBits >> 1 & 3;
                                int xCoord = chunkBits >> 14 & 0x3ff;
                                int yCoord = chunkBits >> 3 & 0x7ff;
                                int mapRegion = (xCoord / 8 << 8) + yCoord / 8;
                                for (int idx = 0; idx < mapCoordinates.length; idx++) {
                                    if (mapCoordinates[idx] != mapRegion || terrainData[idx] == null)
                                        continue;
                                    objectManager.load_sub_terrain_block(z, rotation, collisionMaps, x * 8, (xCoord & 7) * 8,
                                        terrainData[idx], (yCoord & 7) * 8, plane, y * 8);
                                    break;
                                }

                            }
                        }
                    }
                }
                for (int xChunk = 0; xChunk < 13; xChunk++) {
                    for (int yChunk = 0; yChunk < 13; yChunk++) {
                        int tileBits = constructRegionData[0][xChunk][yChunk];
                        if (tileBits == -1)
                            objectManager.set_vertex_heights(yChunk * 8, 8, 8, xChunk * 8);
                    }
                }

                packetSender.sendEmptyPacket();
                for (int chunkZ = 0; chunkZ < 4; chunkZ++) {
                    for (int chunkX = 0; chunkX < 13; chunkX++) {
                        for (int chunkY = 0; chunkY < 13; chunkY++) {
                            int tileBits = constructRegionData[chunkZ][chunkX][chunkY];
                            if (tileBits != -1) {
                                int plane = tileBits >> 24 & 3;
                                int rotation = tileBits >> 1 & 3;
                                int coordX = tileBits >> 14 & 0x3ff;
                                int coordY = tileBits >> 3 & 0x7ff;
                                int mapRegion = (coordX / 8 << 8) + coordY / 8;
                                for (int idx = 0; idx < mapCoordinates.length; idx++) {
                                    if (mapCoordinates[idx] != mapRegion || objectData[idx] == null)
                                        continue;
                                    objectManager.load_sub_object_block(collisionMaps, scene, plane, chunkX * 8,
                                        (coordY & 7) * 8, chunkZ, objectData[idx], (coordX & 7) * 8, rotation,
                                        chunkY * 8);
                                    break;
                                }
                            }
                        }
                    }
                }
                requestMapReconstruct = false;
            }
            packetSender.sendEmptyPacket();
            objectManager.create_region(collisionMaps, scene);
            gameScreenImageProducer.init();
            packetSender.sendEmptyPacket();
            int k3 = Region.min_plane;
            if (k3 > plane)
                k3 = plane;
            if (k3 < plane - 1)
                k3 = plane - 1;
            if (low_detail)
                scene.set_height(Region.min_plane);
            else
                scene.set_height(0);
            for (int i5 = 0; i5 < 104; i5++) {
                for (int i7 = 0; i7 < 104; i7++)
                    spawn_scene_item(i5, i7);

            }

            anInt1051++;
            if (anInt1051 > 98) {
                anInt1051 = 0;
                // anticheat?
                // outgoing.writeOpcode(150);
            }

            clearObjectSpawnRequests();
        } catch (Exception exception) {
            exception.printStackTrace();
            addReportToServer(exception.getMessage());
        }
        ObjectDefinition.model_cache.clear();
        if (super.gameFrame != null) {
            packetSender.sendRegionChange();
        }
        if (low_detail && SignLink.cache_dat != null) {
            int j = resourceProvider.getVersionCount(0);
            for (int i1 = 0; i1 < j; i1++) {
                int l1 = resourceProvider.getModelIndex(i1);
                if ((l1 & 0x79) == 0)
                    Model.method461(i1);
            }
        }
        System.gc();
        Rasterizer3D.reset_textures();
        resourceProvider.clearExtras();

        int startRegionX = (region_x - 6) / 8 - 1;
        int endRegionX = (region_x + 6) / 8 + 1;
        int startRegionY = (region_y - 6) / 8 - 1;
        int endRegionY = (region_y + 6) / 8 + 1;
        for (int regionX = startRegionX; regionX <= endRegionX; regionX++) {
            for (int regionY = startRegionY; regionY <= endRegionY; regionY++) {
                if (regionX == startRegionX || regionX == endRegionX || regionY == startRegionY
                    || regionY == endRegionY) {
                    int floorMapId = resourceProvider.getMapIdForRegions(0, regionY, regionX);
                    if (floorMapId != -1) {
                        resourceProvider.passive_request(floorMapId, 3);
                    }
                    int objectMapId = resourceProvider.getMapIdForRegions(1, regionY, regionX);
                    if (objectMapId != -1) {
                        resourceProvider.passive_request(objectMapId, 3);
                    }
                }
            }
        }
    }

    public static AbstractMap.SimpleEntry<Integer, Integer> getNextInteger(ArrayList<Integer> values) {
        ArrayList<AbstractMap.SimpleEntry<Integer, Integer>> frequencies = new ArrayList<>();
        int maxIndex = 0;
        main:
        for (int i = 0; i < values.size(); ++i) {
            int value = values.get(i);
            for (int j = 0; j < frequencies.size(); ++j) {
                if (frequencies.get(j).getKey() == value) {
                    frequencies.get(j).setValue(frequencies.get(j).getValue() + 1);
                    if (frequencies.get(maxIndex).getValue() < frequencies.get(j).getValue()) {
                        maxIndex = j;
                    }
                    continue main;
                }
            }
            frequencies.add(new AbstractMap.SimpleEntry<Integer, Integer>(value, 1));
        }
        return frequencies.get(maxIndex);
    }

    private void release() {
        ObjectDefinition.model_cache.clear();
        ObjectDefinition.animated_model_cache.clear();
        NpcDefinition.model_cache.clear();
        ItemDefinition.model_cache.clear();
        ItemSpriteFactory.scaled_cache.clear();
        ItemSpriteFactory.sprites_cache.clear();
        Player.model_cache.clear();
        SpotAnimation.model_cache.clear();
    }

    private void renderMapScene(int plane) {
        int pixels[] = minimapImage.pixels;
        int length = pixels.length;

        for (int pixel = 0; pixel < length; pixel++) {
            pixels[pixel] = 0;
        }

        for (int y = 1; y < 103; y++) {
            int drawIndex = 24628 + (103 - y) * 512 * 4;
            for (int x = 1; x < 103; x++) {
                if ((tileFlags[plane][x][y] & 0x18) == 0)
                    scene.draw_minimap_tile(pixels, drawIndex, plane, x, y);
                if (plane < 3 && (tileFlags[plane + 1][x][y] & 8) != 0)
                    scene.draw_minimap_tile(pixels, drawIndex, plane + 1, x, y);
                drawIndex += 4;
            }
        }

        pixels = null;
        int white_rgb = Color.WHITE.getRGB();
        int red_rgb = Color.RED.getRGB();
        minimapImage.init();
        try {
            for (int y = 1; y < 103; y++) {
                for (int x = 1; x < 103; x++) {
                    if ((tileFlags[plane][x][y] & 0x18) == 0)
                        drawMapScenes(y, white_rgb, x, red_rgb, plane);
                    if (plane < 3 && (tileFlags[plane + 1][x][y] & 8) != 0)
                        drawMapScenes(y, white_rgb, x, red_rgb, plane + 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            addReportToServer(e.getMessage());
        }
        gameScreenImageProducer.init();
        objectIconCount = 0;
        for (int x = 0; x < 104; x++) {
            for (int y = 0; y < 104; y++) {
                long id = scene.get_ground_decor_uid(plane, x, y);
                if (id != 0L) {
                    int index = ObjectDefinition.get(get_object_key(id)).minimapFunction;
                    if (index >= 0) {
                        int sprite = AreaDefinition.lookup(index).spriteId;
                        if (sprite != -1) {
                            int hintX = x;
                            int hintY = y;
                            System.out.println("Area id: "+index);
                            minimapHint[objectIconCount] =  mapFunctions[sprite];
                            minimapHintX[objectIconCount] = hintX;
                            minimapHintY[objectIconCount] = hintY;
                            objectIconCount++;
                        }
                    }
                }
            }
        }

        if (ClientConstants.DUMP_MAP_REGIONS) {

            File directory = new File("MapImageDumps/");
            if (!directory.exists()) {
                directory.mkdir();
            }
            BufferedImage bufferedimage = new BufferedImage(minimapImage.width, minimapImage.height, 1);
            bufferedimage.setRGB(0, 0, minimapImage.width, minimapImage.height, minimapImage.pixels, 0,
                minimapImage.width);
            Graphics2D graphics2d = bufferedimage.createGraphics();
            graphics2d.dispose();
            try {
                File file1 = new File("MapImageDumps/" + (directory.listFiles().length + 1) + ".png");
                ImageIO.write(bufferedimage, "png", file1);
            } catch (Exception e) {
                e.printStackTrace();
                addReportToServer(e.getMessage());
            }
        }
    }

    private void drawPlayers(boolean isMyPlayer) {
        if (local_player.world_x >> 7 == travel_destination_x && local_player.world_y >> 7 == travel_destination_y) {
            travel_destination_x = 0;
        }
        int count = players_in_region;
        if (isMyPlayer) {
            count = 1;
        }
        for (int rendered = 0; rendered < count; rendered++) {
            Player player;
            long index;
            if (isMyPlayer) {
                player = local_player;
                index = (long) LOCAL_PLAYER_INDEX << 32;
            } else {
                player = players[local_players[rendered]];
                index = (long) local_players[rendered] << 32;
            }

            if (player == null || !player.visible()) {
                continue;
            }

            int interact = local_player.engaged_entity_id - 32768;
            if (getInteractingWithEntityId() == 0 && interact > 0) {
                setInteractingWithEntityId(interact);
            }
            if (getInteractingWithEntityId() > 0) {
                if (interact > 0) {
                    setInteractingWithEntityId(interact);
                }
                // Do not draw the player i am interacting with, because it is drawn already because prioritization.
                Player interactingPlayer = players[getInteractingWithEntityId()];
                if (player == interactingPlayer) {
                    continue;
                }
            }

            // If players in region is more than 50 and is low mem, then make their animations all stand animation.......
            //player.reference_pose = (low_detail && players_in_region > 50 || players_in_region > 200) && !isMyPlayer && player.queued_animation_id == player.idle_animation_id;
            int x = player.world_x >> 7;
            int y = player.world_y >> 7;
            if (x < 0 || x >= 104 || y < 0 || y >= 104) {
                continue;
            }
            if (player.transformed_model != null && game_tick >= player.transform_delay && game_tick < player.transform_duration) {
                player.reference_pose = false;
                player.height = get_tile_pos(plane, player.world_y, player.world_x);
                scene.add_transformed_entity(plane, player.world_y, player, player.current_rotation, player.transform_height_offset, player.world_x, player.height, player.transform_width, player.transform_width_offset, index, player.transform_height);
                continue;
            }
            if ((player.world_x & 0x7f) == 64 && (player.world_y & 0x7f) == 64) {
                if (tile_cycle_map[x][y] == render_cycle) {
                    continue;
                }
                tile_cycle_map[x][y] = render_cycle;
            }
            player.height = get_tile_pos(plane, player.world_y, player.world_x);
            scene.add_entity(plane, player.current_rotation, player.height, index, player.world_y, 60, player.world_x, player, player.dynamic);
        }
        if (isMyPlayer) {
            //Draw the player we're interacting with
            //Interacting includes combat, following, etc.
            int interact = local_player.engaged_entity_id - 32768;
            if (getInteractingWithEntityId() == 0 && interact > 0) {
                setInteractingWithEntityId(interact);
            }
            if (getInteractingWithEntityId() > 0) {
                if (interact > 0) {
                    setInteractingWithEntityId(interact);
                }
                Player player = players[getInteractingWithEntityId()];
                showPlayer(player, (long) getInteractingWithEntityId() << 32, false);
            }
        }
    }

    private void showPlayer(Player player, long index, boolean isMyPlayer) {
        if (player == null || !player.visible()) {
            return;
        }

        //player.reference_pose = (low_detail && players_in_region > 50 || players_in_region > 200) && !isMyPlayer && player.queued_animation_id == player.idle_animation_id;
        int x = player.world_x >> 7;
        int y = player.world_y >> 7;
        if (x < 0 || x >= 104 || y < 0 || y >= 104) {
            return;
        }
        if (player.transformed_model != null && game_tick >= player.transform_delay && game_tick < player.transform_duration) {
            player.reference_pose = false;
            player.height = get_tile_pos(plane, player.world_y, player.world_x);
            scene.add_transformed_entity(plane, player.world_y, player, player.current_rotation, player.transform_height_offset, player.world_x, player.height, player.transform_width, player.transform_width_offset, index, player.transform_height);
        }
        if ((player.world_x & 0x7f) == 64 && (player.world_y & 0x7f) == 64) {
            if (tile_cycle_map[x][y] == render_cycle) {
                return;
            }
            tile_cycle_map[x][y] = render_cycle;
        }
        player.height = get_tile_pos(plane, player.world_y, player.world_x);
        scene.add_entity(plane, player.current_rotation, player.height, index, player.world_y, 60, player.world_x, player, player.dynamic);
    }

    private void drawNPCs(boolean flag) {
        for (int index = 0; index < npcs_in_region; index++) {
            Npc npc = npcs[local_npcs[index]];
            long k = 0x20000000L | (long) local_npcs[index] << 32;
            if (npc == null || !npc.visible() || npc.desc.render_priority != flag)
                continue;
            int x = npc.world_x >> 7;
            int y = npc.world_y >> 7;
            if (x < 0 || x >= 104 || y < 0 || y >= 104) {
                continue;
            }
            if (npc.occupied_tiles == 1 && (npc.world_x & 0x7f) == 64 && (npc.world_y & 0x7f) == 64) {
                if (tile_cycle_map[x][y] == render_cycle) {
                    continue;
                }
                tile_cycle_map[x][y] = render_cycle;
            }
            if (!npc.desc.isClickable) {
                k |= ~0x7fffffffffffffffL;
            }
            scene.add_entity(plane, npc.current_rotation, get_tile_pos(plane, npc.world_y, npc.world_x), k, npc.world_y, (npc.occupied_tiles - 1) * 64 + 60, npc.world_x, npc, npc.dynamic);
        }
    }

    private int interactingWithEntityId;

    public int getInteractingWithEntityId() {
        return interactingWithEntityId;
    }

    public void setInteractingWithEntityId(int interactingWithEntityId) {
        this.interactingWithEntityId = interactingWithEntityId;
    }

    private void spawn_scene_item(int x, int y) {
        LinkedList list = scene_items[plane][x][y];
        if (list == null) {
            scene.remove_ground_item(plane, x, y);
            return;
        }
        long value_priority = 0xfa0a1f01;
        Object first = null;
        for (Item item = (Item) list.first(); item != null; item = (Item) list.next()) {
            ItemDefinition itemDef = ItemDefinition.get(item.id);
            long value = itemDef.cost;
            if (itemDef.stackable) {
                value *= item.quantity + (long) 1;
            }
            if (value > value_priority) {
                value_priority = value;
                first = item;
            }
        }
        list.addFirst(((Node) (first)));
        Object second = null;
        Object third = null;
        for (Item node = (Item) list.first(); node != null; node = (Item) list.next()) {
            if (node.id != ((Item) (first)).id && second == null)
                second = node;

            if (node.id != ((Item) (first)).id && node.id != ((Item) (second)).id && third == null)
                third = node;

        }
        long key = x + (y << 7) | 0x60000000;
        scene.add_ground_item(x, key, ((Renderable) (second)), get_tile_pos(plane, y * 128 + 64, x * 128 + 64), ((Renderable) (third)), ((Renderable) (first)), plane, y);
    }

    private void buildInterfaceMenu(int x, Widget widget, int cursor_x, int y, int cursor_y, int scrollPosition) {
        if (widget == null || widget.type != 0 || widget.children == null || widget.invisible || widget.drawingDisabled)
            return;
        if (cursor_x < x || cursor_y < y || cursor_x >= x + widget.width || cursor_y >= y + widget.height)
            return;
        int size = widget.children.length;
        //System.out.println("Build interface menu called at: " + super.cursor_x + " | " + super.cursor_y);
        for (int index = 0; index < size; index++) {
            int childX = widget.child_x[index] + x;
            int childY = (widget.child_y[index] + y) - scrollPosition;
            Widget child = Widget.cache[widget.children[index]];
            if (child == null || child.invisible) {
                continue;
            }
            HoverMenuManager.reset();
            childX += child.x;
            childY += child.y;
            checkFilters(child, childX, childY);
            checkHoverWithText(child, childX, childY);
            if ((child.hoverType >= 0 || child.defaultHoverColor != 0) && cursor_x >= childX && cursor_y >= childY && cursor_x < childX + child.width && cursor_y < childY + child.height) {
                if (child.hoverType >= 0) {
                    frameFocusedInterface = child.hoverType;
                    if (child.hoverType == 17) {
                        if (withinRange(child.id, TeleportWidget.STARTING_IMAGE_INDEX, TeleportWidget.ENDING_IMAGE_INDEX)) {
                            String description = getImageDescription(child.id - TeleportWidget.STARTING_IMAGE_INDEX);
                            int xPos = 0;
                            int yPos = 0;
                            if (description != null) {
                                if (screen == ScreenMode.FIXED) {
                                    xPos = 180;
                                    yPos = 295;
                                } else { // :[)
                                    // System.err.println("Extended state: " + this.gameFrame.getExtendedState());
                                    if (this.gameFrame.getExtendedState() == JFrame.MAXIMIZED_BOTH) {
                                        //System.out.println("Frame is Maximized");
                                        xPos = 790;
                                        yPos = 575 + 15;
                                    } else {
                                        //System.out.println("Frame is not Maximized");
                                        xPos = 180 + 28; // i love magic numbers (no)
                                        yPos = 295 + 50; // yes
                                    }
                                }
                                adv_font_regular.draw(description, xPos, yPos, 0xffffee, child.textShadow ? 0 : -1);
                            }
                        }
                    }
                } else {
                    frameFocusedInterface = child.id;
                }
            }
            if (child.type == 8 && cursor_x >= childX && cursor_y >= childY && cursor_x < childX + child.width
                && cursor_y < childY + child.height) {
                anInt1315 = child.id;
            }
            if (child.type == Widget.TYPE_CONTAINER && !child.invisible) {
                buildInterfaceMenu(childX, child, cursor_x, childY, cursor_y, child.scrollPosition);
                if (child.scrollMax > child.height)
                    handleScroll(childX + child.width, child.height, cursor_x, cursor_y, child, childY, child.scrollMax);
            } else {
               /* if(child.id >= 25301) {
                    System.err.println("Mouse x: " + cursor_x + " | Mouse y: " + cursor_y + " | Child x: " + childX + " | child y: " + childY + " | Child width: " + child.width + " | Child height: " + child.height);
                    System.err.println("Expected: " + (cursor_x >= childX) + " | " + (cursor_y >= childY) + " | " + (cursor_x < childX + child.width) + " | " + (cursor_y < childY + child.height));
                    System.err.println("To check: cursor_x >= childX cursor_x >= childX && cursor_y >= childY + && cursor_x < childX + child.width && cursor_y < childY + child.height");

                    System.out.println("Mouse y: " + cursor_y + " | Child y: " + childY);
                }*/

                if (child.optionType == Widget.OPTION_OK && cursor_x >= childX && cursor_y >= childY
                    && cursor_x < childX + child.width && cursor_y < childY + child.height) {
                    //System.err.println("Checking for: " + child.id);
                    boolean flag = false;
                    if (child.contentType != 0) {
                        flag = buildFriendsListMenu(child);
                    }
                    if (child.tooltip == null || child.tooltip.length() == 0) {
                        flag = true;
                    }

                    if (!flag) {
                        menuActionText[menuActionRow] = (myPrivilege >= 2 && myPrivilege <= 4 && ClientConstants.DEBUG_MODE)
                            ? child.tooltip + " " + child.id
                            : child.tooltip;
                        menuActionTypes[menuActionRow] = 315;
                        secondMenuAction[menuActionRow] = child.id;
                        menuActionRow++;
                    }
                    if (child.type == Widget.TYPE_HOVER || child.type == Widget.TYPE_CONFIG_HOVER
                        || child.type == Widget.TYPE_CONFIG_BUTTON_HOVERED_SPRITE_OUTLINE
                        || child.type == Widget.TYPE_ADJUSTABLE_CONFIG || child.type == Widget.TYPE_BOX) {
                        child.toggled = true;
                    }
                } else if (child.optionType == Widget.OPTION_CLOSE && cursor_x >= childX && cursor_y >= childY
                    && cursor_x < childX + child.width && cursor_y < childY + child.height) {
                    if (child.type == Widget.TYPE_HOVER) {
                        child.toggled = true;
                    }
                } else {
                    if (child.type == Widget.TYPE_HOVER || child.type == Widget.TYPE_CONFIG_HOVER
                        || child.type == Widget.TYPE_ADJUSTABLE_CONFIG || child.type == Widget.TYPE_BOX) {
                        child.toggled = false;
                    }
                }
                if (child.optionType == Widget.OPTION_USABLE && widget_highlighted == 0 && cursor_x >= childX
                    && cursor_y >= childY && cursor_x < childX + child.width && cursor_y < childY + child.height) {
                    String s = child.selectedActionName;
                    if (s.contains(" "))
                        s = s.substring(0, s.indexOf(" "));

                    if ((myPrivilege >= 2 && myPrivilege <= 4 && ClientConstants.DEBUG_MODE)) {
                        menuActionText[menuActionRow] = s + " <col=65280>" + child.spellName + " " + child.id;
                        menuActionTypes[menuActionRow] = USE_SPELL;
                        secondMenuAction[menuActionRow] = child.id;
                        menuActionRow++;
                    } else {
                        menuActionText[menuActionRow] = s + " <col=65280>" + child.spellName;
                        menuActionTypes[menuActionRow] = USE_SPELL;
                        secondMenuAction[menuActionRow] = child.id;
                        menuActionRow++;
                    }
                }
                if (child.optionType == Widget.OPTION_CLOSE && cursor_x >= childX && cursor_y >= childY
                    && cursor_x < childX + child.width && cursor_y < childY + child.height) {
                    HoverMenuManager.reset();
                    menuActionText[menuActionRow] = "Close";
                    menuActionTypes[menuActionRow] = 200;
                    secondMenuAction[menuActionRow] = child.id;
                    menuActionRow++;
                }
                if (child.optionType == Widget.OPTION_TOGGLE_SETTING && cursor_x >= childX && cursor_y >= childY
                    && cursor_x < childX + child.width && cursor_y < childY + child.height) {
                    HoverMenuManager.reset();
                    menuActionText[menuActionRow] = (myPrivilege >= 2 && myPrivilege <= 4 && ClientConstants.DEBUG_MODE)
                        ? child.tooltip + " <col=65280>(" + child.id + ")"
                        : child.tooltip;
                    menuActionTypes[menuActionRow] = 169;
                    secondMenuAction[menuActionRow] = child.id;
                    menuActionRow++;
                }
                if (child.optionType == Widget.OPTION_RESET_SETTING && cursor_x >= childX && cursor_y >= childY
                    && cursor_x < childX + child.width && cursor_y < childY + child.height) {
                    boolean flag = false;
                    if (child.tooltip == null || child.tooltip.length() == 0) {
                        flag = true;
                    }
                    if (!flag) {
                        if (child.id == 433) {
                            if (widget.id == 24899) {
                                child.tooltip = "Short fuse";
                            } else if (widget.id == 22899) {
                                child.tooltip = "Scorch";
                            } else {
                                child.tooltip = "Pound";
                            }
                        } else if (child.id == 432) {
                            if (widget.id == 24899) {
                                child.tooltip = "Medium fuse";
                            } else if (widget.id == 22899) {
                                child.tooltip = "Flare";
                            } else {
                                child.tooltip = "Pummel";
                            }
                        } else if (child.id == 431) {
                            if (widget.id == 24899) {
                                child.tooltip = "Long fuse";
                            } else if (widget.id == 22899) {
                                child.tooltip = "Blaze";
                            } else {
                                child.tooltip = "Block";
                            }
                        }
                        HoverMenuManager.reset();
                        menuActionText[menuActionRow] = (myPrivilege >= 2 && myPrivilege <= 4 && ClientConstants.DEBUG_MODE)
                            ? child.tooltip + " <col=FF9040>(" + child.id + ")"
                            : child.tooltip;
                        menuActionTypes[menuActionRow] = 646;
                        secondMenuAction[menuActionRow] = child.id;
                        menuActionRow++;
                    }
                }

                if (child.optionType == Widget.OPTION_CONTINUE && !continuedDialogue && cursor_x >= childX
                    && cursor_y >= childY && cursor_x < childX + child.width && cursor_y < childY + child.height) {
                    HoverMenuManager.reset();
                    menuActionText[menuActionRow] = child.tooltip;
                    menuActionTypes[menuActionRow] = 679;
                    secondMenuAction[menuActionRow] = child.id;
                    menuActionRow++;
                }

                if (child.optionType == Widget.OPTION_DROPDOWN) {

                    boolean flag = false;
                    child.hovered = false;
                    child.dropdownHover = -1;

                    if (child.dropdown.isOpen()) {

                        // Inverted keybinds dropdown
                        if (child.type == Widget.TYPE_KEYBINDS_DROPDOWN && child.inverted && cursor_x >= childX
                            && cursor_x < childX + (child.dropdown.getWidth() - 16)
                            && cursor_y >= childY - child.dropdown.getHeight() - 10 && cursor_y < childY) {

                            int yy = cursor_y - (childY - child.dropdown.getHeight());

                            if (cursor_x > childX + (child.dropdown.getWidth() / 2)) {
                                child.dropdownHover = ((yy / 15) * 2) + 1;
                            } else {
                                child.dropdownHover = (yy / 15) * 2;
                            }
                            flag = true;
                        } else if (!child.inverted && cursor_x >= childX
                            && cursor_x < childX + (child.dropdown.getWidth() - 16) && cursor_y >= childY + 19
                            && cursor_y < childY + 19 + child.dropdown.getHeight()) {

                            int yy = cursor_y - (childY + 19);

                            if (child.type == Widget.TYPE_KEYBINDS_DROPDOWN && child.dropdown.doesSplit()) {
                                if (cursor_x > childX + (child.dropdown.getWidth() / 2)) {
                                    child.dropdownHover = ((yy / 15) * 2) + 1;
                                } else {
                                    child.dropdownHover = (yy / 15) * 2;
                                }
                            } else {
                                child.dropdownHover = yy / 14; // Regular dropdown hover
                            }
                            flag = true;
                        }
                        if (flag) {
                            if (menuActionRow != 1) {
                                menuActionRow = 1;
                            }
                            HoverMenuManager.reset();
                            menuActionText[menuActionRow] = "Select";
                            menuActionTypes[menuActionRow] = 770;
                            secondMenuAction[menuActionRow] = child.id;
                            firstMenuAction[menuActionRow] = child.dropdownHover;
                            selectedMenuActions[menuActionRow] = widget.id;
                            menuActionRow++;
                        }
                    }
                    if (cursor_x >= childX && cursor_y >= childY && cursor_x < childX + child.dropdown.getWidth()
                        && cursor_y < childY + 24 && menuActionRow == 1) {
                        child.hovered = true;
                        HoverMenuManager.reset();
                        menuActionText[menuActionRow] = child.dropdown.isOpen() ? "Hide" : "Show";
                        menuActionTypes[menuActionRow] = 769;
                        secondMenuAction[menuActionRow] = child.id;
                        selectedMenuActions[menuActionRow] = widget.id;
                        menuActionRow++;
                    }
                }

                if (cursor_x >= childX && cursor_y >= childY
                    && cursor_x < childX + child.width + (child.type == 4 ? 100 : child.width)
                    && cursor_y < childY + child.height) {

                    if (!child.invisible) {
                        if (child.actions != null && !child.drawingDisabled) {

                            if (withinRange(child.id, TeleportWidget.STARTING_IMAGE_INDEX, TeleportWidget.ENDING_IMAGE_INDEX)) {
                                menuActionRow = 1;
                            }

                            if (!(child.contentType == 206 && interfaceIsSelected(child))) {
                                if ((child.type == 4 && child.defaultText.length() > 0) || child.type == 5 || !child.invisible) {

                                    boolean drawOptions = true;

                                    // HARDCODE CLICKABLE TEXT HERE
                                    if (child.parent == 37128) { // Clan chat interface, dont show options for guests
                                        drawOptions = showClanOptions;
                                    }

                                    if (drawOptions) {
                                        for (int actionId = child.actions.length - 1; actionId >= 0; actionId--) {
                                            if (child.actions[actionId] != null) {
                                                String action = child.actions[actionId]
                                                    + (child.type == 4 ? " <col=ffb000>" + child.defaultText : "");
                                                try {
                                                    if (action.contains("img")) {
                                                        action = action.replaceAll("<col=ffb000>", "");
                                                        int prefix = action.indexOf("<img=");
                                                        int suffix = action.indexOf(">");
                                                        //System.out.println("Action is: " + action);
                                                        action = action.replaceAll(action.substring(prefix + 3, suffix), "");
                                                        action = action.replaceAll("</img>", "");
                                                        action = action.replaceAll("<img=>", "");
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    addReportToServer(e.getMessage());
                                                }
                                                HoverMenuManager.reset();
                                                menuActionText[menuActionRow] = action;
                                                menuActionTypes[menuActionRow] = 647;
                                                firstMenuAction[menuActionRow] = actionId;
                                                secondMenuAction[menuActionRow] = child.id;
                                                menuActionRow++;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (child.type == Widget.TYPE_INVENTORY && !child.invisible && !child.drawingDisabled) {
                    int itemSlot = 0;
                    int tabAm = tabAmounts[0];
                    int tabSlot = 0;
                    int heightShift = 0;

                    int newSlot = 0;
                    if (child.contentType == 206 && settings[211] != 0) {
                        for (int tab = 0; tab < tabAmounts.length; tab++) {
                            if (tab == settings[211]) {
                                break;
                            }
                            newSlot += tabAmounts[tab];
                        }
                        itemSlot = newSlot;
                    }

                    int results = -1;
                    boolean search = searchingBank && !promptInput.isEmpty() && child.id != 5064;

                    heightLoop:
                    for (int l2 = 0; l2 < child.height; l2++) {
                        for (int i3 = 0; i3 < child.width; i3++) {
                            if (itemSlot >= child.inventoryItemId.length) {
                                continue;
                            }
                            if (search && child.inventoryItemId[itemSlot] > 0) {
                                ItemDefinition definition = ItemDefinition.get(child.inventoryItemId[itemSlot] - 1);
                                if (definition == null || definition.name == null) {
                                    itemSlot++;
                                    continue;
                                }
                                if (!definition.name.toLowerCase().contains(promptInput.toLowerCase())) {
                                    itemSlot++;
                                    continue;
                                }
                                results++;
                            }
                            if (child.contentType == 206 && !search) {
                                if (settings[211] == 0) {
                                    if (itemSlot >= tabAm) {
                                        if (tabSlot + 1 < tabAmounts.length) {
                                            tabAm += tabAmounts[++tabSlot];
                                            if (tabSlot > 0 && tabAmounts[tabSlot - 1] % child.width == 0) {
                                                l2--;
                                            }
                                            heightShift += 8;
                                        }
                                        break;
                                    }
                                } else if (settings[211] <= 9) {
                                    if (itemSlot >= tabAmounts[settings[211]] + newSlot) {
                                        break heightLoop;
                                    }
                                }
                            }

                            int j3 = childX + (search ? (results % child.width) : i3) * (32 + child.inventoryMarginX);
                            int k3 = (childY + (search ? (results / child.width) : l2) * (32 + child.inventoryMarginY)) + heightShift;
                            if (itemSlot < 20) {
                                j3 += child.inventoryOffsetX[itemSlot];
                                k3 += child.inventoryOffsetY[itemSlot];
                            }
                            if (cursor_x >= j3 && cursor_y >= k3 && cursor_x < j3 + 32 && cursor_y < k3 + 32) {
                                mouseInvInterfaceIndex = itemSlot;
                                lastActiveInvInterface = child.id;
                                int item = child.inventoryItemId[itemSlot];
                                if (item > 0) {
                                    ItemDefinition itemDef = ItemDefinition.get(item - 1);
                                    if (item_highlighted == 1 && child.hasActions) {
                                        if (child.id != interfaceitemSelectionTypeIn
                                            || itemSlot != selectedItemIdSlot) {
                                            menuActionText[menuActionRow] = "Use " + selectedItemName
                                                + " with <col=FF9040>" + itemDef.name;
                                            menuActionTypes[menuActionRow] = 870;
                                            selectedMenuActions[menuActionRow] = itemDef.id;
                                            firstMenuAction[menuActionRow] = itemSlot;
                                            secondMenuAction[menuActionRow] = child.id;
                                            menuActionRow++;
                                        }
                                    } else if (widget_highlighted == 1 && child.hasActions) {
                                        if ((selectedTargetMask & 0x10) == 16) {
                                            menuActionText[menuActionRow] = selected_target_id + " <col=FF9040>"
                                                + itemDef.name;
                                            menuActionTypes[menuActionRow] = 543;
                                            selectedMenuActions[menuActionRow] = itemDef.id;
                                            firstMenuAction[menuActionRow] = itemSlot;
                                            secondMenuAction[menuActionRow] = child.id;
                                            menuActionRow++;
                                        }
                                    } else {
                                        if (child.hasActions) {
                                            for (int l3 = 4; l3 >= 3; l3--)
                                                if (itemDef.widget_actions != null
                                                    && itemDef.widget_actions[l3] != null) {
                                                    menuActionText[menuActionRow] = itemDef.widget_actions[l3]
                                                        + " <col=FF9040>" + itemDef.name;
                                                    if (l3 == 3)
                                                        menuActionTypes[menuActionRow] = 493;
                                                    if (l3 == 4)
                                                        menuActionTypes[menuActionRow] = 847;
                                                    selectedMenuActions[menuActionRow] = itemDef.id;
                                                    firstMenuAction[menuActionRow] = itemSlot;
                                                    secondMenuAction[menuActionRow] = child.id;
                                                    menuActionRow++;
                                                } else if (l3 == 4) {
                                                    menuActionText[menuActionRow] = "Drop <col=FF9040>" + itemDef.name;
                                                    menuActionTypes[menuActionRow] = 847;
                                                    selectedMenuActions[menuActionRow] = itemDef.id;
                                                    firstMenuAction[menuActionRow] = itemSlot;
                                                    secondMenuAction[menuActionRow] = child.id;
                                                    menuActionRow++;
                                                }
                                        }
                                        if (child.usableItems && settings[ConfigUtility.SHIFT_CLICK_ID] == 1 && isShiftPressed) {
                                            menuActionText[menuActionRow] = "Drop <col=FF9040>" + itemDef.name;
                                            menuActionTypes[menuActionRow] = 847;
                                            selectedMenuActions[menuActionRow] = itemDef.id;
                                            firstMenuAction[menuActionRow] = itemSlot;
                                            secondMenuAction[menuActionRow] = child.id;
                                            menuActionRow++;
                                        } else if (child.usableItems) {
                                            menuActionText[menuActionRow] = "Use <col=FF9040>" + itemDef.name;
                                            menuActionTypes[menuActionRow] = 447;
                                            selectedMenuActions[menuActionRow] = itemDef.id;
                                            firstMenuAction[menuActionRow] = itemSlot;
                                            secondMenuAction[menuActionRow] = child.id;
                                            menuActionRow++;
                                        }
                                        if (child.hasActions && itemDef.widget_actions != null) {
                                            for (int i4 = 2; i4 >= 0; i4--)
                                                if (itemDef.widget_actions[i4] != null) {
                                                    menuActionText[menuActionRow] = itemDef.widget_actions[i4] + " <col=FF9040>" + itemDef.name;
                                                    if (itemDef.widget_actions[i4].contains("Wield")
                                                        || itemDef.widget_actions[i4].contains("Wear")
                                                        || itemDef.widget_actions[i4].contains("Value")
                                                        || itemDef.widget_actions[i4].contains("Examine")) {
                                                        HoverMenuManager.showMenu = true;
                                                        HoverMenuManager.hintName = itemDef.name;
                                                        HoverMenuManager.hintId = itemDef.id;
                                                    } else {
                                                        HoverMenuManager.reset();
                                                    }
                                                    if (HoverMenuManager.showMenu && HoverMenuManager.drawType() == 1 && widget.parent != 33213) {
                                                        HoverMenuManager.drawHintMenu();
                                                    }
                                                    if (i4 == 0)
                                                        menuActionTypes[menuActionRow] = 74;
                                                    if (i4 == 1)
                                                        menuActionTypes[menuActionRow] = 454;
                                                    if (i4 == 2)
                                                        menuActionTypes[menuActionRow] = 539;
                                                    selectedMenuActions[menuActionRow] = itemDef.id;
                                                    firstMenuAction[menuActionRow] = itemSlot;
                                                    secondMenuAction[menuActionRow] = child.id;
                                                    menuActionRow++;
                                                }
                                            if (settings[ConfigUtility.SHIFT_CLICK_ID] == 1 && isShiftPressed) {
                                                menuActionText[menuActionRow] = "Drop <col=FF9040>" + itemDef.name;
                                                menuActionTypes[menuActionRow] = 847;
                                                selectedMenuActions[menuActionRow] = itemDef.id;
                                                firstMenuAction[menuActionRow] = itemSlot;
                                                secondMenuAction[menuActionRow] = child.id;
                                                menuActionRow++;
                                            }
                                        }
                                        int amount = 0;
                                        if (itemSlot != -1) {
                                            amount = child.inventoryAmounts[itemSlot];
                                        }
                                        if (child.actions != null) {
                                            int length = 6;

                                            boolean lootingBag = itemDef.id == ItemIdentifiers.LOOTING_BAG || itemDef.id == ItemIdentifiers.LOOTING_BAG_22586;
                                            if (child.id == 5064 && lootingBag) {
                                                child.actions = new String[]{"Check", null, null, null, null, null, null};
                                            } else if (child.id == 5064) {
                                                child.actions = new String[]{"Store 1", "Store 5", "Store 10", "Store All", "Store X", null, null};
                                            }
                                            if (child.parent == 5382) {
                                                if (amount != 0) {
                                                    child.actions = new String[]{"Withdraw-1", "Withdraw-5",
                                                        "Withdraw-10", "Withdraw-All", "Withdraw-X", null,
                                                        "Withdraw-All but one"};
                                                    if (modifiableXValue > 0) {
                                                        child.actions[5] = "Withdraw-" + modifiableXValue;
                                                    } else {
                                                        child.actions[5] = null;
                                                    }
                                                    boolean placeholder = Widget.cache[26101].active;
                                                    if (!placeholder) {
                                                        String[] newActions = new String[child.actions.length + 1];
                                                        for (int action = 0; action < newActions.length; action++) {
                                                            if (action == child.actions.length) {
                                                                newActions[action] = "Place holder";
                                                                continue;
                                                            }
                                                            newActions[action] = child.actions[action];
                                                        }
                                                        child.actions = newActions;
                                                        length = 7;
                                                    }
                                                } else {
                                                    menuActionsRow("Release <col=ff9040>" + itemDef.name, 1, 968, 2);
                                                }
                                            }

                                            if (amount != 0) {
                                                for (int type = length; type >= 0; type--) {
                                                    if (type > child.actions.length - 1)
                                                        continue;
                                                    if (type < child.actions.length && child.actions[type] != null) {
                                                        String action = child.actions[type];

                                                        // HARDCODING OF MENU ACTIONS
                                                        menuActionText[menuActionRow] = action + " <col=FF9040>"
                                                            + itemDef.name;
                                                        if (type == 0)
                                                            menuActionTypes[menuActionRow] = 632;
                                                        if (type == 1)
                                                            menuActionTypes[menuActionRow] = 78;
                                                        if (type == 2)
                                                            menuActionTypes[menuActionRow] = 867;
                                                        if (type == 3)
                                                            menuActionTypes[menuActionRow] = 431;
                                                        if (type == 4)
                                                            menuActionTypes[menuActionRow] = 53;
                                                        if (child.parent == 5382) {
                                                            if (child.actions[type] == null) {
                                                                if (type == 5)
                                                                    menuActionTypes[menuActionRow] = 291;
                                                            } else {
                                                                if (type == 5)
                                                                    menuActionTypes[menuActionRow] = 300;
                                                                if (type == 6)
                                                                    menuActionTypes[menuActionRow] = 291;
                                                            }
                                                        }
                                                        if (type == 7)
                                                            menuActionTypes[menuActionRow] = 968;

                                                        selectedMenuActions[menuActionRow] = itemDef.id;
                                                        firstMenuAction[menuActionRow] = itemSlot;
                                                        secondMenuAction[menuActionRow] = child.id;
                                                        menuActionRow++;
                                                    }
                                                }
                                            }
                                        }

                                        if (HoverMenuManager.shouldDraw(itemDef.id)) {
                                            HoverMenuManager.showMenu = true;
                                            HoverMenuManager.hintName = itemDef.name;
                                            HoverMenuManager.hintId = itemDef.id;
                                        }

                                        if (HoverMenuManager.showMenu && HoverMenuManager.drawType() == 1 && widget.parent != 3213) {
                                            HoverMenuManager.drawHintMenu();
                                        }

                                        if (amount != 0 || widget_overlay_id != 41000) { // No examine on
                                            // Slayer Rewards
                                            // interface
                                            //Code commented out by Suic, this is wrong.
                                            //if (!child.displayExamine)
                                            //    return;

                                            //If statement added by Suic
                                            if (child.displayExamine) {
                                                //If statement body moved into if statement by Suic
                                                menuActionText[menuActionRow] = (myPrivilege >= 2 && myPrivilege <= 4 && ClientConstants.DEBUG_MODE) ? "Examine <col=FF9040>" + itemDef.name + " <col=65280>(<col=FFFFFF>" + (child.inventoryItemId[itemSlot] - 1) + "<col=65280>) int: " + child.id : "Examine <col=FF9040>" + itemDef.name;
                                                menuActionTypes[menuActionRow] = 1125;
                                                selectedMenuActions[menuActionRow] = itemDef.id;
                                                firstMenuAction[menuActionRow] = itemSlot;
                                                secondMenuAction[menuActionRow] = child.id;
                                                menuActionRow++;
                                            }
                                        }
                                    }
                                }
                            }
                            itemSlot++;
                        }
                    }
                }
            }
        }
    }

    private void menuActionsRow(String action, int index, int actionId, int row) {
        if (menuOpen)
            return;
        menuActionText[index] = action;
        menuActionTypes[index] = actionId;
        menuActionRow = row;
    }

    private void checkHoverWithText(Widget childInterface, int i2, int j2) {
        if (!childInterface.clickable) {
            return;
        }
        if ((cursor_x < childInterface.width || cursor_y < childInterface.height || cursor_x > +childInterface.width
            || cursor_y > +childInterface.height)) {
            Widget.cache[childInterface.id + 1].textColour = 0xff981f;
        }
        if (cursor_x >= i2 && cursor_y >= j2 && cursor_x < i2 + childInterface.width
            && cursor_y < j2 + childInterface.height) {
            Widget.cache[childInterface.id + 1].textColour = 0xffffff;
        }

    }

    private boolean withinRange(int id, int min, int max) {
        return id >= min && id <= max;
    }

    private void checkFilters(Widget childInterface, int i2, int j2) {
        if (!childInterface.isRadioButton) {
            return;
        }
        if ((cursor_x < childInterface.width || cursor_y < childInterface.height || cursor_x > +childInterface.width
            || cursor_y > +childInterface.height) && childInterface.disabledSprite == spriteCache.get(167)) {
            childInterface.disabledSprite = spriteCache.get(166);
            childInterface.enabledSprite = spriteCache.get(166);
        }
        if (cursor_x >= i2 && cursor_y >= j2 && cursor_x < i2 + childInterface.width
            && cursor_y < j2 + childInterface.height && childInterface.disabledSprite == spriteCache.get(166)) {
            childInterface.disabledSprite = spriteCache.get(167);
            childInterface.enabledSprite = spriteCache.get(167);
        }
        if ((cursor_x < childInterface.width || cursor_y < childInterface.height || cursor_x > +childInterface.width
            || cursor_y > +childInterface.height) && childInterface.disabledSprite == spriteCache.get(170)) {
            childInterface.disabledSprite = spriteCache.get(169);
            childInterface.enabledSprite = spriteCache.get(169);
        }
        if (cursor_x >= i2 && cursor_y >= j2 && cursor_x < i2 + childInterface.width
            && cursor_y < j2 + childInterface.height && childInterface.disabledSprite == spriteCache.get(169)) {
            childInterface.disabledSprite = spriteCache.get(170);
            childInterface.enabledSprite = spriteCache.get(170);
        }
        if ((cursor_x < childInterface.width || cursor_y < childInterface.height || cursor_x > +childInterface.width
            || cursor_y > +childInterface.height) && childInterface.disabledSprite == spriteCache.get(172)) {
            childInterface.disabledSprite = spriteCache.get(171);
            childInterface.enabledSprite = spriteCache.get(171);
        }
        if (cursor_x >= i2 && cursor_y >= j2 && cursor_x < i2 + childInterface.width
            && cursor_y < j2 + childInterface.height && childInterface.disabledSprite == spriteCache.get(171)) {
            childInterface.disabledSprite = spriteCache.get(172);
            childInterface.enabledSprite = spriteCache.get(172);
        }
    }

    public void drawTransparentScrollBar(int x, int y, int height, int maxScroll, int pos) {
        spriteCache.get(29).drawAdvancedSprite(x, y, 120);
        spriteCache.get(30).drawAdvancedSprite(x, y + height - 16, 120);
        Rasterizer2D.drawTransparentVerticalLine(x, y + 16, height - 32, 0xffffff, 64);
        Rasterizer2D.drawTransparentVerticalLine(x + 15, y + 16, height - 32, 0xffffff, 64);
        int barHeight = (height - 32) * height / maxScroll;
        if (barHeight < 10) {
            barHeight = 10;
        }
        int barPos = 0;
        if (maxScroll != height) {
            barPos = (height - 32 - barHeight) * pos / (maxScroll - height);
        }
        Rasterizer2D.drawTransparentBoxOutline(x, y + 16 + barPos, 16,
            5 + y + 16 + barPos + barHeight - 5 - (y + 16 + barPos), 0xffffff, 32);
    }

    public void drawScrollbar(int height, int pos, int y, int x, int maxScroll, boolean transparent) {
        if (transparent) {
            drawTransparentScrollBar(x, y, height, maxScroll, pos);
        } else {
            scrollBar1.drawSprite(x, y);
            scrollBar2.drawSprite(x, (y + height) - 16);
            Rasterizer2D.draw_filled_rect(x, y + 16, 16, height - 32, 0x000001);
            Rasterizer2D.draw_filled_rect(x, y + 16, 15, height - 32, 0x3d3426);
            Rasterizer2D.draw_filled_rect(x, y + 16, 13, height - 32, 0x342d21);
            Rasterizer2D.draw_filled_rect(x, y + 16, 11, height - 32, 0x2e281d);
            Rasterizer2D.draw_filled_rect(x, y + 16, 10, height - 32, 0x29241b);
            Rasterizer2D.draw_filled_rect(x, y + 16, 9, height - 32, 0x252019);
            Rasterizer2D.draw_filled_rect(x, y + 16, 1, height - 32, 0x000001);
            int k1 = ((height - 32) * height) / maxScroll;
            if (k1 < 8) {
                k1 = 8;
            }
            int l1 = ((height - 32 - k1) * pos) / (maxScroll - height);
            Rasterizer2D.draw_filled_rect(x, y + 16 + l1, 16, k1, barFillColor);
            Rasterizer2D.draw_vertical_line(x, y + 16 + l1, k1, 0x000001);
            Rasterizer2D.draw_vertical_line(x + 1, y + 16 + l1, k1, 0x817051);
            Rasterizer2D.draw_vertical_line(x + 2, y + 16 + l1, k1, 0x73654a);
            Rasterizer2D.draw_vertical_line(x + 3, y + 16 + l1, k1, 0x6a5c43);
            Rasterizer2D.draw_vertical_line(x + 4, y + 16 + l1, k1, 0x6a5c43);
            Rasterizer2D.draw_vertical_line(x + 5, y + 16 + l1, k1, 0x655841);
            Rasterizer2D.draw_vertical_line(x + 6, y + 16 + l1, k1, 0x655841);
            Rasterizer2D.draw_vertical_line(x + 7, y + 16 + l1, k1, 0x61553e);
            Rasterizer2D.draw_vertical_line(x + 8, y + 16 + l1, k1, 0x61553e);
            Rasterizer2D.draw_vertical_line(x + 9, y + 16 + l1, k1, 0x5d513c);
            Rasterizer2D.draw_vertical_line(x + 10, y + 16 + l1, k1, 0x5d513c);
            Rasterizer2D.draw_vertical_line(x + 11, y + 16 + l1, k1, 0x594e3a);
            Rasterizer2D.draw_vertical_line(x + 12, y + 16 + l1, k1, 0x594e3a);
            Rasterizer2D.draw_vertical_line(x + 13, y + 16 + l1, k1, 0x514635);
            Rasterizer2D.draw_vertical_line(x + 14, y + 16 + l1, k1, 0x4b4131);
            Rasterizer2D.draw_horizontal_line(x, y + 16 + l1, 15, 0x000001);
            Rasterizer2D.draw_horizontal_line(x, y + 17 + l1, 15, 0x000001);
            Rasterizer2D.draw_horizontal_line(x, y + 17 + l1, 14, 0x655841);
            Rasterizer2D.draw_horizontal_line(x, y + 17 + l1, 13, 0x6a5c43);
            Rasterizer2D.draw_horizontal_line(x, y + 17 + l1, 11, 0x6d5f48);
            Rasterizer2D.draw_horizontal_line(x, y + 17 + l1, 10, 0x73654a);
            Rasterizer2D.draw_horizontal_line(x, y + 17 + l1, 7, 0x76684b);
            Rasterizer2D.draw_horizontal_line(x, y + 17 + l1, 5, 0x7b6a4d);
            Rasterizer2D.draw_horizontal_line(x, y + 17 + l1, 4, 0x7e6e50);
            Rasterizer2D.draw_horizontal_line(x, y + 17 + l1, 3, 0x817051);
            Rasterizer2D.draw_horizontal_line(x, y + 17 + l1, 2, 0x000001);
            Rasterizer2D.draw_horizontal_line(x, y + 18 + l1, 16, 0x000001);
            Rasterizer2D.draw_horizontal_line(x, y + 18 + l1, 15, 0x564b38);
            Rasterizer2D.draw_horizontal_line(x, y + 18 + l1, 14, 0x5d513c);
            Rasterizer2D.draw_horizontal_line(x, y + 18 + l1, 11, 0x625640);
            Rasterizer2D.draw_horizontal_line(x, y + 18 + l1, 10, 0x655841);
            Rasterizer2D.draw_horizontal_line(x, y + 18 + l1, 7, 0x6a5c43);
            Rasterizer2D.draw_horizontal_line(x, y + 18 + l1, 5, 0x6e6046);
            Rasterizer2D.draw_horizontal_line(x, y + 18 + l1, 4, 0x716247);
            Rasterizer2D.draw_horizontal_line(x, y + 18 + l1, 3, 0x7b6a4d);
            Rasterizer2D.draw_horizontal_line(x, y + 18 + l1, 2, 0x817051);
            Rasterizer2D.draw_horizontal_line(x, y + 18 + l1, 1, 0x000001);
            Rasterizer2D.draw_horizontal_line(x, y + 19 + l1, 16, 0x000001);
            Rasterizer2D.draw_horizontal_line(x, y + 19 + l1, 15, 0x514635);
            Rasterizer2D.draw_horizontal_line(x, y + 19 + l1, 14, 0x564b38);
            Rasterizer2D.draw_horizontal_line(x, y + 19 + l1, 11, 0x5d513c);
            Rasterizer2D.draw_horizontal_line(x, y + 19 + l1, 9, 0x61553e);
            Rasterizer2D.draw_horizontal_line(x, y + 19 + l1, 7, 0x655841);
            Rasterizer2D.draw_horizontal_line(x, y + 19 + l1, 5, 0x6a5c43);
            Rasterizer2D.draw_horizontal_line(x, y + 19 + l1, 4, 0x6e6046);
            Rasterizer2D.draw_horizontal_line(x, y + 19 + l1, 3, 0x73654a);
            Rasterizer2D.draw_horizontal_line(x, y + 19 + l1, 2, 0x817051);
            Rasterizer2D.draw_horizontal_line(x, y + 19 + l1, 1, 0x000001);
            Rasterizer2D.draw_horizontal_line(x, y + 20 + l1, 16, 0x000001);
            Rasterizer2D.draw_horizontal_line(x, y + 20 + l1, 15, 0x4b4131);
            Rasterizer2D.draw_horizontal_line(x, y + 20 + l1, 14, 0x544936);
            Rasterizer2D.draw_horizontal_line(x, y + 20 + l1, 13, 0x594e3a);
            Rasterizer2D.draw_horizontal_line(x, y + 20 + l1, 10, 0x5d513c);
            Rasterizer2D.draw_horizontal_line(x, y + 20 + l1, 8, 0x61553e);
            Rasterizer2D.draw_horizontal_line(x, y + 20 + l1, 6, 0x655841);
            Rasterizer2D.draw_horizontal_line(x, y + 20 + l1, 4, 0x6a5c43);
            Rasterizer2D.draw_horizontal_line(x, y + 20 + l1, 3, 0x73654a);
            Rasterizer2D.draw_horizontal_line(x, y + 20 + l1, 2, 0x817051);
            Rasterizer2D.draw_horizontal_line(x, y + 20 + l1, 1, 0x000001);
            Rasterizer2D.draw_vertical_line(x + 15, y + 16 + l1, k1, 0x000001);
            Rasterizer2D.draw_horizontal_line(x, y + 15 + l1 + k1, 16, 0x000001);
            Rasterizer2D.draw_horizontal_line(x, y + 14 + l1 + k1, 15, 0x000001);
            Rasterizer2D.draw_horizontal_line(x, y + 14 + l1 + k1, 14, 0x3f372a);
            Rasterizer2D.draw_horizontal_line(x, y + 14 + l1 + k1, 10, 0x443c2d);
            Rasterizer2D.draw_horizontal_line(x, y + 14 + l1 + k1, 9, 0x483e2f);
            Rasterizer2D.draw_horizontal_line(x, y + 14 + l1 + k1, 7, 0x4a402f);
            Rasterizer2D.draw_horizontal_line(x, y + 14 + l1 + k1, 4, 0x4b4131);
            Rasterizer2D.draw_horizontal_line(x, y + 14 + l1 + k1, 3, 0x564b38);
            Rasterizer2D.draw_horizontal_line(x, y + 14 + l1 + k1, 2, 0x000001);
            Rasterizer2D.draw_horizontal_line(x, y + 13 + l1 + k1, 16, 0x000001);
            Rasterizer2D.draw_horizontal_line(x, y + 13 + l1 + k1, 15, 0x443c2d);
            Rasterizer2D.draw_horizontal_line(x, y + 13 + l1 + k1, 11, 0x4b4131);
            Rasterizer2D.draw_horizontal_line(x, y + 13 + l1 + k1, 9, 0x514635);
            Rasterizer2D.draw_horizontal_line(x, y + 13 + l1 + k1, 7, 0x544936);
            Rasterizer2D.draw_horizontal_line(x, y + 13 + l1 + k1, 6, 0x564b38);
            Rasterizer2D.draw_horizontal_line(x, y + 13 + l1 + k1, 4, 0x594e3a);
            Rasterizer2D.draw_horizontal_line(x, y + 13 + l1 + k1, 3, 0x625640);
            Rasterizer2D.draw_horizontal_line(x, y + 13 + l1 + k1, 2, 0x6a5c43);
            Rasterizer2D.draw_horizontal_line(x, y + 13 + l1 + k1, 1, 0x000001);
            Rasterizer2D.draw_horizontal_line(x, y + 12 + l1 + k1, 16, 0x000001);
            Rasterizer2D.draw_horizontal_line(x, y + 12 + l1 + k1, 15, 0x443c2d);
            Rasterizer2D.draw_horizontal_line(x, y + 12 + l1 + k1, 14, 0x4b4131);
            Rasterizer2D.draw_horizontal_line(x, y + 12 + l1 + k1, 12, 0x544936);
            Rasterizer2D.draw_horizontal_line(x, y + 12 + l1 + k1, 11, 0x564b38);
            Rasterizer2D.draw_horizontal_line(x, y + 12 + l1 + k1, 10, 0x594e3a);
            Rasterizer2D.draw_horizontal_line(x, y + 12 + l1 + k1, 7, 0x5d513c);
            Rasterizer2D.draw_horizontal_line(x, y + 12 + l1 + k1, 4, 0x61553e);
            Rasterizer2D.draw_horizontal_line(x, y + 12 + l1 + k1, 3, 0x6e6046);
            Rasterizer2D.draw_horizontal_line(x, y + 12 + l1 + k1, 2, 0x7b6a4d);
            Rasterizer2D.draw_horizontal_line(x, y + 12 + l1 + k1, 1, 0x000001);
            Rasterizer2D.draw_horizontal_line(x, y + 11 + l1 + k1, 16, 0x000001);
            Rasterizer2D.draw_horizontal_line(x, y + 11 + l1 + k1, 15, 0x4b4131);
            Rasterizer2D.draw_horizontal_line(x, y + 11 + l1 + k1, 14, 0x514635);
            Rasterizer2D.draw_horizontal_line(x, y + 11 + l1 + k1, 13, 0x564b38);
            Rasterizer2D.draw_horizontal_line(x, y + 11 + l1 + k1, 11, 0x594e3a);
            Rasterizer2D.draw_horizontal_line(x, y + 11 + l1 + k1, 9, 0x5d513c);
            Rasterizer2D.draw_horizontal_line(x, y + 11 + l1 + k1, 7, 0x61553e);
            Rasterizer2D.draw_horizontal_line(x, y + 11 + l1 + k1, 5, 0x655841);
            Rasterizer2D.draw_horizontal_line(x, y + 11 + l1 + k1, 4, 0x6a5c43);
            Rasterizer2D.draw_horizontal_line(x, y + 11 + l1 + k1, 3, 0x73654a);
            Rasterizer2D.draw_horizontal_line(x, y + 11 + l1 + k1, 2, 0x7b6a4d);
            Rasterizer2D.draw_horizontal_line(x, y + 11 + l1 + k1, 1, 0x000001);
        }
    }

    private void updateNPCs(Buffer stream, int i) {
        removedMobCount = 0;
        mobsAwaitingUpdateCount = 0;
        method139(stream);
        updateNPCMovement(i, stream);
        npcUpdateMask(stream);
        for (int k = 0; k < removedMobCount; k++) {
            int l = removedMobs[k];
            if (npcs[l].time != game_tick) {
                npcs[l].desc = null;
                npcs[l] = null;
            }
        }

        if (stream.pos != i) {
            addReportToServer("NPC updating broke (stream position mismatch), this is very bad.");
            addReportToServer("Make sure to check buffer received datatypes in client match buffer sent datatypes from server.");
            SignLink.reporterror(myUsername + " size mismatch in getnpcpos - pos:" + stream.pos + " psize:" + i);
            throw new RuntimeException("eek");
        }
        for (int i1 = 0; i1 < npcs_in_region; i1++)
            if (npcs[local_npcs[i1]] == null) {
                addReportToServer("NPC updating broke, this is really bad.");
                SignLink.reporterror(myUsername + " null entry in npc list - pos:" + i1 + " size:" + npcs_in_region);
                throw new RuntimeException("eek");
            }

    }

    private int cButtonHPos;
    private int cButtonCPos;
    private int setChannel;

    public void processChatModeClick() {

        final int yOffset = screen == ScreenMode.FIXED ? 0 : window_height - 503;
        if (super.cursor_x >= 5 && super.cursor_x <= 61 && super.cursor_y >= yOffset + 482
            && super.cursor_y <= yOffset + 503) {
            cButtonHPos = 0;
            update_chat_producer = true;
        } else if (super.cursor_x >= 71 && super.cursor_x <= 127 && super.cursor_y >= yOffset + 482
            && super.cursor_y <= yOffset + 503) {
            cButtonHPos = 1;
            update_chat_producer = true;
        } else if (super.cursor_x >= 137 && super.cursor_x <= 193 && super.cursor_y >= yOffset + 482
            && super.cursor_y <= yOffset + 503) {
            cButtonHPos = 2;
            update_chat_producer = true;
        } else if (super.cursor_x >= 203 && super.cursor_x <= 259 && super.cursor_y >= yOffset + 482
            && super.cursor_y <= yOffset + 503) {
            cButtonHPos = 3;
            update_chat_producer = true;
        } else if (super.cursor_x >= 269 && super.cursor_x <= 325 && super.cursor_y >= yOffset + 482
            && super.cursor_y <= yOffset + 503) {
            cButtonHPos = 4;
            update_chat_producer = true;
        } else if (super.cursor_x >= 335 && super.cursor_x <= 391 && super.cursor_y >= yOffset + 482
            && super.cursor_y <= yOffset + 503) {
            cButtonHPos = 5;
            update_chat_producer = true;
        } else if (super.cursor_x >= 404 && super.cursor_x <= 515 && super.cursor_y >= yOffset + 482
            && super.cursor_y <= yOffset + 503) {
            cButtonHPos = 6;
            update_chat_producer = true;
        } else {
            cButtonHPos = -1;
            update_chat_producer = true;
        }
        if (super.click_type == 1) {
            if (super.click_x >= 5 && super.click_x <= 61 && super.click_y >= yOffset + 482
                && super.click_y <= yOffset + 505) {
                if (screen != ScreenMode.FIXED) {
                    if (setChannel != 0) {
                        cButtonCPos = 0;
                        chatTypeView = 0;
                        setChannel = 0;
                    } else {
                        showChatComponents = !showChatComponents;
                    }
                } else {
                    cButtonCPos = 0;
                    chatTypeView = 0;
                    setChannel = 0;
                }
            } else if (super.click_x >= 71 && super.click_x <= 127 && super.click_y >= yOffset + 482
                && super.click_y <= yOffset + 505) {
                if (screen != ScreenMode.FIXED) {
                    if (setChannel != 1) {
                        cButtonCPos = 1;
                        chatTypeView = 5;
                        setChannel = 1;
                    } else {
                        showChatComponents = !showChatComponents;
                    }
                } else {
                    cButtonCPos = 1;
                    chatTypeView = 5;
                    setChannel = 1;
                }
            } else if (super.click_x >= 137 && super.click_x <= 193 && super.click_y >= yOffset + 482
                && super.click_y <= yOffset + 505) {
                if (screen != ScreenMode.FIXED) {
                    if (setChannel != 2) {
                        cButtonCPos = 2;
                        chatTypeView = 1;
                        setChannel = 2;
                    } else {
                        showChatComponents = !showChatComponents;
                    }
                } else {
                    cButtonCPos = 2;
                    chatTypeView = 1;
                    setChannel = 2;
                }
            } else if (super.click_x >= 203 && super.click_x <= 259 && super.click_y >= yOffset + 482
                && super.click_y <= yOffset + 505) {
                if (screen != ScreenMode.FIXED) {
                    if (setChannel != 3) {
                        cButtonCPos = 3;
                        chatTypeView = 2;
                        setChannel = 3;
                    } else {
                        showChatComponents = !showChatComponents;
                    }
                } else {
                    cButtonCPos = 3;
                    chatTypeView = 2;
                    setChannel = 3;
                }
            } else if (super.click_x >= 269 && super.click_x <= 325 && super.click_y >= yOffset + 482
                && super.click_y <= yOffset + 505) {
                if (screen != ScreenMode.FIXED) {
                    if (setChannel != 4) {
                        cButtonCPos = 4;
                        chatTypeView = 11;
                        setChannel = 4;
                    } else {
                        showChatComponents = !showChatComponents;
                    }
                } else {
                    cButtonCPos = 4;
                    chatTypeView = 11;
                    setChannel = 4;
                }
            } else if (super.click_x >= 335 && super.click_x <= 391 && super.click_y >= yOffset + 482
                && super.click_y <= yOffset + 505) {
                if (screen != ScreenMode.FIXED) {
                    if (setChannel != 5) {
                        cButtonCPos = 5;
                        chatTypeView = 3;
                        setChannel = 5;
                    } else {
                        showChatComponents = !showChatComponents;
                    }
                } else {
                    cButtonCPos = 5;
                    chatTypeView = 3;
                    setChannel = 5;
                }
            } else if (super.click_x >= 404 && super.click_x <= 515 && super.click_y >= yOffset + 482 && super.click_y <= yOffset + 505) {
                if (widget_overlay_id == -1) {
                    clearTopInterfaces();
                    reportAbuseInput = "";
                    canMute = false;
                    for (int i = 0; i < Widget.cache.length; i++) {
                        if (Widget.cache[i] == null || Widget.cache[i].contentType != 600) {
                            continue;
                        }
                        reportAbuseInterfaceID = widget_overlay_id = Widget.cache[i].parent;
                        break;
                    }
                } else {
                    sendMessage("Please close the interface you have open before using this.", 0, "");
                }
            }
        }
    }

    public void updateVarp(int varpId) {
        int varpType = VariableParameter.values[varpId].type;
        //System.out.println(String.format("Varp %d type %d", varpId, varpType));
        if (varpType == 0)
            return;
        int state = settings[varpId];
        //System.out.println(String.format("\tVarp state %d", state));
        if (varpType == 1) {

            if (state == 1) {
                Rasterizer3D.adjust_brightness(0.90000000000000002D);
                setting.save();
            }

            if (state == 2) {
                Rasterizer3D.adjust_brightness(0.80000000000000004D);
                setting.save();
            }

            if (state == 3) {
                Rasterizer3D.adjust_brightness(0.69999999999999996D);
                setting.save();
            }

            if (state == 4) {
                Rasterizer3D.adjust_brightness(0.59999999999999998D);
                setting.save();
            }

            ItemSpriteFactory.sprites_cache.clear();
            ItemSpriteFactory.scaled_cache.clear();
            update_producers = true;
        }

        if (varpType == 3) {
            boolean flag1 = setting.toggle_music;
            if (state == 0) {
                if (SignLink.music != null)
                    adjust_volume(setting.toggle_music, 500);
                setting.toggle_music = true;
            }
            if (state == 1) {
                if (SignLink.music != null)
                    adjust_volume(setting.toggle_music, 300);
                setting.toggle_music = true;
            }
            if (state == 2) {
                if (SignLink.music != null)
                    adjust_volume(setting.toggle_music, 100);
                setting.toggle_music = true;
            }
            if (state == 3) {
                if (SignLink.music != null)
                    adjust_volume(setting.toggle_music, 0);
                setting.toggle_music = true;
            }
            if (state == 4)
                setting.toggle_music = false;
            if (setting.toggle_music != flag1 && !low_detail) {
                if (setting.toggle_music) {
                    next_track = current_track;
                    fade_audio = true;
                    resourceProvider.provide(2, next_track);
                } else {
                    stop_midi();
                }
                previous_track = 0;
            }
        }

        if (varpType == 4) {
            SoundPlayer.setVolume(state);
        }

        if (varpType == 5) {
            useOneMouseButton = state;
        }

        if (varpType == 6) {
            showSpokenEffects = state;
        }

        if (varpType == 8) {
            splitPrivateChat = state;
            update_chat_producer = true;
        }

        if (varpType == 9) {
            anInt913 = state;
        }
    }

    public void updateEntities() {
        try {
            int total_messages = 0;

            for (int entity_index = -1; entity_index < players_in_region + npcs_in_region; entity_index++) {
                final Entity entity;

                if (entity_index == -1) {
                    entity = local_player;
                } else if (entity_index < players_in_region) {
                    entity = players[local_players[entity_index]];
                } else {
                    entity = npcs[local_npcs[entity_index - players_in_region]];
                }

                if (entity == null || !entity.visible()) {
                    continue;
                }

                if (entity instanceof Npc) {
                    NpcDefinition entityDef = ((Npc) entity).desc;
                    if (entityDef.configs != null) {
                        entityDef = entityDef.get_configs();
                    }
                    if (entityDef == null) {
                        continue;
                    }
                }

                if (entity_index < players_in_region) {
                    int offset = 30;
                    final Player player = (Player) entity;
                    get_entity_scene_pos(entity, entity.height + 15);
                    if (scene_draw_x > -1) {
                        if (entity.game_tick_status > game_tick) {
                            if (setting.toggle_overhead_hp) {
                                offset = 5;
                                adv_font_small.draw_centered("<trans=220>" + entity.current_hitpoints + "/" + entity.maximum_hitpoints, scene_draw_x, scene_draw_y - offset, calcHitpointColor(entity), 1);
                                offset += (setting.toggle_overhead_names ? 12 : 35);
                            }
                        }

                        if (setting.toggle_overhead_names) {
                            int color = 0xffffff;
                            if (!setting.toggle_overhead_hp || entity.game_tick_status < game_tick)
                                offset = 5;

                            if (player != local_player)
                                color = 0x27c497;

                            adv_font_small.draw_centered(player.username, scene_draw_x, scene_draw_y - offset, color, 1);
                            offset += 35;
                        }
                    }
                    if (player.overhead_icon >= 0) {
                        if (scene_draw_x > -1) {
                            if (player.skull_icon < 5) {
                                skullIcons[player.skull_icon].drawSprite(scene_draw_x - 12, scene_draw_y - offset);
                                offset += 25;
                            }
                            if (player.overhead_icon < 18) {
                                headIcons[player.overhead_icon].drawSprite(scene_draw_x - 12, scene_draw_y - offset);
                                offset += 25;
                            }
                        }
                    }
                    if (entity_index >= 0 && hintIconDrawType == 10 && hintIconPlayerId == local_players[entity_index]) {
                        if (scene_draw_x > -1) {
                            offset += 13;
                            headIconsHint[player.hint_arrow_icon].drawSprite(scene_draw_x - 12, scene_draw_y - offset);
                        }
                    }
                } else {
                    int offset = 30;
                    Npc npc = ((Npc) entity);
                    NpcDefinition def = npc.desc;
                    get_entity_scene_pos(entity, entity.height + 15);
                    if (scene_draw_x > -1) {
                        if (npc.game_tick_status > game_tick) {
                            if (setting.toggle_npc_overhead_hp && def.cmb_level > 0) {
                                offset = 5;
                                adv_font_small.draw_centered("<trans=220>" + npc.current_hitpoints + "/" + npc.maximum_hitpoints, scene_draw_x, scene_draw_y - offset, calcHitpointColor(npc), 1);
                                offset += (setting.toggle_npc_overhead_names ? 12 : 35);
                            }
                        }
                        if (setting.toggle_npc_overhead_names) {
                            if (!setting.toggle_npc_overhead_hp || def.cmb_level == 0 || npc.game_tick_status < game_tick)
                                offset = 5;

                            adv_font_small.draw_centered("<trans=140>" + (def.cmb_level == 0 ? def.name : def.name + get_level_diff(local_player.combat_level, def.cmb_level) + " (level-" + def.cmb_level + ")"), scene_draw_x, scene_draw_y - offset, 0xffff33, 1);
                            offset += 35;
                        }
                    }
                    if (npc.getHeadIcon() >= 0 && npc.getHeadIcon() < headIcons.length) {
                        if (scene_draw_x > -1)
                            headIcons[npc.getHeadIcon()].drawSprite(scene_draw_x - 12, scene_draw_y - offset);

                        offset += 25;
                    }
                    if (hintIconDrawType == 1 && hintIconNpcId == local_npcs[entity_index - players_in_region] && game_tick % 20 < 10) {
                        if (scene_draw_x > -1) {
                            headIconsHint[0].drawSprite(scene_draw_x - 12, scene_draw_y - 28);
                        }
                    }
                }
                if (entity.entity_message != null && (entity_index >= players_in_region || set_public_channel == 0 || set_public_channel == 3 || set_public_channel == 1 && check_username(((Player) entity).username))) {
                    get_entity_scene_pos(entity, entity.height);
                    if (scene_draw_x > -1 && total_messages < spokenMaxCount) {
                        scene_text_center_x[total_messages] = adv_font_bold.get_width(((Entity) (entity)).entity_message) / 2;
                        scene_text_height[total_messages] = adv_font_bold.base_char_height;
                        scene_text_x[total_messages] = scene_draw_x;
                        scene_text_y[total_messages] = scene_draw_y;
                        scene_text_color[total_messages] = entity.textColour;
                        scene_text_effect[total_messages] = entity.textEffect;
                        scene_text_update_cycle[total_messages] = entity.message_cycle;
                        spokenMessage[total_messages++] = entity.entity_message;
                        // System.out.println("colorCode: "+entity.textColour+" vs effectCode:
                        // "+entity.textEffect);
                        if (showSpokenEffects == 0 && entity.textEffect >= 1 && entity.textEffect <= 3) {
                            scene_text_height[total_messages] += 10;
                            scene_text_y[total_messages] += 5;
                        }

                        if (showSpokenEffects == 0 && entity.textEffect == 4) {
                            scene_text_center_x[total_messages] = 60;
                        }

                        if (showSpokenEffects == 0 && entity.textEffect == 5) {
                            scene_text_height[total_messages] += 5;
                        }
                    }
                }
                // hitmarks
                for (int hit_index = 0; hit_index < 4; hit_index++) {
                    if (entity.damage_cycle[hit_index] > game_tick) {
                        get_entity_scene_pos(entity, entity.height / 2);
                        if (scene_draw_x > -1) {
                            if (hit_index == 1) {// top
                                scene_draw_y -= 20;
                            }
                            if (hit_index == 2) {// left
                                scene_draw_x -= 15;
                                scene_draw_y -= 10;
                            }

                            if (hit_index == 3) {// right
                                scene_draw_x += 15;
                                scene_draw_y -= 10;
                            }

                            hitMarks[entity.damage_marker[hit_index]].drawSprite(scene_draw_x - 12, scene_draw_y - 12);
                            adv_font_small.draw_centered("" + entity.damage_dealt[hit_index] * 1, scene_draw_x, scene_draw_y + 4, 0xffffff, 1);
                        }
                    }
                }

                if (entity.game_tick_status > game_tick) {
                    try {
                        //hitpoint bar
                        get_entity_scene_pos(entity, entity.height + 15);
                        if (scene_draw_x > -1) {
                            int drawingWidth = 30;
                            if (entity instanceof Npc) {
                                final Npc npc = (Npc) entity;
                                if (npc.desc.id == 2668 || npc.desc.id == 7413) {
                                    continue;
                                }
                                if (npc.desc.largeHpBar)
                                    drawingWidth = npc.desc.occupied_tiles * 30;
                            }
                            int filledPixels = (entity.current_hitpoints * drawingWidth) / entity.maximum_hitpoints;
                            if (filledPixels > drawingWidth) {
                                filledPixels = drawingWidth;
                            }
                            Rasterizer2D.drawPixels(5, scene_draw_y - 3, scene_draw_x - drawingWidth / 2, 65280, filledPixels);
                            Rasterizer2D.drawPixels(5, scene_draw_y - 3, (scene_draw_x - drawingWidth / 2) + filledPixels, 0xff0000, drawingWidth - filledPixels);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        addReportToServer(e.getMessage());
                    }
                }
            }
            for (int message_index = 0; message_index < total_messages; message_index++) {
                int raster_x = scene_text_x[message_index];
                int raster_y = scene_text_y[message_index];
                int center_x = scene_text_center_x[message_index];
                int message_height = scene_text_height[message_index];
                boolean update = true;
                while (update) {
                    update = false;
                    for (int index = 0; index < message_index; index++) {
                        if (raster_y + 2 > scene_text_y[index] - scene_text_height[index]
                            && raster_y - message_height < scene_text_y[index] + 2
                            && raster_x - center_x < scene_text_x[index] + scene_text_center_x[index]
                            && raster_x + center_x > scene_text_x[index] - scene_text_center_x[index]
                            && scene_text_y[index] - scene_text_height[index] < raster_y) {
                            raster_y = scene_text_y[index] - scene_text_height[index];
                            update = true;
                        }
                    }
                }
                scene_draw_x = scene_text_x[message_index];
                scene_draw_y = scene_text_y[message_index] = raster_y;
                String message = spokenMessage[message_index];
                // System.out.println("showSpokenEffects: "+showSpokenEffects);
                if (showSpokenEffects == 0) {
                    int color = 0xffff00;
                    if (scene_text_color[message_index] < 6) // send_color
                        color = SPOKEN_PALETTE[scene_text_color[message_index]];
                    if (scene_text_color[message_index] == 6) // flash1
                        color = render_cycle % 20 >= 10 ? 0xffff00 : 0xff0000;
                    if (scene_text_color[message_index] == 7) // flash2
                        color = render_cycle % 20 >= 10 ? 65535 : 255;
                    if (scene_text_color[message_index] == 8) // flash3
                        color = render_cycle % 20 >= 10 ? 0x80ff80 : 45056;
                    if (scene_text_color[message_index] == 9) { // glow1
                        int timer = 150 - scene_text_update_cycle[message_index];
                        if (timer < 50)
                            color = 0xff0000 + 1280 * timer;
                        else if (timer < 100)
                            color = 0xffff00 - 0x50000 * (timer - 50);
                        else if (timer < 150)
                            color = 65280 + 5 * (timer - 100);
                    }
                    if (scene_text_color[message_index] == 10) { // glow2
                        int cycle = 150 - scene_text_update_cycle[message_index];
                        if (cycle < 50)
                            color = 0xff0000 + 5 * cycle;
                        else if (cycle < 100)
                            color = 0xff00ff - 0x50000 * (cycle - 50);
                        else if (cycle < 150)
                            color = (255 + 0x50000 * (cycle - 100)) - 5 * (cycle - 100);
                    }
                    if (scene_text_color[message_index] == 11) {// glow3
                        int cycle = 150 - scene_text_update_cycle[message_index];// next change
                        if (cycle < 50)
                            color = 0xffffff - 0x50005 * cycle;
                        else if (cycle < 100)
                            color = 65280 + 0x50005 * (cycle - 50);
                        else if (cycle < 150)
                            color = 0xffffff - 0x50000 * (cycle - 100);
                    }
                    if (scene_text_effect[message_index] == ClientConstants.NO_EFFECT) {
                        adv_font_bold.draw_centered(message, scene_draw_x, scene_draw_y, color, true);
                    }
                    if (scene_text_effect[message_index] == ClientConstants.WAVE) {
                        adv_font_bold.draw_wave(message, scene_draw_x, scene_draw_y, color, 0, render_cycle);
                    }
                    if (scene_text_effect[message_index] == ClientConstants.WAVE_2) {
                        adv_font_bold.draw_wave2(message, scene_draw_x, scene_draw_y, color, 0, render_cycle);
                    }
                    if (scene_text_effect[message_index] == ClientConstants.SHAKE) {
                        adv_font_bold.draw_shake(message, scene_draw_x, scene_draw_y, color, 0, render_cycle,
                            150 - scene_text_update_cycle[message_index]);
                    }
                    if (scene_text_effect[message_index] == ClientConstants.SCROLL) {
                        int width = adv_font_bold.get_width(message);
                        int horizontal_offset = ((150 - scene_text_update_cycle[message_index]) * (width + 100)) / 150;
                        Rasterizer2D.set_clip(scene_draw_x - 50, 0, scene_draw_x + 50, 334);
                        adv_font_bold.draw(message, (scene_draw_x + 50) - horizontal_offset, scene_draw_y, color, true);
                        Rasterizer2D.set_default_size();
                    }
                    if (scene_text_effect[message_index] == ClientConstants.SLIDE) {
                        int delay = 150 - scene_text_update_cycle[message_index];
                        int vertical_offset = 0;
                        if (delay < 25)
                            vertical_offset = delay - 25;
                        else if (delay > 125)
                            vertical_offset = delay - 125;

                        Rasterizer2D.set_clip(0, scene_draw_y - adv_font_bold.base_char_height - 1, 512,
                            scene_draw_y + 5);
                        adv_font_bold.draw_centered(message, scene_draw_x, scene_draw_y + 1 + vertical_offset, color,
                            true);
                        Rasterizer2D.set_default_size();
                    }
                } else {
                    adv_font_bold.draw_centered(message, scene_draw_x, scene_draw_y + 1, 0xffff00, true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            addReportToServer(e.getMessage());
        }
    }

    /**
     * All the tab identifications
     * <p>
     * ATTACK_TAB = 0 SKILL_TAB = 1 QUEST_TAB = 2 INVENTORY_TAB = 3 EQUIPMENT_TAB =
     * 4 PRAYER_TAB = 5 MAGIC_TAB = 6 CLAN_TAB = 7 FRIENDS_TAB = 8 IGNORE_TAB = 9
     * LOGOUT_TAB = 10 OPTIONS_TAB = 11 EMOTE_TAB = 12 MUSIC_TAB = 13
     */
    private final int[] fixed_side_icon = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13},
        fixed_tab_id = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13},
        fixed_side_icon_x = {17, 49, 83, 114, 146, 180, 214, 16, 49, 82, 116, 148, 184, 217},
        fixed_side_icon_y = {9, 7, 6, 5, 2, 3, 7, 303, 306, 306, 302, 305, 303, 304, 303};

    private final int[] resizable_side_icon = {0, 1, 2, 3, 4, 5, 6, -1, 8, 9, 7, 11, 12, 13},
        resizable_side_icon_x = {219, 189, 156, 126, 93, 62, 30, 219, 189, 156, 124, 92, 59, 28},
        resizable_side_icon_y = {66, 67, 67, 69, 71, 70, 67, 32, 31, 31, 32, 31, 33, 32, 33};

    private final int[] fullscreen_resizable_side_icon = {0, 1, 2, 3, 4, 5, 6, -1, 8, 9, 7, 11, 12, 13},
        fullscreen_resizable_side_icon_x = {50, 80, 114, 143, 176, 208, 240, 242, 273, 306, 338, 370, 404, 433},
        fullscreen_resizable_side_icon_y = {30, 31, 30, 32, 35, 34, 30, 31, 31, 31, 32, 31, 32, 32, 32};
    private int questTabId;
    public boolean isViewingQuests = true;
    public int spellbook = 0;

    /**
     * This method draws the icons on the tab interfaces
     */
    private void drawSideIcons() {
        int x = screen == ScreenMode.FIXED ? 0 : window_width - 247;
        int y = screen == ScreenMode.FIXED ? 0 : window_height - 336;

        if (screen == ScreenMode.FIXED
            || screen != ScreenMode.FIXED && settings[ConfigUtility.SIDE_STONES_ARRANGEMENT_ID] == 0) {
            for (int tab = 0; tab < fixed_tab_id.length; tab++) {
                if (tabInterfaceIDs[fixed_tab_id[tab]] != -1) {
                    if (fixed_side_icon[tab] != -1) {
                        SimpleImage sprite = sideIcons[fixed_side_icon[tab]];
                        // Replace music tab icon with pvp icon
                        //if (ClientConstants.PVP_MODE) {
                        if (tab == 13) {
                            spriteCache.get(336).drawAdvancedSprite(fixed_side_icon_x[tab] + x, fixed_side_icon_y[tab] + y);
                            continue;
                        }
                        //}
                        if (tab == 2) {
                            if (questTabId == 0) {
                                sprite.drawSprite(fixed_side_icon_x[tab] + x + 1, fixed_side_icon_y[tab] + y + 1);
                                continue;
                            } else if (questTabId == 1) {
                                spriteCache.get(456).drawSprite(fixed_side_icon_x[tab] + x - 3, fixed_side_icon_y[tab] + y - 5);
                                continue;
                            } else if (questTabId == 2) {
                                spriteCache.get(848).drawSprite(fixed_side_icon_x[tab] + x - 3, fixed_side_icon_y[tab] + y - 5);
                                continue;
                            }
                        } else if (tab == 6) {
                            if (spellbook == 0) {
                                sprite.drawSprite(fixed_side_icon_x[tab] + x, fixed_side_icon_y[tab] + y);
                                continue;
                            } else if (spellbook == 1) {
                                spriteCache.get(772).drawSprite(fixed_side_icon_x[tab] + x, fixed_side_icon_y[tab] + y);
                                continue;
                            } else if (spellbook == 2) {
                                spriteCache.get(773).drawSprite(fixed_side_icon_x[tab] + x, fixed_side_icon_y[tab] + y);
                                continue;
                            }
                        }
                        sprite.drawSprite(fixed_side_icon_x[tab] + x, fixed_side_icon_y[tab] + y);
                    }
                }
            }
        }

        // Resizeable
        if (screen != ScreenMode.FIXED && settings[ConfigUtility.SIDE_STONES_ARRANGEMENT_ID] == 1
            && window_width < 1000) {
            for (int tab = 0; tab < fixed_tab_id.length; tab++) {
                if (tabInterfaceIDs[fixed_tab_id[tab]] != -1) {
                    if (resizable_side_icon[tab] != -1) {
                        SimpleImage sprite = sideIcons[resizable_side_icon[tab]];
                        if (tab == 13) {
                            spriteCache.get(336).drawAdvancedSprite(window_width - resizable_side_icon_x[tab], window_height - resizable_side_icon_y[tab]);
                            continue;
                        }
                        if (tab == 2) {
                            if (questTabId == 0) {
                                sprite.drawSprite(window_width - resizable_side_icon_x[tab], window_height - resizable_side_icon_y[tab]);
                                continue;
                            } else if (questTabId == 1) {
                                spriteCache.get(456).drawSprite(window_width - resizable_side_icon_x[tab], window_height - resizable_side_icon_y[tab]);
                                continue;
                            } else if (questTabId == 2) {
                                spriteCache.get(848).drawSprite(window_width - resizable_side_icon_x[tab], window_height - resizable_side_icon_y[tab]);
                                continue;
                            }
                        } else if (tab == 6) {
                            if (spellbook == 0) {
                                sprite.drawSprite(window_width - resizable_side_icon_x[tab], window_height - resizable_side_icon_y[tab]);
                                continue;
                            } else if (spellbook == 1) {
                                spriteCache.get(772).drawSprite(window_width - resizable_side_icon_x[tab], window_height - resizable_side_icon_y[tab]);
                                continue;
                            } else if (spellbook == 2) {
                                spriteCache.get(773).drawSprite(window_width - resizable_side_icon_x[tab], window_height - resizable_side_icon_y[tab]);
                                continue;
                            }
                        } else {
                            sprite.drawSprite(window_width - resizable_side_icon_x[tab], window_height - resizable_side_icon_y[tab]);
                        }
                    }
                }
            }
        }

        // Fullscreen resizable
        if (screen != ScreenMode.FIXED && settings[ConfigUtility.SIDE_STONES_ARRANGEMENT_ID] == 1
            && window_width >= 1000) {
            for (int tab = 0; tab < fixed_tab_id.length; tab++) {
                if (tabInterfaceIDs[fixed_tab_id[tab]] != -1) {
                    if (fullscreen_resizable_side_icon[tab] != -1) {
                        SimpleImage sprite = sideIcons[fullscreen_resizable_side_icon[tab]];
                        if (tab == 13) {
                            spriteCache.get(336).drawAdvancedSprite(
                                window_width - 461 + fullscreen_resizable_side_icon_x[tab],
                                window_height - fullscreen_resizable_side_icon_y[tab]);
                            continue;
                        }
                        if (tab == 2) {
                            if (questTabId == 0) {
                                sprite.drawSprite(window_width - 461 + fullscreen_resizable_side_icon_x[tab], window_height - fullscreen_resizable_side_icon_y[tab]);
                                continue;
                            } else if (questTabId == 1) {
                                spriteCache.get(456).drawSprite(window_width - 461 + fullscreen_resizable_side_icon_x[tab], window_height - fullscreen_resizable_side_icon_y[tab]);
                                continue;
                            } else if (questTabId == 2) {
                                spriteCache.get(848).drawSprite(window_width - 461 + fullscreen_resizable_side_icon_x[tab], window_height - fullscreen_resizable_side_icon_y[tab]);
                                continue;
                            }
                        } else if (tab == 6) {
                            if (spellbook == 0) {
                                sprite.drawSprite(window_width - 461 + fullscreen_resizable_side_icon_x[tab], window_height - fullscreen_resizable_side_icon_y[tab]);
                                continue;
                            } else if (spellbook == 1) {
                                spriteCache.get(772).drawSprite(window_width - 461 + fullscreen_resizable_side_icon_x[tab], window_height - fullscreen_resizable_side_icon_y[tab]);
                                continue;
                            } else if (spellbook == 2) {
                                spriteCache.get(773).drawSprite(window_width - 461 + fullscreen_resizable_side_icon_x[tab], window_height - fullscreen_resizable_side_icon_y[tab]);
                                continue;
                            }
                        }
                        sprite.drawSprite(window_width - 461 + fullscreen_resizable_side_icon_x[tab], window_height - fullscreen_resizable_side_icon_y[tab]);
                    }
                }
            }
        }
    }

    private final int[] fixed_red_stones_id = {35, 39, 39, 39, 39, 39, 36, 37, 39, 39, 39, 39, 39, 38},
        fixed_red_stones_x = {6, 44, 77, 110, 143, 176, 209, 6, 44, 77, 110, 143, 176, 209},
        fixed_red_stones_y = {0, 0, 0, 0, 0, 0, 0, 298, 298, 298, 298, 298, 298, 298};

    private final int[] resizable_red_stones_x = {226, 194, 162, 130, 99, 65, 34, 219, 195, 161, 130, 98, 65, 33},
        resizable_red_stones_y = {73, 73, 73, 73, 73, 73, 73, -1, 37, 37, 37, 37, 37, 37, 37};

    private final int[] fullscreen_resizable_red_stones_x = {417, 385, 353, 321, 289, 256, 224, 129, 193, 161, 130, 98,
        65, 33};

    private void drawRedStones() {
        int x = screen == ScreenMode.FIXED ? 0 : window_width - 247;
        int y = screen == ScreenMode.FIXED ? 0 : window_height - 336;

        // Fixed game mode
        if (screen == ScreenMode.FIXED
            || screen != ScreenMode.FIXED && settings[ConfigUtility.SIDE_STONES_ARRANGEMENT_ID] == 0) {
            if (tabInterfaceIDs[sidebarId] != -1 && sidebarId != 15) {
                spriteCache.get(fixed_red_stones_id[sidebarId]).drawSprite(fixed_red_stones_x[sidebarId] + x,
                    fixed_red_stones_y[sidebarId] + y);
            }
        }

        // Resizable game mode
        if (screen != ScreenMode.FIXED && settings[ConfigUtility.SIDE_STONES_ARRANGEMENT_ID] == 1
            && window_width < 1000) {
            if (tabInterfaceIDs[sidebarId] != -1 && sidebarId != 10 && showTabComponents) {
                if (sidebarId == 7) {
                    spriteCache.get(39).drawSprite(window_width - 130, window_height - 37);
                }
                spriteCache.get(39).drawSprite(window_width - resizable_red_stones_x[sidebarId],
                    window_height - resizable_red_stones_y[sidebarId]);
            }
        }

        // Fullscreen resizable game mode
        if (screen != ScreenMode.FIXED && settings[ConfigUtility.SIDE_STONES_ARRANGEMENT_ID] == 1
            && window_width >= 1000) {
            if (tabInterfaceIDs[sidebarId] != -1 && sidebarId != 10 && showTabComponents) {
                spriteCache.get(39).drawSprite(window_width - fullscreen_resizable_red_stones_x[sidebarId],
                    window_height - 37);
            }
        }
    }

    public String getNameForTab(int tab) {
        switch (tab) {
            case 0:
                return "Combat";
            case 1:
                return "Stats";
            case 2:
                return "Spawn tab";
            case 3:
                return "Inventory";
            case 4:
                return "Equipment";
            case 5:
                return "Prayer";
            case 6:
                return "Magic";
            case 7:
                return "Clan chat";
            case 8:
                return "Friends";
            case 9:
                return "Ignores";
            case 10:
                return "Logout";
            case 11:
                return "Settings";
            case 12:
                return "Emotes";
            case 13:
                return "PvP";
        }
        return "";
    }

    private void drawTabArea() {
        final int xOffset = screen == ScreenMode.FIXED ? 0 : window_width - 241;
        final int yOffset = screen == ScreenMode.FIXED ? 0 : window_height - 336;
        if (tabImageProducer != null && screen == ScreenMode.FIXED) {
            tabImageProducer.init();
        }
        Rasterizer3D.line_offsets = anIntArray1181;
        if (screen == ScreenMode.FIXED) {
            spriteCache.get(21).drawSprite(0, 0);
        } else if (screen != ScreenMode.FIXED && settings[ConfigUtility.SIDE_STONES_ARRANGEMENT_ID] == 0) {
            Rasterizer2D.draw_filled_rect(window_width - 217, window_height - 304, 195, 270, 0x3E3529,
                settings[ConfigUtility.TRANSPARENT_SIDE_PANEL_ID] == 1 ? 80 : 256);
            spriteCache.get(47).drawSprite(xOffset, yOffset);
        } else {
            if (window_width >= 1000) {
                if (showTabComponents) {
                    Rasterizer2D.draw_filled_rect(window_width - 197, window_height - 304, 197, 265, 0x3E3529,
                        settings[ConfigUtility.TRANSPARENT_SIDE_PANEL_ID] == 1 ? 80 : 256);
                    spriteCache.get(50).drawSprite(window_width - 204, window_height - 311);
                }
                for (int x = window_width - 417, y = window_height - 37, index = 0; x <= window_width - 30
                    && index < 13; x += 32, index++) {
                    spriteCache.get(46).drawSprite(x, y);
                }
            } else if (window_width < 1000) {
                if (showTabComponents) {
                    Rasterizer2D.draw_filled_rect(window_width - 197, window_height - 341, 195, 265, 0x3E3529,
                        settings[ConfigUtility.TRANSPARENT_SIDE_PANEL_ID] == 1 ? 80 : 256);
                    spriteCache.get(50).drawSprite(window_width - 204, window_height - 348);
                }
                for (int x = window_width - 226, y = window_height - 73, index = 0; x <= window_width - 32
                    && index < 7; x += 32, index++) {
                    spriteCache.get(46).drawSprite(x, y);
                }
                for (int x = window_width - 226, y = window_height - 37, index = 0; x <= window_width - 32
                    && index < 7; x += 32, index++) {
                    spriteCache.get(46).drawSprite(x, y);
                }
            }
        }
        if (overlayInterfaceId == -1) {
            drawRedStones();
            drawSideIcons();
        }
        if (showTabComponents) {
            int x = screen == ScreenMode.FIXED ? 31 : window_width - 215;
            int y = screen == ScreenMode.FIXED ? 37 : window_height - 299;
            if (screen != ScreenMode.FIXED && settings[ConfigUtility.SIDE_STONES_ARRANGEMENT_ID] == 1) {
                x = window_width - 197;
                y = window_width >= 1000 ? window_height - 303 : window_height - 340;
            }
            try {
                if (overlayInterfaceId != -1) {
                    drawInterface(Widget.cache[overlayInterfaceId], x, y, 0);
                } else if (tabInterfaceIDs[sidebarId] != -1) {
                    drawInterface(Widget.cache[tabInterfaceIDs[sidebarId]], x, y, 0);
                    if (sidebarId == 5 && prayerGrabbed != null) {
                        Widget.cache[prayerGrabbed.spriteId].enabledSprite.draw_transparent(
                            screen == ScreenMode.FIXED ? super.cursor_x - 528 : super.cursor_x - 12,
                            screen == ScreenMode.FIXED ? super.cursor_y - 180 : super.cursor_y - 12, 100);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                addReportToServer(ex.getMessage());
            }
        }

        bars.drawStatusBars(xOffset, yOffset);

        if (menuOpen) {
            drawMenu(screen == ScreenMode.FIXED ? 516 : 0, screen == ScreenMode.FIXED ? 168 : 0);
        }
        if (HoverMenuManager.showMenu) {
            HoverMenuManager.drawHintMenu();
        }
        if (screen == ScreenMode.FIXED && tabImageProducer != null) {
            tabImageProducer.drawGraphics(168, super.graphics, 516);
            gameScreenImageProducer.init();
        }
        Rasterizer3D.line_offsets = viewportOffsets;
    }

    public enum AnimatedTextureStore {
        WATER_DROPLETS(17, 1, false),
        WATER(24, 1, false),
        MAGIC_TREE_STARS(34, 1, false),
        LAVA(40, 1, true),
        INFERNAL_LAVA_54(54, 1, false),
        CRIMSON_LAVA(56, 1, false),
        GRAY_LAVA(57, 1, false),
        INFERNAL_LAVA(59, 1, false),
        TEXTURE_61(61, 1, false),
        TEXTURE_62(62, 1, false),
        TEXTURE_63(63, 1, false),
        TEXTURE_64(64, 1, false),
        TEXTURE_65(65, 1, false),
        TEXTURE_66(66, 1, false),
        TEXTURE_67(67, 1, false),
        TEXTURE_68(68, 1, false),
        TEXTURE_69(69, 1, false),
        TEXTURE_71(71, 1, false),
        TEXTURE_72(72, 1, false),
        TEXTURE_73(73, 1, false),
        TEXTURE_74(74, 1, false),
        TEXTURE_75(75, 1, false),
        TEXTURE_76(76, 1, false),
        TEXTURE_77(77, 1, false),
        TEXTURE_78(78, 1, false),
        TEXTURE_79(79, 1, false),
        TEXTURE_80(80, 1, false),
        TEXTURE_81(81, 1, false),
        TEXTURE_82(82, 1, false),
        TEXTURE_83(83, 1, false),
        TEXTURE_84(84, 1, false),
        TEXTURE_85(85, 1, false),
        TEXTURE_86(86, 1, false),
        TEXTURE_87(87, 1, false),
        TEXTURE_88(88, 1, false),
        TEXTURE_89(89, 1, false),
        TEXTURE_90(90, 1, false),
        TEXTURE_91(91, 1, false),
        TEXTURE_92(92, 1, false),
        TEXTURE_93(93, 1, false),
        TEXTURE_94(94, 1, false),
        TEXTURE_95(95, 1, false),
        TEXTURE_96(96, 1, false),
        TEXTURE_97(97, 1, false),
        TEXTURE_98(98, 1, false),
        TEXTURE_99(99, 1, false),
        TEXTURE_100(100, 1, false),
        TEXTURE_101(101, 1, false),
        TEXTURE_102(102, 1, false),
        TEXTURE_103(103, 1, false),
        TEXTURE_104(104, 1, false),
        TEXTURE_105(105, 1, false),
        TEXTURE_106(106, 1, false),
        TEXTURE_107(107, 1, false),
        TEXTURE_108(108, 1, false),
        TEXTURE_109(109, 1, false),
        TEXTURE_110(110, 1, false),
        TEXTURE_111(111, 1, false),
        TEXTURE_112(112, 1, false),
        TEXTURE_113(113, 1, false),
        TEXTURE_114(114, 1, false),
        TEXTURE_115(115, 1, false),
        TEXTURE_116(116, 1, false),
        TEXTURE_117(117, 1, false),
        TEXTURE_118(118, 1, false),
        TEXTURE_119(119, 1, false),
        TEXTURE_120(120, 1, false),
        TEXTURE_121(121, 1, false),
        TEXTURE_122(122, 1, false);

        private final int material_id;
        private final int speed;
        private final boolean reverse;

        AnimatedTextureStore(int material_id, int speed, boolean reverse) {
            this.material_id = material_id;
            this.speed = speed;
            this.reverse = reverse;
        }

        public int get_id() {
            return material_id;
        }

        public int get_speed() {
            return speed;
        }

        public boolean get_dir() {
            return reverse;
        }
    }

    private void render_animated_textures() {
        if (low_detail)
            return;
        for (AnimatedTextureStore data : AnimatedTextureStore.values()) {
            if (Rasterizer3D.cache[data.get_id()] > 0) {
                animate_pallete(data.get_id(), data.get_speed(), data.get_dir());
            }
        }
    }

    private void animate_pallete(int id, int speed, boolean reverse) {
        //draw_texture_backgrounds(id);

        IndexedImage image = Rasterizer3D.tex_images[id];
        int size = (image.width * image.height) - 1;
        int step = (int) ((image.width * animation_step * speed));
        byte[] raster = image.palettePixels;
        byte[] target = tmpTexture;
        for (int y = 0; y <= size; y++) {
            target[y] = raster[(reverse ? y + step & size : y - step & size)];
        }
        image.palettePixels = target;
        tmpTexture = raster;
        Rasterizer3D.reset_texel_pos(id);
    }

    private void processMobChatText() {
        for (int i = -1; i < players_in_region; i++) {
            int j;
            if (i == -1)
                j = LOCAL_PLAYER_INDEX;
            else
                j = local_players[i];
            Player player = players[j];
            if (player != null && player.message_cycle > 0) {
                player.message_cycle--;
                if (player.message_cycle == 0)
                    player.entity_message = null;
            }
        }
        for (int k = 0; k < npcs_in_region; k++) {
            int l = local_npcs[k];
            Npc npc = npcs[l];
            if (npc != null && npc.message_cycle > 0) {
                npc.message_cycle--;
                if (npc.message_cycle == 0)
                    npc.entity_message = null;
            }
        }
    }

    private void rotate_camera() {
        int x = camera_spin_x * 128 + 64;
        int y = camera_spin_y * 128 + 64;
        int z = get_tile_pos(plane, y, x) - camera_spin_z;
        if (camera_abs_x < x) {
            camera_abs_x += camera_spin_rotation_speed + ((x - camera_abs_x) * camera_spin_speed) / 1000;
            if (camera_abs_x > x)
                camera_abs_x = x;
        }
        if (camera_abs_x > x) {
            camera_abs_x -= camera_spin_rotation_speed + ((camera_abs_x - x) * camera_spin_speed) / 1000;
            if (camera_abs_x < x)
                camera_abs_x = x;
        }
        if (camera_abs_z < z) {
            camera_abs_z += camera_spin_rotation_speed + ((z - camera_abs_z) * camera_spin_speed) / 1000;
            if (camera_abs_z > z)
                camera_abs_z = z;
        }
        if (camera_abs_z > z) {
            camera_abs_z -= camera_spin_rotation_speed + ((camera_abs_z - z) * camera_spin_speed) / 1000;
            if (camera_abs_z < z)
                camera_abs_z = z;
        }
        if (camera_abs_y < y) {
            camera_abs_y += camera_spin_rotation_speed + ((y - camera_abs_y) * camera_spin_speed) / 1000;
            if (camera_abs_y > y)
                camera_abs_y = y;
        }
        if (camera_abs_y > y) {
            camera_abs_y -= camera_spin_rotation_speed + ((camera_abs_y - y) * camera_spin_speed) / 1000;
            if (camera_abs_y < y)
                camera_abs_y = y;
        }
        x = camera_tile_target_x * 128 + 64;
        y = camera_tile_target_y * 128 + 64;
        z = get_tile_pos(plane, y, x) - camera_tile_height_offset;
        int dist_x = x - camera_abs_x;
        int dist_z = z - camera_abs_z;
        int dist_y = y - camera_abs_y;
        int scalar = (int) Math.sqrt(dist_x * dist_x + dist_y * dist_y);
        int curve_tilt = (int) (Math.atan2(dist_z, scalar) * 325.94900000000001D) & 0x7ff;
        int curve_pan = (int) (Math.atan2(dist_x, dist_y) * -325.94900000000001D) & 0x7ff;
        if (curve_tilt < 128) {
            curve_tilt = 128;
        }

        if (curve_tilt > 383) {
            curve_tilt = 383;
        }

        if (cam_curve_y < curve_tilt) {
            cam_curve_y += camera_turn_speed + ((curve_tilt - cam_curve_y) * camera_turn_angle) / 1000;
            if (cam_curve_y > curve_tilt) {
                cam_curve_y = curve_tilt;
            }
        }
        if (cam_curve_y > curve_tilt) {
            cam_curve_y -= camera_turn_speed + ((cam_curve_y - curve_tilt) * camera_turn_angle) / 1000;
            if (cam_curve_y < curve_tilt) {
                cam_curve_y = curve_tilt;
            }
        }
        int pan_angle = curve_pan - cam_curve_x;
        if (pan_angle > 1024) {
            pan_angle -= 2048;
        }

        if (pan_angle < -1024) {
            pan_angle += 2048;
        }

        if (pan_angle > 0) {
            cam_curve_x += camera_turn_speed + (pan_angle * camera_turn_angle) / 1000;
            cam_curve_x &= 0x7ff;
        }
        if (pan_angle < 0) {
            cam_curve_x -= camera_turn_speed + (-pan_angle * camera_turn_angle) / 1000;
            cam_curve_x &= 0x7ff;
        }
        int pan_offset = curve_pan - cam_curve_x;
        if (pan_offset > 1024) {
            pan_offset -= 2048;
        }

        if (pan_offset < -1024) {
            pan_offset += 2048;
        }

        if (pan_offset < 0 && pan_angle > 0 || pan_offset > 0 && pan_angle < 0) {
            cam_curve_x = curve_pan;
        }
    }

    public void drawMenu(int xOffSet, int yOffSet) {
        int menu_x = menuOffsetX - (xOffSet - 4);
        int menu_y = (-yOffSet + 1) + menuOffsetY;
        int menu_width = menuWidth;
        int menu_height = menuHeight + 1;

        update_chat_producer = true;
        update_tab_producer = true;

        Rasterizer2D.draw_filled_rect(menu_x, menu_y, menu_width, menu_height, 0x5D5447);
        Rasterizer2D.draw_filled_rect(menu_x + 1, menu_y + 1, menu_width - 2, 16, 0);
        Rasterizer2D.draw_rect_outline(menu_x + 1, menu_y + 18, menu_width - 2, menu_height - 19, 0);
        adv_font_bold.draw("Choose Option", menu_x + 3, menu_y + 16, 0x5d5447, 50);

        int mouse_x = super.cursor_x - (xOffSet);
        int mouse_y = (-yOffSet) + super.cursor_y;
        for (int id = 0; id < menuActionRow; id++) {
            int text_y = menu_y + 34 + (menuActionRow - 1 - id) * 15;
            int text_color = 0xffffff;
            if (mouse_x > menu_x && mouse_x < menu_x + menu_width && mouse_y > text_y - 13 && mouse_y < text_y + 3) {
                text_color = 0xffff00;
            }
            adv_font_bold.draw(menuActionText[id], menu_x + 3, text_y, text_color, 50);
        }
    }

    private void addFriend(String added) {
        try {
            if (added == null)
                return;
            if (friendsCount >= 100 && member != 1) {
                sendMessage("Your friendlist is full. Max of 100 for free users, and 200 for members", 0, "");
                return;
            }
            /// YOU HAVE TO KEEP THIS LOGIC OTHERWISE CLIENT WILL DESYNC
            if (friendsCount >= 200) {
                sendMessage("Your friendlist is full. Max of 100 for free users, and 200 for members", 0, "");
                return;
            }

            for (int i = 0; i < friendsCount; i++)
                if (friendsList[i].equalsIgnoreCase(added)) {
                    sendMessage(added + " is already on your friend list", 0, "");
                    return;
                }
            for (int j = 0; j < ignoreCount; j++)
                if (ignoreList[j].equalsIgnoreCase(added)) {
                    sendMessage("Please remove " + added + " from your ignore list first", 0, "");
                    return;
                }
            if (!added.equalsIgnoreCase(local_player.username)) {
                friendsList[friendsCount] = added;
                friendsListAsLongs[friendsCount] = StringUtils.encodeBase37(added);
                friendsNodeIDs[friendsCount] = 0;
                friendsCount++;
                packetSender.sendFriendAddition(added);
            }
            return;
        } catch (RuntimeException runtimeexception) {
            SignLink.reporterror("15283, " + (byte) 68 + ", " + added + ", " + runtimeexception.toString());
        }
        throw new RuntimeException();
    }

    private int get_tile_pos(int z, int y, int x) {
        int worldX = x >> 7;
        int worldY = y >> 7;
        if (worldX < 0 || worldY < 0 || worldX > 103 || worldY > 103)
            return 0;
        int plane = z;
        if (plane < 3 && (tileFlags[1][worldX][worldY] & 2) == 2)
            plane++;
        int sizeX = x & 0x7f;
        int sizeY = y & 0x7f;
        int i2 = tileHeights[plane][worldX][worldY] * (128 - sizeX)
            + tileHeights[plane][worldX + 1][worldY] * sizeX >> 7;
        int j2 = tileHeights[plane][worldX][worldY + 1] * (128 - sizeX)
            + tileHeights[plane][worldX + 1][worldY + 1] * sizeX >> 7;
        return i2 * (128 - sizeY) + j2 * sizeY >> 7;
    }

    private static String set_k_or_m(int value) {//can set <col tags infront of the value
        if (value < 0x186a0)
            return String.valueOf(value);

        if (value < 0x989680)
            return value / 1000 + "K";
        else
            return value / 0xf4240 + "M";

    }

    private void logout() {
        addReportToServer("Regular logout has been called.");
        logoutTime = System.currentTimeMillis();
        expectedHit.clear();
        // yellowEnded = true; //This prevents other yellow messages from sending.
        try {
            if (socketStream != null)
                socketStream.close();
        } catch (Exception _ex) {
            _ex.printStackTrace();
            addReportToServer(_ex.getMessage());
        }
        firstLoginMessage = secondLoginMessage = "";
        effects_list.clear();
        spriteCache.clear();
        // ken comment, let's clear 2d rasterizer and game object cache.
        scene.reset_interactive_obj();
        Rasterizer2D.clear();
        // ken comment, ground item overlays mess up the login screen font, so let's
        // reset the new small font and ensure the font is correct on the login screen.
        adv_font_small.draw_centered("", 0, 0, 0, 0);
        setInteractingWithEntityId(0);
        socketStream = null;
        loggedIn = false;
        loginScreenState = 0;
        if (fadingScreen != null) {
            fadingScreen.stop();
        }
        release();
        scene.rebuild();
        for (int i = 0; i < 4; i++)
            collisionMaps[i].init();
        Arrays.fill(chatMessages, null);
        if (setting.toggle_music && !low_detail) {
            playSong(SoundConstants.SCAPE_RUNE);
        } else {
            stop_midi();
        }
        current_track = -1;
        next_track = -1;
        previous_track = 0;
        clearTextClicked();
        frameValueW = 765;
        frameValueH = 503;
        frameMode(ScreenMode.FIXED);
        resetInputFieldFocus();

        //Clear private messages
        resetSplitPrivateChatMessages();
        System.gc();
    }

    private void resetCharacterCreation() {
        updateCharacterCreation = true;
        for (int j = 0; j < 7; j++) {
            characterClothing[j] = -1;
            for (int k = 0; k < IdentityKit.length; k++) {
                if (IdentityKit.cache[k].validStyle || IdentityKit.cache[k].bodyPartId != j + (characterGender ? 0 : 7))
                    continue;
                characterClothing[j] = k;
                break;
            }
        }
    }

    private void updateNPCMovement(int i, Buffer stream) {
        while (stream.bitPosition + 21 < i * 8) {
            int k = stream.readBits(14);
            if (k == 16383)
                break;
            if (npcs[k] == null)
                npcs[k] = new Npc();
            Npc npc = npcs[k];
            local_npcs[npcs_in_region++] = k;
            npc.time = game_tick;
            int l = stream.readBits(5);
            if (l > 15)
                l -= 32;
            int i1 = stream.readBits(5);
            if (i1 > 15)
                i1 -= 32;
            int j1 = stream.readBits(1);
            npc.desc = NpcDefinition.get(stream.readBits(ClientConstants.NPC_BITS));
            int updateRequired = stream.readBits(1);
            if (updateRequired == 1)
                mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = k;
            npc.occupied_tiles = npc.desc.occupied_tiles;
            npc.rotation = npc.desc.rotation;
            npc.walk_animation_id = npc.desc.walkingAnimation;
            npc.turn_around_animation_id = npc.desc.halfTurnAnimation;
            npc.pivot_right_animation_id = npc.desc.quarterClockwiseTurnAnimation;
            npc.pivot_left_animation_id = npc.desc.quarterAnticlockwiseTurnAnimation;
            npc.idle_animation_id = npc.desc.standingAnimation;
            npc.setPos(local_player.waypoint_x[0] + i1, local_player.waypoint_y[0] + l, j1 == 1);
            // Facing
            boolean updateFacing = (stream.readBits(1) == 1);
            if (updateFacing) {
                int faceX = stream.readBits(14);
                int faceY = stream.readBits(14);
                npc.faceX = faceX;
                npc.faceY = faceY;
            }
        }
        stream.disableBitAccess();
    }

    static long processGameTime, xpro;

    public void processGameLoop() {
        if (rsAlreadyLoaded || loadingError || genericLoadingError)
            return;
        game_tick++;

        if (!loggedIn) {
            processLoginScreenInput();
        } else {
            mainGameProcessor();
        }
        processOnDemandQueue();

        if (debug_packet_info && readpkts > 0) {
            addReportToServer(Client.xpro++ + " processGame took " + (System.currentTimeMillis() - processGameTime));
        }
        processGameTime = System.currentTimeMillis();
        readpkts = 0;
    }

    private boolean promptUserForInput(Widget widget) {
        int contentType = widget.contentType;
        if (friendServerStatus == 2) {
            if (contentType == 201) {
                update_chat_producer = true;
                inputDialogState = 0;
                messagePromptRaised = true;
                promptInput = "";
                interfaceInputAction = 1;
                inputMessage = "Enter name of friend to add to list";
            }
            if (contentType == 202) {
                update_chat_producer = true;
                inputDialogState = 0;
                messagePromptRaised = true;
                promptInput = "";
                interfaceInputAction = 2;
                inputMessage = "Enter name of friend to delete from list";
            }
        }
        if (contentType == 205) {
            afkCountdown = 250;
            return true;
        }
        if (contentType == 501) {
            update_chat_producer = true;
            inputDialogState = 0;
            messagePromptRaised = true;
            promptInput = "";
            interfaceInputAction = 4;
            inputMessage = "Enter name of player to add to list";
        }
        if (contentType == 502) {
            update_chat_producer = true;
            inputDialogState = 0;
            messagePromptRaised = true;
            promptInput = "";
            interfaceInputAction = 5;
            inputMessage = "Enter name of player to delete from list";
        }
        if (contentType == 550) {
            update_chat_producer = true;
            inputDialogState = 0;
            messagePromptRaised = true;
            promptInput = "";
            interfaceInputAction = 6;
            inputMessage = "Enter the name of the chat you wish to join";
        }

        if (contentType >= 300 && contentType <= 313) {
            int k = (contentType - 300) / 2;
            int j1 = contentType & 1;
            int i2 = characterClothing[k];
            if (i2 != -1) {
                do {
                    if (j1 == 0 && --i2 < 0)
                        i2 = IdentityKit.length - 1;
                    if (j1 == 1 && ++i2 >= IdentityKit.length)
                        i2 = 0;
                } while (IdentityKit.cache[i2].validStyle
                    || IdentityKit.cache[i2].bodyPartId != k + (characterGender ? 0 : 7));
                characterClothing[k] = i2;
                updateCharacterCreation = true;
            }
        }
        if (contentType >= 314 && contentType <= 323) {
            int l = (contentType - 314) / 2;
            int k1 = contentType & 1;
            int j2 = characterDesignColours[l];
            if (k1 == 0 && --j2 < 0)
                j2 = APPEARANCE_COLORS[l].length - 1;
            if (k1 == 1 && ++j2 >= APPEARANCE_COLORS[l].length)
                j2 = 0;
            characterDesignColours[l] = j2;
            updateCharacterCreation = true;
        }
        if (contentType == 324 && !characterGender) {
            characterGender = true;
            resetCharacterCreation();
        }
        if (contentType == 325 && characterGender) {
            characterGender = false;
            resetCharacterCreation();
        }
        if (contentType == 326) {
            // appearance change
            packetSender.sendAppearanceChange(characterGender, characterClothing, characterDesignColours);
            return true;
        }

        if (contentType == 613) {
            canMute = !canMute;
        }

        if (contentType >= 601 && contentType <= 612) {
            clearTopInterfaces();
            if (reportAbuseInput.length() > 0) {
                /*
                 * outgoing.writeOpcode(ClientToServerPackets.REPORT_PLAYER);
                 * outgoing.writeLong(StringUtils.encodeBase37(reportAbuseInput));
                 * outgoing.writeByte(contentType - 601); outgoing.writeByte(canMute ? 1 : 0);
                 */
            }
        }
        return false;
    }

    private void parsePlayerSynchronizationMask(Buffer stream) {
        for (int count = 0; count < mobsAwaitingUpdateCount; count++) {
            int index = mobsAwaitingUpdate[count];
            Player player = players[index];

            int mask = stream.readUByte();

            if ((mask & 0x40) != 0) {
                mask += stream.readUByte() << 8;
            }

            appendPlayerUpdateMask(mask, index, stream, player);
        }
    }

    private void drawMapScenes(int i, int k, int l, int i1, int j1) {
        long id = scene.get_wall_uid(j1, l, i);
        if (id != 0) {
            int orientation = get_object_orientation(id);
            int object_type = get_object_type(id);
            int k3 = k;
            if (id > 0)
                k3 = i1;
            int ai[] = minimapImage.pixels;
            int k4 = 24624 + l * 4 + (103 - i) * 512 * 4;
            int object_id = get_object_key(id);
            ObjectDefinition def = ObjectDefinition.get(object_id);
            if (def.map_scene_id != -1) {
                IndexedImage background_2 = mapScenes[def.map_scene_id];
                if (background_2 != null) {
                    int i6 = (def.width * 4 - background_2.width) / 2;
                    int j6 = (def.height * 4 - background_2.height) / 2;
                    background_2.draw(48 + l * 4 + i6, 48 + (104 - i - def.height) * 4 + j6);
                }
            } else {
                if (object_type == 0 || object_type == 2)
                    if (orientation == 0) {
                        ai[k4] = k3;
                        ai[k4 + 512] = k3;
                        ai[k4 + 1024] = k3;
                        ai[k4 + 1536] = k3;
                    } else if (orientation == 1) {
                        ai[k4] = k3;
                        ai[k4 + 1] = k3;
                        ai[k4 + 2] = k3;
                        ai[k4 + 3] = k3;
                    } else if (orientation == 2) {
                        ai[k4 + 3] = k3;
                        ai[k4 + 3 + 512] = k3;
                        ai[k4 + 3 + 1024] = k3;
                        ai[k4 + 3 + 1536] = k3;
                    } else if (orientation == 3) {
                        ai[k4 + 1536] = k3;
                        ai[k4 + 1536 + 1] = k3;
                        ai[k4 + 1536 + 2] = k3;
                        ai[k4 + 1536 + 3] = k3;
                    }
                if (object_type == 3)
                    if (orientation == 0)
                        ai[k4] = k3;
                    else if (orientation == 1)
                        ai[k4 + 3] = k3;
                    else if (orientation == 2)
                        ai[k4 + 3 + 1536] = k3;
                    else if (orientation == 3)
                        ai[k4 + 1536] = k3;
                if (object_type == 2)
                    if (orientation == 3) {
                        ai[k4] = k3;
                        ai[k4 + 512] = k3;
                        ai[k4 + 1024] = k3;
                        ai[k4 + 1536] = k3;
                    } else if (orientation == 0) {
                        ai[k4] = k3;
                        ai[k4 + 1] = k3;
                        ai[k4 + 2] = k3;
                        ai[k4 + 3] = k3;
                    } else if (orientation == 1) {
                        ai[k4 + 3] = k3;
                        ai[k4 + 3 + 512] = k3;
                        ai[k4 + 3 + 1024] = k3;
                        ai[k4 + 3 + 1536] = k3;
                    } else if (orientation == 2) {
                        ai[k4 + 1536] = k3;
                        ai[k4 + 1536 + 1] = k3;
                        ai[k4 + 1536 + 2] = k3;
                        ai[k4 + 1536 + 3] = k3;
                    }
            }
        }
        id = scene.get_interactive_object_uid(j1, l, i);
        if (id != 0) {
            int orientation = get_object_orientation(id);
            int object_type = get_object_type(id);
            int object_id = get_object_key(id);
            ObjectDefinition class46_1 = ObjectDefinition.get(object_id);
            if (class46_1.map_scene_id != -1) {
                IndexedImage background_1 = mapScenes[class46_1.map_scene_id];
                if (background_1 != null) {
                    int j5 = (class46_1.width * 4 - background_1.width) / 2;
                    int k5 = (class46_1.height * 4 - background_1.height) / 2;
                    background_1.draw(48 + l * 4 + j5, 48 + (104 - i - class46_1.height) * 4 + k5);
                }
            } else if (object_type == 9) {
                int l4 = 0xeeeeee;
                if (id > 0)
                    l4 = 0xee0000;
                int ai1[] = minimapImage.pixels;
                int l5 = 24624 + l * 4 + (103 - i) * 512 * 4;
                if (orientation == 0 || orientation == 2) {
                    ai1[l5 + 1536] = l4;
                    ai1[l5 + 1024 + 1] = l4;
                    ai1[l5 + 512 + 2] = l4;
                    ai1[l5 + 3] = l4;
                } else {
                    ai1[l5] = l4;
                    ai1[l5 + 512 + 1] = l4;
                    ai1[l5 + 1024 + 2] = l4;
                    ai1[l5 + 1536 + 3] = l4;
                }
            }
        }
        id = scene.get_ground_decor_uid(j1, l, i);
        if (id != 0L) {
            int object_id = get_object_key(id);
            ObjectDefinition def = ObjectDefinition.get(object_id);
            if (def.map_scene_id != -1) {
                IndexedImage background = mapScenes[def.map_scene_id];
                if (background != null) {
                    int i4 = (def.width * 4 - background.width) / 2;
                    int j4 = (def.height * 4 - background.height) / 2;
                    background.draw(48 + l * 4 + i4, 48 + (104 - i - def.height) * 4 + j4);
                }
            }
        }
    }

    private void loadTitleScreen() {
        usernameHover = spriteCache.get(1852);
        passwordHover = spriteCache.get(1853);
        loginHover = spriteCache.get(1854);
        draw_loadup(10, "Loading...");

          titleBoxIndexedImage = new IndexedImage(title_archive, "titlebox", 0);
         titleButtonIndexedImage = new IndexedImage(title_archive, "titlebutton", 0);

         titleIndexedImages = new IndexedImage[12]; int icon = 0; try {
//             icon =
//          Integer.parseInt(getParameter("fl_icon"));
         } catch (Exception ex) {

     } if (icon == 0) { for (int index = 0; index < 12; index++) {
        titleIndexedImages[index] = new IndexedImage(title_archive, "runes", index); }

       } else { for (int index = 0; index < 12; index++) { titleIndexedImages[index]
          = new IndexedImage(title_archive, "runes", 12 + (index & 3)); }

          } flameLeftSprite = new SimpleImage(128, 265); flameRightSprite = new SimpleImage(128,
          265);

          System.arraycopy(flameLeftBackground.canvasRaster, 0,
         flameLeftSprite.pixels, 0, 33920);

        System.arraycopy(flameRightBackground.canvasRaster, 0,
        flameRightSprite.pixels, 0, 33920);

         anIntArray851 = new int[256];

          for (int k1 = 0; k1 < 64; k1++) anIntArray851[k1] = k1 * 0x40000;

         for (int l1 = 0; l1 < 64; l1++) anIntArray851[l1 + 64] = 0xff0000 + 1024 *
          l1;

         for (int i2 = 0; i2 < 64; i2++) anIntArray851[i2 + 128] = 0xffff00 + 4 * i2;

          for (int j2 = 0; j2 < 64; j2++) anIntArray851[j2 + 192] = 0xffffff;

         anIntArray852 = new int[256]; for (int k2 = 0; k2 < 64; k2++)
          anIntArray852[k2] = k2 * 1024;

          for (int l2 = 0; l2 < 64; l2++) anIntArray852[l2 + 64] = 65280 + 4 * l2;

          for (int i3 = 0; i3 < 64; i3++) anIntArray852[i3 + 128] = 65535 + 0x40000 *
         i3;

          for (int j3 = 0; j3 < 64; j3++) anIntArray852[j3 + 192] = 0xffffff;

         anIntArray853 = new int[256]; for (int k3 = 0; k3 < 64; k3++)
          anIntArray853[k3] = k3 * 4;

          for (int l3 = 0; l3 < 64; l3++) anIntArray853[l3 + 64] = 255 + 0x40000 * l3;

         for (int i4 = 0; i4 < 64; i4++) anIntArray853[i4 + 128] = 0xff00ff + 1024 *
         i4;

          for (int j4 = 0; j4 < 64; j4++) anIntArray853[j4 + 192] = 0xffffff;

         anIntArray850 = new int[256]; anIntArray1190 = new int[32768]; anIntArray1191
          = new int[32768]; randomizeBackground(null); anIntArray828 = new int[32768];
          anIntArray829 = new int[32768]; draw_loadup(10,
          "Connecting to fileserver"); if (!update_flame_components) { drawFlames = true;
            update_flame_components = true; startRunnable(this, 2); }

    }

    public boolean hover(int x1, int y1, SimpleImage drawnSprite) {
        return super.cursor_x >= x1 && super.cursor_x <= x1 + drawnSprite.width && super.cursor_y >= y1
            && super.cursor_y <= y1 + drawnSprite.height;
    }

    private static void setHighMem() {
        SceneGraph.low_detail = false;
        Rasterizer3D.low_detail = false;
        low_detail = false;
        Region.low_detail = false;
        ObjectDefinition.low_detail = false;
    }

    static {
    }

    //Let's not lazy initialize the singleton, we should initialize it inline to guarantee thread safety.
    public static final Client singleton = new Client();

    public static void main(String args[]) {
        try {
            nodeID = 10;
            setHighMem();
            isMembers = true;
            SignLink.storeid = 32;
            SignLink.startpriv(InetAddress.getLocalHost());
            singleton.createClientFrame(window_width, window_height);
            osName = System.getProperty("os.name");
        } catch (Exception e) {
            e.printStackTrace();
            addReportToServer(e.getMessage());
        }
    }

    private void loadingStages() {
        if (low_detail && loading_phase == 2 && Region.plane != plane) {
            gameScreenImageProducer.init();
            drawLoadingMessages(1, "Loading - please wait.", null);
            gameScreenImageProducer.drawGraphics(screen == ScreenMode.FIXED ? 4 : 0, super.graphics,
                screen == ScreenMode.FIXED ? 4 : 0);
            loading_phase = 1;
            loadingStartTime = System.currentTimeMillis();
        }
        if (loading_phase == 1) {
            int j = getMapLoadingState();
            if (j != 0 && System.currentTimeMillis() - loadingStartTime > 0x57e40L) {
                SignLink.reporterror(myUsername + " glcfb " + serverSeed + "," + j + "," + low_detail + "," + indices[0]
                    + "," + resourceProvider.remaining() + "," + plane + "," + region_x + ","
                    + region_y);
                loadingStartTime = System.currentTimeMillis();
            }
        }
        if (loading_phase == 2 && plane != lastKnownPlane) {
            lastKnownPlane = plane;
            renderMapScene(plane);
        }
    }

    private int getMapLoadingState() {
        if (!floorMaps.equals("") || !objectMaps.equals("")) {
            floorMaps = "";
            objectMaps = "";
        }

        for (int i = 0; i < terrainData.length; i++) {
            floorMaps += "  " + terrainIndices[i];
            objectMaps += "  " + objectIndices[i];
            if (terrainData[i] == null && terrainIndices[i] != -1)
                return -1;
            if (objectData[i] == null && objectIndices[i] != -1)
                return -2;
        }
        boolean flag = true;
        for (int j = 0; j < terrainData.length; j++) {
            byte abyte0[] = objectData[j];
            if (abyte0 != null) {
                int k = (mapCoordinates[j] >> 8) * 64 - next_region_start;
                int l = (mapCoordinates[j] & 0xff) * 64 - next_region_end;
                if (requestMapReconstruct) {
                    k = 10;
                    l = 10;
                }
                flag &= Region.cached(k, abyte0, l);
            }
        }
        if (!flag)
            return -3;
        if (loadingMap) {
            return -4;
        } else {
            loading_phase = 2;
            Region.plane = plane;
            loadRegion();
            packetSender.sendFinalizedRegionChange();
            return 0;
        }
    }

    private void render_projectiles() {
        for (Projectile projectile = (Projectile) projectiles
            .first(); projectile != null; projectile = (Projectile) projectiles.next())
            if (projectile.plane != plane || game_tick > projectile.cycle_limit)
                projectile.remove();
            else if (game_tick >= projectile.delay) {
                if (projectile.target_id > 0) {
                    Npc npc = npcs[projectile.target_id - 1];
                    if (npc != null && npc.world_x >= 0 && npc.world_x < 13312 && npc.world_y >= 0
                        && npc.world_y < 13312)
                        projectile.track(game_tick, npc.world_y,
                            get_tile_pos(projectile.plane, npc.world_y, npc.world_x)
                                - projectile.end_z,
                            npc.world_x);
                }
                if (projectile.target_id < 0) {
                    int index = -projectile.target_id;
                    Player player;
                    if (index == localPlayerIndex)
                        player = local_player;
                    else
                        player = players[index];
                    if (player != null && player.world_x >= 0 && player.world_x < 13312 && player.world_y >= 0
                        && player.world_y < 13312)
                        projectile.track(game_tick, player.world_y,
                            get_tile_pos(projectile.plane, player.world_y, player.world_x)
                                - projectile.end_z,
                            player.world_x);
                }
                projectile.travel(animation_step);
                scene.add_entity(plane, projectile.jaw, (int) projectile.current_height, -1, (int) projectile.current_y,
                    60, (int) projectile.current_x, projectile, false);
            }

    }

    /*
     * public AppletContext getAppletContext() { if (SignLink.mainapp != null)
     * return SignLink.mainapp.getAppletContext(); //else // return
     * super.getAppletContext(); }
     */

    private void drawLogo() {
      //  byte abyte0[] = title_archive.readFile("title.dat");
        byte abyte0[] =title_archive.get("title.dat");
        SimpleImage sprite = new SimpleImage(abyte0);
        flameLeftBackground.init();
        sprite.drawSprite(0,0);

        flameRightBackground.init();
        sprite.drawSprite(-637,0);

        topLeft1BackgroundTile.init();
        sprite.drawSprite(-128,0);

        bottomLeft1BackgroundTile.init();
        sprite.drawSprite(-202, -371);

        loginBoxImageProducer.init();
        sprite.drawSprite(-202, -171);

        loginScreenAccessories.init();
        sprite.drawSprite(0,-400);

        bottomLeft0BackgroundTile.init();
        sprite.drawSprite(0,-265);


        bottomRightImageProducer.init();
        sprite.drawSprite(-562,-265);

        loginMusicImageProducer.init();
        sprite.drawSprite(-562,-265);

        middleLeft1BackgroundTile.init();
        sprite.drawSprite(-128, -171);

        aRSImageProducer_1115.init();
        sprite.drawSprite(-562, -171);

        int ai[] = new int[sprite.width];
        for (int j = 0; j < sprite.height; j++) {
            for (int k = 0; k < sprite.width; k++) {
                ai[k] = sprite.pixels[sprite.width - k - 1 + sprite.width * j];
            }

            System.arraycopy(ai, 0, sprite.pixels, sprite.width * j, sprite.width);

        }
        flameLeftBackground.init();
        sprite.drawSprite(382,0);

        flameRightBackground.init();
        sprite.drawSprite(-255,0);

        topLeft1BackgroundTile.init();
        sprite.drawSprite(254,0);

        bottomLeft1BackgroundTile.init();
        sprite.drawSprite(180, -371);

        loginBoxImageProducer.init();
        sprite.drawSprite(180, -171);

        loginScreenAccessories.init();
        sprite.drawSprite(382, -265);

        bottomLeft0BackgroundTile.init();
        sprite.drawSprite(382, -265);


        bottomRightImageProducer.init();
        sprite.drawSprite(-180, -265);

        loginMusicImageProducer.init();
        sprite.drawSprite(-180, -265);

        middleLeft1BackgroundTile.init();
        sprite.drawSprite(254, -171);

        aRSImageProducer_1115.init();
        sprite.drawSprite(-180, -171);
        sprite = new SimpleImage(title_archive, "logo", 0);
        topLeft1BackgroundTile.init();
        sprite.drawSprite(382 - sprite.width / 2 - 128, 18);
        sprite = null;
        System.gc();



//        spriteCache.clear();
//        flameLeftBackground.init();
//        flameRightBackground.init();
//        topLeft1BackgroundTile.init();
//        bottomLeft1BackgroundTile.init();
//        loginBoxImageProducer.init();
//
//        loginScreenAccessories.init();
//        bottomLeft0BackgroundTile.init();
//        bottomRightImageProducer.init();
//        loginMusicImageProducer.init();
//        middleLeft1BackgroundTile.init();
//        aRSImageProducer_1115.init();

        // Graphics is abstract, not static.
        // Graphics.drawImage(logo.convertToImage(), y, x, null);
    }

    public void toImage(SimpleImage image, String name) {
        File directory = new File(SignLink.findCacheDir() + "rsimg/dump1/");
        if (!directory.exists()) {
            directory.mkdir();
        }
        if (image == null) {
            //System.out.println("Image was null :/");
            return;
        }
        BufferedImage bi = new BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB);
        bi.setRGB(0, 0, image.width, image.height, image.pixels, 0, image.width);
        Image img = makeColorTransparent(bi, new Color(0, 0, 0));
        BufferedImage trans = imageToBufferedImage(img);
        try {
            File out = new File(SignLink.findCacheDir() + "rsimg/dump1/" + name + ".png");
            ImageIO.write(trans, "png", out);
        } catch (Exception e) {
            e.printStackTrace();
            addReportToServer(e.getMessage());
        }
    }

    /**
     * Turns an Image into a BufferedImage.
     *
     * @param image
     * @return
     */
    private static BufferedImage imageToBufferedImage(Image image) {
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null),
            BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bufferedImage.createGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
        return bufferedImage;
    }

    /**
     * Makes the specified color transparent in a buffered image.
     *
     * @param im
     * @param color
     * @return
     */
    public static Image makeColorTransparent(BufferedImage im, final Color color) {
        RGBImageFilter filter = new RGBImageFilter() {
            public int markerRGB = color.getRGB() | 0xFF000000;

            public final int filterRGB(int x, int y, int rgb) {
                if ((rgb | 0xFF000000) == markerRGB) {
                    return 0x00FFFFFF & rgb;
                } else {
                    return rgb;
                }
            }
        };
        ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
        return Toolkit.getDefaultToolkit().createImage(ip);
    }

    public Widget interesecting() {
        if (openWalkableInterface < 1) {
            return null;
        }
        Widget rsi = Widget.cache[openWalkableInterface];
        if (rsi.childToIntersect == 0) {
            //System.out.println("Is 0 so continue.");
            return null;
        }

        Widget ri = Widget.cache[rsi.childToIntersect];

        if (mouseInRegion(ri.positionX, ri.positionY, ri.positionX + (ri.width),
            ri.positionY + (ri.height))) {
            //System.out.println("Intersecting");
            return ri;
        }

        return null;

    }

    private void processOnDemandQueue() {
        do {
            Resource resource;
            do {
                resource = resourceProvider.next();
                if (resource == null)
                    return;
                if (resource.dataType == 0) {
                    Model.method460(resource.buffer, resource.ID);
                    if (backDialogueId != -1)
                        update_chat_producer = true;
                }
                if (resource.dataType == 1) {
                    Animation.load(resource.buffer, resource.ID);
                }
                if (resource.dataType == 2 && resource.ID == next_track && resource.buffer != null)
                    saveMidi(fade_audio, resource.buffer);
                if (resource.dataType == 3 && loading_phase == 1) {
                    for (int i = 0; i < terrainData.length; i++) {
                        if (terrainIndices[i] == resource.ID) {
                            terrainData[i] = resource.buffer;
                            if (resource.buffer == null)
                                terrainIndices[i] = -1;
                            break;
                        }
                        if (objectIndices[i] != resource.ID)
                            continue;
                        objectData[i] = resource.buffer;
                        if (resource.buffer == null)
                            objectIndices[i] = -1;
                        break;
                    }
                }
            } while (resource.dataType != 93 || !resourceProvider.landscapePresent(resource.ID));
            Region.passive_request_obj_models(new Buffer(resource.buffer), resourceProvider);
        } while (true);
    }

    private void calcFlamesPosition() {
        char c = '\u0100';
        for (int j = 10; j < 117; j++) {
            int k = (int) (Math.random() * 100D);
            if (k < 50)
                anIntArray828[j + (c - 2 << 7)] = 255;
        }
        for (int l = 0; l < 100; l++) {
            int i1 = (int) (Math.random() * 124D) + 2;
            int k1 = (int) (Math.random() * 128D) + 128;
            int k2 = i1 + (k1 << 7);
            anIntArray828[k2] = 192;
        }

        for (int j1 = 1; j1 < c - 1; j1++) {
            for (int l1 = 1; l1 < 127; l1++) {
                int l2 = l1 + (j1 << 7);
                anIntArray829[l2] = (anIntArray828[l2 - 1] + anIntArray828[l2 + 1] + anIntArray828[l2 - 128]
                    + anIntArray828[l2 + 128]) / 4;
            }

        }

        anInt1275 += 128;
        if (anInt1275 > anIntArray1190.length) {
            anInt1275 -= anIntArray1190.length;
            int i2 = (int) (Math.random() * 12D);
            randomizeBackground(titleIndexedImages[i2]);
        }
        for (int j2 = 1; j2 < c - 1; j2++) {
            for (int i3 = 1; i3 < 127; i3++) {
                int k3 = i3 + (j2 << 7);
                int i4 = anIntArray829[k3 + 128] - anIntArray1190[k3 + anInt1275 & anIntArray1190.length - 1] / 5;
                if (i4 < 0)
                    i4 = 0;
                anIntArray828[k3] = i4;
            }

        }

        System.arraycopy(anIntArray969, 1, anIntArray969, 0, c - 1);

        anIntArray969[c - 1] = (int) (Math.sin((double) game_tick / 14D) * 16D
            + Math.sin((double) game_tick / 15D) * 14D + Math.sin((double) game_tick / 16D) * 12D);
        if (anInt1040 > 0)
            anInt1040 -= 4;
        if (anInt1041 > 0)
            anInt1041 -= 4;
        if (anInt1040 == 0 && anInt1041 == 0) {
            int l3 = (int) (Math.random() * 2000D);
            if (l3 == 0)
                anInt1040 = 1024;
            if (l3 == 1)
                anInt1041 = 1024;
        }
    }

    private final int TEXT_CHILD_OFFSET = 15;

    private void resetAnimation(int i) {
        Widget class9 = Widget.cache[i];
        if (class9 == null || class9.children == null) {
            return;
        }
        for (int j = 0; j < class9.children.length; j++) {
            if (class9.children[j] == -1)
                break;
            Widget class9_1 = Widget.cache[class9.children[j]];
            if (class9_1.type == 1)
                resetAnimation(class9_1.id);
            class9_1.currentFrame = 0;
            class9_1.lastFrameTime = 0;
        }
    }

    private void drawHeadIcon() {
        if (hintIconDrawType != 2)
            return;
        get_scene_pos((hintIconX - next_region_start << 7) + hintIconLocationArrowRelX, hintIconLocationArrowHeight * 2,
            (hintIconY - next_region_end << 7) + hintIconLocationArrowRelY);
        if (scene_draw_x > -1 && game_tick % 20 < 10) {
            headIconsHint[0].drawSprite(scene_draw_x - 12, scene_draw_y - 28);
        }
    }

    static long lastPackets;
    static long currentPacketTime;
    static int readpkts = 0;

    private void mainGameProcessor() {
        refreshFrameSize();

        if (getGameComponent().getFocusTraversalKeysEnabled()) {
            getGameComponent().setFocusTraversalKeysEnabled(false);
        }

        boolean isFixed = screen == ScreenMode.FIXED;

        if (systemUpdateTime > 1) {
            systemUpdateTime--;
        }

        if (afkCountdown > 0) {
            afkCountdown--;
        }

        long packetsReadTime = System.currentTimeMillis();

        /*
         * This is the number of packets the client can read per tick.
         * This number is 5 by default which is low.
         * If this number is too low then the game will feel unresponsive (like for switching items etc)
         * If this number is too high, and processing packets takes over 20 ms, then the client
         * will drop/skip frames. If you take 1000ms / 50 fps you get 20 ms per frame.
         * A higher number also increases CPU usage.
         * Basically, a higher number will feel more responsive at the cost of possible skipped frames.
         * OSRS uses 100 packets per tick.
         */
        int packetsPerTick = 100;
        for (int j = 0; j < packetsPerTick; j++) {
            if (!readPacket()) {
                break;
            }
            readpkts++;
        }

        if (debug_packet_info) {
            long lastRead = System.currentTimeMillis() - lastPackets;
            long itTook = System.currentTimeMillis() - packetsReadTime;
            addReportToServer("read " + readpkts + " packets last read was " + lastRead + " ms ago. it took " + itTook + " ms ");
        }

        currentPacketTime = System.currentTimeMillis();

        if (currentPacketTime - packetsReadTime > 20 && loggedIn && loggedInWatch.hasElapsed(10_000, TimeUnit.MILLISECONDS)) {
            addReportToServer("It took longer than 20 ms to read packets");
        }
        lastPackets = System.currentTimeMillis();

        if (!loggedIn) {
            return;
        }

        if (super.click_type != 0) {
            long l = (super.event_click_time - aLong1220) / 50L;
            if (l > 4095L)
                l = 4095L;
            aLong1220 = super.event_click_time;
            int k2 = super.click_y;
            if (k2 < 0)
                k2 = 0;
            else if (k2 > 502)
                k2 = 502;
            int k3 = super.click_x;
            if (k3 < 0)
                k3 = 0;
            else if (k3 > 764)
                k3 = 764;
            int k4 = k2 * 765 + k3;
            int j5 = 0;
            if (super.click_type == 2)
                j5 = 1;
            int l5 = (int) l;
            /*
             * outgoing.writeOpcode(ClientToServerPackets.MOUSE_CLICK);
             * outgoing.writeInt((l5 << 20) + (j5 << 19) + k4);
             */
        }

        if (anInt1016 > 0) {
            anInt1016--;
        }

        if (super.key_status[1] == 1 || super.key_status[2] == 1 || super.key_status[3] == 1 || super.key_status[4] == 1)
            aBoolean1017 = true;

        if (aBoolean1017 && anInt1016 <= 0) {
            anInt1016 = 20;
            aBoolean1017 = false;
        }

        if (super.awt_focus && !aBoolean954) {
            aBoolean954 = true;
        }

        if (!super.awt_focus && aBoolean954) {
            aBoolean954 = false;
        }

        loadingStages();
        method115();
        timeoutCounter++;
        //System.out.println(timeoutCounter);
        if (timeoutCounter > 750) {
            addReportToServer("Connection timed out at counter " + timeoutCounter + " (" + (int) ((timeoutCounter / 30) * 0.6) + " secs), dropping client");
            try {
                addReportToServer("Dropping client, not a normal logout.");
                dropClient();
            } catch (Exception e) {
                addReportToServer("There was an error dropping the client:");
                e.printStackTrace();
                addReportToServer(e.getMessage());
            }
        }

        updatePlayerInstances();
        forceNPCUpdateBlock();
        process_track_updates();
        processMobChatText();
        processLagReports();
        animation_step++;
        if (crossType != 0) {
            crossIndex += 20;
            if (crossIndex >= 400)
                crossType = 0;
        }
        if (atInventoryInterfaceType != 0) {
            item_container_cycle++;
            if (item_container_cycle >= 15) {
                if (atInventoryInterfaceType == 2) {
                    update_tab_producer = true;
                }
                if (atInventoryInterfaceType == 3)
                    update_chat_producer = true;

                atInventoryInterfaceType = 0;
            }
            //System.out.println("" + atInventoryInterfaceType);
        }
        if (activeInterfaceType != 0) {
            draggingCycles++;
            if (super.cursor_x > mouseDragX + 5 || super.cursor_x < mouseDragX - 5 || super.cursor_y > mouseDragY + 5
                || super.cursor_y < mouseDragY - 5)
                aBoolean1242 = true;//on an item bounds?

            if (super.mouse_button == 0) {
                if (activeInterfaceType == 2) {
                    update_tab_producer = true;
                }
                if (activeInterfaceType == 3) {
                    update_chat_producer = true;
                }
                activeInterfaceType = 0;

                if (aBoolean1242 && draggingCycles >= setting.drag_item_value) {//dragging start?
                    lastActiveInvInterface = -1;
                    processRightClick();
                    if (focusedDragWidget == 5382) {
                        Point southWest, northEast;

                        if (isFixed) {
                            southWest = new Point(56, 81);
                            northEast = new Point(101, 41);
                        } else {
                            int xOffset = (window_width - 237 - Widget.cache[5292].width) / 2;
                            int yOffset = 36 + ((window_height - 503) / 2);
                            southWest = new Point(xOffset + 76, yOffset + 62);
                            northEast = new Point(xOffset + 117, yOffset + 22);
                        }

                        int[] slots = new int[10];

                        for (int i = 0; i < slots.length; i++) {
                            slots[i] = (40 * i) + (int) southWest.getX();
                        }

                        for (int i = 0; i < slots.length; i++) {
                            if ((super.cursor_x >= slots[i]) && (super.cursor_x <= (slots[i] + 41))
                                && (super.cursor_y >= northEast.getY()) && (super.cursor_y <= southWest.getY())) {
                                packetSender.sendItemContainerSlotSwap(focusedDragWidget, 2, dragFromSlot, i);
                                return;
                            }
                        }

                        slots = null;
                    }

                    if (lastActiveInvInterface == focusedDragWidget && mouseInvInterfaceIndex != dragFromSlot) {
                        Widget widget = Widget.cache[focusedDragWidget];
                        int j1 = 0;
                        if (settings[304] == 1 && widget.contentType == 206) {
                            j1 = 1;
                        }
                        if (widget.inventoryItemId[mouseInvInterfaceIndex] <= 0)
                            j1 = 0;
                        if (widget.replaceItems) {
                            int l2 = dragFromSlot;
                            int l3 = mouseInvInterfaceIndex;
                            widget.inventoryItemId[l3] = widget.inventoryItemId[l2];
                            widget.inventoryAmounts[l3] = widget.inventoryAmounts[l2];
                            widget.inventoryItemId[l2] = -1;
                            widget.inventoryAmounts[l2] = 0;
                        } else if (j1 == 1) {
                            int fromTab = 0;
                            int toTab = 0;
                            if (widget.contentType == 206) {
                                for (int tab = 0, totalSlots = 0; tab < 10; tab++) {
                                    if (dragFromSlot <= totalSlots + tabAmounts[tab] - 1
                                        && dragFromSlot >= totalSlots) {
                                        fromTab = tab;
                                    }
                                    if (mouseInvInterfaceIndex <= totalSlots + tabAmounts[tab] - 1
                                        && mouseInvInterfaceIndex >= totalSlots) {
                                        toTab = tab;
                                    }
                                    totalSlots += tabAmounts[tab];
                                }
                            }
                            if (fromTab == toTab || widget.contentType != 206) {
                                int i3 = dragFromSlot;
                                for (int i4 = mouseInvInterfaceIndex; i3 != i4; )
                                    if (i3 > i4) {
                                        widget.swapInventoryItems(i3, i3 - 1);
                                        i3--;
                                    } else if (i3 < i4) {
                                        widget.swapInventoryItems(i3, i3 + 1);
                                        i3++;
                                    }
                            }
                        } else if (j1 == 0) {
                            widget.swapInventoryItems(dragFromSlot, mouseInvInterfaceIndex);
                        }
                        packetSender.sendItemContainerSlotSwap(focusedDragWidget, j1, dragFromSlot,
                            mouseInvInterfaceIndex);
                    }
                } else if ((useOneMouseButton == 1 || menuHasAddFriend(menuActionRow - 1)) && menuActionRow > 2)
                    determineMenuSize();
                else if (menuActionRow > 0)
                    processMenuActions(menuActionRow - 1);
                item_container_cycle = 10;
                super.click_type = 0;
            }
        }

        if (SceneGraph.click_tile_x != -1) {
            int k = SceneGraph.click_tile_x;
            int k1 = SceneGraph.click_tile_y;
            if ((myPrivilege >= 2) && isShiftPressed && ClientConstants.SHIFT_CLICK_TELEPORT) {
                teleport(SceneGraph.click_tile_x + next_region_start, SceneGraph.click_tile_y + next_region_end, plane);
                crossX = super.click_x;
                crossY = super.click_y;
                crossType = 1;
                crossIndex = 0;
                SceneGraph.click_tile_x = -1;
            } else {
                boolean flag = walk(0, 0, 0, 0, local_player.waypoint_y[0], 0, 0, k1, local_player.waypoint_x[0], true, k);
                SceneGraph.click_tile_x = -1;
                if (flag) {
                    crossX = super.click_x;
                    crossY = super.click_y;
                    crossType = 1;
                    crossIndex = 0;
                }
            }
        }
        if (super.click_type == 1 && clickToContinueString != null) {
            clickToContinueString = null;
            update_chat_producer = true;
            super.click_type = 0;
        }
        processMenuClick();
        if (super.mouse_button == 1 || super.click_type == 1)
            anInt1213++;
        if (chatTooltipSupportId != 0 || tabTooltipSupportId != 0 || gameTooltipSupportId != 0) {
            if (anInt1501 < tooltipDelay) {
                anInt1501++;
                if (anInt1501 == tooltipDelay) {
                    if (chatTooltipSupportId != 0) {
                        update_chat_producer = true;
                    }
                    if (tabTooltipSupportId != 0) {
                        update_tab_producer = true;
                    }
                }
            }
        } else if (anInt1501 > 0) {
            anInt1501--;
        }
        if (loading_phase == 2)
            set_camera();

        if (loading_phase == 2 && cutscene)
            rotate_camera();

        for (int i1 = 0; i1 < 5; i1++)
            camera_horizontal_speed[i1]++;

        manageTextInputs();

        if (super.idle++ > 9000) {
            afkCountdown = 250;
            super.idle = 0;
            packetSender.sendPlayerInactive();
        }

        if (pingPacketCounter++ > 65) {
            packetSender.sendEmptyPacket();
        }

        try {
            if (socketStream != null && packetSender.getBuffer().pos > 0) {
                socketStream.queueBytes(packetSender.getBuffer().pos, packetSender.getBuffer().payload);
                packetSender.getBuffer().resetPosition();
                pingPacketCounter = 0;
            }
        } catch (IOException _ex) {
            try {
                addReportToServer("Dropping client, not a normal logout. 2");
                dropClient();
            } catch (Exception e) {
                addReportToServer("There was an error dropping the client: dropClient()");
                e.printStackTrace();
                addReportToServer(e.getMessage());
            }
            _ex.printStackTrace();
            addReportToServer(_ex.getMessage());
        } catch (Exception exception) {
            logout();
            addReportToServer("There was an error sending logout():");
            exception.printStackTrace();
            addReportToServer(exception.getMessage());
        }
    }

    private void processLagReports() {
        if (reports.isEmpty()) {
            return;
        }
        for (int i = 0; i < reports.size(); i++) {
            if (reports.isEmpty())
                break;
            String text = reports.pop();
            if (text == null) return;
            packetSender.sendClientReport(text);
        }
        reports.clear();
    }

    private void clearObjectSpawnRequests() {
        SpawnedObject spawnedObject = (SpawnedObject) spawns.first();
        for (; spawnedObject != null; spawnedObject = (SpawnedObject) spawns.next())
            if (spawnedObject.getLongetivity == -1) {
                spawnedObject.delay = 0;
                handleTemporaryObjects(spawnedObject);
            } else {
                spawnedObject.remove();
            }

    }

    private void resetImageProducers() {
        if (topLeft1BackgroundTile != null)
            return;
        super.fullGameScreen = null;
        chatboxImageProducer = null;
        minimapImageProducer = null;
        tabImageProducer = null;
        gameScreenImageProducer = null;
        chatSettingImageProducer = null;
      //  titleScreen = new ProducingGraphicsBuffer(window_width, window_height);
        Rasterizer2D.clear();
        flameLeftBackground = new ProducingGraphicsBuffer(128, 265);
        Rasterizer2D.clear();
        flameRightBackground = new ProducingGraphicsBuffer(128, 265);
        Rasterizer2D.clear();
        topLeft1BackgroundTile = new ProducingGraphicsBuffer(509, 171);
        Rasterizer2D.clear();
        bottomLeft1BackgroundTile = new ProducingGraphicsBuffer(360, 132);
        Rasterizer2D.clear();
        loginBoxImageProducer = new ProducingGraphicsBuffer(360, 200);
        Rasterizer2D.clear();
        loginScreenAccessories = new ProducingGraphicsBuffer(300, 800);
        Rasterizer2D.clear();
        bottomLeft0BackgroundTile = new ProducingGraphicsBuffer(202, 238);
        Rasterizer2D.clear();
        bottomRightImageProducer = new ProducingGraphicsBuffer(203, 238);
        Rasterizer2D.clear();
        loginMusicImageProducer = new ProducingGraphicsBuffer(203, 238);
        Rasterizer2D.clear();
        middleLeft1BackgroundTile = new ProducingGraphicsBuffer(74, 94);
        Rasterizer2D.clear();
        aRSImageProducer_1115 = new ProducingGraphicsBuffer(75, 94);
        Rasterizer2D.clear();
        if (title_archive != null) {
            drawLogo();
            loadTitleScreen();
        }
        update_producers = true;
    }

    public void draw_loadup(int percent, String string) {
        loading_bar_percent = percent;
        loading_bar_string = string;
        resetImageProducers();
        if (title_archive == null) {
            super.draw_loadup(percent, string);
            return;
        }
        loginBoxImageProducer.init();
        char x = '\u0168';
        char y = '\310';
        byte y_offset = 20;
        adv_font_bold.draw_centered(ClientConstants.CLIENT_NAME + " is loading - please wait...", x / 2,
            y / 2 - 26 - y_offset, 0xffffff, false);
        int outline_y = y / 2 - 18 - y_offset;
        Rasterizer2D.draw_rect_outline(x / 2 - 152, outline_y, 304, 34, 0x8c1111);
        Rasterizer2D.draw_rect_outline(x / 2 - 151, outline_y + 1, 302, 32, 0);
        Rasterizer2D.draw_filled_rect(x / 2 - 150, outline_y + 2, percent * 3, 30, 0x8c1111);
        Rasterizer2D.draw_filled_rect((x / 2 - 150) + percent * 3, outline_y + 2, 300 - percent * 3, 30, 0);
        adv_font_bold.draw_centered(string, x / 2, (y / 2 + 5) - y_offset, 0xffffff, false);
        loginBoxImageProducer.drawGraphics(171, super.graphics, 202);
        if (update_producers) {
            update_producers = false;
            if (!update_flame_components) {
                flameLeftBackground.drawGraphics(0, super.graphics, 0);
                flameRightBackground.drawGraphics(0, super.graphics, 637);
            }
            topLeft1BackgroundTile.drawGraphics(0, super.graphics, 128);
            bottomLeft1BackgroundTile.drawGraphics(371, super.graphics, 202);
            bottomLeft0BackgroundTile.drawGraphics(265, super.graphics, 0);
            bottomRightImageProducer.drawGraphics(265, super.graphics, 562);
            loginMusicImageProducer.drawGraphics(265, super.graphics, 562);
            middleLeft1BackgroundTile.drawGraphics(171, super.graphics, 128);
            aRSImageProducer_1115.drawGraphics(171, super.graphics, 562);
        }
    }

    private void handleScroll(int childWidth, int childHeight, int xPos, int cursor_y, Widget child, int to,
                              int scrollMax) {

        int anInt992;
        if (aBoolean972)
            anInt992 = 32;
        else
            anInt992 = 0;
        aBoolean972 = false;
        if (xPos >= childWidth && xPos < childWidth + 16 && cursor_y >= to && cursor_y < to + 16) {
            child.scrollPosition -= anInt1213 * 4;
        } else if (xPos >= childWidth && xPos < childWidth + 16 && cursor_y >= (to + childHeight) - 16
            && cursor_y < to + childHeight) {
            child.scrollPosition += anInt1213 * 4;
        } else if (xPos >= childWidth - anInt992 && xPos < childWidth + 16 + anInt992 && cursor_y >= to + 16
            && cursor_y < (to + childHeight) - 16 && anInt1213 > 0) {
            int l1 = ((childHeight - 32) * childHeight) / scrollMax;
            if (l1 < 8)
                l1 = 8;
            int i2 = cursor_y - to - 16 - l1 / 2;
            int j2 = childHeight - 32 - l1;
            if (j2 != 0) {
                child.scrollPosition = ((scrollMax - childHeight) * i2) / j2;
            }
            aBoolean972 = true;
        }
    }

    private boolean clickObject(long object, int y, int x) {
        int object_type = get_object_type(object);
        int orientation = get_object_orientation(object);
        if (object_type == 10 || object_type == 11 || object_type == 22) {
            ObjectDefinition class46 = ObjectDefinition.get(get_object_key(object));//check
            int w;
            int h;
            if (orientation == 0 || orientation == 2) {
                w = class46.width;
                h = class46.height;
            } else {
                w = class46.height;
                h = class46.width;
            }
            int k2 = class46.orientation;
            if (orientation != 0)
                k2 = (k2 << orientation & 0xf) + (k2 >> 4 - orientation);
            walk(2, 0, h, 0, local_player.waypoint_y[0], w, k2, y, local_player.waypoint_x[0], false, x);
        } else {
            walk(2, orientation, 0, orientation + 1, local_player.waypoint_y[0], 0, 0, y, local_player.waypoint_x[0], false, x);
        }
        crossX = super.click_x;
        crossY = super.click_y;
        crossType = 2;
        crossIndex = 0;
        return true;
    }

    //MUSIC
    private void saveMidi(boolean flag, byte abyte0[]) {
        SignLink.fadeMidi = flag ? 1 : 0;
        SignLink.saveMidi(abyte0, abyte0.length);
    }

    private void setWaveVolume(int volume) {
        SignLink.wavevol = volume;
    }

    public void playSong(int id) {
        if (!ClientConstants.SOUNDS_ENABLED && !ClientConstants.LOGIN_MUSIC_ENABLED) {
            return;
        }
        if (id != current_track && setting.toggle_music && !low_detail
            && previous_track == 0) {
            next_track = id;
            fade_audio = true;
            resourceProvider.provide(2, next_track);
            current_track = id;
        }
    }

    public void stop_midi() {
        if (SignLink.music != null) {
            SignLink.music.stop();
        }
        SignLink.fadeMidi = 0;
        SignLink.midi = "stop";
    }

    private void adjust_volume(boolean updateMidi, int volume) {
        if (!ClientConstants.SOUNDS_ENABLED) {
            return;
        }
        SignLink.setVolume(volume);
        if (updateMidi) {
            SignLink.midi = "voladjust";
        }
    }

    private boolean save_wav(byte data[], int id) {
        return data == null || SignLink.wavesave(data, id);
    }

    private void process_track_updates() {
        if (!ClientConstants.SOUNDS_ENABLED) {
            return;
        }
        for (int count = 0; count < trackCount; count++) {
            boolean replay = false;
            try {
                Buffer buffer = Track.data(track_loop[count], track_ids[count]);
                //TODO: fix NullPointerException (NPE) here , randomly happens during playing audio
                new SoundPlayer((InputStream) new ByteArrayInputStream(buffer.payload, 0, buffer.pos), sound_effect_volume[count], audio_delay[count]);
                if (System.currentTimeMillis() + (long) (buffer.pos / 22) > track_timer + (long) (current_track_length / 22)) {
                    current_track_length = buffer.pos;
                    track_timer = System.currentTimeMillis();
                    if (save_wav(buffer.payload, buffer.pos)) {
                        current_track = track_ids[count];
                        current_track_repeat = track_loop[count];
                    } else {
                        replay = true;
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
                addReportToServer(exception.getMessage());
            }
            if (!replay || audio_delay[count] == -5) {
                trackCount--;
                for (int index = count; index < trackCount; index++) {
                    track_ids[index] = track_ids[index + 1];
                    track_loop[index] = track_loop[index + 1];
                    audio_delay[index] = audio_delay[index + 1];
                    sound_effect_volume[index] = sound_effect_volume[index + 1];
                }
                count--;
            } else {
                audio_delay[count] = -5;
            }
        }
        if (previous_track > 0) {
            previous_track -= 20;
            if (previous_track < 0)
                previous_track = 0;

            if (previous_track == 0 && setting.toggle_music && !low_detail) {
                next_track = current_track;
                fade_audio = true;
                resourceProvider.provide(2, next_track);
            }
        }
    }

    public Archive request_archive(int file, String requested, String name, int x) {
        byte buffer[] = null;

        try {
            if (indices[0] != null)
                buffer = indices[0].decompress(file);
        } catch (Exception _ex) {
            _ex.printStackTrace();
            addReportToServer(_ex.getMessage());
        }

        // Compare crc...
        if (buffer != null) {
            /*
             * if (ClientConstants.JAGCACHED_ENABLED) { if (!JagGrab.compareCrc(buffer,
             * expectedCRC)) { buffer = null; } }
             */
        }

        // Re-request archive
        if (buffer == null) {
            return null;
        }

        return new Archive(buffer);
    }

    private void dropClient() {
        addReportToServer("Client dropped");
        packetSender.sendDisconnectByPacket(true);
        if (afkCountdown > 0) {
            try {
                logout();
            } catch (Exception e) {
                addReportToServer("There was an error resetting logout: ");
                e.printStackTrace();
                addReportToServer(e.getMessage());
            }
            return;
        }
        Rasterizer2D.draw_rect_outline(2, 2, 229, 39, 0xffffff); // white box around
        Rasterizer2D.draw_filled_rect(3, 3, 227, 37, 0); // black fill

        adv_font_regular.draw_centered("Connection lost.", 119, 18, 0xffffff, true);
        adv_font_regular.draw_centered("Please wait - attempting to reestablish.", 116, 34, 0xffffff, true);

        if (gameScreenImageProducer != null) {
            gameScreenImageProducer.drawGraphics(screen == ScreenMode.FIXED ? 4 : 0, super.graphics,
                screen == ScreenMode.FIXED ? 4 : 0);
        }
        minimapState = ClientConstants.SHOW_MINIMAP;
        travel_destination_x = 0;
        BufferedConnection rsSocket = socketStream;
        loggedIn = false;
        loginFailures = 0;
        login(myUsername, myPassword, true);
        if (!loggedIn)
            logout();
        try {
            rsSocket.close();
        } catch (Exception _ex) {
            _ex.printStackTrace();
            addReportToServer(_ex.getMessage());
        }
    }

    public void set_camera_north() {
        camera_pan_offset = 0;
        camera_tilt_offset = 0;
        camera_angle = 0;
        camera_pan = 0;
        map_rotation = 0;
        map_zoom = 0;
    }

    public void launchURL(String url) {
        String osName = System.getProperty("os.name");
        try {
            if (osName.startsWith("Mac OS")) {
                Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
                Method openURL = fileMgr.getDeclaredMethod("openURL", new Class[]{String.class});
                openURL.invoke(null, new Object[]{url});
            } else if (osName.startsWith("Windows")) {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            } else {
                String[] browsers = {"firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape", "safari"};
                String browser = null;
                for (int count = 0; count < browsers.length && browser == null; count++) {
                    if (Runtime.getRuntime().exec(new String[]{"which", browsers[count]}).waitFor() == 0) {
                        browser = browsers[count];
                    }
                }
                if (browser == null) {
                    throw new Exception("Could not find web browser");
                } else {
                    Runtime.getRuntime().exec(new String[]{browser, url});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            addReportToServer(e.getMessage());
        }
    }

    // Menu actions
    final int USE_SPELL = 626;
    final int[] CATEGORY_IDS = new int[]{991, 985, 986, 987, 980, 989, 988};
    final int[] CATEGORY_IDS_HOVER = new int[]{1874, 1875, 1876, 1877, 1878, 1879, 1880};

    public void resetsidebars() {
        for (int i = 0; i < CATEGORY_IDS.length; i++) {
            Widget.cache[29055 + i].disabledSprite = Client.spriteCache.get(CATEGORY_IDS_HOVER[i]);
            Widget.cache[29055 + i].disabledSprite = Client.spriteCache.get(CATEGORY_IDS[i]);

        }
    }
    private void processMenuActions(int id) {
        if (id < 0) {
            return;
        }

        if (inputDialogState != 0) {
            inputDialogState = 0;
            update_chat_producer = true;
        }

        int first_menu_action = firstMenuAction[id];
        int second_menu_action = secondMenuAction[id];
        int action = menuActionTypes[id];
        long local_player_index = selectedMenuActions[id];
       // System.out.println("menu action " + first_menu_action + ", " + second_menu_action + ", " + action +", " + local_player_index);

        // 317 BELOW
        if (action >= 2000) {
            action -= 2000;
        }

        if (action == 291) {
            packetSender.withdrawAllButOneAction(first_menu_action, second_menu_action, (int) local_player_index);
        }

        if (action == 300) {
            packetSender.withdrawModifiableX(first_menu_action, second_menu_action, (int) local_player_index, modifiableXValue);
        }

        // World map orb
        if (action == 850) {
            packetSender.sendButtonClick(156);
            return;
        }

        if (action == 851) { // Spec orb
            packetSender.sendButtonClick(155);
            return;
        }
        if(second_menu_action == 29055){//favorite
resetsidebars();
            Widget.cache[29055].enabledSprite = Client.spriteCache.get(1874);
            Widget.cache[29055].disabledSprite = Client.spriteCache.get(1874);
        }

        if(second_menu_action == 29056){//Recent
            resetsidebars();

            Widget.cache[29056].enabledSprite = Client.spriteCache.get(1875);
            Widget.cache[29056].disabledSprite = Client.spriteCache.get(1875);


        }
        if(second_menu_action == 29057){//Recent
            resetsidebars();

            Widget.cache[29057].enabledSprite = Client.spriteCache.get(1876);
            Widget.cache[29057].disabledSprite = Client.spriteCache.get(1876);

        }
        if(second_menu_action == 29058){//Recent
            resetsidebars();

            Widget.cache[29058].enabledSprite = Client.spriteCache.get(1877);
            Widget.cache[29058].disabledSprite = Client.spriteCache.get(1877);

        }
        if(second_menu_action == 29059){//Recent
            resetsidebars();

            Widget.cache[29059].enabledSprite = Client.spriteCache.get(1878);
            Widget.cache[29059].disabledSprite = Client.spriteCache.get(1878);

        }
        if(second_menu_action == 29060){//Recent
            resetsidebars();

            Widget.cache[29060].enabledSprite = Client.spriteCache.get(1879);
            Widget.cache[29060].disabledSprite = Client.spriteCache.get(1879);

        }
        if(second_menu_action == 29061){//Recent
            resetsidebars();

            Widget.cache[29061].enabledSprite = Client.spriteCache.get(1880);
            Widget.cache[29061].disabledSprite = Client.spriteCache.get(1880);

        }
        if(second_menu_action == ESCAPE_CONFIG_BUTTON){

            Keybinding.onVarpUpdate(594,1);

        }
//            case ESCAPE_CONFIG_BUTTON:
//                System.out.println("here esc_close: "+esc_close);
//                esc_close = !esc_close;
//                Client.singleton.toggleConfig(594, esc_close ? 1 : 0);
//                return true;
        //System.err.println(Widget.cache[y]);

        //System.out.println("Action: " + y);

        if (withinRange(second_menu_action, 29055, 29061)) {
            Widget.cache[29078].defaultText = "World Teleports - " + Widget.cache[second_menu_action + TEXT_CHILD_OFFSET].defaultText;
        }

        if (second_menu_action == 29055) {
            teleportCategoryIndex = 1;
        }

        if (second_menu_action == 29056) {
            teleportCategoryIndex = 2;
        }

        if (second_menu_action == 29057 || second_menu_action == 30106 || second_menu_action == 13061 || second_menu_action == 1174) {//PK tps
            teleportCategoryIndex = 3;
            TeleportWidget.handleTeleportTab(1);
        }

        if (second_menu_action == 29058 || second_menu_action == 30075 || second_menu_action == 13045 || second_menu_action == 1167) {//Pvm tps
            teleportCategoryIndex = 4;
            TeleportWidget.handleTeleportTab(2);
        }

        if (second_menu_action == 29059 || second_menu_action == 30083 || second_menu_action == 13053 || second_menu_action == 1170) {//Boss tps
            teleportCategoryIndex = 5;
            TeleportWidget.handleTeleportTab(3);
        }

        if (second_menu_action == 29060) {
            teleportCategoryIndex = 6;
            TeleportWidget.handleTeleportTab(4);
        }

        if (second_menu_action == 29061) {
            teleportCategoryIndex = 7;
            TeleportWidget.handleTeleportTab(5);
        }

        if (action == 1895) {
            //System.out.println("lol hi");
            resetSplitPrivateChatMessages();
        }

        if (action == 3000) {
            //System.out.println("L was: " + action);
            //System.out.println("Link was: " + broadcast.getLink());
            Utils.launchURL(broadcast.getLink());
        }

        if (action == 3555) {
            //System.out.println("L was here: " + action);
            //System.out.println("Link was: " + broadcast.getLink());
            Utils.launchURL(broadcast.getLink());
        }
        if (action == 3001) {
            //System.out.println("Dismissed");
            broadcast.dismiss();
        }

        // click logout tab
        if (action == 700) {
            if (tabInterfaceIDs[10] != -1) {
                if (sidebarId == 10) {
                    showTabComponents = !showTabComponents;
                } else {
                    showTabComponents = true;
                }
                sidebarId = 10;
                update_tab_producer = true;
            }
        }

        if (action == 769) {
            Widget d = Widget.cache[second_menu_action];
            Widget p = Widget.cache[(int) local_player_index];
            if (!d.dropdown.isOpen()) {
                if (p.dropdownOpen != null) {
                    p.dropdownOpen.dropdown.setOpen(false);
                }
                p.dropdownOpen = d;
            } else {
                p.dropdownOpen = null;
            }
            d.dropdown.setOpen(!d.dropdown.isOpen());
        } else if (action == 770) {
            Widget d = Widget.cache[second_menu_action];
            Widget p = Widget.cache[(int) local_player_index];
            if (first_menu_action >= d.dropdown.getOptions().length)
                return;
            d.dropdown.setSelected(d.dropdown.getOptions()[first_menu_action]);
            d.dropdown.setOpen(false);
            d.dropdown.getDrop().selectOption(first_menu_action, d);
            p.dropdownOpen = null;
        }

        // reset compass to north
        if (action == 696) {
            set_camera_north();
        }
        //System.out.println("Clicked button " + action);
        // button clicks
        switch (action) {
            case 1500:
            case 1501:
            case 1506:
            case 1507:
            case 1510:
            case 1511:
            case 1512:
            case 1315:
            case 1316:
            case 1317:
            case 1318:
            case 1319:
            case 1320:
            case 1321:
            case 879:
            case 475:
            case 476:
            case 1050:
            case 268:
                // button click
                packetSender.sendButtonClick(action);
                break;
        }

        // custom
        if (action == 258) {
            if (setting.show_hit_predictor) {
                setting.show_exp_counter = true;
                setting.show_hit_predictor = false;
                spriteCache.get(76).drawSprite(screen == ScreenMode.FIXED ? 0 : window_width - 216, 21);
            } else {
                setting.show_hit_predictor = true;
                setting.show_exp_counter = false;
                spriteCache.get(75).drawSprite(screen == ScreenMode.FIXED ? 0 : window_width - 216, 21);
            }
            setting.save();
        }

        // click autocast
        if (action == 104) {
            Widget widget = Widget.cache[second_menu_action];
            packetSender.sendButtonClick(widget.id);
            /*
             * spellId = widget.id; if (!autocast) { autocast = true; autoCastId =
             * widget.id; sendPacket(new ClickButton(widget.id)); } else if (autoCastId ==
             * widget.id) { autocast = false; autoCastId = 0; sendPacket(new
             * ClickButton(widget.id)); } else if (autoCastId != widget.id) { autocast =
             * true; autoCastId = widget.id; sendPacket(new ClickButton(widget.id)); }
             */
        }

        // item on npc
        if (action == 582) {
            Npc npc = npcs[(int) local_player_index];
            if (npc != null) {
                walk(2, 0, 1, 0, local_player.waypoint_y[0], 1, 0, npc.waypoint_y[0], local_player.waypoint_x[0], false,
                    npc.waypoint_x[0]);
                crossX = super.click_x;
                crossY = super.click_y;
                crossType = 2;
                crossIndex = 0;
                packetSender.sendUseItemOnNPC(useItem, (int) local_player_index, selectedItemIdSlot, interfaceitemSelectionTypeIn);
            }
        }

        // picking up ground item
        if (action == 234) {
            boolean flag1 = walk(2, 0, 0, 0, local_player.waypoint_y[0], 0, 0, second_menu_action, local_player.waypoint_x[0], false,
                first_menu_action);
            if (!flag1)
                flag1 = walk(2, 0, 1, 0, local_player.waypoint_y[0], 1, 0, second_menu_action, local_player.waypoint_x[0], false, first_menu_action);
            crossX = super.click_x;
            crossY = super.click_y;
            crossType = 2;
            crossIndex = 0;
            // pickup ground item
            packetSender.sendPickupItem(second_menu_action + next_region_end, (int) local_player_index, first_menu_action + next_region_start);
        }

        // using item on object
        if (action == 62 && clickObject(local_player_index, second_menu_action, first_menu_action)) {
            packetSender.sendUseItemOnObject(interfaceitemSelectionTypeIn, get_object_key(local_player_index), second_menu_action + next_region_end,
                selectedItemIdSlot, first_menu_action + next_region_start, useItem);
        }

        // using item on ground item
        if (action == 511) {
            boolean flag2 = walk(2, 0, 0, 0, local_player.waypoint_y[0], 0, 0, second_menu_action, local_player.waypoint_x[0], false,
                first_menu_action);
            if (!flag2)
                flag2 = walk(2, 0, 1, 0, local_player.waypoint_y[0], 1, 0, second_menu_action, local_player.waypoint_x[0], false, first_menu_action);
            crossX = super.click_x;
            crossY = super.click_y;
            crossType = 2;
            crossIndex = 0;
            // item on ground item
            packetSender.sendUseItemOnGroundItem(interfaceitemSelectionTypeIn, useItem, (int) local_player_index, second_menu_action + next_region_end,
                selectedItemIdSlot, first_menu_action + next_region_start);
        }

        // item option 1
        if (action == 74) {
            packetSender.sendItemOption1(second_menu_action, (int) local_player_index, first_menu_action);
            item_container_cycle = 0;
            atInventoryInterface = second_menu_action;
            atInventoryIndex = first_menu_action;
            atInventoryInterfaceType = 2;
            if (Widget.cache[second_menu_action].parent == widget_overlay_id) {
                atInventoryInterfaceType = 1;
            }
            if (Widget.cache[second_menu_action].parent == backDialogueId) {
                atInventoryInterfaceType = 3;
            }
        }

        if (first_menu_action == 23004) {
            Client.update_chat_producer = false;
            this.messagePromptRaised = false;
            this.promptInput = "";
        }

        // widget action
        if (action == 315) {
            Widget widget = Widget.cache[second_menu_action];
            boolean flag8 = true;
            if (widget.type == Widget.TYPE_CONFIG || widget.id == 26101 || widget.id == 26102) { // Placeholder (or
                // search) bank button
                // toggle
                widget.active = !widget.active;
            } else if (widget.type == Widget.TYPE_CONFIG_HOVER) {
                Widget.handleConfigHover(widget);
            } else if (widget.type == Widget.TYPE_CONFIG_BUTTON_HOVERED_SPRITE_OUTLINE) {
                Widget.handleConfigSpriteHover(widget);
            }

            // bank search chat
            if (widget.id == 26102) {
                if (widget.active) {
                    searchingBank = true;
                    update_chat_producer = true;
                    inputDialogState = 0;
                    messagePromptRaised = true;
                    promptInput = "";
                    interfaceInputAction = 1;
                    inputMessage = "Enter an item to search for";
                } else {
                    Widget.cache[26102].active = false;
                    searchingBank = false;
                    update_chat_producer = true;
                    inputDialogState = 0;
                    messagePromptRaised = false;
                    promptInput = "";
                    interfaceInputAction = 1;
                    inputMessage = "";
                }
            }

            // System.out.println("Button: "+button);
            if (widget.contentType > 0) {
                flag8 = promptUserForInput(widget);
            }

            if (setting.click(this, second_menu_action)) {
                return;
            }

            if (setting.settingButtons(second_menu_action)) {
                return;
            }

            if (flag8) {
                OptionTabWidget.optionTabButtons(second_menu_action);

                /** Faster spec bars toggle **/
                // Handle radio buttons
                switch (second_menu_action) {

                    case 72004:
                        if (ClientConstants.SPAWN_TAB_DISPLAY_ALL_ITEMS_PRELOADED) {
                            SpawnTabAllItems.searchingSpawnTab = true;
                        } else {
                            SpawnTab.searchingSpawnTab = true;
                        }
                        break;
                    case 72007:
                        if (ClientConstants.SPAWN_TAB_DISPLAY_ALL_ITEMS_PRELOADED) {
                            SpawnTabAllItems.spawnType = SpawnTabType.INVENTORY;
                            SpawnTabAllItems.searchingSpawnTab = true;
                        } else {
                            SpawnTab.spawnType = SpawnTabType.INVENTORY;
                            SpawnTab.searchingSpawnTab = true;
                        }
                        break;
                    case 72011:
                        if (ClientConstants.SPAWN_TAB_DISPLAY_ALL_ITEMS_PRELOADED) {
                            SpawnTabAllItems.spawnType = SpawnTabType.BANK;
                            SpawnTabAllItems.searchingSpawnTab = true;
                        } else {
                            SpawnTab.spawnType = SpawnTabType.BANK;
                            SpawnTab.searchingSpawnTab = true;
                        }
                        break;

                    case 12697: // Drag Setting.
                        enter_amount_title = "Please enter your desired Drag Setting <col=A10081>(5 is OSRS):";
                        enter_amount_title2 = "This setting goes hand in hand with switching, choose wisely and test!";
                        messagePromptRaised = false;
                        inputDialogState = 3;
                        amountOrNameInput = "";
                        update_chat_producer = true;
                        break;

                    case Keybinding.RESTORE_DEFAULT:
                        Keybinding.restoreDefault();
                        Keybinding.updateInterface();
                        sendMessage("Default keys loaded.", 0, "");
                        setting.save();
                        break;
                    case 29138:
                    case 29038:
//                    case 29063:
                    case 29113:
                    case 29163:
                    case 29188:
                    case 29213:
                    case 29238:
                    case 30007:
                    case 48023:
                    case 33033:
                    case 30108:
                    case 7473:
                    case 7562:
                    case 7487:
                    case 7788:
                    case 8481:
                    case 7612:
                    case 7587:
                    case 7662:
                    case 7462:
                    case 7548:
                    case 7687:
                    case 7537:
                    case 7623:
                    case 12322:
                    case 7637:
                    case 12311:
                    case 155:
                        packetSender.sendSpecialAttackToggle(second_menu_action);
                        break;
                    default:
                        if (Widget.radioButtons.contains(second_menu_action)) {
                            checkRadioOptions(second_menu_action);
                        }
                        if (Widget.cache[second_menu_action].clickable && Widget.cache[second_menu_action].serverCheck) {
                            packetSender.sendConfirm(2, 0);
                            widgetId = second_menu_action;
                            Widget.cache[second_menu_action].disabledSprite = spriteCache
                                .get(Widget.cache[second_menu_action].clickSprite2); // change this to add a variable stored in
                            // clickable buttons that can store the
                            // sprite ids
                            Widget.cache[second_menu_action].enabledSprite = spriteCache.get(Widget.cache[second_menu_action].clickSprite2);
                        }
                        if (Widget.cache[second_menu_action].clickable && !Widget.cache[second_menu_action].serverCheck) {
                            if (Widget.cache[second_menu_action].disabledSprite == spriteCache
                                .get(Widget.cache[second_menu_action].clickSprite1)) {
                                Widget.cache[second_menu_action].disabledSprite = spriteCache
                                    .get(Widget.cache[second_menu_action].clickSprite2);
                                Widget.cache[second_menu_action].enabledSprite = spriteCache
                                    .get(Widget.cache[second_menu_action].clickSprite2);
                                break;
                            }
                            if (Widget.cache[second_menu_action].disabledSprite == spriteCache
                                .get(Widget.cache[second_menu_action].clickSprite1)) {
                                Widget.cache[second_menu_action].disabledSprite = spriteCache
                                    .get(Widget.cache[second_menu_action].clickSprite2);
                                Widget.cache[second_menu_action].enabledSprite = spriteCache
                                    .get(Widget.cache[second_menu_action].clickSprite2);
                                break;
                            }
                        }
                        packetSender.sendButtonClick(second_menu_action);
                }
            }
        }

// player option
        if (action == 561) {
            Player player = players[(int) local_player_index];
            if (player != null) {
                walk(2, 0, 1, 0, local_player.waypoint_y[0], 1, 0, player.waypoint_y[0], local_player.waypoint_x[0],
                    false, player.waypoint_x[0]);
                crossX = super.click_x;
                crossY = super.click_y;
                crossType = 2;
                crossIndex = 0;
                anInt1188 += (int) local_player_index;
                if (anInt1188 >= 90) {
                    // (anti-cheat)
                    // outgoing.writeOpcode(136);
                    anInt1188 = 0;
                }
                packetSender.sendPlayerOption1((int) local_player_index);
            }
        }

        // npc option 1
        if (action == 20) {
            Npc npc = npcs[(int) local_player_index];
            if (npc != null) {
                walk(2, 0, 1, 0, local_player.waypoint_y[0], 1, 0, npc.waypoint_y[0], local_player.waypoint_x[0], false,
                    npc.waypoint_x[0]);
                crossX = super.click_x;
                crossY = super.click_y;
                crossType = 2;
                crossIndex = 0;
                // npc action 1
                //System.out.println("npc option 1");
                packetSender.sendNPCOption1((int) local_player_index);
                if (this.isInputFieldInFocus()) {
                    this.resetInputFieldFocus();
                    inputString = "";
                }
            }
        }

        // player option 2
        if (action == 779) {
            Player player = players[(int) local_player_index];
            if (player != null) {
                walk(2, 0, 1, 0, local_player.waypoint_y[0], 1, 0, player.waypoint_y[0], local_player.waypoint_x[0],
                    false, player.waypoint_x[0]);
                crossX = super.click_x;
                crossY = super.click_y;
                crossType = 2;
                crossIndex = 0;
                // player option 2
                packetSender.sendAttackPlayer((int) local_player_index);
            }
        }

        // clicking tiles hello
        if (action == 519) {
            // System.out.println("Clicked  here.");
            if (!menuOpen) {
                //System.out.println("Clicked at: " + (super.click_y - 4) + " | " + (super.click_x - 4));
                scene.register_click(super.click_y - 4, super.click_x - 4);
            } else {
                scene.register_click(second_menu_action - 4, first_menu_action - 4);
                //System.out.println("Clicked at else: " + (super.click_y - 4) + " | " + (slot - 4));
            }
        }

        // object option 4
        if (action == 1062) {
            clickObject(local_player_index, second_menu_action, first_menu_action);
            // object option 4
            packetSender.sendObjectOption4(get_object_key(local_player_index), second_menu_action + next_region_end, first_menu_action + next_region_start);
        }

        // continue dialogue
        if (action == 679 && !continuedDialogue) {
            packetSender.sendNextDialogue(second_menu_action);
            continuedDialogue = true;
        }

        // Pressed button, this used to be 647 but is now 648 for presets text and 649
        // for spawning text because we add choice to the action in buildInterfaceMenu.
        if (action == 647 || action == 648) {
            // Spawn tab?
            if (second_menu_action >= 72031 && second_menu_action <= 73475) {
                int index = second_menu_action - 72031;
                if (ClientConstants.SPAWN_TAB_DISPLAY_ALL_ITEMS_PRELOADED) {
                    int item = SpawnTabAllItems.getResultsArray()[index];
                    if (item > 0) {
                        packetSender.sendSpawnTabSelection(item, first_menu_action == 1, SpawnTabAllItems.spawnType == SpawnTabType.BANK);
                    }
                    if (first_menu_action == 0) {
                        SpawnTabAllItems.searchingSpawnTab = true;
                    }
                } else {
                    int item = SpawnTab.getResultsArray()[index];
                    if (item > 0) {
                        packetSender.sendSpawnTabSelection(item, first_menu_action == 1, SpawnTab.spawnType == SpawnTabType.BANK);
                    }
                    if (first_menu_action == 0) {
                        SpawnTab.searchingSpawnTab = true;
                    }
                }
                return;
            }

            // Key bindings?
            if (widget_overlay_id == 53000) {
                for (int i = 0; i < 14; i++) {
                    if (second_menu_action == 53048 + (i * 3)) {
                        int key = KeyEvent.VK_F1 + first_menu_action;
                        if (key > KeyEvent.VK_F12) {
                            key = KeyEvent.VK_ESCAPE;
                        }
                        Keybinding.bind(i, key);
                        return;
                    }
                }
            }
            packetSender.sendButtonAction(second_menu_action, first_menu_action);
        }

        // using bank all option of the bank interface
        if (action == 431) {
            packetSender.sendItemContainerOption4(first_menu_action, second_menu_action, (int) local_player_index);
            item_container_cycle = 0;
            atInventoryInterface = second_menu_action;
            atInventoryIndex = first_menu_action;
            atInventoryInterfaceType = 2;
            if (Widget.cache[second_menu_action].parent == widget_overlay_id) {
                atInventoryInterfaceType = 1;
            }
            if (Widget.cache[second_menu_action].parent == backDialogueId) {
                atInventoryInterfaceType = 3;
            }
        }
        if (action == 449) {
            String url = selectedMsg.substring(18, selectedMsg.lastIndexOf(">"));
            Utils.launchURL(url);
        }
        if (action == 337 || action == 42 || action == 792 || action == 322 || action == 338) {
            String string = menuActionText[id];
            int indexOf = string.indexOf(">");
            if (indexOf != -1) {
                String addedName = string.substring(indexOf + 1);
                //System.out.println("Username string is: " + string);
                if (!StringUtils.VALID_NAME.matcher(addedName).matches())
                    return;
                addedName = StringUtils.capitalizeIf(addedName);
//                long usernameHash = StringUtils.encodeBase37(string.substring(indexOf).trim());
                if (action == 337)
                    addFriend(addedName);
                if (action == 42)
                    addIgnore(addedName);
                if (action == 792)
                    removeFriend(addedName);
                if (action == 322)
                    removeIgnore(addedName);
                if (action == 338)
                    openPrivateChatMessageInput(addedName);
            }
        }
        // using the bank x option on the bank interface
        if (action == 53) {
            packetSender.sendItemContainerOption5(second_menu_action, first_menu_action, (int) local_player_index);
            item_container_cycle = 0;
            atInventoryInterface = second_menu_action;
            atInventoryIndex = first_menu_action;
            atInventoryInterfaceType = 2;
            if (Widget.cache[second_menu_action].parent == widget_overlay_id)
                atInventoryInterfaceType = 1;
            if (Widget.cache[second_menu_action].parent == backDialogueId)
                atInventoryInterfaceType = 3;
        }

        if (action == 539) {
            packetSender.sendItemOption3((int) local_player_index, first_menu_action, second_menu_action);
            item_container_cycle = 0;
            atInventoryInterface = second_menu_action;
            atInventoryIndex = first_menu_action;
            atInventoryInterfaceType = 2;
            if (Widget.cache[second_menu_action].parent == widget_overlay_id) {
                atInventoryInterfaceType = 1;
            }
            if (Widget.cache[second_menu_action].parent == backDialogueId) {
                atInventoryInterfaceType = 3;
            }
        }
        if (action == 484 || action == 6 || action == 525) {
            try {
                String string = menuActionText[id];
                int indexOf = string.indexOf(">");
                if (indexOf != -1) {
                    //System.out.println("Username string is actually: " + string);
                    if (string.contains("Accept trade") || string.contains("Accept challenge") || string.contains("Accept gamble")) {
                        string = string.substring(indexOf + 1).trim();
                    } else {
                        string = string.substring(indexOf + 5).trim();
                    }
                    String username = StringUtils
                        .formatText(StringUtils.decodeBase37(StringUtils.encodeBase37(string)));
                    boolean flag9 = false;
                    for (int count = 0; count < players_in_region; count++) {
                        Player player = players[local_players[count]];
                        if (player == null || player.username == null || !player.username.equalsIgnoreCase(username)) {
                            continue;
                        }
                        walk(2, 0, 1, 0, local_player.waypoint_y[0], 1, 0, player.waypoint_y[0],
                            local_player.waypoint_x[0], false, player.waypoint_x[0]);

                        // accepting trade
                        if (action == 484) {
                            packetSender.sendTradePlayer(local_players[count]);
                        }

                        // accepting gamble
                        if (action == 525) {
                            packetSender.sendGambleRequest(local_players[count]);
                        }

                        // accepting a challenge
                        if (action == 6) {
                            anInt1188 += (int) local_player_index;
                            if (anInt1188 >= 90) {
                                // (anti-cheat)
                                // outgoing.writeOpcode(136);
                                anInt1188 = 0;
                            }
                            packetSender.sendChatboxDuel(local_players[count]);
                        }
                        flag9 = true;
                        break;
                    }

                    if (!flag9)
                        sendMessage("Unable to find " + username, 0, "");
                }
            } catch (Exception e) {
                e.printStackTrace();
                addReportToServer(e.getMessage());
            }
        }

        // Using an item on another item
        if (action == 870) {
            packetSender.sendUseItemOnItem(first_menu_action, selectedItemIdSlot, (int) local_player_index, interfaceitemSelectionTypeIn,
                useItem, second_menu_action);
            item_container_cycle = 0;
            atInventoryInterface = second_menu_action;
            atInventoryIndex = first_menu_action;
            atInventoryInterfaceType = 2;
            if (Widget.cache[second_menu_action].parent == widget_overlay_id)
                atInventoryInterfaceType = 1;
            if (Widget.cache[second_menu_action].parent == backDialogueId)
                atInventoryInterfaceType = 3;
        }

        // Using the drop option of an item
        if (action == 847) {
            packetSender.sendDropItem((int) local_player_index, second_menu_action, first_menu_action);
            item_container_cycle = 0;
            atInventoryInterface = second_menu_action;
            atInventoryIndex = first_menu_action;
            atInventoryInterfaceType = 2;
            if (Widget.cache[second_menu_action].parent == widget_overlay_id)
                atInventoryInterfaceType = 1;
            if (Widget.cache[second_menu_action].parent == backDialogueId)
                atInventoryInterfaceType = 3;
        }
        // useable spells
        if (action == USE_SPELL) {
            Widget widget = Widget.cache[second_menu_action];
            widget_highlighted = 1;
            spellId = widget.id;
            anInt1137 = second_menu_action;
            selectedTargetMask = widget.selectedTargetMask;
            item_highlighted = 0;
            String tooltip = widget.spellName;
            if (tooltip.contains("<br>")) {
                tooltip = tooltip.replaceAll("<br>", " ");
            }
            if (widget.selectedActionName.toLowerCase().contains("cast on")) {
                widget.selectedActionName = widget.selectedActionName.replaceAll(" on", "");
            }
            selected_target_id = widget.selectedActionName + " <col=65280>" + tooltip + "</col> -> ";
            if (selectedTargetMask == 16) {
                sidebarId = 3;
                update_tab_producer = true;
            }
            return;
        }

        // Using the bank 5 option on a bank widget
        if (action == 78) {
            packetSender.sendItemContainerOption2(second_menu_action, (int) local_player_index, first_menu_action);
            item_container_cycle = 0;
            atInventoryInterface = second_menu_action;
            atInventoryIndex = first_menu_action;
            atInventoryInterfaceType = 2;
            if (Widget.cache[second_menu_action].parent == widget_overlay_id)
                atInventoryInterfaceType = 1;
            if (Widget.cache[second_menu_action].parent == backDialogueId)
                atInventoryInterfaceType = 3;
        }

        // player option 2
        if (action == 27) {
            Player player = players[(int) local_player_index];
            if (player != null) {
                walk(2, 0, 1, 0, local_player.waypoint_y[0], 1, 0, player.waypoint_y[0], local_player.waypoint_x[0],
                    false, player.waypoint_x[0]);
                crossX = super.click_x;
                crossY = super.click_y;
                crossType = 2;
                crossIndex = 0;
                anInt986 += (int) local_player_index;
                if (anInt986 >= 54) {
                    // (anti-cheat)
                    // outgoing.writeOpcode(189);
                    // outgoing.writeByte(234);
                    anInt986 = 0;
                }
                packetSender.sendFollowPlayer((int) local_player_index);
            }
        }

        // Used for lighting logs
        if (action == 213) {
            boolean flag3 = walk(2, 0, 0, 0, local_player.waypoint_y[0], 0, 0, second_menu_action, local_player.waypoint_x[0], false,
                first_menu_action);
            if (!flag3)
                flag3 = walk(2, 0, 1, 0, local_player.waypoint_y[0], 1, 0, second_menu_action, local_player.waypoint_x[0], false, first_menu_action);
            crossX = super.click_x;
            crossY = super.click_y;
            crossType = 2;
            crossIndex = 0;
            // light item
            /*
             * outgoing.writeOpcode(79); outgoing.writeLEShort(button + regionBaseY);
             * outgoing.writeShort(clicked); outgoing.writeShortA(first + regionBaseX);
             */
        }

        // Using the unequip option on the equipment tab interface
        if (action == 632) {
            packetSender.sendItemContainerOption1(second_menu_action, first_menu_action, (int) local_player_index);
            if (widget_overlay_id == 52000) {
                tradeModified(second_menu_action, first_menu_action);
            }
            // System.out.println(button + " " + first + " " + clicked);
            item_container_cycle = 0;
            atInventoryInterface = second_menu_action;
            atInventoryIndex = first_menu_action;
            atInventoryInterfaceType = 2;
            if (Widget.cache[second_menu_action].parent == widget_overlay_id)
                atInventoryInterfaceType = 1;
            if (Widget.cache[second_menu_action].parent == backDialogueId)
                atInventoryInterfaceType = 3;
            if (this.isInputFieldInFocus()) {
                this.resetInputFieldFocus();
                inputString = "";
            }
        }

        if (action == 1004) {
            if (tabInterfaceIDs[10] != -1) {
                sidebarId = 10;
                update_tab_producer = true;
            }
        }
        if (action == 1003) {
            clanChatMode = 2;
            update_chat_producer = true;
        }
        if (action == 1002) {
            clanChatMode = 1;
            update_chat_producer = true;
        }
        if (action == 1001) {
            clanChatMode = 0;
            update_chat_producer = true;
        }
        if (action == 1000) {
            cButtonCPos = 4;
            chatTypeView = 11;
            update_chat_producer = true;
        }

        if (action == 999) {
            cButtonCPos = 0;
            chatTypeView = 0;
            update_chat_producer = true;
        }
        if (action == 998) {
            cButtonCPos = 1;
            chatTypeView = 5;
            update_chat_producer = true;
        }

        // public chat "hide" option
        if (action == 997) {
            set_public_channel = 3;
            update_chat_producer = true;

            packetSender.sendChatConfigurations(set_public_channel, privateChatMode, tradeMode, clanChatMode);
        }

        // public chat "off" option
        if (action == 996) {
            set_public_channel = 2;
            update_chat_producer = true;

            packetSender.sendChatConfigurations(set_public_channel, privateChatMode, tradeMode, clanChatMode);
        }

        // public chat "friends" option
        if (action == 995) {
            set_public_channel = 1;
            update_chat_producer = true;

            packetSender.sendChatConfigurations(set_public_channel, privateChatMode, tradeMode, clanChatMode);
        }

        // public chat "on" option
        if (action == 994) {
            set_public_channel = 0;
            update_chat_producer = true;

            packetSender.sendChatConfigurations(set_public_channel, privateChatMode, tradeMode, clanChatMode);
        }

        // public chat main click
        if (action == 993) {
            cButtonCPos = 2;
            chatTypeView = 1;
            update_chat_producer = true;
        }

        // private chat "off" option
        // private chat "off" option
        if (action == 992) {
            privateChatMode = 2;
            update_chat_producer = true;

            packetSender.sendChatConfigurations(set_public_channel, privateChatMode, tradeMode, clanChatMode);
        }

        // private chat "friends" option
        if (action == 991) {
            privateChatMode = 1;
            update_chat_producer = true;

            packetSender.sendChatConfigurations(set_public_channel, privateChatMode, tradeMode, clanChatMode);
        }

        // private chat "on" option
        if (action == 990) {
            privateChatMode = 0;
            update_chat_producer = true;

            packetSender.sendChatConfigurations(set_public_channel, privateChatMode, tradeMode, clanChatMode);
        }

        // private chat main click
        if (action == 989) {
            cButtonCPos = 3;
            chatTypeView = 2;
            update_chat_producer = true;
        }

        // trade message privacy option "off" option
        if (action == 987) {
            tradeMode = 2;
            update_chat_producer = true;

            packetSender.sendChatConfigurations(set_public_channel, privateChatMode, tradeMode, clanChatMode);
        }

        // trade message privacy option "friends" option
        if (action == 986) {
            tradeMode = 1;
            update_chat_producer = true;

            packetSender.sendChatConfigurations(set_public_channel, privateChatMode, tradeMode, clanChatMode);
        }

        // trade message privacy option "on" option
        if (action == 985) {
            tradeMode = 0;
            update_chat_producer = true;

            packetSender.sendChatConfigurations(set_public_channel, privateChatMode, tradeMode, clanChatMode);
        }

        // trade message privacy option "off" option
        if (action == 987) {
            tradeMode = 2;
            update_chat_producer = true;

            packetSender.sendChatConfigurations(set_public_channel, privateChatMode, tradeMode, clanChatMode);
        }

        // trade message privacy option "friends" option
        if (action == 986) {
            tradeMode = 1;
            update_chat_producer = true;

            packetSender.sendChatConfigurations(set_public_channel, privateChatMode, tradeMode, clanChatMode);
        }

        // trade message privacy option "on" option
        if (action == 985) {
            tradeMode = 0;
            update_chat_producer = true;

            packetSender.sendChatConfigurations(set_public_channel, privateChatMode, tradeMode, clanChatMode);
        }

        // trade message privacy option main click
        if (action == 984) {
            cButtonCPos = 5;
            chatTypeView = 3;
            update_chat_producer = true;
        }

        if (action == 980) {
            cButtonCPos = 6;
            chatTypeView = 4;
            update_chat_producer = true;
        }

        // Using 3rd option of an item
        if (action == 493) {
            // item option 3
            packetSender.sendItemOption2(second_menu_action, first_menu_action, (int) local_player_index);
            item_container_cycle = 0;
            atInventoryInterface = second_menu_action;
            atInventoryIndex = first_menu_action;
            atInventoryInterfaceType = 2;
            if (Widget.cache[second_menu_action].parent == widget_overlay_id)
                atInventoryInterfaceType = 1;
            if (Widget.cache[second_menu_action].parent == backDialogueId)
                atInventoryInterfaceType = 3;
        }

        // clicking some sort of tile
        if (action == 652) {
            boolean flag4 = walk(2, 0, 0, 0, local_player.waypoint_y[0], 0, 0, second_menu_action, local_player.waypoint_x[0], false,
                first_menu_action);
            if (!flag4)
                flag4 = walk(2, 0, 1, 0, local_player.waypoint_y[0], 1, 0, second_menu_action, local_player.waypoint_x[0], false, first_menu_action);
            crossX = super.click_x;
            crossY = super.click_y;
            crossType = 2;
            crossIndex = 0;
            // unknown (non-anti bot)
            /*
             * outgoing.writeOpcode(156); outgoing.writeShortA(first + regionBaseX);
             * outgoing.writeLEShort(button + regionBaseY); outgoing.writeLEShortA(clicked);
             */
        }

        // Using a spell on a ground item
        if (action == 94) {
            boolean flag5 = walk(2, 0, 0, 0, local_player.waypoint_y[0], 0, 0, second_menu_action, local_player.waypoint_x[0], false,
                first_menu_action);
            if (!flag5)
                flag5 = walk(2, 0, 1, 0, local_player.waypoint_y[0], 1, 0, second_menu_action, local_player.waypoint_x[0], false, first_menu_action);
            crossX = super.click_x;
            crossY = super.click_y;
            crossType = 2;
            crossIndex = 0;
            packetSender.sendUseMagicOnGroundItem(second_menu_action + next_region_end, (int) local_player_index, first_menu_action + next_region_start, anInt1137);
        }
        if (action == 646) {
            // button click

            switch (second_menu_action) {

                default:
                    packetSender.sendButtonClick(second_menu_action);
                    break;
            }

            Widget widget = Widget.cache[second_menu_action];
            if (widget.valueIndexArray != null && widget.valueIndexArray[0][0] == 5) {
                int settingId = widget.valueIndexArray[0][1];
                if (settings[settingId] != widget.requiredValues[0]) {
                    if (widget.updateConfig) {
                        settings[settingId] = widget.requiredValues[0];
                        //System.out.println("setting: " + settings[id] + " value=" + id);
                        updateVarp(settingId);
                    }
                }
            }
        }

        // Using the 2nd option of an npc
        if (action == 225) {
            Npc npc = npcs[(int) local_player_index];
            if (npc != null) {
                walk(2, 0, 1, 0, local_player.waypoint_y[0], 1, 0, npc.waypoint_y[0], local_player.waypoint_x[0], false,
                    npc.waypoint_x[0]);
                crossX = super.click_x;
                crossY = super.click_y;
                crossType = 2;
                crossIndex = 0;
                anInt1226 += (int) local_player_index;
                if (anInt1226 >= 85) {
                    // (anti-cheat)
                    // outgoing.writeOpcode(230);
                    // outgoing.writeByte(239);
                    anInt1226 = 0;
                }
                //System.out.println("npc option 2");
                packetSender.sendNPCOption2((int) local_player_index);
            }
        }

        // Using the 3rd option of an npc
        if (action == 965) {
            Npc npc = npcs[(int) local_player_index];
            if (npc != null) {
                walk(2, 0, 1, 0, local_player.waypoint_y[0], 1, 0, npc.waypoint_y[0], local_player.waypoint_x[0], false,
                    npc.waypoint_x[0]);
                crossX = super.click_x;
                crossY = super.click_y;
                crossType = 2;
                crossIndex = 0;
                anInt1134++;
                if (anInt1134 >= 96) {
                    // (anti-cheat)
                    // outgoing.writeOpcode(152);
                    // outgoing.writeByte(88);
                    anInt1134 = 0;
                }
                packetSender.sendNPCOption3((int) local_player_index);
            }
        }

        // Using a spell on an npc
        if (action == 413) {
            Npc npc = npcs[(int) local_player_index];
            if (npc != null) {
                walk(2, 0, 1, 0, local_player.waypoint_y[0], 1, 0, npc.waypoint_y[0], local_player.waypoint_x[0], false,
                    npc.waypoint_x[0]);
                crossX = super.click_x;
                crossY = super.click_y;
                crossType = 2;
                crossIndex = 0;
                packetSender.sendUseMagicOnNPC((int) local_player_index, anInt1137);
            }
        }

        // close open interfaces
        if (action == 200) {
            clearTopInterfaces();
        }

        // Clicking "Examine" option on an npc
        //TODO: warning by ken, why check the same ID twice?
        if (action == 1025 || action == 1025) {
            Npc npc = npcs[(int) local_player_index];
            if (npc != null) {
                NpcDefinition entityDef = npc.desc;
                if (entityDef.configs != null)
                    entityDef = entityDef.get_configs();
                if (entityDef != null) {
                    packetSender.sendExamineNPC(entityDef.id);
                }
            }
        }

        if (action == 900) {
            clickObject(local_player_index, second_menu_action, first_menu_action);
            // object option 2
            packetSender.sendObjectOption2(get_object_key(local_player_index), second_menu_action + next_region_end, first_menu_action + next_region_start);
        }

        // Using the "Attack" option on a npc
        if (action == 412) {
            Npc npc = npcs[(int) local_player_index];
            if (npc != null) {
                walk(2, 0, 1, 0, local_player.waypoint_y[0], 1, 0, npc.waypoint_y[0], local_player.waypoint_x[0], false,
                    npc.waypoint_x[0]);
                crossX = super.click_x;
                crossY = super.click_y;
                crossType = 2;
                crossIndex = 0;
                packetSender.sendAttackNPC((int) local_player_index);
            }
        }

        // Using spells on a player
        if (action == 365) {
            Player player = players[(int) local_player_index];
            if (player != null) {
                walk(2, 0, 1, 0, local_player.waypoint_y[0], 1, 0, player.waypoint_y[0], local_player.waypoint_x[0],
                    false, player.waypoint_x[0]);
                crossX = super.click_x;
                crossY = super.click_y;
                crossType = 2;
                crossIndex = 0;
                // spells on plr
                packetSender.sendUseMagicOnPlayer((int) local_player_index, anInt1137);
            }
        }

        if (action == 729) {
            Player player = players[(int) local_player_index];
            if (player != null) {
                walk(2, 0, 1, 0, local_player.waypoint_y[0], 1, 0, player.waypoint_y[0], local_player.waypoint_x[0],
                    false, player.waypoint_x[0]);
                crossX = super.click_x;
                crossY = super.click_y;
                crossType = 2;
                crossIndex = 0;
                packetSender.sendTradePlayer((int) local_player_index);
            }
        }

        if (action == 577) {
            Player player = players[(int) local_player_index];
            if (player != null) {
                walk(2, 0, 1, 0, local_player.waypoint_y[0], 1, 0, player.waypoint_y[0], local_player.waypoint_x[0],
                    false, player.waypoint_x[0]);
                crossX = super.click_x;
                crossY = super.click_y;
                crossType = 2;
                crossIndex = 0;
                packetSender.sendTradePlayer((int) local_player_index);
            }
        }

        // Using a spell on an item
        if (action == 956 && clickObject(local_player_index, second_menu_action, first_menu_action)) {
            // magic on item
            // sendPacket(new MagicOnItem(first + regionBaseX, anInt1137, button +
            // regionBaseY, clicked >> 14 & 0x7fff));
        }

        // Some walking action (packet 23)
        if (action == 567) {
            boolean flag6 = walk(2, 0, 0, 0, local_player.waypoint_y[0], 0, 0, second_menu_action, local_player.waypoint_x[0], false,
                first_menu_action);
            if (!flag6)
                flag6 = walk(2, 0, 1, 0, local_player.waypoint_y[0], 1, 0, second_menu_action, local_player.waypoint_x[0], false, first_menu_action);
            crossX = super.click_x;
            crossY = super.click_y;
            crossType = 2;
            crossIndex = 0;
            // anti-cheat)
            /*
             * outgoing.writeOpcode(23); outgoing.writeLEShort(button + regionBaseY);
             * outgoing.writeLEShort(clicked); outgoing.writeLEShort(first + regionBaseX);
             */
        }
        if (action == 968) {// custom place holder packet
            packetSender.sendItemContainerOption1(968, first_menu_action, (int) local_player_index);
            item_container_cycle = 0;
            atInventoryInterface = second_menu_action;
            atInventoryIndex = first_menu_action;
            atInventoryInterfaceType = 2;
            if (Widget.cache[second_menu_action].parent == widget_overlay_id)
                atInventoryInterfaceType = 1;
            if (Widget.cache[second_menu_action].parent == backDialogueId)
                atInventoryInterfaceType = 3;
            if (this.isInputFieldInFocus()) {
                this.resetInputFieldFocus();
                inputString = "";
            }
        }

        // Using the bank 10 option on the bank interface
        if (action == 867) {

            if (((int) local_player_index & 3) == 0) {
                anInt1175++;
            }

            if (anInt1175 >= 59) {
                // (anti-cheat)
                // outgoing.writeOpcode(200);
                // outgoing.writeShort(25501);
                anInt1175 = 0;
            }
            packetSender.sendItemContainerOption3(second_menu_action, (int) local_player_index, first_menu_action);
            item_container_cycle = 0;
            atInventoryInterface = second_menu_action;
            atInventoryIndex = first_menu_action;
            atInventoryInterfaceType = 2;
            if (Widget.cache[second_menu_action].parent == widget_overlay_id)
                atInventoryInterfaceType = 1;
            if (Widget.cache[second_menu_action].parent == backDialogueId)
                atInventoryInterfaceType = 3;
        }

        // Using a spell on an inventory item
        if (action == 543) {
            // magic on item
            packetSender.sendUseMagicOnItem(first_menu_action, (int) local_player_index, second_menu_action, anInt1137);
            item_container_cycle = 0;
            atInventoryInterface = second_menu_action;
            atInventoryIndex = first_menu_action;
            atInventoryInterfaceType = 2;
            if (Widget.cache[second_menu_action].parent == widget_overlay_id)
                atInventoryInterfaceType = 1;
            if (Widget.cache[second_menu_action].parent == backDialogueId)
                atInventoryInterfaceType = 3;
        }

        // Clicking report abuse button
        if (action == 606) {
            String s2 = menuActionText[id];
            int j2 = s2.indexOf(">");
            if (j2 != -1)
                if (widget_overlay_id == -1) {
                    clearTopInterfaces();
                    reportAbuseInput = s2.substring(j2 + 5).trim();
                    canMute = false;
                    for (int index = 0; index < Widget.cache.length; index++) {
                        if (Widget.cache[index] == null || Widget.cache[index].contentType != 600)
                            continue;
                        reportAbuseInterfaceID = widget_overlay_id = Widget.cache[index].parent;
                        break;
                    }

                } else {
                    sendMessage("Please close the interface you have open before using this.", 0, "");
                }
        }

        // Using an inventory item on a player
        if (action == 491) {
            Player player = players[(int) local_player_index];

            if (player != null) {
                walk(2, 0, 1, 0, local_player.waypoint_y[0], 1, 0, player.waypoint_y[0], local_player.waypoint_x[0],
                    false, player.waypoint_x[0]);
                crossX = super.click_x;
                crossY = super.click_y;
                crossType = 2;
                crossIndex = 0;
                packetSender.sendUseItemOnPlayer(interfaceitemSelectionTypeIn, (int) local_player_index, useItem,
                    selectedItemIdSlot);
            }
        }

        // reply to private message
        if (action == 639) {
            String text = menuActionText[id];

            int indexOf = text.indexOf(">");

            if (indexOf != -1) {
                indexOf++; // skip ">" at the end
                //System.out.println("Text: " + text);
                //System.out.println("IndexOf " + indexOf);
                //System.out.println("Text substr: " + text.substring(indexOf).trim());
                long usernameHash = StringUtils.encodeBase37(text.substring(indexOf).trim());
                int resultIndex = -1;
                for (int friendIndex = 0; friendIndex < friendsCount; friendIndex++) {
                    if (friendsList[friendIndex].equalsIgnoreCase(text.substring(indexOf).trim())) {
                        resultIndex = friendIndex;
                        break;
                    }
                }

                if (resultIndex != -1 && friendsNodeIDs[resultIndex] > 0) {
                    update_chat_producer = true;
                    inputDialogState = 0;
                    messagePromptRaised = true;
                    promptInput = "";
                    interfaceInputAction = 3;
//                    aLong953 = friendsListAsLongs[resultIndex];
                    selectedSocialListName = friendsList[resultIndex];
                    inputMessage = "Enter a message to send to " + selectedSocialListName;
                }
            }
        }

        // Using the equip option of an item in the inventory
        if (action == 454) {
            // equip item
            packetSender.sendEquipItem((int) local_player_index, first_menu_action, second_menu_action);
            item_container_cycle = 0;
            atInventoryInterface = second_menu_action;
            atInventoryIndex = first_menu_action;
            atInventoryInterfaceType = 2;
            if (Widget.cache[second_menu_action].parent == widget_overlay_id)
                atInventoryInterfaceType = 1;
            if (Widget.cache[second_menu_action].parent == backDialogueId)
                atInventoryInterfaceType = 3;
        }

        // Npc option 4
        if (action == 478) {
            Npc npc = npcs[(int) local_player_index];
            if (npc != null) {
                walk(2, 0, 1, 0, local_player.waypoint_y[0], 1, 0, npc.waypoint_y[0], local_player.waypoint_x[0], false,
                    npc.waypoint_x[0]);
                crossX = super.click_x;
                crossY = super.click_y;
                crossType = 2;
                crossIndex = 0;

                if (((int) local_player_index & 3) == 0) {
                    anInt1155++;
                }

                if (anInt1155 >= 53) {
                    // outgoing.writeOpcode(85);
                    // outgoing.writeByte(66);
                    anInt1155 = 0;
                }

                packetSender.sendNPCOption4((int) local_player_index);
            }
        }

        // Object option 3
        if (action == 113) {
            clickObject(local_player_index, second_menu_action, first_menu_action);
            // object option 3
            packetSender.sendObjectOption3(first_menu_action + next_region_start, second_menu_action + next_region_end, get_object_key(local_player_index));
        }

        // Object option 4
        if (action == 872) {
            clickObject(local_player_index, second_menu_action, first_menu_action);
            packetSender.sendObjectOption4(first_menu_action + next_region_start, get_object_key(local_player_index), second_menu_action + next_region_end);
        }

        // Object option 1
        if (action == 502) {
            clickObject(local_player_index, second_menu_action, first_menu_action);
            packetSender.sendObjectOption1(first_menu_action + next_region_start, get_object_key(local_player_index), second_menu_action + next_region_end);
        }

        if (action == 169) {
            if (isCtrlPressed && setting.moving_prayers) {
                PrayerSystem.InterfaceData grabbed = PrayerSystem.InterfaceData.searchByButton(second_menu_action);
                if (grabbed != null) {
                    prayerGrabbed = grabbed;
                    return;
                }
            }
            packetSender.sendButtonClick(second_menu_action);

            if (second_menu_action != 19158) { // Run button, server handles config
                Widget widget = Widget.cache[second_menu_action];

                if (widget.valueIndexArray != null && widget.valueIndexArray[0][0] == 5) {
                    int setting = widget.valueIndexArray[0][1];
                    settings[setting] = 1 - settings[setting];
                    updateVarp(setting);
                }
            }
        }
        if (action == 447) {
            item_highlighted = 1;
            selectedItemIdSlot = first_menu_action;
            interfaceitemSelectionTypeIn = second_menu_action;
            useItem = (int) local_player_index;
            selectedItemName = ItemDefinition.get((int) local_player_index).name;
            widget_highlighted = 0;
            return;
        }

        if (action == 1226) {
            int objectId = get_object_key(local_player_index);
            ObjectDefinition definition = ObjectDefinition.get(objectId);
            String message;
            if (definition.description != null)
                message = new String(definition.description);
            else
                message = "It's a " + definition.name + ".";
            sendMessage(message, 0, "");
        }

        // Click First Option Ground Item
        if (action == 244) {
            boolean flag7 = walk(2, 0, 0, 0, local_player.waypoint_y[0], 0, 0, second_menu_action, local_player.waypoint_x[0], false,
                first_menu_action);
            if (!flag7)
                flag7 = walk(2, 0, 1, 0, local_player.waypoint_y[0], 1, 0, second_menu_action, local_player.waypoint_x[0], false, first_menu_action);
            crossX = super.click_x;
            crossY = super.click_y;
            crossType = 2;
            crossIndex = 0;
            packetSender.sendGroundItemOption1(second_menu_action + next_region_end, (int) local_player_index, first_menu_action + next_region_start);
        }

        if (action == 1448 || action == 1125) {
            ItemDefinition definition = ItemDefinition.get((int) local_player_index);
            if (definition != null) {
                packetSender.sendExamineItem((int) local_player_index, second_menu_action);
            }
        }

        item_highlighted = 0;
        widget_highlighted = 0;

    }

    private void tradeModified(int container, int slot) {
        if (container != 52015) {
            return;
        }
        Widget tradeWidget = Widget.cache[52017 + slot];
        tradeSlot.add(new TradeOpacity(tradeWidget, slot, 1));
        Widget.cache[52013].drawingDisabled = false;
        packetSender.sendWidgetChange(slot);
        packetSender.sendConfirm(4, 1);
    }

    public void run() {
        if (drawFlames) {
            drawFlames();
        } else {
            super.run();
        }
    }

    private void createMenu() {
        // System.out.println("I am walkable interface id ok: " + openWalkableInterface);
        if (widget_overlay_id == 16244) {
            return;
        }

        if (item_highlighted == 0 && widget_highlighted == 0) {
            menuActionText[menuActionRow] = "Walk here";
            menuActionTypes[menuActionRow] = 519;
            firstMenuAction[menuActionRow] = super.cursor_x;
            secondMenuAction[menuActionRow] = super.cursor_y;
            menuActionRow++;
        }

        // System.out.println("open interface: " + openWalkableInterface);

        long previous = -1L;
        boolean drawInteractedPlayer = false;

        // It loops through all visible models on the screen, from furthest to nearest.
        for (int cached = 0; cached < Model.anInt1687; cached++) {
            long current = Model.anIntArray1688[cached];
            int x = get_object_x(current);
            int y = get_object_y(current);
            int opcode = get_object_opcode(current);
            int uid = get_object_key(current);
            if (current == previous) {
                continue;
            }
            previous = current;
            if (opcode == 2 && scene.get_object(plane, x, y, current)) {
                ObjectDefinition def = ObjectDefinition.get(uid);
                if (def.configs != null)
                    def = def.get_configs();

                if (def == null)
                    continue;
                if (item_highlighted == 1) {
                    menuActionText[menuActionRow] = "Use " + selectedItemName + " with <col=00FFFF>" + def.name;
                    menuActionTypes[menuActionRow] = 62;
                    selectedMenuActions[menuActionRow] = current;
                    firstMenuAction[menuActionRow] = x;
                    secondMenuAction[menuActionRow] = y;
                    menuActionRow++;
                } else if (widget_highlighted == 1) {
                    if ((selectedTargetMask & 4) == 4) {
                        menuActionText[menuActionRow] = selected_target_id + " <col=00FFFF>" + def.name;
                        menuActionTypes[menuActionRow] = 956;
                        selectedMenuActions[menuActionRow] = current;
                        firstMenuAction[menuActionRow] = x;
                        secondMenuAction[menuActionRow] = y;
                        menuActionRow++;
                    }
                } else {
                    if (def.scene_actions != null) {
                        for (int type = 4; type >= 0; type--)
                            if (def.scene_actions[type] != null) {
                                menuActionText[menuActionRow] = def.scene_actions[type] + " <col=00FFFF>" + def.name;
                                if (type == 0)
                                    menuActionTypes[menuActionRow] = 502;
                                if (type == 1)
                                    menuActionTypes[menuActionRow] = 900;
                                if (type == 2)
                                    menuActionTypes[menuActionRow] = 113;
                                if (type == 3)
                                    menuActionTypes[menuActionRow] = 872;
                                if (type == 4)
                                    menuActionTypes[menuActionRow] = 1062;
                                selectedMenuActions[menuActionRow] = current;
                                firstMenuAction[menuActionRow] = x;
                                secondMenuAction[menuActionRow] = y;
                                menuActionRow++;
                            }

                    }
                    menuActionText[menuActionRow] = (myPrivilege >= 2 && myPrivilege <= 4 && ClientConstants.DEBUG_MODE)
                        ? "Examine <col=00FFFF>" + def.name + " <col=65280>(<col=FFFFFF>" + uid
                        + "<col=65280>) (<col=FFFFFF>" + (x + next_region_start) + "," + (y + next_region_end)
                        + "<col=65280>)"
                        : "Examine <col=65535>" + def.name;
                    menuActionTypes[menuActionRow] = 1226;
                    selectedMenuActions[menuActionRow] = current;
                    firstMenuAction[menuActionRow] = x;
                    secondMenuAction[menuActionRow] = y;
                    menuActionRow++;
                }
            }

            //System.out.println("Open interface id(walkable): " + openWalkableInterface);

            if (opcode == 1) {
                Npc npc = npcs[uid];

                try {
                    if (npc.desc.occupied_tiles == 1 && (npc.world_x & 0x7f) == 64 && (npc.world_y & 0x7f) == 64) {
                        for (int j2 = 0; j2 < npcs_in_region; j2++) {
                            Npc npc2 = npcs[local_npcs[j2]];
                            if (npc2 != null && npc2 != npc && npc2.desc.occupied_tiles == 1
                                && npc2.world_x == npc.world_x && npc2.world_y == npc.world_y) {
                                if (npc2.showActions()) {
                                    buildAtNPCMenu(npc2.desc, local_npcs[j2], y, x);
                                }
                            }
                        }
                        for (int l2 = 0; l2 < players_in_region; l2++) {
                            Player player = players[local_players[l2]];
                            if (player != null && player.world_x == npc.world_x && player.world_y == npc.world_y)
                                buildAtPlayerMenu(x, local_players[l2], player, y);
                        }
                    }
                    if (npc.showActions()) {
                        buildAtNPCMenu(npc.desc, uid, y, x);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    addReportToServer(e.getMessage());
                }
            }
            if (opcode == 0) {
                Player playerOnTop = players[uid];// The player ontop.
                Player interactingPlayer = getInteractingWithEntityId() == -1 ? null : players[getInteractingWithEntityId()];
                if ((playerOnTop.world_x & 0x7f) == 64 && (playerOnTop.world_y & 0x7f) == 64) {
                    for (int k2 = 0; k2 < npcs_in_region; k2++) {
                        Npc npc = npcs[local_npcs[k2]];
                        try {
                            if (npc != null && npc.desc.occupied_tiles == 1 && npc.world_x == playerOnTop.world_x && npc.world_y == playerOnTop.world_y) {
                                buildAtNPCMenu(npc.desc, local_npcs[k2], y, x);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    for (int i3 = 0; i3 < players_in_region; i3++) {
                        Player loop = players[local_players[i3]];
                        if (loop != null && loop != playerOnTop && loop.world_x == playerOnTop.world_x && loop.world_y == playerOnTop.world_y) {
                            buildAtPlayerMenu(x, local_players[i3], loop, y);
                        }
                    }

                }
                // These two is called on players.
                if (interactingPlayer != null) {
                    if (!interactingPlayer.username.equals(playerOnTop.username)) {
                        buildAtPlayerMenu(x, uid, playerOnTop, y); // Only called for the person that is ontop of my mouse.
                    } else {
                        drawInteractedPlayer = true;
                    }
                } else {
                    buildAtPlayerMenu(x, uid, playerOnTop, y); // Only called for the person that is ontop of my mouse.
                }
            }
            if (opcode == 3) {
                LinkedList class19 = scene_items[plane][x][y];
                if (class19 != null) {
                    for (Item item = (Item) class19.last(); item != null; item = (Item) class19.previous()) {
                        ItemDefinition itemDef = ItemDefinition.get(item.id);
                        if (item_highlighted == 1) {
                            menuActionText[menuActionRow] = "Use " + selectedItemName + " with <col=FF9040>"
                                + itemDef.name;
                            menuActionTypes[menuActionRow] = 511;
                            selectedMenuActions[menuActionRow] = item.id;
                            firstMenuAction[menuActionRow] = x;
                            secondMenuAction[menuActionRow] = y;
                            menuActionRow++;
                        } else if (widget_highlighted == 1) {
                            if ((selectedTargetMask & 1) == 1) {
                                menuActionText[menuActionRow] = selected_target_id + " <col=FF9040>" + itemDef.name;
                                menuActionTypes[menuActionRow] = 94;
                                selectedMenuActions[menuActionRow] = item.id;
                                firstMenuAction[menuActionRow] = x;
                                secondMenuAction[menuActionRow] = y;
                                menuActionRow++;
                            }
                        } else {
                            for (int j3 = 4; j3 >= 0; j3--)
                                if (itemDef.scene_actions != null && itemDef.scene_actions[j3] != null) {
                                    menuActionText[menuActionRow] = itemDef.scene_actions[j3] + " <col=FF9040>"
                                        + itemDef.name;
                                    if (j3 == 0)
                                        menuActionTypes[menuActionRow] = 652;
                                    if (j3 == 1)
                                        menuActionTypes[menuActionRow] = 567;
                                    if (j3 == 2)
                                        menuActionTypes[menuActionRow] = 234;
                                    if (j3 == 3)
                                        menuActionTypes[menuActionRow] = 244;
                                    if (j3 == 4)
                                        menuActionTypes[menuActionRow] = 213;
                                    selectedMenuActions[menuActionRow] = item.id;
                                    firstMenuAction[menuActionRow] = x;
                                    secondMenuAction[menuActionRow] = y;
                                    menuActionRow++;
                                } else if (j3 == 2) {
                                    menuActionText[menuActionRow] = "Take <col=FF9040>" + itemDef.name;
                                    menuActionTypes[menuActionRow] = 234;
                                    selectedMenuActions[menuActionRow] = item.id;
                                    firstMenuAction[menuActionRow] = x;
                                    secondMenuAction[menuActionRow] = y;
                                    menuActionRow++;
                                }
                        }
                        menuActionText[menuActionRow] = (myPrivilege >= 2 && myPrivilege <= 4 && ClientConstants.DEBUG_MODE)
                            ? "Examine <col=FF9040>" + itemDef.name + " <col=65280> (<col=FFFFFF>" + item.id
                            + "<col=65280>)"
                            : "Examine <col=FF9040>" + itemDef.name;
                        menuActionTypes[menuActionRow] = 1448;
                        selectedMenuActions[menuActionRow] = item.id;
                        firstMenuAction[menuActionRow] = x;
                        secondMenuAction[menuActionRow] = y;
                        menuActionRow++;
                    }
                }
            }
        }

        if (drawInteractedPlayer) {
            Player interactingPlayer = players[getInteractingWithEntityId()];
            if (interactingPlayer != null) {
                buildAtPlayerMenu(0, getInteractingWithEntityId(), interactingPlayer, 0); // Only called for the person that is ontop of my mouse.
            }
        }
    }

    public boolean exitRequested = false;
    public int dropdownInversionFlag;

    float spinSpeed = 1;
    private boolean startSpin = false;

    public void clear() {
        exitRequested = true;
        SignLink.reporterror = false;
        try {
            if (socketStream != null) {
                socketStream.close();
            }
            if (spriteCache != null) {
                spriteCache.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            addReportToServer(e.getMessage());
        }
        socketStream = null;
        stop_midi();
        //if (mouseDetection != null)
        //    mouseDetection.running = false;
        //mouseDetection = null;
        if (resourceProvider != null)
            resourceProvider.disable();
        resourceProvider = null;
        outBuffer = null;
        chatBuffer = null;
        loginBuffer = null;
        packetSender = null;
        incoming = null;
        mapCoordinates = null;
        terrainData = null;
        objectData = null;
        terrainIndices = null;
        objectIndices = null;
        tileHeights = null;
        tileFlags = null;
        scene = null;
        collisionMaps = null;
        waypoints = null;
        travel_distances = null;
        walking_queue_x = null;
        walking_queue_y = null;
        tmpTexture = null;
        tabImageProducer = null;
        leftFrame = null;
        topFrame = null;
        minimapImageProducer = null;
        gameScreenImageProducer = null;
        chatboxImageProducer = null;
        chatSettingImageProducer = null;
        /* Null pointers for custom sprites */
        backgroundFix = null;
        mapBack = null;
        sideIcons = null;
        compass = null;
        hitMarks = null;
        headIcons = null;
        skullIcons = null;
        headIconsHint = null;
        autoBackgroundSprites = null;
        crosses = null;
        mapDotItem = null;
        mapDotNPC = null;
        mapDotPlayer = null;
        mapDotFriend = null;
        mapDotTeam = null;
        mapScenes = null;
        mapFunctions = null;
        tile_cycle_map = null;
        players = null;
        local_players = null;
        mobsAwaitingUpdate = null;
        playerSynchronizationBuffers = null;
        removedMobs = null;
        npcs = null;
        local_npcs = null;
        scene_items = null;
        spawns = null;
        projectiles = null;
        incompleteAnimables = null;
        firstMenuAction = null;
        secondMenuAction = null;
        menuActionTypes = null;
        selectedMenuActions = null;
        menuActionText = null;
        settings = null;
        minimapHintX = null;
        minimapHintY = null;
        minimapHint = null;
        minimapImage = null;
        friendsList = null;
        ignoreList = null;
        friendsListAsLongs = null;
        friendsNodeIDs = null;
        flameLeftBackground = null;
        flameRightBackground = null;
        topLeft1BackgroundTile = null;
        bottomLeft1BackgroundTile = null;
        loginBoxImageProducer = null;
        loginScreenAccessories = null;
        bottomLeft0BackgroundTile = null;
        bottomRightImageProducer = null;
        loginMusicImageProducer = null;
        middleLeft1BackgroundTile = null;
        aRSImageProducer_1115 = null;
        multiOverlay = null;
        nullLoader();
        ObjectDefinition.release();
        NpcDefinition.clear();
        ItemDefinition.release();
        FloDefinition.underlay = null;
        FloDefinition.overlay = null;
        IdentityKit.cache = null;
        Widget.cache = null;
        Sequence.cache = null;
        SpotAnimation.cache = null;
        SpotAnimation.model_cache = null;
        VariableParameter.values = null;
        super.fullGameScreen = null;
        Player.model_cache = null;
        Rasterizer3D.clear();
        SceneGraph.release();
        Model.clear();
        Animation.release();
        System.gc();
    }

    Component getGameComponent() {
        // Commented out for Java9+ compatibility, we no longer use Applets.
        // if (SignLink.mainapp != null)
        // return SignLink.mainapp;
        if (super.gameFrame != null)
            return super.gameFrame;
        else
            return this;
    }

    public boolean tradingPostOpen() {
        return widget_overlay_id == 66_000;
    }

    final List<Integer> OPTION_DIALOGUE_IDS = Arrays.asList(2459, 2469, 2480, 2492);

    public int texA, texB, texC;
    public boolean debugTextures;

    private void manageTextInputs() {
        do {
            int key = readChar(-796);

            if (key == -1)
                break;

            if (ClientConstants.SPAWN_TAB_DISPLAY_ALL_ITEMS_PRELOADED) {
                if (SpawnTabAllItems.searchingSpawnTab && sidebarId == 13 && inputDialogState != 1) {
                    if (key == 8 && SpawnTabAllItems.searchSyntax.length() > 0) {
                        SpawnTabAllItems.searchSyntax = SpawnTabAllItems.searchSyntax.substring(0, SpawnTabAllItems.searchSyntax.length() - 1);
                    }
                    if (key >= 32 && key <= 122 && SpawnTabAllItems.searchSyntax.length() < 15) {
                        SpawnTabAllItems.searchSyntax += (char) key;
                    }
                    SpawnTabAllItems.fetchSearchResults = true;
                    return;
                }
            } else {
                if (SpawnTab.searchingSpawnTab && sidebarId == 13 && inputDialogState != 1) {
                    if (key == 8 && SpawnTab.searchSyntax.length() > 0) {
                        SpawnTab.searchSyntax = SpawnTab.searchSyntax.substring(0, SpawnTab.searchSyntax.length() - 1);
                    }
                    if (key >= 32 && key <= 122 && SpawnTab.searchSyntax.length() < 15) {
                        SpawnTab.searchSyntax += (char) key;
                    }
                    SpawnTab.fetchSearchResults = true;
                    return;
                }
            }

            /**
             * @author Suic Continue a dialogue with space bar (spacebar)
             */
            if (key == 32 && !OPTION_DIALOGUE_IDS.contains(backDialogueId)) { // p much assures that it only sends data
                // when needed
                //System.out.println("Skipped dialogue");
                packetSender.sendNextDialogue(4899);
                continuedDialogue = true;
            }

            /**
             * Author @Suic
             */

            // all the checks assure that it only sends the 3rd option for three option
            // dialogue(for example) if a 3 option dialogue is open
            // and if a 2 option dialogue is open, and "1" is pressed it only sends a click
            // for that, not all of em. (eg a 3 option dialogue) since they all have the
            // first option
            // p much what i mean is that, it only sends data to the server for the click
            // when necessary, and only the data thats needed.
            // 1234 1 2 3 4
            if (key == 49) {

                switch (backDialogueId) {
                    case 2459:
                        packetSender.sendButtonClick(2461);
                        break;
                    case 2469:
                        packetSender.sendButtonClick(2471);
                        break;
                    case 2480:
                        packetSender.sendButtonClick(2482);
                        break;
                    case 2492:
                        packetSender.sendButtonClick(2494);
                        break;
                }

            } else if (key == 50) {
                switch (backDialogueId) {
                    case 2459:
                        packetSender.sendButtonClick(2462);
                        break;
                    case 2469:
                        packetSender.sendButtonClick(2472);
                        break;
                    case 2480:
                        packetSender.sendButtonClick(2483);
                        break;
                    case 2492:
                        packetSender.sendButtonClick(2495);
                        break;
                }
            } else if (key == 51) {
                switch (backDialogueId) {
                    case 2459:
                        packetSender.sendButtonClick(2464);
                        break;
                    case 2469:
                        packetSender.sendButtonClick(2473);
                        break;
                    case 2480:
                        packetSender.sendButtonClick(2484);
                        break;
                    case 2492:
                        packetSender.sendButtonClick(2496);
                        break;
                }
            } else if (key == 52) {
                switch (backDialogueId) {
                    case 2480:
                        packetSender.sendButtonClick(2485);
                        break;
                    case 2492:
                        packetSender.sendButtonClick(2497);
                        break;
                }
            } else if (key == 53 && backDialogueId != 4882) {
                if (backDialogueId == 2492) {
                    packetSender.sendButtonClick(2498);
                }
                continuedDialogue = true;
            }

            if (widget_overlay_id != -1 && widget_overlay_id == reportAbuseInterfaceID) {
                if (key == 8 && reportAbuseInput.length() > 0)
                    reportAbuseInput = reportAbuseInput.substring(0, reportAbuseInput.length() - 1);
                if ((key >= 97 && key <= 122 || key >= 65 && key <= 90 || key >= 48 && key <= 57 || key == 32)
                    && reportAbuseInput.length() < 12)
                    reportAbuseInput += (char) key;
            } else if (messagePromptRaised) {
                if (key >= 32 && key <= 122 && promptInput.length() < 80) {
                    promptInput += (char) key;
                    update_chat_producer = true;
                }
                if (key == 8 && promptInput.length() > 0) {
                    promptInput = promptInput.substring(0, promptInput.length() - 1);
                    update_chat_producer = true;
                }
                if (key == 9) {
                    tabToReplyPm();
                } else if (key == 13 || key == 10) {
                    messagePromptRaised = false;
                    update_chat_producer = true;
                    privateChatUserListPtr = 0;
                    if (searchingBank) {
                        Widget.cache[26102].active = false;
                        searchingBank = false;
                        promptInput = "";
                    } else {
                        String enteredName = promptInput;

                        if (interfaceInputAction != 3 && !StringUtils.VALID_NAME.matcher(enteredName).matches())
                            return;
                        enteredName = StringUtils.capitalizeIf(enteredName);
                        if (interfaceInputAction == 1) {
                            addFriend(enteredName);
                        }
                        if (interfaceInputAction == 2 && friendsCount > 0) {
                            removeFriend(enteredName);
                        }

                        if (interfaceInputAction == 3 && promptInput.length() > 0) {
                            // private message
                            packetSender.sendPrivateMessage(selectedSocialListName, promptInput);
                            promptInput = ChatMessageCodec.processText(promptInput);
                            privateChatUserListPtr = 0;

                            sendMessage(enteredName, 6, selectedSocialListName);
                            if (privateChatMode == 2) {
                                privateChatMode = 1;
                                // privacy option
                                packetSender.sendChatConfigurations(set_public_channel, privateChatMode, tradeMode, clanChatMode);
                            }
                        }
                        if (interfaceInputAction == 4 && ignoreCount < 100) {
                            addIgnore(enteredName);
                        }
                        if (interfaceInputAction == 5 && ignoreCount > 0) {
                            removeIgnore(enteredName);
                        }
                    }
                }
            } else if (inputDialogState == 3 || inputDialogState == 4) { // Only Allow Numbers
                if (key >= 48 && key <= 57 && amountOrNameInput.length() < 2) {
                    amountOrNameInput += (char) key;
                    update_chat_producer = true;
                }

                if (key == 8 && amountOrNameInput.length() > 0) {
                    amountOrNameInput = amountOrNameInput.substring(0, amountOrNameInput.length() - 1);
                    update_chat_producer = true;
                }

                if (key == 13 || key == 10) {
                    if (amountOrNameInput.length() > 0) {
                        int amount = 0;

                        try {
                            amount = Integer.parseInt(amountOrNameInput);

                            // overflow concious code
                            if (amount < 0) {
                                amount = 0;
                            } else if (amount > Integer.MAX_VALUE) {
                                amount = Integer.MAX_VALUE;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            addReportToServer(e.getMessage());
                        }

                        if (amount > 0) {
                            packetSender.sendEnteredAmount((int) amount, (byte) inputDialogState);
                        }

                        if (inputDialogState == 3) {
                            if (amount >= 0 && amount < 51) {
                                setting.drag_item_value = (int) amount;
                                Widget.cache[12697].defaultText = "Drag setting: <col=ffffff>" + amount + (amount == 5 ? " (OSRS)" : amount == 10 ? " (Pre-EOC)" : " (Custom)");
                            } else {
                                sendMessage("Please only enter a number between 0 - 50!", 0, amountOrNameInput);
                            }
                        }

                        if (inputDialogState == 4) {
                            if (amount >= 4 && amount < 13) {
                                packetSender.sendEnteredAmount((int) amount, (byte) inputDialogState);
                            } else {
                                sendMessage("Please only enter a number between 4 - 12!", 0, amountOrNameInput);
                            }
                        }
                    }

                    inputDialogState = 0;
                    update_chat_producer = true;
                }
            } else if (inputDialogState == 1) {
                if (amountOrNameInput.length() < 10) {
                    String test = amountOrNameInput + (char) key;
                    boolean valid = test.matches("[0-9]+[kmbKMB]");
                    boolean noLettersBefore = test.matches("(?<![A-Za-z0-9.])[0-9.]+");
                    if (valid || noLettersBefore) {
                        amountOrNameInput += (char) key;
                        update_chat_producer = true;
                    }
                }
                if (key == 8 && amountOrNameInput.length() > 0) {
                    amountOrNameInput = amountOrNameInput.substring(0, amountOrNameInput.length() - 1);
                    update_chat_producer = true;
                }
                if (key == 13 || key == 10) {
                    if (amountOrNameInput.length() > 0) {
                        //System.out.println("enter "+widget_overlay_id);
                        int length = amountOrNameInput.length();
                        char lastChar = amountOrNameInput.charAt(length - 1);

                        if (lastChar == 'k' || lastChar == 'K') {
                            amountOrNameInput = amountOrNameInput.substring(0, length - 1) + "000";
                        } else if (lastChar == 'm' || lastChar == 'M') {
                            amountOrNameInput = amountOrNameInput.substring(0, length - 1) + "000000";
                        } else if (lastChar == 'b' || lastChar == 'B') {
                            amountOrNameInput = amountOrNameInput.substring(0, length - 1) + "000000000";
                        }

                        long amount = 0;
                        boolean insideTp = tradingPostOpen();
                        long maxAmt = insideTp ? Long.MAX_VALUE : Integer.MAX_VALUE;

                        try {
                            amount = Long.parseLong(amountOrNameInput);

                            // overflow concious code
                            if (amount < 0) {
                                amount = 0;
                            } else if (amount > maxAmt) {
                                amount = maxAmt;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            addReportToServer(e.getMessage());
                        }

                        if (amount > 0) {
                            packetSender.sendEnteredAmount(insideTp ? amount : (int) amount, (byte) -1);
                            if (widget_overlay_id == 26000) {
                                modifiableXValue = (int) amount;
                            }
                        }
                    }
                    inputDialogState = 0;
                    update_chat_producer = true;
                }
            } else if (inputDialogState == 2) {
                //System.out.println("Hello lol");
                int limit = 100;
                if (key >= 32 && key <= 122 && amountOrNameInput.length() < limit) {
                    amountOrNameInput += (char) key;
                    update_chat_producer = true;
                }
                if (key == 8 && amountOrNameInput.length() > 0) {
                    amountOrNameInput = amountOrNameInput.substring(0, amountOrNameInput.length() - 1);
                    update_chat_producer = true;
                }
                if (key == 13 || key == 10) {
                    if (amountOrNameInput.length() > 0) {
                        packetSender.sendEnteredSyntax(amountOrNameInput);
                        inputDialogState = 0;
                        update_chat_producer = true;
                    }
                }
                //System.out.println("KEY: " + key + ", inputString: " + amountOrNameInput);
            } else if (backDialogueId == -1) {
                if (this.isInputFieldInFocus()) {
                    Widget rsi = this.getInputFieldFocusOwner();
                    if (rsi == null) {
                        return;
                    }

                    if (key >= 32 && key <= 122 && rsi.defaultText.length() < rsi.characterLimit) {
                        if (rsi.inputRegex.length() > 0) {
                            Pattern regex = Pattern.compile(rsi.inputRegex);
                            Matcher match = regex.matcher(Character.toString(((char) key)));
                            if (match.matches()) {
                                rsi.defaultText += (char) key;
                                update_chat_producer = true;
                            }
                        } else {
                            rsi.defaultText += (char) key;
                            update_chat_producer = true;
                        }
                    }
                    if (key == 8 && rsi.defaultText.length() > 0) {
                        rsi.defaultText = rsi.defaultText.substring(0, rsi.defaultText.length() - 1);
                        update_chat_producer = true;
                    }
                    if (rsi.updatesEveryInput && rsi.defaultText.length() > 0 && key != 10 && key != 13) {
                        packetSender.inputField(4 + rsi.defaultText.length() + 1, rsi.id, rsi.defaultText);
                        inputString = "";
                        promptInput = "";
                        break;
                    } else if ((key == 10 || key == 13) && !rsi.updatesEveryInput) {
                        packetSender.inputField(4 + rsi.defaultText.length() + 1, rsi.id, rsi.defaultText);
                        inputString = "";
                        promptInput = "";
                        break;
                    }
                }
                if (key >= 32 && key <= 122 && inputString.length() < 80) {
                    inputString += (char) key;
                    update_chat_producer = true;
                }
                if (key == 8 && inputString.length() > 0) {
                    inputString = inputString.substring(0, inputString.length() - 1);
                    update_chat_producer = true;
                }

                //System.out.println("key is " + key);

                if (key == 9) {
                    tabToReplyPm();
                }

                // Remove the ability for players to do crowns..
                if (inputString.contains("<img=")) {
                    // System.err.println("Removed crown");
                    inputString = inputString.replaceAll("<img=", "");
                }
                // Remove the ability for players to do double color messages..
                if (inputString.contains("@@")) {
                    inputString = inputString.replaceAll("@@", "");
                }
                // Remove the ability for players to do colour messages..
                if (inputString.contains("<col")) {
                    inputString = inputString.replaceAll("<col", "");
                }
                // Remove the ability for players to do transparent messages..
                if (inputString.contains("<trans")) {
                    inputString = inputString.replaceAll("<trans", "");
                }
                // Remove the ability for players to do shaded messages..
                if (inputString.contains("<shad")) {
                    inputString = inputString.replaceAll("<shad", "");
                }
                // Remove the ability for players to do global messages..
                if (inputString.contains("[Global]")) {
                    inputString = inputString.replaceAll("Global", "");
                }
                // Remove the ability for players to do global messages..
                if (inputString.contains("[global]")) {
                    inputString = inputString.replaceAll("global", "");
                }
                if ((key == 13 || key == 10) && inputString.length() > 0) {
                    boolean isDeveloper = (myPrivilege == 4 || myPrivilege == 3 || myPrivilege == 2);
                    if (inputString.equals("::fps")) {
                        setting.draw_fps = !setting.draw_fps;
                    }
                    /* Client debug commands not actually used in the game */
                    if (isDeveloper) {

                        if (inputString.equals("::lagtest")) {
                            addReportToServer("testy1!");
                            addReportToServer("testy2!");
                            addReportToServer("testy3!");
                        }
                        if (inputString.equals("::dc")) {
                            dropClient();
                        }
                        if (inputString.equals("::lagtest2")) {
                            String[] data = new String[]{"" + timeoutCounter, "" + opcode, "" + lastOpcode, "" + secondLastOpcode, "" + thirdLastOpcode};
                            addReportToServer(Arrays.toString(data));
                        }

                        if (inputString.startsWith("::debugt")) {
                            debugTextures = !debugTextures;
                        }

                        if (debugTextures) {
                            if (inputString.startsWith("::abc")) {
                                int a = Integer.parseInt(inputString.split(" ")[1]);
                                int b = Integer.parseInt(inputString.split(" ")[2]);
                                int c = Integer.parseInt(inputString.split(" ")[3]);
                                texA = a;
                                texB = b;
                                texC = c;
                                sendMessage("New mapping = @red@" + texA + " | " + texB + " | " + texC, 0, "");
                            }

                            if (inputString.startsWith("::a")) {
                                texA = Integer.parseInt(inputString.split(" ")[1]);
                                sendMessage("New mapping for a = @red@" + texA, 0, "");
                            }

                            if (inputString.startsWith("::b")) {
                                texB = Integer.parseInt(inputString.split(" ")[1]);
                                sendMessage("New mapping for b = @red@" + texB, 0, "");
                            }

                            if (inputString.startsWith("::c")) {
                                texC = Integer.parseInt(inputString.split(" ")[1]);
                                sendMessage("New mapping for c = @red@" + texC, 0, "");
                            }
                        }

                        if (inputString.startsWith("::colors")) {
                            final int id = Integer.parseInt(inputString.split(" ")[1]);
                            final Model model = Model.get(id);
                            final ArrayList<Short> colors = new ArrayList<Short>();
                            if (model != null) {
                                for (int i = 0; i < model.faces; ++i) {
                                    if (!colors.contains(model.face_color[i])) {
                                        colors.add((short) model.face_color[i]);
                                    }
                                }
                                for (int m = 0; m < colors.size(); ++m) {
                                    sendMessage("[" + m + "] = " + colors.get(m), 0, "");
                                    System.out.print(colors.get(m) + ", ");
                                }
                            } else {
                                sendMessage("Can't find model!", 0, "");
                            }
                        }
                        if (inputString.startsWith("::getcolors")) {
                            try {
                                //int id = Integer.parseInt(inputString.split(" ")[1]);
                                for (int id = 1; id < ItemDefinition.length; id++) {
                                    ItemDefinition def = ItemDefinition.get(id);
                                    if (def == null) continue;
                                    //some checks
                                    Model model;
                                    if (def.male_equip_main > 0) {
                                        model = def.get_equipped_model(0);
                                    } else {
                                        model = def.get_widget_model(0);
                                    }
                                    if (model != null) {
                                        //System.out.println("model is not null!");
                                        Set<Short> colors = new HashSet<>();
                                        for (int i = 0; i < model.face_color.length; i++) {
                                            colors.add((short) model.face_color[i]);
                                        }
                                        writeColors(id, colors);
                                        System.out.println("Colors for id " + id + " : " + colors);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                addReportToServer(e.getMessage());
                            }

                        }
                        if (inputString.startsWith("::getinvmodel")) {
                            try {



                                String[] data = inputString.split(" ");

                                int itemid = Integer.parseInt(data[1]);//639
                                ItemDefinition def = ItemDefinition.get(itemid);


                                sendMessage("model of item id "+itemid+" is: "+def.inventory_model, 0,"");



                            } catch (Exception e) {
                                //  pushMessage("Error", 0, "");
                            }
                        }
                        if (inputString.startsWith("::getwearmodel")) {
                            try {



                                String[] data = inputString.split(" ");

                                int itemid = Integer.parseInt(data[1]);//639
                                ItemDefinition def = ItemDefinition.get(itemid);


                                sendMessage("model of item id "+itemid+" is: "+def.male_equip_main, 0,"");



                            } catch (Exception e) {
                                //  pushMessage("Error", 0, "");
                            }
                        }
                        if (inputString.startsWith("::changew")) {
                            try {



                                String[] data = inputString.split(" ");

                                int mainid = Integer.parseInt(data[1]);//639

                                int width = Integer.parseInt(data[2]);


                                Widget rsi = Widget.cache[mainid];

                              sendMessage("Changing  width of "+mainid+" to "+width, 0,"");

                                rsi.width = width;


                            } catch (Exception e) {
                              //  pushMessage("Error", 0, "");
                            }
                        }
                        if (inputString.startsWith("::changeh")) {
                            try {



                                String[] data = inputString.split(" ");

                                int mainid = Integer.parseInt(data[1]);//639

                                int height = Integer.parseInt(data[2]);


                                Widget rsi = Widget.cache[mainid];

                            sendMessage("Changing height of "+mainid+" to "+height, 0,"");

                                rsi.height = height;


                            } catch (Exception e) {
                              //  pushMessage("Error", 0, "");
                            }
                        }

                        if (inputString.startsWith("::moveint")) {
                            try {

                                String[] data = inputString.split(" ");

                                int mainid = Integer.parseInt(data[1]);
                                int childid = Integer.parseInt(data[2]);
                                int xid = Integer.parseInt(data[3]);
                                int yid = Integer.parseInt(data[4]);


                                Widget rsi = Widget.cache[mainid];

                              sendMessage("Moving interface " + childid + " to  x: "+xid+" and y: "+yid+".", 0, "");

                                rsi.child_x[childid] = xid;
                                rsi.child_y[childid] = yid;

                            } catch (Exception e) {
                               // pushMessage("Error", 0, "");
                            }
                        }


                        // System.out.println(inputString);
                        if (inputString.toLowerCase().equalsIgnoreCase("::resetpm")) {
                            resetSplitPrivateChatMessages();
                            //System.out.println("Reset");
                        }

                        if (inputString.startsWith("::getcolor") && !inputString.equals("::getcolors")) {
                            int id = Integer.parseInt(inputString.split(" ")[1]);
                            ItemDefinition def = ItemDefinition.get(id);
                            //some checks
                            Model model;
                            if (def.male_equip_main > 0) {
                                model = def.get_equipped_model(0);
                            } else {
                                model = def.get_widget_model(0);
                            }
                            if (model != null) {
                                //System.out.println("model is not null!");
                                Set<Short> colors = new HashSet<>();
                                for (int i = 0; i < model.face_color.length; i++) {
                                    colors.add((short) model.face_color[i]);
                                }
                                System.out.println("Colors: " + colors);
                            }
                        }
                        if (inputString.toLowerCase().equalsIgnoreCase("::ri")
                            || inputString.toLowerCase().equalsIgnoreCase("::reloadinterfaces")) {
                            Archive interfaces = request_archive(3, "interface", "interface", 35);
                            Archive graphics = request_archive(4, "2d graphics", "media", 40);
                            AdvancedFont fonts[] = {adv_font_small, adv_font_regular, adv_font_bold, adv_font_fancy};
                            Widget.load(interfaces, fonts, graphics);
                        }
                        if (inputString.toLowerCase().equals("::debugobj")) {
                            int objectToDebug = 34895;
                            ObjectDefinition data = ObjectDefinition.get(objectToDebug);
                            for (int i = 0; i < data.model_ids.length; i++) {
                                System.out.println("objectDef.modelIds[" + i + "] = " + data.model_ids[i] + ";");
                            }
                        }
                        if (inputString.toLowerCase().equals("::chatcomp")) {
                            if (screen == ScreenMode.RESIZABLE) {
                                showChatComponents = !showChatComponents;
                                sendMessage("showing chat components is now "
                                    + (showChatComponents ? "enabled" : "disabled") + ".", 0, "");
                            }
                        }
                        if (inputString.toLowerCase().equals("::showtab")) {
                            if (screen == ScreenMode.RESIZABLE) {
                                showTabComponents = !showTabComponents;
                                sendMessage("showing tab components is now "
                                    + (showTabComponents ? "enabled" : "disabled") + ".", 0, "");
                            }
                        }
                        if (inputString.toLowerCase().equals("::fixed")) {
                            frameMode(ScreenMode.FIXED);
                        }
                        if (inputString.toLowerCase().equals("::resize")) {
                            frameMode(ScreenMode.RESIZABLE);
                        }
                        if (inputString.toLowerCase().equals("::sendtestpacket")) {
                            packetSender.sendTestPacket(0);
                        }
                        if (inputString.toLowerCase().equals("::renderdistance")) {
                            sendMessage("Render distance is: " + SceneGraph.render_distance, 0, "");
                        }
                    }
                    if (inputString.toLowerCase().equals("::lastpacketsread")) {
                        Client.debug_packet_info = !Client.debug_packet_info;
                    }
                    if (inputString.toLowerCase().equals("::debug")) {

                        Client.dump_requested = !Client.dump_requested;
                    }
                    if (inputString.toLowerCase().equals("::data")) {
                        ClientConstants.CLIENT_DATA = !ClientConstants.CLIENT_DATA;
                    }
                    if (inputString.toLowerCase().equals("::data1")) {
                        ClientConstants.FORCE_OVERLAY_ABOVE_WIDGETS = !ClientConstants.FORCE_OVERLAY_ABOVE_WIDGETS;
                    }
                    if (inputString.toLowerCase().equals("::dumpitemsprites")) {
                        //We need to run this dumpitemsprites command twice, likely to get the images into the cache.
                        System.out.println("Dumping item images.");
                        CacheUtils.dumpItemImages(0, ItemDefinition.length - 1, 128, 128);
                        //CacheUtils.dumpItemImages( 0, ItemDefinition.length-1);
                        System.out.println("Dumped item images.");
                    }
                    if (inputString.startsWith("/")) {
                        inputString = "::" + inputString;
                    }

                    if (inputString.startsWith("::")) {
                        packetSender.sendCommand(inputString.substring(2));
                    } else {
                        String text = inputString.toLowerCase();
                        int colorCode = 0;
                        if (text.startsWith("yellow:")) {
                            colorCode = 0;
                            inputString = inputString.substring(7);
                        } else if (text.startsWith("red:")) {
                            colorCode = 1;
                            inputString = inputString.substring(4);
                        } else if (text.startsWith("green:")) {
                            colorCode = 2;
                            inputString = inputString.substring(6);
                        } else if (text.startsWith("cyan:")) {
                            colorCode = 3;
                            inputString = inputString.substring(5);
                        } else if (text.startsWith("purple:")) {
                            colorCode = 4;
                            inputString = inputString.substring(7);
                        } else if (text.startsWith("white:")) {
                            colorCode = 5;
                            inputString = inputString.substring(6);
                        } else if (text.startsWith("flash1:")) {
                            colorCode = 6;
                            inputString = inputString.substring(7);
                        } else if (text.startsWith("flash2:")) {
                            colorCode = 7;
                            inputString = inputString.substring(7);
                        } else if (text.startsWith("flash3:")) {
                            colorCode = 8;
                            inputString = inputString.substring(7);
                        } else if (text.startsWith("glow1:")) {
                            colorCode = 9;
                            inputString = inputString.substring(6);
                        } else if (text.startsWith("glow2:")) {
                            colorCode = 10;
                            inputString = inputString.substring(6);
                        } else if (text.startsWith("glow3:")) {
                            colorCode = 11;
                            inputString = inputString.substring(6);
                        }
                        text = inputString.toLowerCase();
                        int effectCode = 0;
                        if (text.startsWith("wave:")) {
                            effectCode = 1;
                            inputString = inputString.substring(5);
                        } else if (text.startsWith("wave2:")) {
                            effectCode = 2;
                            inputString = inputString.substring(6);
                        } else if (text.startsWith("shake:")) {
                            effectCode = 3;
                            inputString = inputString.substring(6);
                        } else if (text.startsWith("scroll:")) {
                            effectCode = 4;
                            inputString = inputString.substring(7);
                        } else if (text.startsWith("slide:")) {
                            effectCode = 5;
                            inputString = inputString.substring(6);
                        }

                        if (chatDelay.elapsed() < 599) {
                            return;
                        }

                        // chat
                        packetSender.sendChatMessage(colorCode, effectCode, inputString);
                        inputString = ChatMessageCodec.processText(inputString);

                        local_player.entity_message = inputString;
                        local_player.textColour = colorCode;
                        local_player.textEffect = effectCode;
                        // System.out.println("colorCode: "+colorCode+" vs effectCode: "+effectCode);
                        local_player.message_cycle = 150;

                        List<ChatCrown> crowns = ChatCrown.get(myPrivilege, donatorPrivilege);
                        String crownPrefix = "";
                        for (ChatCrown c : crowns) {
                            crownPrefix += c.getIdentifier();
                        }

                        sendMessage(local_player.entity_message, 2, crownPrefix + local_player.username, local_player.getTitle(false));

                        if (set_public_channel == 2) {
                            set_public_channel = 3;
                            // privacy option
                            packetSender.sendChatConfigurations(set_public_channel, privateChatMode, tradeMode, clanChatMode);
                        }

                        chatDelay.reset();
                    }
                    inputString = "";
                    update_chat_producer = true;
                }
            }
        } while (true);
    }

    final Path colorPath = Paths.get("./colordump.txt");

    private void writeColors(int id, Set<Short> colors) {
        int[] colorArray = colors.stream().mapToInt(Short::shortValue).toArray();
        try {
            String str = "Id = " + id + " | Colors = " + Arrays.toString(colorArray) + System.lineSeparator() + "-----------" + System.lineSeparator();
            Files.write(colorPath, str.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
            addReportToServer(e.getMessage());
        }
    }

    private void updateInputField(final Widget rsi) {

        switch (rsi.id) {

            default:
                packetSender.inputField(4 + rsi.defaultText.length() + 1, rsi.id, rsi.defaultText);
                inputString = "";
                promptInput = "";
                break;

        }
    }

    private void buildPublicChat(int chatIndex) {
        int index = 0;
        for (int message = 0; message < 500; message++) {

            if (chatMessages[message] == null) {
                continue;
            }

            if (chatTypeView != 1) {
                continue;
            }

            int chatType = chatMessages[message].getType();
            String name = chatMessages[message].getName();
            int scrollAmount = (70 - index * 14 + 42) + chatScrollAmount + 4 + 5;
            if (scrollAmount < -23)
                break;
            if ((chatType == 1 || chatType == 2)
                && (chatType == 1 || set_public_channel == 0 || set_public_channel == 1 && !check_username(name))) {
                if (chatIndex > scrollAmount - 14 && chatIndex <= scrollAmount && !name.equalsIgnoreCase(local_player.username)) {
                    if (myPrivilege >= 1) {
                        menuActionText[menuActionRow] = "Report abuse <col=FFFFFF>" + name;
                        menuActionTypes[menuActionRow] = 606;
                        menuActionRow++;
                    }
                    menuActionText[menuActionRow] = "Add ignore <col=FFFFFF>" + name;
                    menuActionTypes[menuActionRow] = 42;
                    menuActionRow++;
                    menuActionText[menuActionRow] = "Add friend <col=FFFFFF>" + name;
                    menuActionTypes[menuActionRow] = 337;
                    menuActionRow++;
                    menuActionText[menuActionRow] = "Reply to <col=FFFFFF>" + name;
                    menuActionTypes[menuActionRow] = 338;
                    menuActionRow++;
                }
                index++;
            }
        }
    }

    private void buildFriendChat(int chatIndex) {
        if (chatTypeView != 2) {
            return;
        }
        int index = 0;
        for (int i1 = 0; i1 < 500; i1++) {
            if (chatMessages[i1] == null)
                continue;
            if (chatTypeView != 2)
                continue;
            int chatType = chatMessages[i1].getType();
            String name = chatMessages[i1].getName();
            int scrollAmount = (70 - index * 14 + 42) + chatScrollAmount + 4 + 5;
            if (scrollAmount < -23)
                break;
            if ((chatType == 5 || chatType == 6) && (splitPrivateChat == 0 || chatTypeView == 2)
                && (chatType == 6 || privateChatMode == 0 || privateChatMode == 1 && check_username(name)))
                index++;
            if ((chatType == 3 || chatType == 7) && (splitPrivateChat == 0 || chatTypeView == 2)
                && (chatType == 7 || privateChatMode == 0 || privateChatMode == 1 && check_username(name))) {
                if (chatIndex > scrollAmount - 14 && chatIndex <= scrollAmount) {
                    if (myPrivilege >= 1) {
                        menuActionText[menuActionRow] = "Report abuse <col=FFFFFF>" + name;
                        menuActionTypes[menuActionRow] = 606;
                        menuActionRow++;
                    }
                    menuActionText[menuActionRow] = "Add ignore <col=FFFFFF>" + name;
                    menuActionTypes[menuActionRow] = 42;
                    menuActionRow++;
                    menuActionText[menuActionRow] = "Add friend <col=FFFFFF>" + name;
                    menuActionTypes[menuActionRow] = 337;
                    menuActionRow++;
                    menuActionText[menuActionRow] = "Reply to <col=FFFFFF>" + name;
                    menuActionTypes[menuActionRow] = 338;
                    menuActionRow++;
                }
                index++;
            }
        }
    }

    private void buildDuelorTrade(int chatIndex) {
        int index = 0;
        for (int i1 = 0; i1 < 500; i1++) {
            if (chatMessages[i1] == null)
                continue;
            if (chatTypeView != 3 && chatTypeView != 4)
                continue;
            int chatType = chatMessages[i1].getType();
            String name = chatMessages[i1].getName();
            int scrollAmount = (70 - index * 14 + 42) + chatScrollAmount + 4 + 5;
            if (scrollAmount < -23)
                break;
            if (chatTypeView == 3 && chatType == 4 && (tradeMode == 0 || tradeMode == 1 && check_username(name))) {
                if (chatIndex > scrollAmount - 14 && chatIndex <= scrollAmount) {
                    menuActionText[menuActionRow] = "Accept trade <col=FFFFFF>" + name;
                    menuActionTypes[menuActionRow] = 484;
                    menuActionRow++;
                }
                index++;
            }
            if (chatTypeView == 4 && chatType == 8 && (tradeMode == 0 || tradeMode == 1 && check_username(name))) {
                if (chatIndex > scrollAmount - 14 && chatIndex <= scrollAmount) {
                    menuActionText[menuActionRow] = "Accept challenge <col=FFFFFF>" + name;
                    menuActionTypes[menuActionRow] = 6;
                    menuActionRow++;
                }
                index++;
            }
            if (chatTypeView == 3 && chatType == 40 && (tradeMode == 0 || tradeMode == 1 && check_username(name))) {
                if (chatIndex > scrollAmount - 14 && chatIndex <= scrollAmount) {
                    menuActionText[menuActionRow] = "Accept gamble <col=FFFFFF>" + name;
                    menuActionTypes[menuActionRow] = 525;
                    menuActionRow++;
                }
                index++;
            }
        }
    }

    private void buildChatAreaMenu(int j) {
        if (inputDialogState == 3) {
            return;
        }

        int index = 0;
        for (int i1 = 0; i1 < 500; i1++) {
            if (chatMessages[i1] == null)
                continue;
            int chatType = chatMessages[i1].getType();
            int scrollAmount = (70 - index * 14 + 42) + chatScrollAmount + 4 + 5;
            if (scrollAmount < -23)
                break;
            String chatName = chatMessages[i1].getName();
            String message = chatMessages[i1].getMessage();
            if (chatTypeView == 1) {
                buildPublicChat(j);
                break;
            }
            if (chatTypeView == 2) {
                buildFriendChat(j);
                break;
            }
            if (chatTypeView == 3 || chatTypeView == 4) {
                buildDuelorTrade(j);
                break;
            }
            if (chatTypeView == 5) {
                break;
            }
            if (chatName == null) {
                continue;
            }
            if (chatType == 0)
                index++;
            for (String s : menuActionText) {
                if (s != null) {
                    if (!s.contains("link") && message.contains("<col=3030ff>")) {
                        message = message.substring(12);
                    }
                }
            }
            if (j > scrollAmount - 14 && j <= scrollAmount && message.contains("<link")) {
                message = "<col=3030ff>" + message;
                menuActionText[menuActionRow] = "Visit link";
                menuActionTypes[menuActionRow] = 449;
                selectedMsg = message;
                menuActionRow++;
            }
            if (j < scrollAmount - 14 && j > scrollAmount && message.contains("<link")) {
                message = message.substring(12);
                message = "<col=0>" + message;
            }
            if ((chatType == 1 || chatType == 2)
                && (chatType == 1 || set_public_channel == 0 || set_public_channel == 1 && check_username(chatName))) {
                if (j > scrollAmount - 14 && j <= scrollAmount && !chatName.equalsIgnoreCase(local_player.username)) {
                    if (myPrivilege >= 1) {
                        menuActionText[menuActionRow] = "Report abuse <col=FFFFFF>" + chatName;
                        menuActionTypes[menuActionRow] = 606;
                        menuActionRow++;
                    }
                    menuActionText[menuActionRow] = "Add ignore <col=FFFFFF>" + chatName;
                    menuActionTypes[menuActionRow] = 42;
                    menuActionRow++;
                    menuActionText[menuActionRow] = "Add friend <col=FFFFFF>" + chatName;
                    menuActionTypes[menuActionRow] = 337;
                    menuActionRow++;
                    menuActionText[menuActionRow] = "Reply to <col=FFFFFF>" + chatName;
                    menuActionTypes[menuActionRow] = 338;
                    menuActionRow++;
                }
                index++;
            }
            if ((chatType == 3 || chatType == 7) && splitPrivateChat == 0
                && (chatType == 7 || privateChatMode == 0 || privateChatMode == 1 && check_username(chatName))) {
                if (j > scrollAmount - 14 && j <= scrollAmount) {
                    if (myPrivilege >= 1) {
                        menuActionText[menuActionRow] = "Report abuse <col=FFFFFF>" + chatName;
                        menuActionTypes[menuActionRow] = 606;
                        menuActionRow++;
                    }
                    menuActionText[menuActionRow] = "Add ignore <col=FFFFFF>" + chatName;
                    menuActionTypes[menuActionRow] = 42;
                    menuActionRow++;
                    menuActionText[menuActionRow] = "Add friend <col=FFFFFF>" + chatName;
                    menuActionTypes[menuActionRow] = 337;
                    menuActionRow++;
                    menuActionText[menuActionRow] = "Reply to <col=FFFFFF>" + chatName;
                    menuActionTypes[menuActionRow] = 338;
                    menuActionRow++;
                }
                index++;
            }
            if (chatType == 4 && (tradeMode == 0 || tradeMode == 1 && check_username(chatName))) {
                if (j > scrollAmount - 14 && j <= scrollAmount) {
                    menuActionText[menuActionRow] = "Accept trade <col=FFFFFF>" + chatName;
                    menuActionTypes[menuActionRow] = 484;
                    menuActionRow++;
                }
                index++;
            }
            if ((chatType == 5 || chatType == 6) && splitPrivateChat == 0 && privateChatMode < 2)
                index++;
            if (chatType == 8 && (tradeMode == 0 || tradeMode == 1 && check_username(chatName))) {
                if (j > scrollAmount - 14 && j <= scrollAmount) {
                    menuActionText[menuActionRow] = "Accept challenge <col=FFFFFF>" + chatName;
                    menuActionTypes[menuActionRow] = 6;
                    menuActionRow++;
                }
                index++;
            }
            if (chatType == 40 && (tradeMode == 0 || tradeMode == 1 && check_username(chatName))) {
                if (j > scrollAmount - 14 && j <= scrollAmount) {
                    menuActionText[menuActionRow] = "Accept gamble <col=FFFFFF>" + chatName;
                    menuActionTypes[menuActionRow] = 525;
                    menuActionRow++;
                }
                index++;
            }

            if (chatType == 20) {
                if (j > scrollAmount - 14 && j <= scrollAmount) {
                    if (broadcast != null && broadcast.hasUrl()) {
                        menuActionText[menuActionRow] = "Open Link";
                        menuActionTypes[menuActionRow] = 5555;
                        menuActionRow++;
                    }
                }
                index++;
            }
        }
    }

    public String setSkillTooltip(int skillLevel) {
        String totalExperience = "";
        String[] getToolTipText = new String[4];
        String setToolTipText = "";
        int maxLevel = 99;

        if (maximumLevels[skillLevel] > maxLevel) {
            if (skillLevel != 24) {
                maximumLevels[skillLevel] = 99;
            } else if (maximumLevels[skillLevel] > 120) {
                maximumLevels[skillLevel] = 120;
            }
        }

        NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
        int[] getSkillId = {0, 0, 2, 1, 4, 5, 6, 20, 22, 3, 16, 15, 17, 12, 9, 18, 21, 14, 14, 13, 10, 7, 11, 8, 19,
            24};
        int totalXP = currentExp[0] + currentExp[1] + currentExp[2] + currentExp[3] + currentExp[4] + currentExp[5]
            + currentExp[6] + currentExp[7] + currentExp[8] + currentExp[9] + currentExp[10] + currentExp[11]
            + currentExp[12] + currentExp[13] + currentExp[14] + currentExp[15] + currentExp[16] + currentExp[17]
            + currentExp[18] + currentExp[19] + currentExp[20] + currentExp[21] + currentExp[22];
        totalExperience = numberFormat.format(totalXP);
        if (!SkillConstants.SKILL_NAMES_SKILLSTAB[skillLevel].equals("-1")) {
            if (maximumLevels[getSkillId[skillLevel]] >= 99) {
                getToolTipText[0] = SkillConstants.SKILL_NAMES_SKILLSTAB[skillLevel] + " XP: "
                    + numberFormat.format(currentExp[getSkillId[skillLevel]]) + "<br>";
                setToolTipText = getToolTipText[0];
            } else {
                getToolTipText[0] = SkillConstants.SKILL_NAMES_SKILLSTAB[skillLevel] + " XP: "
                    + numberFormat.format(currentExp[getSkillId[skillLevel]]) + "<br>";
                getToolTipText[1] = "Next level at: "
                    + (numberFormat.format(getXPForLevel(maximumLevels[getSkillId[skillLevel]] + 1))) + "<br>";
                getToolTipText[2] = "Remaining XP: " + numberFormat.format(
                    getXPForLevel(maximumLevels[getSkillId[skillLevel]] + 1) - currentExp[getSkillId[skillLevel]])
                    + "<br>";
                getToolTipText[3] = "";
                setToolTipText = getToolTipText[0] + getToolTipText[1] + getToolTipText[2];
            }
        } else {
            setToolTipText = "Total XP: " + totalExperience;
        }
        return setToolTipText;
    }

    /**
     * interface_handle_auto_content
     */
    private void handle_widget_support(Widget child) {
        int support_opcode = child.contentType;

        if (child.invisible)
            return;

        if ((support_opcode >= 205) && (support_opcode <= (205 + 25))) {
            support_opcode -= 205;
            child.defaultText = setSkillTooltip(support_opcode);
            return;
        }

        if (support_opcode == 831) {
            child.defaultText = setSkillTooltip(0);
            return;
        }
        if (support_opcode >= 1 && support_opcode <= 100 || support_opcode >= 701 && support_opcode <= 800) {
            if (support_opcode == 1 && friendServerStatus == 0) {
                child.defaultText = "Loading friend list";
                child.optionType = 0;
                return;
            }
            if (support_opcode == 1 && friendServerStatus == 1) {
                child.defaultText = "Connecting to friendserver";
                child.optionType = 0;
                return;
            }
            if (support_opcode == 2 && friendServerStatus != 2) {
                child.defaultText = "Please wait...";
                child.optionType = 0;
                return;
            }
            int k = friendsCount;
            if (friendServerStatus != 2)
                k = 0;
            if (support_opcode > 700)
                support_opcode -= 601;
            else
                support_opcode--;
            if (support_opcode >= k) {
                child.defaultText = "";
                child.optionType = 0;
                return;
            } else {
                child.defaultText = friendsList[support_opcode];
                child.optionType = 1;
                return;
            }
        }
        if (support_opcode >= 101 && support_opcode <= 200 || support_opcode >= 801 && support_opcode <= 900) {
            int l = friendsCount;
            if (friendServerStatus != 2)
                l = 0;
            if (support_opcode > 800)
                support_opcode -= 701;
            else
                support_opcode -= 101;
            if (support_opcode >= l) {
                child.defaultText = "";
                child.optionType = 0;
                return;
            }
            if (friendsNodeIDs[support_opcode] == 0)
                child.defaultText = "<col=FF0000>Offline";
            else if (friendsNodeIDs[support_opcode] == nodeID)
                child.defaultText = "<col=00FF00>Online";
            else
                child.defaultText = "<col=FF0000>Offline";
            child.optionType = 1;
            return;
        }

        if (support_opcode == 203) {
            int i1 = friendsCount;
            if (friendServerStatus != 2)
                i1 = 0;
            child.scrollMax = i1 * 15 + 20;
            if (child.scrollMax <= child.height)
                child.scrollMax = child.height + 1;
            return;
        }
        if (support_opcode >= 401 && support_opcode <= 500) {
            if ((support_opcode -= 401) == 0 && friendServerStatus == 0) {
                child.defaultText = "Loading ignore list";
                child.optionType = 0;
                return;
            }
            if (support_opcode == 1 && friendServerStatus == 0) {
                child.defaultText = "Please wait...";
                child.optionType = 0;
                return;
            }
            int j1 = ignoreCount;
            if (friendServerStatus == 0)
                j1 = 0;
            if (support_opcode >= j1) {
                child.defaultText = "";
                child.optionType = 0;
                return;
            } else {
                child.defaultText = ignoreList[support_opcode];
                child.optionType = 1;
                return;
            }
        }
        if (support_opcode == 503) {
            child.scrollMax = ignoreCount * 15 + 20;
            if (child.scrollMax <= child.height)
                child.scrollMax = child.height + 1;
            return;
        }
        if (support_opcode == 327 || support_opcode == 328) {
            child.modelRotation1 = 150;
            child.modelRotation2 = (int) (Math.sin((double) game_tick / 40D) * 256D) & 0x7ff;
            if (updateCharacterCreation) {
                Model build = null;
                if (support_opcode == 327) {
                    for (int index = 0; index < 7; index++) {
                        int kits = characterClothing[index];
                        if (kits >= 0 && !IdentityKit.cache[kits].body_cached()) {
                            return;
                        }
                    }
                    Model[] default_model = new Model[7];
                    int part = 0;
                    for (int j2 = 0; j2 < 7; j2++) {
                        int kits = characterClothing[j2];
                        if (kits >= 0) {
                            default_model[part++] = IdentityKit.cache[kits].get_body();
                        }
                    }
                    build = new Model(part, default_model, true);
                }
                if (support_opcode == 328) {
                    build = local_player.get_animated_model();
                }
                //updateCharacterCreation = false;
                if (build != null) {
                    for (int color = 0; color < 5; color++) {
                        if (characterDesignColours[color] != 0) {
                            build.recolor(APPEARANCE_COLORS[color][0], APPEARANCE_COLORS[color][characterDesignColours[color]]);
                            if (color == 1) {
                                build.recolor(SHIRT_SECONDARY_COLORS[0], SHIRT_SECONDARY_COLORS[characterDesignColours[color]]);
                            }
                        }
                    }
                    if (local_player.idle_animation_id != -1) {
                        build.skin();
                        try {
                            build.interpolate(Sequence.cache[local_player.idle_animation_id].primary_frame[0]);
                        } catch (ArrayIndexOutOfBoundsException error) {
                            addReportToServer("[CATCHING ERROR] Support_opcode: [327] : [328] - Frame overflow");
                        }
                        child.model_type = 5;
                        child.model_id = 0;
                        child.set_model(aBoolean994, build);
                    } else {
                        addReportToServer("[ERROR] Support opcode: [327] : [328] - variable [localPlayer.standAnimIndex] == -1");
                    }
                } else {
                    addReportToServer("[ERROR] Support opcode: [327] : [328] - variable [model] == null");
                }
            }
        }

        if (support_opcode == 1430 && child.scrollMax > 5) {
            if (child.pauseTicks > 0) {
                child.pauseTicks--;
                return;
            }
            Widget parent = Widget.cache[child.parent];
            if (child.scrollPosition == -child.scrollMax) {
                child.endReached = true;
                child.pauseTicks = 20;
            }
            if (child.endReached) {
                if (child.scrollPosition == 0) {
                    child.endReached = false;
                    child.pauseTicks = 20;
                }
                child.scrollPosition++;
            } else {
                child.scrollPosition--;
            }
            parent.child_x[0] = child.scrollPosition;
        }
        if (support_opcode == 324) {
            if (aClass30_Sub2_Sub1_Sub1_931 == null) {
                aClass30_Sub2_Sub1_Sub1_931 = child.enabledSprite;
                aClass30_Sub2_Sub1_Sub1_932 = child.disabledSprite;
            }
            if (characterGender) {
                child.enabledSprite = aClass30_Sub2_Sub1_Sub1_932;
                return;
            } else {
                child.enabledSprite = aClass30_Sub2_Sub1_Sub1_931;
                return;
            }
        }
        if (support_opcode == 325) {
            if (aClass30_Sub2_Sub1_Sub1_931 == null) {
                aClass30_Sub2_Sub1_Sub1_931 = child.enabledSprite;
                aClass30_Sub2_Sub1_Sub1_932 = child.disabledSprite;
            }
            if (characterGender) {
                child.enabledSprite = aClass30_Sub2_Sub1_Sub1_931;
                return;
            } else {
                child.enabledSprite = aClass30_Sub2_Sub1_Sub1_932;
                return;
            }
        }
        if (support_opcode == 600) {
            child.defaultText = reportAbuseInput;
            if (game_tick % 20 < 10) {
                child.defaultText += "|";
                return;
            } else {
                child.defaultText += " ";
                return;
            }
        }
        if (support_opcode == 613)
            if (myPrivilege >= 1) {
                if (canMute) {
                    child.textColour = 0xff0000;
                    child.defaultText = "Moderator option: Mute player for 48 hours: <ON>";
                } else {
                    child.textColour = 0xffffff;
                    child.defaultText = "Moderator option: Mute player for 48 hours: <OFF>";
                }
            } else {
                child.defaultText = "";
            }
        if (support_opcode == 650 || support_opcode == 655)
            if (anInt1193 != 0) {
                String s;
                if (daysSinceLastLogin == 0)
                    s = "earlier today";
                else if (daysSinceLastLogin == 1)
                    s = "yesterday";
                else
                    s = daysSinceLastLogin + " days ago";
                child.defaultText = "You last logged in " + s + " from: " + SignLink.dns;
            } else {
                child.defaultText = "";
            }
        if (support_opcode == 651) {
            if (unreadMessages == 0) {
                child.defaultText = "0 unread messages";
                child.textColour = 0xffff00;
            }
            if (unreadMessages == 1) {
                child.defaultText = "1 unread defaultText";
                child.textColour = 65280;
            }
            if (unreadMessages > 1) {
                child.defaultText = unreadMessages + " unread messages";
                child.textColour = 65280;
            }
        }
        if (support_opcode == 652)
            if (daysSinceRecovChange == 201) {
                if (membersInt == 1)
                    child.defaultText = "<col=ffff00>This is a non-members world: <col=FFFFFF>Since you are a member we";
                else
                    child.defaultText = "";
            } else if (daysSinceRecovChange == 200) {
                child.defaultText = "You have not yet set any password recovery questions.";
            } else {
                String s1;
                if (daysSinceRecovChange == 0)
                    s1 = "Earlier today";
                else if (daysSinceRecovChange == 1)
                    s1 = "Yesterday";
                else
                    s1 = daysSinceRecovChange + " days ago";
                child.defaultText = s1 + " you changed your recovery questions";
            }
        if (support_opcode == 653)
            if (daysSinceRecovChange == 201) {
                if (membersInt == 1)
                    child.defaultText = "<col=FFFFFF>recommend you use a members world instead. You may use";
                else
                    child.defaultText = "";
            } else if (daysSinceRecovChange == 200)
                child.defaultText = "We strongly recommend you do so now to secure your account.";
            else
                child.defaultText = "If you do not remember making this change then cancel it immediately";
        if (support_opcode == 654) {
            if (daysSinceRecovChange == 201)
                if (membersInt == 1) {
                    child.defaultText = "<col=FFFFFF>this world but member benefits are unavailable whilst here.";
                    return;
                } else {
                    child.defaultText = "";
                    return;
                }
            if (daysSinceRecovChange == 200) {
                child.defaultText = "Do this from the 'account management' area on our front webpage";
                return;
            }
            child.defaultText = "Do this from the 'account management' area on our front webpage";
        }
    }

    private int update_offset = 0;

    private Constraint createPMConstraint() {
        if (update_offset == 0) {
            return null;
        }
        return new Constraint(5, 110, 321 - (update_offset * 10), 334); // TODO: endX should be determined by the width of that specific msg

    }

    private void drawSplitPrivateChat() {
        if (splitPrivateChat == 0) {
            return;
        }
        update_offset = 0;
        if (systemUpdateTime != 0) {
            update_offset = 1;
        }
        // Increase from 100 to full list size.
        for (int message_index = 0; message_index < 100; message_index++) {
            if (splitPrivateChatMessages.get(message_index) != null) {
                int messageType = splitPrivateChatMessages.get(message_index).getType();
                String name = StringUtils.capitalize(splitPrivateChatMessages.get(message_index).getName());
                List<ChatCrown> crowns = splitPrivateChatMessages.get(message_index).getCrowns();
                if ((messageType == 3 || messageType == 7) && (privateChatMode == 0 || privateChatMode == 1 && check_username(name))) {
                    boolean onIgnore = false;
                    if (name != null) {
                        for (int index = 0; index < ignoreCount; index++) {
                            if (ignoreListAsLongs[index] != Utils.longForName(name)) {
                                continue;
                            }
                            onIgnore = true;
                            break;
                        }
                        if (onIgnore) break;
                    }
                    int y = this.broadcast != null && isDisplayed ? 309 - update_offset * 13 : 329 - update_offset * 13;
                    if (screen != ScreenMode.FIXED) {
                        y = window_height - 170 - update_offset * 13;
                        if (broadcast != null && isDisplayed) {
                            y -= 20;
                        }
                        if (cButtonCPos == -1)
                            y += 135;
                    }
                    int x = 4;
                    adv_font_regular.draw("From", x, y, 65535, true);
                    x += adv_font_regular.get_width("From ");
                    for (ChatCrown c : crowns) {
                        SimpleImage sprite = spriteCache.get(c.getSpriteId());
                        if (sprite != null) {
                            sprite.drawSprite(x, y - 12);
                            x += sprite.width + 2;
                        }
                    }

                    adv_font_regular.draw(name + ": " + splitPrivateChatMessages.get(message_index).getMessage(), x, y, 65535, true);

                    if (++update_offset >= 5) {
                        return;
                    }
                }
                if (messageType == 5 && privateChatMode < 2) {
                    int y = this.broadcast != null && isDisplayed ? 309 - update_offset * 13 : 329 - update_offset * 13;
                    if (screen != ScreenMode.FIXED) {
                        y = window_height - 170 - update_offset * 13;
                        if (broadcast != null && isDisplayed) {
                            y -= 20;
                        }
                    }

                    adv_font_regular.draw(splitPrivateChatMessages.get(message_index).getMessage(), 4, y, 65535, true);

                    if (++update_offset >= 5) {
                        return;
                    }
                }
                if (messageType == 6 && privateChatMode < 2) {
                    int y = this.broadcast != null && isDisplayed ? 309 - update_offset * 13 : 329 - update_offset * 13;
                    if (screen != ScreenMode.FIXED) {
                        y = window_height - 170 - update_offset * 13;
                        if (broadcast != null && isDisplayed) {
                            y -= 20;
                        }
                    }

                    adv_font_regular.draw("To " + name + ": " + capitalizeJustFirst(splitPrivateChatMessages.get(message_index).getMessage()), 4, y, 65535, 0);
                    if (++update_offset >= 5) {
                        return;
                    }
                }
            }
        }
    }

    public static String capitalizeJustFirst(String str) {
        str = str.toLowerCase();
        if (str.length() > 1) {
            str = str.substring(0, 1).toUpperCase() + str.substring(1);
        } else {
            return str.toUpperCase();
        }
        return str;
    }

    private final ChatMessage[] chatMessages;
    private List<ChatMessage> splitPrivateChatMessages;

    private void resetSplitPrivateChatMessages() {
        for (int index = 0; index < 500; index++) {
            splitPrivateChatMessages.set(index, null);
        }
    }

    public void sendMessage(String message, int type, String name) {
        sendMessage(message, type, name, null);
    }

    public void sendMessage(String message, int type, String name, String title) {
        if (name == null || name.length() == 0) {
            name = "";
            //return;
        }
        List<ChatCrown> crowns = new ArrayList<>();
        for (ChatCrown c : ChatCrown.values()) {
            boolean exists = false;
            if (message.contains(c.getIdentifier())) {
                message = message.replaceAll(c.getIdentifier(), "");
                exists = true;
            }
            if (name.contains(c.getIdentifier())) {
                name = name.replaceAll(c.getIdentifier(), "");
                exists = true;
            }
            if (exists) {
                // System.out.println("Crown exists!");
                if (!crowns.contains(c)) {
                    crowns.add(c);
                }
            }
        }

        if (type == 0 && dialogueId != -1) {
            clickToContinueString = message;
            super.click_type = 0;
        }

        if (backDialogueId == -1) {
            update_chat_producer = true;
        }

        // Create new chat message
        ChatMessage chatMessage = new ChatMessage(message, name, title, type, rights, crowns);
        // System.out.println("message is actually: " + message);
        // Shift all other messages
        for (int index = 499; index > 0; index--) {
            chatMessages[index] = chatMessages[index - 1];
            splitPrivateChatMessages.set(index, splitPrivateChatMessages.get(index - 1));
        }

        // Insert new message
        chatMessages[0] = chatMessage;
        splitPrivateChatMessages.set(0, chatMessage);
        //splitPrivateChatMessages.add(chatMessage);
        //System.out.println("chat message: " + chatMessage);
    }

    public static void setTab(int id) {
        sidebarId = id;
        update_tab_producer = true;
    }

    private void minimapHovers() {
        final boolean fixed = screen == ScreenMode.FIXED;

        prayHover = fixed
            ? prayHover = super.cursor_x >= 517 && super.cursor_x <= 572
            && super.cursor_y >= (setting.draw_special_orb ? 79 : 94)
            && super.cursor_y < (setting.draw_special_orb ? 111 : 118)
            : super.cursor_x >= window_width - 210 && super.cursor_x <= window_width - 157
            && super.cursor_y >= (setting.draw_special_orb ? 90 : 95)
            && super.cursor_y < (setting.draw_special_orb ? 119 : 124);

        runHover = fixed
            ? runHover = super.cursor_x >= (setting.draw_special_orb ? 528 : 542)
            && super.cursor_x <= (setting.draw_special_orb ? 582 : 594)
            && super.cursor_y >= (setting.draw_special_orb ? 111 : 129)
            && super.cursor_y < (setting.draw_special_orb ? 143 : 157)
            : super.cursor_x >= window_width - (setting.draw_special_orb ? 196 : 186)
            && super.cursor_x <= window_width - (setting.draw_special_orb ? 142 : 132)
            && super.cursor_y >= (setting.draw_special_orb ? 123 : 132)
            && super.cursor_y < (setting.draw_special_orb ? 150 : 159);

        expCounterHover = fixed ? super.cursor_x >= 517 && super.cursor_x <= 530 && super.cursor_y >= 22 && super.cursor_y <= 41 :
            //resize
            super.cursor_x >= window_width - 215 && super.cursor_x <= window_width - 200 && super.cursor_y >= 21 && super.cursor_y <= 40;

        bankHover = fixed ? super.cursor_x >= 743 && super.cursor_x <= 757 && super.cursor_y >= 116 && super.cursor_y <= 134 :
            //resize
            super.cursor_x >= window_width - 25 && super.cursor_x <= window_width - 11 && super.cursor_y >= 160 && super.cursor_y <= 179;

        //public static int window_width = 765;
        //public static int window_height = 503;
        healHover = fixed ? super.cursor_x >= 722 && super.cursor_x <= 736 && super.cursor_y >= 143 && super.cursor_y <= 163 :
            //resize
            super.cursor_x >= window_width - 45 && super.cursor_x <= window_width - 30 && super.cursor_y >= 185 && super.cursor_y <= 203;

        potionsHover = fixed ? super.cursor_x >= 742 && super.cursor_x <= 758 && super.cursor_y >= 142 && super.cursor_y <= 163 :
            //resize
            super.cursor_x >= window_width - 25 && super.cursor_x <= window_width - 10 && super.cursor_y >= 184 && super.cursor_y <= 203;
    }

    private final int[] tabClickX = {38, 33, 33, 33, 33, 33, 38, 38, 33, 33, 33, 33, 33, 38},
        tabClickStart = {522, 560, 593, 625, 659, 692, 724, 522, 560, 593, 625, 659, 692, 724},
        tabClickY = {169, 169, 169, 169, 169, 169, 169, 466, 466, 466, 466, 466, 466, 466};

    private void processTabClick() {
        if (super.click_type == 1) {
            resetInputFieldFocus();
            if (screen == ScreenMode.FIXED
                || screen != ScreenMode.FIXED && settings[ConfigUtility.SIDE_STONES_ARRANGEMENT_ID] == 0) {
                int xOffset = screen == ScreenMode.FIXED ? 0 : window_width - 765;
                int yOffset = screen == ScreenMode.FIXED ? 0 : window_height - 503;
                for (int i = 0; i < tabClickX.length; i++) {
                    if (super.cursor_x >= tabClickStart[i] + xOffset
                        && super.cursor_x <= tabClickStart[i] + tabClickX[i] + xOffset
                        && super.cursor_y >= tabClickY[i] + yOffset && super.cursor_y < tabClickY[i] + 37 + yOffset
                        && tabInterfaceIDs[i] != -1) {
                        sidebarId = i;
                        update_tab_producer = true;
                        if (ClientConstants.SPAWN_TAB_DISPLAY_ALL_ITEMS_PRELOADED) {
                            // Spawn tab
                            if (sidebarId == 2) {
                                SpawnTabAllItems.searchingSpawnTab = true;
                            } else {
                                SpawnTabAllItems.searchingSpawnTab = false;
                            }
                        } else {
                            // Spawn tab
                            if (sidebarId == 2) {
                                SpawnTab.searchingSpawnTab = true;
                            } else {
                                SpawnTab.searchingSpawnTab = false;
                            }
                        }
                        break;
                    }
                }
            } else if (screen != ScreenMode.FIXED && settings[ConfigUtility.SIDE_STONES_ARRANGEMENT_ID] == 1
                && window_width < 1000) {
                if (super.click_x >= window_width - 226 && super.click_x <= window_width - 195
                    && super.click_y >= window_height - 72 && super.click_y < window_height - 40
                    && tabInterfaceIDs[0] != -1) {
                    if (sidebarId == 0) {
                        showTabComponents = !showTabComponents;
                    } else {
                        showTabComponents = true;
                    }
                    sidebarId = 0;
                    update_tab_producer = true;

                }
                if (super.click_x >= window_width - 194 && super.click_x <= window_width - 163
                    && super.click_y >= window_height - 72 && super.click_y < window_height - 40
                    && tabInterfaceIDs[1] != -1) {
                    if (sidebarId == 1) {
                        showTabComponents = !showTabComponents;
                    } else {
                        showTabComponents = true;
                    }
                    sidebarId = 1;
                    update_tab_producer = true;

                }
                if (super.click_x >= window_width - 162 && super.click_x <= window_width - 131
                    && super.click_y >= window_height - 72 && super.click_y < window_height - 40
                    && tabInterfaceIDs[2] != -1) {
                    if (sidebarId == 2) {
                        showTabComponents = !showTabComponents;
                    } else {
                        showTabComponents = true;
                    }
                    sidebarId = 2;
                    update_tab_producer = true;

                }
                if (super.click_x >= window_width - 129 && super.click_x <= window_width - 98
                    && super.click_y >= window_height - 72 && super.click_y < window_height - 40
                    && tabInterfaceIDs[3] != -1) {
                    if (sidebarId == 3) {
                        showTabComponents = !showTabComponents;
                    } else {
                        showTabComponents = true;
                    }
                    sidebarId = 3;
                    update_tab_producer = true;

                }
                if (super.click_x >= window_width - 97 && super.click_x <= window_width - 66
                    && super.click_y >= window_height - 72 && super.click_y < window_height - 40
                    && tabInterfaceIDs[4] != -1) {
                    if (sidebarId == 4) {
                        showTabComponents = !showTabComponents;
                    } else {
                        showTabComponents = true;
                    }
                    sidebarId = 4;
                    update_tab_producer = true;

                }
                if (super.click_x >= window_width - 65 && super.click_x <= window_width - 34
                    && super.click_y >= window_height - 72 && super.click_y < window_height - 40
                    && tabInterfaceIDs[5] != -1) {
                    if (sidebarId == 5) {
                        showTabComponents = !showTabComponents;
                    } else {
                        showTabComponents = true;
                    }
                    sidebarId = 5;
                    update_tab_producer = true;

                }
                if (super.click_x >= window_width - 33 && super.click_x <= window_width
                    && super.click_y >= window_height - 72 && super.click_y < window_height - 40
                    && tabInterfaceIDs[6] != -1) {
                    if (sidebarId == 6) {
                        showTabComponents = !showTabComponents;
                    } else {
                        showTabComponents = true;
                    }
                    sidebarId = 6;
                    update_tab_producer = true;

                }

                if (super.click_x >= window_width - 194 && super.click_x <= window_width - 163
                    && super.click_y >= window_height - 37 && super.click_y < window_height - 0
                    && tabInterfaceIDs[8] != -1) {
                    if (sidebarId == 8) {
                        showTabComponents = !showTabComponents;
                    } else {
                        showTabComponents = true;
                    }
                    sidebarId = 8;
                    update_tab_producer = true;

                }
                if (super.click_x >= window_width - 162 && super.click_x <= window_width - 131
                    && super.click_y >= window_height - 37 && super.click_y < window_height - 0
                    && tabInterfaceIDs[9] != -1) {
                    if (sidebarId == 9) {
                        showTabComponents = !showTabComponents;
                    } else {
                        showTabComponents = true;
                    }
                    sidebarId = 9;
                    update_tab_producer = true;

                }
                if (super.click_x >= window_width - 129 && super.click_x <= window_width - 98
                    && super.click_y >= window_height - 37 && super.click_y < window_height - 0
                    && tabInterfaceIDs[10] != -1) {
                    if (sidebarId == 7) {
                        showTabComponents = !showTabComponents;
                    } else {
                        showTabComponents = true;
                    }
                    sidebarId = 7;
                    update_tab_producer = true;

                }
                if (super.click_x >= window_width - 97 && super.click_x <= window_width - 66
                    && super.click_y >= window_height - 37 && super.click_y < window_height - 0
                    && tabInterfaceIDs[11] != -1) {
                    if (sidebarId == 11) {
                        showTabComponents = !showTabComponents;
                    } else {
                        showTabComponents = true;
                    }
                    sidebarId = 11;
                    update_tab_producer = true;

                }
                if (super.click_x >= window_width - 65 && super.click_x <= window_width - 34
                    && super.click_y >= window_height - 37 && super.click_y < window_height - 0
                    && tabInterfaceIDs[12] != -1) {
                    if (sidebarId == 12) {
                        showTabComponents = !showTabComponents;
                    } else {
                        showTabComponents = true;
                    }
                    sidebarId = 12;
                    update_tab_producer = true;

                }
                if (super.click_x >= window_width - 33 && super.click_x <= window_width
                    && super.click_y >= window_height - 37 && super.click_y < window_height - 0
                    && tabInterfaceIDs[13] != -1) {
                    if (sidebarId == 13) {
                        showTabComponents = !showTabComponents;
                    } else {
                        showTabComponents = true;
                    }
                    sidebarId = 13;
                    update_tab_producer = true;

                }
            } else if (screen != ScreenMode.FIXED && settings[ConfigUtility.SIDE_STONES_ARRANGEMENT_ID] == 1
                && window_width >= 1000) {
                if (super.cursor_y >= window_height - 37 && super.cursor_y <= window_height) {
                    if (super.cursor_x >= window_width - 417 && super.cursor_x <= window_width - 386) {
                        if (sidebarId == 0) {
                            showTabComponents = !showTabComponents;
                        } else {
                            showTabComponents = true;
                        }
                        sidebarId = 0;
                        update_tab_producer = true;
                    }
                    if (super.cursor_x >= window_width - 385 && super.cursor_x <= window_width - 354) {
                        if (sidebarId == 1) {
                            showTabComponents = !showTabComponents;
                        } else {
                            showTabComponents = true;
                        }
                        sidebarId = 1;
                        update_tab_producer = true;
                    }
                    if (super.cursor_x >= window_width - 353 && super.cursor_x <= window_width - 322) {
                        if (sidebarId == 2) {
                            showTabComponents = !showTabComponents;
                        } else {
                            showTabComponents = true;
                        }
                        sidebarId = 2;
                        update_tab_producer = true;
                    }
                    if (super.cursor_x >= window_width - 321 && super.cursor_x <= window_width - 290) {
                        if (sidebarId == 3) {
                            showTabComponents = !showTabComponents;
                        } else {
                            showTabComponents = true;
                        }
                        sidebarId = 3;
                        update_tab_producer = true;
                    }
                    if (super.cursor_x >= window_width - 289 && super.cursor_x <= window_width - 258) {
                        if (sidebarId == 4) {
                            showTabComponents = !showTabComponents;
                        } else {
                            showTabComponents = true;
                        }
                        sidebarId = 4;
                        update_tab_producer = true;
                    }
                    if (super.cursor_x >= window_width - 257 && super.cursor_x <= window_width - 226) {
                        if (sidebarId == 5) {
                            showTabComponents = !showTabComponents;
                        } else {
                            showTabComponents = true;
                        }
                        sidebarId = 5;
                        update_tab_producer = true;
                    }
                    if (super.cursor_x >= window_width - 225 && super.cursor_x <= window_width - 194) {
                        if (sidebarId == 6) {
                            showTabComponents = !showTabComponents;
                        } else {
                            showTabComponents = true;
                        }
                        sidebarId = 6;
                        update_tab_producer = true;
                    }
                    if (super.cursor_x >= window_width - 193 && super.cursor_x <= window_width - 163) {
                        if (sidebarId == 8) {
                            showTabComponents = !showTabComponents;
                        } else {
                            showTabComponents = true;
                        }
                        sidebarId = 8;
                        update_tab_producer = true;
                    }
                    if (super.cursor_x >= window_width - 162 && super.cursor_x <= window_width - 131) {
                        if (sidebarId == 9) {
                            showTabComponents = !showTabComponents;
                        } else {
                            showTabComponents = true;
                        }
                        sidebarId = 9;
                        update_tab_producer = true;
                    }
                    if (super.cursor_x >= window_width - 130 && super.cursor_x <= window_width - 99) {
                        if (sidebarId == 7) {
                            showTabComponents = !showTabComponents;
                        } else {
                            showTabComponents = true;
                        }
                        sidebarId = 7;
                        update_tab_producer = true;
                    }
                    if (super.cursor_x >= window_width - 98 && super.cursor_x <= window_width - 67) {
                        if (sidebarId == 11) {
                            showTabComponents = !showTabComponents;
                        } else {
                            showTabComponents = true;
                        }
                        sidebarId = 11;
                        update_tab_producer = true;
                    }
                    if (super.cursor_x >= window_width - 66 && super.cursor_x <= window_width - 45) {
                        if (sidebarId == 12) {
                            showTabComponents = !showTabComponents;
                        } else {
                            showTabComponents = true;
                        }
                        sidebarId = 12;
                        update_tab_producer = true;
                    }
                    if (super.cursor_x >= window_width - 31 && super.cursor_x <= window_width) {
                        if (sidebarId == 13) {
                            showTabComponents = !showTabComponents;
                        } else {
                            showTabComponents = true;
                        }
                        sidebarId = 13;
                        update_tab_producer = true;
                    }
                }
            }
        }
    }

    private void resetImageProducers2() {
        if (chatboxImageProducer != null) {
            return;
        }

        nullLoader();
        //super.fullGameScreen = null;
        topLeft1BackgroundTile = null;
        bottomLeft1BackgroundTile = null;
        loginBoxImageProducer = null;
        loginScreenAccessories = null;
        flameLeftBackground = null;
        flameRightBackground = null;
        bottomLeft0BackgroundTile = null;
        bottomRightImageProducer = null;
        loginMusicImageProducer = null;
        middleLeft1BackgroundTile = null;
        aRSImageProducer_1115 = null;
        super.fullGameScreen = new ProducingGraphicsBuffer(765, 503);
        chatboxImageProducer = new ProducingGraphicsBuffer(519, 165);// chatback
        minimapImageProducer = new ProducingGraphicsBuffer(249, 168);// mapback
        Rasterizer2D.clear();
        spriteCache.get(19).drawSprite(0, 0);
        tabImageProducer = new ProducingGraphicsBuffer(249, 335);// inventory
        gameScreenImageProducer = new ProducingGraphicsBuffer(512, 334);// gamescreen
        Rasterizer2D.clear();
        chatSettingImageProducer = new ProducingGraphicsBuffer(249, 45);
        update_producers = true;
    }

    private void refreshMinimap(SimpleImage sprite, int j, int k) {
        int l = k * k + j * j;
        if (l > 4225 && l < 0x15f90) {
            int i1 = camera_pan + map_rotation & 0x7ff;
            int j1 = Model.SINE[i1];
            int k1 = Model.COSINE[i1];
            j1 = (j1 * 256) / (map_zoom + 256);
            k1 = (k1 * 256) / (map_zoom + 256);
        } else {
            markMinimap(sprite, k, j);
        }
    }

    public void rightClickChatButtons() {
        if (cursor_y >= window_height - 22 && cursor_y <= window_height) {
            if (super.cursor_x >= 5 && super.cursor_x <= 61) {
                menuActionText[1] = "View All";
                menuActionTypes[1] = 999;
                menuActionRow = 2;
            } else if (super.cursor_x >= 71 && super.cursor_x <= 127) {
                menuActionText[1] = "View Game";
                menuActionTypes[1] = 998;
                menuActionRow = 2;
            } else if (super.cursor_x >= 137 && super.cursor_x <= 193) {
                menuActionText[1] = "Hide public";
                menuActionTypes[1] = 997;
                menuActionText[2] = "Off public";
                menuActionTypes[2] = 996;
                menuActionText[3] = "Friends public";
                menuActionTypes[3] = 995;
                menuActionText[4] = "On public";
                menuActionTypes[4] = 994;
                menuActionText[5] = "View public";
                menuActionTypes[5] = 993;
                menuActionRow = 6;
            } else if (super.cursor_x >= 203 && super.cursor_x <= 259) {
                menuActionText[1] = "Off private";
                menuActionTypes[1] = 992;
                menuActionText[2] = "Friends private";
                menuActionTypes[2] = 991;
                menuActionText[3] = "On private";
                menuActionTypes[3] = 990;
                menuActionText[4] = "View private";
                menuActionTypes[4] = 989;
                menuActionRow = 5;
                menuActionText[5] = "Clear private";
                menuActionTypes[5] = 1895;
                menuActionRow = 6;
            } else if (super.cursor_x >= 269 && super.cursor_x <= 325) {
                menuActionText[1] = "Off clan chat";
                menuActionTypes[1] = 1003;
                menuActionText[2] = "Friends clan chat";
                menuActionTypes[2] = 1002;
                menuActionText[3] = "On clan chat";
                menuActionTypes[3] = 1001;
                menuActionText[4] = "View clan chat";
                menuActionTypes[4] = 1000;
                menuActionRow = 5;
            } else if (super.cursor_x >= 335 && super.cursor_x <= 391) {
                menuActionText[1] = "Off trade";
                menuActionTypes[1] = 987;
                menuActionText[2] = "Friends trade";
                menuActionTypes[2] = 986;
                menuActionText[3] = "On trade";
                menuActionTypes[3] = 985;
                menuActionText[4] = "View trade";
                menuActionTypes[4] = 984;
                menuActionRow = 5;
            } else if (super.cursor_x >= 404 && super.cursor_x <= 515) {
                menuActionText[1] = "Report Abuse";
                menuActionTypes[1] = 606;
                menuActionRow = 2;
            }
        }
    }

    private boolean interfaceOpen() {
        return widget_overlay_id > 0;
    }

    public void processRightClick() {
        if (activeInterfaceType != 0) {
            return;
        }
        menuActionText[0] = "Cancel";
        menuActionTypes[0] = 1107;
        menuActionRow = 1;
        if (showChatComponents) {
            buildSplitPrivateChatMenu();
        }

        frameFocusedInterface = 0;
        anInt1315 = 0;
        if (screen == ScreenMode.FIXED) {
            if (super.cursor_x > 4 && super.cursor_y > 4 && super.cursor_x < 516 && super.cursor_y < 338) {
                if (widget_overlay_id != -1) {
                    buildInterfaceMenu(4, Widget.cache[widget_overlay_id], super.cursor_x, 4, super.cursor_y, 0);
                    //When this condition is true, the screen menu is never created, therefore "walk here" and other things aren't created, which will prevent the player from clicking on the screen. We commented this out for "walk here" to work. It might change how "walk here" works in right click menus but it should be fine.
                } else {
                    createMenu();
                }
            }
        } else if (screen != ScreenMode.FIXED) {
            if (getMousePositions()) {
                if (super.cursor_x > (window_width / 2) - 356 && super.cursor_y > (window_height / 2) - 230 && super.cursor_x < ((window_width / 2) + 356) && super.cursor_y < (window_height / 2) + 230 && widget_overlay_id != -1) {
                    buildInterfaceMenu((window_width / 2) - 356, Widget.cache[widget_overlay_id], super.cursor_x, (window_height / 2) - 230, super.cursor_y, 0);
                    //When this condition is true, the screen menu is never created, therefore "walk here" and other things aren't created, which will prevent the player from clicking on the screen. We commented this out for "walk here" to work. It might change how "walk here" works in right click menus but it should be fine.
                } else {
                    createMenu();
                }
            }
        }

        if (frameFocusedInterface != focusedViewportWidget) {
            focusedViewportWidget = frameFocusedInterface;
        }
        if (anInt1315 != gameTooltipSupportId) {
            gameTooltipSupportId = anInt1315;
        }
        frameFocusedInterface = 0;
        anInt1315 = 0;

        if (screen == ScreenMode.FIXED || settings[ConfigUtility.SIDE_STONES_ARRANGEMENT_ID] == 0) {
            final int yOffset = screen == ScreenMode.FIXED ? 0 : window_height - 503;
            final int xOffset = screen == ScreenMode.FIXED ? 0 : window_width - 765;
            if (super.cursor_x > 548 + xOffset && super.cursor_x < 740 + xOffset && super.cursor_y > 207 + yOffset
                && super.cursor_y < 468 + yOffset) {
                if (overlayInterfaceId != -1) {
                    buildInterfaceMenu(548 + xOffset, Widget.cache[overlayInterfaceId], super.cursor_x,
                        207 + yOffset, super.cursor_y, 0);
                } else if (tabInterfaceIDs[sidebarId] != -1) {
                    buildInterfaceMenu(548 + xOffset, Widget.cache[tabInterfaceIDs[sidebarId]], super.cursor_x,
                        207 + yOffset, super.cursor_y, 0);
                }
            }
        } else if (settings[ConfigUtility.SIDE_STONES_ARRANGEMENT_ID] == 1) {
            final int yOffset = window_width >= 1000 ? 37 : 74;
            if (super.cursor_x > window_width - 197 && super.cursor_y > window_height - yOffset - 267
                && super.cursor_x < window_width - 7 && super.cursor_y < window_height - yOffset - 7
                && showTabComponents) {
                if (overlayInterfaceId != -1) {
                    buildInterfaceMenu(window_width - 197, Widget.cache[overlayInterfaceId], super.cursor_x,
                        window_height - yOffset - 267, super.cursor_y, 0);
                } else if (tabInterfaceIDs[sidebarId] != -1) {
                    buildInterfaceMenu(window_width - 197, Widget.cache[tabInterfaceIDs[sidebarId]],
                        super.cursor_x, window_height - yOffset - 267, super.cursor_y, 0);
                }
            }
        }
        if (frameFocusedInterface != focusedSidebarWidget) {
            update_tab_producer = true;
            focusedSidebarWidget = frameFocusedInterface;
        }
        if (anInt1315 != tabTooltipSupportId) {
            update_tab_producer = true;
            tabTooltipSupportId = anInt1315;
        }

        //    System.out.println(update_offset);


        // System.out.println(splitPrivateChatMessages.get(17).getMessage());
        /*Constraint constraint = createPMConstraint();
        if (constraint != null) {
            if (super.cursor_x > constraint.getStartX() && super.cursor_y > (screen == ScreenMode.FIXED ? constraint.getStartY() : window_height - 187 - (update_offset * 10)) && super.cursor_x < constraint.getEndX() && super.cursor_y < (screen == ScreenMode.FIXED ? constraint.getEndY() : window_height - 167)) {
                if(interfaceOpen()) {
                    return;
                }
                menuActionRow = 1;
                menuActionText[menuActionRow] = "Walk here";
                menuActionTypes[menuActionRow] = 519;
                firstMenuAction[menuActionRow] = super.cursor_x;
                secondMenuAction[menuActionRow] = super.cursor_y;
                menuActionRow++;
                menuActionText[menuActionRow] = "Clear Private Messages";
                menuActionTypes[menuActionRow] = 1895;
                menuActionRow++;
            }
        }*/


        if (broadcast != null) {
            broadcast.isHovered(super.cursor_x, super.cursor_y);
        }
        frameFocusedInterface = 0;
        anInt1315 = 0;

        if (super.cursor_x > 0 && super.cursor_y > (screen == ScreenMode.FIXED ? 338 : window_height - 165) && super.cursor_x < 490 && super.cursor_y < (screen == ScreenMode.FIXED ? 463 : window_height - 40)) {
            if (backDialogueId != -1) {
                buildInterfaceMenu(20, Widget.cache[backDialogueId], super.cursor_x, (screen == ScreenMode.FIXED ? 358 : window_height - 145), super.cursor_y, 0);
            } else if (super.cursor_y < (screen == ScreenMode.FIXED ? 463 : window_height - 40) && super.cursor_x < 490) {
                buildChatAreaMenu(super.cursor_y - (screen == ScreenMode.FIXED ? 338 : window_height - 165));
            }
        }
        if (backDialogueId != -1 && frameFocusedInterface != focusedChatWidget) {
            update_chat_producer = true;
            focusedChatWidget = frameFocusedInterface;
        }
        if (backDialogueId != -1 && anInt1315 != chatTooltipSupportId) {
            update_chat_producer = true;
            chatTooltipSupportId = anInt1315;
        }
        if (super.cursor_x > 4 && super.cursor_y > 480 && super.cursor_x < 516 && super.cursor_y < window_height) {
            rightClickChatButtons();
        }
        processMinimapActions();
        boolean flag = false;
        while (!flag) {
            flag = true;
            for (int j = 0; j < menuActionRow - 1; j++) {
                if (menuActionTypes[j] < 1000 && menuActionTypes[j + 1] > 1000) {
                    String s = menuActionText[j];
                    menuActionText[j] = menuActionText[j + 1];
                    menuActionText[j + 1] = s;
                    int k = menuActionTypes[j];
                    menuActionTypes[j] = menuActionTypes[j + 1];
                    menuActionTypes[j + 1] = k;
                    k = firstMenuAction[j];
                    firstMenuAction[j] = firstMenuAction[j + 1];
                    firstMenuAction[j + 1] = k;
                    k = secondMenuAction[j];
                    secondMenuAction[j] = secondMenuAction[j + 1];
                    secondMenuAction[j + 1] = k;
                    long k2 = selectedMenuActions[j];
                    selectedMenuActions[j] = selectedMenuActions[j + 1];
                    selectedMenuActions[j + 1] = k2;
                    flag = false;
                }
            }
        }
    }

    private int method83(int i, int j, int k) {
        int l = 256 - k;
        return ((i & 0xff00ff) * l + (j & 0xff00ff) * k & 0xff00ff00)
            + ((i & 0xff00) * l + (j & 0xff00) * k & 0xff0000) >> 8;
    }

    private long lastLoginAttempt;

    /**
     * The login method for the 317 protocol.
     *
     * @param name         The name of the user trying to login.
     * @param password     The password of the user trying to login.
     * @param reconnecting The flag for the user indicating to attempt to reconnect.
     */
    public void login(String name, String password, boolean reconnecting) {
        setting.save();
        SignLink.setError(name);
        try {
            if (name.length() < 1) {
                firstLoginMessage = "";
                secondLoginMessage = "Your username is too short.";
                return;
            }
            if (password.length() < 3) {
                firstLoginMessage = "";
                secondLoginMessage = "Your password is too short.";
                return;
            }
            if (!reconnecting) {
                firstLoginMessage = "";
                secondLoginMessage = "Connecting to server...";
                drawLoginScreen(true);
            }

            socketStream = new BufferedConnection(this, openSocket(ClientConstants.SERVER_PORT));
            outBuffer.pos = 0;
            outBuffer.writeByte(14); // REQUEST
            socketStream.queueBytes(1, outBuffer.payload);

            int response = socketStream.read();

            addReportToServer("Server login response code " + response);

            int copy = response;

            if (response == 0) {

                socketStream.flushInputStream(incoming.payload, 8);
                incoming.pos = 0;
                serverSeed = incoming.readLong(); // aka server session key
                int[] seed = new int[4];
                seed[0] = (int) (Math.random() * 99999999D);
                seed[1] = (int) (Math.random() * 99999999D);
                seed[2] = (int) (serverSeed >> 32);
                seed[3] = (int) serverSeed;
                outBuffer.pos = 0;
                outBuffer.writeByte(10);
                outBuffer.writeInt(seed[0]);
                outBuffer.writeInt(seed[1]);
                outBuffer.writeInt(seed[2]);
                outBuffer.writeInt(seed[3]);
                outBuffer.writeString(ClientConstants.CLIENT_VERSION);
                outBuffer.writeString(name);
                outBuffer.writeString(password);
                byte[] mac = getMACAddress();
                outBuffer.writeByte(mac.length);
                outBuffer.writeBytes(mac);
                /*outBuffer.writeWordBigEndian(identifierSet.hardDiskSerial.size());
                for (int index = 0; index < identifierSet.hardDiskSerial.size(); index++) {
                    outBuffer.writeString(WhatIsBAN());
                }
                outBuffer.writeWordBigEndian(identifierSet.fileStoreUuid.size());
                for (int index = 0; index < identifierSet.fileStoreUuid.size(); index++) {
                    outBuffer.writeString(WhatIsBAN());
                }

                Collection<UUID> uuids = cachedUUIDGroup.values();
                outBuffer.writeWordBigEndian(uuids.size());
                uuids.forEach(uuid -> sendStuff(outBuffer, uuid));*/
                outBuffer.writeString("");
                outBuffer.encryptRSAContent();

                loginBuffer.pos = 0;
                loginBuffer.writeByte(reconnecting ? 18 : 16);
                loginBuffer.writeByte(outBuffer.pos + 2); // size of the
                // login block
                loginBuffer.writeByte(255);
                loginBuffer.writeByte(low_detail ? 1 : 0); // low mem or not
                loginBuffer.writeBytes(outBuffer.payload, outBuffer.pos, 0);
                cipher = new IsaacCipher(seed);
                for (int index = 0; index < 4; index++)
                    seed[index] += 50;

                encryption = new IsaacCipher(seed);
                socketStream.queueBytes(loginBuffer.pos, loginBuffer.payload);
                response = socketStream.read();
            }
            addReportToServer("Server login response code is now " + response);
            if (response == 1) {
                try {
                    Thread.sleep(2000L);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                login(name, password, reconnecting);
                return;
            }
            addReportToServer("Server login response code is again now " + response);
            if (response == 2) {
                addReportToServer("Successful login (response code 2)");
                int rights = myPrivilege;
                rights = socketStream.read();
                // flagged = socketStream.read() == 1;
                aLong1220 = 0L;
                recentIncomingPrivateChatUserList = new String[10];
                //mouseDetection.coordsIndex = 0;
                super.awt_focus = true;
                aBoolean954 = true;
                loggedIn = true;
                loggedInWatch.reset();
                Widget.cache[12697].defaultText = "Drag setting: <col=ffffff>" + setting.drag_item_value + (setting.drag_item_value == 5 ? " (OSRS)" : setting.drag_item_value == 10 ? " (Pre-EOC)" : " (Custom)");
                packetSender = new PacketSender(cipher);
                outBuffer.pos = 0;
                incoming.pos = 0;
                opcode = -1;
                lastOpcode = -1;
                secondLastOpcode = -1;
                thirdLastOpcode = -1;
                packetSize = 0;
                timeoutCounter = 0;
                systemUpdateTime = 0;
                afkCountdown = 0;
                hintIconDrawType = 0;
                menuActionRow = 0;
                menuOpen = false;
                informationFile.write();
                super.idle = 0;
                for (int index = 0; index < 100; index++)
                    chatMessages[index] = null;
                item_highlighted = 0;
                widget_highlighted = 0;
                loading_phase = 0;
                trackCount = 0;
                set_camera_north();
                minimapState = ClientConstants.SHOW_MINIMAP;
                lastKnownPlane = -1;
                travel_destination_x = 0;
                travel_destination_y = 0;
                players_in_region = 0;
                npcs_in_region = 0;
                for (int index = 0; index < maxPlayers; index++) {
                    players[index] = null;
                    playerSynchronizationBuffers[index] = null;
                }
                for (int index = 0; index < maxNpcs; index++)
                    npcs[index] = null;
                local_player = players[LOCAL_PLAYER_INDEX] = new Player();
                projectiles.clear();
                incompleteAnimables.clear();
                clearRegionalSpawns();
                fullscreenInterfaceID = -1;
                friendServerStatus = 0;
                friendsCount = 0;
                dialogueId = -1;
                backDialogueId = -1;
                widget_overlay_id = -1;
                overlayInterfaceId = -1;
                openWalkableInterface = -1;
                continuedDialogue = false;
                sidebarId = 3;
                inputDialogState = 0;
                menuOpen = false;
                messagePromptRaised = false;
                clickToContinueString = null;
                multicombat = 0;
                flashingSidebarId = -1;
                characterGender = true;
                resetCharacterCreation();
                for (int index = 0; index < 5; index++)
                    characterDesignColours[index] = 0;
                for (int index = 0; index < 5; index++) {
                    playerOptions[index] = null;
                    playerOptionsHighPriority[index] = false;
                }
                anInt1175 = 0;
                anInt1134 = 0;
                anInt986 = 0;
                current_walking_queue_length = 0;
                anInt924 = 0;
                anInt1188 = 0;
                anInt1155 = 0;
                anInt1226 = 0;
                regenHealthStart = System.currentTimeMillis();
                regenSpecStart = System.currentTimeMillis();
                loginTime = System.currentTimeMillis();
                updateGame();
                resetImageProducers2();
                return;
            }

            if (response == 97) {
                firstLoginMessage = "That username is invalid.";
                secondLoginMessage = "Please try another username.";
                return;
            }

            if (response == 98) {
                firstLoginMessage = "The server is currently under maintenance.";
                secondLoginMessage = "Please try again later.";
                return;
            }

            if (response == 28) {
                firstLoginMessage = "Username or password contains illegal";
                secondLoginMessage = "characters. Try other combinations.";
                return;
            }
            if (response == 30) {
                // firstLoginMessage = "Old client usage detected.";
                // secondLoginMessage = "Please download the latest one.";
                firstLoginMessage = "An updated client is available. Please restart the";
                secondLoginMessage = "launcher";
                //Utils.launchURL(ClientConstants.DOWNLOAD_URL);
                return;
            }
            if (response == 3) {
                firstLoginMessage = "";
                secondLoginMessage = "Invalid username or password.";
                return;
            }
            if (response == 4) {
                firstLoginMessage = "Your account has been banned.";
                secondLoginMessage = "Please check the website for more details.";
                return;
            }
            if (response == 22) {
                firstLoginMessage = "Your computer has been banned.";
                secondLoginMessage = "";
                return;
            }
            if (response == 27) {
                firstLoginMessage = "Your host-address has been banned.";
                secondLoginMessage = "";
                return;
            }
            if (response == 5) {
                firstLoginMessage = "Your account is already logged in.";
                secondLoginMessage = "Try again in 60 seconds...";
                return;
            }
            if (response == 6) {
                firstLoginMessage = ClientConstants.CLIENT_NAME + " is being updated.";
                secondLoginMessage = "Try again in 60 seconds...";
                return;
            }
            if (response == 7) {
                firstLoginMessage = "The world is currently full.";
                secondLoginMessage = "";
                return;
            }
            if (response == 8) {
                firstLoginMessage = "Unable to connect.";
                secondLoginMessage = "Login server offline.";
                return;
            }
            if (response == 9) {
                firstLoginMessage = "Login limit exceeded.";
                secondLoginMessage = "Too many connections from your address.";
                return;
            }
            if (response == 10) {
                firstLoginMessage = "Unable to connect. Bad session id.";
                secondLoginMessage = "Try again in 60 secs...";
                return;
            }
            if (response == 11) {
                secondLoginMessage = "Login server rejected session.";
                secondLoginMessage = "Try again in 60 secs...";
                return;
            }
            if (response == 12) {
                firstLoginMessage = "You need a members account to login to this world.";
                secondLoginMessage = "Please subscribe, or use a different world.";
                return;
            }
            if (response == 13) {
                firstLoginMessage = "Could not complete login.";
                secondLoginMessage = "Please check the Discord server.";
                return;
            }
            if (response == 14) {
                firstLoginMessage = "The server is being updated.";
                secondLoginMessage = "Please wait 1 minute and try again.";
                return;
            }
            if (response == 15) {
                loggedIn = true;
                incoming.pos = 0;
                opcode = -1;
                lastOpcode = -1;
                secondLastOpcode = -1;
                thirdLastOpcode = -1;
                packetSize = 0;
                timeoutCounter = 0;
                systemUpdateTime = 0;
                menuActionRow = 0;
                menuOpen = false;
                loadingStartTime = System.currentTimeMillis();
                return;
            }
            if (response == 16) {
                firstLoginMessage = "Login attempts exceeded.";
                secondLoginMessage = "Please wait 1 minute and try again.";
                return;
            }
            if (response == 17) {
                firstLoginMessage = "You are standing in a members-only area.";
                secondLoginMessage = "To play on this world move to a free area first";
                return;
            }
            if (response == 20) {
                firstLoginMessage = "Invalid loginserver requested";
                secondLoginMessage = "Please try using a different world.";
                return;
            }
            if (response == 21) {
                for (int k1 = socketStream.read(); k1 >= 0; k1--) {
                    firstLoginMessage = "You have only just left another world";
                    secondLoginMessage = "Your profile will be transferred in: " + k1 + " seconds";
                    drawLoginScreen(true);
                    try {
                        Thread.sleep(1000L);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                login(name, password, reconnecting);
                return;
            }
            if (response == 22) {
                firstLoginMessage = "Your computer has been UUID banned.";
                secondLoginMessage = "Please appeal on the forums.";
                return;
            }
            if (response == -1) {
                if (copy == 0) {
                    if (loginFailures < 2) {
                        try {
                            Thread.sleep(2000L);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        loginFailures++;
                        login(name, password, reconnecting);
                        return;
                    } else {
                        firstLoginMessage = "No response from loginserver";
                        secondLoginMessage = "Please wait 1 minute and try again.";
                        return;
                    }
                } else {
                    firstLoginMessage = "No response from server";
                    secondLoginMessage = "Please try using a different world.";
                    return;
                }
            } else {
                firstLoginMessage = "Unexpected server response";
                secondLoginMessage = "Please try using a different world.";
                return;
            }
        } catch (IOException _ex) {
            firstLoginMessage = "";
        } catch (Exception e) {
            addReportToServer("Error while generating uid. Skipping step.");
            e.printStackTrace();
            addReportToServer(e.getMessage());
        }
        addReportToServer("Cannot connect to server, IP is " + ClientConstants.SERVER_ADDRESS + " with port " + ClientConstants.SERVER_PORT);
        secondLoginMessage = "Error connecting to server.";
    }

    private void clearRegionalSpawns() {
        for (int plane = 0; plane < 4; plane++) {
            for (int x = 0; x < 104; x++) {
                for (int y = 0; y < 104; y++) {
                    scene_items[plane][x][y] = null;
                }
            }
        }
        if (spawns == null) {
            spawns = new LinkedList();
        }
        for (SpawnedObject object = (SpawnedObject) spawns
            .first(); object != null; object = (SpawnedObject) spawns.next())
            object.getLongetivity = 0;
    }

    static class Tile {
        public int x, y, level;

        public Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public int distance(Tile other, Tile from) {
        int deltaX = other.x - from.x, deltaY = other.y - from.y;
        double dis = Math.sqrt(Math.pow(deltaX, 2D) + Math.pow(deltaY, 2D));
        if (dis > 1.0 && dis < 2)
            return 2;
        return (int) dis;
    }

    private boolean walk(int opcode, int obstruction_orientation, int obstruction_height, int obstruction_type, int local_y_path, int obstruction_width, int orientation_mask, int path_to_y_position, int local_x_path, boolean minimap_click, int path_to_x_position) {
        //logging.log(Level.INFO, String.format("Walking distance %s from %s,%s to %s,%s,%s%n", distance(new Tile(local_x_path, local_y_path), new Tile(path_to_x_position, path_to_y_position)), local_x_path, local_y_path, path_to_x_position, path_to_y_position, obstruction_height));
        try {
            byte region_x = 104;
            byte region_y = 104;
            for (int x = 0; x < region_x; x++) {
                for (int y = 0; y < region_y; y++) {
                    waypoints[x][y] = 0;
                    travel_distances[x][y] = 0x5f5e0ff;//99999999
                }
            }
            int x = local_x_path;
            int y = local_y_path;
            waypoints[local_x_path][local_y_path] = 99;
            travel_distances[local_x_path][local_y_path] = 0;
            int next_pos = 0;
            int current_pos = 0;
            walking_queue_x[next_pos] = local_x_path;
            walking_queue_y[next_pos++] = local_y_path;
            boolean reached = false;
            int path_length = walking_queue_x.length;
            int[][] adjacencies = collisionMaps[plane].adjacencies;
            while (current_pos != next_pos) {
                x = walking_queue_x[current_pos];
                y = walking_queue_y[current_pos];
                current_pos = (current_pos + 1) % path_length;
                if (x == path_to_x_position && y == path_to_y_position) {
                    reached = true;
                    break;
                }
                if (obstruction_type != 0) {
                    if ((obstruction_type < 5 || obstruction_type == 10) && collisionMaps[plane].obstruction_wall(path_to_x_position, x, y,
                        obstruction_orientation, obstruction_type - 1, path_to_y_position)) {
                        reached = true;
                        break;
                    }
                    if (obstruction_type < 10 && collisionMaps[plane].obstruction_decor(path_to_x_position, path_to_y_position, y, obstruction_type - 1, obstruction_orientation, x)) {
                        reached = true;
                        break;
                    }
                }
                if (obstruction_width != 0 && obstruction_height != 0 && collisionMaps[plane].obstruction(path_to_y_position, path_to_x_position, x, obstruction_height, orientation_mask, obstruction_width, y)) {
                    reached = true;
                    break;
                }
                int updated_distance = travel_distances[x][y] + 1;
                if (x > 0 && waypoints[x - 1][y] == 0
                    && (adjacencies[x - 1][y] & 0x1280108) == 0) {
                    walking_queue_x[next_pos] = x - 1;
                    walking_queue_y[next_pos] = y;
                    next_pos = (next_pos + 1) % path_length;
                    waypoints[x - 1][y] = 2;
                    travel_distances[x - 1][y] = updated_distance;
                }
                if (x < region_x - 1 && waypoints[x + 1][y] == 0 && (adjacencies[x + 1][y] & 0x1280180) == 0) {
                    walking_queue_x[next_pos] = x + 1;
                    walking_queue_y[next_pos] = y;
                    next_pos = (next_pos + 1) % path_length;
                    waypoints[x + 1][y] = 8;
                    travel_distances[x + 1][y] = updated_distance;
                }
                if (y > 0 && waypoints[x][y - 1] == 0 && (adjacencies[x][y - 1] & 0x1280102) == 0) {
                    walking_queue_x[next_pos] = x;
                    walking_queue_y[next_pos] = y - 1;
                    next_pos = (next_pos + 1) % path_length;
                    waypoints[x][y - 1] = 1;
                    travel_distances[x][y - 1] = updated_distance;
                }
                if (y < region_y - 1 && waypoints[x][y + 1] == 0 && (adjacencies[x][y + 1] & 0x1280120) == 0) {
                    walking_queue_x[next_pos] = x;
                    walking_queue_y[next_pos] = y + 1;
                    next_pos = (next_pos + 1) % path_length;
                    waypoints[x][y + 1] = 4;
                    travel_distances[x][y + 1] = updated_distance;
                }
                if (x > 0 && y > 0 && waypoints[x - 1][y - 1] == 0
                    && (adjacencies[x - 1][y - 1] & 0x128010e) == 0
                    && (adjacencies[x - 1][y] & 0x1280108) == 0
                    && (adjacencies[x][y - 1] & 0x1280102) == 0) {
                    walking_queue_x[next_pos] = x - 1;
                    walking_queue_y[next_pos] = y - 1;
                    next_pos = (next_pos + 1) % path_length;
                    waypoints[x - 1][y - 1] = 3;
                    travel_distances[x - 1][y - 1] = updated_distance;
                }
                if (x < region_x - 1 && y > 0 && waypoints[x + 1][y - 1] == 0
                    && (adjacencies[x + 1][y - 1] & 0x1280183) == 0
                    && (adjacencies[x + 1][y] & 0x1280180) == 0
                    && (adjacencies[x][y - 1] & 0x1280102) == 0) {
                    walking_queue_x[next_pos] = x + 1;
                    walking_queue_y[next_pos] = y - 1;
                    next_pos = (next_pos + 1) % path_length;
                    waypoints[x + 1][y - 1] = 9;
                    travel_distances[x + 1][y - 1] = updated_distance;
                }
                if (x > 0 && y < region_y - 1 && waypoints[x - 1][y + 1] == 0
                    && (adjacencies[x - 1][y + 1] & 0x1280138) == 0
                    && (adjacencies[x - 1][y] & 0x1280108) == 0
                    && (adjacencies[x][y + 1] & 0x1280120) == 0) {
                    walking_queue_x[next_pos] = x - 1;
                    walking_queue_y[next_pos] = y + 1;
                    next_pos = (next_pos + 1) % path_length;
                    waypoints[x - 1][y + 1] = 6;
                    travel_distances[x - 1][y + 1] = updated_distance;
                }
                if (x < region_x - 1 && y < region_y - 1 && waypoints[x + 1][y + 1] == 0
                    && (adjacencies[x + 1][y + 1] & 0x12801e0) == 0
                    && (adjacencies[x + 1][y] & 0x1280180) == 0
                    && (adjacencies[x][y + 1] & 0x1280120) == 0) {
                    walking_queue_x[next_pos] = x + 1;
                    walking_queue_y[next_pos] = y + 1;
                    next_pos = (next_pos + 1) % path_length;
                    waypoints[x + 1][y + 1] = 12;
                    travel_distances[x + 1][y + 1] = updated_distance;
                }
            }
            destination_mask = 0;
            if (!reached) {
                if (minimap_click) {
                    int steps = 100;
                    for (int deviation_offset = 1; deviation_offset < 2; deviation_offset++) {
                        for (int deviation_x = path_to_x_position - deviation_offset; deviation_x <= path_to_x_position + deviation_offset; deviation_x++) {
                            for (int deviation_y = path_to_y_position - deviation_offset; deviation_y <= path_to_y_position + deviation_offset; deviation_y++) {
                                if (deviation_x >= 0 && deviation_y >= 0 && deviation_x < 104 && deviation_y < 104 && travel_distances[deviation_x][deviation_y] < steps) {
                                    steps = travel_distances[deviation_x][deviation_y];
                                    x = deviation_x;
                                    y = deviation_y;
                                    destination_mask = 1;
                                    reached = true;
                                }
                            }
                        }
                        if (reached)
                            break;
                    }
                }
                if (!reached) {
                    return false;
                }
            }
            current_pos = 0;
            walking_queue_x[current_pos] = x;
            walking_queue_y[current_pos++] = y;
            int skip;
            for (int waypoint = skip = waypoints[x][y]; x != local_x_path
                || y != local_y_path; waypoint = waypoints[x][y]) {
                if (waypoint != skip) {
                    skip = waypoint;
                    walking_queue_x[current_pos] = x;
                    walking_queue_y[current_pos++] = y;
                }
                if ((waypoint & 2) != 0)
                    x++;
                else if ((waypoint & 8) != 0)
                    x--;
                if ((waypoint & 1) != 0)
                    y++;
                else if ((waypoint & 4) != 0)
                    y--;
            }
            if (current_pos > 0) {
                int max_path = current_pos;
                if (max_path > 25)
                    max_path = 25;
                current_pos--;
                int walking_x = walking_queue_x[current_pos];
                int walking_y = walking_queue_y[current_pos];
                current_walking_queue_length += max_path;
                if (current_walking_queue_length >= 92) {
                    /*
                     * Anti-cheatValidates, walking. Not used. OUTPUT_BUFFER.createFrame(36);
                     * OUTPUT_BUFFER.writeDWord(0);
                     */
                    current_walking_queue_length = 0;
                }
                if (opcode == 0) {
                    packetSender.getBuffer().writeOpcode(164);
                    packetSender.getBuffer().writeByte(max_path + max_path + 4);
                } else if (opcode == 1) {
                    packetSender.getBuffer().writeOpcode(248);
                    packetSender.getBuffer().writeByte(max_path + max_path + 4);
                } else if (opcode == 2) {
                    packetSender.getBuffer().writeOpcode(98);
                    packetSender.getBuffer().writeByte(max_path + max_path + 4);
                }
                packetSender.getBuffer().writeByte(plane);
                packetSender.getBuffer().writeLEShortA(walking_x + next_region_start);
                travel_destination_x = walking_queue_x[0];
                travel_destination_y = walking_queue_y[0];
                for (int step = 1; step < max_path; step++) {
                    current_pos--;
                    packetSender.getBuffer().writeByte(walking_queue_x[current_pos] - walking_x);
                    packetSender.getBuffer().writeByte(walking_queue_y[current_pos] - walking_y);
                }
                packetSender.getBuffer().writeLEShort(walking_y + next_region_end);
                packetSender.getBuffer().writeNegatedByte((super.key_status[5] != 1 ? 0 : 1));
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            addReportToServer(e.getMessage());
        }
        return opcode != 1;
    }

    private void npcUpdateMask(Buffer stream) {
        for (int j = 0; j < mobsAwaitingUpdateCount; j++) {
            int k = mobsAwaitingUpdate[j];
            Npc npc = npcs[k];
            int mask = stream.readUByte();
            if ((mask & 0x10) != 0) {
                int i1 = stream.readLEUShort();
                if (i1 == 65535)
                    i1 = -1;
                int i2 = stream.readUByte();
                if (i1 == npc.animation && i1 != -1) {
                    int l2 = Sequence.cache[i1].reset;
                    if (l2 == 1) {
                        npc.current_animation_frame = 0;
                        npc.current_animation_duration = 0;
                        npc.animation_delay = i2;
                        npc.animation_loops = 0;
                    }
                    if (l2 == 2)
                        npc.animation_loops = 0;
                } else if (i1 == -1 || npc.animation == -1
                    || Sequence.cache[i1].appended_frames >= Sequence.cache[npc.animation].appended_frames) {
                    npc.animation = i1;
                    npc.current_animation_frame = 0;
                    npc.current_animation_duration = 0;
                    npc.animation_delay = i2;
                    npc.animation_loops = 0;
                    npc.remaining_steps = npc.waypoint_index;
                }
            }
            if ((mask & 0x80) != 0) {
                npc.graphic_id = stream.readUShort();
                int k1 = stream.readInt();
                npc.graphic_height = k1 >> 16;
                npc.graphic_cycle = game_tick + (k1 & 0xffff);
                npc.current_animation_id = 0;
                npc.current_animation_time_remaining = 0;
                if (npc.graphic_cycle > game_tick)
                    npc.current_animation_id = -1;
                if (npc.graphic_id == 65535)
                    npc.graphic_id = -1;
            }
            if ((mask & 8) != 0) {
                int count = stream.readUByte();
                for (int i = 0; i < count; i++) {
                    int damage = stream.readShort();
                    int type = stream.readUByte();
                    int hp = stream.readShort();
                    int maxHp = stream.readShort();
                    npc.updateHitData(type, damage, game_tick);
                    npc.game_tick_status = game_tick + 300;
                    npc.current_hitpoints = hp;
                    npc.maximum_hitpoints = maxHp;
                }
            }
            if ((mask & 0x20) != 0) {
                npc.engaged_entity_id = stream.readUShort();
                if (npc.engaged_entity_id == 65535)
                    npc.engaged_entity_id = -1;
            }
            if ((mask & 1) != 0) {
                npc.entity_message = stream.readString();
                npc.message_cycle = 100;
            }
            /*if ((mask & 0x40) != 0) { // no longer used
                int damage = stream.readShort();
                int type = stream.readUByte();
                int hp = stream.readShort();
                int maxHp = stream.readShort();
                npc.updateHitData(type, damage, game_tick);
                npc.game_tick_status = game_tick + 300;
                npc.current_hitpoints = hp;
                npc.maximum_hitpoints = maxHp;
            }*/
            if ((mask & 0x2) != 0) {//Transform
                npc.desc = NpcDefinition.get(stream.readLEUShortA());
                if (npc.desc != null) {
                    String[] matchingNames = {"pure bot", "maxed bot", "f2p bot", "archer bot", "pure archer bot"};
                    if (Arrays.stream(matchingNames).anyMatch(npcName -> npc.desc.name.toLowerCase().contains(npcName))) {
                        npc.headIcon = stream.readUByte();
                        //System.out.println("client icon received: "+npc.headIcon);
                    }
                    npc.occupied_tiles = npc.desc.occupied_tiles;
                    npc.rotation = npc.desc.rotation;
                    npc.walk_animation_id = npc.desc.walkingAnimation;
                    npc.turn_around_animation_id = npc.desc.halfTurnAnimation;
                    npc.pivot_right_animation_id = npc.desc.quarterClockwiseTurnAnimation;
                    npc.pivot_left_animation_id = npc.desc.quarterAnticlockwiseTurnAnimation;
                    npc.idle_animation_id = npc.desc.standingAnimation;
                }
            }
            if ((mask & 4) != 0) {
                npc.faceX = stream.readLEUShort();
                npc.faceY = stream.readLEUShort();
            }
        }
    }

    private void buildAtNPCMenu(NpcDefinition npcDefinition, int npcIndex, int j, int npcArrayIndex) {
        if (widget_overlay_id == 16244) {
            return;
        }
        if (menuActionRow >= 400)
            return;
        if (npcDefinition.configs != null)
            npcDefinition = npcDefinition.get_configs();
        if (npcDefinition == null)
            return;
        if (!npcDefinition.isClickable)
            return;
        String name = npcDefinition.name;

        if (npcDefinition.cmb_level != 0)
            name = name + get_level_diff(local_player.combat_level, npcDefinition.cmb_level) + " (level-" + npcDefinition.cmb_level + ")";

        // If pet does not belong to me, then do not show pick up option or anything at all.
        if (npcDefinition.actions != null) {
            for (int l = 4; l >= 0; l--) {
                // Is not an attack option.
                if (npcDefinition.actions[l] != null) {
                    if (npcDefinition.actions[l].equalsIgnoreCase("Pick-up")) {
                        //System.out.println("npcPetId = "+npcPetId+" vs index: "+npcIndex);
                        if (npcPetId != npcIndex) {
                            return;
                        }
                    }
                }
            }
        }

        if (item_highlighted == 1) {
            menuActionText[menuActionRow] = "Use " + selectedItemName + " with <col=ffff00>" + name;
            menuActionTypes[menuActionRow] = 582;
            selectedMenuActions[menuActionRow] = npcIndex;
            firstMenuAction[menuActionRow] = npcArrayIndex;
            secondMenuAction[menuActionRow] = j;
            menuActionRow++;
            return;
        }

        if (widget_highlighted == 1) {
            if ((selectedTargetMask & 2) == 2) {
                // Stop the player from being able to use a spell on his own pet.
                if (npcPetId == npcArrayIndex) {
                    return;
                }

                menuActionText[menuActionRow] = selected_target_id + " <col=ffff00>" + name;
                menuActionTypes[menuActionRow] = 413;
                selectedMenuActions[menuActionRow] = npcIndex;
                firstMenuAction[menuActionRow] = npcArrayIndex;
                secondMenuAction[menuActionRow] = j;
                menuActionRow++;
            }
        } else {
            boolean isPet = false;
            if (npcDefinition.actions != null) {
                for (int l = 4; l >= 0; l--) {
                    // Is not an attack option.
                    if (npcDefinition.actions[l] != null && !npcDefinition.actions[l].equalsIgnoreCase("attack")) {
                        if (npcDefinition.actions[l].contains("Pick-up")) {
                            isPet = true;
                        }
                        char c = '\0';
                        if (isPet && setting.shift_pet_options) {
                            c = '\u07D0';
                        }
                        menuActionText[menuActionRow] = npcDefinition.actions[l] + " <col=ffff00>" + name;
                        if (l == 0)
                            menuActionTypes[menuActionRow] = 20 + c;
                        if (l == 1)
                            menuActionTypes[menuActionRow] = 412 + c;
                        if (l == 2)
                            menuActionTypes[menuActionRow] = 225 + c;
                        if (l == 3)
                            menuActionTypes[menuActionRow] = 965 + c;
                        if (l == 4)
                            menuActionTypes[menuActionRow] = 478 + c;
                        selectedMenuActions[menuActionRow] = npcIndex;
                        firstMenuAction[menuActionRow] = npcArrayIndex;
                        secondMenuAction[menuActionRow] = j;
                        menuActionRow++;
                    }
                }
            }
            if (npcDefinition.actions != null) {
                for (int i1 = 4; i1 >= 0; i1--) {
                    if (npcDefinition.actions[i1] != null && npcDefinition.actions[i1].equalsIgnoreCase("attack")) {
                        char c = '\0';
                        if (setting.npc_attack_priority == 0) {
                            if (npcDefinition.cmb_level > local_player.combat_level)
                                c = '\u07D0';
                        } else if (setting.npc_attack_priority == 1) {
                            c = '\u07D0';
                        } else if (setting.npc_attack_priority == 3) {
                            continue;
                        }
                        menuActionText[menuActionRow] = npcDefinition.actions[i1] + " <col=ffff00>" + name;
                        if (i1 == 0)
                            menuActionTypes[menuActionRow] = 20 + c;
                        if (i1 == 1)
                            menuActionTypes[menuActionRow] = 412 + c;
                        if (i1 == 2)
                            menuActionTypes[menuActionRow] = 225 + c;
                        if (i1 == 3)
                            menuActionTypes[menuActionRow] = 965 + c;
                        if (i1 == 4)
                            menuActionTypes[menuActionRow] = 478 + c;
                        selectedMenuActions[menuActionRow] = npcIndex;
                        firstMenuAction[menuActionRow] = npcArrayIndex;
                        secondMenuAction[menuActionRow] = j;
                        menuActionRow++;
                    }
                }
            }
            if (ClientConstants.DEBUG_MODE) {
                menuActionText[menuActionRow] = "Examine <col=ffff00>" + name + ", " + npcDefinition.interfaceType;
            } else {
                menuActionText[menuActionRow] = "Examine <col=ffff00>" + name;
            }
            menuActionTypes[menuActionRow] = 1025;
            selectedMenuActions[menuActionRow] = npcIndex;
            firstMenuAction[menuActionRow] = npcArrayIndex;
            secondMenuAction[menuActionRow] = j;
            menuActionRow++;
        }
    }

    private void buildAtPlayerMenu(int i, int j, Player player, int k) {
        if (widget_overlay_id == 16244) {
            return;
        }
        if (player == local_player)
            return;
        if (menuActionRow >= 400)
            return;
        String s;
        List<ChatCrown> crowns = ChatCrown.get(player.rights, player.donatorRights);
        StringBuilder crownPrefix = new StringBuilder();
        for (ChatCrown c : crowns) {
            crownPrefix.append("<img=").append(c.getSpriteId()).append(">");
        }

        //System.out.println(player.username+" "+player.title);
        if (player.skill_level == 0)
            s = player.getTitle(true) + crownPrefix + player.username + get_level_diff(local_player.combat_level, player.combat_level) + " (level-" + player.combat_level + ")";
        else
            s = player.getTitle(true) + crownPrefix + player.username + " (skill-" + player.skill_level + ")";
        if (item_highlighted == 1) {
            menuActionText[menuActionRow] = "Use " + selectedItemName + " with <col=FFFFFF>" + s;
            menuActionTypes[menuActionRow] = 491;
            selectedMenuActions[menuActionRow] = j;
            firstMenuAction[menuActionRow] = i;
            secondMenuAction[menuActionRow] = k;
            menuActionRow++;
        } else if (widget_highlighted == 1) {
            if ((selectedTargetMask & 8) == 8) {
                menuActionText[menuActionRow] = selected_target_id + " <col=FFFFFF>" + s;
                menuActionTypes[menuActionRow] = 365;
                selectedMenuActions[menuActionRow] = j;
                firstMenuAction[menuActionRow] = i;
                secondMenuAction[menuActionRow] = k;
                menuActionRow++;
            }
        } else {
            for (int type = 4; type >= 0; type--) {
                if (playerOptions[type] != null) {
                    menuActionText[menuActionRow] = playerOptions[type] + " <col=FFFFFF>" + s;
                    char c = '\0';
                    if (playerOptions[type].equalsIgnoreCase("attack")) {

                        if (setting.player_attack_priority == 0) {
                            if (player.combat_level > local_player.combat_level)
                                c = '\u07D0';
                        } else if (setting.player_attack_priority == 1) {
                            c = '\u07D0';
                        } else if (setting.player_attack_priority == 3) {
                            continue;
                        }

                        boolean clanMember = false;

                        for (String clan : clanList) {
                            if (clan == null) {
                                continue;
                            }
                            if (!clan.equalsIgnoreCase(player.username)) {
                                continue;
                            }
                            clanMember = true;
                            break;
                        }

                        if (local_player.team_id != 0 && player.team_id != 0)
                            if (local_player.team_id == player.team_id) {
                                c = '\u07D0';
                            } else {
                                c = '\0';
                            }

                        if (clanMember) {
                            c = '\u07D0';
                        }
                    } else if (playerOptionsHighPriority[type])
                        c = '\u07D0';
                    if (type == 0) {
                        menuActionTypes[menuActionRow] = 561 + c;
                    }
                    if (type == 1) {
                        menuActionTypes[menuActionRow] = 779 + c;
                    }
                    if (type == 2) {
                        menuActionTypes[menuActionRow] = 27 + c;
                    }
                    if (type == 3) {
                        menuActionTypes[menuActionRow] = 577 + c;
                    }
                    if (type == 4) {
                        menuActionTypes[menuActionRow] = 729 + c;
                    }
                    selectedMenuActions[menuActionRow] = j;
                    firstMenuAction[menuActionRow] = i;
                    secondMenuAction[menuActionRow] = k;
                    menuActionRow++;
                }
            }
        }
        for (int row = 0; row < menuActionRow; row++) {
            if (menuActionTypes[row] == 519) {
                menuActionText[row] = "Walk here <col=FFFFFF>" + s;
                return;
            }
        }
    }

    private void handleTemporaryObjects(SpawnedObject spawnedObject) {
        long id = 0L;
        int key = -1;
        int type = 0;
        int orientation = 0;
        if (spawnedObject.group == 0)
            id = scene.get_wall_uid(spawnedObject.plane, spawnedObject.x, spawnedObject.y);
        if (spawnedObject.group == 1)
            id = scene.get_wall_decor_uid(spawnedObject.plane, spawnedObject.x, spawnedObject.y);
        if (spawnedObject.group == 2)
            id = scene.get_interactive_object_uid(spawnedObject.plane, spawnedObject.x, spawnedObject.y);
        if (spawnedObject.group == 3)
            id = scene.get_ground_decor_uid(spawnedObject.plane, spawnedObject.x, spawnedObject.y);
        if (id != 0L) {
            key = get_object_key(id);
            type = get_object_type(id);
            orientation = get_object_orientation(id);
        }
        spawnedObject.getPreviousId = key;
        spawnedObject.previousType = type;
        spawnedObject.previousOrientation = orientation;
    }

    public static int[] removeDuplicates(int[] arr) {
        int end = arr.length;

        for (int i = 0; i < end; i++) {
            for (int j = i + 1; j < end; j++) {
                if (arr[i] == arr[j]) {
                    arr[j] = arr[end - 1];
                    end--;
                    j--;
                }
            }
        }

        int[] whitelist = new int[end];
        System.arraycopy(arr, 0, whitelist, 0, end);
        return whitelist;
    }

    public static long ping;

    private void setStartupActions() {
        try {
            long starting_up_start_time = System.currentTimeMillis();
            HoverMenuManager.init();
            if (SignLink.cache_dat != null) {
                for (int index = 0; index < 5; index++) {
                    indices[index] = new FileStore(SignLink.cache_dat, SignLink.indices[index], index + 1);
                }
            }

//            if (ClientConstants.production) {
//                CacheDownloader.init(false);
//            }

            SpecialBarSpriteLoader.loadSprites();
            specialBarSprite = SpecialBarSpriteLoader.getSprites();
            try {
                spriteCache.init(Paths.get(SignLink.findCacheDir(), ClientConstants.SPRITE_FILE_NAME + ".dat").toFile(), Paths.get(SignLink.findCacheDir(), ClientConstants.SPRITE_FILE_NAME + ".idx").toFile());
            } catch (IOException e) {
                e.printStackTrace();
            }

            new Thread(() -> Client.addressMac = Client.singleton.getMACAddress()).start();

            title_archive = request_archive(1, "title screen", "title", 25);
            adv_font_small = new AdvancedFont(false, "p11_full", title_archive);
            adv_font_regular = new AdvancedFont(false, "p12_full", title_archive);
            adv_font_bold = new AdvancedFont(false, "b12_full", title_archive);
            adv_font_fancy = new AdvancedFont(true, "q8_full", title_archive);

            drawLogo();
            loadTitleScreen();

            tileFlags = new byte[4][104][104];
            tileHeights = new int[4][105][105];
            scene = new SceneGraph(tileHeights);

            for (int index = 0; index < 4; index++) {
                collisionMaps[index] = new CollisionMap();
            }

            minimapImage = new SimpleImage(512, 512);

            long starting_up_time_elapsed = System.currentTimeMillis() - starting_up_start_time;
            if (ClientConstants.DISPLAY_CLIENT_LOAD_TIME_VERBOSE) {
                System.out.println("It took " + starting_up_time_elapsed + " ms to start up the client.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateServer() {
        try {
            long connecting_to_update_server_start_time = System.currentTimeMillis();

            Archive update_server = request_archive(5, "update list", "versionlist", 60);
            resourceProvider = new ResourceProvider();
            resourceProvider.initialize(update_server, this);
            Model.method459(resourceProvider.getModelCount(), resourceProvider);

            long connecting_to_update_server_start_time_elapsed = System.currentTimeMillis() - connecting_to_update_server_start_time;
            if (ClientConstants.DISPLAY_CLIENT_LOAD_TIME_VERBOSE) {
                System.out.println("It took " + connecting_to_update_server_start_time_elapsed + " ms to connect with the update server.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unpackingMedia() {
        try {
            long unpacking_media_start_time = System.currentTimeMillis();

            Archive media_archive = request_archive(4, "2d graphics", "media", 40);
          //  backgroundFix = spriteCache.get(1847);
//            accountManager = new AccountManager(this, spriteCache.get(1850));
//            accountManager.loadAccounts();
            saveButton = spriteCache.get(1851);

            if (ClientConstants.repackIndexOne) {
                CacheUtils.repackCacheIndex(this, Store.MODEL);
            }

            if (ClientConstants.repackIndexTwo) {
                CacheUtils.repackCacheIndex(this, Store.ANIMATION);
            }

            if (ClientConstants.repackIndexThree) {
                CacheUtils.repackCacheIndex(this, Store.MUSIC);
            }

            if (ClientConstants.repackIndexFour) {
                CacheUtils.repackCacheIndex(this, Store.MAP);
            }

            for (int imageId = 82, index = 0; index < SkillConstants.SKILL_COUNT; imageId++, index++) {
                skill_sprites[index] = spriteCache.get(imageId);
            }

            for (int index = 0; index < fadingScreenImages.length; index++) {
                fadingScreenImages[index] = new SimpleImage("fadingscreen/" + (index + 1));
            }

            try {
                for (int index = 0; index < 14; index++) {
                    hitMarks[index] = spriteCache.get(index + 507);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Sprites from the media cache
            multiOverlay = new SimpleImage(media_archive, "overlay_multiway", 0);
            mapBack = new IndexedImage(media_archive, "mapback", 0);
            compass = new SimpleImage(media_archive, "compass", 0);
            mapFlag = new SimpleImage(media_archive, "mapmarker", 0);
            mapMarker = new SimpleImage(media_archive, "mapmarker", 1);
            mapDotItem = new SimpleImage(media_archive, "mapdots", 0);
            mapDotNPC = new SimpleImage(media_archive, "mapdots", 1);
            mapDotPlayer = new SimpleImage(media_archive, "mapdots", 2);
            mapDotFriend = new SimpleImage(media_archive, "mapdots", 3);
            mapDotTeam = new SimpleImage(media_archive, "mapdots", 4);
            mapDotClan = new SimpleImage(media_archive, "mapdots", 5);
            scrollBar1 = new SimpleImage(media_archive, "scrollbar", 0);
            scrollBar2 = new SimpleImage(media_archive, "scrollbar", 1);

            top508 = spriteCache.get(759 + 54);
            bottom508 = spriteCache.get(759 + 55);

            try {
                for (int index = 0; index <= 14; index++) {
                    sideIcons[index] = new SimpleImage(media_archive, "sideicons", index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                byte[] mapsceneData = Files.readAllBytes(Paths.get(SignLink.findCacheDir() + "mapscene.dat"));
                mapScenes = Arrays.stream(new SpriteLoader().load(317, mapsceneData)).map(IndexedImage::new).toArray(IndexedImage[]::new);
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("Loaded " + mapScenes.length + " map scenes loading OSRS version " + ClientConstants.OSRS_DATA_VERSION + " and SUB version " + ClientConstants.OSRS_DATA_SUB_VERSION);

            try {
                for (int index = 0; index < 124; index++) {
                    mapFunctions[index] = new SimpleImage(media_archive, "mapfunction", index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Loaded " + mapFunctions.length + " map functions loading OSRS version " + ClientConstants.OSRS_DATA_VERSION + " and SUB version " + ClientConstants.OSRS_DATA_SUB_VERSION);

            try {
                for (int index = 0; index < 6; index++) {
                    headIconsHint[index] = new SimpleImage(media_archive, "headicons_hint", index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                for (int index = 0; index < 18; index++) {
                    headIcons[index] = new SimpleImage(media_archive, "headicons_prayer", index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                for (int index = 0; index < 5; index++) {
                    skullIcons[index] = new SimpleImage(media_archive, "headicons_pk", index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                int i = 0;
                autoBackgroundSprites[i++] = new SimpleImage(media_archive, "tradebacking", 0);
                for (int j = 0; j < 4; j++)
                    autoBackgroundSprites[i++] = new SimpleImage(media_archive, "steelborder", j);
                for (int j = 0; j < 2; j++)
                    autoBackgroundSprites[i++] = new SimpleImage(media_archive, "steelborder2", j);
                for (int j = 2; j < 4; j++)
                    autoBackgroundSprites[i++] = new SimpleImage(media_archive, "miscgraphics", j);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                for (int index = 0; index < 8; index++) {
                    crosses[index] = new SimpleImage(media_archive, "cross", index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            SimpleImage sprite = new SimpleImage(media_archive, "screenframe", 0);
            leftFrame = new ProducingGraphicsBuffer(sprite.width, sprite.height);
            sprite.draw_inverse(0, 0);
            sprite = new SimpleImage(media_archive, "screenframe", 1);
            topFrame = new ProducingGraphicsBuffer(sprite.width, sprite.height);
            sprite.draw_inverse(0, 0);
            int i5 = (int) (Math.random() * 21D) - 10;
            int j5 = (int) (Math.random() * 21D) - 10;
            int k5 = (int) (Math.random() * 21D) - 10;
            int l5 = (int) (Math.random() * 41D) - 20;
            for (int index = 0; index < 119; index++) {
                if (mapFunctions[index] != null) {
                    mapFunctions[index].blend(i5 + l5, j5 + l5, k5 + l5);
                }
            }
            long unpacking_media_time_elapsed = System.currentTimeMillis() - unpacking_media_start_time;
            if (ClientConstants.DISPLAY_CLIENT_LOAD_TIME_VERBOSE) {
                System.out.println("It took " + unpacking_media_time_elapsed + " ms to unpack the media.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unpackTextures() {
        try {
            long unpacking_textures_start_time = System.currentTimeMillis();

            Archive texture_archive = request_archive(6, "textures", "textures", 45);

            Rasterizer3D.init(texture_archive);
            Rasterizer3D.adjust_brightness(0.80000000000000004D);
            Rasterizer3D.reset_textures();

            long unpacking_textures_time_elapsed = System.currentTimeMillis() - unpacking_textures_start_time;
            if (ClientConstants.DISPLAY_CLIENT_LOAD_TIME_VERBOSE) {
                System.out.println("It took " + unpacking_textures_time_elapsed + " ms to unpack the textures.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unpackConfigs() {
        try {
            long unpacking_configs_start_time = System.currentTimeMillis();

            Archive config_archive = request_archive(2, "config", "config", 30);

            Sequence.init(config_archive);
            ObjectDefinition.init(config_archive);
            FloDefinition.init(config_archive);
            NpcDefinition.init(config_archive);
            AreaDefinition.init(config_archive);
            IdentityKit.init(config_archive);
            SpotAnimation.init(config_archive);
            VariableParameter.init(config_archive);
            VariableBits.init(config_archive);
            ItemDefinition.init(config_archive);
            ItemDefinition.membership_required = isMembers;
            long unpacking_configs_time_elapsed = System.currentTimeMillis() - unpacking_configs_start_time;
            if (ClientConstants.DISPLAY_CLIENT_LOAD_TIME_VERBOSE) {
                System.out.println("It took " + unpacking_configs_time_elapsed + " ms to unpack the configs.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unpackInterfaces() {
        try {
            long unpacking_interfaces_start_time = System.currentTimeMillis();

            Archive interface_archive = request_archive(3, "interface", "interface", 35);
            Archive media_archive = request_archive(4, "2d graphics", "media", 40);

            AdvancedFont fonts[] = {adv_font_small, adv_font_regular, adv_font_bold, adv_font_fancy};
            Widget.load(interface_archive, fonts, media_archive);
            fixed = Widget.cache[OptionTabWidget.FIXED_MODE];
            resizable = Widget.cache[OptionTabWidget.RESIZABLE_MODE];

            long unpacking_interfaces_time_elapsed = System.currentTimeMillis() - unpacking_interfaces_start_time;
            if (ClientConstants.DISPLAY_CLIENT_LOAD_TIME_VERBOSE) {
                System.out.println("It took " + unpacking_interfaces_time_elapsed + " ms to unpack the interfaces.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void prepareGameEngine() throws IOException {
        long preparing_game_engine_start_time = System.currentTimeMillis();

        Archive worden_archive = request_archive(7, "chat system", "wordenc", 50);

        request_archive(8, "sound effects", "sounds", 70);

        Archive sound_archive = request_archive(8, "sound effects", "sounds", 80);

        byte[] sound_data = sound_archive.get("sounds.dat");

        Buffer song_buffer = new Buffer(sound_data);
        Track.init(song_buffer);

        for (int j6 = 0; j6 < 33; j6++) {
            int k6 = 999;
            int i7 = 0;
            for (int k7 = 0; k7 < 34; k7++) {
                if (mapBack.palettePixels[k7 + j6 * mapBack.width] == 0) {
                    if (k6 == 999)
                        k6 = k7;
                    continue;
                }
                if (k6 == 999)
                    continue;
                i7 = k7;
                break;
            }
            anIntArray968[j6] = k6;
            anIntArray1057[j6] = i7 - k6;
        }
        for (int l6 = 1; l6 < 153; l6++) {
            int j7 = 999;
            int l7 = 0;
            for (int j8 = 24; j8 < 177; j8++) {
                if (mapBack.palettePixels[j8 + l6 * mapBack.width] == 0 && (j8 > 34 || l6 > 34)) {
                    if (j7 == 999) {
                        j7 = j8;
                    }
                    continue;
                }
                if (j7 == 999) {
                    continue;
                }
                l7 = j8;
                break;
            }
            minimapLeft[l6 - 1] = j7 - 24;
            minimapLineWidth[l6 - 1] = l7 - j7;
        }
        updateGame();
        MessageCensor.load(worden_archive);
        //mouseDetection = new MouseDetection(this);
        //startRunnable(mouseDetection, 10);
        NpcDefinition.clientInstance = this;
        setting.load();
        setting.update();
        setting.toggleVarbits();
        informationFile.read();
        if (informationFile.isUsernameRemembered()) {
            myUsername = informationFile.getStoredUsername();
            myPassword = informationFile.getStoredPassword();
        }
        OptionTabWidget.updateSettings();
        Keybinding.updateInterface();
        Save.load();
        loadImageDescriptions();

        if (setting.ground_snow) {
            //System.out.println("Ground Snow has been enabled, reloading floors.");
            toggleSnow();
        }

        // resourceProvider.writeAll();

        spriteCache.clear(); // this clears all the sprites that the interfaces loaded, this is to only load
        // sprites if the interface is used

        long game_engine_elapsed = System.currentTimeMillis() - preparing_game_engine_start_time;
        if (ClientConstants.DISPLAY_CLIENT_LOAD_TIME_VERBOSE) {
            System.out.println("It took " + game_engine_elapsed + " ms, to prepare the game engine.");
        }
    }

    void startUp() {
        System.out.println("Loading " + ClientConstants.CLIENT_NAME + " " + ((ClientConstants.PVP_MODE) ? "in PVP mode " : "in economy mode ") + "on port " + ClientConstants.SERVER_PORT + ".");

        try {
            long clientLoadStart = System.currentTimeMillis();
            draw_loadup(20, "Starting up");
            setStartupActions();
            draw_loadup(60, "Connecting to update server");
            updateServer();
            draw_loadup(80, "Unpacking media");
            unpackingMedia();
            draw_loadup(83, "Unpacking textures");
            unpackTextures();
            draw_loadup(86, "Unpacking config");
            unpackConfigs();
            draw_loadup(95, "Unpacking interfaces");
            unpackInterfaces();
            draw_loadup(100, "Preparing game engine");
            prepareGameEngine();
            //The map function for 30191 ladder is -1 for some reason.
            //System.out.println(ObjectDefinition.get(30191).mapIcon);
            long clientLoadEnd = System.currentTimeMillis();
            long clientLoadDifference = clientLoadEnd - clientLoadStart;
            if (ClientConstants.DISPLAY_CLIENT_LOAD_TIME) {
                System.out.println("It took " + clientLoadDifference + " ms to load the client.");
            }

            return;
        } catch (Exception exception) {
            exception.printStackTrace();
            addReportToServer(exception.getMessage());
        }
    }

    private Widget fixed;
    private Widget resizable;

    private final void adjustWhenOnResizeable() {
        fixed.disabledSprite = Client.spriteCache.get(619);
        fixed.enabledAltSprite = Client.spriteCache.get(595);
        fixed.enabledSprite = Client.spriteCache.get(596);
        fixed.disabledAltSprite = Client.spriteCache.get(595);

        resizable.disabledSprite = Client.spriteCache.get(618);
        resizable.enabledSprite = Client.spriteCache.get(618);
        resizable.enabledAltSprite = Client.spriteCache.get(597);
        resizable.disabledAltSprite = Client.spriteCache.get(598);

        fixed.active = false;
        resizable.active = true;
    }

    private final void adjustWhenOnFixed() {
        fixed.disabledSprite = Client.spriteCache.get(595);
        fixed.enabledAltSprite = Client.spriteCache.get(619);
        fixed.enabledSprite = Client.spriteCache.get(595);
        fixed.disabledAltSprite = Client.spriteCache.get(596);

        resizable.disabledSprite = Client.spriteCache.get(597);
        resizable.enabledSprite = Client.spriteCache.get(598);
        resizable.enabledAltSprite = Client.spriteCache.get(618);
        resizable.disabledAltSprite = Client.spriteCache.get(618);

        fixed.active = true;
        resizable.active = false;

    }

    private void updatePlayerList(Buffer stream, int packetSize) {
        while (stream.bitPosition + 10 < packetSize * 8) {
            int index = stream.readBits(11);
            if (index == 2047) {
                break;
            }
            if (players[index] == null) {
                players[index] = new Player();
                if (playerSynchronizationBuffers[index] != null) {
                    players[index].update(playerSynchronizationBuffers[index]);
                }
            }
            local_players[players_in_region++] = index;
            Player player = players[index];
            player.time = game_tick;

            int update = stream.readBits(1);

            if (update == 1)
                mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = index;

            int discardWalkingQueue = stream.readBits(1);

            int y = stream.readBits(5);

            if (y > 15) {
                y -= 32;
            }

            int x = stream.readBits(5);

            if (x > 15) {
                x -= 32;
            }

            player.setPos(local_player.waypoint_x[0] + x, local_player.waypoint_y[0] + y, discardWalkingQueue == 1);
        }
        stream.disableBitAccess();
    }

    public boolean circle_clip(int x, int y, int click_x, int click_y, int radius) {
        return java.lang.Math.pow((x + radius - click_x), 2)
            + java.lang.Math.pow((y + radius - click_y), 2) < java.lang.Math.pow(radius, 2);
    }

    private void processMainScreenClick() {
        if (widget_overlay_id == 16244) {
            // TODO: fix this welcome screen for resize
            if (super.click_type == 1 && super.click_x >= 267 && super.click_x <= 500 && super.click_y >= 300
                && super.click_y <= 393) {
                clearTopInterfaces();
            }
            return;
        }
        if (minimapState != ClientConstants.SHOW_MINIMAP) {
            return;
        }

        if (super.click_type == 1) {
            resetInputFieldFocus();
            int screen_click_x = super.click_x - 25 - 547;
            int screen_click_y = super.click_y - 5 - 3;
            if (screen != ScreenMode.FIXED) {
                screen_click_x = super.click_x - (window_width - 182 + 24);
                screen_click_y = super.click_y - 8;
            }
            if (circle_clip(0, 0, screen_click_x, screen_click_y, 76) && mouseMapPosition() && !runHover) {
                screen_click_x -= 73;
                screen_click_y -= 75;
                int k = camera_pan + map_rotation & 0x7ff;
                int i1 = Rasterizer3D.SINE[k];
                int j1 = Rasterizer3D.COSINE[k];
                i1 = i1 * (map_zoom + 256) >> 8;
                j1 = j1 * (map_zoom + 256) >> 8;
                int k1 = screen_click_y * i1 + screen_click_x * j1 >> 11;
                int l1 = screen_click_y * j1 - screen_click_x * i1 >> 11;
                int i2 = local_player.world_x + k1 >> 7;
                int j2 = local_player.world_y - l1 >> 7;
                if ((myPrivilege >= 2 && myPrivilege <= 4) && isShiftPressed
                    && ClientConstants.SHIFT_CLICK_TELEPORT) {
                    teleport(next_region_start + i2, next_region_end + j2, plane);
                } else {
                    boolean accessible = walk(1, 0, 0, 0, local_player.waypoint_y[0], 0, 0, j2,
                        local_player.waypoint_x[0], true, i2);
                    if (accessible) {

                        /*
                         * outgoing.writeByte(i); outgoing.writeByte(j);
                         * outgoing.writeShort(cameraHorizontal); outgoing.writeByte(57);
                         * outgoing.writeByte(minimapRotation); outgoing.writeByte(minimapZoom);
                         * outgoing.writeByte(89); outgoing.writeShort(localPlayer.x);
                         * outgoing.writeShort(localPlayer.y); outgoing.writeByte(anInt1264);
                         * outgoing.writeByte(63);
                         */
                    }
                }
            }
            anInt1117++;
            if (anInt1117 > 1151) {
                anInt1117 = 0;
                // anti-cheat
                /*
                 * outgoing.writeOpcode(246); outgoing.writeByte(0); int bufPos =
                 * outgoing.currentPosition;
                 *
                 * if ((int) (Math.random() * 2D) == 0) { outgoing.writeByte(101); }
                 *
                 * outgoing.writeByte(197); outgoing.writeShort((int) (Math.random() * 65536D));
                 * outgoing.writeByte((int) (Math.random() * 256D)); outgoing.writeByte(67);
                 * outgoing.writeShort(14214);
                 *
                 * if ((int) (Math.random() * 2D) == 0) { outgoing.writeShort(29487); }
                 *
                 * outgoing.writeShort((int) (Math.random() * 65536D));
                 *
                 * if ((int) (Math.random() * 2D) == 0) { outgoing.writeByte(220); }
                 *
                 * outgoing.writeByte(180); outgoing.writeBytes(outgoing.currentPosition -
                 * bufPos);
                 */
            }
        }
    }

    private String interfaceIntToString(int j) {
        if (j < 0x3b9ac9ff)
            return String.valueOf(format.format(j));
        else
            return "*";
    }

    private void showErrorScreen() {
        Graphics g = getGameComponent().getGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, 765, 503);
        method4(1);
        if (loadingError) {
            update_flame_components = false;
            g.setFont(new Font("Helvetica", 1, 16));
            g.setColor(Color.yellow);
            int k = 35;
            g.drawString("Sorry, an error has occured whilst loading " + ClientConstants.CLIENT_NAME, 30, k);
            k += 50;
            g.setColor(Color.white);
            g.drawString("To fix this try the following (in order):", 30, k);
            k += 50;
            g.setColor(Color.white);
            g.setFont(new Font("Helvetica", 1, 12));
            g.drawString("1: Try closing ALL open web-browser windows, and reloading", 30, k);
            k += 30;
            g.drawString("2: Try clearing your web-browsers cache from tools->internet options", 30, k);
            k += 30;
            g.drawString("3: Try using a different game-world", 30, k);
            k += 30;
            g.drawString("4: Try rebooting your computer", 30, k);
            k += 30;
            g.drawString("5: Try selecting a different version of Java from the play-game menu", 30, k);
        }
        if (genericLoadingError) {
            update_flame_components = false;
            g.setFont(new Font("Helvetica", 1, 20));
            g.setColor(Color.white);
            g.drawString("Error - unable to load game!", 50, 50);
            g.drawString("To play " + ClientConstants.CLIENT_NAME + " make sure you play from", 50, 100);
            g.drawString("www." + ClientConstants.CLIENT_NAME + ".com", 50, 150);
        }
        if (rsAlreadyLoaded) {
            update_flame_components = false;
            g.setColor(Color.yellow);
            int l = 35;
            g.drawString("Error a copy of " + ClientConstants.CLIENT_NAME + " already appears to be loaded", 30, l);
            l += 50;
            g.setColor(Color.white);
            g.drawString("To fix this try the following (in order):", 30, l);
            l += 50;
            g.setColor(Color.white);
            g.setFont(new Font("Helvetica", 1, 12));
            g.drawString("1: Try closing ALL open web-browser windows, and reloading", 30, l);
            l += 30;
            g.drawString("2: Try rebooting your computer, and reloading", 30, l);
            l += 30;
        }
    }

    private void forceNPCUpdateBlock() {
        for (int j = 0; j < npcs_in_region; j++) {
            int k = local_npcs[j];
            Npc npc = npcs[k];
            if (npc != null)
                entityUpdateBlock(npc);
        }
    }

    private void entityUpdateBlock(Entity entity) {
        if (entity.world_x < 128 || entity.world_y < 128 || entity.world_x >= 13184 || entity.world_y >= 13184) {
            entity.animation = -1;
            entity.graphic_id = -1;
            entity.initiate_movement = 0;
            entity.cease_movement = 0;
            entity.world_x = entity.waypoint_x[0] * 128 + entity.occupied_tiles * 64;
            entity.world_y = entity.waypoint_y[0] * 128 + entity.occupied_tiles * 64;
            entity.resetPath();
        }
        if (entity == local_player
            && (entity.world_x < 1536 || entity.world_y < 1536 || entity.world_x >= 11776 || entity.world_y >= 11776)) {
            entity.animation = -1;
            entity.graphic_id = -1;
            entity.initiate_movement = 0;
            entity.cease_movement = 0;
            entity.world_x = entity.waypoint_x[0] * 128 + entity.occupied_tiles * 64;
            entity.world_y = entity.waypoint_y[0] * 128 + entity.occupied_tiles * 64;
            entity.resetPath();
        }
        if (entity.initiate_movement > game_tick) {
            entity.refreshEntityPosition();
        } else if (entity.cease_movement >= game_tick) {
            entity.refreshEntityFaceDirection();
        } else {
            entity.getDegreesToTurn();
        }
        appendFocusDestination(entity);
        appendEmote(entity);
    }

    public void appendEmote(Entity entity) {
        try {
            if (entity.graphic_id > SpotAnimation.cache.length)
                entity.graphic_id = -1;

            entity.dynamic = false;

            if (entity.queued_animation_id > Sequence.cache.length)
                entity.queued_animation_id = -1;

            if (entity.queued_animation_id != -1) {
                if (entity.queued_animation_id > Sequence.cache.length)
                    entity.queued_animation_id = 0;

                Sequence animation = Sequence.cache[entity.queued_animation_id];
                entity.queued_animation_duration++;
                if (animation == null)
                    return;

                if (entity.queued_animation_frame < animation.frames && entity.queued_animation_duration > animation.get_length(entity.queued_animation_frame)) {
                    entity.queued_animation_duration = 1;
                    entity.queued_animation_frame++;
                    entity.next_idle_frame++;
                }

                entity.next_idle_frame = entity.queued_animation_frame + 1;
                if (entity.next_idle_frame >= animation.frames) {
                    if (entity.next_idle_frame >= animation.frames)
                        entity.next_idle_frame = 0;
                }

                if (entity.queued_animation_frame >= animation.frames) {
                    entity.queued_animation_duration = 1;
                    entity.queued_animation_frame = 0;
                }
            }
            if (entity.graphic_id != -1 && game_tick >= entity.graphic_cycle) {
                if (entity.current_animation_id < 0)
                    entity.current_animation_id = 0;

                //System.out.println("gfx: " + entity.gfxId + " anim: " + SpotAnimation.cache[entity.gfxId].animationId);
                Sequence animation_1 = SpotAnimation.cache[entity.graphic_id].seq;
                if (animation_1 == null) {
                    System.out.println("Spotanim seq == null");
                    return;
                }
                //System.out.println("length: " + animation_1.get_length(entity.currentAnimation) + " frames: " + animation_1.frames);

                for (entity.current_animation_time_remaining++; entity.current_animation_id < animation_1.frames && entity.current_animation_time_remaining > animation_1.get_length(entity.current_animation_id); entity.current_animation_id++)
                    entity.current_animation_time_remaining -= animation_1.get_length(entity.current_animation_id);

                if (entity.current_animation_id >= animation_1.frames && (entity.current_animation_id < 0 || entity.current_animation_id >= animation_1.frames))
                    entity.graphic_id = -1;

                entity.next_graphic_frame = entity.current_animation_id + 1;
                if (entity.next_graphic_frame >= animation_1.frames) {
                    if (entity.next_graphic_frame < 0 || entity.next_graphic_frame >= animation_1.frames)
                        entity.graphic_id = -1;
                }
            }
            if (entity.animation != -1 && entity.animation_delay <= 1) {
                if (entity.animation >= Sequence.cache.length) {
                    entity.animation = -1;
                }
                Sequence animation_2 = Sequence.cache[entity.animation];
                if (animation_2.tempo == 1 && entity.remaining_steps > 0 && entity.initiate_movement <= game_tick && entity.cease_movement < game_tick) {
                    entity.animation_delay = 1;
                    return;
                }
            }
            if (entity.animation != -1 && entity.animation_delay == 0) {
                Sequence animation_3 = Sequence.cache[entity.animation];
                for (entity.current_animation_duration++; entity.current_animation_frame < animation_3.frames && entity.current_animation_duration > animation_3.get_length(entity.current_animation_frame); entity.current_animation_frame++)
                    entity.current_animation_duration -= animation_3.get_length(entity.current_animation_frame);

                if (entity.current_animation_frame >= animation_3.frames) {
                    entity.current_animation_frame -= animation_3.step;
                    entity.animation_loops++;
                    if (entity.animation_loops >= animation_3.loops)
                        entity.animation = -1;
                    if (entity.current_animation_frame < 0 || entity.current_animation_frame >= animation_3.frames)
                        entity.animation = -1;
                }
                entity.next_animation_frame = entity.current_animation_frame + 1;
                if (entity.next_animation_frame >= animation_3.frames) {
                    if (entity.animation_loops >= animation_3.loops)
                        entity.next_animation_frame = entity.current_animation_frame + 1;
                    if (entity.next_animation_frame < 0 || entity.next_animation_frame >= animation_3.frames)
                        entity.next_animation_frame = entity.current_animation_frame;
                }
                entity.dynamic = animation_3.stretch;
            }
            if (entity.animation_delay > 0)
                entity.animation_delay--;

        } catch (Exception e) {
            e.printStackTrace();
            addReportToServer(e.getMessage());
        }
    }

    private void appendFocusDestination(Entity entity) {
        if (entity.rotation == 0)
            return;
        if (entity.engaged_entity_id != -1 && entity.engaged_entity_id < 32768
            && entity.engaged_entity_id < npcs.length) {
            Npc npc = npcs[entity.engaged_entity_id];
            if (npc != null) {
                int i1 = entity.world_x - npc.world_x;
                int k1 = entity.world_y - npc.world_y;
                if (i1 != 0 || k1 != 0)
                    entity.turn_direction = (int) (Math.atan2(i1, k1) * 325.94900000000001D) & 0x7ff;
            }
        }
        if (entity.engaged_entity_id >= 32768) {
            int j = entity.engaged_entity_id - 32768;
            if (j == localPlayerIndex) {
                j = LOCAL_PLAYER_INDEX;
            }
            Player player = players[j];
            if (player != null) {
                int l1 = entity.world_x - player.world_x;
                int i2 = entity.world_y - player.world_y;
                if (l1 != 0 || i2 != 0) {
                    entity.turn_direction = (int) (Math.atan2(l1, i2) * 325.94900000000001D) & 0x7ff;
                }
            }
        }
        if ((entity.faceX != 0 || entity.faceY != 0) && (entity.waypoint_index == 0 || entity.step_tracker > 0)) {
            int k = entity.world_x - (entity.faceX - next_region_start - next_region_start) * 64;
            int j1 = entity.world_y - (entity.faceY - next_region_end - next_region_end) * 64;
            if (k != 0 || j1 != 0)
                entity.turn_direction = (int) (Math.atan2(k, j1) * 325.94900000000001D) & 0x7ff;
            entity.faceX = 0;
            entity.faceY = 0;
        }
        int l = entity.turn_direction - entity.current_rotation & 0x7ff;
        if (l != 0) {
            if (l < entity.rotation || l > 2048 - entity.rotation)
                entity.current_rotation = entity.turn_direction;
            else if (l > 1024)
                entity.current_rotation -= entity.rotation;
            else
                entity.current_rotation += entity.rotation;
            entity.current_rotation &= 0x7ff;
            if (entity.queued_animation_id == entity.standing_turn_animation_id
                && entity.current_rotation != entity.turn_direction) {
                if (entity.walk_animation_id != -1) {
                    entity.queued_animation_id = entity.walk_animation_id;
                    return;
                }
                entity.queued_animation_id = entity.walk_animation_id;
            }
        }
    }

    private void drawGameScreen() {
        if (fullscreenInterfaceID != -1 && (loading_phase == 2 || super.fullGameScreen != null)) {
            if (loading_phase == 2) {
                try {
                    processWidgetAnimations(animation_step, fullscreenInterfaceID);
                    if (widget_overlay_id != -1) {
                        processWidgetAnimations(animation_step, widget_overlay_id);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    addReportToServer(ex.getMessage());
                }
                animation_step = 0;
                resetAllImageProducers();
                super.fullGameScreen.init();
                Rasterizer3D.line_offsets = fullscreen_texture_raster;
                Rasterizer2D.clear();
                update_producers = true;
                if (widget_overlay_id != -1) {
                    Widget rsInterface_1 = Widget.cache[widget_overlay_id];
                    if (rsInterface_1.width == 512 && rsInterface_1.height == 334 && rsInterface_1.type == 0) {
                        rsInterface_1.width = 765;
                        rsInterface_1.height = 503;
                    }
                    try {
                        drawInterface(rsInterface_1, 0, 8, 0);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        addReportToServer(ex.getMessage());
                    }
                }
                Widget rsInterface = Widget.cache[fullscreenInterfaceID];
                if (rsInterface.width == 512 && rsInterface.height == 334 && rsInterface.type == 0) {
                    rsInterface.width = 765;
                    rsInterface.height = 503;
                }
                try {
                    drawInterface(rsInterface, 0, 8, 0);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    addReportToServer(ex.getMessage());
                }
                if (!menuOpen) {
                    processRightClick();
                    drawTooltip();
                } else {
                    drawMenu(screen == ScreenMode.FIXED ? 4 : 0, screen == ScreenMode.FIXED ? 4 : 0);
                }
            }
            drawCount++;

            super.fullGameScreen.drawGraphics(0, super.graphics, 0);
            return;
        } else {
            if (drawCount != 0) {
                resetImageProducers2();
            }
        }
        //System.out.println("Canvas size " + super.fullGameScreen.canvasWidth  + " by " + super.fullGameScreen.canvasHeight);
        if (update_producers) {
            update_producers = false;
            if (screen == ScreenMode.FIXED) {
                topFrame.drawGraphics(0, super.graphics, 0);
                leftFrame.drawGraphics(4, super.graphics, 0);
            }
            update_chat_producer = true;
            update_tab_producer = true;
            if (loading_phase != 2) {
                if (screen == ScreenMode.FIXED) {
                    if (gameScreenImageProducer != null) {
                        gameScreenImageProducer.drawGraphics(screen == ScreenMode.FIXED ? 4 : 0, super.graphics, screen == ScreenMode.FIXED ? 4 : 0);
                    }
                    if (minimapImageProducer != null) {
                        minimapImageProducer.drawGraphics(0, super.graphics, 516);
                    }
                }
            }
        }
        if (overlayInterfaceId != -1) {
            try {
                processWidgetAnimations(animation_step, overlayInterfaceId);
            } catch (Exception ex) {
                ex.printStackTrace();
                addReportToServer(ex.getMessage());
            }
        }
        drawTabArea();
        if (backDialogueId == -1) {
            aClass9_1059.scrollPosition = chatScrollHeight - chatScrollAmount - 110;
            if (super.cursor_x >= 496 && super.cursor_x <= 511
                && super.cursor_y > (screen == ScreenMode.FIXED ? 345 : window_height - 158))
                handleScroll(494, 110, super.cursor_x,
                    super.cursor_y - (screen == ScreenMode.FIXED ? 345 : window_height - 158), aClass9_1059, 0,
                    chatScrollHeight);
            int i = chatScrollHeight - 110 - aClass9_1059.scrollPosition;
            if (i < 0) {
                i = 0;
            }
            if (i > chatScrollHeight - 110) {
                i = chatScrollHeight - 110;
            }
            if (chatScrollAmount != i) {
                chatScrollAmount = i;
                update_chat_producer = true;
            }
        }
        if (backDialogueId != -1) {
            boolean flag2 = false;

            try {
                flag2 = processWidgetAnimations(animation_step, backDialogueId);
            } catch (Exception ex) {
                ex.printStackTrace();
                addReportToServer(ex.getMessage());
            }

            if (flag2) {
                update_chat_producer = true;
            }
        }
        if (atInventoryInterfaceType == 3)
            update_chat_producer = true;
        if (activeInterfaceType == 3)
            update_chat_producer = true;
        if (clickToContinueString != null)
            update_chat_producer = true;
        if (menuOpen && menuScreenArea == 2)
            update_chat_producer = true;
        if (update_chat_producer) {
            drawChatArea();
            update_chat_producer = false;
        }
        if (loading_phase == 2)
            render();
        if (loading_phase == 2) {
            if (screen == ScreenMode.FIXED) {
                drawMinimap();
                minimapImageProducer.drawGraphics(0, super.graphics, 516);
            }
        }
        if (flashingSidebarId != -1)
            update_tab_producer = true;
        if (update_tab_producer) {
            if (flashingSidebarId != -1 && flashingSidebarId == sidebarId) {
                flashingSidebarId = -1;
                // flashing sidebar
                /*
                 * outgoing.writeOpcode(120); outgoing.writeByte(tabId);
                 */
            }
            update_tab_producer = false;
            chatSettingImageProducer.init();
            gameScreenImageProducer.init();
        }
        animation_step = 0;
    }

    private boolean buildFriendsListMenu(Widget widget) {
        int type = widget.contentType;
        if (type >= 1 && type <= 200 || type >= 701 && type <= 900) {
            if (type >= 801)
                type -= 701;
            else if (type >= 701)
                type -= 601;
            else if (type >= 101)
                type -= 101;
            else
                type--;
            menuActionText[menuActionRow] = "Remove <col=FFFFFF>" + friendsList[type];
            menuActionTypes[menuActionRow] = 792;
            menuActionRow++;
            menuActionText[menuActionRow] = "Message <col=FFFFFF>" + friendsList[type];
            menuActionTypes[menuActionRow] = 639;
            menuActionRow++;
            return true;
        }
        if (type >= 401 && type <= 500) {
            menuActionText[menuActionRow] = "Remove <col=FFFFFF>" + widget.defaultText;
            menuActionTypes[menuActionRow] = 322;
            menuActionRow++;
            return true;
        }

        if (type == 902) {
            menuActionText[menuActionRow] = "Choose " + widget.defaultText;
            menuActionTypes[menuActionRow] = 169;
            menuActionRow++;
            return true;
        } else {
            return false;
        }
    }

    private void render_stationary_graphics() {
        StaticObject class30_sub2_sub4_sub3 = (StaticObject) incompleteAnimables.first();
        for (; class30_sub2_sub4_sub3 != null; class30_sub2_sub4_sub3 = (StaticObject) incompleteAnimables
            .next())
            if (class30_sub2_sub4_sub3.z != plane || class30_sub2_sub4_sub3.expired)
                class30_sub2_sub4_sub3.remove();
            else if (game_tick >= class30_sub2_sub4_sub3.cycle) {
                class30_sub2_sub4_sub3.step(animation_step);
                if (class30_sub2_sub4_sub3.expired)
                    class30_sub2_sub4_sub3.remove();
                else
                    scene.add_entity(class30_sub2_sub4_sub3.z, 0, class30_sub2_sub4_sub3.height, -1,
                        class30_sub2_sub4_sub3.y, 60, class30_sub2_sub4_sub3.x,
                        class30_sub2_sub4_sub3, false);
            }

    }

    public void drawBlackBox(int xPos, int yPos) {
        Rasterizer2D.draw_filled_rect(xPos - 2, yPos - 1, 1, 71, 0x726451);
        Rasterizer2D.draw_filled_rect(xPos + 174, yPos, 1, 69, 0x726451);
        Rasterizer2D.draw_filled_rect(xPos - 2, yPos - 2, 178, 1, 0x726451);
        Rasterizer2D.draw_filled_rect(xPos, yPos + 68, 174, 1, 0x726451);
        Rasterizer2D.draw_filled_rect(xPos - 1, yPos - 1, 1, 71, 0x2E2B23);
        Rasterizer2D.draw_filled_rect(xPos + 175, yPos - 1, 1, 71, 0x2E2B23);
        Rasterizer2D.draw_filled_rect(xPos, yPos - 1, 175, 1, 0x2E2B23);
        Rasterizer2D.draw_filled_rect(xPos, yPos + 69, 175, 1, 0x2E2B23);
        Rasterizer2D.draw_filled_rect(xPos, yPos, 174, 68, 0, 220);
    }

    private SimpleImage top508;
    private SimpleImage bottom508;

    public void draw508Scrollbar(int height, int pos, int y, int x, int maxScroll, boolean transparent) {
        if (transparent) {
            drawTransparentScrollBar(x, y, height, maxScroll, pos);
        } else {

            top508.drawSprite(x, y);
            bottom508.drawSprite(x, (y + height) - 16);
            Rasterizer2D.draw_filled_rect(x, y + 16, 16, height - 32, 0x746241);
            Rasterizer2D.draw_filled_rect(x, y + 16, 15, height - 32, 0x77603e);
            Rasterizer2D.draw_filled_rect(x, y + 16, 14, height - 32, 0x77603e);
            Rasterizer2D.draw_filled_rect(x, y + 16, 13, height - 32, 0x95784a);
            Rasterizer2D.draw_filled_rect(x, y + 16, 12, height - 32, 0x997c52);
            Rasterizer2D.draw_filled_rect(x, y + 16, 11, height - 32, 0x9e8155);
            Rasterizer2D.draw_filled_rect(x, y + 16, 10, height - 32, 0xa48558);
            Rasterizer2D.draw_filled_rect(x, y + 16, 8, height - 32, 0xaa8b5c);
            Rasterizer2D.draw_filled_rect(x, y + 16, 6, height - 32, 0xb09060);
            Rasterizer2D.draw_filled_rect(x, y + 16, 3, height - 32, 0x866c44);
            Rasterizer2D.draw_filled_rect(x, y + 16, 1, height - 32, 0x7c6945);

            int k1 = ((height - 32) * height) / maxScroll;
            if (k1 < 8) {
                k1 = 8;
            }
            int l1 = ((height - 32 - k1) * pos) / (maxScroll - height);
            int l2 = ((height - 32 - k1) * pos) / (maxScroll - height) + 6;
            Rasterizer2D.draw_vertical_line(x + 1, y + 16 + l1, k1, 0x5c492d);
            Rasterizer2D.draw_vertical_line(x + 14, y + 16 + l1, k1, 0x5c492d);
            Rasterizer2D.draw_horizontal_line(x + 1, y + 16 + l1, 14, 0x5c492d);
            Rasterizer2D.draw_horizontal_line(x + 1, y + 15 + l1 + k1, 14, 0x5c492d);
            Rasterizer2D.draw_horizontal_line(x + 4, y + 18 + l1, 8, 0x664f2b);
            Rasterizer2D.draw_horizontal_line(x + 4, y + 13 + l1 + k1, 8, 0x664f2b);
            Rasterizer2D.draw_horizontal_line(x + 3, y + 19 + l1, 2, 0x664f2b);
            Rasterizer2D.draw_horizontal_line(x + 11, y + 19 + l1, 2, 0x664f2b);
            Rasterizer2D.draw_horizontal_line(x + 3, y + 12 + l1 + k1, 2, 0x664f2b);
            Rasterizer2D.draw_horizontal_line(x + 11, y + 12 + l1 + k1, 2, 0x664f2b);
            Rasterizer2D.draw_horizontal_line(x + 3, y + 14 + l1 + k1, 11, 0x866c44);
            Rasterizer2D.draw_horizontal_line(x + 3, y + 17 + l1, 11, 0x866c44);
            Rasterizer2D.draw_vertical_line(x + 13, y + 12 + l2, k1 - 4, 0x866c44);
            Rasterizer2D.draw_vertical_line(x + 3, y + 13 + l2, k1 - 6, 0x664f2b);
            Rasterizer2D.draw_vertical_line(x + 12, y + 13 + l2, k1 - 6, 0x664f2b);
            Rasterizer2D.draw_horizontal_line(x + 2, y + 18 + l1, 2, 0x866c44);
            Rasterizer2D.draw_horizontal_line(x + 2, y + 13 + l1 + k1, 2, 0x866c44);
            Rasterizer2D.draw_horizontal_line(x + 12, y + 18 + l1, 1, 0x866c44);
            Rasterizer2D.draw_horizontal_line(x + 12, y + 13 + l1 + k1, 1, 0x866c44);
        }
    }

    private int interfaceDrawY;

    private void drawProgressBar(int xPos, int yPos, int width, int height, int currentPercent, int firstColor, int secondColor, int strokeWidth) {
        Rasterizer2D.draw_filled_rect(xPos, yPos, width, height, firstColor, 30);
        Rasterizer2D.draw_filled_rect(xPos, yPos, (int) (width * (currentPercent / 100.0f)), height, secondColor, 100);
        Rasterizer2D.drawStroke(xPos - strokeWidth, yPos, width + strokeWidth, height, 0x000000, strokeWidth);
    }

    private static final int NORMAL_SUB_SPELLBOOK = 938;
    private static final int ANCIENT_SUB_SPELLBOOK = 838;
    private static final int LUNAR_SPELLBOOK = 29999;

    private boolean isMagicBook() {
        return tabInterfaceIDs[sidebarId] != NORMAL_SUB_SPELLBOOK && tabInterfaceIDs[sidebarId] != ANCIENT_SUB_SPELLBOOK && tabInterfaceIDs[sidebarId] != LUNAR_SPELLBOOK;
    }

    private void drawInterface(Widget widget, int x, int y, int scroll_y) {
        if (widget == null)
            return;

        if (widget.type != 0 || widget.children == null)
            return;

        if (isMagicBook() && widget.invisible)
            return;
        if (widget.invisible && focusedViewportWidget != widget.id && focusedSidebarWidget != widget.id
            && focusedChatWidget != widget.id || widget.drawingDisabled) {
            return;
        }

        drawSpecialAttack(widget);

        if (widget.parent == 193) {
            widget.width = 765;
            widget.height = 503;
            x = 455;
            y = 285;
        }

        if (widget.id == 72000) {
            if (ClientConstants.SPAWN_TAB_DISPLAY_ALL_ITEMS_PRELOADED) {
                SpawnTabAllItems.processSpawnTab();
            } else {
                SpawnTab.processSpawnTab();
            }
        }

        int clipLeft = Rasterizer2D.clip_left;
        int clipTop = Rasterizer2D.clip_top;
        int clipRight = Rasterizer2D.clip_right;
        int clipBottom = Rasterizer2D.clip_bottom;
        Rasterizer2D.set_clip(x, y, x + widget.width, y + widget.height);
        int childCount = widget.children.length;
        for (int childId = 0; childId < childCount; childId++) {
            int child_x_in_bounds = widget.child_x[childId] + x;
            int child_y_in_bounds = (widget.child_y[childId] + y) - scroll_y;
            Widget child = Widget.cache[widget.children[childId]];
            if (child == null) {
                continue;
            }
            if (child.drawingDisabled || (isMagicBook() && child.invisible) && focusedViewportWidget != child.id && focusedSidebarWidget != child.id) {
                continue;
            }
            child_x_in_bounds += child.x;
            child_y_in_bounds += child.y;
            if (child.defaultText != null) {
                //System.out.println("Child defaultText is " + child.defaultText);
            }
            if (child.contentType > 0)
                handle_widget_support(child);

            if (child.type == 287 && !child.invisible) {
                drawProgressBar(child_x_in_bounds, child_y_in_bounds, child.width, child.height, child.currentPercent, 0xFF0000, 0x008000, 1);
            }

            if (child.type == Widget.TYPE_CONTAINER) {
                if (child.scrollPosition > child.scrollMax - child.height)
                    child.scrollPosition = child.scrollMax - child.height;
                if (child.scrollPosition < 0)
                    child.scrollPosition = 0;
                drawInterface(child, child_x_in_bounds, child_y_in_bounds, child.scrollPosition);
                if (child.scrollMax > child.height) {
                    if (child.id == 36350 || child.newScroller) {
                        draw508Scrollbar(child.height, child.scrollPosition, child_y_in_bounds, child_x_in_bounds + child.width, child.scrollMax, false);
                    } else {
                        drawScrollbar(child.height, child.scrollPosition, child_y_in_bounds, child_x_in_bounds + child.width, child.scrollMax,
                            false);
                    }
                }
            } else if (child.type != 1)
                if (child.type == Widget.TYPE_INVENTORY) {
                    // sendMessage("draw "+child+" "+child.type+" "+child.id, 0, "");
                    boolean isFixed = screen == ScreenMode.FIXED;
                    int slot = 0;
                    int newSlot = 0;
                    int tabAm = 0;
                    int tabSlot = -1;
                    int hh = 2;
                    int results = -1;
                    boolean search = searchingBank && !promptInput.isEmpty() && child.id != 5064; // exclude inventory searching
                    if (child.contentType == 206) {
                        int tabHeight = 0;
                        for (int i = 0; i < tabAmounts.length; i++) {
                            if (tabSlot + 1 < tabAmounts.length && tabAmounts[tabSlot + 1] > 0) {
                                tabAm += tabAmounts[++tabSlot];
                                tabHeight += (tabAmounts[tabSlot] / child.width) + (tabAmounts[tabSlot] % child.width == 0 ? 0 : 1);
                                if (tabSlot + 1 < tabAmounts.length && tabAmounts[tabSlot + 1] > 0 && settings[211] == 0 && !search) {
                                    Rasterizer2D.draw_horizontal_line(child_x_in_bounds, (child_y_in_bounds + tabHeight * (32 + child.inventoryMarginY) + hh) - 1, ((32 + child.inventoryMarginX) * child.width) - 10, 0x3F3528);
                                    Rasterizer2D.draw_horizontal_line(child_x_in_bounds, (child_y_in_bounds + tabHeight * (32 + child.inventoryMarginY) + hh), ((32 + child.inventoryMarginX) * child.width) - 10, 0x3F3528);
                                }
                                hh += 8;
                            }

                            if (i > 0) {
                                int itemSlot = tabAm - tabAmounts[i];
                                if (itemSlot == 816) {
                                    itemSlot--;
                                }
                                int xOffset = (window_width - 237 - Widget.cache[26000].width) / 2;
                                int yOffset = 36 + ((window_height - 503) / 2);
                                int x2 = xOffset + 77;
                                int y2 = yOffset + 25;
                                try {
                                    int item = Widget.cache[5382].inventoryItemId[itemSlot];
                                    if (tabAmounts[i] > 0 && item > 0) {
                                        SimpleImage icon = null;
                                        int amount = child.inventoryAmounts[itemSlot];

                                        if (settings[750] == 1) {
                                            icon = ItemSpriteFactory.get_item_sprite(item - 1, amount, 0);
                                        } else if (settings[751] == 1) {
                                            icon = spriteCache.get(219 + i);
                                        } else if (settings[752] == 1) {
                                            icon = spriteCache.get(210 + i);
                                        }

                                        if (icon != null) {
                                            if (settings[750] == 1 && amount == 0) {
                                                icon.drawSprite1((isFixed ? 59 : x2 + 3) + 40 * i,
                                                    (isFixed ? 41 : y2 + 2), 110, true);
                                            } else {
                                                icon.drawSprite1((isFixed ? 59 : x2 + 3) + 40 * i,
                                                    (isFixed ? 41 : y2 + 2), 255, true);
                                            }
                                        }

                                        Widget.cache[26031 + i * 4].y = 0;
                                        Widget.cache[26032 + i * 4].y = 0;
                                        Widget.cache[26032 + i * 4].tooltip = "View tab <col=ff7000>" + i;
                                        Widget.cache[26032 + i * 4].enabledSprite = spriteCache.get(110);
                                    } else if (tabAmounts[i - 1] <= 0) {
                                        Widget.cache[26031 + i * 4].y = -500;
                                        if (i > 1) {
                                            Widget.cache[26032 + i * 4].y = -500;
                                        } else {
                                            spriteCache.get(210).drawSprite1((isFixed ? 59 : x2) + 40 * i,
                                                (isFixed ? 41 : y2), 255, true);
                                        }
                                        Widget.cache[26032 + i * 4].tooltip = "New tab";
                                    } else {
                                        Widget.cache[26031 + i * 4].y = -500;
                                        Widget.cache[26032 + i * 4].y = 0;
                                        Widget.cache[26032 + i * 4].tooltip = "New tab";
                                        Widget.cache[26032 + i * 4].enabledSprite = spriteCache.get(110);
                                        spriteCache.get(210).drawSprite1((isFixed ? 59 : x2) + 40 * i,
                                            (isFixed ? 41 : y2), 255, true);
                                    }
                                } catch (Exception e) {
                                    addReportToServer("Bank tab icon error: tab [" + i + "], amount [" + tabAm + "], tabAmount [" + tabAmounts[i] + "], itemSlot [" + itemSlot + "]");
                                    e.printStackTrace();
                                    addReportToServer(e.getMessage());
                                }
                            }
                        }

                        Rasterizer2D.clip_bottom += 3;

                        tabAm = tabAmounts[0];
                        tabSlot = 0;
                        hh = 0;

                        newSlot = 0;
                        int tabH = 0;
                        if (settings[211] != 0) {
                            for (int i = 0; i < tabAmounts.length; i++) {
                                if (i == settings[211]) {
                                    tabH = (int) Math.ceil(tabAmounts[i] / 9.0);
                                    break;
                                }
                                newSlot += tabAmounts[i];
                            }
                            slot = newSlot;
                            Widget.cache[5385].scrollMax = tabH * 45; //This used to be 42, but it's probably better as a multiple of 9 so is now 45.
                        } else {
                            int totalTabs = 0;
                            for (int i = 0; i < tabAmounts.length; i++) {
                                if (tabAmounts[i] > 0) {
                                    totalTabs = i;
                                    tabH += (int) Math.ceil(tabAmounts[i] / 9.0); //This used to be 42, but it's probably better as a multiple of 9 so is now 45.
                                }
                            }

                            Widget.cache[5385].scrollMax = tabH * 45 + (totalTabs * 10);
                        }
                    }

                    int dragX = 0, dragY = 0;
                    SimpleImage draggedItem = null;

                    heightLoop:
                    for (int height = 0; height < child.height; height++) {
                        for (int width = 0; width < child.width; width++) {
                            if (child.contentType == 206 && !search) {
                                if (settings[211] == 0) {
                                    if (slot == tabAm) {
                                        if (tabSlot + 1 < tabAmounts.length) {
                                            tabAm += tabAmounts[++tabSlot];
                                            if (tabSlot > 0 && tabAmounts[tabSlot - 1] % child.width == 0) {
                                                height--;
                                            }
                                            hh += 8;
                                        }
                                        break;
                                    }
                                } else if (settings[211] <= 9) {
                                    if (slot >= tabAmounts[settings[211]] + newSlot) {
                                        break heightLoop;
                                    }
                                }
                            }
                            if (slot >= child.inventoryItemId.length) {
                                continue;
                            }
                            if (search && child.inventoryItemId[slot] > 0) {
                                final ItemDefinition definition = ItemDefinition.get(child.inventoryItemId[slot] - 1);
                                if (definition == null || definition.name == null || !definition.name.toLowerCase().contains(promptInput.toLowerCase())) {
                                    slot++;
                                    continue;
                                }
                                results++;
                            }

                            int w = child_x_in_bounds + (search ? (results % child.width) : width) * (32 + child.inventoryMarginX);
                            int h = (child_y_in_bounds + ((search ? (results / child.width) : height)) * (32 + child.inventoryMarginY)) + hh;
                            if (slot < 20) {
                                w += child.inventoryOffsetX[slot];
                                h += child.inventoryOffsetY[slot];
                            }

                            if (slot < child.inventoryItemId.length && child.inventoryItemId[slot] > 0) {
                                int x2 = 0;
                                int y2 = 0;
                                int itemId = child.inventoryItemId[slot] - 1;
                                //System.out.println("itemId: "+itemId);
                                if (w > Rasterizer2D.clip_left - 32 && w < Rasterizer2D.clip_right && h > Rasterizer2D.clip_top - 32 && h < Rasterizer2D.clip_bottom || activeInterfaceType != 0 && dragFromSlot == slot) {
                                    int color = 0;
                                    if (item_highlighted == 1 && selectedItemIdSlot == slot
                                        && interfaceitemSelectionTypeIn == child.id) {
                                        color = 0xFFFFFF;
                                    }
                                    SimpleImage itemSprite = ItemSpriteFactory.get_item_sprite(itemId, child.inventoryAmounts[slot], color);
                                    //Draw item sprites for NPC drops interface.
                                    if (child.id == 15100) {
                                        itemSprite = ItemSpriteFactory.get_sized_item_sprite(itemId, child.inventoryAmounts[slot], color, 24, 24, false);
                                    }
                                    if (itemSprite != null) {
                                        if (activeInterfaceType != 0 && dragFromSlot == slot
                                            && focusedDragWidget == child.id) {
                                            draggedItem = itemSprite;
                                            x2 = super.cursor_x - mouseDragX;
                                            y2 = super.cursor_y - mouseDragY;
                                            if (x2 < 5 && x2 > -5)
                                                x2 = 0;
                                            if (y2 < 5 && y2 > -5)
                                                y2 = 0;
                                            if (draggingCycles < setting.drag_item_value) {
                                                x2 = 0;
                                                y2 = 0;
                                            }
                                            dragX = w + x2;
                                            if (dragX < Rasterizer2D.clip_left) {
                                                dragX = Rasterizer2D.clip_left - (x2);
                                                if (x2 < Rasterizer2D.clip_left)
                                                    dragX = Rasterizer2D.clip_left;
                                            }
                                            if (dragX > Rasterizer2D.clip_right - 32) {
                                                dragX = Rasterizer2D.clip_right - 32;
                                            }

                                            dragY = h + y2;
                                            if (dragY < Rasterizer2D.clip_top && widget.scrollMax == 0) {
                                                dragY = Rasterizer2D.clip_top - (y2);
                                                if (y2 < Rasterizer2D.clip_top)
                                                    dragY = Rasterizer2D.clip_top;
                                            }
                                            if (dragY > Rasterizer2D.clip_bottom - 32)
                                                dragY = Rasterizer2D.clip_bottom - 32;

                                            if (h + y2 < Rasterizer2D.clip_top && widget.scrollPosition > 0) {
                                                int scrollValue = (animation_step * (Rasterizer2D.clip_top - h - y2)) / 3;
                                                if (scrollValue > animation_step * 10)
                                                    scrollValue = animation_step * 10;
                                                if (scrollValue > widget.scrollPosition)
                                                    scrollValue = widget.scrollPosition;
                                                widget.scrollPosition -= scrollValue;
                                                mouseDragY += scrollValue;
                                            }
                                            if (h + y2 + 32 > Rasterizer2D.clip_bottom
                                                && widget.scrollPosition < widget.scrollMax - widget.height) {
                                                int scrollValue = (animation_step
                                                    * ((h + y2 + 32) - Rasterizer2D.clip_bottom)) / 3;
                                                if (scrollValue > animation_step * 10) {
                                                    scrollValue = animation_step * 10;
                                                }
                                                if (scrollValue > widget.scrollMax - widget.height
                                                    - widget.scrollPosition) {
                                                    scrollValue = widget.scrollMax - widget.height
                                                        - widget.scrollPosition;
                                                }
                                                widget.scrollPosition += scrollValue;
                                                mouseDragY -= scrollValue;
                                            }
                                        } else if (atInventoryInterfaceType != 0 && atInventoryIndex == slot
                                            && atInventoryInterface == child.id) {
                                            itemSprite.drawSprite1(w, h);
                                        } else {
                                            if (child.alpha > 0) {
                                                if (child.id == 61026) {
                                                    if (child.inventoryAmounts[slot] == 0) {
                                                        itemSprite.draw_transparent(w, h, 90);
                                                    } else {
                                                        itemSprite.drawSprite(w, h);
                                                    }
                                                } else if (child.id == 73318) {
                                                    if (child.inventoryAmounts[slot] == 0) {
                                                        itemSprite.draw_transparent(w, h, 90);
                                                    } else {
                                                        itemSprite.drawSprite(w, h);
                                                    }
                                                } else {
                                                    itemSprite.draw_transparent(w, h, 180);
                                                }
                                            } else {
                                                RuneType runeType = RuneType.forId(itemId);
                                                int amount = child.inventoryAmounts[slot];
                                                if (child.parent == 5382 && amount == 0) {
                                                    itemSprite.draw_transparent(w, h, 110);
                                                } else if (child.parent == 3824 && amount == 0) {
                                                    itemSprite.draw_transparent(w, h, 110);
                                                } else if (widget_overlay_id == 48700 && runeType == null) {
                                                    itemSprite.draw_transparent(w, h, 110);
                                                } else if (child.parent == 26806 && (itemId == ItemIdentifiers.LOOTING_BAG || itemId == ItemIdentifiers.LOOTING_BAG_22586)) {
                                                    itemSprite.draw_transparent(w, h, 110);
                                                } else {
                                                    itemSprite.drawSprite(w, h);
                                                }
                                            }
                                        }

                                        int amount = child.inventoryAmounts[slot];

                                        if (itemSprite.max_width == 33 || amount != 1) {
                                            final boolean showAmount = child.displayAmount;
                                            boolean flag = true;

                                            if (flag) {
                                                if (showAmount) {
                                                    // item container drawing
                                                    if (child.parent == 5382 && amount == 0) {
                                                        adv_font_small.draw("0", w + 1 + x2, h + 10 + y2, 0xFFE100, -1);
                                                    } else if (child.displayAmount) {
                                                        if (amount >= 1500000000 && child.drawInfinity) {
                                                            spriteCache.get(105).drawSprite(w, h);
                                                        } else {
                                                            if (amount >= -1 && amount < 1 && child.parent != 54301) {
                                                                adv_font_small.draw(set_k_or_m(amount), w + x2,
                                                                    h + 9 + y2, 0xa9a417, 0x201c17, 256);
                                                            }
                                                            if (amount >= 1 && amount < 100000) {
                                                                adv_font_small.draw(set_k_or_m(amount), w + x2,
                                                                    h + 9 + y2, 0xFFFF00, 0x000000, 256);
                                                            }
                                                            if (amount >= 100000 && amount < 10000000) {
                                                                adv_font_small.draw(set_k_or_m(amount), w + x2,
                                                                    h + 9 + y2, 0xFFFFFF, 0x000000, 256);
                                                            }
                                                            if (amount >= 10000000) {
                                                                adv_font_small.draw(set_k_or_m(amount), w + x2,
                                                                    h + 9 + y2, 0x00FF80, 0x000000, 256);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } else if (child.sprites != null && slot < 20) {
                                SimpleImage childSprite = child.sprites[slot];
                                if (childSprite != null) {
                                    childSprite.drawSprite(w, h);
                                }
                            }
                            slot++;
                        }
                    }
                    if (draggedItem != null) {
                        draggedItem.drawSprite1(dragX, dragY, 200 + (int) (50 * Math.sin(game_tick / 10.0)), child.contentType == 206);
                    }
                } else if (child.type == Widget.TYPE_RECTANGLE) {
                    boolean hover = false;
                    if (focusedChatWidget == child.id || focusedSidebarWidget == child.id
                        || focusedViewportWidget == child.id)
                        hover = true;
                    int colour;
                    if (interfaceIsSelected(child)) {
                        colour = child.secondaryColor;
                        if (hover && child.secondaryHoverColor != 0)
                            colour = child.secondaryHoverColor;
                    } else {
                        colour = child.textColour;
                        if (hover && child.defaultHoverColor != 0)
                            colour = child.defaultHoverColor;
                    }
                    if (child.opacity == 0) {
                        if (child.filled)
                            Rasterizer2D.draw_filled_rect(child_x_in_bounds, child_y_in_bounds, child.width, child.height, colour);
                        else
                            Rasterizer2D.draw_rect_outline(child_x_in_bounds, child_y_in_bounds, child.width, child.height, colour);
                    } else if (child.filled)
                        Rasterizer2D.draw_filled_rect(child_x_in_bounds, child_y_in_bounds, child.width, child.height, colour,
                            256 - (child.opacity & 0xff));
                    else
                        Rasterizer2D.drawTransparentBoxOutline(child_x_in_bounds, child_y_in_bounds, child.width, child.height, colour,
                            256 - (child.opacity & 0xff));
                } else if (child.type == Widget.TYPE_TEXT) {
                    AdvancedFont font = child.text_type;
                    String text = child.defaultText;

                    //  if (child.id == 14945) {
                    // System.out.println("Font: " + font.anInt4142 + " | text: " + text);
                    //}

                    if (text == null) {
                        continue;
                    }
                    // text = text + " " + child.id;
                    if (child.parent == 2400) {
                        //System.out.println("(Widget) Text is " + text);
                        //System.out.println("font.base_char_height = " + font.base_char_height);
                        //System.out.println("child.height = " + child.height);
                        //System.out.println("child_y_in_bounds = " + child_y_in_bounds);
                        //text = text.replaceAll("\\R", "");
                        //text = text.replaceAll("\\n\\n\\n\\n", "");
                        //text = text.replaceAll("\n\n\n\n", "");
                        //text = text.replaceAll("\\n", "");
                        //text = text.replaceAll("\n", "");
                    }
                    if (child.id == 8147) {
                        //System.out.println("8147 text: " + child.defaultText);
                        // System.out.println("8147 font: " + child.text_type + " | X: " + childX + " | Y: " + childY);
                        // System.out.println("8147 center: " + child.centerText);


                    }

                    if (child.id == 8157) {
                        // System.out.println("8157 text: " + child.defaultText);
                        //System.out.println("8157 font: " + child.text_type + " | X: " + childX + " | Y: " + childY);
                        // System.out.println("8157 center: " + child.centerText);

                    }

                    SimpleImage sprite;
                    if (child.textIsClicked) {
                        sprite = child.textSpriteClicked;
                        sprite.drawSprite(child_x_in_bounds - child.textClickedX, child_y_in_bounds - child.textClickedY, 0xffffff);
                    }

                    boolean flag1 = false;
                    if (focusedChatWidget == child.id || focusedSidebarWidget == child.id
                        || focusedViewportWidget == child.id)
                        flag1 = true;
                    int colour;
                    if (interfaceIsSelected(child)) {
                        colour = child.secondaryColor;
                        if (flag1 && child.secondaryHoverColor != 0)
                            colour = child.secondaryHoverColor;
                        if (child.secondaryText.length() > 0)
                            text = child.secondaryText;
                    } else {
                        colour = child.textColour;
                        if (flag1 && child.defaultHoverColor != 0)
                            colour = child.defaultHoverColor;
                    }
                    if (child.optionType == Widget.OPTION_CONTINUE && continuedDialogue) {
                        text = "Please wait...";
                        if (child.id == 6209) { // Level up clicking continue by Suic
                            if (backDialogueId != -1) {
                                backDialogueId = -1;
                                update_chat_producer = true;
                            }
                        }
                        colour = child.textColour;
                    }
                    if (Rasterizer2D.width == 519) {
                        if (colour == 0xffff00)
                            colour = 255;
                        if (colour == 49152)
                            colour = 0xffffff;
                    }
                    if (screen != ScreenMode.FIXED) {
                        if ((backDialogueId != -1 || dialogueId != -1
                            || child.defaultText.contains("Click here to continue"))
                            && (widget.id == backDialogueId || widget.id == dialogueId)) {
                            if (colour == 0xffff00) {
                                colour = 255;
                            }
                            if (colour == 49152) {
                                colour = 0xffffff;
                            }
                        }
                    }
                    if ((child.parent == 1151) || (child.parent == 12855)) {
                        switch (colour) {
                            case 16773120:
                                colour = 0xFE981F;
                                break;
                            case 7040819:
                                colour = 0xAF6A1A;
                                break;
                        }
                    }

                    int image = -1;

                    for (int drawY = child_y_in_bounds + font.base_char_height; text
                        .length() > 0;
                         drawY += font.base_char_height) {
                        //Commented out this way of drawing it, it broke interface 2400. Who knows why it was here.
                        //drawY += (child.height > font.base_char_height ? child.height : font.base_char_height)) {// can set the offset for <br> by child.height +
                        // font.getHeight() + offset
                        if (image != -1) {

                            // CLAN CHAT LIST = 37128
                            if (child.parent == 37128) {
                                spriteCache.get(image).drawAdvancedSprite(child_x_in_bounds,
                                    drawY - spriteCache.get(image).height - 1);
                                child_x_in_bounds += spriteCache.get(image).width + 3;
                            } else {
                                spriteCache.get(image).drawAdvancedSprite(child_x_in_bounds,
                                    drawY - spriteCache.get(image).height + 3);
                                child_x_in_bounds += spriteCache.get(image).width + 4;
                            }
                        }

                        if (text.indexOf("%") != -1) {
                            do {
                                int index = text.indexOf("%1");
                                if (index == -1)
                                    break;
                                if (child.id < 4000 || child.id > 5000 && child.id != 13921 && child.id != 13922
                                    && child.id != 12171 && child.id != 12172) {
                                    text = text.substring(0, index) + formatCoins(executeScript(child, 0))
                                        + text.substring(index + 2);

                                } else {
                                    text = text.substring(0, index) + interfaceIntToString(executeScript(child, 0))
                                        + text.substring(index + 2);
                                }
                            } while (true);
                            do {
                                int index = text.indexOf("%2");
                                if (index == -1) {
                                    break;
                                }
                                text = text.substring(0, index) + interfaceIntToString(executeScript(child, 1))
                                    + text.substring(index + 2);
                            } while (true);
                            do {
                                int index = text.indexOf("%3");
                                if (index == -1) {
                                    break;
                                }

                                text = text.substring(0, index) + interfaceIntToString(executeScript(child, 2))
                                    + text.substring(index + 2);
                            } while (true);
                            do {
                                int index = text.indexOf("%4");
                                if (index == -1) {
                                    break;
                                }
                                text = text.substring(0, index) + interfaceIntToString(executeScript(child, 3))
                                    + text.substring(index + 2);
                            } while (true);
                            do {
                                int index = text.indexOf("%5");
                                if (index == -1) {
                                    break;
                                }

                                text = text.substring(0, index) + interfaceIntToString(executeScript(child, 4))
                                    + text.substring(index + 2);
                            } while (true);
                        }

                        //System.out.println("(Widget) Text is now " + text);
                        //For interfaces like 2400, it's not actually a newline character,
                        //It's literally stored in the cache as a backslash and an n.
                        int break1 = text.indexOf("\\n");
                        int break2 = text.indexOf("<br>");
                        String widgetText;
                        if (break1 != -1 || break2 != -1) {
                            widgetText = text.substring(0, (break2 != -1 ? break2 : break1));
                            text = text.substring((break2 != -1 ? break2 + 4 : break1 + 2));// was +2, changed it to +3, then back to +2 for interface 2400 since "\\n".length() is actually 2
                            // System.out.println(widgetText + "_" + text);//Debug string breaks
                        } else {
                            widgetText = text;
                            text = "";// empty the string field
                        }
                        if (child.centerText) {
                            font.draw_centered(
                                widgetText.equals("Block") && child.id == 439
                                    && WeaponInterfacesWidget.weaponId == 13263 ? "Smash" : widgetText,
                                child_x_in_bounds + (child.width / 2) + (widgetText.contains("(Off") ? 1 : 0), drawY, colour,
                                child.textShadow ? 0 : -1);
                        } else if (child.rightText) {
                            font.draw(widgetText, child_x_in_bounds - font.get_width(widgetText), drawY, colour, child.textShadow ? 0 : -1);
                        } else {
                            font.draw(widgetText, child_x_in_bounds, drawY, colour, child.textShadow ? 0 : -1);
                        }
                    }
                } else if (child.type == Widget.TYPE_CONFIG_BUTTON_HOVERED_SPRITE_OUTLINE) {
                    boolean flag = false;

                    if (child.toggled) {
                        child.enabledSprite.drawSprite(child_x_in_bounds, child_y_in_bounds);
                        child.spriteWithOutline.draw_highlighted(child_x_in_bounds + child.hoveredOutlineSpriteXOffset,
                            child_y_in_bounds + child.hoveredOutlineSpriteYOffset, 0xffffff);
                        flag = true;
                        child.toggled = false;
                    } else {
                        child.disabledSprite.drawSprite(child_x_in_bounds, child_y_in_bounds);
                        child.spriteWithOutline.drawSprite(child_x_in_bounds + child.hoveredOutlineSpriteXOffset,
                            child_y_in_bounds + child.hoveredOutlineSpriteYOffset);
                    }
                    // Draw text
                    if (child.defaultText == null) {
                        continue;
                    }
                    if (child.centerText) {
                        child.text_type.draw_centered(child.defaultText, child_x_in_bounds + child.msgX, child_y_in_bounds + child.msgY,
                            flag ? child.defaultHoverColor : child.textColour, 0);
                    } else {
                        child.text_type.draw(child.defaultText, child_x_in_bounds + 5, child_y_in_bounds + child.msgY,
                            flag ? child.defaultHoverColor : child.textColour, 0);
                    }
                } else if (child.type == Widget.TYPE_SPRITE) {
                    if (prayerGrabbed != null && prayerGrabbed.spriteId == child.id) {
                        continue;
                    }

                    //Added by suic for testing, comment this out because it breaks the logout button
                    //but don't remove it.
                    /*if (child.enabledSprite == null || child.disabledSprite == null) {
                        continue;
                    }*/
                    SimpleImage sprite;

                    if (child.spriteXOffset != 0) {
                        child_x_in_bounds += child.spriteXOffset;
                    }

                    if (child.spriteYOffset != 0) {
                        child_y_in_bounds += child.spriteYOffset;
                    }

                    if (interfaceIsSelected(child)) {
                        sprite = child.disabledSprite;
                    } else {
                        sprite = child.enabledSprite;
                    }

                    if (child.parent == 1764) {
                        ItemDefinition item = ItemDefinition.get(WeaponInterfacesWidget.weaponId);
                        if (item.name.contains("ross") || item.name.contains("'bow")) {
                            if (child.id == 1773) {
                                sprite = spriteCache.get(825);
                            }
                            if (child.id == 1774) {
                                sprite = spriteCache.get(823);
                            }
                            if (child.id == 1775) {
                                sprite = spriteCache.get(824);
                            }
                        }
                    }

                    if (widget_highlighted == 1 && child.id == spellId && spellId != 0 && sprite != null) {
                        sprite.drawSpriteWithOutline(child_x_in_bounds, child_y_in_bounds, 0xffffff, true);
                    } else {
                        if (sprite != null) {

                            switch (child.id) {
//                                case 13035:
//                                case 1164:
//                                case 30064:
//                                    sprite = spriteCache.get(1298);
//                                    break;
//                                case 1167:
//                                case 13045:
//                                case 30075:
//                                    sprite = spriteCache.get(713);
//                                    break;
//                                case 1170:
//                                case 13053:
//                                case 30083:
//                                    sprite = spriteCache.get(710);
//                                    break;
//                                case 1174:
//                                case 13061:
//                                case 30106:
//                                    sprite = spriteCache.get(712);
//                                    break;
                            }

                            boolean drawTransparent = child.drawsTransparent;
                            boolean highDetail = child.hightDetail;

                            // Check if parent draws as transparent..
                            if (!drawTransparent && child.parent > 0 && Widget.cache[child.parent] != null) {
                                drawTransparent = Widget.cache[child.parent].drawsTransparent;
                            }

                            if (drawTransparent) {
                                sprite.draw_transparent(child_x_in_bounds, child_y_in_bounds, child.transparency);
                            } else if (!highDetail) {
                                sprite.drawSprite(child_x_in_bounds, child_y_in_bounds);
                            } else {
                                sprite.drawAdvancedSprite(child_x_in_bounds, child_y_in_bounds);
                            }
                        }
                    }

                } else if (child.type == 279) {

                    //  System.out.println("Child type is 279 for child id: " + child.id);

                    int startX = child_x_in_bounds;
                    int startY = child_y_in_bounds; // might use later
                    int shiftX = child.inventoryMarginX;
                    int shiftY = child.inventoryMarginY;
                    int[] spritesToDraw = child.spritesToDraw;
                    int splitIndex = child.spritesPerRow;

                    for (int i = 0; i < spritesToDraw.length; i++) {
                        SimpleImage sprite = spriteCache.get(spritesToDraw[i]);
                        if (i % splitIndex == 0 && i != 0) {
                            child_x_in_bounds = startX;
                            child_y_in_bounds += shiftY;
                        } else {
                            if (i != 0) {
                                child_x_in_bounds += shiftX;
                            }
                        }
                        //System.out.println("Drawing sprite at: " + child_x_in_bounds + " | " + child_y_in_bounds);
                        sprite.drawSprite(child_x_in_bounds, child_y_in_bounds);

                    }

                } else if (child.type == Widget.TYPE_SPELL_SPRITE) {
                    SimpleImage sprite;

                    if (child.spriteXOffset != 0) {
                        child_x_in_bounds += child.spriteXOffset;
                    }

                    if (child.spriteYOffset != 0) {
                        child_y_in_bounds += child.spriteYOffset;
                    }

                    if (interfaceIsSelected(child)) {
                        sprite = child.disabledSprite;
                    } else {
                        sprite = child.enabledSprite;
                    }

                    if (widget_highlighted == 1 && child.id == spellId && spellId != 0 && sprite != null) {
                        sprite.draw_highlighted(child_x_in_bounds, child_y_in_bounds, 0xffffff);
                    } else {
                        if (sprite != null) {

                            boolean drawTransparent = child.drawsTransparent;

                            // Check if parent draws as transparent..
                            if (!drawTransparent && child.parent > 0 && Widget.cache[child.parent] != null) {
                                drawTransparent = Widget.cache[child.parent].drawsTransparent;
                            }

                            if (drawTransparent) {
                                sprite.draw_transparent(child_x_in_bounds, child_y_in_bounds, child.transparency);
                            } else {
                                // if (autoCastId != 65535) {
                                // TODO: check if this != 1 check breaks anything since it fixes trident
                                // autocast book sprites.
                                if (autoCastId != 65535 && autoCastId != 1) {
                                    Widget spellSprite = Widget.cache[autoCastId];
                                    sprite = spellSprite.disabledSprite;
                                    // System.out.println("autocastid " + autoCastId);
                                    // Since we re-assign sprite here, we may need to
                                    if (sprite != null) {
                                        sprite.drawSprite(child_x_in_bounds + 2, child_y_in_bounds + 2);
                                    } else {
                                        //System.err.println("Spell sprite seems to be null.");
                                    }
                                } else {
                                    sprite.drawSprite(child_x_in_bounds, child_y_in_bounds);
                                }
                            }
                        }
                    }
                } else if (child.type == Widget.TYPE_MODEL) {
                    int centreX = Rasterizer3D.center_x;
                    int centreY = Rasterizer3D.center_y;
                    Rasterizer3D.center_x = child_x_in_bounds + child.width / 2;
                    Rasterizer3D.center_y = child_y_in_bounds + child.height / 2;
                    int sine = Rasterizer3D.SINE[child.modelRotation1] * child.modelZoom >> 16;
                    int cosine = Rasterizer3D.COSINE[child.modelRotation1] * child.modelZoom >> 16;

                    boolean selected = interfaceIsSelected(child);
                    int anim;
                    if (selected)
                        anim = child.secondaryAnimationId;
                    else
                        anim = child.defaultAnimationId;

                    Model model = null;
                    if (anim == -1) {
                        model = child.get_animated_model(-1, -1, selected);
                    } else {
                        try {
                            Sequence animation = Sequence.cache[anim];
                            model = child.get_animated_model(animation.frame_list[child.currentFrame], animation.primary_frame[child.currentFrame], selected);
                        } catch (Exception e) {
                            e.printStackTrace();
                            addReportToServer(e.getMessage());
                        }
                    }

                    if (model != null) {
                        //     System.out.println("Rendering 2d modelr");
                        model.render_2D(child.modelRotation2, 0, child.modelRotation1, 0, sine, cosine);
                    }
                    Rasterizer3D.center_x = centreX;
                    Rasterizer3D.center_y = centreY;
                } else if (child.type == Widget.TYPE_ITEM_LIST) {
                    AdvancedFont font = child.text_type;
                    int slot = 0;
                    for (int row = 0; row < child.height; row++) {
                        for (int column = 0; column < child.width; column++) {
                            if (child.inventoryItemId[slot] > 0) {
                                ItemDefinition item = ItemDefinition.get(child.inventoryItemId[slot] - 1);
                                bars.setConsume(StatusBars.Restore.get(item.id));
                                String name = item.name;
                                if (item.stackable || child.inventoryAmounts[slot] != 1)
                                    name = name + " x" + intToKOrMilLongName(child.inventoryAmounts[slot]);
                                int spriteX = child_x_in_bounds + column * (115 + child.inventoryMarginX);
                                int spriteY = child_y_in_bounds + row * (12 + child.inventoryMarginY);
                                if (child.centerText)
                                    font.draw_centered(name, spriteX + child.width / 2, spriteY, child.textColour,
                                        child.textShadow);
                                else
                                    font.draw(name, spriteX, spriteY, child.textColour, child.textShadow);
                            }
                            slot++;
                        }
                    }
                } else if (child.type == Widget.TYPE_OTHER
                    && (chatTooltipSupportId == child.id || tabTooltipSupportId == child.id
                    || gameTooltipSupportId == child.id)
                    && anInt1501 == tooltipDelay && !menuOpen && prayerGrabbed == null) {
                    AdvancedFont font = adv_font_regular;
                    String text = child.defaultText;
                    String tooltip;
                    int break_index_old;
                    int break_index_new;
                    int box_width = 0;
                    int box_height = 0;

                    if (child.parent == 3917) {
                        return;
                    }

                    if (child.hoverXOffset != 0) {
                        child_x_in_bounds += child.hoverXOffset;
                    }

                    if (child.hoverYOffset != 0) {
                        child_y_in_bounds += child.hoverYOffset;
                    }

                    // calculate tooltip box size
                    for (text = widget_tooltip_text_script(text, child); text.length() > 0; box_height = box_height
                        + font.base_char_height + 1) {
                        break_index_old = text.indexOf("\\n");
                        break_index_new = text.indexOf("<br>");
                        if (break_index_old != -1 || break_index_new != -1) {
                            tooltip = text.substring(0, (break_index_new != -1 ? break_index_new : break_index_old));
                            text = text.substring((break_index_new != -1 ? break_index_new + 4 : break_index_old + 2));
                        } else {
                            tooltip = text;
                            text = "";
                        }
                        int text_width = font.get_width(tooltip);
                        if (text_width > box_width) {
                            box_width = text_width;
                        }
                    }

                    box_width += 6;
                    box_height += 7;
                    int x_pos = (child_x_in_bounds + child.width) - 5 - box_width;
                    int y_pos = child_y_in_bounds + child.height + 5;
                    if (x_pos < child_x_in_bounds + 5) {
                        x_pos = child_x_in_bounds + 5;
                    }
                    if (x_pos + box_width > x + widget.width) {
                        x_pos = (x + widget.width) - box_width;
                    }
                    if (y_pos + box_height > y + widget.height) {
                        y_pos = (child_y_in_bounds - box_height);
                    }

                    // Skill tab
                    if (child.parent >= 14918 && child.parent <= 14941) {
                        // if (box_height > 30) {
                        // box_width = box_width - 10;
                        // }

                        if (screen == ScreenMode.FIXED) {
                            if (child.inventoryHover) {
                                if (x_pos + box_width > 221) {
                                    x_pos = 252 - box_width - x;
                                }
                                if (y_pos + box_height + interfaceDrawY > 291
                                    && y_pos + box_height + interfaceDrawY < 319) {
                                    y_pos = 265 - box_height - y;
                                } else if (y_pos + box_height + interfaceDrawY >= 319) {
                                    y_pos = (box_height == 46 ? 297 : 298) - box_height - y;
                                }
                                if (x_pos == 100) {
                                    x_pos = 99;
                                }
                            }
                        } else {
                            if (child.inventoryHover) {
                                if (x_pos + box_width > window_width - 26) {
                                    x_pos = window_width - (box_width) - 26;
                                }
                                if (y_pos + box_height > window_height - 38
                                    && y_pos + box_height < window_height - 18) {
                                    y_pos = window_height - (box_height) - 108;
                                } else if (y_pos + box_height >= window_height - 18) {
                                    y_pos = window_height - (box_height) - (box_height == 46 ? 76 : 75);
                                }
                                if (x_pos == window_width - 146) {
                                    x_pos = window_width - 147;
                                }
                            }
                        }
                    }

                    Rasterizer2D.draw_filled_rect(x_pos, y_pos, box_width, box_height, 0xFFFFA0);
                    Rasterizer2D.draw_rect_outline(x_pos, y_pos, box_width, box_height, 0);

                    text = child.defaultText;
                    int text_y = y_pos + font.base_char_height + 2;
                    for (text = widget_tooltip_text_script(text, child); text.length() > 0; text_y = text_y
                        + font.base_char_height + 1) {
                        break_index_old = text.indexOf("\\n");
                        break_index_new = text.indexOf("<br>");
                        if (break_index_old != -1 || break_index_new != -1) {
                            tooltip = text.substring(0, break_index_new != -1 ? break_index_new : break_index_old);
                            text = text.substring((break_index_new != -1 ? break_index_new + 4 : break_index_old + 2));
                        } else {
                            tooltip = text;
                            text = "";
                        }

                        font.draw(tooltip, x_pos + 3, text_y, 0, -1);
                    }
                } else if (child.type == Widget.TYPE_HOVER || child.type == Widget.TYPE_CONFIG_HOVER) {
                    // Draw sprite
                    boolean flag = false;

                    if (child.toggled) {
                        child.enabledSprite.drawAdvancedSprite(child_x_in_bounds, child_y_in_bounds, child.spriteOpacity);
                        flag = true;
                        child.toggled = false;
                    } else {
                        child.disabledSprite.drawSprite(child_x_in_bounds, child_y_in_bounds);
                    }

                    // Draw text
                    if (child.defaultText == null) {
                        continue;
                    }

                    if (child.centerText) {
                        child.text_type.draw_centered(child.defaultText, child_x_in_bounds + child.msgX, child_y_in_bounds + child.msgY, flag ? child.defaultHoverColor : child.textColour, 0);
                    } else {
                        child.text_type.draw(child.defaultText, child_x_in_bounds + 5, child_y_in_bounds + child.msgY, flag ? child.defaultHoverColor : child.textColour, 0);
                    }
                } else if (child.type == Widget.TYPE_CONFIG) {
                    SimpleImage sprite = child.active ? child.disabledSprite : child.enabledSprite;
                    sprite.drawSprite(child_x_in_bounds, child_y_in_bounds);
                } else if (child.type == Widget.TYPE_SLIDER) {
                    Slider slider = child.slider;
                    if (slider != null) {
                        slider.draw(child_x_in_bounds, child_y_in_bounds);
                    }
                } else if (child.type == Widget.TYPE_DROPDOWN) {
                    DropdownMenu d = child.dropdown;

                    int bgColour = child.dropdownColours[2];
                    int fontColour = 0xfe971e;
                    int downArrow = 609;

                    if (child.hovered || d.isOpen()) {
                        downArrow = 610;
                        fontColour = 0xffb83f;
                        bgColour = child.dropdownColours[3];
                    }

                    Rasterizer2D.drawPixels(20, child_y_in_bounds, child_x_in_bounds, child.dropdownColours[0], d.getWidth());
                    Rasterizer2D.drawPixels(18, child_y_in_bounds + 1, child_x_in_bounds + 1, child.dropdownColours[1], d.getWidth() - 2);
                    Rasterizer2D.drawPixels(16, child_y_in_bounds + 2, child_x_in_bounds + 2, bgColour, d.getWidth() - 4);

                    int xOffset = child.centerText ? 3 : 16;
                    if (widget.id == 41900) {
                        adv_font_regular.draw_centered(d.getSelected(), child_x_in_bounds + (d.getWidth() - xOffset) / 2,
                            child_y_in_bounds + 14, fontColour, 0);
                    } else {
                        adv_font_small.draw_centered(d.getSelected(), child_x_in_bounds + (d.getWidth() - xOffset) / 2,
                            child_y_in_bounds + 14, fontColour, 0);
                    }

                    if (d.isOpen()) {
                        // Up arrow
                        spriteCache.get(608).drawSprite(child_x_in_bounds + d.getWidth() - 18, child_y_in_bounds + 2);

                        Rasterizer2D.drawPixels(d.getHeight(), child_y_in_bounds + 19, child_x_in_bounds, child.dropdownColours[0],
                            d.getWidth());
                        Rasterizer2D.drawPixels(d.getHeight() - 2, child_y_in_bounds + 20, child_x_in_bounds + 1, child.dropdownColours[1],
                            d.getWidth() - 2);
                        Rasterizer2D.drawPixels(d.getHeight() - 4, child_y_in_bounds + 21, child_x_in_bounds + 2, child.dropdownColours[3],
                            d.getWidth() - 4);

                        int yy = 2;
                        for (int i = 0; i < d.getOptions().length; i++) {
                            if (child.dropdownHover == i) {
                                Rasterizer2D.drawPixels(13, child_y_in_bounds + 19 + yy, child_x_in_bounds + 2, child.dropdownColours[4],
                                    d.getWidth() - 4);
                                if (widget.id == 41900) {
                                    adv_font_regular.draw_centered(d.getOptions()[i],
                                        child_x_in_bounds + (d.getWidth() - xOffset) / 2, child_y_in_bounds + 29 + yy, 0xffb83f, 0);
                                } else {
                                    adv_font_small.draw_centered(d.getOptions()[i],
                                        child_x_in_bounds + (d.getWidth() - xOffset) / 2, child_y_in_bounds + 29 + yy, 0xffb83f, 0);
                                }

                            } else {
                                Rasterizer2D.drawPixels(13, child_y_in_bounds + 19 + yy, child_x_in_bounds + 2, child.dropdownColours[3],
                                    d.getWidth() - 4);
                                if (widget.id == 41900) {
                                    adv_font_regular.draw_centered(d.getOptions()[i],
                                        child_x_in_bounds + (d.getWidth() - xOffset) / 2, child_y_in_bounds + 29 + yy, 0xfe971e, 0);
                                } else {
                                    adv_font_small.draw_centered(d.getOptions()[i],
                                        child_x_in_bounds + (d.getWidth() - xOffset) / 2, child_y_in_bounds + 29 + yy, 0xfe971e, 0);
                                }
                            }
                            yy += 14;
                        }
                        drawScrollbar(d.getHeight() - 4, child.scrollPosition, child_y_in_bounds + 21, child_x_in_bounds + d.getWidth() - 18,
                            d.getHeight() - 5, false);

                    } else {
                        spriteCache.get(downArrow).drawSprite(child_x_in_bounds + d.getWidth() - 18, child_y_in_bounds + 2);
                    }
                } else if (child.type == Widget.TYPE_KEYBINDS_DROPDOWN) {

                    DropdownMenu d = child.dropdown;

                    // If dropdown inverted, don't draw following 2 menus
                    if (dropdownInversionFlag > 0) {
                        dropdownInversionFlag--;
                        continue;
                    }

                    Rasterizer2D.drawPixels(18, child_y_in_bounds + 1, child_x_in_bounds + 1, 0x544834, d.getWidth() - 2);
                    Rasterizer2D.drawPixels(16, child_y_in_bounds + 2, child_x_in_bounds + 2, 0x2e281d, d.getWidth() - 4);
                    adv_font_regular.draw(d.getSelected(), child_x_in_bounds + 7, child_y_in_bounds + 15, 0xff8a1f, 0);
                    spriteCache.get(449).drawSprite(child_x_in_bounds + d.getWidth() - 18, child_y_in_bounds + 2);

                    if (d.isOpen()) {

                        Widget.cache[child.id - 1].active = true; // Alter stone colour

                        int yPos = child_y_in_bounds + 18;

                        // Dropdown inversion for lower stones
                        if (child.inverted) {
                            yPos = child_y_in_bounds - d.getHeight() - 10;
                            dropdownInversionFlag = 2;
                        }

                        Rasterizer2D.drawPixels(d.getHeight() + 17, yPos, child_x_in_bounds + 1, 0x544834, d.getWidth() - 2);
                        Rasterizer2D.drawPixels(d.getHeight() + 15, yPos + 1, child_x_in_bounds + 2, 0x2e281d, d.getWidth() - 4);

                        int yy = 2;
                        int xx = 0;
                        int bb = d.getWidth() / 2;

                        for (int i = 0; i < d.getOptions().length; i++) {

                            int fontColour = 0xff981f;
                            if (child.dropdownHover == i) {
                                fontColour = 0xffffff;
                            }

                            if (xx == 0) {
                                adv_font_regular.draw(d.getOptions()[i], child_x_in_bounds + 5, yPos + 14 + yy, fontColour,
                                    0x2e281d);
                                xx = 1;

                            } else {
                                adv_font_regular.draw(d.getOptions()[i], child_x_in_bounds + 5 + bb, yPos + 14 + yy, fontColour,
                                    0x2e281d);
                                xx = 0;
                                yy += 15;
                            }
                        }
                    } else {
                        Widget.cache[child.id - 1].active = false;
                    }
                } else if (child.type == Widget.TYPE_ADJUSTABLE_CONFIG) {

                    int totalWidth = child.width;
                    int spriteWidth = child.disabledSprite.width;
                    int totalHeight = child.height;
                    int spriteHeight = child.disabledSprite.height;
                    SimpleImage behindSprite = child.active ? child.enabledAltSprite : child.disabledAltSprite;

                    if (child.toggled) {
                        behindSprite.drawSprite(child_x_in_bounds, child_y_in_bounds);
                        child.disabledSprite.drawAdvancedSprite(child_x_in_bounds + (totalWidth / 2) - spriteWidth / 2,
                            child_y_in_bounds + (totalHeight / 2) - spriteHeight / 2, child.spriteOpacity);
                        child.toggled = false;
                    } else {
                        behindSprite.drawSprite(child_x_in_bounds, child_y_in_bounds);
                        child.disabledSprite.drawSprite(child_x_in_bounds + (totalWidth / 2) - spriteWidth / 2,
                            child_y_in_bounds + (totalHeight / 2) - spriteHeight / 2);
                    }
                } else if (child.type == Widget.TYPE_BOX) {
                    // Draw outline
                    Rasterizer2D.draw_filled_rect(child_x_in_bounds - 2, child_y_in_bounds - 2, child.width + 4, child.height + 4, 0x0e0e0c);
                    Rasterizer2D.draw_filled_rect(child_x_in_bounds - 1, child_y_in_bounds - 1, child.width + 2, child.height + 2, 0x474745);
                    // Draw base box
                    if (child.toggled) {
                        Rasterizer2D.draw_filled_rect(child_x_in_bounds, child_y_in_bounds, child.width, child.height,
                            child.secondaryHoverColor);
                        child.toggled = false;
                    } else {
                        Rasterizer2D.draw_filled_rect(child_x_in_bounds, child_y_in_bounds, child.width, child.height,
                            child.defaultHoverColor);
                    }
                } else if (child.type == Widget.CLICKABLE_SPRITES) {
                    if (child.backgroundSprites.length > 1) {
                        if (child.enabledSprite != null) {
                            child.enabledSprite.drawAdvancedSprite(child_x_in_bounds, child_y_in_bounds);
                        }
                    }
                } else if (child.type == Widget.TYPE_INPUT_FIELD) {
                    drawInputField(child, x, y, child_x_in_bounds, child_y_in_bounds, child.width, child.height);
                } else if (child.type == Widget.DARKEN) {
                    if (child.id != 66010) {
                        Rasterizer2D.set_clip(0, 0, window_width, window_height);
                    }
                    Rasterizer2D.fillRectangle(child_x_in_bounds, child_y_in_bounds, child.width, child.height, child.fillColor, child.opacity);
                    Rasterizer2D.set_clip(x, y, x + child.width, y + widget.height);
                } else if (child.type == Widget.OUTLINE) {
                    int color = child.parent == 34006 || child.parent == 34025 ? 0x494034 : 0x383023;
                    Rasterizer2D.draw_rect_outline(child_x_in_bounds - 1, child_y_in_bounds - 1, child.width + 2, child.height + 2, 0x383023);
                    Rasterizer2D.draw_filled_rect(child_x_in_bounds, child_y_in_bounds, child.width, child.height, child.color, child.transparency);
                } else if (child.type == Widget.LINE) {
                    Rasterizer2D.draw_horizontal_line(child_x_in_bounds, child_y_in_bounds, child.width, child.color);
                } else if (child.type == Widget.COLOR) {
                    Rasterizer2D.draw_filled_rect(child_x_in_bounds, child_y_in_bounds, child.width, child.height, child.color,
                        child.transparency);
                } else if (child.type == Widget.DRAW_LINE) {
                    if (child instanceof DrawLine) {
                        DrawLine inter = (DrawLine) child;
                        if (inter.getLineType() == DrawLine.LineType.HORIZONTAL) {
                            Rasterizer2D.drawTransparentHorizontalLine(child_x_in_bounds, child_y_in_bounds, child.width, child.textColour, child.opacity2);
                        } else if (inter.getLineType() == DrawLine.LineType.VERTICAL) {
                            int localHeight = child.width;
                            Rasterizer2D.drawTransparentVerticalLine(child_x_in_bounds, child_y_in_bounds, localHeight, child.textColour, child.opacity2);
                        }
                    }
                }
        }
        Rasterizer2D.set_clip(clipLeft, clipTop, clipRight, clipBottom);
    }

    public int brightness;

    public int getSpriteBrightness() {
        brightness = 4;
        if (brightness == 4) {
            return 255;
        } else if (brightness == 3) {
            return 225;
        } else if (brightness == 2) {
            return 195;
        } else
            return 182;
    }

    public static String[] addLinebreaks(String input, double maxCharInLine) {
        StringTokenizer tok = new StringTokenizer(input, " ");
        StringBuilder output = new StringBuilder(input.length());
        int lineLen = 0;
        while (tok.hasMoreTokens()) {
            String word = tok.nextToken();
            while (word.length() > maxCharInLine) {
                output.append(word.substring(0, (int) (maxCharInLine) - lineLen) + "\n");
                word = word.substring((int) (maxCharInLine) - lineLen);
                lineLen = 0;
            }
            if (lineLen + word.length() > maxCharInLine) {
                output.append("\n");
                lineLen = 0;
            }
            output.append(word + " ");
            lineLen += word.length() + 1;
        }
        return output.toString().split("\n");
    }

    public static int tooltipDelay = 25;

    public void drawSpecialAttack(Widget widget) {
        boolean fixed = screen == ScreenMode.FIXED;
        /** Blue special attack bar **/
        for (int i = 0; i < SpecialAttackBars.values().length; i++) {
            if (widget.id == SpecialAttackBars.values()[i].readInterfaceId()
                && WeaponInterfacesWidget.weaponId == SpecialAttackBars.values()[i].getItemId()) {

                if (screen != ScreenMode.FIXED && settings[ConfigUtility.SIDE_STONES_ARRANGEMENT_ID] == 1) {
                    int x = fixed ? 51 : window_width < 1000 ? window_width - 178 : window_width - 178;
                    int y = fixed ? 242 : window_width < 1000 ? window_height - 135 : window_height - 97;
                    specialBarSprite[0].drawSprite(x, y);
                    specialBarSprite[specialAttack >= 25 ? 1 : 2] = new SimpleImage(
                        SpecialBarSpriteLoader.cache[specialAttack >= SpecialAttackBars.values()[i]
                            .getSpecialAmount() ? 1 : 2].spriteData,
                        (142 / 100.0f) * specialAttack, 11);

                    int x2 = fixed ? 55 : window_width < 1000 ? window_width - 174 : window_width - 174;
                    int y2 = fixed ? 249 : window_width < 1000 ? window_height - 128 : window_height - 90;
                    specialBarSprite[specialAttack >= 25 ? 1 : 2].drawShadedSprite(x2, y2, getSpriteBrightness());
                } else {
                    specialBarSprite[0].drawSprite(fixed ? 51 : window_width - 194, fixed ? 242 : window_height - 94);
                    specialBarSprite[specialAttack >= 25 ? 1 : 2] = new SimpleImage(
                        SpecialBarSpriteLoader.cache[specialAttack >= SpecialAttackBars.values()[i]
                            .getSpecialAmount() ? 1 : 2].spriteData,
                        (142 / 100.0f) * specialAttack, 11);
                    specialBarSprite[specialAttack >= 25 ? 1 : 2].drawShadedSprite(fixed ? 55 : window_width - 190,
                        fixed ? 249 : window_height - 87, getSpriteBrightness());
                }
            }
        }
        boolean specialAttackBarHover = super.cursor_x >= (fixed ? 569 : window_width - 192)
            && super.cursor_x <= (fixed ? 718 : window_width - 50)
            && super.cursor_y >= (fixed ? 413 : window_height - 89)
            && super.cursor_y <= (fixed ? 439 : window_height - 67);
        /** Tooltips **/
        if (anInt1501 < tooltipDelay && specialAttackBarHover) {
            anInt1501++;
        } else {
            int boxLength;
            int boxHeight;
            for (int i = 0; i < SpecialAttackBars.values().length; i++) {
                if (widget.id == SpecialAttackBars.values()[i].readInterfaceId()
                    && WeaponInterfacesWidget.weaponId == SpecialAttackBars.values()[i].getItemId()
                    && specialAttackBarHover) {
                    String tooltip = SpecialAttackBars.values()[i].getTooltip() + " ("
                        + (int) (SpecialAttackBars.values()[i].getSpecialAmount()) + "%)";
                    if (WeaponInterfacesWidget.weaponId == 11235 || WeaponInterfacesWidget.weaponId == 12768
                        || WeaponInterfacesWidget.weaponId == 12767 || WeaponInterfacesWidget.weaponId == 12766
                        || WeaponInterfacesWidget.weaponId == 12765) {
                        if (WeaponInterfacesWidget.ammoId == 11212 || WeaponInterfacesWidget.ammoId == 11227
                            || WeaponInterfacesWidget.ammoId == 11228 || WeaponInterfacesWidget.ammoId == 11229) {
                            tooltip = "Descent of Dragons: Deal a double attack with dragon arrows that inflicts up to 50% more damage (minimum damage of 8 per hit).";
                        }
                    }
                    String[] tooltipArray = addLinebreaks(tooltip,
                        SpecialAttackBars.values()[i].getItemId() == 1215
                            || SpecialAttackBars.values()[i].getItemId() == 1231
                            || SpecialAttackBars.values()[i].getItemId() == 5680
                            || SpecialAttackBars.values()[i].getItemId() == 5698 ? 25 : 26);
                    boxLength = adv_font_regular.get_width(tooltipArray[0]);
                    for (int lengthLoop = 0; lengthLoop < tooltipArray.length; lengthLoop++) {
                        if (adv_font_regular.get_width(tooltipArray[lengthLoop]) > boxLength) {
                            boxLength = adv_font_regular.get_width(tooltipArray[lengthLoop]);
                        }
                    }
                    boxHeight = 7 + (tooltipArray.length * 12);
                    Rasterizer2D.draw_filled_rect(fixed ? 55 : window_width - 190,
                        fixed ? 237 - boxHeight : (window_height - boxHeight) - 100, boxLength + 4, boxHeight,
                        0xFFFFA0);
                    Rasterizer2D.draw_rect_outline(fixed ? 55 : window_width - 190,
                        fixed ? 237 - boxHeight : (window_height - boxHeight) - 100, boxLength + 4, boxHeight, 0);
                    for (int tooltipSplit = 0; tooltipSplit < tooltipArray.length; tooltipSplit++) {
                        adv_font_regular
                            .draw(tooltipArray[tooltipSplit], fixed ? 57 : window_width - 188,
                                fixed ? (250 - boxHeight) + (12 * tooltipSplit)
                                    : (window_height - boxHeight) + (12 * tooltipSplit) - 87,
                                0x000000, 0xFFFFA0);
                    }
                }
            }
        }
    }

    // Bank vars
    private int[] tabAmounts = new int[]{350, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    int modifiableXValue;

    private void randomizeBackground(IndexedImage background) {
        int j = 256;
        for (int k = 0; k < anIntArray1190.length; k++)
            anIntArray1190[k] = 0;

        for (int l = 0; l < 5000; l++) {
            int i1 = (int) (Math.random() * 128D * (double) j);
            anIntArray1190[i1] = (int) (Math.random() * 256D);
        }
        for (int j1 = 0; j1 < 20; j1++) {
            for (int k1 = 1; k1 < j - 1; k1++) {
                for (int i2 = 1; i2 < 127; i2++) {
                    int k2 = i2 + (k1 << 7);
                    anIntArray1191[k2] = (anIntArray1190[k2 - 1] + anIntArray1190[k2 + 1] + anIntArray1190[k2 - 128]
                        + anIntArray1190[k2 + 128]) / 4;
                }

            }
            int ai[] = anIntArray1190;
            anIntArray1190 = anIntArray1191;
            anIntArray1191 = ai;
        }
        if (background != null) {
            int l1 = 0;
            for (int j2 = 0; j2 < background.height; j2++) {
                for (int l2 = 0; l2 < background.width; l2++)
                    if (background.palettePixels[l1++] != 0) {
                        int i3 = l2 + 16 + background.drawOffsetX;
                        int j3 = j2 + 16 + background.drawOffsetY;
                        int k3 = i3 + (j3 << 7);
                        anIntArray1190[k3] = 0;
                    }
            }
        }
    }

    private void appendPlayerUpdateMask(int mask, int index, Buffer buffer, Player player) {
        if ((mask & 0x400) != 0) {

            int initialX = buffer.readUByteS();
            int initialY = buffer.readUByteS();
            int destinationX = buffer.readUByteS();
            int destinationY = buffer.readUByteS();
            int startForceMovement = buffer.readLEUShortA() + game_tick;
            int endForceMovement = buffer.readUShortA() + game_tick;
            int direction = buffer.readUByteS();

            player.initialX = initialX;
            player.initialY = initialY;
            player.destinationX = destinationX;
            player.destinationY = destinationY;
            player.initiate_movement = startForceMovement;
            player.cease_movement = endForceMovement;
            player.direction = direction;

            player.resetPath();
        }
        if ((mask & 0x100) != 0) {
            player.graphic_id = buffer.readLEUShort();
            int info = buffer.readInt();
            player.graphic_height = info >> 16;
            player.graphic_cycle = game_tick + (info & 0xffff);
            player.current_animation_id = 0;
            player.current_animation_time_remaining = 0;
            if (player.graphic_cycle > game_tick)
                player.current_animation_id = -1;
            if (player.graphic_id == 65535) {
                player.graphic_id = -1;
            }
        }
        if ((mask & 8) != 0) {
            int animation = buffer.readLEUShort();
            if (animation == 65535)
                animation = -1;
            int delay = buffer.readNegUByte();

            if (animation == player.animation && animation != -1) {
                int replayMode = Sequence.cache[animation].reset;
                if (replayMode == 1) {
                    player.current_animation_frame = 0;
                    player.current_animation_duration = 0;
                    player.animation_delay = delay;
                    player.animation_loops = 0;
                }
                if (replayMode == 2)
                    player.animation_loops = 0;
            } else if (animation == -1 || player.animation == -1
                || Sequence.cache[animation].appended_frames >= Sequence.cache[player.animation].appended_frames) {
                player.animation = animation;
                player.current_animation_frame = 0;
                player.current_animation_duration = 0;
                player.animation_delay = delay;
                player.animation_loops = 0;
                player.remaining_steps = player.waypoint_index;
            }
        }
        if ((mask & 4) != 0) {
            player.entity_message = buffer.readString();
            // System.out.println("mask &4 != 0 text: " + player.spokenText);
            if (player.entity_message.charAt(0) == '~') {
                player.entity_message = player.entity_message.substring(1);
                sendMessage(player.entity_message, 2, player.username, player.getTitle(false));
            } else if (player == local_player)
                sendMessage(player.entity_message, 2, player.username, player.getTitle(false));
            player.textColour = 0;
            player.textEffect = 0;
            player.message_cycle = 150;
        }
        if ((mask & 0x80) != 0) {
            int textColorAndEffect = buffer.readLEUShort();
            int privilege = buffer.readUnsignedByte();
            int donatorPrivilege = buffer.readUnsignedByte();
            int j3 = buffer.readNegUByte(); // chat text size
            int k3 = buffer.pos; // chat text
            if (player.username != null && player.visible) {
                long name = StringUtils.encodeBase37(player.username);
                boolean ignored = false;
                if (privilege <= 1) {
                    for (int count = 0; count < ignoreCount; count++) {
                        if (ignoreListAsLongs[count] != name)
                            continue;
                        ignored = true;
                        break;
                    }

                }
                if (!ignored && onTutorialIsland == 0)
                    try {

                        chatBuffer.pos = 0;
                        buffer.readReverseData(chatBuffer.payload, j3, 0);
                        chatBuffer.pos = 0;
                        String text = ChatMessageCodec.decode(j3, chatBuffer);
                        //System.out.println("Text is: " + text);
                        // String text = buffer.readString();
                        // s = Censor.doCensor(s);
                        player.entity_message = text;
                        player.textColour = textColorAndEffect >> 8;
                        player.rights = privilege;
                        player.donatorRights = donatorPrivilege;
                        player.textEffect = textColorAndEffect & 0xff;
                        player.message_cycle = 150;

                        List<ChatCrown> crowns = ChatCrown.get(privilege, donatorPrivilege);
                        String crownPrefix = "";
                        for (ChatCrown c : crowns) {
                            crownPrefix += c.getIdentifier();
                        }

                        sendMessage(text, 1, crownPrefix + player.username, player.getTitle(false));

                    } catch (Exception exception) {
                        exception.printStackTrace();
                        addReportToServer(exception.getMessage());
                    }
            }
            buffer.pos = k3 + j3;
        }
        if ((mask & 1) != 0) {
            player.engaged_entity_id = buffer.readLEUShort();
            if (player.engaged_entity_id == 65535)
                player.engaged_entity_id = -1;
        }
        if ((mask & 0x10) != 0) {
            int length = buffer.readNegUByte();
            byte data[] = new byte[length];
            Buffer appearanceBuffer = new Buffer(data);
            buffer.readBytes(length, 0, data);
            playerSynchronizationBuffers[index] = appearanceBuffer;
            player.update(appearanceBuffer);
        }
        if ((mask & 2) != 0) {
            player.faceX = buffer.readLEUShortA();
            player.faceY = buffer.readLEUShort();
        }
        if ((mask & 0x20) != 0) {
            int count = buffer.readUByte();
            for (int i = 0; i < count; i++) {
                int damage = buffer.readShort();
                int type = buffer.readUByte();
                // System.out.println("Client received damage type: "+type);
                int hp = buffer.readShort();
                int maxHp = buffer.readShort();
                player.updateHitData(type, damage, game_tick);
                player.game_tick_status = game_tick + 300;
                player.current_hitpoints = hp;
                player.maximum_hitpoints = maxHp;
            }
        }
        /*if ((mask & 0x200) != 0) { // no longer used
            int damage = buffer.readShort();
            int type = buffer.readUByte();
            int hp = buffer.readShort();
            int maxHp = buffer.readShort();
            player.updateHitData(type, damage, game_tick);
            player.game_tick_status = game_tick + 300;
            player.current_hitpoints = hp;
            player.maximum_hitpoints = maxHp;
        }*/
    }

    private void set_camera() {
        try {
            int x_pos = local_player.world_x + camera_pan_offset;
            int y_pos = local_player.world_y + camera_tilt_offset;

            if (current_camera_pan - x_pos < -500 || current_camera_pan - x_pos > 500
                || current_camera_tilt - y_pos < -500 || current_camera_tilt - y_pos > 500) {
                current_camera_pan = x_pos;
                current_camera_tilt = y_pos;
            }
            if (current_camera_pan != x_pos)
                current_camera_pan += (x_pos - current_camera_pan) / 16;

            if (current_camera_tilt != y_pos)
                current_camera_tilt += (y_pos - current_camera_tilt) / 16;

            int LEFT = 1;
            int RIGHT = 2;
            int UP = 3;
            int DOWN = 4;
            if (super.key_status[LEFT] == 1)
                camera_pan_modifier += (-24 - camera_pan_modifier) / 2;

            else if (super.key_status[RIGHT] == 1)
                camera_pan_modifier += (24 - camera_pan_modifier) / 2;
            else
                camera_pan_modifier /= 2;

            if (super.key_status[UP] == 1)
                camera_tilt_modifier += (12 - camera_tilt_modifier) / 2;

            else if (super.key_status[DOWN] == 1)
                camera_tilt_modifier += (-12 - camera_tilt_modifier) / 2;
            else
                camera_tilt_modifier /= 2;

            camera_pan = camera_pan + camera_pan_modifier / 2 & 0x7ff;
            camera_tilt += camera_tilt_modifier / 2;
            if (camera_tilt < 128)
                camera_tilt = 128;

            if (camera_tilt > 383)
                camera_tilt = 383;

            int max_pan = current_camera_pan >> 7;
            int max_tilt = current_camera_tilt >> 7;
            int height = get_tile_pos(this.plane, current_camera_tilt, current_camera_pan);
            int max_height = 0;
            if (max_pan > 3 && max_tilt > 3 && max_pan < 100 && max_tilt < 100) {
                for (int x = max_pan - 4; x <= max_pan + 4; x++) {
                    for (int y = max_tilt - 4; y <= max_tilt + 4; y++) {
                        int plane = this.plane;
                        if (plane < 3 && (tileFlags[1][x][y] & 2) == 2)
                            plane++;

                        int offset = height - tileHeights[plane][x][y];
                        if (offset > max_height)
                            max_height = offset;

                    }
                }
            }
			/*anInt1005++;
			if (anInt1005 > 1512) {
				anInt1005 = 0;
				outgoing.write_opcode(77);
				outgoing.write_byte(0);
				int i2 = outgoing.pos;
				outgoing.write_byte((int) (Math.random() * 256D));
				outgoing.write_byte(101);
				outgoing.write_byte(233);
				outgoing.writeShort(45092);
				if ((int) (Math.random() * 2D) == 0)
					outgoing.writeShort(35784);

				outgoing.write_byte((int) (Math.random() * 256D));
				outgoing.write_byte(64);
				outgoing.write_byte(38);
				outgoing.writeShort((int) (Math.random() * 65536D));
				outgoing.writeShort((int) (Math.random() * 65536D));
				outgoing.put_length(outgoing.pos - i2);
			}*///remnant

            int factor = max_height * 192;
            if (factor > 0x17f00)
                factor = 0x17f00;

            if (factor < 32768)
                factor = 32768;

            if (factor > maximum_camera_tilt) {
                maximum_camera_tilt += (factor - maximum_camera_tilt) / 24;
                return;
            }
            if (factor < maximum_camera_tilt) {
                maximum_camera_tilt += (factor - maximum_camera_tilt) / 80;
            }

        } catch (Exception _ex) {
            addReportToServer(_ex.getMessage());
            SignLink.reporterror("Client.set_camera() : " + local_player.world_x + "," + local_player.world_y + "," + current_camera_pan + ","
                + current_camera_tilt + "," + region_x + "," + region_y + "," + next_region_start + "," + next_region_end);
            throw new RuntimeException("eek");
        }
    }

    public void processDrawing() {
        if (rsAlreadyLoaded || loadingError || genericLoadingError) {
            showErrorScreen();
            return;
        }
        if (!loggedIn)
            drawLoginScreen(false);
        else
            drawGameScreen();
        anInt1213 = 0;
    }

    private boolean check_username(String s) {
        if (s == null)
            return false;
        for (int i = 0; i < friendsCount; i++)
            if (s.equalsIgnoreCase(friendsList[i]))
                return true;
        return s.equalsIgnoreCase(local_player.username);
    }

    private static String get_level_diff(int local, int entity) {
        int dif = local - entity;
        if (dif < -9)
            return "<col=ff0000>";

        if (dif < -6)
            return "<col=ff3000>";

        if (dif < -3)
            return "<col=ff7000>";

        if (dif < 0)
            return "<col=ffb000>";

        if (dif > 9)
            return "<col=00FF00>";

        if (dif > 6)
            return "<col=40ff00>";

        if (dif > 3)
            return "<col=80ff00>";

        if (dif > 0)
            return "<col=c0ff00>";
        else
            return "<col=ffff00>";

    }

    private String objectMaps = "", floorMaps = "";
    Runtime runtime = Runtime.getRuntime();
    int clientMemory = (int) ((runtime.totalMemory() - runtime.freeMemory()) / 1024L);

    public String entityFeedName;
    public int entityFeedHP;
    public int entityFeedMaxHP;
    public int entityFeedHP2;
    public int entityAlpha;
    private int entityTick;

    public void pushFeed(String entityName, int HP, int maxHP) {
        entityFeedHP2 = entityFeedHP <= 0 ? entityFeedMaxHP : entityFeedHP;
        entityFeedName = entityName;
        entityFeedHP = HP;
        entityFeedMaxHP = maxHP;
        entityAlpha = 255;
        entityTick = entityName.isEmpty() ? 0 : 600;
    }

    private void displayEntityFeed() {
        if (entityFeedName == null)
            return;
        if (entityFeedHP == 0)
            return;
        if (entityTick-- <= 0)
            return;

        double percentage = entityFeedHP / (double) entityFeedMaxHP;
        double percentage2 = (entityFeedHP2 - entityFeedHP) / (double) entityFeedMaxHP;
        int width = (int) (135 * percentage);

        if (width > 132)
            width = 132;

        int xOff = 3;
        int yOff = 25;

        // background
        Rasterizer2D.fillRectangle(xOff, yOff, 141, 50, 0x4c433d, 155);
        Rasterizer2D.drawRectangle(xOff, yOff, 141, 50, 0x332f2d, 255);

        // name
        adv_font_small.draw_centered(entityFeedName, xOff + 69, yOff + 23, 0xFDFDFD, 0);

        // Hp fill
        Rasterizer2D.fillRectangle(xOff + 7, yOff + 32, width - 4, 12, 0x66b754, 130);
        Rasterizer2D.fillRectangle(xOff + 7, yOff + 32, width - 4, 12, 0x66b754, 130);

        // Hp empty
        Rasterizer2D.fillRectangle(xOff + 4 + width, yOff + 32, 135 - width - 4, 12, 0xc43636, 130);

        if (entityAlpha > 0) {
            entityAlpha -= 5;
            Rasterizer2D.fillRectangle(xOff + 4 + width, yOff + 32, (int) (135 * percentage2) - 4, 12, 0xFFDB00, (int) (130 * entityAlpha / 255.0));
        }

        Rasterizer2D.drawRectangle(xOff + 7, yOff + 32, 128, 12, 0x332f2d, 130);

        // HP text
        adv_font_small.draw_centered(NumberFormat.getInstance(Locale.US).format(entityFeedHP) + " / "
            + NumberFormat.getInstance(Locale.US).format(entityFeedMaxHP), xOff + 72, yOff + 44, 0xFDFDFD, 0);
    }

    private void displayHits() {
        ArrayList<IncomingHit> temp = new ArrayList<>(expectedHit);
        for (int index = 0; index < temp.size(); index++) {
            if (index >= expectedHit.size())
                continue;

            final int addPos = 1;
            final IncomingHit hit = expectedHit.get(index);
            final int damage = hit.damage;
            //System.out.println("damage "+damage);
            final int color = (damage > 0) ? 16711680 : 65535;
            int opacity = 255 - hit.pos * 2;
            if (opacity < 0) {
                opacity = 0;
                expectedHit.remove(hit);
            }

            hit.incrementPos(addPos);
            int yOffset = ((index + 1) * 20) + hit.pos;
            spriteCache.get((damage > 0) ? 345 : 346).draw_transparent(448 + ((damage > 0) ? 0 : 3), 52 + yOffset, opacity);
            adv_font_bold.draw(Integer.toString(damage), 488, 68 + yOffset, color, (damage > 0) ? 3407872 : 100, opacity);
        }
    }

    private void draw3dScreen() {
        try {
            boolean fixed = screen == ScreenMode.FIXED;
            if (setting.show_exp_counter) {
                ExpCounter.drawExperienceCounter();
            }

            if (setting.show_hit_predictor) {
                displayHits();
            }

            if (showChatComponents) {
                drawSplitPrivateChat();
            }

            if (fadingScreen != null) {
                fadingScreen.draw();
            }

            if (broadcast != null && isDisplayed) {
                broadcast.process();
            }

            // Effect timers
            if (setting.draw_timers) {
                drawEffectTimers();
            }

            if (setting.draw_health_overlay) {
                displayEntityFeed();
            }

            if (startSpin) {
                startSpinner();
            }

            if (crossType == 1) {
                int offSet = fixed ? 4 : 0;
                crosses[crossIndex / 100].drawSprite(crossX - 8 - offSet, crossY - 8 - offSet);
                anInt1142++;
                if (anInt1142 > 67) {
                    anInt1142 = 0;
                    // sendPacket(new ClearMinimapFlag()); //Not server-sided, flag is only handled
                    // in the client
                }
            }
            if (crossType == 2) {
                int offSet = fixed ? 4 : 0;
                crosses[4 + crossIndex / 100].drawSprite(crossX - 8 - offSet, crossY - 8 - offSet);
            }
            if (openWalkableInterface != -1) {
                try {
                    processWidgetAnimations(animation_step, openWalkableInterface);
                    Widget rsinterface = Widget.cache[openWalkableInterface];
                    if (openWalkableInterface == 21100 && lobbyTimer.secondsRemaining() > 0) {
                        drawLobbyTimer();
                    }
                    if (fixed) {
                        drawInterface(rsinterface, 0, 0, 0);
                    } else {
                        Widget r = Widget.cache[openWalkableInterface];
                        int x = window_width - 215;
                        x -= r.width;
                        int min_y = Integer.MAX_VALUE;
                        for (int i = 0; i < r.children.length; i++) {
                            min_y = Math.min(min_y, r.child_y[i]);
                        }

                        // barrows kc interface
                        if (openWalkableInterface == 4535) {
                            if (screen == ScreenMode.RESIZABLE) {
                                //System.out.println("Window height is: " + window_height);
                                drawInterface(rsinterface, (window_height > 685) ? x + 100 : x - 20, 30, 0);
                            } else {
                                drawInterface(rsinterface, 0, 0, 0);
                            }
                        } else if (openWalkableInterface == 23300 && screen != ScreenMode.FIXED) {
                            drawInterface(rsinterface, 16, 0, 0);
                        } else {
                            drawInterface(Widget.cache[openWalkableInterface], x, 0 - min_y + 10, 0);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    addReportToServer(ex.getMessage());
                }
            }

            if (widget_overlay_id != -1) {
                try {
                    processWidgetAnimations(animation_step, widget_overlay_id);
                    int w = 512, h = 334;
                    int x = screen == ScreenMode.FIXED ? 0 : (window_width / 2) - 256;
                    int y = screen == ScreenMode.FIXED ? 0 : (window_height / 2) - 167;
                    int count = settings[ConfigUtility.SIDE_STONES_ARRANGEMENT_ID] == 1 ? 3 : 4;
                    if (screen != ScreenMode.FIXED) {
                        for (int i = 0; i < count; i++) {
                            if (x + w > (window_width - 225)) {
                                x = x - 30;
                                if (x < 0) {
                                    x = 0;
                                }
                            }
                            if (y + h > (window_height - 182)) {
                                y = y - 30;
                                if (y < 0) {
                                    y = 0;
                                }
                            }
                        }
                    }
                    // barrows kc interface
                    if (widget_overlay_id == 4335) {
                        drawInterface(Widget.cache[widget_overlay_id], 0, 0, 0);
                    }
                    drawInterface(Widget.cache[widget_overlay_id],
                        screen == ScreenMode.FIXED ? 0 : (window_width / 2) - 356,
                        screen == ScreenMode.FIXED ? 0 : (window_height / 2) - 230, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // System.out.println("widget overlay id: " + widget_overlay_id);

            if (!menuOpen) {
                processRightClick();
                drawTooltip();
            } else if (menuScreenArea == 0) {
                drawMenu(screen == ScreenMode.FIXED ? 4 : 0, screen == ScreenMode.FIXED ? 4 : 0);
            }

            // Multi sign
            if (multicombat == 1) {
                multiOverlay.drawSprite(fixed ? 445 : 480, window_height - 200);
            }

            if (broadcastText != null && !broadcastText.isEmpty()) {
                adv_font_regular.draw(broadcastText, 0, systemUpdateTime != 0 ? 316 : 329, 0xffff00, 0);
            }

            if ((widget_overlay_id == -1 || ClientConstants.FORCE_OVERLAY_ABOVE_WIDGETS) && (setting.draw_fps || ClientConstants.CLIENT_DATA)) {
                int x = window_width - 750;
                int y = 20;
                if (setting.draw_fps) {
                    int rgb = 0xFFFF00;
                    if (super.fps < 15) {
                        rgb = 0xff0000;
                    }
                    adv_font_small.draw("Fps:  " + super.fps, x, y, rgb, true);
                    y += 15;
                    //Update memory display every 30 ticks.
                    if (game_tick % 30 == 0) {
                        clientMemory = (int) ((runtime.totalMemory() - runtime.freeMemory()) / 1024L);
                    }

                    int memoryColour;
                    if (clientMemory > 350_000 && clientMemory < 450_000) {
                        memoryColour = 0xff8624;
                    } else if (clientMemory > 450_000) {
                        memoryColour = 0xff0000;
                    } else {
                        memoryColour = 0xffff00;
                    }
                    adv_font_small.draw("Memory: " + NumberFormat.getInstance().format(clientMemory) + "k", x, y, memoryColour, 40);
                }

                if (ClientConstants.CLIENT_DATA) {
                    int playerX = next_region_start + (local_player.world_x - 6 >> 7);
                    int playerY = next_region_end + (local_player.world_y - 6 >> 7);
                    adv_font_small.draw("Coords: " + playerX + ", " + playerY, x, y + 15, 0xffff00, 40);
                    adv_font_small.draw("Resolution: " + window_width + "x" + window_height, x, y + 30, 0xffff00, 40);
                    adv_font_small.draw("Build: " + ClientConstants.CLIENT_VERSION, x, y + 45, 0xffff00, 40);
                    String world = ClientConstants.PVP_MODE ? "PvP" : "Eco";
                    adv_font_small.draw("World: " + world, x, y + 60, 0xffff00, 40);
                    adv_font_small.draw("Mouse X: " + cursor_x, x, y + 75, 0xffff00, 40);
                    adv_font_small.draw("Mouse Y: " + cursor_y, x, y + 90, 0xffff00, 40);
                    adv_font_small.draw("Frame Width: " + window_width + ", Frame Height: " + window_height, x, y + 105, 0xffff00, 40);
                    adv_font_small.draw("Object Maps: " + objectMaps, x, y + 120, 0xffff00, 40);
                    adv_font_small.draw("Floor Maps: " + floorMaps, x, y + 135, 0xffff00, 40);
                    adv_font_small.draw("Zoom: " + zoom_distance, x, y + 150, 0xffff00, 40);
                }
            }

            if (systemUpdateTime != 0) {
                int seconds = systemUpdateTime / 50;
                int minutes = seconds / 60;
                int yOffset = screen == ScreenMode.FIXED ? 0 : window_height - 498;
                if (this.broadcast != null && isDisplayed) {
                    yOffset -= 20;
                }
                seconds %= 60;
                adv_font_regular.draw("System update in: " + minutes + ":" + (seconds < 10 ? "0" : "") + seconds, 4, 329 + yOffset, 0xffff00, false);
                anInt849++;
                if (anInt849 > 75) {
                    anInt849 = 0;
                    // unknown (system updating)
                    // outgoing.writeOpcode(148);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            addReportToServer(e.getMessage());
        }
    }

    private void addIgnore(String name) {
        try {
            if (name == null)
                return;

            if (ignoreCount >= 100) {
                sendMessage("Your ignore list is full. Max of 100 hit", 0, "");
                return;
            }
            /// YOU HAVE TO KEEP THIS LOGIC OTHERWISE CLIENT WILL DESYNC
            for (int j = 0; j < ignoreCount; j++)
                if (ignoreList[j].equalsIgnoreCase(name)) {
                    sendMessage(name + " is already on your ignore list", 0, "");
                    return;
                }
            for (int k = 0; k < friendsCount; k++)
                if (friendsList[k].equalsIgnoreCase(name)) {
                    sendMessage("Please remove " + name + " from your friend list first", 0, "");
                    return;
                }

            ignoreList[ignoreCount] = name;
            ignoreListAsLongs[ignoreCount++] = StringUtils.encodeBase37(name);
            packetSender.sendIgnoreAddition(name);
            return;
        } catch (RuntimeException runtimeexception) {
            SignLink.reporterror("45688, " + name + ", " + 4 + ", " + runtimeexception.toString());
        }
        throw new RuntimeException();
    }

    private void updatePlayerInstances() {
        for (int index = -1; index < players_in_region; index++) {

            int playerIndex;

            if (index == -1) {
                playerIndex = LOCAL_PLAYER_INDEX;
            } else {
                playerIndex = local_players[index];
            }

            Player player = players[playerIndex];

            if (player != null) {
                entityUpdateBlock(player);
            }
        }
    }

    private void checkRadioOptions(int second) {
        for (int ids : Widget.radioButtons) {
            if (Widget.cache[ids].radioID == Widget.cache[second].radioID && ids != second) {
                Widget.cache[ids].disabledSprite = spriteCache.get(166);
                Widget.cache[ids].enabledSprite = spriteCache.get(166);
            }
        }
        if (Widget.cache[second].disabledSprite == spriteCache.get(167)) {
            Widget.cache[second].disabledSprite = spriteCache.get(169);
            Widget.cache[second].enabledSprite = spriteCache.get(169);
            return;
        }
        if (Widget.cache[second].disabledSprite == spriteCache.get(170)) {
            Widget.cache[second].disabledSprite = spriteCache.get(171);
            Widget.cache[second].enabledSprite = spriteCache.get(171);
            return;
        }
        if (Widget.cache[second].disabledSprite == spriteCache.get(172)) {
            Widget.cache[second].disabledSprite = spriteCache.get(169);
            Widget.cache[second].enabledSprite = spriteCache.get(169);
            return;
        }
    }

    private void method115() {
        if (loading_phase == 2) {
            for (SpawnedObject spawnedObject = (SpawnedObject) spawns
                .first(); spawnedObject != null; spawnedObject = (SpawnedObject) spawns
                .next()) {
                if (spawnedObject.getLongetivity > 0)
                    spawnedObject.getLongetivity--;
                if (spawnedObject.getLongetivity == 0) {
                    if (spawnedObject.getPreviousId < 0
                        || Region.cached(spawnedObject.getPreviousId, spawnedObject.previousType)) {
                        clear_object(spawnedObject.y, spawnedObject.plane, spawnedObject.previousOrientation,
                            spawnedObject.previousType, spawnedObject.x, spawnedObject.group,
                            spawnedObject.getPreviousId);
                        spawnedObject.remove();
                    }
                } else {
                    if (spawnedObject.delay > 0)
                        spawnedObject.delay--;
                    if (spawnedObject.delay == 0 && spawnedObject.x >= 1 && spawnedObject.y >= 1
                        && spawnedObject.x <= 102 && spawnedObject.y <= 102
                        && (spawnedObject.id < 0 || Region.cached(spawnedObject.id, spawnedObject.type))) {
                        clear_object(spawnedObject.y, spawnedObject.plane, spawnedObject.orientation,
                            spawnedObject.type, spawnedObject.x, spawnedObject.group, spawnedObject.id);
                        spawnedObject.delay = -1;
                        if (spawnedObject.id == spawnedObject.getPreviousId && spawnedObject.getPreviousId == -1)
                            spawnedObject.remove();
                        else if (spawnedObject.id == spawnedObject.getPreviousId
                            && spawnedObject.orientation == spawnedObject.previousOrientation
                            && spawnedObject.type == spawnedObject.previousType)
                            spawnedObject.remove();
                    }
                }
            }

        }
    }

    private void determineMenuSize() {
        int width = adv_font_bold.get_width("Choose option");
        for (int row = 0; row < menuActionRow; row++) {
            int actionLength = adv_font_bold.get_width(menuActionText[row]);
            if (width < actionLength) {
                width = actionLength;
            }
        }
        width += 8;
        int height = 15 * menuActionRow + 21;
        int x = super.click_x - width / 2;
        if (x + width > window_width)
            x = window_width - width;

        if (x < 0)
            x = 0;

        int y = super.click_y;
        if (y + height > window_height)
            y = window_height - height;

        if (y < 0)
            y = 0;

        menuOpen = true;
        menuOffsetX = x;
        menuOffsetY = y;
        menuWidth = width;
        menuHeight = 15 * menuActionRow + 22;
    }

    private boolean isMouseWithin(int minX, int maxX, int minY, int maxY) {
        return super.cursor_x >= minX && super.cursor_x <= maxX && super.cursor_y >= minY && super.cursor_y <= maxY;
    }

    private void updateLocalPlayerMovement(Buffer stream) {
        stream.initBitAccess();

        int update = stream.readBits(1);

        if (update == 0) {
            return;
        }

        int type = stream.readBits(2);
        if (type == 0) {
            mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = LOCAL_PLAYER_INDEX;
            return;
        }
        if (type == 1) {
            int direction = stream.readBits(3);
            local_player.moveInDir(false, direction);
            int updateRequired = stream.readBits(1);

            if (updateRequired == 1) {
                mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = LOCAL_PLAYER_INDEX;
            }
            return;
        }
        if (type == 2) {
            int firstDirection = stream.readBits(3);
            local_player.moveInDir(true, firstDirection);

            int secondDirection = stream.readBits(3);
            local_player.moveInDir(true, secondDirection);

            int updateRequired = stream.readBits(1);

            if (updateRequired == 1) {
                mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = LOCAL_PLAYER_INDEX;
            }
            return;
        }
        if (type == 3) {
            plane = stream.readBits(2);

            // Fix for height changes
            if (lastKnownPlane != plane) {
                loading_phase = 1;
            }
            lastKnownPlane = plane;

            int teleport = stream.readBits(1);
            int updateRequired = stream.readBits(1);

            if (updateRequired == 1) {
                mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = LOCAL_PLAYER_INDEX;
            }

            int y = stream.readBits(7);
            int x = stream.readBits(7);

            local_player.setPos(x, y, teleport == 1);
        }
    }

    private void nullLoader() {
        update_flame_components = false;
        while (drawingFlames) {
            update_flame_components = false;
            try {
                Thread.sleep(50L);
            } catch (Exception _ex) {
            }
        }
        titleBoxIndexedImage = null;
        titleButtonIndexedImage = null;
        titleIndexedImages = null;
        anIntArray850 = null;
        anIntArray851 = null;
        anIntArray852 = null;
        anIntArray853 = null;
        anIntArray1190 = null;
        anIntArray1191 = null;
        anIntArray828 = null;
        anIntArray829 = null;
        flameLeftSprite = null;
        flameRightSprite = null;
    }

    private boolean processWidgetAnimations(int tick, int interfaceId) throws Exception {
        boolean redrawRequired = false;
        Widget widget = Widget.cache[interfaceId];

        if (widget == null || widget.children == null) {
            return false;
        }

        for (int element : widget.children) {
            if (element == -1) {
                break;
            }

            Widget child = Widget.cache[element];

            if (child.type == Widget.TYPE_MODEL_LIST) {
                redrawRequired |= processWidgetAnimations(tick, child.id);
            }

            if (child.type == 6 && (child.defaultAnimationId != -1 || child.secondaryAnimationId != -1)) {
                boolean updated = interfaceIsSelected(child);

                int animationId = updated ? child.secondaryAnimationId : child.defaultAnimationId;

                if (animationId != -1) {
                    Sequence animation = Sequence.cache[animationId];
                    for (child.lastFrameTime += tick; child.lastFrameTime > animation.get_length(child.currentFrame); ) {
                        child.lastFrameTime -= animation.get_length(child.currentFrame) + 1;
                        child.currentFrame++;
                        if (child.currentFrame >= animation.frames) {
                            child.currentFrame -= animation.step;
                            if (child.currentFrame < 0 || child.currentFrame >= animation.frames)
                                child.currentFrame = 0;
                        }
                        redrawRequired = true;
                    }

                }
            }
        }

        return redrawRequired;
    }

    private int get_visible_planes() {
        if (settings[ConfigUtility.ROOF_REMOVAL_ID] == 1)
            return plane;
        int j = 3;
        if (cam_curve_y < 310) {
            int k = camera_abs_x >> 7;
            int l = camera_abs_y >> 7;
            int i1 = local_player.world_x >> 7;
            int j1 = local_player.world_y >> 7;
            if ((tileFlags[plane][k][l] & 4) != 0)
                j = plane;
            int k1;
            if (i1 > k)
                k1 = i1 - k;
            else
                k1 = k - i1;
            int l1;
            if (j1 > l)
                l1 = j1 - l;
            else
                l1 = l - j1;
            if (k1 > l1) {
                int i2 = (l1 * 0x10000) / k1;
                int k2 = 32768;
                while (k != i1) {
                    if (k < i1)
                        k++;
                    else if (k > i1)
                        k--;
                    if ((tileFlags[plane][k][l] & 4) != 0)
                        j = plane;
                    k2 += i2;
                    if (k2 >= 0x10000) {
                        k2 -= 0x10000;
                        if (l < j1)
                            l++;
                        else if (l > j1)
                            l--;
                        if ((tileFlags[plane][k][l] & 4) != 0)
                            j = plane;
                    }
                }
            } else {
                int j2 = (k1 * 0x10000) / l1;
                int l2 = 32768;
                while (l != j1) {
                    if (l < j1)
                        l++;
                    else if (l > j1)
                        l--;
                    if ((tileFlags[plane][k][l] & 4) != 0)
                        j = plane;
                    l2 += j2;
                    if (l2 >= 0x10000) {
                        l2 -= 0x10000;
                        if (k < i1)
                            k++;
                        else if (k > i1)
                            k--;
                        if ((tileFlags[plane][k][l] & 4) != 0)
                            j = plane;
                    }
                }
            }
        }
        if ((tileFlags[plane][local_player.world_x >> 7][local_player.world_y >> 7] & 4) != 0)
            j = plane;
        return j;
    }

    private int get_cutscene_planes() {
        int orientation = get_tile_pos(plane, camera_abs_y, camera_abs_x);
        if (orientation - camera_abs_z < 800 && (tileFlags[plane][camera_abs_x >> 7][camera_abs_y >> 7] & 4) != 0)
            return plane;
        else
            return 3;
    }

    private void removeFriend(String name) {
        if (name == null)
            return;
        packetSender.sendFriendDeletion(name);
        // Fallback incase a name exists client-sided but not in server.
        for (int i = 0; i < friendsCount; i++) {
            if (!friendsList[i].equalsIgnoreCase(name)) {
                continue;
            }

            friendsCount--;
            for (int n = i; n < friendsCount; n++) {
                friendsList[n] = friendsList[n + 1];
                friendsNodeIDs[n] = friendsNodeIDs[n + 1];
                friendsListAsLongs[n] = friendsListAsLongs[n + 1];
            }
            break;
        }
    }

    private void removeIgnore(String name) {
        if (name == null)
            return;
        packetSender.sendIgnoreDeletion(name);
        for (int index = 0; index < ignoreCount; index++) {
            if (ignoreList[index].equalsIgnoreCase(name)) {
                ignoreCount--;
                System.arraycopy(ignoreList, index + 1, ignoreList, index, ignoreCount - index);
                break;
            }
        }
    }

    public String widget_tooltip_text_script(String text, Widget child) {
        if (text.indexOf("%") != -1) {
            int script_id;
            for (script_id = 1; script_id <= 5; script_id++) {
                while (true) {
                    int index = text.indexOf("%" + script_id);
                    if (index == -1) {
                        break;
                    }
                    text = text.substring(0, index) + interfaceIntToString(executeScript(child, script_id - 1))
                        + text.substring(index + 2);
                }
            }
        }
        return text;
    }

    public int int_to_percent;

    public final int calcHitpointColor(Entity entity) {
        int_to_percent = (int) (((double) entity.current_hitpoints / (double) entity.maximum_hitpoints) * 100D);
        return (int_to_percent >= 75 && int_to_percent <= Integer.MAX_VALUE ? 65280
            : int_to_percent < 75 && int_to_percent >= 50 ? 16776960
            : int_to_percent < 50 && int_to_percent >= 25 ? 16557575 : 16059661);
    }

    private int executeScript(Widget widget, int id) {
        if (widget.valueIndexArray == null || id >= widget.valueIndexArray.length)
            return -2;
        try {
            int script[] = widget.valueIndexArray[id];
            int accumulator = 0;
            int counter = 0;
            int operator = 0;
            do {
                int instruction = script[counter++];
                int value = 0;
                byte next = 0;

                if (instruction == 0) {
                    return accumulator;
                }

                if (instruction == 1) {
                    value = currentLevels[script[counter++]];
                }

                if (instruction == 2) {
                    value = maximumLevels[script[counter++]];
                }

                if (instruction == 3) {
                    value = currentExp[script[counter++]];
                }

                if (instruction == 4) {
                    Widget other = Widget.cache[script[counter++]];
                    int item = script[counter++];
                    if (item >= 0 && item < ItemDefinition.length
                        && (!ItemDefinition.get(item).membership_required || isMembers)) {
                        for (int slot = 0; slot < other.inventoryItemId.length; slot++) {
                            if (other.inventoryItemId[slot] == item + 1) {
                                value += other.inventoryAmounts[slot];
                            }

                            if (other.inventoryItemId[slot] == CustomItemIdentifiers.VENGEANCE_SKULL + 1) {
                                if ((vengeance[0][0] + 1) == (item + 1)) {
                                    value += vengeance[0][1];
                                }
                                if ((vengeance[1][0] + 1) == (item + 1)) {
                                    value += vengeance[1][1];
                                }
                                if ((vengeance[2][0] + 1) == (item + 1)) {
                                    value += vengeance[2][1];
                                }
                            }

                            //Blighted ancient ice sack
                            if (other.inventoryItemId[slot] == 24607 + 1) {
                                if ((iceSack[0][0] + 1) == (item + 1)) {
                                    value += iceSack[0][1];
                                }
                                if ((iceSack[1][0] + 1) == (item + 1)) {
                                    value += iceSack[1][1];
                                }
                                if ((iceSack[2][0] + 1) == (item + 1)) {
                                    value += iceSack[2][1];
                                }
                            }

                            //Blighted bind sack
                            if (other.inventoryItemId[slot] == 24609 + 1) {
                                if ((bindSack[0][0] + 1) == (item + 1)) {
                                    value += bindSack[0][1];
                                }
                                if ((bindSack[1][0] + 1) == (item + 1)) {
                                    value += bindSack[1][1];
                                }
                                if ((bindSack[2][0] + 1) == (item + 1)) {
                                    value += bindSack[2][1];
                                }
                            }

                            //Blighted snare sack
                            if (other.inventoryItemId[slot] == 24611 + 1) {
                                if ((snareSack[0][0] + 1) == (item + 1)) {
                                    value += snareSack[0][1];
                                }
                                if ((snareSack[1][0] + 1) == (item + 1)) {
                                    value += snareSack[1][1];
                                }
                                if ((snareSack[2][0] + 1) == (item + 1)) {
                                    value += snareSack[2][1];
                                }
                            }

                            //Blighted entangle sack
                            if (other.inventoryItemId[slot] == 24613 + 1) {
                                if ((entangleSack[0][0] + 1) == (item + 1)) {
                                    value += entangleSack[0][1];
                                }
                                if ((entangleSack[1][0] + 1) == (item + 1)) {
                                    value += entangleSack[1][1];
                                }
                                if ((entangleSack[2][0] + 1) == (item + 1)) {
                                    value += entangleSack[2][1];
                                }
                            }

                            //Blighted teleport sack
                            if (other.inventoryItemId[slot] == 24615 + 1) {
                                if ((teleportSack[0][0] + 1) == (item + 1)) {
                                    value += teleportSack[0][1];
                                }
                                if ((teleportSack[1][0] + 1) == (item + 1)) {
                                    value += teleportSack[1][1];
                                }
                                if ((teleportSack[2][0] + 1) == (item + 1)) {
                                    value += teleportSack[2][1];
                                }
                            }

                            //Blighted vengeance sack
                            if (other.inventoryItemId[slot] == 24621 + 1) {
                                if ((vengeance[0][0] + 1) == (item + 1)) {
                                    value += vengeance[0][1];
                                }
                                if ((vengeance[1][0] + 1) == (item + 1)) {
                                    value += vengeance[1][1];
                                }
                                if ((vengeance[2][0] + 1) == (item + 1)) {
                                    value += vengeance[2][1];
                                }
                            }
                        }

                        if ((runePouch[0][0] + 1) == (item + 1)) { // match ID of rune which the spell uses with the one
                            // in RP
                            value += runePouch[0][1];
                        }
                        if ((runePouch[1][0] + 1) == (item + 1)) {
                            value += runePouch[1][1];
                        }
                        if ((runePouch[2][0] + 1) == (item + 1)) {
                            value += runePouch[2][1];
                        }
                        if ((runePouch[3][0] + 1) == (item + 1)) {
                            value += runePouch[3][1];
                        }
                    }
                }
                if (instruction == 5) {
                    value = settings[script[counter++]];
                }

                if (instruction == 6) {
                    value = SKILL_EXPERIENCE[maximumLevels[script[counter++]] - 1];
                }

                if (instruction == 7) {
                    value = (settings[script[counter++]] * 100) / 46875;
                }

                if (instruction == 8) {
                    value = local_player.combat_level;
                }

                if (instruction == 9) {
                    for (int skill = 0; skill < SkillConstants.SKILL_COUNT; skill++)
                        if (SkillConstants.ENABLED_SKILLS[skill])
                            value += maximumLevels[skill];
                }

                if (instruction == 10) {
                    Widget other = Widget.cache[script[counter++]];
                    int item = script[counter++] + 1;
                    if (item >= 0 && item < ItemDefinition.length && isMembers) {
                        for (int stored = 0; stored < other.inventoryItemId.length; stored++) {
                            if (other.inventoryItemId[stored] != item)
                                continue;
                            value = 0x3b9ac9ff;
                            break;
                        }

                    }
                }

                if (instruction == 11) {
                    value = runEnergy;
                }

                if (instruction == 12) {
                    value = weight;
                }

                if (instruction == 13) {
                    int bool = settings[script[counter++]];
                    int shift = script[counter++];
                    value = (bool & 1 << shift) == 0 ? 0 : 1;
                }

                if (instruction == 14) {
                    int index = script[counter++];
                    VariableBits bits = VariableBits.cache[index];
                    int setting = bits.configId;
                    int low = bits.leastSignificantBit;
                    int high = bits.mostSignificantBit;
                    int mask = BIT_MASKS[high - low];
                    value = settings[setting] >> low & mask;
                }

                if (instruction == 15) {
                    next = 1;
                }

                if (instruction == 16) {
                    next = 2;
                }

                if (instruction == 17) {
                    next = 3;
                }

                if (instruction == 18) {
                    value = (local_player.world_x >> 7) + next_region_start;
                }

                if (instruction == 19) {
                    value = (local_player.world_y >> 7) + next_region_end;
                }

                if (instruction == 20) {
                    value = script[counter++];
                }

                if (instruction == 21) {
                    value = tabAmounts[script[counter++]];
                }

                if (instruction == 22) {
                    Widget class9_1 = Widget.cache[script[counter++]];
                    int initAmount = class9_1.inventoryItemId.length;
                    for (int j3 = 0; j3 < class9_1.inventoryItemId.length; j3++) {
                        if (class9_1.inventoryItemId[j3] <= 0) {
                            initAmount--;
                        }
                    }
                    value += initAmount;
                }

                if (next == 0) {

                    if (operator == 0) {
                        accumulator += value;
                    }

                    if (operator == 1) {
                        accumulator -= value;
                    }

                    if (operator == 2 && value != 0) {
                        accumulator /= value;
                    }

                    if (operator == 3) {
                        accumulator *= value;
                    }
                    operator = 0;
                } else {
                    operator = next;
                }
            } while (true);
        } catch (Exception _ex) {
            _ex.printStackTrace();
            addReportToServer(_ex.getMessage());
            return -1;
        }
    }

    private void drawTooltip() {
        if (widget_overlay_id == 16244) {
            return;
        }

        if (menuActionRow < 2 && item_highlighted == 0 && widget_highlighted == 0) {
            return;
        }

        if (item_highlighted == 1 && menuActionRow < 2) {
            tooltip = "Use " + selectedItemName + " with...";
        } else if (widget_highlighted == 1 && menuActionRow < 2) {
            tooltip = selected_target_id + "...";
        } else {
            tooltip = menuActionText[menuActionRow - 1];
        }

        if (menuActionRow > 2) {
            tooltip = tooltip + "<col=ffffff> / " + (menuActionRow - 2) + " more options";
        }

        if (tooltip.contains("<br>")) {
            tooltip = tooltip.replaceAll("<br>", " ");
        }

        adv_font_bold.draw(tooltip, 4, 15, 0xffffff, 0);
    }

    private void markMinimap(SimpleImage sprite, int x, int y) {
        if (sprite == null) {
            return;
        }
        int angle = camera_pan + map_rotation & 0x7ff;
        int l = x * x + y * y;
        if (l > 6400) {
            return;
        }
        int sineAngle = Model.SINE[angle];
        int cosineAngle = Model.COSINE[angle];
        sineAngle = (sineAngle * 256) / (map_zoom + 256);
        cosineAngle = (cosineAngle * 256) / (map_zoom + 256);
        int spriteOffsetX = y * sineAngle + x * cosineAngle >> 16;
        int spriteOffsetY = y * cosineAngle - x * sineAngle >> 16;
        if (screen == ScreenMode.FIXED) {
            sprite.drawSprite(((94 + spriteOffsetX) - sprite.max_width / 2) + 4 + 30,
                83 - spriteOffsetY - sprite.max_height / 2 - 4 + 5);
        } else {
            sprite.drawSprite(((77 + spriteOffsetX) - sprite.max_width / 2) + 4 + 5 + (window_width - 167),
                85 - spriteOffsetY - sprite.max_height / 2);
        }
    }

    private void drawMinimap() {
        if (screen == ScreenMode.FIXED) {
            minimapImageProducer.init();
        }
        if (minimapState == 2) {
            if (screen == ScreenMode.FIXED) {
                // ken comment, let's fix barrows minimap and any other occurrences of minimap
                // state 2. Simply clear the 2D rasterizer to set the minimap to black.
                Rasterizer2D.clear();
                spriteCache.get(19).drawSprite(0, 0);
            } else {
                spriteCache.get(44).drawSprite(window_width - 181, 0);
                spriteCache.get(45).drawSprite(window_width - 158, 7);
            }

            if (screen != ScreenMode.FIXED && settings[ConfigUtility.SIDE_STONES_ARRANGEMENT_ID] == 1) {
                if (super.cursor_x >= window_width - 26 && super.cursor_x <= window_width - 1 && super.cursor_y >= 2
                    && super.cursor_y <= 24 || sidebarId == 15) {
                    spriteCache.get(27).drawSprite(window_width - 25, 2);
                } else {
                    spriteCache.get(27).drawAdvancedSprite(window_width - 25, 2, 165);
                }
            }
            if (settings[ConfigUtility.DATA_ORBS_ID] == 1) {
                loadAllOrbs(screen == ScreenMode.FIXED ? 0 : window_width - 217);
            }
            compass.rotate_raster(33, camera_pan, anIntArray1057, 256, anIntArray968, (screen == ScreenMode.FIXED ? 25 : 24),
                4, (screen == ScreenMode.FIXED ? 29 : window_width - 176), 33, 25);
            if (menuOpen) {
                drawMenu(screen == ScreenMode.FIXED ? 516 : 0, 0);
            }
            if (screen == ScreenMode.FIXED) {
                minimapImageProducer.init();
            }
            return;
        }
        int angle = camera_pan + map_rotation & 0x7ff;
        int centreX = 48 + local_player.world_x / 32;
        int centreY = 464 - local_player.world_y / 32;
        minimapImage.rotate_raster(151, angle, minimapLineWidth, 256 + map_zoom, minimapLeft, centreY,
            (screen == ScreenMode.FIXED ? 9 : 7), (screen == ScreenMode.FIXED ? 54 : window_width - 158), 146,
            centreX);

        for (int icon = 0; icon < objectIconCount; icon++) {
            int mapX = (minimapHintX[icon] * 4 + 2) - local_player.world_x / 32;
            int mapY = (minimapHintY[icon] * 4 + 2) - local_player.world_y / 32;
            markMinimap(minimapHint[icon], mapX, mapY);
        }
        for (int x = 0; x < 104; x++) {
            for (int y = 0; y < 104; y++) {
                LinkedList class19 = scene_items[plane][x][y];
                if (class19 != null) {
                    int mapX = (x * 4 + 2) - local_player.world_x / 32;
                    int mapY = (y * 4 + 2) - local_player.world_y / 32;
                    markMinimap(mapDotItem, mapX, mapY);
                }
            }
        }
        for (int n = 0; n < npcs_in_region; n++) {
            Npc npc = npcs[local_npcs[n]];
            if (npc != null && npc.visible()) {
                NpcDefinition entityDef = npc.desc;
                if (entityDef.configs != null) {
                    entityDef = entityDef.get_configs();
                }
                if (entityDef != null && entityDef.renderOnMinimap && entityDef.isClickable) {
                    int mapX = npc.world_x / 32 - local_player.world_x / 32;
                    int mapY = npc.world_y / 32 - local_player.world_y / 32;
                    markMinimap(mapDotNPC, mapX, mapY);
                }
            }
        }
        for (int p = 0; p < players_in_region; p++) {
            Player player = players[local_players[p]];
            if (player != null && player.visible()) {
                int mapX = player.world_x / 32 - local_player.world_x / 32;
                int mapY = player.world_y / 32 - local_player.world_y / 32;
                boolean friend = false;
                boolean clanMember = false;

                for (String s : clanList) {
                    if (s == null) {
                        continue;
                    }
                    if (!s.equalsIgnoreCase(player.username)) {
                        continue;
                    }
                    clanMember = true;
                    break;
                }

                //System.out.println(clanMember+" list: "+ Arrays.toString(Arrays.stream(clanList).toArray()));

                long nameHash = StringUtils.encodeBase37(player.username);
                for (int f = 0; f < friendsCount; f++) {
                    if (nameHash != friendsListAsLongs[f] || friendsNodeIDs[f] == 0) {
                        continue;
                    }
                    friend = true;
                    break;
                }
                boolean team = false;
                if (local_player.team_id != 0 && player.team_id != 0 && local_player.team_id == player.team_id) {
                    team = true;
                }
                if (clanMember) {
                    markMinimap(mapDotClan, mapX, mapY);
                } else if (friend) {
                    markMinimap(mapDotFriend, mapX, mapY);
                } else if (team) {
                    markMinimap(mapDotTeam, mapX, mapY);
                } else {
                    markMinimap(mapDotPlayer, mapX, mapY);
                }
            }
        }
        if (hintIconDrawType != 0 && game_tick % 20 < 10) {
            if (hintIconDrawType == 1 && hintIconNpcId >= 0 && hintIconNpcId < npcs.length) {
                Npc npc = npcs[hintIconNpcId];
                if (npc != null) {
                    int mapX = npc.world_x / 32 - local_player.world_x / 32;
                    int mapY = npc.world_y / 32 - local_player.world_y / 32;
                    refreshMinimap(mapMarker, mapY, mapX);
                }
            }
            if (hintIconDrawType == 2) {
                int mapX = ((hintIconX - next_region_start) * 4 + 2) - local_player.world_x / 32;
                int mapY = ((hintIconY - next_region_end) * 4 + 2) - local_player.world_y / 32;
                refreshMinimap(mapMarker, mapY, mapX);
            }
            if (hintIconDrawType == 10 && hintIconPlayerId >= 0 && hintIconPlayerId < players.length) {
                Player player = players[hintIconPlayerId];
                if (player != null) {
                    int mapX = player.world_x / 32 - local_player.world_x / 32;
                    int mapY = player.world_y / 32 - local_player.world_y / 32;
                    refreshMinimap(mapMarker, mapY, mapX);
                }
            }
        }
        if (travel_destination_x != 0) {
            int mapX = (travel_destination_x * 4 + 2) - local_player.world_x / 32;
            int mapY = (travel_destination_y * 4 + 2) - local_player.world_y / 32;
            markMinimap(mapFlag, mapX, mapY);
        }
        Rasterizer2D.draw_filled_rect((screen == ScreenMode.FIXED ? 127 : window_width - 88),
            (screen == ScreenMode.FIXED ? 83 : 80), 3, 3, 0xffffff);
        if (screen == ScreenMode.FIXED) {
            spriteCache.get(19).drawSprite(0, 0);
        } else {
            spriteCache.get(44).drawSprite(window_width - 181, 0);
        }
        compass.rotate_raster(33, camera_pan, anIntArray1057, 256, anIntArray968, (screen == ScreenMode.FIXED ? 25 : 24), 4,
            (screen == ScreenMode.FIXED ? 29 : window_width - 176), 33, 25);

        if (screen != ScreenMode.FIXED && settings[ConfigUtility.SIDE_STONES_ARRANGEMENT_ID] == 1) {
            if (super.cursor_x >= window_width - 26 && super.cursor_x <= window_width - 1 && super.cursor_y >= 2
                && super.cursor_y <= 24 || sidebarId == 10) {
                spriteCache.get(27).drawSprite(window_width - 25, 2);
            } else {
                spriteCache.get(27).drawAdvancedSprite(window_width - 25, 2, 165);
            }
        }
        if (settings[ConfigUtility.DATA_ORBS_ID] == 1) {
            loadAllOrbs(screen == ScreenMode.FIXED ? 0 : window_width - 217);
        }
        if (menuOpen) {
            drawMenu(screen == ScreenMode.FIXED ? 516 : 0, 0);
        }
        if (screen == ScreenMode.FIXED) {
            gameScreenImageProducer.init();
        }
    }

    private final float REGEN_HEALTH_TIME = 60000.0f;
    private final float REGEN_SPEC_TIME = 30000.0f;
    private long regenHealthStart = 0;
    private long regenSpecStart = 0;
    private long loginTime = 0;
    private long logoutTime = 0;
    private int lastHp = 0;
    private int lastSpec = 0;
    private int specialAttack = 0;

    private void loadSpecialOrb(int xOffset) {
        boolean fixed = screen == ScreenMode.FIXED;
        int yOff = setting.draw_special_orb ? fixed ? -10 : 1 : 0;
        int xOff = setting.draw_special_orb ? fixed ? 138 : 113 : 0;
        SimpleImage image = spriteCache.get(7);
        SimpleImage fill = spriteCache.get(768);
        SimpleImage sword = spriteCache.get(770);
        double percent = specialAttack / (double) 100;
        image.drawSprite((fixed ? 170 - xOff : 159 - xOff) + xOffset, fixed ? 122 - yOff : 147 - yOff);
        fill.drawShadedSprite((fixed ? 197 - xOff : 186 - xOff) + xOffset, fixed ? 126 - yOff : 151 - yOff, 233);
        spriteCache.get(14).height = (int) (26 * (1 - percent));
        spriteCache.get(14).drawSprite((fixed ? 197 - xOff : 186 - xOff) + xOffset, fixed ? 126 - yOff : 151 - yOff);
        sword.drawSprite((fixed ? 202 - xOff : 191 - xOff) + xOffset, fixed ? 131 - yOff : 156 - yOff);
        adv_font_small.draw_centered(specialAttack + "", (fixed ? 185 - xOff : 173 - xOff) + xOffset,
            fixed ? 148 - yOff : 172 - yOff, getOrbTextColor((int) (percent * 100)), true);

        if (percent < 1) {
            if (regenSpecStart > 0) {
                float difference = (int) (System.currentTimeMillis() - regenSpecStart);
                float angle = (difference / REGEN_SPEC_TIME) * 360.0f;
                if (setting.draw_orb_arc) {
                    Rasterizer2D.draw_arc((fixed ? 194 - xOff : 183 - xOff) + xOffset, fixed ? 123 - yOff : 149 - yOff,
                        28, 28, 2, 90, -(int) angle, 65535, 210, 0, false);
                }
                if (angle > 358.0f && specialAttack != lastSpec)
                    regenSpecStart = System.currentTimeMillis();
                lastSpec = specialAttack;
            }
        }
    }

    public int poisonType = 0;

    private void loadHpOrb(int xOffset) {
        final boolean fixed = screen == ScreenMode.FIXED;
        int yOff = setting.draw_special_orb ? fixed ? 0 : -5 : fixed ? 0 : -5;
        int xOff = setting.draw_special_orb ? fixed ? 0 : -6 : fixed ? 0 : -6;
        SimpleImage bg = spriteCache.get(7);
        SimpleImage fg = null;
        if (poisonType == 0) {
            fg = spriteCache.get(0);
        }
        if (poisonType == 1) {
            fg = spriteCache.get(803);
        }
        if (poisonType == 2) {
            fg = spriteCache.get(804);
        }
        bg.drawSprite(0 + xOffset - xOff, 41 - yOff);
        if (poisonType > 0) {
            fg.drawSprite(27 + xOffset - xOff, 45 - yOff);
        } else {
            fg.drawShadedSprite(27 + xOffset - xOff, 45 - yOff, 233);
        }
        int level = Integer.parseInt(Widget.cache[4016].defaultText.replaceAll("%", ""));
        int max = Integer.parseInt(Widget.cache[4017].defaultText.replaceAll("%", ""));
        double percent = level / (double) max;
        spriteCache.get(14).height = (int) (26 * (1 - percent));
        spriteCache.get(14).drawSprite(27 + xOffset - xOff, 45 - yOff);
        spriteCache.get(9).drawSprite(33 + xOffset - xOff, 50 - yOff);
        adv_font_small.draw_centered("" + level, 15 + xOffset - xOff, 67 - yOff, getOrbTextColor((int) (percent * 100)), true);

        if (percent < 1) {
            if (regenHealthStart > 0) {
                float difference = (int) (System.currentTimeMillis() - regenHealthStart);
                float angle = (difference / REGEN_HEALTH_TIME) * 360.0f;
                if (setting.draw_orb_arc) {
                    Rasterizer2D.draw_arc(24 + xOffset - xOff, 42 - yOff, 28, 28, 2, 90, -(int) angle, 0xff0000, 210, 0,
                        false);
                }
                if (angle > 358.0f && level != lastHp)
                    regenHealthStart = System.currentTimeMillis();
                lastHp = level;
            }
        }
    }

    private void loadPrayerOrb(int xOffset) {
        int yOff = setting.draw_special_orb ? screen == ScreenMode.FIXED ? 10 : 2 : screen == ScreenMode.FIXED ? 0 : -5;
        int xOff = setting.draw_special_orb ? screen == ScreenMode.FIXED ? 0 : -7
            : screen == ScreenMode.FIXED ? -1 : -7;
        SimpleImage bg = spriteCache.get(prayHover ? 8 : 7);
        SimpleImage fg = spriteCache.get(759 + (prayClicked ? 42 : 41));
        bg.drawSprite(0 + xOffset - xOff, 85 - yOff);
        fg.drawShadedSprite(27 + xOffset - xOff, 89 - yOff, 233);
        int level = Integer.parseInt(Widget.cache[4012].defaultText.replaceAll("%", ""));
        int max = Integer.parseInt(Widget.cache[4013].defaultText.replaceAll("%", ""));
        double percent = level / (double) max;
        spriteCache.get(14).height = (int) (26 * (1 - percent));
        spriteCache.get(14).drawSprite(27 + xOffset - xOff, 89 - yOff);
        if (prayClicked) {
            spriteCache.get(802).drawSprite(30 + xOffset - xOff, 92 - yOff);
        } else {
            spriteCache.get(10).drawSprite(30 + xOffset - xOff, 92 - yOff);
        }
        adv_font_small.draw_centered("" + level, 15 + xOffset - xOff, 111 - yOff,
            getOrbTextColor((int) (percent * 100)), true);
    }

    private boolean staminaActive = false;

    private void loadRunOrb(int xOffset) {
        int yOff = setting.draw_special_orb ? screen == ScreenMode.FIXED ? 15 : 5 : screen == ScreenMode.FIXED ? 1 : -4;
        int xMinus = setting.draw_special_orb ? screen == ScreenMode.FIXED ? 14 : 5 : screen == ScreenMode.FIXED ? -1 : -6;
        SimpleImage bg = spriteCache.get(runHover ? 8 : 7);
        SimpleImage fg = spriteCache.get(settings[ConfigUtility.RUN_ORB_ID] == 1 ? 4 : 3);
        bg.drawSprite(24 + xOffset - xMinus, 122 - yOff);
        fg.drawShadedSprite(51 + xOffset - xMinus, 126 - yOff, 233);
        int level = runEnergy;
        double percent = level / (double) 100;
        spriteCache.get(14).height = (int) (26 * (1 - percent));
        spriteCache.get(14).drawSprite(51 + xOffset - xMinus, 126 - yOff);
        spriteCache.get(staminaActive ? 1035 : settings[ConfigUtility.RUN_ORB_ID] == 1 ? 12 : 11).drawSprite(staminaActive ? 51 + xOffset - xMinus : 57 + xOffset - xMinus, staminaActive ? 126 - yOff : 130 - yOff);
        adv_font_small.draw_centered("" + level, 39 + xOffset - xMinus, 148 - yOff, getOrbTextColor((int) (percent * 100)), true);
    }

    private void loadAllOrbs(int xOffset) {
        loadHpOrb(xOffset);
        loadPrayerOrb(xOffset);
        loadRunOrb(xOffset);

        if (setting.draw_special_orb) {
            loadSpecialOrb(xOffset);
        }

        /* Xp counter */
        SimpleImage bg = spriteCache.get(expCounterHover ? 73 : 74);
        bg.drawSprite(screen == ScreenMode.FIXED ? 0 : window_width - 216, 21);
        spriteCache.get(setting.show_exp_counter ? 76 : 75).drawSprite(screen == ScreenMode.FIXED ? 0 : window_width - 216, 21);

        SimpleImage bank_bg = spriteCache.get(bankHover ? 73 : 74);
        SimpleImage bank_icon = spriteCache.get(1832);
        bank_bg.drawSprite(screen == ScreenMode.FIXED ? 227 : window_width - 26, screen == ScreenMode.FIXED ? 115 : window_height - 400);
        bank_icon.drawSprite(screen == ScreenMode.FIXED ? 227 : window_width - 26, screen == ScreenMode.FIXED ? 118 : window_height - 396);

        SimpleImage pot_bg = spriteCache.get(potionsHover ? 73 : 74);
        SimpleImage pot_icon = spriteCache.get(1834);
        pot_bg.drawSprite(screen == ScreenMode.FIXED ? 227 : window_width - 26, screen == ScreenMode.FIXED ? 143 : window_height - 375);
        pot_icon.drawSprite(screen == ScreenMode.FIXED ? 227 : window_width - 26, screen == ScreenMode.FIXED ? 147 : window_height - 371);

        SimpleImage heal_bg = spriteCache.get(healHover ? 73 : 74);
        SimpleImage heal_icon = spriteCache.get(1833);
        heal_bg.drawSprite(screen == ScreenMode.FIXED ? 205 : window_width - 46, screen == ScreenMode.FIXED ? 143 : window_height - 375);
        heal_icon.drawSprite(screen == ScreenMode.FIXED ? 205 : window_width - 46, screen == ScreenMode.FIXED ? 148 : window_height - 371);

        Rasterizer2D.draw_filled_rect(0, 0, 1, 200, 0x332B16, 250);
    }

    private void get_entity_scene_pos(Entity entity, int height) {
        get_scene_pos(entity.world_x, height, entity.world_y);
    }

    private void get_scene_pos(int x, int vertical_offset, int y) {
        if (x < 128 || y < 128 || x > 13056 || y > 13056) {
            scene_draw_x = -1;
            scene_draw_y = -1;
            return;
        }
        int z = get_tile_pos(plane, y, x) - vertical_offset;
        x -= camera_abs_x;
        z -= camera_abs_z;
        y -= camera_abs_y;

        int sin_y = Model.SINE[cam_curve_y];
        int cos_y = Model.COSINE[cam_curve_y];

        int sin_x = Model.SINE[cam_curve_x];
        int cos_x = Model.COSINE[cam_curve_x];

        int a_x = y * sin_x + x * cos_x >> 16;
        int b_x = y * cos_x - x * sin_x >> 16;

        int a_y = z * cos_y - b_x * sin_y >> 16;
        int b_y = z * sin_y + b_x * cos_y >> 16;

        if (b_y >= 50 && b_y <= Model.VIEW_DISTANCE) {
            scene_draw_x = Rasterizer3D.center_x + (a_x << SceneGraph.view_dist) / b_y;
            scene_draw_y = Rasterizer3D.center_y + (a_y << SceneGraph.view_dist) / b_y;
        } else {
            scene_draw_x = -1;
            scene_draw_y = -1;
        }
    }

    private void buildSplitPrivateChatMenu() {
        if (splitPrivateChat == 0)
            return;
        int message = 0;
        if (systemUpdateTime != 0)
            message = 1;
        if (broadcastActive())
            message += 1;
        for (int index = 0; index < 100; index++)
            if (chatMessages[index] != null) {
                int type = chatMessages[index].getType();
                String name = chatMessages[index].getName();
                if ((type == 3 || type == 7)
                    && (type == 7 || privateChatMode == 0 || privateChatMode == 1 && check_username(name))) {
                    int y = broadcastActive() ? 309 - update_offset * 13 : 329 - message * 13;
                    if (screen != ScreenMode.FIXED) {
                        y = window_height - 170 - message * 13;
                        if (broadcastActive()) {
                            y -= 20;
                        }
                    }
                    if (super.cursor_x > 4 && super.cursor_y - 4 > y - 10 && super.cursor_y - 4 <= y + 3) {
                        int width = adv_font_regular.get_width("From:  " + name + chatMessages[index]) + 25;
                        if (width > 450)
                            width = 450;
                        if (super.cursor_x < 4 + width) {
                            if (interfaceOpen()) {
                                return;
                            }
                            menuActionText[menuActionRow] = "Add ignore <col=FFFFFF>" + name;
                            menuActionTypes[menuActionRow] = 2042;
                            menuActionRow++;
                            menuActionText[menuActionRow] = "Add friend <col=FFFFFF>" + name;
                            menuActionTypes[menuActionRow] = 2337;
                            menuActionRow++;
                            menuActionText[menuActionRow] = "Reply to <col=FFFFFF>" + name;
                            menuActionTypes[menuActionRow] = 338;
                            menuActionRow++;
                        }
                    }
                    if (++message >= 5)
                        return;
                }
                if ((type == 5 || type == 6) && privateChatMode < 2 && ++message >= 5)
                    return;
            }
    }

    private void requestSpawnObject(int longetivity, int id, int orientation, int group, int y, int type, int plane, int x, int delay) {
        SpawnedObject object = null;
        for (SpawnedObject node = (SpawnedObject) spawns.first(); node != null; node = (SpawnedObject) spawns.next()) {
            if (node.plane != plane || node.x != x || node.y != y || node.group != group)
                continue;
            object = node;
            break;
        }

        if (object == null) {
            object = new SpawnedObject();
            object.plane = plane;
            object.group = group;
            object.x = x;
            object.y = y;
            handleTemporaryObjects(object);
            spawns.insertBack(object);
            //System.out.println("Object null with id: "+id);
        }
        object.id = id;
        //System.out.println("Object id changed to: "+id);
        object.type = type;
        object.orientation = orientation;
        object.delay = delay;
        object.getLongetivity = longetivity;
    }

    private boolean interfaceIsSelected(Widget widget) {
        if (widget.valueCompareType == null)
            return false;
        for (int i = 0; i < widget.valueCompareType.length; i++) {
            int j = executeScript(widget, i);
            int k = widget.requiredValues[i];
            if (widget.valueCompareType[i] == 2) {
                if (j >= k)
                    return false;
            } else if (widget.valueCompareType[i] == 3) {
                if (j <= k)
                    return false;
            } else if (widget.valueCompareType[i] == 4) {
                if (j == k)
                    return false;
            } else if (j != k)
                return false;
        }

        return true;
    }

    private void doFlamesDrawing() {
        char c = '\u0100';
        if (anInt1040 > 0) {
            for (int i = 0; i < 256; i++)
                if (anInt1040 > 768)
                    anIntArray850[i] = method83(anIntArray851[i], anIntArray852[i], 1024 - anInt1040);
                else if (anInt1040 > 256)
                    anIntArray850[i] = anIntArray852[i];
                else
                    anIntArray850[i] = method83(anIntArray852[i], anIntArray851[i], 256 - anInt1040);

        } else if (anInt1041 > 0) {
            for (int j = 0; j < 256; j++)
                if (anInt1041 > 768)
                    anIntArray850[j] = method83(anIntArray851[j], anIntArray853[j], 1024 - anInt1041);
                else if (anInt1041 > 256)
                    anIntArray850[j] = anIntArray853[j];
                else
                    anIntArray850[j] = method83(anIntArray853[j], anIntArray851[j], 256 - anInt1041);

        } else {
            System.arraycopy(anIntArray851, 0, anIntArray850, 0, 256);

        }
        System.arraycopy(flameLeftSprite.pixels, 0, flameLeftBackground.canvasRaster, 0, 33920);

        int i1 = 0;
        int j1 = 1152;
        for (int k1 = 1; k1 < c - 1; k1++) {
            int l1 = (anIntArray969[k1] * (c - k1)) / c;
            int j2 = 22 + l1;
            if (j2 < 0)
                j2 = 0;
            i1 += j2;
            for (int l2 = j2; l2 < 128; l2++) {
                int j3 = anIntArray828[i1++];
                if (j3 != 0) {
                    int l3 = j3;
                    int j4 = 256 - j3;
                    j3 = anIntArray850[j3];
                    int l4 = flameLeftBackground.canvasRaster[j1];
                    flameLeftBackground.canvasRaster[j1++] = ((j3 & 0xff00ff) * l3 + (l4 & 0xff00ff) * j4 & 0xff00ff00)
                        + ((j3 & 0xff00) * l3 + (l4 & 0xff00) * j4 & 0xff0000) >> 8;
                } else {
                    j1++;
                }
            }

            j1 += j2;
        }

        flameLeftBackground.drawGraphics(0, super.graphics, 0);
        System.arraycopy(flameRightSprite.pixels, 0, flameRightBackground.canvasRaster, 0, 33920);

        i1 = 0;
        j1 = 1176;
        for (int k2 = 1; k2 < c - 1; k2++) {
            int i3 = (anIntArray969[k2] * (c - k2)) / c;
            int k3 = 103 - i3;
            j1 += i3;
            for (int i4 = 0; i4 < k3; i4++) {
                int k4 = anIntArray828[i1++];
                if (k4 != 0) {
                    int i5 = k4;
                    int j5 = 256 - k4;
                    k4 = anIntArray850[k4];
                    int k5 = flameRightBackground.canvasRaster[j1];
                    flameRightBackground.canvasRaster[j1++] = ((k4 & 0xff00ff) * i5 + (k5 & 0xff00ff) * j5 & 0xff00ff00)
                        + ((k4 & 0xff00) * i5 + (k5 & 0xff00) * j5 & 0xff0000) >> 8;
                } else {
                    j1++;
                }
            }

            i1 += 128 - k3;
            j1 += 128 - k3 - i3;
        }

        flameRightBackground.drawGraphics(0, super.graphics, 637);
    }

    private void updateOtherPlayerMovement(Buffer stream) {
        int count = stream.readBits(8);

        if (count < players_in_region) {
            for (int index = count; index < players_in_region; index++) {
                removedMobs[removedMobCount++] = local_players[index];
            }
        }
        if (count > players_in_region) {
            SignLink.reporterror(myUsername + " Too many players");
            throw new RuntimeException("eek");
        }
        players_in_region = 0;
        for (int globalIndex = 0; globalIndex < count; globalIndex++) {
            int index = local_players[globalIndex];
            Player player = players[index];
            player.index = index;
            int updateRequired = stream.readBits(1);

            if (updateRequired == 0) {
                local_players[players_in_region++] = index;
                player.time = game_tick;
            } else {
                int movementType = stream.readBits(2);
                if (movementType == 0) {
                    local_players[players_in_region++] = index;
                    player.time = game_tick;
                    mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = index;
                } else if (movementType == 1) {
                    local_players[players_in_region++] = index;
                    player.time = game_tick;

                    int direction = stream.readBits(3);

                    player.moveInDir(false, direction);

                    int update = stream.readBits(1);

                    if (update == 1) {
                        mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = index;
                    }
                } else if (movementType == 2) {
                    local_players[players_in_region++] = index;
                    player.time = game_tick;

                    int firstDirection = stream.readBits(3);
                    player.moveInDir(true, firstDirection);

                    int secondDirection = stream.readBits(3);
                    player.moveInDir(true, secondDirection);

                    int update = stream.readBits(1);
                    if (update == 1) {
                        mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = index;
                    }
                } else if (movementType == 3) {
                    removedMobs[removedMobCount++] = index;
                }
            }
        }
    }

    private ProducingGraphicsBuffer loginScreenAccessories;

    public void drawMusicSprites() {

        int musicState = 0;
        bottomRightImageProducer.init();
        switch (musicState) {
            case 0:
                spriteCache.get(58).drawSprite(158, 196);
                break;

            case 1:
                spriteCache.get(59).drawSprite(158, 196);
                break;
        }
    }

    private SimpleImage loginHover;
    private SimpleImage usernameHover;
    private SimpleImage passwordHover;
    public SimpleImage backgroundFix;
    private SimpleImage saveButton;

    private void drawLoginScreen(boolean flag) {

        resetImageProducers();

loginBoxImageProducer.init();
titleBoxIndexedImage.draw(0,0);
        if (ClientConstants.DEBUG_MODE) {
            adv_font_regular.draw("cursor_x: " + super.cursor_x, 10, 20, 0xFFFFFF, 0);
            adv_font_regular.draw("cursor_y: " + super.cursor_y, 10, 40, 0xFFFFFF, 0);
        }

        char c = '\u0168';
        char c1 = '\310';
        if (loginScreenState == 0) {
            int i =  c1 / 2 + 80;
            adv_font_regular.draw_centered(resourceProvider.loadingMessage, i, c / 2, 0x75a9a9, true);
            i = c1 / 2 - 20;
            adv_font_regular.draw_centered("Welcome to " + ClientConstants.CLIENT_NAME,c/2,i,0xffff00, true);

            i += 30;
            int l = c / 2 - 80;
            int k1 = c1 / 2 + 20;
titleButtonIndexedImage.draw(l - 73, k1 - 20);
            adv_font_regular.draw_centered("New User",l,k1+5,0xffffff, true);
            l = c / 2 + 80;

            titleButtonIndexedImage.draw(l - 73, k1 - 20);
            adv_font_regular.draw_centered("Existing User",l,k1+5,0xffffff, true);

        }
        if (loginScreenState == 2) {
            int j = c1 / 2 - 40;
            if (firstLoginMessage.length() > 0) {

                adv_font_small.draw_centered(firstLoginMessage,c/2,j-15,0xffff00, true);
                adv_font_small.draw_centered(secondLoginMessage,c/2,j,0xffff00, true);


                j += 30;
            } else {
                adv_font_small.draw_centered(secondLoginMessage,c/2,j-7,0xffff00, true);

                j += 30;
            }
            adv_font_bold.draw("Username:",  c / 2 - 100, j, 0xffffff, true);

            adv_font_bold.draw(myUsername + (loginScreenCursorPos == 0 & game_tick % 40 < 20 ? "@yel@|" : ""),  c / 2 - 30, j, 0xffffff, true);
            j += 18;
            adv_font_regular.draw(StringUtils.passwordAsterisks(myPassword) + (((this.loginScreenCursorPos == 1 ? 1 : 0) & (game_tick % 40 < 20 ? 1 : 0)) != 0 ? "|" : ""), c / 2 - 30, j, 0xffffff, true);
            adv_font_bold.draw("Password:",  c / 2 - 100, j, 0xffffff, true);
            j += 15;
            if (!flag) {
                int i1 = c / 2 - 80;
                int l1 = c1 / 2 + 50;
                titleButtonIndexedImage.draw(i1 - 73, l1 - 20);
                adv_font_regular.draw_centered("Login",i1,l1+5,0xffffff, true);
                i1 = c / 2 + 80;

                titleButtonIndexedImage.draw(i1 - 73, l1 - 20);
                adv_font_regular.draw_centered("Cancel",i1,l1+5,0xffffff, true);
            }
            int rememberhoverx = 140;
            int rememberhovery = 170;
            if (!informationFile.isUsernameRemembered()) {
                spriteCache.get(546).drawSprite(rememberhoverx, rememberhovery);
            } else {
                spriteCache.get(547).drawSprite(rememberhoverx, rememberhovery);
            }
            adv_font_small.draw_centered("@yel@Save account",210,182,0xffffff, true);

        }
        if (loginScreenState == 3) {
            adv_font_regular.draw_centered("Cancel",c/2,c1/2-60,0xffff00, true);
            int k = c1 / 2 - 35;
            adv_font_regular.draw_centered("To create a new account you need to",c/2,k,0xffffff, true);
            k += 15;
            adv_font_regular.draw_centered("go back to the main "+ClientConstants.CLIENT_NAME+" web page",c/2,k,0xffffff, true);
            k += 15;
            adv_font_regular.draw_centered("and choose the red 'create account'",c/2,k,0xffffff, true);
            k += 15;
            adv_font_regular.draw_centered("button at the top right of that page.",c/2,k,0xffffff, true);
            k += 15;
            int j1 = c / 2;
            int i2 = c1 / 2 + 50;

            titleButtonIndexedImage.draw(j1 - 73, i2 - 20);
            adv_font_regular.draw_centered("Cancel",j1,i2+5,0xffffff, true);
        }


        loginBoxImageProducer.drawGraphics(171, super.graphics, 202);
        if (update_producers) {
            update_producers = false;
            topLeft1BackgroundTile.drawGraphics(0, super.graphics, 128);
            bottomLeft1BackgroundTile.drawGraphics(371, super.graphics, 202);
            bottomLeft0BackgroundTile.drawGraphics(265, super.graphics, 0);
            bottomRightImageProducer.drawGraphics(265, super.graphics, 562);
            loginMusicImageProducer.drawGraphics(265, super.graphics, 562);
            middleLeft1BackgroundTile.drawGraphics(171, super.graphics, 128);
            aRSImageProducer_1115.drawGraphics(171, super.graphics, 562);
        }
     //   accountManager.processAccountDrawing();

    }



    private void drawFlames() {
        drawingFlames = true;
        try {
            long l = System.currentTimeMillis();
            int i = 0;
            int j = 20;
            while (update_flame_components) {
                calcFlamesPosition();
                calcFlamesPosition();
                doFlamesDrawing();
                if (++i > 10) {
                    long l1 = System.currentTimeMillis();
                    int k = (int) (l1 - l) / 10 - j;
                    j = 40 - k;
                    if (j < 5)
                        j = 5;
                    i = 0;
                    l = l1;
                }
                try {
                    Thread.sleep(j);
                } catch (Exception _ex) {
                    _ex.printStackTrace();
                    addReportToServer(_ex.getMessage());
                }
            }
        } catch (Exception _ex) {
            _ex.printStackTrace();
            addReportToServer(_ex.getMessage());
        }
        drawingFlames = false;
    }

    public void raiseWelcomeScreen() {
        update_producers = true;
    }

    private void parseRegionPackets(Buffer stream, int packetType) {
        if (packetType == ServerToClientPackets.SEND_ALTER_GROUND_ITEM_COUNT) {
            int offset = stream.readUByte();
            int xLoc = localX + (offset >> 4 & 7);
            int yLoc = localY + (offset & 7);
            int itemId = stream.readUShort();
            int oldItemCount = stream.readUShort();
            int newItemCount = stream.readUShort();
            if (xLoc >= 0 && yLoc >= 0 && xLoc < 104 && yLoc < 104) {
                LinkedList groundItemsDeque = scene_items[plane][xLoc][yLoc];
                if (groundItemsDeque != null) {
                    for (Item groundItem = (Item) groundItemsDeque
                        .first(); groundItem != null; groundItem = (Item) groundItemsDeque
                        .next()) {
                        if (groundItem.id != (itemId & 0x7fff) || groundItem.quantity != oldItemCount)
                            continue;
                        groundItem.quantity = newItemCount;
                        break;
                    }

                    spawn_scene_item(xLoc, yLoc);
                }
            }
            return;
        }
        if (packetType == 105) {
            int l = stream.readUnsignedByte();
            int k3 = localX + (l >> 4 & 7);
            int j6 = localY + (l & 7);
            int i9 = stream.readUShort();
            int l11 = stream.readUnsignedByte();
            int i14 = l11 >> 4 & 0xf;
            int i16 = l11 & 7;
            if (local_player.waypoint_x[0] >= k3 - i14 && local_player.waypoint_x[0] <= k3 + i14 && local_player.waypoint_y[0] >= j6 - i14 && local_player.waypoint_y[0] <= j6 + i14 && soundsAreEnabled && !low_detail && trackCount < 50) {
                track_ids[trackCount] = i9;
                track_loop[trackCount] = i16;
                audio_delay[trackCount] = Track.delays[i9];
                trackCount++;
            }
        }
        if (packetType == 215) {
            int i1 = stream.readUShortA();
            int l3 = stream.readUByteS();
            int k6 = localX + (l3 >> 4 & 7);
            int j9 = localY + (l3 & 7);
            int i12 = stream.readUShortA();
            int j14 = stream.readUShort();
            if (k6 >= 0 && j9 >= 0 && k6 < 104 && j9 < 104 && i12 != localPlayerIndex) {
                Item class30_sub2_sub4_sub2_2 = new Item();
                class30_sub2_sub4_sub2_2.id = i1;
                class30_sub2_sub4_sub2_2.quantity = j14;
                if (scene_items[plane][k6][j9] == null)
                    scene_items[plane][k6][j9] = new LinkedList();
                scene_items[plane][k6][j9].insertBack(class30_sub2_sub4_sub2_2);
                spawn_scene_item(k6, j9);
            }
            return;
        }
        if (packetType == ServerToClientPackets.SEND_REMOVE_GROUND_ITEM) {
            int offset = stream.readUByteA();
            int xLoc = localX + (offset >> 4 & 7);
            int yLoc = localY + (offset & 7);
            int itemId = stream.readUShort();
            if (xLoc >= 0 && yLoc >= 0 && xLoc < 104 && yLoc < 104) {
                LinkedList groundItemsDeque = scene_items[plane][xLoc][yLoc];
                if (groundItemsDeque != null) {
                    for (Item item = (Item) groundItemsDeque
                        .first(); item != null; item = (Item) groundItemsDeque.next()) {
                        if (item.id != (itemId & 0x7fff))
                            continue;
                        item.remove();
                        break;
                    }

                    if (groundItemsDeque.first() == null)
                        scene_items[plane][xLoc][yLoc] = null;
                    spawn_scene_item(xLoc, yLoc);
                }
            }
            return;
        }
        if (packetType == ServerToClientPackets.ANIMATE_OBJECT) {
            int offset = stream.readUByteS();
            int xLoc = localX + (offset >> 4 & 7);
            int yLoc = localY + (offset & 7);
            int objectTypeFace = stream.readUByteS();
            int objectType = objectTypeFace >> 2;
            int objectFace = objectTypeFace & 3;
            int objectGenre = objectGroups[objectType];
            int animId = stream.readUShortA();
            if (xLoc >= 0 && yLoc >= 0 && xLoc < 103 && yLoc < 103) {
                int heightA = tileHeights[plane][xLoc][yLoc];
                int heightB = tileHeights[plane][xLoc + 1][yLoc];
                int heightC = tileHeights[plane][xLoc + 1][yLoc + 1];
                int heightD = tileHeights[plane][xLoc][yLoc + 1];
                if (objectGenre == 0) {// WallObject
                    Wall wallObjectObject = scene.get_wall(plane, xLoc, yLoc);
                    if (wallObjectObject != null) {
                        int objectId = get_object_key(wallObjectObject.uid);
                        if (objectType == 2) {
                            wallObjectObject.wall = new SceneObject(objectId, 4 + objectFace, 2, heightB,
                                heightC, heightA, heightD, animId, false);
                            wallObjectObject.corner = new SceneObject(objectId, objectFace + 1 & 3, 2, heightB,
                                heightC, heightA, heightD, animId, false);
                        } else {
                            wallObjectObject.wall = new SceneObject(objectId, objectFace, objectType, heightB,
                                heightC, heightA, heightD, animId, false);
                        }
                    }
                }
                if (objectGenre == 1) { // WallDecoration
                    WallDecoration wallDecoration = scene.get_wall_decor(xLoc, yLoc, plane);
                    if (wallDecoration != null)
                        wallDecoration.node = new SceneObject(get_object_key(wallDecoration.uid), 0, 4, heightB,
                            heightC, heightA, heightD, animId, false);
                }
                if (objectGenre == 2) { // TiledObject
                    InteractiveObject tiledObject = scene.get_interactive_object(xLoc, yLoc, plane);
                    if (objectType == 11)
                        objectType = 10;
                    if (tiledObject != null)
                        tiledObject.node = new SceneObject(get_object_key(tiledObject.uid), objectFace, objectType,
                            heightB, heightC, heightA, heightD, animId, false);
                }
                if (objectGenre == 3) { // GroundDecoration
                    GroundDecoration groundDecoration = scene.get_ground_decor(yLoc, xLoc, plane);
                    if (groundDecoration != null)
                        groundDecoration.node = new SceneObject(get_object_key(groundDecoration.uid), objectFace,
                            22, heightB, heightC, heightA, heightD, animId, false);
                }
            }
            return;
        }
        if (packetType == ServerToClientPackets.TRANSFORM_PLAYER_TO_OBJECT) {
            int offset = stream.readUByteS();
            int xLoc = localX + (offset >> 4 & 7);
            int yLoc = localY + (offset & 7);
            int playerIndex = stream.readUShort();
            byte byte0GreaterXLoc = stream.readByteS();
            int startDelay = stream.readLEUShort();
            byte byte1GreaterYLoc = stream.readNegByte();
            int stopDelay = stream.readUShort();
            int objectTypeFace = stream.readUByteS();
            int objectType = objectTypeFace >> 2;
            int objectFace = objectTypeFace & 3;
            int objectGenre = objectGroups[objectType];
            byte byte2LesserXLoc = stream.readSignedByte();
            int objectId = stream.readUShort();
            byte byte3LesserYLoc = stream.readNegByte();
            Player player;
            if (playerIndex == localPlayerIndex)
                player = local_player;
            else
                player = players[playerIndex];
            if (player != null) {
                ObjectDefinition objectDefinition = ObjectDefinition.get(objectId);
                int heightA = tileHeights[plane][xLoc][yLoc];
                int heightB = tileHeights[plane][xLoc + 1][yLoc];
                int heightC = tileHeights[plane][xLoc + 1][yLoc + 1];
                int heightD = tileHeights[plane][xLoc][yLoc + 1];
                Model model = objectDefinition.get_object(objectType, objectFace, heightA, heightB, heightC, heightD, -1);
                if (model != null) {
                    requestSpawnObject(stopDelay + 1, -1, 0, objectGenre, yLoc, 0, plane, xLoc, startDelay + 1);
                    player.transform_delay = startDelay + game_tick;
                    player.transform_duration = stopDelay + game_tick;
                    player.transformed_model = model;
                    int playerSizeX = objectDefinition.width;
                    int playerSizeY = objectDefinition.height;
                    if (objectFace == 1 || objectFace == 3) {
                        playerSizeX = objectDefinition.height;
                        playerSizeY = objectDefinition.width;
                    }
                    player.x_offset = xLoc * 128 + playerSizeX * 64;
                    player.y_offset = yLoc * 128 + playerSizeY * 64;
                    player.z_offset = get_tile_pos(plane, player.y_offset, player.x_offset);
                    if (byte2LesserXLoc > byte0GreaterXLoc) {
                        byte tmp = byte2LesserXLoc;
                        byte2LesserXLoc = byte0GreaterXLoc;
                        byte0GreaterXLoc = tmp;
                    }
                    if (byte3LesserYLoc > byte1GreaterYLoc) {
                        byte tmp = byte3LesserYLoc;
                        byte3LesserYLoc = byte1GreaterYLoc;
                        byte1GreaterYLoc = tmp;
                    }
                    player.transform_width = xLoc + byte2LesserXLoc;
                    player.transform_width_offset = xLoc + byte0GreaterXLoc;
                    player.transform_height = yLoc + byte3LesserYLoc;
                    player.transform_height_offset = yLoc + byte1GreaterYLoc;
                }
            }
        }
        if (packetType == ServerToClientPackets.SEND_OBJECT) {
            int offset = stream.readUByteA();
            int x = localX + (offset >> 4 & 7);
            int y = localY + (offset & 7);
            int id = stream.readLEUShort();
            int objectTypeFace = stream.readUByteS();
            int type = objectTypeFace >> 2;
            int orientation = objectTypeFace & 3;
            int group = objectGroups[type];
            if (x >= 0 && y >= 0 && x < 104 && y < 104) {
                requestSpawnObject(-1, id, orientation, group, y, type, plane, x, 0);
            }
            return;
        }
        if (packetType == ServerToClientPackets.SEND_GFX) {
            int offset = stream.readUByte();
            int xLoc = localX + (offset >> 4 & 7);
            int yLoc = localY + (offset & 7);
            int gfxId = stream.readUShort();
            int gfxHeight = stream.readUByte();
            int gfxDelay = stream.readUShort();
            if (xLoc >= 0 && yLoc >= 0 && xLoc < 104 && yLoc < 104) {
                xLoc = xLoc * 128 + 64;
                yLoc = yLoc * 128 + 64;
                StaticObject loneGfx = new StaticObject(plane, game_tick, gfxDelay, gfxId,
                    get_tile_pos(plane, yLoc, xLoc) - gfxHeight, yLoc, xLoc);
                incompleteAnimables.insertBack(loneGfx);
            }
            return;
        }
        if (packetType == ServerToClientPackets.SEND_GROUND_ITEM) {
            int itemId = stream.readLEUShortA();
            // int itemCount = stream.readUShort();
            // Also changed in server's PacketSender.
            int itemCount = stream.readInt();
            int offset = stream.readShort();
            int xLoc = localX + (offset >> 4 & 7);
            int yLoc = localY + (offset & 7);
            if (xLoc >= 0 && yLoc >= 0 && xLoc < 104 && yLoc < 104) {
                Item groundItem = new Item();
                groundItem.id = itemId;
                groundItem.quantity = itemCount;
                if (scene_items[plane][xLoc][yLoc] == null)
                    scene_items[plane][xLoc][yLoc] = new LinkedList();
                scene_items[plane][xLoc][yLoc].insertBack(groundItem);
                spawn_scene_item(xLoc, yLoc);
            }
            return;
        }
        if (packetType == ServerToClientPackets.SEND_REMOVE_OBJECT) {
            int objectTypeFace = stream.readNegUByte();
            int type = objectTypeFace >> 2;
            int orientation = objectTypeFace & 3;
            int group = objectGroups[type];
            int offset = stream.readUByte();
            int x = localX + (offset >> 4 & 7);
            int y = localY + (offset & 7);
            if (x >= 0 && y >= 0 && x < 104 && y < 104) {
                requestSpawnObject(-1, -1, orientation, group, y, type, plane, x, 0);
            }
            return;
        }
        if (packetType == ServerToClientPackets.SEND_PROJECTILE) {
            int offset = stream.readUByte();
            int sourceX = localX + (offset >> 4 & 7);
            int sourceY = localY + (offset & 7);
            int destX = sourceX + stream.readSignedByte();
            int destY = sourceY + stream.readSignedByte();
            int target = stream.readShort();
            int gfxMoving = stream.readUShort();
            int startHeight = stream.readUByte() * 4;
            int endHeight = stream.readUByte() * 4;
            int startDelay = stream.readUShort();
            int speed = stream.readUShort();
            int initialSlope = stream.readUByte();
            int frontOffset = stream.readUByte();
            if (sourceX >= 0 && sourceY >= 0 && sourceX < 104 && sourceY < 104 && destX >= 0 && destY >= 0 && destX < 104 && destY < 104
                && gfxMoving != 65535) {
                sourceX = sourceX * 128 + 64;
                sourceY = sourceY * 128 + 64;
                destX = destX * 128 + 64;
                destY = destY * 128 + 64;
                Projectile projectile = new Projectile(initialSlope, endHeight, startDelay + game_tick,
                    speed + game_tick, frontOffset, plane, get_tile_pos(plane, sourceY, sourceX) - startHeight, sourceY, sourceX,
                    target, gfxMoving);
                projectile.track(startDelay + game_tick, destY, get_tile_pos(plane, destY, destX) - endHeight,
                    destX);
                projectiles.insertBack(projectile);
            }
        }
    }

    private void method139(Buffer stream) {
        stream.initBitAccess();
        int k = stream.readBits(8);
        if (k < npcs_in_region) {
            for (int l = k; l < npcs_in_region; l++)
                removedMobs[removedMobCount++] = local_npcs[l];

        }
        if (k > npcs_in_region) {
            SignLink.reporterror(myUsername + " Too many npcs");
            throw new RuntimeException("eek");
        }
        npcs_in_region = 0;
        for (int i1 = 0; i1 < k; i1++) {
            int j1 = local_npcs[i1];
            Npc npc = npcs[j1];
            npc.index = j1;
            int k1 = stream.readBits(1);
            if (k1 == 0) {
                local_npcs[npcs_in_region++] = j1;
                npc.time = game_tick;
            } else {
                int l1 = stream.readBits(2);
                if (l1 == 0) {
                    local_npcs[npcs_in_region++] = j1;
                    npc.time = game_tick;
                    mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = j1;
                } else if (l1 == 1) {
                    local_npcs[npcs_in_region++] = j1;
                    npc.time = game_tick;
                    int i2 = stream.readBits(3);
                    npc.moveInDir(false, i2);
                    int k2 = stream.readBits(1);
                    if (k2 == 1)
                        mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = j1;
                } else if (l1 == 2) {
                    local_npcs[npcs_in_region++] = j1;
                    npc.time = game_tick;
                    int j2 = stream.readBits(3);
                    npc.moveInDir(true, j2);
                    int l2 = stream.readBits(3);
                    npc.moveInDir(true, l2);
                    int i3 = stream.readBits(1);
                    if (i3 == 1)
                        mobsAwaitingUpdate[mobsAwaitingUpdateCount++] = j1;
                } else if (l1 == 3)
                    removedMobs[removedMobCount++] = j1;
            }
        }
    }

    public final SecondsTimer loginTimer = new SecondsTimer();

public void processLoginScreenInput() {
    if (loginScreenState == 0) {
        int i = super.myWidth / 2 - 80;
        int l = super.myHeight / 2 + 20;
        l += 20;
        if (super.click_type == 1 && super.click_x >= i - 75 && super.cursor_x <= i + 75 && super.click_y >= l - 20 && super.click_y <= l + 20) {
            loginScreenState = 3;
            loginScreenCursorPos = 0;
        }
        i = super.myWidth / 2 + 80;
        if (super.click_type == 1 && super.click_x >= i - 75 && super.cursor_x <= i + 75 && super.click_y >= l - 20 && super.click_y <= l + 20) {
            firstLoginMessage = "";
            secondLoginMessage = "Enter your username & password.";
            loginScreenState = 2;
            loginScreenCursorPos = 0;
        }
    } else {
        if (loginScreenState == 2) {
            int j = super.myHeight / 2 - 40;
            j += 30;
            j += 25;
            if (super.click_type == 1 && super.click_y >= j - 15 && super.click_y < j) {
                loginScreenCursorPos = 0;
            }
            j += 15;
            if (super.click_type == 1 && super.click_y >= j - 15 && super.click_y < j) {
                loginScreenCursorPos = 1;
            }
            j += 15;
            int i1 = super.myWidth / 2 - 80;
            int k1 = super.myHeight / 2 + 50;
            k1 += 20;
            if (super.click_type == 1 && super.click_x >= i1 - 75 && super.click_x <= i1 + 75 && super.click_y >= k1 - 20 && super.click_y <= k1 + 20) {
                loginFailures = 0;
                login(myUsername, myPassword, false);
                if (loggedIn) {
                    return;
                }
            }
            if (super.click_type == 1 && super.click_y >= 340 && super.click_y <= 360
                && super.click_x >= 340 && super.click_x <= 356) {
                informationFile.setUsernameRemembered(!informationFile.isUsernameRemembered());
                if (informationFile.isUsernameRemembered()) {
                    informationFile.setStoredUsername(myUsername);
                    informationFile.setStoredPassword(myPassword);
                }
            }

            i1 = super.myWidth / 2 + 80;
            if (super.click_type == 1 && super.click_x >= i1 - 75 && super.click_x <= i1 + 75 && super.click_y >= k1 - 20 && super.click_y <= k1 + 20) {
                loginScreenState = 0;
                // myUsername = "";
                // myPassword = "";
            }
            do {
                int l1 = readChar(-796);
                if (l1 == -1) {
                    break;
                }
                boolean flag1 = false;
                for (int i2 = 0; i2 < validUserPassChars.length(); i2++) {
                    if (l1 != validUserPassChars.charAt(i2)) {
                        continue;
                    }
                    flag1 = true;
                    break;
                }

                if (loginScreenCursorPos == 0) {
                    if (l1 == 8 && myUsername.length() > 0) {
                        myUsername = myUsername.substring(0, myUsername.length() - 1);
                    }
                    if (l1 == 9 || l1 == 10 || l1 == 13) {
                        loginScreenCursorPos = 1;
                    }
                    if (flag1) {
                        myUsername += (char) l1;
                    }
                    if (myUsername.length() > 12) {
                        myUsername = myUsername.substring(0, 12);
                    }
                } else if (loginScreenCursorPos == 1) {
                    if (l1 == 8 && myPassword.length() > 0) {
                        myPassword = myPassword.substring(0, myPassword.length() - 1);
                    }
                    if (l1 == 9 || l1 == 10 || l1 == 13) {
                        login(myUsername, myPassword, false);
                        loginScreenCursorPos = 0;
                    }
                    if (flag1) {
                        myPassword += (char) l1;
                    }
                    if (myPassword.length() > 20) {
                        myPassword = myPassword.substring(0, 20);
                    }
                }
            } while (true);
            return;
        }
        if (loginScreenState == 3) {
            int k = super.myWidth / 2;
            int j1 = super.myHeight / 2 + 50;
            j1 += 20;
            if (super.click_type == 1 && super.click_x >= k - 75 && super.click_x <= k + 75 && super.click_y >= j1 - 20 && super.click_y <= j1 + 20) {
                loginScreenState = 0;
            }
        }
    }
}
    //OBJECTS
    private int get_object_x(long id) {
        return (int) id & 0x7f;
    }

    private int get_object_y(long id) {
        return (int) (id >> 7) & 0x7f;
    }

    private int get_object_opcode(long id) {
        return (int) id >> 29 & 0x3;
    }

    private int get_object_key(long id) {
        return (int) (id >>> 32) & 0x7fffffff;
    }

    private int get_object_type(long id) {
        return (int) id >> 14 & 0x1f;
    }

    private int get_object_orientation(long id) {
        return (int) id >> 20 & 0x3;
    }

    private void clear_object(int y, int z, int k, int l, int x, int group, int previousId) {
        if (x >= 1 && y >= 1 && x <= 102 && y <= 102) {
            if (low_detail && z != plane)
                return;
            long key = 0L;
            if (group == 0)
                key = scene.get_wall_uid(z, x, y);
            if (group == 1)
                key = scene.get_wall_decor_uid(z, x, y);
            if (group == 2)
                key = scene.get_interactive_object_uid(z, x, y);
            if (group == 3)
                key = scene.get_ground_decor_uid(z, x, y);
            if (key != 0L) {
                int object_id = get_object_key(key);
                int object_type = get_object_type(key);
                int orientation = get_object_orientation(key);

                if (group == 0) {
                    scene.remove_wall(x, z, y);
                    ObjectDefinition objectDef = ObjectDefinition.get(object_id);
                    if (objectDef.solid)
                        collisionMaps[z].clear_wall(orientation, object_type, objectDef.walkable, x, y);
                }
                if (group == 1)
                    scene.remove_wall_decor(y, z, x);
                if (group == 2) {
                    scene.remove_object(z, x, y);
                    ObjectDefinition objectDef = ObjectDefinition.get(object_id);
                    if (x + objectDef.width > 103 || y + objectDef.width > 103 || x + objectDef.height > 103
                        || y + objectDef.height > 103)
                        return;
                    if (objectDef.solid)
                        collisionMaps[z].clear_interactive_obj(orientation, objectDef.width, x, y, objectDef.height,
                            objectDef.walkable);
                }
                if (group == 3) {
                    scene.remove_ground_decor(z, y, x);
                    ObjectDefinition objectDef = ObjectDefinition.get(object_id);
                    if (objectDef.solid && objectDef.interact_state == 1)
                        collisionMaps[z].clear_ground_decor(y, x);
                }
            }
            if (previousId >= 0) {
                int plane = z;
                if (plane < 3 && (tileFlags[1][x][y] & 2) == 2)
                    plane++;
                Region.place(scene, k, y, l, plane, collisionMaps[z], tileHeights, x, previousId, z);
            }
        }
    }

    private void updatePlayers(int packetSize, Buffer stream) {
        removedMobCount = 0;
        mobsAwaitingUpdateCount = 0;
        updateLocalPlayerMovement(stream);
        updateOtherPlayerMovement(stream);
        updatePlayerList(stream, packetSize);
        parsePlayerSynchronizationMask(stream);
        for (int count = 0; count < removedMobCount; count++) {
            int index = removedMobs[count];

            if (players[index].time != game_tick) {
                players[index] = null;
            }
        }

        if (stream.pos != packetSize) {
            addReportToServer("Player updating broke, this is very bad.");
            addReportToServer("Make sure to check buffer received datatypes in client match buffer sent datatypes from server.");
            SignLink.reporterror("Error packet size mismatch in getplayer pos:" + stream.pos + " psize:" + packetSize);
            throw new RuntimeException("eek");
        }
        for (int count = 0; count < players_in_region; count++) {
            if (players[local_players[count]] == null) {
                addReportToServer("Player updating broke, this is really bad.");
                SignLink.reporterror(myUsername + " null entry in pl list - pos:" + count + " size:" + players_in_region);
                throw new RuntimeException("eek");
            }
        }
    }

    private void set_camera_pos(int depth, int tilt_curve, int x, int z, int pan_curve, int y) {
        int tilt_diff = 2048 - tilt_curve & 0x7ff;
        int pan_diff = 2048 - pan_curve & 0x7ff;
        int x_offset = 0;
        int z_offset = 0;
        int y_offset = depth;
        if (tilt_diff != 0) {
            int sin = Model.SINE[tilt_diff];
            int cos = Model.COSINE[tilt_diff];
            int pos = z_offset * cos - y_offset * sin >> 16;
            y_offset = z_offset * sin + y_offset * cos >> 16;
            z_offset = pos;
        }
        if (pan_diff != 0) {
            int sin = Model.SINE[pan_diff];
            int cos = Model.COSINE[pan_diff];
            int pos = y_offset * sin + x_offset * cos >> 16;
            y_offset = y_offset * cos - x_offset * sin >> 16;
            x_offset = pos;
        }
        camera_abs_x = x - x_offset;
        camera_abs_z = z - z_offset;
        camera_abs_y = y - y_offset;
        cam_curve_y = tilt_curve;
        cam_curve_x = pan_curve;
    }

    private boolean cheapHaxPacket(int id, String text) {
        if (id == 99900) { // @shadowrs
            String[] parts = text.split(":");
            int specialCheck = Integer.parseInt(parts[1]),
                specialBar = Integer.parseInt(parts[2]),
                specialAmount = Integer.parseInt(parts[3]);
            //System.out.println("spec bar "+Arrays.toString(parts));
            for (int i = 0; i < 10; i++) {
                moveWidget(specialAmount >= specialCheck ? 500 : 0, 0, --specialBar);
                specialCheck--;
            }
            return true;
        }
        if (id == 52260 && text.equals("--clearall--")) {
            for (int i = 0; i < 28; i++) {
                sendString("", 52260 + i);
                sendString("", 52290 + i);
            }
            return true;
        }
        return false;
    }

    void moveWidget(int horizontalOffset, int verticalOffset, int id) {
        Widget widget = Widget.cache[id];
        widget.x = horizontalOffset;
        widget.y = verticalOffset;
    }

    public void updateString(String text, int id) {
        if (Widget.cache[id] != null) {
            Widget.cache[id].defaultText = text;
            Widget.cache[id].scrollPosition = 0;
        }
    }

    public void toggleConfig(int configId, int value) {
        anIntArray1045[configId] = value;
        if (settings[configId] != value) {
            settings[configId] = value;
            updateVarp(configId);
        }
    }

    public static final HashMap<Integer, OldState> kek1 = new HashMap();

    public static class OldState {
        public int x, y;
        public AdvancedFont font;
    }

    /**
     * Sends a string
     */
    public void sendString(String text, int interfaceId) {

        if (Widget.cache[interfaceId] == null) {
            return;
        }
        if (widget_overlay_id == 52250) { // trade screen 2
            int loop = 0;
            // reset back to original positons before changing to reset prev trade adjustments
            for (int i = 0; i < 28; i++) {
                OldState oldState = kek1.get(interfaceId + i);
                if (oldState != null) {
                    Widget.cache[interfaceId + i].x = oldState.x;
                    Widget.cache[interfaceId + i].y = oldState.y;
                    Widget.cache[interfaceId + i].text_type = oldState.font;
                }
            }
            //String[] text2 = text.split("\\\\n");
            String[] text2 = text.split("<br>");
            for (String texts : text2) {
                Widget.cache[interfaceId + loop].defaultText = texts;
                loop++;

                // loop 12 i guess is the threshold for items displayed on this interface
                // other loops will be username, accept buton, pkp string etc
                if (loop > 12) {
                    // System.out.println("beo wtf: "+Arrays.toString(text2));
                    // System.out.printf("client txt %s from %s%n", texts, loop);

                    for (int i = 0; i < 28; i++) {
                        Widget sub = Widget.cache[interfaceId + i];
                        OldState oldState = kek1.get(interfaceId + i);
                        if (oldState == null) {
                            // cache the original before changing
                            oldState = new OldState();
                            oldState.x = sub.x;
                            oldState.y = sub.y;
                            oldState.font = sub.text_type;
                            kek1.put(interfaceId + i, oldState);
                        }
                        // half the currently displayed amount of items: 28 -> 14 or 20 -> 10
                        // to get 2 columns
                        if (i <= text2.length / 2) {
                            // column 1 render
                            sub.x -= 55;
                        }
                        if (i > text2.length / 2) {
                            int base2ndColumnId = text2.length / 2;
                            sub.y -= (i + 1) * 13; // put the text at the top firstly
                            int additionals = (i - base2ndColumnId) * 13;
                            // column 1 render
                            sub.x += 55;
                            sub.y += additionals;
                            //  System.out.printf("base %s this id:%s add:%s remove: %s%n", base2ndColumnId, i, additionals, sub.y, base2ndColumnId * 13);
                        }
                        sub.text_type = adv_font_regular;
                    }
                    // only adjust the columns, dont keep adjusting for every loop after the first item
                    break;
                }
            }
            return;
        }
        Widget.cache[interfaceId].defaultText = text;
    }

    public boolean handleConfirmationPacket(int state, int value) {
        switch (state) {
            case 0:
                return true;
            case 1:
                switch (value) {
                }
                return true;
            case 2:
                switch (value) {
                    case 0:
                        Widget.cache[widgetId].disabledSprite = spriteCache
                            .get(Widget.cache[widgetId].clickSprite1);
                        Widget.cache[widgetId].enabledSprite = spriteCache
                            .get(Widget.cache[widgetId].clickSprite1);
                        return true;
                    case 1:
                        return true;

                }
                return true;

            case 4:
                if (value == 1) {
                    return true;
                }
                return true;
        }
        return false;
    }

    public void sendButtonClick(int button, int toggle, int type) {
        Widget widget = Widget.cache[button];
        // case reset setting widget
        switch (type) {
            case 135:
                boolean flag8 = true;
                if (widget.contentType > 0) {
                    flag8 = promptUserForInput(widget);
                }
                if (flag8) {
                    packetSender.sendButtonClick(button);
                }
                break;
            case 646:
                packetSender.sendButtonClick(button);
                if (widget.valueIndexArray != null && widget.valueIndexArray[0][0] == 5) {
                    if (settings[toggle] != widget.requiredValues[0]) {
                        settings[toggle] = widget.requiredValues[0];
                        updateVarp(toggle);
                    }
                }
                break;
            case 169:
                packetSender.sendButtonClick(button);
                if (widget.valueIndexArray != null && widget.valueIndexArray[0][0] == 5) {
                    settings[toggle] = 1 - settings[toggle];
                    updateVarp(toggle);
                }
                break;
            default:
                System.out.println("button: " + button + " - toggle: " + toggle + " - type: " + type);
        }
    }

    /**
     * Sets button configurations on interfaces.
     */
    public void sendConfiguration(int id, int value) {
        //System.out.println("id: " + id + " vs value: " + value);
        if (id < anIntArray1045.length) {
            anIntArray1045[id] = value;
            if (settings[id] != value) {
                settings[id] = value;
                updateVarp(id);
                if (dialogueId != -1)
                    update_chat_producer = true;
            }
        }
    }

    /**
     * Clears the screen of all open interfaces.
     */
    public void clearScreen() {
        if (overlayInterfaceId != -1) {
            overlayInterfaceId = -1;
            update_tab_producer = true;
        }
        if (backDialogueId != -1) {
            backDialogueId = -1;
            update_chat_producer = true;
        }
        if (inputDialogState != 0) {
            inputDialogState = 0;
            update_chat_producer = true;
        }
        widget_overlay_id = -1;
        continuedDialogue = false;
    }

    /**
     * Displays an interface over the sidebar area.
     */
    public void inventoryOverlay(int interfaceId, int sideInterfaceId) {
        if (backDialogueId != -1) {
            backDialogueId = -1;
            update_chat_producer = true;
        }
        if (inputDialogState != 0) {
            inputDialogState = 0;
            update_chat_producer = true;
        }
        widget_overlay_id = interfaceId;
        overlayInterfaceId = sideInterfaceId;
        update_tab_producer = true;
        continuedDialogue = false;
    }

    private boolean readPacket() {
        if (socketStream == null)
            return false;
        try {
            int available = socketStream.available();
            if (available == 0)
                return false;

            // Read opcode...
            if (opcode == -1) {
                socketStream.flushInputStream(incoming.payload, 1);
                opcode = incoming.payload[0] & 0xff;
                if (encryption != null)
                    opcode = opcode - encryption.value() & 0xff;

                packetSize = ServerToClientPackets.PACKET_SIZES[opcode];
                available--;
            }

            // The packet size -3 means the packet shouldn't be sent by the server, did we
            // forget to add the size in the client ServerToClientPackets class?
            if (packetSize == -3) {
                addReportToServer("The packet opcode " + opcode + " is not whitelisted!");
                return false;
            }

            // Read size
            if (packetSize == -1)
                if (available > 0) {
                    socketStream.flushInputStream(incoming.payload, 1);
                    packetSize = incoming.payload[0] & 0xff;
                    available--;
                } else {
                    return false;
                }

            if (packetSize == -2)
                if (available > 1) {
                    socketStream.flushInputStream(incoming.payload, 2);
                    incoming.pos = 0;
                    packetSize = incoming.readUShort();
                    available -= 2;
                } else {
                    return false;
                }

            if (available < packetSize) {
                // data still in progress being sent over internet. wait until all expected bytes are available before moving onto parsing.
                return false;
            }

            incoming.pos = 0;
            socketStream.flushInputStream(incoming.payload, packetSize);
            if (timeoutCounter > 50) {
                String[] data = new String[]{"" + timeoutCounter, "" + opcode, "" + lastOpcode, "" + secondLastOpcode, "" + thirdLastOpcode};
                //final String txt = "timeoutCounter above threshold " + timeoutCounter + ", packet: opcode: " + opcode + ", lastOpcode: " + lastOpcode + ", secondLastOpcode: " + secondLastOpcode + ", thirdLastOpcode: " + thirdLastOpcode;
                //addReportToServer(txt);
                //addReportToServer(Arrays.toString(data));
            }
            timeoutCounter = 0;
            thirdLastOpcode = secondLastOpcode;
            secondLastOpcode = lastOpcode;
            lastOpcode = opcode;

            if (debug_packet_info) {
                //addReportToServer("> " + opcode);
            }

            // Size is 1 (packet number is 6)
            if (opcode == ServerToClientPackets.HEALTH_ORB) {
                try {
                    poisonType = incoming.readNegUByte();
                } catch (Exception e) {
                    e.printStackTrace();
                    addReportToServer(e.getMessage());
                }
                opcode = -1;
                return true;
            }

            // Size is 5 (packet number is 7)
            if (opcode == ServerToClientPackets.ADD_CLICKABLE_SPRITES) {
                int componentId = 0;
                try {
                    componentId = incoming.readInt();
                    byte spriteIndex = incoming.readSignedByte();
                    Widget component = Widget.cache[componentId];
                    if (component != null) {
                        if (component.backgroundSprites != null && spriteIndex <= component.backgroundSprites.length - 1) {
                            SimpleImage sprite = component.backgroundSprites[spriteIndex];
                            if (sprite != null) {
                                component.enabledSprite = component.backgroundSprites[spriteIndex];
                            }
                        }
                    }
                    opcode = -1;
                } catch (Exception e) {
                    e.printStackTrace();
                    addReportToServer("Packet 7 error caused by interface: " + componentId);
                    addReportToServer(e.getMessage());
                }
                return true;
            }

            // Size is 6 opcode 128
            if (opcode == ServerToClientPackets.SET_FRAME_MODE) {
                int width = incoming.readLEUShortA();
                int height = incoming.readInt();
                frameMode(width, height);
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SEND_EXP_DROP) {
                try {
                    int skill = incoming.readUByte();
                    int exp = incoming.readInt();
                    boolean increment = incoming.readUByte() == 1;
                    ExpCounter.addXP(skill, exp, increment);
                } catch (Exception e) {
                    e.printStackTrace();
                    addReportToServer(e.getMessage());
                }
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.RECEIVE_WIDGET_SLOT) {
                int slot = incoming.readUByte();
                Widget tradeWidget = Widget.cache[52046 + slot];
                tradeSlot.add(new TradeOpacity(tradeWidget, slot, 2));
                Widget.cache[52014].drawingDisabled = false;
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SET_SCROLLBAR_HEIGHT) {
                int interface_ = incoming.readInt();
                int scrollMax = incoming.readShort();
                Widget w = Widget.cache[interface_];
                if (w != null) {
                    w.scrollMax = scrollMax;
                }
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.INTERFACE_SCROLL_RESET) {
                int interface_ = incoming.readInt();
                Widget w = Widget.cache[interface_];
                if (w != null) {
                    w.scrollPosition = 0;
                }
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SEND_WEAPON) {
                int weaponId = incoming.readUShort();
                int ammoId = incoming.readUShort();
                WeaponInterfacesWidget.weaponId = weaponId;
                WeaponInterfacesWidget.ammoId = ammoId;
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SEND_TELEPORT) {
                int length = incoming.readUShort(); // reads an unsigned short as the length of teleports
                //System.out.println("Read length: " + length);
                boolean recent = incoming.readUByte() == 1; // reads a signed byte to determine whether to update recent or favourites tab
                //System.out.println("Update recent tab: " + recent);
                //If it's not recent then it's favorite.
                teleportCategoryIndex = recent ? 2 : 1;
                final int[] sprites = new int[length];
                String[] names = new String[length];
                for (int i = 0; i < length; i++) {
                    sprites[i] = incoming.readUShort(); // reads unsigned short(no point really to make it read a signed short, even tho sprite ids will never exceed 32767, they will also never be negative.
                    names[i] = incoming.readString();
                }
                this.teleportSprites = sprites;
                this.teleportNames = names;
                TeleportWidget.updateTeleportsTab(recent);
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SEND_SPRITE_CHANGE) {
                int interfaceID = incoming.readInt();
                int spriteID = incoming.readShort();

                Widget.cache[interfaceID].enabledSprite = Client.spriteCache.get(spriteID);

                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.UPDATE_PLAYER_RIGHTS) {
                myPrivilege = incoming.readUByte();
                donatorPrivilege = incoming.readUByte();
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.PLAYER_UPDATING) {
                // System.out.println("player updating packet size: " + packetSize);
                updatePlayers(packetSize, incoming);
                loadingMap = false;
                opcode = -1;
                if (debug_packet_info) {
                    addReportToServer("last gpi " + (System.currentTimeMillis() - LAST_GPI) + " ms ago.. call count " + Client.game_tick + " (+" + (game_tick - lastcallcount) + ")");
                }
                lastcallcount = game_tick;
                LAST_GPI = System.currentTimeMillis();
                xpro = 0;
                return true;
            }

            if (opcode == ServerToClientPackets.SHOW_CLANCHAT_OPTIONS) {
                showClanOptions = incoming.readUByte() == 1;
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.ENTITY_FEED) {
                try {
                    pushFeed(incoming.readString(), incoming.readUShort(), incoming.readUShort());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.OPEN_WELCOME_SCREEN) {
                daysSinceRecovChange = incoming.readNegUByte();
                unreadMessages = incoming.readUShortA();
                membersInt = incoming.readUByte();
                anInt1193 = incoming.readIMEInt();
                daysSinceLastLogin = incoming.readUShort();
                if (anInt1193 != 0 && widget_overlay_id == -1) {
                    SignLink.dnslookup(StringUtils.decodeIp(anInt1193));
                    clearTopInterfaces();
                    char character = '\u028A';
                    if (daysSinceRecovChange != 201 || membersInt == 1)
                        character = '\u028F';
                    reportAbuseInput = "";
                    canMute = false;
                    for (int interfaceId = 0; interfaceId < Widget.cache.length; interfaceId++) {
                        if (Widget.cache[interfaceId] == null
                            || Widget.cache[interfaceId].contentType != character)
                            continue;
                        widget_overlay_id = Widget.cache[interfaceId].parent;

                    }
                }
                opcode = -1;
                return true;
            }

            if (opcode == 178) {
                clearRegionalSpawns();
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.DELETE_GROUND_ITEM) {
                localX = incoming.readNegUByte();
                localY = incoming.readUByteS();
                for (int x = localX; x < localX + 8; x++) {
                    for (int y = localY; y < localY + 8; y++)
                        if (scene_items[plane][x][y] != null) {
                            scene_items[plane][x][y] = null;
                            spawn_scene_item(x, y);
                        }
                }
                for (SpawnedObject object = (SpawnedObject) spawns
                    .first(); object != null; object = (SpawnedObject) spawns.next())
                    if (object.x >= localX && object.x < localX + 8 && object.y >= localY && object.y < localY + 8
                        && object.plane == plane)
                        object.getLongetivity = 0;
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SHOW_PLAYER_HEAD_ON_INTERFACE) {
                int playerHeadModelId = incoming.readLEUShortA();
                Widget.cache[playerHeadModelId].model_type = 3;
                if (local_player.desc == null)
                    Widget.cache[playerHeadModelId].model_id = (local_player.appearance_colors[0] << 25)
                        + (local_player.appearance_colors[4] << 20) + (local_player.player_appearance[0] << 15)
                        + (local_player.player_appearance[8] << 10) + (local_player.player_appearance[11] << 5)
                        + local_player.player_appearance[1];
                else
                    Widget.cache[playerHeadModelId].model_id = (int) (0x12345678L
                        + local_player.desc.interfaceType);
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.CLAN_CHAT) {
                try {
                    name = incoming.readString();
                    defaultText = incoming.readString();
                    clanname = incoming.readString();
                    rights = incoming.readUShort();
                    //System.out.println("name is " + defaultText);
                    sendMessage(Character.toUpperCase(defaultText.charAt(0)) + defaultText.substring(1), 16, name);
                    // sendMessage(new ChatMessage(clanname, name,
                    // Character.toUpperCase(defaultText.charAt(0)) + defaultText.substring(1),
                    // 16));
                } catch (Exception e) {
                    e.printStackTrace();
                    addReportToServer(e.getMessage());
                }
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.RESET_CAMERA) {
                cutscene = false;
                for (int l = 0; l < 5; l++)
                    camera_attributes[l] = false;
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.CLEAN_ITEMS_OF_INTERFACE) {
                int id = incoming.readUShort();
                Widget widget = Widget.cache[id];
                for (int slot = 0; slot < widget.inventoryItemId.length; slot++) {
                    widget.inventoryItemId[slot] = -1;
                    widget.inventoryItemId[slot] = 0;
                }
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SPIN_CAMERA) {
                cutscene = true;
                camera_spin_x = incoming.readUByte();
                camera_spin_y = incoming.readUByte();
                camera_spin_z = incoming.readUShort();
                camera_spin_rotation_speed = incoming.readUByte();
                camera_spin_speed = incoming.readUByte();
                if (camera_spin_speed >= 100) {
                    camera_abs_x = camera_spin_x * 128 + 64;
                    camera_abs_y = camera_spin_y * 128 + 64;
                    camera_abs_z = get_tile_pos(plane, camera_abs_y, camera_abs_x) - camera_spin_rotation_speed;
                }
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SEND_SKILL) {
                int skill = incoming.readUByte();
                int level = incoming.readMEInt();
                int xp = incoming.readUByte();

                currentExp[skill] = level;
                currentLevels[skill] = xp;
                maximumLevels[skill] = 1;
                for (int index = 0; index < 98; index++)
                    if (level >= SKILL_EXPERIENCE[index])
                        maximumLevels[skill] = index + 2;
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.CHANGE_WIDGET_TOOLTIP_TEXT) {
                int widget_id = incoming.readShort();
                String text = incoming.readString();

                Widget.cache[widget_id].tooltip = text;
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.CHANGE_WIDGET_TEXT) {
                int widget_id = incoming.readShort();
                String text = incoming.readString();

                if (Widget.cache[widget_id].defaultText != null)
                    Widget.cache[widget_id].defaultText = text;
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.HIT_PREDICTOR) {
                int hit = incoming.readUShort();
                if (hit > -1) {
                    expectedHit.add(new IncomingHit(hit));
                }
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.STAMINA) {
                staminaActive = incoming.readUByte() == 1;
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SEND_SIDE_TAB) {
                int id = incoming.readInt();
                int tab = incoming.readUByteA();
                // System.out.println("id "+id+" vs side tab "+tab);
                if (id == 65535)
                    id = -1;
                tabInterfaceIDs[tab] = id;
                update_tab_producer = true;
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.PLAY_SONG) {
                int id = incoming.readLEUShort();
                if (id == 65535)
                    id = -1;
                if (ClientConstants.SOUNDS_ENABLED && id != current_track && setting.toggle_music && !low_detail && previous_track == 0) {
                    next_track = id;
                    fade_audio = true;
                    resourceProvider.provide(2, next_track);
                }
                current_track = id;
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.NEXT_OR_PREVIOUS_SONG) {
                int id = incoming.readLEUShortA();
                int delay = incoming.readUShortA();
                if (ClientConstants.SOUNDS_ENABLED && setting.toggle_music && !low_detail) {
                    next_track = id;
                    fade_audio = false;
                    resourceProvider.provide(2, next_track);
                    previous_track = delay;
                }
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.PLAY_SOUND_EFFECT) {
                int id = incoming.readUShort();
                int type = incoming.readUByte();
                int delay = incoming.readUShort();
                int volume = incoming.readUShort();
                if (ClientConstants.SOUNDS_ENABLED && !low_detail) {
                    track_ids[trackCount] = id;
                    track_loop[trackCount] = type;
                    audio_delay[trackCount] = delay + Track.delays[id];
                    sound_effect_volume[trackCount] = volume;
                    trackCount++;
                }
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.LOGOUT) {
                logout();
                opcode = -1;
                return false;
            }

            if (opcode == ServerToClientPackets.MOVE_COMPONENT) {
                int x = incoming.readShort();
                int y = incoming.readShort();
                int id = incoming.readInt();
                //System.out.println("x " + x + " y " + y + " id " + id);
                Widget widget = Widget.cache[id];
                widget.x = x;
                widget.y = y;
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SEND_MAP_REGION
                || opcode == ServerToClientPackets.SEND_REGION_MAP_REGION) {
                clearRegionalSpawns();
                int regionX = region_x;
                int regionY = region_y;
                if (opcode == ServerToClientPackets.SEND_MAP_REGION) {
                    regionX = incoming.readUShortA();
                    regionY = incoming.readUShort();
                } else if (opcode == ServerToClientPackets.SEND_REGION_MAP_REGION) {
                    regionY = incoming.readUShortA();
                    incoming.initBitAccess();
                    for (int z = 0; z < 4; z++) {
                        for (int x = 0; x < 13; x++) {
                            for (int y = 0; y < 13; y++) {
                                int visible = incoming.readBits(1);
                                if (visible == 1) {
                                    constructRegionData[z][x][y] = incoming.readBits(26);
                                } else {
                                    constructRegionData[z][x][y] = -1;
                                }
                            }
                        }
                    }
                    incoming.disableBitAccess();
                    regionX = incoming.readUShort();
                    requestMapReconstruct = true;
                }
                if (opcode != ServerToClientPackets.SEND_REGION_MAP_REGION && region_x == regionX
                    && region_y == regionY && loading_phase == 2) {
                    opcode = -1;
                    return true;
                }
                region_x = regionX;
                region_y = regionY;
                next_region_start = (region_x - 6) * 8;
                next_region_end = (region_y - 6) * 8;
                inTutorialIsland = (region_x / 8 == 48 || region_x / 8 == 49) && region_y / 8 == 48;
                if (region_x / 8 == 48 && region_y / 8 == 148)
                    inTutorialIsland = true;
                loading_phase = 1;
                loadingStartTime = System.currentTimeMillis();
                gameScreenImageProducer.init();
                drawLoadingMessages(1, "Loading - please wait.", null);
                gameScreenImageProducer.drawGraphics(screen == ScreenMode.FIXED ? 4 : 0, super.graphics,
                    screen == ScreenMode.FIXED ? 4 : 0);
                if (opcode == 73) {
                    int regionCount = 0;
                    for (int x = (region_x - 6) / 8; x <= (region_x + 6) / 8; x++) {
                        for (int y = (region_y - 6) / 8; y <= (region_y + 6) / 8; y++)
                            regionCount++;
                    }
                    terrainData = new byte[regionCount][];
                    objectData = new byte[regionCount][];
                    mapCoordinates = new int[regionCount];
                    terrainIndices = new int[regionCount];
                    objectIndices = new int[regionCount];
                    regionCount = 0;

                    for (int x = (region_x - 6) / 8; x <= (region_x + 6) / 8; x++) {
                        for (int y = (region_y - 6) / 8; y <= (region_y + 6) / 8; y++) {
                            mapCoordinates[regionCount] = (x << 8) + y;
                            if (inTutorialIsland
                                && (y == 49 || y == 149 || y == 147 || x == 50 || x == 49 && y == 47)) {
                                terrainIndices[regionCount] = -1;
                                objectIndices[regionCount] = -1;
                                regionCount++;
                            } else {
                                int map = terrainIndices[regionCount] = resourceProvider.getMapIdForRegions(0, y, x);
                                if (map != -1) {
                                    resourceProvider.provide(3, map);
                                }

                                int landscape = objectIndices[regionCount] = resourceProvider.getMapIdForRegions(1, y, x);
                                if (landscape != -1) {
                                    resourceProvider.provide(3, landscape);
                                }

                                regionCount++;
                            }
                        }
                    }
                }
                if (opcode == 241) {
                    int totalLegitChunks = 0;
                    int totalChunks[] = new int[676];
                    for (int z = 0; z < 4; z++) {
                        for (int x = 0; x < 13; x++) {
                            for (int y = 0; y < 13; y++) {
                                int tileBits = constructRegionData[z][x][y];
                                if (tileBits != -1) {
                                    int xCoord = tileBits >> 14 & 0x3ff;
                                    int yCoord = tileBits >> 3 & 0x7ff;
                                    int mapRegion = (xCoord / 8 << 8) + yCoord / 8;
                                    for (int idx = 0; idx < totalLegitChunks; idx++) {
                                        if (totalChunks[idx] != mapRegion)
                                            continue;
                                        mapRegion = -1;

                                    }
                                    if (mapRegion != -1) {
                                        totalChunks[totalLegitChunks++] = mapRegion;
                                    }
                                }
                            }
                        }
                    }
                    terrainData = new byte[totalLegitChunks][];
                    objectData = new byte[totalLegitChunks][];
                    mapCoordinates = new int[totalLegitChunks];
                    terrainIndices = new int[totalLegitChunks];
                    objectIndices = new int[totalLegitChunks];
                    for (int idx = 0; idx < totalLegitChunks; idx++) {
                        int region = mapCoordinates[idx] = totalChunks[idx];
                        int l30 = region >> 8 & 0xff;
                        int l31 = region & 0xff;
                        int terrainMapId = terrainIndices[idx] = resourceProvider.getMapIdForRegions(0, l31, l30);
                        if (terrainMapId != -1)
                            resourceProvider.provide(3, terrainMapId);
                        int objectMapId = objectIndices[idx] = resourceProvider.getMapIdForRegions(1, l31, l30);
                        if (objectMapId != -1)
                            resourceProvider.provide(3, objectMapId);
                    }
                }
                int dx = next_region_start - previousAbsoluteX;
                int dy = next_region_end - previousAbsoluteY;
                previousAbsoluteX = next_region_start;
                previousAbsoluteY = next_region_end;
                for (int index = 0; index < 16384; index++) {
                    Npc npc = npcs[index];
                    if (npc != null) {
                        for (int point = 0; point < 10; point++) {
                            npc.waypoint_x[point] -= dx;
                            npc.waypoint_y[point] -= dy;
                        }
                        npc.world_x -= dx * 128;
                        npc.world_y -= dy * 128;
                    }
                }
                for (int index = 0; index < maxPlayers; index++) {
                    Player player = players[index];
                    if (player != null) {
                        for (int point = 0; point < 10; point++) {
                            player.waypoint_x[point] -= dx;
                            player.waypoint_y[point] -= dy;
                        }
                        player.world_x -= dx * 128;
                        player.world_y -= dy * 128;
                    }
                }
                loadingMap = true;
                byte startX = 0;
                byte endX = 104;
                byte stepX = 1;
                if (dx < 0) {
                    startX = 103;
                    endX = -1;
                    stepX = -1;
                }
                byte startY = 0;
                byte endY = 104;
                byte stepY = 1;

                if (dy < 0) {
                    startY = 103;
                    endY = -1;
                    stepY = -1;
                }
                for (int x = startX; x != endX; x += stepX) {
                    for (int y = startY; y != endY; y += stepY) {
                        int shiftedX = x + dx;
                        int shiftedY = y + dy;
                        for (int plane = 0; plane < 4; plane++)
                            if (shiftedX >= 0 && shiftedY >= 0 && shiftedX < 104 && shiftedY < 104) {
                                scene_items[plane][x][y] = scene_items[plane][shiftedX][shiftedY];
                            } else {
                                scene_items[plane][x][y] = null;
                            }
                    }
                }
                for (SpawnedObject object = (SpawnedObject) spawns
                    .first(); object != null; object = (SpawnedObject) spawns.next()) {
                    object.x -= dx;
                    object.y -= dy;
                    if (object.x < 0 || object.y < 0 || object.x >= 104 || object.y >= 104)
                        object.remove();
                }
                if (travel_destination_x != 0) {
                    travel_destination_x -= dx;
                    travel_destination_y -= dy;
                }
                cutscene = false;
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SEND_WALKABLE_INTERFACE) {
                int interfaceId = incoming.readInt();
                if (interfaceId >= 0)
                    resetAnimation(interfaceId);
                openWalkableInterface = interfaceId;
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SEND_MINIMAP_STATE) {
                minimapState = incoming.readUByte();
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SHOW_NPC_HEAD_ON_INTERFACE) {
                int npcId = incoming.readLEUShortA();
                int interfaceId = incoming.readLEUShortA();
                Widget.cache[interfaceId].model_type = 2;
                Widget.cache[interfaceId].model_id = npcId;
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SYSTEM_UPDATE) {
                systemUpdateTime = incoming.readLEUShort() * 30;
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SEND_MULTIPLE_MAP_PACKETS) {
                localY = incoming.readUByte();
                localX = incoming.readNegUByte();
                while (incoming.pos < packetSize) {
                    int k3 = incoming.readUByte();
                    parseRegionPackets(incoming, k3);
                }
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SEND_EARTHQUAKE) {
                int quakeDirection = incoming.readUByte();
                int quakeMagnitude = incoming.readUByte();
                int quakeAmplitude = incoming.readUByte();
                int fourPiOverPeriod = incoming.readUByte();
                camera_attributes[quakeDirection] = true;
                camera_vertical_shake[quakeDirection] = quakeMagnitude;
                camera_vertical_speed[quakeDirection] = quakeAmplitude;
                camera_horizontal_shake[quakeDirection] = fourPiOverPeriod;
                camera_horizontal_speed[quakeDirection] = 0;
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SET_AUTOCAST_ID) {
                int auto = incoming.readUShort();
                if (auto == -1) {
                    autocast = false;
                    autoCastId = 0;
                } else {
                    autocast = true;
                    autoCastId = auto;
                }
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.MYSTERY_BOX_SPIN) {
                spinSpeed = 1;
                Widget.cache[71101].x = 0;
                Widget.cache[71200].x = 0;
                startSpin = true;
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.BLACK_FADING_SCREEN) {
                String text = incoming.readString();
                byte state = incoming.readSignedByte();
                byte seconds = incoming.readSignedByte();
                int drawingWidth = Client.screen == ScreenMode.FIXED ? 519 : Client.window_width;
                int drawingHeight = Client.screen == ScreenMode.FIXED ? 338 : Client.window_height;
                fadingScreen = new BlackFadingScreen(adv_font_bold, text, state, seconds, 0, 0, drawingWidth, drawingHeight, 100);
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SEND_PLAYER_OPTION) {
                int slot = incoming.readNegUByte();
                int lowPriority = incoming.readUByteA();
                String message = incoming.readString();
                if (slot >= 1 && slot <= 5) {
                    if (message.equalsIgnoreCase("null"))
                        message = null;
                    playerOptions[slot - 1] = message;
                    playerOptionsHighPriority[slot - 1] = lowPriority == 0;
                }
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.CLEAR_MINIMAP_FLAG) {
                travel_destination_x = 0;
                opcode = -1;
                return true;
            }

            /**
             * Packet 79: Used to set scrollPosition and scrollMax on an interface.
             */
            if (opcode == ServerToClientPackets.SCROLL_POSITION) {
                int scrollChildId = incoming.readUShort();
                int scrollPosition = incoming.readUShort();
                int scrollMax = incoming.readUShort();
                Widget widget = Widget.cache[scrollChildId];
                if (widget != null && widget.type == 0) {
                    if (scrollPosition < 0)
                        scrollPosition = 0;
                    if (scrollPosition > widget.scrollMax - widget.height)
                        scrollPosition = widget.scrollMax - widget.height;
                    widget.scrollPosition = scrollPosition;
                }
                if (scrollMax == 0 || scrollMax < widget.height) {
                    widget.scrollMax = widget.height + 1;
                } else {
                    widget.scrollMax = scrollMax;
                }
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.ENABLE_NOCLIP) {
                for (int plane = 0; plane < 4; plane++) {
                    for (int x = 1; x < 103; x++) {
                        for (int y = 1; y < 103; y++) {
                            collisionMaps[plane].adjacencies[x][y] = 0;
                        }
                    }
                }
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SEND_URL) {
                String url = incoming.readString();
                Utils.launchURL(url);
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.RECEIVE_BROADCAST) {
                String message = incoming.readString();
                broadcastText = message;
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SEND_CLAN_CHAT_MESSAGE) {
                int type = incoming.readUByte();
                String name = incoming.readString();
                String message = incoming.readString();
                //System.out.println("clan chat msg: " + message);
                sendMessage(message, type, name);
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SEND_MESSAGE) {
                String message = incoming.readString();

                if (message.startsWith("dismissbroadcast##")) {
                    if (broadcast != null) {
                        broadcast.dismiss();
                    }
                    opcode = -1;
                    return true;
                }
                if (message.startsWith("osrsbroadcast##")) {
                    String[] args = message.split("##");
                    String[] args1 = message.split("%%");
                    //System.out.println("args 1: " + Arrays.toString(args1));
                    // System.out.println("args 1 size: " + args1[1].length());
                    int linkIndex = args[1].indexOf(args1[1]);
                    String link = args[1].substring(linkIndex);
                    String text = args[1].substring(0, linkIndex - 2);
                    if (link.equalsIgnoreCase("no_link")) {
                        broadcast = new Announcement(text);
                    } else {
                        broadcast = new Announcement(text, link);
                    }
                    sendMessage(text, 20, "");
                    isDisplayed = true;
                    opcode = -1;
                    return true;
                }

                if (message.endsWith(":spin:")) {
                    startSpin = true;
                    opcode = -1;
                    return true;
                }
                if (message.startsWith("resetsidebars##")) {
               resetsidebars();
                    opcode = -1;
                    return true;
                }
                if (message.endsWith(":clearspin:")) {
                    startSpin = false;
                    opcode = -1;
                    return true;
                }

                if (message.endsWith(":cleartextclicked:")) {
                    clearTextClicked();
                }

                if (message.endsWith(":settextclicked:")) {
                    String[] args = message.split(" ");
                    int id = Integer.parseInt(args[1]);
                    boolean state = Boolean.parseBoolean(args[2]);
                    setTextClicked(id, state);
                }

                if (message.startsWith("npcpetid")) {
                    String[] args = message.split(":");
                    Client.npcPetId = Integer.parseInt(args[1]);
                    opcode = -1;
                    return true;
                }

                if (message.startsWith("prioritizetarget")) {
                    String[] args = message.split(":");
                    Client.singleton.setInteractingWithEntityId(Integer.parseInt(args[1]));
                    opcode = -1;
                    return true;
                }

                if (message.endsWith(":invite:")) {
                    String name = message.substring(0, message.indexOf(":"));
                    long encodedName = StringUtils.encodeBase37(name);
                    boolean ignored = false;
                    for (int index = 0; index < ignoreCount; index++) {
                        if (ignoreListAsLongs[index] != encodedName)
                            continue;
                        ignored = true;

                    }
                    if (!ignored && onTutorialIsland == 0)
                        sendMessage("wishes to invite you to join their ironman group.", 4, name);
                } else if (message.endsWith(":tradereq:")) {
                    String name = message.substring(0, message.indexOf(":"));
                    long encodedName = StringUtils.encodeBase37(name);
                    boolean ignored = false;
                    for (int index = 0; index < ignoreCount; index++) {
                        if (ignoreListAsLongs[index] != encodedName)
                            continue;
                        ignored = true;

                    }
                    if (!ignored && onTutorialIsland == 0)
                        sendMessage("wishes to trade with you.", 4, name);
                } else if (message.endsWith("#url#")) {
                    String link = message.substring(0, message.indexOf("#"));
                    sendMessage("Join us at: ", 9, link);
                } else if (message.endsWith(":duelreqddswhiponly:") || message.endsWith(":duelreqwhiponly:") || message.endsWith(":duelreqnormal:")) {
                    String name = message.substring(0, message.indexOf(":"));
                    long encodedName = StringUtils.encodeBase37(name);
                    boolean ignored = false;
                    for (int count = 0; count < ignoreCount; count++) {
                        if (ignoreListAsLongs[count] != encodedName)
                            continue;
                        ignored = true;

                    }
                    if (!ignored && onTutorialIsland == 0) {
                        if (message.endsWith(":duelreqddswhiponly:")) {
                            sendMessage("wishes to whip and dds duel with you.", 8, name);
                        } else if (message.endsWith(":duelreqwhiponly:")) {
                            sendMessage("wishes to whip only duel with you.", 8, name);
                        } else {
                            sendMessage("wishes to duel with you.", 8, name);
                        }
                    }
                } else if (message.endsWith(":gamblereq:")) {
                    String name = message.substring(0, message.indexOf(":"));
                    long encodedName = StringUtils.encodeBase37(name);
                    boolean ignored = false;
                    for (int count = 0; count < ignoreCount; count++) {
                        if (ignoreListAsLongs[count] != encodedName)
                            continue;
                        ignored = true;

                    }
                    if (!ignored && onTutorialIsland == 0) {
                        if (message.endsWith(":gamblereq:")) {
                            sendMessage("wishes to gamble with you.", 40, name);
                        }
                    }
                } else if (message.endsWith(":chalreq:")) {
                    String name = message.substring(0, message.indexOf(":"));
                    long encodedName = StringUtils.encodeBase37(name);
                    boolean ignored = false;
                    for (int index = 0; index < ignoreCount; index++) {
                        if (ignoreListAsLongs[index] != encodedName)
                            continue;
                        ignored = true;

                    }
                    if (!ignored && onTutorialIsland == 0) {
                        String msg = message.substring(message.indexOf(":") + 1, message.length() - 9);
                        sendMessage(msg, 8, name);
                        // System.out.println("msg is: " + msg);
                    }
                } else {
                    // System.out.println("message is: " + message);
                    sendMessage(message, 0, "");
                }
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.STOP_ALL_ANIMATIONS) {
                for (int index = 0; index < players.length; index++) {
                    if (players[index] != null)
                        players[index].animation = -1;
                }
                for (int index = 0; index < npcs.length; index++) {
                    if (npcs[index] != null)
                        npcs[index].animation = -1;
                }
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.ADD_FRIEND) {
                String friendName = incoming.readNewString();
                int world = incoming.readUnsignedByte();
                // add friend i guess is used for updating online/offline too.
                for (int playerIndex = 0; playerIndex < friendsCount; playerIndex++) {
                    String currentFriend = friendsList[playerIndex];
                    if (currentFriend == null)
                        continue;
                    if (!friendName.equalsIgnoreCase(currentFriend))
                        continue;
                    if (friendsNodeIDs[playerIndex] != world) {
                        friendsNodeIDs[playerIndex] = world;
                        if (world >= 2) {
                            sendMessage(friendName + " has logged in.", 5, "");
                        }
                        if (world <= 1) {
                            sendMessage(friendName + " has logged out.", 5, "");
                        }
                    }
                    friendName = null; // don't add the name to friends list, its already here
                    break;
                }
                if (friendName != null && friendsCount < 200) {
                    friendsListAsLongs[friendsCount] = StringUtils.encodeBase37(friendName);
                    friendsList[friendsCount] = friendName;
                    friendsNodeIDs[friendsCount] = world;
                    friendsCount++;
                }
                for (boolean stopSorting = false; !stopSorting; ) {
                    stopSorting = true;
                    for (int friendIndex = 0; friendIndex < friendsCount - 1; friendIndex++)
                        if (friendsNodeIDs[friendIndex] != nodeID && friendsNodeIDs[friendIndex + 1] == nodeID
                            || friendsNodeIDs[friendIndex] == 0 && friendsNodeIDs[friendIndex + 1] != 0) {
                            int tempFriendNodeId = friendsNodeIDs[friendIndex];
                            friendsNodeIDs[friendIndex] = friendsNodeIDs[friendIndex + 1];
                            friendsNodeIDs[friendIndex + 1] = tempFriendNodeId;
                            String tempFriendName = friendsList[friendIndex];
                            friendsList[friendIndex] = friendsList[friendIndex + 1];
                            friendsList[friendIndex + 1] = tempFriendName;
                            long tempFriendLong = friendsListAsLongs[friendIndex];
                            friendsListAsLongs[friendIndex] = friendsListAsLongs[friendIndex + 1];
                            friendsListAsLongs[friendIndex + 1] = tempFriendLong;
                            stopSorting = false;
                        }
                }
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.REMOVE_FRIEND) {
                String nameHash = incoming.readString();

                for (int i = 0; i < friendsCount; i++) {
                    if (!friendsList[i].equalsIgnoreCase(nameHash)) {
                        continue;
                    }

                    friendsCount--;
                    for (int n = i; n < friendsCount; n++) {
                        friendsList[n] = friendsList[n + 1];
                        friendsNodeIDs[n] = friendsNodeIDs[n + 1];
                        friendsListAsLongs[n] = friendsListAsLongs[n + 1];
                    }
                    break;
                }

                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.ADD_IGNORE) {
                String encodedName = incoming.readString();
                boolean exists = false;
                // THIS IS DIFFERENT FROM 317 WHY THE FUCK? SOMEONE TRYING TO BE CLEVER AND "OPTIMIZE"
                // DIDNT GO WELL - SHADOWRS jan 12 2021
                // old packet OVERWROTE ignores and this just ADDS - so remember to check if theyre already here!!
                for (String ignoredName : ignoreList) {
                    if (ignoredName != null)
                        if (ignoredName.equalsIgnoreCase(encodedName)) { // already there bro
                            exists = true;
                            break;
                        }
                }
                if (!exists && ignoreCount < 200) {
                    ignoreList[ignoreCount] = encodedName;
                    ignoreCount++;
                }
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.REMOVE_IGNORE) {
                String nameHash = incoming.readString();
                int before = ignoreCount;
                for (int index = 0; index < ignoreCount; index++) {
                    if (ignoreList[index].equalsIgnoreCase(nameHash)) {
                        ignoreCount--;
                        System.arraycopy(ignoreListAsLongs, index + 1, ignoreListAsLongs, index, ignoreCount - index);
                        break;
                    }
                }
                if (ignoreCount == before) {
                    addReportToServer("unable to find " + nameHash);
                }
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SEND_TOGGLE_QUICK_PRAYERS) {
                prayClicked = incoming.readUByte() == 1;
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SEND_RUN_ENERGY) {
                runEnergy = incoming.readUByte();
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SEND_TOGGLE_RUN) {
                settings[ConfigUtility.RUN_ORB_ID] = settings[ConfigUtility.RUN_ID] = incoming.readUByte();
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SEND_EXIT) {
                System.exit(1);
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SEND_HINT_ICON) {
                // the first byte, which indicates the type of mob
                hintIconDrawType = incoming.readUByte();
                if (hintIconDrawType == 1) // NPC Hint Arrow
                    // the world index or slot of the npc in the server (which is also the same for
                    // the client (should))
                    hintIconNpcId = incoming.readUShort();
                if (hintIconDrawType >= 2 && hintIconDrawType <= 6) { // Location Hint Arrow
                    if (hintIconDrawType == 2) { // Center
                        hintIconLocationArrowRelX = 64;
                        hintIconLocationArrowRelY = 64;
                    }
                    if (hintIconDrawType == 3) { // West side
                        hintIconLocationArrowRelX = 0;
                        hintIconLocationArrowRelY = 64;
                    }
                    if (hintIconDrawType == 4) { // East side
                        hintIconLocationArrowRelX = 128;
                        hintIconLocationArrowRelY = 64;
                    }
                    if (hintIconDrawType == 5) { // South side
                        hintIconLocationArrowRelX = 64;
                        hintIconLocationArrowRelY = 0;
                    }
                    if (hintIconDrawType == 6) { // North side
                        hintIconLocationArrowRelX = 64;
                        hintIconLocationArrowRelY = 128;
                    }
                    hintIconDrawType = 2;
                    // x offset
                    hintIconX = incoming.readUShort();

                    // y offset
                    hintIconY = incoming.readUShort();

                    // z offset
                    hintIconLocationArrowHeight = incoming.readUByte();
                }
                if (hintIconDrawType == 10) // Player Hint Arrow
                    hintIconPlayerId = incoming.readUShort();
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SEND_DUO_INTERFACE) {
                int mainInterfaceId = incoming.readInt();
                int sidebarOverlayInterfaceId = incoming.readInt();
                if (backDialogueId != -1) {
                    backDialogueId = -1;
                    update_chat_producer = true;
                }
                if (inputDialogState != 0) {
                    inputDialogState = 0;
                    update_chat_producer = true;
                }
                widget_overlay_id = mainInterfaceId;
                overlayInterfaceId = sidebarOverlayInterfaceId;
                update_tab_producer = true;
                continuedDialogue = false;
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SEND_SIDE_TAB_DISABLED_TABS) {
                int tabInterface = incoming.readUShort();
                if (backDialogueId != -1) {
                    backDialogueId = -1;
                    update_chat_producer = true;
                }
                if (inputDialogState != 0) {
                    inputDialogState = 0;
                    update_chat_producer = true;
                }
                overlayInterfaceId = tabInterface;
                update_tab_producer = true;
                continuedDialogue = false;
                opcode = -1;
                return true;
            }

            if (opcode == 79) {
                int id = incoming.readLEUShort();
                int scrollPosition = incoming.readUShortA();
                Widget widget = Widget.cache[id];
                if (widget != null && widget.type == 0) {
                    if (scrollPosition < 0)
                        scrollPosition = 0;
                    if (scrollPosition > widget.scrollMax - widget.height)
                        scrollPosition = widget.scrollMax - widget.height;
                    widget.scrollPosition = scrollPosition;
                }
                opcode = -1;
                return true;
            }

            if (opcode == 68) {
                for (int k5 = 0; k5 < settings.length; k5++)
                    if (settings[k5] != anIntArray1045[k5]) {
                        settings[k5] = anIntArray1045[k5];
                        updateVarp(k5);
                    }
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SEND_RECEIVED_PRIVATE_MESSAGE) {
                String sender = incoming.readString();
                int messageId = incoming.readInt();
                int rights = incoming.readUByte();
                int memberRights = incoming.readUByte();
                boolean ignoreRequest = false;

                if (rights <= 1) {
                    for (int index = 0; index < ignoreCount; index++) {
                        if (!ignoreList[index].equalsIgnoreCase(sender))
                            continue;
                        ignoreRequest = true;

                    }
                }
                if (!ignoreRequest && onTutorialIsland == 0)
                    try {
                        privateMessageIds[privateMessageCount] = messageId;
                        privateMessageCount = (privateMessageCount + 1) % 100;
                        String message = ChatMessageCodec.decode(packetSize - incoming.pos, incoming);
                        List<ChatCrown> crowns = ChatCrown.get(rights, memberRights);
                        String crownPrefix = "";
                        for (ChatCrown c : crowns) {
                            crownPrefix += c.getIdentifier();
                        }

                        sendMessage(message, 3, crownPrefix + sender);
                        addToPrivateChatHistory(sender);
                    } catch (Exception ex) {
                        SignLink.reporterror("cde1");
                        ex.printStackTrace();
                        addReportToServer(ex.getMessage());
                    }
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SEND_REGION) {
                localY = incoming.readNegUByte();
                localX = incoming.readNegUByte();
                opcode = -1;
                return true;
            }

            if (opcode == 24) {
                flashingSidebarId = incoming.readUByteS();
                if (flashingSidebarId == sidebarId) {
                    if (flashingSidebarId == 3)
                        sidebarId = 1;
                    else
                        sidebarId = 3;
                }
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SEND_ITEM_TO_INTERFACE) {
                int widget = incoming.readLEUShort();
                int scale = incoming.readUShort();
                int item = incoming.readUShort();
                if (item == 65535) {
                    Widget.cache[widget].model_type = 0;
                } else {
                    ItemDefinition definition = ItemDefinition.get(item);
                    Widget.cache[widget].model_type = 4;
                    Widget.cache[widget].model_id = item;
                    Widget.cache[widget].modelRotation1 = definition.rotation_y;
                    Widget.cache[widget].modelRotation2 = definition.rotation_x;
                    Widget.cache[widget].modelZoom = (definition.model_zoom * 100 / scale);
                }
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.CONFIRM) {
                int state = incoming.readUShort();
                int value = incoming.readUShort();
                handleConfirmationPacket(state, value);
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SET_DEFENSIVE_AUTOCAST_STATE) {
                int state = incoming.readUShort();
                Widget.cache[24111].active = state != 0;
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.UPDATE_TAB) {
                int value = incoming.readUShort();
                int id = incoming.readUShort();
                if (id == 0) {
                    spellbook = value;
                } else if (id == 1) {
                    questTabId = value;
                }
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SHOW_HIDE_INTERFACE_CONTAINER) {
                try {
                    boolean hide = incoming.readUByte() == 1;
                    int id = incoming.readInt();
                    Widget w = Widget.cache[id];
                    w.invisible = hide;
                    opcode = -1;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }

            if (opcode == ServerToClientPackets.WIDGET_ACTIVE) {
                boolean active = incoming.readUByte() == 1;
                int id = incoming.readUShort();
                Widget.cache[id].active = active;
                //System.out.println("Widget active: "+active+" child: "+id);
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SEND_SOLO_NON_WALKABLE_SIDEBAR_INTERFACE) {
                int id = incoming.readLEUShort();
                resetAnimation(id);
                if (backDialogueId != -1) {
                    backDialogueId = -1;
                    update_chat_producer = true;
                }
                if (inputDialogState != 0) {
                    inputDialogState = 0;
                    update_chat_producer = true;
                }
                overlayInterfaceId = id;
                update_tab_producer = true;
                widget_overlay_id = -1;
                continuedDialogue = false;
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SPECIAL_ATTACK_OPCODE) {
                specialAttack = incoming.readUByte();
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SEND_MULTIPLE_STRINGS) {
                int amount = incoming.readUnsignedByte();

                for (int index = 0; index < amount; index++) {
                    String string = incoming.readString();
                    int id = incoming.readInt();

                    handleRunePouch(string, id);

                    if (string.startsWith("www.") || string.startsWith("http")) {
                        launchURL(string);
                        opcode = -1;
                        return true;
                    }
                    sendString(string, id);
                    if (id >= 33821 && id <= 33921) {
                        clanList[id - 33821] = Utils.replaceIcons(string);
                    }
                }
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SET_INTERFACE_TEXT) {
                try {
                    String text = incoming.readString();
                    int id = incoming.readInt();
                    if (cheapHaxPacket(id, text)) {
                        opcode = -1;
                        return true;
                    }
                    handleRunePouch(text, id);
                    sendString(text, id);
                    if (id >= 33821 && id <= 33921) {
                        clanList[id - 33821] = Utils.replaceIcons(text);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    addReportToServer(e.getMessage());
                }
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.UPDATE_CHAT_MODES) {
                set_public_channel = incoming.readUByte();
                privateChatMode = incoming.readUByte();
                tradeMode = incoming.readUByte();
                update_chat_producer = true;
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SEND_PLAYER_WEIGHT) {
                weight = incoming.readShort();
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.TOURNY_LOBBY_TIMER) {
                lobbyTimer.start(incoming.readShort());
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SEND_MODEL_TO_INTERFACE) {
                int id = incoming.readLEUShortA();
                int model = incoming.readUShort();
                Widget.cache[id].model_type = 1;
                Widget.cache[id].model_id = model;
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SEND_CHANGE_INTERFACE_COLOUR) {
                int intId = incoming.readLEUShortA();
                int color = incoming.readInt();
                if (Widget.cache[intId] != null) {
                    Widget.cache[intId].textColour = color;
                }
                opcode = -1;
                return true;
            }

            /*
             * Packet 53: Setting large amount of items on interfaces in order of receiving,
             * no index is being used to determine the position.
             */
            if (opcode == ServerToClientPackets.SEND_UPDATE_ITEMS) {
                int frameId = -1;
                try {
                    frameId = incoming.readInt();
                    Widget container = Widget.cache[frameId];
                    int totalItems = incoming.readUShort();
                    for (int index = 0; index < totalItems; index++) {
                        int itemAmount = incoming.readUByte();
                        if (itemAmount == 255) {
                            itemAmount = incoming.readIMEInt();
                        }
                        int itemId = incoming.readLEUShortA();
                        container.inventoryItemId[index] = itemId;
                        container.inventoryAmounts[index] = itemAmount;
                    }

                    for (int index = totalItems; index < container.inventoryItemId.length; index++) {
                        container.inventoryItemId[index] = 0;
                        container.inventoryAmounts[index] = 0;
                    }

                    if (container.contentType == 206) {
                        for (int tab = 0; tab < 10; tab++) {
                            int itemAmount = incoming.readSignedByte() << 8 | incoming.readUShort();
                            tabAmounts[tab] = itemAmount;
                        }
                    }
                    //sendMessage("updated items "+Arrays.toString(container.inventoryItemId), 0, "");
                } catch (Exception e) {
                    e.printStackTrace();
                    addReportToServer("Container error caused by interface: " + frameId);
                    addReportToServer(e.getMessage());
                }
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SEND_BANKTABS) {
                for (int tab = 0; tab < 10; tab++) {
                    int itemAmount = incoming.readSignedByte() << 8 | incoming.readUShort();
                    tabAmounts[tab] = itemAmount;
                }
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SEND_EFFECT_TIMER) {
                try {

                    int timer = incoming.readShort();
                    int sprite = incoming.readShort();

                    addEffectTimer(new EffectTimer(timer, sprite));
                } catch (Exception e) {
                    e.printStackTrace();
                    addReportToServer(e.getMessage());
                }
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.PROGRESS_BAR) {
                try {
                    int id = incoming.readInt();
                    Widget.cache[id].currentPercent = incoming.readUShort();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.CLEAR_CLICKED_TEXT) {
                clearTextClicked();
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SET_CLICKED_TEXT) {
                boolean state = incoming.readUByte() == 1;
                int id = incoming.readInt();
                setTextClicked(id, state);
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SET_MODEL_INTERFACE_ZOOM) {
                int scale = incoming.readUShortA();
                int id = incoming.readUShort();
                int pitch = incoming.readUShort();
                int roll = incoming.readLEUShortA();
                Widget.cache[id].modelRotation1 = pitch;
                Widget.cache[id].modelRotation2 = roll;
                Widget.cache[id].modelZoom = scale;
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SET_FRIENDSERVER_STATUS) {
                friendServerStatus = incoming.readUByte();
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.MOVE_CAMERA) { // Gradually turn camera to spatial point.
                cutscene = true;
                camera_tile_target_x = incoming.readUByte();
                camera_tile_target_y = incoming.readUByte();
                camera_tile_height_offset = incoming.readUShort();
                camera_turn_speed = incoming.readUByte();
                camera_turn_angle = incoming.readUByte();
                if (camera_turn_angle >= 100) {
                    int cinCamXViewpointPos = camera_tile_target_x * 128 + 64;
                    int cinCamYViewpointPos = camera_tile_target_y * 128 + 64;
                    int cinCamZViewpointPos = get_tile_pos(plane, cinCamYViewpointPos, cinCamXViewpointPos)
                        - camera_tile_height_offset;
                    int dXPos = cinCamXViewpointPos - camera_abs_x;
                    int dYPos = cinCamYViewpointPos - camera_abs_y;
                    int dZPos = cinCamZViewpointPos - camera_abs_z;
                    int flatDistance = (int) Math.sqrt(dXPos * dXPos + dYPos * dYPos);
                    cam_curve_y = (int) (Math.atan2(dZPos, flatDistance) * 325.94900000000001D) & 0x7ff;
                    cam_curve_x = (int) (Math.atan2(dXPos, dYPos) * -325.94900000000001D) & 0x7ff;
                    if (cam_curve_y < 128)
                        cam_curve_y = 128;
                    if (cam_curve_y > 383)
                        cam_curve_y = 383;
                }
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SEND_INITIALIZE_PACKET) {
                member = incoming.readUByteA();
                localPlayerIndex = incoming.readShort();
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.NPC_UPDATING) {
                updateNPCs(incoming, packetSize);
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SEND_ENTER_AMOUNT) {
                String title = incoming.readString();
                enter_amount_title = title;
                messagePromptRaised = false;
                inputDialogState = 1;
                amountOrNameInput = "";
                update_chat_producer = true;
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SEND_ENTER_NAME) { // Send Enter Name Dialogue (still allows numbers)
                String title = incoming.readString();
                enter_name_title = title;
                messagePromptRaised = false;
                inputDialogState = 2;
                amountOrNameInput = "";
                update_chat_producer = true;
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SEND_NON_WALKABLE_INTERFACE) {
                // ken comment, changed in server's PacketSender.java from short to int to allow
                // interfaces greater than 65000.
                // int interfaceId = incoming.readUShort();
                int interfaceId = incoming.readInt();
                resetAnimation(interfaceId);
                if (overlayInterfaceId != -1) {
                    overlayInterfaceId = -1;
                    update_tab_producer = true;
                }
                if (backDialogueId != -1) {
                    backDialogueId = -1;
                    update_chat_producer = true;
                }
                if (inputDialogState != 0) {
                    inputDialogState = 0;
                    update_chat_producer = true;
                }
                if (interfaceId == 16244) {
                    fullscreenInterfaceID = 16244;
                    widget_overlay_id = 16244;
                }
                widget_overlay_id = interfaceId;
                continuedDialogue = false;
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SEND_WALKABLE_CHATBOX_INTERFACE) {
                dialogueId = incoming.readLEShortA();
                update_chat_producer = true;
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SEND_CONFIG_INT) {
                int id = incoming.readLEUShort();
                int value = incoming.readMEInt();
                anIntArray1045[id] = value;
                if (settings[id] != value) {
                    settings[id] = value;
                    updateVarp(id);
                    if (dialogueId != -1)
                        update_chat_producer = true;
                }
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SEND_CONFIG_BYTE) {
                int id = incoming.readLEUShort();
                byte value = incoming.readSignedByte();
                if (id < anIntArray1045.length) {
                    anIntArray1045[id] = value;
                    if (settings[id] != value) {
                        settings[id] = value;
                        Keybinding.onVarpUpdate(id, value);
                        updateVarp(id);
                        if (dialogueId != -1)
                            update_chat_producer = true;
                    }
                }
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SEND_MULTICOMBAT_ICON) {
                multicombat = incoming.readUByte(); // 1 is active
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SEND_ANIMATE_INTERFACE) {
                int id = incoming.readUShort();
                int animation = incoming.readShort();
                Widget widget = Widget.cache[id];
                widget.defaultAnimationId = animation;
                widget.modelZoom = 796;//In OSRS the chat size is always 796!
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.CLOSE_INTERFACE) {
                if (overlayInterfaceId != -1) {
                    overlayInterfaceId = -1;
                    update_tab_producer = true;
                }
                if (backDialogueId != -1) {
                    backDialogueId = -1;
                    update_chat_producer = true;
                }
                if (inputDialogState != 0) {
                    inputDialogState = 0;
                    update_chat_producer = true;
                }
                if (this.isInputFieldInFocus()) {
                    this.resetInputFieldFocus();
                    inputString = "";
                }
                if (searchingBank) {
                    Widget.cache[26102].active = false;
                    searchingBank = false;
                    update_chat_producer = true;
                    inputDialogState = 0;
                    messagePromptRaised = false;
                    promptInput = "";
                    interfaceInputAction = 1;
                    inputMessage = "";
                }
                widget_overlay_id = -1;
                continuedDialogue = false;
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.CLOSE_DIALOGUE_INTERFACE) {
                if (backDialogueId != -1) {
                    backDialogueId = -1;
                    update_chat_producer = true;
                }
                if (inputDialogState != 0) {
                    inputDialogState = 0;
                    update_chat_producer = true;
                }
                if (this.isInputFieldInFocus()) {
                    this.resetInputFieldFocus();
                    inputString = "";
                }

                continuedDialogue = false;
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.UPDATE_SPECIFIC_ITEM) {
                int widgetId = incoming.readInt();
                Widget widget = Widget.cache[widgetId];
                while (incoming.pos < packetSize) {
                    int slot = incoming.readUSmart();
                    int itemId = incoming.readUShort();
                    int amount = incoming.readUByte();
                    if (amount == 255)
                        amount = incoming.readInt();
                    if (slot >= 0 && slot < widget.inventoryItemId.length) {
                        widget.inventoryItemId[slot] = itemId;
                        widget.inventoryAmounts[slot] = amount;
                    }
                }
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SEND_GFX || opcode == ServerToClientPackets.SEND_GROUND_ITEM
                || opcode == ServerToClientPackets.SEND_ALTER_GROUND_ITEM_COUNT
                || opcode == ServerToClientPackets.SEND_REMOVE_OBJECT || opcode == 105
                || opcode == ServerToClientPackets.SEND_PROJECTILE
                || opcode == ServerToClientPackets.TRANSFORM_PLAYER_TO_OBJECT
                || opcode == ServerToClientPackets.SEND_OBJECT
                || opcode == ServerToClientPackets.SEND_REMOVE_GROUND_ITEM
                || opcode == ServerToClientPackets.ANIMATE_OBJECT || opcode == 215) {
                parseRegionPackets(incoming, opcode);
                opcode = -1;
                return true;
            }

            if (opcode == ServerToClientPackets.SWITCH_TAB) {
                sidebarId = incoming.readNegUByte();
                update_tab_producer = true;
                opcode = -1;
                return true;
            }
            if (opcode == ServerToClientPackets.SEND_CHATBOX_INTERFACE) {
                int id = incoming.readLEUShort();

                resetAnimation(id);
                backDialogueId = id;
                update_chat_producer = true;
                continuedDialogue = false;
                opcode = -1;
                return true;
            }

            SignLink.reporterror(
                "T1 - " + opcode + "," + packetSize + " - " + secondLastOpcode + "," + thirdLastOpcode);
            logout();
        } catch (IOException _ex) {
            addReportToServer("There has been an exception reading packets, dropping client. dropClient()");
            try {
                addReportToServer("Dropping client, not a normal logout. 3");
                dropClient();
            } catch (Exception e) {
                addReportToServer("There was an error dropping the client: dropClient()");
                e.printStackTrace();
                addReportToServer(e.getMessage());
            }
            _ex.printStackTrace();
            addReportToServer(_ex.getMessage());
        } catch (Throwable exception) {
            StringBuilder s2 = new StringBuilder("T2 - " + opcode + "," + secondLastOpcode + "," + thirdLastOpcode + " - " + packetSize + ","
                + (next_region_start + local_player.waypoint_x[0]) + "," + (next_region_end + local_player.waypoint_y[0])
                + " - ");
            for (int j15 = 0; j15 < packetSize && j15 < 50; j15++)
                s2.append(incoming.payload[j15]).append(",");
            SignLink.reporterror(s2.toString());
            exception.printStackTrace();
            addReportToServer(exception.getMessage());
            // logout();
        }
        opcode = -1;
        return true;
    }

    static final Deque<String> reports = new java.util.LinkedList<>();

    public static void addReportToServer(String s) {
        if (reports != null) {
            // append newest to end
            reports.addLast(s);
        }
    }

    private void render() {
        render_cycle++;
        drawPlayers(true);
        drawPlayers(false);
        drawNPCs(true);
        drawNPCs(false);
        render_projectiles();
        render_stationary_graphics();
        if (!cutscene) {
            int tilt = camera_tilt;
            if (maximum_camera_tilt / 256 > tilt)
                tilt = maximum_camera_tilt / 256;

            if (camera_attributes[4] && camera_vertical_speed[4] + 128 > tilt)
                tilt = camera_vertical_speed[4] + 128;

            int pan = camera_pan + camera_angle & 0x7ff;
            //System.out.println("view_dist = " + SceneGraph.view_dist);
            //This determines the field of view of the client aka depth.
            //This FOV is more narrow and is closer to OSRS.
            set_camera_pos(zoom_distance + tilt * ((SceneGraph.view_dist == 9) && (screen == ScreenMode.RESIZABLE) ? 2 : SceneGraph.view_dist == 10 ? 5 : 3),
                tilt, current_camera_pan, get_tile_pos(plane, local_player.world_y, local_player.world_x) - 50, pan, current_camera_tilt);
        }
        int plane;
        if (!cutscene)
            plane = get_visible_planes();
        else
            plane = get_cutscene_planes();

        int x = camera_abs_x;
        int y = camera_abs_z;
        int z = camera_abs_y;
        int curve_y = cam_curve_y;
        int curve_x = cam_curve_x;
        for (int index = 0; index < 5; index++) {
            if (camera_attributes[index]) {
                int random_intensity = (int) ((Math.random() * (double) (camera_vertical_shake[index] * 2 + 1) - (double) camera_vertical_shake[index])
                    + Math.sin((double) camera_horizontal_speed[index] * ((double) camera_horizontal_shake[index] / 100D))
                    * (double) camera_vertical_speed[index]);

                if (index == 0)
                    camera_abs_x += random_intensity;

                if (index == 1)
                    camera_abs_z += random_intensity;

                if (index == 2)
                    camera_abs_y += random_intensity;

                if (index == 3)
                    cam_curve_x = cam_curve_x + random_intensity & 0x7ff;

                if (index == 4) {
                    cam_curve_y += random_intensity;
                    if (cam_curve_y < 128)
                        cam_curve_y = 128;

                    if (cam_curve_y > 383)
                        cam_curve_y = 383;
                }
            }
        }
        Model.obj_exists = true;
        Model.anInt1687 = 0;
        Model.anInt1685 = super.cursor_x - (screen == ScreenMode.FIXED ? 4 : 0);
        Model.anInt1686 = super.cursor_y - (screen == ScreenMode.FIXED ? 4 : 0);
        Rasterizer2D.clear();
        scene.render(camera_abs_x, camera_abs_y, cam_curve_x, camera_abs_z, plane, cam_curve_y);
        scene.reset_interactive_obj();
        render_item_pile_attatchments();
        updateEntities();
        drawHeadIcon();
        render_animated_textures();
        draw3dScreen();

        if (screen != ScreenMode.FIXED) {
            drawChatArea();
            drawMinimap();
            drawTabArea();
        }

        gameScreenImageProducer.drawGraphics(screen == ScreenMode.FIXED ? 4 : 0, super.graphics, screen == ScreenMode.FIXED ? 4 : 0);
        camera_abs_x = x;
        camera_abs_z = y;
        camera_abs_y = z;
        cam_curve_y = curve_y;
        cam_curve_x = curve_x;
    }

    private int privateChatUserListPtr;
    private String[] recentIncomingPrivateChatUserList;

    private void tabToReplyPm() {
        String user = recentIncomingPrivateChatUserList[privateChatUserListPtr];
        if (user == null) {
            sendMessage("You haven't received any messages to which you can reply.", 0, "");
            return;
        }

        openPrivateChatMessageInput(user);
        ++privateChatUserListPtr;
        if (privateChatUserListPtr == 10)
            privateChatUserListPtr = 0;
        boolean next = recentIncomingPrivateChatUserList[privateChatUserListPtr % recentIncomingPrivateChatUserList.length] != null;
        if (!next)
            privateChatUserListPtr = 0;
    }

    private void addToPrivateChatHistory(String user) {
        for (int i = recentIncomingPrivateChatUserList.length - 1; i > 0; i--) {
            boolean remove = recentIncomingPrivateChatUserList[i - 1] == user && i != 1;
            if (remove)
                continue;

            recentIncomingPrivateChatUserList[i] = recentIncomingPrivateChatUserList[i - 1];
        }

        recentIncomingPrivateChatUserList[0] = user;
    }

    private void openPrivateChatMessageInput(String user) {
        update_chat_producer = true;
        inputDialogState = 0;
        messagePromptRaised = true;
        promptInput = "";
        interfaceInputAction = 3;
//        aLong953 = userHash;
        this.selectedSocialListName = user;
        inputMessage = "Enter message to send to " + selectedSocialListName;
    }

    private void processMinimapActions() {
        if (widget_overlay_id == 16244) {
            return;
        }
        final boolean fixed = screen == ScreenMode.FIXED;
        if (fixed ? super.cursor_x >= 542 && super.cursor_x <= 579 && super.cursor_y >= 2 && super.cursor_y <= 38
            : super.cursor_x >= window_width - 180 && super.cursor_x <= window_width - 139 && super.cursor_y >= 0
            && super.cursor_y <= 40) {
            menuActionText[1] = "Face North";
            menuActionTypes[1] = 696;
            menuActionRow = 2;
        }
        if (screen != ScreenMode.FIXED && settings[ConfigUtility.SIDE_STONES_ARRANGEMENT_ID] == 1) {
            if (super.cursor_x >= window_width - 26 && super.cursor_x <= window_width - 1 && super.cursor_y >= 2
                && super.cursor_y <= 24) {
                menuActionText[1] = "Logout";
                menuActionTypes[1] = 700;
                menuActionRow = 2;
            }
        }

        if (settings[ConfigUtility.DATA_ORBS_ID] == 1) {
            if (expCounterHover) {
                menuActionText[1] = setting.show_hit_predictor ? "Toggle exp drops" : "Toggle hit drops";
                menuActionTypes[1] = 258;
                menuActionRow = 2;
            }
            if (bankHover) {
                menuActionText[1] = "Bank all";
                menuActionTypes[1] = 1510;
                menuActionRow = 2;
            }
            if (potionsHover) {
                menuActionText[1] = "Fill potions";
                menuActionTypes[1] = 1511;
                menuActionRow = 2;
            }
            if (healHover) {
                menuActionText[1] = "Heal";
                menuActionTypes[1] = 1512;
                menuActionRow = 2;
            }
            if (prayHover) {
                menuActionText[2] = prayClicked ? "Turn Quick Prayers off" : "Turn Quick Prayers on";
                menuActionTypes[2] = 1500;
                menuActionRow = 2;
                menuActionText[1] = "Setup Quick Prayers";
                menuActionTypes[1] = 1506;
                menuActionRow = 3;
            }
            if (runHover) {
                menuActionText[1] = settings[ConfigUtility.RUN_ORB_ID] == 1 ? "Toggle Run" : "Toggle Run";
                menuActionTypes[1] = 1050;
                menuActionRow = 2;
            }
        }
    }

    /**
     * Gets the progress color for the xp bar
     *
     * @param percent
     * @return
     */
    public static int getProgressColor(int percent) {
        if (percent <= 15) {
            return 0x808080;
        }
        if (percent <= 45) {
            return 0x7f7f00;
        }
        if (percent <= 65) {
            return 0x999900;
        }
        if (percent <= 75) {
            return 0xb2b200;
        }
        if (percent <= 90) {
            return 0x007f00;
        }
        return 31744;
    }

    public static int getXPForLevel(int level) {
        int points = 0;
        int output = 0;
        for (int lvl = 1; lvl <= level; lvl++) {
            points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
            if (lvl >= level) {
                return output;
            }
            output = (int) Math.floor(points / 4);
        }
        return 0;
    }

    private boolean runHover;
    private boolean prayHover;
    private boolean prayClicked;
    private boolean expCounterHover;
    private boolean bankHover;
    private boolean healHover;
    private boolean potionsHover;

    public int getOrbTextColor(int statusInt) {
        if (statusInt >= 75 && statusInt <= Integer.MAX_VALUE)
            return 0x00FF00;
        else if (statusInt >= 50 && statusInt <= 74)
            return 0xFFFF00;
        else if (statusInt >= 25 && statusInt <= 49)
            return 0xFF981F;
        else
            return 0xFF0000;
    }

    public void clearTopInterfaces() {
        // close interface
        packetSender.sendInterfaceClear();
        if (overlayInterfaceId != -1) {
            overlayInterfaceId = -1;
            continuedDialogue = false;
            update_tab_producer = true;
        }
        if (backDialogueId != -1) {
            backDialogueId = -1;
            update_chat_producer = true;
            continuedDialogue = false;
        }
        widget_overlay_id = -1;
        fullscreenInterfaceID = -1;
    }

    public void addObject(int x, int y, int objectId, int face, int type, int height) {
        int mX = region_x - 6;
        int mY = region_y - 6;
        int x2 = x - mX * 8;
        int y2 = y - mY * 8;
        int i15 = 40 >> 2;
        int l17 = objectGroups[i15];
        if (y2 > 0 && y2 < 103 && x2 > 0 && x2 < 103) {
            requestSpawnObject(-1, objectId, face, l17, y2, type, height, x2, 0);

        }
    }

    @SuppressWarnings("unused")
    private int currentTrackPlaying;

    public Settings setting;

    public Client() {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            e.printStackTrace();
            addReportToServer(t.toString());
            addReportToServer(e.getMessage());
        });
        addReportToServer("client init");
        expectedHit = new ArrayList<>();
        setting = new Settings();
        chatMessages = new ChatMessage[500];
        splitPrivateChatMessages = new ArrayList<>(Collections.nCopies(500, null));
        packetSender = new PacketSender(null);
        chatBuffer = new Buffer(new byte[40_000]);
        outBuffer = new Buffer(new byte[40_000]);
        loginBuffer = new Buffer(new byte[40_000]);
        soundsAreEnabled = true;
        // Buffer size 40k is supposedly what OSRS uses.
        incoming = new Buffer(new byte[40_000]);
        fullscreenInterfaceID = -1;
        chatRights = new int[500];
        sound_effect_volume = new int[50];
        chatTypeView = 0;
        clanChatMode = 0;
        cButtonHPos = -1;
        currentTrackPlaying = -1;
        cButtonCPos = 0;
        //serverAddress = ClientConstants.SERVER_ADDRESS;
        travel_distances = new int[104][104];
        friendsNodeIDs = new int[200];
        scene_items = new LinkedList[4][104][104];
        update_flame_components = false;
        npcs = new Npc[16_384];
        local_npcs = new int[16_384];
        removedMobs = new int[1000];
        soundEnabled = true;
        widget_overlay_id = -1;
        currentExp = new int[SkillConstants.SKILL_COUNT];
        camera_vertical_shake = new int[5];
        camera_attributes = new boolean[5];
        drawFlames = false;
        reportAbuseInput = "";
        localPlayerIndex = -1;
        menuOpen = false;
        inputString = "";
        maxNpcs = 16_384;
        maxPlayers = 2_048;
        LOCAL_PLAYER_INDEX = 2_047;
        players = new Player[maxPlayers];
        local_players = new int[maxPlayers];
        mobsAwaitingUpdate = new int[maxPlayers];
        playerSynchronizationBuffers = new Buffer[maxPlayers];
        anInt897 = 1;
        waypoints = new int[104][104];
        tmpTexture = new byte[16_384];
        currentLevels = new int[SkillConstants.SKILL_COUNT];
        ignoreListAsLongs = new long[100];
        loadingError = false;
        camera_horizontal_shake = new int[5];
        tile_cycle_map = new int[104][104];
        sideIcons = new SimpleImage[15];
        aBoolean954 = true;
        friendsListAsLongs = new long[200];
        current_track = -1;
        drawingFlames = false;
        scene_draw_x = -1;
        scene_draw_y = -1;
        anIntArray968 = new int[33];
        anIntArray969 = new int[256];
        indices = new FileStore[5];
        settings = new int[10_000];
        aBoolean972 = false;
        spokenMaxCount = 50;
        scene_text_x = new int[spokenMaxCount];
        scene_text_y = new int[spokenMaxCount];
        scene_text_height = new int[spokenMaxCount];
        scene_text_center_x = new int[spokenMaxCount];
        scene_text_color = new int[spokenMaxCount];
        scene_text_effect = new int[spokenMaxCount];
        scene_text_update_cycle = new int[spokenMaxCount];
        spokenMessage = new String[spokenMaxCount];
        lastKnownPlane = -1;
        hitMarks = new SimpleImage[15];
        characterDesignColours = new int[5];
        aBoolean994 = false;
        amountOrNameInput = "";
        projectiles = new LinkedList();
        aBoolean1017 = false;
        openWalkableInterface = -1;
        camera_horizontal_speed = new int[5];
        updateCharacterCreation = false;
        mapFunctions = new SimpleImage[124]; // 116 new, revert back to 88 untill further notice
        dialogueId = -1;
        maximumLevels = new int[SkillConstants.SKILL_COUNT];
        anIntArray1045 = new int[2_000];
        characterGender = true;
        minimapLeft = new int[152];
        minimapLineWidth = new int[152];
        flashingSidebarId = -1;
        incompleteAnimables = new LinkedList();
        anIntArray1057 = new int[33];
        aClass9_1059 = new Widget();
        mapScenes = new IndexedImage[116];
        barFillColor = 0x4d4233;
        characterClothing = new int[7];
        minimapHintX = new int[1_000];
        minimapHintY = new int[1_000];
        loadingMap = false;
        friendsList = new String[200];
        ignoreList = new String[100];
        firstMenuAction = new int[500];
        secondMenuAction = new int[500];
        menuActionTypes = new int[500];
        selectedMenuActions = new long[500];
        headIcons = new SimpleImage[20];
        skullIcons = new SimpleImage[20];
        headIconsHint = new SimpleImage[20];
        autoBackgroundSprites = new SimpleImage[20];
        update_tab_producer = false;
        inputMessage = "";
        playerOptions = new String[5];
        playerOptionsHighPriority = new boolean[5];
        constructRegionData = new int[4][13][13];
        anInt1132 = 2;
        minimapHint = new SimpleImage[1_000];
        inTutorialIsland = false;
        continuedDialogue = false;
        crosses = new SimpleImage[8];
        loggedIn = false;
        canMute = false;
        requestMapReconstruct = false;
        cutscene = false;
        anInt1171 = 1;
        myUsername = "";
        myPassword = "";
        genericLoadingError = false;
        reportAbuseInterfaceID = -1;
        spawns = new LinkedList();
        camera_tilt = 128;
        overlayInterfaceId = -1;
        menuActionText = new String[500];
        camera_vertical_speed = new int[5];
        track_ids = new int[50];
        anInt1210 = 2;
        chatScrollHeight = 78;
        promptInput = "";
        sidebarId = 3;
        update_chat_producer = false;
        fade_audio = true;
        collisionMaps = new CollisionMap[4];
        privateMessageIds = new int[100];
        track_loop = new int[50];
        aBoolean1242 = false;
        audio_delay = new int[50];
        rsAlreadyLoaded = false;
        update_producers = false;
        messagePromptRaised = false;
        firstLoginMessage = "";
        secondLoginMessage = "";
        backDialogueId = -1;
        anInt1279 = 2;
        walking_queue_x = new int[4_000];
        walking_queue_y = new int[4_000];
    }

    public int rights;
    public String name;
    public String defaultText;
    public String clanname;
    private final int[] chatRights;
    public int chatTypeView;
    public int clanChatMode;
    private boolean autocast;
    public int autoCastId = 0;
    public static SimpleImage[] specialBarSprite;
    public static final SpriteCache spriteCache = new SpriteCache();
    private ProducingGraphicsBuffer leftFrame;
    private ProducingGraphicsBuffer topFrame;
    private int ignoreCount;
    private long loadingStartTime;
    private int[][] travel_distances;
    private int[] friendsNodeIDs;
    private LinkedList[][][] scene_items;
    private int[] anIntArray828;
    private int[] anIntArray829;
    private volatile boolean update_flame_components;
    private int loginScreenState = 0;
    private Npc[] npcs;
    private int npcs_in_region;
    private int[] local_npcs;
    private int removedMobCount;
    private int[] removedMobs;
    private int lastOpcode;
    private int secondLastOpcode;
    private int thirdLastOpcode;
    public String clickToContinueString;
    public String prayerBook;
    private int privateChatMode;
    public boolean soundEnabled;
    private static int anInt849;
    private int[] anIntArray850;
    private int[] anIntArray851;
    private int[] anIntArray852;
    private int[] anIntArray853;
    private int hintIconDrawType;
    public static int widget_overlay_id; //The active interface (widget)
    private Stopwatch chatDelay = new Stopwatch();
    public int camera_abs_x;
    public int camera_abs_z;
    public int camera_abs_y;
    private InformationFile informationFile = new InformationFile();
    private int cam_curve_y;
    private int cam_curve_x;
    private int myPrivilege, donatorPrivilege;
    public final int[] currentExp;
    private SimpleImage mapFlag;
    private SimpleImage mapMarker;
    private final int[] camera_vertical_shake;
    private final boolean[] camera_attributes;
    private int weight;
    //private MouseDetection mouseDetection;
    private volatile boolean drawFlames;
    private String reportAbuseInput;
    public int localPlayerIndex;
    public boolean menuOpen;
    private int frameFocusedInterface;
    private String inputString;
    private final int maxPlayers;
    private final int maxNpcs;
    private final int LOCAL_PLAYER_INDEX;
    private Player[] players;
    private int players_in_region;
    private int[] local_players;
    private int mobsAwaitingUpdateCount;
    private int[] mobsAwaitingUpdate;
    private Buffer[] playerSynchronizationBuffers;
    private int camera_angle;
    public int anInt897;
    private String[] clanList = new String[100];
    private int friendsCount;
    private int friendServerStatus;
    private int[][] waypoints;
    private byte[] tmpTexture;
    private int anInt913;
    private int crossX;
    private int crossY;
    private int crossIndex;
    private int crossType;
    // private int plane;
    public int plane;
    public final int[] currentLevels;
    private static int anInt924;
    private final long[] ignoreListAsLongs;
    private boolean loadingError;
    private final int[] camera_horizontal_shake;
    private int[][] tile_cycle_map;
    private SimpleImage aClass30_Sub2_Sub1_Sub1_931;
    private SimpleImage aClass30_Sub2_Sub1_Sub1_932;
    private int hintIconPlayerId;
    private int hintIconX;
    private int hintIconY;
    private int hintIconLocationArrowHeight;
    private int hintIconLocationArrowRelX;
    private int hintIconLocationArrowRelY;
    private int animation_step;
    private SceneGraph scene;
    private SimpleImage[] sideIcons;
    private int menuScreenArea;
    private int menuOffsetX;
    private int menuOffsetY;
    private int menuWidth;
    private int menuHeight;
    private long aLong953;
    private String selectedSocialListName;
    private boolean aBoolean954;
    private long[] friendsListAsLongs;
    private int current_track;
    private static int nodeID = 10;
    private static boolean isMembers = true;
    private static boolean low_detail = ClientConstants.CLIENT_LOW_MEMORY;
    private volatile boolean drawingFlames;
    private int scene_draw_x;
    private int scene_draw_y;
    private final int[] SPOKEN_PALETTE = {0xffff00, 0xff0000, 65280, 65535, 0xff00ff, 0xffffff};
    private IndexedImage titleBoxIndexedImage;
    private IndexedImage titleButtonIndexedImage;
    private final int[] anIntArray968;
    private final int[] anIntArray969;
    public final FileStore[] indices;
    public int settings[];
    private boolean aBoolean972;
    private final int spokenMaxCount;
    private final int[] scene_text_x;
    private final int[] scene_text_y;
    private final int[] scene_text_height;
    private final int[] scene_text_center_x;
    private final int[] scene_text_color;
    private final int[] scene_text_effect;
    private final int[] scene_text_update_cycle;
    private final String[] spokenMessage;
    private int maximum_camera_tilt;
    private int lastKnownPlane;
    private static int anInt986;
    private SimpleImage[] hitMarks;
    public int anInt988;
    private int draggingCycles;
    private final int[] characterDesignColours;
    private final boolean aBoolean994;
    private int camera_tile_target_x;
    private int camera_tile_target_y;
    private int camera_tile_height_offset;
    private int camera_turn_speed;
    private int camera_turn_angle;
    private IsaacCipher encryption;
    private SimpleImage multiOverlay;
    public static final int[][] APPEARANCE_COLORS = {
        {6798, 107, 10283, 16, 4797, 7744, 5799, 4634, 33697, 22433, 2983, 54193},
        {8741, 12, 64030, 43162, 7735, 8404, 1701, 38430, 24094, 10153, 56621, 4783, 1341, 16578, 35003, 25239},
        {25238, 8742, 12, 64030, 43162, 7735, 8404, 1701, 38430, 24094, 10153, 56621, 4783, 1341, 16578, 35003},
        {4626, 11146, 6439, 12, 4758, 10270},
        {4550, 4537, 5681, 5673, 5790, 6806, 8076, 4574, 15909, 32689, 130770, 947, 60359, 32433, 4960, 76770, 491770}
    };
    public String amountOrNameInput;
    private static int anInt1005;
    private int daysSinceLastLogin;
    private int packetSize;
    private int opcode;
    static long LAST_GPI;
    private int timeoutCounter;
    public int pingPacketCounter;
    private int afkCountdown;
    private LinkedList projectiles;
    private int current_camera_pan;
    private int current_camera_tilt;
    private int anInt1016;
    private boolean aBoolean1017;
    public int openWalkableInterface;
    private static final int[] SKILL_EXPERIENCE;
    private int minimapState;

    public String getInputString() {
        return inputString;
    }

    public void setInputString(String input) {
        this.inputString = input;
    }

    public int getLoading_phase() {
        return loading_phase;
    }

    public void setLoading_phase(int loading_phase) {
        this.loading_phase = loading_phase;
    }

    private int loading_phase;
    private SimpleImage scrollBar1;
    private SimpleImage scrollBar2;
    private int focusedViewportWidget;
    private final int[] camera_horizontal_speed;
    private boolean updateCharacterCreation;
    private SimpleImage[] mapFunctions;
    // private int regionBaseX;
    // private int regionBaseY;
    public int next_region_start;
    public int next_region_end;
    private int previousAbsoluteX;
    private int previousAbsoluteY;
    private int loginFailures;
    private int focusedChatWidget;
    private int anInt1040;
    private int anInt1041;
    public int dialogueId;
    public final int[] maximumLevels;
    private final int[] anIntArray1045;
    private int member;
    private boolean characterGender;
    private int focusedSidebarWidget;
    private String loading_bar_string;
    private static int anInt1051;
    private final int[] minimapLeft;
    private Archive title_archive;
    private int flashingSidebarId;
    private int multicombat;
    private LinkedList incompleteAnimables;
    private final int[] anIntArray1057;
    public final Widget aClass9_1059;
    private IndexedImage[] mapScenes;
    public int trackCount;
    private final int barFillColor;
    private int interfaceInputAction;
    private final int[] characterClothing;
    private int mouseInvInterfaceIndex;
    private int lastActiveInvInterface;
    public ResourceProvider resourceProvider;
    public int region_x;
    public int region_y;
    private int objectIconCount;
    private int[] minimapHintX;
    private int[] minimapHintY;
    private SimpleImage mapDotItem;
    private SimpleImage mapDotNPC;
    private SimpleImage mapDotPlayer;
    private SimpleImage mapDotFriend;
    private SimpleImage mapDotTeam;
    private SimpleImage mapDotClan;
    private int loading_bar_percent;
    private boolean loadingMap;
    private String[] friendsList;
    private String[] ignoreList;
    private Buffer incoming;
    private int focusedDragWidget;
    private int dragFromSlot;
    private int activeInterfaceType;
    private int mouseDragX;
    private int mouseDragY;
    public static int chatScrollAmount;
    public static int spellId = 0;
    public static int totalRead = 0;
    private int[] firstMenuAction;
    private int[] secondMenuAction;
    public int[] menuActionTypes;
    private long[] selectedMenuActions;
    private SimpleImage[] headIcons;
    private SimpleImage[] skullIcons;
    private SimpleImage[] headIconsHint;
    public static SimpleImage[] autoBackgroundSprites;
    private static int anInt1097;
    private int camera_spin_x;
    private int camera_spin_y;
    private int camera_spin_z;
    private int camera_spin_rotation_speed;
    private int camera_spin_speed;
    public static boolean update_tab_producer;
    private int systemUpdateTime;
    private ProducingGraphicsBuffer topLeft1BackgroundTile;
    private ProducingGraphicsBuffer bottomLeft1BackgroundTile;
    private static ProducingGraphicsBuffer loginBoxImageProducer;
    private ProducingGraphicsBuffer titleScreen;
    private ProducingGraphicsBuffer flameLeftBackground;
    private ProducingGraphicsBuffer flameRightBackground;
    private ProducingGraphicsBuffer bottomLeft0BackgroundTile;
    private ProducingGraphicsBuffer bottomRightImageProducer;
    private ProducingGraphicsBuffer loginMusicImageProducer;
    private ProducingGraphicsBuffer middleLeft1BackgroundTile;
    private ProducingGraphicsBuffer aRSImageProducer_1115;
    private static int anInt1117;
    private int membersInt;
    public String inputMessage;
    private SimpleImage compass;
    private ProducingGraphicsBuffer chatSettingImageProducer;
    public static Player local_player;
    private final String[] playerOptions;
    private final boolean[] playerOptionsHighPriority;
    private final int[][][] constructRegionData;
    public static final int[] tabInterfaceIDs = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1};
    private int camera_tilt_offset;
    public int anInt1132;
    public int menuActionRow;
    private static int anInt1134;
    private int spellButtonUsed = -1;
    private int widget_highlighted;
    private int anInt1137;
    private int selectedTargetMask;
    private String selected_target_id;
    private SimpleImage[] minimapHint;
    private boolean inTutorialIsland;
    private static int anInt1142;
    public int runEnergy;
    public boolean continuedDialogue;
    private SimpleImage[] crosses;
    private IndexedImage[] titleIndexedImages;
    private int unreadMessages;
    private static int anInt1155;
    private static boolean fpsOn;
    public static boolean loggedIn;
    private boolean canMute;
    public boolean searchingBank;
    private boolean requestMapReconstruct;
    private boolean cutscene;
    public static int game_tick;
    static long lastcallcount;
    private static final String validUserPassChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"\243$%^&*()-_=+[{]};:'@#~,<.>/?\\| ";
    private static ProducingGraphicsBuffer tabImageProducer;
    private ProducingGraphicsBuffer minimapImageProducer;
    private static ProducingGraphicsBuffer gameScreenImageProducer;
    private static ProducingGraphicsBuffer chatboxImageProducer;
    private int daysSinceRecovChange;
    private BufferedConnection socketStream;
    public IsaacCipher cipher;
    public Buffer outBuffer;
    public PacketSender packetSender;
    private Buffer loginBuffer;
    private int privateMessageCount;
    private int map_zoom;
    public int anInt1171;
    public String myUsername;
    public String myPassword;
    private boolean showClanOptions;
    private static int anInt1175;
    private boolean genericLoadingError;
    private final int[] objectGroups = {0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3};
    private int reportAbuseInterfaceID;
    private LinkedList spawns;
    private static int[] chatOffsets;
    private static int[] anIntArray1181;
    private static int[] viewportOffsets;
    private byte[][] terrainData;
    private int camera_tilt;
    private int camera_pan;
    private int camera_pan_modifier;
    private int camera_tilt_modifier;
    private static int anInt1188;
    public int overlayInterfaceId;
    private int[] anIntArray1190;
    private int[] anIntArray1191;
    public Buffer chatBuffer;
    private int anInt1193;
    private int splitPrivateChat;
    private IndexedImage mapBack;
    public String[] menuActionText;
    private SimpleImage flameLeftSprite;
    private SimpleImage flameRightSprite;
    private final int[] camera_vertical_speed;
    public static final int[] SHIRT_SECONDARY_COLORS = {9104, 10275, 7595, 3610, 7975, 8526, 918, 38802, 24466, 10145,
        58654, 5027, 1457, 16565, 34991, 25486};
    private static boolean flagged;
    public final int[] track_ids;
    private int map_rotation;
    public int anInt1210;
    static int chatScrollHeight;
    private String promptInput;
    private int anInt1213;
    private int[][][] tileHeights;
    private long serverSeed;
    public int loginScreenCursorPos;
    private long aLong1220;
    public static int sidebarId;
    private int hintIconNpcId;
    public static boolean update_chat_producer;
    public int inputDialogState;
    private static int anInt1226;
    public int next_track;
    private boolean fade_audio;
    private final int[] minimapLineWidth;
    private CollisionMap[] collisionMaps;
    public static int BIT_MASKS[];
    private int[] mapCoordinates;
    private int[] terrainIndices;
    private int[] objectIndices;
    private int anInt1237;
    private int anInt1238;
    public final int anInt1239 = 100;
    private final int[] privateMessageIds;
    public final int[] track_loop;
    private boolean aBoolean1242;
    private int item_container_cycle;
    private int atInventoryInterface;
    private int atInventoryIndex;
    private int atInventoryInterfaceType;
    private byte[][] objectData;
    private int tradeMode;
    private int showSpokenEffects;
    public final int[] audio_delay;
    private int onTutorialIsland;
    private final boolean rsAlreadyLoaded;
    private int useOneMouseButton;
    public int anInt1254;
    private boolean update_producers;
    public boolean messagePromptRaised;
    private byte[][][] tileFlags;
    private int previous_track;
    private int travel_destination_x;
    private int travel_destination_y;
    private SimpleImage minimapImage;
    private int markerAngle;
    private long lastMarkerRotation;
    private int destination_mask;
    private int render_cycle;
    public String firstLoginMessage;
    public String secondLoginMessage;
    private int localX;
    private int localY;
    public static AdvancedFont adv_font_small;
    public static AdvancedFont adv_font_regular;
    public static AdvancedFont adv_font_bold;
    public static AdvancedFont adv_font_fancy;
    private int anInt1275;
    public int backDialogueId;
    private int camera_pan_offset;
    public int anInt1279;
    private int[] walking_queue_x;
    private int[] walking_queue_y;
    private int item_highlighted;
    private int selectedItemIdSlot;
    private int interfaceitemSelectionTypeIn;
    private int useItem;
    private String selectedItemName;
    private int set_public_channel;
    private static int current_walking_queue_length;
    public static int anInt1290;
    public static final String serverAddress = ClientConstants.SERVER_ADDRESS;
    public int drawCount;
    public int fullscreenInterfaceID;
    public int tabTooltipSupportId;// 377
    public int gameTooltipSupportId;// 377
    public int anInt1315;// 377
    public int chatTooltipSupportId;// 377
    public static int anInt1501;// 377
    public static int[] fullscreen_texture_raster;
    public static boolean isShiftPressed;
    public static boolean isCtrlPressed;

    public void resetAllImageProducers() {
        if (super.fullGameScreen != null) {
            return;
        }
        chatboxImageProducer = null;
        minimapImageProducer = null;
        tabImageProducer = null;
        gameScreenImageProducer = null;
        chatSettingImageProducer = null;
        topLeft1BackgroundTile = null;
        bottomLeft1BackgroundTile = null;
        loginBoxImageProducer = null;
        flameLeftBackground = null;
        flameRightBackground = null;
        bottomLeft0BackgroundTile = null;
        bottomRightImageProducer = null;
        loginMusicImageProducer = null;
        middleLeft1BackgroundTile = null;
        aRSImageProducer_1115 = null;
        super.fullGameScreen = new ProducingGraphicsBuffer(765, 503);
        update_producers = true;
    }

    public void setMouseDragY(int mouseDragY) {
        this.mouseDragY = mouseDragY;
    }

    public int getMouseDragY() {
        return mouseDragY;
    }

    public void setActiveInterfaceType(int activeInterfaceType) {
        this.activeInterfaceType = activeInterfaceType;
    }

    public int getActiveInterfaceType() {
        return activeInterfaceType;
    }

    @Override
    public void mouseWheelDragged(int pan, int tilt) {
        if (!mouse_wheel_down) {
            return;
        }
        this.camera_pan_modifier += pan * 3;
        this.camera_tilt_modifier += (tilt << 1);
    }

    //Also known as renderGroundItemNames
    private void render_item_pile_attatchments() {
        if (!setting.toggle_item_pile_names)
            return;

        for (int x = 0; x < 104; x++) {
            for (int y = 0; y < 104; y++) {
                LinkedList node = scene_items[plane][x][y];
                int offset = 12;
                if (node != null) {
                    for (Item item = (Item) node.last(); item != null; item = (Item) node.previous()) {
                        ItemDefinition itemDef = ItemDefinition.get(item.id);
                        get_scene_pos((x << 7) + 64, 64, (y << 7) + 64);
                        if (itemDef.cost < 0xC350 && item.quantity < 0x186A0) {
                            if (setting.filter_item_pile_names)
                                continue;
                        }
                        if (scene_draw_x > -1 && setting.toggle_item_pile_names) {
                            String color = "<trans=120><col=ffb000>";//Fallback
                            adv_font_small.draw_centered((itemDef.cost >= 0xC350 || item.quantity >= 0x186A0 ? color : "<trans=120>") + itemDef.name + (item.quantity > 1 ? "</col> (" + set_k_or_m((int) item.quantity) + "</col>)" : ""), scene_draw_x, scene_draw_y - offset, 0xffffff, 1);
                            offset += 12;
                        }
                    }
                }
            }
        }
    }

    /**
     * If toggled, render ground item names and lootbeams
     * Use {@link #render_item_pile_attatchments} instead
     */
    @Deprecated
    private void renderGroundItemNames() {
        for (int x = 0; x < 104; x++) {
            for (int y = 0; y < 104; y++) {
                LinkedList node = scene_items[plane][x][y];
                int offset = 12;
                if (node != null) {
                    for (Item item = (Item) node.last(); item != null; item = (Item) node.previous()) {
                        ItemDefinition item_definition = ItemDefinition.get(item.id);
                        final Item it = item;
                        get_scene_pos((x << 7) + 64, 64, (y << 7) + 64);
                        // Red if default value is >= 50k || amount >= 100k
                        adv_font_small.draw_centered(
                            (item_definition.cost >= 0xC350 || item.quantity >= 0x186A0 ? "<col=ff0000>"
                                : "<trans=120>")
                                + item_definition.name
                                + (item.quantity > 1
                                ? "</col> (" + StringUtils.insertCommasToNumber(item.quantity + "")
                                + "</col>)"
                                : ""),
                            scene_draw_x, scene_draw_y - offset, 0xffffff, 1);
                        offset += 12;
                    }
                }
            }
        }
    }

    //This actually reloads the floors.
    public static void toggleSnow() {
        Archive configArchive = singleton.request_archive(2, "config", "config", 30);
        ObjectDefinition.init(configArchive);
        singleton.setLoading_phase(1);
        FloDefinition.init(configArchive);
    }

    public String date() {
        Date date = new Date();
        SimpleDateFormat sd = new SimpleDateFormat("h:mm:ss");
        return sd.format(date);
    }

    public static String capitalizeEachFirstLetter(String str) {

        String[] words = str.split(": ");

        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].substring(0, 1).toUpperCase() + words[i].substring(1).toLowerCase();
        }
        return String.join(": ", words);
    }

    public static String capitalizeFirstChar(final String character) {
        try {
            if (!character.equals("")) {
                return (character.substring(0, 1).toUpperCase() + character.substring(1).toLowerCase()).trim();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            addReportToServer(ex.getMessage());
        }
        return character;
    }

    float PercentCalc(long Number1, long number2) {
        float percentage;
        percentage = (Number1 * 100 / number2);
        return percentage;
    }

    public static String readableFileSize(long size) {
        if (size <= 0)
            return "0";
        final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static long findSize(String path) {
        long totalSize = 0;
        ArrayList<String> directory = new ArrayList<String>();
        File file = new File(path);

        if (file.isDirectory()) {
            directory.add(file.getAbsolutePath());
            while (directory.size() > 0) {
                String folderPath = directory.get(0);
                directory.remove(0);
                File folder = new File(folderPath);
                File[] filesInFolder = folder.listFiles();
                int noOfFiles = filesInFolder.length;

                for (int i = 0; i < noOfFiles; i++) {
                    File f = filesInFolder[i];
                    if (f.isDirectory()) {
                        directory.add(f.getAbsolutePath());
                    } else {
                        totalSize += f.length();
                    }
                }
            }
        } else {
            totalSize = file.length();
        }
        return totalSize;
    }

    public static boolean inCircle(int circleX, int circleY, int clickX, int clickY, int radius) {
        return java.lang.Math.pow((circleX + radius - clickX), 2) + java.lang.Math.pow((circleY + radius - clickY), 2) < java.lang.Math.pow(radius, 2);
    }

    static {
        SKILL_EXPERIENCE = new int[99];
        int i = 0;
        for (int j = 0; j < 99; j++) {
            int l = j + 1;
            int i1 = (int) ((double) l + 300D * Math.pow(2D, (double) l / 7D));
            i += i1;
            SKILL_EXPERIENCE[j] = i / 4;
        }
        BIT_MASKS = new int[32];
        i = 2;
        for (int k = 0; k < 32; k++) {
            BIT_MASKS[k] = i - 1;
            i += i;
        }
    }

    /**
     * Used for storing all the clicked texts set to true. So when a new clicked interface is changed to true, the ones stored here
     * can be reset.
     */
    public static ArrayList<String> textClickedList = new ArrayList<String>();

    public static void clearTextClicked() {
        for (int index = 0; index < textClickedList.size(); index++) {
            Widget.cache[Integer.parseInt(textClickedList.get(index))].textIsClicked = false;
        }
        textClickedList.clear();
    }

    /**
     * Have a specific interface text to have the clicked sprite.
     *
     * @param interfaceId
     * @param clicked
     */
    public static void setTextClicked(int interfaceId, boolean clicked) {
        //System.out.println("id: " + interfaceId + " text: " + Widget.cache[interfaceId].defaultText + " clicked: " + clicked);
        if (clicked && Widget.cache[interfaceId].defaultText.isEmpty()) {
            return;
        }
        for (int index = 0; index < textClickedList.size(); index++) {
            Widget.cache[Integer.parseInt(textClickedList.get(index))].textIsClicked = false;
        }
        textClickedList.clear();
        Widget.cache[interfaceId].textIsClicked = clicked;
        if (clicked) {
            textClickedList.add("" + interfaceId);
        }
    }

    public PrayerSystem.InterfaceData prayerGrabbed = null;

    public void releasePrayer() {
        if (!setting.moving_prayers) {
            return;
        }
        if (prayerGrabbed != null) {
            int yMod = 249;
            int posX = screen == ScreenMode.FIXED ? super.cursor_x - 550 : super.cursor_x - (window_width - 195);
            int posY = screen == ScreenMode.FIXED ? super.cursor_y - 205 : super.cursor_y - (window_height - yMod);
            if (screen != ScreenMode.FIXED) {
                yMod = 340;
                if (settings[ConfigUtility.SIDE_STONES_ARRANGEMENT_ID] == 0) {
                    posY = super.cursor_y - (window_width >= 1000 ? window_height - 303 : window_height - yMod);
                } else {
                    posX = super.cursor_x - (window_width - 215);
                    posY = super.cursor_y - (window_height - yMod);

                }
            }
            PrayerSystem.release(prayerGrabbed, posX, posY);
            prayerGrabbed = null;
        }
    }

    public static boolean isLoggedIn() {
        return loggedIn;
    }

    public static void setLoggedIn(boolean loggedIn) {
        Client.loggedIn = loggedIn;
    }

    private void startSpinner() {
        Widget w = Widget.cache[71101];
        Widget w2 = Widget.cache[71200];
        if (w.x >= -600) {
            w.x -= 25;
            w2.x -= 25;
        }
        if (w.x >= -1512 && w.x <= -601) {
            w.x -= (25 / spinSpeed);
            w2.x -= (25 / spinSpeed);
            spinSpeed = spinSpeed + 0.07f;
        }
        if (w.x >= -1600 && w.x < -1513) {
            w.x -= (25 / spinSpeed);
            w2.x -= (25 / spinSpeed);
            spinSpeed = spinSpeed + 2f;
        }
        if (w.x <= -1513) {
            startSpin = false;
        }
    }

    private boolean broadcastActive() {
        return broadcastText != null && !broadcastText.isEmpty() || (broadcast != null && isDisplayed);
    }
}
