package com.qryl.qrylyh.VO.WalletRecordVO;


import java.util.List;

/**
 * Created by hp on 2017/11/23.
 */

public class WalletRecord {
    private List<Data> data;
    private String resultCode;

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }
}
