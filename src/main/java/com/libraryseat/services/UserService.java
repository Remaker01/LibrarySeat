package com.libraryseat.services;

import com.libraryseat.dao.ReservationDao;
import com.libraryseat.dao.RoomDao;
import com.libraryseat.dao.UserDao;
import com.libraryseat.pojo.User;
import com.libraryseat.utils.EncryptUtil;
import com.libraryseat.utils.ExcelUtil;
import com.libraryseat.utils.ImageBase64Util;
import com.libraryseat.utils.VerifyUtil;
import com.libraryseat.utils.cache.*;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;
/* 已测试功能
* 1.登录--ok
* 2.新增单个--ok
* 3.批量新增--ok
* 3.重置密码--ok
* 4.22:
为防止将用户名设置为已存在的导致缓存污染，对update进行修改
测试 1.修改密码--ok  2.重置密码--ok  3.修改信息--ok
* 每轮测试后，都必须重新登录2次。
*/
@Service
public class UserService {
    private static final Cache<String,User> userNameCache = new LRUCache<>(60);
    private static final Cache<Integer,User> idCache = new LRUCache<>(60);
    private static final Cache<String,User> phoneCache = new LRUCache<>(60);
    private static final Logger LOGGER = LogManager.getLogger(UserService.class.getName());
    private static volatile byte[] DEFAULT_IMAGE_DATA = null;
    public static final int PAGE_SIZE = 20;
    @Autowired
    private UserDao userDao;
    @Autowired
    @Lazy
    private ReservationDao reservationDao;
    @Autowired
    @Lazy
    private RoomDao roomDao;
    private final SecureRandom generator;

    public UserService() {
        SecureRandom sr;
        try {
            sr = SecureRandom.getInstance("SHA1PRNG"); //防止linux中可能发生阻塞
        } catch (NoSuchAlgorithmException e) { //基本不可能发生
            sr = new SecureRandom();
        }
        generator = sr;
    }
    /**
     * 采用加盐的方式对密码进行hash.
     */
    private void encryptPasswordSalted(User user){
        int salt = generator.nextInt(); //可能为负数
        String saltString = Integer.toString(salt);
        user.setPassword(EncryptUtil.encrypt(user.getPassword(),saltString,StandardCharsets.ISO_8859_1));
        user.setSalt(salt);
    }

    String[] args(User u) {
        if (u == null)
            return null;
        return new String[]{
                u.getUsername(),
                u.getPassword(),
                u.getTruename(),
                u.getGender(),
                u.getPhone()
        };
    }

    /**给出用户信息，增加用户*/
    public String addUser(String[] info,short role) {
        assert role>=0&&role<=2;
        String uname = info[0],
                pswd = info[1],
                trueName = info[2],
                gender = info[3],
                phone = info[4];
        try {
            User newUser = new User(uname, pswd, trueName, gender, phone, role);
            encryptPasswordSalted(newUser);
            userDao.add(newUser);
            return "添加成功！";
        } catch (DataIntegrityViolationException e){
            return "添加失败,用户名或手机号可能已存在！";
        }
    }
    /**从fileitem中获取用户信息并添加*/
    public String addUsersInFileItem(FileItem fileItem) {
        if (!fileItem.isFormField()){
            String format = ExcelUtil.detectFormatViaMIME(fileItem.getContentType());
            if (format.isEmpty()){ //尝试通过后缀名获取类型
                format = FilenameUtils.getExtension(fileItem.getName());
            }
            try(InputStream stream = fileItem.getInputStream()){
                List<User> parseResult = ExcelUtil.getUsersInWorkbook(stream,format);
                if (parseResult.isEmpty())
                    return "文件中不包含任何有效信息！";
                parseResult = parseResult.stream()
                        .peek(user->encryptPasswordSalted(user)).collect(Collectors.toList());
                int add=userDao.add(parseResult);
                return String.format("上传成功，共添加%d条数据.", add);
            } catch (DataIntegrityViolationException e){
                LOGGER.debug(e.getMessage());
                return "至少有一个用户的用户名或手机号与已有信息重复！";
            } catch (IOException|DataAccessException ex){
                LOGGER.error("",ex);
                return "处理文件过程中发生异常，请稍后重试！";
            } catch (IllegalArgumentException exx){
                return "上传失败，文件格式不合法，请重新选择！";
            }
        }
        return "上传失败，文件不合法！";
    }

    public String updateImage(int uid, FileItem image) {
        try(InputStream stream = image.getInputStream()) {
            String base64 = ImageBase64Util.encodeToBase64(stream,64,64);
            if (userDao.updateImage(uid, base64,new java.sql.Date(System.currentTimeMillis())) != 0) {
                return "更新用户头像成功！";
            }
            return "更新头像失败！";
        } catch (IOException e) {
            LOGGER.error("",e);
            return "解析图片失败！";
        } catch (IllegalArgumentException ex) {
            return ex.getMessage();
        }
    }

    /**
     * 修改用户信息。<br>
     * 可以修改的信息有：1.用户名（仅超管），2.密码（不要通过此处修改，用下面修改密码的方法），3.真实姓名，4.手机号<br>
     * 盐值将重新计算。
     * @param info 包括username,password,truename,gender,phone
     */
    public String updateUser(int uid,String[] info,short role,int salt,boolean skipCheck) {
        assert role>=0&&role<=2;
        User u = new User(info[0],info[1],info[2],info[3],info[4],role);
        u.setUid(uid);
        u.setSalt(salt);
        if(!skipCheck&&role==2) {
            if (reservationDao.getActiveReservationCountByUser(uid) > 0)
                return "更新失败，该用户尚有未签退/放弃的座位预定信息！";
        }
        if(VerifyUtil.verifyPassword(u.getPassword())) { //是md5
            encryptPasswordSalted(u);
        }
        try {
            int res = userDao.update(u);
            if(res != 0) {
                synchronized (UserService.class) {
                    userNameCache.put(u.getUsername(), u);
                    idCache.put(uid, u);
                    phoneCache.put(u.getPhone(),u);
                }
                return "更新成功！";
            }
            return "更新失败，请确认用户存在！";
        } catch (DataIntegrityViolationException e) {
            return "更新失败，用户名或手机号已存在！";
        }
    }

