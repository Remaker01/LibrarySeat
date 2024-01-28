package com.libraryseat.dao;

import com.libraryseat.pojo.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

//Userdao，加密工作放在service层
//Uid存在的意义，主要是为了作为数值类型的主/外键存在，如果直接用varchar类型的username做主/外键可能对性能之类的有影响
@Repository
public class UserDao extends BaseDao {
    static class UserMapper implements RowMapper<User> {
        static final UserMapper INSTANCE = new UserMapper();
        @Override
        public User mapRow(ResultSet resultSet, int i) throws SQLException {
            User u = new User();
            u.setUid(resultSet.getInt("uid"));
            u.setUsername(resultSet.getString("username"));
            u.setPassword(resultSet.getString("password"));
            u.setTruename(resultSet.getString("truename"));
            u.setGender(resultSet.getString("gender"));
            u.setPhone(resultSet.getString("phone"));
            u.setRole(resultSet.getShort("role"));
            return u;
        }
    }

    public User getUserByUid(int uid) {
        String sql = "select * from users where uid=? and valid=1";
        try{
            return template.queryForObject(sql,UserMapper.INSTANCE,uid);
        } catch (DataAccessException e) {
            return null;
        }
    }

    public String getUsernameByUid(int uid) {
        String sql = "select username from users where uid=? and valid=1";
        try {
            return template.queryForObject(sql,String.class,uid);
        } catch (DataAccessException e) {
            return null;
        }
    }

    public User getUserByUsername(String uname) {
        String sql = "select * from users where username=? and valid=1";
        try {
            return template.queryForObject(sql, UserMapper.INSTANCE, uname);
        } catch (DataAccessException e) {
            return null;
        }
    }

    public User getUserByUsernameAndPswd(String uname,String pswd) {
        String sql = "select * from users where username=? and `password`=? and valid=1";
        try {
            return template.queryForObject(sql, UserMapper.INSTANCE, uname, pswd);
        } catch (DataAccessException e) {
            return null;
        }
    }

    public User getUserByTruenameAndPhone(String trueName, String phone) {
        String sql = "select * from users where truename=? and phone=?";
        try{
            return template.queryForObject(sql,UserMapper.INSTANCE,trueName,phone);
        } catch (DataAccessException e) {
            return null;
        }
    }

    public List<User> getUsers(int start,int rows) {
        String sql = "select * from users where 1=1";
        return super.findByPage(sql,UserMapper.INSTANCE,start,rows,new HashMap<>(0));
    }

    public List<User> getUsers(int start,int rows,String orderBy,Order order) {
        String sql = "select * from users where 1=1";
        return super.findByPage(sql,UserMapper.INSTANCE,start,rows,new HashMap<>(0),orderBy,order);
    }
    @Override
    public void add(Object o) {
        if(!(o instanceof User))
            throw new IllegalArgumentException();
        User user = (User) o;
        String sql = "insert into users(username,`password`,truename,gender,phone,`role`) values(?,?,?,?,?,?)";
        template.update(sql,
                user.getUsername(),
                user.getPassword(),
                user.getTruename(),
                user.getGender(),
                user.getPhone(),
                user.getRole());
    }
    /**删除user，只要有uid或username即可*/
    @Override
    public void delete(Object o) {
        if(!(o instanceof User))
            throw new IllegalArgumentException();
        User user = (User) o;
        if(user.getUid() != 0) {
            String sql = "update users set valid=0,username=username+'#' where uid=?";
            template.update(sql, user.getUid());
        } else {
            String sql = "update users set valid=0,username=username+'#' where username=?";
            template.update(sql,user.getUsername());
        }
    }

    @Override
    public int update(Object o) {
        if(!(o instanceof User))
            throw new IllegalArgumentException();
        User user = (User) o;
        String sql = "update users set username=?, truename=?,`password`=?,phone=? where uid=? and valid=1";
        return template.update(sql,user.getUsername(), user.getTruename(), user.getPassword(), user.getPhone(), user.getUid());
    }
}
