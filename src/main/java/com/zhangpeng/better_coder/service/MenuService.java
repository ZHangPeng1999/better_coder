package com.zhangpeng.better_coder.service;

import com.zhangpeng.better_coder.entity.Menu;
import com.zhangpeng.better_coder.repository.MenuRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
public class MenuService {
    @Autowired
    private MenuRepository menuRepository;
    public List<Menu> getAllMenu() {
        return menuRepository.findAll();
    }
    public Menu getMenu(Integer id) {
        return menuRepository.getOne(id);
    }
    public Menu addMenu(String name, String path, Integer open){
        Menu menu = new Menu();
        menu.setName(name);
        menu.setPath(path);
        menu.setOpen(open); // 默认开启
        menuRepository.save(menu);
        menuRepository.refresh(menu);
        return menu;
    }
    public Menu updateMenu(Integer id,Integer open){
        Menu menu = menuRepository.getOne(id);
//        menu.setName(name);
//        menu.setPath(path);
        menu.setOpen(open); // 默认开启
        menuRepository.save(menu);
        return menu;
    }
}

