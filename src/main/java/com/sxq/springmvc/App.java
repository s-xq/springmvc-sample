package com.sxq.springmvc;

import com.sxq.springmvc.mapper.RoleMapper;
import com.sxq.springmvc.pojo.Role;
import com.sxq.springmvc.utils.SqlSessionFactoryUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

public class App{

    public static void main(String [] args){
        Logger log = Logger.getLogger(App.class);
        SqlSession sqlSession = null;
        try{
            sqlSession = SqlSessionFactoryUtils.openSqlSession();
            RoleMapper roleMapper = sqlSession.getMapper(RoleMapper.class);
            Role role = roleMapper.getRole(1L);
            log.info(role.getRoleName());
        }finally {
            if(sqlSession != null){
                sqlSession.close();
            }
        }
    }
}