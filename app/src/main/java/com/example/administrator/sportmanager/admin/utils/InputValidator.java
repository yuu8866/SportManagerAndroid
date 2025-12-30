package com.example.administrator.sportmanager.admin.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * 注册 + 个人信息修改 共用校验
 * 生日格式固定：yyyy.MM.dd（严格校验，不允许 2005.02.30）
 */
public class InputValidator {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^1\\d{10}$");
    private static final Pattern BIRTHDAY_PATTERN = Pattern.compile("^\\d{4}\\.\\d{2}\\.\\d{2}$");

    private InputValidator() {}

    public static boolean isUsernameValid(String username) {
        return username != null && username.trim().length() > 0;
    }

    /** 你注册页现在要求：至少6位数字；个人信息修改目前没校验，这里按“非空”做基础校验 */
    public static boolean isPasswordNotEmpty(String password) {
        return password != null && password.trim().length() > 0;
    }

    /** 注册页要求至少6位数字时可以用这个 */
    public static boolean isPasswordAtLeast6Digits(String password) {
        if (password == null) return false;
        return password.trim().matches("\\d{6,}");
    }

    public static boolean isNameValid(String name) {
        return name != null && name.trim().length() > 0;
    }

    /** 性别只能 男 / 女 */
    public static boolean isGenderValid(String gender) {
        if (gender == null) return false;
        String g = gender.trim();
        return "男".equals(g) || "女".equals(g);
    }

    /** 手机号必须 11 位且 1 开头 */
    public static boolean isPhoneValid(String phone) {
        if (phone == null) return false;
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /** 生日：yyyy.MM.dd 且必须真实存在日期 */
    public static boolean isBirthdayValid(String birthday) {
        if (birthday == null) return false;
        String b = birthday.trim();
        if (b.isEmpty()) return false;
        if (!BIRTHDAY_PATTERN.matcher(b).matches()) return false;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        sdf.setLenient(false);
        try {
            sdf.parse(b);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /** 生日不能是未来日期 */
    public static boolean isBirthdayNotFuture(String birthday) {
        if (!isBirthdayValid(birthday)) return false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        sdf.setLenient(false);
        try {
            Date input = sdf.parse(birthday.trim());
            Date today = sdf.parse(sdf.format(new Date()));
            return !input.after(today);
        } catch (ParseException e) {
            return false;
        }
    }
}
