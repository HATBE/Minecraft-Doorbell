package ch.hatbe.minecraft.tcpserver;

import ch.hatbe.minecraft.DoorbellPlugin;
import ch.hatbe.minecraft.client.HardwareClient;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

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

                String[] segments = data.split(",");

                for(int i = 0; i < segments.length; i++) {
                    segments[i] = new String(Base64.getDecoder().decode(segments[i].getBytes()));
                }

                if(hwCLient == null) {
                    // CLIENT IS NOT LOGGEDIN

                    if(!segments[0].equals("LOGIN")) {
                        this.toClientWriter.println(PackageHelper.encodeNetworkPackage(new String[]{"LOGIN", "NACK"}));
                        this.disconnect();
                        return;
                    }

                    String username = segments[1];
                    String password = segments[2];

                    if(!JavaPlugin.getPlugin(DoorbellPlugin.class).getHardwareClientsStorage().contains(username)) {
                        this.toClientWriter.println(PackageHelper.encodeNetworkPackage(new String[]{"LOGIN", "NACK"}));
                        this.disconnect();
                        return;
                    }

                    String passwordFromConfig = JavaPlugin.getPlugin(DoorbellPlugin.class).getHardwareClientsStorage().getString(String.format("%s.password", username));

                    if(!passwordFromConfig.equals(password)) {
                        this.toClientWriter.println(PackageHelper.encodeNetworkPackage(new String[]{"LOGIN", "NACK"}));
                        this.disconnect();
                        return;
                    }

                    this.hwCLient = new HardwareClient(username);

                    this.toClientWriter.println(PackageHelper.encodeNetworkPackage(new String[]{"LOGIN", "ACK"}));
                    continue CLIENT_LOOP;
                }

                if(segments[0].equals("RING") && segments[1].equals("ACK")) {
                    System.out.println("RING ACK");
                }

                this.toClientWriter.println(PackageHelper.encodeNetworkPackage(new String[]{"ACK"}));
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

    public HardwareClient getHwCLient() {
        return hwCLient;
    }
}
