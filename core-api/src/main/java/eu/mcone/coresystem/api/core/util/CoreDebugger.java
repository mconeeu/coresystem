package eu.mcone.coresystem.api.core.util;

import java.util.Collection;
import java.util.List;

public interface CoreDebugger<P> {

    void registerTargets(String... targets);

    void removeTargets(String... target);

    void removeViewer(P p);

    List<P> getViewersForTarget(String target);

    void registerViewerTargets(P p, String... targets);

    void removeViewerTargets(P p, String... targets);

    boolean hasViewerTarget(P p, String target);

    String existsTargets(String... targets);

    Collection<String> getTargetsForViewer(P p);

    Collection<P> getViewer();

    Collection<String> getTargets();

}
