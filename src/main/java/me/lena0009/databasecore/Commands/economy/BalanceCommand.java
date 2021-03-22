package me.lena0009.databasecore.Commands.economy;

import me.lena0009.databasecore.DatabaseCore;
import me.lena0009.databasecore.Messages;
import me.lena0009.databasecore.player.User;
import me.lena0009.databasecore.utils.DatabaseUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;

public class BalanceCommand implements CommandExecutor, TabCompleter {

    DatabaseCore databaseCore = DatabaseCore.getDatabaseCore();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 0) {
                player.sendMessage(databaseCore.getUserManager().getUser(player.getUniqueId()).getFormattedBalance());
                return true;
            } else if (args.length == 1) {
                if (DatabaseUtils.isOnline(args[0])) {
                    Player target = Bukkit.getPlayer(args[0]);
                    player.sendMessage(databaseCore.getUserManager().getUser(target.getUniqueId()).getFormattedBalance());
                    return true;
                }
                String receiverUUID = databaseCore.getPlayerDB().getUUID(args[0]);
                if (receiverUUID != null) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            User receiverUser = databaseCore.getPlayerDB().getUser(UUID.fromString(receiverUUID));
                            player.sendMessage(receiverUser.getFormattedBalance() + " " + receiverUser.getUsername());
                        }
                    }.runTaskAsynchronously(databaseCore);
                } else {
                    player.sendMessage("That player was not found in our database.");
                }
                return true;
            }
            player.sendMessage("oi bruv. use /balance to check ur own balance and /bal <player> to check someone else.");
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
