package com.libraryseat.controller;

import com.libraryseat.Response;
import com.libraryseat.pojo.Room;
import com.libraryseat.pojo.User;
import com.libraryseat.services.RoomService;
import com.libraryseat.services.UserService;
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
@RequestMapping("/room")
public class RoomController {
    @Autowired
    private RoomService roomService;
    @Autowired
    @Lazy
    private UserService userService;

    @RequestMapping(value = "/add.do",method = {RequestMethod.POST})
    @ResponseBody
    public void addRoom(String name, Integer admin, HttpServletResponse resp,HttpSession session) throws IOException {
        User now = (User) session.getAttribute("user");
        if (now == null||now.getRole() != 0) {
            resp.sendError(403,"校验失败");
            return;
        }
        resp.setContentType("application/json");
        User adminAccount = userService.getUserById(admin);
        if (adminAccount == null||adminAccount.getRole() != 1) {
            resp.sendError(400,"参数错误");
            return;
        }
        String info = roomService.addRoom(name,admin);
        JsonUtil.writeResponse(new Response("/room/add.do","POST",info),resp.getOutputStream());
    }

    @RequestMapping(value = "/delete.do",method = {RequestMethod.POST})
    @ResponseBody
    public void removeRoom(Integer id, HttpServletResponse response, HttpSession session) throws IOException {
        User now = (User) session.getAttribute("user");
        if (now == null||now.getRole() != 0) {
            response.sendError(403,"校验失败");
            return;
        }
        response.setContentType("application/json");
        String info = roomService.deleteRoom(id);
        JsonUtil.writeResponse(new Response("/room/delete.do","POST",info),response.getOutputStream());
    }
    @RequestMapping(value = "/update.do",method = {RequestMethod.POST})
    @ResponseBody
    public void updateRoom(Integer id,String name,Integer admin,HttpServletResponse resp,HttpSession session) throws IOException {
        User now = (User) session.getAttribute("user");
        if (now == null||now.getRole() != 0) {
            resp.sendError(403,"校验失败");
            return;
        }
        User adminAccount = userService.getUserById(admin);
        if (adminAccount.getRole() != 1) {
            resp.sendError(400,"参数错误");
            return;
        }
        resp.setContentType("application/json");
        String info = roomService.updateRoom(id,name,admin);
        JsonUtil.writeResponse(new Response("/room/update.do","POST",info),resp.getOutputStream());
    }
    @RequestMapping(value = "/listrooms.do",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public void listRooms(@RequestParam(required=false) Integer admin,HttpServletResponse resp,HttpSession session) throws IOException {
        User now = (User) session.getAttribute("user");
        if(now == null) {
            resp.sendError(403,"校验失败");
            return;
        }
        resp.setContentType("application/json");
        if(admin == null) {
            List<Room> rooms = roomService.getRooms();
            JsonUtil.writeList(rooms,resp.getOutputStream());
        }
        else {
            //试图获取某个管理员管理的图书室信息，需要校验管理员身份
            if(now.getRole() != 0&&now.getUid() != admin) {
                resp.sendError(403,"校验失败");
                return;
            }
            List<Room> rooms = roomService.getRoomsByAdministrator(admin);
            JsonUtil.writeList(rooms,resp.getOutputStream());
        }
    }
}
