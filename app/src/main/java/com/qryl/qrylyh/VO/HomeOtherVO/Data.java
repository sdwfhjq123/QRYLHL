package com.qryl.qrylyh.VO.HomeOtherVO;

/**
 * Created by yinhao on 2017/10/24.
 */

public class Data {
    private String id;//订单id
    private int status;//用户状态
    private int orderType;
    private Patient patient;
    private DoctorNurse result;

    public Data(String id, int status, int orderType, Patient patient, DoctorNurse doctorNurse) {
        this.id = id;
        this.status = status;
        this.orderType = orderType;
        this.patient = patient;
        this.result = doctorNurse;
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

    public DoctorNurse getDoctorNurse() {
        return result;
    }

    public void setDoctorNurse(DoctorNurse doctorNurse) {
        this.result = doctorNurse;
    }
}
