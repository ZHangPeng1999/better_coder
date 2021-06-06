package com.zhangpeng.better_coder.service;

import com.zhangpeng.better_coder.entity.*;
import com.zhangpeng.better_coder.repository.ChapterRepository;
import com.zhangpeng.better_coder.repository.ChooseCourseRepository;
import com.zhangpeng.better_coder.repository.CourseRepository;
import com.zhangpeng.better_coder.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.zip.CheckedOutputStream;

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
                            String sourceUri, String projectUri, Integer status) {
        Course course = new Course();
        course.setContent(content);
        course.setTitle(title);
        course.setBizType(bizType);
        course.setProjectUri(projectUri);
        course.setSourceUri(sourceUri);
        course.setType(type);
        course.setStatus(status);
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
        chooseCourse.setCompleteness(0);
        chooseCourse.setChapterOrderId(0);
        chooseCourseRepository.save(chooseCourse);
        return true;
    }

    public Course updateCourse(Integer id, String title, String content, Integer type, String bizType,
                            String sourceUri, String projectUri, Integer status) {
        Course course = courseRepository.getOne(id);
        course.setContent(content);
        course.setTitle(title);
        course.setBizType(bizType);
        course.setProjectUri(projectUri);
        course.setSourceUri(sourceUri);
        course.setType(type);
        course.setStatus(status);
        courseRepository.save(course);
        return course;
    }
    public Chapter addChapter(Integer cId, String title, String content, Integer orderId, String vId) {
        Course course = courseRepository.getOne(cId);
        Chapter chapter = new Chapter();
        chapter.setTitle(title);
        chapter.setContent(content);
        chapter.setStatus(0);
        chapter.setCourse(course);
        chapter.setOrderId(orderId);
        chapter.setVideoId(vId);
        chapterRepository.save(chapter);
        chapterRepository.refresh(chapter);
        return chapter;
    }
    public Chapter updateChapter(Integer id, String title, String content, Integer status, Integer orderId, String vId){
        Chapter chapter = chapterRepository.getOne(id);
        chapter.setTitle(title);
        chapter.setContent(content);
        chapter.setStatus(status);
        chapter.setOrderId(orderId);
        chapter.setVideoId(vId);
        chapterRepository.save(chapter);
        return chapter;
    }
    public Chapter getChapter(Integer cid) {
        return chapterRepository.getOne(cid);
    }
    public List<Chapter> getChapters(Integer cid) {
        return chapterRepository.listChaptesByCourseId(cid);
    }

    public Boolean readChapter(Integer chapterId, Integer cId,Integer uId) {
        User user = userRepository.getOne(uId);
        log.error(String.valueOf(cId));
        log.error(String.valueOf(uId));
        Chapter chapter = chapterRepository.getOne(chapterId);
        log.error(String.valueOf(chapter.getOrderId()));
        user.getChooseCourses().forEach(
                chooseCourse ->{
                    if (chooseCourse.getCourse().getId() == cId) {
                        if (chooseCourse.getChapterOrderId() < chapter.getOrderId()){
                            log.error(String.valueOf(chooseCourse.getCompleteness()));
                            chooseCourse.setChapterOrderId(chapter.getOrderId());
                            Course course = chooseCourse.getCourse();
                            Float size = Float.valueOf((float)course.getChapters().size());
                            Float nowNum =  Float.valueOf(chapter.getOrderId().toString());
                            Float complete = size/nowNum * 100;
                            chooseCourse.setCompleteness(Integer.valueOf(complete.intValue()));
                            chooseCourseRepository.save(chooseCourse);
                        }
                    }
                }
        );
        return true;
    }
    public Map searchCourse(String title, String content, Integer type, String bizType, Integer status, Integer offset, Integer limit) {
        if (limit == 0) {
            limit = 20;
        }
        Pageable pageable = PageRequest.of(offset, limit);
        Course course = new Course();
        if (type !=0) {
            course.setType(type);
        }
        if (content.length()!=0){
            course.setContent(content);
        }
        if (title.length()!=0 ){
        course.setTitle(title);}
        if (bizType.length() !=0 ){
        course.setBizType(bizType);}
        if (status != 0) {
            course.setStatus(status);
        }
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("title", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("content", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("biz_type", ExampleMatcher.GenericPropertyMatchers.contains());

        Example<Course> ex = Example.of(course, matcher);
        Page<Course> result = courseRepository.findAll(ex, pageable);
        return Map.of("courses",result.getContent(), "total", result.getTotalElements());
    }

}
