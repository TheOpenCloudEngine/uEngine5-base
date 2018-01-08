package org.uengine.five.eventsourcing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * Created by uengine on 2018. 1. 8..
 */
public class EventSender {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendBusinessEvent(String payload) {
        kafkaTemplate.send("bpm.topic", payload);
    }

}

