package ru.szherbekov.emailnotificationms.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import ru.stasila_zh.core.ProductCreatedEvent;
import ru.szherbekov.emailnotificationms.exception.NonRetryableException;
import ru.szherbekov.emailnotificationms.exception.RetryableException;
import ru.szherbekov.emailnotificationms.persistence.entity.ProcessedEventEntity;
import ru.szherbekov.emailnotificationms.persistence.repository.ProcessedEventRepository;

import java.util.ArrayList;
import java.util.List;

@Component
@KafkaListener(topics = "product-created-events-topic")
// groupId = "product-created-events - св-во для установки групайди, чтобы читать нескольким консьюмерам(инстансов) разные сообщения
// второй способ через app.properties - spring.kafka.consumer.group-id=product-created-events
// 3-й джава конфиг - KafkaConfig
@Slf4j
@RequiredArgsConstructor
public class ProductCreatedEventHandler {

    private final RestTemplate restTemplate;
    private final ProcessedEventRepository eventRepository;

    @Transactional
    @KafkaHandler
    public void handle(@Payload ProductCreatedEvent productCreatedEvent,
                       @Header("messageId") String messageId,
                       @Header(KafkaHeaders.RECEIVED_KEY) String messageKey) {

        log.info("Received event: {}, productId {}", productCreatedEvent.getTitle(), productCreatedEvent.getProductId());
        ProcessedEventEntity processedEventEntity = eventRepository.findByMessageId(messageId);
        if (processedEventEntity != null) {
            log.info("Duplicate message id: {}", messageId);
            return;
        }

        try {
            String url = "http://localhost:8090/response/200";
            ResponseEntity<String> response =
                    restTemplate.exchange(url, HttpMethod.GET, null, String.class);
            if (response.getStatusCode().value() == HttpStatus.OK.value()) {
                log.info("Received response: {}", response.getBody());
            }
        } catch (ResourceAccessException e) {
            log.error(e.getMessage());
            throw new RetryableException(e);
        } catch (HttpServerErrorException e) {
            log.error(e.getMessage());
            throw new NonRetryableException(e);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new NonRetryableException(e);
        }

        try {
            eventRepository.save(new ProcessedEventEntity(
                    messageId,
                    productCreatedEvent.getProductId())
            );
        } catch (DataIntegrityViolationException e) {
            log.error(e.getMessage());
            throw new NonRetryableException(e);
        }
    }
    public int[] twoSum(int[] nums, int target) {
        //  int i = 0;
        List<Integer> result = new ArrayList(nums.length);
        for (int i = 0; nums[i] < target; i++){

            if (nums[i] + nums[i+1] == target){

                result.add(i);
                result.add(i+1);
            }
        }
        int[] array = result.stream().mapToInt(t -> t).toArray();
        return array;
    }

}
