package Task_2;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {

        Phonebook phonebook = new Phonebook();

        phonebook.add("Вася", 176546546L);
        phonebook.add("Вася", 243343426L);


        System.out.println(phonebook.get("Вася"));

        System.out.println(Arrays.toString(phonebook.getArray("Вася")));
    }
}