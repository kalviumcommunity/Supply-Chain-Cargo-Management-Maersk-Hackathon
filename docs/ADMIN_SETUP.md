# Admin User Setup Instructions

This guide explains how to set up the admin user for the Supply Chain Cargo Management system.

## Overview

The system now implements role-based access control with the following roles:
- **ADMIN**: Can approve/reject pending operators
- **OPERATOR**: Can access the full dashboard and manage cargo/shipments/routes
- **PENDING**: New signups waiting for admin approval

## Creating the Admin User

### Method 1: Using SQL Script (Recommended)

1. **Connect to your PostgreSQL database**:
   ```bash
   psql -h your-database-host -U your-username -d your-database-name
   ```

2. **Execute the SQL script**:
   ```bash
   \i server/create-admin-user.sql
   ```

   Or run it directly:
   ```bash
   psql -h your-database-host -U your-username -d your-database-name -f server/create-admin-user.sql
   ```

### Method 2: Direct SQL Query

Execute this SQL directly in your database:

```sql
INSERT INTO users (email, password, name, provider, role, is_active, picture, provider_id, created_at, updated_at)
VALUES (
  'admin@gmail.com',
  '$2a$10$MoBrMzedeInSwu2/1vkx0OkyMxikxfoy9qsdgNgMoG6bACk567BIa',
  'System Administrator',
  'local',
  'ADMIN',
  true,
  NULL,
  NULL,
  NOW(),
  NOW()
)
ON CONFLICT (email) DO UPDATE SET
  role = 'ADMIN',
  is_active = true,
  updated_at = NOW();
```

## Admin Credentials

- **Email**: `admin@gmail.com`
- **Password**: `123456`

**⚠️ IMPORTANT**: Change this password immediately after first login in a production environment!

## How the System Works

### 1. New Operator Signup
- User signs up via the login page
- User is created with `role = 'PENDING'` and `is_active = false`
- User receives message: "Registration successful. Your account is pending admin approval."
- User **cannot** login until approved

### 2. Admin Approval Process
- Admin logs in with `admin@gmail.com` / `123456`
- Admin is redirected to `/admin` page (not the regular dashboard)
- Admin sees list of pending operators
- Admin can:
  - **Approve**: Changes user to `role = 'OPERATOR'` and `is_active = true`
  - **Reject**: Deletes the user from the database

### 3. Operator Login
- After approval, operator can login normally
- Operator is redirected to `/dashboard`
- Operator has full access to all features (cargo, shipments, routes, vendors, etc.)

### 4. Admin Login
- Admin logs in with their credentials
- Admin is **always** redirected to `/admin` page
- Admin **cannot** access regular operator routes (dashboard, cargo, etc.)
- Admin's sole purpose is to approve/reject operators

## Existing Users

All existing users with `role = 'OPERATOR'` are **not affected** by this change. They can continue logging in normally.

## API Endpoints

### Admin Endpoints (require ADMIN role)
- `GET /api/admin/pending-users` - Get list of pending users
- `GET /api/admin/all-users` - Get all users
- `POST /api/admin/approve-user/:userId` - Approve a pending user
- `DELETE /api/admin/reject-user/:userId` - Reject and delete a pending user

### Authentication Endpoints
- `POST /api/auth/signup` - Register new user (becomes PENDING)
- `POST /api/auth/login` - Login (checks if user is active)
- `POST /api/auth/logout` - Logout
- `GET /api/auth/user` - Get current user

## Testing the Flow

### Test New Operator Signup
1. Go to the login page
2. Click "Sign Up"
3. Fill in the form and submit
4. You should see: "Registration successful. Your account is pending admin approval."
5. Try to login - you should see: "Your account is pending admin approval."

### Test Admin Approval
1. Login as admin (`admin@gmail.com` / `123456`)
2. You should be redirected to `/admin`
3. You should see the pending user in the list
4. Click "Approve"
5. The user should disappear from the pending list

### Test Approved Operator Login
1. Logout from admin
2. Login with the newly approved operator credentials
3. You should be redirected to `/dashboard`
4. You should have full access to all features

## Database Schema Changes

No schema changes are required! The existing `users` table already has:
- `role` column (supports 'ADMIN', 'OPERATOR', 'PENDING')
- `is_active` column (boolean)

## Troubleshooting

### "Access denied" when accessing /admin
- Make sure you're logged in as admin
- Check the database to verify the user's role is 'ADMIN'

### Pending user not appearing in admin panel
- Check if the user was created with `role = 'PENDING'`
- Check browser console for any API errors

### Cannot login after approval
- Verify the user's `is_active` is set to `true`
- Verify the user's `role` changed to 'OPERATOR'

## Security Notes

1. **Change the default admin password** immediately in production
2. The admin endpoints are protected by authentication checks
3. All passwords are hashed using BCrypt
4. Session management is handled by Spring Security

## Impact on Kafka

**No changes to Kafka functionality.** The role-based access control is purely at the authentication/authorization layer and does not affect:
- Kafka message producers
- Kafka message consumers
- Event streaming
- Data pipelines

All Kafka integrations remain unchanged.
