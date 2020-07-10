package com.myproject.jms.binder.jms.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.jms.DefaultJmsHeaderMapper;
import org.springframework.messaging.MessageHeaders;

import javax.jms.Message;
import java.util.HashMap;
import java.util.Map;

/**
 * Replace all header names that contain '-' with '_' due to JMS spec header name
 * constraints.
 * <p>
 * See http://stackoverflow.com/a/30024766/2408961 for context.
 *
 * @author Donovan Muller
 */
@Slf4j
public class SpecCompliantJmsHeaderMapper extends DefaultJmsHeaderMapper {

    @Override
    public void fromHeaders(MessageHeaders headers, Message jmsMessage) {
        Map<String, Object> compliantHeaders = new HashMap<>(headers.size());
        for (Map.Entry<String, Object> entry : headers.entrySet()) {
            if (entry.getKey().contains("-")) {
                String key = entry.getKey().replaceAll("-", "_");
                log.trace("Rewriting header name '{}' to conform to JMS spec", key);
                compliantHeaders.put(key, entry.getValue());
            } else {
                compliantHeaders.put(entry.getKey(), entry.getValue());
            }
        }

        super.fromHeaders(new MessageHeaders(compliantHeaders), jmsMessage);
    }
}