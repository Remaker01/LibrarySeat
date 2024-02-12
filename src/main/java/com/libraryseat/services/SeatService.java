package com.libraryseat.services;

import com.libraryseat.dao.BaseDao;
import com.libraryseat.dao.SeatDao;
import com.libraryseat.pojo.Seat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeatService {
    @Autowired
    private SeatDao seatDao;
    public static final int PAGE_SIZE = 20;

    public String addSeat(int roomid, int seatid) {
        try {
            Seat seat = new Seat();
            seat.setRoomid(roomid);
            seat.setSeatid(seatid);
            seatDao.add(seat);
            return "添加成功！";
        } catch (DataAccessException e){
            return "添加失败，请确认阅览室存在，且阅览室内没有编号相同的座位！";
        }
    }

    public String removeSeat(int roomid, int seatid) {
        try {
            Seat seat = new Seat();
            seat.setRoomid(roomid);
            seat.setSeatid(seatid);
            seatDao.delete(seat);
            return "删除成功！";
        } catch (DataAccessException e) {
            return "删除失败，请确认阅览室存在！";
        }
    }

    public String setStatus(int roomid, int seatid, short status) {
        Seat seat = new Seat(roomid,seatid);
        seat.setStatus(status);
        return (seatDao.update(seat) != 0) ? "更新成功！" : "更新失败！";
    }

    public String setFree(int roomid, int seatid) {
        return setStatus(roomid,seatid,(short) 0);
    }

    public String setReserved(int roomid, int seatid) {
        return setStatus(roomid,seatid,(short) 1);
    }

    public String setOccupied(int roomid,int seatid) {
        return setStatus(roomid,seatid, (short) 2);
    }

    public List<Seat> getSeatsInRoom(int roomid, int page, String orderBy, boolean descend) {
        BaseDao.Order order = (descend) ? BaseDao.Order.DESCEND : BaseDao.Order.ASCEND;
        return seatDao.getSeatsInRoom(roomid,(page-1)*PAGE_SIZE,PAGE_SIZE,orderBy,order);
    }

    public List<Seat> getSeatsInRoom(int roomid, int page) {
        return seatDao.getSeatsInRoom(roomid,(page-1)*PAGE_SIZE,PAGE_SIZE,null,null);
    }

    public List<Seat> getFreeSeatsInRoom(int roomid, int page, String orderBy, boolean descend) {
        BaseDao.Order order = (descend) ? BaseDao.Order.DESCEND : BaseDao.Order.ASCEND;
        return seatDao.getFreeSeatsInRoom(roomid,(page-1)*PAGE_SIZE,PAGE_SIZE,orderBy,order);
    }
}
