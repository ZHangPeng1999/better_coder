package com.zhangpeng.better_coder.repository;

import com.zhangpeng.better_coder.entity.Board;
import com.zhangpeng.better_coder.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository  extends BaseRepository<Board,Integer> {
}
