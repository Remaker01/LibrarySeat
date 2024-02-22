package com.libraryseat.services;

import com.libraryseat.dao.BaseDao;
import com.libraryseat.dao.ReservationDao;
import com.libraryseat.dao.SeatDao;
import com.libraryseat.pojo.Reservation;
import com.libraryseat.pojo.Seat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Service
public class ReservationService {
    public static final int PAGE_SIZE = 20;
    @Autowired
    private ReservationDao reservationDao;
    @Autowired
    @Lazy
    private SeatDao seatDao;
    private static final Logger LOGGER = LogManager.getLogger(ReservationService.class.getName());

    public String addReservation(int seatid, int roomid, int uid) {
        Seat seat = seatDao.getSeatById(seatid,roomid);
        if(seat == null||seat.getStatus() != (short) 0)
            return "预定失败，座位不存在或已被占用";
        seat.setStatus((short) 1);
        if(reservationDao.getActiveReservationCountByUser(uid) > 0){
            return "不可重复预定座位！";
        }
        if (seatDao.update(seat) == 0)
            return "锁定座位失败，请稍后重试。";
        Timestamp timestamp = new Timestamp((System.currentTimeMillis()/1000)*1000L);
        Reservation reservation = new Reservation(seatid,roomid,uid,timestamp);
        try {
            reservationDao.add(reservation);
            return "预定成功，请在30分钟内签到，逾期视为自动放弃！";
        } catch (DataAccessException e) { //bug1:预定失败没有回退
            seat.setStatus((short) 0);
            seatDao.update(seat);
            LOGGER.error("",e);
            return "预定失败，请检查1. 座位是否已被占用，2.用户是否存在";
        }
    }

    public String removeReservation(int seatid, int roomid, int uid, Date resTime) {
        Timestamp old = new Timestamp(resTime.getTime());
        Reservation reservation = reservationDao.getReservation(seatid,roomid,uid,old);
        if (reservation == null){
            return "删除失败，预定信息不存在！";
        }
        if (reservation.getSignoutTime() == null)
            return "只能删除已签退/放弃的预定信息！";
        try {
            reservationDao.delete(reservation);
            return "删除成功！";
        } catch (DataAccessException e) {
            LOGGER.error("",e);
            return "删除失败，请确认预定信息合法且用户存在！";
        }
    }

    public String signIn(int seatid, int roomid, int uid, Date resTime) {
        Timestamp old = new Timestamp(resTime.getTime());
        Timestamp now = new Timestamp((System.currentTimeMillis()/1000)*1000L);
        Reservation reservation = reservationDao.getReservation(seatid,roomid,uid,old);
        if(reservation == null)
            return "签到失败，该预定信息不存在！";
        if(reservation.getSignoutTime() != null) {
            return "无法对已签退/放弃的预定信息操作！";
        }
        if (reservation.getSigninTime() != null) {
            return "不可重复签到！";
        }
        reservation.setSigninTime(now);
//        Seat seat = new Seat(seatid,roomid);
//        seat.setStatus((short) 2); //已签到
        if(reservationDao.updateWithStatus(reservation,(short) 2) == 0) {
            return "签到失败,请稍后重试。";
        }
//        reservationDao.update(reservation); //既然预定信息存在，就一定能更新成功
//        seatDao.update(seat);
        return "签到成功！";
    }
    /**签退或放弃座位*/
    public String signOut(int seatid, int roomid, int uid, Date resTime) {
        Timestamp old = new Timestamp(resTime.getTime());
        Reservation reservation = reservationDao.getReservation(seatid,roomid,uid,old);
        if (reservation == null) {
            return "签退失败，该预定信息不存在！";
        }
        if (reservation.getSignoutTime() != null) {
            return "无法对已签退的预定信息操作！";
        }
        //即使未签到，也可放弃座位。故不校验是否有签到时间
        Timestamp now = new Timestamp((System.currentTimeMillis()/1000)*1000L);
        reservation.setSignoutTime(now);
        if(reservationDao.updateWithStatus(reservation,(short) 0) == 0) {
            return "操作失败，请稍后重试。";
        }
        return "操作成功！";
    }

    public List<Reservation> getReservations(int uid, int page) {
        return reservationDao.getReservationsByUser(uid,(page-1)*PAGE_SIZE,PAGE_SIZE,"reservation_time", BaseDao.Order.DESCEND);
    }

    public List<Reservation> getReservations(int page){
        return reservationDao.getReservations((page-1)*PAGE_SIZE,PAGE_SIZE,"reservation_time", BaseDao.Order.DESCEND);
    }
}