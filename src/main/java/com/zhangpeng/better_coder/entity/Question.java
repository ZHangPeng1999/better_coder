package com.zhangpeng.better_coder.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@JsonIgnoreProperties({"chooseQuestions"})
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title; // 标题
    private String content; // 描述 不进行具体细分 用户自己填
    private Integer type; // 0 选择 1 填空 2 简答 3编程
    private String bizType; // c++ / java / python / 计算机组成原理....
    private String answer; // 0 A/B/C/D 1 对应结果 2 对应答案 3 正确运行结果数据uri
    private String testData; // 编程题 测试数据 文件路径
    private Integer level; // 题目等级用于组题
    @OneToMany
    private List<ChooseQuestion> chooseQuestions;
    @Column(columnDefinition = "timestamp default current_timestamp",
            insertable = false,
            updatable = false)
    private LocalDateTime insertTime;
    @Column(columnDefinition = "timestamp default current_timestamp"+" on update current_timestamp",
            insertable = false,
            updatable = false)
    private LocalDateTime updateTime;
}
