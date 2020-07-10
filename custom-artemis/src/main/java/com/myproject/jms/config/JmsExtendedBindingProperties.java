package com.myproject.jms.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.stream.binder.AbstractExtendedBindingProperties;
import org.springframework.cloud.stream.binder.BinderSpecificPropertiesProvider;

import java.util.Map;


@ConfigurationProperties("spring.cloud.stream.jms")
public class JmsExtendedBindingProperties extends AbstractExtendedBindingProperties<JmsConsumerProperties, JmsProducerProperties, JmsBindingProperties> {
    //implements ExtendedBindingProperties<JmsConsumerProperties, JmsProducerProperties> {

    public static final String DEFAULT_PREFIX = "spring.cloud.stream.jms.default";
//    private Map<String, JmsBindingProperties> bindings = new HashMap<>();

    public Map<String, JmsBindingProperties> getBindings() {
        return this.doGetBindings();
    }


//    public String getDefaultsPrefix() {
//        return "spring.cloud.stream.kafka.default";
//    }
//
//    public Map<String, KafkaBindingProperties> getBindings() {
//        return this.doGetBindings();
//    }

//    public void setBindings(Map<String, JmsBindingProperties> bindings) {
//        this.bindings = bindings;
//    }

    @Override
    public String getDefaultsPrefix() {
        return DEFAULT_PREFIX;
    }

    @Override
    public Class<? extends BinderSpecificPropertiesProvider> getExtendedPropertiesEntryClass() {
        return JmsBindingProperties.class;
    }

//    @Override
//    public JmsConsumerProperties getExtendedConsumerProperties(String channelName) {
//        if (bindings.containsKey(channelName) && bindings.get(channelName).getConsumer() != null) {
//            return bindings.get(channelName).getConsumer();
//        } else {
//            return new JmsConsumerProperties();
//        }
//    }
//
//    @Override
//    public JmsProducerProperties getExtendedProducerProperties(String channelName) {
//        if (bindings.containsKey(channelName) && bindings.get(channelName).getProducer() != null) {
//            return bindings.get(channelName).getProducer();
//        } else {
//            return new JmsProducerProperties();
//        }
//    }
}