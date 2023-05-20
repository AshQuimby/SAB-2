package sab.game.screen;

import sab.net.client.Client;
import sab.net.client.ClientListener;
import sab.net.packet.Packet;
import sab.net.server.Server;
import sab.net.server.ServerListener;
import sab.screen.ScreenAdapter;

import java.io.IOException;

public class NetScreen extends ScreenAdapter {
    protected final boolean host;
    protected final boolean local;
    protected final Server server;
    protected final Client client;

    public NetScreen() {
        local = true;
        host = false;
        server = null;
        client = null;
    }

    public NetScreen(Server server) {
        if (server == null) throw new NullPointerException("Server cannot be null");
        host = true;
        local = false;
        this.server = server;
        client = null;

        server.setServerListener(new ServerListener() {
            @Override
            public void connected(int connection) {
                NetScreen.this.connected(connection);
            }

            @Override
            public void disconnected(int connection) {
                NetScreen.this.disconnected(connection);
            }

            @Override
            public void received(int connection, Packet packet) {
                NetScreen.this.receive(connection, packet);
            }
        });
    }

    public NetScreen(Client client) {
        if (client == null) throw new NullPointerException("Client cannot be null");
        host = false;
        local = false;
        this.client = client;
        server = null;

        client.setClientListener(new ClientListener() {
            @Override
            public void received(Packet packet) {
                NetScreen.this.receive(packet);
            }

            @Override
            public void disconnected() {
                NetScreen.this.disconnected();
            }
        });
    }

    @Override
    public void close() {
        if (local) return;
        try {
            if (host) server.close();
            else client.close();
        } catch (IOException ignored) {
        }
    }

    protected void connected(int connection) {

    }

    protected void receive(Packet p) {

    }

    protected void receive(int connection, Packet p) {

    }

    protected void disconnected() {

    }

    protected void disconnected(int connection) {

    }
}
