package com.gosqu.user.address;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {
    List<Address> findAllByUserId(UUID userId);
    Optional<Address> findByIdAndUserId(UUID id, UUID userId);
    Optional<Address> findByUserIdAndIsDefaultTrue(UUID userId);
    long countByUserId(UUID userId);

    @Modifying
    @Query("UPDATE Address a SET a.isDefault = false WHERE a.userId = :userId")
    void clearDefaultForUser(UUID userId);
}
