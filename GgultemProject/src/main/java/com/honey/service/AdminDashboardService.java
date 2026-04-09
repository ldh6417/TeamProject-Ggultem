package com.honey.service;

import com.honey.dto.ListsDTO;
import com.honey.dto.StatsDTO;

public interface AdminDashboardService {

	StatsDTO getStats();

	ListsDTO getLists();

}
