package com.nowcoder.community.service;

import com.nowcoder.community.dao.CommentMapper;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService implements CommunityConstant {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostService discussPostService;

    public List<Comment> findCommentsByEntity(int entityType,int entityId,int offset,int limit){
        return commentMapper.selectCommentsByEntity(entityType,entityId,offset,limit);
    }

    public int findCommentsCount(int entityType,int entityId){
        return commentMapper.selectCountByEntity(entityType,entityId);
    }

    public Comment findCommentById(int id){
        return commentMapper.selectCommentById(id);
    }

    /**
     * 增加评论，并更新帖子的评论数
     */
    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public int addComment(Comment comment){
        if(comment == null){
            throw new IllegalArgumentException("参数不能为空！");
        }
        //1.增加评论
        //1.1过滤评论内容：过滤html标签、过滤敏感词
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        //1.2向数据库插入评论
        int rows = commentMapper.insertComment(comment);

        //2.更新帖子的评论数
        if(comment.getEntityType() == ENTITY_TYPE_POST){
            //2.1获取帖子的最新评论数
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            //2.2更新帖子的评论数
            discussPostService.updateCommentCount(comment.getEntityId(),count);
        }

        return rows;
    }
}
