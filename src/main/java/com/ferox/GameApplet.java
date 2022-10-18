package com.ferox;

import com.ferox.Client.ScreenMode;
import com.ferox.cache.graphics.widget.Slider;
import com.ferox.cache.graphics.widget.Widget;
import com.ferox.cache.graphics.widget.impl.OptionTabWidget;
import com.ferox.draw.ProducingGraphicsBuffer;
import com.ferox.model.content.Keybinding;
import com.ferox.util.ConfigUtility;

import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

public class GameApplet extends Canvas implements Runnable, MouseListener, MouseMotionListener, MouseWheelListener,
    KeyListener, FocusListener, WindowListener {

    /**
     * Warning: Do not send packets (like calling client.teleport()) from anywhere in here except for the main do while loop inside the run method. The reason for this is Swing creates its own threads to run code and you do NOT want to send packets from those threads but rather you should send packets from the GameApplet thread or the main client thread instead to prevent race conditions, especially with ISAAC encryption enabled for packets.
     */
    private static final long serialVersionUID = 1L;

    private int anInt4;
    private int delay;
    int min_delay;
    private final long[] aLongArray7 = new long[10];
    int fps;
    public static boolean dump_requested;
    protected int myWidth;
    protected int myHeight;
    Graphics graphics;
    ProducingGraphicsBuffer fullGameScreen;
    GameFrame gameFrame;
    private boolean clear_screen;
    boolean awt_focus;
    int idle;
    int mouse_button;
    public int cursor_x;
    public int cursor_y;
    int mouse_button_event;
    private int event_click_x;
    private int event_click_y;
    public long event_click_time;
    public int click_type;
    public int clickMode4;
    public int click_x;
    public int click_y;
    final int[] key_status = new int[128];
    private final int[] charQueue = new int[128];
    public int[] keysHeld = new int[128];
    public boolean isLoading;
    private int readIndex;
    private int writeIndex;
    public static int anInt34;
    public boolean isApplet;
    protected int rotationGliding;
    public boolean resized;
    int forceWidth = -1;
    int forceHeight = -1;
    private boolean shiftTeleport = false;
    private int mouseRotation;
    public void rebuildFrame(int width, int height, boolean resizable, boolean full) {
        Component component = getGameComponent();
        component.setBackground(Color.black);
        component.removeMouseWheelListener(this);
        component.removeMouseListener(this);
        component.removeMouseMotionListener(this);
        component.removeKeyListener(this);
        component.removeFocusListener(this);

        boolean createdByApplet = (isApplet && !full);
        myWidth = width;
        myHeight = height;

        if (gameFrame != null) {
            gameFrame.removeWindowListener(this);
            gameFrame.setVisible(false);
            gameFrame.dispose();
            gameFrame = null;
        }

        if (!createdByApplet) {
            gameFrame = new GameFrame(this, width, height, resizable, full);
            gameFrame.addWindowListener(this);
        }

        graphics = (createdByApplet ? this : gameFrame).getGraphics();
        if (!createdByApplet) {
            getGameComponent().addMouseWheelListener(this);
            getGameComponent().addMouseListener(this);
            getGameComponent().addMouseMotionListener(this);
            getGameComponent().addKeyListener(this);
            getGameComponent().addFocusListener(this);
        }
        cursor_x = cursor_y = -1;
    }

    int getScreenWidth() {
        if (forceWidth >= 0) {
            return forceWidth;
        }
        return getRealScreenWidth();
    }

    int getScreenHeight() {
        if (forceHeight >= 0) {
            return forceHeight;
        }

        return getRealScreenHeight();
    }

    private int getRealScreenWidth() {
        Component component = getGameComponent();
        if (component == null) {
            return forceWidth >= 0 ? forceWidth : 765;
        }

        int w = component.getWidth();
        if (component instanceof java.awt.Container) {
            java.awt.Insets insets = ((java.awt.Container) component).getInsets();
            w -= insets.left + insets.right;
        }
        return w;
    }

    private int getRealScreenHeight() {
        Component component = getGameComponent();
        if (component == null) {
            return forceHeight >= 0 ? forceHeight : 503;
        }

        int h = component.getHeight();
        if (component instanceof java.awt.Container) {
            java.awt.Insets insets = ((java.awt.Container) component).getInsets();
            h -= insets.top + insets.bottom;
        }
        return h;
    }

    public boolean appletClient() {
        return gameFrame == null && isApplet == true;
    }

    final void createClientFrame(int w, int h) {
        isApplet = false;
        myWidth = forceWidth = w;
        myHeight = forceHeight = h;
        gameFrame = new GameFrame(this, myWidth, myHeight, Client.screen == Client.ScreenMode.RESIZABLE, Client.screen == Client.ScreenMode.FULLSCREEN);
        gameFrame.setFocusTraversalKeysEnabled(false);
        graphics = getGameComponent().getGraphics();
        fullGameScreen = new ProducingGraphicsBuffer(myWidth, myHeight);
        startRunnable(this, 1);
    }

    final void initClientFrame(int w, int h) {
        isApplet = true;
        myWidth = forceWidth = w;
        myHeight = forceHeight = h;
        graphics = getGameComponent().getGraphics();
        fullGameScreen = new ProducingGraphicsBuffer(myWidth, myHeight);
        startRunnable(this, 1);
    }

    static long lastloop;
     /**
     * A queue of synchronization tasks.
     */
    private static final java.util.Queue<Runnable> syncTasks = new ConcurrentLinkedQueue<>();
    public static void addSyncTask(Runnable runnable) {
        syncTasks.add(runnable);
    }
     /**
     * Run all pending tasks from other threads.
     */
    private void runPendingTasks() {
        for (; ; ) {
            Runnable pending = syncTasks.poll();
            if (pending == null) {
                break;
            }
            try {
                pending.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void run() {
        getGameComponent().addMouseListener(this);
        getGameComponent().addMouseMotionListener(this);
        getGameComponent().addKeyListener(this);
        getGameComponent().addFocusListener(this);
        getGameComponent().addMouseWheelListener(this);
        if (gameFrame != null) {
            gameFrame.addWindowListener(this);
        }
        draw_loadup(0, "Loading...");
        startUp();
        int i = 0;
        int j = 256;
        int k = 1;
        int l = 0;
        int i1 = 0;
        for (int j1 = 0; j1 < 10; j1++) {
            aLongArray7[j1] = System.currentTimeMillis();
        }
        do {
            if (anInt4 < 0) {
                break;
            }
            if (anInt4 > 0) {
                anInt4--;
                if (anInt4 == 0) {
                    exit();
                    return;
                }
            }
            int k1 = j;
            int i2 = k;
            j = 300;
            k = 1;
            long l2 = System.currentTimeMillis();
            if (aLongArray7[i] == 0L) {
                j = k1;
                k = i2;
            } else if (l2 > aLongArray7[i]) {
                j = (int) ((long) (2560 * delay) / (l2 - aLongArray7[i]));
            }
            if (j < 25) {
                j = 25;
            }
            if (j > 256) {
                j = 256;
                k = (int) ((long) delay - (l2 - aLongArray7[i]) / 10L);
            }
            if (k > delay) {
                k = delay;
            }
            aLongArray7[i] = l2;
            i = (i + 1) % 10;
            if (k > 1) {
                for (int j2 = 0; j2 < 10; j2++) {
                    if (aLongArray7[j2] != 0L) {
                        aLongArray7[j2] += k;
                    }
                }

            }
            if (k < min_delay) {
                k = min_delay;
            }
            String sleepy = "";
            try {
                    sleepy = "sleepy "+k;
                Thread.sleep(k);
            } catch (InterruptedException interruptedexception) {
                i1++;
            }
            for (; l < 256; l += j) {
                click_type = mouse_button_event;
                clickMode4 = 1;
                click_x = event_click_x;
                click_y = event_click_y;
                mouse_button_event = 0;
                processGameLoop();
                sleepy = sleepy + " last was "+(System.currentTimeMillis()-lastloop)+" ms ago";
                lastloop = System.currentTimeMillis();
                readIndex = writeIndex;
            }
            if (Client.debug_packet_info) {
                System.out.println(sleepy);
            }
            l &= 0xff;
            if (delay > 0) {
                fps = (1000 * j) / (delay * 256);
            }
            processDrawing();
            if (dump_requested) {
                System.out.println((new StringBuilder()).append("ntime:")
                        .append(l2).toString());
                for (int k2 = 0; k2 < 10; k2++) {
                    int i3 = ((i - k2 - 1) + 20) % 10;
                    System.out.println((new StringBuilder()).append("otim")
                            .append(i3).append(":").append(aLongArray7[i3])
                            .toString());
                }

                System.out.println((new StringBuilder()).append("fps:")
                        .append(fps).append(" ratio:").append(j)
                        .append(" count:").append(l).toString());
                System.out.println((new StringBuilder()).append("del:")
                        .append(k).append(" deltime:").append(delay)
                        .append(" mindel:").append(min_delay).toString());
                System.out.println((new StringBuilder()).append("intex:")
                        .append(i1).append(" opos:").append(i).toString());
                //shouldDebug = false;
                i1 = 0;
            }
            if (shiftTeleport) {
                int newHeight = Client.singleton.plane - mouseRotation;
                if (newHeight < 0) {
                    newHeight = 0;
                }
                if (newHeight > 3) {
                    newHeight = 3;
                }
                Client.teleport(Client.local_player.waypoint_x[0] + Client.singleton.next_region_start, Client.local_player.waypoint_y[0] + Client.singleton.next_region_end, newHeight);
                shiftTeleport = false;
            }
            runPendingTasks();
        } while (true);
        if (anInt4 == -1) {
            exit();
        }
    }

    private void exit() {
        anInt4 = -2;
        clear();
        if (gameFrame != null) {
            try {
                Thread.sleep(1000L);
            } catch (Exception exception) {
            }
            try {
                System.exit(0);
            } catch (Throwable throwable) {
            }
        }
    }

    final void method4(int i) {
        delay = 1000 / i;
    }

    public final void start() {
        if (anInt4 >= 0) {
            anInt4 = 0;
        }
    }

    public final void stop() {
        if (anInt4 >= 0) {
            anInt4 = 4000 / delay;
        }
    }

    public final void destroy() {
        anInt4 = -1;
        try {
            Thread.sleep(5000L);
        } catch (Exception exception) {
        }
        if (anInt4 == -1) {
            exit();
        }
    }

    public final void update(Graphics g) {
        if (graphics == null) {
            graphics = g;
        }
        clear_screen = true;
        raiseWelcomeScreen();
    }

    public final void paint(Graphics g) {
        if (graphics == null) {
            graphics = g;
        }
        clear_screen = true;
        raiseWelcomeScreen();
    }

    public int getChildWidth(Widget Interface, int Index) {
        return Widget.cache[Interface.children[Index]].width;
    }

    public int getChildHeight(Widget Interface, int Index) {
        return Widget.cache[Interface.children[Index]].height;
    }

    public void mouseWheelMoved(MouseWheelEvent event) {
        int rotation = event.getWheelRotation();
        handleInterfaceScrolling(event);
        if (cursor_x > 0 && cursor_x < 512 && cursor_y > Client.window_height - 165 && cursor_y < Client.window_height - 25) {
            int scrollPos = Client.chatScrollAmount;
            scrollPos -= rotation * 30;
            if (scrollPos < 0)
                scrollPos = 0;
            if (scrollPos > Client.chatScrollHeight - 110)
                scrollPos = Client.chatScrollHeight - 110;
            if (Client.chatScrollAmount != scrollPos) {
                Client.chatScrollAmount = scrollPos;
                Client.update_chat_producer = true;
            }
        } else if (Client.loggedIn) {
            // Admin shift scrollwheel height changing. Do not send packets from the client to the server here, send packets from the main do while loop inside run method instead.
            if ((Client.singleton.getMyPrivilege() >= 2 && Client.singleton.getMyPrivilege() <= 4) && Client.isShiftPressed) {
                this.mouseRotation = rotation;
                shiftTeleport = true;
            } else {
                shiftTeleport = false;
            }

            /** ZOOMING **/
            boolean zoom = Client.screen == ScreenMode.FIXED ? (cursor_x < 512) : (cursor_x < Client.window_width - 200);
            if (zoom && Client.widget_overlay_id == -1 && Client.singleton.settings[ConfigUtility.ZOOM_TOGGLE_ID] == 0) {
                Client.zoom_distance += rotation * 35;

                int max_zoom_1 = (Client.screen == ScreenMode.FIXED ? -150 : -300);
                if (Client.zoom_distance < max_zoom_1) {
                    Client.zoom_distance = max_zoom_1;
                }

                if (Client.zoom_distance > 1100) {
                    Client.zoom_distance = 1100;
                }
                if (Client.zoom_distance < 0) {
                    Client.zoom_distance = 0;
                }
                Widget.cache[OptionTabWidget.ZOOM_SLIDER].slider.setValue(Client.zoom_distance);
                Client.singleton.setting.save();
            }
            Client.update_chat_producer = true;
        }
    }

    public void handleInterfaceScrolling(MouseWheelEvent event) {
        int rotation = event.getWheelRotation();
        int tabInterfaceId = Client.tabInterfaceIDs[Client.singleton.sidebarId];
        if (tabInterfaceId != -1) {
            handleScrolling(rotation, tabInterfaceId, Client.singleton.screen == Client.ScreenMode.FIXED ? Client.singleton.getScreenWidth() - 218
                : (Client.singleton.screen == Client.ScreenMode.FIXED ? 28
                : Client.singleton.getScreenWidth() - 197), Client.singleton.screen == Client.ScreenMode.FIXED ? Client.singleton.getScreenHeight() - 298
                : (Client.singleton.screen == Client.ScreenMode.FIXED ? 37
                : Client.singleton.getScreenHeight()
                - (Client.singleton.getScreenWidth() >= 1000 ? 37 : 74) - 267));
        }
        if (Client.singleton.widget_overlay_id != -1) {
            handleScrolling(rotation, Client.singleton.widget_overlay_id, Client.singleton.screen == Client.ScreenMode.FIXED ? 4
                : (Client.singleton.getScreenWidth() / 2) - 356, Client.singleton.screen == Client.ScreenMode.FIXED ? 4
                : (Client.singleton.getScreenHeight() / 2) - 230);
        }
    }

    private void handleScrolling(int rotation, int interfaceId, int offsetX, int offsetY) {
        try {
            //No widget found
            if(Widget.cache[interfaceId] == null) {
                return;
            }
            Widget widget = Widget.cache[interfaceId];
            for (int index = 0; index < widget.children.length; index++) {
                Widget child = Widget.cache[widget.children[index]];
                if (child != null && child.scrollMax > child.height) {
                    int positionX = widget.child_x[index] + child.x;
                    int positionY = widget.child_y[index] + child.y;
                    int width = child.width;
                    int height = child.height;
                    if (cursor_x >= offsetX + positionX && cursor_y >= offsetY + positionY
                        && cursor_x < offsetX + positionX + width
                        && cursor_y < offsetY + positionY + height) {
                        int newRotation = rotation * 30;
                        if (newRotation > child.scrollMax - child.height - child.scrollPosition) {
                            newRotation = child.scrollMax - child.height - child.scrollPosition;
                        } else if (newRotation < -child.scrollPosition) {
                            newRotation = -child.scrollPosition;
                        }
                        if (Client.singleton.getActiveInterfaceType() != 0) {
                            Client.singleton.setMouseDragY(Client.singleton.getMouseDragY() - newRotation);
                        }
                        child.scrollPosition += newRotation;
                        return;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int clickType;
    public final int LEFT = 0;
    public final int RIGHT = 1;
    public final int DRAG = 2;
    public final int RELEASED = 3;
    public final int MOVE = 4;
    public int releasedX;
    public int releasedY;
    public boolean mouse_wheel_down;

    public final void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        int type = e.getButton();
        if (gameFrame != null) {
            Insets insets = gameFrame.getInsets();
            x -= insets.left;// 4
            y -= insets.top;// 22
        }
        idle = 0;
        event_click_x = x;
        event_click_y = y;
        event_click_time = System.currentTimeMillis();
        if (type == 2) {
            mouse_wheel_down = true;
            mouse_wheel_x = x;
            mouse_wheel_y = y;
            return;
        }
        //ken comment, java 11 fix
        //if (e.isMetaDown()) {
        if (e.getButton() == MouseEvent.BUTTON3) {
            clickType = RIGHT;
            mouse_button_event = 2;
            mouse_button = 2;
        } else {
            clickType = LEFT;
            mouse_button_event = 1;
            mouse_button = 1;
        }
    }

    public final void mouseReleased(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        if (gameFrame != null) {
            Insets insets = gameFrame.getInsets();
            x -= insets.left;// 4
            y -= insets.top;// 22
        }
        releasedX = x;
        releasedY = y;
        idle = 0;
        mouse_button = 0;
        clickType = RELEASED;
        mouse_wheel_down = false;
        if (Client.singleton.setting.moving_prayers)
            Client.singleton.releasePrayer();
    }

    public final void mouseClicked(MouseEvent mouseevent) {
    }

    public final void mouseEntered(MouseEvent mouseevent) {
    }

    public final void mouseExited(MouseEvent mouseevent) {
        idle = 0;
        cursor_x = -1;
        cursor_y = -1;
    }

    public int mouse_wheel_x;
    public int mouse_wheel_y;

    public final void mouseDragged(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        if (gameFrame != null) {
            Insets insets = gameFrame.getInsets();
            x -= insets.left;// 4
            y -= insets.top;// 22
        }
        if (mouse_wheel_down && Client.singleton.settings[ConfigUtility.MOUSE_CAMERA_ID] == 1) {
            y = mouse_wheel_x - e.getX();
            int k = mouse_wheel_y - e.getY();
            mouseWheelDragged(y, -k);
            mouse_wheel_x = e.getX();
            mouse_wheel_y = e.getY();
            return;
        }
        idle = 0;
        cursor_x = x;
        cursor_y = y;
        clickType = DRAG;
        Slider.handleSlider(x, y);
    }

    void mouseWheelDragged(int param1, int param2) {

    }

    public final void mouseMoved(MouseEvent mouseevent) {
        int x = mouseevent.getX();
        int y = mouseevent.getY();
        if (gameFrame != null) {
            Insets insets = gameFrame.getInsets();
            x -= insets.left;// 4
            y -= insets.top;// 22
        }
        idle = 0;
        cursor_x = x;
        cursor_y = y;
        clickType = MOVE;
    }

    public final void keyPressed(KeyEvent keyevent) {
        idle = 0;
        int keycode = keyevent.getKeyCode();
        int keychar = keyevent.getKeyChar();

        if (Keybinding.isBound(keycode)) {
            return;
        }

        if (keycode == KeyEvent.VK_ESCAPE && Client.singleton.settings[ConfigUtility.ESC_CLOSE_ID] == 1) {
            //Close any open interfaces.
            if (Client.loggedIn && Client.widget_overlay_id != -1) {
                if(Client.widget_overlay_id == 48700) {
                    Client.tabInterfaceIDs[3] = 3213;
                }
                if (Client.widget_overlay_id == 16200) {
                    //Queue the task to run on the main Client thread to prevent a race condition.
                    //We use addSyncTask to send the packet from the main Client thread to prevent racing.
                    addSyncTask(() -> {
                        Client.singleton.packetSender.sendButtonClick(16202);
                    });
                }
                Client.singleton.clearTopInterfaces();
                return;
            }

            //Close the Client settings menu if it is open.
            if (Client.loggedIn && Client.tabInterfaceIDs[Client.sidebarId] == 50290) {
                //Queue the task to run on the main Client thread to prevent a race condition.
                //We use addSyncTask to send the packet from the main Client thread to prevent racing.
                addSyncTask(() -> {
                    Client.singleton.packetSender.sendButtonClick(50293);
                });
                return;
            }
        }

        if (keycode == KeyEvent.VK_SHIFT) {
            Client.isShiftPressed = true;
        }
        if (keycode == KeyEvent.VK_CONTROL) {
            Client.isCtrlPressed = true;
        }
        if (keycode == KeyEvent.VK_B && Client.isCtrlPressed) {
            Client.singleton.packetSender.sendCommand("bank");
        }
        if (keychar < 30)
            keychar = 0;
        if (keycode == 37)
            keychar = 1;
        if (keycode == 39)
            keychar = 2;
        if (keycode == 38)
            keychar = 3;
        if (keycode == 40)
            keychar = 4;
        if (keycode == 17)
            keychar = 5;
        if (keycode == 8)
            keychar = 8;
        if (keycode == 127)
            keychar = 8;
        if (keycode == 9)
            keychar = 9;
        if (keycode == 10)
            keychar = 10;
        if (keycode >= 112 && keycode <= 123)
            keychar = (1008 + keycode) - 112;
        if (keycode == 36)
            keychar = 1000;
        if (keycode == 35)
            keychar = 1001;
        if (keycode == 33)
            keychar = 1002;
        if (keycode == 34)
            keychar = 1003;
        if (keychar > 0 && keychar < 128) {
            key_status[keychar] = 1;
        }
        if (keychar > 4) {
            charQueue[writeIndex] = keychar;
            writeIndex = writeIndex + 1 & 0x7f;
        }
    }

    public final void keyReleased(KeyEvent keyevent) {
        idle = 0;
        int keycode = keyevent.getKeyCode();
        char keychar = keyevent.getKeyChar();
        if (keycode == KeyEvent.VK_SHIFT) {
            Client.isShiftPressed = false;
        }

        if (keycode == KeyEvent.VK_CONTROL) {
            Client.isCtrlPressed = false;
            Client.singleton.prayerGrabbed = null;
        }

        if (keychar < '\036')
            keychar = '\0';
        if (keycode == 37)
            keychar = '\001';
        if (keycode == 39)
            keychar = '\002';
        if (keycode == 38)
            keychar = '\003';
        if (keycode == 40)
            keychar = '\004';
        if (keycode == 17)
            keychar = '\005';
        if (keycode == 8)
            keychar = '\b';
        if (keycode == 127)
            keychar = '\b';
        if (keycode == 9)
            keychar = '\t';
        if (keycode == 10)
            keychar = '\n';
        if (keychar > 0 && keychar < '\200')
            key_status[keychar] = 0;
    }

    public final void keyTyped(KeyEvent keyevent) {
    }

    final int readChar(int dummy) {
        while (dummy >= 0) {
            for (int j = 1; j > 0; j++)
                ;
        }
        int k = -1;
        if (writeIndex != readIndex) {
            k = charQueue[readIndex];
            readIndex = readIndex + 1 & 0x7f;
        }
        return k;
    }

    public final void focusGained(FocusEvent focusevent) {
        awt_focus = true;
        clear_screen = true;
        raiseWelcomeScreen();
    }

    public final void focusLost(FocusEvent focusevent) {
        awt_focus = false;
        for (int i = 0; i < 128; i++) {
            key_status[i] = 0;
        }

    }

    public final void windowActivated(WindowEvent windowevent) {
    }

    public final void windowClosed(WindowEvent windowevent) {
    }

    public final void windowClosing(WindowEvent windowevent) {
        destroy();

    }

    public final void windowDeactivated(WindowEvent windowevent) {
    }

    public final void windowDeiconified(WindowEvent windowevent) {
    }

    public final void windowIconified(WindowEvent windowevent) {
    }

    public final void windowOpened(WindowEvent windowevent) {
    }

    void startUp() {
    }

    void processGameLoop() {
    }

    void clear() {
    }

    void processDrawing() {
    }

    void raiseWelcomeScreen() {
    }

    Component getGameComponent() {
        if (gameFrame != null && !isApplet) {
            return gameFrame;
        } else {
            return this;
        }
    }
    private final AtomicLong counter = new AtomicLong();
    //Since we call this startRunnable more than once, we should count which thread it is.
    public void startRunnable(Runnable runnable, int i) {
        Thread thread = new Thread(runnable);
        String nameFormat = ""+ ClientConstants.CLIENT_NAME+"ClientThread";
        thread.setName(nameFormat + "-" + counter.incrementAndGet());
        thread.start();
        thread.setPriority(i);
        thread.setUncaughtExceptionHandler((t, e) -> {
            e.printStackTrace();
            Client.addReportToServer(e.getMessage());
        });
    }

    void draw_loadup(int percentage, String loadingText) {
        while (graphics == null) {
            graphics = (isApplet ? this : gameFrame).getGraphics();
            try {
                getGameComponent().repaint();
            } catch (Exception _ex) {
                _ex.printStackTrace();
            }
            try {
                Thread.sleep(1000L);
            } catch (Exception _ex) {
                _ex.printStackTrace();
            }
        }
        Font font = new Font("Helvetica", 1, 13);
        FontMetrics fontmetrics = getGameComponent().getFontMetrics(font);
        Font font1 = new Font("Helvetica", 0, 13);
        FontMetrics fontmetrics1 = getGameComponent().getFontMetrics(font1);
        if (clear_screen) {
            graphics.setColor(Color.black);
            graphics.fillRect(0, 0, Client.window_width, Client.window_height);
            clear_screen = false;
        }
        Color color = new Color(140, 17, 17);
        int y = Client.window_height / 2 - 18;
        graphics.setColor(color);
        graphics.drawRect(Client.window_width / 2 - 152, y, 304, 34);
        graphics.fillRect(Client.window_width / 2 - 150, y + 2, percentage * 3, 30);
        graphics.setColor(Color.black);
        graphics.fillRect((Client.window_width / 2 - 150) + percentage * 3, y + 2,
                300 - percentage * 3, 30);
        graphics.setFont(font);
        graphics.setColor(Color.white);
        graphics.drawString(loadingText,
                (Client.window_width - fontmetrics.stringWidth(loadingText)) / 2,
                y + 22);
        graphics.drawString("",
                (Client.window_width - fontmetrics1.stringWidth("")) / 2, y - 8);
    }

    GameApplet() {
        delay = 20;
        min_delay = 1;
        dump_requested = false;
        clear_screen = true;
        awt_focus = true;
    }

}
