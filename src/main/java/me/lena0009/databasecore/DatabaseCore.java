package me.lena0009.databasecore;

import me.lena0009.databasecore.Commands.economy.BalanceCommand;
import me.lena0009.databasecore.Commands.economy.PayCommand;
import me.lena0009.databasecore.Commands.economy.WithdrawCommand;
import me.lena0009.databasecore.database.MoneyLogs;
import me.lena0009.databasecore.database.PlayerDB;
import me.lena0009.databasecore.listeners.ItemListener;
import me.lena0009.databasecore.listeners.UserCreator;
import me.lena0009.databasecore.player.UserManager;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class DatabaseCore extends JavaPlugin {

    private static DatabaseCore databaseCore;
    private UserManager userManager;
    private PlayerDB playerDB;
    private MoneyLogs moneyLogs;

    @Override
    public void onEnable() {
        // Plugin startup logic
        databaseCore = this;
        userManager = new UserManager(databaseCore);
        registerDatabase();

        getServer().getPluginManager().registerEvents(new UserCreator(), databaseCore);
        getServer().getPluginManager().registerEvents(new ItemListener(), databaseCore);
        databaseCore.getCommand("pay").setExecutor(new PayCommand());
        databaseCore.getCommand("pay").setTabCompleter(new PayCommand());
        databaseCore.getCommand("balance").setExecutor(new BalanceCommand());
        databaseCore.getCommand("balance").setTabCompleter(new BalanceCommand());
        databaseCore.getCommand("withdraw").setExecutor(new WithdrawCommand());
        databaseCore.getCommand("withdraw").setTabCompleter(new WithdrawCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        closeDatabaseConnections();
    }


    public static DatabaseCore getDatabaseCore() {
        return databaseCore;
    }

    public PlayerDB getPlayerDB() {
        return playerDB;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public MoneyLogs getMoneyLogs() {
        return moneyLogs;
    }

    public void registerDatabase() {
        try {
            playerDB = new PlayerDB("localhost", "3306", "paperprisonplayer", "paperprisonplayer", "t{4{k#H+$A_'t^6E6,Hy=M+f]3@gLPJnjD`R=4+zP8AUuNx$\"`\\K~-.NmAqpV>]DcVmd}+)C@w*MGx{PTwgEjZMQ#W>M>67bwU`!APCQB:H6<']kfW)$Xm>Mt6E{}X$6");
        } catch (SQLException e) {
            e.printStackTrace();
            getServer().getConsoleSender().sendMessage(ChatColor.RED + "Player database not connected!");
            getServer().shutdown();
        }
        if (playerDB.isConnected()) {
            playerDB.createTable();
        }
        try {
            moneyLogs = new MoneyLogs("localhost", "3306", "moneylogs", "moneylogs", "]4ce9g6,7;d~!SnmGQ\"2!ExrmTkNnE'THe`[R:MA#/5(T62pW^ad8yFB5]n-SX({;~U??rBrb').w)Qe+-xKSj_eMwh4PKZ$q>wDr]uU)x>H'45=pyU(BHe3CL</!zUQ");
        } catch (SQLException e) {
            e.printStackTrace();
            getServer().getConsoleSender().sendMessage(ChatColor.RED + "MoneyLogs database not connected!");
            getServer().shutdown();
        }
        if (moneyLogs.isConnected()) {
            moneyLogs.createTable();
        }
    }

    public void closeDatabaseConnections() {
        if (playerDB.isConnected()) {
            playerDB.disconnect();
        }
        if (moneyLogs.isConnected())
            moneyLogs.disconnect();
    }
}
