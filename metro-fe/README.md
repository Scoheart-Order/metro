# 城市地铁便民服务信息系统前端

基于 Vue.js 的城市地铁便民服务信息系统前端项目，提供地铁时刻表查询、线路信息查询、用户评价和需求提交等功能。

## 技术栈

- Vue 3
- TypeScript
- Pinia（状态管理）
- Vue Router（路由管理）
- Axios（HTTP 请求）
- Element Plus（UI 组件库）
- Vite（构建工具）

## 功能特性

- 用户认证（登录、注册、密码找回）
- 地铁时刻查询
- 线路信息查询
- 评价发布与查看
- 需求提交与跟踪
- 个人信息管理
- 管理员控制台

## 快速开始

### 环境准备

确保已安装：
- Node.js 16+
- npm 7+

### 安装依赖

```bash
npm install
```

### 开发模式

```bash
npm run dev
```

### 构建生产版本

```bash
npm run build
```

### 预览生产构建

```bash
npm run preview
```

## 项目结构

```
metro-fe/
  ├── src/                  # 源代码
  │   ├── api/              # API 请求
  │   ├── assets/           # 静态资源
  │   ├── components/       # 公共组件
  │   ├── router/           # 路由配置
  │   ├── stores/           # Pinia 状态管理
  │   ├── views/            # 页面视图
  │   ├── App.vue           # 根组件
  │   └── main.ts           # 入口文件
  ├── public/               # 公共资源
  ├── index.html            # HTML 模板
  ├── vite.config.ts        # Vite 配置
  ├── tsconfig.json         # TypeScript 配置
  └── package.json          # 项目依赖
```

## 用户角色

### 普通用户

- 浏览地铁时刻表和线路信息
- 提交评价和需求
- 管理个人信息

### 普通管理员

- 管理地铁时刻表和线路信息
- 回复用户评价和需求
- 发布运营公告

### 系统管理员

- 拥有普通管理员的所有权限
- 管理普通管理员账户
- 配置系统权限

## Docker Deployment

### Building the Docker Image

To build the Docker image optimized for low-memory environments (2GB RAM):

```bash
docker build -t metro-fe .
```

The Dockerfile is already configured with memory optimizations for 2-core, 2GB environments.

### Running the Container

To run the container:

```bash
# Run with port mapping
docker run -d -p 3333:3333 --name metro-frontend --memory=1.5g --cpus=1 metro-fe
```

### Using Docker Compose

For a complete deployment with backend and database services:

```bash
docker-compose up -d
```

Docker Compose is configured with appropriate memory limits for all services.

### Accessing the Application

The application will be available at:

```
http://localhost:3333
```

## Build Optimization

This project is optimized for low-memory environments (2GB RAM). The build process:

- Limits Node.js memory usage
- Uses esbuild instead of Terser for faster, more memory-efficient minification
- Disables parallel processing to reduce memory spikes
- Uses optimized chunk splitting
- Applies selective compression
- Provides container memory limits

No additional configuration is needed for production builds.
