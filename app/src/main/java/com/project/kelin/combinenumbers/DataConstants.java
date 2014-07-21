package com.project.kelin.combinenumbers;

import java.util.Random;

/**
 * Created by kelin on 14-7-18.
 */
public class DataConstants {
    /**
     *
     */
    static int data1[][] =
           {{1, 1,1, 2, 2},
            {1, 0, 0,0, 2},
            {4, 0,0, 0, 2},
            {4, 0,0, 0, 3},
            {4, 4, 3,3, 3},


    };

    static int data2[][] =
            {{1, 1, 2, 2, 2},
            {1, 0, 0, 0, 2},
            {5, 0, 0, 0, 3},
            {5, 0, 0, 0, 3},
            {5, 4, 4, 4, 3}

    };
    static int data3[][] =
                   {{1, 1, 2, 2, 2},
                    {1, 0, 0, 0, 3},
                    {6, 0, 0, 0, 3},
                    {6, 0, 0, 0, 3},
                    {5, 5, 4, 4, 4}

            };
    static int data4[][] =
                   {{1, 1, 2, 2, 2},
                    {8, 0, 0, 0, 3},
                    {8, 0, 0, 0, 3},
                    {7, 0, 0, 0, 4},
                    {7, 6, 6, 5, 4}

            };
    static int data5[][] =
                   {{1, 1, 2, 3, 3},
                    {10, 0, 0, 0, 3},
                    {9, 0, 0, 0, 4},
                    {8, 0, 0, 0, 5},
                    {8, 7, 7, 6, 5}

            };
    static int data6[][] =
                    {{1, 1, 2, 3, 3},
                    {11, 0, 0, 0, 4},
                    {9, 0, 0, 0, 4},
                    {10, 0, 0, 0, 5},
                    {8, 8, 7, 6, 5}

            };
    static int data7[][] =
                    {{1, 2, 2, 3, 4},
                    {12, 0, 0, 0, 4},
                    {12, 0, 0, 0, 5},
                    {11, 0, 0, 0, 6},
                    {10, 9, 8, 7, 7}

            };
    static int data8[][] =
                    {{1, 2, 3, 4, 5},
                    {13, 0, 0, 0, 6},
                    {13, 0, 0, 0, 7},
                    {12, 0, 0, 0, 8},
                    {11, 11, 10, 9, 8}

            };
    static int data9[][] =
                    {{1, 2, 3, 4, 5},
                    {14, 0, 0, 0, 6},
                    {13, 0, 0, 0, 7},
                    {13, 0, 0, 0, 8},
                    {12, 11, 10, 10, 9}

            };
    static int data10[][] =
                    {{1, 2, 3, 4, 5},
                    {16, 0, 0, 0, 6},
                    {15, 0, 0, 0, 7},
                    {14, 0, 0, 0, 8},
                    {13, 12, 11, 10, 9}

            };
    static int numbers1[]={0,4,4,9,5};
    static int numbers2[]={0,4,3,4,5,7};
    static int numbers3[]={0,7,5,4,6,8,3};
    static int numbers4[]={0,12,13,10,16,12,12,9,14};
    static int numbers5[]={0,15,13,18,22,20,12,19,24,9,30};
    static int numbers6[]={0,23,23,28,32,30,22,29,28,29,30};
    static int numbers7[]={0,33,23,48,32,50,22,39,28,39,40,43};
    static int numbers8[]={0,53,43,68,52,47,42,39,28,59,30,39,30};
    static int numbers9[]={0,56,53,68,42,30,72,29,48,49,50,40,60,23,44};
    static int numbers10[]={0,53,63,68,32,70,22,69,58,49,30,80,32,100,43,60,74};

    private static int passNumberCount[]={4,5,6,8,10,11,12,13,14,16};
    public static int[] searchNumbers(int max,int n){
        switch (sPassIndex){
            case 1:return numbers1;
            case 2:return numbers2;
            case 3:return numbers3;
            case 4:return numbers4;
            case 5:return numbers5;
            case 6:return numbers6;
            case 7:return numbers7;
            case 8:return numbers8;
            case 9:return numbers9;
            case 10:return numbers10;
        }
        int[] numbers=new int[n+1];
        Random random = new Random();
        while (true) {
            for (int i=1;i<=n;i++) {
                numbers[i]=random.nextInt((int)Math.sqrt(max*2))+1;
            }
            numbers[0]=0;
            if(CombineUtils.getMaxScore(numbers)==max)
            return numbers;
        }
    }
    public static int sNumbers[];
    public static int sProjectionArray[][];
    public static int sNumbersArray[][];
    public static int sPassIndex=1;
    public static int sPassMaxScore[]={64,128,256,512,1024};
    public static String sMessage[]={"您的智商，不解释...再试试吧 ~_~;",
            "恭喜!!!智商90了...Y(^_^)Y",
            "其实，智商还是挺重要的...(*^_^*)",
            "牛掰阿！",
            "牛B阿！！",
            "智商超群了！！！",
            "非一般的存在！！！！",
            "爱因斯坦转世！！！！！",
            "神一般的存在！！！！！！",
             "你一定不是人！！！！！！！！！！！！！！！！！！"};
    public static void initData(){
        sNumbers =searchNumbers(sPassMaxScore[sPassIndex-1],passNumberCount[sPassIndex-1]);
        sProjectionArray =getProjection2Array();
        sNumbersArray =getNumbersArray();
    }
    private static int[][] getProjection2Array(){
        switch (sPassIndex){
            case 1:return data1;
            case 2:return data2;
            case 3:return data3;
            case 4:return data4;
            case 5:return data5;
            case 6:return data6;
            case 7:return data7;
            case 8:return data8;
            case 9:return data9;
            case 10:return data10;
        }
        return null;
    }
    private static int[][] getNumbersArray(){
     int[][] number2Arr=new int[sProjectionArray.length][sProjectionArray[0].length];
     for (int index=1;index<=passNumberCount[sPassIndex-1];index++) {
         for (int i = 0; i < sProjectionArray.length; i++) {
             for (int j = 0; j < sProjectionArray[i].length; j++) {
                 if (sProjectionArray[i][j] ==index){
                     number2Arr[i][j]= sNumbers[index];
                 }
             }
         }
     }
        return number2Arr;
    }
}
