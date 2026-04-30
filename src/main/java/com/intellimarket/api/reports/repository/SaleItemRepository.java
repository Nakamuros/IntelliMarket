package com.intellimarket.api.reports.repository;

import com.intellimarket.api.reports.model.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {

    List<SaleItem> findBySale_DateBetween(LocalDateTime startDate, LocalDateTime endDate);

}