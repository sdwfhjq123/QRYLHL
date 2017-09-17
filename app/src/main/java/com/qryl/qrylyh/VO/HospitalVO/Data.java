package com.qryl.qrylyh.VO.HospitalVO;

import java.util.List;

/**
 * Created by yinhao on 2017/9/17.
 */

public class Data {
    private String total;
    private List<DataArea> data;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<DataArea> getData() {
        return data;
    }

    public void setData(List<DataArea> data) {
        this.data = data;
    }
}
