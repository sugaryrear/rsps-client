package com.ferox.model.content.account;

import com.ferox.Client;
import com.ferox.cache.graphics.SimpleImage;
import com.ferox.io.Buffer;
import com.ferox.sign.SignLink;
import com.ferox.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * AccountManager
 * Handles most actions for the account saving and loading.
 *
 * @author Lennard
 */
public class AccountManager {

    /**
     * The maximum amount of Accounts that can be saved.
     */
    private static final int MAXIMUM_ACCOUNTS = 5;

    /**
     * The {@link Path} to the Accounts save file.
     */
    private static final Path ACCOUNTS_FILE = Paths.get(SignLink.findDataDir(), "accounts.dat");

    /**
     * A {@link Collection} of holding all saved {@link Account}s.
     */
    private final Collection<Account> accountList = new ArrayList<Account>(MAXIMUM_ACCOUNTS);

    /**
     * The back ground sprite of the account box.
     */
    private final SimpleImage backgroundSprite;

    /**
     * The {@link Client} instance.
     */
    private final Client client;

    public AccountManager(final Client client, final SimpleImage backgroundSprite) {
        this.client = client;
        this.backgroundSprite = backgroundSprite;
    }

    /**
     * Adds an account with the given username and password.
     *
     * @param userName
     *            The username of this account.
     * @param userPassword
     *            The password of this account.
     * @throws IOException
     */
    public void addAccount(String userName, String userPassword) throws IOException {
        if (accountList.size() == MAXIMUM_ACCOUNTS) {
            client.firstLoginMessage = "Account list full!";
            client.secondLoginMessage = "Delete an account to create space for a new one.";
            return;
        }
        if (userName.length() == 0 || userPassword.length() == 0) {
            client.firstLoginMessage = "Username & Password";
            client.secondLoginMessage = "Must be more than 1 character";
            return;
        }
        Account accountToAdd = new Account(userName, userPassword);
        for (Account account : accountList) {
            if (accountToAdd.getUserName().equals(account.getUserName())) {
                client.firstLoginMessage = "There is already an account";
                client.secondLoginMessage = "saved with that username.";
                return;
            }
        }
        accountList.add(accountToAdd);
        saveAccounts();
    }

    /**
     * Draws the accounts on the login screen.
     */
    public void processAccountDrawing() {
        int xPosition = Client.window_width / 2;
        final int yPosition = Client.window_height / 2 + 145;
        xPosition -= -3 + (accountList.size() * (backgroundSprite.width / 2 + (accountList.size() > 1 ? 10 : 0)));
        for (Account account : accountList) {
            if (accountList.size() > 1) {
                xPosition += 10;
            }
            account.setXPosition(xPosition);
            account.setYPosition(yPosition);
            draw(account);
            xPosition += backgroundSprite.width + 10;
        }
    }

    /**
     * Handles the mouse click actions performed on the account buttons.
     *
     * @throws IOException
     */
    public void processAccountInput() throws IOException {
        ArrayList<Account> accountsToRemove = new ArrayList<Account>();
        for (Account account : accountList) {
            if (client.click_type == 1) {
                if (client.cursor_x >= account.getXPosition() + 14
                    && client.cursor_x <= account.getXPosition() + backgroundSprite.width - 14
                    && client.cursor_y >= account.getYPosition() + 23
                    && client.cursor_y <= account.getYPosition() + 40) {
                    accountsToRemove.add(account);
                    client.firstLoginMessage = "Deleted Account:";
                    client.secondLoginMessage = account.getUserName();
                } else if (client.cursor_x >= account.getXPosition()
                    && client.cursor_x <= account.getXPosition() + backgroundSprite.width
                    && client.cursor_y >= account.getYPosition()
                    && client.cursor_y <= account.getYPosition() + 23) {
                    client.myUsername = account.getUserName();
                    client.myPassword = account.getUserPassword();
                    if(client.loginTimer.finished()) {
                        client.login(client.myUsername, client.myPassword, false);
                        client.loginTimer.start(2);
                    }
                }
            }
        }
        if (!accountsToRemove.isEmpty()) {
            accountList.removeAll(accountsToRemove);
            saveAccounts();
        }
    }

    /**
     * Draws the accounts background sprite, username and delete text.
     *
     * @param account
     */
    private void draw(Account account) {
        backgroundSprite.drawSprite(account.getXPosition(), account.getYPosition());
        if (client.cursor_x >= account.getXPosition() + 14
            && client.cursor_x <= account.getXPosition() + backgroundSprite.width - 14
            && client.cursor_y >= account.getYPosition() + 23
            && client.cursor_y <= account.getYPosition() + 40) {
            SimpleImage backgroundSpriteHover = Client.spriteCache.get(1849);
            backgroundSpriteHover.drawSprite(account.getXPosition(), account.getYPosition());
        } else if (client.cursor_x >= account.getXPosition()
            && client.cursor_x <= account.getXPosition() + backgroundSprite.width
            && client.cursor_y >= account.getYPosition()
            && client.cursor_y <= account.getYPosition() + 23) {
            SimpleImage backgroundSpriteHover = Client.spriteCache.get(1848);
            backgroundSpriteHover.drawSprite(account.getXPosition(), account.getYPosition());
        }
        Client.adv_font_regular.draw_centered(account.getUserName(),
            account.getXPosition() + backgroundSprite.width / 2, account.getYPosition() + 16, 0xFFFFFF, 0);
    }

    /**
     * Saves the accounts to a new file, located at the {@code ACCOUNTS_FILE}
     * path.
     *
     * @throws IOException
     */
    public void saveAccounts() throws IOException {
        if (accountList.isEmpty()) {
            Files.deleteIfExists(ACCOUNTS_FILE);
            return;
        }
        Files.deleteIfExists(ACCOUNTS_FILE);
        Files.createFile(ACCOUNTS_FILE);
        Buffer buffer = Buffer.create(30000,null);
        buffer.writeByte(accountList.size());
        for (Account account : accountList) {
            buffer.writeString(account.getUserName());
            buffer.writeString(account.getUserPassword());
        }
        FileUtils.writeFile(ACCOUNTS_FILE.toString(), Arrays.copyOf(buffer.payload, buffer.pos));
    }

    /**
     * Loads the accounts from the save file.
     *
     * @throws IOException
     */
    public void loadAccounts() throws IOException {
        File file = ACCOUNTS_FILE.toFile();
        if (!file.exists()) {
            return;
        }
        byte[] fileData = FileUtils.read(file);
        Buffer buffer = new Buffer(fileData);
        try {
            int size = buffer.readUnsignedByte();
            String userName;
            String userPassword;
            for (int index = 0; index < size; index++) {
                userName = buffer.readString();
                userPassword = buffer.readString();
                accountList.add(new Account(userName, userPassword));
            }
        } catch (Exception e) {
            file.delete();
            e.printStackTrace();
        }
    }

}
