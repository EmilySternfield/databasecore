package me.lena0009.databasecore.database;

import me.lena0009.databasecore.DatabaseCore;
import me.lena0009.databasecore.database.DatabaseCreator;
import me.lena0009.databasecore.player.items.Banknote;
import me.lena0009.databasecore.utils.DatabaseUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class MoneyLogs extends DatabaseCreator {

    public MoneyLogs(String host, String port, String database, String username, String password) throws SQLException {
        super(host, port, database, username, password);
    }

    @Override
    public void createTable() {
        try {
            PreparedStatement moneyLogsTable = super.getConnection().prepareStatement("create table if not exists MoneyLogs(" +
                    "SenderUUID varchar(36) NOT NULL DEFAULT ''," +
                    "ReceivingUUID varchar(36) NOT NULL DEFAULT ''," +
                    "Amount double NOT NULL DEFAULT '0'," +
                    "Time DATETIME NOT NULL DEFAULT '1000-01-01 00:00:00'," +
                    "Cause varchar(20) NOT NULL DEFAULT ''," +
                    "TypeID varchar(36) NOT NULL DEFAULT ''" +
                    ")");
            moneyLogsTable.executeUpdate();
            PreparedStatement withdrawLogsTable = super.getConnection().prepareStatement("create table if not exists WithdrawLogs(" +
                    "PlayerUUID varchar(36) NOT NULL DEFAULT ''," +
                    "Action varchar(10) NOT NULL DEFAULT ''," +
                    "Money double NOT NULL DEFAULT '0'," +
                    "Time DATETIME NOT NULL DEFAULT  '1000-01-01 00:00:00'," +
                    "BanknoteID varchar(36) NOT NULL DEFAULT ''," +
                    "TransactionID varchar(36) NOT NULL DEFAULT ''" +
                    ")");
            withdrawLogsTable.executeUpdate();
            PreparedStatement bankNotes = super.getConnection().prepareStatement("create table if not exists Banknotes(" +
                    "WithdrawerUUID varchar(36) NOT NULL DEFAULT ''," +
                    "Money double NOT NULL DEFAULT '0'," +
                    "AmountOfBanknotes integer NOT NULL DEFAULT '1'," +
                    "BanknoteID varchar(36) NOT NULL DEFAULT ''" +
                    ")");
            bankNotes.executeUpdate();
            PreparedStatement payLogsTable = super.getConnection().prepareStatement("create table if not exists PayLogs(" +
                    "SenderUUID varchar(36) NOT NULL DEFAULT ''," +
                    "ReceivingUUID varchar(36) NOT NULL DEFAULT ''," +
                    "Amount double NOT NULL DEFAULT '0'," +
                    "Time DATETIME NOT NULL DEFAULT '1000-01-01 00:00:00'," +
                    "TransactionID varchar(36) NOT NULL DEFAULT ''" +
                    ")");
            payLogsTable.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean createPayLog(UUID sender, UUID receiver, Double amount) {
        try {
            String uuid = this.getNewUUID();
            String time = DatabaseUtils.getDateAndTime();
            PreparedStatement moneyLogsUpdate = super.getConnection().prepareStatement("insert into MoneyLogs values (?, ?, ?, ?, ?, ?)");
            moneyLogsUpdate.setString(1, sender.toString());
            moneyLogsUpdate.setString(2, receiver.toString());
            moneyLogsUpdate.setString(3, amount.toString());
            moneyLogsUpdate.setString(4, time);
            moneyLogsUpdate.setString(5, "pay");
            moneyLogsUpdate.setString(6, uuid);
            moneyLogsUpdate.executeUpdate();

            PreparedStatement payLogsUpdate = super.getConnection().prepareStatement("insert into PayLogs values (?, ?, ?, ?, ?)");
            payLogsUpdate.setString(1, sender.toString());
            payLogsUpdate.setString(2, receiver.toString());
            payLogsUpdate.setString(3, amount.toString());
            payLogsUpdate.setString(4, time);
            payLogsUpdate.setString(5, uuid);
            payLogsUpdate.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String createBanknote(UUID withdrawer, Double amount) {
        try {
            Boolean exist = null;
            PreparedStatement query = super.getConnection().prepareStatement("select BanknoteID from banknotes where WithdrawerUUID=? and Money=? and AmountOfBanknotes>0;");
            query.setString(1, withdrawer.toString());
            query.setString(2, amount.toString());
            ResultSet rs = query.executeQuery();
            String banknoteID = null;
            if (rs.next()) {
                banknoteID = rs.getString("BanknoteID");
                exist = true;
            }
            if (banknoteID == null) {
                banknoteID = getNewUUID();
                exist = false;
            }

            String tid = getNewUUID();

            if (!exist) {
                PreparedStatement banknotesLog = super.getConnection().prepareStatement("insert into Banknotes VALUES (?, ?, ?, ?)");
                banknotesLog.setString(1, withdrawer.toString());
                banknotesLog.setString(2, amount.toString());
                banknotesLog.setString(3, "1");
                banknotesLog.setString(4, banknoteID);
                banknotesLog.executeUpdate();
            } else {
                PreparedStatement banknotesLog = super.getConnection().prepareStatement("update Banknotes set AmountOfBanknotes=AmountOfBanknotes+1 where BanknoteID=?");
                banknotesLog.setString(1, banknoteID);
                banknotesLog.executeUpdate();
            }

            PreparedStatement withdrawLog = super.getConnection().prepareStatement("insert into WithdrawLogs VALUES (?, ?, ?, ?, ?, ?)");
            withdrawLog.setString(1, withdrawer.toString());
            withdrawLog.setString(2, "Withdraw");
            withdrawLog.setString(3, amount.toString());
            withdrawLog.setString(4, DatabaseUtils.getDateAndTime());
            withdrawLog.setString(5, banknoteID);
            withdrawLog.setString(6, tid);
            withdrawLog.executeUpdate();

            PreparedStatement moneyLogs = super.getConnection().prepareStatement("insert into MoneyLogs VALUES (?, ?, ?, ?, ?, ?)");
            moneyLogs.setString(1, withdrawer.toString());
            moneyLogs.setString(2, "");
            moneyLogs.setString(3, amount.toString());
            moneyLogs.setString(4, DatabaseUtils.getDateAndTime());
            moneyLogs.setString(5, "withdraw");
            moneyLogs.setString(6, tid);
            moneyLogs.executeUpdate();

            return banknoteID;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void redeemBanknote(UUID redeemer, Banknote banknote) {
        try {
            String tid = getNewUUID();

            PreparedStatement moneyLogs = super.getConnection().prepareStatement("insert into MoneyLogs VALUES (?, ?, ?, ?, ?, ?)");
            moneyLogs.setString(1, "");
            moneyLogs.setString(2, redeemer.toString());
            moneyLogs.setString(3, banknote.getMoney().toString());
            moneyLogs.setString(4, DatabaseUtils.getDateAndTime());
            moneyLogs.setString(5, "redeem");
            moneyLogs.setString(6, tid);
            moneyLogs.executeUpdate();

            PreparedStatement withdrawLog = super.getConnection().prepareStatement("insert into WithdrawLogs VALUES (?, ?, ?, ?, ?, ?)");
            withdrawLog.setString(1, redeemer.toString());
            withdrawLog.setString(2, "Redeem");
            withdrawLog.setString(3, banknote.getMoney().toString());
            withdrawLog.setString(4, DatabaseUtils.getDateAndTime());
            withdrawLog.setString(5, banknote.getBanknoteID().toString());
            withdrawLog.setString(6, tid);
            withdrawLog.executeUpdate();

            PreparedStatement banknotesLog = super.getConnection().prepareStatement("update Banknotes set AmountOfBanknotes=AmountOfBanknotes-1 where BanknoteID=?");
            banknotesLog.setString(1, banknote.getBanknoteID().toString());
            banknotesLog.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Banknote getBanknote(UUID uuid) {
        try {
            PreparedStatement ps = super.getConnection().prepareStatement("select * from Banknotes where BanknoteID=?");
            ps.setString(1, uuid.toString());
            ResultSet resultSet = ps.executeQuery();
            if (resultSet == null)
                return null;

            ArrayList<String> result = parseResult(resultSet);
            if (result == null || result.size() == 0)
                return null;

            return new Banknote(
                    UUID.fromString(result.get(0)),
                    Double.parseDouble(result.get(1)),
                    Integer.valueOf(result.get(2)),
                    UUID.fromString(result.get(3))
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getNewUUID() {
        try {
            String UUID;
            PreparedStatement ps = super.getConnection().prepareStatement("select UUID() as uuid");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                UUID = rs.getString("uuid");
                return UUID;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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
}
