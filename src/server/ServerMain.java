package server;

import ClientServer.Msg;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class ServerMain {

    private Vector<ClientHandler> clients;

    public ServerMain()
    {
        clients = new Vector();
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

    public void subscribe(ClientHandler client) {
        clients.add(client);
        broadcastClientList();
    }

    public void unsubscribe(ClientHandler client) {
        clients.remove(client);
        broadcastClientList();
    }

    public void sendMsg(ClientHandler c, String from, String msg)
    {
        if(c != null && !c.isInBlacklist(from))
            c.sendMsg(msg);
    }
    public void broadcastMsg(String from, String to, String msg)
    {
        for(ClientHandler c: clients){
            sendMsg(c, from, Msg._msginfo + from + " " + to + " " + msg);
        }
    }
    public void prepareMsg(String from, String to, String msg)
    {
        System.out.println("sendMsg " + from + " " + to + " " + msg);

        if(to.equals(Msg._broadcast)) {
            broadcastMsg(from, to, msg);
            return;
        }
        sendMsg(findClientByLogin(to), from, Msg._msginfo + from + " " + to + " " + msg);
        sendMsg(findClientByLogin(from), from, Msg._msginfo + from + " " + to + " " + msg);
    }


    public boolean isLoggedIn(String login)
    {
        return findClientByLogin(login) != null;
    }

    private ClientHandler findClientByLogin(String login) {
        for(ClientHandler c: clients){
            if(c.getLogin().equals(login))
                return c;
        }
        return null;
    }


    public void broadcastClientList() {
        StringBuilder sb = new StringBuilder();
        sb.append(Msg._query + Msg._online + " ");
        for (ClientHandler o : clients) {
            sb.append(o.getLogin() + ":" + o.getNick() + " ");
        }
        String out = sb.toString();
        for (ClientHandler o: clients) {
            o.sendMsg(out);
        }
    }
}
