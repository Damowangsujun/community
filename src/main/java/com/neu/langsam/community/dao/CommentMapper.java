package com.neu.langsam.community.dao;

import com.neu.langsam.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CommentMapper {
    //根据评论类型和ID查询评论
    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);
    int selectCountByEntity(int entityType, int entityId);//查询评论的数量
    //增加评论
    int insertComment(Comment comment);
}
