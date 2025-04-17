package com.scoding.metro.mapper;

import com.scoding.metro.entity.ReplyDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReplyMapper {
    /**
     * 根据父ID和类型获取回复列表
     *
     * @param parentId 父ID(反馈或需求ID)
     * @param parentType 父类型(FEEDBACK或REQUEST)
     * @return 回复列表
     */
    List<ReplyDO> listRepliesByParent(@Param("parentId") Long parentId, @Param("parentType") String parentType);
    
    /**
     * 根据ID获取回复
     *
     * @param id 回复ID
     * @return 回复信息
     */
    ReplyDO getReplyById(@Param("id") Long id);

    /**
     * 保存回复
     *
     * @param replyDO 回复信息
     * @return 影响行数
     */
    int saveReply(ReplyDO replyDO);

    /**
     * 更新回复
     *
     * @param replyDO 回复信息
     * @return 影响行数
     */
    int updateReply(ReplyDO replyDO);

    /**
     * 删除回复
     *
     * @param id 回复ID
     * @return 影响行数
     */
    int removeReply(@Param("id") Long id);
    
    /**
     * 根据父ID和类型删除所有回复
     *
     * @param parentId 父ID
     * @param parentType 父类型
     * @return 影响行数
     */
    int removeRepliesByParent(@Param("parentId") Long parentId, @Param("parentType") String parentType);
} 