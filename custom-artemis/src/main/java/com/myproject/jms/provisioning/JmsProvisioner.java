package com.myproject.jms.provisioning;

import com.myproject.jms.config.JmsConsumerProperties;
import com.myproject.jms.config.JmsProducerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.binder.ExtendedConsumerProperties;
import org.springframework.cloud.stream.binder.ExtendedProducerProperties;
import org.springframework.cloud.stream.provisioning.ConsumerDestination;
import org.springframework.cloud.stream.provisioning.ProducerDestination;
import org.springframework.cloud.stream.provisioning.ProvisioningException;
import org.springframework.cloud.stream.provisioning.ProvisioningProvider;
import org.springframework.jms.support.JmsUtils;

import javax.jms.*;

@Slf4j
public class JmsProvisioner
        implements ProvisioningProvider<ExtendedConsumerProperties<JmsConsumerProperties>, ExtendedProducerProperties<JmsProducerProperties>> {

    private final ConnectionFactory connectionFactory;

    public JmsProvisioner(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    // TODO
    @Override
    public ProducerDestination provisionProducerDestination(String name, ExtendedProducerProperties<JmsProducerProperties> properties) throws ProvisioningException {
        log.info("provision Producer destination: {}", name);
        // TODO
//        properties.getExtension().getDlqName()
        String destName = name; //"test.Input";
        Session session = null;
        Connection connection = null;
        try {

            // TODO isnt this redundant with JmsMessageChannelBinder??
            connection = connectionFactory.createConnection();
            session = connection.createSession(true, JMSContext.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue(destName);
            return new JmsQueueProducerDestination(queue);

        } catch (JMSException e) {
            throw new ProvisioningException("Provisioning failed", JmsUtils.convertJmsAccessException(e));
        } finally {
            JmsUtils.closeSession(session);
            JmsUtils.closeConnection(connection);
        }
    }

    @Override
    public ConsumerDestination provisionConsumerDestination(String name, String group, ExtendedConsumerProperties<JmsConsumerProperties> properties) throws ProvisioningException {

        log.info("provision Consumer destination: {}", name);
        // TODO
//        properties.getExtension().getDlqName()
        String destName = name; //"test.Output";
        Session session = null;
        Connection connection = null;
        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(true, JMSContext.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue(destName);
            return new JmsConsumerDestination(queue);

        } catch (JMSException e) {
            throw new ProvisioningException("Provisioning failed", JmsUtils.convertJmsAccessException(e));
        } finally {
            JmsUtils.closeSession(session);
            JmsUtils.closeConnection(connection);
        }
    }


//    protected ActiveMQQueue createActiveMQQueue(String destinationName) {
//        return new ActiveMQQueue(destinationName);
//    }

//    @Override
//    public JmsConsumerProperties getExtendedConsumerProperties(String channelName) {
//        return this.extendedBindingProperties.getExtendedConsumerProperties(channelName);
//    }
//
//    @Override
//    public JmsProducerProperties getExtendedProducerProperties(String channelName) {
//        return this.extendedBindingProperties.getExtendedProducerProperties(channelName);
//    }

//    private Queue createQueue(String topicName, Session session, String consumerName) throws JMSException {
//
//        MessageConsumer consumer = session.createConsumer(queue);
//        session.createConsumer()
//        Queue queue = session.createQueue(String.format("Consumer.%s.VirtualTopic.%s", consumerName, topicName));
//        //TODO: Understand why a producer is required to actually create the queue, it's not mentioned in ActiveMQ docs
//        session.createProducer(queue).close();
//        return queue;
//    }
}
