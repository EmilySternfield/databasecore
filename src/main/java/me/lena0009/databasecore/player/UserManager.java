package me.lena0009.databasecore.player;

import me.lena0009.databasecore.DatabaseCore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserManager {

    private final Map<UUID, User> map;
    private final DatabaseCore databaseCore;

    public UserManager(DatabaseCore db) {
        map = new HashMap<>();
        databaseCore = db;
    }

    public User getUser(UUID uuid) {
        User user = this.map.get(uuid);
        if (user == null) {
            this.add(uuid);
            user = this.map.get(uuid);
        }
        return user;
    }

    public boolean exist(UUID uuid) {
        return map.containsKey(uuid);
    }

    public void add(UUID uuid) {
        if (exist(uuid)) {
            throw new RuntimeException("UUID found: " + uuid);
        }

        User user = null;
        if (databaseCore.getPlayerDB().hasPlayer(uuid)) {
            user = databaseCore.getPlayerDB().getUser(uuid);
        }
        this.map.put(uuid, user);
    }

    public void remove(UUID uuid) {
        // notnull check, not found throw exception
        if (!exist(uuid)) {
            throw new NullPointerException("UUID not found: " + uuid.toString());
        }
        // otherwise, remove them out
        this.map.remove(uuid);
    }
}
