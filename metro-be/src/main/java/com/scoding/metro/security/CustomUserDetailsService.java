package com.scoding.metro.security;

import com.scoding.metro.entity.Role;
import com.scoding.metro.entity.User;
import com.scoding.metro.mapper.RoleMapper;
import com.scoding.metro.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;

    // 手机号正则表达式
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user;
        boolean isPhone = PHONE_PATTERN.matcher(username).matches();

        // 根据输入类型查询用户
        if (isPhone) {
            user = userMapper.getUserByPhone(username);
        } else {
            user = userMapper.getUserByUsername(username);
        }

        // 用户不存在时抛出异常
        if (user == null) {
            String errorType = isPhone ? "手机号" : "用户名";
            throw new UsernameNotFoundException(errorType + "不存在: " + username);
        }

        // 加载用户角色
        List<Role> roles = roleMapper.getRolesByUserId(user.getId());
        user.setRoles(roles);

        return new SecurityUser(user);
    }
}