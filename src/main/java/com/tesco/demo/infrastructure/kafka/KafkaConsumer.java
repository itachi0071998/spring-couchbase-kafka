//package com.tesco.demo.infrastructure.kafka;
//
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//
//@Service
//@Slf4j
//public class KafkaConsumer {
//    @KafkaListener(topics = "minprice", groupId = "group_id")
//    public void consume(String minprice) throws IOException {
//        log.info("message consumed {}", minprice);
//    }
//}