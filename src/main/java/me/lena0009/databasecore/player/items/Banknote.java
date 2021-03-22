package me.lena0009.databasecore.player.items;

import java.util.UUID;

public class Banknote {

    private UUID withdrawerUUID;
    private Double money;
    private Integer amountOfBanknotes;
    private UUID banknoteID;

    public Banknote(UUID withdrawerUUID, Double money, Integer amountOfBanknotes, UUID banknoteID) {
        this.withdrawerUUID = withdrawerUUID;
        this.money = money;
        this.amountOfBanknotes = amountOfBanknotes;
        this.banknoteID = banknoteID;
    }

    public UUID getWithdrawerUUID() {
        return withdrawerUUID;
    }

    public Double getMoney() {
        return money;
    }

    public Integer getAmountOfBanknotes() {
        return amountOfBanknotes;
    }

    public UUID getBanknoteID() {
        return banknoteID;
    }
}
