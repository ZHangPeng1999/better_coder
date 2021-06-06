package com.zhangpeng.better_coder.controller;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Data
@NoArgsConstructor
public class JudgeExam {
    String id;
    String answer;
}
