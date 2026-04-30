package com.intellimarket.api.reports.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

public record DashboardResponse(

        @JsonProperty("Ingresos_totales")
        Double totalRevenue,

        @JsonProperty("Recuento_total_de_ventas")
        Integer totalSalesCount,

        @JsonProperty("Top_vendido")
        String topSellingProduct

) {}
