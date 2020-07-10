package com.myproject.jms.config;

import org.springframework.cloud.stream.binder.BinderSpecificPropertiesProvider;

public class JmsBindingProperties  implements BinderSpecificPropertiesProvider {

    private JmsConsumerProperties consumer = new JmsConsumerProperties();

    private JmsProducerProperties producer = new JmsProducerProperties();

    @Override
    public JmsConsumerProperties getConsumer() {
        return consumer;
    }

    public void setConsumer(JmsConsumerProperties consumer) {
        this.consumer = consumer;
    }

    @Override
    public JmsProducerProperties getProducer() {
        return producer;
    }

    public void setProducer(JmsProducerProperties producer) {
        this.producer = producer;
    }
}