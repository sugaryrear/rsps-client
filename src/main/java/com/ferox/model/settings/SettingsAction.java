package com.ferox.model.settings;

public interface SettingsAction<T> {

    /**
     * The name of the setting.
     *
     * @param client The client.
     * @return The setting name.
     */
    String name(final T client);

    boolean status();

    /**
     * Handles toggling the setting.
     *
     * @param client The client.
     */
    void handle(final T client);
}
