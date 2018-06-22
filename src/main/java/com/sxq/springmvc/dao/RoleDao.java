package com.sxq.springmvc.dao;

import com.sxq.springmvc.pojo.Role;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleDao {

    int insertRole(Role role);

    int deleteRole(Long id);

    int updateRole(Role role);

    Role getRole(Long id);

    List<Role> findRoles(@Param("roleName") String roleName, @Param("note") String note);
}