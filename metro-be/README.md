# Metro Application

A Spring Boot application with user authentication and account management features.

## Features

- User registration and authentication
- JWT based security
- Role-based access control
- User account balance management and recharge system
- Super admin initialization at application startup
- Announcement management system for administrators
- User profile management

## API Endpoints

### Authentication

- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login and get authentication tokens
- `POST /api/auth/refresh-token` - Refresh access token
- `POST /api/auth/logout` - Logout and invalidate tokens
- `GET /api/auth/me` - Get current user information

### User Management

- `GET /api/user/profile` - Get current user profile
- `PUT /api/user/profile` - Update current user profile
- `PUT /api/user/profile/password` - Update current user password
- `POST /api/user/recharge` - Recharge account (with bonus system)
- `GET /api/user/balance` - Get current account balance

### Announcement Management

- `GET /api/announcements` - Get all announcements (public)
- `GET /api/announcements/{id}` - Get announcement by ID (public)
- `POST /api/announcements` - Create a new announcement (admin only)
- `PUT /api/announcements/{id}` - Update an announcement (admin only)
- `DELETE /api/announcements/{id}` - Delete an announcement (admin only)

## Database Setup

Run the SQL script in the `sql` directory:

```
sql/init.sql - Contains all database tables and schema
```

## Super Admin

The application automatically initializes a super admin account at startup if it doesn't already exist:

- Username: `superadmin`
- Password: `88888888`
- Role: `ROLE_SUPER_ADMIN`

## Roles

The application has two admin roles:

- `ROLE_SUPER_ADMIN` - Super administrator with all permissions
- `ROLE_ADMIN` - Regular administrator with announcement management permissions

## Testing

The `curl` directory contains shell scripts to test the API:

```
curl/index.sh - Test authentication endpoints
curl/recharge.sh - Test recharge functionality
``` 