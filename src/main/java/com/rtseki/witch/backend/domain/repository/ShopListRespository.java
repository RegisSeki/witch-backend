package com.rtseki.witch.backend.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rtseki.witch.backend.domain.model.ShopList;

@Repository
public interface ShopListRespository extends JpaRepository<ShopList, Long> {

}
