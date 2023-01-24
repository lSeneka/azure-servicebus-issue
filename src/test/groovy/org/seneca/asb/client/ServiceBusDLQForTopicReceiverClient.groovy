package org.seneca.asb.client

import com.azure.messaging.servicebus.ServiceBusClientBuilder
import com.azure.messaging.servicebus.ServiceBusReceiverClient
import com.azure.messaging.servicebus.models.ServiceBusReceiveMode
import com.azure.messaging.servicebus.models.SubQueue

class ServiceBusDLQForTopicReceiverClient extends AbstractServiceBusReceiverClient {

    final ServiceBusReceiverClient client

    ServiceBusDLQForTopicReceiverClient(String connection,
                                        String entityPath,
                                        String subscription) {
        client = new ServiceBusClientBuilder()
                .connectionString(connection)
                .receiver()
                .receiveMode(ServiceBusReceiveMode.RECEIVE_AND_DELETE)
                .topicName(entityPath)
                .subscriptionName(subscription)
                .subQueue(SubQueue.DEAD_LETTER_QUEUE)
                .buildClient()
    }
}

