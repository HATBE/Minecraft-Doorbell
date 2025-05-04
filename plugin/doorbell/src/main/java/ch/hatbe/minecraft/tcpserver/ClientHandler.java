package ch.hatbe.minecraft.tcpserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientHandler implements Runnable {

    private Thread thread;
    private Socket client;
    private TcpServer server;
    private BufferedReader fromClientReader;
    private PrintWriter toClientWriter;

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

           this.toClientWriter.println("ACK");

            while(!this.server.getServerSocket().isClosed() && this.client.isConnected()) {
                 String data = this.fromClientReader.readLine();

                 if(data == null) {
                     // client disconnects, goto finally
                     break;
                 }

                 if(data.isEmpty()) {
                     continue;
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

        if(this.client != null) {
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
