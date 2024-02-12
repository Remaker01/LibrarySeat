package com.libraryseat.utils;

public class VerifyUtil {
    public static final int PASS_MIN_LEN = 8,PASS_MAX_LEN = 30;
    @Deprecated
    public static final String PASSWORD_REQUIREMENT = String.format("密码长度不能小于%d位或大于%d位，且至少含3个不同字符",PASS_MIN_LEN,PASS_MAX_LEN);
    public static boolean verifyTimestamp(String timestamp) {
        long cur = System.currentTimeMillis();
        try {
            long tm = Long.parseLong(timestamp);
            cur /= 1000L;
            return (cur >= tm-30L&&cur - tm <= 180L); //防止客户端时间偏快
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public static boolean verifyPassword(String password) {
        if (password == null)
            return false;
        int len = password.length();
        if (len != 32)
            return false;
        for (int i = 0; i < len; i++) {
            char ch = password.charAt(i);
            if (ch > 255)
                return false;
            if (!Character.isLetter(ch) && !Character.isDigit(ch))
                return false;
        }
        return true;
    }

    public static boolean verifyPhone(String phone){
        if (phone == null||phone.length() != 11)
            return false;
        try {
            long number = Long.parseLong(phone);
            return number > 1e10+2e9&&number < 1e11;
        } catch (NumberFormatException e){
            return false;
        }
    }

    public static boolean verifyNonEmptyStrings(String... strings) {
        for(String s:strings) {
            if(s == null||s.isEmpty())
                return false;
        }
        return true;
    }
}