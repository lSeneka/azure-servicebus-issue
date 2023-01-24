package org.seneca.asb.processor

import org.seneca.asb.TestConfiguration
import org.seneca.asb.client.ServiceBusDLQForTopicReceiverClient
import org.seneca.asb.client.ServiceBusTopicReceiverClient
import org.seneca.asb.client.ServiceBusTopicSenderClient
import spock.lang.Shared
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

class AbstractSpec extends Specification {
    @Shared
    def sender = new ServiceBusTopicSenderClient(TestConfiguration.connection, TestConfiguration.entityPath)
    @Shared
    def receiver = new ServiceBusTopicReceiverClient(TestConfiguration.connection, TestConfiguration.entityPath, TestConfiguration.subscription)
    @Shared
    def receiverForDLQ = new ServiceBusDLQForTopicReceiverClient(TestConfiguration.connection, TestConfiguration.entityPath, TestConfiguration.subscription)
    @Shared
    def processor

    PollingConditions conditions = new PollingConditions(timeout: 30)

    def setup() {
        receiver.clearMessages()
        receiverForDLQ.clearMessages()
    }

    def cleanup() {
        receiver.clearMessages()
        receiverForDLQ.clearMessages()
        // we close processor that is opened/created within each test case
        processor.close()
    }

    def cleanupSpec() {
        sender.close()
        receiver.close()
        receiverForDLQ.close()
    }
}
