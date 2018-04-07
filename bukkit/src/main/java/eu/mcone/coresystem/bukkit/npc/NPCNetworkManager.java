/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.npc;

import io.netty.channel.*;
import net.minecraft.server.v1_8_R3.EnumProtocolDirection;
import net.minecraft.server.v1_8_R3.NetworkManager;

import java.lang.reflect.Field;
import java.net.SocketAddress;

class NPCNetworkManager extends NetworkManager {

    NPCNetworkManager() {
        super(EnumProtocolDirection.CLIENTBOUND);

        try {
            final Field channel = NetworkManager.class.getDeclaredField("channel");
            final Field address = NetworkManager.class.getDeclaredField("l");

            channel.setAccessible(true);
            address.setAccessible(true);
            channel.set(this, new NullChannel());
            address.set(this, new NullSocketAdress());
        } catch (NoSuchFieldException | IllegalAccessException e1) {
            e1.printStackTrace();
        }
    }

    class NullChannel extends AbstractChannel {

        NullChannel() {
            super((Channel) null);
        }

        @Override
        protected AbstractUnsafe newUnsafe() {
            return null;
        }

        @Override
        protected boolean isCompatible(EventLoop eventLoop) {
            return false;
        }

        @Override
        protected SocketAddress localAddress0() {
            return null;
        }

        @Override
        protected SocketAddress remoteAddress0() {
            return null;
        }

        @Override
        protected void doBind(SocketAddress socketAddress) throws Exception {

        }

        @Override
        protected void doDisconnect() throws Exception {

        }

        @Override
        protected void doClose() throws Exception {

        }

        @Override
        protected void doBeginRead() throws Exception {

        }

        @Override
        protected void doWrite(ChannelOutboundBuffer channelOutboundBuffer) throws Exception {

        }

        @Override
        public ChannelConfig config() {
            return null;
        }

        @Override
        public boolean isOpen() {
            return false;
        }

        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        public ChannelMetadata metadata() {
            return null;
        }
    }

    private class NullSocketAdress extends SocketAddress {

        private static final long serialVersionUID = 1L;

    }

}
