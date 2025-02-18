package com.dentalclinic.entities;

public class UserSession {
    private static RoleType currentUserRole;

    public static RoleType getCurrentUserRole() {
        return currentUserRole;
    }

    public static void setCurrentUserRole(RoleType currentUserRole) {
        UserSession.currentUserRole = currentUserRole;
    }
}
