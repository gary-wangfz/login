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

import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.xsili.business.user.enums.UserStatus;
import com.xsili.business.user.model.AdminUser;
import com.xsili.business.user.model.QAdminUser;
import com.xsili.business.user.repository.AdminUserRepository;
import com.xsili.business.user.service.AdminUserService;
import com.xsili.web.responsemodel.SimplePage;

@Component
@Transactional
class AdminUserServiceImpl implements AdminUserService {

    @Resource
    private AdminUserRepository adminUserRepository;

    @Override
    public Page<AdminUser> findAll(Pageable pageable) {
        Page<AdminUser> page = adminUserRepository.findAll(pageable);
        return page;
    }

    @Override
    public AdminUser findById(Integer id) {
        return adminUserRepository.findById(id).orElse(null);
    }

    @Override
    public AdminUser create(AdminUser adminUser) {
        adminUser.setCreatedTime(new Date());
        adminUser.setUpdatedTime(new Date());
        return adminUserRepository.save(adminUser);
    }

    @Override
    public AdminUser update(AdminUser adminUser) {
        adminUser.setUpdatedTime(new Date());
        return adminUserRepository.saveAndFlush(adminUser);
    }

    @Override
    public boolean checkLoginPhone(String loginPhone) {
        return (adminUserRepository.checkLoginPhone(loginPhone) == 1);
    }

    @Override
    public void deleteById(Integer id) {
        adminUserRepository.deleteById(id);
    }

    @SuppressWarnings("deprecation")
    @Override
    public SimplePage<AdminUser> list(String loginPhone, String userName, UserStatus status, int page, int limit) {
        QAdminUser qAdminUser = QAdminUser.adminUser;
        Predicate predicate = null;
        if (StringUtils.isNotBlank(loginPhone)) {
            predicate = ExpressionUtils.and(predicate, qAdminUser.loginPhone.like("%" + loginPhone + "%"));
        }
        if (StringUtils.isNotBlank(userName)) {
            predicate = ExpressionUtils.and(predicate, qAdminUser.userName.like("%" + userName + "%"));
        }
        if (status != null) {
            predicate = ExpressionUtils.and(predicate, qAdminUser.status.eq(status));
        }
        Sort sort = new Sort(Sort.Direction.DESC, "createdTime");
        PageRequest pageRequest = PageRequest.of(page, limit, sort);

        Page<AdminUser> pager = adminUserRepository.findAll(predicate, pageRequest);
        return SimplePage.getPage(pager);
    }
}
