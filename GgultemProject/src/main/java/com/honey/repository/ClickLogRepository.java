package com.honey.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.honey.domain.ClickLog;

public interface ClickLogRepository extends JpaRepository<ClickLog, Long> {

}
