package com.sxq.springmvc.plugin;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.Configuration;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;


@Intercepts({
        @Signature(
                type = StatementHandler.class,
                method = "prepare",
                args = {Connection.class, Integer.class}
        )
})
public class PagePlugin implements Interceptor {

    private Integer defaultPage;

    private Integer defaultPageSize;

    private Boolean defaultUseFlag;

    private Boolean defaultCheckFlag;

    private Boolean defaultCleanOrderBy;


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler stmtHandler = (StatementHandler) getUnProxyObject(invocation.getTarget());
        MetaObject metaStatementHandler = SystemMetaObject.forObject(stmtHandler);
        String sql = (String) metaStatementHandler.getValue("delegate.boundSql.sql");
        MappedStatement mappedStatement = (MappedStatement) metaStatementHandler.getValue("delegate.mappedStatement");
        if (!checkSelect(sql)) {
            return invocation.proceed();
        }
        BoundSql boundSql = (BoundSql) metaStatementHandler.getValue("delegate.boundSql");
        Object parameterObject = boundSql.getParameterObject();
        PageParams pageParams = getPageParamsForParamObj(parameterObject);
        if (pageParams == null) {
            return invocation.proceed();
        }

        // 获取配置中是否启用分页功能
        Boolean useFlag = pageParams.getUseFlag() == null ? this.defaultUseFlag : pageParams.getUseFlag();
        if (!useFlag) { // 不使用分页插件
            return invocation.proceed();
        }
        // 获取相关配置的参数
        Integer pageNum = pageParams.getPage() == null ? defaultPage : pageParams.getPage();
        Integer pageSize = pageParams.getPageSize() == null ? defaultPageSize : pageParams.getPageSize();
        Boolean checkFlag = pageParams.getCheckFlag() == null ? defaultCheckFlag : pageParams.getCheckFlag();
        Boolean cleanOrderBy = pageParams.getCleanOrderBy() == null ? defaultCleanOrderBy
                : pageParams.getCleanOrderBy();
        // 计算总条数
        int total = getTotal(invocation, metaStatementHandler, boundSql, cleanOrderBy);
        // 回填总条数到分页参数
        pageParams.setTotal(total);
        // 计算总页数.
        int totalPage = total % pageSize == 0 ? total / pageSize : total / pageSize + 1;
        // 回填总页数到分页参数
        pageParams.setTotalPage(totalPage);
        // 检查当前页码的有效性
        checkPage(checkFlag, pageNum, totalPage);
        // 修改sql
        return preparedSQL(invocation, metaStatementHandler, boundSql, pageNum, pageSize);

    }

    @Override
    public Object plugin(Object target) {
        //生成代理对象
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties props) {
        // 从配置中获取参数
        String strDefaultPage = props.getProperty("default.page", "1");
        String strDefaultPageSize = props.getProperty("default.pageSize", "50");
        String strDefaultUseFlag = props.getProperty("default.useFlag", "false");
        String strDefaultCheckFlag = props.getProperty("default.checkFlag", "false");
        String StringDefaultCleanOrderBy = props.getProperty("default.cleanOrderBy", "false");
        // 设置默认参数.
        this.defaultPage = Integer.parseInt(strDefaultPage);
        this.defaultPageSize = Integer.parseInt(strDefaultPageSize);
        this.defaultUseFlag = Boolean.parseBoolean(strDefaultUseFlag);
        this.defaultCheckFlag = Boolean.parseBoolean(strDefaultCheckFlag);
        this.defaultCleanOrderBy = Boolean.parseBoolean(StringDefaultCleanOrderBy);
    }

    /**
     * 每一个拦截器都会拦截并生成一个代理对象，这里从代理对象钟分离出真实对象
     *
     * @param target {@link Invocation}
     * @return 非代理的 {@link StatementHandler}对象
     */
    private Object getUnProxyObject(Object target) {
        MetaObject metaStatementHandler = SystemMetaObject.forObject(target);
        Object object = null;
        while (metaStatementHandler.hasGetter("h")) {
            object = metaStatementHandler.getValue("h");
            metaStatementHandler = SystemMetaObject.forObject(object);
        }
        if (object == null) {
            return target;
        }
        return object;
    }

    /**
     * 判断是否为select语句
     *
     * @param sql 当前执行的SQL
     * @return 是否为select语句
     */
    private boolean checkSelect(String sql) {
        String trimSql = sql.trim();
        int idx = trimSql.toLowerCase().indexOf("select");
        return idx == 0;
    }

    /**
     * 分离出分页参数{@link PageParams}
     *
     * @param parameterObject 执行参数
     * @return {@link PageParams}
     * @throws Exception
     */
    public PageParams getPageParamsForParamObj(Object parameterObject) throws Exception {
        PageParams pageParams = null;
        if (parameterObject == null) {
            return null;
        }
        if (parameterObject instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> paramMap = (Map<String, Object>) parameterObject;
            Set<String> keySet = paramMap.keySet();
            Iterator<String> iterator = keySet.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                Object value = paramMap.get(key);
                if (value instanceof PageParams) {
                    return (PageParams) value;
                }
            }
        } else if (parameterObject instanceof PageParams) {
            return (PageParams) parameterObject;
        } else {
            /**
             * 反射获取{@link PageParams}对象
             */
            Field[] fields = parameterObject.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.getType() == PageParams.class) {
                    PropertyDescriptor pd = new PropertyDescriptor(field.getName(), parameterObject.getClass());
                    Method method = pd.getReadMethod();
                    return (PageParams) method.invoke(parameterObject);
                }
            }
        }
        return pageParams;
    }


    /**
     * 执行SQL获取总条数
     *
     * @param ivt
     * @param metaStatementHandler
     * @param boundSql
     * @param cleanOrderBy
     * @return 查询总条数
     * @throws Throwable
     */
    private int getTotal(Invocation ivt, MetaObject metaStatementHandler,
                         BoundSql boundSql, Boolean cleanOrderBy) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) metaStatementHandler.getValue("delegate.mappedStatement");
        Configuration cfg = mappedStatement.getConfiguration();
        String sql = (String) metaStatementHandler.getValue("delegate.boundSql.sql");
        if (cleanOrderBy) {
            sql = this.cleanOrderByForSql(sql);
        }
        String countSql = "select count(*) as total from (" + sql + ") $_paging";
        Connection connection = (Connection) ivt.getArgs()[0];
        PreparedStatement ps = null;
        int total = 0;
        try {
            ps = connection.prepareStatement(countSql);
            BoundSql countBoundSql = new BoundSql(cfg, countSql, boundSql.getParameterMappings(), boundSql.getParameterObject());
            ParameterHandler handler = new DefaultParameterHandler(mappedStatement, boundSql.getParameterObject(), countBoundSql);
            handler.setParameters(ps);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                total = rs.getInt("total");
            }
        } finally {
            // 这里不关闭Connection，否则后续的SQL就没法继续执行
            if (ps != null) {
                ps.close();
            }
        }
        return total;
    }

    /**
     * @param sql
     * @return
     */
    private String cleanOrderByForSql(String sql) {
        StringBuilder sb = new StringBuilder(sql);
        String newSql = sql.toLowerCase();
        if (newSql.indexOf("order") == -1) {
            return sql;
        }
        int idx = newSql.lastIndexOf("order");
        return sb.substring(0, idx).toString();
    }

    /**
     * 检查当前页码的有效性
     *
     * @param checkFlag 检查标志
     * @param pageNum   当前页码
     * @param pageTotal 最大页码
     * @throws Throwable
     */
    private void checkPage(Boolean checkFlag, Integer pageNum, Integer pageTotal) throws Throwable {
        if (checkFlag) {
            //检查页码page是否合法
            if (pageNum > pageTotal) {
                throw new Exception("查询失败，查询页码【" + pageNum + "】大于总页数【" + pageTotal + "】！！");
            }
        }
    }

    /**
     * 预编译改写后的SQL,并设置分页参数
     *
     * @param invocation
     * @param metaStatementHandler
     * @param boundSql
     * @param pageNum
     * @param pageSize
     * @return
     * @throws Exception
     */
    private Object preparedSQL(Invocation invocation, MetaObject metaStatementHandler, BoundSql boundSql, int pageNum, int pageSize) throws Exception {
        String sql = boundSql.getSql();
        /**
         * TODO 支持不同数据库的分页查询
         */
        String newSql = "select * from (" + sql + ") $_paging_table limit ?, ?";
        //修改当前需要执行的SQL
        metaStatementHandler.setValue("delegate.boundSql.sql", newSql);
        //执行编译，相当于StatementHandler执行了prepared()方法
        Object statementObj = invocation.proceed();
        //设置两个分页参数
        this.preparePageDataParams((PreparedStatement) statementObj, pageNum, pageSize);
        return statementObj;
    }

    /**
     * 使用{@link PreparedStatement}预编译两个分页参数，如果数据的规则不一样，需要改写设置的参数规则
     *
     * @param ps
     * @param pageNum
     * @param pageSize
     * @throws Exception
     */
    private void preparePageDataParams(PreparedStatement ps, int pageNum, int pageSize) throws Exception {
        // prepared()方法编译SQL，由于MyBatis上下文没有分页参数的信息，所以这里需要设置这两个参数
        // 获取需要设置的参数个数，由于参数是最后的两个，所以很容易得到其位置
        int idx = ps.getParameterMetaData().getParameterCount();
        // 最后两个是我们的分页参数
        ps.setInt(idx - 1, (pageNum - 1) * pageSize);// 开始行
        ps.setInt(idx, pageSize); // 限制条数
    }
}