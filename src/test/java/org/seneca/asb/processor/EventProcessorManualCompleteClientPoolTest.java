package org.seneca.asb.processor;

import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.seneca.asb.TestProperties;

import java.util.List;
import java.util.UUID;

class EventProcessorManualCompleteClientPoolTest extends AbstractTest {

    @Test
    @DisplayName("Should receive invalid messages from DLQ")
    void testInvalidMessagesReceiveFromDLQ() {
        //given
        //prepared invalid messages to be sent
        var messages = List.of(
                buildInvalidMessage(UUID.randomUUID()),
                buildInvalidMessage(UUID.randomUUID()),
                buildInvalidMessage(UUID.randomUUID())
        );

        //each message is sent
        messages.forEach(sender::sendMessage);

        //and delivered to subscription
        Awaitility.await().until(() -> receiver.peekMessages(messages.size()).size() > 0);

        //when
        processor = new EventProcessorManualCompleteClientPool(TestProperties.PROCESSOR_PROPS);

        //then all messages are received by dispatcher
        Awaitility.await().until(() -> processor.getReceivedMessagesIds().size() == messages.size());

        //and all messages are in DLQ
        Assertions.assertThat(receiverForDLQ.receiveMessages(messages.size())).hasSameElementsAs(messages);
    }
}