package com.slk.drools.db.entity;

import lombok.Data;

@Data
public class Order {
    //优惠前价格
    private Double originalPrice;
    //优惠后价格
    private Double realPrice;

    public Order(Double originalPrice) {
        this.originalPrice = originalPrice;
    }
}
