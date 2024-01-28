package com.libraryseat.pojo;

import java.util.Date;
import java.sql.Timestamp;
import java.util.Arrays;

public class Reservation {
    private int seatid, roomid, uid;
    private Timestamp resTime;
    private Timestamp signinTime;
    private Timestamp signoutTime;

    public Reservation() {}

    public Reservation(int seatid, int roomid, int uid, Timestamp resTime) {
        this.seatid = seatid;
        this.roomid = roomid;
        this.uid = uid;
        this.resTime = resTime;
    }

    public int getSeatid() {
        return seatid;
    }

    public void setSeatid(int seatid) {
        this.seatid = seatid;
    }

    public int getRoomid() {
        return roomid;
    }

    public void setRoomid(int roomid) {
        this.roomid = roomid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public Timestamp getResTime() {
        return resTime;
    }

    public void setResTime(Timestamp resTime) {
        this.resTime = resTime;
    }

    public Timestamp getSigninTime() {
        return signinTime;
    }

    public void setSigninTime(Timestamp signinTime) {
        this.signinTime = signinTime;
    }

    public Timestamp getSignoutTime() {
        return signoutTime;
    }

    public void setSignoutTime(Timestamp signoutTime) {
        this.signoutTime = signoutTime;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Reservation that = (Reservation) o;

        if (seatid != that.seatid) return false;
        if (roomid != that.roomid) return false;
        if (uid != that.uid) return false;
        return resTime != null ? resTime.equals(that.resTime) : that.resTime == null;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{seatid,roomid,uid,resTime});
    }
}
