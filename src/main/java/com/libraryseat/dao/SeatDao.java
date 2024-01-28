package com.libraryseat.dao;

import com.libraryseat.pojo.Seat;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Repository
public class SeatDao extends BaseDao{
//    static class SeatMapper implements RowMapper<Seat> {
//        private SeatMapper() {}
//
//        static final SeatMapper INSTANCE = new SeatMapper();
//        @Override
//        public Seat mapRow(ResultSet resultSet, int i) throws SQLException {
//            Seat seat = new Seat();
//            seat.setSeatid(resultSet.getInt("seatid"));
//            seat.setRoomid(resultSet.getInt("roomid"));
//            seat.setStatus(resultSet.getShort("status"));
//            return seat;
//        }
//    }
    private static final BeanPropertyRowMapper<Seat> MAPPER = new BeanPropertyRowMapper<>(Seat.class);
    @Override
    public void add(Object o) {
        assert (o instanceof Seat);
        Seat seat = (Seat) o;
        String sql = "insert into seat(seatid,roomid) values (?,?)";
        template.update(sql,seat.getSeatid(),seat.getRoomid());
    }

    @Override
    public void delete(Object o) {
        assert (o instanceof Seat);
        Seat seat = (Seat) o;
        if(seat.getStatus() != 0)
            return;
        String sql = "delete from seat where seatid=? and roomid=?";
        template.update(sql,seat.getSeatid(),seat.getRoomid());
    }

    @Override
    public int update(Object o) {
        assert (o instanceof Seat);
        Seat seat = (Seat) o;
        String sql = "update seat set `status`=? where seatid=? and roomid=? and `status`<>?"; //status<>是为了并发问题
        return template.update(sql,seat.getStatus(),seat.getSeatid(),seat.getRoomid(),seat.getStatus());
    }

    public List<Seat> getSeatsInRoom(int roomid, int start, int rows, String orderBy, Order order) {
        String sql = "select * from seat where 1=1";
        Map<String,String> conditions = new HashMap<>(1);
        conditions.put("roomid",Integer.toString(roomid));
        return super.findByPage(sql, MAPPER,start,rows,conditions,orderBy,order);
    }

    public List<Seat> getFreeSeatsInRoom(int roomid, int start, int rows, String orderBy, Order order) {
        String sql = "select * from seat where status=0";
        Map<String,String> conditions = new HashMap<>(1);
        conditions.put("roomid",Integer.toString(roomid));
        return super.findByPage(sql, MAPPER,start,rows,conditions,orderBy,order);
    }
    //虽然也可通过上面的方法.size()，但可能性能啥的会有问题
    public int getFreeSeatsInRoom(int roomid) {
        String sql = "select count(*) from seat where roomid=? and `status`=0";
        Integer ret = template.queryForObject(sql,Integer.class,roomid);
        return ret == null ? -1 : ret;
    }

    public int getNonFreeSeatsInRoom(int roomid) {
        String sql = "select count(*) from seat where roomid=? and `status`<>0";
        Integer ret = template.queryForObject(sql,Integer.class,roomid);
        return ret == null ? -1 : ret;
    }

    public Seat getSeatById(int seatid, int roomid) {
        String sql = "select * from seat where seatid=? and roomid=?";
        return template.queryForObject(sql,MAPPER,seatid,roomid);
    }
}
