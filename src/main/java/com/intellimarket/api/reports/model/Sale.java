package com.intellimarket.api.reports.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Sale {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sale_date", nullable = false)
    private LocalDateTime date;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @Column(name = "payment_method", nullable = false, length = 50)
    private String paymentMethod;
}