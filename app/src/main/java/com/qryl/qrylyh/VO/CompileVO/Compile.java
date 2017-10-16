package com.qryl.qrylyh.VO.CompileVO;

/**
 * Created by yinhao on 2017/10/9.
 */

public class Compile {
    private Data data;
    private String resultCode;

    public Compile(Data data, String resultCode) {
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
