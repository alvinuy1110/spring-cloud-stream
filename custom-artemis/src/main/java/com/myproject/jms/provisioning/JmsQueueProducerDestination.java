package com.myproject.jms.provisioning;


import org.springframework.cloud.stream.provisioning.ProducerDestination;
import org.springframework.cloud.stream.provisioning.ProvisioningException;
import org.springframework.jms.support.JmsUtils;

import javax.jms.JMSException;
import javax.jms.Queue;

public class JmsQueueProducerDestination implements ProducerDestination {
    // TODO review this, what if topic???
    private final Queue queue;

    public JmsQueueProducerDestination(final Queue queue) {
        this.queue = queue;
    }

    @Override
    public String getName() {
        try {
            return this.queue.getQueueName();
        }
        catch (JMSException e) {
            throw new ProvisioningException("Error getting queue name",
                    JmsUtils.convertJmsAccessException(e));
        }
    }

    @Override
    public String getNameForPartition(int partition) {

        // TODO
        throw new UnsupportedOperationException("Partitioning is not implemented for Jms Producer Queue.");
    }

    @Override
    public String toString() {
        return "JmsQueueProducerDestination{" + "queue=" + queue + '}';
    }
}