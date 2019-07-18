package com.example.goforlunch.utils;

public class ConvertDate {

    //Convert Date by language
    public static String convertDate(String date, String language) {
        int hour = Integer.parseInt(date.substring(0, 2));
        String mn = date.substring(2);
        if (language.equals("English")) {
            if (hour > 12) {
                return (hour - 12) + "." + mn + "pm";
            } else if (hour == 12) {
                return "12" + "." + mn + "pm";
            } else if (hour == 0) {
                return "12" + "." + mn + "am";
            } else {
                return hour + "." + mn + "am";
            }
        } else
            return hour + "h" + mn;
    }
}
