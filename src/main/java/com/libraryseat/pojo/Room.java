package com.libraryseat.pojo;

public class Room {
    private int roomid, admin;
    private String roomname;
    private boolean valid = true;

    public int getRoomid() {
        return roomid;
    }

    public void setRoomid(int roomid) {
        this.roomid = roomid;
    }

    public int getAdmin() {
        return admin;
    }

    public void setAdmin(int admin) {
        this.admin = admin;
    }

    public String getRoomname() {
        return roomname;
    }

    public void setRoomname(String roomname) {
        this.roomname = roomname;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Room room = (Room) o;

        if (roomid != room.roomid) return false;
        if (admin != room.admin) return false;
        if (valid != room.valid) return false;
        return roomname != null ? roomname.equals(room.roomname) : room.roomname == null;
    }

    @Override
    public int hashCode() {
        int result = roomid;
        result = 31 * result + admin;
        result = 31 * result + (roomname != null ? roomname.hashCode() : 0);
        result = 31 * result + (valid ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Room{" +
                "roomid=" + roomid +
                ", admin=" + admin +
                ", roomname='" + roomname + '\'' +
                '}';
    }
}
