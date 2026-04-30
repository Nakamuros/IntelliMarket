package com.intellimarket.api.reports.service;

import com.intellimarket.api.reports.dto.DashboardResponse;
import com.intellimarket.api.reports.exception.InvalidDateRangeException;
import com.intellimarket.api.reports.model.Sale;
import com.intellimarket.api.reports.model.SaleItem;
import com.intellimarket.api.reports.repository.SaleItemRepository;
import com.intellimarket.api.reports.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class DashboardService implements IDashboardService {

    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    @Override
    @Transactional(readOnly = true)
    public DashboardResponse getSalesReport(LocalDateTime startDate, LocalDateTime endDate) {

        if (startDate.isAfter(endDate)) {
            throw new InvalidDateRangeException(startDate.toString(), endDate.toString());
        }

        List<Sale> sales = saleRepository.findByDateBetween(startDate, endDate);

        Double totalRevenue = 0.0;
        for (Sale sale : sales) {
            totalRevenue += sale.getTotalAmount();
        }

        Integer totalSalesCount = sales.size();

        List<SaleItem> items = saleItemRepository.findBySale_DateBetween(startDate, endDate);

        Map<Long, Integer> scoreboard = new HashMap<>();

        for (SaleItem item : items) {
            Long productId = item.getProductId();
            Integer quantity = item.getQuantity();
            scoreboard.put(productId, scoreboard.getOrDefault(productId, 0) + quantity);
        }

        Long topProductId = null;
        int maxQuantity = 0;

        for (Map.Entry<Long, Integer> entry : scoreboard.entrySet()) {
            if (entry.getValue() > maxQuantity) {
                maxQuantity = entry.getValue();
                topProductId = entry.getKey();
            }
        }

        String topSellingProduct;
        if (topProductId != null) {
            topSellingProduct = "Producto ID #" + topProductId + " (Vendidos: " + maxQuantity + ")";
        } else {
            topSellingProduct = "No hay ventas en estas fechas";
        }

        return new DashboardResponse(totalRevenue, totalSalesCount, topSellingProduct);
    }
}