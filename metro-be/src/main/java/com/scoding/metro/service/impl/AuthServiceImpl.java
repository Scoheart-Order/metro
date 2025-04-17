package com.scoding.metro.service.impl;

import com.scoding.metro.common.RCode;
import com.scoding.metro.dto.LoginDto;
import com.scoding.metro.dto.PhoneLoginDto;
import com.scoding.metro.dto.RegisterDto;
import com.scoding.metro.dto.TokenDto;
import com.scoding.metro.entity.Role;
import com.scoding.metro.entity.User;
import com.scoding.metro.exception.BusinessException;
import com.scoding.metro.mapper.RoleMapper;
import com.scoding.metro.mapper.UserMapper;
import com.scoding.metro.security.SecurityUser;
import com.scoding.metro.service.AuthService;
import com.scoding.metro.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;

    @Override
    @Transactional
    public void register(RegisterDto registerDto) {
        if (userMapper.getUserByUsername(registerDto.getUsername()) != null) {
            throw new BusinessException(RCode.BAD_REQUEST, "用户名已存在");
        }
        
        // 检查手机号是否已存在
        if (userMapper.getUserByPhone(registerDto.getPhone()) != null) {
            throw new BusinessException(RCode.BAD_REQUEST, "手机号已存在");
        }
        
        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setEmail(registerDto.getEmail());
        user.setPhone(registerDto.getPhone());
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        
        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        user.setCreateTime(now);
        user.setUpdateTime(now);
        
        userMapper.saveUser(user);

        // 检查默认角色是否已存在
        String roleName = "ROLE_USER";
        Role userRole = roleMapper.getRoleByName(roleName);
        
        // 如果角色不存在，则创建
        if (userRole == null) {
            userRole = new Role();
            userRole.setName(roleName);
            userRole.setDescription("普通用户");
            roleMapper.saveRole(userRole);
        }

        // 分配角色给用户
        roleMapper.saveUserRole(user.getId(), userRole.getId());
    }

    @Override
    public TokenDto login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsername(),
                        loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);
        return new TokenDto(accessToken, refreshToken);
    }

    @Override
    public TokenDto loginByPhone(PhoneLoginDto phoneLoginDto) {
        try {
            // 使用标准UsernamePasswordAuthenticationToken
            // CustomUserDetailsService会自动识别手机号格式并进行相应处理
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            phoneLoginDto.getPhone(),
                            phoneLoginDto.getPassword()));
            
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String accessToken = jwtUtil.generateToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);

            return new TokenDto(accessToken, refreshToken);
        } catch (BadCredentialsException e) {
            throw new BusinessException(RCode.UNAUTHORIZED, "手机号或密码错误");
        }
    }

    @Override
    public TokenDto refreshToken(HttpServletRequest request) {
        String refreshToken = jwtUtil.extractRefreshToken(request);
        if (refreshToken == null) {
            throw new BusinessException(RCode.UNAUTHORIZED, "无效的刷新令牌");
        }

        String username = jwtUtil.extractUsername(refreshToken);
        if (username == null) {
            throw new BusinessException(RCode.UNAUTHORIZED, "无效的刷新令牌");
        }

        User user = userMapper.getUserByUsername(username);
        if (user == null) {
            throw new BusinessException(RCode.UNAUTHORIZED, "用户不存在");
        }

        // 加载用户角色
        List<Role> roles = roleMapper.getRolesByUserId(user.getId());
        user.setRoles(roles);

        // 创建安全用户
        SecurityUser securityUser = new SecurityUser(user);

        if (!jwtUtil.isTokenValid(refreshToken, securityUser)) {
            throw new BusinessException(RCode.UNAUTHORIZED, "刷新令牌已过期");
        }

        String newAccessToken = jwtUtil.generateToken(securityUser);
        String newRefreshToken = jwtUtil.generateRefreshToken(securityUser);

        return new TokenDto(newAccessToken, newRefreshToken);
    }

    @Override
    public void logout(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
    }

    @Override
    public User getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(RCode.UNAUTHORIZED, "用户未登录");
        }

        if (authentication.getPrincipal() instanceof SecurityUser) {
            return ((SecurityUser) authentication.getPrincipal()).getUser();
        }

        throw new BusinessException(RCode.UNAUTHORIZED, "无效的用户认证信息");
    }
}