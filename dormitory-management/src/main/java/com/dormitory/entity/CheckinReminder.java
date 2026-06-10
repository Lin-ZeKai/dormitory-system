package com.dormitory.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 管理员向学生发出的签到提醒
 */
public class CheckinReminder implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer userId;
    private Date remindDate;
    private String message;
    private String adminName;
    private boolean read;
    private Date createTime;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Date getRemindDate() { return remindDate; }
    public void setRemindDate(Date remindDate) { this.remindDate = remindDate; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getAdminName() { return adminName; }
    public void setAdminName(String adminName) { this.adminName = adminName; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
