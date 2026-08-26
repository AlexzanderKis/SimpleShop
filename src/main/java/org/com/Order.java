package org.com;

import java.math.BigDecimal;
import java.util.List;

public class Order {
    private final User user;
    private final String orderID;
    private final List<CartItem> orderProductList;
    private final BigDecimal orderTotalPrice;
    private final BigDecimal orderTotalPriceWithDiscount;
    private final OrderStatus orderCurrentStatus;
    private String orderDeliveryAddress;

    public Order(User user,
                 String orderID,
                 List<CartItem> orderProductList,
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

    public List<CartItem> getOrderProductList() {
        return orderProductList;
    }

//    public BigDecimal getOrderTotalPrice() {
//        return orderTotalPrice;
//    }

    // Order — это зафиксированный снимок (snapshot) на момент покупки:
    // у него уже есть сохранённая итоговая сумма (orderTotalPrice),
    // которая не должна меняться со временем,
    // даже если цены на товары в каталоге изменятся.
//    public BigDecimal getOrderTotalPrice(Cart cart) {
//        return cart.getTotalPrice();
//    }

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