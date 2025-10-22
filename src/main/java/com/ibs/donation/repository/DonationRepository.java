package com.ibs.donation.repository;

import com.ibs.donation.domain.Donation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DonationRepository extends JpaRepository<Donation, UUID> {

    Optional<Donation> findByOrderId(String orderId);

    boolean existsByOrderId(String orderId);
}
