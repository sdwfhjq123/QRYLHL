package com.qryl.qrylyh.VO.BeGoodAtWorkVO;

import java.util.List;

/**
 * Created by hp on 2017/9/18.
 * 擅长的工作实体类
 */

public class BeGoodAtWork {
    private List<Data> data;

    public BeGoodAtWork(List<Data> data) {
        this.data = data;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }
}
