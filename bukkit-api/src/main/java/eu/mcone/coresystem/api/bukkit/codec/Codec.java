package eu.mcone.coresystem.api.bukkit.codec;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

@Getter
public abstract class Codec<T> {

    private String typ;

    public Codec(String type) {
        this.typ = type;
    }

    public abstract void decode(Player player, T packet);

    public abstract List<Packet<?>> encode(Object object);

    protected void write(ByteArrayDataOutput out) {
        try {
            out.writeUTF(typ);
            onWrite(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void read(ObjectInputStream in) {
        try {
            this.typ = in.readUTF();
            onRead(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract void onWrite(ByteArrayDataOutput out) throws IOException;

    public abstract void onRead(ObjectInputStream in) throws IOException;

    public abstract Class<T> getCodecClass();
}
