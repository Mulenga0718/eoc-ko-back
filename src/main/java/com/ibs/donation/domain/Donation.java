package com.ibs.donation.domain;

import com.ibs.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "donations")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Donation extends BaseTimeEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "donation_id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true, length = 64)
    private String orderId;

    @Column(nullable = false, length = 100)
    private String orderName;

    @Column(nullable = false)
    private Long amount;

    @Builder.Default
    @Column(nullable = false, length = 3)
    private String currency = "KRW";

    @Column(length = 120)
    private String paymentKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DonationStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DonationType donationType;

    @Column(length = 100)
    private String donorName;

    @Column(length = 120)
    private String donorEmail;

    @Column(length = 30)
    private String donorPhone;

    @Column(length = 40)
    private String paymentMethod;

    @Column(length = 40)
    private String easyPayProvider;

    @Column(length = 200)
    private String receiptUrl;

    @Column(length = 128)
    private String billingKey;

    @Column(length = 512)
    private String failureMessage;

    private LocalDateTime approvedAt;

    @Column(nullable = false)
    private boolean receiptRequired;

    public static Donation createPending(String orderId,
                                         String orderName,
                                         Long amount,
                                         DonationType donationType,
                                         String donorName,
                                         String donorEmail,
                                         String donorPhone,
                                         boolean receiptRequired) {
        return Donation.builder()
                .orderId(orderId)
                .orderName(orderName)
                .amount(amount)
                .donationType(donationType)
                .donorName(donorName)
                .donorEmail(donorEmail)
                .donorPhone(donorPhone)
                .status(DonationStatus.PENDING)
                .receiptRequired(receiptRequired) // Set the new field
                .build();
    }

    public void markCompleted(String paymentKey,
                              LocalDateTime approvedAt,
                              String receiptUrl,
                              String paymentMethod,
                              String easyPayProvider,
                              String billingKey) {
        this.paymentKey = paymentKey;
        this.approvedAt = approvedAt;
        this.receiptUrl = receiptUrl;
        this.paymentMethod = paymentMethod;
        this.easyPayProvider = easyPayProvider;
        this.billingKey = billingKey;
        this.status = DonationStatus.COMPLETED;
        this.failureMessage = null;
    }

    public void markFailed(String failureMessage) {
        this.status = DonationStatus.FAILED;
        this.failureMessage = failureMessage;
    }

    public void markCanceled(String failureMessage) {
        this.status = DonationStatus.CANCELED;
        this.failureMessage = failureMessage;
    }
}