    /**通过用户名和密码修改密码*/
    public String modifyPswd(String username, String oldPswd, String newPswd) {
        //1.根据用户名密码查找，如果没有符合的，说明用户不存在或原密码错误
        User userToBeModified = login(username,oldPswd); //id一定not null
        if (userToBeModified == null)
            return String.format("用户%s不存在，或原密码错误！",username);
        //2.存在原用户
        userToBeModified.setPassword(newPswd);
        return updateUser(userToBeModified.getUid(),
                args(userToBeModified),
                userToBeModified.getRole(),
                userToBeModified.getSalt(),
                true);
    }
    /**通过手机号+验证码重置密码。用于忘记密码的情况*/
    public String resetPswd(String phone, String newPswd) {
        // 1.获取用户信息
        User u = loginByPhone(phone);
        if(u == null)
            return "用户不存在！";
        //2.加密用户密码
        u.setPassword(newPswd);
        return updateUser(u.getUid(),
                args(u),
                u.getRole(),
                u.getSalt(),
                false);
    }
    public String removeUser(int uid) {
        //1.根据id查找用户
        User u = idCache.get(uid);
        if(u == null)
            u = userDao.getUserByUid(uid);
        if (u == null)
            return "用户不存在！";
        if(u.getRole()==2) {
            if (reservationDao.getActiveReservationCountByUser(uid) > 0)
                return "删除失败，该用户尚有未签退/放弃的座位预定信息！";
        } else if (u.getRole()==1) {
            if(roomDao.getRoomsByAdministrator(uid).size()>0)
                return "删除失败，该用户仍有正在管理的阅览室！";
        }
        //2.从缓存中删除
        try {
            userDao.delete(u);
            synchronized (UserService.class) {
                userNameCache.invalidate(u.getUsername());
                idCache.invalidate(uid);
                phoneCache.invalidate(u.getPhone());
            }
            return "删除成功！";
        } catch (DataAccessException e) {
            LOGGER.error("",e);
            return "删除失败，请稍后重试！";
        }
    }

    public String removeUser(String username) {
        //1.通过用户名查找用户
        User u = userNameCache.get(username);
        if (u == null)
            u = userDao.getUserByUsername(username); //不用put进去了
        if(u == null)
            return "用户不存在！";
        if(u.getRole()==2) {
            if (reservationDao.getActiveReservationCountByUser(u.getUid()) > 0)
                return "删除失败，该用户尚有未签退/放弃的座位预定信息！";
        } else if (u.getRole()==1) {
            if(roomDao.getRoomsByAdministrator(u.getUid()).size()>0)
                return "删除失败，该用户仍有正在管理的阅览室！";
        }
        //2.从2个缓存中删除。
        try {
            userDao.delete(u);
            synchronized (UserService.class) {
                //从缓存1中删除
                userNameCache.invalidate(username);
                //从缓存2中删除
                idCache.invalidate(u.getUid());
                phoneCache.invalidate(u.getPhone());
            }
            return "删除成功！";
        } catch (DataAccessException e) {
            LOGGER.error("",e);
            return "删除失败，请稍后重试！";
        }
    }
    /**执行登录动作，返回登录的用户。若登录失败（密码错误等），返回null*/
    public User login(String username,String password) {
        User u = userNameCache.get(username);
        if(u == null) {
            u = userDao.getUserByUsername(username);
            userNameCache.put(username,u);
        }
        if (u != null) {
            password = EncryptUtil.encrypt(password, Integer.toString(u.getSalt()), StandardCharsets.ISO_8859_1);
            return u.getPassword().equals(password) ? u : null;
        }
        return null; //用户不存在
    }

    public User login(int uid, String password) {
        User u = idCache.get(uid);
        if (u == null) {
            u = userDao.getUserByUid(uid);
            idCache.put(uid,u);
        }
        if (u != null) {
            password = EncryptUtil.encrypt(password,Integer.toString(u.getSalt()), StandardCharsets.ISO_8859_1);
            return u.getPassword().equals(password) ? u : null;
        }
        return null;
    }
    /**使用手机号+验证码登录，也可用于忘记密码时寻找密码*/
    public User loginByPhone(String phone) {
        //1.获取用户名
        User u = phoneCache.get(phone);
        if (u == null){
            u = userDao.getUserByPhone(phone);
            phoneCache.put(phone,u);
        }
        return u;
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

    /**
     * 指定uid，获取用户头像
     * @return 用户头像数据。如果没有返回null
     */
    public byte[] getUserImage(int uid) throws IllegalArgumentException {
        String base64 = userDao.getUserImageByUid(uid).getImgBase64();
        byte[] data = ImageBase64Util.base64ToImageData(base64);
        if (data == null) {
            if (DEFAULT_IMAGE_DATA == null) {
                try (InputStream stream = ImageBase64Util.class.getResourceAsStream("/user-img.jpg")) {
                    assert stream != null;
                    DEFAULT_IMAGE_DATA = new byte[stream.available()];
                    stream.read(DEFAULT_IMAGE_DATA);
                } catch (NullPointerException | IOException e) {LOGGER.error("",e);return null;} //impossible
            }
            data = DEFAULT_IMAGE_DATA;
        }
        return data;
    }
}
