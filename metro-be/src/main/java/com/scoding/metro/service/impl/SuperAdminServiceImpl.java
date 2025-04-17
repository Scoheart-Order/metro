package com.scoding.metro.service.impl;

import com.scoding.metro.entity.User;
import com.scoding.metro.entity.Role;
import com.scoding.metro.exception.BusinessException;
import com.scoding.metro.mapper.UserMapper;
import com.scoding.metro.mapper.RoleMapper;
import com.scoding.metro.service.SuperAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 超级管理员服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SuperAdminServiceImpl implements SuperAdminService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;
    private static final String DEFAULT_ROLE = "ROLE_USER";

    @Override
    public List<User> listAllUsers() {
        log.info("获取所有用户信息");
        return userMapper.listAllWithRoles();
    }

    @Override
    public User getUserById(Long id) {
        log.info("获取用户 ID:{} 的信息", id);
        User user = userMapper.getUserByIdWithRoles(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    @Override
    @Transactional
    public User saveUser(User user) {
        log.info("创建新用户: {}", user.getUsername());

        // 检查用户名是否已存在
        if (userMapper.getUserByUsername(user.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }

        prepareUserForSave(user);

        // 插入用户
        userMapper.saveUser(user);

        // 处理用户角色关系 - 从DTO中获取角色名称
        String roleName = user.getRoles() != null && !user.getRoles().isEmpty() && user.getRoles().get(0) != null
                ? user.getRoles().get(0).getName()
                : null;
        assignRoleToUser(user.getId(), roleName);

        return getUserById(user.getId());
    }

    /**
     * 准备用户数据用于保存
     */
    private void prepareUserForSave(User user) {
        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        user.setCreateTime(now);
        user.setUpdateTime(now);

        // 对密码进行加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 设置用户状态为启用
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
    }

    /**
     * 为用户分配角色
     */
    private void assignRoleToUser(Long userId, String roleName) {
        if (StringUtils.hasText(roleName)) {
            Role role = roleMapper.getRoleByName(roleName);
            if (role != null) {
                userMapper.saveUserRole(userId, role.getId());
                log.info("用户ID:{}已分配角色:{}", userId, roleName);
            } else {
                log.warn("角色不存在: {}, 将分配默认角色", roleName);
                assignDefaultRole(userId);
            }
        } else {
            assignDefaultRole(userId);
        }
    }

    /**
     * 分配默认角色给用户
     */
    private void assignDefaultRole(Long userId) {
        Role defaultRole = roleMapper.getRoleByName(DEFAULT_ROLE);
        if (defaultRole != null) {
            userMapper.saveUserRole(userId, defaultRole.getId());
            log.info("用户ID:{}已分配默认角色", userId);
        } else {
            log.error("默认角色{}不存在，用户ID:{}未分配任何角色", DEFAULT_ROLE, userId);
        }
    }

    @Override
    @Transactional
    public User updateUser(Long id, User userUpdate) {
        log.info("更新用户 ID:{} 的信息", id);

        // 检查用户是否存在
        User existingUser = getUserById(id);

        // 确保ID一致
        userUpdate.setId(id);

        // 保留原密码（不允许通过此方法更新密码）
        userUpdate.setPassword(existingUser.getPassword());

        // 设置更新时间
        userUpdate.setUpdateTime(LocalDateTime.now());

        // 更新用户信息
        userMapper.updateUserProfile(userUpdate);

        // 更新用户角色关系（如果提供了新的角色列表）
        if (userUpdate.getRoles() != null && !userUpdate.getRoles().isEmpty()) {
            // 先删除现有角色关系
            userMapper.removeUserRoles(id);

            // 获取角色名称
            String roleName = userUpdate.getRoles().get(0).getName();
            assignRoleToUser(id, roleName);
        }

        return getUserById(id);
    }

    @Override
    @Transactional
    public User toggleUserStatus(Long id) {
        log.info("切换用户 ID:{} 的状态", id);

        // 检查用户是否存在
        User user = getUserById(id);

        // 切换状态
        boolean newStatus = !user.isEnabled();
        user.setEnabled(newStatus);
        user.setUpdateTime(LocalDateTime.now());

        // 更新用户状态
        userMapper.updateUserStatus(id, newStatus);

        return getUserById(id);
    }

    @Override
    @Transactional
    public void resetPassword(Long id, String newPassword) {
        log.info("重置用户 ID:{} 的密码", id);

        // 检查用户是否存在
        getUserById(id);

        // 加密新密码
        String encodedPassword = passwordEncoder.encode(newPassword);

        // 更新密码
        userMapper.updatePassword(id, encodedPassword);
    }

    @Override
    @Transactional
    public void removeUser(Long id) {
        log.info("删除用户 ID:{}", id);

        // 检查用户是否存在
        getUserById(id);

        // 删除用户角色关系
        userMapper.removeUserRoles(id);

        // 删除用户
        userMapper.removeById(id);
    }
}