package com.dormitory.util;

import java.util.Calendar;
import java.util.Date;

/**
 * 签到时段校验工具
 * <p>
 * 修改下方常量即可调整全局签到开放时间。
 * </p>
 */
public final class CheckinTimeUtil {

    /** 签到开始：时 */
    private static final int START_HOUR = 8;
    /** 签到开始：分 */
    private static final int START_MINUTE = 20;
    /** 签到结束：时（该时刻起不可签到，即结束时间为开区间） */
    private static final int END_HOUR = 20;
    /** 签到结束：分 */
    private static final int END_MINUTE = 0;

    private CheckinTimeUtil() {
    }

    /**
     * 判断当前时间是否在签到时段内
     */
    public static boolean isWithinWindow(Date date) {
        int minutes = toMinutes(date);
        return minutes >= getStartMinutes() && minutes < getEndMinutes();
    }

    /**
     * 计算签到状态：时段内签记为 normal
     */
    public static String resolveStatus(Date date) {
        return "normal";
    }

    public static String getWindowText() {
        return String.format("%02d:%02d - %02d:%02d", START_HOUR, START_MINUTE, END_HOUR, END_MINUTE);
    }

    public static int getStartHour() {
        return START_HOUR;
    }

    public static int getStartMinute() {
        return START_MINUTE;
    }

    public static int getEndHour() {
        return END_HOUR;
    }

    public static int getEndMinute() {
        return END_MINUTE;
    }

    private static int getStartMinutes() {
        return START_HOUR * 60 + START_MINUTE;
    }

    private static int getEndMinutes() {
        return END_HOUR * 60 + END_MINUTE;
    }

    private static int toMinutes(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);
    }
}
