package dev.buha007.spigot.simplelootchests;

import java.util.UUID;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class ChestListener implements Listener {

    public ChestListener() {
    }

    @EventHandler
    public void onChestInteract(PlayerInteractEvent e) {
        Block block;
        try {
            block = e.getClickedBlock();
            String blockType = block.getType().toString();
            if (!blockType.contains("CHEST"))
                return;
        } catch (NullPointerException npe) {
            return;
        }

        String location = LootCore.getLocationFormat(block);

        if (!LootCore.isLootChestAtLocation(location))
            return;
        else
            e.setCancelled(true);

        Player player = e.getPlayer();

        if (player.hasPermission("slc.admin")) {
            if (LootCore.playerEditChests(e))
                return;
        }

        UUID playerUUID = player.getUniqueId();
        String chestType = LootCore.getChestTypeAtLocation(location);

        if (!LootCore.isPlayerInMap(playerUUID)) {
            LootCore.putPlayerInMap(playerUUID, location, System.currentTimeMillis());
            LootCore.openChestInventory(player, chestType);
        } else {
            Long cooldown = LootCore.timeBeforeOpenChest(playerUUID, location, chestType, System.currentTimeMillis());
            if (cooldown == 0) {
                LootCore.openChestInventory(player, chestType);
            } else {
                String msgCooldown = Msg.COOLDOWN.replace("{time}", "" + cooldown);
                player.sendMessage(msgCooldown);
            }
        }

    }

}