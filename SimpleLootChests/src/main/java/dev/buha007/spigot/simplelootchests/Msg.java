package dev.buha007.spigot.simplelootchests;

import org.bukkit.configuration.file.FileConfiguration;

import net.md_5.bungee.api.ChatColor;

public class Msg {

    private static SimpleLootChests main;

    public static String PREFIX, COOLDOWN, NO_PERMISSION, NOT_REGISTERED_CHEST_TYPE, WAND_RECEIVED;

    private Msg() {
    }

    public static void init(SimpleLootChests instance) {
        main = instance;
        loadMessages();
    }

    public static void loadMessages() {
        FileConfiguration cfg = main.getMessages();
        PREFIX = ChatColor.translateAlternateColorCodes('&', cfg.getString("prefix"));
        COOLDOWN = ChatColor.translateAlternateColorCodes('&', cfg.getString("cooldown"));
        NO_PERMISSION = ChatColor.translateAlternateColorCodes('&', cfg.getString("noPermission"));
        NOT_REGISTERED_CHEST_TYPE = ChatColor.translateAlternateColorCodes('&', cfg.getString("notRegisteredChest"));
        WAND_RECEIVED = ChatColor.translateAlternateColorCodes('&', cfg.getString("wandReceived"));
    }

}