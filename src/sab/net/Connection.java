package sab.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Connection {
    private final Socket socket;
    private final DataOutputStream out;
    private final DataInputStream in;

    public Connection(String address, int port) {
        try {
            this.socket = new Socket(address, port);

            out = new DataOutputStream(this.socket.getOutputStream());
            in = new DataInputStream(this.socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection(Socket socket) {
        this.socket = socket;

        try {
            out = new DataOutputStream(this.socket.getOutputStream());
            in = new DataInputStream(this.socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            socket.close();
            out.close();
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean readBoolean() {
        try {
            return in.readBoolean();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte readByte() {
        try {
            return in.readByte();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int readInt() {
        try {
            return in.readInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public long readLong() {
        try {
            return in.readLong();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public float readFloat() {
        try {
            return in.readFloat();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String readUTF() {
        try {
			return in.readUTF();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }

    public void writeBoolean(boolean b) {
        try {
            out.writeBoolean(b);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeByte(byte b) {
        try {
            out.writeByte(b);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeInt(int i) {
        try {
            out.writeInt(i);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeLong(long l) {
        try {
            out.writeLong(l);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeFloat(float f) {
        try {
            out.writeFloat(f);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeUTF(String utf) {
        try {
            out.writeUTF(utf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}