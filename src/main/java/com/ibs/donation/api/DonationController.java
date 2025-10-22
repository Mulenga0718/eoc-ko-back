package com.ibs.donation.api;

import com.ibs.donation.dto.DonationConfirmRequest;
import com.ibs.donation.dto.DonationPrepareRequest;
import com.ibs.donation.dto.DonationPrepareResponse;
import com.ibs.donation.dto.DonationResponse;
import com.ibs.donation.service.DonationService;
import com.ibs.global.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/donations")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;

    @PostMapping("/prepare")
    public ResponseEntity<ApiResponse<DonationPrepareResponse>> prepareDonation(
            @RequestBody @Valid DonationPrepareRequest request
    ) {
        DonationPrepareResponse response = donationService.prepareDonation(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<DonationResponse>> confirmDonation(
            @RequestBody @Valid DonationConfirmRequest request
    ) {
        DonationResponse response = donationService.confirmDonation(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
