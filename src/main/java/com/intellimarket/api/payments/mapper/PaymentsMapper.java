package com.intellimarket.api.payments.mapper;

import com.intellimarket.api.order.model.Order;
import com.intellimarket.api.payments.dto.PaymentsResponse;
import com.intellimarket.api.payments.model.Payments;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel="spring")
public interface PaymentsMapper {
    @Mapping(target="id", source="payments.id")
    @Mapping(target="externalReference", source="payments.externalReference")
    @Mapping(target="orderId", source="order.id")
    @Mapping(target="method", source="payments.method")
    @Mapping(target="status", source="payments.status")
    @Mapping(target="createdAt", source="payments.createdAt")
    PaymentsResponse toResponse(Payments payments);
}
