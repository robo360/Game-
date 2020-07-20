package com.example.game.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatorsUtil {
    public static boolean checkTimePattern(String Time){
        Pattern p = Pattern.compile("\\d{2}/\\d{2}/\\d{4}\\h\\d{2}:\\d{2}");
//        Pattern p = Pattern.compile("\\d{2}/\\d{2}/\\d{4}\\h\\w{3}");
        Matcher m = p.matcher(Time);
        boolean b = m.matches();
        return  b;
    }
}
