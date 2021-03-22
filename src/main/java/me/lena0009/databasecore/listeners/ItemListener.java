package me.lena0009.databasecore.listeners;

import me.lena0009.databasecore.DatabaseCore;
import me.lena0009.databasecore.player.User;
import me.lena0009.databasecore.player.items.Banknote;
import me.lena0009.databasecore.utils.DatabaseUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class ItemListener implements Listener {

    DatabaseCore databaseCore = DatabaseCore.getDatabaseCore();

    @EventHandler
    public void onPlayerInteraction(PlayerInteractEvent event) {
        Action action = event.getAction();
        if ((action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) && event.hasItem()) {
            if (event.getItem().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(databaseCore, "itemtype"), PersistentDataType.STRING)) {
                String itemType = event.getItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(databaseCore, "itemtype"), PersistentDataType.STRING);
                switch (itemType) {
                    case "banknote":
                        String banknoteID = event.getItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(databaseCore, "id"), PersistentDataType.STRING);
                        Banknote banknote = databaseCore.getMoneyLogs().getBanknote(UUID.fromString(banknoteID));
                        User user = databaseCore.getUserManager().getUser(event.getPlayer().getUniqueId());
                        if (banknote.getAmountOfBanknotes()-1 < 0) {
                            // Say player is a cheater here
                            if (!user.isSuspectedCheater())
                                user.setServerSetting("suspectedCheater", true);
                        }

                        user.addBalance(banknote.getMoney());
                        event.getItem().setAmount(event.getItem().getAmount() - 1);
                        event.getPlayer().sendMessage("Added " + DatabaseUtils.formatDouble(banknote.getMoney()) + " to your account");
                        databaseCore.getMoneyLogs().redeemBanknote(event.getPlayer().getUniqueId(), banknote);
                }
            }
        }
    }
}
