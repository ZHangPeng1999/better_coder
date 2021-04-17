package com.zhangpeng.better_coder.entity;

import com.zhangpeng.better_coder.repository.QuestionRepository;
import com.zhangpeng.better_coder.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Slf4j
@Transactional
@Rollback(value = false)
public class EntityTest {
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private QuestionService questionService;
    @Test
    void testExamRepository(){
        questionRepository.getRandQuestions(1,0, PageRequest.of(0,1)).getContent()
                .forEach(question -> System.out.println(question.getContent()));
    }

}
