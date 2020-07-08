package eu.mcone.coresystem.api.bukkit.codec;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

@Getter
public abstract class Codec<T> implements Serializable {

    private Class<T> codecClass;
    private String typ;

    public Codec(String type) {
        this.typ = type;
    }

    public abstract Object[] decode(Player player, T packet);

    public abstract List<Object> encode(Object... args);

    private void writeObject(ObjectOutputStream out) throws IOException {
        try {
            out.writeUTF(codecClass.getSimpleName());
            out.writeUTF(typ);
            onWriteObject(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        try {
            this.codecClass = (Class<T>) Class.forName(in.readUTF());
            this.typ = in.readUTF();
            onReadObject(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract void onWriteObject(ObjectOutputStream out) throws IOException;

    protected abstract void onReadObject(ObjectInputStream in) throws IOException, ClassNotFoundException;

    protected Class<T> getCodecClass() {
        return codecClass;
    }
}
