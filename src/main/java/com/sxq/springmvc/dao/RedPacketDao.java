package com.sxq.springmvc.dao;

import com.sxq.springmvc.pojo.RedPacket;
import org.apache.ibatis.annotations.Param;
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

    /**
     * 扣减抢红包数，使用乐观锁
     * @param id
     * @param version
     * @return
     */
    int decreaseRedPacketForVersion(@Param("id") Long id, @Param("version") Integer version);
}
