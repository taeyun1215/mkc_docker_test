package com.mck.domain.alert;

import com.mck.domain.alert.response.AlertAllResDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AlertRepo extends JpaRepository<Alert, Long> {
    @Query(value = "select id, message, type, url, date_format(created_at, '%Y-%m-%d %H:%i:%s') as create_time from alert " +
            "where 1=1 " +
            "and type in ('comment', 'reply') " +
            "and confirm = 0 " +
            "and username = :username " +
            "order by create_time desc " +
            "limit 10", nativeQuery = true)
    List<AlertAllResDto> getAlertByUsername(String username);

    @Query(value = "select * from alert " +
            "where 1=1 " +
            "and username = :id " +
            "and confirm = 0 " +
            "order by createdAt desc " +
            "limit 10", nativeQuery = true)
    Alert findByIdThenConfirmIsZero(String username);
}
