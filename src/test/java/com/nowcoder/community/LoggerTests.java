package com.nowcoder.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class LoggerTests {

    //实例化logger对象
    //传入参数：当前类
    public static final Logger logger = LoggerFactory.getLogger(LoggerTests.class);

    @Test
    public void testLogger(){
        System.out.println(logger.getName());//logger的名字就是传入的类的名字

        logger.debug("log debug");
        logger.info("log info");
        logger.warn("log warn");
        logger.error("log error");
    }
    //在application.properties里配置日志级别，大于等于该日志级别的日志信息都会被打印到控制台
}
