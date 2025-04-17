package com.scoding.metro.mapper;

import com.scoding.metro.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    User getUserByUsername(@Param("username") String username);

    /**
     * 根据手机号获取用户信息
     *
     * @param phone 手机号
     * @return 用户信息
     */
    User getUserByPhone(@Param("phone") String phone);

    /**
     * 根据用户ID获取用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    User getUserById(@Param("id") Long id);

    /**
     * 保存用户信息
     *
     * @param user 用户信息
     * @return 影响行数
     */
    int saveUser(User user);

    /**
     * 根据用户ID更新用户余额
     *
     * @param userId 用户ID
     * @param money 更新后的余额
     * @return 影响的行数
     */
    int updateUserMoney(@Param("userId") Long userId, @Param("money") Double money);

    /**
     * 更新用户个人信息
     *
     * @param user 用户信息
     * @return 影响的行数
     */
    int updateUserProfile(User user);

    /**
     * 更新用户密码
     *
     * @param userId 用户ID
     * @param newPassword 新密码（已加密）
     * @return 影响的行数
     */
    int updatePassword(@Param("userId") Long userId, @Param("newPassword") String newPassword);
    
    /**
     * 获取所有用户（包含角色信息）
     *
     * @return 用户列表
     */
    List<User> listAllWithRoles();
    
    /**
     * 根据ID获取用户信息（包含角色信息）
     *
     * @param id 用户ID
     * @return 用户信息
     */
    User getUserByIdWithRoles(@Param("id") Long id);
    
    /**
     * 保存用户角色关系
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 影响行数
     */
    int saveUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);
    
    /**
     * 删除用户角色关系
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int removeUserRoles(@Param("userId") Long userId);
    
    /**
     * 更新用户状态
     *
     * @param userId 用户ID
     * @param enabled 是否启用
     * @return 影响行数
     */
    int updateUserStatus(@Param("userId") Long userId, @Param("enabled") boolean enabled);
    
    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 影响行数
     */
    int removeById(@Param("id") Long id);
}