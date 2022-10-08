package sab.net;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final ServerSocket serverSocket;

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public Connection accept() throws IOException {
        Socket socket = serverSocket.accept();
        return new Connection(socket);
    }

    public void close() throws IOException {
        serverSocket.close();
    }
}