package com.example.administrator.sportmanager.util;
import com.example.administrator.sportmanager.admin.utils.InputValidator;

import org.junit.Test;
import static org.junit.Assert.*;

public class InputValidatorTest {

    @Test
    public void gender_should_only_be_male_or_female() {
        assertTrue(InputValidator.isGenderValid("男"));
        assertTrue(InputValidator.isGenderValid("女"));
        assertFalse(InputValidator.isGenderValid(""));
        assertFalse(InputValidator.isGenderValid("abc"));
        assertFalse(InputValidator.isGenderValid(null));
    }

    @Test
    public void phone_should_be_11_digits_and_start_with_1() {
        assertTrue(InputValidator.isPhoneValid("13800138000"));
        assertFalse(InputValidator.isPhoneValid("23800138000"));   // 非1开头
        assertFalse(InputValidator.isPhoneValid("1380013800"));    // 10位
        assertFalse(InputValidator.isPhoneValid("138001380000"));  // 12位
        assertFalse(InputValidator.isPhoneValid("13800A38000"));   // 含字母
        assertFalse(InputValidator.isPhoneValid(null));
    }

    @Test
    public void birthday_should_be_real_date_in_yyyy_MM_dd() {
        assertTrue(InputValidator.isBirthdayValid("2005.01.01"));
        assertTrue(InputValidator.isBirthdayValid("2004.02.29"));  // 闰年
        assertFalse(InputValidator.isBirthdayValid("2005.02.30")); // 不存在日期
        assertFalse(InputValidator.isBirthdayValid("2005-01-01")); // 格式不对
        assertFalse(InputValidator.isBirthdayValid(""));
        assertFalse(InputValidator.isBirthdayValid(null));
    }

    @Test
    public void password_should_not_be_empty() {
        assertTrue(InputValidator.isPasswordNotEmpty("Abc@123"));
        assertFalse(InputValidator.isPasswordNotEmpty(""));
        assertFalse(InputValidator.isPasswordNotEmpty("   "));
        assertFalse(InputValidator.isPasswordNotEmpty(null));
    }

    @Test
    public void register_password_should_be_at_least_6_digits() {
        assertTrue(InputValidator.isPasswordAtLeast6Digits("123456"));
        assertTrue(InputValidator.isPasswordAtLeast6Digits("1234567"));
        assertFalse(InputValidator.isPasswordAtLeast6Digits("12345"));
        assertFalse(InputValidator.isPasswordAtLeast6Digits("abc123"));
    }
}
