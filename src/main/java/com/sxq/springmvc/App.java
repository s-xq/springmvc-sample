package com.sxq.springmvc;

import com.sxq.springmvc.mapper.RoleMapper;
import com.sxq.springmvc.plugin.PageParams;
import com.sxq.springmvc.pojo.Role;
import com.sxq.springmvc.utils.SqlSessionFactoryUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import java.util.List;

public class App {

    public static void main(String[] args) {
        testPagePlugin();
    }


    private static void testPagePlugin() {
        Logger log = Logger.getLogger(App.class);
        SqlSession sqlSession = null;
        try {
            sqlSession = SqlSessionFactoryUtils.openSqlSession();
            RoleMapper roleMapper = sqlSession.getMapper(RoleMapper.class);
            PageParams pageParams = new PageParams();
            pageParams.setPageSize(5);
            List<Role> roleList = roleMapper.findRoles(pageParams, "admin");
            log.info(roleList.size());
        } catch (Exception ex) {
            ex.printStackTrace();
            sqlSession.rollback();
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }
}