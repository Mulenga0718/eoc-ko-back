package com.ibs.donation.service.mapper;

import com.ibs.donation.domain.Donation;
import com.ibs.donation.dto.DonationResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DonationMapper {

    DonationResponse toResponse(Donation donation);
}
