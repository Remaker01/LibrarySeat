package com.libraryseat.services;

import com.libraryseat.dao.UserDao;
import com.libraryseat.pojo.User;
import com.libraryseat.utils.EncryptUtil;
import com.libraryseat.utils.LogUtil;
import com.libraryseat.utils.cache.Cache;
import com.libraryseat.utils.cache.LRUCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Logger;

@Service
public class UserService {
    private static final Cache<String,User> userNameCache = new LRUCache<>(60);
    private static final Cache<Integer,User> idCache = new LRUCache<>(60);
    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());
    static{LogUtil.initLogger(LOGGER);}
    public static final int PAGE_SIZE = 20;
    @Autowired
    private UserDao userDao;

    /**给出用户信息，增加用户*/
    public String addUser(String[] info,short role) {
        String uname = info[0],
                pswd = EncryptUtil.encrypt(info[1],info[0],StandardCharsets.ISO_8859_1),
                trueName = info[2],
                gender = info[3],
                phone = info[4];
        userDao.add(new User(uname,pswd,trueName,gender,phone,role));
        return "添加成功！";
    }
    /**
     * 修改用户信息
     * 可以修改的信息有：1.用户名（仅超管），2.密码（不要通过此处修改，用下面修改密码的方法），3.真实姓名，4.手机号
     */
    public String updateUser(User u) {
        String uname = u.getUsername();
        int uid = u.getUid(); //TODO:这个uid是否总是正确？
        int res = userDao.update(u) ;
        if(res != 0) {
            synchronized (this) {
                userNameCache.put(uname, u);
                idCache.put(uid, u);
            }
            return "更新成功！";
        }
        return "更新失败，请确认用户存在！";
    }

    public String removeUser(int uid) {
        //1.根据id查找用户
        User u = idCache.get(uid);
        if(u == null)
            u = userDao.getUserByUid(uid);
        if (u == null)
            return "用户不存在！";
        //2.从缓存中删除
        try {
            userDao.delete(u);
            synchronized (this) {
                userNameCache.invalidate(u.getUsername());
                idCache.invalidate(uid);
            }
            return "删除成功！";
        } catch (DataAccessException e) {
            LogUtil.log(LOGGER,e);
            return "删除失败，请确认该用户不是任何阅览室的管理员！";
        }
    }

    public String removeUser(String username) {
        //1.通过用户名查找用户
        User u = userNameCache.get(username);
        if (u == null)
            u = userDao.getUserByUsername(username); //不用put进去了
        if(u == null)
            return "用户不存在！";
        //2.从2个缓存中删除。
        try {
            userDao.delete(u);
            synchronized (this) {
                //从缓存1中删除
                userNameCache.invalidate(username);
                //从缓存2中删除
                idCache.invalidate(u.getUid());
            }
            return "删除成功！";
        } catch (DataAccessException e) {
            LogUtil.log(LOGGER,e);
            return "删除失败，请确认该用户不是任何阅览室的管理员！";
        }
    }
    /**执行登录动作，返回登录的用户。若登录失败（密码错误等），返回null*/
    public User login(String username,String password) {
        password = EncryptUtil.encrypt(password,username, StandardCharsets.ISO_8859_1);
        User u = userNameCache.get(username);
        if(u == null) {
            u = userDao.getUserByUsername(username);
            userNameCache.put(username,u);
        }
        if (u != null&&u.getPassword().equals(password)) {
            return u;
        }
        return null;
    }
    /**使用手机号+验证码登录，也可用于忘记密码时寻找密码*/
    public User loginByPhone(String phone) {
        //1.获取用户名
        return userDao.getUserByPhone(phone);
    }

    public User getUserById(int uid) {
        User u = idCache.get(uid);
        if(u == null) {
            u = userDao.getUserByUid(uid);
            idCache.put(uid,u);
        }
        return u;
    }

    public List<User> getUsers(int page) {
        return userDao.getUsers((page-1)*PAGE_SIZE,PAGE_SIZE);
    }

    public List<User> getUsersByRole(int page,short role) {
        return userDao.getUsers((page-1)*PAGE_SIZE,PAGE_SIZE,role);
    }

    /**通过用户名和密码修改密码*/
    public String modifyPswd(String username, String oldPswd, String newPswd) {
        //1.根据用户名密码查找，如果没有符合的，说明用户不存在或原密码错误
        User userToBeModified = login(username,oldPswd); //id一定not null
        if (userToBeModified == null)
            return String.format("用户%s不存在，或原密码错误！",username);
        //2.存在原用户
        newPswd = EncryptUtil.encrypt(newPswd,username,StandardCharsets.ISO_8859_1);
        userToBeModified.setPassword(newPswd);
        return updateUser(userToBeModified);
    }
    /**通过手机号+验证码重置密码。用于忘记密码的情况*/
    public String resetPswd(String phone, String newPswd) {
        // 1.获取用户信息
        User u = loginByPhone(phone);
        if(u == null)
            return "用户不存在！";
        //2.加密用户密码
        newPswd = EncryptUtil.encrypt(newPswd,u.getUsername(),StandardCharsets.ISO_8859_1);
        u.setPassword(newPswd);
        return updateUser(u);
    }
}
