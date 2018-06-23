package com.sxq.springmvc.service;

import com.sxq.springmvc.pojo.RedPacket;

public interface RedPacketService {

    /**
     * 获取红包
     * @param id
     * @return
     */
    RedPacket getRedPacket(Long id);

    /**
     * 获取红包，使用悲观锁
     * @param id
     * @return
     */
    RedPacket getRedPacketForUpdate(Long id);

    /**
     * 扣减红包
     * @param id
     * @return
     */
    int decreaseRedPacket(Long id);

}
