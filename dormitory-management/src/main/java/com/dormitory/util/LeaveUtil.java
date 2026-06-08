package com.dormitory.util;

/**
 * 请假类型与状态显示工具
 */
public final class LeaveUtil {

    private LeaveUtil() {
    }

    public static String getTypeName(String leaveType) {
        if (leaveType == null) {
            return "未知";
        }
        if ("late".equals(leaveType)) {
            return "晚归";
        }
        if ("overnight".equals(leaveType)) {
            return "外宿";
        }
        if ("sick".equals(leaveType)) {
            return "病假";
        }
        if ("public".equals(leaveType)) {
            return "公假";
        }
        if ("event".equals(leaveType)) {
            return "事假";
        }
        if ("other".equals(leaveType)) {
            return "其他";
        }
        return leaveType;
    }

    public static String getStatusName(String status) {
        if ("pending".equals(status)) {
            return "待审批";
        }
        if ("approved".equals(status)) {
            return "已通过";
        }
        if ("rejected".equals(status)) {
            return "已拒绝";
        }
        return status;
    }

    public static String getStatusBadgeClass(String status) {
        if ("pending".equals(status)) {
            return "badge-warning";
        }
        if ("approved".equals(status)) {
            return "badge-success";
        }
        if ("rejected".equals(status)) {
            return "badge-danger";
        }
        return "badge-info";
    }
}
