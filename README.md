## 图书馆座位预定系统
采用Spring+SpringMVC的图书馆座位预定系统，灵感来源于大学学校的图书馆小程序。

需求及总体设计见docx文件。

1.28 目前已完成dao,service层,一部分controller层和前端页面。

2.8  基本完成后端功能，前端页面部分完善中。

3.21 基本完成全部功能。

**4.7 重大更新：修改密码加密方式，改为加盐，需要在`users`表中新增一列`salt`。**
**详情见下文“数据库密码存储方式”一节。**
### 项目信息
**环境**

|   名称   |             值              |          备注          |
|:------:|:--------------------------:|:--------------------:|
|  操作系统  |       MS Windows 10        |                      |
|  IDE   |    Intellij IDEA 2022.1    |   Ultimate Edition   |
|  数据库   |         MySQL 8.0          |        InnoDB        |
| 项目管理工具 |           Maven            |                      |
|  运行环境  | OpenJDK 11+Apache Tomcat 9 | 未使用Java9及以上的语言特性和API |

**配置方法**

- 首先创建数据库 名称为library,运行根目录下ddl.sql文件创建表。表结构见docx文件。
> 数据库密码存储方式：
> ```
> pswd_md5=md5(md5(user.password)).toHexString()
> pswd_final=HmacSHA256(text=pswd_md5,key=user.salt.toString())
> //学生的默认密码为Student1，管理员的默认密码为Admin135
> ```
运行rand_users.py获取随机的用户数据文件users.sql。运行此sql文件向数据库中添加随机用户。

- 在/resources下创建文件jdbc.properties,加入以下内容：
```properties
driverClassName=com.mysql.cj.jdbc.Driver
url=jdbc:mysql://localhost:3306/library?characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8
username=你的数据库用户名
password=你的数据库密码
```

- 将/resources/library.properties中的省份(province)和城市(city)改为你所在的省份和城市的拼音。

- 在tomcat根目录/conf/context.xml中加入如下内容配置JNDI数据源：
```xml
<Resource name="jdbc/library" auth="Container"  
           type="javax.sql.DataSource" maxTotal="100" maxIdle="30" 
           maxWaitMillis="10000" username="" password="" 
           driverClassName="com.mysql.cj.jdbc.Driver" 
           url="jdbc:mysql://localhost:3306/library" 
           factory="org.apache.tomcat.jdbc.pool.DataSourceFactory"/>
```

同样将username和password换成你的数据库用户名和密码。

### 目前问题
1. ~~日志方面，目前记录日志很不方便，考虑修改为log4j等主流日志系统。~~
2. ~~暂未实现预定座位一定时间后自动释放的功能。~~
3. 暂未实现手机短信验证功能。

### 参考
[登录页面](https://blog.csdn.net/qq_41325698/article/details/102591169)