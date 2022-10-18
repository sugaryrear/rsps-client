package com.ferox.model.content.account;

/**
 * Account
 * Represents a (saved) log-in account.
 *
 * @author Lennard
 */
public class Account {

    /**
     * The username the account.
     */
    private String userName;

    /**
     * The password of the account.
     */
    private String userPassword;

    /**
     * The X drawing position of this account box.
     */
    private int xPosition;

    /**
     * The Y drawing position of this account box.
     */
    private int yPosition;

    /**
     * Creates a new Account.
     * @param userName The username of the account.
     * @param userPassword The password of the account.
     */
    public Account(String userName, String userPassword) {
        this.userName = userName;
        this.userPassword = userPassword;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public int getXPosition() {
        return xPosition;
    }

    public void setXPosition(int xPosition) {
        this.xPosition = xPosition;
    }

    public int getYPosition() {
        return yPosition;
    }

    public void setYPosition(int yPosition) {
        this.yPosition = yPosition;
    }

    @Override
    public String toString() {
        return "Account[userName: " + userName + ", userPassword: " + userPassword + "]";
    }

}
