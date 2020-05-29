package eu.mcone.coresystem.api.bungee.overwatch.punish;

import java.util.Map;
import java.util.UUID;

public interface PunishManager {

    boolean isBanned(UUID uuid);

    boolean isMuted(UUID uuid);

    Punish getPunish(UUID uuid);

    boolean hasPoints(UUID uuid);

    Map<String, Integer> getPoints(UUID uuid);

    long getBanTimestampByPoints(int points);

    long getMuteTimestampByPoints(int points);

    String getEndeString(long end);
}
