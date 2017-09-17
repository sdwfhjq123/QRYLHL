package com.qryl.qrylyh.VO;

/**
 * Created by yinhao on 2017/9/16.
 */

public class Work {
    private String workName;
    private int workId;

    public Work(String s, int i) {
        this.workName = s;
        this.workId = i;
    }

    public String getWorkName() {
        return workName;
    }

    public void setWorkName(String workName) {
        this.workName = workName;
    }

    public int getWorkId() {
        return workId;
    }

    public void setWorkId(int workId) {
        this.workId = workId;
    }
}
