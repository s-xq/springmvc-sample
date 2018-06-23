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
     * 扣减抢红包数
     * @param id
     * @return
     */
    int decreaseRedPacket(Long id);

}
