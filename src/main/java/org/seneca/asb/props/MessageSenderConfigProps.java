package org.seneca.asb.props;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MessageSenderConfigProps {
    private String connection;
    private String entityPath;
    private Integer factoriesCount;
}
