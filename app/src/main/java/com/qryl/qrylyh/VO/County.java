package com.qryl.qrylyh.VO;

import android.inputmethodservice.Keyboard;

/**
 * Created by hp on 2017/9/15.
 */

public class County {
    private String county;
    private int countyId;

    public County(String county, int countyId) {
        this.county = county;
        this.countyId = countyId;
    }

    public int getCountyId() {
        return countyId;
    }

    public void setCountyId(int countyId) {
        this.countyId = countyId;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }



}
