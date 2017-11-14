package com.qryl.qrylyh.VO.MakeList;

import java.util.List;

/**
 * Created by hp on 2017/10/30.
 */

public class DataArea {
    private int id;
    private Double price;
    private Patient patient;
    private DoctorNurse doctorNurse;
    private Long createTime;
    private List<DrugsList> drugsList;

    public DataArea(int id, Double price, Patient patient, DoctorNurse doctorNurse, Long createTime, List<DrugsList> drugsList) {
        this.id = id;
        this.price = price;
        this.patient = patient;
        this.doctorNurse = doctorNurse;
        this.createTime = createTime;
        this.drugsList = drugsList;
    }

    public List<DrugsList> getDrugsList() {
        return drugsList;
    }

    public void setDrugsList(List<DrugsList> drugsList) {
        this.drugsList = drugsList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
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

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}
