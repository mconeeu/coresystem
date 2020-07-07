package eu.mcone.coresystem.api.bukkit.codec;

import lombok.Getter;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

@Getter
public abstract class Codec<T> implements Serializable {

    private String typ;

    public Codec(String type) {
        this.typ = type;
    }

    public abstract void decode(Player player, T packet);

    public abstract List<Packet<?>> encode(Object object);

    private void writeObject(ObjectOutputStream out) throws IOException {
        try {
            out.writeUTF(typ);
            onWriteObject(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        try {
            this.typ = in.readUTF();
            onReadObject(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract void onWriteObject(ObjectOutputStream out) throws IOException;

    protected abstract void onReadObject(ObjectInputStream in) throws IOException, ClassNotFoundException;

    public abstract Class<T> getCodecClass();
}
