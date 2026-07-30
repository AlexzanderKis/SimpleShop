package org.com;

import java.math.BigDecimal;
import java.util.List;

public class Product {
    private final String productId;
    private final String productName;
    private final String productBrand;
    private final BigDecimal productPrice;
    private final List<String> productKeywords;

    public Product(String productId, String productName, String productBrand, BigDecimal productPrice, List<String> productKeywords) {
        this.productId = productId;
        this.productName = productName;
        this.productBrand = productBrand;
        this.productPrice = productPrice;
        this.productKeywords = productKeywords;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
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