package com.ferox;

import com.ferox.util.Assets;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;

public final class GameFrame extends Frame {

    private final GameApplet applet;
    public Toolkit toolkit = Toolkit.getDefaultToolkit();
    public Dimension screenSize = toolkit.getScreenSize();
    public int screenWidth = (int) screenSize.getWidth();
    public int screenHeight = (int) screenSize.getHeight();
    private static final long serialVersionUID = 1L;

    public GameFrame(GameApplet applet, int width, int height, boolean resizable, boolean fullscreen) {
        this.applet = applet;
        if (ClientConstants.production || !ClientConstants.DISPLAY_CLIENT_VERSION_IN_TITLE) {
            this.setTitle(ClientConstants.CLIENT_NAME);
        } else {
            this.setTitle(ClientConstants.CLIENT_NAME + " - Version " + ClientConstants.CLIENT_VERSION.charAt(0));
        }
        this.setResizable(resizable);
        this.setUndecorated(fullscreen);
        this.setFocusTraversalKeysEnabled(false);
        this.setBackground(Color.BLACK);

        // Load and set the taskbar icon
        URL url = Assets.getResource("assets", "icon.png");
        if (url != null) {
            try {
                setIconImage(ImageIO.read(url));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Unable to find resource: [icon.png]");
        }

        this.setVisible(true);
        Insets insets = getInsets();

        if (resizable) {
            int padding = 6; //Here we use 6px of padding to ensure the set resize height of 760x553 is maintained.
            setMinimumSize(new Dimension(760 + padding + insets.left + insets.right, 553 + padding + insets.top + insets.bottom));
        }
        this.setSize(width + insets.left + insets.right, height + insets.top + insets.bottom);
        //This line breaks jFrame on Linux when loading sometimes, let's setWindowPosition instead.
        //this.setLocationRelativeTo(null);
        //This prevents the fixed/resize mode switching from moving the client ot the top left
        //TODO: Test on windows and mac
        setWindowPosition();
        //Cheaphax fix for Linux, if the Frame doesn't have the correct width, it will be set to the correct width.
        while (this.getSize().getWidth() < width) {
            //TODO: test if while vs if is correct for setting size and position, while might be an infinite loop.
            this.setSize(width + insets.left + insets.right, height + insets.top + insets.bottom);
            setWindowPosition();
        }
        this.requestFocus();
        this.addWindowFocusListener(new WindowAdapter() {
            public void windowLostFocus(WindowEvent e) {
                Client.isShiftPressed = false;
                Client.isCtrlPressed = false;
            }
        });
        this.toFront();
    }

    private void setWindowPosition() {
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

        int topLeftX = device.getDefaultConfiguration().getBounds().x;
        int topLeftY = device.getDefaultConfiguration().getBounds().y;

        int screenX = device.getDefaultConfiguration().getBounds().width;
        int screenY = device.getDefaultConfiguration().getBounds().height;


        int windowPosX = ((screenX - getWidth()) / 2) + topLeftX;
        int windowPosY = ((screenY - getHeight()) / 2) + topLeftY;

        setLocation(windowPosX, windowPosY);
    }

    public void setIcon(Image image) {
        if (image != null) {
            this.setIconImage(image);
        }
    }

    public Graphics getGraphics() {
        final Graphics graphics = super.getGraphics();
        Insets insets = this.getInsets();
        graphics.fillRect(0, 0, getWidth(), getHeight());
        graphics.translate(insets != null ? insets.left : 0, insets != null ? insets.top : 0);
        return graphics;
    }

    public int getFrameWidth() {
        Insets insets = this.getInsets();
        return getWidth() - (insets.left + insets.right);
    }

    public int getFrameHeight() {
        Insets insets = this.getInsets();
        return getHeight() - (insets.top + insets.bottom);
    }

    public void update(Graphics graphics) {
        applet.update(graphics);
    }

    public void paint(Graphics graphics) {
        applet.paint(graphics);
    }
}
