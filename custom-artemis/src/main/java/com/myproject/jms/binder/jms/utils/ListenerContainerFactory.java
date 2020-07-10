package com.myproject.jms.binder.jms.utils;


import javax.jms.ConnectionFactory;
import javax.jms.Destination;

import org.springframework.jms.listener.AbstractMessageListenerContainer;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

public class ListenerContainerFactory {

    private ConnectionFactory factory;

    public ListenerContainerFactory(ConnectionFactory factory) {
        this.factory = factory;
    }

    public AbstractMessageListenerContainer build(Destination group) {
        DefaultMessageListenerContainer listenerContainer = new DefaultMessageListenerContainer();
        listenerContainer.setDestination(group);

        // TODO false for queue; true for topic
        listenerContainer.setPubSubDomain(false);
        listenerContainer.setConnectionFactory(factory);
        listenerContainer.setSessionTransacted(true);
        return listenerContainer;
    }
}