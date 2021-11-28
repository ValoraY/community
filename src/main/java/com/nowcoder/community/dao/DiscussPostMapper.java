package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    //offset：分页查询时数据的起始行  limit：分页查询每页显示的数据条数
    List<DiscussPost> selectDiscussPosts(int userId,int offset,int limit);

    //查询数据库中帖子的总条数
    int selectDiscussPostRows(int userId);

    //插入帖子
    int insertDiscussPost(DiscussPost discussPost);

    //查询帖子
    DiscussPost selectDiscussPostById(int id);
}
