package com.intellimarket.api.reports.exception;

public class InvalidDateRangeException extends RuntimeException {
    public InvalidDateRangeException(String startDate, String endDate) {
        super("El rango de fechas es inválido: La fecha inicial (" + startDate + ") no puede ser posterior a la fecha final (" + endDate + ")");
    }
}