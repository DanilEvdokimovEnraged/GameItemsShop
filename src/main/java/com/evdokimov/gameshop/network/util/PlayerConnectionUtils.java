package com.evdokimov.gameshop.network.util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerConnectionUtils {

    private static List<UUID> connectedPlayerIds = new ArrayList<>();

    public static synchronized boolean isConnected(UUID playerId) {
        return connectedPlayerIds.contains(playerId);
    }

    public static synchronized void connectPlayer(UUID playerId) {
        connectedPlayerIds.add(playerId);
    }

    public static synchronized void disconnectPlayer(UUID playerId) {
        connectedPlayerIds.remove(playerId);
    }
}
