package com.libraryseat.dao;

import com.libraryseat.pojo.Room;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
//阅览室不会太多，无需分页
@Repository
public class RoomDao extends BaseDao{
    private static final BeanPropertyRowMapper<Room> MAPPER = new BeanPropertyRowMapper<>(Room.class);
    @Override
    public void add(Object o) {
        assert (o instanceof Room);
        Room room = (Room) o;
        String sql = "insert into room(roomname, `admin`) values (?,?)";
        template.update(sql,room.getRoomname(),room.getAdmin());
    }

    @Override
    public void delete(Object o) { //TODO:要不要改成逻辑删除？
        assert (o instanceof Room);
        String sql = "delete from room where roomid=? and `admin`>0";
        template.update(sql,((Room) o).getRoomid());
    }

    @Override
    public int update(Object o) {
        assert (o instanceof Room);
        Room room = (Room) o;
        String sql = "update room set roomname=?,`admin`=? where roomid=? and `admin`>0";
        return template.update(sql,room.getRoomname(),room.getAdmin(),room.getRoomid());
    }

    public List<Room> getRoomsByAdministrator(int admin) {
        String sql = "select * from room where `admin`=?";
        return template.query(sql, MAPPER,admin);
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
        return template.query(sql, MAPPER);
    }

    public Room getRoomById(int id) {
        String sql = "select * from room where roomid=? and `admin`>0";
        try {
            return template.queryForObject(sql, MAPPER,id);
        } catch (DataAccessException e) {
            return null;
        }
    }
}
