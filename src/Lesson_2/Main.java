package Lesson_2;

import Lesson_2.MyExceptions.*;

public class Main {

    public static void main(String[] args) {

        String[][] stringMatrix = generateMatrix(4);
        //stringMatrix[2] = new String[3];  // для проверки
        //stringMatrix[2][1] = "Восемь";

        int sum = 0;
        Exception error = null;

        try {

            sum = testMatrix(stringMatrix, 4);

        } catch (NullPointerException e) {
            System.out.println("Матрица не инициализирована");
            e.printStackTrace();
            error = e;
        } catch (MyArraySizeException | MyArrayDataException e) {
            e.printStackTrace();
            error = e;
        }

        if(error != null) {
            System.out.println("Не удалось посчитать сумму.\nБыла допущена ошибка " + error.getClass().getSimpleName());
        }
        else System.out.println("Сумма всех членов матрицы ровна " + sum);
    }

    public static String[][] generateMatrix(int matrixSize) {
        if (matrixSize <= 0) return new String[0][0];

        String[][] result = new String[matrixSize][matrixSize];
        for (int i =0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                result[i][j] = Integer.toString( (int) (Math.random() * 100) );
            }
        }
        return result;
    }

    public static int testMatrix(String[][] matrix, int matrixSize) throws NullPointerException, MyArraySizeException, MyArrayDataException {

        checkMatrixSize(matrix, matrixSize);

        return getSumArrayMembers(matrix);
    }

    public static void checkMatrixSize(Object[][] matrix, int matrixSize) throws NullPointerException, MyArraySizeException {

        if(matrix.length != matrixSize) throw new MyArraySizeException("Matrix", matrix.length, matrixSize);

        for (int i = 0; i < matrix.length; i++) {
            if(matrix[i].length != matrixSize) throw new MyArraySizeException("array["+ i +"]", matrix[i].length, matrixSize);
        }
    }

    public static int getSumArrayMembers(String[][] array) throws NullPointerException, MyArrayDataException
    {
        int result = 0;
        int i = 0, j = 0;

        try {
            for (i = 0; i < array.length; i++) {
                for (j = 0; j < array[i].length; j++) {
                    result += Integer.parseInt(array[i][j]);
                }
            }
        }
        catch (NumberFormatException e) {
            throw new MyArrayDataException(array[i][j], i, j);
        }

        return result;
    }



}
