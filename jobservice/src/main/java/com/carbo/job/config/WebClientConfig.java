package com.carbo.job.config;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Getter
@Slf4j
@Configuration
public class WebClientConfig {

    @Value("${carbo.fracproop.service.baseurl}")
    private String baseUrl;

    @Bean
    public WebClient.Builder getWebClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public SslContext getSSLContext() {
        SslContext sslContext = null;
        try {
            sslContext = SslContextBuilder
                    .forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();
        } catch (Exception e) {
            log.error("Error from creating webClientConfig: {}", e.getMessage());
        }
        return sslContext;
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
