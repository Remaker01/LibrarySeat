package com.libraryseat.controller;

import com.libraryseat.Response;
import com.libraryseat.pojo.Message;
import com.libraryseat.pojo.User;
import com.libraryseat.services.MessageService;
import com.libraryseat.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/message")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @RequestMapping(value = "/send.do",method = {RequestMethod.POST})
    @ResponseBody
    public void sendMessage(@RequestParam(required = false) String title,String content,HttpServletResponse resp,HttpSession session)
            throws IOException {
        User u = (User) session.getAttribute("user");
        if (u == null||u.getRole()==2){
            resp.sendError(403,"校验失败");
            return;
        }
        resp.setContentType("application/json");
        String info=messageService.addMessage(u.getUid(),title,content);
        JsonUtil.writeResponse(new Response("/message/send.do","POST",info),resp.getOutputStream());
    }

    @RequestMapping(value = "/remove.do",method = {RequestMethod.POST})
    @ResponseBody
    public void removeMessage(Integer uid,Long time,HttpServletResponse resp,HttpSession session) throws IOException {
        User u = (User) session.getAttribute("user");
        if (u == null||u.getRole()!=0){
            resp.sendError(403,"校验失败");
            return;
        }
        resp.setContentType("application/json");
        String info = messageService.removeMessage(uid,new Date(time));
        JsonUtil.writeResponse(new Response("/message/send.do", "POST", info), resp.getOutputStream());
    }
    @RequestMapping(value = "/listmessages.do",method = {RequestMethod.POST})
    @ResponseBody
    public void listMessages(@RequestParam(required = false)Integer pageno, Integer limit,HttpServletResponse resp,HttpSession session)
        throws IOException {
        User u = (User) session.getAttribute("user");
        if (u == null){
            resp.sendError(403,"校验失败");
            return;
        }
        if (pageno == null)
            pageno = 1;
        resp.setContentType("application/json");
        List<Message> messages = messageService.getMessageSummaries(pageno,limit);
        JsonUtil.writeCollection(messages,resp.getOutputStream());
    }
    @RequestMapping(value = "/get.do",method = {RequestMethod.POST})
    @ResponseBody
    public void getMessage(Integer uid, Long time, HttpServletResponse resp,HttpSession session) throws IOException {
        if (session.getAttribute("user") == null){
            resp.sendError(403,"校验失败");
            return;
        }
        resp.setContentType("application/json");
        Message message = messageService.getMessage(uid,new Date(time));
        JsonUtil.writePojo(message,resp.getOutputStream());
    }
}
