package com.zhangpeng.better_coder.service;

import com.zhangpeng.better_coder.component.BaiDuAIComponent;
import com.zhangpeng.better_coder.entity.ChooseCourse;
import com.zhangpeng.better_coder.entity.ChooseQuestion;
import com.zhangpeng.better_coder.entity.Question;
import com.zhangpeng.better_coder.entity.User;
import com.zhangpeng.better_coder.repository.ChooseQuestionRepository;
import com.zhangpeng.better_coder.repository.QuestionRepository;
import com.zhangpeng.better_coder.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private BaiDuAIComponent baiDuAIComponent;

    public String getVoiceText(Question question){
        String ans = "请听题，该题的标题是";
        ans += question.getTitle();
        ans += "该题的描述为";
        ans += question.getAnswer();
        ans += "，请在60s内，回答该问题。";
        return ans;
    }
    /**
     * 添加选择
     * @param title
     * @param content 选项用, 隔开 用的时候split一下
     * @param bizType
     * @param level
     * @param answer
     * @return
     */
    public Question addSelectQuestion(String title, String content, String bizType, Integer level, Integer status,String answer, String a, String b, String c, String d) {
        Question question = new Question();
        question.setTitle(title);
        question.setAnswer(answer);
        question.setBizType(bizType);
        question.setLevel(level);
        question.setContent(content);
        question.setType(1); // 选择
        question.setASelect(a);
        question.setBSelect(b);
        question.setCSelect(c);
        question.setDSelect(d);
        question.setStatus(status);
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
    public Question addJudgeQuestion(String title, String content, String bizType, Integer level,  Integer status, String answer, String a, String b) {
        Question question = new Question();
        question.setTitle(title);
        question.setAnswer(answer);
        question.setBizType(bizType);
        question.setLevel(level);
        question.setContent(content);
        question.setType(2); // 判断
        question.setASelect(a);
        question.setBSelect(b);
        question.setStatus(status);

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
    public Question addAnalysisQuestion(String title, String content, String bizType, Integer level, Integer status, String answer) {
        Question question = new Question();
        question.setTitle(title);
        question.setAnswer(answer);
        question.setBizType(bizType);
        question.setLevel(level);
        question.setContent(content);
        question.setType(3); // 简答
        question.setStatus(status);
        String voiceText = getVoiceText(question);
        question.setVoice(new String(baiDuAIComponent.TextToVoice(voiceText)));
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
    public Map searchQuestion(String title, String content, String bizType, Integer level,Integer status, Integer type, Integer offset, Integer limit) {
        if (limit == 0) {
            limit = 20;
        }
        Pageable pageable = PageRequest.of(offset, limit);
        Question question = new Question();
        if (title.length() !=0) {
            question.setTitle(title);
        }
        if (content.length()!=0) {
            question.setContent(content);
        }
        if (bizType.length() != 0 ){
            question.setBizType(bizType);
        }
        if (level!=0) {
            question.setLevel(level);
        }
        if (status!=0) {
            question.setStatus(status);
        }
        if (type != 0 ){
            question.setType(type);
        }
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("title", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("content", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("biz_type", ExampleMatcher.GenericPropertyMatchers.contains());

        Example<Question> ex = Example.of(question, matcher);
        Page<Question> result = questionRepository.findAll(ex, pageable);
        return Map.of("questions",result.getContent(),"total",result.getTotalElements());
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

        if (question.getType() == 2 || question.getType() == 1) {
            chooseQuestion.setResult("错误");
            if (chooseQuestion.getAnswer().equals(question.getAnswer())) {
                chooseQuestion.setResult("正确");
            }
            chooseQuestionRepository.save(chooseQuestion);
        } else {
            chooseQuestion.setResult("简答题不做判断");
            chooseQuestionRepository.save(chooseQuestion);
        }
        return chooseQuestion.getResult();
    }
    public Question updateQuestion(Integer id, String title, String content, String bizType, Integer level, Integer status,String answer,
                                   String a, String b, String c, String d){
        Question question = getQuestion(id);
        question.setTitle(title);
        question.setAnswer(answer);
        question.setBizType(bizType);
        question.setLevel(level);
        question.setContent(content);
        question.setASelect(a);
        question.setBSelect(b);
        question.setCSelect(c);
        question.setDSelect(d);
        question.setStatus(status);
        if (question.getType() == 3) {
            String voiceText = getVoiceText(question);
            question.setVoice(new String(baiDuAIComponent.TextToVoice(voiceText)));
        }
        questionRepository.save(question);
        return question;
    }
    // 编程题 明天专门写
    public ChooseQuestion getChooseQuestion(Integer id) {
        return chooseQuestionRepository.getOne(id);
    }
}
