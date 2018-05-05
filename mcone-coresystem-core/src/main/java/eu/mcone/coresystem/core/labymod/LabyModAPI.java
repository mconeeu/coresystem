/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.core.labymod;

import eu.mcone.coresystem.api.core.exception.LabyModAPIException;
import eu.mcone.coresystem.api.core.labymod.LabyPermission;
import eu.mcone.coresystem.api.core.player.GlobalCorePlayer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.Charset;
import java.util.Map;

public abstract class LabyModAPI implements eu.mcone.coresystem.api.core.labymod.LabyModAPI {

    @Override
    public void sendMessage(GlobalCorePlayer p, String messageKey, String messageContents) {
        send(p, getBytesToSend(messageKey, messageContents));
    }

    @Override
    public void setLabyModPermissions(GlobalCorePlayer p, Map<LabyPermission, Boolean> permissions) {
        send(p, getBytesToSend(permissions));
    }

    public abstract void send(GlobalCorePlayer p, byte[] bytes);

    public static byte[] getBytesToSend(Map<LabyPermission, Boolean> permissions)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        int i = 0;
        for (Map.Entry<LabyPermission, Boolean> permissionEntry : permissions.entrySet()) {
            sb.append("\"").append(permissionEntry.getKey()).append("\"").append(":").append("\"").append(permissionEntry.getValue().toString()).append("\"");
            if (i != permissions.size()) sb.append(",");
        }
        sb.append("}");

        return getBytesToSend("PERMISSIONS", sb.toString());
    }

    private static byte[] getBytesToSend(String messageKey, String messageContents)
    {
        ByteBuf byteBuf = Unpooled.buffer();

        writeString(byteBuf, messageKey);

        writeString(byteBuf, messageContents);

        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);

        return bytes;
    }

    private static void writeVarIntToBuffer(ByteBuf buf, int input)
    {
        while ((input & 0xFFFFFF80) != 0)
        {
            buf.writeByte(input & 0x7F | 0x80);
            input >>>= 7;
        }
        buf.writeByte(input);
    }

    private static void writeString(ByteBuf buf, String string)
    {
        byte[] abyte = string.getBytes(Charset.forName("UTF-8"));
        if (abyte.length > 32767) {
            throw new LabyModAPIException("String too big (was " + string.length() + " bytes encoded, max " + 32767 + ")");
        }
        writeVarIntToBuffer(buf, abyte.length);
        buf.writeBytes(abyte);
    }

    @Override
    public int readVarIntFromBuffer(ByteBuf buf)
    {
        int i = 0;
        int j = 0;
        byte b0;
        do
        {
            b0 = buf.readByte();
            i |= (b0 & 0x7F) << j++ * 7;
            if (j > 5) {
                throw new RuntimeException("VarInt too big");
            }
        } while ((b0 & 0x80) == 128);
        return i;
    }

    @Override
    public String readString(ByteBuf buf, int maxLength)
    {
        int i = readVarIntFromBuffer(buf);
        if (i > maxLength * 4) {
            throw new LabyModAPIException("The received encoded string buffer length is longer than maximum allowed (" + i + " > " + maxLength * 4 + ")");
        }
        if (i < 0) {
            throw new LabyModAPIException("The received encoded string buffer length is less than zero! Weird string!");
        }
        byte[] bytes = new byte[i];
        buf.readBytes(bytes);

        String s = new String(bytes, Charset.forName("UTF-8"));
        if (s.length() > maxLength) {
            throw new LabyModAPIException("The received string length is longer than maximum allowed (" + i + " > " + maxLength + ")");
        }
        return s;
    }

}
