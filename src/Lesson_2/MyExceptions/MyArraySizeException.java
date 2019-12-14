package Lesson_2.MyExceptions;

public class MyArraySizeException extends Exception {

    public MyArraySizeException(String msg) {
        super(msg);
    }
    public MyArraySizeException(int haveSize, int needSize) {
        this("array", haveSize, needSize);
    }
    public MyArraySizeException(String arrayName, int haveSize, int needSize) {
        super(arrayName + ". Неправильный размер массива! Размер: " + haveSize + ", заданный размер: " + needSize);
    }
}
