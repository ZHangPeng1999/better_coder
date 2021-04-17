package com.zhangpeng.better_coder.repository;

import com.zhangpeng.better_coder.entity.Course;
import com.zhangpeng.better_coder.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository  extends BaseRepository<Course,Integer> {
}
