/**
 * Role name constants to be used throughout the application
 * for consistent role checking.
 */
export const ROLE_NAMES = {
  ADMIN: 'ROLE_ADMIN',
  SUPER_ADMIN: 'ROLE_SUPER_ADMIN',
  USER: 'ROLE_USER'
};

/**
 * Helper function to check if a user has a specific role
 */
export function hasRole(userRoles: { name: string }[] | null | undefined, roleName: string): boolean {
  if (!userRoles?.length) return false;
  return userRoles.some(role => role.name === roleName);
}

/**
 * Check if a user has admin privileges (either admin or superadmin)
 */
export function isAdmin(userRoles: { name: string }[] | null | undefined): boolean {
  if (!userRoles?.length) return false;
  return userRoles.some(role => 
    role.name === ROLE_NAMES.ADMIN || role.name === ROLE_NAMES.SUPER_ADMIN
  );
}

/**
 * Check if a user has superadmin privileges
 */
export function isSuperAdmin(userRoles: { name: string }[] | null | undefined): boolean {
  if (!userRoles?.length) return false;
  return userRoles.some(role => role.name === ROLE_NAMES.SUPER_ADMIN);
} 