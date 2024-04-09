package com.libraryseat.controller;

import com.libraryseat.Response;
import com.libraryseat.pojo.*;
import com.libraryseat.services.MetadataService;
import com.libraryseat.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.libraryseat.utils.JsonUtil;

@Controller
@RequestMapping("/reservation")
public class ReservationController {
    @Autowired
    private ReservationService reservationService;

//    public String currentReservation(HttpSession session) {
//
//        return null;
//    }

//    public String newReservation(HttpSession session) {
//
//        return null;
//    }
    @RequestMapping(value = "/show.do",method = {RequestMethod.GET})
    public String toReservationsPage(@RequestParam(required = false)Integer pageno,
                                     @RequestParam(required = false)Integer uid,
                                     HttpServletResponse resp,
                                     HttpSession session)
            throws IOException{
        User u = (User) session.getAttribute("user");
        if (u == null) {
            resp.sendError(403,"校验失败");
            return null;
        }
        resp.setHeader("Cache-Control","no-cache");
        StringBuilder target = new StringBuilder(String.format("/library/reservation-role%d.html?",u.getRole()));
        if (uid != null) {
            target.append("uid=").append(uid);
            if (pageno != null)
                target.append("&pageno=?").append(pageno);
        }
        else if (pageno != null)
            target.append("pageno=").append(pageno);
        return target.toString();

    }
    @RequestMapping(value = "/reserve.do",method = {RequestMethod.POST})
    @ResponseBody
    public void reserveSeat(Integer seatid, Integer roomid, HttpServletResponse resp, HttpSession session) throws IOException {
        User u = (User) session.getAttribute("user");
        if(u == null||u.getRole() != 2) {
            resp.sendError(403,"校验失败，只有学生可以预定座位");
            return;
        }
        String info = reservationService.addReservation(seatid,roomid,u.getUid());
        JsonUtil.writeResponse(new Response("/reservation/reserve.do","POST",info),resp.getOutputStream());
    }

    @RequestMapping(value = "/signin.do",method = {RequestMethod.POST})
    @ResponseBody
    public void signIn(@RequestParam Map<String,String> params, HttpServletResponse resp, HttpSession session) //bug0:大小写错误
        throws IOException {
        User u = (User) session.getAttribute("user");
        if(u == null||u.getRole() != 2) {
            resp.sendError(403,"校验失败，只有学生可以签到");
            return;
        }
        int seatid = Integer.parseInt(params.get("seatid")), roomid=Integer.parseInt(params.get("roomid"));
        long time = Long.parseLong(params.get("time"));
        String province = params.get("province"),city = params.get("city");
        String info = reservationService.signIn(seatid,roomid,u.getUid(),new Date(time),province,city);
        JsonUtil.writeResponse(new Response("/reservation/signin.do","POST",info),resp.getOutputStream());
    }
    @RequestMapping(value = "/signout.do",method = {RequestMethod.POST})
    @ResponseBody
    public void signOut(Integer seatid, Integer roomid, Long time, HttpServletResponse resp, HttpSession session)
        throws IOException {
        User u = (User) session.getAttribute("user");
        if(u == null||u.getRole() != 2) {
            resp.sendError(403,"校验失败，只有学生可以签退");
            return;
        }
        String info = reservationService.signOut(seatid,roomid,u.getUid(),new Date(time));
        JsonUtil.writeResponse(new Response("/reservation/signin.do","POST",info),resp.getOutputStream());
    }
    @RequestMapping(value = "/delete.do",method = {RequestMethod.POST})
    @ResponseBody
    public void removeReservation(@RequestParam Map<String,String> params,HttpServletResponse resp, HttpSession session)
        throws IOException{
        User u = (User)session.getAttribute("user");
        if (u == null||u.getRole() != 0){
            resp.sendError(403,"校验失败");
            return;
        }
        try{
            int uid = Integer.parseInt(params.get("uid")),
                    seatid=Integer.parseInt(params.get("seatid")),
                    roomid=Integer.parseInt(params.get("roomid"));
            long timestamp=Long.parseLong(params.get("resTime"));
            String info=reservationService.removeReservation(seatid,roomid,uid,new Date(timestamp));
            JsonUtil.writeResponse(new Response("/reservation/delete.do","POST",info),resp.getOutputStream());
        } catch (RuntimeException e){
            resp.sendError(400,"参数错误");
        }
    }
    @RequestMapping(value = "/list.do",method = {RequestMethod.POST})
    @ResponseBody
    public void getReservations(@RequestParam(required = false) Integer uid,
                                @RequestParam(defaultValue = "1") Integer pageno,
                                HttpServletResponse resp,
                                HttpSession session) throws IOException{
        User u = (User) session.getAttribute("user");
        List<Reservation> reservations;
        if(uid != null) {
            if (u.getUid() != uid&&u.getRole() != 0){
                resp.sendError(403,"校验失败");
                return;
            }
            reservations = reservationService.getReservations(uid, pageno);
        } else {
            if (u.getRole() != 0){
                resp.sendError(403,"校验失败");
                return;
            }
            reservations = reservationService.getReservations(pageno);
        }
        JsonUtil.writeCollection(reservations, resp.getOutputStream());
    }
}
