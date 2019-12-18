package Task_2;

import java.util.ArrayList;
import java.util.HashMap;

public class Phonebook {

    private HashMap<String, ArrayList<Long>> contacts = new HashMap<>();



    public void add(String name, long number) {

        ArrayList<Long> numbers = contacts.computeIfAbsent(name, k -> new ArrayList<>());

        if(!numbers.contains(number))
            numbers.add(number);
    }


    public String get(String name) {

        ArrayList<Long> numbers = contacts.get(name);
        if(numbers != null) {
            return numbers.toString();
        }
        return "";
    }


    public Long[] getArray(String name) {
        ArrayList<Long> numbers = contacts.get(name);
        if(numbers != null) {
            return numbers.toArray(new Long[0]);
        }
        return new Long[0] ;
    }
}
