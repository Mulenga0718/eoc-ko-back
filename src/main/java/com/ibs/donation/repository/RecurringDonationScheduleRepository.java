package com.ibs.donation.repository;

import com.ibs.donation.domain.Donation;
import com.ibs.donation.domain.RecurringDonationSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface RecurringDonationScheduleRepository extends JpaRepository<RecurringDonationSchedule, UUID> {

    List<RecurringDonationSchedule> findByActiveTrueAndNextChargeAtLessThanEqual(LocalDateTime chargeTime);

    boolean existsByDonation(Donation donation);
}
