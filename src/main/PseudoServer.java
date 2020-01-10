package main;

import main.User.*;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class PseudoServer {

    private PseudoDB db;
    private HashMap<String, PseudoSocket> connectedUsers = new HashMap<>();
    private HashMap<String, String> tokens = new HashMap<>();


    public PseudoServer() {
        db = new PseudoDB();
        initTestBots();
    }

    public String connect(PseudoSocket userSocket, String userName, String password) throws ConnectionError {

        if(db.checkPassword(userName, password)) {

            String token = generateToken();
            connectedUsers.put(token, userSocket);
            tokens.put(userName, token);

            return token;
        }
        else throw new ConnectionError();
    }
    public void close(String token) {
        connectedUsers.remove(token);
        tokens.values().removeIf(v -> v.equals(token));
    }

    private String generateToken() {
        byte[] byteToken = new byte[7];
        new Random().nextBytes(byteToken);
        return new String(byteToken, StandardCharsets.UTF_8);
    }


    public TreeMap<String, String> getFriendsInfo (String token, String userName) throws ConnectionError {

        if(isUserConnected(token)) {
            return db.getFriendsInfo(userName);
        }
        throw new ConnectionError();
    }


    public ArrayList<UserMessage> getChat (String token, String userName, String friendName) throws ConnectionError {

        if(isUserConnected(token)) {
            return db.getChat(userName, friendName);
        }
        throw new ConnectionError();
    }

    public void newMessage(String token, UserMessage message) {

        if(isUserConnected(token)) {
            if(db.addMessage(message)) {
                try {
                    // Делаю небольшую задержку перед отправкой сообщения на клиент (для реалистичности)
                    Timer t = new Timer();
                    t.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (AppMain.IsStopped) {
                                t.cancel();
                                return;
                            }
                            // Отправляю сообщение обоим клиентам, если подключены
                            connectedUsers.get(token).newMessage(message);
                            if (tokens.containsKey(message.toUser))
                                connectedUsers.get(tokens.get(message.toUser)).newMessage(message);

                            t.cancel();
                        }
                    }, 600);

                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private boolean isUserConnected(String token) {
        return connectedUsers.containsKey(token);
    }




    private String[] testUsers = {"User", "Молчаливый Бот", "Болтливый Бот"};
    private void initTestBots() {

        new TestBot(this, testUsers[1], TestBot.Character.Passive);
        new TestBot(this, testUsers[2], TestBot.Character.Active);
    }





    private class PseudoDB {

        private HashMap<String, UserAccount> userData = new HashMap<>();


        public PseudoDB() {  testInit();   }


        public boolean checkPassword(String userName, String password) {
            UserAccount ua = userData.get(userName);
            return ua != null && ua.getPassword().equals(password);
        }

        // Получить имена и url аватаров друзей
        private TreeMap<String, String> getFriendsInfo (String userName) {

            TreeMap <String, String> fi = new TreeMap<>();
            try {
                userData.get(userName).friendList.forEach(v -> fi.put(v, userData.get(v).avatar));
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            return fi;
        }
        public ArrayList<UserMessage> getChat (String userName, String friendName) throws ConnectionError {
            try {
                ArrayList<UserMessage> chat = userData.get(userName).getChat(friendName);

                return new ArrayList<>(chat.subList(Math.max(0, chat.size()-15), chat.size()));
            } catch (NullPointerException e) {
                throw new ConnectionError();
            }
        }

        public boolean addMessage(UserMessage message) {

            try {
                userData.get(message.fromUser).addMessage(message.toUser, message);
                userData.get(message.toUser).addMessage(message.fromUser, message);
            } catch (NullPointerException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }






        public void testInit() {
            UserAccount testAcc;

            for (int i = 0; i < testUsers.length; i++) {

                testAcc = new UserAccount(testUsers[i], "1234");
                ArrayList<String> fl = testAcc.friendList;
                Collections.addAll(fl, testUsers);
                fl.remove(testUsers[i]);
                testAcc.avatar = "/img/avatar" + i + ".png";

                userData.put(testUsers[i], testAcc);
            }

            addMessage(new UserMessage(testUsers[1], testUsers[0], new Date(System.currentTimeMillis()-100000000), "Не пиши мне"));
        }
    }

}