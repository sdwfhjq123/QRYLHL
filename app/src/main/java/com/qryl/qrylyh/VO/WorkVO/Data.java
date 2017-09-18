package com.qryl.qrylyh.VO.WorkVO;

import java.util.List;

/**
 * Created by hp on 2017/9/18.
 */

public class Data {
    private int total;
    private List<DataArea> data;

    public Data(int total, List<DataArea> data) {
        this.total = total;
        this.data = data;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<DataArea> getData() {
        return data;
    }

    public void setData(List<DataArea> data) {
        this.data = data;
    }
}
