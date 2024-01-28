package com.libraryseat.controller;

import com.libraryseat.Response;
import com.libraryseat.pojo.User;
import com.libraryseat.services.RoomService;
import com.libraryseat.services.UserService;
import com.libraryseat.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

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
        User adminAccount = userService.getUserById(admin);
        if (adminAccount.getRole() != 1) {
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
        String info = roomService.updateRoom(id,name,admin);
        JsonUtil.writeResponse(new Response("/room/update.do","POST",info),resp.getOutputStream());
    }
}
