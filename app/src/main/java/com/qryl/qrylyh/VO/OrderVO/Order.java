package com.qryl.qrylyh.VO.OrderVO;

/**
 * Created by yinhao on 2017/9/22.
 */

public class Order {
    private OrderInfo data;

    public Order(OrderInfo data) {
        this.data = data;
    }

    public OrderInfo getData() {
        return data;
    }

    public void setData(OrderInfo data) {
        this.data = data;
    }
}
