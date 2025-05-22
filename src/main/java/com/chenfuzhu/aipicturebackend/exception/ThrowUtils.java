package com.chenfuzhu.aipicturebackend.exception;

import lombok.Getter;

/**
 *  异常处理工具类(用于抛出异常)
 */

@Getter
public class ThrowUtils {

    /**
     *
     * @param condition 条件
     * @param e         异常
     */
    public static void throwIf(boolean condition,RuntimeException e) {
        if (condition) {
            throw e;
        }
    }

    /**
     *
     * @param condition 条件
     * @param errorCode 错误码
     */
    public static void throwIf(boolean condition,ErrorCode errorCode) {
        throwIf(condition,new BusinessException(errorCode));
    }

    /**
     *
     * @param condition 条件
     * @param errorCode 错误码
     * @param Message   错误信息
     */
    public static void throwIf(boolean condition,ErrorCode errorCode,String Message) {
        throwIf(condition,new BusinessException(errorCode,Message));
    }

}
