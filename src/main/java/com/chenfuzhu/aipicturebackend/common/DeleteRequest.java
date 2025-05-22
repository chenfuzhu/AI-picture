package com.chenfuzhu.aipicturebackend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用删除请求类
 */
@Data
public class DeleteRequest implements Serializable {

    private long id;

    private static final long seriaVersionUID = 1L;
}
