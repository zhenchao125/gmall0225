package com.atguigu.gmall0225publisher2;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.atguigu.gmall0225publisher2.mapper")
public class Gmall0225Publisher2Application {

	public static void main(String[] args) {
		SpringApplication.run(Gmall0225Publisher2Application.class, args);
	}

}
