package sab.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Connection {
    private final Socket socket;
    private final DataOutputStream out;
    private final DataInputStream in;

    public Connection(String address, int port) throws IOException {
        this.socket = new Socket(address, port);
        socket.setTcpNoDelay(true);

        out = new DataOutputStream(this.socket.getOutputStream());
        in = new DataInputStream(this.socket.getInputStream());
    }

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        socket.setTcpNoDelay(true);
        out = new DataOutputStream(this.socket.getOutputStream());
        in = new DataInputStream(this.socket.getInputStream());
    }

    public boolean isConnected() {
        return socket.isConnected();
    }

    public void close() throws IOException {
        socket.close();
        out.close();
        in.close();
    }

    public boolean readBoolean() throws IOException {
        return in.readBoolean();
    }

    public byte readByte() throws IOException {
        return in.readByte();
    }

    public int readInt() throws IOException {
        return in.readInt();
    }

    public long readLong() throws IOException {
        return in.readLong();
    }

    public float readFloat() throws IOException {
        return in.readFloat();
    }

    public String readUTF() throws IOException {
        return in.readUTF();
    }

    public void writeBoolean(boolean b) throws IOException {
        out.writeBoolean(b);
    }

    public void writeByte(byte b) throws IOException {
        out.writeByte(b);
    }

    public void writeInt(int i) throws IOException {
        out.writeInt(i);
    }

    public void writeLong(long l) throws IOException {
        out.writeLong(l);
    }

    public void writeFloat(float f) throws IOException {
        out.writeFloat(f);
    }

    public void writeUTF(String utf) throws IOException {
        out.writeUTF(utf);
    }
}