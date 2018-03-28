package sample;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler {
    Socket client;
    int id;
    Server server;
    int nextType = -1;
    Thread thread;
    boolean isReady = false;
    List<LanShip> ships;
    List<Point> shoots = new ArrayList<>();
    public ClientHandler getEnemy(){
        for(ClientHandler c:server.getClients())
            if(c.id != id)
                return c;
        return null;
    }
    public boolean isMyStep(){
        if(server.isStepFirst && server.getClients().get(0).id == id ||
                !server.isStepFirst && server.getClients().get(1).id == id)
            return true;
        return false;

    }
    public ClientHandler(Server server, Socket client, int id) {
        this.client = client;
        this.id = id;
        this.server = server;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                while (server.isAlive){
                    InputStream is = client.getInputStream();
                    OutputStream os = client.getOutputStream();
                    nextType= -1;
                        while (server.isAlive) {
                            try {
                                nextType = is.read();
                                System.out.println("Client "+id+" receeved "+nextType);
                                break;
                            } catch (SocketTimeoutException e) {

                            }
                        }
                    switch (nextType){
                        case 1://Проверка соединения
                            System.out.println(id+":check connect");
                            if(id >= 2)
                                os.write(2);
                            else
                                os.write(1);
                            os.flush();
                            break;
                        case 2://Готов ли второй игрок? 1 если да
                            //System.out.println(id+":Is ready? ");
                            if(getEnemy() != null && getEnemy().isReady)
                                os.write(1);
                            else
                                os.write(0);
                            os.flush();
                            break;
                        case 3://Клиент пришлет свое поле
                            while (server.isAlive){
                                try {
                                    ObjectInputStream ois = new ObjectInputStream(is);
                                    ships = (List<LanShip>)ois.readObject();
                                    System.out.println(id+":Field recieved! ");
                                    isReady = true;
                                    break;
                                }
                                catch (SocketTimeoutException s){

                                }
                            }
                            break;
                        case 4://Клиент закончил игру(вышел)
                            server.stop();
                            break;
                        case 5://Присывает координаты выстрела

                            while (server.isAlive){
                                try {
                                    ObjectInputStream ois = new ObjectInputStream(is);
                                    Point p = (Point)ois.readObject();
                                    System.out.println(id+":Shoot recieved! ");
                                        if (isMyStep()) {
                                            getEnemy().shoots.add(p);
                                            if(LanShip.getShip(p,getEnemy().ships) == null)
                                                server.doStep();
                                        }

                                    break;
                                }
                                catch (SocketTimeoutException s){
                                    System.out.println(s.getMessage());
                                }
                                catch (Exception e){
                                    e.getStackTrace();
                                }
                            }
                            break;
                        case 6://Cейчас мой шаг? 1 -да, 0 - нет
                            if(isMyStep())
                                os.write(1);
                            else
                                os.write(0);
                            os.flush();
                            break;
                        case 7:{//Отдать расстонку кораблей противника
                            ObjectOutputStream oos = new ObjectOutputStream(os);
                            oos.writeObject(getEnemy().ships);
                            oos.flush();
                            break;}
                        case 8://Отдать выстрела по мне
                            ObjectOutputStream oos = new ObjectOutputStream(os);
                            oos.writeObject(shoots);
                            oos.flush();
                            break;
                        default:System.out.println(nextType);
                    }
                }
                }
                catch (Exception e){
                    e.getStackTrace();
                }
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(id+":Client  disconnected");
            }


        });
        thread.start();
    }
}
