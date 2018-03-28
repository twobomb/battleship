package sample;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class Server {
    boolean isAlive = true;
    int port;
    Thread thread;
    ServerSocket server;
    int client_id = 0;
    boolean isStepFirst = true;//true - шаг первого, false - второго
    Server _this = this;
    List<server_events> events = new ArrayList<>();
    List<ClientHandler> clients = new ArrayList<>();
    public Server(int port) throws IOException {
         this.port = port;
    }
    public void start(){
        thread = new Thread(th);
        thread.start();

    }
    public void doStep(){
        System.out.println("Next step");
        isStepFirst = !isStepFirst;
    }
    public List<ClientHandler> getClients() {
        return clients;
    }

    public void stop() {
        isAlive = false;
    }
    Runnable th  = new Runnable() {
        @Override
        public void run() {
            try {
                server = new ServerSocket(port);
                server.setSoTimeout(1000);
                Socket s = null;
                while (isAlive) {
                    try {
                        s = server.accept();
                        s.setSoTimeout(1000);
                        System.out.println("Client connected id:"+client_id);
                        clients.add(new ClientHandler(_this,s,client_id));
                        client_id++;
                    }
                    catch (SocketTimeoutException e){
                        
                    }
                }

            }
            catch (SocketTimeoutException ste){
                for(server_events se:events)
                    se.server_timeout();
                isAlive = false;
                thread = null;
            }
            catch (SocketException e) {

                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            isAlive = false;
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("THREAD END");
        }
    };
}

interface server_events{
    public void server_timeout();
}
