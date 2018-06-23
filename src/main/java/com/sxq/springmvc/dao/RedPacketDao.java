package com.sxq.springmvc.dao;

import com.sxq.springmvc.pojo.RedPacket;
import org.springframework.stereotype.Repository;

@Repository
public interface RedPacketDao {

    /**
     * 获取红包信息
     * @param id
     * @return
     */
    RedPacket getRedPacket(Long id);

    /**
     * 获取红包信息，使用悲观锁消除高并发下的超发红包现象
     * @param id
     * @return
     */
    RedPacket getRedPacketForUpdate(Long id);

    /**
     * 扣减抢红包数
     * @param id
     * @return
     */
    int decreaseRedPacket(Long id);

}
