package com.myproject.jms.provisioning;


import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Topic;

import org.springframework.cloud.stream.provisioning.ProducerDestination;
import org.springframework.cloud.stream.provisioning.ProvisioningException;
import org.springframework.jms.support.JmsUtils;

public class JmsProducerDestination implements ProducerDestination {
// TODO review this!!! what if queue???
    private final Map<Integer, Topic> partitionTopics;

    public JmsProducerDestination(Map<Integer, Topic> partitionTopics) {
        this.partitionTopics = partitionTopics;
    }

    @Override
    public String getName() {
        try {
            return partitionTopics.get(-1).getTopicName();
        }
        catch (JMSException e) {
            throw new ProvisioningException("Error getting topic name",
                    JmsUtils.convertJmsAccessException(e));
        }
    }

    @Override
    public String getNameForPartition(int partition) {
        try {
            return partitionTopics.get(partition).getTopicName();
        }
        catch (JMSException e) {
            throw new ProvisioningException("Error getting topic name",
                    JmsUtils.convertJmsAccessException(e));
        }
    }

    @Override
    public String toString() {
        return "JmsProducerDestination{" + "partitionTopics=" + partitionTopics + '}';
    }
}