package me.lena0009.databasecore.player;

import me.lena0009.databasecore.DatabaseCore;
import me.lena0009.databasecore.database.MoneyLogs;
import me.lena0009.databasecore.database.PlayerDB;
import me.lena0009.databasecore.utils.DatabaseUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class User {

    private UUID uuid;
    private String username;
    private String joinDate;
    private String discordId;
    private Double balance;
    private Map<String, Boolean> serverSettings;

    PlayerDB playerDB = DatabaseCore.getDatabaseCore().getPlayerDB();
    MoneyLogs moneyLogs = DatabaseCore.getDatabaseCore().getMoneyLogs();
    DatabaseCore databaseCore = DatabaseCore.getDatabaseCore();

    public User(UUID uuid, String username, String joinDate, String discordId, Double balance, Map<String, Boolean> map) {
        this.uuid = uuid;
        this.username = username;
        this.joinDate = joinDate;
        this.discordId = discordId;
        this.balance = balance;
        this.serverSettings = map;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public String getJoinDate() {
        return joinDate;
    }

    public String getDiscordId() {
        return discordId;
    }

    public Double getBalance() {
        return balance;
    }

    public Boolean cantPay() {
        return this.serverSettings.get("cantPay");
    }

    public Boolean cantWithdraw() {
        return this.serverSettings.get("cantWithdraw");
    }

    public Boolean isSuspectedCheater() {
        return this.serverSettings.get("suspectedCheater");
    }

    public void setServerSetting(String setting, Boolean state) {
        if (serverSettings.containsKey(setting)) {
            this.serverSettings.replace(setting, state);
            playerDB.setSetting(this.getUuid(), setting, state);
        }
    }

    public String getFormattedBalance() {
        return String.format("%,.2f", balance);
    }

    public void setBalance(Double balance) {
        try {
            playerDB.setBalance(this.uuid, balance);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.balance = balance;
    }

    public void addBalance(Double balance) {
        this.setBalance(this.balance + balance);
    }

    public void takeBalance(Double balance) {
        this.setBalance(this.balance - balance);
    }

    public void payPlayer(User receiver, Double amount) {
        this.takeBalance(amount);
        receiver.addBalance(amount);
        moneyLogs.createPayLog(this.getUuid(), receiver.getUuid(), amount);
    }

    public ItemStack createBanknote(Double amount) {
        String banknoteID = moneyLogs.createBanknote(this.getUuid(), amount);
        if (banknoteID != null) {
            this.takeBalance(amount);
            ItemStack banknote = new ItemStack(Material.PAPER);
            ItemMeta im = banknote.getItemMeta();
            im.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6&l * Bank Note *"));
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Withdrawn by: &f" + this.getUsername()));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Amount: &f$" + DatabaseUtils.formatDouble(amount)));
            PersistentDataContainer data = im.getPersistentDataContainer();
            data.set(new NamespacedKey(databaseCore, "itemtype"), PersistentDataType.STRING, "banknote");
            data.set(new NamespacedKey(databaseCore, "id"), PersistentDataType.STRING, banknoteID);
            im.setLore(lore);
            banknote.setItemMeta(im);
            return banknote;
        }
        return null;
    }
}
