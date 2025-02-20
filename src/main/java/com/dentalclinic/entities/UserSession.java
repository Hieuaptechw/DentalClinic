package com.dentalclinic.entities;

public class UserSession {
    private static Staff currentUser;
    private static RoleType currentUserRole;

    public static void setCurrentUser(Staff user) {
        currentUser = user;
        currentUserRole = user.getRole();
    }

    public static Long getCurrentUserId() {
        return (currentUser != null) ? currentUser.getStaffId() : null;
    }

    public static String getCurrentUserName() {
        return (currentUser != null) ? currentUser.getName() : null;
    }

    public static RoleType getCurrentUserRole() {
        return currentUserRole;
    }

    public static Staff getCurrentUser() {
        return currentUser;
    }

    public static void clearSession() {
        currentUser = null;
        currentUserRole = null;
    }
}
