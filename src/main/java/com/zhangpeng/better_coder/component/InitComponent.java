package com.zhangpeng.better_coder.component;

import com.zhangpeng.better_coder.entity.User;
import com.zhangpeng.better_coder.service.UserService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

//实现初始化接口
@Component
public class InitComponent implements InitializingBean {
    @Autowired
    private UserService userService;
    @Override
    public void afterPropertiesSet() throws Exception {
        int num=1001;
        if(userService.getUserByNum(num)==null){
            userService.addUser("root", "root", User.Role.ADMIN, num);
        }
    }
}
