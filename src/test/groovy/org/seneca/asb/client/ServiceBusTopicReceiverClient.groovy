package org.seneca.asb.client

import com.azure.messaging.servicebus.ServiceBusClientBuilder
import com.azure.messaging.servicebus.ServiceBusReceiverClient
import com.azure.messaging.servicebus.models.ServiceBusReceiveMode

class ServiceBusTopicReceiverClient extends AbstractServiceBusReceiverClient {

    final ServiceBusReceiverClient client

    ServiceBusTopicReceiverClient(String connection,
                                  String entityPath,
                                  String subscription) {
        client = new ServiceBusClientBuilder()
                .connectionString(connection)
                .receiver()
                .receiveMode(ServiceBusReceiveMode.RECEIVE_AND_DELETE)
                .topicName(entityPath)
                .subscriptionName(subscription)
                .buildClient()
    }

}

