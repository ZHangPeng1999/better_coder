package com.zhangpeng.better_coder.repository;

import com.zhangpeng.better_coder.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface UserRepository extends BaseRepository<User,Integer> {
    @Query("from User u where u.number=:num")
    User findByNum(@Param("num")Integer num);

}
