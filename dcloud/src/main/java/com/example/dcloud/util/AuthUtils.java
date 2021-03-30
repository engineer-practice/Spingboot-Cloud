package com.example.dcloud.util;

import com.auth0.jwt.JWT;

import javax.servlet.http.HttpServletRequest;

public class AuthUtils {
    public static Integer getUserId(HttpServletRequest req) {
        String token = req.getHeader("token");
        String userId = JWT.decode(token).getAudience().get(0);
        return Integer.parseInt(userId);
    }
}
