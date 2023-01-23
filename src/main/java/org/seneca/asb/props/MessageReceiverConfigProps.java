package org.seneca.asb.props;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MessageReceiverConfigProps {
    private String connection;
    private String entityPath;
    private String subscription;
}
