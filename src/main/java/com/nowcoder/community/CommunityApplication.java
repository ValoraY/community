package com.nowcoder.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class CommunityApplication {

	//解决netty启动冲突问题
	//see Netty4Utils.setAvailableProcessors(int availableProcessors)
	@PostConstruct
	public void init(){
		System.setProperty("es.set.netty.runtime.available.processors","false");
	}

	public static void main(String[] args) {
		SpringApplication.run(CommunityApplication.class, "--debug");
	}


}
