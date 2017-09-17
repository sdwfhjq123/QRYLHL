package com.qryl.qrylyh.VO.HospitalVO;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by hp on 2017/9/14.
 */

public class Hospital {

    private Data data;
    private String resultCode;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }
}
