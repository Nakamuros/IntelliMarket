package com.intellimarket.api.supplier.service;

import com.intellimarket.api.supplier.dto.ProviderRequestDTO;
import com.intellimarket.api.supplier.dto.ProviderResponseDTO;
import com.intellimarket.api.supplier.mapper.ProviderMapper;
import com.intellimarket.api.supplier.model.Provider;
import com.intellimarket.api.supplier.repository.ProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements ISupplierService {

    private final ProviderRepository providerRepository;
    private final ProviderMapper providerMapper;

    @Override
    public List<ProviderResponseDTO> getAllProviders() {
        return providerRepository.findAll().stream()
                .map(providerMapper::providerToProviderResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProviderResponseDTO getProviderById(Long id) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
        return providerMapper.providerToProviderResponseDTO(provider);
    }

    @Override
    public ProviderResponseDTO createProvider(ProviderRequestDTO request) {
        Provider provider = providerMapper.providerRequestDTOToProvider(request);
        Provider savedProvider = providerRepository.save(provider);
        return providerMapper.providerToProviderResponseDTO(savedProvider);
    }

    @Override
    public ProviderResponseDTO updateProvider(Long id, ProviderRequestDTO request) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
        
        providerMapper.updateProviderFromDto(request, provider);
        Provider updatedProvider = providerRepository.save(provider);
        return providerMapper.providerToProviderResponseDTO(updatedProvider);
    }

    @Override
    public void deleteProvider(Long id) {
        if (!providerRepository.existsById(id)) {
            throw new RuntimeException("Proveedor no encontrado");
        }
        providerRepository.deleteById(id);
    }
}