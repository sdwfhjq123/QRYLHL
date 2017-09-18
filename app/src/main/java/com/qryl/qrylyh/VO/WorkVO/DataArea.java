package com.qryl.qrylyh.VO.WorkVO;

/**
 * Created by hp on 2017/9/18.
 */

public class DataArea {
    private int id;
    private String name;
    private String note;

    public DataArea(int id, String name, String note) {
        this.id = id;
        this.name = name;
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
