package com.honey.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.honey.dto.ListsDTO;
import com.honey.dto.StatsDTO;
import com.honey.service.AdminDashboardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
@Slf4j
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    // 1. 상단 카드용 통계 데이터 조회 💰
    @GetMapping("/stats")
    public StatsDTO getStats() {
        log.info("대쉬보드 상단 통계 데이터 조회 요청 🍯");
        return dashboardService.getStats();
    }

    // 2. 중단/하단 리스트 및 차트용 데이터 조회 📊
    @GetMapping("/lists")
    public ListsDTO getLists() {
        log.info("대쉬보드 하단 리스트 및 유저 상태 통계 조회 요청 🐝");
        return dashboardService.getLists();
    }
}
