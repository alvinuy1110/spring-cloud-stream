package com.myproject.jms.config;

public class JmsConsumerProperties {

    // TODO : remove this....
    public static final String DEFAULT_DLQ_NAME = "Spring.Cloud.Stream.dlq";

    /** the name of the dead letter queue **/
    private String dlqName = DEFAULT_DLQ_NAME;

    public String getDlqName() {
        return dlqName;
    }

    public void setDlqName(String dlqName) {
        this.dlqName = dlqName;
    }

}