package com.ibs.donation.client;

import com.ibs.donation.client.dto.TossPaymentsConfirmResponse;
import com.ibs.donation.client.dto.TossPaymentsErrorResponse;
import com.ibs.donation.config.TossPaymentsProperties;
import com.ibs.donation.dto.DonationConfirmRequest;
import com.ibs.donation.exception.TossPaymentsApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TossPaymentsClient {

    private final WebClient.Builder webClientBuilder;
    private final TossPaymentsProperties properties;

    private WebClient tossClient;

    private WebClient getClient() {
        if (tossClient == null) {
            tossClient = webClientBuilder
                    .clone()
                    .baseUrl(properties.baseUrlOrDefault())
                    .defaultHeader(HttpHeaders.AUTHORIZATION, buildBasicAuthValue())
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build();
        }
        return tossClient;
    }

    public TossPaymentsConfirmResponse confirmPayment(DonationConfirmRequest request) {
        return getClient().post()
                .uri("/v1/payments/confirm")
                .bodyValue(Map.of(
                        "paymentKey", request.paymentKey(),
                        "orderId", request.orderId(),
                        "amount", request.amount()
                ))
                .retrieve()
                .onStatus(status -> status.isError(), clientResponse -> clientResponse
                        .bodyToMono(TossPaymentsErrorResponse.class)
                        .defaultIfEmpty(new TossPaymentsErrorResponse("UNKNOWN", "Toss Payments error occurred"))
                        .flatMap(error -> Mono.error(new TossPaymentsApiException(error.message() + " (" + error.code() + ")"))
                        ))
                .bodyToMono(TossPaymentsConfirmResponse.class)
                .block();
    }

    private String buildBasicAuthValue() {
        String credential = properties.secretKey() + ":";
        return "Basic " + Base64.getEncoder().encodeToString(credential.getBytes(StandardCharsets.UTF_8));
    }
}
