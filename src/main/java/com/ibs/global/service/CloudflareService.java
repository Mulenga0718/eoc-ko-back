package com.ibs.global.service;

import com.ibs.global.dto.CloudflareResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CloudflareService {

    private static final String DEFAULT_CONTENT_TYPE = MediaType.APPLICATION_OCTET_STREAM_VALUE;
    private static final Pattern IMAGE_ID_PATTERN = Pattern.compile("https://imagedelivery.net/[^/]+/([^/]+)/.*");

    private final WebClient.Builder webClientBuilder;

    @Value("${cloudflare.api.token}")
    private String apiToken;

    @Value("${cloudflare.account.id}")
    private String accountId;

    @Value("${cloudflare.base.url:https://api.cloudflare.com/client/v4}")
    private String baseUrl;

    private WebClient cloudflareClient;

    private WebClient getClient() {
        if (cloudflareClient == null) {
            cloudflareClient = webClientBuilder
                    .baseUrl(baseUrl)
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiToken)
                    .build();
        }
        return cloudflareClient;
    }

    public String uploadImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image file must not be null or empty");
        }

        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("file", new ByteArrayResource(file.getBytes()) {
                    @Override
                    public String getFilename() {
                        return file.getOriginalFilename();
                    }
                })
                .header(HttpHeaders.CONTENT_TYPE, resolveContentType(file));

        CloudflareResponseDto response = getClient().post()
                .uri(uriBuilder -> uriBuilder
                        .path("/accounts/{accountId}/images/v1")
                        .build(accountId))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> clientResponse
                        .bodyToMono(String.class)
                        .defaultIfEmpty("Unknown error")
                        .flatMap(errorBody -> Mono.error(new IllegalStateException("Cloudflare Images upload error: " + errorBody))))
                .bodyToMono(CloudflareResponseDto.class)
                .block();

        validateResponse(response);
        return pickVariantUrl(response.result().variants());
    }

    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }

        Matcher matcher = IMAGE_ID_PATTERN.matcher(imageUrl);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Could not extract image ID from URL: " + imageUrl);
        }

        String imageId = matcher.group(1);

        getClient().delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/accounts/{accountId}/images/v1/{imageId}")
                        .build(accountId, imageId))
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> clientResponse
                        .bodyToMono(String.class)
                        .defaultIfEmpty("Unknown error")
                        .flatMap(errorBody -> Mono.error(new IllegalStateException("Cloudflare Images deletion error: " + errorBody))))
                .bodyToMono(Void.class)
                .block();
    }

    private void validateResponse(CloudflareResponseDto response) {
        if (response == null || !response.success()) {
            throw new IllegalStateException("Cloudflare Images upload failed: empty response or unsuccessful status");
        }
        if (response.result() == null || response.result().variants() == null || response.result().variants().isEmpty()) {
            throw new IllegalStateException("Cloudflare Images upload failed: no variants returned");
        }
    }

    private String pickVariantUrl(List<String> variants) {
        return variants.stream()
                .filter(url -> url != null && url.endsWith("/public"))
                .findFirst()
                .orElseGet(() -> variants.get(0));
    }

    private String resolveContentType(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null ? contentType : DEFAULT_CONTENT_TYPE;
    }
}
