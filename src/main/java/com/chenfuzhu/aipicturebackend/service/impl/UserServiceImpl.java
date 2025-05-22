package com.chenfuzhu.aipicturebackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chenfuzhu.aipicturebackend.exception.BusinessException;
import com.chenfuzhu.aipicturebackend.exception.ErrorCode;
import com.chenfuzhu.aipicturebackend.exception.ThrowUtils;
import com.chenfuzhu.aipicturebackend.model.dto.user.UserQueryRequest;
import com.chenfuzhu.aipicturebackend.model.entity.User;
import com.chenfuzhu.aipicturebackend.model.enums.UserRoleEnum;
import com.chenfuzhu.aipicturebackend.model.vo.LoginUserVO;
import com.chenfuzhu.aipicturebackend.model.vo.UserVO;
import com.chenfuzhu.aipicturebackend.service.UserService;
import com.chenfuzhu.aipicturebackend.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.chenfuzhu.aipicturebackend.constant.UserConstant.USER_LOGIN_STATE;
import static com.chenfuzhu.aipicturebackend.exception.ErrorCode.NOT_LOGIN_ERROR;
import static com.chenfuzhu.aipicturebackend.exception.ErrorCode.OPERATION_ERROR;

/**
* @author chenfuzhu
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2025-04-15 17:29:01
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    /**
     * 用户注册
     *
     * @param userAccount   用户账号
     * @param userPassword  用户密码
     * @param checkPassword 检查密码
     * @return 新用户
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        //1.校验基本参数

        /*校验是否为空*/
        if(StrUtil.hasBlank(userAccount,userPassword,checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"Params is null or empty");
        }

        /*校验长度是否合规*/
        if(userAccount.length()<3){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"The account must not be less than 4 digits.");
        }
        if(userAccount.length()>16){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"The account must not be more than 16 digits.");
        }
        if(userPassword.length()<6){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"The password must not be less than 6 digits.");
        }
        if(userPassword.length()>18){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"The account must not be more than 18 digits.");
        }

        /*校验输入密码是否一致*/
        if(!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"The passwords entered twice are inconsistent.");
        }

        //2.检查用户名是否重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        long count=this.baseMapper.selectCount(queryWrapper);
        if(count>0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"The account already exists.");
        }
        //3.加密密码
        String encryptPassword=getEncryptPassword(userPassword);
        //4.插入数据到数据库
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName("<Null>");
        user.setUserRole(UserRoleEnum.USER.getValue());
        try{
            boolean saveResult = this.save(user);
            if(!saveResult){
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"\n" + "Database error: User information was not successfully saved.");
            }
        }catch (Exception e){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,e.getMessage());
        }
        return  user.getId();
    }

    /**
     * 用户登录
     * @param userAccount   用户账号
     * @param userPassword  用户密码
     * @param request       session请求
     * @return  userVO
     */
    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1.校验基本参数

        /*校验是否为空*/
        if(StrUtil.hasBlank(userAccount)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"Params is null or empty");
        }

        /*校验长度是否合规*/
        if(userAccount.length()<3||userAccount.length()>10){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"The user account is incorrect.");
        }
        if(userPassword.length()<6||userPassword.length()>18){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"The user password is incorrect.");
        }

        //2.对用户传递密码加密
        /* 密文对密文，因为数据库保存的密码是加密后的，所以登录验证的密码也需要进行相同的加密后进行对比 */
        String encryptPassword = getEncryptPassword(userPassword);

        //3.查询数据库中用户是否存在且密码正确
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        queryWrapper.eq("userPassword",encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        if(user==null){
            log.info("user is null");
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"\n" + "The user does not exist or the password is incorrect.");
        }

        //4.保存用户状态
        request.getSession().setAttribute(USER_LOGIN_STATE,user);

        return this.transformLoginUserVO(user);
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        User currentUser = (User)request.getSession().getAttribute(USER_LOGIN_STATE);
        ThrowUtils.throwIf(currentUser==null || currentUser.getId()==null,NOT_LOGIN_ERROR);

        long userId = currentUser.getId();
        currentUser= this.getById(userId);
        ThrowUtils.throwIf(currentUser==null || currentUser.getId()==null,NOT_LOGIN_ERROR);

        return currentUser;
    }

    @Override
    public LoginUserVO transformLoginUserVO(User user) {
        if(user==null){
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        /*使用hutool工具，将user类中的属性全部赋值给userVO，如果userVO中没有，则自动不赋值*/
        BeanUtil.copyProperties(user,loginUserVO);
        return loginUserVO;
    }

    @Override
    public UserVO transformUserVO(User user) {
        if(user==null){
            return null;
        }
        UserVO userVO = new UserVO();
        /*使用hutool工具，将user类中的属性全部赋值给userVO，如果userVO中没有，则自动不赋值*/
        BeanUtil.copyProperties(user,userVO);
        return userVO;
    }

    @Override
    public List<UserVO> transformUserVOList(List<User> userList) {
        if(CollUtil.isEmpty(userList)){
            return new ArrayList<>();
        }
        return userList.stream().map(this::transformUserVO).collect(Collectors.toList());
    }

    @Override
    public Boolean userLogout(HttpServletRequest request) {
        //校验账号是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        ThrowUtils.throwIf(!(userObj instanceof User),OPERATION_ERROR,"Not log in");
        //移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Params is null or empty");
        }
        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.eq(StrUtil.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StrUtil.isNotBlank(userAccount), "userAccount", userAccount);
        queryWrapper.like(StrUtil.isNotBlank(userName), "userName", userName);
        queryWrapper.like(StrUtil.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }


    /**
     * 加密密码
     * @param userPassword 用户密码
     * @return 二进制密码
     */
    public String getEncryptPassword(String userPassword) {
        // 加盐，混淆密码
        final String salt="chenfuzhu";
        return DigestUtils.md5DigestAsHex((salt + userPassword).getBytes());
    }
}




