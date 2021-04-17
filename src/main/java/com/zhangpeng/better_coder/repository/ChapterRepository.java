package com.zhangpeng.better_coder.repository;

import com.zhangpeng.better_coder.entity.Chapter;
import com.zhangpeng.better_coder.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface ChapterRepository  extends BaseRepository<Chapter,Integer> {
}
