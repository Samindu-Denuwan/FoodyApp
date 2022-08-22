package com.jiat.foodyapp.seller.model;

public class ModelOrderSeller {
    String orderId, orderTime, orderStatus, orderCost, orderBy, orderTo, latitude, logitude, deliveryFee;

    public ModelOrderSeller() {
    }

    public ModelOrderSeller(String orderId, String orderTime, String orderStatus, String orderCost, String orderBy, String orderTo, String latitude, String logitude, String deliveryFee) {
        this.orderId = orderId;
        this.orderTime = orderTime;
        this.orderStatus = orderStatus;
        this.orderCost = orderCost;
        this.orderBy = orderBy;
        this.orderTo = orderTo;
        this.latitude = latitude;
        this.logitude = logitude;
        this.deliveryFee = deliveryFee;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderCost() {
        return orderCost;
    }

    public void setOrderCost(String orderCost) {
        this.orderCost = orderCost;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrderTo() {
        return orderTo;
    }

    public void setOrderTo(String orderTo) {
        this.orderTo = orderTo;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLogitude() {
        return logitude;
    }

    public void setLogitude(String logitude) {
        this.logitude = logitude;
    }

    public String getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(String deliveryFee) {
        this.deliveryFee = deliveryFee;
    }
}
