package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {

    //查询某用户的会话列表
    List<Message> selectConversations(int userId,int offset,int limit);
    //查询某用户的会话总数
    int selectConversationCount(int userId);
    //查询某会话的私信列表
    List<Message> selectLetters(String conversationId,int offset,int limit);
    //查询某会话的私信总数
    int selectLetterCount(String conversationId);
    //查询未读消息数
    int selectLetterUnreadCount(int userId,String conversationId);
    //增加私信
    int insertMessage(Message message);
    //修改私信状态
    int updateStatus(List<Integer> ids,int status);

    //查询某个主题下最新的通知
    Message selectLatestNotice(int userId,String topic);

    //查询某个主题所包含的全部通知的数量
    int selectNoticeCount(int userId,String topic);

    //查询未读消息的数量
    int selectNoticeUnreadCount(int userId,String topic);

    //查询某个主题包含的全部通知
    List<Message> selectNotices(int userId,String topic,int offset,int limit);
}
