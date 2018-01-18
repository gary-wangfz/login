/*
 * Copyright 2012-2016 the original author or authors.
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

package com.xsili.business.user.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import com.xsili.business.user.enums.SystemUserType;
import com.xsili.business.user.model.LoginInfo;

public interface LoginInfoRepository extends JpaRepository<LoginInfo, Integer>, QuerydslPredicateExecutor<LoginInfo> {

    LoginInfo findByLoginPhoneAndUserType(String loginPhone, SystemUserType userType);

    LoginInfo findByUserIdAndUserType(Integer userId, SystemUserType userType);

    @Modifying
    @Query("update LoginInfo l set l.lastLoginTime = ?2 where l.id = ?1")
    int updateLoginTime(Integer id, Date lastLoginTime);

    @Modifying
    @Query("update LoginInfo l set l.password = ?3 where l.userId = ?1 and l.userType = ?2")
    void updatePwd(Integer userId, SystemUserType userType, String password);

}
