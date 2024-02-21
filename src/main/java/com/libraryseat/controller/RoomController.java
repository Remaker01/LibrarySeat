package com.libraryseat.controller;

import com.libraryseat.Response;
import com.libraryseat.pojo.Room;
import com.libraryseat.pojo.User;
import com.libraryseat.services.RoomService;
import com.libraryseat.services.UserService;
import com.libraryseat.utils.JsonUtil;
import com.libraryseat.utils.VerifyUtil;
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

    @RequestMapping(value = "/show.do",method = {RequestMethod.GET})
    public String toRoomsPage(@RequestParam(required = false)String my,
                              @RequestParam(required = false)Integer uid,
                              HttpSession session,HttpServletResponse resp) throws IOException {
        User u = (User) session.getAttribute("user");
        if (u == null) {
            resp.sendError(403,"校验失败");
            return null;
        }
        resp.setHeader("Cache-Control","no-cache");
        switch (u.getRole()) {
            case 0:
                return "/library/rooms-role0.html" + ((uid == null) ? "" : String.format("?uid=%d", uid));
            case 1:
                //如果my=1
                if (!VerifyUtil.verifyNonEmptyStrings(my))
                    my = "1";
                if (!my.equals("1") && !my.equals("0")) {
                    resp.sendError(400, "参数错误");
                    return null;
                }
                return "/library/rooms-role1.html?my=" + my;
            case 2:
                return "/library/rooms-role2.html";
        }
        return null; //不会发生
    }
    @RequestMapping(value = "/add.do",method = {RequestMethod.POST})
    @ResponseBody
    public void addRoom(String name, Integer admin, HttpServletResponse resp,HttpSession session) throws IOException {
        User now = (User) session.getAttribute("user");
        if (now == null||now.getRole() != 0) {
            resp.sendError(403,"校验失败");
            return;
        }
        User adminAccount = userService.getUserById(admin);
        if (adminAccount == null||adminAccount.getRole() != 1) {
            JsonUtil.writeResponse(new Response("/room/add.do","POST","指定的用户不存在或不是图书室管理员"), resp.getOutputStream());
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
        if (adminAccount==null||adminAccount.getRole() != 1) {
            JsonUtil.writeResponse(new Response("/room/add.do","POST","指定的用户不存在或不是图书室管理员"), resp.getOutputStream());
            return;
        }
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
            JsonUtil.writeCollection(rooms,resp.getOutputStream());
        }
        else {
            //试图获取某个管理员管理的图书室信息，需要校验管理员身份
            if(now.getRole() != 0&&now.getUid() != admin) {
                resp.sendError(403,"校验失败");
                return;
            }
            List<Room> rooms = roomService.getRoomsByAdministrator(admin);
            JsonUtil.writeCollection(rooms,resp.getOutputStream());
        }
    }
    @RequestMapping(value = "/getbyid.do",method = {RequestMethod.POST})
    @ResponseBody
    public void getRoom(Integer roomid,HttpServletResponse resp,HttpSession session) throws IOException{
        User now = (User) session.getAttribute("user");
        if(now == null) {
            resp.sendError(403,"校验失败");
            return;
        }
        Room room = roomService.getRoom(roomid);
        JsonUtil.writePojo(room,resp.getOutputStream());
    }
}
