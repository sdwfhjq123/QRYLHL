package com.qryl.qrylyh.VO.MakeList;

/**
 * Created by hp on 2017/10/30.
 */

public class DoctorNurse {
    private String realName;
    private int gender;
    private int workYears;
    private int goodRate;
    private int age;

    public DoctorNurse(String realName, int gender, int workYears, int goodRate, int age) {
        this.realName = realName;
        this.gender = gender;
        this.workYears = workYears;
        this.goodRate = goodRate;
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getWorkYears() {
        return workYears;
    }

    public void setWorkYears(int workYears) {
        this.workYears = workYears;
    }

    public int getGoodRate() {
        return goodRate;
    }

    public void setGoodRate(int goodRate) {
        this.goodRate = goodRate;
    }
}


