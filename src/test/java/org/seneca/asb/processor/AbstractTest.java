package org.seneca.asb.processor;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.seneca.asb.TestProperties;
import org.seneca.asb.receiver.EventReceiverClientPool;
import org.seneca.asb.receiver.EventReceiverDLQClientPool;
import org.seneca.asb.sender.EventSenderClientPool;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

abstract class AbstractTest {

    EventSenderClientPool sender;

    EventReceiverDLQClientPool receiverForDLQ;
    EventReceiverClientPool receiver;
    EventProcessorClientPool processor;

    @BeforeEach
    void setUpBefore() {
        Awaitility.setDefaultPollInterval(10, TimeUnit.MILLISECONDS);
        Awaitility.setDefaultPollDelay(Duration.ZERO);
        Awaitility.setDefaultTimeout(Duration.ofSeconds(30));

        //receiver
        receiver = new EventReceiverClientPool(TestProperties.RECEIVER_PROPS);
        //DLQ receiver
        receiverForDLQ = new EventReceiverDLQClientPool(TestProperties.RECEIVER_PROPS);
        //sender
        sender = new EventSenderClientPool(TestProperties.SENDER_PROPS);

        cleanUp();
    }

    @AfterEach
    void setUpAfter() {
        //make clean up for testing purposes
        cleanUp();

        //close all clients
        receiver.close();
        sender.close();
        receiverForDLQ.close();
        processor.close();
    }

    void cleanUp() {
        //make clean up for testing purposes
        receiver.cleanUp();
        receiverForDLQ.cleanUp();
    }

    protected String buildInvalidMessage(UUID index) {
        return """
                    {"invalid"="message%s"}
                """.formatted(index);
    }
}