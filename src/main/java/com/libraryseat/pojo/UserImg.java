package com.libraryseat.pojo;

import java.sql.Date;

public class UserImg {
    private int uid;
    private String imgBase64;
    private Date lastupd;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getImgBase64() {
        return imgBase64;
    }

    public void setImgBase64(String imgBase64) {
        this.imgBase64 = imgBase64;
    }

    public Date getLastupd() {
        return lastupd;
    }

    public void setLastupd(Date lastupd) {
        this.lastupd = lastupd;
    }
}
