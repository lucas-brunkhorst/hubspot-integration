package com.lucasbrunkhorst.hubspotintegration.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public com.github.benmanes.caffeine.cache.Cache<String, String> tokenCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(3500, TimeUnit.SECONDS)
                .maximumSize(10)
                .build();
    }
}

