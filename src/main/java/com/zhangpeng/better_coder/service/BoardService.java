package com.zhangpeng.better_coder.service;

import com.zhangpeng.better_coder.entity.Board;
import com.zhangpeng.better_coder.repository.BoardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
public class BoardService {
    @Autowired
    private BoardRepository boardRepository;
    public List<Board> getNewBoardList() {
        List<Board> boards = new ArrayList<>();
        for (Integer i = 0;i<=7;i++){
            boards.addAll(boardRepository.findByIdDesc(i, PageRequest.of(0, 1)).getContent());
        }
        return boards;
    }
}

