package com.intellimarket.api.payments.service;

import com.intellimarket.api.payments.dto.PaymentsRequest;
import com.intellimarket.api.payments.dto.PaymentsResponse;
import com.intellimarket.api.payments.repository.PaymentsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentsService implements IPaymentsService{
    private final PaymentsRepository paymentsRepository;

    /*@Override
    @Transactional
    public PaymentsResponse createPayment(PaymentsRequest request){
        // Crear
    }*/
}
