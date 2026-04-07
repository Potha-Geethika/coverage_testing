package com.carbo.job.events.handlers;

import com.carbo.job.events.UpdateWellEvent;
import com.carbo.job.events.model.WellChangeModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@Slf4j
public class WellChangeHandler {

    private final ApplicationEventPublisher publisher;

    public WellChangeHandler(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Bean
    public Consumer<WellChangeModel> wellChangeTopic() {
        return changeModel -> {
            log.info("=== WELL CHANGE EVENT RECEIVED ===");
            log.info("Action: {}, WellAPI: {}",
                    changeModel.getAction(), changeModel.getWellAPI());

            if ("SAVE".equals(changeModel.getAction())
                    || "UPDATE".equals(changeModel.getAction())) {
                publisher.publishEvent(new UpdateWellEvent(changeModel));
            }
        };
    }
}
