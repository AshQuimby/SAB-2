package sab.net.packet;

import sab.net.Connection;

import java.io.IOException;

public class VersionPacket implements Packet {
    public String namespace;
    public String version;

    public VersionPacket(String namespace, String version) {
        this.namespace = namespace;
        this.version = version;
    }

    public VersionPacket() {

    }

    @Override
    public void send(Connection connection) throws IOException {
        connection.writeUTF(namespace);
        connection.writeUTF(version);
    }

    @Override
    public void receive(Connection connection) throws IOException {
        namespace = connection.readUTF();
        version = connection.readUTF();
    }
}
