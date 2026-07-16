package com.icbc.reminder.mapper;

import com.icbc.reminder.model.AuditLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface AuditLogMapper {
    
    @Insert("INSERT INTO audit_log (operator, action, target_id, target_type, detail, result, create_time) " +
            "VALUES (#{operator}, #{action}, #{targetId}, #{targetType}, #{detail}, #{result}, #{createTime})")
    void insertAuditLog(AuditLog log);

    @Select("SELECT * FROM audit_log ORDER BY id DESC LIMIT 100")
    List<AuditLog> getRecentLogs(); 
}
