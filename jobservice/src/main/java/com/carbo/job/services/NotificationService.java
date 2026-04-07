package com.carbo.job.services;

import com.carbo.job.events.model.WellChangeModel;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationService {
    SseEmitter initSseEmitters(String organizationId);
    void sendSseEventsToUI(WellChangeModel notification);
}
