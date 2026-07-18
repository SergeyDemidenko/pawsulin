package com.pawsulin.util;

import com.pawsulin.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class SecurityUtil {

    private SecurityUtil() {
    }

    public static Long getCurrentUserId() {
        UserPrincipal userPrincipal = getCurrentUserDetails();
        return userPrincipal.getUserId();
    }

    public static UserPrincipal getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found in security context");
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserPrincipal)) {
            throw new IllegalStateException("Unexpected principal type: " + principal.getClass().getName());
        }
        return (UserPrincipal) principal;
    }
}
