package com.nowcoder.community;


import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void testSelectUser(){
        User user = userMapper.selectById(101);
        System.out.println(user);

        user = userMapper.selectByName("liubei");
        System.out.println(user);

        user = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);
    }

    @Test
    public void testInsertUser(){
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Test
    public void testUpdateUser(){
        int rows = userMapper.updateStatus(150, 1);
        System.out.println(rows);

        rows = userMapper.updateHeaderUrl(150, "www.abc.com");
        System.out.println(rows);

        rows = userMapper.updatePassword(150, "111111");
        System.out.println(rows);
    }

    @Test
    public void testSelectPosts(){
        List<DiscussPost> posts = discussPostMapper.selectDiscussPosts(149, 0, 10);
        for (DiscussPost post : posts) {
            System.out.println(post);
        }

        int rows = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(rows);
    }

    @Test
    public void testInsertLoginTicket(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 *10));

        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectAndUpdateLoginTicket(){
        LoginTicket loginTicket = loginTicketMapper.selectLoginTicket("abc");
        System.out.println(loginTicket);

        loginTicketMapper.updateStatus("abc",1);
        loginTicket = loginTicketMapper.selectLoginTicket("abc");
        System.out.println(loginTicket);
    }

    @Test
    public void testInsertDiscussPost(){
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(150);
        discussPost.setTitle("test");
        discussPost.setContent("test……");
        discussPost.setType(0);
        discussPost.setStatus(0);
        discussPost.setCreateTime(new Date());
        discussPost.setCommentCount(1000);
        discussPost.setScore(10.00);
        discussPostMapper.insertDiscussPost(discussPost);
    }

    @Test
    public void testSelectLetters(){
        List<Message> list = messageMapper.selectConversations(111, 0, 20);
        for (Message message : list) {
            System.out.println(message);
        }

        int count = messageMapper.selectConversationCount(111);
        System.out.println(count);//14

        list = messageMapper.selectLetters("111_112",0,10);
        for (Message message : list) {
            System.out.println(message);
        }

        count = messageMapper.selectLetterCount("111_112");
        System.out.println(count);

        count = messageMapper.selectLetterUnreadCount(131,"111_131");
        System.out.println(count);//2
    }

    @Test
    public void testInsertAndUpdateMessage(){
        Message message = new Message();
        message.setFromId(111);
        message.setToId(112);
        message.setConversationId("111_112");
        message.setContent("hhhhhhhhhhhhxjy");
        message.setStatus(0);
        message.setCreateTime(new Date());
        int insertCount = messageMapper.insertMessage(message);
        System.out.println(insertCount);

        List<Integer> list = new ArrayList<>();
        list.add(350);
        list.add(351);
        list.add(352);
        list.add(353);
        list.add(354);
        int updateCount = messageMapper.updateStatus(list, 1);
        System.out.println(updateCount);
    }
}
