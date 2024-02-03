package com.libraryseat.services;

import com.libraryseat.dao.RoomDao;
import com.libraryseat.dao.SeatDao;
import com.libraryseat.pojo.Room;
import com.libraryseat.utils.LogUtil;
import com.libraryseat.utils.cache.Cache;
import com.libraryseat.utils.cache.LRUCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

@Service
public class RoomService {
    private static final Cache<Integer,Room> ROOM_CACHE = new LRUCache<>();
    private static final Logger LOGGER = Logger.getLogger(RoomService.class.getName());
    public static final int MAX_ROOMS = 20;
    static {LogUtil.initLogger(LOGGER);}
    @Autowired
    private RoomDao roomDao;
    @Autowired
    @Lazy
    private SeatDao seatDao;

    public String addRoom(String name, int admin) {
        if(roomDao.getRoomCount() >= MAX_ROOMS) {
            return String.format("当前阅览室数量已达上限%d,无法添加！",MAX_ROOMS);
        }
        Room room = new Room();
        room.setRoomname(name);
        room.setAdmin(admin);
        try {
            roomDao.add(room);
            return "添加成功！";
        } catch (DataAccessException e) {
            LogUtil.log(LOGGER,e);
            return "添加失败，请确认1.管理员存在！2.阅览室名称不与其他已存在阅览室相同。";
        }
    }

    public String deleteRoom(int id) {
        Room room = getRoom(id);  //先确认存在，不存在的不能删
        if (room == null) {
            return "阅览室号错误！";
        }
        if(seatDao.getNonFreeSeatsInRoom(id) > 0) {
            return "该阅览室尚有座位未释放，无法删除！";
        }
        try {
            roomDao.delete(room);
            ROOM_CACHE.invalidate(id);
        } catch (DataAccessException e) {
            LogUtil.log(LOGGER,e);
            return "删除失败，请稍后重试！";
        }
        return "删除成功！";
    }

    public String updateRoom(int roomid,String name,int admin) {
        Room room = new Room();
        room.setRoomid(roomid);
        room.setRoomname(name);
        room.setAdmin(admin);
        int ret = roomDao.update(room);
        if (ret != 0) {
            ROOM_CACHE.put(roomid,room);
            return "更新成功！";
        }
        return "更新失败，请确认管理员用户存在！";
    }

    public Room getRoom(int id) {
        Room room = ROOM_CACHE.get(id);
        if (room == null) {
            room = roomDao.getRoomById(id);
            ROOM_CACHE.put(id,room);
        }
        return room;
    }

    public List<Room> getRooms() {
        return roomDao.getAllRooms();
    }

    public List<Room> getRoomsByAdministrator(int uid) {
        return roomDao.getRoomsByAdministrator(uid);
    }

    public int getRoomCount() {
        return roomDao.getRoomCount();
    }
}
