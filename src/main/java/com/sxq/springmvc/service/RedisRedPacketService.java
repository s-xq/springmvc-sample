package com.sxq.springmvc.service;

public interface RedisRedPacketService {


    /**
     *
     * @param redPacketId
     * @param unitAmount
     */
    void saveUserRedPacketByRedis(Long redPacketId, Double unitAmount);

}
