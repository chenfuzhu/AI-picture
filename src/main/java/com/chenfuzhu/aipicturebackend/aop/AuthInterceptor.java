package com.chenfuzhu.aipicturebackend.aop;

import com.chenfuzhu.aipicturebackend.annotation.AuthCheck;
import com.chenfuzhu.aipicturebackend.exception.BusinessException;
import com.chenfuzhu.aipicturebackend.exception.ErrorCode;
import com.chenfuzhu.aipicturebackend.exception.ThrowUtils;
import com.chenfuzhu.aipicturebackend.model.entity.User;
import com.chenfuzhu.aipicturebackend.model.enums.UserRoleEnum;
import com.chenfuzhu.aipicturebackend.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static com.chenfuzhu.aipicturebackend.model.enums.UserRoleEnum.ADMIN;

@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private UserService userService;

    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        //获取当前模块所需要的权限
        String mustRole = authCheck.mustRole();
        UserRoleEnum mustRoleEnum = UserRoleEnum.getUserRoleEnum(mustRole);

        //获取当前登录用户的权限
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        User loginUser = userService.getLoginUser(request);
        String currentRole = loginUser.getUserRole();
        UserRoleEnum userRoleEnum = UserRoleEnum.getUserRoleEnum(currentRole);


        //无需权限，直接放行
        if(mustRoleEnum == null){
            joinPoint.proceed();
        }

        //审查基本权限
        ThrowUtils.throwIf(userRoleEnum==null, ErrorCode.NO_AUTH_ERROR);

        //要求用户必须有管理员权限，否则拒绝
        ThrowUtils.throwIf(mustRoleEnum==ADMIN && userRoleEnum!=ADMIN, ErrorCode.NO_AUTH_ERROR);

        //审查通过，放行
        return joinPoint.proceed();
    }
}

