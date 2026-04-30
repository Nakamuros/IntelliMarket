package com.intellimarket.api.reports.repository;

import com.intellimarket.api.reports.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    List<Sale> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate);

}
