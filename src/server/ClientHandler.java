package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

import ClientServer.Msg;

public class ClientHandler {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private ServerMain server;
    private String login;
    public boolean isAuthorized = false;

    public ClientHandler(ServerMain server, Socket socket) {
        try {
            this.socket = socket;
            this.server = server;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    while (true) {
                        String str = in.readUTF();
                        System.out.println(str);

                        if (str.startsWith(Msg._query + Msg._auth)) {

                            String[] tokes = str.substring(str.indexOf(",") + 1).split(" ");
                            String newNick = AuthService.getNickByLoginAndPass(tokes[0], tokes[1]);
                            if(server.isLoggedIn(tokes[0]))
                                sendMsg("Пользователь уже подключен");
                            else if (newNick != null) {

                                sendMsg(Msg._query + Msg._authok + newNick);
                                login = tokes[0];
                                isAuthorized = true;
                                server.subscribe(login, ClientHandler.this);

                            } else {
                                sendMsg("Неверный логин/пароль");
                            }
                            continue;
                        }
                        if (str.startsWith(Msg._query + Msg._logout)) {
                            isAuthorized = false;
                            server.unsubscribe(login);
                        }
                        else if(!isAuthorized) continue;
                        else if (str.startsWith(Msg._query + Msg._friends)) {
                            String login = str.substring(str.indexOf(",") + 1);
                            String strFriendsInfo = AuthService.getFriendsInfo(login);
                            sendMsg(Msg._query + Msg._friends + strFriendsInfo);

                        }

                        if (str.startsWith(Msg._msginfo)) {
                            String[] msgInfo = str.substring(Msg._msginfo.length(), str.indexOf(",")).split(" ");
                            server.broadcastMsg(msgInfo[0], msgInfo[1], str.substring(str.indexOf(",") + 1));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    server.unsubscribe(login);
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String str) {
        System.out.println(str);
        try {
            out.writeUTF(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
