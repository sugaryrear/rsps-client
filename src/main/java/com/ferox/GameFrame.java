package com.ferox;

import com.ferox.GameFrame.MotionPanel;
import com.ferox.util.ResourceLoader;
import net.miginfocom.swing.MigLayout;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public final class gameFrame extends Frame {

    private final GameApplet applet;
    public Toolkit toolkit = Toolkit.getDefaultToolkit();
    public Dimension screenSize = toolkit.getScreenSize();
    public int screenWidth = (int) screenSize.getWidth();
    public int screenHeight = (int) screenSize.getHeight();
    private static final long serialVersionUID = 1L;
    public static JPanel menuPanel;
    private static JFrame frame;
    public static JLabel text;

    public gameFrame(GameApplet applet, int width, int height, boolean resizable, boolean fullscreen) {
        this.applet = applet;
        if (ClientConstants.production || !ClientConstants.DISPLAY_CLIENT_VERSION_IN_TITLE) {
            this.setTitle(ClientConstants.CLIENT_NAME);
        } else {
            this.setTitle(ClientConstants.CLIENT_NAME + " - Version " + ClientConstants.CLIENT_VERSION.charAt(0));
        }
        this.setLayout(new BorderLayout());
        this.setResizable(resizable);

        this.setUndecorated(true);
        this.setEnabled(true);
        this.setFocusTraversalKeysEnabled(false);
        this.setBackground(Color.BLACK);

      //  menuPanel = new MotionPanel(frame);
        MigLayout layout = new MigLayout("insets 0 0 0 0", "[:20:20][:90:90]550[:20:20][:20:20][:20:20][:20:20]");
        //  MigLayout layout = new MigLayout("insets 0 0 0 0, fill, debug", "[right,:60:60][][left][left,:20:20]");

        menuPanel.setLayout(layout);
        menuPanel.setPreferredSize(new Dimension(765, 26));//can change this dynamically

        this.add(menuPanel);

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
        this.add(menuPanel, BorderLayout.NORTH);
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
