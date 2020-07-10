package com.myproject.jms.binder.jms.utils;

import org.springframework.context.Lifecycle;
import org.springframework.integration.handler.AbstractMessageHandler;
import org.springframework.integration.jms.JmsHeaderMapper;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.messaging.Message;

import javax.jms.JMSException;
import java.util.concurrent.atomic.AtomicBoolean;

public class JmsSendingMessageHandler extends AbstractMessageHandler implements Lifecycle {
    private final AtomicBoolean running = new AtomicBoolean();

    private final JmsTemplate jmsTemplate;
    private final JmsHeaderMapper headerMapper;

    private final String destinationName;

    public JmsSendingMessageHandler(JmsTemplate jmsTemplate,
                                    JmsHeaderMapper headerMapper, String destinationName) {
        this.jmsTemplate = jmsTemplate;
//        this.destinations = destinations;
        this.headerMapper = headerMapper;
        this.destinationName = destinationName;
    }

    @Override
    protected void handleMessageInternal(Message<?> message) {
        if (message == null) {
            throw new IllegalArgumentException("message must not be null");
        }
        // TODO how to determine destination!!!
//        Object destination = this.determineDestination(message);
        String destinationName = this.determineDestination(message);
        Object objectToSend = message.getPayload();
        HeaderMappingMessagePostProcessor messagePostProcessor = new HeaderMappingMessagePostProcessor(message, this.headerMapper);

        this.jmsTemplate.convertAndSend(destinationName, objectToSend, messagePostProcessor);
    }

    private String determineDestination(Message<?> message) {
        if (this.destinationName != null) {
            return this.destinationName;
        } else {
            return jmsTemplate.getDefaultDestinationName();
        }

//        return destinations.getDestination(message.getHeaders().get(BinderHeaders.PARTITION_HEADER));
    }

    @Override
    public void start() {
        this.running.set(true);
    }

    @Override
    public void stop() {
        if (this.running.compareAndSet(true, false)) {

        }

    }

    @Override
    public boolean isRunning() {
        return this.running.get();
    }

    private static final class HeaderMappingMessagePostProcessor implements MessagePostProcessor {
        private final Message<?> integrationMessage;
        private final JmsHeaderMapper headerMapper;

        private HeaderMappingMessagePostProcessor(Message<?> integrationMessage, JmsHeaderMapper headerMapper) {
            this.integrationMessage = integrationMessage;
            this.headerMapper = headerMapper;
        }

        public javax.jms.Message postProcessMessage(javax.jms.Message jmsMessage) throws JMSException {
            this.headerMapper.fromHeaders(this.integrationMessage.getHeaders(), jmsMessage);
            return jmsMessage;
        }
    }


}
