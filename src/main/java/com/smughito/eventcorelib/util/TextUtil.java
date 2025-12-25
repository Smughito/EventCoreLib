package com.smughito.eventcorelib.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class TextUtil {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    public static Component parse(String text) {
        if (text == null || text.isEmpty()) {
            return Component.empty();
        }
        return MINI_MESSAGE.deserialize(text);
    }

    public static String serialize(Component component) {
        if (component == null) {
            return "";
        }
        return MINI_MESSAGE.serialize(component);
    }

    public static Component colored(String text) {
        return parse(text);
    }
}
