package com.ferox;

import java.util.List;

import com.ferox.util.ChatCrown;

/**
 * Represents a chat message shown in the chat box.
 *
 * @author Lennard
 */
public class ChatMessage {

    private String message;
    private final String name;
    private final String title;
    private final int type;
    private final int rights;
    private final List<ChatCrown> crowns;
    
    public ChatMessage(String message, String name, String title, int type, int rights, List<ChatCrown> crowns) {
        this.message = message;
        this.name = name;
        this.title = title;
        this.type = type;
        this.rights = rights;
        this.crowns = crowns;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public int getType() {
        return type;
    }

    public int getRights() {
        return rights;
    }

    public List<ChatCrown> getCrowns() {
        return crowns;
    }
}
