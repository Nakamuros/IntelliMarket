package com.intellimarket.api.store.repository;

import com.intellimarket.api.store.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    Optional<Store> findByOwnerEmail(String email);

    @Query(value = "SELECT * FROM stores s " +
            "WHERE LOWER(REPLACE(s.name, ' ', '-')) LIKE LOWER(REPLACE(CONCAT('%', ?1, '%'), ' ', '-')) " +
            "LIMIT 1",
            nativeQuery = true)
    Optional<Store> findByNameIgnoreCase(String nombreTienda);
}