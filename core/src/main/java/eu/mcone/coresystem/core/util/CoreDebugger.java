package eu.mcone.coresystem.core.util;

import eu.mcone.coresystem.core.CoreModuleCoreSystem;

import java.util.*;

public abstract class CoreDebugger<P> implements eu.mcone.coresystem.api.core.util.CoreDebugger<P> {

    private final HashMap<P, List<String>> viewers;
    private final HashSet<String> targets;


    public CoreDebugger(CoreModuleCoreSystem coreModuleCoreSystem) {
        viewers = new HashMap<>();

        String debugTargets = System.getProperty("DebugTargets");
        if (debugTargets != null && !debugTargets.isEmpty()) {
            System.out.println(debugTargets);
            targets = new HashSet<>(Arrays.asList(coreModuleCoreSystem.getGson().fromJson(debugTargets, String[].class)));
        } else {
            targets = new HashSet<>();
        }
    }

    public void registerTargets(String... targets) {
        this.targets.addAll(Arrays.asList(targets));
    }

    public void removeTargets(String... target) {
        this.targets.removeAll(Arrays.asList(target));
    }

    public void removeViewer(P p) {
        viewers.remove(p);
    }

    public List<P> getViewersForTarget(String target) {
        List<P> viewers = new ArrayList<>();
        for (Map.Entry<P, List<String>> entry : this.viewers.entrySet()) {
            if (entry.getValue().contains(target)) {
                viewers.add(entry.getKey());
            }
        }

        return viewers;
    }

    public void registerViewerTargets(P p, String... targets) {
        if (!viewers.containsKey(p)) {
            viewers.put(p, new ArrayList<>());
        }

        for (String target : targets) {
            viewers.get(p).add(target);
        }
    }

    public void removeViewerTargets(P p, String... targets) {
        if (viewers.containsKey(p)) {
            for (String target : targets) {
                viewers.get(p).remove(target);
            }
        }
    }

    public boolean hasViewerTarget(P p, String target) {
        if (viewers.containsKey(p)) {
            return viewers.get(p).contains(target);
        } else {
            return false;
        }
    }

    public String existsTargets(String... targets) {
        for (String target : targets) {
            if (!this.targets.contains(target)) {
                return target;
            }
        }

        return null;
    }

    public Collection<String> getTargetsForViewer(P p) {
        return this.viewers.get(p);
    }

    public Collection<P> getViewer() {
        return this.viewers.keySet();
    }

    public Collection<String> getTargets() {
        return targets;
    }
}
