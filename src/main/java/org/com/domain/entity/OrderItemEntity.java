package org.com.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "order_items")
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    // Поля *_snapshot - фиксация цены и названия на момент заказа
    // если завтра ProductEntity.price изменится, order_items.unit_price_snapshot останется прежним
    // клиент увидит цену, по которой реально купил
    @Column(name = "product_name_snapshot", length = 255, nullable = false)
    private String productNameSnapshot;

    @Column(name = "product_brand_snapshot", length = 100)
    private String productBrandSnapshot;

    @Column(name = "product_price_snapshot", precision = 12, scale = 2, nullable = false)
    private BigDecimal productPriceSnapshot;

    @Column(name = "unit_price_snapshot", precision = 12, scale = 2, nullable = false)
    private BigDecimal unitPriceSnapshot;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    public OrderItemEntity(OrderEntity order, ProductEntity product, String productNameSnapshot,
                           String productBrandSnapshot, BigDecimal unitPriceSnapshot,
                           @NotNull(message = "Количество обязательно для отображения в корзине")
                           @Min(value = 1, message = "Количество должно быть не менее 1") int quantity) {
        this.order = order;
        this.product = product;
        this.productNameSnapshot = productNameSnapshot;
        this.productBrandSnapshot = productBrandSnapshot;
        this.unitPriceSnapshot = unitPriceSnapshot;
        this.quantity = quantity;
    }

    //  Если изменится quantity,
    //  subtotal пересчитается автоматически
    public BigDecimal getSubtotal() {
        return unitPriceSnapshot.multiply(BigDecimal.valueOf(quantity));
    }

    // generated
    @Override
    public final boolean equals(Object obj) {
        if (!(obj instanceof OrderItemEntity that)) return false;

        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
