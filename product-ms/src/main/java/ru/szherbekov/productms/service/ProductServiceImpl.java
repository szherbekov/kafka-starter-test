package ru.szherbekov.productms.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import ru.stasila_zh.core.ProductCreatedEvent;
import ru.szherbekov.productms.dto.CreateProductDto;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;

    @Override
    public String createProduct(CreateProductDto createProductDto) throws ExecutionException, InterruptedException {
        // TODO save to database
        String productId = UUID.randomUUID().toString();
        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent(
                productId,
                createProductDto.getTitle(),
                createProductDto.getPrice(),
                createProductDto.getQuantity()
        );
        //асинхронно
//        CompletableFuture<SendResult<String, ProductCreatedEvent>> future =
//                kafkaTemplate.send("product-created-events-topic", productId, productCreatedEvent);
//        future.whenComplete((result, exception) -> {
//            if (exception != null) {
//                log.error("Failed to send message: {}", exception.getMessage());
//            } else {
//                log.info("Message sent successfully: {}", result.getRecordMetadata());
//            }
//        });
//        future.join();
        //синхронно

        ProducerRecord<String, ProductCreatedEvent> record = new ProducerRecord<>(
                "product-created-events-topic",
                productId,
                productCreatedEvent
        );
        record.headers().add("messageId", UUID.randomUUID().toString().getBytes());
//        SendResult<String, ProductCreatedEvent> result =
//                kafkaTemplate.send("product-created-events-topic", productId, productCreatedEvent).get();
        SendResult<String, ProductCreatedEvent> result =
                kafkaTemplate.send(record).get();
        log.info("Topic: {}", result.getRecordMetadata().topic());
        log.info("Partition: {}", result.getRecordMetadata().partition());
        log.info("Offset: {}", result.getRecordMetadata().offset());
        log.info("Return: {}", productId);

        return productId;
    }

}
