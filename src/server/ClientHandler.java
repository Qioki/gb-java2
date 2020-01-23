package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import ClientServer.Msg;

public class ClientHandler {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private ServerMain server;
    private String login = "";
    private String nick = "";
    public boolean isAuthorized = false;
    private ArrayList<String> myBlacklist = new ArrayList<>();

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

                        if (str.startsWith(Msg._query)) {
                            String[] tokes = str.split(" ");
                            if(tokes.length < 2) continue;
                            switch(tokes[1]) {
                                case Msg._auth:
                                    if(tokes.length < 4) break;
                                    if(server.isLoggedIn(tokes[2])) {
                                        sendMsg("Пользователь уже подключен");
                                        break;
                                    }
                                    String newNick = AuthService.getNickByLoginAndPass(tokes[2], tokes[3]);
                                    if (newNick != null) {
                                        sendMsg(Msg._query + Msg._authok + " " + tokes[2] + " " + newNick);
                                        login = tokes[2];
                                        nick = newNick;
                                        isAuthorized = true;
                                        myBlacklist.clear();
                                        myBlacklist.addAll(AuthService.getBlacklist(login));
                                        server.subscribe(ClientHandler.this);
                                    } else {
                                        sendMsg("Неверный логин/пароль");
                                    }
                                    break;
                                case Msg._logout:
                                    isAuthorized = false;
                                    login = "";
                                    server.unsubscribe(ClientHandler.this);
                                    break;
                                case Msg._friends:
                                    if(tokes.length < 3) break;
                                    String strFriendsInfo = AuthService.getFriendsInfo(tokes[2]);
                                    sendMsg(Msg._query + Msg._friends + " " + strFriendsInfo);
                                    break;
                                case Msg._blacklist:
                                    if(tokes.length < 4) break;
                                    AuthService.addToBlacklist(tokes[2], tokes[3]);
                                    myBlacklist.clear();
                                    myBlacklist.addAll(AuthService.getBlacklist(login));
                                    break;
                                default:
                                    System.out.println("Error query");
                            }
                        }
                        else if (str.startsWith(Msg._msginfo)) {
                            String[] msgInfo = str.split(" ", 4);
                            if(msgInfo.length > 3)
                                //server.broadcastMsg(msgInfo[1], msgInfo[2], msgInfo[3]);
                                server.prepareMsg(msgInfo[1], msgInfo[2], msgInfo[3]);
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
                    server.unsubscribe(ClientHandler.this);
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


    public String getLogin() {
        return login;
    }
    public String getNick() {
        return nick;
    }

    public boolean isInBlacklist(String login) {
        return myBlacklist.contains(login);
        //return false;
    }


}
