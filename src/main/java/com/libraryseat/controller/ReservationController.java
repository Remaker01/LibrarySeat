package com.libraryseat.controller;

import com.libraryseat.Response;
import com.libraryseat.pojo.*;
import com.libraryseat.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import com.libraryseat.utils.JsonUtil;

@Controller
@RequestMapping("/reservation")
public class ReservationController {
    @Autowired
    private ReservationService reservationService;
    private DateFormat formatter = null;

    public String currentReservation(HttpSession session) {
        //TODO:返回'当前预定'的视图
        return null;
    }

    public String newReservation(HttpSession session) {
        //TODO:返回‘预定新座位’
        return null;
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
    public void signIn(Integer seatId, Integer roomid, String time, HttpServletResponse resp, HttpSession session)
        throws IOException {
        User u = (User) session.getAttribute("user");
        if(u == null||u.getRole() != 2) {
            resp.sendError(403,"校验失败，只有学生可以签到");
            return;
        }
        if (formatter == null)
            formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        try {
            String info = reservationService.signIn(seatId,roomid,u.getUid(),formatter.parse(time));
            JsonUtil.writeResponse(new Response("/reservation/signin.do","POST",info),resp.getOutputStream());
        } catch (ParseException e) {
            resp.sendError(400,"参数错误：格式错误");
        }
    }
    @RequestMapping(value = "/signout.do",method = {RequestMethod.POST})
    @ResponseBody
    public void signOut(Integer seatId, Integer roomid, String time, HttpServletResponse resp, HttpSession session)
        throws IOException {
        User u = (User) session.getAttribute("user");
        if(u == null||u.getRole() != 2) {
            resp.sendError(403,"校验失败，只有学生可以签退");
            return;
        }
        if (formatter == null)
            formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        try {
            String info = reservationService.signOut(seatId,roomid,u.getUid(),formatter.parse(time));
            JsonUtil.writeResponse(new Response("/reservation/signin.do","POST",info),resp.getOutputStream());
        } catch (ParseException e) {
            resp.sendError(400,"参数错误：格式错误");
        }
    }
    @RequestMapping(value = "/getbyuser.do",method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public void getReservationsByUser(Integer uid, @RequestParam(required = false) Integer pageno, HttpServletResponse resp, HttpSession session)
        throws IOException{
        User u = (User) session.getAttribute("user");
        if(u.getUid() != uid&&u.getRole() != 0) {
            resp.sendError(403,"校验失败");
            return;
        }
        if(pageno == null)
            pageno = 1;
        List<Reservation> reservations = reservationService.getReservationsByUser(uid,pageno);
        JsonUtil.writeReservations(reservations,resp.getOutputStream());
    }
}
