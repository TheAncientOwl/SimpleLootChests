package dev.buha007.spigot.simplelootchests;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class SimpleLootChests extends JavaPlugin {

    private ConfigAccessor configuration;
    private ConfigAccessor messages;

    private ConsoleCommandSender console;

    @Override
    public void onEnable() {
        configuration = new ConfigAccessor(this, "config.yml");
        messages = new ConfigAccessor(this, "messages.yml");

        console = getServer().getConsoleSender();

        LootCore.init(this);
        Msg.init(this);
    }

    // <console messager>
    public void consolePrint(String message) {
        console.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
    // <console messager>

    // <configuration>
    @Override
    public FileConfiguration getConfig() {
        return configuration.getConfig();
    }

    @Override
    public void reloadConfig() {
        configuration.reloadConfig();
        LootCore.loadConfiguration();
    }
    // </configuration>

    // <messages>
    public FileConfiguration getMessages() {
        return messages.getConfig();
    }

    public void reloadMessages() {
        messages.reloadConfig();
        Msg.loadMessages();
    }
    // </messages>

    @Override
    public void onDisable() {

    }

}