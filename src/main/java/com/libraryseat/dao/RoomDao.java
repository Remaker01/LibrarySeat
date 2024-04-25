package com.libraryseat.dao;

import com.libraryseat.pojo.Room;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
//阅览室不会太多，无需分页
@Repository
public class RoomDao extends BaseDao{
    static class RoomMapper implements RowMapper<Room> {
        private static final RoomMapper INSTANCE = new RoomMapper();
        @Override
        public Room mapRow(ResultSet resultSet, int i) throws SQLException {
            Room room = new Room();
            room.setRoomid(resultSet.getInt("roomid"));
            room.setRoomname(resultSet.getString("roomname"));
            room.setAdmin(resultSet.getInt("admin"));
            room.setValid(resultSet.getByte("valid") == 1);
            return room;
        }
    }
    @Override
    public void add(Object o) {
        Assert.isInstanceOf(Room.class,o);
        Room room = (Room) o;
        String sql = "insert into room(roomname, `admin`,valid) values (?,?,?)";
        template.update(sql,room.getRoomname(),room.getAdmin(),room.isValid() ? 1 : 0);
    }

    @Override
    public void delete(Object o) {
        Assert.isInstanceOf(Room.class,o);
        String sql = "delete from room where roomid=? and `admin`>0";
        template.update(sql,((Room) o).getRoomid());
    }

    @Override
    public int update(Object o) {
        Assert.isInstanceOf(Room.class,o);
        Room room = (Room) o;
        String sql = "update room set roomname=?,`admin`=?,valid=? where roomid=? and `admin`>0";
        return template.update(sql,room.getRoomname(),room.getAdmin(),room.isValid() ? 1 : 0,room.getRoomid());
    }

    public List<Room> getRoomsByAdministrator(int admin) {
        String sql = "select * from room where `admin`=?";
        return template.query(sql, RoomMapper.INSTANCE,admin);
    }

    public int getRoomCount() {
        String sql = "select count(*) from room where `admin`>0";
        Integer count = template.queryForObject(sql,Integer.class);
        if (count == null)
            return -1;
        return count;
    }

    public List<Room> getAllRooms() {
        String sql = "select * from room where `admin`>0";
        return template.query(sql, RoomMapper.INSTANCE);
    }

    public Room getRoomById(int id) {
        String sql = "select * from room where roomid=? and `admin`>0";
        try {
            return template.queryForObject(sql, RoomMapper.INSTANCE,id);
        } catch (DataAccessException e) {
            return null;
        }
    }
}
