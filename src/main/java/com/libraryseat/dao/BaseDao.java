package com.libraryseat.dao;

import com.libraryseat.utils.JDBCUtil;
import com.libraryseat.utils.VerifyUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class BaseDao {
    protected final JdbcTemplate template = new JdbcTemplate(JDBCUtil.getDataSource());
    private static final Logger LOGGER = LogManager.getLogger(BaseDao.class.getName());
    public enum Order {
        ASCEND("ASC"),DESCEND("DESC");
        final String value;
        Order(String value) {
            this.value = value;
        }
        Order() {
            this.value = "ASC";
        }
    }

    public abstract void add(Object o);
    public abstract void delete(Object o);
    public abstract int update(Object o);

    protected <T> List<T> findByPage(String sql,
                                     RowMapper<T> rowMapper,
                                     int start,
                                     int rows,
                                     Map<String, String> condition) {
        return findByPage(sql,rowMapper,start,rows,condition,null,null);
    }

    protected <T> List<T> findByPage(String sql,
                                     RowMapper<T> rowMapper,
                                     int start,
                                     int rows,
                                     Map<String, String> conditions,
                                     String orderColumn,
                                     Order order) { //查
        StringBuilder sb = new StringBuilder(sql);
        //2.遍历map
        Set<String> keySet = conditions.keySet();
        //定义参数的集合
        ArrayList<Object> params = new ArrayList<>(conditions.size());
        for (String key : keySet) {
            //排除分页条件参数
            if("currentPage".equals(key) || "rows".equals(key)){
                continue;
            }
            //获取value
            String value = conditions.get(key);
            //判断value是否有值
            if(value != null && !"".equals(value)){
                //有值
                sb.append(" and ").append(key).append(" =? ");
                params.add(value);//？条件的值
            }
        }
//        LogUtil.log(LOGGER,Level.FINE,sb.toString());
        //添加分页查询
        if (VerifyUtil.verifyNonEmptyStrings(orderColumn)&&order != null) {
            sb.append(" order by ").append(orderColumn).append(' ').append(order.value);
        }
        sb.append(" limit ?,? ");
        //添加分页查询参数值
        params.add(start);
        params.add(rows);
        LOGGER.debug(sb.toString());
        return template.query(sb.toString(), rowMapper,params.toArray());
    }
}
