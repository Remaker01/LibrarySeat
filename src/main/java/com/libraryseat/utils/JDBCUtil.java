package com.libraryseat.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class JDBCUtil {
    private static DataSource ds;
    private static final Logger LOGGER = LogManager.getLogger(JDBCUtil.class.getName());

    private JDBCUtil() {}
    static {
        try {
            //1.加载配置文件
            Context initContext = new InitialContext();
            Context context = (Context) initContext.lookup("java:comp/env");
            ds = (DataSource) context.lookup("/jdbc/library");
            LOGGER.debug("数据源加载成功");
        } catch (NamingException | ClassCastException e) {
            ds = null;
            LOGGER.fatal("数据源加载失败！",e);
        }
    }
    /**
     * 获取连接池对象
     */
    public static DataSource getDataSource(){
        return ds;
    }
    /**
     * 获取连接Connection对象
     */
    public static Connection getConnection() throws SQLException {
        return  ds.getConnection();
    }
}
