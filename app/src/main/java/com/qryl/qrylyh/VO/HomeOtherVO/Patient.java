package com.qryl.qrylyh.VO.HomeOtherVO;

/**
 * Created by yinhao on 2017/10/24.
 */

public class Patient {
    private int id;//病人id
    private int puId;//病患端用户登录id
    private String name;

    public Patient(int id, int puId, String name) {
        this.id = id;
        this.puId = puId;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPuId() {
        return puId;
    }

    public void setPuId(int puId) {
        this.puId = puId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
