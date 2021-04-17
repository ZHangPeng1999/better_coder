package com.zhangpeng.better_coder.service;

import com.zhangpeng.better_coder.entity.*;
import com.zhangpeng.better_coder.repository.ChapterRepository;
import com.zhangpeng.better_coder.repository.ChooseCourseRepository;
import com.zhangpeng.better_coder.repository.CourseRepository;
import com.zhangpeng.better_coder.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
public class CourseService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private ChapterRepository chapterRepository;
    @Autowired
    private ChooseCourseRepository chooseCourseRepository;
    public Course getCourse(Integer id) {
        return courseRepository.getOne(id);
    }
    public Course addCourse(String title, String content, Integer type, String bizType,
                            String sourceUri, String projectUri) {
        Course course = new Course();
        course.setContent(content);
        course.setTitle(title);
        course.setBizType(bizType);
        course.setProjectUri(projectUri);
        course.setSourceUri(sourceUri);
        course.setType(type);
        courseRepository.save(course);
        courseRepository.refresh(course);
        return course;
    }
    public Boolean selectCourse(Integer uId, Integer cId) {
        User user = userRepository.getOne(uId);
        Course course = courseRepository.getOne(cId);
        ChooseCourse chooseCourse = new ChooseCourse();
        chooseCourse.setCourse(course);
        chooseCourse.setUser(user);
        chooseCourseRepository.save(chooseCourse);
        return true;
    }

    public Course updateCourse(Integer id, String title, String content, Integer type, String bizType,
                            String sourceUri, String projectUri) {
        Course course = courseRepository.getOne(id);
        course.setContent(content);
        course.setTitle(title);
        course.setBizType(bizType);
        course.setProjectUri(projectUri);
        course.setSourceUri(sourceUri);
        course.setType(type);
        courseRepository.save(course);
        courseRepository.refresh(course);
        return course;
    }
    public Chapter addChapter(Integer cId, String title, String content, Integer orderId) {
        Course course = courseRepository.getOne(cId);
        Chapter chapter = new Chapter();
        chapter.setTitle(title);
        chapter.setContent(content);
        chapter.setStatus(0);
        chapter.setCourse(course);
        chapter.setOrderId(orderId);
        chapterRepository.save(chapter);
        chapterRepository.refresh(chapter);
        return chapter;
    }
    public Chapter updateChapter(Integer id, String title, String content, Integer status, Integer orderId){
        Chapter chapter = chapterRepository.getOne(id);
        chapter.setTitle(title);
        chapter.setContent(content);
        chapter.setStatus(status);
        chapter.setOrderId(orderId);

        chapterRepository.save(chapter);
        chapterRepository.refresh(chapter);
        return chapter;
    }
    public Chapter getChapter(Integer cid) {
        return chapterRepository.getOne(cid);
    }

    public Boolean readChapter(Integer chapterId, Integer cId,Integer uId) {
        User user = userRepository.getOne(uId);
        Chapter chapter = chapterRepository.getOne(chapterId);
        user.getChooseCourses().forEach(
                chooseCourse ->{
                    if (chooseCourse.getCourse().getId() == cId) {
                        if (chooseCourse.getChapterOrderId() > chapter.getOrderId()){
                            chooseCourse.setChapterOrderId(chapter.getOrderId());
                            Course course = chooseCourse.getCourse();
                            chooseCourse.setCompleteness((float) (chapter.getOrderId() / course.getChapters().size()));
                            chooseCourseRepository.save(chooseCourse);
                        }
                    }
                }
        );
        return true;
    }
    public List<Course> searchCourse(String title, String content, Integer type, String bizType, Integer offset, Integer limit) {
        if (limit == 0) {
            limit = 20;
        }
        Pageable pageable = PageRequest.of(offset, limit);
        Course course = new Course();
        course.setTitle(title);
        course.setContent(content);
        course.setType(type);
        course.setBizType(bizType);

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("title", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("content", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("biz_type", ExampleMatcher.GenericPropertyMatchers.contains());

        Example<Course> ex = Example.of(course, matcher);
        return courseRepository.findAll(ex, pageable).getContent();
    }

}
