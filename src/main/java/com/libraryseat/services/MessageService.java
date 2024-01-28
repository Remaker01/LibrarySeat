package com.libraryseat.services;

import com.libraryseat.dao.MessageDao;
import com.libraryseat.pojo.Message;
import com.libraryseat.utils.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class MessageService {
    @Autowired
    private MessageDao messageDao;
    public static final int MAX_LENGTH = 1000;
    public static final int PAGE_SIZE = 10;
    private static final Logger LOGGER = Logger.getLogger(MessageService.class.getName());
    static {LogUtil.initLogger(LOGGER);LOGGER.setLevel(Level.INFO);}

    public String addMessage(int uid,String title,String content) {
        if(content == null||content.isEmpty())
            return "消息内容不能为空！";
        if (title == null||title.isEmpty())
            title = "无标题";
        if(content.length() > MAX_LENGTH) {
            return String.format("消息长度为%d,超过限制%d！",content.length(),MAX_LENGTH);
        }
        Timestamp now = new Timestamp((System.currentTimeMillis() / 1000) * 1000L); //精确到秒
        Message message = new Message();
        message.setUid(uid);
        message.setTime(now);
        message.setTitle(title);
        message.setContent(content);
        messageDao.add(message);
        return "添加成功！";
    }

    public String removeMessage(int uid, Date time) {
        Message m = new Message();
        m.setUid(uid);
        m.setTime(new Timestamp(time.getTime()));
        try {
            messageDao.delete(m);
            return "删除成功！";
        } catch (DataAccessException e) {
            LogUtil.log(LOGGER,e);
            return "删除失败，请确认用户存在！";
        }
    }
    /**
     * 获取某一页的通知摘要，限长limit.
     */
    public List<Message> getMessageSummaries(int page,int limit) {
        List<Message> messages = messageDao.getMessages(page-1,PAGE_SIZE);
        for(Message m:messages) {
            m.setContent(m.getContent().substring(0,limit)); //TODO:是否会真正更改？
        }
        return messages;
    }

    public Message getMessage(int uid,Date time) {
        return messageDao.getMessage(uid,new Timestamp(time.getTime()));
    }
}
