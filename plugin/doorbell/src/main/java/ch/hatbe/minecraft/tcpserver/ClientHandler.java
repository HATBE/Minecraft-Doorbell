package ch.hatbe.minecraft.tcpserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientHandler implements Runnable {

    private Thread thread;
    private Socket socket;
    private BufferedReader inputReader;
    private PrintWriter outputWriter;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.thread = new Thread(this);
        this.thread.start();
    }

    @Override
    public void run() {
       try {
           this.inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
           this.outputWriter =  new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

           this.outputWriter.println("ACK");

            while(this.socket.isConnected()) {
                 String data = this.inputReader.readLine();

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
            //this.close();
       }
    }

    public BufferedReader getInputReader() {
        return inputReader;
    }

    public PrintWriter getOutputWriter() {
        return outputWriter;
    }

    private void close() {
        if(this.inputReader != null) {
            try {
                this.inputReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(this.outputWriter != null) {
            this.outputWriter.close();
        }

        if(this.socket != null) {
            try {
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println(String.format("Client %s has disconnected", this.socket.getRemoteSocketAddress()));
    }
}
