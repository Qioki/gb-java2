package main.User;

import java.util.ArrayList;
import java.util.HashMap;

public class UserAccount {

    private String userName;
    private String password;

    public ArrayList<String> friendList = new ArrayList<>();
    private HashMap<String, ArrayList<UserMessage>> chats = new HashMap<>();

    public String avatar = "";

    public UserAccount(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }


    public String getUserName() { return userName; }
    public String getPassword() { return password; }


    public void addMessage(String toUser, UserMessage message) throws NullPointerException {

        getChat(toUser).add(message);
    }

    public ArrayList<UserMessage> getChat(String friendName) {

        return chats.computeIfAbsent(friendName, k -> new ArrayList<>());
    }

    public boolean isNewMessage(UserMessage message) {

        String friendName;
        if(message.fromUser.equals(userName))
            friendName = message.toUser;
        else friendName = message.fromUser;

        if(getChat(friendName).stream().noneMatch(e -> e.messageID == message.messageID)) {
            addMessage(friendName, message);
            return true;
        }
        return false;
    }
}

