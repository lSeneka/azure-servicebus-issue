package org.seneca.asb.client

import com.azure.messaging.servicebus.ServiceBusMessage
import com.azure.messaging.servicebus.ServiceBusSenderClient
import org.seneca.asb.processor.InvalidMessage

abstract class AbstractServiceBusSenderClient implements AutoCloseable {

    abstract ServiceBusSenderClient getClient()

    void sendMessage(InvalidMessage message) {
        def asbMessage = new ServiceBusMessage(message.body)
        asbMessage.correlationId = message.id
        this.sendMessage(asbMessage)
    }

    void sendMessage(ServiceBusMessage message) {
        println("Sending message with correlation id ${message.correlationId}" as String)
        client.sendMessage(message)
    }

    @Override
    void close() throws Exception {
        client.close()
    }
}
