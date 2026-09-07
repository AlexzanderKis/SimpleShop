package org.com;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private final User user;
    private final String orderID;
    private final List<CartItem> orderProductList;
    private final BigDecimal orderTotalPrice;
    private final BigDecimal orderTotalPriceWithDiscount;
    private final OrderStatus orderCurrentStatus;
    private String orderDeliveryAddress;
    private final LocalDateTime orderCreationDate;

    public Order(User user,
                 String orderID,
                 List<CartItem> orderProductList,
                 BigDecimal orderTotalPrice,
                 BigDecimal orderTotalPriceWithDiscount,
                 OrderStatus orderCurrentStatus,
                 String orderDeliveryAddress,
                 LocalDateTime orderCreationDate) {
        this.user = user;
        this.orderID = orderID;
        this.orderProductList = orderProductList;
        this.orderTotalPrice = orderTotalPrice;
        this.orderTotalPriceWithDiscount = orderTotalPriceWithDiscount;
        this.orderCurrentStatus = orderCurrentStatus;
        this.orderDeliveryAddress = orderDeliveryAddress;
        this.orderCreationDate = orderCreationDate;
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

    public LocalDateTime getOrderCreationDate(){
        return orderCreationDate;
    }

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

    public String getOrderDeliveryAddress() {
        return orderDeliveryAddress;
    }

    public Order setNewStatus(OrderStatus orderNewStatus) {
        return new Order(this.user,
                this.orderID,
                this.orderProductList,
                this.orderTotalPrice,
                this.orderTotalPriceWithDiscount,
                orderNewStatus,
                this.orderDeliveryAddress,
                orderCreationDate);
    }

    public Order setNewOrderID(String newOrderID){
        return new Order(this.user,
                newOrderID,
                this.orderProductList,
                this.orderTotalPrice,
                this.orderTotalPriceWithDiscount,
                this.getOrderCurrentStatus(),
                this.orderDeliveryAddress,
                orderCreationDate);
    }
    public void setOrderDeliveryAddress(String orderNewDeliveryAddress) {
        this.orderDeliveryAddress = orderNewDeliveryAddress;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        System.out.printf("""
                OrderID: %s
                Order date: %s
                Current status: %s
                Delivery address: %s
                Total price: %s
                """, orderID, orderCreationDate, orderCurrentStatus, orderDeliveryAddress, orderTotalPrice);
        for (CartItem item : orderProductList) {
            if (item.getQuantity() == 0) {
                continue;
            }
            stringBuilder.append(String.format("""
                            %s | %s - %s QTY for %s
                            """,
                    item.getProduct().getProductId(),
                    item.getProduct().getProductName(),
                    item.getQuantity(),
                    item.getProduct().getProductPrice().multiply(BigDecimal.valueOf(item.getQuantity()))));
        }
        return stringBuilder.toString();
    }
}