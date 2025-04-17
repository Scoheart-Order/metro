package com.scoding.metro.config;

import com.scoding.metro.entity.Role;
import com.scoding.metro.entity.User;
import com.scoding.metro.mapper.RoleMapper;
import com.scoding.metro.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用于应用启动时初始化超级管理员和普通管理员账户
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SuperAdminInitializer implements ApplicationRunner {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;

    // 超级管理员账户信息
    private static final String SUPER_ADMIN_USERNAME = "superadmin";
    private static final String SUPER_ADMIN_PASSWORD = "88888888";
    private static final String SUPER_ADMIN_ROLE = "ROLE_SUPER_ADMIN";
    
    // 普通管理员账户信息
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "12345678";
    private static final String ADMIN_ROLE = "ROLE_ADMIN";

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("初始化系统角色...");
        
        // 初始化超级管理员和普通管理员角色
        Role superAdminRole = findOrCreateRole(SUPER_ADMIN_ROLE, "超级管理员");
        Role adminRole = findOrCreateRole(ADMIN_ROLE, "普通管理员");
        
        // 初始化超级管理员账户
        initializeAdmin(SUPER_ADMIN_USERNAME, SUPER_ADMIN_PASSWORD, superAdminRole, "超级管理员");
        
        // 初始化普通管理员账户
        initializeAdmin(ADMIN_USERNAME, ADMIN_PASSWORD, adminRole, "普通管理员");
    }
    
    /**
     * 初始化管理员账户
     * 
     * @param username 用户名
     * @param password 密码
     * @param role 角色
     * @param description 描述
     */
    private void initializeAdmin(String username, String password, Role role, String description) {
        log.info("检查{}账户是否存在...", description);
        
        // 检查账户是否已存在
        User existingAdmin = userMapper.getUserByUsername(username);
        if (existingAdmin != null) {
            log.info("{}账户已存在，无需初始化", description);
            return;
        }
        
        log.info("初始化{}账户...", description);
        
        // 创建管理员用户
        User admin = new User();
        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setEnabled(true);
        admin.setAccountNonExpired(true);
        admin.setAccountNonLocked(true);
        admin.setCredentialsNonExpired(true);
        admin.setMoney(0.0);
        
        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        admin.setCreateTime(now);
        admin.setUpdateTime(now);
        
        // 保存管理员用户
        userMapper.saveUser(admin);
        
        // 关联用户和角色
        roleMapper.saveUserRole(admin.getId(), role.getId());
        
        log.info("{}账户初始化完成: {}", description, admin.getUsername());
    }
    
    /**
     * 查找或创建角色
     * 
     * @param roleName 角色名称
     * @param description 角色描述
     * @return 角色对象
     */
    private Role findOrCreateRole(String roleName, String description) {
        // 查找角色是否已存在
        Role role = roleMapper.getRoleByName(roleName);
        
        // 如果角色不存在，则创建新角色
        if (role == null) {
            role = new Role();
            role.setName(roleName);
            role.setDescription(description);
            
            roleMapper.saveRole(role);
            log.info("创建角色: {}", roleName);
        } else {
            log.info("角色已存在: {}", roleName);
        }
        
        return role;
    }
} 