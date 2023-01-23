package org.seneca.asb.receiver;

import com.azure.messaging.servicebus.ServiceBusReceivedMessage;
import com.azure.messaging.servicebus.ServiceBusReceiverClient;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class ReceiverClientPool {
    private static final Duration RECEIVE_TIMEOUT = Duration.ofSeconds(20);

    protected abstract ServiceBusReceiverClient getClient();

    public List<String> receiveMessages(int limit) {
        var messages = new ArrayList<String>();
        for (int i = 0; i < limit; i++) {
            messages.add(this.receiveMessage());
        }
        return messages;
    }

    public String receiveMessage() {
        return this.getClient().receiveMessages(1, RECEIVE_TIMEOUT)
                .stream()
                .map(message -> message.getBody().toString())
                .findFirst()
                .orElse(null);
    }

    public List<ServiceBusReceivedMessage> peekMessages(int limit) {
        return this.getClient().peekMessages(limit)
                .stream().toList();
    }

    public void cleanUp() {
        int size;
        do {
            size = (int) getClient().peekMessages(100).stream().count();
            if (size > 0) {
                this.receiveMessages(size);
                log.info("Clean up {} messages for {}", size, this.getClient().getEntityPath());
            }
        } while (size > 0);
    }
}
