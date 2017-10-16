package com.qryl.qrylyh.VO.OrderVO;

import java.util.List;

/**
 * Created by yinhao on 2017/9/22.
 */

public class OrderInfo {
    private String total;
    private List<OrderInfoArea> data;

    public OrderInfo(String total, List<OrderInfoArea> data) {
        this.total = total;
        this.data = data;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<OrderInfoArea> getData() {
        return data;
    }

    public void setData(List<OrderInfoArea> data) {
        this.data = data;
    }
}
