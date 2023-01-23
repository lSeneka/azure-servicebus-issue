package org.seneca.asb.dispatcher;

import com.azure.messaging.servicebus.ServiceBusReceivedMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

import java.io.IOException;

@UtilityClass
public class ServiceBusMessageConverter {

    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public <T> T from(ServiceBusReceivedMessage message, Class<T> type) {
        try {
            return OBJECT_MAPPER.readValue(message.getBody().toBytes(), type);
        } catch (IOException e) {
            throw new MessageProcessingException(
                    String.format("Service bus message with id %s can not be converted to class %s",
                            message.getMessageId(), type.getSimpleName()), e);
        }
    }

}
