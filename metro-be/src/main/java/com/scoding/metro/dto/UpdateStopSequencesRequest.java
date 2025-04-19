package com.scoding.metro.dto;

import lombok.Data;

import java.util.List;

/**
 * 更新经停站序号的请求DTO
 */
@Data
public class UpdateStopSequencesRequest {
    
    /**
     * 路线ID
     */
    private Long routeId;
    
    /**
     * 经停站序号列表
     */
    private List<StopSequence> stopSequences;
    
    /**
     * 经停站序号
     */
    @Data
    public static class StopSequence {
        /**
         * 经停站ID
         */
        private Long id;
        
        /**
         * 序号
         */
        private Integer seq;
    }
} 