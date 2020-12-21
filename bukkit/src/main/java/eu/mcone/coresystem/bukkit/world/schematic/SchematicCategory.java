package eu.mcone.coresystem.bukkit.world.schematic;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bukkit.Material;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class SchematicCategory implements eu.mcone.coresystem.api.bukkit.world.schematic.SchematicCategory {

    @BsonProperty("_id")
    private String id;
    @Setter
    private String name, material;
    private UUID creator;

    public SchematicCategory(String name, Material material, UUID creator) {
        this.id = CoreSystem.getInstance().getUniqueIdUtil().getUniqueKey("schematic");
        this.name = name;
        this.material = material.name();
        this.creator = creator;
    }

    public ItemBuilder getItem() {
        Material material = Material.getMaterial(this.material);
        return new ItemBuilder((material != null ? material : Material.BARRIER));
    }
}
