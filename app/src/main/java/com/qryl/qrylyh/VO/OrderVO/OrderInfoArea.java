package com.qryl.qrylyh.VO.OrderVO;

/**
 * Created by yinhao on 2017/9/22.
 */

public class OrderInfoArea {
    private String note;
    private Double price;
    private String id;
    private String title;
    private String content;
    private int orderType;

    public OrderInfoArea(String note, Double price, String id, String title, String content, int orderType) {
        this.note = note;
        this.price = price;
        this.id = id;
        this.title = title;
        this.content = content;
        this.orderType = orderType;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }
}
