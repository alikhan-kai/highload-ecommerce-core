package kz.kaspi.core.order;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity 
@Table(name = "orders")
@Getter 
@Setter 
@NoArgsConstructor
@AllArgsConstructor
@Builder 

public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    //метод Spring вызовет сам прямо перед сохранением в базу
    @PrePersist
    protected void onCreate(){
        this.createdAt = LocalDateTime.now();
    }
}
