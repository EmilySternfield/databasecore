package me.lena0009.databasecore;

public enum Messages {

    VAR_PLAYER_NOT_EXIST("&c%player% cannot be found in our database or has never played before."),
    PAY_INVALID_USAGE("&7Pays another player from your balance.\n/pay <player> <amount>"),
    PAY_SUCCESS("&dYou sent &a$%amount% &dto &a%player%&d!"),
    PAY_SUCCESS_TARGET("&a%sender% &dsent you &a$%amount%&d!"),
    PAY_NOT_ENOUGH("BITCH UR TOO BROKE");

    private final String message;

    Messages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
