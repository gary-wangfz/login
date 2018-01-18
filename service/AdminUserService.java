package com.xsili.business.user.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.xsili.business.user.enums.UserStatus;
import com.xsili.business.user.model.AdminUser;
import com.xsili.web.responsemodel.SimplePage;

public interface AdminUserService {

    Page<AdminUser> findAll(Pageable pageable);

    AdminUser findById(Integer id);
    
    AdminUser create(AdminUser adminUser);
    
    AdminUser update(AdminUser adminUser);

    boolean checkLoginPhone(String loginPhone);

    void deleteById(Integer id);

    SimplePage<AdminUser> list(String loginPhone, String userName, UserStatus status, int page, int limit);
    
}
