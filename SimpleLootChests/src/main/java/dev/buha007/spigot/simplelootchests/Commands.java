package dev.buha007.spigot.simplelootchests;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class Commands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().toString().equalsIgnoreCase("slc"))
            return true;

        if (!sender.hasPermission("slc.admin") || !(sender instanceof Player)) {
            sender.sendMessage(Msg.PREFIX + Msg.NO_PERMISSION);
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            this.sendHelp(sender);
            return true;
        }

        if (args[0].equals("edit") && args.length == 3) {
            LootCore.giveWand((Player) sender, args[1], args[2]);
            return true;
        }

        this.sendHelp(sender);
        return true;
    }

    private final void sendHelp(CommandSender sender) {
        sender.sendMessage(
                ChatColor.translateAlternateColorCodes('&', "\n&8&l<< &2Simple&6Loot&eChests &aHelp &8&l>>"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "\n&7"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8/&2slc &7help"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8/&2slc &7reload"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8/&2slc &7chestTypes"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8/&2slc &7checkChests"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8/&2slc &7listLocations"));
        sender.sendMessage(
                ChatColor.translateAlternateColorCodes('&', "&8/&2slc &7edit &8[&7chestType&8] [&7add&8/&7remove&8]"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "\n&7"));
        sender.sendMessage(
                ChatColor.translateAlternateColorCodes('&', "&8&l&o>> &7&oSimpleLootChests developed by Bufnita&8."));
    }

}