package com.libraryseat.pojo;

public class Seat {
    private int seatid,roomid;
    private short status;

    public Seat() {}

    public Seat(int seatid, int roomid) {
        this.seatid = seatid;
        this.roomid = roomid;
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

    public short getStatus() {
        return status;
    }

    public void setStatus(short status) {
        if (status < 0)
            throw new IllegalArgumentException();
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Seat seat = (Seat) o;

        if (seatid != seat.seatid) return false;
        if (roomid != seat.roomid) return false;
        return status == seat.status;
    }

    @Override
    public int hashCode() {
        int result = seatid;
        result = 31 * result + roomid;
        result = 31 * result + (int) status;
        return result;
    }

    @Override
    public String toString() {
        return "Seat{" +
                "seatid=" + seatid +
                ", roomid=" + roomid +
                ", status=" + status +
                '}';
    }
}
