# 登录
curl -X POST http://localhost:8123/api/auth/login \
    -H "Content-Type: application/json" \
    -d '{"username": "user1", "password": "123456"}'

# 注册
curl -X POST http://localhost:8123/api/auth/register \
    -H "Content-Type: application/json" \
    -d '{"username": "user1", "password": "123456", "email": "user1@example.com"}'

curl -X POST http://localhost:8123/api/auth/register -H "Content-Type: application/json" -d '{"username": "testuser2", "password": "123456", "email": "test2@example.com", "phone": "13800138001"}'

curl -X POST http://localhost:8123/api/auth/login/phone -H "Content-Type: application/json" -d '{"phone": "13800138001", "password": "123456"}'

# 刷新令牌
curl -X POST http://localhost:8123/api/auth/refresh-token \
    -H "Authorization: Bearer <access_token>"

# 登出
curl -X POST http://localhost:8080/api/auth/logout \
    -H "Authorization: Bearer <access_token>"

eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0dXNlcjIiLCJpYXQiOjE3NDQ4ODU2OTEsImV4cCI6MTc0NDk3MjA5MX0.ausC19GAA8cIe2CavFugepanaBwgZ49VGkE5KSIiA-RdvCap63-xxXx71Lsap7k5jpemEDv3EPNkwj-5iEPm6A