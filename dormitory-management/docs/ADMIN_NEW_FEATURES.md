# 管理员扩展功能说明（新增模块）

> 本次仅**新增**文件，未修改任何已有源码。原有 `/admin/home`、`/announcements` 等功能保持不变。

## 新增访问地址

| 功能 | 地址 | 说明 |
|------|------|------|
| 学生管理 | `/admin/students` | 列表、添加、修改、删除 |
| 考勤管理 | `/admin/attendance` | 全部签到记录，按日期/学生筛选 |
| 请假审核 | `/admin/leaves` | 待审/已审 Tab，通过、驳回（填原因） |
| 公告管理（完整） | `/admin/announcements` | 发布、**修改**、删除 |
| 统计中心 | `/admin/stats` | 今日数据 + Chart.js 图表 |

登录管理员账号后，直接访问上述 URL，或使用新侧边栏 `includes/admin-shell-sidebar.jsp`。

## 功能对照

| 需求 | 原有 | 新增 |
|------|------|------|
| 学生 CRUD | 无 | `/admin/students` |
| 考勤列表筛选 | 仅首页数字 | `/admin/attendance` |
| 请假审核 | `/admin/home` 已有 | `/admin/leaves` 独立页 |
| 公告发布/删除 | `/announcements` 已有 | `/admin/announcements` 增加**修改** |
| 统计图表 | 首页 4 卡片 | `/admin/stats` 图表 |

## 新增文件清单

### Java
- `dao/admin/AdminStudentDao.java`
- `dao/admin/AdminAttendanceQueryDao.java`
- `dao/admin/AdminAnnouncementManageDao.java`
- `dao/admin/AdminStatsDao.java`
- `servlet/admin/AdminServletSupport.java`
- `servlet/admin/AdminStudentServlet.java`
- `servlet/admin/AdminAttendanceServlet.java`
- `servlet/admin/AdminLeaveManageServlet.java`
- `servlet/admin/AdminStatsServlet.java`
- `servlet/admin/AdminAnnouncementManageServlet.java`

### 前端
- `webapp/admin/students.jsp`
- `webapp/admin/attendance.jsp`
- `webapp/admin/leaves.jsp`
- `webapp/admin/stats.jsp`
- `webapp/admin/announcements-manage.jsp`
- `webapp/includes/admin-shell-sidebar.jsp`（完整左侧菜单）
- `webapp/css/admin-pages.css`

## 使用步骤

1. IDEA **Rebuild Project**
2. 重启 Tomcat
3. 管理员登录后访问例如：`http://localhost:8081/dormitory-management/admin/students`

## UI 说明

- 蓝白校园风，复用 `common.css`
- 新页面额外引入 `admin-pages.css`
- 左侧菜单 + 右侧内容区
- 每页底部预留 **数据展示扩展区**（虚线框），便于后续接入导出、报表等

## 可选后续（需改已有文件）

若希望旧版 `/admin/home` 也显示完整菜单，可将 `admin/index.jsp` 中的 `admin-sidebar.jsp` 替换为 `admin-shell-sidebar.jsp`（本次未改动）。
