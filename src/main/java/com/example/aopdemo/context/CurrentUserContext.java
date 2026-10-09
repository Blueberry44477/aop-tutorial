package com.example.aopdemo.context;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class CurrentUserContext {

    private static final ThreadLocal<String> CURRENT_ROLE = new ThreadLocal<>();

    public static void setRole(String role) {
        CURRENT_ROLE.set(role);
    }

    public static String getRole() {
        return CURRENT_ROLE.get();
    }

    public static void clear() {
        CURRENT_ROLE.remove();
    }
}
