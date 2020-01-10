package main.User;

import main.AppMain;
import main.ConnectionError;
import main.PseudoServer;
import main.PseudoSocket;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class TestBot implements PseudoSocket {

    private PseudoServer server;
    private String token = "";

    private String userName;
    private String friendNow = "User";


    Random random = new Random();
    private String[] textMessages = {
            "ObjectOutput — интерфейс стрима для “ручной” сериализации объектов. \nОсновные методы writeObject(объект) — запись объекта в \nстрим,write(...) — запись массива, flush() — сохранение \nстрима. Основная реализация ObjectOutputStream",
            "ObjectInput — интерфейс стрима для ручного восстановления объектов \nпосле сериализации. Основные методы readObject() — чтение\nобъектов, skip — пропустить n байт. Основная реализация \nObjectInputStream.",
            "Serializable — интерфейс, при котором Object Serialization \nавтоматически сохраняет и восстанавливает состояние объектов.",
            "Externalizable — интерфейс, при котором Object Serialization \nделегирует функции сохранения и восстановления объектов функциям, \nопределенных программистами. Externalizable расширяет \nинтерфейс Serializable",
            "How can we make sure main() is the last thread to finish in \nJava Program?",
            "Как thread могут взаимодействовать с друг другом?",
            "Why thread communication methods wait(), notify() and \nnotifyAll() are in Object class?",
            "Why wait(), notify() and notifyAll() methods have to \nbe called from synchronized method or block?",
            "Почему Thread sleep() и yield() методы статические?",
            "Как обеспечить thread safety в Java?",
            "Что такое volatile keyword в Java",
            "Что предпочтительнее Synchronized метод или Synchronized блок?",
            "Что такое ThreadLocal?",
            "Что такое Thread Group? Стоит ли их использовать?",
            "Что такое Java Thread Dump, Как получить Java Thread dump программы?",
            "Что такое Java Timer Class? Как запустить task после \nопределенного интервала времени?",
            "Что такое Thread Pool? Как создать Thread Pool в Java?",
            "Что произойдет если не override метод run() у класса Thread ?",
    };

    public enum Character {
        Active,
        Passive
    }
    private Character character;



    public TestBot(PseudoServer server, String userName, Character character) {

        this.server = server;
        this.userName = userName;
        this.character = character;


        try {
            token = server.connect(this, userName, "1234");
        } catch (ConnectionError connectionError) {
            connectionError.printStackTrace();
        }

        if(this.character == Character.Passive) return;



        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(AppMain.IsStopped) {
                    t.cancel();
                    return;
                }
                if(AppMain.IsLoggedIn)
                    server.newMessage(token, new UserMessage(userName, friendNow, textMessages[random.nextInt(textMessages.length)]));
            }
        }, 2000, 5000);
    }

    @Override
    public void newMessage(UserMessage message) {

        if(message.fromUser.equals(userName)) return;


        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override public void run() {
                if(AppMain.IsStopped) {
                    t.cancel();
                    return;
                }

                if(character == Character.Passive)
                    server.newMessage(token, new UserMessage(userName, message.fromUser, "Отстань"));
                else if(character == Character.Active)
                    server.newMessage(token, new UserMessage(userName, message.fromUser, textMessages[random.nextInt(textMessages.length)]));

                t.cancel();
            }
        }, 1500);


    }


}
