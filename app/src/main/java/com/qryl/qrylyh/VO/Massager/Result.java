package com.qryl.qrylyh.VO.Massager;

/**
 * Created by hp on 2017/11/17.
 */

public class Result {

    private String id;//订单id
    private int status;//用户状态
    private int orderType;
    private Patient patient;
    private Massager massager;

    public Massager getMassager() {
        return massager;
    }

    public void setMassager(Massager massager) {
        this.massager = massager;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

}
