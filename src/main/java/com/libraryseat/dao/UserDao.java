package com.libraryseat.dao;

import com.libraryseat.pojo.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

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
        String sql = "select * from users where uid=?";
        try{
            return template.queryForObject(sql,UserMapper.INSTANCE,uid);
        } catch (DataAccessException e) {
            return null;
        }
    }

    public String getUsernameByUid(int uid) {
        String sql = "select username from users where uid=?";
        try {
            return template.queryForObject(sql,String.class,uid);
        } catch (DataAccessException e) {
            return null;
        }
    }

    public User getUserByUsername(String uname) {
        String sql = "select * from users where username=? and uid>0";
        try {
            return template.queryForObject(sql, UserMapper.INSTANCE, uname);
        } catch (DataAccessException e) {
            return null;
        }
    }

    public User getUserByUsernameAndPswd(String uname,String pswd) {
        String sql = "select * from users where username=? and `password`=? and uid>0";
        try {
            return template.queryForObject(sql, UserMapper.INSTANCE, uname, pswd);
        } catch (DataAccessException e) {
            return null;
        }
    }

    public User getUserByPhone(String phone) {
        String sql = "select * from users where phone=? and uid>0";
        try{
            return template.queryForObject(sql,UserMapper.INSTANCE,phone);
        } catch (DataAccessException e) {
            return null;
        }
    }

    public List<User> getUsers(int start,int rows) {
        String sql = "select * from users where uid>0";
        return super.findByPage(sql,UserMapper.INSTANCE,start,rows,new HashMap<>(0));
    }

    public List<User> getUsers(int start, int rows, short role) {
        String sql = "select * from users where uid>0";
        Map<String,String> condition = new HashMap<>(1);
        condition.put("role",Short.toString(role));
        return super.findByPage(sql,UserMapper.INSTANCE,start,rows,condition);
    }

    public List<User> getUsers(int start,int rows,String orderBy,Order order) {
        String sql = "select * from users where uid>0";
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
    /**批量添加*/
    public int add(Collection<User> users){
        List<Object[]> args = new ArrayList<>(users.size());
        String sql = "insert into users(username,`password`,truename,gender,phone,`role`) values(?,?,?,?,?,?)";
        for (User user:users){
            if (user == null)
                continue;
            args.add(new Object[]{
                    user.getUsername(),
                    user.getPassword(),
                    user.getTruename(),
                    user.getGender(),
                    user.getPhone(),
                    user.getRole()
            });
        }
        return Arrays.stream(template.batchUpdate(sql,args)).reduce(0,Integer::sum);
    }
    /**删除user，只要有uid或username即可*/
    @Override
    public void delete(Object o) {
        if(!(o instanceof User))
            throw new IllegalArgumentException();
        User user = (User) o;
        if(user.getUid() != 0) {
            String sql = "update users set uid=-uid,username=CONCAT(username,uid),phone=CONCAT(phone,uid) where uid=?";
            //防止后续出现唯一键冲突。注意concat要在uid=-uid之后，这样方便提取真正的用户名出来
            //删除用户后将uid变为负数，关联表查的时候过滤一下
            template.update(sql, user.getUid());
        } else {
            String sql = "update users set uid=-uid,username=CONCAT(username,uid),phone=CONCAT(phone,uid) where username=?";
            template.update(sql,user.getUsername());
        }
    }

    @Override
    public int update(Object o) {
        if(!(o instanceof User))
            throw new IllegalArgumentException();
        User user = (User) o;
        String sql = "update users set username=?, truename=?,`password`=?,phone=? where uid=?";
        return template.update(sql,user.getUsername(), user.getTruename(), user.getPassword(), user.getPhone(), user.getUid());
    }
}
