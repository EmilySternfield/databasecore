package me.lena0009.databasecore.Commands.economy;

import me.lena0009.databasecore.DatabaseCore;
import me.lena0009.databasecore.Messages;
import me.lena0009.databasecore.player.User;
import me.lena0009.databasecore.utils.DatabaseUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;

public class PayCommand implements CommandExecutor, TabCompleter {

    DatabaseCore databaseCore = DatabaseCore.getDatabaseCore();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            User userPlayer = databaseCore.getUserManager().getUser(player.getUniqueId());
            if (userPlayer.isSuspectedCheater() || userPlayer.cantPay()) {
                player.sendMessage("I'm sorry but you aren't allowed to do that.");
                return true;
            }
            if (args.length < 2) {
                player.sendMessage(DatabaseUtils.format(Messages.PAY_INVALID_USAGE.getMessage()));
                return true;
            }
            if (!DatabaseUtils.isDouble(args[1])) {
                player.sendMessage("bitch that aint a number");
                return true;
            }
            Double dummyValue = Double.valueOf(args[1]);
            if (dummyValue > userPlayer.getBalance()) {
                player.sendMessage("LOL UR TOO BROKE XDDDDDDDD");
                return true;
            }
            if (dummyValue < 0) {
                player.sendMessage("you can only send positive numbers");
                return true;
            }
            if (!DatabaseUtils.isValidDouble(args[1])) {
                player.sendMessage("you can only send to 2 decimal places.");
                return true;
            }

            Player receiver = Bukkit.getPlayer(args[0]);
            try {
                if (receiver.getName() == player.getName() || args[0] == player.getName()) {
                    player.sendMessage("u cant pay urself bruv");
                    return true;
                }
            } catch (NullPointerException e) {

            }
            String formattedDummyValue = DatabaseUtils.formatDouble(dummyValue);
            if (DatabaseUtils.isOnline(args[0])) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        User receiverUser = databaseCore.getUserManager().getUser(receiver.getUniqueId());
                        userPlayer.payPlayer(receiverUser, dummyValue);
                        player.sendMessage(DatabaseUtils.format(Messages.PAY_SUCCESS.getMessage().replace("%amount%", formattedDummyValue).replace("%player%", receiver.getName())));
                        receiver.sendMessage(DatabaseUtils.format(Messages.PAY_SUCCESS_TARGET.getMessage().replace("%sender%", player.getName()).replace("%amount%", formattedDummyValue)));
                    }
                }.runTaskAsynchronously(databaseCore);
                return true;
            }
            String receiverUUID = databaseCore.getPlayerDB().getUUID(args[0]);
            if (receiverUUID != null) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        User receiverUser = databaseCore.getPlayerDB().getUser(UUID.fromString(receiverUUID));
                        userPlayer.payPlayer(receiverUser, dummyValue);
                        player.sendMessage(DatabaseUtils.format(Messages.PAY_SUCCESS.getMessage().replace("%amount%", formattedDummyValue).replace("%player%", receiverUser.getUsername())));
                    }
                }.runTaskAsynchronously(databaseCore);
            } else {
                player.sendMessage("That player was not found in our database.");
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
