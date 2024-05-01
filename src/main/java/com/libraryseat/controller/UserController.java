package com.libraryseat.controller;

import com.libraryseat.Response;
import com.libraryseat.pojo.User;
import com.libraryseat.services.UserService;
import com.libraryseat.utils.EncryptUtil;
import com.libraryseat.utils.JsonUtil;
import com.libraryseat.utils.VerifyUtil;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/* 用户Controller(/user)
1.userLogin(login.do): POST:para={username,pswd,vcode(验证码),token(时间戳)} or {name,phone,vcode,token}
2.userAdd(usr_add.do): POST:para={username,pswd,truename,phone,gender,role,vcode,token}
 */
//正常请求可能产生的全部信息均写在Response里。异常请求（如爬虫等）产生的信息（如时间戳错误等）返回http 4xx 5xx
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private DiskFileItemFactory fileItemFactory;
    @Autowired
    private ServletFileUpload fileUpload;

    private User base64Phone(User old,boolean copy){
        if (old == null)
            return null;
        if (copy) {
            User u = new User();
            BeanUtils.copyProperties(old, u, "password", "phone","salt");
            u.setPhone(EncryptUtil.base64Encode(old.getPhone()));
            return u;
        }
        old.setPhone(EncryptUtil.base64Encode(old.getPhone()));
        return old;
    }

    @RequestMapping(value = "/logged_user.do",method = {RequestMethod.GET})
    @ResponseBody
    public void loggedUser(HttpSession session, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        User u = (User) (session.getAttribute("user"));
        JsonUtil.writePojo(base64Phone(u,true),response.getOutputStream());
    }

    @RequestMapping(value = "/login.do",method = {RequestMethod.POST})
    @ResponseBody
    public void userLogin(@RequestParam Map<String,String> params, HttpServletResponse response, HttpSession session) throws IOException {
        OutputStream out = response.getOutputStream();
        String uid = params.get("uid"),
                username = params.get("username"),
                pswd = params.get("pswd"),
                vcode = params.get("vcode"),
                token = params.get("token");
        String realVcode = (String) session.getAttribute("CHECKCODE_SERVER");
        session.removeAttribute("CHECKCODE_SERVER");
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
        //账号+密码
        if (VerifyUtil.verifyNonEmptyStrings(uid,pswd)) {
            int uid_ = Integer.parseInt(uid);
            u = userService.login(uid_,pswd);
        }
        else if(VerifyUtil.verifyNonEmptyStrings(username,pswd)) //用户名+密码
            u = userService.login(username,pswd);
        else {
            String phone=EncryptUtil.base64Decode(params.get("phone"));
            u = userService.loginByPhone(phone);
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
    public void addUser(User user,
                        String vcode,
                        String token,
                        HttpServletResponse resp,
                        HttpSession session) throws IOException {
        User logged = (User) session.getAttribute("user");
        if(logged == null||logged.getRole() != 0) {
            resp.sendError(403,"校验失败，请尝试重新登录！");
            return;
        }

        if(!VerifyUtil.verifyTimestamp(token)) {
            resp.sendError(403,"校验失败");
            return;
        }
        OutputStream out = resp.getOutputStream();
        user.setPhone(EncryptUtil.base64Decode(user.getPhone()));
        String realcode = (String) session.getAttribute("CHECKCODE_SERVER");
        session.removeAttribute("CHECKCODE_SERVER");
        if(!realcode.equals(vcode)) {
            Response r = new Response("/user/add.do","POST","验证码错误");
            JsonUtil.writeResponse(r,out);
            return;
        }
        String info = userService.addUser(new String[]{
                user.getUsername(),user.getPassword(),user.getTruename(),user.getGender(),user.getPhone()
        },user.getRole());
        JsonUtil.writeResponse(new Response("/user/add.do","POST",info),out);
    }

    @RequestMapping(value = "/add_users.do",method = {RequestMethod.POST})
    @ResponseBody
    public void addUsers(HttpServletRequest req, HttpSession session, HttpServletResponse resp) throws IOException{
        User logged = (User) session.getAttribute("user");
        if(logged == null||logged.getRole() != 0) {
            resp.sendError(403,"校验失败，请尝试重新登录！");
            return;
        }
        if (!ServletFileUpload.isMultipartContent(req)){
            resp.sendError(400,"参数错误：文件不合法");
            return;
        }
        if (fileItemFactory.getRepository() == null)
            fileItemFactory.setRepository(new File(System.getProperty("java.io.tmpdir")));
        try {
            String info = userService.addUsersInFileItem(fileUpload.parseRequest(req).get(0));
            JsonUtil.writeResponse(new Response("/user/add_users.do","POST",info),resp.getOutputStream());
        } catch (FileUploadException e){
            JsonUtil.writeResponse(new Response("/user/add_users.do","POST","文件过大或出现异常，请重新选择文件！"),
                    resp.getOutputStream());
        }
    }

    @RequestMapping(value = "/delete.do",method = {RequestMethod.POST})
    @ResponseBody
    public void removeUser(@RequestParam Integer uid, HttpServletResponse resp, HttpSession session) throws IOException {
        User u = (User) session.getAttribute("user");
        if(u == null||u.getRole() != 0) {
            resp.sendError(403,"校验失败，请尝试重新登录！");
            return;
        }
        String info = userService.removeUser(uid);
        JsonUtil.writeResponse(new Response("/user/delete.do","POST",info),resp.getOutputStream());
    }
    @RequestMapping(value = "/modifypswd.do",method = {RequestMethod.POST})
    @ResponseBody
    public void modifyPswd(@RequestParam Map<String,String> params,HttpServletResponse resp,HttpSession session) throws IOException {
        User now = (User) session.getAttribute("user");
        if(now == null) {
            resp.sendError(403,"校验失败，请尝试重新登录！");
            return;
        }
        String old = params.get("old"),newPswd=params.get("new"),uid_=params.get("uid"),vcode=params.get("vcode");
        String realVcode = (String) session.getAttribute("CHECKCODE_SERVER");
        if(!realVcode.equals(vcode)) {
            JsonUtil.writeResponse(new Response("/user/modifypswd.do","POST","验证码错误"),resp.getOutputStream());
            return;
        }
        session.removeAttribute("CHECKCODE_SERVER");
        try{
            int uid=Integer.parseInt(uid_);
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
                String info = userService.resetPswd(u.getPhone(), newPswd);
                JsonUtil.writeResponse(new Response("/user/modifypswd.do","POST",info),resp.getOutputStream());
            }
        } catch (NumberFormatException e){
            resp.sendError(400,"参数错误");
        }
    }
    @RequestMapping(value = "/update.do",method = {RequestMethod.POST})
    @ResponseBody
    public void updateUser(User new_, HttpServletResponse resp, HttpSession session) throws IOException {
        User now = (User) session.getAttribute("user");
        if(now == null||now.getRole() != 0) {
            resp.sendError(403,"校验失败，请尝试重新登录！");
            return;
        }
        int uid_ = new_.getUid();
        User old = userService.getUserById(uid_); //不能直接new不然密码不正确
        String info = userService.updateUser(uid_,new String[]{
                new_.getUsername(),
                old.getPassword(),
                new_.getTruename(),
                old.getGender(),
                EncryptUtil.base64Decode(new_.getPhone())
        },old.getRole(),old.getSalt(),false);
        JsonUtil.writeResponse(new Response("/user/update.do","POST",info),resp.getOutputStream());
    }

    @RequestMapping(value = "/exit.do",method = {RequestMethod.GET})
    public String exit(HttpSession session) {
        session.removeAttribute("user");
        return "redirect:/";
    }

    @RequestMapping(value = "/listusers.do",method = {RequestMethod.POST})
    @ResponseBody
    public void listUsers(@RequestParam(value = "pageno",defaultValue = "1") Integer pageno,
                          @RequestParam(required = false) Short role,
                          HttpServletResponse resp,
                          HttpSession session) throws IOException {
        User u = (User) session.getAttribute("user");
        if(u == null||u.getRole() == 2) {
            resp.sendError(403,"校验失败，请尝试重新登录！");
            return;
        }
        List<User> users = (role == null) ? userService.getUsers(pageno) : userService.getUsersByRole(pageno,role);
        users = users.stream()
                .peek(user -> base64Phone(user, false))
                .collect(Collectors.toList()); //均来自数据库，可以为false
        JsonUtil.writeCollection(users,resp.getOutputStream());
    }
    @RequestMapping(value = "/getbyid.do",method = {RequestMethod.POST})
    @ResponseBody
    public void getUserById(Integer uid, HttpServletResponse resp, HttpSession session) throws IOException {
        User u = (User) session.getAttribute("user");
        if (u == null) {
            resp.sendError(403,"校验失败，请尝试重新登录！");
            return;
        }
        User result = userService.getUserById(uid);
        JsonUtil.writePojo(base64Phone(result,true),resp.getOutputStream()); //可能来自缓存，必须为true
    }
    @RequestMapping(value = "/getimagebyid.do",method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public void getUserImage(Integer uid, HttpServletResponse resp, HttpSession session) throws IOException {
        User u = (User) session.getAttribute("user");
        if (u == null) {
            resp.sendError(403,"校验失败，请尝试重新登录！");
            return;
        }
        resp.setContentType("image/jpeg");
        resp.setHeader("Cache-control","no-cache"); //TODO:加上max-age
        if (u.getUid() == uid||u.getRole() != 2) {
            resp.getOutputStream().write(userService.getUserImage(uid));
            return;
        }
        resp.sendError(403,"校验失败，请尝试重新登录！");
    }

    @RequestMapping(value = "/updateimage.do",method = {RequestMethod.POST})
    @ResponseBody
    public void updateImage(HttpServletRequest req, HttpServletResponse resp, HttpSession session) throws Exception{
        User logged = (User) session.getAttribute("user");
        if (!ServletFileUpload.isMultipartContent(req)){
            resp.sendError(400,"参数错误：文件不合法");
            return;
        }
        if (fileItemFactory.getRepository() == null)
            fileItemFactory.setRepository(new File(System.getProperty("java.io.tmpdir")));
        try {
            List<FileItem> fileItems = fileUpload.parseRequest(req);
            int uid = Integer.parseInt(new String(fileItems.get(0).get(), StandardCharsets.UTF_8));
            if (logged == null||(uid != logged.getUid()&&logged.getRole() != 0)){
                resp.sendError(403,"校验失败，请尝试重新登录！");
                return;
            }
            String info = userService.updateImage(uid, fileItems.get(1));
            JsonUtil.writeResponse(new Response("/user/updateimage.do","POST",info),resp.getOutputStream());
        } catch (FileUploadException e) {
            JsonUtil.writeResponse(new Response("/user/updateimage.do","POST","文件过大或出现异常，请重新选择文件！"),
                    resp.getOutputStream());
        }
    }
}
