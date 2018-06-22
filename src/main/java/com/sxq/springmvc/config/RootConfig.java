package com.sxq.springmvc.config;

import com.sxq.springmvc.utils.SqlSessionFactoryUtils;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;


import javax.sql.DataSource;
import java.util.Properties;

@Configuration
//定义Spring扫描包
@ComponentScan("com.*")
//使用事务驱动管理器
@EnableTransactionManagement
//实现接口TransactionManagementConfigurer，这样就可以配置注解驱动事务
public class RootConfig implements TransactionManagementConfigurer{

    private DataSource dataSource;


    /**
     * 配置数据库
     *
     * @return
     */
    @Bean(name = "dataSource")
    public DataSource initDataSource(){
        if(dataSource != null){
            return dataSource;
        }
        Properties props = new Properties();
        props.setProperty("driverClassName", "com.mysql.jdbc.Driver");
        props.setProperty("url", "jdbc:mysql://localhost:3306/ssm?useSSL=true");
        props.setProperty("username", "root");
        props.setProperty("password", "");
        try{
            dataSource = BasicDataSourceFactory.createDataSource(props);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return dataSource;
    }

    /**
     * 配置{@link SqlSessionFactory}
     * @return
     */
    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory initSqlSessionFactory(){
        return SqlSessionFactoryUtils.getSqlSessionFactory();
    }

    /**
     * 通过自动扫描，发现MyBatis Mapper接口
     *
     * @return
     */
    @Bean
    public MapperScannerConfigurer initMapperScannerConfigurer(){
        MapperScannerConfigurer msc = new MapperScannerConfigurer();
        //扫描包
//        msc.setBasePackage("com.sxq.springmvc.dao.*");//error
        msc.setBasePackage("com.*");
        msc.setSqlSessionFactoryBeanName("sqlSessionFactory");
        msc.setAnnotationClass(Repository.class);
        return msc;
    }

    /**
     * 实现接口方法，注册注解事务，档@Transactional使用的时候产生数据库事务
     * @return
     */
    @Override
    @Bean(name = "annotationDriverTransactionManager")
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(initDataSource());
        return transactionManager;
    }

}