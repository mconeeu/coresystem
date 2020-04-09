/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.core.labymod;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.mcone.coresystem.api.core.labymod.LabyModAPI;
import eu.mcone.coresystem.api.core.labymod.LabyPermission;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.UUID;

public abstract class LMCUtils<P> implements LabyModAPI<P> {

    public static final String DOMAIN = "play.mcone.eu";

    protected abstract void sendLMCMessage(P player, byte[] message);

    @Override
    public void sendPermissions(P player, Map<LabyPermission, Boolean> permissions) {
        JsonObject object = new JsonObject();

        for (Map.Entry<LabyPermission, Boolean> permissionEntry : permissions.entrySet()) {
            object.addProperty(permissionEntry.getKey().name(), permissionEntry.getValue());
        }

        sendServerMessage(player, "PERMISSIONS", object);
    }

    @Override
    public void unsetCurrentServer(P player) {
        setCurrentServer(player, false, null);
    }

    @Override
    public void setCurrentServer(P player, String gamemodeName) {
        setCurrentServer(player, true, gamemodeName);
    }

    private void setCurrentServer(P player, boolean visible, String gamemodeName) {
        JsonObject object = new JsonObject();
        object.addProperty("show_gamemode", visible); // Gamemode visible for everyone
        object.addProperty("gamemode_name", gamemodeName); // Name of the current playing gamemode

        // Send to LabyMod using the API
        sendServerMessage(player, "server_gamemode", object);
    }

    @Override
    public void unsetCurrentGameInfo(P player) {
        setCurrentGameInfo(player, false, null, 0, 0);
    }

    @Override
    public void setCurrentGameInfo(P player, String gamemode, long startTime, long endTime) {
        setCurrentGameInfo(player, true, gamemode, startTime, endTime);
    }

    private void setCurrentGameInfo(P player, boolean hasGame, String gamemode, long startTime, long endTime) {
        // Create game json object
        JsonObject obj = new JsonObject();
        obj.addProperty("hasGame", hasGame);

        if (hasGame) {
            obj.addProperty("game_mode", gamemode);
            obj.addProperty("game_startTime", startTime); // Set to 0 for countdown
            obj.addProperty("game_endTime", endTime); // // Set to 0 for timer
        }

        // Send to user
        sendServerMessage(player, "discord_rpc", obj);
    }

    @Override
    public void unsetPartyInfo(P player) {
        setPartyInfo(player, false, null, 0, 0);
    }

    @Override
    public void setPartyInfo(P player, UUID partyLeaderUUID, int partySize, int maxPartyMembers) {
        setPartyInfo(player, true, partyLeaderUUID, partySize, maxPartyMembers);
    }

    public void setPartyInfo(P player, boolean hasParty, UUID partyLeaderUUID, int partySize, int maxPartyMembers) {
        // Create party json object
        JsonObject obj = new JsonObject();
        obj.addProperty("hasParty", hasParty);

        if (hasParty) {
            obj.addProperty("partyId", partyLeaderUUID.toString() + ":" + DOMAIN);
            obj.addProperty("party_size", partySize);
            obj.addProperty("party_max", maxPartyMembers);
        }

        // Send to user
        sendServerMessage(player, "discord_rpc", obj);
    }

    @Override
    public void setSubtitle(P receiver, UUID subtitlePlayer, String value) {
        // List of all subtitles
        JsonArray array = new JsonArray();

        // Add subtitle
        JsonObject subtitle = new JsonObject();
        subtitle.addProperty("uuid", subtitlePlayer.toString());

        // Optional: Size of the subtitle
        subtitle.addProperty("size", 0.8d); // Range is 0.8 - 1.6 (1.6 is Minecraft default)

        // no value = remove the subtitle
        if (value != null)
            subtitle.addProperty("value", value);

        // You can set multible subtitles in one packet
        array.add(subtitle);

        // Send to LabyMod using the API
        sendServerMessage(receiver, "account_subtitle", array);
    }

    @Override
    public void sendServerMessage(P player, String messageKey, JsonElement data) {
        // Getting an empty buffer
        ByteBuf byteBuf = Unpooled.buffer();

        // Writing the message-key to the buffer
        writeString(byteBuf, messageKey);

        // Writing the contents to the buffer
        writeString(byteBuf, data.toString());

        // Copying the buffer's bytes to the byte array
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);

        // Returning the byte array
        sendLMCMessage(player, bytes);
    }

    @Override
    public void sendClientToServer(P player, String title, String address, boolean preview) {
        JsonObject object = new JsonObject();
        object.addProperty("title", title); // Title of the warning
        object.addProperty("address", address); // Destination server address
        object.addProperty("preview", preview); // Display the server icon, motd and user count

        // Send to LabyMod using the API
        sendServerMessage(player, "server_switch", object);
    }

    /**
     * Writes a varint to the given byte buffer
     *
     * @param buf   the byte buffer the int should be written to
     * @param input the int that should be written to the buffer
     */
    private void writeVarIntToBuffer(ByteBuf buf, int input) {
        while ((input & -128) != 0) {
            buf.writeByte(input & 127 | 128);
            input >>>= 7;
        }

        buf.writeByte(input);
    }

    /**
     * Writes a string to the given byte buffer
     *
     * @param buf    the byte buffer the string should be written to
     * @param string the string that should be written to the buffer
     */
    private void writeString(ByteBuf buf, String string) {
        byte[] abyte = string.getBytes(Charset.forName("UTF-8"));

        if (abyte.length > Short.MAX_VALUE) {
            throw new EncoderException("String too big (was " + string.length() + " bytes encoded, max " + Short.MAX_VALUE + ")");
        } else {
            writeVarIntToBuffer(buf, abyte.length);
            buf.writeBytes(abyte);
        }
    }

    /**
     * Reads a varint from the given byte buffer
     *
     * @param buf the byte buffer the varint should be read from
     * @return the int read
     */
    public int readVarIntFromBuffer(ByteBuf buf) {
        int i = 0;
        int j = 0;

        byte b0;
        do {
            b0 = buf.readByte();
            i |= (b0 & 127) << j++ * 7;
            if (j > 5) {
                throw new RuntimeException("VarInt too big");
            }
        } while ((b0 & 128) == 128);

        return i;
    }

    /**
     * Reads a string from the given byte buffer
     *
     * @param buf       the byte buffer the string should be read from
     * @param maxLength the string's max-length
     * @return the string read
     */
    public String readString(ByteBuf buf, int maxLength) {
        int i = this.readVarIntFromBuffer(buf);

        if (i > maxLength * 4) {
            throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + i + " > " + maxLength * 4 + ")");
        } else if (i < 0) {
            throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
        } else {
            byte[] bytes = new byte[i];
            buf.readBytes(bytes);

            String s = new String(bytes, Charset.forName("UTF-8"));
            if (s.length() > maxLength) {
                throw new DecoderException("The received string length is longer than maximum allowed (" + i + " > " + maxLength + ")");
            } else {
                return s;
            }
        }
    }

}
