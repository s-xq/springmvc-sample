package com.sxq.springmvc.service;

public interface UserRedPacketService {

    /**
     * 保存抢红包信息
     * @param redPacketId
     * @param userId
     * @return
     */
    int grapRedPacket(Long redPacketId, Long userId);

    /**
     * 通过Redis实现抢红包
     * @param redPacketId
     * @param userId
     * @return
     * 0 没有库存
     * 1 成功，且不是最后一个红包
     * 2 成功，且是最后一个红包
     */
    Long grapRedisPacketByRedis(Long redPacketId, Long userId);
}
