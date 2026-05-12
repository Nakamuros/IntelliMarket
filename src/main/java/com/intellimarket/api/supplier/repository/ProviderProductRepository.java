package com.intellimarket.api.supplier.repository;

import com.intellimarket.api.supplier.model.ProviderProduct;
import com.intellimarket.api.supplier.model.ProviderProductId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProviderProductRepository extends JpaRepository<ProviderProduct, ProviderProductId> {
}