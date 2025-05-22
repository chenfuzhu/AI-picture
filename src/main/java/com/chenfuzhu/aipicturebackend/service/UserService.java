package com.chenfuzhu.aipicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chenfuzhu.aipicturebackend.model.dto.user.UserQueryRequest;
import com.chenfuzhu.aipicturebackend.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chenfuzhu.aipicturebackend.model.vo.LoginUserVO;
import com.chenfuzhu.aipicturebackend.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author chenfuzhu
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-04-15 17:29:01
*/
public interface UserService extends IService<User> {
    /**
     * 用户注册
     * @param userAccount   用户账号
     * @param userPassword  用户密码
     * @param checkPassword 检查密码
     * @return 用户id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     * @param userAccount   用户账号
     * @param userPassword  用户密码
     * @param request       session请求
     * @return 用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取当前登录用户
     * @param request   http请求
     * @return  user
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 将User转化为UserVO(数据脱敏)-->登录信息
     * @param user  用户
     * @return userVO
     */
    LoginUserVO transformLoginUserVO(User user);

    /**
     * 将User转化为UserVO(数据脱敏)-->登录后的信息
     * @param user  用户
     * @return userVO
     */
    UserVO transformUserVO(User user);

    /**
     * 将User转化为UserVO(数据脱敏)-->登录后的信息 列表
     * @param
     * @return userVO
     */
    List<UserVO> transformUserVOList(List<User> userList);

    /**
     * 用户登录态注销（用户登出）
     * @param request session清空
     * @return  是否退出
     */
    Boolean userLogout(HttpServletRequest request);

    /**
     * 查询
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * md5加盐加密
     * @param userPassword
     * @return
     */
    public String getEncryptPassword(String userPassword);

}
