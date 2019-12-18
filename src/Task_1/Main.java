package Task_1;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Main {

    public static void main(String[] args) {

        String[] arrStrings = "boolean byte char short char short int long short int long float double".split(" ");

        Map<String, Integer> map = new HashMap<>();

        for (String str : arrStrings) {
            int count = map.getOrDefault(str, 0);
            map.put(str, count+1);
        }


        Set<String> uniqueList = map.keySet();
        System.out.println("Список уникальных слов:\n" + uniqueList);


        System.out.println("Считаем сколько раз встречается каждое слово:");
        map.forEach((k, v) -> System.out.println("Слово \"" + k + "\" - " + v));
    }

}
