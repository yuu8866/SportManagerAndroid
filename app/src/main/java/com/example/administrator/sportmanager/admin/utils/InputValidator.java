package com.example.administrator.sportmanager.admin.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public final class InputValidator {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^1\\d{10}$");
    private static final SimpleDateFormat BIRTHDAY_FORMAT =
            new SimpleDateFormat("yyyy.MM.dd", Locale.CHINA);

    static {
        // 关键：禁止“2005.02.30”这种自动纠正为3月1日
        BIRTHDAY_FORMAT.setLenient(false);
    }

    private InputValidator() {}

    public static boolean isNotBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    /** 密码：不少于6位（允许字母/数字/特殊字符） */
    public static boolean isPasswordValid(String s) {
        return s != null && s.length() >= 6;
    }

    /** 性别：只能 男/女 */
    public static boolean isGenderValid(String s) {
        if (s == null) return false;
        String t = s.trim();
        return "男".equals(t) || "女".equals(t);
    }

    /** 手机号：11位且1开头 */
    public static boolean isPhoneValid(String s) {
        if (s == null) return false;
        return PHONE_PATTERN.matcher(s.trim()).matches();
    }

    /** 生日：格式必须 yyyy.MM.dd 且为真实日期 */
    public static boolean isBirthdayFormatValid(String s) {
        return parseBirthday(s) != null;
    }

    /** 生日：必须早于今天（严格小于今天，不允许今天/未来） */
    public static boolean isBirthdayBeforeToday(String s) {
        Date birth = parseBirthday(s);
        if (birth == null) return false;

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        return birth.before(today.getTime());
    }

    private static Date parseBirthday(String s) {
        if (s == null) return null;
        String t = s.trim();
        if (t.isEmpty()) return null;

        // SimpleDateFormat非线程安全，这里简单同步一下最稳
        synchronized (BIRTHDAY_FORMAT) {
            try {
                return BIRTHDAY_FORMAT.parse(t);
            } catch (ParseException e) {
                return null;
            }
        }
    }
}
