package com.myproject.jms.binder.jms.utils;

import javax.jms.Message;

/**
 * Interface used to define the recovery strategy once the maximum number
 * of delivery attempts has been reached.
 *
 */
public interface MessageRecoverer {

    /**
     * Recover from the failure to deliver a message.
     *
     * @param undeliveredMessage the message that has not been delivered.
     * @param cause the reason for the failure to deliver.
     */
    void recover(Message undeliveredMessage, String dlq, Throwable cause);
}