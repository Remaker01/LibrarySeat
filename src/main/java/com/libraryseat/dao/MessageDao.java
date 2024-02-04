package com.libraryseat.dao;

import com.libraryseat.pojo.Message;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

@Repository
public class MessageDao extends BaseDao {
    private static final BeanPropertyRowMapper<Message> MAPPER = new BeanPropertyRowMapper<>(Message.class);
    @Override
    public void add(Object o) {
        assert (o instanceof Message);
        Message message = (Message) o;
        String sql = "insert into message(uid, `time`, title, content) values(?,?,?,?)";
        template.update(sql,message.getUid(),message.getTime(),message.getTitle(),message.getContent());
    }

    @Override
    public void delete(Object o) {
        assert (o instanceof Message);
        Message message = (Message) o;
        String sql = "delete from message where uid=? and `time`=?";
        template.update(sql,message.getUid(),message.getTime());
    }

    @Override
    public int update(Object o) {
        assert (o instanceof Message);
        Message message = (Message) o;
        String sql = "update message set content=? where uid=? and `time`=?";
        return template.update(sql,message.getContent(),message.getUid(),message.getTime());
    }

    public List<Message> getMessages(int start,int rows) {
        String sql = "select * from message where uid>0";
        return super.findByPage(sql, MAPPER,start,rows,new HashMap<>(0));
    }

    public List<Message> getMessages(int start,int rows,String orderBy,Order order) {
        String sql = "select * from message where uid>0";
        return super.findByPage(sql, MAPPER,start,rows,new HashMap<>(0),orderBy,order);
    }

    public Message getMessage(int uid, Timestamp timestamp) {
        String sql = "select * from message where uid=? and `time`=?";
        try {
            return template.queryForObject(sql, MAPPER,uid,timestamp);
        } catch (DataAccessException e) {
            return null;
        }
    }
}
