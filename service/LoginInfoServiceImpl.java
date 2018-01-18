/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xsili.business.user.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.xsili.business.user.enums.SystemUserType;
import com.xsili.business.user.model.LoginInfo;
import com.xsili.business.user.repository.LoginInfoRepository;
import com.xsili.business.user.service.LoginInfoService;
import com.xsili.context.exception.BusinessException;

@Component
@Transactional
class LoginInfoServiceImpl implements LoginInfoService {

    @Resource
    private LoginInfoRepository loginInfoRepository;

    @Override
    public LoginInfo find(Integer userId, SystemUserType userType) {
        LoginInfo loginInfo = loginInfoRepository.findByUserIdAndUserType(userId, userType);
        return loginInfo;
    }

    @Override
    public Page<LoginInfo> findAll(Pageable pageable) {
        Page<LoginInfo> page = loginInfoRepository.findAll(pageable);
        return page;
    }

    @Override
    public LoginInfo getPasswordByLoginName(String loginName, SystemUserType userType) {
        return loginInfoRepository.findByLoginPhoneAndUserType(loginName, userType);
    }

    @Override
    public void loginSuccess(Integer userId, SystemUserType userType) {
        if (userId == null) {
            throw new BusinessException("参数userId不能为空");
        }
        if (userType == null) {
            throw new BusinessException("参数userType不能为空");
        }
        LoginInfo loginInfo = find(userId, userType);
        if (loginInfo == null) {
            throw new BusinessException("用户登陆信息不存在");
        }

        loginInfo.setLastLoginTime(new Date());
        if (this.loginInfoRepository.updateLoginTime(loginInfo.getId(), loginInfo.getLastLoginTime()) != 1) {

        }
    }

    @Override
    public LoginInfo create(LoginInfo loginInfo) {
        loginInfo.setCreatedTime(new Date());
        loginInfo.setUpdatedTime(new Date());
        return loginInfoRepository.save(loginInfo);
    }

    @Override
    public LoginInfo update(LoginInfo loginInfo) {
        loginInfo.setUpdatedTime(new Date());
        return loginInfoRepository.saveAndFlush(loginInfo);
    }

    @Override
    public void updatePwd(Integer userId, SystemUserType userType, String password) {
        loginInfoRepository.updatePwd(userId, userType, password);
    }

    @Override
    public void deleteByUser(Integer userId, SystemUserType userType) {
        LoginInfo loginInfo = loginInfoRepository.findByUserIdAndUserType(userId, userType);
        loginInfoRepository.delete(loginInfo);
    }

}
