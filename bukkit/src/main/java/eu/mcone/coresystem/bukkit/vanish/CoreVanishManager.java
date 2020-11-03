package eu.mcone.coresystem.bukkit.vanish;

import eu.mcone.coresystem.api.bukkit.CorePlugin;
import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.vanish.VanishManager;
import eu.mcone.coresystem.api.bukkit.vanish.VanishRule;
import eu.mcone.coresystem.bukkit.listener.VanishListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

import static java.util.stream.Collectors.toMap;

public class CoreVanishManager implements VanishManager {

    private static final VanishRule VANISH_SYSTEM_RULE = (player, playerCanSee) -> {
        if (!player.hasPermission("system.bukkit.vanish")) {
            playerCanSee.removeIf(p -> CoreSystem.getInstance().getCorePlayer(p).isVanished());
        }
    };

    private LinkedHashMap<Integer, VanishRule> vanishRules;
    private final Map<Player, Set<Player>> hiddenPlayers;

    public CoreVanishManager(CorePlugin plugin) {
        this.vanishRules = new LinkedHashMap<>();
        this.hiddenPlayers = new HashMap<>();
        plugin.registerEvents(new VanishListener(this));

        this.vanishRules.put(Integer.MAX_VALUE, VANISH_SYSTEM_RULE);
    }

    @Override
    public void registerVanishRule(int priority, VanishRule rule) {
        vanishRules.put(priority, rule);
        vanishRules = vanishRules
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                .collect(toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e2,
                        LinkedHashMap::new
                ));
    }

    @Override
    public void recalculateVanishes() {
        List<Player> visibleForPlayer = new ArrayList<>();

        for (Player p : Bukkit.getOnlinePlayers()) {
            visibleForPlayer.clear();
            visibleForPlayer.addAll(Bukkit.getOnlinePlayers());
            visibleForPlayer.remove(p);

            for (VanishRule rule : vanishRules.values()) {
                if (!visibleForPlayer.isEmpty()) {
                    rule.visibleForPlayer(p, visibleForPlayer);
                } else break;
            }

            // hide Player all players for p that he should not see
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player != p) {
                    if (visibleForPlayer.contains(player)) {
                        showPlayer(p, player);
                    } else {
                        hidePlayer(p, player);
                    }
                }
            }
        }
    }

    private void hidePlayer(Player player, Player shouldBeHidden) {
        player.hidePlayer(shouldBeHidden);

        if (hiddenPlayers.containsKey(player)) {
            hiddenPlayers.get(player).add(shouldBeHidden);
        } else {
            hiddenPlayers.put(player, new HashSet<>(Collections.singleton(shouldBeHidden)));
        }
    }

    private void showPlayer(Player player, Player shouldBeShown) {
        player.showPlayer(shouldBeShown);

        if (hiddenPlayers.containsKey(player)) {
            hiddenPlayers.get(player).remove(shouldBeShown);
        }
    }

    @Override
    public boolean shouldSee(Player player, Player shouldBeSeen) {
        return !hiddenPlayers.containsKey(player) || !hiddenPlayers.get(player).contains(shouldBeSeen);
    }

    public void playerLeaved(Player player) {
        hiddenPlayers.remove(player);
    }

}
