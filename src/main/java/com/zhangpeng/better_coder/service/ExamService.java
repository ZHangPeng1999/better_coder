package com.zhangpeng.better_coder.service;

import com.sun.xml.bind.v2.schemagen.xmlschema.ComplexTypeHost;
import com.zhangpeng.better_coder.entity.ChooseQuestion;
import com.zhangpeng.better_coder.entity.Exam;
import com.zhangpeng.better_coder.entity.Question;
import com.zhangpeng.better_coder.entity.User;
import com.zhangpeng.better_coder.repository.ChooseQuestionRepository;
import com.zhangpeng.better_coder.repository.ExamRepository;
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
public class ExamService {
    @Autowired
    private ExamRepository examRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private ChooseQuestionRepository chooseQuestionRepository;
    @Autowired
    private UserRepository userRepository;
    public Exam getExam(Integer id) {
        return examRepository.getOne(id);
    }

    /**
     * 添加考试
     * @param uId
     * @param bizType
     * @param level
     * @return
     */
    public Exam addExam(Integer uId,String bizType,Integer level){
        List<Question> questions = List.of();
        questionRepository.getRandQuestions(level, bizType, 0, PageRequest.of(0,5))
                .getContent().forEach(question -> questions.add(question));
        questionRepository.getRandQuestions(level, bizType, 1, PageRequest.of(0,5))
                .getContent().forEach(question -> questions.add(question));
        User user = userRepository.getOne(uId);
        Exam exam = new Exam();
        exam.setUser(user);
        examRepository.save(exam);
        examRepository.refresh(exam);
        questions.forEach(question -> {
            ChooseQuestion chooseQuestion = new ChooseQuestion();
            chooseQuestion.setQuestion(question);
            chooseQuestion.setExam(exam);
            chooseQuestionRepository.save(chooseQuestion);
        });
        return null;
    }
    public List<Exam> searchExam(Integer offset, Integer limit) {
        if (limit == 0) {
            limit = 20;
        }
        Pageable pageable = PageRequest.of(offset, limit);
        return examRepository.findAll(pageable).getContent();
    }

    /**
     * 判题
     * @param eId
     * @param uId
     * @param chooseQuestions
     * @return
     */
    public Exam checkExam(Integer eId, Integer uId, List<ChooseQuestion> chooseQuestions) {
        Exam exam = examRepository.getOne(eId);
        User user = userRepository.getOne(uId);
        int sum=0;
        for (ChooseQuestion chooseQuestion : chooseQuestions) {
            Question question = chooseQuestion.getQuestion();
            if (question.getType() == 0 || question.getType() == 1) {
                chooseQuestion.setResult("错误");
                if (chooseQuestion.getAnswer() == question.getAnswer()) {
                    chooseQuestion.setResult("正确");
                    sum = sum+1;
                }
                chooseQuestionRepository.save(chooseQuestion);
            }
        }
        exam.setGrade(sum);
        examRepository.save(exam);
        return exam;
    }



}
