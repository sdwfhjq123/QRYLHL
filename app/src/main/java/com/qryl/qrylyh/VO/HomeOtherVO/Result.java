package com.qryl.qrylyh.VO.HomeOtherVO;

/**
 * Created by hp on 2017/11/17.
 */

public class Result {

    private String id;//订单id
    private int status;//用户状态
    private int orderType;
    private Patient patient;
    private DoctorNurse doctorNurse;

    public Result(String id, int status, int orderType, Patient patient, DoctorNurse doctorNurse) {
        this.id = id;
        this.status = status;
        this.orderType = orderType;
        this.patient = patient;
        this.doctorNurse = doctorNurse;
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
        return doctorNurse;
    }

    public void setDoctorNurse(DoctorNurse doctorNurse) {
        this.doctorNurse = doctorNurse;
    }
}
