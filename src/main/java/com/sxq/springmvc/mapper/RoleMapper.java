package com.sxq.springmvc.mapper;

import com.sxq.springmvc.pojo.Role;

import java.util.List;

public interface RoleMapper{

     int insertRole(Role role);
     int deleteRole(Long id);
     int updateRole(Role role);
     Role getRole(Long id);
     List<Role> findRoles(String roleName);
}