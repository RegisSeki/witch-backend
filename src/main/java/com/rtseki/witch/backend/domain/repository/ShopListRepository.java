package com.rtseki.witch.backend.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rtseki.witch.backend.domain.model.ShopList;

@Repository
public interface ShopListRepository extends JpaRepository<ShopList, Long> {
	Optional<ShopList> findByNameAndUser_Id(String name, Long user_id);
}
