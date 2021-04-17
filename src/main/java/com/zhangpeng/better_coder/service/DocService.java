package com.zhangpeng.better_coder.service;

import com.zhangpeng.better_coder.entity.Doc;
import com.zhangpeng.better_coder.entity.Question;
import com.zhangpeng.better_coder.entity.User;
import com.zhangpeng.better_coder.repository.DocRepository;
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
public class DocService {
    @Autowired
    private DocRepository docRepository;
    @Autowired
    private UserRepository userRepository;
    public Doc addDoc(Integer userId, Integer type, String title, String content, Integer status) {
        Doc doc = new Doc();
        doc.setType(type);
        doc.setContent(content);
        doc.setStatus(status);
        doc.setTitle(title);
        User user = userRepository.getOne(userId);
        doc.setUser(user);
        docRepository.save(doc);
        docRepository.refresh(doc);
        return doc;
    }
    public Doc updateDoc(Integer id, Integer type, String title, String content, Integer status) {
        Doc doc = docRepository.getOne(id);
        doc.setType(type);
        doc.setContent(content);
        doc.setStatus(status);
        doc.setTitle(title);
        docRepository.save(doc);
        docRepository.refresh(doc);
        return doc;
    }
    public Doc getDoc(Integer id){
        return docRepository.getOne(id);
    }
    public List<Doc> searchDoc(String title, String content, Integer type,Integer status, Integer offset, Integer limit) {
        if (limit == 0) {
            limit = 20;
        }
        Pageable pageable = PageRequest.of(offset, limit);
        Doc doc = new Doc();
        doc.setTitle(title);
        doc.setContent(content);
        doc.setType(type);
        doc.setStatus(status);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("title", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("content", ExampleMatcher.GenericPropertyMatchers.contains());

        Example<Doc> ex = Example.of(doc, matcher);
        return docRepository.findAll(ex, pageable).getContent();
    }

}
