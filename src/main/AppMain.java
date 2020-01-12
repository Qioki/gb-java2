package main;
import java.util.Arrays;

public class AppMain {

    final static int size = 10000000;
    final static int h = size / 2;

    public static void main(String[] args) {
        run1();
        run2();
    }


    public static void run1() {

        float[] arr = prepareArray();

        long a = System.currentTimeMillis();

        System.out.println("Один поток:");

        longProcess(arr, 0);

        long processTime = System.currentTimeMillis() - a;

        System.out.println("Затраченное время: " + processTime + " ms\n");
    }

    public static void run2() {

        float[] arr = prepareArray();

        String resultText = "";
        long step1, step2, step3;

        long a = System.currentTimeMillis();
        System.out.println("Два потока:");


        // Разделение массива
        float[] a1 = new float[h];
        float[] a2 = new float[h];

        System.arraycopy(arr, 0, a1, 0, h);
        System.arraycopy(arr, h, a2, 0, h);

        step1 = System.currentTimeMillis() - a;
        resultText += "\nВремя разделения массива: " + step1;


        // Запуск потоков
        Thread t1 = new Thread(() -> longProcess(a1, 0));
        Thread t2 = new Thread(() -> longProcess(a2, h));
        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        step2 = System.currentTimeMillis() - a;
        resultText += "\nВремя работы двух потоков: " + (step2 - step1) + " ms";


        // Склеивание массивов
        System.arraycopy(a1, 0, arr, 0, h);
        System.arraycopy(a2, 0, arr, h, h);

        step3 = System.currentTimeMillis() - a;
        resultText += "\nВремя склеивания массивов: " + (step3 - step2) + " ms";

        resultText = "Затраченное время: " + step3 + " ms" + resultText;

        System.out.println(resultText);
    }

    private static void longProcess(float[] arr, int start){
        for (int i = 0; i < arr.length; i++)
            arr[i] = (float)(arr[i] * Math.sin(0.2f + (start + i) / 5f) * Math.cos(0.2f + (start + i) / 5f) * Math.cos(0.4f + (start + i) / 2f));
    }

    private static float[] prepareArray() {
        float[] arr = new float[size];
        Arrays.fill(arr, 1f);

        return arr;
    }
}