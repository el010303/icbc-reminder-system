package com.icbc.reminder.mapper;

import com.icbc.reminder.model.EmailRetryLog;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface EmailRetryMapper {
    @Insert("INSERT IGNORE INTO email_retry_log (remind_id, email, title, retry_count, status, error_msg, create_time, idempotency_key) " +
            "VALUES (#{remindId}, #{email}, #{title}, 0, 'PENDING', #{errorMsg}, #{createTime}, #{idempotencyKey})")   
    void insertRetryLog(EmailRetryLog log);

    @Select("SELECT * FROM email_retry_log WHERE status = 'PENDING' AND retry_count <= 3")
    List<EmailRetryLog> findPendingRetries();

    @Update("UPDATE email_retry_log SET retry_count = #{retryCount}, last_retry_time = #{lastRetryTime}, status = #{status}, error_msg = #{errorMsg} WHERE id = #{id}")
    void updateRetryLog(EmailRetryLog log);

    @Select("SELECT COUNT(*) FROM email_retry_log WHERE status = 'FAILED'")
    int getFailedCount();
}