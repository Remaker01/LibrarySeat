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

import java.io.InputStream;
import java.sql.Timestamp;
import java.util.*;

@Service
public class ReservationService {
    public static final int PAGE_SIZE = 20;
    @Autowired
    private ReservationDao reservationDao;
    @Autowired
    @Lazy
    private SeatDao seatDao;
    private final Calendar open,close;
    private final Calendar latest;
    private String province,city;
    private static final Logger LOGGER = LogManager.getLogger(ReservationService.class.getName());

    public String addReservation(int seatid, int roomid, int uid) {
        Seat seat = seatDao.getSeatById(seatid,roomid);
        if(seat == null||seat.getStatus() != (short) 0)
            return "预定失败，座位不存在或已被占用";
        seat.setStatus((short) 1);
        Timestamp timestamp = new Timestamp((System.currentTimeMillis()/1000)*1000L);
        if (timestamp.getTime() >= latest.getTimeInMillis())
            return String.format("%d:%02d以后不能预定座位！",latest.get(Calendar.HOUR_OF_DAY),latest.get(Calendar.MINUTE));
        if (timestamp.getTime() < open.getTimeInMillis())
            return String.format("尚未到预定时间，请于%d:%02d之后预定！",open.get(Calendar.HOUR_OF_DAY),open.get(Calendar.MINUTE));
        if(reservationDao.getActiveReservationCountByUser(uid) > 0){
            return "不可重复预定座位！";
        }
        if (seatDao.update(seat) == 0)
            return "锁定座位失败，请稍后重试。";
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
        return String.format("签到成功，请于闭馆时间%d:%02d之前退座，逾期将自动退座！",close.get(Calendar.HOUR_OF_DAY),close.get(Calendar.MINUTE));
    }
    /**签退或放弃座位*/
    public String signOut(int seatid, int roomid, int uid, Date resTime) {
        Timestamp old = new Timestamp(resTime.getTime());
        Reservation reservation = reservationDao.getReservation(seatid,roomid,uid,old);
        if (reservation == null) {
            return "操作失败，该预定信息不存在！";
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

    public Map<String,String> getOpenAndCloseTime(){
        HashMap<String,String> result = new HashMap<>(2);
        StringBuilder str = new StringBuilder(5);
        str.append(open.get(Calendar.HOUR_OF_DAY)).append(':');
        if (open.get(Calendar.MINUTE) < 10){
            str.append('0');
        }
        str.append(open.get(Calendar.MINUTE));
        result.put("open",str.toString());

        str.setLength(0);
        str.append(close.get(Calendar.HOUR_OF_DAY)).append(':');
        if (close.get(Calendar.MINUTE) < 10){
            str.append('0');
        }
        str.append(close.get(Calendar.MINUTE));
        result.put("close",str.toString());
        return result;
    }
    //TODO:把这个放到其他类里面？
    public Map<String,String> getLibraryLocation(){
        HashMap<String,String> result = new HashMap<>(2);
        result.put("province",province);
        result.put("city",city);
        return result;
    }
    /**
     * 默认开馆时间8:00，闭馆时间22:00
     */
    public ReservationService(){
        open = Calendar.getInstance(TimeZone.getDefault());
        close = Calendar.getInstance(TimeZone.getDefault());
        Properties properties = new Properties();
        // 想改就到properties里，代码就不用动了
        try(InputStream stream = ReservationService.class.getResourceAsStream("/library.properties")){
            properties.load(stream);
            province = properties.getProperty("location.province");
            city = properties.getProperty("location.city");
            String openHour = properties.getProperty("open.hour","8");
            String closeHour = properties.getProperty("close.hour","22");
            String openMinute = properties.getProperty("open.minute","0");
            String closeMinute = properties.getProperty("close.minute","0");
            setCalendar(open,Integer.parseInt(openHour),Integer.parseInt(openMinute));
            setCalendar(close,Integer.parseInt(closeHour),Integer.parseInt(closeMinute));
            LOGGER.debug("open={}:{}",openHour,openMinute);
        } catch (Exception e) {
            LOGGER.error("配置文件不存在或格式错误：",e);
            setCalendar(open,8,0);
            setCalendar(close,22,0);
        }
        latest = (Calendar) close.clone();
        latest.add(Calendar.MINUTE,-30);
    }

    private static void setCalendar(Calendar calendar,int hour,int minute){
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,minute);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
    }
}