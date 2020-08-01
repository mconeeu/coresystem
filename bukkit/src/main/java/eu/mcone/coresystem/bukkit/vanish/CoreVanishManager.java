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

    private static final VanishRule VANISH_SYSTEM_RULE = (p, canSeePlayer) -> {
        if (CoreSystem.getInstance().getCorePlayer(p).isVanished()) {
            for (int i = 0; i < canSeePlayer.size(); i++) {
                Player player = canSeePlayer.get(i);

                if (!canSeePlayer.get(i).hasPermission("system.bukkit.vanish")) {
                    canSeePlayer.remove(player);
                }
            }
        }
        System.out.println("can see player "+p.getName()+": "+canSeePlayer);
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
        List<Player> canSeePlayer = new ArrayList<>();

        for (Player p : Bukkit.getOnlinePlayers()) {
            canSeePlayer.clear();
            canSeePlayer.addAll(Bukkit.getOnlinePlayers());
            canSeePlayer.remove(p);

            for (VanishRule rule : vanishRules.values()) {
                rule.allowToSeePlayer(p, canSeePlayer);
            }

            // hide Player p for all players that should not see him
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player != p) {
                    if (canSeePlayer.contains(player)) {
                        showPlayer(player, p);
                    } else {
                        hidePlayer(player, p);
                    }
                }
            }
        }
    }

    private void hidePlayer(Player player, Player shouldBeHidden) {
        System.out.println("hide player "+shouldBeHidden+" for "+player);

        if (!hiddenPlayers.containsKey(player) || !hiddenPlayers.get(player).contains(shouldBeHidden)) {
            player.hidePlayer(shouldBeHidden);
            System.out.println(player+" can see "+shouldBeHidden+"? "+player.canSee(shouldBeHidden));

            if (hiddenPlayers.containsKey(player)) {
                hiddenPlayers.get(player).add(shouldBeHidden);
            } else {
                hiddenPlayers.put(player, new HashSet<>(Collections.singleton(shouldBeHidden)));
            }
        } else {
            System.out.println("aborted");
        }
    }

    private void showPlayer(Player player, Player shouldBeShown) {
        if (hiddenPlayers.containsKey(player) && hiddenPlayers.get(player).contains(shouldBeShown)) {
            player.showPlayer(shouldBeShown);
            hiddenPlayers.get(player).remove(shouldBeShown);
        }
    }

    @Override
    public boolean shouldSee(Player player, Player shouldBeSeen) {
        return !hiddenPlayers.containsKey(player) || !hiddenPlayers.get(player).contains(shouldBeSeen);
    }

    @Override
    public boolean showIfShouldBeSeen(Player target, Player shouldShow) {
        if (shouldSee(target, shouldShow)) {
            target.showPlayer(shouldShow);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean hideIfShouldBeHidden(Player target, Player shouldShow) {
        if (!shouldSee(target, shouldShow)) {
            target.hidePlayer(shouldShow);
            return true;
        } else {
            return false;
        }
    }

    public void playerLeaved(Player player) {
        hiddenPlayers.remove(player);
    }

}
