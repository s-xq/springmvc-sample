package com.sxq.springmvc.service;

import com.sxq.springmvc.pojo.Role;

import java.util.List;

public interface RoleService{

    Role getRole(Long id);

    int deleteRole(Long id);

    Role insertRole(Role role);

    Role updateRole(Role role);

    List<Role> findRoles(String roleName, String note);
}