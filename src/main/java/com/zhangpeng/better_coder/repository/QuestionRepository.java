package com.zhangpeng.better_coder.repository;

import com.zhangpeng.better_coder.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository  extends BaseRepository<Question,Integer> {
    @Query(value = "select * from Question  where level in (:level) and biz_type like :bizType and type = :type order by Rand()", nativeQuery = true)
    Page<Question> getRandQuestions(@Param("level") List<Integer> level, @Param("bizType")String bizType, @Param("type")Integer type, Pageable pageable);


}
