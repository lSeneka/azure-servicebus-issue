package org.seneca.asb

import org.seneca.asb.props.EventProcessorConfigProps

import java.time.Duration

class TestConfiguration {

    static def connection = "PLACE HERE CONNECTION STRING"
    static def entityPath = "PLACE HERE ENTITY PATH"
    static def subscription = "PLACE HERE SUB"

    public static final EventProcessorConfigProps PROCESSOR_PROPS = EventProcessorConfigProps
            .builder()
            .connection(connection)
            .entityPath(entityPath)
            .subscription(subscription)
            .factoriesCount(1)
            .prefetchCount(0)
            .maxConcurrentCalls(2)
            .maxAutoRenewDuration(Duration.ofMinutes(5))
            .build()
}
