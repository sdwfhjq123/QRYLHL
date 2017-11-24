package com.qryl.qrylyh.VO.Massager;

/**
 * Created by yinhao on 2017/10/24.
 */

public class Massager {
    private int loginId;
    private int serviceNum;
    private int status;//0空闲 1上班未接单 2已接单

    public Massager(int loginId, int serviceNum, int status) {
        this.loginId = loginId;
        this.serviceNum = serviceNum;
        this.status = status;
    }

    public int getLoginId() {
        return loginId;
    }

    public void setLoginId(int loginId) {
        this.loginId = loginId;
    }

    public int getServiceNum() {
        return serviceNum;
    }

    public void setServiceNum(int serviceNum) {
        this.serviceNum = serviceNum;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
