package com.zhangpeng.better_coder.service;

import com.zhangpeng.better_coder.entity.User;
import com.zhangpeng.better_coder.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@Slf4j
public class UserService {
    @Autowired
    private UserRepository userRepository;

    /**
     * 列表页 搜索某姓名用户
     * @param name
     * @param offset
     * @param limit
     * @return
     */
    public Map searchUser(Integer num, String name, Integer offset, Integer limit) {
        if (limit == 0) {
            limit = 20;
        }
        Pageable pageable = PageRequest.of(offset, limit);
        User user = new User();
        if (num != 0) {
            user.setNumber(num);
        }
        if (name.length() != 0) {
            user.setName(name);
        }
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains());
        Example<User> ex = Example.of(user, matcher);
        Page<User> result = userRepository.findAll(ex, pageable);
        return Map.of("users",result.getContent(), "total", result.getTotalElements());
    }

    /**
     * 根据id获取用户
     * @param id
     * @return
     */
    public User getUser(Integer id) {
        return userRepository.findById(id).orElse(null);
    }
    public User getUserByNum(Integer num) {
        return userRepository.findByNum(num);
    }
    /**
     * 添加一个用户
     * @param name
     * @param password
     * @param role
     * @return
     */
    public User addUser (String name, String password, User.Role role, Integer number) {
        User user = new User();
        user.setName(name);
        user.setRole(role);
        user.setPassword(password);
        user.setNumber(number);
        userRepository.save(user);
        userRepository.refresh(user);
        return user;
    }

    /**
     * 更新用户信息
     * @param id
     * @param name
     * @param password
     * @param role
     * @return
     */
    public User updateUser(Integer id, String name, String password, User.Role role) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return null;
        }
        user.setName(name);
        user.setRole(role);
        user.setPassword(password);
        userRepository.save(user);
        return user;
    }

}
