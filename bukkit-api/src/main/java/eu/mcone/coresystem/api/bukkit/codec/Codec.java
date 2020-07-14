package eu.mcone.coresystem.api.bukkit.codec;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

@Getter
public abstract class Codec<C, E> implements Serializable {

    private Class<C> codecClass;
    private Class<E> encodeClass;
    private String typ;

    public Codec(String type) {
        this.typ = type;
    }

    public abstract Object[] decode(Player player, C packet);

    public abstract void encode(E encode);

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
            this.codecClass = (Class<C>) Class.forName(in.readUTF());
            this.encodeClass = (Class<E>) Class.forName(in.readUTF());
            this.typ = in.readUTF();
            onReadObject(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract void onWriteObject(ObjectOutputStream out) throws IOException;

    protected abstract void onReadObject(ObjectInputStream in) throws IOException, ClassNotFoundException;

    public Class<C> getCodecClass() {
        return codecClass;
    }

    public Class<E> getEncodeClass() {
        return encodeClass;
    }
}
