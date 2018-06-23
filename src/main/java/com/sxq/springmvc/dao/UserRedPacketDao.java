package com.sxq.springmvc.dao;

import com.sxq.springmvc.pojo.UserRedPacket;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRedPacketDao {

    /**
     * 插入抢红包信息
     * @param userRedPacket 抢红包信息
     * @return 影响条数
     */
    int grapRedPacket(UserRedPacket userRedPacket);
}
