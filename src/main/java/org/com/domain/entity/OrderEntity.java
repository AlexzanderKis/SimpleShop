package org.com.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.com.domain.entity.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// id, user, totalPrice, totalPrice%, status, deliveryAddress, createdAt, items
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "total_price", precision = 12, scale = 2, nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "total_price_with_discount", precision = 12, scale = 2, nullable = false)
    private BigDecimal totalPriceWIthDiscount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    private OrderStatus status = OrderStatus.NEW_ORDER;

    @Column(name = "delivery_address", length = 500, nullable = false)
    private String deliveryAddress;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderItemEntity> items = new ArrayList<>();

    public OrderEntity(String id, UserEntity user,
                       BigDecimal totalPrice, BigDecimal totalPriceWIthDiscount,
                       OrderStatus status, String deliveryAddress,
                       LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.totalPrice = totalPrice;
        this.totalPriceWIthDiscount = totalPriceWIthDiscount;
        this.status = status != null ? status : OrderStatus.NEW_ORDER;
        this.deliveryAddress = deliveryAddress;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public void addItem(OrderItemEntity item) {
        items.add(item);
        item.setOrder(this);
    }

    // gen
    @Override
    public final boolean equals(Object obj) {

        if (!(obj instanceof OrderEntity that)) return false;

        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
