package com.libraryseat.controller;

import com.libraryseat.Response;
import com.libraryseat.pojo.User;
import com.libraryseat.services.UserService;
import com.libraryseat.utils.JsonUtil;
import com.libraryseat.utils.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
/* 用户Controller(/user)
1.userLogin(login.do):
    POST:para={username,pswd,vcode(验证码),token(时间戳)} or {name,phone,vcode,token}
2.userAdd(usr_add.do):
    POST:para={username,pswd,truename,phone,gender,role,vcode,token}
    将检查当前登录的user是否为超级管理员(role==0)，不符合将返回403
 */
//正常请求可能产生的全部信息均写在Response里。异常请求（如爬虫等）产生的信息（如时间戳错误等）返回http 4xx 5xx
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/logged_user.do",method = {RequestMethod.GET})
    @ResponseBody
    public void loggedUser(HttpSession session, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        User u = (User) (session.getAttribute("user"));
        JsonUtil.writePojo(u,response.getOutputStream());
    }

    @RequestMapping(value = "/login.do",method = {RequestMethod.POST})
    @ResponseBody
    public void userLogin(@RequestParam Map<String,String> params, HttpServletResponse response, HttpSession session) throws IOException {
        OutputStream out = response.getOutputStream();
        String username = params.get("username"),pswd = params.get("pswd"),vcode = params.get("vcode"),token = params.get("token");
        String realVcode = (String) session.getAttribute("CHECKCODE_SERVER");
        session.removeAttribute("CHECKCODE_SERVER");
        response.setContentType("application/json");
        if(!realVcode.equals(vcode)) { //real的写在前面，万一出错直接报500方便排错
            Response r = new Response("/user/login.do","POST","验证码错误");
            JsonUtil.writeResponse(r,out);
            return;
        }
        if(!VerifyUtil.verifyTimestamp(token)) {
            response.sendError(403,"校验失败");
            return;
        }
        User u;
        if(VerifyUtil.verifyNonEmptyStrings(username,pswd))
            u = userService.login(username,pswd);
        else {
            String trueName = params.get("truename"),phone=params.get("phone");
            u = userService.loginByTrueNamePhone(trueName,phone);
        }
        if(u == null) {
            JsonUtil.writeResponse(new Response("/user/login.do","POST","登录信息错误！"),out);
        } else {
            JsonUtil.writeResponse(new Response("/user/login.do","POST","登录成功！"),out);
            session.setAttribute("user",u);
        }
    }

    @RequestMapping(value = "/add.do",method = {RequestMethod.POST})
    @ResponseBody
    public void addUser(@RequestParam Map<String,String> params,HttpServletResponse resp,HttpSession session) throws IOException {
        User logged = (User) session.getAttribute("user");
        if(logged == null||logged.getRole() != 0) {
            resp.sendError(403,"校验失败");
            return;
        }
        resp.setContentType("application/json");
        OutputStream out = resp.getOutputStream();
        String username = params.get("username"),
                pswd = params.get("pswd"),
                trueName = params.get("truename"),
                gender = params.get("gender"),
                phone = params.get("phone"),
                roleStr = params.get("role"),
                vcode = params.get("vcode"),
                token = params.get("token");
        if (!VerifyUtil.verifyNonEmptyStrings(username,pswd,trueName,gender,phone,roleStr,vcode,token)||
                !VerifyUtil.verifyPassword(pswd)) {
            resp.sendError(400,"参数错误");
            return;
        }
        if(!VerifyUtil.verifyTimestamp(token)) {
            resp.sendError(403,"校验失败");
        }
        short role;
        if(roleStr.equals("1")) role = (short)1;
        else if (roleStr.equals("2"))   role = (short)2;
        else {
            resp.sendError(400,"参数错误");
            return;
        }
        String realcode = (String) session.getAttribute("CHECKCODE_SERVER");
        session.removeAttribute("CHECKCODE_SERVER");
        if(!realcode.equals(vcode)) {
            Response r = new Response("/user/add.do","POST","验证码错误");
            JsonUtil.writeResponse(r,out);
            return;
        }
        String info = userService.addUser(new String[]{username,pswd,trueName,gender,phone},role);
        JsonUtil.writeResponse(new Response("/user/add.do","POST",info),out);
    }

    @RequestMapping(value = "/delete.do",method = {RequestMethod.POST})
    @ResponseBody
    public void removeUser(@RequestParam Integer uid, HttpServletResponse resp, HttpSession session) throws IOException {
//        String uid == req.getParameter("uid")[0];
//        if(uid == null) {
//            resp.sendError(400,"参数错误");
//            return;
//        }
        User u = (User) session.getAttribute("user");
        if(u == null||u.getRole() != 0) {
            resp.sendError(403,"校验失败");
            return;
        }
        resp.setContentType("application/json");
        String info = userService.removeUser(uid);
        JsonUtil.writeResponse(new Response("/user/delete.do","POST",info),resp.getOutputStream());
    }
    @RequestMapping(value = "/modifypswd.do",method = {RequestMethod.POST})
    @ResponseBody
    public void modifyPswd(Integer uid,@RequestParam(required = false) String old,@RequestParam("new") String newPswd, HttpServletResponse resp, HttpSession session)
            throws IOException {
        User now = (User) session.getAttribute("user");
        resp.setContentType("application/json");
        if(now == null) {
            resp.sendError(403,"校验失败！");
            return;
        }
        if(now.getUid() == uid) {
            String info = userService.modifyPswd(now.getUsername(),old,newPswd);
            JsonUtil.writeResponse(new Response("/user/modifypswd.do","POST",info),resp.getOutputStream());
        } else {
            if(now.getRole() != 0) {
                resp.sendError(403,"校验失败！");
                return;
            }
            //2.不同，进行超管重置密码操作
            User u = userService.getUserById(uid);
            String info = userService.resetPswd(u.getTruename(), u.getPhone(), newPswd);
            JsonUtil.writeResponse(new Response("/user/modifypswd.do","POST",info),resp.getOutputStream());
        }
    }
    @RequestMapping(value = "/update.do",method = {RequestMethod.POST})
    @ResponseBody
    public void updateUser(@RequestParam Map<String,String> params, HttpServletResponse resp, HttpSession session) throws IOException {
        User now = (User) session.getAttribute("user");
        if(now == null||now.getRole() != 0) {
            resp.sendError(403,"校验失败！");
        }
        String uid = params.get("uid"),uname = params.get("uname"),trueName = params.get("truename"),phone = params.get("phone");
        int uid_;
        try{
            uid_ = Integer.parseInt(uid);
        } catch (RuntimeException e) {
            resp.sendError(400,"参数错误！");
            return;
        }
        User u = userService.getUserById(uid_);
        u.setUsername(uname);u.setTruename(trueName);u.setPhone(phone);
        String info = userService.updateUser(u);
        JsonUtil.writeResponse(new Response("/user/update.do","POST",info),resp.getOutputStream());
    }
    @RequestMapping(value = "/exit.do",method = {RequestMethod.GET})
    public String exit(HttpSession session) {
        session.removeAttribute("user");
        return "redirect:/";
    }

    @RequestMapping(value = "/listusers.do",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public void listUsers(@RequestParam(value = "pageno",required = false) Integer pageno,HttpServletResponse resp, HttpSession session)
            throws IOException {
        User u = (User) session.getAttribute("user");
        if(u == null||u.getRole() != 0) {
            resp.sendError(403,"校验失败！");
            return;
        }
        if (pageno == null)
            pageno = 1;
        List<User> users = userService.getUsers(pageno);
        JsonUtil.writeList(users,resp.getOutputStream());
    }
}
