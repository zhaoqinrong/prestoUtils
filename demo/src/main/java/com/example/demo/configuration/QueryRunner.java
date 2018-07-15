package com.example.demo.configuration;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.RowProcessor;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import javax.xml.ws.handler.Handler;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.dbutils.DbUtils.close;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QueryRunner {
    DataSource dataSource;
    Connection conn;

    public QueryRunner(DataSource dataSource) {
        this.dataSource = dataSource;
        if (conn == null) {
            try {
                this.conn = this.dataSource.getConnection();
            } catch (SQLException e) {
                log.error("创建连接失败");
                e.printStackTrace();
            }


        }
    }

    private <T> T query(Connection conn, boolean closeConn, String sql, ResultSetHandler<T> rsh)
            throws SQLException {
        if (conn == null) {
            throw new SQLException("Null connection");
        }

        if (sql == null) {
            if (closeConn) {
                close(conn);
            }
            throw new SQLException("Null SQL statement");
        }

        if (rsh == null) {
            if (closeConn) {
                close(conn);
            }
            throw new SQLException("Null ResultSetHandler");
        }

        Statement stmt = null;
        ResultSet rs = null;
        T result = null;

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            result = rsh.handle(rs);
        } catch (SQLException e) {
            log.error("查询出错{}", sql);
        } finally {
            try {
                close(rs);
            } finally {
                close(stmt);
                if (closeConn) {
                    close(conn);
                }
            }
        }

        return result;
    }

    /**
     *
     * @param closeConn 是否关闭
     * @param sql sql语句
     * @param rsh dbutils中的handler
     * @param <T>
     * @return
     */
    public <T> T query(boolean closeConn, String sql, ResultSetHandler<T> rsh) {
        try {
            return this.query(this.conn, closeConn, sql, rsh);
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("查询出错{}", sql);
        }
        return null;
    }

    /**
     * 查询结果封装到实体中
     * @param sql
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> List<T> queryListBean(String sql, Class clazz) {
        try {
            BeanListHandler<T> rsh = new BeanListHandler<T>(clazz);
            return this.query(this.conn, true, sql, rsh);

        } catch (SQLException e) {
            e.printStackTrace();
            log.error("查询出错{}", sql);
        }
        return null;
    }

    /**
     * 查询结果由ResultSetHandler决定
     * @param sql
     * @param rsh
     * @param <T>
     * @return
     * @throws SQLException
     */
    public <T> T query(String sql, ResultSetHandler<T> rsh) throws SQLException {
        if (conn != null) {
            return this.<T>query(conn, true, sql, rsh);
        }
        return null;
    }

    public <T> T queryBean(String sql, Class clazz) {
        try {
            BeanHandler<T> rsh = new BeanHandler<T>(clazz);
            return this.query(this.conn, true, sql, rsh);
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("查询出错{}", sql);
        }
        return null;
    }

    public <T> Map<String, Object> queryMap(String sql) {
        try {
            RowProcessor rowProcessor = new BasicRowProcessor();
            MapHandler rsh = new MapHandler(rowProcessor);
            return this.query(this.conn, true, sql, rsh);
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("查询出错{}", sql);
        }
        return null;
    }

    public <T> List<Map<String, Object>> queryListMap(String sql) {
        try {
            RowProcessor rowProcessor = new BasicRowProcessor();
            MapListHandler rsh = new MapListHandler(rowProcessor);
            return this.query(this.conn, true, sql, rsh);
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("查询出错{}", sql);
        }
        return null;
    }

    /**
     * 查询Long
     * @param sql
     * @param name
     * @return
     */
    public Long getNumber(String sql, String name) {
        Map<String, Object> stringObjectMap = this.queryMap(sql);
        Object o = stringObjectMap.get(name);
        if (o != null) {
            try {

                Long l = Long.parseLong(o.toString());
                return l;
            } catch (Exception e) {
                e.printStackTrace();
                log.error("字符串转数字异常");
                return null;
            }
        }
        return null;


    }

    /**
     * 解析实体类，获取字段名称，对应数据库中的列名，字段类型和值
     * @param obj
     * @return
     */
    private List<Map<String, Object>> parseBean(Object obj) {
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        List<Map<String, Object>> list = new ArrayList<>();
        for (Field field : fields) {
            Column annotation = field.getAnnotation(Column.class);
            if (annotation != null) {
                Map<String, Object> map = new HashMap<>();
                String column = annotation.name();
                String type = field.getType().getName();
                String name = field.getName();
                try {
                    PropertyDescriptor pd = new PropertyDescriptor(name, clazz);
                    Method readMethod = pd.getReadMethod();
                    try {
                        Object invoke = readMethod.invoke(obj);
                        System.out.println(invoke);
                        if (name == null || type == null || column == null || invoke == null) {
                            continue;
                        }
                        map.put("name", name);
                        map.put("type", type);
                        map.put("column", column);
                        map.put("value", invoke);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                } catch (IntrospectionException e) {
                    e.printStackTrace();
                }
                list.add(map);

            }
        }
        return list;

    }

    /**
     * 根据实体类中属性是否有值，属性上是否有@column注解，来生成带where条件的sql
     * where 1=1  AND username='123' AND password='adimin' AND date='Sun Jul 15 18:29:20 CST 2018' AND date1='2018-07-15' AND isnum=true
     * @param obj 具体的bean
     * @return
     */
    public StringBuilder fillStatementWithBean(Object obj) {
        if (obj == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(" where 1=1 ");
        List<Map<String, Object>> list = this.parseBean(obj);
        for (Map<String, Object> map : list) {
            Object type = map.get("type");
            if (type != null) {

                if ("java.lang.String".equals(type) || type.toString().contains("Date")) {
                    sb.append(" AND ")
                            .append(map.get("column"))
                            .append("='")
                            .append(map.get("value"))
                            .append("'");
                    continue;
                }
            }
            sb.append(" AND ")
                    .append(map.get("column"))
                    .append("=")
                    .append(map.get("value"))
                    .append("");
        }
        return sb;
    }
}
