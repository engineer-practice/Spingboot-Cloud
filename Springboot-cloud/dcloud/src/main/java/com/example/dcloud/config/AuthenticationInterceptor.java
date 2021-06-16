package com.example.dcloud.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.dcloud.annotation.NoToken;
import com.example.dcloud.entity.User;
import com.example.dcloud.service.UserService;
import com.example.dcloud.util.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@CrossOrigin
public class AuthenticationInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                             Object object) throws Exception {

        // 如果不是映射到方法直接通过
        if (!(object instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) object;
        Method method = handlerMethod.getMethod();
        // 检查是否有passtoken注释，有则跳过认证
        if (method.isAnnotationPresent(NoToken.class)) {
            NoToken passToken = method.getAnnotation(NoToken.class);
            if (passToken.required()) {
                return true;
            }
        } else {
            String token = httpServletRequest.getHeader("token");
            // Token不存在
            if (token == null) {
                System.out.println("not token");
                throw new NeedLoginException(ResultCode.USER_NOT_LOGGED_IN);
            }
            // 获取用户ID，如果出错抛异常
            Integer userId;
            try {
                userId = AuthUtils.getUserId(httpServletRequest);
            } catch (JWTDecodeException j) {
                System.out.println("id error");
                throw new NeedLoginException(ResultCode.USER_NOT_LOGGED_IN);
            }
            User user = userService.getById(userId);
            if (user == null) {
                System.out.println("not user");
                throw new NeedLoginException(ResultCode.USER_NOT_LOGGED_IN);
            }
            // 验证Token是否有效
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getPassword() + "")).build();
            try {
                jwtVerifier.verify(token);
            } catch (JWTVerificationException e) {
                System.out.println("token error");
                throw new NeedLoginException(ResultCode.USER_NOT_LOGGED_IN);
            }
            return true;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o,
                           ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                Object o, Exception e) throws Exception {
    }
}
