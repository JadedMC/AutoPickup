package net.jadedmc.autopickup.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A collection of chat-related utilities to make working with colored and formatted strings easier.
 */
public class ChatUtils {

    /**
     * A quicker way to send colored messages to a command sender.
     * @param sender Command Sender
     * @param message Message to be sent.
     */
    public static void chat(CommandSender sender, String message) {
        sender.sendMessage(translate(message));
    }

    /**
     * Translate a String to a colorful String.
     * @param message Message to translate.
     * @return Translated Message.
     */
    public static String translate(String message) {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        int subVersion = Integer.parseInt(version.replace("1_", "").replaceAll("_R\\d", "").replace("v", ""));

        // If the server is 1.16 or later, allows hex color codes.
        if(subVersion >= 16) {
            Pattern pattern = Pattern.compile("&#[a-fA-F0-9]{6}");
            Matcher matcher = pattern.matcher(message);

            while (matcher.find()) {
                String color = message.substring(matcher.start() + 1, matcher.end());
                message = message.replace("&" + color, ChatColor.of(color) + "");
                matcher = pattern.matcher(message);
            }
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}