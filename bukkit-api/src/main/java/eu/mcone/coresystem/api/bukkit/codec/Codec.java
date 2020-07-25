package eu.mcone.coresystem.api.bukkit.codec;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.io.*;

@NoArgsConstructor
@Setter
public abstract class Codec<C, E> implements Serializable {
    @Getter
    private byte codecID;
    @Getter
    private byte encoderID;

    public Codec(byte codecID, byte encoderID) {
        this.codecID = codecID;
        this.encoderID = encoderID;
    }

    public abstract Object[] decode(Player player, C packet);

    public abstract void encode(E encode);

    public void writeObject(DataOutputStream out) {
        try {
            out.writeByte(encoderID);
            onWriteObject(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readObject(DataInputStream in) throws IOException, ClassNotFoundException {
        this.encoderID = in.readByte();
        onReadObject(in);
    }

    protected abstract void onWriteObject(DataOutputStream out) throws IOException;

    protected abstract void onReadObject(DataInputStream in) throws IOException, ClassNotFoundException;

    public void migrate(DataInputStream in, DataOutputStream out) throws IOException {
        encoderID = in.readByte();
        out.writeByte(encoderID);
    }
}