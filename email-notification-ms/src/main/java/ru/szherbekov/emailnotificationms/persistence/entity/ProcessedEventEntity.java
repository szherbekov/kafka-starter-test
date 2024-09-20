package ru.szherbekov.emailnotificationms.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "processed_evets")
@Getter
@Setter
public class ProcessedEventEntity {

    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false, unique = true)
    private String messageId;
    @Column(nullable = false)
    private String productId;

    public ProcessedEventEntity() {

    }

    public ProcessedEventEntity(String messageId, String productId) {
        this.messageId = messageId;
        this.productId = productId;
    }
}
