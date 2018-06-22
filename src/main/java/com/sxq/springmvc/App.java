package com.sxq.springmvc;

import com.sxq.springmvc.config.RedisConfig;
import com.sxq.springmvc.config.RootConfig;
import com.sxq.springmvc.pojo.Role;
import com.sxq.springmvc.service.RoleService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class App {

    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(RootConfig.class, RedisConfig.class);
        RoleService roleService = ctx.getBean(RoleService.class);
        Role role = new Role();
        role.setRoleName("name_1");
        role.setNote("role_note_1");
        roleService.insertRole(role);
        Role getRole = roleService.getRole(role.getId());//从缓存中获取
        getRole.setNote("role_note_1_update");
        roleService.updateRole(getRole);
        /**
         * 查看Redis所有键值，使用:
         *
         *<code>redis-cli key * </code>
         *
         */
//        roleService.deleteRole(getRole.getId());
    }
}