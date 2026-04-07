package com.carbo.job.events.handlers;

import com.carbo.job.events.UpdateJobStartDateEvent;
import com.carbo.job.events.model.PadChangeModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@Slf4j
public class PadTimezoneChangeHandler {

    private final ApplicationEventPublisher publisher;

    public PadTimezoneChangeHandler(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Bean
    public Consumer<PadChangeModel> padTimezoneChangeTopic() {
        return changeModel -> {
            log.info("=== PAD TIMEZONE CHANGE EVENT RECEIVED ===");
            log.info("Action: {}, Pad: {}",
                    changeModel.getAction(), changeModel.getPadName());

            if ("UPDATE".equals(changeModel.getAction())) {
                publisher.publishEvent(
                        new UpdateJobStartDateEvent(changeModel)
                );
            }
        };
    }
}
