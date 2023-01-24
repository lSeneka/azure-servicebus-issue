package org.seneca.asb.processor;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusProcessorClient;
import com.azure.messaging.servicebus.ServiceBusReceivedMessageContext;
import com.azure.messaging.servicebus.models.ServiceBusReceiveMode;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.seneca.asb.dispatcher.EventHandlersDispatcher;
import org.seneca.asb.props.EventProcessorConfigProps;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Slf4j
public class EventProcessorManualCompleteClientPool extends EventProcessorClientPool {

    @Getter(AccessLevel.PROTECTED)
    private final List<ServiceBusProcessorClient> clients;
    private final EventHandlersDispatcher dispatcher = new EventHandlersDispatcher();

    @Override
    public Set<String> getReceivedMessagesIds() {
        return dispatcher.getReceived();
    }

    public EventProcessorManualCompleteClientPool(EventProcessorConfigProps eventProcessorConfigProps) {
        var clientBuilder = new ServiceBusClientBuilder()
                .connectionString(eventProcessorConfigProps.getConnection());
        this.clients = Stream.generate(
                        () -> this.buildClient(
                                clientBuilder,
                                eventProcessorConfigProps,
                                this.buildMessageContextConsumer())
                )
                .limit(eventProcessorConfigProps.getFactoriesCount())
                .toList();

        this.clients.forEach(ServiceBusProcessorClient::start);
        log.info("Event processor client pool with size = {} for topic '{}' and subscription '{}' has been successfully initialized",
                clients.size(), eventProcessorConfigProps.getEntityPath(), eventProcessorConfigProps.getSubscription());
    }

    private Consumer<ServiceBusReceivedMessageContext> buildMessageContextConsumer() {
        return messageContext ->
                Try.run(() -> dispatcher.dispatchToHandler(messageContext.getMessage()))
                        .onSuccess(unused -> messageContext.complete())
                        .onFailure(throwable -> {
                                    var message = messageContext.getMessage();
                                    log.error("Unhandled exception occurred while processing message [id={}, correlationId={}] for entity-path [{}]. Delivery count {}",
                                            message.getMessageId(),
                                            message.getCorrelationId(),
                                            messageContext.getEntityPath(),
                                            message.getDeliveryCount() + 1
                                    );
                                    messageContext.abandon();
                                }
                        );
    }

    private ServiceBusProcessorClient buildClient(ServiceBusClientBuilder clientBuilder,
                                                  EventProcessorConfigProps eventProcessorConfigProps,
                                                  Consumer<ServiceBusReceivedMessageContext> messageContextConsumer) {
        return clientBuilder
                .processor()
                .receiveMode(ServiceBusReceiveMode.PEEK_LOCK)
                .topicName(eventProcessorConfigProps.getEntityPath())
                .subscriptionName(eventProcessorConfigProps.getSubscription())
                .prefetchCount(eventProcessorConfigProps.getPrefetchCount())
                .maxConcurrentCalls(eventProcessorConfigProps.getMaxConcurrentCalls())
                .maxAutoLockRenewDuration(eventProcessorConfigProps.getMaxAutoRenewDuration())
                .processMessage(messageContextConsumer)
                .processError(this::logError)
                .disableAutoComplete()
                .buildProcessorClient();
    }
}
