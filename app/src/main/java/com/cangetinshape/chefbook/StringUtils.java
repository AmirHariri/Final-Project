package com.cangetinshape.chefbook;

import java.util.ArrayList;

/**
 * Created by Amir on 12/18/2017.
 */

public class StringUtils {

    public static String stringSeparator = "__,__";
    public static String convertArrayListToString(ArrayList<String > array){
        String str = "";
        for (int i = 0;i<array.size(); i++) {
            str = str+ array.get(i);
            // Do not append comma at the end of last element
            if(i<(array.size()-1)){
                str = str+stringSeparator;
            }
        }
        return str;
    }
    public static String[] convertStringToArray(String str){
        String[] arr = str.split(stringSeparator);
        return arr;
    }
}
