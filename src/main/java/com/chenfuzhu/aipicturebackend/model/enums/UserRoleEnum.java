package com.chenfuzhu.aipicturebackend.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户角色枚举
 */
@Getter
public enum UserRoleEnum {

    USER("用户", "user"),
    ADMIN("管理员", "admin");

    private final String text;
    private final String value;

   UserRoleEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 将字符串类型的role改为枚举类的role
     * @param value 用户权限
     * @return userRoleEnum
     */
    public static UserRoleEnum getUserRoleEnum(String value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (UserRoleEnum userRoleEnum : UserRoleEnum.values()) {
            if (userRoleEnum.value.equals(value)) {
                return userRoleEnum;
            }
        }
        return null;
    }
}
