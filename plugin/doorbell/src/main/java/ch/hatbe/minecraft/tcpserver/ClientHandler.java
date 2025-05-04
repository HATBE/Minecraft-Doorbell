package ch.hatbe.minecraft.tcpserver;

import ch.hatbe.minecraft.Doorbell;
import ch.hatbe.minecraft.DoorbellPlugin;
import ch.hatbe.minecraft.HardwareClient;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ClientHandler implements Runnable {

    private Thread thread;
    private Socket client;
    private TcpServer server;
    private BufferedReader fromClientReader;
    private PrintWriter toClientWriter;

    private HardwareClient hwCLient = null;

    public ClientHandler(Socket client, TcpServer tcpServer) {
        this.client = client;
        this.server = tcpServer;

        this.thread = new Thread(this);
        this.thread.start();
    }

    @Override
    public void run() {
       try {
           this.fromClientReader = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
           this.toClientWriter =  new PrintWriter(new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8), true);

           // TODO: this.toClientWriter.println("ACK");

            while(!this.server.getServerSocket().isClosed() && this.client.isConnected()) {
                 String data = this.fromClientReader.readLine();

                 if(data == null) {
                     // client disconnects, goto finally
                     break;
                 }

                 if(data.isEmpty()) {
                     continue;
                 }

                 String[] parts = data.split("/:/");

                 if(hwCLient == null) {

                     // Check login
                     if(parts.length < 3) {
                         this.toClientWriter.println("NACK");
                         return;
                     }

                     if(!parts[0].equals("LOGIN")) {
                         this.toClientWriter.println("NACK");
                         return;
                     }

                     String clientId = parts[1];
                     String password = parts[2];

                     List<Doorbell> doorbells = JavaPlugin.getPlugin(DoorbellPlugin.class).getRegisteredDoorbells();

                     doorbells.forEach(doorbell -> {
                        if(doorbell.getId().equals(clientId)) {
                            if(doorbell.getPassword().equals(password)) {
                                hwCLient = new HardwareClient(doorbell.getId());
                                this.toClientWriter.println("ACK");
                            }
                        }
                     });

                     //this.toClientWriter.println("NACK");
                     //return;
                 }

                 System.out.println(data);

            }
       } catch (IOException e) {
           e.printStackTrace();
       } finally {
            this.disconnect();
       }
    }

    public BufferedReader getFromClientReader() {
        return fromClientReader;
    }

    public PrintWriter getToClientWriter() {
        return toClientWriter;
    }

    public void disconnect() {
        this.server.removeClient(this);

        if(this.fromClientReader != null) {
            try {
                this.fromClientReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(this.toClientWriter != null) {
            this.toClientWriter.close();
        }

        if (this.client != null) {
            try {
                this.client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Socket getClient() {
        return client;
    }
}
