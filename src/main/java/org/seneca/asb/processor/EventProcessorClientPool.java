package org.seneca.asb.processor;

import com.azure.messaging.servicebus.ServiceBusErrorContext;
import com.azure.messaging.servicebus.ServiceBusProcessorClient;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;

@Slf4j
public abstract class EventProcessorClientPool implements AutoCloseable {

    public abstract Set<String> getReceivedMessagesIds();

    protected abstract List<ServiceBusProcessorClient> getClients();

    protected void logError(ServiceBusErrorContext errorContext) {
        log.error("Error occurred during message processing, errorSource {}, azure namespace {}, entity path {}",
                errorContext.getErrorSource(), errorContext.getFullyQualifiedNamespace(), errorContext.getEntityPath(), errorContext.getException());
    }

    @Override
    public void close() {
        this.getClients().forEach(ServiceBusProcessorClient::close);
    }
}
