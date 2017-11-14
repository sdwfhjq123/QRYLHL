package com.qryl.qrylyh.VO.MakeList;

/**
 * Created by hp on 2017/10/30.
 */

public class MakeList {
    private Data data;
    private String resultCode;


    public MakeList(Data data, String resultCode) {
        this.data = data;
        this.resultCode = resultCode;
    }

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
