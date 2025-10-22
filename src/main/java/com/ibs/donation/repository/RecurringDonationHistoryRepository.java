package com.ibs.donation.repository;

import com.ibs.donation.domain.RecurringDonationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RecurringDonationHistoryRepository extends JpaRepository<RecurringDonationHistory, UUID> {
}
