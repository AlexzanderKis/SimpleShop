package org.com;

import java.math.BigDecimal;
import java.util.List;

public class Product {
    private final Long productId;
    private final String productName;
    private final CategoryOfProd productCategory;
    private final String productBrand;
    private final BigDecimal productPrice;
    private final List<String> productKeywords;

    public Product(Long productId,
                   String productName,
                   String productCategory,
                   String productBrand,
                   BigDecimal productPrice,
                   List<String> productKeywords) {
        this.productId = productId;
        this.productName = productName;
        this.productCategory = CategoryOfProd.valueOf(productCategory);
        this.productBrand = productBrand;
        this.productPrice = productPrice;
        this.productKeywords = productKeywords;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public CategoryOfProd getProductCategory() {
        return productCategory;
    }

    public String getProductBrand() {
        return productBrand;
    }

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public List<String> getProductKeywords() {
        return productKeywords;
    }
}