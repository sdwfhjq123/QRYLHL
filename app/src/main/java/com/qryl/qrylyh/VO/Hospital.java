package com.qryl.qrylyh.VO;

/**
 * Created by hp on 2017/9/14.
 */

public class Hospital {
    private int id;
    private String hospitalName;

    public Hospital(int i, String s) {
        id = i;
        hospitalName = s;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }
}
