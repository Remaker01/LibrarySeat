package com.libraryseat.pojo;

import java.util.Calendar;

public class LibraryMetadata implements Cloneable {
    private Calendar openTime,closeTime,latestReservationTime;
    private String province = ""; //省份
    private String city = ""; //城市

    public LibraryMetadata() {}

    public LibraryMetadata(String province, String city) {
        this.province = province;
        this.city = city;
    }

    public Calendar getOpenTime() {
        return openTime;
    }

    public void setOpenTime(Calendar openTime) {
        this.openTime = openTime;
    }

    public Calendar getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(Calendar closeTime) {
        this.closeTime = closeTime;
    }

    public Calendar getLatestReservationTime() {
        return latestReservationTime;
    }

    public void setLatestReservationTime(Calendar latestReservationTime) {
        this.latestReservationTime = latestReservationTime;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
    /**
     * 对此metadata进行深拷贝
     */
    @Override
    public Object clone() {
        try {
            super.clone();
        } catch (CloneNotSupportedException e) {}
        LibraryMetadata another = new LibraryMetadata(province,city);
        another.setOpenTime((Calendar) openTime.clone());
        another.setCloseTime((Calendar) closeTime.clone());
        another.setLatestReservationTime((Calendar) latestReservationTime.clone());
        return another;
    }
}
