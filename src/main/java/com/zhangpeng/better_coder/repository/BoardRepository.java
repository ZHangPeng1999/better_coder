package com.zhangpeng.better_coder.repository;

import com.zhangpeng.better_coder.entity.Board;
import com.zhangpeng.better_coder.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository  extends BaseRepository<Board,Integer> {
    @Query("select b from Board b where b.type =:type order by b.id desc")
    Page<Board> findByIdDesc(@Param("type")Integer type, Pageable pageable);
}
