package com.icbc.reminder.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DashboardMapper {

    @Select("SELECT COUNT(*) FROM tjfh_INM_REMIND WHERE DELETE_FLAG='0'")
    int getTotalReminders();

    @Select("SELECT COUNT(*) FROM tjfh_INM_REMIND_LIST WHERE handle_flag='0' AND delete_flag='0'")
    int getPendingReminders();

    @Select("SELECT COUNT(*) FROM tjfh_INM_REMIND_LIST WHERE handle_flag='1' AND delete_flag='0'")
    int getCompletedReminders();

    @Select("SELECT COUNT(*) FROM ctp_user WHERE status='1'")
    int getTotalUsers();

    @Select("SELECT COUNT(*) FROM tjfh_INM_REMIND_LIST WHERE DATE(send_date) = CURDATE()")
    int getTodayEmailsSent();

    @Select("SELECT COUNT(*) FROM tjfh_INM_REMIND WHERE DATE_FORMAT(COMMIT_DATE, '%Y-%m') = DATE_FORMAT(CURDATE(), '%Y-%m')")
    int getMonthlyNewReminders();

    @Select("SELECT CASE WHEN COUNT(*) = 0 THEN 0 " +
        "ELSE ROUND(SUM(CASE WHEN status = 'SUCCESS' THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 1) END " +
        "FROM email_retry_log")
    double getSendSuccessRate();

    @Select("SELECT COUNT(*) FROM email_retry_log WHERE status = 'FAILED'")
    int getFailedEmails();
}