package ch.hatbe.minecraft.tcpserver;

import ch.hatbe.minecraft.Doorbell;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TcpServer implements Runnable {

    private int port;
    private Thread thread;
    private ServerSocket serverSocket;

    private List<ClientHandler> clients;

    public TcpServer(int port)  {
        this.port = port;
        this.clients = new CopyOnWriteArrayList<ClientHandler>();

        this.thread = new Thread(this);
    }

    @Override
    public void run() {
        try {
            this.serverSocket = new ServerSocket(this.port);

            JavaPlugin.getPlugin(Doorbell.class).getLogger().info(String.format("TCP server successfully started on port %s", this.port));

            while(!serverSocket.isClosed()) {
                JavaPlugin.getPlugin(Doorbell.class).getLogger().info("Waiting for new client to connect");

                Socket client = serverSocket.accept();

                ClientHandler clientHandler = new ClientHandler(client, this);

               this.addClient(clientHandler);
            }
        } catch(IOException e) {
            JavaPlugin.getPlugin(Doorbell.class).getLogger().severe(String.format("Could not start a tcp server on port %s!", this.port));
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
            JavaPlugin.getPlugin(Doorbell.class).getLogger().info("TCP server successfully stopped.");
        } catch (IOException e) {
            JavaPlugin.getPlugin(Doorbell.class).getLogger().severe("Could not stop tcp server!");
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

    public void removeClient(ClientHandler client) {
        this.clients.remove(client);
        JavaPlugin.getPlugin(Doorbell.class).getLogger().info(String.format("Client has disconnected %s. (%s)", client.getClient().getRemoteSocketAddress().toString(), this.clients.size()));
    }

    public void addClient(ClientHandler client) {
        this.clients.add(client);
        JavaPlugin.getPlugin(Doorbell.class).getLogger().info(String.format("New client has connected %s. (%s)", client.getClient().getRemoteSocketAddress().toString(), this.clients.size()));
    }
}
