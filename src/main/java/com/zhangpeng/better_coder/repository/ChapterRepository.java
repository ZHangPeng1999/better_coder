package com.zhangpeng.better_coder.repository;

import com.zhangpeng.better_coder.entity.Chapter;
import com.zhangpeng.better_coder.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChapterRepository  extends BaseRepository<Chapter,Integer> {
    @Query("select c from Chapter c where c.course.id = :cid")
    List<Chapter> listChaptesByCourseId (@Param("cid")Integer cid);
}
