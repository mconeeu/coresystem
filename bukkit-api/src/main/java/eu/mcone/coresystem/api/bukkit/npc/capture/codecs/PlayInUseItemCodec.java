package eu.mcone.coresystem.api.bukkit.npc.capture.codecs;

import eu.mcone.coresystem.api.bukkit.codec.Codec;
import eu.mcone.coresystem.api.bukkit.npc.entity.PlayerNpc;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Getter
public class PlayInUseItemCodec extends Codec<PlayerInteractEvent, PlayerNpc> {

    public static final byte CODEC_VERSION = 1;

    private Material material;

    public PlayInUseItemCodec() {
        super((byte) 3, (byte) 2);
    }

    @Override
    public Object[] decode(Player player, PlayerInteractEvent interactEvent) {
        ItemStack item = interactEvent.getItem();

        if (item != null) {
            if (item.getType() == Material.BOW) {
                System.out.println("BOW ADD");
                material = item.getType();
                return new Object[]{interactEvent.getPlayer()};
            }
        }

        return null;
    }

    @Override
    public void encode(PlayerNpc npc) {
        System.out.println("Material");
        if (material == Material.BOW) {
            npc.setBow(true);
        }
    }

    @Override
    public void onWriteObject(DataOutputStream out) throws IOException {
        out.writeInt(material.getId());
    }

    @Override
    public void onReadObject(DataInputStream in) throws IOException {
        material = Material.getMaterial(in.readInt());
    }

    @Override
    public String toString() {
        return "PlayInUseItemCodec{" +
                "material=" + material +
                '}';
    }
}
