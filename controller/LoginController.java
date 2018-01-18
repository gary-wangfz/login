package com.xsili.web.controller.auth;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xsili.business.user.enums.SystemUserType;
import com.xsili.business.user.model.AdminUser;
import com.xsili.business.user.model.CommonUser;
import com.xsili.business.user.model.User;
import com.xsili.business.user.service.LoginInfoService;
import com.xsili.business.user.service.UserService;
import com.xsili.context.ConfigProperties;
import com.xsili.context.ConstantI18NKey;
import com.xsili.context.Constants;
import com.xsili.context.annotation.OperationLogger;
import com.xsili.context.annotation.OperationLogger.ModuleEnum;
import com.xsili.context.annotation.OperationLogger.SystemEnum;
import com.xsili.context.exception.BusinessException;
import com.xsili.context.util.CommonUtil;
import com.xsili.integration.shiro.XsiliAuthenticationToken;
import com.xsili.web.controller.base.AbstractController;
import com.xsili.web.responsemodel.Result;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/base/login")
@Api("登录管理")
public class LoginController extends AbstractController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @Resource
    private ConfigProperties configProperties;

    @Resource
    private LoginInfoService loginInfoService;

    @Resource
    private UserService userService;

    @RequestMapping(value = "/portal/verify_login", method = RequestMethod.POST)
    @ApiOperation(value = "判断是否已登录", notes = "errorCode==2表示未登录, data值是loginUrl", response = Result.class)
    public Result verifyLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CommonUser commonuser = getLoginCommonUser();

        User user = null;
        try {
            user = (User) commonuser;
        } catch (ClassCastException e) {// 如果登陆了adminUser, 那么就会出现该异常
            // ignore
        }

        if (user == null) {
            if (CommonUtil.isWechatRequest(request)) {// 微信
                LOGGER.debug("user-agent contains MicroMessenger");
                LOGGER.debug("sendRedirect(configInfo.getWxOauth2Url()): {}", configProperties.getWx().getUrl().getOauth2Url());
                response.setHeader(Constants.RESPONSE_HEADER_SESSION_STATUS, Constants.RESPONSE_HEADER_SESSION_STATUS_TIMEOUT);
                return Result.sessionTimeout(configProperties.getWx().getUrl().getOauth2Url());
            } else {
                response.setHeader(Constants.RESPONSE_HEADER_SESSION_STATUS, Constants.RESPONSE_HEADER_SESSION_STATUS_TIMEOUT);
                return Result.sessionTimeout();
            }
        } else {
            return super.success(user);
        }
    }

    @RequestMapping(value = "/admin/verify_login", method = RequestMethod.POST)
    @ApiOperation(value = "判断是否已登录", notes = "errorCode==2表示未登录, data值是loginUrl", response = Result.class)
    public Result verifyLoginAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CommonUser commonuser = getLoginCommonUser();

        AdminUser user = null;
        try {
            user = (AdminUser) commonuser;
        } catch (ClassCastException e) {// 如果登陆了user, 那么就会出现该异常
            // ignore
        }

        if (user == null) {
            response.setHeader(Constants.RESPONSE_HEADER_SESSION_STATUS, Constants.RESPONSE_HEADER_SESSION_STATUS_TIMEOUT);
            return Result.sessionTimeout();
        } else {
            return super.success(user);
        }

    }

    @OperationLogger(system = SystemEnum.PORTAL, module = ModuleEnum.LOGIN, operation = "login", filterParameters = Constants.PARAM_KEY_PASSWORD)
    @RequestMapping(value = "/portal/login", method = RequestMethod.POST)
    @ApiOperation(value = "门户登录", notes = "登录失败 data.isDisplayVerifyCode == true; 登录超时, header Session-Timeout==true 且 result.errorCode==2;", response = Result.class)
    public Result portalLogin(@RequestParam String loginName,
                              @RequestParam(Constants.PARAM_KEY_PASSWORD) String password,
                              @RequestParam(required = false) String verificationCode) {
        return login(loginName, password, SystemUserType.PORTAL, verificationCode);
    }

    @OperationLogger(system = SystemEnum.ADMIN, module = ModuleEnum.LOGIN, operation = "login", filterParameters = Constants.PARAM_KEY_PASSWORD)
    @RequestMapping(value = "/admin/login", method = RequestMethod.POST)
    @ApiOperation(value = "管理后台登录", notes = "参考/portal/login的说明", response = Result.class)
    public Result adminLogin(@RequestParam String loginName,
                             @RequestParam String password,
                             @RequestParam(required = false) String verificationCode) {
        return login(loginName, password, SystemUserType.ADMIN, verificationCode);
    }

    private Result login(String loginName, String password, SystemUserType systemUserType, String verificationCode) {
        if (StringUtils.isBlank(loginName)) {
            throw new BusinessException(ConstantI18NKey.AUTH_ACCOUNT_IS_NULL);
        }
        if (StringUtils.isBlank(password)) {
            throw new BusinessException(ConstantI18NKey.AUTH_PASSWORD_IS_NULL);
        }

        Subject subject = SecurityUtils.getSubject();
        XsiliAuthenticationToken token = new XsiliAuthenticationToken(loginName, password, systemUserType);
        subject.login(token);

        Result result = super.success(subject.getSession().getId());
        return result;
    }

    @RequestMapping(value = "/portal/logout", method = RequestMethod.POST)
    public Result portalLogout() throws IOException {
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.logout();
        } catch (Exception e) {
            LOGGER.debug("Encountered session exception during logout.  This can generally safely be ignored.", e);
        }
        return super.success();
    }

    @RequestMapping(value = "/admin/logout", method = RequestMethod.POST)
    public Result adminLogout() throws IOException {
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.logout();
        } catch (Exception e) {
            LOGGER.debug("Encountered session exception during logout.  This can generally safely be ignored.", e);
        }
        return super.success();
    }

}
