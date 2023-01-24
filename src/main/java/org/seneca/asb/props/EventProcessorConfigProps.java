package org.seneca.asb.props;

import lombok.Builder;
import lombok.Getter;

import java.time.Duration;

@Getter
@Builder
public class EventProcessorConfigProps {
    private String connection;
    private String entityPath;
    private String subscription;
    private Integer factoriesCount;
    private Integer maxConcurrentCalls;
    private Integer prefetchCount;
    private Duration maxAutoRenewDuration;
}
