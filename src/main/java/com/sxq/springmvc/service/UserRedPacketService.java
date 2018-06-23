package com.sxq.springmvc.service;

public interface UserRedPacketService {

    /**
     * 保存抢红包信息
     * @param redPacketId
     * @param userId
     * @return
     */
    int grapRedPacket(Long redPacketId, Long userId);
}
