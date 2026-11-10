package org.com.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.com.domain.entity.enums.CategoryOfProd;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "products")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "brand", length = 255, nullable = false)
    private String brand;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 50, nullable = false)
    private CategoryOfProd category;

    @Column(name = "price", length = 12, scale = 2, nullable = false)
    private BigDecimal price;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_keywords", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "keywords")
    private List<String> keywords = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    public ProductEntity(Long id, String name, String brand,
                         CategoryOfProd category, BigDecimal price,
                         List<String> keywords, String imageUrl) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.price = price;
        this.imageUrl = imageUrl;
        if (keywords != null) {
            this.keywords = keywords;
        }
        this.createdAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ProductEntity that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
