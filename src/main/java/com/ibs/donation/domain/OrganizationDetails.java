package com.ibs.donation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrganizationDetails {

    @Column(length = 100)
    private String organizationName;

    @Column(length = 20)
    private String businessNumber;

    @Column(length = 100)
    private String managerName;

    @Column(length = 120)
    private String managerEmail;

}