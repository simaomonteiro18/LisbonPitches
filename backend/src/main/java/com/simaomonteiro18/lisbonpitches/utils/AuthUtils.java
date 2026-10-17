package com.simaomonteiro18.lisbonpitches.utils;

import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtils {

    public static Long getAuthenticatedUserId() {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return (Long) principal;

    }

}