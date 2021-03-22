package me.lena0009.databasecore.listeners;

import me.lena0009.databasecore.DatabaseCore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class UserCreator implements Listener {

    DatabaseCore databaseCore = DatabaseCore.getDatabaseCore();

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        new BukkitRunnable() {

            @Override
            public void run() {
                databaseCore.getPlayerDB().createPlayer(event.getPlayer());
                databaseCore.getUserManager().add(event.getPlayer().getUniqueId());
            }
        }.runTaskAsynchronously(databaseCore);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        new BukkitRunnable() {

            @Override
            public void run() {
                databaseCore.getUserManager().remove(event.getPlayer().getUniqueId());
            }
        }.runTaskAsynchronously(databaseCore);
    }
}
