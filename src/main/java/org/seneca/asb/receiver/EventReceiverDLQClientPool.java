package org.seneca.asb.receiver;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusReceiverClient;
import com.azure.messaging.servicebus.models.ServiceBusReceiveMode;
import com.azure.messaging.servicebus.models.SubQueue;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.seneca.asb.props.MessageReceiverConfigProps;

@Slf4j
public class EventReceiverDLQClientPool extends ReceiverClientPool implements AutoCloseable {

    @Getter
    private final ServiceBusReceiverClient client;

    public EventReceiverDLQClientPool(MessageReceiverConfigProps props) {
        this.client = new ServiceBusClientBuilder()
                .connectionString(props.getConnection())
                .receiver()
                .receiveMode(ServiceBusReceiveMode.RECEIVE_AND_DELETE)
                .topicName(props.getEntityPath())
                .subscriptionName(props.getSubscription())
                .subQueue(SubQueue.DEAD_LETTER_QUEUE)
                .buildClient();
    }

    @Override
    public void close() {
        this.client.close();
    }
}
