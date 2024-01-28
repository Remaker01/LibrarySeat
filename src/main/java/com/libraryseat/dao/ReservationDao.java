package com.libraryseat.dao;

import com.libraryseat.pojo.Reservation;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

@Repository
public class ReservationDao extends BaseDao{
    static class ReservationMapper implements RowMapper<Reservation> {
        static final ReservationMapper INSTANCE = new ReservationMapper();
        private ReservationMapper() {}
        @Override
        public Reservation mapRow(ResultSet resultSet, int i) throws SQLException {
            Reservation reservation = new Reservation();
            reservation.setSeatid(resultSet.getInt("seatid"));
            reservation.setRoomid(resultSet.getInt("roomid"));
            reservation.setUid(resultSet.getInt("uid"));
            reservation.setResTime(resultSet.getTimestamp("reservation_time"));
            reservation.setSigninTime(resultSet.getTimestamp("signin_time"));
            reservation.setSignoutTime(resultSet.getTimestamp("signout_time"));
            return reservation;
        }
    }
    @Override
    public void add(Object o) {
        assert (o instanceof Reservation);
        Reservation r = (Reservation) o;
        String sql = "insert into reservation(seatid,roomid,uid,reservation_time,signin_time,signout_time) values (?,?,?,?,?,?)";
        template.update(sql,
                r.getSeatid(),
                r.getRoomid(),
                r.getResTime(),
                r.getSigninTime(),
                r.getSignoutTime());
    }

    @Override
    public void delete(Object o) {
        assert (o instanceof Reservation);
        Reservation r = (Reservation) o;
        String sql = "delete from reservation where seatid=? and roomid=? and uid=? and reservation_time=?";
        template.update(sql,
                r.getSeatid(),
                r.getRoomid(),
                r.getUid(),
                r.getResTime());
    }

    @Override
    public int update(Object o) {
        assert (o instanceof Reservation);
        Reservation r = (Reservation) o;
        String sql = "update reservation set signin_time=?, signout_time=? where seatid=? and roomid=? and uid=? and reservation_time=?";
        return template.update(sql,
                r.getSigninTime(),
                r.getSignoutTime(),
                r.getSeatid(),
                r.getRoomid(),
                r.getResTime());
    }
    /**更新预定表的同时更新座位表。将两次更新操作合并为一次，防止不一致问题，可能提高效率.*/
    public int updateWithStatus(Reservation r,short status) {
        String sql = "update reservation r,seat s set r.signin_time=?, r.signout_time=?, s.status=? " +
                "where r.seatid=s.seatid and r.roomid=s.roomid and " +
                "s.status<>? and r.seatid=? and r.roomid=? and uid=? and reservation_time=?";
        return template.update(sql,
                r.getSigninTime(),
                r.getSignoutTime(),
                status,status,
                r.getSeatid(),
                r.getRoomid(),
                r.getResTime());
    }

    public List<Reservation> getReservationsByUser(int uid,int start,int rows) {
        String sql = "select * from reservation where uid=?";
        HashMap<String,String> condition = new HashMap<>(1);
        condition.put("uid",Integer.toString(uid));
        return super.findByPage(sql,ReservationMapper.INSTANCE,start,rows,condition);
    }

    public List<Reservation> getReservationsByUser(int uid,int start,int rows,String orderBy,Order order) {
        String sql = "select * from reservation where uid=?";
        HashMap<String,String> condition = new HashMap<>(1);
        condition.put("uid",Integer.toString(uid));
        return super.findByPage(sql,ReservationMapper.INSTANCE,start,rows,condition,orderBy,order);
    }

    public Reservation getReservation(int seatid, int roomid, int uid, Timestamp time) {
        String sql = "select * from reservation where seatid=? and roomid=? and uid=? and reservation_time=?";
        try {
            return template.queryForObject(sql,ReservationMapper.INSTANCE,seatid,roomid,uid,time);
        } catch (DataAccessException e) {
            return null;
        }
    }
    //获取某用户未签退的预定信息总数，目前仅在预定座位时校验使用。
    public int getActiveReservationCountByUser(int uid) {
        String sql = "select count(*) from reservation where uid=? and signout_time is not null";
        Integer ret = template.queryForObject(sql,Integer.class,uid);
        return ret == null ? -1 :ret;
    }
}
