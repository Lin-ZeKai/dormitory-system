package com.dormitory.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 管理员查看：某日期下每位学生的考勤概况
 */
public class StudentAttendanceOverview implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer userId;
    private String username;
    private String phone;
    private String realName;
    private String dormNo;
    private Date checkTime;
    /** 签到状态：normal / late；未签到时为 null */
    private String status;

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }

    public String getDormNo() { return dormNo; }
    public void setDormNo(String dormNo) { this.dormNo = dormNo; }

    public Date getCheckTime() { return checkTime; }
    public void setCheckTime(Date checkTime) { this.checkTime = checkTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isCheckedIn() {
        return status != null && !status.isEmpty();
    }
}
