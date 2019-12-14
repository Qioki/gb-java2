package Lesson_2.MyExceptions;

public class MyArrayDataException extends Exception {

    public MyArrayDataException(String value, int indexX, int indexY) {
        super("Ошибка при конвертации! В array[" + indexX +"][" + indexY +"] значение \"" + value + "\"");
    }
}
