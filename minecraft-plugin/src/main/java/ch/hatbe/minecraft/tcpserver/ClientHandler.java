package ch.hatbe.minecraft.tcpserver;

import ch.hatbe.minecraft.DoorbellPlugin;
import ch.hatbe.minecraft.client.HardwareClient;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

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

           // TODO: this.toClientWriter.println("CONN,ACK");

           CLIENT_LOOP: while(!this.server.getServerSocket().isClosed() && this.client.isConnected()) {
                String data = this.fromClientReader.readLine();

                if (data == null) {
                    // client disconnects, goto finally
                    break;
                }

                if (data.isEmpty()) {
                    continue;
                }

               System.out.println(data);

               this.toClientWriter.println("ABC");

               /*

                String[] segments = data.split("/:/");

                // NOT LOGGED IN!
                if(hwCLient == null) {
                    if(segments.length != 3) {
                        return;
                    }

                    System.out.println("1");


                    if(!segments[0].equals("LOGIN")) {
                        System.out.println("NO LOGIN");
                        return;
                    }

                    String clientId = segments[1];
                    String password = segments[2];

                    System.out.println(clientId + " " + password);

                    for(HardwareClient client : JavaPlugin.getPlugin(DoorbellPlugin.class).getRegisteredHardwareClients()) {
                        if(client.getId().equals(clientId)) {
                            // TODO: HASHING!!
                            if(client.getPasswordHash().equals(password)) {
                                // LOGIN IS SUCCESSFULLY
                                this.hwCLient = client;
                                this.toClientWriter.println("LOGIN/:/ACK");
                                continue CLIENT_LOOP;
                            }

                        }
                    }

                    this.toClientWriter.println("LOGIN/:/NACK");

                    this.disconnect();
                    return;
                }

                // TODO: GOON::::*/

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
