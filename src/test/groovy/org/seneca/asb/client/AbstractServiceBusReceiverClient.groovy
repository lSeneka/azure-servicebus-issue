package org.seneca.asb.client


import com.azure.messaging.servicebus.ServiceBusReceiverClient

import java.time.Duration

abstract class AbstractServiceBusReceiverClient implements AutoCloseable {

    private static final RECEIVE_TIMEOUT = Duration.ofSeconds(20)

    abstract ServiceBusReceiverClient getClient()

    List<String> receiveMessagesAsJsonString(int limit) {
        def messages = []
        limit.times {
            messages.add(receiveMessageAsJsonString())
        }
        return messages
    }

    def receiveMessageAsJsonString() {
        def message = client.receiveMessages(1, RECEIVE_TIMEOUT)
                .collect { it.body.toString() }
                .first()
        println("Recieved ${message}" as String)
        message
    }

    boolean isEmpty() {
        client.peekMessage() == null
    }

    void clearMessages() {
        int size
        while ((size = client.peekMessages(100).size()) > 0) {
            def messages = []
            size.times {
                def message = receiveAzureMessage()
                messages.add(message ? message : receiveAzureMessage())
            }
            messages.forEach {
                println("Clean up for ${client.entityPath}: message with correlation id ${it.correlationId}" as String)
            }
        }
    }

    def receiveAzureMessage() {
        def messages = client.receiveMessages(1, RECEIVE_TIMEOUT)
        return messages.isEmpty() ? null : messages.first()
    }

    @Override
    void close() throws Exception {
        clearMessages()
        client.close()
    }
}
