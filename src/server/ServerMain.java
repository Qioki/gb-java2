package server;

import ClientServer.Msg;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.TreeMap;


public class ServerMain {

    private TreeMap<String, ClientHandler> clients;

    public ServerMain()
    {
        clients = new TreeMap();
        ServerSocket server = null;
        Socket socket = null;

        try {
            AuthService.connect();
            server = new ServerSocket(8187);
            System.out.println("Сервер запущен!");

            while (true) {
                socket = server.accept();
                System.out.println("Клиент подключился");
                new ClientHandler(this, socket);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            AuthService.disconnect();
        }
    }

    public void subscribe(String login, ClientHandler client) {
        clients.put(login, client);
    }

    public void unsubscribe(String login) {
        clients.remove(login);
    }

    public void broadcastMsg(String from, String to, String msg)
    {
        System.out.println("broadcastMsg " + from + " " + to + " " + msg);

        if(clients.containsKey(from))
            clients.get(from).sendMsg(Msg._msginfo + from + " " + to + "," + msg);
        if(clients.containsKey(to))
            clients.get(to).sendMsg(Msg._msginfo + from + " " + to + "," + msg);

    }
    public boolean isLoggedIn(String login)
    {
        return clients.containsKey(login);
    }
}
