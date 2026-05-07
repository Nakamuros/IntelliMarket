package com.intellimarket.api.stores.repository;

import com.intellimarket.api.stores.model.Stores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoresRepository extends JpaRepository<Stores, Long> {

}
