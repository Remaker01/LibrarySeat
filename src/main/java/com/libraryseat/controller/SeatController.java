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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @RequestMapping(value = "/show.do",method = {RequestMethod.GET})
    public String toSeatsPage(Integer roomid, @RequestParam(required = false) Integer page, HttpServletResponse resp, HttpSession session)
            throws IOException {
        User u = (User) session.getAttribute("user");
        if (u == null) {
            resp.sendError(403,"校验失败");
            return null;
        }
        resp.setHeader("Cache-Control","no-cache");
        StringBuilder target = new StringBuilder(String.format("/library/seat-role%d.html?",u.getRole()));
        if (roomid != null) {
            target.append("roomid=").append(roomid); //room!=null,page=null
            if (page != null)
                target.append("&page=?").append(page); //room,page!=null
        }
        else if (page != null) //room=null,page!=null
            target.append("page=").append(page);
        return target.toString();
    }
    @RequestMapping(value = "/add.do",method = {RequestMethod.POST})
    @ResponseBody
    public void addSeat(Integer seatid, Integer roomid, HttpServletResponse resp, HttpSession session) throws IOException {
        User u = (User)session.getAttribute("user");
        if (!checkUserRole(roomid,u,resp))
            return;
        String info = seatService.addSeat(roomid,seatid);
        JsonUtil.writeResponse(new Response("/seat/add.do","POST",info),resp.getOutputStream());
    }

    @RequestMapping(value = "/delete.do",method = {RequestMethod.POST})
    @ResponseBody
    public void deleteSeat(Integer seatid, Integer roomid, HttpServletResponse resp, HttpSession session) throws IOException {
        User u = (User)session.getAttribute("user");
        if (!checkUserRole(roomid,u,resp))
            return;
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
        JsonUtil.writeCollection(seats,resp.getOutputStream());
    }
    @RequestMapping(value = "/getseat.do",method = {RequestMethod.POST})
    @ResponseBody
    public void getSeat(Integer seatid, Integer roomid,HttpServletResponse resp, HttpSession session)
        throws IOException{
        User u = (User)session.getAttribute("user");
        if (u == null) {
            resp.sendError(403,"校验失败");
            return;
        }
        resp.setContentType("application/json");
        Seat seat = seatService.getSeatById(seatid,roomid);
        JsonUtil.writePojo(seat,resp.getOutputStream());
    }
    @RequestMapping(value = "/count.do",method = {RequestMethod.POST})
    @ResponseBody
    public void getSeatCount(Integer roomid, @RequestParam(required = false)Short status, HttpServletResponse resp, HttpSession session)
        throws IOException{
        User u = (User)session.getAttribute("user");
        if (u == null) {
            resp.sendError(403,"校验失败");
            return;
        }
        Response response = new Response("/seat/count.do","POST","查找成功");
        HashMap<String,Integer> extra = new HashMap<>(2);
        int count,totalCount;
        if (status != null) {
            count = seatService.getSeatCountInRoom(roomid,status);
            extra.put("count",count);
        }
        totalCount = seatService.getTotalSeatCountInRoom(roomid);
        extra.put("totalCount",totalCount);
        response.setExtra(extra);
        JsonUtil.writeResponse(response,resp.getOutputStream());
    }
}