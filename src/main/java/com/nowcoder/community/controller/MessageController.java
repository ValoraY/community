package com.nowcoder.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

@Controller
public class MessageController implements CommunityConstant {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    //获取会话列表
    @RequestMapping(path = "/letter/list",method = RequestMethod.GET)
    public String getLetterList(Model model, Page page){
        //获取当前用户
        User user = hostHolder.getUser();
        //设置分页信息
        page.setPath("/letter/list");
        page.setLimit(5);
        page.setRows(messageService.findConversationCount(user.getId()));
        //获取会话列表
        List<Message> conversationList = messageService.findConversations(
                user.getId(), page.getOffset(), page.getLimit());
        List<Map<String,Object>> conversations = new ArrayList<>();
        if(conversationList != null){
            for (Message conversation : conversationList) {
                Map<String,Object> map = new HashMap<>();
                map.put("conversation",conversation);
                map.put("letterUnread",messageService.findLetterUnreadCount(user.getId(), conversation.getConversationId()));
                map.put("letterCount",messageService.findLetterCount(conversation.getConversationId()));
                int targetId = user.getId() == conversation.getFromId() ? conversation.getToId() : conversation.getFromId();
                map.put("target",userService.findUserById(targetId));
                conversations.add(map);
            }
        }
        model.addAttribute("conversations",conversations);
        //获取总的未读消息数
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(),null);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);

        return "/site/letter";
    }

    //获取私信列表
    @RequestMapping(path = "/letter/detail/{conversationId}",method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId,Page page,Model model){
        //设置分页信息
        page.setPath("/letter/detail/"+conversationId);
        page.setLimit(5);
        page.setRows(messageService.findLetterCount(conversationId));
        //获取私信详情列表
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        //封装私信详情列表
        List<Map<String,Object>> letters = new ArrayList<>();
        if(letterList != null){
            for (Message letter : letterList) {
                Map<String,Object> map = new HashMap<>();
                map.put("letter",letter);
                map.put("fromUser",userService.findUserById(letter.getFromId()));
                letters.add(map);
            }
        }

        model.addAttribute("letters",letters);

        //获取私信的目标用户
        User target = getLetterTarget(conversationId);
        model.addAttribute("target",target);

        //修改私信状态为已读
        List<Integer> ids = getLetterIds(letterList);
        if(!ids.isEmpty()){
            messageService.readMessage(ids);
        }

        return "/site/letter-detail";
    }

    private List<Integer> getLetterIds(List<Message> letterList){
        List<Integer> ids = new ArrayList<>();
        if(letterList != null){
            for (Message letter : letterList) {
                if(hostHolder.getUser().getId() == letter.getToId() && letter.getStatus() == 0){
                    ids.add(letter.getId());
                }
            }
        }
        return ids;
    }

    private User getLetterTarget(String conversationId){
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);
        if(hostHolder.getUser().getId() == id0){
            return userService.findUserById(id1);
        }else{
            return userService.findUserById(id0);
        }

    }

    //发送私信
    @RequestMapping(path = "/letter/send",method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName,String content){
        User target = userService.findUserByName(toName);
        if(target == null){
            return CommunityUtil.getJSONString(1,"您发送的用户不存在！");
        }
        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        if(message.getFromId() < message.getToId()){
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        }else{
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        message.setContent(content);
        message.setStatus(0);
        message.setCreateTime(new Date());

        messageService.addMessage(message);

        return CommunityUtil.getJSONString(0);
    }

    //系统通知列表
    @RequestMapping(path = "/notice/list",method = RequestMethod.GET)
    public String getNoticeList(Model model){
        User user = hostHolder.getUser();

        //查询评论类通知
        Map<String,Object> messageVO = new HashMap<>();//存储聚合后的数据
        Message message = messageService.findLatestNotice(user.getId(), TOPIC_COMMENT);//最新一条通知
        if(message != null){

            messageVO.put("message",message);
            String content = HtmlUtils.htmlUnescape(message.getContent());//反转义content内容
            Map<String,Object> map = JSONObject.parseObject(content, HashMap.class);//将字符串转成hashmap，方便取值

            messageVO.put("user",userService.findUserById((Integer) map.get("userId")));//事件的触发者
            messageVO.put("entityType",map.get("entityType"));
            messageVO.put("entityId",map.get("entityId"));
            messageVO.put("postId",map.get("postId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_COMMENT);
            messageVO.put("count",count);

            int unreadCount = messageService.findNoticeUnreadCount(user.getId(), TOPIC_COMMENT);
            messageVO.put("unreadCount",unreadCount);
        }
        model.addAttribute("commentNotice",messageVO);

        //查询点赞类通知
        messageVO = new HashMap<>();//存储聚合后的数据
        message = messageService.findLatestNotice(user.getId(), TOPIC_LIKE);//最新一条通知
        messageVO.put("message",message);
        if(message != null){
            String content = HtmlUtils.htmlUnescape(message.getContent());//反转义content内容
            HashMap map = JSONObject.parseObject(content, HashMap.class);//将字符串转成hashmap，方便取值
            messageVO.put("user",userService.findUserById((Integer) map.get("userId")));//事件的触发者
            messageVO.put("entityType",map.get("entityType"));
            messageVO.put("entityId",map.get("entityId"));
            messageVO.put("postId",map.get("postId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_LIKE);
            messageVO.put("count",count);

            int unreadCount = messageService.findNoticeUnreadCount(user.getId(), TOPIC_LIKE);
            messageVO.put("unreadCount",unreadCount);
        }
        model.addAttribute("likeNotice",messageVO);

        //查询关注类通知
        messageVO = new HashMap<>();//存储聚合后的数据
        message = messageService.findLatestNotice(user.getId(), TOPIC_FOLLOW);//最新一条通知
        messageVO.put("message",message);
        if(message != null){
            String content = HtmlUtils.htmlUnescape(message.getContent());//反转义content内容
            HashMap map = JSONObject.parseObject(content, HashMap.class);//将字符串转成hashmap，方便取值
            messageVO.put("user",userService.findUserById((Integer) map.get("userId")));//事件的触发者
            messageVO.put("entityType",map.get("entityType"));
            messageVO.put("entityId",map.get("entityId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_FOLLOW);
            messageVO.put("count",count);

            int unreadCount = messageService.findNoticeUnreadCount(user.getId(), TOPIC_FOLLOW);
            messageVO.put("unreadCount",unreadCount);
        }
        model.addAttribute("followNotice",messageVO);

        //查询未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(),null);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);

        return "/site/notice";
    }

    @RequestMapping(path = "/notice/detail/{topic}",method = RequestMethod.GET)
    public String getNoticeDetail(@PathVariable("topic") String topic,Page page,Model model){
        User user = hostHolder.getUser();

        page.setLimit(5);
        page.setPath("/notice/detail/"+topic);
        page.setRows(messageService.findNoticeCount(user.getId(), topic));

        List<Message> noticeList = messageService.findNotices(user.getId(), topic, page.getOffset(), page.getLimit());
        List<Map<String,Object>> noticeVoList = new ArrayList<>();
        if(noticeList!=null){
            for (Message notice : noticeList) {
                Map<String,Object> map = new HashMap<>();
                //通知
                map.put("notice",notice);
                //内容
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String,Object> data = JSONObject.parseObject(content,HashMap.class);
                map.put("user",userService.findUserById((Integer) data.get("userId")));
                map.put("entityType",data.get("entityType"));
                map.put("entityId",data.get("entityId"));
                map.put("postId",data.get("postId"));

                map.put("fromUser",userService.findUserById(notice.getFromId()));

                noticeVoList.add(map);
            }
        }
        model.addAttribute("notices",noticeVoList);

        //设置已读
        List<Integer> ids = getLetterIds(noticeList);
        if(!ids.isEmpty()){
            messageService.readMessage(ids);
        }

        return "/site/notice-detail";
    }
}
