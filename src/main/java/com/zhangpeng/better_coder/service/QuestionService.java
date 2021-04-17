package com.zhangpeng.better_coder.service;

import com.zhangpeng.better_coder.entity.ChooseQuestion;
import com.zhangpeng.better_coder.entity.Question;
import com.zhangpeng.better_coder.entity.User;
import com.zhangpeng.better_coder.repository.ChooseQuestionRepository;
import com.zhangpeng.better_coder.repository.QuestionRepository;
import com.zhangpeng.better_coder.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

@Service
@Transactional
@Slf4j
public class QuestionService {
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private ChooseQuestionRepository chooseQuestionRepository;
    @Autowired
    private UserRepository userRepository;

    /**
     * 添加选择
     * @param title
     * @param content 选项用, 隔开 用的时候split一下
     * @param bizType
     * @param level
     * @param answer
     * @return
     */
    public Question addSelectQuestion(String title, String content, String bizType, Integer level, String answer) {
        Question question = new Question();
        question.setTitle(title);
        question.setAnswer(answer);
        question.setBizType(bizType);
        question.setLevel(level);
        question.setContent(content);
        question.setType(0); // 选择
        questionRepository.save(question);
        questionRepository.refresh(question);
        return question;
    }

    /**
     * 添加判断题
     * @param title
     * @param content
     * @param bizType
     * @param level
     * @param answer
     * @return
     */
    public Question addJudgeQuestion(String title, String content, String bizType, Integer level, String answer) {
        Question question = new Question();
        question.setTitle(title);
        question.setAnswer(answer);
        question.setBizType(bizType);
        question.setLevel(level);
        question.setContent(content);
        question.setType(1); // 判断
        questionRepository.save(question);
        questionRepository.refresh(question);
        return question;
    }

    /**
     * 添加简答题
     * @param title
     * @param content
     * @param bizType
     * @param level
     * @param answer
     * @return
     */
    public Question addAnalysisQuestion(String title, String content, String bizType, Integer level, String answer) {
        Question question = new Question();
        question.setTitle(title);
        question.setAnswer(answer);
        question.setBizType(bizType);
        question.setLevel(level);
        question.setContent(content);
        question.setType(3); // 简答
        questionRepository.save(question);
        questionRepository.refresh(question);
        return question;
    }

    /**
     * 搜索题目 包括题目 标题 题目课程类型 题目难度 参数
     * @param title
     * @param content
     * @param bizType
     * @param level
     * @param type
     * @param offset
     * @param limit
     * @return
     */
    public List<Question> searchQuestion(String title, String content, String bizType, Integer level, Integer type, Integer offset, Integer limit) {
        if (limit == 0) {
            limit = 20;
        }
        Pageable pageable = PageRequest.of(offset, limit);
        Question question = new Question();
        question.setTitle(title);
        question.setContent(content);
        question.setBizType(bizType);
        question.setLevel(level);
        question.setType(type);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("title", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("content", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("biz_type", ExampleMatcher.GenericPropertyMatchers.contains());

        Example<Question> ex = Example.of(question, matcher);
        return questionRepository.findAll(ex, pageable).getContent();
    }
    public Question getQuestion(Integer id) {
        return questionRepository.getOne(id);
    }

    /**
     * 用户回答单个选择/判断/简答题目
     * @param answer
     * @param questionId
     * @param userId
     * @return
     */
    public String judgeQuestion(String answer, Integer questionId, Integer userId) {
        Question question = getQuestion(questionId);
        User user = userRepository.getOne(userId);
        ChooseQuestion chooseQuestion = new ChooseQuestion();
        chooseQuestion.setQuestion(question);
        chooseQuestion.setUser(user);
        chooseQuestion.setAnswer(answer);

        if (question.getType() == 0 || question.getType() == 1) {
            chooseQuestion.setResult("错误");
            if (chooseQuestion.getAnswer() == question.getAnswer()) {
                chooseQuestion.setResult("正确");
            }
            chooseQuestionRepository.save(chooseQuestion);
        } else {
            chooseQuestion.setResult("简答题不做判断");
            chooseQuestionRepository.save(chooseQuestion);
        }
        return chooseQuestion.getResult();
    }
    private Question updateQuestion(Integer id, String title, String content, String bizType, Integer level, String answer){
        Question question = getQuestion(id);
        question.setTitle(title);
        question.setAnswer(answer);
        question.setBizType(bizType);
        question.setLevel(level);
        question.setContent(content);
        questionRepository.save(question);
        questionRepository.refresh(question);
        return question;
    }
    // 编程题 明天专门写

}
