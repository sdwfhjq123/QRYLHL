package com.qryl.qrylyh.VO.WorkVO;

/**
 * Created by yinhao on 2017/9/16.
 */

public class Work {
    private String resultCode;
    private Data data;

    public Work(String resultCode, Data data) {
        this.resultCode = resultCode;
        this.data = data;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
