package com.dormitory.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户实体类（Model 层）
 * <p>
 * 对应数据库表 t_user，用于在各层之间传递用户数据。
 * 登录成功后，该对象会被存入 Session 的 loginUser 属性中。
 * </p>
 */
public class User implements Serializable {

    /** 序列化版本号，保证对象可存入 Session */
    private static final long serialVersionUID = 1L;

    /** 用户主键 ID */
    private Integer id;
    /** 登录用户名（学号），唯一 */
    private String username;
    /** 手机号，唯一，可用于登录 */
    private String phone;
    /** 登录密码（骨架阶段为明文，后续可改为加密存储） */
    private String password;
    /** 真实姓名，用于页面展示 */
    private String realName;
    /** 用户角色：student（学生）/ admin（管理员） */
    private String role;
    /** 宿舍号 */
    private String dormNo;
    /** 账号创建时间 */
    private Date createTime;

    public User() {
    }

    public User(Integer id, String username, String password, String realName, String role, Date createTime) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.realName = realName;
        this.role = role;
        this.createTime = createTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDormNo() {
        return dormNo;
    }

    public void setDormNo(String dormNo) {
        this.dormNo = dormNo;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
