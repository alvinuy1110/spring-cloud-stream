package com.myproject.jms.binder;


import com.myproject.jms.binder.jms.utils.JmsMessageDrivenChannelAdapterFactory;
import com.myproject.jms.binder.jms.utils.JmsSendingMessageHandlerFactory;
import com.myproject.jms.config.JmsConsumerProperties;
import com.myproject.jms.config.JmsExtendedBindingProperties;
import com.myproject.jms.config.JmsProducerProperties;
import com.myproject.jms.provisioning.JmsProvisioner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.binder.*;
import org.springframework.cloud.stream.provisioning.ConsumerDestination;
import org.springframework.cloud.stream.provisioning.ProducerDestination;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.jms.JmsMessageDrivenEndpoint;
import org.springframework.integration.jms.dsl.JmsMessageDrivenChannelAdapterSpec;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.Queue;
import javax.jms.Session;

/**
 * Binder definition for JMS.
 *
 */
@Slf4j
public class JmsMessageChannelBinder
        extends AbstractMessageChannelBinder<ExtendedConsumerProperties<JmsConsumerProperties>, ExtendedProducerProperties<JmsProducerProperties>,
        JmsProvisioner>
        implements ExtendedPropertiesBinder<MessageChannel, JmsConsumerProperties, JmsProducerProperties> {

    private final JmsSendingMessageHandlerFactory jmsSendingMessageHandlerFactory;
    private final JmsMessageDrivenChannelAdapterFactory jmsMessageDrivenChannelAdapterFactory;
    private final ConnectionFactory connectionFactory;
    private final DestinationResolver destinationResolver;
    private JmsExtendedBindingProperties extendedBindingProperties = new JmsExtendedBindingProperties();

    public JmsMessageChannelBinder(JmsProvisioner provisioningProvider,
                                   JmsSendingMessageHandlerFactory jmsSendingMessageHandlerFactory,
                                   JmsMessageDrivenChannelAdapterFactory jmsMessageDrivenChannelAdapterFactory,
                                   JmsTemplate jmsTemplate,
                                   ConnectionFactory connectionFactory) {
        super(null, provisioningProvider);
        this.jmsSendingMessageHandlerFactory = jmsSendingMessageHandlerFactory;
        this.jmsMessageDrivenChannelAdapterFactory = jmsMessageDrivenChannelAdapterFactory;
        this.connectionFactory = connectionFactory;
        this.destinationResolver = jmsTemplate.getDestinationResolver();
    }

    public void setExtendedBindingProperties(JmsExtendedBindingProperties extendedBindingProperties) {
        this.extendedBindingProperties = extendedBindingProperties;
    }


//    @Override
//    protected MessageHandler createProducerMessageHandler(ProducerDestination producerDestination,
//                                                          ExtendedProducerProperties<JmsProducerProperties> producerProperties) throws Exception {
//        TopicPartitionRegistrar topicPartitionRegistrar = new TopicPartitionRegistrar();
//        Session session = connectionFactory.createConnection().createSession(true, 1);
//
//        if (producerProperties.isPartitioned()) {
//            int partitionCount = producerProperties.getPartitionCount();
//            for (int i = 0; i < partitionCount; ++i) {
//                String destination = producerDestination.getNameForPartition(i);
//                Topic topic = (Topic) destinationResolver.resolveDestinationName(session, destination, true);
//                topicPartitionRegistrar.addDestination(i, topic);
//            }
//        } else {
//            String destination = producerDestination.getName();
//            Topic topic = (Topic) destinationResolver.resolveDestinationName(session, destination, true);
//            topicPartitionRegistrar.addDestination(-1, topic);
//        }
//        return this.jmsSendingMessageHandlerFactory.build(topicPartitionRegistrar);
//    }


    @Override
    protected MessageHandler createProducerMessageHandler(ProducerDestination destination, ExtendedProducerProperties<JmsProducerProperties> producerProperties, MessageChannel errorChannel) throws Exception {

        log.info("Producer destination {}", destination.getName());
// TODO: should be from name or from properties??
        return this.jmsSendingMessageHandlerFactory.build(destination.getName());

    }

    @Override
    protected MessageProducer createConsumerEndpoint(
            ConsumerDestination consumerDestination, String group, ExtendedConsumerProperties<JmsConsumerProperties> properties) throws Exception {

        log.info("Consumer destination {}", consumerDestination.getName());
        Session session = connectionFactory.createConnection().createSession(true, JMSContext.AUTO_ACKNOWLEDGE);

        // TODO: should be from name or from properties??
        Queue queue = (Queue) destinationResolver.resolveDestinationName(session, consumerDestination.getName(), false);
//        return jmsMessageDrivenChannelAdapterFactory.build(queue, properties);

        JmsMessageDrivenEndpoint jmsMessageDrivenEndpoint = (JmsMessageDrivenEndpoint) jmsMessageDrivenChannelAdapterFactory.build(queue,properties).get();

//        MessageChannel messageChannel = JmsMessageChannelBinder
//        jmsMessageDrivenEndpoint.setOutputChannel("some-channel");
//        IntegrationBinderInboundChannelAdapter adapter = new IntegrationBinderInboundChannelAdapter(messageListenerContainer);


        return  jmsMessageDrivenEndpoint;
    }

    @Override
    public JmsConsumerProperties getExtendedConsumerProperties(String channelName) {
        return this.extendedBindingProperties.getExtendedConsumerProperties(channelName);
    }

    @Override
    public JmsProducerProperties getExtendedProducerProperties(String channelName) {
        return this.extendedBindingProperties.getExtendedProducerProperties(channelName);
    }

//    @Override
//    public Map<String, ?> getBindings() {
//        return null;
//    }

    @Override
    public String getDefaultsPrefix() {
        return this.extendedBindingProperties.getDefaultsPrefix();
    }

    @Override
    public Class<? extends BinderSpecificPropertiesProvider> getExtendedPropertiesEntryClass() {
        return this.extendedBindingProperties.getExtendedPropertiesEntryClass();
    }
}
