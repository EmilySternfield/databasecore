package me.lena0009.databasecore.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class DatabaseUtils {

    public static String getDateAndTime() {
        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(dt);
    }

    public static String format(String toFormat) {
        return ChatColor.translateAlternateColorCodes('&', toFormat);
    }

    public static boolean isDouble(String number) {
        try {
            Double.parseDouble(number);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isValidDouble(String number) {
        if (number.contains(".")) {
            String[] splitter = number.split("\\.");
            int decimalLength = splitter[1].length();  // After Decimal Count
            if (splitter[1].length() <= 2) {
                return true;
            }
            return false;
        } else {
            return true;
        }
    }

    public static String formatDouble(Double number) {
        if (number % 1 == 0) {
            return df.format(number);
        }
        return String.format("%,.2f", number);
    }

    public static Boolean isOnline(String username) {
        Player onlineReceiver = Bukkit.getPlayer(username);
        return onlineReceiver != null;
    }
}
