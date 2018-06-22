package com.sxq.springmvc.service.impl;

import com.sxq.springmvc.dao.RoleDao;
import com.sxq.springmvc.pojo.Role;
import com.sxq.springmvc.service.RoleService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService{

    @Autowired
    private RoleDao roleDao = null;

    Logger log = Logger.getLogger(RoleServiceImpl.class);

    /**
     * 使用@Cacheable定义缓存策略
     * 当缓存钟有值，则返回缓存数据，否则访问方法得到数据
     * 通过value引用缓存管理器，通过key定义键
     * @param id
     * @return
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED,
        propagation = Propagation.REQUIRED)
    @Cacheable(value = "redisCacheManager", key = "'redis_role_' + #id")
    public Role getRole(Long id) {
        log.info("execute getRole");
        return roleDao.getRole(id);
    }

    /**
     * 删除数据库记录的同时，使用@CacheEvict删除redis缓存对应的key
     * @param id
     * @return 返回删除记录条数
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED,
        propagation = Propagation.REQUIRED)
    @CacheEvict(value = "redisCacheManager", key = "'redis_role_' + #id")
    public int deleteRole(Long id) {
        return roleDao.deleteRole(id);
    }

    /**
     * 使用@CachePut则标识无论如何都会执行方法，最后将方法的返回值再保存到缓存中
     * 使用在插入数据的地方，则标识保存到数据库后，会同步插入到Redis缓存中
     *
     * @param role
     * @return 角色对象（会回填主键）
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED,
        propagation = Propagation.REQUIRED)
    @CachePut(value = "redisCacheManager", key = "'redis_role_' + #result.id")
    public Role insertRole(Role role) {
        log.info("before insert:" + role.toString());
        roleDao.insertRole(role);
        log.info("after insert:" + role.toString());// test if primary key has been feed back
        //在mapper.xml中需要配置useGeneratedKeys="true" keyProperty="id"
        return role;
    }

    /**
     *
     * @param role
     * @return 影响条数
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.REQUIRED)
    @CachePut(value = "redisCacheManager", key = "'redis_role_' + #role.id")
    public Role updateRole(Role role) {
        roleDao.updateRole(role);
        return role;
    }

    /**
     * 不缓存
     *
     * @param roleName
     * @param note
     * @return
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED,
        propagation = Propagation.REQUIRED)
    public List<Role> findRoles(String roleName, String note) {
        return roleDao.findRoles(roleName, note);
    }
}