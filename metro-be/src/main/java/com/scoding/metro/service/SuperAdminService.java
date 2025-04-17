package com.scoding.metro.service;

import com.scoding.metro.entity.User;

import java.util.List;

/**
 * 超级管理员服务接口
 * 提供用户管理相关的操作
 */
public interface SuperAdminService {
    
    /**
     * 获取所有用户
     * 
     * @return 用户列表
     */
    List<User> listAllUsers();
    
    /**
     * 根据ID获取用户
     * 
     * @param id 用户ID
     * @return 用户信息
     */
    User getUserById(Long id);
    
    /**
     * 创建新用户
     * 
     * @param user 用户信息
     * @return 创建后的用户
     */
    User saveUser(User user);
    
    /**
     * 更新用户信息
     * 
     * @param id 用户ID
     * @param user 更新的用户信息
     * @return 更新后的用户
     */
    User updateUser(Long id, User user);
    
    /**
     * 切换用户状态（启用/禁用）
     * 
     * @param id 用户ID
     * @return 更新后的用户
     */
    User toggleUserStatus(Long id);
    
    /**
     * 重置用户密码
     * 
     * @param id 用户ID
     * @param newPassword 新密码
     */
    void resetPassword(Long id, String newPassword);
    
    /**
     * 删除用户
     * 
     * @param id 用户ID
     */
    void removeUser(Long id);
} 