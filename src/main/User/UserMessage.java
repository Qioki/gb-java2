package main.User;

import java.util.Date;
import java.util.Random;

public class UserMessage {

    final public String fromUser;
    final public String toUser;
    final public Date date;
    final public String message;
    final public int messageID;


    public UserMessage(String fromUser, String toUser, String message) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.date = new Date(System.currentTimeMillis());
        this.message = message;
        messageID = new Random().nextInt();
    }
    public UserMessage(String fromUser, String toUser, Date date, String message) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.date = date;
        this.message = message;
        messageID = new Random().nextInt();
    }

}
