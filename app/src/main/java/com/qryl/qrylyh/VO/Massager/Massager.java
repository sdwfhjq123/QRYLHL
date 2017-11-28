package com.qryl.qrylyh.VO.Massager;

/**
 * Created by yinhao on 2017/10/24.
 */

public class Massager {
    private int loginId;
    private int serviceNum;
    private int status;//0空闲 1上班未接单 2已接单
    private String realName;

    public Massager(int loginId, int serviceNum, int status, String realName) {
        this.loginId = loginId;
        this.serviceNum = serviceNum;
        this.status = status;
        this.realName = realName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
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
