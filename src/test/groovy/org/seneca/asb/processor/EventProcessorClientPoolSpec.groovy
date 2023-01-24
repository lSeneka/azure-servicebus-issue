package org.seneca.asb.processor

import groovy.util.logging.Slf4j
import org.seneca.asb.TestConfiguration

@Slf4j
class EventProcessorClientPoolSpec extends AbstractSpec {

    def "should put messages to the DLQ in case of any unhandled exception during handling of an event by processor with disabled auto complete"() {
        given: "at least 3 invalid messages are prepared"
        def invalidMessages = [new InvalidMessage(),
                               new InvalidMessage(),
                               new InvalidMessage()
        ] as Set

        log.info("Prepared $invalidMessages")

        and:
        assert receiverForDLQ.isEmpty()
        assert receiver.isEmpty()

        and:
        invalidMessages.forEach { sender.sendMessage(it) }

        when: "all invalid messages are sent"
        processor = new EventProcessorManualCompleteClientPool(TestConfiguration.PROCESSOR_PROPS)

        then: "all messages went through the dispatcher"
        conditions.eventually {
            assert processor.getReceivedMessagesIds().size() == invalidMessages.size()
        }

        and: "each invalid message should be found in DLQ"
        receiverForDLQ.receiveMessagesAsJsonString(invalidMessages.size()) as Set == invalidMessages*.body as Set

        and: "DLQ is empty after all messages retrieval"
        receiverForDLQ.isEmpty()

        and: "no messages left in subscription"
        receiver.isEmpty()
    }

    def "should put messages to the DLQ in case of any unhandled exception during handling of an event by processor with enabled auto complete"() {
        given: "at least 3 invalid messages are prepared"
        def invalidMessages = [new InvalidMessage(),
                               new InvalidMessage(),
                               new InvalidMessage()
        ] as Set

        log.info("Prepared $invalidMessages")

        and:
        assert receiverForDLQ.isEmpty()
        assert receiver.isEmpty()

        and:
        invalidMessages.forEach { sender.sendMessage(it) }

        when: "all invalid messages are sent"
        processor = new EventProcessorAutoCompleteClientPool(TestConfiguration.PROCESSOR_PROPS)


        then: "all messages went through the dispatcher"
        conditions.eventually {
            assert processor.getReceivedMessagesIds().size() == invalidMessages.size()
        }

        and: "each invalid message should be found in DLQ"
        receiverForDLQ.receiveMessagesAsJsonString(invalidMessages.size()) as Set == invalidMessages*.body as Set

        and: "DLQ is empty after all messages retrieval"
        receiverForDLQ.isEmpty()

        and: "no messages left in subscription"
        receiver.isEmpty()
    }
}
