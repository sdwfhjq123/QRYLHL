package com.qryl.qrylyh.VO.MakeList;

/**
 * Created by hp on 2017/10/30.
 */

public class DrugsList {
    private int amount;
    private String drugName;

    public DrugsList(int amount, String drugName) {
        this.amount = amount;
        this.drugName = drugName;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }
}
