package com.zhangpeng.better_coder.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@JsonIgnoreProperties({"chooseQuestions","chooseCourses","exams","docs","hibernateLazyInitializer"})
public class User {
    public enum Role{
        ADMIN,USER
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private Integer number;
    private Role role;
    private String password;
    @OneToMany (mappedBy = "user")
    private List<ChooseQuestion> chooseQuestions;
    @OneToMany (mappedBy = "user")
    private List<ChooseCourse> chooseCourses;
    @OneToMany (mappedBy = "user")
    private List<Exam> exams;
    @OneToMany (mappedBy = "user")
    private List<Doc> docs;
    @Column(columnDefinition = "timestamp default current_timestamp",
            insertable = false,
            updatable = false)
    private LocalDateTime insertTime;
    @Column(columnDefinition = "timestamp default current_timestamp"+" on update current_timestamp",
            insertable = false,
            updatable = false)
    private LocalDateTime updateTime;
}
