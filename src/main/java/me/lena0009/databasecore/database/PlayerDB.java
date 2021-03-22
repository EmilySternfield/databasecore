package me.lena0009.databasecore.database;

import me.lena0009.databasecore.DatabaseCore;
import me.lena0009.databasecore.player.User;
import me.lena0009.databasecore.utils.DatabaseUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.xml.crypto.Data;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDB extends DatabaseCreator {

    DatabaseCore databaseCore = DatabaseCore.getDatabaseCore();

    public PlayerDB(String host, String port, String database, String username, String password) throws SQLException {
        super(host, port, database, username, password);
    }

    @Override
    public void createTable() {
        try {
            PreparedStatement playersTable = super.getConnection().prepareStatement("create table if not exists Players(" +
                    "UUID varchar(36) NOT NULL DEFAULT ''," +
                    "Username varchar(16) NOT NULL DEFAULT ''," +
                    "JoinDate DATETIME NOT NULL DEFAULT '1000-01-01 00:00:00'," +
                    "DiscordID varchar(18) NOT NULL DEFAULT ''," +
                    "Balance double NOT NULL DEFAULT '0'," +
                    "CantPay boolean NOT NULL default false," +
                    "CantWithdraw boolean NOT NULL default false," +
                    "SuspectedCheater boolean NOT NULL default false," +
                    "primary key (UUID))");
            playersTable.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean playerExists(Player player) {
        try {
            PreparedStatement ps = super.getConnection().prepareStatement("select Username from Players where UUID=?");
            ps.setString(1, player.getUniqueId().toString());
            String username;

            // Updates the username if the username is different from that of the database.
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                username = resultSet.getString("Username");
                if (username == player.getName()) {
                    PreparedStatement update = super.getConnection().prepareStatement("update Players set Username=? where UUID=?");
                    update.setString(1, player.getName());
                    update.setString(2, player.getUniqueId().toString());
                    update.executeUpdate();
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasPlayer(UUID uuid) {
        try {
            PreparedStatement ps = super.getConnection().prepareStatement("select Username from Players where UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void createPlayer(Player player) {
        try {
            if (!playerExists(player)) {
                PreparedStatement ps = super.getConnection().prepareStatement("insert into Players (UUID, Username, JoinDate, Balance) values (?,?,?,?)");
                ps.setString(1, player.getUniqueId().toString());
                ps.setString(2, player.getName());
                ps.setString(3, DatabaseUtils.getDateAndTime());
                ps.setString(4, "500");
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getUUID(String username) {
        try {
            PreparedStatement ps = super.getConnection().prepareStatement("select UUID from Players where Username=?");
            ps.setString(1, username);
            ResultSet resultSet = ps.executeQuery();
            String UUID;
            if (resultSet.next()) {
                UUID = resultSet.getString("UUID");
                System.out.println(UUID);
                return UUID;
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
    }

    public User getUser(UUID uuid) {
        try {
            PreparedStatement ps = super.getConnection().prepareStatement("select * from Players where UUID=?");
            ps.setString(1, uuid.toString());
            ResultSet resultSet = ps.executeQuery();
            if (resultSet == null)
                return null;

            ArrayList<String> result = parseResult(resultSet);
            if (result == null || result.size() == 0)
                return null;

            Map<String, Boolean> map = new HashMap<>();
            map.put("cantPay", stringToBoolean(result.get(5)));
            map.put("cantWithdraw", stringToBoolean(result.get(6)));
            map.put("suspectedCheater", stringToBoolean(result.get(7)));
            return new User(
                    UUID.fromString(result.get(0)),
                    result.get(1),
                    result.get(2),
                    result.get(3),
                    Double.parseDouble(result.get(4)),
                    map
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setSetting(UUID uuid, String name, Boolean state) {
        try {
            PreparedStatement update = super.getConnection().prepareStatement("update Players set " + name + "=? where UUID=?");
            update.setString(1, booleanToString(state));
            update.setString(2, uuid.toString());
            update.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void setBalance(UUID uuid, Double newBalance) throws SQLException {
        PreparedStatement ps = super.getConnection().prepareStatement("update Players set Balance=? where UUID=?");
        ps.setString(1, newBalance.toString());
        ps.setString(2, uuid.toString());
        ps.executeUpdate();
    }

    public void addBalance(UUID uuid, Double amount) throws SQLException {
        this.setBalance(uuid, databaseCore.getUserManager().getUser(uuid).getBalance() + amount);
    }

    public void takeBalance(UUID uuid, Double amount) throws SQLException {
        this.setBalance(uuid, databaseCore.getUserManager().getUser(uuid).getBalance() - amount);
    }

    private ArrayList<String> parseResult(ResultSet rs) {
        ArrayList<String> as = new ArrayList<>();
        ResultSetMetaData resultSetMetaData;
        try {
            resultSetMetaData = rs.getMetaData();
            while (rs.next()) {
                for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                    as.add(rs.getString(i));
                }
            }
            return as;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String booleanToString(Boolean state) {
        if (state) {
            return "1";
        }
        return "0";
    }

    private Boolean stringToBoolean(String str) {
        if (str == "1") {
            return true;
        }
        return false;
    }
}
