package org.com;

import java.math.BigDecimal;
import java.util.List;

public class Order {
    private final String orderID;
    private final List<Product> orderProductList;
    private final BigDecimal orderTotalPrice;
    private final BigDecimal orderTotalPriceWithDiscount;
    private final OrderStatus orderCurrentStatus;
    private final String orderDeliveryAddress;

    public Order(String orderID, List<Product> orderProductList, BigDecimal orderTotalPrice, BigDecimal orderTotalPriceWithDiscount, OrderStatus orderCurrentStatus, String orderDeliveryAddress) {
        this.orderID = orderID;
        this.orderProductList = orderProductList;
        this.orderTotalPrice = orderTotalPrice;
        this.orderTotalPriceWithDiscount = orderTotalPriceWithDiscount;
        this.orderCurrentStatus = orderCurrentStatus;
        this.orderDeliveryAddress = orderDeliveryAddress;
    }

    public String getOrderID() {
        return orderID;
    }

    public List<Product> getOrderProductList() {
        return orderProductList;
    }

    public BigDecimal getOrderTotalPrice() {
        return orderTotalPrice;
    }

    public BigDecimal getOrderTotalPriceWithDiscount() {
        return orderTotalPriceWithDiscount;
    }

    public OrderStatus getOrderCurrentStatus() {
        return orderCurrentStatus;
    }

    public String getOrderDeliveryAddress() {
        return orderDeliveryAddress;
    }
}