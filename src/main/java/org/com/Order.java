package org.com;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class Order {
    private final User user;
    private final String orderID;
    private final List<Product> orderProductList;
    private final BigDecimal orderTotalPrice;
    private final BigDecimal orderTotalPriceWithDiscount;
    private final OrderStatus orderCurrentStatus;
    private String orderDeliveryAddress;

    public Order(User user,
                 String orderID,
                 List<Product> orderProductList,
                 BigDecimal orderTotalPrice,
                 BigDecimal orderTotalPriceWithDiscount,
                 OrderStatus orderCurrentStatus,
                 String orderDeliveryAddress) {
        this.user = user;
        this.orderID = orderID;
        this.orderProductList = orderProductList;
        this.orderTotalPrice = orderTotalPrice;
        this.orderTotalPriceWithDiscount = orderTotalPriceWithDiscount;
        this.orderCurrentStatus = orderCurrentStatus;
        this.orderDeliveryAddress = orderDeliveryAddress;
    }

    public User getUser(){
        return user;
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

//    public void setOrderCurrentStatus(OrderStatus orderNewStatus) {
//        this.orderCurrentStatus = orderNewStatus;
//    }

    public Order newStatus(OrderStatus orderNewStatus) {
        return new Order(this.user,
                this.orderID,
                this.orderProductList,
                this.orderTotalPrice,
                this.orderTotalPriceWithDiscount,
                orderNewStatus,
                this.orderDeliveryAddress);
    }

    public String getOrderDeliveryAddress() {
        return orderDeliveryAddress;
    }

    public void setOrderDeliveryAddress(String orderNewDeliveryAddress) {
        this.orderDeliveryAddress = orderNewDeliveryAddress;
    }
}