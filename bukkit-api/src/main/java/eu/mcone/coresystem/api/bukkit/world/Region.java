package eu.mcone.coresystem.api.bukkit.world;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Location;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public final class Region {

    public enum Selection {
        RECTANGULAR, CUBIC, CIRCULAR, SPHERICAL
    }

    private String name;
    private Selection selection;
    private CoreBlockLocation pos1, pos2;

    public Region(String name, CoreBlockLocation pos1, CoreBlockLocation pos2) {
        this(name, Selection.CUBIC, pos1, pos2);
    }

    public boolean isInRegion(Location location) {
        return isInRegion(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public boolean isInRegion(CoreBlockLocation location) {
        return isInRegion(location.getX(), location.getY(), location.getZ());
    }

    private boolean isInRegion(int x, int y, int z) {
        int x1 = pos1.getX(), x2 = pos2.getX(), y1 = pos1.getY(), y2 = pos2.getY(), z1 = pos1.getZ(), z2 = pos2.getZ();

        switch (selection) {
            case RECTANGULAR:
            case CUBIC: {
                return ((x1 - x2 > 0) ? (x <= x1 && x >= x2) : (x >= x1 && x <= x2))
                        && ((z1 - z2 > 0) ? (z <= z1 && z >= z2) : (z >= z1 && z <= z2))
                        && (selection.equals(Selection.RECTANGULAR) || ((y1 - y2 > 0) ? (y <= y1 && y >= y2) : (y >= y1 && y <= y2)));
            }
            case CIRCULAR:
            case SPHERICAL: {
                double rad = Math.sqrt(((x1 - x2) ^ 2) + ((z1 - z2) ^ 2) + (selection.equals(Selection.SPHERICAL) ? ((y1 - y2) ^ 2) : 0));
                double distance = Math.sqrt(((x1 - x) ^ 2) + ((z1 - z) ^ 2) + (selection.equals(Selection.SPHERICAL) ? ((y1 - y) ^ 2) : 0));

                return distance <= rad;
            }
            default:
                return false;
        }
    }

    @Override
    public String toString() {
        return "Region{" +
                "name='" + name + '\'' +
                ", selection=" + selection +
                ", pos1=" + pos1 +
                ", pos2=" + pos2 +
                '}';
    }
}
