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
@JsonIgnoreProperties(value = {"chapters","chooseCourses","hibernateLazyInitializer"},ignoreUnknown = true)
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private String content;
    private Integer status; // 1 草稿 2 发布
    private Integer type; // 1 课程 2 项目
    private String bizType; // c++ / java / python / 计算机组成原理....
    private String sourceUri; // 课程资料 百度云链接
    private String projectUri; // 项目uri github链接
    @OneToMany(mappedBy = "course")
    private List<Chapter> chapters;
    @OneToMany(mappedBy = "course")
    private List<ChooseCourse> chooseCourses;
    @Column(columnDefinition = "timestamp default current_timestamp",
            insertable = false,
            updatable = false)
    private LocalDateTime insertTime;
    @Column(columnDefinition = "timestamp default current_timestamp"+" on update current_timestamp",
            insertable = false,
            updatable = false)
    private LocalDateTime updateTime;
}
