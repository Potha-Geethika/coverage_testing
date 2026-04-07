package com.carbo.job.services;

import com.carbo.job.events.model.WellChangeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;

@Service
public class NotificationServiceImpl implements NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    public static final Map<String, List<SseEmitter>> emittersByOrganization = Collections.synchronizedMap(new HashMap<>());

    @Override
    public SseEmitter initSseEmitters(String organizationId) {
        SseEmitter emitter = new SseEmitter();
        List<SseEmitter> emitters = emittersByOrganization.get(organizationId);
        if (emitters == null) {
            emittersByOrganization.put(organizationId, Collections.synchronizedList(new ArrayList<>()));
        }
        emittersByOrganization.get(organizationId).add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));

        return emitter;
    }

    @Override
    public void sendSseEventsToUI(WellChangeModel notification) {
        List<SseEmitter> sseEmitterListToRemove = new ArrayList<>();
        List<SseEmitter> emitters = emittersByOrganization.get(notification.getOrganizationId());
        if (emitters != null) {
            emitters.forEach((SseEmitter emitter) -> {
                try {
                    emitter.send(notification, MediaType.APPLICATION_JSON);
                } catch (IOException e) {
                    emitter.complete();
                    sseEmitterListToRemove.add(emitter);
                    logger.error("Error during emitting SSE: ", e);
                }
            });
            emitters.removeAll(sseEmitterListToRemove);
        }
    }
}
