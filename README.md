# azure-servicebus-issue

The purpose of this project is to reproduce an issue with Azure ASB messages receiving.

**Setup:**
- OS: Linux/k8s container 
- IDE: IntelliJ IDEA (Community edition) Build #IC-223.8214.52
- Library/Libraries: "com.azure:azure-messaging-servicebus:7.13.0"
- Java version: 17
- Test framework: Spock + groovy/JUnit 5
- Build automation tool: gradle-7.6


**Actual behavior**:
Each unexpected exception in **ServiceBusProcessorClient** message processor callback totally blocks the receiving.
Moreover, the message whose processing caused an error is considered settled and disappears from the queue/subscription
and will be lost if the service is stopped.

**Expected behavior**:
Each message that processing causes an unexpected exception should be redelivered by means of the Message Broker side and in case of exceeding the message's MaxDeliveryCount this message should end up in DLQ.
The same behavior was in the preceding library  "com.microsoft.azure:azure-servicebus" so it is expected from the new one.

**Code**:
[GitHub azure issue project](https://github.com/lSeneka/azure-servicebus-issue)

**Used workaround**:
In order not to block messages **ServiceBusProcessorClient** AutoComplete was disabled. 
```java
        return clientBuilder
                .processor()
                .receiveMode(ServiceBusReceiveMode.PEEK_LOCK)
                .topicName(eventProcessorConfigProps.getEntityPath())
                .subscriptionName(eventProcessorConfigProps.getSubscription())
                .prefetchCount(eventProcessorConfigProps.getPrefetchCount())
                .maxConcurrentCalls(eventProcessorConfigProps.getMaxConcurrentCalls())
                .maxAutoLockRenewDuration(eventProcessorConfigProps.getMaxAutoRenewDuration())
                .processMessage(messageContextConsumer)
                .processError(this::logError)
                .disableAutoComplete()
                .buildProcessorClient();
    }
```
and added **complete** and **abandon** for messages
```java
// consumer used to set up the processor callback for handling messages    
private Consumer<ServiceBusReceivedMessageContext> buildMessageContextConsumer() {
        return messageContext ->
                Try.run(() -> this.dispatcher.dispatchToHandler(messageContext.getMessage()))
                        .onSuccess(unused -> messageContext.complete())
                        .onFailure(throwable -> {
                                    var message = messageContext.getMessage();
                                    log.error("Unhandled exception occurred while processing message [id={}, correlationId={}] for entity-path [{}]. Delivery count {}",
                                            message.getMessageId(),
                                            message.getCorrelationId(),
                                            messageContext.getEntityPath(),
                                            message.getDeliveryCount() + 1,
                                            throwable
                                    );
                                    messageContext.abandon();
                                }
                        );
    }
```

**To Reproduce**:
In order to reproduce
1) Topic and subscription should be created: e.g. [use the Azure portal to create a Service Bus topic and subscriptions](https://learn.microsoft.com/en-us/azure/service-bus-messaging/service-bus-quickstart-topics-subscriptions-portal)
2) Clone the code from repository. Like an option the next command might be used:
```bash
git clone https://github.com/lSeneka/azure-servicebus-issue.git
```
3) Find a class in project directory 
```
../src/test/groovy/org/seneca/asb/TestConfiguration.groovy
```
4) Fulfill the properties
```groovy
    static def connection = "PLACE HERE CONNECTION STRING"
    static def entityPath = "PLACE HERE ENTITY PATH"
    static def subscription = "PLACE HERE SUB"
```
accordingly to the properties of the entity, created on the first step
5) There is 2 tests placed in **EventProcessorClientPoolSpec**. 
   - ```groovy
     "should put messages to the DLQ in case of any unhandled exception during handling of an event by processor with disabled auto complete"   
     ```
     this test uses **EventProcessorManualCompleteClientPool.java** with disabled autocommit processor client.
   - ```groovy
     "should put messages to the DLQ in case of any unhandled exception during handling of an event by processor with enabled auto complete"   
     ```
     this test uses **EventProcessorAutoCompleteClientPool.java** with disabled autocommit processor client.
       
    Both of these tests do pretty much the same:
    1. Creates a set of invalid messages (3 messages)
    2. Sends these messages to the topic by means of test client sender **ServiceBusTopicSenderClient**
    3. Creates a **processor** and thereby start reading messages from the subscription
    4. Check if the count messages processed by **EventHandlersDispatcher.dispatchToHandler** is equals to the expected size of sent messages
    5. Then reads all the messages from the DLQ expecting that all sent messages should end up there.
    6. Checks if subscription is empty by means of test receiver client **ServiceBusTopicReceiverClient**
    7. Check if DLQ for the subscription is empty by means of test DLQ receiver client **ServiceBusDLQForTopicReceiverClient**

    >The only difference is that: one of test uses **EventProcessorManualCompleteClientPool** and sucessfuly passes and another uses **EventProcessorAutoCompleteClientPool** and always failes
  
