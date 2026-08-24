package org.com;

import java.math.BigDecimal;
import java.util.List;

public class Product {
    private Long productId;
    private String productName;
    private CategoryOfProd productCategory;
    private String productBrand;
    private BigDecimal productPrice;
    private List<String> productKeywords;

    public Product(Long productId,
                   String productName,
                   CategoryOfProd productCategory,
                   String productBrand,
                   BigDecimal productPrice,
                   List<String> productKeywords) {
        this.productId = productId;
        this.productName = productName;
        this.productCategory = productCategory;
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

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductCategory(CategoryOfProd productCategory) {
        this.productCategory = productCategory;
    }

    public void setProductBrand(String productBrand) {
        this.productBrand = productBrand;
    }

    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }

    public void setProductKeywords(List<String> productKeywords) {
        this.productKeywords = productKeywords;
    }
}