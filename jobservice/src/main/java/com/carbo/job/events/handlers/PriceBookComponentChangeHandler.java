package com.carbo.job.events.handlers;

import com.carbo.job.events.PriceBookComponentChangeEvent;
import com.carbo.job.events.model.PriceBookComponentNameChangeModel; // The DTO from Kafka
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@Slf4j
public class PriceBookComponentChangeHandler {

    private final ApplicationEventPublisher publisher;

    public PriceBookComponentChangeHandler(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Bean
    public Consumer<PriceBookComponentNameChangeModel> priceBookComponentChangeTopic() {
        log.info("Creating priceBookComponentChangeTopic Consumer Bean");
        return event -> {
            log.info("JOB SERVICE RECEIVED PRICE BOOK COMPONENT CHANGE EVENT");
            log.info("Organization ID: {}", event.getOrganizationId());
            log.info("Old Name: {} -> New Name: {}", event.getOldName(), event.getNewName());
            log.info("Timestamp: {}", event.getTimestamp());

             publisher.publishEvent(new PriceBookComponentChangeEvent(this, event));
        };
    }

}