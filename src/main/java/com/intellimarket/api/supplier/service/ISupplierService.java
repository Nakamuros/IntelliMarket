package com.intellimarket.api.supplier.service;

import com.intellimarket.api.supplier.dto.ProviderRequestDTO;
import com.intellimarket.api.supplier.dto.ProviderResponseDTO;
import java.util.List;

public interface ISupplierService {
    List<ProviderResponseDTO> getAllProviders();
    ProviderResponseDTO getProviderById(Long id);
    ProviderResponseDTO createProvider(ProviderRequestDTO request);
    ProviderResponseDTO updateProvider(Long id, ProviderRequestDTO request);
    void deleteProvider(Long id);
}