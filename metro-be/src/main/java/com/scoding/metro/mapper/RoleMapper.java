package com.scoding.metro.mapper;

import com.scoding.metro.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMapper {
    /**
     * 根据用户ID获取角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<Role> getRolesByUserId(@Param("userId") Long userId);

    /**
     * 保存角色
     *
     * @param role 角色信息
     * @return 影响行数
     */
    int saveRole(Role role);

    /**
     * 保存用户角色关联
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 影响行数
     */
    int saveUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /**
     * 根据角色名称查找角色
     *
     * @param name 角色名称
     * @return 角色信息，如果不存在则返回null
     */
    Role getRoleByName(@Param("name") String name);

    /**
     * 获取所有角色
     *
     * @return 所有角色列表
     */
    List<Role> getAllRoles();
} 