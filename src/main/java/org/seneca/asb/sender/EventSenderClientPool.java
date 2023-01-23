package org.seneca.asb.sender;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import org.seneca.asb.props.MessageSenderConfigProps;

public class EventSenderClientPool implements AutoCloseable {

    private final ServiceBusSenderClient client;

    public EventSenderClientPool(MessageSenderConfigProps props) {
        this.client = new ServiceBusClientBuilder()
                .connectionString(props.getConnection())
                .sender()
                .topicName(props.getEntityPath())
                .buildClient();
    }

    public void sendMessage(String message) {
        this.client.sendMessage(new ServiceBusMessage(message));
    }

    @Override
    public void close() {
        this.client.close();
    }
}
