package com.sxq.springmvc.utils;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public class SqlSessionFactoryUtils{

    private final static Class<SqlSessionFactoryUtils> LOCK = SqlSessionFactoryUtils.class;

    private static SqlSessionFactory SQL_SESSION_FACTORY = null;

    private SqlSessionFactoryUtils(){}

    public static SqlSessionFactory getSqlSessionFactory(){
        synchronized(LOCK) {
            if(SQL_SESSION_FACTORY != null){
                return SQL_SESSION_FACTORY;
            }
            String resource = "mybatis-config.xml";
            try{
                InputStream inputStream = Resources.getResourceAsStream(resource);
                SQL_SESSION_FACTORY = new SqlSessionFactoryBuilder().build(inputStream);
            }catch (IOException ex){
                ex.printStackTrace();
                return null;
            }
            return SQL_SESSION_FACTORY;
        }
    }

    public static SqlSession openSqlSession(){
        if(SQL_SESSION_FACTORY == null){
            getSqlSessionFactory();
        }
        return SQL_SESSION_FACTORY.openSession();
    }
}