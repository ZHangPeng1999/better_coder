package com.zhangpeng.better_coder;

import com.zhangpeng.better_coder.repository.Impl.BaseRepositoryImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(repositoryBaseClass = BaseRepositoryImpl.class)//告诉springboot使用自己实现的jpa容器s实现类
public class BetterCoderApplication {
    public static void main(String[] args) {
        SpringApplication.run(BetterCoderApplication.class, args);
    }

}
