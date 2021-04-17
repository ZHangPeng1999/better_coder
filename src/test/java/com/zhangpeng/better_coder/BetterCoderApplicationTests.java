package com.zhangpeng.better_coder;

import com.zhangpeng.better_coder.entity.Question;
import com.zhangpeng.better_coder.repository.ExamRepository;
import com.zhangpeng.better_coder.repository.QuestionRepository;
import com.zhangpeng.better_coder.service.QuestionService;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Slf4j
@Transactional
@Rollback(value = false)
class BetterCoderApplicationTests {
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private QuestionService questionService;
    @Test
    void contextLoads() {
    }
    @Test
    void testExamRepository(){
       questionService.addSelectQuestion("123","2313","123",1,"12312");
    }

}
