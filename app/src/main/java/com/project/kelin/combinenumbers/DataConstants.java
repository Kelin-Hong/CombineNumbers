package com.project.kelin.combinenumbers;

import android.content.Context;

import java.util.Random;

/**
 * Created by kelin on 14-7-18.
 */
public class DataConstants {
    public static int sNumbers[];
    public static int sProjectionArray[][];
    public static int sNumbersArray[][];
    public static int sPassIndex = 1;
    private static int passNumberCount[] = {4, 5, 6, 8, 10, 11, 12, 13, 14, 16};
    public static String sFailureMessage[];
    public static String sSuccessMessage[];

    public static int[] searchNumbers(Context context) {
        String numberStr = context.getResources().getStringArray(R.array.game_data)[sPassIndex - 1];
        String[] numberStrArr = numberStr.substring(1, numberStr.length() - 1).split(", ");
        int numbers[] = new int[numberStrArr.length];
        for (int i = 0; i < numberStrArr.length; i++) {
            numbers[i] = Integer.parseInt(numberStrArr[i]);
        }
        return numbers;
    }


    public static void initData(Context context) {
        sFailureMessage = context.getResources().getStringArray(R.array.failure_message_array);
        sSuccessMessage = context.getResources().getStringArray(R.array.success_message_array);
        sNumbers = searchNumbers(context);
        sProjectionArray = getProjection2Array();
        sNumbersArray = getNumbersArray();
    }

    private static int[][] getProjection2Array() {
        int avg = 16 / (sNumbers.length - 1);
        int remainder = 16 % (sNumbers.length - 1);
        Random random = new Random();
        int projection[][] = new int[5][5];
        boolean b[] = new boolean[sNumbers.length];
        int p = 0, q = 0, n = 0;
        while (n < remainder) {
            int x = random.nextInt(sNumbers.length);
            if (!b[x] && x != 0) {
                b[x] = true;
                n++;
            }
        }
        for (int i = 1; i <= sNumbers.length - 1; i++) {
            for (int j = 1; j <= (b[i] ? avg + 1 : avg); j++) {
                projection[p][q] = i;
                if (p == 0 && q < 4) {
                    q++;
                } else if (q == 4 && p < 4) {
                    p++;
                } else if (p == 4 && q > 0) {
                    q--;
                } else if (q == 0 && p > 0) {
                    p--;
                }
            }
        }
        return projection;
    }

    private static int[][] getNumbersArray() {
        int[][] number2Arr = new int[sProjectionArray.length][sProjectionArray[0].length];
        for (int index = 1; index <= passNumberCount[sPassIndex - 1]; index++) {
            for (int i = 0; i < sProjectionArray.length; i++) {
                for (int j = 0; j < sProjectionArray[i].length; j++) {
                    if (sProjectionArray[i][j] == index) {
                        number2Arr[i][j] = sNumbers[index];
                    }
                }
            }
        }
        return number2Arr;
    }
}
