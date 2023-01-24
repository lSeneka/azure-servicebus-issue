package org.seneca.asb.dispatcher;

import com.azure.messaging.servicebus.ServiceBusReceivedMessage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class EventHandlersDispatcher {

    @Getter
    private final Set<String> received = ConcurrentHashMap.newKeySet();

    public void dispatchToHandler(ServiceBusReceivedMessage message) {
        log.info("Received event from topic with correlation id: {}", message.getCorrelationId());
        received.add(message.getCorrelationId());
        ServiceBusMessageConverter.from(message, BasicEvent.class);
        //possible further processing
        log.info("Event from topic with message id: {} has been processed", message.getCorrelationId());
    }
}
