package com.intellimarket.api.supplier.mapper;

import com.intellimarket.api.supplier.dto.ProviderRequestDTO;
import com.intellimarket.api.supplier.dto.ProviderResponseDTO;
import com.intellimarket.api.supplier.model.Provider;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProviderMapper {
    
    ProviderResponseDTO providerToProviderResponseDTO(Provider provider);
    
    Provider providerRequestDTOToProvider(ProviderRequestDTO requestDTO);
    
    void updateProviderFromDto(ProviderRequestDTO requestDTO, @MappingTarget Provider provider);
}