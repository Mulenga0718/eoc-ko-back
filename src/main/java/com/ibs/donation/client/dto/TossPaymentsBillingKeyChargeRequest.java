package com.ibs.donation.client.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * Request payload for charging a saved Toss Payments billing key.
 */
public record TossPaymentsBillingKeyChargeRequest(
        String orderId,
        Long amount,
        String orderName,
        String customerName,
        String customerEmail,
        String customerMobilePhone,
        Long taxFreeAmount
) {

    public Map<String, Object> toRequestBody() {
        Map<String, Object> body = new HashMap<>();
        if (orderId != null && !orderId.isBlank()) {
            body.put("orderId", orderId);
        }
        if (amount != null) {
            body.put("amount", amount);
        }
        if (orderName != null && !orderName.isBlank()) {
            body.put("orderName", orderName);
        }
        if (customerName != null && !customerName.isBlank()) {
            body.put("customerName", customerName);
        }
        if (customerEmail != null && !customerEmail.isBlank()) {
            body.put("customerEmail", customerEmail);
        }
        if (customerMobilePhone != null && !customerMobilePhone.isBlank()) {
            body.put("customerMobilePhone", customerMobilePhone);
        }
        if (taxFreeAmount != null) {
            body.put("taxFreeAmount", taxFreeAmount);
        }
        return body;
    }
}
