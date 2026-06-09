package com.dormitory.util;

import java.util.regex.Pattern;

/**
 * 学号、手机号等格式校验（学校规定学号为 14 位数字）
 */
public final class ValidationUtil {

    /** 14 位学号 */
    public static final Pattern STUDENT_ID = Pattern.compile("^\\d{14}$");

    /** 中国大陆 11 位手机号 */
    public static final Pattern PHONE = Pattern.compile("^1[3-9]\\d{9}$");

    public static final String USERNAME_MESSAGE = "学号须为14位数字，或使用11位手机号";
    public static final String PHONE_MESSAGE = "请输入正确的11位手机号";

    private ValidationUtil() {
    }

    public static boolean isStudentId(String value) {
        return value != null && STUDENT_ID.matcher(value.trim()).matches();
    }

    public static boolean isPhone(String value) {
        return value != null && PHONE.matcher(value.trim()).matches();
    }

    /** 登录账号：14 位学号或手机号 */
    public static boolean isUsername(String value) {
        String v = value == null ? "" : value.trim();
        return isStudentId(v) || isPhone(v);
    }
}
