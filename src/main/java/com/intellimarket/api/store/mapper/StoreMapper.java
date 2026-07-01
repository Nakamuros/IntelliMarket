package com.intellimarket.api.store.mapper;

import com.intellimarket.api.store.dto.StoreResponse;
import com.intellimarket.api.store.model.Store;
import org.springframework.stereotype.Component;

@Component
public class StoreMapper {

    public StoreResponse toResponse(Store store) {
        if (store == null) return null;

        return new StoreResponse(
            store.getId(),
            store.getName(),
            store.getAddress(),
            store.getDistrict(),
            store.getOwner().getFirstName() + " " + store.getOwner().getLastName(),
            store.isActive(),
            store.getCreatedAt(),
                store.getImageUrl()
        );
    }
}
