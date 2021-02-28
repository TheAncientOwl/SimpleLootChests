package dev.buha007.spigot.simplelootchests;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;

import net.md_5.bungee.api.ChatColor;

public class LootCore {

    private static SimpleLootChests main;

    private static HashMap<String, Inventory> inventories; // >> type -> inventory
    private static HashMap<String, Long> cooldowns; // >> type -> cooldown
    private static HashMap<String, String> locations; // >> location -> type

    // >> uuid of player -> HashMap(location -> time when opened)
    private static HashMap<UUID, HashMap<String, Long>> players;

    private LootCore() {
    }

    public static void init(SimpleLootChests instance) {
        main = instance;
        inventories = new HashMap<String, Inventory>();
        cooldowns = new HashMap<String, Long>();
        locations = new HashMap<String, String>();
        players = new HashMap<UUID, HashMap<String, Long>>();
        PluginManager pm = main.getServer().getPluginManager();
        pm.registerEvents(new ChestListener(), main);
        loadConfiguration();
    }

    public static void loadConfiguration() {
        FileConfiguration cfg = main.getConfig();

        inventories.clear();
        cooldowns.clear();
        locations.clear();
        for (String type : cfg.getStringList("registered")) {

            String pathToType = "types." + type + ".";

            // <create inventory>
            int chestSize = cfg.getInt(pathToType + "size");
            if (chestSize % 9 != 0) {
                chestSize = 27;
                main.consolePrint("&8<&cWARN &8- &2SimpleLootChests&8>");
                main.consolePrint(
                        "&cWrong chest size in config at " + pathToType + "size -> Size must be a multiple of 9!");
                main.consolePrint("&cA default size of 27 will be used");
                main.consolePrint("&8<&cWARN &8- &2SimpleLootChests&8>");
            }

            Inventory inv = Bukkit.createInventory(null, chestSize,
                    ChatColor.translateAlternateColorCodes('&', cfg.getString(pathToType + "displayName")));
            int index = -1;
            String pathToItems = pathToType + "items";
            for (String key : cfg.getConfigurationSection(pathToItems).getKeys(false)) {
                Random random = new Random();
                String pathToItem = pathToItems + "." + key + ".";
                if (random.nextInt(100) + 2 > cfg.getInt(pathToItem + "chance"))
                    continue;
                ItemStack item;
                try {
                    item = new ItemStack(Material.getMaterial(cfg.getString(pathToItem + "item")),
                            cfg.getInt(pathToItem + "amount"));
                    ++index;
                    inv.setItem(index, item);
                } catch (IllegalArgumentException e) {
                    main.consolePrint("&8<&cERROR &8- &2SimpleLootChests&8>");
                    main.consolePrint("&cWrong item in config at " + pathToItem + "item");
                    main.consolePrint("&8<&ERROR &8- &2SimpleLootChests&8>");
                    return;
                }
            }
            // </create inventory>
            inventories.put(type, inv);

            cooldowns.put(type, cfg.getLong(pathToType + "cooldown") * 1000);

            for (String location : cfg.getStringList("locations." + type)) {
                locations.put(location, type);
            }
        }
    }

    public static boolean isRegisteredChestType(String type) {
        return (main.getConfig().getStringList("registered").contains(type));
    }

    public static boolean isLootChestAtLocation(String chestLocation) {
        return (locations.containsKey(chestLocation));
    }

    public static String getChestTypeAtLocation(String location) {
        return locations.get(location);
    }

    public static boolean isPlayerInMap(UUID playerUUID) {
        return players.containsKey(playerUUID);
    }

    /**
     * >> Creates a new entry in players map
     * 
     * @param playerUUID     -> uuid that will be put in the map
     * @param location       -> location of opened chest
     * @param timeWhenOpened -> system's time when the chest was opened
     * @return nothing
     */
    public static void putPlayerInMap(UUID playerUUID, String location, Long timeWhenOpened) {
        HashMap<String, Long> newEntry = new HashMap<String, Long>();
        newEntry.put(location, timeWhenOpened);
        players.put(playerUUID, newEntry);
    }

    /**
     * >> Get the cooldown before a player can reopen a chest;_____________________
     * >> If the cooldown is 0, update players map
     * 
     * @param playerUUID     -> uuid that will be checked and updated
     * @param location       -> location of opened chest
     * @param chestType      -> chest's type at given location
     * @param timeWhenOpened -> system's time when the chest was opened
     * @return the cooldown before reopen the chest
     */
    public static Long timeBeforeOpenChest(UUID playerUUID, String location, String chestType, Long timeWhenOpened) {

        HashMap<String, Long> map = players.get(playerUUID);

        if (!map.containsKey(location)) {
            map.put(location, timeWhenOpened);
            players.put(playerUUID, map);
            return 0L;
        }

        Long dif = timeWhenOpened - map.get(location);

        if (dif >= cooldowns.get(chestType)) {
            map.put(location, timeWhenOpened);
            players.put(playerUUID, map);
            return 0L;
        }

        return dif;
    }

    public static String getLocationFormat(Block block) {
        return ("" + block.getX() + " " + block.getY() + " " + block.getZ());
    }

    public static void openChestInventory(Player player, String chestType) {
        Inventory inv = inventories.get(chestType);
        player.openInventory(inv);
    }

    public static void giveWand(Player player, String chestTypeToEdit, String option) {
        if (!isRegisteredChestType(chestTypeToEdit)) {
            player.sendMessage(Msg.PREFIX + Msg.NOT_REGISTERED_CHEST_TYPE);
        }
        String wandName = "&8&l<< ";
        if (option.equals("add"))
            wandName += "&2&lSLC &8&l- &e" + chestTypeToEdit + " &8&l- &2add &8&l>>";
        else if (option.equals("remove"))
            wandName += "&4&lSLC &8&l- &e" + chestTypeToEdit + " &8&l- &4remove &8&l>>";
        else {
            player.sendMessage(Msg.PREFIX + Msg.NOT_REGISTERED_CHEST_TYPE);
            return;
        }
        wandName = ChatColor.translateAlternateColorCodes('&', wandName);

        ItemStack wand = new ItemStack(Material.BLAZE_ROD);
        ItemMeta wandMeta = wand.getItemMeta();
        wandMeta.setLore(Arrays.asList(ChatColor.translateAlternateColorCodes('&', "&8&l>> &2Simple&6Loot&eChests"),
                ChatColor.translateAlternateColorCodes('&', "&8&l>> &aclick a chest to add/remove from loot chests&8."),
                ChatColor.translateAlternateColorCodes('&', "&8&l>> &7developed by Bufnita")));
        wandMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        wandMeta.setDisplayName(wandName);
        wand.setItemMeta(wandMeta);

        player.getInventory().addItem(wand);
        player.sendMessage(Msg.PREFIX + Msg.WAND_RECEIVED);
    }

    public static boolean playerEditChests(PlayerInteractEvent e) {
        return false;
    }

}