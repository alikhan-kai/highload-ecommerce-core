package kz.kaspi.core.outbox;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity 
@Table(name = "outbox_events")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder

public class OutboxEvent {

    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id; //Уникальный ID события (защита от дубликатов)

    @Column(nullable = false)
    private String aggregateType; //Например, "Product" или "Order"
    
    @Column(nullable = false)
    private String aggregateId; //ID измененной сущности (например, "1")
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload; //Здесь будет JSON с данными

    @Column(nullable = false)
    private String status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist 
    protected void onCreate(){
        this.createdAt = LocalDateTime.now();
    }

}
