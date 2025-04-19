package com.scoding.metro.config;

import com.scoding.metro.entity.Role;
import com.scoding.metro.entity.User;
import com.scoding.metro.mapper.RoleMapper;
import com.scoding.metro.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用于应用启动时初始化超级管理员、普通管理员和普通用户账户
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SuperAdminInitializer implements ApplicationRunner {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;

    // 超级管理员账户信息
    @Value("${app.init.users.super-admin.username:superadmin}")
    private String superAdminUsername;
    
    @Value("${app.init.users.super-admin.password:88888888}")
    private String superAdminPassword;
    
    @Value("${app.init.users.super-admin.role:ROLE_SUPER_ADMIN}")
    private String superAdminRole;
    
    @Value("${app.init.users.super-admin.description:超级管理员}")
    private String superAdminDescription;
    
    // 普通管理员账户信息
    @Value("${app.init.users.admin.username:admin}")
    private String adminUsername;
    
    @Value("${app.init.users.admin.password:12345678}")
    private String adminPassword;
    
    @Value("${app.init.users.admin.role:ROLE_ADMIN}")
    private String adminRole;
    
    @Value("${app.init.users.admin.description:普通管理员}")
    private String adminDescription;
    
    // 普通用户账户信息
    @Value("${app.init.users.user.username:user1}")
    private String userUsername;
    
    @Value("${app.init.users.user.password:88888888}")
    private String userPassword;
    
    @Value("${app.init.users.user.role:ROLE_USER}")
    private String userRole;
    
    @Value("${app.init.users.user.description:普通用户}")
    private String userDescription;
    
    @Value("${app.init.users.user.initial-money:100.0}")
    private Double userInitialMoney;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("初始化系统角色...");
        
        // 初始化超级管理员、普通管理员和普通用户角色
        Role superAdminRoleObj = findOrCreateRole(superAdminRole, superAdminDescription);
        Role adminRoleObj = findOrCreateRole(adminRole, adminDescription);
        Role userRoleObj = findOrCreateRole(userRole, userDescription);
        
        // 初始化超级管理员账户
        initializeAdmin(superAdminUsername, superAdminPassword, superAdminRoleObj, superAdminDescription);
        
        // 初始化普通管理员账户
        initializeAdmin(adminUsername, adminPassword, adminRoleObj, adminDescription);
        
        // 初始化普通用户账户
        initializeUser(userUsername, userPassword, userRoleObj, userDescription);
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
     * 初始化普通用户账户
     * 
     * @param username 用户名
     * @param password 密码
     * @param role 角色
     * @param description 描述
     */
    private void initializeUser(String username, String password, Role role, String description) {
        log.info("检查{}账户是否存在...", description);
        
        // 检查账户是否已存在
        User existingUser = userMapper.getUserByUsername(username);
        if (existingUser != null) {
            log.info("{}账户已存在，无需初始化", description);
            return;
        }
        
        log.info("初始化{}账户...", description);
        
        // 创建普通用户
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setMoney(userInitialMoney);
        
        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        user.setCreateTime(now);
        user.setUpdateTime(now);
        
        // 保存普通用户
        userMapper.saveUser(user);
        
        // 关联用户和角色
        roleMapper.saveUserRole(user.getId(), role.getId());
        
        log.info("{}账户初始化完成: {}", description, user.getUsername());
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