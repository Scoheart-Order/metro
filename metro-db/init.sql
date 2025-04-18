CREATE DATABASE IF NOT EXISTS metro;
USE metro;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
    credentials_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    money DECIMAL(19,4) NOT NULL DEFAULT 0.0 COMMENT '金额（精确到小数点后4位）',
    real_name VARCHAR(50) COMMENT '真实姓名',
    email VARCHAR(100) COMMENT '电子邮箱',
    phone VARCHAR(20) COMMENT '手机号码',
    gender VARCHAR(10) COMMENT '性别',
    birth_date DATE COMMENT '出生日期',
    avatar VARCHAR(255) COMMENT '头像URL',
    address VARCHAR(255) COMMENT '地址',
    bio TEXT COMMENT '个人简介',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 角色表
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id)
) ENGINE=InnoDB;

-- 公告表
CREATE TABLE IF NOT EXISTS announcements (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL COMMENT '公告标题',
    content TEXT NOT NULL COMMENT '公告内容',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    admin_id BIGINT NOT NULL COMMENT '发布管理员ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Feedback表
CREATE TABLE IF NOT EXISTS feedback (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    rating INT NOT NULL,
    created_at TIMESTAMP NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Request表
CREATE TABLE IF NOT EXISTS request (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Reply表
CREATE TABLE IF NOT EXISTS reply (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    parent_id BIGINT NOT NULL,
    parent_type VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    is_admin BOOLEAN NOT NULL DEFAULT FALSE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 地铁线路表
CREATE TABLE IF NOT EXISTS line (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '线路名称',
    code VARCHAR(20) UNIQUE NOT NULL COMMENT '线路编码',
    color VARCHAR(10) COMMENT '线路颜色',
    operator VARCHAR(50) COMMENT '运营商'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 车站表
CREATE TABLE IF NOT EXISTS station (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '站点名称',
    code VARCHAR(20) UNIQUE COMMENT '站点编码'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 路线方向表
CREATE TABLE IF NOT EXISTS route (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    line_id BIGINT NOT NULL COMMENT '所属线路ID',
    name VARCHAR(50) NOT NULL COMMENT '路线名称',
    start_station_id BIGINT COMMENT '起始站点ID',
    end_station_id BIGINT COMMENT '终点站点ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 路线停靠站点表
CREATE TABLE IF NOT EXISTS stop (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    route_id BIGINT NOT NULL COMMENT '所属路线ID',
    station_id BIGINT NOT NULL COMMENT '站点ID',
    seq INT NOT NULL COMMENT '序号',
    is_transfer BOOLEAN DEFAULT FALSE COMMENT '是否为换乘站',
    UNIQUE KEY (route_id, station_id) COMMENT '一条路线上一个站点只能出现一次',
    UNIQUE KEY (route_id, seq) COMMENT '同一路线上序号不能重复'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 列车行程表
CREATE TABLE IF NOT EXISTS train_trip (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    route_id BIGINT NOT NULL COMMENT '所属路线ID',
    train_number VARCHAR(20) NOT NULL COMMENT '列车车次号',
    run_date DATE NOT NULL COMMENT '运行日期',
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 到站时刻表
CREATE TABLE IF NOT EXISTS stop_time (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    train_trip_id BIGINT NOT NULL COMMENT '所属列车行程ID',
    stop_id BIGINT NOT NULL COMMENT '所属停靠点ID',
    arrival_time TIME COMMENT '到达时间',
    departure_time TIME COMMENT '离开时间',
    stop_seq INT COMMENT '在当前行程中的顺序',
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;