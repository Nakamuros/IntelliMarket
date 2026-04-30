package com.intellimarket.api.reports.service;

import com.intellimarket.api.reports.dto.DashboardResponse;
import java.time.LocalDateTime;

public interface IDashboardService {

    DashboardResponse getSalesReport(LocalDateTime startDate, LocalDateTime endDate);

}