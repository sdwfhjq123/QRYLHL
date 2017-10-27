package com.qryl.qrylyh.VO.HomeOtherVO;

/**
 * Created by yinhao on 2017/10/24.
 */

public class HomeOther {
    private Data data;
    private String resultCode;

    public HomeOther(Data data, String resultCode) {
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
