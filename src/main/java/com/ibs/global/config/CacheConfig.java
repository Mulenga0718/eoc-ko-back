package com.ibs.global.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        List<CaffeineCache> caches = Arrays.stream(CacheType.values())
                .map(cache -> new CaffeineCache(cache.getCacheName(), Caffeine.newBuilder().recordStats()
                        .expireAfterWrite(cache.getExpiredAfterWrite(), TimeUnit.SECONDS)
                        .maximumSize(cache.getMaximumSize())
                        .build()))
                .collect(Collectors.toList());
        cacheManager.setCaches(caches);
        return cacheManager;
    }

    @Getter
    public enum CacheType {
        // 로그인 시 사용자 정보를 캐싱합니다.
        USER_CACHE("userCache", 60 * 10, 10000); // 10분 동안 캐시, 최대 10000개

        private final String cacheName;
        private final int expiredAfterWrite;
        private final int maximumSize;

        // 명시적 생성자 정의
        CacheType(String cacheName, int expiredAfterWrite, int maximumSize) {
            this.cacheName = cacheName;
            this.expiredAfterWrite = expiredAfterWrite;
            this.maximumSize = maximumSize;
        }

        // 명시적 getter 메소드 추가
        public String getCacheName() {
            return cacheName;
        }

        public int getExpiredAfterWrite() {
            return expiredAfterWrite;
        }

        public int getMaximumSize() {
            return maximumSize;
        }
    }
}
