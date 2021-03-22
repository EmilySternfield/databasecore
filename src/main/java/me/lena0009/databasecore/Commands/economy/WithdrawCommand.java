package me.lena0009.databasecore.Commands.economy;

import me.lena0009.databasecore.DatabaseCore;
import me.lena0009.databasecore.player.User;
import me.lena0009.databasecore.utils.DatabaseUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class WithdrawCommand implements CommandExecutor, TabCompleter {

    DatabaseCore databaseCore = DatabaseCore.getDatabaseCore();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            User userPlayer = databaseCore.getUserManager().getUser(player.getUniqueId());
            if (userPlayer.cantWithdraw() || userPlayer.isSuspectedCheater()) {
                player.sendMessage("I'm sorry but you aren't allowed to do that.");
                return true;
            }
            if (args.length != 1) {
                player.sendMessage("improper usage. use /withdraw <amount>");
                return true;
            }
            if (!DatabaseUtils.isDouble(args[0])) {
                player.sendMessage("bitch that aint a number");
                return true;
            }
            Double dummyValue = Double.valueOf(args[0]);
            if (dummyValue > userPlayer.getBalance()) {
                player.sendMessage("LOL UR TOO BROKE XDDDDDDDD");
                return true;
            }
            if (dummyValue < 0) {
                player.sendMessage("you can only withdraw positive numbers");
                return true;
            }
            if (!DatabaseUtils.isValidDouble(args[0])) {
                player.sendMessage("you can only withdraw to 2 decimal places.");
                return true;
            }
            ItemStack banknote = userPlayer.createBanknote(dummyValue);
            player.getInventory().addItem(banknote);
            player.sendMessage("you withdrawed " + DatabaseUtils.formatDouble(dummyValue));
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
     // file:///C:/Users/LuisL/Downloads/tabs.html
    // https://www.homemoviestube.com/search/big%20pussy/page37.html
}
