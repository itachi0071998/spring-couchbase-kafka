//package com.tesco.demo.infrastructure.kafka;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Service;
//
//@Service
//@Slf4j
//public class KafkaProducer {
//    private static final String TOPIC = "minprice";
//
//    @Autowired
//    private KafkaTemplate<String, String> kafkaTemplate;
//
//    public void sendMessage(String message) {
//        this.kafkaTemplate.send(TOPIC, message);
//        log.info("message sent {}", message);
//
//    }
//}
