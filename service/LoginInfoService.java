
package com.xsili.business.user.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.xsili.business.user.enums.SystemUserType;
import com.xsili.business.user.model.LoginInfo;

public interface LoginInfoService {

    Page<LoginInfo> findAll(Pageable pageable);

    LoginInfo find(Integer userId, SystemUserType userType);

    LoginInfo getPasswordByLoginName(String loginName, SystemUserType userType);

    void loginSuccess(Integer userId, SystemUserType userType);

    LoginInfo create(LoginInfo loginInfo);

    LoginInfo update(LoginInfo loginInfo);

    void updatePwd(Integer userId, SystemUserType userType, String password);

    void deleteByUser(Integer userId, SystemUserType userType);

}
