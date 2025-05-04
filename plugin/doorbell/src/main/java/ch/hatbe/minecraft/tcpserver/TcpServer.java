package ch.hatbe.minecraft.tcpserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TcpServer implements Runnable {

    private Thread thread;
    private ServerSocket serverSocket;

    List<ClientHandler> clients;

    public TcpServer()  {
        this.clients = new CopyOnWriteArrayList<ClientHandler>();

        this.thread = new Thread(this);
    }

    @Override
    public void run() {
        try {
            this.serverSocket = new ServerSocket(1337);

            System.out.println("Server started on 1337");

            while(!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();

                ClientHandler clientHandler = new ClientHandler(socket);

                clients.add(clientHandler);

                System.out.println(String.format("Client has connected %s", socket.getRemoteSocketAddress().toString()));
            }

            System.out.println("SUCCESSFULLY STARTED SERVER");
        } catch(IOException e) {
            System.err.println("ERR STARTING SERVER");
            e.printStackTrace();
        }
    }

    public TcpServer start() {
        this.thread.start();
        return this;

    }

    public void stop() {
        if(serverSocket == null) {
            return;
        }

        try {
            this.serverSocket.close();
            System.out.println("Server stopped.");
        } catch (IOException e) {
            System.err.println("Could not stop server!");
            e.printStackTrace();
        }
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public Thread getThread() {
        return thread;
    }

    public List<ClientHandler> getClients() {
        return clients;
    }
}
