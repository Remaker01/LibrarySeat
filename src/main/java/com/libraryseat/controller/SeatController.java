package com.libraryseat.controller;

import com.libraryseat.Response;
import com.libraryseat.pojo.Room;
import com.libraryseat.pojo.Seat;
import com.libraryseat.pojo.User;
import com.libraryseat.services.RoomService;
import com.libraryseat.services.SeatService;
import com.libraryseat.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/seat")
public class SeatController {
    @Autowired
    private SeatService seatService;
    @Autowired
    @Lazy
    private RoomService roomService;

    private boolean checkUserRole(int roomid,User u, HttpServletResponse resp) throws IOException {
        if (u.getRole() == 2) {
            resp.sendError(403,"校验失败");
            return false;
        }
        //1.如果该用户不是超管，则验证其是否为对应roomid的管理员
        if(u.getRole() == 1) {
            Room r = roomService.getRoom(roomid);
            if (r.getAdmin() != u.getUid()) {
                resp.sendError(403,"校验失败");
                return false;
            }
        }
        return true;
    }
    @RequestMapping(value = "/add.do",method = {RequestMethod.POST})
    @ResponseBody
    public void addSeat(Integer seatid, Integer roomid, HttpServletResponse resp, HttpSession session) throws IOException {
        User u = (User)session.getAttribute("user");
        if (!checkUserRole(roomid,u,resp))
            return;
        String info = seatService.addSeat(roomid,seatid);
        resp.setContentType("application/json");
        JsonUtil.writeResponse(new Response("/seat/add.do","POST",info),resp.getOutputStream());
    }

    @RequestMapping(value = "/delete.do",method = {RequestMethod.POST})
    @ResponseBody
    public void deleteSeat(Integer seatid, Integer roomid, HttpServletResponse resp, HttpSession session) throws IOException {
        User u = (User)session.getAttribute("user");
        if (!checkUserRole(roomid,u,resp))
            return;
        resp.setContentType("application/json");
        String info = seatService.removeSeat(roomid,seatid);
        JsonUtil.writeResponse(new Response("/seat/add.do","POST",info),resp.getOutputStream());
    }

    @RequestMapping(value = "/listseats.do",method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public void listSeatsInRoom(Integer roomid, @RequestParam(required = false) Integer page, HttpServletResponse resp, HttpSession session)
            throws IOException {
        User u = (User)session.getAttribute("user");
        if (u == null) {
            resp.sendError(403,"校验失败");
            return;
        }
        if (page == null)
            page = 1;
        resp.setContentType("application/json");
        List<Seat> seats = seatService.getSeatsInRoom(roomid,page);
        JsonUtil.writeList(seats,resp.getOutputStream());
    }
}