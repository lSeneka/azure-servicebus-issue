package org.seneca.asb.client

import com.azure.messaging.servicebus.ServiceBusClientBuilder
import com.azure.messaging.servicebus.ServiceBusSenderClient

class ServiceBusTopicSenderClient extends AbstractServiceBusSenderClient {

    final ServiceBusSenderClient client

    ServiceBusTopicSenderClient(String connection, String entityPath) {
        client = new ServiceBusClientBuilder()
                .connectionString(connection)
                .sender()
                .topicName(entityPath)
                .buildClient()
    }

}
