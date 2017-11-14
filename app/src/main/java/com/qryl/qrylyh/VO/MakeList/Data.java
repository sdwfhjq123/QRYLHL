package com.qryl.qrylyh.VO.MakeList;

import java.util.List;

/**
 * Created by hp on 2017/10/30.
 */

public class Data {
    private List<DataArea> data;
    private int total;

    public Data(List<DataArea> data, int total) {
        this.data = data;
        this.total = total;
    }

    public List<DataArea> getData() {
        return data;
    }

    public void setData(List<DataArea> data) {
        this.data = data;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
