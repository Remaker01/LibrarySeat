package com.libraryseat.services;

import com.libraryseat.dao.BaseDao;
import com.libraryseat.dao.MessageDao;
import com.libraryseat.pojo.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageDao messageDao;
    public static final int MAX_LENGTH = 1000;
    public static final int PAGE_SIZE = 10;
    private static final Logger LOGGER = LogManager.getLogger(MessageService.class.getName());

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
        try {
            messageDao.add(message);
            return "添加成功！";
        } catch (DataAccessException e){
            LOGGER.error("",e);
            return "添加通知失败，请稍后重试！";
        }
    }

    public String updateMessage(int uid, Date time, String content) {
        Message m = messageDao.getMessage(uid,new Timestamp(time.getTime()));
        if (m == null)
            return "更新失败，通知不存在！";
        m.setContent(content);
        if (messageDao.update(m) != 0)
            return "更新成功！";
        return "更新失败，请稍后重试！";
    }

    public String removeMessage(int uid, Date time) {
        Message m = new Message();
        m.setUid(uid);
        m.setTime(new Timestamp(time.getTime()));
        try {
            messageDao.delete(m);
            return "删除成功！";
        } catch (DataAccessException e) {
            LOGGER.error("",e);
            return "删除失败，请确认用户存在！";
        }
    }
    /**
     * 获取某一页的通知摘要，限长limit.
     */
    public List<Message> getMessageSummaries(int page,int limit) {
        List<Message> messages = messageDao.getMessages((page-1)*PAGE_SIZE,PAGE_SIZE,"time", BaseDao.Order.DESCEND);
        for(Message m:messages) {
            if (m.getContent().length() > limit)
                m.setContent(m.getContent().substring(0,limit));
        }
        return messages;
    }

    public Message getMessage(int uid,Date time) {
        return messageDao.getMessage(uid,new Timestamp(time.getTime()));
    }
}
